import { NgModule } from '@angular/core';

import { PaginationComponent } from './components/pagination/pagination.component';
import { CommonModule } from '@angular/common';
import { ScientistSearchPipe } from './pipes/scientist-search.pipe';
import { LabelSearchPipe } from './pipes/label-search.pipe';
import { ChairsByFacultyPipe } from './pipes/chairs-by-faculty.pipe';
import { FieldSearchPipe } from './pipes/field-search.pipe';

@NgModule({
  declarations: [PaginationComponent, ScientistSearchPipe, LabelSearchPipe, ChairsByFacultyPipe, FieldSearchPipe],
  imports: [CommonModule],
  exports: [PaginationComponent, ScientistSearchPipe, LabelSearchPipe, ChairsByFacultyPipe, FieldSearchPipe]
})
export class SharedModule { }
