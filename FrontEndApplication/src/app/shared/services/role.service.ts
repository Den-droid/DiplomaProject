import { HttpClient, HttpParams } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { Observable } from "rxjs";
import { Role, UpdateDefaultPermissions } from "../models/role.model";
import { Permission } from "../models/permission.model";
import { baseUrl } from "../constants/url.constant";

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

  getPossiblePermissions(id: number): Observable<Permission[]> {
    return this.httpClient.get<Permission[]>(this.url + "/" + id + "/possible-permissions");
  }

  getDefaultPermissions(id: number): Observable<Permission[]> {
    return this.httpClient.get<Permission[]>(this.url + "/" + id + "/default-permissions");
  }

  updateDefaultPermissions(updateDefaultPermissionsDto: UpdateDefaultPermissions[]): Observable<any> {
    return this.httpClient.put(this.url + "/update-default-permissions", updateDefaultPermissionsDto);
  }
}
