import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { UserService } from 'src/app/shared/services/user.service';
import { JWTTokenService } from 'src/app/shared/services/jwt-token.service';
import { PermissionService } from 'src/app/shared/services/permission.service';
import { RoleService } from 'src/app/shared/services/role.service';
import { Role } from 'src/app/shared/models/role.model';
import { Permission } from 'src/app/shared/models/permission.model';
import { RoleName } from 'src/app/shared/constants/roles.constant';

@Component({
  selector: 'app-administration-user-settings',
  templateUrl: './settings.component.html',
  styleUrls: ['./settings.component.css']
})
export class SettingsComponent implements OnInit {
  constructor(private readonly router: Router, private readonly userService: UserService,
    private readonly jwtService: JWTTokenService, private readonly permissionService: PermissionService,
    private readonly roleService: RoleService
  ) {
  }

  fullName = '';
  errorFullname = '';

  userRole = '';
  currentUserRole = '';

  allRoles: Role[] = [];
  allPermissions: Permission[] = [];

  ngOnInit(): void {
    this.currentUserRole = this.jwtService.getRoles()[0];
    this.roleService.getAll().subscribe({
      next: (data: Role[]) => {
        this.allRoles = data.filter(x => x.name != RoleName.MAIN_ADMIN);
      }
    })
    this.permissionService.getAll().subscribe({
      next: (data: Permission[]) => {
        this.allPermissions = data;
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

    // let editAdminDto = new EditAdminDto(this.fullName, [this.selectedFaculty], []);


    this.userService.editAdmin(this.userId, editAdminDto).subscribe({
      complete: () => {
        this.router.navigateByUrl('/user/users');
      }
    });
  }

  validateFullName(): string {
    if (this.fullName.length === 0) {
      return 'Enter Fullname';
    }
    return '';
  }
}
