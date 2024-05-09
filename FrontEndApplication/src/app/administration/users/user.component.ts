import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { UserService } from '../../shared/services/user.service';
import { Role, mapStringToRoleLabel } from 'src/app/shared/models/role.model';
import { RoleService } from 'src/app/shared/services/role.service';
import { FacultyService } from 'src/app/shared/services/faculty.service';
import { ChairService } from 'src/app/shared/services/chair.service';
import { Chair } from 'src/app/shared/models/chair.model';
import { Faculty } from 'src/app/shared/models/faculty.model';
import { AuthService } from 'src/app/shared/services/auth.service';
import { RoleLabel, RoleName } from 'src/app/shared/constants/roles.constant';
import { GetUsersDto, User } from 'src/app/shared/models/user.model';
import { Permission } from 'src/app/shared/models/permission.model';
import { JWTTokenService } from 'src/app/shared/services/jwt-token.service';

@Component({
  selector: 'app-administration-user',
  templateUrl: './user.component.html',
  styleUrls: ['./user.component.css']
})
export class UserComponent implements OnInit {
  constructor(private readonly router: Router, private readonly userService: UserService,
    private readonly roleService: RoleService, private readonly facultyService: FacultyService,
    private readonly chairService: ChairService, private readonly authService: AuthService,
    private readonly jwtService: JWTTokenService) { }

  currentPage = 1;
  totalPages = 1;

  searchQuery = '';
  isSearchMode = false;
  error = '';

  _selectedRole = 0;
  _selectedFaculty = 0;
  selectedChair = 0;
  isChairDisabled = false;
  isFacultyDisabled = false;

  roles: Role[] = [];
  faculties: Faculty[] = [];
  chairs: Chair[] = [];
  displayedChairs: Chair[] = [];

  displayedUsers: User[] = [];

  userPermissions: Permission[] = [];
  currentUserRole: string = this.jwtService.getRoles()[0];

  public get selectedFaculty(): number {
    return this._selectedFaculty;
  }

  public set selectedFaculty(value: number) {
    this._selectedFaculty = value;
    this.setDisplayedChairs();
  }

  public get selectedRole(): number {
    return this._selectedRole;
  }

  public set selectedRole(value: number) {
    this._selectedRole = value;
    for (let role of this.roles) {
      if (role.name === RoleLabel.FACULTY_ADMIN && role.id == value) {
        this.isFacultyDisabled = false;
        this.isChairDisabled = true;
        this.selectedChair = 0;
      } else if (role.name === RoleLabel.CHAIR_ADMIN && role.id == value) {
        this.isFacultyDisabled = false;
        this.isChairDisabled = false;
      } else if (role.name === RoleLabel.USER && role.id == value) {
        this.isFacultyDisabled = false;
        this.isChairDisabled = false;
      }
    }
  }

  ngOnInit(): void {
    this.roleService.getAll().subscribe({
      next: (data: Role[]) => {
        if (this.authService.isAdmin()) {
          for (let role of data) {
            if (role.name !== RoleName.MAIN_ADMIN) {
              role.name = mapStringToRoleLabel(role.name);
              this.roles.push(role);
            }
          }
        } else if (this.authService.isFacultyAdmin() || this.authService.isChairAdmin()) {
          for (let role of data) {
            if (role.name === RoleName.USER) {
              role.name = mapStringToRoleLabel(role.name);
              this.roles.push(role);
            }
          }
        }
      }
    });
    this.facultyService.getByUser().subscribe({
      next: (data: Faculty[]) => {
        this.faculties = data;
      }
    });
    this.chairService.getByUser().subscribe({
      next: (data: Chair[]) => {
        this.chairs = data;
      }
    })
    this.userService.getCurrentUserPermissions().subscribe({
      next: (data: Permission[]) => {
        this.userPermissions = data;
      }
    })

    this.getPageElements(this.currentPage);
  }

  hasPermissionForAction(permissionName: string): boolean {
    return this.userPermissions.filter(x => x.name === permissionName).length > 0;
  }

  setDisplayedChairs() {
    this.displayedChairs = [];
    for (let chair of this.chairs) {
      if (chair.facultyId == this.selectedFaculty) {
        this.displayedChairs.push(chair);
      }
    }
  }

  validate(): string {
    if (this.selectedRole == 0) {
      return 'Select Role';
    }
    return '';
  }

  getPageElements(page: number) {
    this.userService.getAllUsers(page).subscribe({
      next: (data: GetUsersDto) => {
        if (data.users.length == 0) {
          this.displayedUsers = [];
          this.currentPage = 1;
          this.totalPages = 1;
        } else {
          this.displayedUsers = data.users;
          this.currentPage = page;
          this.totalPages = data.pageDto.totalPages;
        }
      }
    });
  }

  pageChange(page: number) {
    if (this.isSearchMode) {
      this.search(page);
    }

    this.getPageElements(page);

    window.scroll({
      top: 0,
      left: 0,
      behavior: 'smooth'
    });
  }

  search(page: number) {
    if (this.searchQuery === '' && this.selectedRole == 0 && this.selectedChair == 0 && this.selectedFaculty == 0) {
      this.isSearchMode = false;
      this.pageChange(1);
    } else {
      let validationResult = this.validate();
      if (validationResult.length > 0) {
        this.error = validationResult;
        return;
      } else {
        this.error = '';
      }

      this.isSearchMode = true;
      this.userService.searchUsers(page, this.searchQuery, this.selectedRole,
        this.selectedFaculty, this.selectedChair).subscribe({
          next: (data: GetUsersDto) => {
            if (data.users.length == 0) {
              this.displayedUsers = [];
              this.currentPage = 1;
              this.totalPages = 1;
            } else {
              this.displayedUsers = data.users;
              this.currentPage = page;
              this.totalPages = data.pageDto.totalPages;
            }
          }
        });
    }
  }

  clear() {
    this.clearError();
    this.selectedRole = 0;
    this.selectedFaculty = 0;
    this.selectedChair = 0;
    this.searchQuery = '';
  }

  clearError() {
    this.error = '';
  }

  goToEditPage(id: number) {
    this.router.navigateByUrl("/user/users/" + id + "/edit");
  }

  approve(id: number) {
    let user = this.displayedUsers.filter(user => user.id == id);
    user[0].isApproved = true;
    user[0].isActive = true;

    this.userService.approve(id).subscribe();
  }

  reject(id: number) {
    this.displayedUsers = this.displayedUsers.filter(user => user.id != id);

    this.userService.reject(id).subscribe();
  }

  activate(id: number) {
    let user = this.displayedUsers.filter(user => user.id == id);
    user[0].isActive = true;

    this.userService.activate(id).subscribe();
  }

  deactivate(id: number) {
    let user = this.displayedUsers.filter(user => user.id == id);
    user[0].isActive = false;

    this.userService.deactivate(id).subscribe();
  }
}
