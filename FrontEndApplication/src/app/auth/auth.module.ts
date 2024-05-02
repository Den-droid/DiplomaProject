import { NgModule } from '@angular/core';
import { AuthComponent } from './auth.component';
import { SignInComponent } from './sign-in/sign-in.component';
import { Router, RouterModule, Routes } from '@angular/router';
import { AuthService } from './services/auth.service';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { HTTP_INTERCEPTORS } from '@angular/common/http';
import { AuthInterceptor } from './interceptors/auth.interceptor';
import { SignUpComponent } from './sign-up/sign-up.component';
import { SearchPipe } from './pipes/search.pipe';
import { SignUpSuccessComponent } from './sign-up-success/sign-up-success.component';
import { ForgotPasswordComponent } from './forgot-password/forgot-password.component';
import { ForgotPasswordSuccessComponent } from './forgot-password-success/forgot-password-success.component';
import { SetNewPasswordComponent } from './set-new-password/set-new-password.component';

const authRoutes: Routes = [
  { path: "signin", component: SignInComponent },
  { path: "signup", component: SignUpComponent },
  { path: "signup/success/:dummy", component: SignUpSuccessComponent },
  { path: "forgotpassword", component: ForgotPasswordComponent },
  { path: "forgotpassword/success/:dummy", component: ForgotPasswordSuccessComponent },
  { path: "forgotpassword/:token", component: SetNewPasswordComponent },
  { path: "signupbyinvitecode/:inviteCode", component: SignUpSuccessComponent }
]

@NgModule({
  declarations: [
    AuthComponent, SignInComponent, SignUpComponent, SignUpSuccessComponent,
    ForgotPasswordComponent, ForgotPasswordSuccessComponent,
    SetNewPasswordComponent, SearchPipe
  ],
  imports: [
    CommonModule, FormsModule, RouterModule.forChild(authRoutes)
  ],
  exports: [RouterModule],
  providers: [AuthService,
    {
      provide: HTTP_INTERCEPTORS,
      useFactory: function (router: Router) {
        return new AuthInterceptor(router);
      },
      multi: true,
      deps: [Router]
    },
  ],
  bootstrap: [AuthComponent]
})
export class AuthModule { }
