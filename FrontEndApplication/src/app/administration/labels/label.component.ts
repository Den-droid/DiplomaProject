import { Component, OnInit } from '@angular/core';
import { LabelService } from '../../shared/services/label.service';
import { GetLabelsDto, Label } from '../../shared/models/label.model';
import { Router } from '@angular/router';

@Component({
  selector: 'app-administration-label',
  templateUrl: './label.component.html',
  styleUrls: ['./label.component.css']
})
export class LabelComponent implements OnInit {
  currentPage = 1;
  totalPages = 1;

  displayedLabels: Label[] = [];

  searchQuery = '';
  isSearchMode = false;

  constructor(private readonly router: Router, private readonly labelService: LabelService) {
  }

  ngOnInit(): void {
    this.getPageElements(this.currentPage);
  }

  getPageElements(page: number) {
    this.labelService.getLabelsByPage(page).subscribe({
      next: (data: GetLabelsDto) => {
        if (data.labels.length == 0) {
          this.displayedLabels = [];
          this.currentPage = 1;
          this.totalPages = 1;
        } else {
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

      this.labelService.getLabelsByPageAndName(page, this.searchQuery).subscribe({
        next: (data: GetLabelsDto) => {
          if (data.labels.length == 0) {
            this.displayedLabels = [];
            this.currentPage = 1;
            this.totalPages = 1;
          } else {
            this.displayedLabels = data.labels;
            this.currentPage = page;
            this.totalPages = data.pageDto.totalPages;
          }
        }
      });
    }
  }

  goToEditPage(id: number) {
    this.router.navigateByUrl("/user/labels/" + id + "/edit");
  }

  goToDeletePage(id: number) {
    this.router.navigateByUrl("/user/labels/" + id + "/delete");
  }
}
