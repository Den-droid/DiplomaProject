import { Component, OnInit } from '@angular/core';
import { Router, ActivatedRoute, Params } from '@angular/router';
import { Label, DeleteLabelDto, GetLabelsDto } from '../../../shared/models/label.model';
import { LabelService } from '../../../shared/services/label.service';

@Component({
  selector: 'app-administration-label-delete',
  templateUrl: './label-delete.component.html',
  styleUrls: ['./label-delete.component.css']
})
export class LabelDeleteComponent implements OnInit {
  _searchQuery = '';

  selectedLabel = 0;
  error = '';

  currentLabel!: Label;
  labels: Label[] = [];
  possibleLabels: Label[] = [];

  constructor(private readonly router: Router, private readonly labelService: LabelService,
    private readonly activatedRoute: ActivatedRoute) {
  }

  ngOnInit(): void {
    this.activatedRoute.params.subscribe((data: Params) => {
      let id = data['id'];
      this.labelService.getById(id).subscribe({
        next: (result: Label) => {
          this.currentLabel = result;

          this.labelService.getAll().subscribe({
            next: (data: GetLabelsDto) => {
              this.labels = data.labels.filter(label => label.name !== this.currentLabel.name);

              this.setPossibleLabels();
            }
          })
        },
        error: (error: any) => {
          this.router.navigateByUrl("/error/404");
        }
      });
    });
  }

  setLabelSearchQuery(value: Event) {
    let valueText = (value.target as HTMLInputElement).value;

    this._searchQuery = valueText;
    this.selectedLabel = 0;

    this.setPossibleLabels();
  }

  setPossibleLabels() {
    this.possibleLabels = this.labels;

    this.possibleLabels = this.possibleLabels.filter(x => x.name.toLowerCase().includes(this._searchQuery.toLowerCase()));

    if (this.possibleLabels.length > 0)
      this.selectedLabel = this.possibleLabels[0].id;
  }

  validate(): string {
    if (this.selectedLabel === 0) {
      return "Виберіть ключове слово!";
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

    this.labelService.delete(this.currentLabel.id, deleteLabelDto).subscribe({
      error: (error: any) => {
        this.error = error?.error?.error;
      },
      complete: () => {
        this.router.navigateByUrl("/user/labels");
      }
    })
  }
}
