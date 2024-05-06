import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';

import { Chair } from 'src/app/shared/models/chair.model';
import { Faculty } from 'src/app/shared/models/faculty.model';
import { AddAdminDto } from 'src/app/shared/models/user.model';
import { ChairService } from 'src/app/shared/services/chair.service';
import { FacultyService } from 'src/app/shared/services/faculty.service';
import { UserService } from 'src/app/shared/services/user.service';
import { ValidateEmails } from 'src/app/shared/validators/emails.validator';

@Component({
  selector: 'app-administration-user-add',
  templateUrl: './user-add.component.html',
  styleUrls: ['./user-add.component.css']
})
export class UserAddComponent implements OnInit {
  constructor(private readonly router: Router, private readonly userService: UserService,
    private readonly facultyService: FacultyService, private readonly chairService: ChairService) { }

  email = '';
  errorEmail = '';
  errorFaculty = '';

  faculties: Faculty[] = [];
  chairs: Chair[] = [];

  selectedFaculty = 0;
  selectedChair = 0;
  wholeFaculty = false;

  isMainAdminCreated = false;

  ngOnInit(): void {
    this.facultyService.getAll().subscribe({
      next: (data: Faculty[]) => {
        this.faculties = data;
      }
    });
    this.chairService.getAll().subscribe({
      next: (data: Chair[]) => {
        this.chairs = data;
      }
    })
  }

  addAdmin() {
    let emailCorrect = this.validateEmail();
    if (emailCorrect.length > 0) {
      this.errorEmail = emailCorrect
      return;
    } else {
      this.errorEmail = '';
    }

    if (!this.isMainAdminCreated) {
      let facultyChairCorrect = this.validateFacultyChair();
      if (facultyChairCorrect.length > 0) {
        this.errorFaculty = facultyChairCorrect;
        return;
      } else {
        this.errorFaculty = '';
      }
    }

    let addAdminDto;
    if (this.wholeFaculty) {
      addAdminDto = new AddAdminDto(this.email, [this.selectedFaculty], [], this.isMainAdminCreated);
    } else {
      addAdminDto = new AddAdminDto(this.email, [], [this.selectedChair], this.isMainAdminCreated);
    }

    this.userService.addAdmin(addAdminDto).subscribe({
      error: (error: any) => {
        this.errorEmail = error?.error?.error;
        this.clearEmail();
      },
      complete: () => {
        this.router.navigateByUrl('/user/users');
      }
    });
  }

  validateEmail(): string {
    if (this.email.length === 0) {
      return 'Enter email!';
    }
    if (!ValidateEmails(this.email)) {
      return 'Enter correct email!'
    }
    return '';
  }

  validateFacultyChair(): string {
    if (this.selectedFaculty === 0) {
      return 'Select Faculty';
    }
    if (this.selectedChair === 0 && !this.wholeFaculty) {
      return 'Select Chair';
    }
    return '';
  }

  clearEmail() {
    this.email = '';
  }
}
