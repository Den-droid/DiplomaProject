import { Component, OnInit } from '@angular/core';
import { ForgotPasswordDto, SignUpDto, SignUpScientistDto } from '../models/auth.model';
import { Router } from '@angular/router';
import { AuthService } from '../services/auth.service';
import { ValidateEmails } from '../functions/emails.validator';
import { v4 as uuidv4 } from 'uuid';

@Component({
  selector: 'app-auth-forgot-password',
  templateUrl: './forgot-password.component.html',
  styleUrls: ['./forgot-password.component.css']
})
export class ForgotPasswordComponent implements OnInit {
  email = '';
  error = '';
  uuid = '';

  constructor(private readonly router: Router, private readonly authService: AuthService) {
  }

  ngOnInit(): void {
    this.uuid = uuidv4();
  }

  forgotPassword() {
    let validationResult = this.validate();
    if (validationResult.length > 0) {
      this.error = validationResult;
      return;
    } else {
      this.error = '';
    }

    let forgotPasswordDto = new ForgotPasswordDto(this.email);

    this.authService.forgotPassword(forgotPasswordDto).subscribe({
      error: (error: any) => {
        this.error = error?.error?.error;
        this.clearEmail();
      },
      complete: () => {
        this.clear();
        this.router.navigateByUrl("/auth/forgotpassword/success/" + this.uuid);
      }
    });
  }

  validate(): string {
    if (this.email.length === 0) {
      return "Enter email!";
    } if (!ValidateEmails(this.email)) {
      return "Enter correct email!";
    }
    return '';
  }

  clear() {
    this.email = '';
    this.error = '';
  }

  clearEmail() {
    this.email = '';
  }
}
