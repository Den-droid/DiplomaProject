import { NgModule } from '@angular/core';

import { ErrorComponent } from './error.component';
import { RouterModule, Routes } from '@angular/router';
import { NotFoundComponent } from './not-found/not-found.component';
import { ForbiddenComponent } from './forbidden/forbidden.component';

const errorRoutes: Routes = [
  { path: "404", component: NotFoundComponent, title: 'Page Not Found' },
  { path: "403", component: ForbiddenComponent, title: 'No Access' }
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
