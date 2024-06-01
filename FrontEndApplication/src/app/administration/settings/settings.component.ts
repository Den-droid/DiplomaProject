import { Component, OnInit } from '@angular/core';
import { UserService } from 'src/app/shared/services/user.service';
import { JWTTokenService } from 'src/app/shared/services/jwt-token.service';
import { PermissionService } from 'src/app/shared/services/permission.service';
import { RoleService } from 'src/app/shared/services/role.service';
import { Role, UpdateDefaultPermissions, mapStringToRoleLabel } from 'src/app/shared/models/role.model';
import { Permission, mapStringToPermissionLabel } from 'src/app/shared/models/permission.model';
import { RoleName } from 'src/app/shared/constants/roles.constant';
import { UpdateCurrentUserDto, User } from 'src/app/shared/models/user.model';

@Component({
  selector: 'app-administration-user-settings',
  templateUrl: './settings.component.html',
  styleUrls: ['./settings.component.css']
})
export class SettingsComponent implements OnInit {
  constructor(private readonly userService: UserService, private readonly jwtService: JWTTokenService,
    private readonly permissionService: PermissionService, private readonly roleService: RoleService
  ) {
  }

  fullName = '';
  errorFullname = '';

  userRole = '';
  currentUserRole = '';

  allRoles: Role[] = [];
  allPermissions: Permission[] = [];

  possiblePermissions: Permission[][] = [];
  defaultPermissions: boolean[][] = [];

  ngOnInit(): void {
    this.currentUserRole = this.jwtService.getRoles()[0];

    this.userService.getCurrentUser().subscribe({
      next: (user: User) => {
        this.fullName = user.fullName;
      }
    })
    this.roleService.getAll().subscribe({
      next: (data: Role[]) => {
        this.allRoles = data.filter(x => x.name != RoleName.MAIN_ADMIN);
        this.allRoles.forEach(x => x.name = mapStringToRoleLabel(x.name));
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
  }

  editFullname() {
    let fullNameCorrect = this.validateFullName();
    if (fullNameCorrect.length > 0) {
      this.errorFullname = fullNameCorrect
      return;
    } else {
      this.errorFullname = '';
    }

    let updateUserDto = new UpdateCurrentUserDto(this.fullName);

    this.userService.updateCurrentUser(updateUserDto).subscribe();
  }

  updateDefaultPermissions() {
    let newDefaultPermissions: UpdateDefaultPermissions[] = [];
    for (let i = 0; i < this.allRoles.length; i++) {
      let permissionList = [];
      for (let j = 0; j < this.possiblePermissions[i].length; j++) {
        if (this.defaultPermissions[i][j]) {
          permissionList.push(this.possiblePermissions[i][j].id);
        }
      }
      newDefaultPermissions.push(new UpdateDefaultPermissions(this.allRoles[i].id, permissionList));
    }

    this.roleService.updateDefaultPermissions(newDefaultPermissions).subscribe();
  }

  validateFullName(): string {
    if (this.fullName.length === 0) {
      return 'Введіть ім\'я!';
    }
    return '';
  }
}
