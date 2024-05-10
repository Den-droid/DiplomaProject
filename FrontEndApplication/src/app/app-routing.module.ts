import { NgModule } from "@angular/core";
import { RouterModule, Routes } from "@angular/router";
import { AuthComponent } from "./auth/auth.component";
import { AdministrationComponent } from "./administration/administration.component";
import { ErrorComponent } from "./error/error.component";

const routes: Routes = [
  {
    path: "auth", component: AuthComponent,
    loadChildren: () => import('./auth/auth.module').then(m => m.AuthModule)
  },
  {
    path: "user", component: AdministrationComponent,
    loadChildren: () => import('./administration/administration.module').then(m => m.AdministrationModule)
  },
  {
    path: "error", component: ErrorComponent,
    loadChildren: () => import('./error/error.module').then(m => m.ErrorModule)
  },
  { path: '**', redirectTo: '/error/404', pathMatch: 'full' }
]

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
