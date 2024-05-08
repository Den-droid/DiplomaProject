import { HttpClient } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { Observable } from "rxjs";
import { baseUrl } from "../constants/url.constant";
import { Faculty } from "../models/faculty.model";

@Injectable({
  providedIn: 'root'
})
export class FacultyService {
  private url: string = baseUrl + "/faculties";

  constructor(private readonly httpClient: HttpClient) {
  }

  getAll(): Observable<Faculty[]> {
    return this.httpClient.get<Faculty[]>(this.url);
  }

  getByUser(): Observable<Faculty[]> {
    return this.httpClient.get<Faculty[]>(this.url + "/currentUser");
  }
}
