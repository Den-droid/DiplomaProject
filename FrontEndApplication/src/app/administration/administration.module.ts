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

const userRoutes: Routes = [
  { path: "extraction", component: ExtractionComponent },
  { path: "labels", component: LabelComponent },
  { path: "labels/add", component: LabelAddComponent },
  { path: "labels/edit/:id", component: LabelEditComponent },
  { path: "labels/delete/:id", component: LabelDeleteComponent },
  { path: "users", component: UserComponent },
  { path: "users/add", component: UserAddComponent },
  { path: "users/edit/:id", component: UserEditComponent },
  { path: "profiles", component: ProfileComponent },
  { path: "settings", component: SettingsComponent }
]

@NgModule({
  declarations: [
    AdministrationComponent, ExtractionComponent,
    LabelComponent, LabelAddComponent, LabelDeleteComponent, LabelEditComponent,
    UserComponent, UserAddComponent, UserEditComponent,
    ProfileComponent,
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
