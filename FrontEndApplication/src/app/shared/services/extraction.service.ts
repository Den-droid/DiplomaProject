import { HttpClient } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { Observable } from "rxjs";
import { baseUrl } from "../constants/url.constant";

@Injectable({
  providedIn: 'root'
})
export class ExtractionService {
  private extractionUrl: string = baseUrl + "/extraction";

  constructor(private readonly httpClient: HttpClient) {
  }

  launchScholarExtraction(): Observable<any> {
    return this.httpClient.get(this.extractionUrl + "/scholar");
  }
}
