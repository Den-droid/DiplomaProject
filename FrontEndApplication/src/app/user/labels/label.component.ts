import { Component, OnInit } from '@angular/core';
import { LabelService } from '../services/label.service';
import { GetLabelsDto, Label } from '../models/label.model';
import { Router } from '@angular/router';

@Component({
  selector: 'app-user-label',
  templateUrl: './label.component.html',
  styleUrls: ['./label.component.css']
})
export class LabelComponent implements OnInit {
  currentPage = 1;
  totalPages = 1;
  isEmpty = false;

  displayedLabels: Label[] | undefined = [];

  searchQuery = '';
  isSearchMode = false;
  searchedQuery = '';

  constructor(private readonly router: Router, private readonly labelService: LabelService) {
  }

  ngOnInit(): void {
    this.getPageElements(this.currentPage);
  }

  getPageElements(page: number) {
    this.labelService.getLabelsByPage(page).subscribe({
      next: (data: GetLabelsDto) => {
        if (data.labels.length == 0) {
          this.isEmpty = true;
          this.currentPage = 1;
          this.totalPages = 1;
        } else {
          this.isEmpty = false;
          this.displayedLabels = data.labels;
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
      this.labelService.getLabelsByPageAndName(page, this.searchQuery).subscribe({
        next: (data: GetLabelsDto) => {
          if (data.labels.length == 0) {
            this.isEmpty = true;
            this.currentPage = 1;
            this.totalPages = 1;
          } else {
            this.isEmpty = false;
            this.displayedLabels = data.labels;
            this.currentPage = page;
            this.totalPages = data.pageDto.totalPages;
          }
        }
      });
    }
  }

  goToEditPage(id: number) {
    this.router.navigateByUrl("/user/labels/edit/" + id);
  }

  goToDeletePage(id: number) {
    this.router.navigateByUrl("/user/labels/delete/" + id);
  }

  goToAddPage() {
    this.router.navigateByUrl("/user/labels/add");
  }
}
