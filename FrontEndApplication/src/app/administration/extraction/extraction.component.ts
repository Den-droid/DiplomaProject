import { Component, OnInit } from '@angular/core';
import { ScientometricSystem, mapStringToScientometricSystemLabel } from '../../shared/models/scientometric.model';
import { ExtractionService } from '../../shared/services/extraction.service';
import { ScientometricSystemLabel, ScientometricSystemName } from 'src/app/shared/constants/scientometric-system.constant';
import { ScientometricSystemService } from 'src/app/shared/services/scientometric-System.service';

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

  constructor(private readonly extractionService: ExtractionService,
    private readonly scientometricSystemService: ScientometricSystemService) {

  }

  ngOnInit(): void {
    this.scientometricSystemService.getAllScientometricSystems().subscribe({
      next: (data: ScientometricSystem[]) => {
        this.scientometricSystems = data;

        for (let scientometricSystem of data) {
          this.scientometricSystemsLabels.push(mapStringToScientometricSystemLabel(scientometricSystem.name));
          this.isPossible.push(new Date(scientometricSystem.nextMinImportDate).getTime() < this.dateNow);

          this.scientometricSystemService.getExtractionIsRunning(scientometricSystem.id).subscribe({
            next: (result: boolean) => {
              this.isRunning.push(result);
            }
          });
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
