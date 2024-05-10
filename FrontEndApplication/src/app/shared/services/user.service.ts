import { HttpClient, HttpParams } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { baseUrl } from "src/app/shared/constants/url.constant";
import { AddAdminDto, EditAdminDto, EditUserDto, GetUsersDto, User } from "../models/user.model";
import { Observable } from "rxjs/internal/Observable";
import { Permission } from "../models/permission.model";
import { Role } from "../models/role.model";

@Injectable({
  providedIn: 'root'
})
export class UserService {
  private url: string = baseUrl + "/users";

  constructor(private readonly httpClient: HttpClient) {
  }

  getAllUsers(page: number): Observable<GetUsersDto> {
    const options = page ?
      { params: new HttpParams().set('currentPage', page) } : {};

    return this.httpClient.get<GetUsersDto>(this.url, options);
  }

  getEditDto(id: number): Observable<EditAdminDto> {
    return this.httpClient.get<EditAdminDto>(this.url + "/" + id + "/editDto");
  }

  getRoles(id: number): Observable<Role[]> {
    return this.httpClient.get<Role[]>(this.url + "/" + id + "/roles");
  }

  getCurrentUserPermissions(): Observable<Permission[]> {
    return this.httpClient.get<Permission[]>(this.url + "/currentUser/permissions");
  }

  getUserPermissionsById(id: number): Observable<Permission[]> {
    return this.httpClient.get<Permission[]>(this.url + "/" + id + "/permissions");
  }

  searchUsers(page: number, fullName: string, roleId: number, facultyId: number, chairId: number) {
    const options = page ?
      {
        params: new HttpParams()
          .set('currentPage', page)
          .set('fullName', fullName)
          .set('roleId', roleId)
          .set('facultyId', facultyId)
          .set('chairId', chairId)
      } : {};

    return this.httpClient.get<GetUsersDto>(this.url + "/search", options);
  }

  addAdmin(addAdminDto: AddAdminDto): Observable<any> {
    return this.httpClient.post(this.url + "/admins", addAdminDto);
  }

  editAdmin(id: number, editAdmin: EditAdminDto): Observable<any> {
    return this.httpClient.put(this.url + "/admins/" + id, editAdmin);
  }

  editUser(id: number, editUser: EditUserDto): Observable<any> {
    return this.httpClient.put(this.url + "/" + id, editUser);
  }

  getCurrentUser():Observable<User> {
    return this.httpClient.get<User>(this.url + "/current");
  }

  editCurrentUser(editUser: EditUserDto): Observable<any> {
    return this.httpClient.put(this.url + "/current", editUser);
  }

  existsById(id: number): Observable<boolean> {
    return this.httpClient.get<boolean>(this.url + "/exists/" + id);
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
