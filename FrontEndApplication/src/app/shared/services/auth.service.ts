import { HttpClient } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { ChangePasswordDto, ForgotPasswordDto, RefreshTokenDto, SignInDto, SignUpByInviteDto, SignUpDto, SignUpScientistDto, TokensDto } from "../models/auth.model";
import { baseUrl } from "src/app/shared/constants/url.constant";
import { Observable } from "rxjs";
import { JWTTokenService } from "./jwt-token.service";
import { RoleName } from "../constants/roles.constant";

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private url: string = baseUrl + "/auth";

  constructor(private readonly httpClient: HttpClient, private readonly jwtService: JWTTokenService) {
  }

  signIn(signInDto: SignInDto): Observable<TokensDto> {
    return this.httpClient.post<TokensDto>(this.url + "/signIn", signInDto);
  }

  getSignUp(): Observable<SignUpScientistDto[]> {
    return this.httpClient.get<SignUpScientistDto[]>(this.url + "/signUp");
  }

  signUp(signUpDto: SignUpDto): Observable<any> {
    return this.httpClient.post(this.url + "/signUp", signUpDto);
  }

  forgotPassword(forgotPasswordDto: ForgotPasswordDto): Observable<any> {
    return this.httpClient.post(this.url + "/forgotPassword/create", forgotPasswordDto);
  }

  existsByForgotPasswordToken(token: string): Observable<boolean> {
    return this.httpClient.get<boolean>(this.url + "/forgotPassword/tokenExists/" + token);
  }

  existsByInviteCode(inviteCode: string): Observable<boolean> {
    return this.httpClient.get<boolean>(this.url + "/signUp/existsByInviteCode/" + inviteCode);
  }

  changePassword(token: string, changePasswordDto: ChangePasswordDto): Observable<any> {
    return this.httpClient.post(this.url + "/forgotPassword/change/" + token, changePasswordDto);
  }

  signUpByInviteCode(inviteCode: string, signUpByInviteCode: SignUpByInviteDto): Observable<any> {
    return this.httpClient.put(this.url + "/signUp/" + inviteCode, signUpByInviteCode);
  }

  refreshToken(refreshTokenDto: RefreshTokenDto): Observable<TokensDto> {
    return this.httpClient.put<TokensDto>(this.url + "/refreshToken", refreshTokenDto);
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
