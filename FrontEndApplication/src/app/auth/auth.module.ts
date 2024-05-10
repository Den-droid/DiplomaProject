import { NgModule } from '@angular/core';
import { AuthComponent } from './auth.component';
import { SignInComponent } from './sign-in/sign-in.component';
import { RouterModule, Routes } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { SignUpComponent } from './sign-up/sign-up.component';
import { SignUpSuccessComponent } from './sign-up-success/sign-up-success.component';
import { ForgotPasswordComponent } from './forgot-password/forgot-password.component';
import { ForgotPasswordSuccessComponent } from './forgot-password-success/forgot-password-success.component';
import { SetNewPasswordComponent } from './set-new-password/set-new-password.component';
import { SignUpByInviteComponent } from './sign-up-by-invite/sign-up-by-invite.component';
import { SharedModule } from '../shared/shared.module';

const authRoutes: Routes = [
  { path: "signin", component: SignInComponent },
  { path: "signup", component: SignUpComponent },
  { path: "signup/success/:dummy", component: SignUpSuccessComponent },
  { path: "forgotpassword", component: ForgotPasswordComponent },
  { path: "forgotpassword/success/:dummy", component: ForgotPasswordSuccessComponent },
  { path: "forgotpassword/:token", component: SetNewPasswordComponent },
  { path: "signupbyinvitecode/:inviteCode", component: SignUpByInviteComponent },
  { path: "**", redirectTo: "/error/404" }
]

@NgModule({
  declarations: [
    AuthComponent, SignInComponent, SignUpComponent, SignUpSuccessComponent,
    ForgotPasswordComponent, ForgotPasswordSuccessComponent,
    SetNewPasswordComponent, SignUpByInviteComponent
  ],
  imports: [
    CommonModule, FormsModule, SharedModule, RouterModule.forChild(authRoutes)
  ],
  exports: [RouterModule],
  bootstrap: [AuthComponent]
})
export class AuthModule { }
