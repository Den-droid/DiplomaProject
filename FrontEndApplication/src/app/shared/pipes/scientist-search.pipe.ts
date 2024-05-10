import { Pipe, PipeTransform } from "@angular/core";
import { ScientistPreview } from "../models/scientist.model";

@Pipe({
  name: 'scientist_search'
})
export class ScientistSearchPipe implements PipeTransform {
  transform(scientists: ScientistPreview[], search: string = ''): ScientistPreview[] {
    if (!search.trim()) {
      return scientists;
    }
    return scientists.filter(scientist => {
      return scientist.name.toLowerCase().includes(search.toLowerCase())
    })
  }
}
