import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { SharedModule } from '../shared/shared.module';
import { HomeComponent } from './home.component';
import { NgApexchartsModule } from 'ng-apexcharts';
import { FacultiesComponent } from './faculties/faculties.component';
import { ChairsComponent } from './chairs/chairs.component';

const homeRoutes: Routes = [
  { path: "", pathMatch: "full", redirectTo: "faculties"},
  { path: "faculties/chairs", component: ChairsComponent },
  { path: "faculties", component: FacultiesComponent },
  // { path: "commonlabels" },
  // { path: "scientists/data" }
]

@NgModule({
  declarations: [
    HomeComponent, FacultiesComponent, ChairsComponent
  ],
  imports: [
    CommonModule, FormsModule, SharedModule, NgApexchartsModule, RouterModule.forChild(homeRoutes)
  ],
  exports: [RouterModule],
  bootstrap: [HomeComponent]
})
export class HomeModule { }
