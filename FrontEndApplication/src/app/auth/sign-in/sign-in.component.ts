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
        this.error = error?.error?.status == 401 ? "Invalid username or password. Try again!" : error?.error?.error;
      },
      complete: () => {
        this.router.navigateByUrl("/user/profiles");
      }
    });
  }

  validate(): string {
    if (this.email.length === 0) {
      return "Enter email!";
    }
    if (this.password.length < 8) {
      return "Password must be at least 8 characters long!";
    } if (!ValidateEmails(this.email)) {
      return "Enter correct email!";
    }
    return '';
  }
}
