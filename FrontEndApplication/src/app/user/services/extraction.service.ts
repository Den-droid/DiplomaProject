import { HttpClient } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { Observable } from "rxjs";
import { baseUrl } from "src/app/shared/constants/url.constant";
import { ScientometricSystem } from "../models/scientometric.model";

@Injectable()
export class ExtractionService {
  private extractionUrl: string = baseUrl + "/extraction";
  private scientometricSystemUrl: string = baseUrl + "/scientometricSystems";

  constructor(private readonly httpClient: HttpClient) {
  }

  getAllScientometricSystems(): Observable<ScientometricSystem[]> {
    return this.httpClient.get<ScientometricSystem[]>(this.scientometricSystemUrl);
  }

  getExtractionIsRunning(id: number): Observable<boolean> {
    return this.httpClient.get<boolean>(this.scientometricSystemUrl + "/" + id + "/isRunning")
  }

  getExtractionIsPossible(id: number): Observable<boolean> {
    return this.httpClient.get<boolean>(this.scientometricSystemUrl + "/" + id + "/isPossible")
  }

  launchScholarExtraction(): Observable<any> {
    return this.httpClient.get(this.extractionUrl + "/scholar");
  }
}
