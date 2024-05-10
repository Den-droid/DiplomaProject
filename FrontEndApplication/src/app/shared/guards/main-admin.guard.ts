import { Injectable } from "@angular/core";
import { ActivatedRouteSnapshot, CanActivate, Router, RouterStateSnapshot, UrlTree } from "@angular/router";
import { AuthService } from "../services/auth.service";
import { Observable } from "rxjs";

@Injectable({
  providedIn: 'root',
})
export class MainAdminGuard implements CanActivate {
  constructor(
    private readonly authorizeService: AuthService,
    private readonly router: Router,
  ) {
  }

  canActivate(route: ActivatedRouteSnapshot,
    state: RouterStateSnapshot): Observable<boolean | UrlTree> | Promise<boolean | UrlTree> | boolean | UrlTree {
    if (this.authorizeService.isAdmin()) {
      return true;
    }

    return this.router.createUrlTree(["/", "error", "403"]);
  }
}
