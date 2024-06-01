import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { FieldTypeName } from 'src/app/shared/constants/field-type.constant';
import { CreateFieldDto, FieldType, mapStringToFieldTypeLabel } from 'src/app/shared/models/field.model';
import { FieldService } from 'src/app/shared/services/field.service';

@Component({
  selector: 'app-administration-field-add',
  templateUrl: './field-add.component.html',
  styleUrls: ['./field-add.component.css']
})
export class FieldAddComponent implements OnInit {
  name = '';
  error = '';
  selectedType = 0;
  fieldTypes: FieldType[] = [];

  constructor(private readonly router: Router, private readonly fieldService: FieldService) {
  }

  ngOnInit(): void {
    this.fieldService.getAllFieldTypes().subscribe({
      next: (data: FieldType[]) => {
        this.fieldTypes = data;
        this.fieldTypes = this.fieldTypes.filter(x => x.name != FieldTypeName.LABEL &&
          x.name != FieldTypeName.CITATION && x.name != FieldTypeName.H_INDEX
        )
        this.fieldTypes.forEach(fieldType => fieldType.name = mapStringToFieldTypeLabel(fieldType.name));
        if (this.fieldTypes.length > 0)
          this.selectedType = this.fieldTypes[0].id;
      }
    });
  }

  addField() {
    let validationResult = this.validate();
    if (validationResult.length > 0) {
      this.error = validationResult;
      return;
    } else {
      this.error = '';
    }

    let createFieldDto = new CreateFieldDto(this.name, this.selectedType);

    this.fieldService.create(createFieldDto).subscribe({
      error: (error: any) => {
        this.error = error?.error?.error;
      },
      complete: () => {
        this.router.navigateByUrl("/user/fields");
      }
    })
  }

  validate(): string {
    if (this.name.length === 0) {
      return "Введіть назву поля!";
    }
    if (this.selectedType === 0) {
      return "Виберіть тип поля!";
    }
    return '';
  }
}
