import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Params, Router } from '@angular/router';
import { EditFieldDto, Field } from 'src/app/shared/models/field.model';
import { FieldService } from 'src/app/shared/services/field.service';

@Component({
  selector: 'app-administration-field-edit',
  templateUrl: './field-edit.component.html',
  styleUrls: ['./field-edit.component.css']
})
export class FieldEditComponent implements OnInit {
  id!: number;
  name = '';
  error = '';

  constructor(private readonly router: Router, private readonly activatedRoute: ActivatedRoute,
    private readonly fieldService: FieldService
  ) {
  }

  ngOnInit(): void {
    this.activatedRoute.params.subscribe((data: Params) => {
      this.id = data['id'];
      this.fieldService.getFieldById(this.id).subscribe({
        next: (result: Field) => {
          this.id = result.id;
          this.name = result.name;
        },
        error: (error: any) => {
          this.router.navigateByUrl("/error/404");
        }
      });
    });
  }

  editField() {
    let validationResult = this.validate();
    if (validationResult.length > 0) {
      this.error = validationResult;
      return;
    } else {
      this.error = '';
    }

    let editFieldDto = new EditFieldDto(this.name);

    this.fieldService.editField(this.id, editFieldDto).subscribe({
      error: (error: any) => {
        this.error = error?.error?.error;
      },
      complete: () => {
        this.router.navigateByUrl("/user/fields");
      }
    })
  }

  validate(): string {
    if (this.name.length === 0) {
      return "Введіть назву поля!";
    }
    return '';
  }
}
