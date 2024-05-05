import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { UserService } from '../../shared/services/user.service';

@Component({
  selector: 'app-administration-user',
  templateUrl: './user.component.html',
  styleUrls: ['./user.component.css']
})
export class UserComponent {
  searchQuery = '';

  constructor(private readonly router: Router, private readonly userService: UserService) { }

  search(page: number) {

  }

  goToAddPage() {

  }
}
