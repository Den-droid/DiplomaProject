import { Component, OnInit } from '@angular/core';
import { ExtractionErrors, ScientometricSystem, mapStringToScientometricSystemLabel } from '../../shared/models/scientometric.model';
import { ExtractionService } from '../../shared/services/extraction.service';
import { ScientometricSystemLabel } from 'src/app/shared/constants/scientometric-system.constant';
import { ScientometricSystemService } from 'src/app/shared/services/scientometric-system.service';
@Component({
  selector: 'app-administration-extraction',
  templateUrl: './extraction.component.html',
  styleUrls: ['./extraction.component.css']
})
export class ExtractionComponent implements OnInit {
  isRunning: boolean[] = [];
  isPossible: boolean[] = [];
  scientometricSystems: ScientometricSystem[] = [];
  scientometricSystemsLabels: ScientometricSystemLabel[] = [];
  dateNow = Date.now();
  scientometricSystemErrors: ExtractionErrors[] = [];

  constructor(private readonly extractionService: ExtractionService,
    private readonly scientometricSystemService: ScientometricSystemService) {
  }

  ngOnInit(): void {
    this.scientometricSystemService.getAllScientometricSystems().subscribe({
      next: (data: ScientometricSystem[]) => {
        this.scientometricSystems = data;

        for (let i = 0; i < data.length; i++) {
          this.scientometricSystemsLabels.push(mapStringToScientometricSystemLabel(data[i].name));
          this.isPossible.push(new Date(data[i].nextMinImportDate).getTime() < this.dateNow);
          this.isRunning.push(true);
          this.scientometricSystemService.getExtractionIsRunning(data[i].id).subscribe({
            next: (result: boolean) => {
              this.isRunning[i] = result;
            }
          });
          this.scientometricSystemService.getExtractionErrors(data[i].id).subscribe({
            next: (errors: ExtractionErrors) => {
              this.scientometricSystemErrors[i] = errors;
            }
          })
        }
      }
    })
  }

  launchExtraction(index: number, name: string) {
    let enumName = mapStringToScientometricSystemLabel(name);
    switch (enumName) {
      case ScientometricSystemLabel.SCHOLAR:
        this.extractionService.launchScholarExtraction().subscribe({
          complete: () => {
            this.isRunning[index] = true;
          }
        })
        break;
    }
  }
}
