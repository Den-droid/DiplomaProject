import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { SharedModule } from '../shared/shared.module';
import { HomeComponent } from './home.component';
import { NgApexchartsModule } from 'ng-apexcharts';
import { FacultiesComponent } from './faculties/faculties.component';
import { ChairsComponent } from './chairs/chairs.component';
import { CommonLabelsComponent } from './common-labels/common-labels.component';
import { ProfilesComponent } from './profiles/profiles.component';

const homeRoutes: Routes = [
  { path: "", pathMatch: "full", redirectTo: "faculties" },
  { path: "faculties/chairs", component: ChairsComponent, title: 'Faculty Chairs Indices' },
  { path: "faculties", component: FacultiesComponent, title: 'Faculties Indices' },
  { path: "commonlabels", component: CommonLabelsComponent, title: 'Common Labels' },
  { path: "profiles", component: ProfilesComponent, title: 'Profiles' }
]

@NgModule({
  declarations: [
    HomeComponent, FacultiesComponent, ChairsComponent, CommonLabelsComponent, ProfilesComponent
  ],
  imports: [
    CommonModule, FormsModule, SharedModule, NgApexchartsModule, RouterModule.forChild(homeRoutes)
  ],
  exports: [RouterModule],
  bootstrap: [HomeComponent]
})
export class HomeModule { }
