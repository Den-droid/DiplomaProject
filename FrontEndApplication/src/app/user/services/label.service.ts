import { HttpClient, HttpParams } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { Observable } from "rxjs";
import { baseUrl } from "src/app/shared/constants/url.constant";
import { AddLabelDto, DeleteLabelDto, EditLabelDto, GetLabelsDto, Label } from "../models/label.model";

@Injectable()
export class LabelService {
  private labelsUrl: string = baseUrl + "/labels";

  constructor(private readonly httpClient: HttpClient) {
  }

  getAllLabels(): Observable<GetLabelsDto> {
    return this.httpClient.get<GetLabelsDto>(this.labelsUrl);
  }

  getLabelsByPage(currentPage: number): Observable<GetLabelsDto> {
    const options = currentPage ?
      { params: new HttpParams().set('currentPage', currentPage) } : {};

    return this.httpClient.get<GetLabelsDto>(this.labelsUrl, options);
  }

  getLabelsByPageAndName(currentPage: number, name: string): Observable<GetLabelsDto> {
    const options = currentPage && name ?
      { params: new HttpParams().set('currentPage', currentPage).set('name', name) } : {};

    return this.httpClient.get<GetLabelsDto>(this.labelsUrl + "/search", options);
  }

  getLabelById(id: number): Observable<Label> {
    return this.httpClient.get<Label>(this.labelsUrl + "/" + id);
  }

  addLabel(addLabelDto: AddLabelDto): Observable<any> {
    return this.httpClient.post(this.labelsUrl, addLabelDto);
  }


  editLabel(id: number, editLabelDto: EditLabelDto): Observable<any> {
    return this.httpClient.put(this.labelsUrl + "/" + id, editLabelDto);
  }


  deleteLabel(id: number, deleteLabelDto: DeleteLabelDto): Observable<any> {
    return this.httpClient.put(this.labelsUrl + "/delete/" + id, deleteLabelDto);
  }
}
