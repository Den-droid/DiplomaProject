import { HttpClient } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { Permission } from "../models/permission.model";
import { Observable } from "rxjs";
import { baseUrl } from "../constants/url.constant";

@Injectable({
  providedIn: 'root'
})
export class PermissionService {
  private url: string = baseUrl + "/permissions";

  constructor(private readonly httpClient: HttpClient) {
  }

  getAll(): Observable<Permission[]> {
    return this.httpClient.get<Permission[]>(this.url);
  }

}
