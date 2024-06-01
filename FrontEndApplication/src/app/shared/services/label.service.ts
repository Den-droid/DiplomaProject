import { HttpClient, HttpParams } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { Observable } from "rxjs";
import { CreateLabelDto, DeleteLabelDto, UpdateLabelDto, GetLabelsDto, Label } from "../models/label.model";
import { baseUrl } from "../constants/url.constant";

@Injectable({
  providedIn: 'root'
})
export class LabelService {
  private labelsUrl: string = baseUrl + "/labels";

  constructor(private readonly httpClient: HttpClient) {
  }

  getAll(): Observable<GetLabelsDto> {
    return this.httpClient.get<GetLabelsDto>(this.labelsUrl);
  }

  getAllByPage(currentPage: number): Observable<GetLabelsDto> {
    const options = currentPage ?
      { params: new HttpParams().set('currentPage', currentPage) } : {};

    return this.httpClient.get<GetLabelsDto>(this.labelsUrl, options);
  }

  search(currentPage: number, name: string): Observable<GetLabelsDto> {
    const options = currentPage && name ?
      { params: new HttpParams().set('currentPage', currentPage).set('name', name) } : {};

    return this.httpClient.get<GetLabelsDto>(this.labelsUrl + "/search", options);
  }

  getById(id: number): Observable<Label> {
    return this.httpClient.get<Label>(this.labelsUrl + "/" + id);
  }

  create(addLabelDto: CreateLabelDto): Observable<any> {
    return this.httpClient.post(this.labelsUrl, addLabelDto);
  }


  update(id: number, editLabelDto: UpdateLabelDto): Observable<any> {
    return this.httpClient.put(this.labelsUrl + "/" + id, editLabelDto);
  }


  delete(id: number, deleteLabelDto: DeleteLabelDto): Observable<any> {
    return this.httpClient.put(this.labelsUrl + "/" + id + "/delete", deleteLabelDto);
  }
}
