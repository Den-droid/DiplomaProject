import { Component, EventEmitter, Input, OnInit, Output, SimpleChanges } from '@angular/core';

@Component({
  selector: 'app-pagination',
  templateUrl: './pagination.component.html',
  styleUrls: ['./pagination.component.css']
})
export class PaginationComponent implements OnInit {
  _currentPage = 1;
  _totalPages = 1;

  @Input() set currentPage(value: number) {
    this._currentPage = value;
    this.updatePages();
  }

  get currentPage(): number {
    return this._currentPage;
  }

  @Input() set totalPages(value: number) {
    this._totalPages = value;
    this.updatePages();
  }

  get totalPages(): number {
    return this._totalPages;
  }

  @Output('pageChange')
  pageChange: EventEmitter<number> = new EventEmitter();

  pages: number[] = [];
  pagesText: string[] = [];

  ngOnInit(): void {
    this.updatePages();
  }

  updatePages() {
    this.pages = [];
    if (this.totalPages <= 9) {
      for (let i = 0; i < this.totalPages; i++) {
        this.pages.push(i + 1);
        this.pagesText.push((i + 1).toString());
      }
    } else {
      if (this.currentPage >= 5 && this.currentPage <= this.totalPages - 5) {
        let suffix = -2;
        for (let i = 0; i < 9; i++) {
          if (i === 0) {
            this.pages[i] = 1;
            this.pagesText[i] = '1';
          } else if (i == 1) {
            this.pages[i] = this.currentPage - 3;
            this.pagesText[i] = '...';
          } else if (i >= 2 && i <= 6) {
            this.pages[i] = this.currentPage + suffix;
            this.pagesText[i] = (this.currentPage + suffix).toString();
            suffix++;
          } else if (i == 7) {
            this.pages[i] = this.currentPage + 3;
            this.pagesText[i] = '...';
          } else if (i == 8) {
            this.pages[i] = this.totalPages;
            this.pagesText[i] = (this.totalPages).toString();
          }
        }
      }
      else if (this.currentPage < 5) {
        for (let i = 0; i < 7; i++) {
          this.pages[i] = i + 1;
          this.pagesText[i] = (i + 1).toString();
        }
        this.pages[7] = 8;
        this.pagesText[7] = '...';

        this.pages[8] = this.totalPages;
        this.pagesText[8] = (this.totalPages).toString();
      }

      else if (this.totalPages - 5 < this.currentPage) {
        this.pages[0] = 1;
        this.pagesText[0] = '1';

        this.pages[1] = this.totalPages - 7;
        this.pagesText[1] = '...';

        let pageIndex = 2;

        for (let i = this.totalPages - 6; i <= this.totalPages; i++) {
          this.pages[pageIndex] = i;
          this.pagesText[pageIndex] = i.toString();
          pageIndex++;
        }
      }
    }
  }

  changePage(selectedPage: number) {
    this.currentPage = selectedPage;
    this.updatePages();
    this.pageChange.emit(this.currentPage);
  }

  previousPage() {
    this.changePage(this.currentPage - 1);
  }

  nextPage() {
    this.changePage(this.currentPage + 1);
  }
}
