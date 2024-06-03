import { HttpInterceptor, HttpRequest, HttpHandler, HttpEvent } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { BehaviorSubject, Observable, catchError, concatMap, filter, finalize, take, throwError } from "rxjs";
import { JWTTokenService } from "../services/jwt-token.service";
import { AuthService } from "../services/auth.service";
import { RefreshTokenDto, TokensDto } from "../models/auth.model";
import { Router } from "@angular/router";

@Injectable({
  providedIn: 'root',
})
export class AuthorizeInterceptor implements HttpInterceptor {
  constructor(private readonly jwtService: JWTTokenService, private readonly authService: AuthService,
    private readonly router: Router
  ) { }
  isRefreshingToken = false;

  tokenRefreshed$ = new BehaviorSubject<boolean>(false);

  setHeader(req: HttpRequest<any>): HttpRequest<any> {
    const token = this.jwtService.getToken();
    return token ? req.clone({ setHeaders: { Authorization: 'Bearer ' + token } }) : req;
  }

  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    return next.handle(this.setHeader(req)).pipe(
      catchError(err => {
        if (err.status === 401) {
          return this.handle401Error(req, next);
        }

        return throwError(err);
      })
    );
  }

  private handle401Error(req: HttpRequest<any>, next: HttpHandler): Observable<any> {
    if (this.isRefreshingToken) {
      return this.tokenRefreshed$.pipe(
        filter(Boolean),
        take(1),
        concatMap(() => next.handle(this.setHeader(req)))
      );
    }

    this.isRefreshingToken = true;

    // Reset here so that the following requests wait until the token
    // comes back from the refreshToken call.
    this.tokenRefreshed$.next(false);

    return this.authService.refreshToken(new RefreshTokenDto(this.jwtService.getRefreshToken())).pipe(
      concatMap((res: TokensDto) => {
        this.jwtService.setToken(res.accessToken);
        this.jwtService.setRefreshToken(res.refreshToken);

        this.tokenRefreshed$.next(true);
        return next.handle(this.setHeader(req));
      }),
      catchError((err) => {
        this.authService.logout();
        this.router.navigateByUrl("/auth/signin");
        return throwError(err);
      }),
      finalize(() => {
        this.isRefreshingToken = false;
      })
    );
  }
}

