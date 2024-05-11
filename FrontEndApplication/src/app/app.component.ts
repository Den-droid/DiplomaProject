import { Component } from '@angular/core';
import { AuthService } from './shared/services/auth.service';
import { JWTTokenService } from './shared/services/jwt-token.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {
  isAuthenticated = false;
  role = '';

  constructor(private router: Router, private jwtService: JWTTokenService, private authService: AuthService) {
    jwtService.tokenChange.subscribe({
      next: (value: string | null) => {
        if (value === null || value === '') {
          this.isAuthenticated = false;
          this.role = '';
        } else {
          this.isAuthenticated = authService.isAuthenticated();
          this.role = jwtService.getRoles()[0];
        }
      }
    })
  }

  logout() {
    this.authService.logout();
    this.router.navigateByUrl("/auth/signin");
  }
}
