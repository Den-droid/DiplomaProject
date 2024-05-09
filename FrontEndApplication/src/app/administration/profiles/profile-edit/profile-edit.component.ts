import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Params, Router } from '@angular/router';
import { FieldTypeName } from 'src/app/shared/constants/field-type.constant';
import { Field, FieldType, GetFieldsDto, ProfileField } from 'src/app/shared/models/field.model';
import { GetLabelsDto, Label } from 'src/app/shared/models/label.model';
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

  allFieldTypes: FieldType[] = [];
  allFields: Field[] = [];

  profileLabels: Label[] = [];
  profileFields: ProfileField[] = [];

  labelSearchQuery = '';
  selectedLabel = 0;

  fieldSearchQuery = '';
  selectedField = 0;

  selectedFieldError = '';
  selectedLabelError = '';

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
        }
      })

      this.profileService.getProfileFields(this.profileId).subscribe({
        next: (profileFields: ProfileField[]) => {
          this.profileFields = profileFields;
          this.profileFields = this.profileFields.filter(x => {
            return x.field.fieldType.name != FieldTypeName.LABEL &&
              x.field.fieldType.name != FieldTypeName.YEAR_CITATION
          });

          this.fieldService.getAllFields().subscribe({
            next: (allFields: GetFieldsDto) => {
              this.allFields = allFields.fields;

              this.allFields = this.allFields.filter(x => {
                return x.fieldType.name != FieldTypeName.LABEL &&
                  x.fieldType.name != FieldTypeName.YEAR_CITATION &&
                  this.profileFields.filter(y => y.field.id == x.id).length == 0
              });
            }
          })
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

  // changeProfileFieldValue(index: number, newValue: Event) {
  //   newValue.target.
  //   this.profileFields[index].value = newValue;
  // }

  addFieldToProfile() {
    let selectedFieldError = this.validateSelectedField();
    if (selectedFieldError.length !== 0) {
      this.selectedFieldError = selectedFieldError;
      return;
    } else {
      this.selectedFieldError = '';
    }

    let field = this.allFields.filter(x => x.id == this.selectedField)[0];
    this.profileFields.push(new ProfileField(-1, '', field));

    this.allFields = this.allFields.filter(x => x.id != field.id);

    this.selectedField = 0;
  }

  removeFieldFromProfile(index: number) {
    let profileField = this.profileFields.filter((x, ind) => ind == index)[0];

    this.allFields.push(profileField.field);
    this.profileFields = this.profileFields.filter(x => x.id != profileField.id);
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
    console.log(this.profileFields);
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
}
