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
import { MainAdminGuard } from '../shared/guards/main-admin.guard';
import { AnyAdminGuard } from '../shared/guards/any-admin.guard';
import { AuthenticatedGuard } from '../shared/guards/authenticated.guard';

const userRoutes: Routes = [
  { path: "extraction", component: ExtractionComponent, canActivate: [MainAdminGuard] },
  { path: "labels", component: LabelComponent, canActivate: [MainAdminGuard] },
  { path: "labels/add", component: LabelAddComponent, canActivate: [MainAdminGuard] },
  { path: "labels/:id/edit", component: LabelEditComponent, canActivate: [MainAdminGuard] },
  { path: "labels/:id/delete", component: LabelDeleteComponent, canActivate: [MainAdminGuard] },
  { path: "users", component: UserComponent, canActivate: [AnyAdminGuard] },
  { path: "users/add", component: UserAddComponent, canActivate: [MainAdminGuard] },
  { path: "users/:id/edit", component: UserEditComponent, canActivate: [AnyAdminGuard] },
  { path: "profiles", component: ProfileComponent, canActivate: [AuthenticatedGuard] },
  { path: "profiles/add", component: ProfileAddComponent, canActivate: [AuthenticatedGuard] },
  { path: "profiles/:id/edit", component: ProfileEditComponent, canActivate: [AuthenticatedGuard] },
  { path: "fields", component: FieldComponent, canActivate: [MainAdminGuard] },
  { path: "fields/add", component: FieldAddComponent, canActivate: [MainAdminGuard] },
  { path: "fields/:id/edit", component: FieldEditComponent, canActivate: [MainAdminGuard] },
  { path: "fields/:id/delete", component: FieldDeleteComponent, canActivate: [MainAdminGuard] },
  { path: "settings", component: SettingsComponent, canActivate: [AuthenticatedGuard] },
  { path: "**", redirectTo: "/error/404" }
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
