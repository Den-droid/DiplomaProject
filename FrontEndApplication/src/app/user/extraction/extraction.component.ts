import { Component, OnInit } from '@angular/core';
import { ScientometricSystem, ScientometricSystemName, mapStringToScientometricSystem } from '../models/scientometric.model';
import { ExtractionService } from '../services/extraction.service';

@Component({
  selector: 'app-user-extraction',
  templateUrl: './extraction.component.html',
  styleUrls: ['./extraction.component.css']
})
export class ExtractionComponent implements OnInit {
  isRunning: boolean[] = [];
  isPossible: boolean[] = [];
  scientometricSystems: ScientometricSystem[] = [];
  scientometricSystemsNames: ScientometricSystemName[] = [];
  dateNow = Date.now();

  constructor(private readonly extractionService: ExtractionService) {

  }

  ngOnInit(): void {
    this.extractionService.getAllScientometricSystems().subscribe({
      next: (data: ScientometricSystem[]) => {
        this.scientometricSystems = data;

        for (let scientometricSystem of data) {
          this.scientometricSystemsNames.push(mapStringToScientometricSystem(scientometricSystem.name));
          this.isPossible.push(new Date(scientometricSystem.nextMinImportDate).getTime() < this.dateNow);

          this.extractionService.getExtractionIsRunning(scientometricSystem.id).subscribe({
            next: (result: boolean) => {
              this.isRunning.push(result);
            }
          });
        }
      }
    })
  }

  launchExtraction(index: number, name: string) {
    let enumName = mapStringToScientometricSystem(name);
    switch (enumName) {
      case ScientometricSystemName.SCHOLAR:
        this.extractionService.launchScholarExtraction().subscribe({
          complete: () => {
            this.isRunning[index] = true;
          }
        })
        break;
    }
  }
}
