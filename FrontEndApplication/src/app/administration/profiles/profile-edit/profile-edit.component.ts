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

  allFieldTypes: FieldType[] = [];
  allFields: Field[] = [];

  originalProfileFields: ProfileField[] = [];
  updatedProfileFields: ProfileField[] = [];

  labelSearchQuery = '';
  selectedLabel = 0;

  fieldSearchQuery = '';
  selectedField = 0;

  selectedFieldError = '';
  selectedLabelError = '';
  profileFieldsError: string[] = [];

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

          this.updatedProfileFields = this.updatedProfileFields.filter(x => {
            return x.field.fieldType.name != FieldTypeName.LABEL &&
              x.field.fieldType.name != FieldTypeName.YEAR_CITATION
          });
          for (let val of this.updatedProfileFields) {
            this.profileFieldsError.push('');
          }

          this.fieldService.getAllFields().subscribe({
            next: (allFields: GetFieldsDto) => {
              this.allFields = allFields.fields;

              this.allFields = this.allFields.filter(x => {
                return x.fieldType.name != FieldTypeName.LABEL &&
                  x.fieldType.name != FieldTypeName.YEAR_CITATION &&
                  this.updatedProfileFields.filter(y => y.field.id == x.id).length == 0
              });
            }
          })
        },
        error: (error: any) => {
          this.router.navigateByUrl("/error/404");
        }
      })
    });

    this.fieldService.getAllFieldTypes().subscribe({
      next: (allFieldTypes: FieldType[]) => {
        this.allFieldTypes = allFieldTypes;

        this.allFieldTypes = this.allFieldTypes.filter(x => {
          return x.name != FieldTypeName.CITATION && x.name != FieldTypeName.H_INDEX
            && x.name != FieldTypeName.LABEL && x.name != FieldTypeName.YEAR_CITATION
        });
      }
    })
  }

  changeProfileFieldValue(index: number, newValue: EventTarget | null) {
    if (this.updatedProfileFields[index].field.fieldType.name === FieldTypeName.BOOLEAN) {
      this.updatedProfileFields[index].value = (newValue as HTMLInputElement).checked ? 'true' : 'false';
    } else {
      this.updatedProfileFields[index].value = (newValue as HTMLInputElement).value;
    }
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
      previousField[0].value = '';
      this.updatedProfileFields.push(previousField[0]);
    } else {
      this.updatedProfileFields.push(new ProfileField(-1, '', field));
    }

    this.allFields = this.allFields.filter(x => x.id != field.id);

    this.selectedField = 0;
    this.profileFieldsError.push('');
  }

  removeFieldFromProfile(index: number) {
    let profileField = this.updatedProfileFields.filter((x, ind) => ind == index)[0];

    this.allFields.push(profileField.field);
    this.updatedProfileFields = this.updatedProfileFields.filter(x => x.id != profileField.id);

    this.profileFieldsError = this.profileFieldsError.filter((x, ind) => ind != index);
  }

  removeLabel(id: number) {
    this.profileLabels = this.profileLabels.filter(x => x.id != id);

    this.possibleLabels = this.allLabels;

    for (let profileLabel of this.profileLabels) {
      this.possibleLabels = this.possibleLabels.filter(x => x.id != profileLabel.id);
    }
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

    this.possibleLabels = this.allLabels;

    for (let profileLabel of this.profileLabels) {
      this.possibleLabels = this.possibleLabels.filter(x => x.id != profileLabel.id);
    }

    this.selectedLabel = 0;
  }

  editProfile() {
    this.validateFields();
    if (this.profileFieldsError.filter(x => x !== '').length > 0) {
      window.scroll({
        top: 0,
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
      if (this.updatedProfileFields[i].field.fieldType.name === FieldTypeName.NUMBER) {
        if (this.updatedProfileFields[i].value === '') {
          break;
        }

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
        if (this.updatedProfileFields[i].value === '') {
          break;
        }

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
