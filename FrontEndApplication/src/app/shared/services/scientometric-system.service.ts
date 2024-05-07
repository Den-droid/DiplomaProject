import { HttpClient } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { Observable } from "rxjs";
import { baseUrl } from "../constants/url.constant";
import { ScientometricSystem } from "../models/scientometric.model";

@Injectable({
  providedIn: 'root'
})
export class ScientometricSystemService {
  private scientometricSystemUrl: string = baseUrl + "/scientometricSystems";

  constructor(private readonly httpClient: HttpClient) {
  }

  getAllScientometricSystems(): Observable<ScientometricSystem[]> {
    return this.httpClient.get<ScientometricSystem[]>(this.scientometricSystemUrl);
  }

  getExtractionIsRunning(id: number): Observable<boolean> {
    return this.httpClient.get<boolean>(this.scientometricSystemUrl + "/extraction/" + id + "/isRunning")
  }

  getExtractionIsPossible(id: number): Observable<boolean> {
    return this.httpClient.get<boolean>(this.scientometricSystemUrl + "/extraction/" + id + "/isPossible")
  }
}