import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Params, Router } from '@angular/router';
import { FieldTypeName } from 'src/app/shared/constants/field-type.constant';
import { FieldType, Field, ProfileField, GetFieldsDto } from 'src/app/shared/models/field.model';
import { Label, GetLabelsDto } from 'src/app/shared/models/label.model';
import { AddProfileDto } from 'src/app/shared/models/profile.model';
import { ScientistPreview } from 'src/app/shared/models/scientist.model';
import { ScientometricSystem, mapStringToScientometricSystemLabel } from 'src/app/shared/models/scientometric.model';
import { FieldService } from 'src/app/shared/services/field.service';
import { LabelService } from 'src/app/shared/services/label.service';
import { ProfileService } from 'src/app/shared/services/profile.service';
import { ScientistService } from 'src/app/shared/services/scientist.service';
import { ScientometricSystemService } from 'src/app/shared/services/scientometric-System.service';

@Component({
  selector: 'app-administration-profile-add',
  templateUrl: './profile-add.component.html',
  styleUrls: ['./profile-add.component.css']
})
export class ProfileAddComponent implements OnInit {
  constructor(private readonly router: Router, private readonly activatedRoute: ActivatedRoute,
    private readonly fieldService: FieldService, private readonly profileService: ProfileService,
    private readonly labelService: LabelService, private readonly scientometricSystemService: ScientometricSystemService,
    private readonly scientistService: ScientistService
  ) {
  }
  selectedScientist = 0;

  scientometricSystems: ScientometricSystem[] = [];
  selectedScientometricSystem = 0;

  scientistSearchQuery = '';
  scientists: ScientistPreview[] = [];

  errorCanAddProfile = '';

  profileCanBeAdded = false;
  addProfileButtonClicked = false;

  allLabels: Label[] = [];
  possibleLabels: Label[] = [];
  profileLabels: Label[] = [];

  allFieldTypes: FieldType[] = [];
  allFields: Field[] = [];

  profileFields: ProfileField[] = [];

  labelSearchQuery = '';
  selectedLabel = 0;

  fieldSearchQuery = '';
  selectedField = 0;

  selectedFieldError = '';
  selectedLabelError = '';
  profileFieldsError: string[] = [];

  ngOnInit(): void {
    this.activatedRoute.params.subscribe((data: Params) => {
      this.labelService.getAllLabels().subscribe({
        next: (allLabels: GetLabelsDto) => {
          this.allLabels = allLabels.labels;
          this.possibleLabels = this.allLabels;

          for (let profileLabel of this.profileLabels) {
            this.possibleLabels = this.possibleLabels.filter(x => x.id != profileLabel.id);
          }
        }
      })

      this.fieldService.getAllFields().subscribe({
        next: (allFields: GetFieldsDto) => {
          this.allFields = allFields.fields;

          this.allFields = this.allFields.filter(x => {
            return x.fieldType.name != FieldTypeName.LABEL &&
              x.fieldType.name != FieldTypeName.YEAR_CITATION
          });
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

    this.scientistService.getAllScientistPreview().subscribe({
      next: (data: ScientistPreview[]) => {
        this.scientists = data;
      }
    })

    this.scientometricSystemService.getAllScientometricSystems().subscribe({
      next: (data: ScientometricSystem[]) => {
        this.scientometricSystems = data;
        this.scientometricSystems.forEach(x => x.name = mapStringToScientometricSystemLabel(x.name));
      }
    })
  }

  canAddProfile() {
    let validate = this.validateCanAddProfile();
    if (validate.length > 0) {
      this.errorCanAddProfile = validate;
      return;
    } else {
      this.errorCanAddProfile = '';
    }

    this.profileService.canAddProfile(this.selectedScientist, this.selectedScientometricSystem).subscribe({
      next: (result: boolean) => {
        if (result) {
          this.profileCanBeAdded = true;
        } else {
          this.errorCanAddProfile = 'Profile for this scientist and scientometric system already exists!';
        }
      }
    })
  }

  changeProfileFieldValue(index: number, newValue: EventTarget | null) {
    if (this.profileFields[index].field.fieldType.name === FieldTypeName.BOOLEAN) {
      this.profileFields[index].value = (newValue as HTMLInputElement).checked ? 'true' : 'false';
    } else {
      this.profileFields[index].value = (newValue as HTMLInputElement).value;
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

    this.profileFields.push(new ProfileField(-1, '', field));

    this.allFields = this.allFields.filter(x => x.id != field.id);

    this.selectedField = 0;
    this.profileFieldsError.push('');
  }

  removeFieldFromProfile(index: number) {
    let profileField = this.profileFields.filter((x, ind) => ind == index)[0];

    this.allFields.push(profileField.field);
    this.profileFields = this.profileFields.filter(x => x.id != profileField.id);

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

  addProfile() {
    this.validateFields();
    if (this.profileFieldsError.filter(x => x !== '').length > 0) {
      return;
    }

    let addProfileDto = new AddProfileDto(this.selectedScientist, this.selectedScientometricSystem,
      this.profileFields, this.profileLabels.map(x => x.id));

    this.profileService.addProfile(addProfileDto).subscribe({
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
    for (let i = 0; i < this.profileFields.length; i++) {
      if (this.profileFields[i].value === '') {
        this.profileFieldsError[i] = 'Enter field!';
        break;
      }

      if (this.profileFields[i].field.fieldType.name === FieldTypeName.NUMBER) {
        try {
          if (isNaN(parseInt(this.profileFields[i].value))
            || isNaN(parseFloat(this.profileFields[i].value))) {
            throw new Error()
          }
          this.profileFieldsError[i] = '';
        } catch (e) {
          this.profileFieldsError[i] = 'Enter number here!';
        }
      }
      else if (this.profileFields[i].field.fieldType.name === FieldTypeName.CITATION ||
        this.profileFields[i].field.fieldType.name === FieldTypeName.H_INDEX
      ) {
        try {
          if (isNaN(parseInt(this.profileFields[i].value))) {
            throw new Error()
          }
          this.profileFieldsError[i] = '';
        } catch (e) {
          this.profileFieldsError[i] = 'Enter integer here!';
        }
      }
    }
  }

  validateCanAddProfile() {
    if (this.selectedScientometricSystem == 0) {
      return 'Select Scientometric System'
    }
    if (this.selectedScientist == 0) {
      return 'Select Scientist';
    }
    return '';
  }
}
