import { Component, OnInit } from '@angular/core';
import { SignUpDto, SignUpScientistDto } from '../models/auth.model';
import { Router } from '@angular/router';
import { AuthService } from '../services/auth.service';
import { ValidateEmails } from '../functions/emails.validator';

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

  constructor(private readonly router: Router, private readonly authService: AuthService) {
  }

  ngOnInit(): void {

  }

  signUpSuccess() {
    this.router.navigateByUrl("/auth/signin");
  }

  signUp() {
    let validationResult = this.validate();
    if (validationResult.length > 0) {
      this.error = validationResult;
      return;
    } else {
      this.error = '';
    }

    let signUpDto = new SignUpDto(this.fullname, this.email, this.password, this.selectedScientist);
    console.log(signUpDto);

    // this.authService.signUp(signUpDto).subscribe({
    //   error: (error: any) => {
    //     this.error = error?.error?.error;
    //     this.clearEmailPasswordFullname();
    //   },
    //   complete: () => {
    //     this.clear();
    //     this.router.navigateByUrl("/");
    //   }
    // });
  }

  validate(): string {
    if (this.fullname.length === 0) {
      return "Enter fullname!";
    }
    if (this.email.length === 0) {
      return "Enter email!";
    } if (!ValidateEmails(this.email)) {
      return "Enter correct email!";
    }
    if (this.password.length < 8) {
      return "Password must be at least 8 characters long!";
    } if (this.confirmPassword.length === 0) {
      return "Enter confirm password!";
    }
    if (this.confirmPassword !== this.password) {
      return "Password and confirm password must match!";
    }
    if (this.selectedScientist === 0) {
      return "Select scientist!";
    }
    return '';
  }

  clear() {
    this.email = '';
    this.password = '';
    this.confirmPassword = '';
    this.fullname = '';
    this.error = '';
  }

  clearEmailPasswordFullname() {
    this.email = '';
    this.password = '';
    this.confirmPassword = '';
    this.fullname = '';
  }
}
