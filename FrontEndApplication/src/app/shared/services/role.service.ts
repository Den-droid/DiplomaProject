import { HttpClient, HttpParams } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { baseUrl } from "../constants/url.constant";
import { Observable } from "rxjs";
import { Role, UpdateDefaultPermissions } from "../models/role.model";
import { Permission } from "../models/permission.model";

@Injectable({
  providedIn: 'root'
})
export class RoleService {
  private url: string = baseUrl + "/roles";

  constructor(private readonly httpClient: HttpClient) {
  }

  getAll(): Observable<Role[]> {
    return this.httpClient.get<Role[]>(this.url);
  }

  getByName(name: string): Observable<Role> {
    const options = name ?
      {
        params: new HttpParams()
          .set('roleName', name)
      } : {};
    return this.httpClient.get<Role>(this.url);
  }

  getPossiblePermissions(id: number): Observable<Permission[]> {
    return this.httpClient.get<Permission[]>(this.url + "/" + id + "/possiblePermissions");
  }

  getDefaultPermissions(id: number): Observable<Permission[]> {
    return this.httpClient.get<Permission[]>(this.url + "/" + id + "/defaultPermissions");
  }

  updateDefaultPermissions(updateDefaultPermissionsDto: UpdateDefaultPermissions[]): Observable<any> {
    return this.httpClient.put(this.url + "/updateDefaultPermissions", updateDefaultPermissionsDto);
  }
}