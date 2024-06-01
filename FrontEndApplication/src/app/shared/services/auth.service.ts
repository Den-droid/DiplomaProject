import { HttpClient, HttpParams } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { ChangePasswordDto, ForgotPasswordDto, RefreshTokenDto, SignInDto, SignUpByInviteDto, SignUpDto, TokensDto } from "../models/auth.model";
import { Observable } from "rxjs";
import { JWTTokenService } from "./jwt-token.service";
import { RoleName } from "../constants/roles.constant";
import { baseUrl } from "../constants/url.constant";

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private url: string = baseUrl + "/auth";

  constructor(private readonly httpClient: HttpClient, private readonly jwtService: JWTTokenService) {
  }

  signIn(signInDto: SignInDto): Observable<TokensDto> {
    return this.httpClient.post<TokensDto>(this.url + "/sign-in", signInDto);
  }

  signUp(signUpDto: SignUpDto): Observable<any> {
    return this.httpClient.post(this.url + "/sign-up", signUpDto);
  }

  forgotPassword(forgotPasswordDto: ForgotPasswordDto): Observable<any> {
    return this.httpClient.post(this.url + "/forgot-password/create", forgotPasswordDto);
  }

  existsByForgotPasswordToken(token: string): Observable<boolean> {
    const options = token ?
      {
        params: new HttpParams().set('token', token)
      } : {};

    return this.httpClient.get<boolean>(this.url + "/forgot-password/token-exists", options);
  }

  existsByInviteCode(inviteCode: string): Observable<boolean> {
    const options = inviteCode ?
      {
        params: new HttpParams().set('inviteCode', inviteCode)
      } : {};

    return this.httpClient.get<boolean>(this.url + "/sign-up/invite-code-exists", options);
  }

  changePassword(token: string, changePasswordDto: ChangePasswordDto): Observable<any> {
    return this.httpClient.post(this.url + "/forgot-password/change/" + token, changePasswordDto);
  }

  signUpByInviteCode(inviteCode: string, signUpByInviteCode: SignUpByInviteDto): Observable<any> {
    return this.httpClient.put(this.url + "/sign-up/" + inviteCode, signUpByInviteCode);
  }

  refreshToken(refreshTokenDto: RefreshTokenDto): Observable<TokensDto> {
    return this.httpClient.put<TokensDto>(this.url + "/refresh-token", refreshTokenDto);
  }

  isAuthenticated(): boolean {
    return this.jwtService.getToken() != null;
  }

  isUser(): boolean {
    return this.jwtService.getRoles()?.includes(RoleName.USER) ?? false;
  }

  isChairAdmin(): boolean {
    return this.jwtService.getRoles()?.includes(RoleName.CHAIR_ADMIN) ?? false;
  }

  isFacultyAdmin(): boolean {
    return this.jwtService.getRoles()?.includes(RoleName.FACULTY_ADMIN)
      ?? false;
  }

  isAdmin(): boolean {
    return this.jwtService.getRoles()?.includes(RoleName.MAIN_ADMIN) ?? false;
  }

  logout() {
    this.jwtService.clearTokens();
  }
}
