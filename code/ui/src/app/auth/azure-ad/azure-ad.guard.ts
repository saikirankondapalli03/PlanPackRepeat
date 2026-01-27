import { Injectable } from '@angular/core';
import { CanActivate, ActivatedRouteSnapshot, RouterStateSnapshot, Router } from '@angular/router';
import { Observable, of } from 'rxjs';
import { map, take, catchError } from 'rxjs/operators';
import { AzureAdAuthService } from './azure-ad-auth.service';

/**
 * Route guard that requires Azure AD sign-in.
 * Interview: Part 6 – Routing & Guards (CanActivate).
 * @see ANGULAR_INTERVIEW_GUIDE/10-AZURE-AD-IDENTITY-INTEGRATION.md
 */
@Injectable({ providedIn: 'root' })
export class AzureAdGuard implements CanActivate {
  constructor(
    private azureAd: AzureAdAuthService,
    private router: Router
  ) {}

  canActivate(
    route: ActivatedRouteSnapshot,
    state: RouterStateSnapshot
  ): Observable<boolean> {
    return this.azureAd.isAuthenticated$.pipe(
      take(1),
      map((loggedIn) => {
        if (loggedIn) return true;
        this.azureAd.login(state.url);
        return false;
      }),
      catchError(() => {
        this.azureAd.login(state.url);
        return of(false);
      })
    );
  }
}
