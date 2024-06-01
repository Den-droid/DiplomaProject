import { HttpClient, HttpParams } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { Observable } from "rxjs";
import { CreateFieldDto, DeleteFieldDto, UpdateFieldDto, Field, FieldType, GetFieldsDto } from "../models/field.model";
import { baseUrl } from "../constants/url.constant";

@Injectable({
  providedIn: 'root'
})
export class FieldService {
  private url: string = baseUrl + "/fields";

  constructor(private readonly httpClient: HttpClient) {
  }

  getAllFieldTypes(): Observable<FieldType[]> {
    return this.httpClient.get<FieldType[]>(this.url + "/types");
  }

  getAll(): Observable<GetFieldsDto> {
    return this.httpClient.get<GetFieldsDto>(this.url);
  }

  getAllByPage(currentPage: number): Observable<GetFieldsDto> {
    const options = currentPage ?
      { params: new HttpParams().set('currentPage', currentPage) } : {};

    return this.httpClient.get<GetFieldsDto>(this.url, options);
  }

  search(currentPage: number, name: string): Observable<GetFieldsDto> {
    const options = currentPage ?
      {
        params: new HttpParams().set('currentPage', currentPage)
          .set('name', name)
      } : {};

    return this.httpClient.get<GetFieldsDto>(this.url + "/search", options);
  }

  getById(id: number): Observable<Field> {
    return this.httpClient.get<Field>(this.url + "/" + id);
  }

  create(addFieldDto: CreateFieldDto): Observable<any> {
    return this.httpClient.post(this.url, addFieldDto);
  }

  update(id: number, editFieldDto: UpdateFieldDto): Observable<any> {
    return this.httpClient.put(this.url + "/" + id, editFieldDto);
  }

  delete(id: number, deleteFieldDto: DeleteFieldDto): Observable<any> {
    return this.httpClient.put(this.url + "/" + id + "/delete", deleteFieldDto);
  }
}
