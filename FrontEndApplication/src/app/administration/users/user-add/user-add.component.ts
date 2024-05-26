import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { RoleName } from 'src/app/shared/constants/roles.constant';
import { Chair } from 'src/app/shared/models/chair.model';
import { Faculty } from 'src/app/shared/models/faculty.model';
import { Permission, mapStringToPermissionLabel } from 'src/app/shared/models/permission.model';
import { Role } from 'src/app/shared/models/role.model';
import { AddAdminDto } from 'src/app/shared/models/user.model';
import { ChairService } from 'src/app/shared/services/chair.service';
import { FacultyService } from 'src/app/shared/services/faculty.service';
import { PermissionService } from 'src/app/shared/services/permission.service';
import { RoleService } from 'src/app/shared/services/role.service';
import { UserService } from 'src/app/shared/services/user.service';
import { ValidateEmails } from 'src/app/shared/validators/emails.validator';

@Component({
  selector: 'app-administration-user-add',
  templateUrl: './user-add.component.html',
  styleUrls: ['./user-add.component.css']
})
export class UserAddComponent implements OnInit {
  constructor(private readonly router: Router, private readonly userService: UserService,
    private readonly facultyService: FacultyService, private readonly chairService: ChairService,
    private readonly roleService: RoleService, private readonly permissionService: PermissionService) {
  }

  email = '';
  errorEmail = '';
  errorFaculty = '';

  faculties: Faculty[] = [];
  chairs: Chair[] = [];
  displayedChairs: Chair[] = [];

  _selectedFaculty = 0;
  selectedChair = 0;
  wholeFaculty = false;

  isMainAdminCreated = false;

  allRoles: Role[] = [];
  allPermissions: Permission[] = [];

  userPermissions: Permission[] = [];
  selectedUserPermissions: boolean[] = [];

  possiblePermissions: Permission[][] = [];
  defaultPermissions: boolean[][] = [];

  userRole: string = RoleName.CHAIR_ADMIN;

  public get selectedFaculty(): number {
    return this._selectedFaculty;
  }

  public set selectedFaculty(value: number) {
    this._selectedFaculty = value;
    this.setDisplayedChairs();
  }

  ngOnInit(): void {
    this.facultyService.getAll().subscribe({
      next: (data: Faculty[]) => {
        this.faculties = data;

        if (this.faculties.length > 0) {
          this.selectedFaculty = this.faculties[0].id;
        }

        this.chairService.getAll().subscribe({
          next: (data: Chair[]) => {
            this.chairs = data;

            if (this.selectedFaculty !== 0 && this.chairs.length > 0) {
              this.selectedChair = this.chairs.filter(x => x.facultyId == this.selectedFaculty)[0].id;
            }

            this.setDisplayedChairs();
          }
        })
      }
    });
    this.roleService.getAll().subscribe({
      next: (data: Role[]) => {
        this.allRoles = data.filter(x => x.name != RoleName.MAIN_ADMIN);
      },
      complete: () => {
        this.permissionService.getAll().subscribe({
          next: (allPermissions: Permission[]) => {
            this.allPermissions = allPermissions;
          },
          complete: () => {
            for (let role of this.allRoles) {
              this.possiblePermissions.push([]);
            }

            for (let role of this.allRoles) {
              this.defaultPermissions.push([]);
            }

            for (let role of this.allRoles) {
              this.roleService.getPossiblePermissions(role.id).subscribe({
                next: (possiblePermissions: Permission[]) => {
                  let tmpRole = this.allRoles.filter(x => x.id == role.id)[0];
                  let index = this.allRoles.indexOf(tmpRole);

                  for (let possiblePermission of possiblePermissions) {
                    possiblePermission.name = mapStringToPermissionLabel(possiblePermission.name);
                    this.possiblePermissions[index].push(possiblePermission);

                    this.defaultPermissions[index].push(false);
                  }

                  this.roleService.getDefaultPermissions(role.id).subscribe({
                    next: (defaultPermissions: Permission[]) => {
                      for (let i = 0; i < this.possiblePermissions[index].length; i++) {
                        this.defaultPermissions[index][i] = defaultPermissions
                          .filter(x => x.id == this.possiblePermissions[index][i].id)
                          .length > 0;
                      }

                      if (role.name === this.userRole) {
                        this.updatePermissionsAndDefaultPermissions();
                      }
                    }
                  })
                }
              })
            }
          }
        })
      }
    })
  }

  setDisplayedChairs() {
    this.displayedChairs = [];
    for (let chair of this.chairs) {
      if (chair.facultyId == this.selectedFaculty) {
        this.displayedChairs.push(chair);
      }
    }
  }

  updateWholeFaculty() {
    this.wholeFaculty = !this.wholeFaculty;
    if (this.wholeFaculty) {
      this.userRole = RoleName.FACULTY_ADMIN;
    } else {
      this.userRole = RoleName.CHAIR_ADMIN;
    }

    this.updatePermissionsAndDefaultPermissions();
  }

  updateSelectedPermission(index: number) {
    this.selectedUserPermissions[index] = !this.selectedUserPermissions[index];
  }

  updatePermissionsAndDefaultPermissions() {
    let tmpRole = this.allRoles.filter(x => x.name == this.userRole)[0];
    let roleIndex = this.allRoles.indexOf(tmpRole);

    let newUserPermissions = [];
    let newSelectedUserPermissions = [];

    for (let i = 0; i < this.possiblePermissions[roleIndex].length; i++) {
      newUserPermissions.push(this.possiblePermissions[roleIndex][i]);
      newSelectedUserPermissions.push(this.defaultPermissions[roleIndex][i]);
    }

    this.userPermissions = newUserPermissions;
    this.selectedUserPermissions = newSelectedUserPermissions;
  }

  addAdmin() {
    let emailCorrect = this.validateEmail();
    if (emailCorrect.length > 0) {
      this.errorEmail = emailCorrect
      return;
    } else {
      this.errorEmail = '';
    }

    let addAdminDto;
    if (!this.isMainAdminCreated) {
      let permissionList = [];

      for (let i = 0; i < this.selectedUserPermissions.length; i++) {
        if (this.selectedUserPermissions[i]) {
          permissionList.push(this.userPermissions[i].id);
        }
      }

      if (this.wholeFaculty) {
        addAdminDto = new AddAdminDto(this.email, [this.selectedFaculty], [], this.isMainAdminCreated, permissionList);
      } else {
        addAdminDto = new AddAdminDto(this.email, [], [this.selectedChair], this.isMainAdminCreated, permissionList);
      }
    } else {
      addAdminDto = new AddAdminDto(this.email, [], [], this.isMainAdminCreated, []);
    }

    this.userService.addAdmin(addAdminDto).subscribe({
      error: (error: any) => {
        this.errorEmail = error?.error?.error;
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
}
