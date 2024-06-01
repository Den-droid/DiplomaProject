import { HttpClient } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { Observable } from "rxjs";
import { ScientistPreview } from "../models/scientist.model";
import { baseUrl } from "../constants/url.constant";

@Injectable({
  providedIn: 'root'
})
export class ScientistService {
  private url: string = baseUrl + "/scientists";

  constructor(private readonly httpClient: HttpClient) {
  }

  getNotRegisteredScientists(): Observable<ScientistPreview[]> {
    return this.httpClient.get<ScientistPreview[]>(this.url + "/not-registered");
  }

  getForCurrentUser(): Observable<ScientistPreview[]> {
    return this.httpClient.get<ScientistPreview[]>(this.url + "/accessible-for-current-user");
  }
}
