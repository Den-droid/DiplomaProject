import { HttpClient, HttpParams } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { CreateAdminDto, UpdateAdminDto, UpdateCurrentUserDto, UpdateUserDto, GetUsersDto, User } from "../models/user.model";
import { Observable } from "rxjs/internal/Observable";
import { Permission } from "../models/permission.model";
import { Role } from "../models/role.model";
import { baseUrl } from "../constants/url.constant";

@Injectable({
  providedIn: 'root'
})
export class UserService {
  private url: string = baseUrl + "/users";

  constructor(private readonly httpClient: HttpClient) {
  }

  getForCurrentUser(page: number): Observable<GetUsersDto> {
    const options = page ?
      { params: new HttpParams().set('currentPage', page) } : {};

    return this.httpClient.get<GetUsersDto>(this.url + "/accessible-for-current-user", options);
  }

  getEditDto(id: number): Observable<UpdateAdminDto> {
    return this.httpClient.get<UpdateAdminDto>(this.url + "/" + id + "/edit-dto");
  }

  getRoles(id: number): Observable<Role[]> {
    return this.httpClient.get<Role[]>(this.url + "/" + id + "/roles");
  }

  getCurrentUserPermissions(): Observable<Permission[]> {
    return this.httpClient.get<Permission[]>(this.url + "/current-user/permissions");
  }

  getUserPermissionsById(id: number): Observable<Permission[]> {
    return this.httpClient.get<Permission[]>(this.url + "/" + id + "/permissions");
  }

  searchForCurrentUser(page: number, fullName: string, roleId: number, facultyId: number, chairId: number) {
    const options = page ?
      {
        params: new HttpParams()
          .set('currentPage', page)
          .set('fullName', fullName)
          .set('roleId', roleId)
          .set('facultyId', facultyId)
          .set('chairId', chairId)
      } : {};

    return this.httpClient.get<GetUsersDto>(this.url + "/accessible-for-current-user/search", options);
  }

  createAdmin(addAdminDto: CreateAdminDto): Observable<any> {
    return this.httpClient.post(this.url + "/admins", addAdminDto);
  }

  updateAdmin(id: number, editAdmin: UpdateAdminDto): Observable<any> {
    return this.httpClient.put(this.url + "/admins/" + id, editAdmin);
  }

  updateUser(id: number, editUser: UpdateUserDto): Observable<any> {
    return this.httpClient.put(this.url + "/" + id, editUser);
  }

  getById(id: number): Observable<User> {
    return this.httpClient.get<User>(this.url + "/" + id);
  }

  getCurrentUser(): Observable<User> {
    return this.httpClient.get<User>(this.url + "/current-user");
  }

  canEditUser(id: number): Observable<boolean> {
    const options = id ?
      {
        params: new HttpParams()
          .set('userId', id)
      } : {};

    return this.httpClient.get<boolean>(this.url + "/current-user/can-update-user", options);
  }

  canEditProfile(id: number): Observable<boolean> {
    const options = id ?
      {
        params: new HttpParams()
          .set('profileId', id)
      } : {};

    return this.httpClient.get<boolean>(this.url + "/current-user/can-update-profile", options);
  }

  updateCurrentUser(editUser: UpdateCurrentUserDto): Observable<any> {
    return this.httpClient.put(this.url + "/current-user", editUser);
  }

  approve(id: number): Observable<any> {
    return this.httpClient.get(this.url + "/" + id + "/approve");
  }

  reject(id: number): Observable<any> {
    return this.httpClient.get(this.url + "/" + id + "/reject");
  }

  activate(id: number): Observable<any> {
    return this.httpClient.get(this.url + "/" + id + "/activate");
  }

  deactivate(id: number): Observable<any> {
    return this.httpClient.get(this.url + "/" + id + "/deactivate");
  }
}
