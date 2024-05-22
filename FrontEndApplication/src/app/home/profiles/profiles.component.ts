import { Component, OnInit } from '@angular/core';
import { Chair } from 'src/app/shared/models/chair.model';
import { Faculty } from 'src/app/shared/models/faculty.model';
import { ProfileForUser } from 'src/app/shared/models/profile.model';
import { ScientometricSystem, mapStringToScientometricSystemLabel } from 'src/app/shared/models/scientometric.model';
import { ChairService } from 'src/app/shared/services/chair.service';
import { FacultyService } from 'src/app/shared/services/faculty.service';
import { ProfileService } from 'src/app/shared/services/profile.service';
import { ScientometricSystemService } from 'src/app/shared/services/scientometric-system.service';

@Component({
  selector: 'app-home-profiles',
  templateUrl: './profiles.component.html',
  styleUrls: ['./profiles.component.css']
})
export class ProfilesComponent implements OnInit {
  constructor(private readonly profileService: ProfileService, private readonly facultyService: FacultyService,
    private readonly scientometricSystemService: ScientometricSystemService, private readonly chairService: ChairService
  ) { }

  selectedScientometricSystem = 0;
  _selectedFaculty = 0;
  selectedChair = 0;

  scientometricSystems: ScientometricSystem[] = [];
  faculties: Faculty[] = [];
  chairs: Chair[] = [];
  displayedChairs: Chair[] = [];

  profiles: ProfileForUser[] = [];

  public get selectedFaculty(): number {
    return this._selectedFaculty;
  }

  public set selectedFaculty(value: number) {
    this._selectedFaculty = value;

    this.setDisplayedChairs();
    if (this.displayedChairs.length > 0) {
      this.selectedChair = this.displayedChairs[0].id;
    }
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
      }
    })

    this.facultyService.getAll().subscribe({
      next: (data: Faculty[]) => {
        this.faculties = data;

        if (this.faculties.length > 0) {
          this.selectedFaculty = this.faculties[0].id;
        }
      }
    })
    this.chairService.getAll().subscribe({
      next: (data: Chair[]) => {
        this.chairs = data;
        this.setDisplayedChairs();

        if (this.displayedChairs.length > 0) {
          this.selectedChair = this.displayedChairs[0].id;
        }
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

  getProfiles() {
    this.profileService.getProfilesForUser(this.selectedScientometricSystem, this.selectedChair).subscribe({
      next: (data: ProfileForUser[]) => {
        this.profiles = data;
      }
    });
  }
}
