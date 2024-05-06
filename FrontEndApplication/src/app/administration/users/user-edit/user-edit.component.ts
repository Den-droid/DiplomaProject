import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Params, Router } from '@angular/router';
import { UserService } from 'src/app/shared/services/user.service';
import { Faculty } from 'src/app/shared/models/faculty.model';
import { Chair } from 'src/app/shared/models/chair.model';
import { EditAdminDto } from 'src/app/shared/models/user.model';
import { JWTTokenService } from 'src/app/shared/services/jwt-token.service';
import { ChairService } from 'src/app/shared/services/chair.service';
import { FacultyService } from 'src/app/shared/services/faculty.service';
import { RoleName } from 'src/app/shared/constants/roles.constant';

@Component({
  selector: 'app-administration-user-edit',
  templateUrl: './user-edit.component.html',
  styleUrls: ['./user-edit.component.css']
})
export class UserEditComponent implements OnInit {
  constructor(private readonly router: Router, private readonly activatedRoute: ActivatedRoute,
    private readonly userService: UserService, private readonly facultyService: FacultyService,
    private readonly chairService: ChairService, private readonly jwtService: JWTTokenService
  ) {
  }

  fullName = '';
  userId!: number;
  errorFullname = '';
  errorFaculty = '';

  faculties: Faculty[] = [];
  chairs: Chair[] = [];

  selectedFaculty = 0;
  selectedChair = 0;
  wholeFaculty = false;

  userRole = '';
  currentUserRole = '';

  ngOnInit(): void {
    this.activatedRoute.params.subscribe((data: Params) => {
      this.userId = data['id'];
      this.userService.getEditDto(this.userId).subscribe({
        next: (result: EditAdminDto) => {
          this.facultyService.getAll().subscribe({
            next: (data: Faculty[]) => {
              this.faculties = data;

              this.chairService.getAll().subscribe({
                next: (data: Chair[]) => {
                  this.chairs = data;

                  this.userService.getRoles(this.userId).subscribe({
                    next: (data: string[]) => {
                      this.userRole = data[0];
                      this.currentUserRole = this.jwtService.getRoles()[0];

                      if (this.userRole != RoleName.USER) {
                        if (result.facultyIds[0] === undefined) {
                          let chair = this.chairs.filter(x => x.id === result.chairIds[0])[0];
                          this.selectedFaculty = this.faculties.filter(x => x.id == chair.facultyId)[0].id;
                          this.selectedChair = result.chairIds[0];
                        } else if (result.chairIds[0] === undefined) {
                          this.selectedFaculty = this.faculties.filter(x => x.id == result.facultyIds[0])[0].id;
                          this.wholeFaculty = true;
                        }
                      }
                    }
                  });
                }
              })
            }
          });

          this.fullName = result.fullName;
        },
        error: (error: any) => {
          this.router.navigateByUrl("/user/users");
        }
      });
    });
  }

  editAdmin() {
    let fullNameCorrect = this.validateFullName();
    if (fullNameCorrect.length > 0) {
      this.errorFullname = fullNameCorrect
      return;
    } else {
      this.errorFullname = '';
    }

    let facultyChairCorrect = this.validateFacultyChair();
    if (facultyChairCorrect.length > 0) {
      this.errorFaculty = facultyChairCorrect;
      return;
    } else {
      this.errorFaculty = '';
    }

    let editAdminDto;
    if (this.wholeFaculty) {
      editAdminDto = new EditAdminDto(this.fullName, [this.selectedFaculty], []);
    } else {
      editAdminDto = new EditAdminDto(this.fullName, [], [this.selectedChair]);
    }

    this.userService.editAdmin(this.userId, editAdminDto).subscribe({
      complete: () => {
        this.router.navigateByUrl('/user/users');
      }
    });
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

  validateFullName(): string {
    if (this.fullName.length === 0) {
      return 'Enter Fullname';
    }
    return '';
  }
}
