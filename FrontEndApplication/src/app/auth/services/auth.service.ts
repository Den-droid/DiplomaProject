import { HttpClient } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { ChangePasswordDto, ForgotPasswordDto, RoleTokensDto, SignInDto, SignUpByInviteDto, SignUpDto, SignUpScientistDto } from "../models/auth.model";
import { baseUrl } from "src/app/shared/constants/url.constant";
import { Observable } from "rxjs";

@Injectable()
export class AuthService {
  private url: string = baseUrl + "/auth";

  constructor(private readonly httpClient: HttpClient) {
  }

  signIn(signInDto: SignInDto): Observable<RoleTokensDto> {
    return this.httpClient.post<RoleTokensDto>(this.url + "/signIn", signInDto);
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
}
