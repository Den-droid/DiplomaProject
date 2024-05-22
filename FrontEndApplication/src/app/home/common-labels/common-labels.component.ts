import { Component, OnInit } from '@angular/core';
import { GetLabelsDto, Label } from 'src/app/shared/models/label.model';
import { ProfileByLabel } from 'src/app/shared/models/profile.model';
import { mapStringToScientometricSystemLabel } from 'src/app/shared/models/scientometric.model';
import { LabelService } from 'src/app/shared/services/label.service';
import { ProfileService } from 'src/app/shared/services/profile.service';

@Component({
  selector: 'app-home-common-labels',
  templateUrl: './common-labels.component.html',
  styleUrls: ['./common-labels.component.css']
})
export class CommonLabelsComponent implements OnInit {
  constructor(private readonly labelService: LabelService, private readonly profileService: ProfileService) { }

  selectedLabel = 0;

  allLabels: Label[] = [];
  displayedLabels: Label[] = [];

  profiles: ProfileByLabel[] = [];

  error = '';

  set labelSearchQuery(labelSearchQuery: string) {
    this.selectedLabel = 0;
    this.displayedLabels = this.allLabels.filter(x => x.name.toLowerCase().includes(labelSearchQuery.toLowerCase()));

    if (this.displayedLabels.length > 0) {
      this.selectedLabel = this.displayedLabels[0].id;
    }
  }

  ngOnInit(): void {
    this.labelService.getAllLabels().subscribe({
      next: (labels: GetLabelsDto) => {
        this.allLabels = labels.labels;
        this.displayedLabels = this.allLabels;

        if (this.displayedLabels.length > 0) {
          this.selectedLabel = this.allLabels[0].id;
        }
      }
    });
  }

  getProfiles() {
    let validate = this.validate();
    if (validate.length !== 0) {
      this.error = validate;
      return;
    }

    this.profileService.getProfilesByLabel(this.selectedLabel).subscribe({
      next: (data: ProfileByLabel[]) => {
        this.profiles = data;

        this.profiles.forEach(profile => profile.scientometricSystemName =
          mapStringToScientometricSystemLabel(profile.scientometricSystemName));

        this.clear();
      }
    })
  }

  validate(): string {
    if (this.selectedLabel == 0) {
      return 'Select label';
    }
    return '';
  }

  clear() {
    this.clearError();
  }

  clearError() {
    this.error = '';
  }
}
