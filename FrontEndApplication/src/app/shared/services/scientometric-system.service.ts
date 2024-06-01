import { HttpClient } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { Observable } from "rxjs";
import { ExtractionErrors, ScientometricSystem } from "../models/scientometric.model";
import { baseUrl } from "../constants/url.constant";

@Injectable({
  providedIn: 'root'
})
export class ScientometricSystemService {
  private scientometricSystemUrl: string = baseUrl + "/scientometric-systems";

  constructor(private readonly httpClient: HttpClient) {
  }

  getAllScientometricSystems(): Observable<ScientometricSystem[]> {
    return this.httpClient.get<ScientometricSystem[]>(this.scientometricSystemUrl);
  }

  getExtractionIsRunning(id: number): Observable<boolean> {
    return this.httpClient.get<boolean>(this.scientometricSystemUrl + "/" + id + "/extraction/is-running")
  }

  getExtractionIsPossible(id: number): Observable<boolean> {
    return this.httpClient.get<boolean>(this.scientometricSystemUrl + "/" + id + "/extraction/is-possible")
  }

  getExtractionErrors(id: number): Observable<ExtractionErrors> {
    return this.httpClient.get<ExtractionErrors>(this.scientometricSystemUrl + "/" + id + "/extraction/errors")
  }
}
