import { NgModule } from '@angular/core';

import { ErrorComponent } from './error.component';
import { RouterModule, Routes } from '@angular/router';
import { NotFoundComponent } from './not-found/not-found.component';
import { ForbiddenComponent } from './forbidden/forbidden.component';

const errorRoutes: Routes = [
  { path: "404", component: NotFoundComponent },
  { path: "403", component: ForbiddenComponent }
];

@NgModule({
  declarations: [
    ErrorComponent, NotFoundComponent, ForbiddenComponent
  ],
  imports: [
    RouterModule.forChild(errorRoutes)
  ],
  providers: [RouterModule],
  bootstrap: [ErrorComponent]
})
export class ErrorModule { }
