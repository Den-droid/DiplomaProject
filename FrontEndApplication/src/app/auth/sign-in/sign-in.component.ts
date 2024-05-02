import { Component, OnInit } from '@angular/core';
import { RoleTokensDto, SignInDto } from '../models/auth.model';
import { Router } from '@angular/router';
import { AuthService } from '../services/auth.service';
import { ValidateEmails } from '../functions/emails.validator';

@Component({
  selector: 'app-auth-signIn',
  templateUrl: './sign-in.component.html',
  styleUrls: ['./sign-in.component.css']
})
export class SignInComponent {
  email = '';
  password = '';
  error = '';

  constructor(private readonly router: Router, private readonly authService: AuthService) {
  }

  signIn() {
    let validationResult = this.validate();
    if (validationResult.length > 0) {
      this.error = validationResult;
      return;
    }

    let signInDto = new SignInDto(this.email, this.password);

    this.authService.signIn(signInDto).subscribe({
      next: (result: RoleTokensDto) => {
        console.log(result)
      },
      error: (error: any) => {
        this.error = error?.error?.status == 401 ? "Invalid username or password. Try again!" : error?.error?.error;
        this.clearEmailPassword();
      },
      complete: () => {
        this.clear();
        this.router.navigateByUrl("/");
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

  clear() {
    this.email = '';
    this.password = '';
    this.error = '';
  }

  clearEmailPassword() {
    this.email = '';
    this.password = '';
  }
}
