import { Injectable } from "@angular/core";
import { HttpClient, HttpParams } from "@angular/common/http";
import { CreateProfileDto, UpdateProfileDto, GetProfilesDto, ProfileByLabel, ProfileForUser } from "../models/profile.model";
import { Observable } from "rxjs";
import { ProfileField } from "../models/field.model";
import { Label } from "../models/label.model";
import { baseUrl } from "../constants/url.constant";

@Injectable({
  providedIn: 'root'
})
export class ProfileService {
  private url: string = baseUrl + "/profiles";

  constructor(private readonly httpClient: HttpClient) {
  }

  getForCurrentUser(page: number, scientometricSystemId: number): Observable<GetProfilesDto> {
    const options = page ?
      {
        params: new HttpParams().set('currentPage', page)
          .set('scientometricSystemId', scientometricSystemId)
      } : {};

    return this.httpClient.get<GetProfilesDto>(this.url + "/accessible-for-current-user", options);
  }

  searchForCurrentUser(page: number, scientometricSystemId: number, fullName: string,
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

    return this.httpClient.get<GetProfilesDto>(this.url + "/accessible-for-current-user/search", options);
  }

  editProfile(id: number, editProfileDto: UpdateProfileDto): Observable<any> {
    return this.httpClient.put(this.url + "/" + id, editProfileDto);
  }

  addProfile(addProfileDto: CreateProfileDto): Observable<any> {
    return this.httpClient.post(this.url, addProfileDto);
  }

  canAddProfile(scientistId: number, scientometricSystemId: number): Observable<boolean> {
    const options =
    {
      params: new HttpParams()
        .set('scientistId', scientistId)
        .set('scientometricSystemId', scientometricSystemId)
    };

    return this.httpClient.get<boolean>(this.url + "/can-create-profile", options);
  }

  markDoubtful(id: number): Observable<any> {
    return this.httpClient.get(this.url + "/" + id + "/mark-doubtful");
  }

  unmarkDoubtful(id: number): Observable<any> {
    return this.httpClient.get(this.url + "/" + id + "/unmark-doubtful");
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

  getByLabel(labelId: number): Observable<ProfileByLabel[]> {
    const options =
    {
      params: new HttpParams()
        .set('labelId', labelId)
    };

    return this.httpClient.get<ProfileByLabel[]>(this.url + "/common-labels", options);
  }

  getAll(scientometricSystemId: number, chairId: number): Observable<ProfileForUser[]> {
    const options =
    {
      params: new HttpParams()
        .set('scientometricSystemId', scientometricSystemId)
        .set('chairId', chairId)
    };

    return this.httpClient.get<ProfileForUser[]>(this.url, options);
  }
}
