import { HttpClient, HttpParams } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { Observable } from "rxjs";
import { baseUrl } from "../constants/url.constant";
import { Faculty } from "../models/faculty.model";
import { EntityIndices } from "../models/indices";

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

  getFacultiesIndices(scientometricSystemId: number): Observable<EntityIndices[]> {
    const options = scientometricSystemId ?
      {
        params: new HttpParams().set('scientometricSystemId', scientometricSystemId)
      } : {};

    return this.httpClient.get<EntityIndices[]>(this.url + "/indices", options);
  }

  getFacultyChairsIndices(scientometricSystemId: number, facultyId: number): Observable<EntityIndices[]> {
    const options = scientometricSystemId ?
      {
        params: new HttpParams().set('scientometricSystemId', scientometricSystemId)
      } : {};

    return this.httpClient.get<EntityIndices[]>(this.url + "/" + facultyId + "/indices", options);
  }
}
