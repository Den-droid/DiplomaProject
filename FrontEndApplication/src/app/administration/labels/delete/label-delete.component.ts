import { Component, OnInit } from '@angular/core';
import { Router, ActivatedRoute, Params } from '@angular/router';
import { Label, DeleteLabelDto, GetLabelsDto } from '../../../shared/models/label.model';
import { LabelService } from '../../../shared/services/label.service';

@Component({
  selector: 'app-administration-label-delete',
  templateUrl: './label-delete.component.html',
  styleUrls: ['./label-delete.component.css']
})
export class LabelDeleteComponent implements OnInit{
  _searchQuery = '';

  public get searchQuery(): string {
    return this._searchQuery;
  }

  public set searchQuery(v: string) {
    this._searchQuery = v;
    this.selectedLabel = 0;
  }

  selectedLabel = 0;
  error = '';
  currentLabel!: Label;
  labels: Label[] = [];

  constructor(private readonly router: Router, private readonly labelService: LabelService,
    private readonly activatedRoute: ActivatedRoute) {
  }

  ngOnInit(): void {
    this.activatedRoute.params.subscribe((data: Params) => {
      let id = data['id'];
      this.labelService.getLabelById(id).subscribe({
        next: (result: Label) => {
          this.currentLabel = result;

          this.labelService.getAllLabels().subscribe({
            next: (data: GetLabelsDto) => {
              this.labels = data.labels.filter(label => label.name !== this.currentLabel.name);
            }
          })
        },
        error: (error: any) => {
          this.router.navigateByUrl("/error/404");
        }
      });
    });
  }

  validate(): string {
    if (this.selectedLabel === 0) {
      return "Select label!";
    }
    return '';
  }

  deleteLabel() {
    let validationResult = this.validate();
    if (validationResult.length > 0) {
      this.error = validationResult;
      return;
    } else {
      this.error = '';
    }

    let deleteLabelDto = new DeleteLabelDto(this.selectedLabel);

    this.labelService.deleteLabel(this.currentLabel.id, deleteLabelDto).subscribe({
      error: (error: any) => {
        this.error = error?.error?.error;
      },
      complete: () => {
        this.router.navigateByUrl("/user/labels");
      }
    })
  }
}
