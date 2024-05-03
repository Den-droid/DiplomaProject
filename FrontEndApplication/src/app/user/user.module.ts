import { NgModule } from '@angular/core';

import { UserComponent } from './user.component';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule, Routes } from '@angular/router';
import { LabelComponent } from './labels/label.component';
import { ExtractionComponent } from './extraction/extraction.component';
import { ExtractionService } from './services/extraction.service';
import { LabelService } from './services/label.service';
import { SharedModule } from '../shared/shared.module';
import { LabelAddComponent } from './labels/add/label-add.component';
import { LabelDeleteComponent } from './labels/delete/label-delete.component';
import { LabelEditComponent } from './labels/edit/label-edit.component';
import { LabelSearchPipe } from './pipes/label-search.pipe';

const userRoutes: Routes = [
  { path: "extraction", component: ExtractionComponent },
  { path: "labels", component: LabelComponent },
  { path: "labels/add", component: LabelAddComponent },
  { path: "labels/edit/:id", component: LabelEditComponent },
  { path: "labels/delete/:id", component: LabelDeleteComponent }
]

@NgModule({
  declarations: [
    UserComponent, ExtractionComponent,
    LabelComponent, LabelAddComponent, LabelDeleteComponent, LabelEditComponent, LabelSearchPipe
  ],
  imports: [
    CommonModule, FormsModule, SharedModule, RouterModule.forChild(userRoutes)
  ],
  exports: [
    RouterModule
  ],
  providers: [ExtractionService, LabelService],
  bootstrap: [UserComponent]
})
export class UserModule { }
