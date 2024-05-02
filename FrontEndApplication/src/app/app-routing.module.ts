import { NgModule } from "@angular/core";
import { RouterModule, Routes } from "@angular/router";
import { AuthComponent } from "./auth/auth.component";

const routes: Routes = [
  {
    path: "auth", component: AuthComponent,
    loadChildren: () => import('./auth/auth.module').then(m => m.AuthModule)
  }
]

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
