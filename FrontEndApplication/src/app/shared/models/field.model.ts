import { FieldTypeLabel, FieldTypeName } from "../constants/field-type.constant";
import { Page } from "./page.model";

export class Field {
  constructor(public id: number, public name: string, public canBeDeleted: boolean, public fieldType: FieldType) { }
}

export class FieldType {
  constructor(public id: number, public name: string) { }
}

export class ProfileField {
  constructor(public id: number, public value: string, public field: Field) { }
}

export class GetFieldsDto {
  constructor(public fields: Field[], public pageDto: Page) { }
}

export class CreateFieldDto {
  constructor(public name: string, public typeId: number) {
  }
}

export class UpdateFieldDto {
  constructor(public name: string) { }
}

export class DeleteFieldDto {
  constructor(public replacementFieldId: number) { }
}

export function mapStringToFieldTypeName(name: string): FieldTypeName {
  return FieldTypeName[name as keyof typeof FieldTypeName];
}

export function mapStringToFieldTypeLabel(name: string): FieldTypeLabel {
  return FieldTypeLabel[name as keyof typeof FieldTypeLabel];
}
