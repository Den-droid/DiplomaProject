import { NgModule } from '@angular/core';

import { PaginationComponent } from './components/pagination/pagination.component';
import { CommonModule } from '@angular/common';
import { JoinStringsPipe } from './pipes/join.pipe';

@NgModule({
  declarations: [PaginationComponent, JoinStringsPipe],
  imports: [CommonModule],
  exports: [PaginationComponent, JoinStringsPipe]
})
export class SharedModule { }
