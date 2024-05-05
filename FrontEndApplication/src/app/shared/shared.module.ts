import { NgModule } from '@angular/core';

import { PaginationComponent } from './components/pagination/pagination.component';
import { CommonModule } from '@angular/common';
import { ScientistSearchPipe } from './pipes/scientist-search.pipe';
import { LabelSearchPipe } from './pipes/label-search.pipe';

@NgModule({
  declarations: [PaginationComponent, ScientistSearchPipe, LabelSearchPipe],
  imports: [CommonModule],
  exports: [PaginationComponent, ScientistSearchPipe, LabelSearchPipe]
})
export class SharedModule { }
