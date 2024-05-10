import { Injectable } from "@angular/core";
import { CanActivate, Router, ActivatedRouteSnapshot, RouterStateSnapshot, UrlTree } from "@angular/router";
import { Observable } from "rxjs";
import { AuthService } from "../services/auth.service";

@Injectable({
  providedIn: 'root',
})
export class AnyAdminGuard implements CanActivate {
  constructor(
    private readonly authorizeService: AuthService,
    private readonly router: Router,
  ) {
  }

  canActivate(route: ActivatedRouteSnapshot,
    state: RouterStateSnapshot): Observable<boolean | UrlTree> | Promise<boolean | UrlTree> | boolean | UrlTree {
    if (this.authorizeService.isChairAdmin() || this.authorizeService.isFacultyAdmin() || this.authorizeService.isAdmin()) {
      return true;
    }

    return this.router.createUrlTree(["/", "error", "403"]);
  }
}
