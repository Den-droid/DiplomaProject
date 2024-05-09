import { HttpClient, HttpParams } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { Observable } from "rxjs";
import { baseUrl } from "../constants/url.constant";
import { AddFieldDto, DeleteFieldDto, EditFieldDto, Field, FieldType, GetFieldsDto } from "../models/field.model";

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

  getAllFields(): Observable<GetFieldsDto> {
    return this.httpClient.get<GetFieldsDto>(this.url);
  }

  getFieldsByPage(currentPage: number): Observable<GetFieldsDto> {
    const options = currentPage ?
      { params: new HttpParams().set('currentPage', currentPage) } : {};

    return this.httpClient.get<GetFieldsDto>(this.url, options);
  }

  getFieldsByPageAndName(currentPage: number, name: string): Observable<GetFieldsDto> {
    const options = currentPage ?
      {
        params: new HttpParams().set('currentPage', currentPage)
          .set('name', name)
      } : {};

    return this.httpClient.get<GetFieldsDto>(this.url + "/search", options);
  }

  getFieldById(id: number): Observable<Field> {
    return this.httpClient.get<Field>(this.url + "/" + id);
  }

  addField(addFieldDto: AddFieldDto): Observable<any> {
    return this.httpClient.post(this.url, addFieldDto);
  }

  editField(id: number, editFieldDto: EditFieldDto): Observable<any> {
    return this.httpClient.put(this.url + "/" + id, editFieldDto);
  }

  deleteField(id: number, deleteFieldDto: DeleteFieldDto): Observable<any> {
    return this.httpClient.put(this.url + "/delete/" + id, deleteFieldDto);
  }
}
