import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { ValidateEmails } from '../../shared/validators/emails.validator';
import { v4 as uuidv4 } from 'uuid';
import { AuthService } from 'src/app/shared/services/auth.service';
import { ForgotPasswordDto } from 'src/app/shared/models/auth.model';

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
      },
      complete: () => {
        this.router.navigateByUrl("/auth/forgotpassword/success/" + this.uuid);
      }
    });
  }

  validate(): string {
    if (this.email.length === 0) {
      return 'Введіть електронну адресу!';
    }
    if (!ValidateEmails(this.email)) {
      return 'Введіть правильну електронну адресу!'
    }
    return '';
  }
}
