import { Component, OnInit } from '@angular/core';
import { EditLabelDto, Label } from '../../../shared/models/label.model';
import { ActivatedRoute, Params, Router } from '@angular/router';
import { LabelService } from '../../../shared/services/label.service';

@Component({
  selector: 'app-administration-label-edit',
  templateUrl: './label-edit.component.html',
  styleUrls: ['./label-edit.component.css']
})
export class LabelEditComponent implements OnInit {
  id!: number;
  name = '';
  error = '';

  constructor(private readonly router: Router, private readonly labelService: LabelService,
    private readonly activatedRoute: ActivatedRoute) {
  }

  ngOnInit(): void {
    this.activatedRoute.params.subscribe((data: Params) => {
      this.id = data['id'];
      this.labelService.getLabelById(this.id).subscribe({
        next: (result: Label) => {
          this.id = result.id;
          this.name = result.name;
        },
        error: (error: any) => {
          this.router.navigateByUrl("/error/404");
        }
      });
    });
  }

  editLabel() {
    let validationResult = this.validate();
    if (validationResult.length > 0) {
      this.error = validationResult;
      return;
    } else {
      this.error = '';
    }

    let editLabelDto = new EditLabelDto(this.name);

    this.labelService.editLabel(this.id, editLabelDto).subscribe({
      error: (error: any) => {
        this.error = error?.error?.error;
        this.clearName();
      },
      complete: () => {
        this.clear();
        this.router.navigateByUrl("/user/labels");
      }
    })
  }

  validate(): string {
    if (this.name.length === 0) {
      return "Enter name!";
    }
    return '';
  }

  clear() {
    this.name = '';
    this.error = '';
  }

  clearName() {
    this.name = '';
  }
}
