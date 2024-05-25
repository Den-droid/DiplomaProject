import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { FieldTypeName } from 'src/app/shared/constants/field-type.constant';
import { Field, ProfileField, GetFieldsDto } from 'src/app/shared/models/field.model';
import { Label, GetLabelsDto } from 'src/app/shared/models/label.model';
import { AddProfileDto } from 'src/app/shared/models/profile.model';
import { ScientistPreview } from 'src/app/shared/models/scientist.model';
import { ScientometricSystem, mapStringToScientometricSystemLabel } from 'src/app/shared/models/scientometric.model';
import { FieldService } from 'src/app/shared/services/field.service';
import { LabelService } from 'src/app/shared/services/label.service';
import { ProfileService } from 'src/app/shared/services/profile.service';
import { ScientistService } from 'src/app/shared/services/scientist.service';
import { ScientometricSystemService } from 'src/app/shared/services/scientometric-system.service';

@Component({
  selector: 'app-administration-profile-add',
  templateUrl: './profile-add.component.html',
  styleUrls: ['./profile-add.component.css']
})
export class ProfileAddComponent implements OnInit {
  constructor(private readonly router: Router, private readonly fieldService: FieldService,
    private readonly profileService: ProfileService, private readonly labelService: LabelService,
    private readonly scientometricSystemService: ScientometricSystemService,
    private readonly scientistService: ScientistService
  ) {
  }

  selectedScientist = 0;

  scientometricSystems: ScientometricSystem[] = [];
  selectedScientometricSystem = 0;

  scientists: ScientistPreview[] = [];
  displayedScientists: ScientistPreview[] = [];

  errorCanAddProfile = '';

  profileCanBeAdded = false;

  allLabels: Label[] = [];
  possibleLabels: Label[] = [];
  profileLabels: Label[] = [];

  allFields: Field[] = [];
  possibleFields: Field[] = [];

  profileFields: ProfileField[] = [];

  _labelSearchQuery = '';
  selectedLabel = 0;

  _fieldSearchQuery = '';
  selectedField = 0;

  selectedFieldError = '';
  selectedLabelError = '';
  profileFieldsError: string[] = [];

  set scientistSearchQuery(value: string) {
    this.selectedScientist = 0;
    this.displayedScientists = this.scientists.filter(x => x.name.toLowerCase().includes(value.toLowerCase()));

    if (this.displayedScientists.length > 0) {
      this.selectedScientist = this.displayedScientists[0].id;
    }
  }

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
    this.labelService.getAllLabels().subscribe({
      next: (allLabels: GetLabelsDto) => {
        this.allLabels = allLabels.labels;
        this.setPossibleLabels();
      }
    })

    this.fieldService.getAllFields().subscribe({
      next: (allFields: GetFieldsDto) => {
        this.allFields = allFields.fields.filter(x => x.fieldType.name != FieldTypeName.LABEL);
        this.setPossibleFields();
      }
    })

    this.scientistService.getAllScientistPreviewByUser().subscribe({
      next: (data: ScientistPreview[]) => {
        this.scientists = data;
        this.displayedScientists = this.scientists;

        if (this.displayedScientists.length > 0) {
          this.selectedScientist = this.displayedScientists[0].id;
        }
      }
    })

    this.scientometricSystemService.getAllScientometricSystems().subscribe({
      next: (data: ScientometricSystem[]) => {
        this.scientometricSystems = data;
        this.scientometricSystems.forEach(x => x.name = mapStringToScientometricSystemLabel(x.name));

        if (this.scientometricSystems.length > 0) {
          this.selectedScientometricSystem = this.scientometricSystems[0].id;
        }
      }
    })
  }

  changeScientometricSystem(event: Event) {
    let eventTarget = event.target as HTMLSelectElement;
    this.selectedScientometricSystem = parseInt(eventTarget.value);

    this.profileCanBeAdded = false;
  }

  changeScientist(event: Event) {
    let eventTarget = event.target as HTMLSelectElement;
    this.selectedScientist = parseInt(eventTarget.value);

    this.profileCanBeAdded = false;
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

    for (let profileField of this.profileFields) {
      this.possibleFields = this.possibleFields.filter(x => x.id != profileField.field.id);
    }
    this.possibleFields = this.possibleFields.filter(x => x.name.toLowerCase().includes(this._fieldSearchQuery.toLowerCase()));

    if (this.possibleFields.length > 0)
      this.selectedField = this.possibleFields[0].id;
  }

  addFieldToProfile() {
    let selectedFieldError = this.validateSelectedField();
    if (selectedFieldError.length !== 0) {
      this.selectedFieldError = selectedFieldError;
      return;
    } else {
      this.selectedFieldError = '';
    }

    let field = this.possibleFields.filter(x => x.id == this.selectedField)[0];

    if (field.fieldType.name === FieldTypeName.BOOLEAN) {
      this.profileFields.push(new ProfileField(-1, 'false', field));
    } else {
      this.profileFields.push(new ProfileField(-1, '', field));
    }

    this.setPossibleFields();

    this.profileFieldsError.push('');
  }

  removeFieldFromProfile(index: number) {
    this.profileFields = this.profileFields.filter((x, ind) => ind != index);

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
    else if (this.possibleFields.length === 0)
      return 'No more Fields available'
    return '';
  }

  validateSelectedLabel() {
    if (this.selectedLabel === 0)
      return 'Select Label';
    else if (this.possibleLabels.length === 0)
      return 'No more Labels available'
    return '';
  }

  validateFields() {
    for (let i = 0; i < this.profileFields.length; i++) {
      if (this.profileFields[i].value === '') {
        this.profileFieldsError[i] = 'Enter field!';
        continue;
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
    if (this.selectedScientist == 0) {
      return 'Select Scientist';
    }
    return '';
  }
}
