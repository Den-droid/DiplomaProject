import { NgModule } from '@angular/core';

import { PaginationComponent } from './components/pagination/pagination.component';
import { CommonModule } from '@angular/common';
import { ScientistSearchPipe } from './pipes/scientist-search.pipe';
import { LabelSearchPipe } from './pipes/label-search.pipe';
import { ChairsByFacultyPipe } from './pipes/chairs-by-faculty.pipe';

@NgModule({
  declarations: [PaginationComponent, ScientistSearchPipe, LabelSearchPipe, ChairsByFacultyPipe],
  imports: [CommonModule],
  exports: [PaginationComponent, ScientistSearchPipe, LabelSearchPipe, ChairsByFacultyPipe]
})
export class SharedModule { }
