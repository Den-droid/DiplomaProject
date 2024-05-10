import { Injectable } from "@angular/core";
import { baseUrl } from "../constants/url.constant";
import { HttpClient, HttpParams } from "@angular/common/http";
import { AddProfileDto, EditProfileDto, GetProfilesDto } from "../models/profile.model";
import { Observable } from "rxjs";
import { ProfileField } from "../models/field.model";
import { Label } from "../models/label.model";

@Injectable({
  providedIn: 'root'
})
export class ProfileService {
  private url: string = baseUrl + "/profiles";

  constructor(private readonly httpClient: HttpClient) {
  }

  getAllProfiles(page: number, scientometricSystemId: number): Observable<GetProfilesDto> {
    const options = page ?
      {
        params: new HttpParams().set('currentPage', page)
          .set('scientometricSystemId', scientometricSystemId)
      } : {};

    return this.httpClient.get<GetProfilesDto>(this.url, options);
  }

  searchProfiles(page: number, scientometricSystemId: number, fullName: string,
    facultyId: number, chairId: number): Observable<GetProfilesDto> {
    const options = page ?
      {
        params: new HttpParams()
          .set('currentPage', page)
          .set('fullName', fullName)
          .set('facultyId', facultyId)
          .set('chairId', chairId)
          .set('scientometricSystemId', scientometricSystemId)
      } : {};

    return this.httpClient.get<GetProfilesDto>(this.url + "/search", options);
  }

  editProfile(id: number, editProfileDto: EditProfileDto): Observable<any> {
    return this.httpClient.put(this.url + "/" + id, editProfileDto);
  }

  addProfile(addProfileDto: AddProfileDto): Observable<any> {
    return this.httpClient.post(this.url, addProfileDto);
  }

  canAddProfile(scientistId: number, scientometricSystemId: number): Observable<boolean> {
    const options =
    {
      params: new HttpParams()
        .set('scientistId', scientistId)
        .set('scientometricSystemId', scientometricSystemId)
    };

    return this.httpClient.get<boolean>(this.url + "/canAddProfile", options);
  }

  markAsDoubtful(id: number): Observable<any> {
    return this.httpClient.get(this.url + "/" + id + "/markDoubtful");
  }

  unmarkAsDoubtful(id: number): Observable<any> {
    return this.httpClient.get(this.url + "/" + id + "/unmarkDoubtful");
  }

  activate(id: number): Observable<any> {
    return this.httpClient.get(this.url + "/" + id + "/activate");
  }

  deactivate(id: number): Observable<any> {
    return this.httpClient.get(this.url + "/" + id + "/deactivate");
  }

  getProfileFields(id: number): Observable<ProfileField[]> {
    return this.httpClient.get<ProfileField[]>(this.url + "/" + id + "/fields");
  }

  getProfileLabels(id: number): Observable<Label[]> {
    return this.httpClient.get<Label[]>(this.url + "/" + id + "/labels");
  }
}
