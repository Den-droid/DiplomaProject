import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { Field, GetFieldsDto } from 'src/app/shared/models/field.model';
import { FieldService } from 'src/app/shared/services/field.service';

@Component({
  selector: 'app-administration-field',
  templateUrl: './field.component.html',
  styleUrls: ['./field.component.css']
})
export class FieldComponent implements OnInit {
  currentPage = 1;
  totalPages = 1;

  displayedFields: Field[] = [];

  searchQuery = '';
  isSearchMode = false;
  searchedQuery = '';

  constructor(private readonly router: Router, private readonly fieldService: FieldService) {
  }

  ngOnInit(): void {
    this.getPageElements(this.currentPage);
  }

  getPageElements(page: number) {
    this.fieldService.getFieldsByPage(page).subscribe({
      next: (data: GetFieldsDto) => {
        if (data.fields.length == 0) {
          this.currentPage = 1;
          this.totalPages = 1;
        } else {
          this.displayedFields = data.fields;
          this.currentPage = page;
          this.totalPages = data.pageDto.totalPages;
        }
      }
    });
  }

  pageChange(page: number) {
    if (this.isSearchMode) {
      this.search(page);
    }

    this.getPageElements(page);

    window.scroll({
      top: 0,
      left: 0,
      behavior: 'smooth'
    });
  }

  search(page: number) {
    if (this.searchQuery === '') {
      this.isSearchMode = false;
      this.pageChange(1);
    } else {
      this.isSearchMode = true;
      this.searchedQuery = this.searchQuery;
      this.fieldService.getFieldsByPageAndName(page, this.searchQuery).subscribe({
        next: (data: GetFieldsDto) => {
          if (data.fields.length == 0) {
            this.currentPage = 1;
            this.totalPages = 1;
          } else {
            this.displayedFields = data.fields;
            this.currentPage = page;
            this.totalPages = data.pageDto.totalPages;
          }
        }
      });
    }
  }

  goToEditPage(id: number) {
    this.router.navigateByUrl("/user/fields/" + id + "/edit");
  }

  goToDeletePage(id: number) {
    this.router.navigateByUrl("/user/fields/" + id + "/delete");
  }
}
