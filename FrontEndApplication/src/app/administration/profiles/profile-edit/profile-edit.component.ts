import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Params, Router } from '@angular/router';
import { FieldTypeName } from 'src/app/shared/constants/field-type.constant';
import { Field, FieldType, GetFieldsDto, ProfileField } from 'src/app/shared/models/field.model';
import { GetLabelsDto, Label } from 'src/app/shared/models/label.model';
import { EditProfileDto } from 'src/app/shared/models/profile.model';
import { FieldService } from 'src/app/shared/services/field.service';
import { LabelService } from 'src/app/shared/services/label.service';
import { ProfileService } from 'src/app/shared/services/profile.service';

@Component({
  selector: 'app-administration-profile-edit',
  templateUrl: './profile-edit.component.html',
  styleUrls: ['./profile-edit.component.css']
})
export class ProfileEditComponent implements OnInit {
  constructor(private readonly router: Router, private readonly activatedRoute: ActivatedRoute,
    private readonly fieldService: FieldService, private readonly profileService: ProfileService,
    private readonly labelService: LabelService
  ) {
  }

  profileId!: number;

  allLabels: Label[] = [];
  possibleLabels: Label[] = [];
  profileLabels: Label[] = [];

  allFields: Field[] = [];
  possibleFields: Field[] = [];

  originalProfileFields: ProfileField[] = [];
  updatedProfileFields: ProfileField[] = [];

  _labelSearchQuery = '';
  selectedLabel = 0;

  _fieldSearchQuery = '';
  selectedField = 0;

  selectedFieldError = '';
  selectedLabelError = '';
  profileFieldsError: string[] = [];

  setFieldSearchQuery(value: Event) {
    let valueText = (value.target as HTMLInputElement).value;

    this._fieldSearchQuery = valueText;
    this.selectedField = 0;

    this.setPossibleFields();
  }

  setLabelSearchQuery(value: Event) {
    let valueText = (value.target as HTMLInputElement).value;

    this._labelSearchQuery = valueText;
    this.selectedLabel = 0;

    this.setPossibleLabels();
  }

  ngOnInit(): void {
    this.activatedRoute.params.subscribe((data: Params) => {
      this.profileId = data['id'];

      this.profileService.getProfileLabels(this.profileId).subscribe({
        next: (labels: Label[]) => {
          this.profileLabels = labels;

          this.labelService.getAllLabels().subscribe({
            next: (allLabels: GetLabelsDto) => {
              this.allLabels = allLabels.labels;
              this.possibleLabels = this.allLabels;

              for (let profileLabel of this.profileLabels) {
                this.possibleLabels = this.possibleLabels.filter(x => x.id != profileLabel.id);
              }

              if (this.possibleLabels.length > 0) {
                this.selectedLabel = this.possibleLabels[0].id;
              }
            }
          })
        },
        error: (error: any) => {
          this.router.navigateByUrl("/error/404");
        }
      })

      this.profileService.getProfileFields(this.profileId).subscribe({
        next: (profileFields: ProfileField[]) => {
          this.originalProfileFields = profileFields;
          this.updatedProfileFields = this.originalProfileFields;

          for (let val of this.updatedProfileFields) {
            this.profileFieldsError.push('');
          }

          this.fieldService.getAllFields().subscribe({
            next: (allFields: GetFieldsDto) => {
              this.allFields = allFields.fields.filter(x => x.fieldType.name != FieldTypeName.LABEL);
              this.possibleFields = this.allFields;

              this.possibleFields = this.possibleFields.filter(x => {
                return this.updatedProfileFields.filter(y => y.field.id == x.id).length == 0
              });

              if (this.possibleFields.length > 0) {
                this.selectedField = this.possibleFields[0].id;
              }
            }
          })
        },
        error: (error: any) => {
          this.router.navigateByUrl("/error/404");
        }
      })
    });
  }

  setPossibleLabels() {
    this.possibleLabels = this.allLabels;

    for (let profileLabel of this.profileLabels) {
      this.possibleLabels = this.possibleLabels.filter(x => x.id != profileLabel.id);
    }
    this.possibleLabels = this.possibleLabels.filter(x => x.name.toLowerCase().includes(this._labelSearchQuery.toLowerCase()));

    if (this.possibleLabels.length > 0)
      this.selectedLabel = this.possibleLabels[0].id;
  }

  setPossibleFields() {
    this.possibleFields = this.allFields;

    for (let profileField of this.updatedProfileFields) {
      this.possibleFields = this.possibleFields.filter(x => x.id != profileField.field.id);
    }
    this.possibleFields = this.possibleFields.filter(x => x.name.toLowerCase().includes(this._fieldSearchQuery.toLowerCase()));

    if (this.possibleFields.length > 0)
      this.selectedField = this.possibleFields[0].id;
  }

  changeProfileFieldValue(index: number, newValue: EventTarget | null) {
    if (this.updatedProfileFields[index].field.fieldType.name === FieldTypeName.BOOLEAN) {
      this.updatedProfileFields[index].value = (newValue as HTMLInputElement).checked ? 'true' : 'false';
    } else {
      this.updatedProfileFields[index].value = (newValue as HTMLInputElement).value;
    }
    console.log(this.updatedProfileFields[index].value);
  }

  addFieldToProfile() {
    let selectedFieldError = this.validateSelectedField();
    if (selectedFieldError.length !== 0) {
      this.selectedFieldError = selectedFieldError;
      return;
    } else {
      this.selectedFieldError = '';
    }

    let field = this.allFields.filter(x => x.id == this.selectedField)[0];

    // if field was in profile before changing
    let previousField = this.originalProfileFields.filter(x => x.field.id == this.selectedField);

    if (previousField.length > 0) {
      this.updatedProfileFields.push(previousField[0]);
    } else {
      if (field.fieldType.name === FieldTypeName.BOOLEAN) {
        this.updatedProfileFields.push(new ProfileField(-1, 'false', field));
      } else {
        this.updatedProfileFields.push(new ProfileField(-1, '', field));
      }
    }

    this.setPossibleFields();

    this.profileFieldsError.push('');
  }

  removeFieldFromProfile(index: number) {
    this.updatedProfileFields = this.updatedProfileFields.filter((x, ind) => ind != index);

    this.setPossibleFields();

    this.profileFieldsError = this.profileFieldsError.filter((x, ind) => ind != index);
  }

  removeLabel(id: number) {
    this.profileLabels = this.profileLabels.filter(x => x.id != id);

    this.setPossibleLabels();
  }

  addLabel() {
    let selectedLabelError = this.validateSelectedLabel();
    if (selectedLabelError.length !== 0) {
      this.selectedLabelError = selectedLabelError;
      return;
    } else {
      this.selectedLabelError = '';
    }

    this.profileLabels.push(this.possibleLabels.filter(x => x.id == this.selectedLabel)[0]);

    this.setPossibleLabels();
  }

  editProfile() {
    this.validateFields();
    if (this.profileFieldsError.filter(x => x !== '').length > 0) {
      window.scroll({
        top: 120,
        left: 0,
        behavior: 'smooth'
      });
      return;
    }

    let editProfileDto = new EditProfileDto(this.updatedProfileFields, this.profileLabels.map(x => x.id));

    this.profileService.editProfile(this.profileId, editProfileDto).subscribe({
      complete: () => {
        this.router.navigateByUrl("/user/profiles");
      }
    });
  }

  validateSelectedField() {
    if (this.selectedField === 0)
      return 'Select Field';
    return '';
  }

  validateSelectedLabel() {
    if (this.selectedLabel === 0)
      return 'Select Label';
    return '';
  }

  validateFields() {
    for (let i = 0; i < this.updatedProfileFields.length; i++) {
      if (this.updatedProfileFields[i].value === '') {
        this.profileFieldsError[i] = 'Enter field!';
        continue;
      }

      if (this.updatedProfileFields[i].field.fieldType.name === FieldTypeName.NUMBER) {
        try {
          if (isNaN(parseInt(this.updatedProfileFields[i].value))
            || isNaN(parseFloat(this.updatedProfileFields[i].value))) {
            throw new Error()
          }
          this.profileFieldsError[i] = '';
        } catch (e) {
          this.profileFieldsError[i] = 'Enter number here!';
        }
      }
      else if (this.updatedProfileFields[i].field.fieldType.name === FieldTypeName.CITATION ||
        this.updatedProfileFields[i].field.fieldType.name === FieldTypeName.H_INDEX
      ) {
        try {
          if (isNaN(parseInt(this.updatedProfileFields[i].value))) {
            throw new Error()
          }
          this.profileFieldsError[i] = '';
        } catch (e) {
          this.profileFieldsError[i] = 'Enter integer here!';
        }
      }
    }
  }
}
