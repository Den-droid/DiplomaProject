import { Pipe, PipeTransform } from "@angular/core";
import { Label } from "../models/label.model";

@Pipe({
  name: 'label_search'
})
export class LabelSearchPipe implements PipeTransform {
  transform(labels: Label[], search: string = ''): Label[] {
    if (!search.trim()) {
      return labels;
    }
    return labels.filter(label => {
      return label.name.toLowerCase().includes(search.toLowerCase())
    })
  }
}
