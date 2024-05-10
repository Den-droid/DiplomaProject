import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Params, Router } from '@angular/router';
import { UserService } from 'src/app/shared/services/user.service';
import { Faculty } from 'src/app/shared/models/faculty.model';
import { Chair } from 'src/app/shared/models/chair.model';
import { EditAdminDto, EditUserDto } from 'src/app/shared/models/user.model';
import { JWTTokenService } from 'src/app/shared/services/jwt-token.service';
import { ChairService } from 'src/app/shared/services/chair.service';
import { FacultyService } from 'src/app/shared/services/faculty.service';
import { RoleName } from 'src/app/shared/constants/roles.constant';
import { RoleService } from 'src/app/shared/services/role.service';
import { Permission, mapStringToPermissionLabel } from 'src/app/shared/models/permission.model';
import { Role } from 'src/app/shared/models/role.model';
import { PermissionService } from 'src/app/shared/services/permission.service';

@Component({
  selector: 'app-administration-user-edit',
  templateUrl: './user-edit.component.html',
  styleUrls: ['./user-edit.component.css']
})
export class UserEditComponent implements OnInit {
  constructor(private readonly router: Router, private readonly activatedRoute: ActivatedRoute,
    private readonly userService: UserService, private readonly facultyService: FacultyService,
    private readonly chairService: ChairService, private readonly jwtService: JWTTokenService,
    private readonly roleService: RoleService, private readonly permissionService: PermissionService
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

  isEditedToMainAdmin = false;

  userRole = '';
  currentUserRole = '';

  allRoles: Role[] = [];
  allPermissions: Permission[] = [];

  userPermissions: Permission[] = [];
  selectedUserPermissions: boolean[] = [];

  possiblePermissions: Permission[][] = [];
  defaultPermissions: boolean[][] = [];

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
                    next: (data: Role[]) => {
                      this.userRole = data[0].name;

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

                      this.roleService.getPossiblePermissions(data[0].id).subscribe({
                        next: (possiblePermissions: Permission[]) => {
                          possiblePermissions.forEach(x => x.name = mapStringToPermissionLabel(x.name));
                          this.userPermissions = possiblePermissions;

                          this.userService.getUserPermissionsById(this.userId).subscribe({
                            next: (userPermissions: Permission[]) => {
                              this.selectedUserPermissions = [];

                              for (let possiblePermission of this.userPermissions) {
                                this.selectedUserPermissions.push(
                                  userPermissions.filter(x => x.id === possiblePermission.id).length > 0
                                );
                              }
                            }
                          })
                        }
                      })
                    }
                  });
                }
              })
            }
          });

          this.fullName = result.fullName;
        },
        error: (error: any) => {
          this.router.navigateByUrl("/error/404");
        }
      });
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
                    }
                  })
                }
              })
            }
          }
        })
      }
    })

    this.currentUserRole = this.jwtService.getRoles()[0];
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

  updateSelectedPermission(index: number) {
    this.selectedUserPermissions[index] = !this.selectedUserPermissions[index];
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

    if (this.userRole === RoleName.FACULTY_ADMIN || this.userRole === RoleName.CHAIR_ADMIN) {
      if (!this.isEditedToMainAdmin) {
        let permissionList = [];

        for (let i = 0; i < this.selectedUserPermissions.length; i++) {
          if (this.selectedUserPermissions[i]) {
            permissionList.push(this.userPermissions[i].id);
          }
        }

        if (this.wholeFaculty) {
          editAdminDto = new EditAdminDto(this.fullName, [this.selectedFaculty], [], this.isEditedToMainAdmin, permissionList);
        } else {
          editAdminDto = new EditAdminDto(this.fullName, [], [this.selectedChair], this.isEditedToMainAdmin, permissionList);
        }
      } else {
        editAdminDto = new EditAdminDto(this.fullName, [], [], this.isEditedToMainAdmin, []);
      }

      this.userService.editAdmin(this.userId, editAdminDto).subscribe({
        complete: () => {
          this.router.navigateByUrl('/user/users');
        }
      });
    } else {
      let editUserDto = new EditUserDto(this.fullName);

      this.userService.editUser(this.userId, editUserDto).subscribe({
        complete: () => {
          this.router.navigateByUrl('/user/users');
        }
      });
    }

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
