import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Params, Router } from '@angular/router';
import { ChangePasswordDto } from 'src/app/shared/models/auth.model';
import { AuthService } from 'src/app/shared/services/auth.service';

@Component({
  selector: 'app-auth-set-new-password',
  templateUrl: './set-new-password.component.html',
  styleUrls: ['./set-new-password.component.css']
})
export class SetNewPasswordComponent implements OnInit {
  password = '';
  confirmPassword = '';
  error = '';
  token = ''

  constructor(private readonly router: Router, private readonly authService: AuthService,
    private readonly activatedRoute: ActivatedRoute) { }

  ngOnInit(): void {
    this.activatedRoute.params.subscribe((data: Params) => {
      this.token = data['token'];
      this.authService.existsByForgotPasswordToken(this.token).subscribe({
        next: (result: boolean) => {
          console.log(result);
          if (!result) {
            this.router.navigateByUrl("/error/404");
          }
        }
      });
    });
  }

  setNewPassword() {
    let validationResult = this.validate();
    if (validationResult.length > 0) {
      this.error = validationResult;
      return;
    } else {
      this.error = '';
    }

    let changePasswordDto = new ChangePasswordDto(this.password);

    this.authService.changePassword(this.token, changePasswordDto).subscribe({
      error: (error: any) => {
        this.error = error?.error?.error;
        this.clearPasswordFullname();
      },
      complete: () => {
        this.clear();
        this.router.navigateByUrl("/auth/signin");
      }
    });
  }

  validate(): string {
    if (this.password.length < 8) {
      return "Password must be at least 8 characters long!";
    } if (this.confirmPassword.length === 0) {
      return "Enter confirm password!";
    }
    if (this.confirmPassword !== this.password) {
      return "Password and confirm password must match!";
    }
    return '';
  }

  clear() {
    this.password = '';
    this.confirmPassword = '';
    this.error = '';
  }

  clearPasswordFullname() {
    this.password = '';
    this.confirmPassword = '';
  }
}
