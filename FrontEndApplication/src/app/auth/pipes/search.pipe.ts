import { Pipe, PipeTransform } from "@angular/core";
import { SignUpScientistDto } from "../models/auth.model";

@Pipe({
  name: 'search'
})
export class SearchPipe implements PipeTransform {
  transform(scientists: SignUpScientistDto[], search: string = ''): SignUpScientistDto[] {
    if (!search.trim()) {
      return scientists;
    }
    return scientists.filter(scientist => {
      return scientist.scientistName.toLowerCase().includes(search.toLowerCase())
    })
  }
}
