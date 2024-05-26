import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Params, Router } from '@angular/router';
import { SignUpByInviteDto } from 'src/app/shared/models/auth.model';
import { AuthService } from 'src/app/shared/services/auth.service';

@Component({
  selector: 'app-auth-signUp-by-invite',
  templateUrl: './sign-up-by-invite.component.html',
  styleUrls: ['./sign-up-by-invite.component.css']
})
export class SignUpByInviteComponent implements OnInit {
  fullname = '';
  password = '';
  confirmPassword = '';
  error = '';
  inviteCode = '';

  constructor(private readonly router: Router, private readonly authService: AuthService,
    private readonly activatedRoute: ActivatedRoute) {
  }

  ngOnInit(): void {
    this.activatedRoute.params.subscribe((data: Params) => {
      this.inviteCode = data['inviteCode'];
      this.authService.existsByInviteCode(this.inviteCode).subscribe({
        next: (result: boolean) => {
          if (!result) {
            this.router.navigateByUrl("/error/404");
          }
        }
      });
    });
  }

  signUpByInviteCode() {
    let validationResult = this.validate();
    if (validationResult.length > 0) {
      this.error = validationResult;
      return;
    } else {
      this.error = '';
    }

    let signUpByInviteDto = new SignUpByInviteDto(this.fullname, this.password);

    this.authService.signUpByInviteCode(this.inviteCode, signUpByInviteDto).subscribe({
      error: (error: any) => {
        this.error = error?.error?.error;
      },
      complete: () => {
        this.router.navigateByUrl("/auth/signin");
      }
    });
  }

  validate(): string {
    if (this.fullname.length === 0) {
      return "Введіть ім'я!";
    }
    if (this.password.length < 8) {
      return "Пароль має містити хоча б 8 символів!";
    } if (this.confirmPassword.length === 0) {
      return "Введіть підтвердження пароля!";
    }
    if (this.confirmPassword !== this.password) {
      return "Пароль та підтвердження пароля мають збігатись!";
    }
    return '';
  }
}
