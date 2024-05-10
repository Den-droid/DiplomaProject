import { HttpClient } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { Observable } from "rxjs";
import { baseUrl } from "../constants/url.constant";
import { ScientistPreview } from "../models/scientist.model";

@Injectable({
  providedIn: 'root'
})
export class ScientistService {
  private url: string = baseUrl + "/scientists";

  constructor(private readonly httpClient: HttpClient) {
  }

  getAllScientistPreview(): Observable<ScientistPreview[]> {
    return this.httpClient.get<ScientistPreview[]>(this.url + "/preview");
  }
}
