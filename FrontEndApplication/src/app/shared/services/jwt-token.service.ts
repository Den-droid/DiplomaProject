import { Injectable } from "@angular/core";
import { LocalStorageService } from "./local-storage.service";
import { jwtDecode } from 'jwt-decode';
import { BehaviorSubject } from "rxjs";

@Injectable({
  providedIn: 'root'
})
export class JWTTokenService {
  constructor(
    private readonly localStorage: LocalStorageService
  ) {
  }

  tokenChange: BehaviorSubject<string | null> = new BehaviorSubject<string | null>(this.jwtToken && !this.isTokenExpired()
    ? this.jwtToken : '');

  get jwtToken(): string | null {
    return this.localStorage.get('access_token');
  }

  get decodedToken(): { [key: string]: string | string[] } {
    if (this.jwtToken) {
      return jwtDecode(this.jwtToken);
    } else {
      return {};
    }
  }

  getToken(): string | null {
    return this.localStorage.get('access_token');
  }

  setToken(token: string) {
    if (token) {
      this.localStorage.set('access_token', token);
      this.tokenChange.next(token);
    }
  }

  getRefreshToken(): string {
    let refreshToken = this.localStorage.get('refresh_token') ?? '';
    return refreshToken;
  }

  setRefreshToken(refreshToken: string) {
    if (refreshToken) {
      this.localStorage.set('refresh_token', refreshToken);
    }
  }

  isTokenAvailable(): boolean {
    // eslint-disable-next-line prefer-const
    let token = this.localStorage.get('access_token') ?? '';
    return (token !== '');
  }

  getRoles() {
    return this.decodedToken['roles'];
  }

  getExpiryTime() {
    return this.decodedToken['exp'];
  }

  isTokenExpired(): boolean {
    const expiryTime: number = Number(this.getExpiryTime());
    if (expiryTime) {
      return ((1000 * expiryTime) - (new Date()).getTime()) < 5000;
    } else {
      return false;
    }
  }

  clearTokens() {
    this.localStorage.clear();
    this.tokenChange.next('');
  }
}

