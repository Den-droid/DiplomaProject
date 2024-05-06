import { HttpInterceptor, HttpRequest, HttpHandler, HttpEvent, HttpErrorResponse } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { Observable, share, catchError, switchMap, filter, take, BehaviorSubject, throwError, tap } from "rxjs";
import { JWTTokenService } from "../services/jwt-token.service";
import { isApiRequest } from "../helpers/interceptors.helper";
import { AuthService } from "../services/auth.service";
import { RefreshTokenDto, TokensDto } from "../models/auth.model";
import { Router } from "@angular/router";

@Injectable({
  providedIn: 'root',
})
export class AuthorizeInterceptor implements HttpInterceptor {
  constructor(private readonly jwtService: JWTTokenService, private readonly authService: AuthService) { }

  // intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
  //   return this.processRequestWithToken(req, next);
  // }

  // // Checks if there is an access_token available in the authorize service
  // // and adds it to the request in case it's targeted at the same origin as the
  // // single page application.
  // private processRequestWithToken(req: HttpRequest<any>, next: HttpHandler) {
  //   if (this.jwtService.jwtToken && isApiRequest(req)) {
  //     console.log();
  //     if (this.jwtService.isTokenExpired() && !req.url.includes('refreshToken')) {
  //       this.authService.refreshToken(new RefreshTokenDto(this.jwtService.getRefreshToken())).pipe(
  //         switchMap((data : TokensDto) => {
  //           this.jwtService.setToken(data.accessToken);
  //           this.jwtService.setRefreshToken(data.refreshToken);

  //           req = req.clone({
  //             setHeaders: {
  //               Authorization: `Bearer ${this.jwtService.jwtToken}`,
  //             },
  //           });
  //           return next.handle(req);
  //         })
  //       )
  //     }
  //     req = req.clone({
  //       setHeaders: {
  //         Authorization: `Bearer ${this.jwtService.jwtToken}`,
  //       },
  //     });
  //   }

  //   return next.handle(req);
  // }

  // refresh = false;

  intercept(request: HttpRequest<unknown>, next: HttpHandler): Observable<HttpEvent<unknown>> {
    const req = request.clone({
      setHeaders: {
        Authorization: `Bearer ${this.jwtService.getToken()}`
      }
    });

    return next.handle(req);

    // return next.handle(req).pipe(catchError((err: HttpErrorResponse) => {
    //   if (err.status === 401 && !this.refresh) {
    //     this.refresh = true;

    //     console.log("refresh");

    //     return this.authService.refreshToken(new RefreshTokenDto(this.jwtService.getRefreshToken())).pipe(
    //       switchMap((res: TokensDto) => {
    //         console.log(res);

    //         this.jwtService.setToken(res.accessToken);
    //         this.jwtService.setRefreshToken(res.refreshToken);

    //         return next.handle(request.clone({
    //           setHeaders: {
    //             Authorization: `Bearer ${this.jwtService.getToken()}`
    //           }
    //         }));
    //       })
    //     );
    //   }
    //   this.refresh = false;
    //   return throwError(() => err);
    // }));
  }
}
