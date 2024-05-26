import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { LabelService } from '../../../shared/services/label.service';
import { AddLabelDto } from '../../../shared/models/label.model';

@Component({
  selector: 'app-administration-label-add',
  templateUrl: './label-add.component.html',
  styleUrls: ['./label-add.component.css']
})
export class LabelAddComponent {
  name = '';
  error = '';

  constructor(private readonly router: Router, private readonly labelService: LabelService) {
  }

  addLabel() {
    let validationResult = this.validate();
    if (validationResult.length > 0) {
      this.error = validationResult;
      return;
    } else {
      this.error = '';
    }

    let addLabelDto = new AddLabelDto(this.name);

    this.labelService.addLabel(addLabelDto).subscribe({
      error: (error: any) => {
        this.error = error?.error?.error;
      },
      complete: () => {
        this.router.navigateByUrl("/user/labels");
      }
    })
  }

  validate(): string {
    if (this.name.length === 0) {
      return "Введіть назву ключового слова!";
    }
    return '';
  }
}
