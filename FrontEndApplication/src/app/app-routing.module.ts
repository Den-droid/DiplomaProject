import { NgModule } from "@angular/core";
import { RouterModule, Routes } from "@angular/router";
import { AuthComponent } from "./auth/auth.component";
import { AdministrationModule } from "./administration/administration.module";
import { AdministrationComponent } from "./administration/administration.component";

const routes: Routes = [
  {
    path: "auth", component: AuthComponent,
    loadChildren: () => import('./auth/auth.module').then(m => m.AuthModule)
  },
  {
    path: "user", component: AdministrationComponent,
    loadChildren: () => import('./administration/administration.module').then(m => m.AdministrationModule)
  }
]

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
