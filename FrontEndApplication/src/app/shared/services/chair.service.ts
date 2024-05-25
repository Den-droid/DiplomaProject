import { HttpClient } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { Observable } from "rxjs";
import { baseUrl } from "../constants/url.constant";
import { Chair } from "../models/chair.model";

@Injectable({
  providedIn: 'root'
})
export class ChairService {
  private url: string = baseUrl + "/chairs";

  constructor(private readonly httpClient: HttpClient) {
  }

  getAll(): Observable<Chair[]> {
    return this.httpClient.get<Chair[]>(this.url);
  }
}
