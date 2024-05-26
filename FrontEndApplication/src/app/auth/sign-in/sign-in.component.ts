import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { ValidateEmails } from '../../shared/validators/emails.validator';
import { SignInDto, TokensDto } from 'src/app/shared/models/auth.model';
import { AuthService } from 'src/app/shared/services/auth.service';
import { JWTTokenService } from 'src/app/shared/services/jwt-token.service';

@Component({
  selector: 'app-auth-signIn',
  templateUrl: './sign-in.component.html',
  styleUrls: ['./sign-in.component.css']
})
export class SignInComponent {
  email = '';
  password = '';
  error = '';

  constructor(private readonly router: Router, private readonly authService: AuthService,
    private jwtService: JWTTokenService) {
  }

  signIn() {
    let validationResult = this.validate();
    if (validationResult.length > 0) {
      this.error = validationResult;
      return;
    }

    let signInDto = new SignInDto(this.email, this.password);

    this.authService.signIn(signInDto).subscribe({
      next: (result: TokensDto) => {
        this.jwtService.setToken(result.accessToken);
        this.jwtService.setRefreshToken(result.refreshToken);
      },
      error: (error: any) => {
        this.error = error?.error?.status == 401 ? "Неправильна електронна адреса чи пароль! Спробуйте знову!" : error?.error?.error;
      },
      complete: () => {
        this.router.navigateByUrl("/user/profiles");
      }
    });
  }

  validate(): string {
    if (this.email.length === 0) {
      return "Введіть електронну адресу!";
    }
    if (!ValidateEmails(this.email)) {
      return "Введіть правильну електронну адресу!";
    }
    if (this.password.length < 8) {
      return "Пароль має містити хоча б 8 символів!";
    }
    return '';
  }
}
