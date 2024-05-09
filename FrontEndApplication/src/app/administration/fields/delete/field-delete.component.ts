import { Component, OnInit } from '@angular/core';
import { Router, ActivatedRoute, Params } from '@angular/router';
import { DeleteFieldDto, Field, GetFieldsDto } from 'src/app/shared/models/field.model';
import { FieldService } from 'src/app/shared/services/field.service';

@Component({
  selector: 'app-administration-field-delete',
  templateUrl: './field-delete.component.html',
  styleUrls: ['./field-delete.component.css']
})
export class FieldDeleteComponent implements OnInit {
  _searchQuery = '';

  public get searchQuery(): string {
    return this._searchQuery;
  }

  public set searchQuery(v: string) {
    this._searchQuery = v;
    this.selectedField = 0;
  }

  selectedField = 0;
  error = '';
  currentField!: Field;
  fields: Field[] = [];

  constructor(private readonly router: Router, private readonly activatedRoute: ActivatedRoute,
    private readonly fieldService: FieldService,
  ) {
  }

  ngOnInit(): void {
    this.activatedRoute.params.subscribe((data: Params) => {
      let id = data['id'];
      this.fieldService.getFieldById(id).subscribe({
        next: (result: Field) => {
          this.currentField = result;

          this.fieldService.getAllFields().subscribe({
            next: (data: GetFieldsDto) => {
              this.fields = data.fields.filter(field => field.name !== this.currentField.name &&
                field.fieldType.id === this.currentField.fieldType.id
              );
            }
          })
        },
        error: (error: any) => {
          this.router.navigateByUrl("/auth/signin");
        }
      });
    });
  }

  validate(): string {
    if (this.selectedField === 0) {
      return "Select field!";
    }
    return '';
  }

  deleteField() {
    let validationResult = this.validate();
    if (validationResult.length > 0) {
      this.error = validationResult;
      return;
    } else {
      this.error = '';
    }

    let deleteFieldDto = new DeleteFieldDto(this.selectedField);

    this.fieldService.deleteField(this.currentField.id, deleteFieldDto).subscribe({
      error: (error: any) => {
        this.error = error?.error?.error;
      },
      complete: () => {
        this.router.navigateByUrl("/user/fields");
      }
    })
  }
}
