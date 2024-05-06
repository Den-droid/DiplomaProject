import { Pipe, PipeTransform } from "@angular/core";
import { Chair } from "../models/chair.model";

@Pipe({
  name: 'chairs_by_faculty'
})
export class ChairsByFacultyPipe implements PipeTransform {
  transform(chairs: Chair[], facultyId: number): Chair[] {
    return chairs.filter(chair => chair.facultyId == facultyId);
  }
}
