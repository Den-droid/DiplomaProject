import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { FacultyService } from 'src/app/shared/services/faculty.service';
import { ChairService } from 'src/app/shared/services/chair.service';
import { Chair } from 'src/app/shared/models/chair.model';
import { Faculty } from 'src/app/shared/models/faculty.model';
import { AuthService } from 'src/app/shared/services/auth.service';
import { GetUsersDto, User } from 'src/app/shared/models/user.model';
import { ProfileService } from 'src/app/shared/services/profile.service';
import { GetProfilesDto, ProfilePreview } from 'src/app/shared/models/profile.model';
import { ScientometricSystemService } from 'src/app/shared/services/scientometric-System.service';
import { ScientometricSystem, mapStringToScientometricSystemLabel } from 'src/app/shared/models/scientometric.model';

@Component({
  selector: 'app-administration-profile',
  templateUrl: './profile.component.html',
  styleUrls: ['./profile.component.css']
})
export class ProfileComponent implements OnInit {
  constructor(private readonly router: Router, private readonly profileService: ProfileService,
    private readonly facultyService: FacultyService, private readonly chairService: ChairService,
    private readonly authService: AuthService, private readonly scientometricSystemService: ScientometricSystemService) { }

  currentPage = 1;
  totalPages = 1;
  isEmpty = false;

  searchQuery = '';
  isSearchMode = false;
  error = '';

  selectedScientometricSystem = 0;
  _selectedFaculty = 0;
  selectedChair = 0;
  isChairDisabled = false;
  isFacultyDisabled = false;

  scientometricSystems: ScientometricSystem[] = [];
  faculties: Faculty[] = [];
  chairs: Chair[] = [];
  displayedChairs: Chair[] = [];

  displayedProfiles: ProfilePreview[] = [];

  canMarkAsDoubtful = false;

  public get selectedFaculty(): number {
    return this._selectedFaculty;
  }

  public set selectedFaculty(value: number) {
    this._selectedFaculty = value;
    this.setDisplayedChairs();
  }

  ngOnInit(): void {
    this.scientometricSystemService.getAllScientometricSystems().subscribe({
      next: (data: ScientometricSystem[]) => {
        for (let scientometricSystem of data) {
          scientometricSystem.name = mapStringToScientometricSystemLabel(scientometricSystem.name);
          this.scientometricSystems.push(scientometricSystem);
        }
        if (this.scientometricSystems.length > 0) {
          this.selectedScientometricSystem = this.scientometricSystems[0].id;
        }

        this.getPageElements(this.currentPage);
      }
    })
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

    if (this.authService.isAdmin()) {
      this.canMarkAsDoubtful = true;
    }
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

    return '';
  }

  getPageElements(page: number) {
    this.profileService.getAllProfiles(page, this.selectedScientometricSystem).subscribe({
      next: (data: GetProfilesDto) => {
        if (data.profiles.length == 0) {
          this.displayedProfiles = [];
          this.currentPage = 1;
          this.totalPages = 1;
        } else {
          this.displayedProfiles = data.profiles;
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
    if (this.searchQuery === '' && this.selectedChair == 0 && this.selectedFaculty == 0) {
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
      this.profileService.searchProfiles(page, this.selectedScientometricSystem,
        this.searchQuery, this.selectedFaculty, this.selectedChair).subscribe({
          next: (data: GetProfilesDto) => {
            if (data.profiles.length == 0) {
              this.displayedProfiles = [];
              this.currentPage = 1;
              this.totalPages = 1;
            } else {
              this.displayedProfiles = data.profiles;
              this.currentPage = page;
              this.totalPages = data.pageDto.totalPages;
            }
          }
        });
    }
  }

  clear() {
    this.clearError();
    this.selectedFaculty = 0;
    this.selectedChair = 0;
    this.searchQuery = '';
  }

  clearError() {
    this.error = '';
  }

  goToEditPage(id: number) {
    this.router.navigateByUrl("/user/users/edit/" + id);
  }

  goToAddPage() {
    this.router.navigateByUrl("/user/users/add");
  }

  markAsDoubtful(id: number) {
    let profile = this.displayedProfiles.filter(profile => profile.id == id);
    profile[0].areWorksDoubtful = true;

    this.profileService.markAsDoubtful(id).subscribe();
  }

  unmarkAsDoubtful(id: number) {
    let profile = this.displayedProfiles.filter(profile => profile.id == id);
    profile[0].areWorksDoubtful = false;

    this.profileService.unmarkAsDoubtful(id).subscribe();
  }

  activate(id: number) {
    let profile = this.displayedProfiles.filter(profile => profile.id == id);
    profile[0].isActive = true;

    this.profileService.activate(id).subscribe();
  }

  deactivate(id: number) {
    let profile = this.displayedProfiles.filter(profile => profile.id == id);
    profile[0].isActive = false;

    this.profileService.deactivate(id).subscribe();
  }
}
