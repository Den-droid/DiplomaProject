import { Pipe, PipeTransform } from "@angular/core";
import { Field } from "../models/field.model";

@Pipe({
  name: 'field_search'
})
export class FieldSearchPipe implements PipeTransform {
  transform(fields: Field[], search: string = ''): Field[] {
    if (!search.trim()) {
      return fields;
    }
    return fields.filter(field => {
      return field.name.toLowerCase().includes(search.toLowerCase())
    })
  }
}
