import { Component, OnInit } from '@angular/core';
import { SignUpDto, SignUpScientistDto } from '../models/auth.model';
import { Router } from '@angular/router';
import { AuthService } from '../services/auth.service';
import { ValidateEmails } from '../../shared/validators/emails.validator';
import { v4 as uuidv4 } from 'uuid';

@Component({
  selector: 'app-auth-signUp',
  templateUrl: './sign-up.component.html',
  styleUrls: ['./sign-up.component.css']
})
export class SignUpComponent implements OnInit {
  email = '';
  password = '';
  confirmPassword = '';
  scientists: SignUpScientistDto[] = [];
  selectedScientist = 0;

  _searchQuery = '';

  public get searchQuery(): string {
    return this._searchQuery;
  }

  public set searchQuery(v: string) {
    this._searchQuery = v;
    this.selectedScientist = 0;
  }

  error = '';
  uuid = '';

  constructor(private readonly router: Router, private readonly authService: AuthService) {
  }

  ngOnInit(): void {
    this.uuid = uuidv4();
    this.authService.getSignUp().subscribe({
      next: (result: SignUpScientistDto[]) => {
        this.scientists = result;
        this.selectedScientist = this.scientists[0].id;
      }
    });
  }

  signUp() {
    let validationResult = this.validate();
    if (validationResult.length > 0) {
      this.error = validationResult;
      return;
    } else {
      this.error = '';
    }

    let signUpDto = new SignUpDto(this.email, this.password, this.selectedScientist);

    this.authService.signUp(signUpDto).subscribe({
      error: (error: any) => {
        this.error = error?.error?.error;
        this.clearEmailPasswordFullname();
      },
      complete: () => {
        this.clear();
        this.router.navigateByUrl("/auth/signup/success/" + this.uuid);
      }
    });
  }

  validate(): string {
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
    this.error = '';
  }

  clearEmailPasswordFullname() {
    this.email = '';
    this.password = '';
    this.confirmPassword = '';
  }
}
