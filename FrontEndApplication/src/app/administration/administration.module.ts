import { NgModule } from '@angular/core';

import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule, Routes } from '@angular/router';
import { LabelComponent } from './labels/label.component';
import { ExtractionComponent } from './extraction/extraction.component';
import { SharedModule } from '../shared/shared.module';
import { LabelAddComponent } from './labels/add/label-add.component';
import { LabelDeleteComponent } from './labels/delete/label-delete.component';
import { LabelEditComponent } from './labels/edit/label-edit.component';
import { AdministrationComponent } from './administration.component';
import { UserComponent } from './users/user.component';
import { HTTP_INTERCEPTORS } from '@angular/common/http';
import { AuthorizeInterceptor } from '../shared/interceptors/authorize.interceptor';
import { UserAddComponent } from './users/user-add/user-add.component';
import { UserEditComponent } from './users/user-edit/user-edit.component';
import { ProfileComponent } from './profiles/profile.component';
import { SettingsComponent } from './settings/settings.component';
import { ProfileAddComponent } from './profiles/profile-add/profile-add.component';
import { ProfileEditComponent } from './profiles/profile-edit/profile-edit.component';
import { FieldComponent } from './fields/field.component';
import { FieldAddComponent } from './fields/add/field-add.component';
import { FieldEditComponent } from './fields/edit/field-edit.component';
import { FieldDeleteComponent } from './fields/delete/field-delete.component';

const userRoutes: Routes = [
  { path: "extraction", component: ExtractionComponent },
  { path: "labels", component: LabelComponent },
  { path: "labels/add", component: LabelAddComponent },
  { path: "labels/:id/edit", component: LabelEditComponent },
  { path: "labels/:id/delete", component: LabelDeleteComponent },
  { path: "users", component: UserComponent },
  { path: "users/add", component: UserAddComponent },
  { path: "users/:id/edit", component: UserEditComponent },
  { path: "profiles", component: ProfileComponent },
  { path: "profiles/add", component: ProfileAddComponent },
  { path: "profiles/:id/edit", component: ProfileEditComponent },
  { path: "fields", component: FieldComponent },
  { path: "fields/add", component: FieldAddComponent },
  { path: "fields/:id/edit", component: FieldEditComponent },
  { path: "fields/:id/delete", component: FieldDeleteComponent },
  { path: "settings", component: SettingsComponent },
]

@NgModule({
  declarations: [
    AdministrationComponent, ExtractionComponent,
    LabelComponent, LabelAddComponent, LabelDeleteComponent, LabelEditComponent,
    UserComponent, UserAddComponent, UserEditComponent,
    ProfileComponent, ProfileAddComponent, ProfileEditComponent,
    FieldComponent, FieldAddComponent, FieldEditComponent, FieldDeleteComponent,
    SettingsComponent
  ],
  imports: [
    CommonModule, FormsModule, SharedModule, RouterModule.forChild(userRoutes)
  ],
  exports: [
    RouterModule
  ],
  providers: [
    {
      provide: HTTP_INTERCEPTORS,
      useClass: AuthorizeInterceptor, multi: true,
    }
  ],
  bootstrap: [AdministrationComponent]
})
export class AdministrationModule { }
