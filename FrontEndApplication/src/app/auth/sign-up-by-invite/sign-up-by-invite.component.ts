import { Component, OnInit } from '@angular/core';
import { SignUpByInviteDto, SignUpDto, SignUpScientistDto } from '../models/auth.model';
import { ActivatedRoute, Params, Router } from '@angular/router';
import { AuthService } from '../services/auth.service';
import { ValidateEmails } from '../../shared/validators/emails.validator';

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
      console.log(this.inviteCode);
      this.authService.existsByInviteCode(this.inviteCode).subscribe({
        next: (result: boolean) => {
          if (!result) {
            this.router.navigateByUrl("/auth/signin");
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
        this.clearPasswordFullname();
      },
      complete: () => {
        this.clear();
        this.router.navigateByUrl("/auth/signin");
      }
    });
  }

  validate(): string {
    if (this.fullname.length === 0) {
      return "Enter fullname!";
    }
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
    this.fullname = '';
    this.error = '';
  }

  clearPasswordFullname() {
    this.password = '';
    this.confirmPassword = '';
    this.fullname = '';
  }
}
