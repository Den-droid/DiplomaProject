import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { ValidateEmails } from '../../shared/validators/emails.validator';
import { v4 as uuidv4 } from 'uuid';
import { SignUpDto } from 'src/app/shared/models/auth.model';
import { AuthService } from 'src/app/shared/services/auth.service';
import { ScientistPreview } from 'src/app/shared/models/scientist.model';
import { ScientistService } from 'src/app/shared/services/scientist.service';

@Component({
  selector: 'app-auth-signUp',
  templateUrl: './sign-up.component.html',
  styleUrls: ['./sign-up.component.css']
})
export class SignUpComponent implements OnInit {
  email = '';
  password = '';
  confirmPassword = '';

  scientists: ScientistPreview[] = [];
  displayedScientists: ScientistPreview[] = [];

  selectedScientist = 0;

  error = '';
  uuid = '';

  public set searchQuery(searchQuery: string) {
    this.selectedScientist = 0;
    this.displayedScientists = this.scientists.filter(x => x.name.toLowerCase().includes(searchQuery.toLowerCase()));

    if (this.displayedScientists.length > 0) {
      this.selectedScientist = this.displayedScientists[0].id;
    }
  }

  constructor(private readonly router: Router, private readonly authService: AuthService,
    private readonly scientistService: ScientistService
  ) {
  }

  ngOnInit(): void {
    this.uuid = uuidv4();
    this.scientistService.getNotRegisteredScientists().subscribe({
      next: (result: ScientistPreview[]) => {
        this.scientists = result;
        this.displayedScientists = this.scientists;

        if (this.displayedScientists.length > 0) {
          this.selectedScientist = this.displayedScientists[0].id;
        }
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
      },
      complete: () => {
        this.router.navigateByUrl("/auth/signup/success/" + this.uuid);
      }
    });
  }

  validate(): string {
    if (this.email.length === 0) {
      return "Введіть елетронну адресу!";
    } if (!ValidateEmails(this.email)) {
      return "Введіть правильну електронну адресу";
    }
    if (this.password.length < 8) {
      return "Пароль має містити хоча б 8 символів!!";
    } if (this.confirmPassword.length === 0) {
      return "Введіть підтвердження пароля!";
    }
    if (this.confirmPassword !== this.password) {
      return "Пароль та підтвердження пароля мають збігатись!";
    }
    if (this.selectedScientist === 0) {
      return "Виберіть науковця!";
    }
    return '';
  }
}
