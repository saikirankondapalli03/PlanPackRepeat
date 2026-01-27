import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { BehaviorSubject, Observable, from, of } from 'rxjs';
import { map, catchError, tap } from 'rxjs/operators';
import { MsalService } from '@azure/msal-angular';
import { azureAdConfig } from '../../models/azure-ad-config';

/**
 * Azure AD auth service – wraps MSAL for login, logout, and user state.
 * Interview: Part 3 (Services/DI), Part 4 (Observables), Part 6 (Guards use this).
 * Supports MSAL Angular 1.x (Angular 8) and 2.x (Angular 10+).
 * @see ANGULAR_INTERVIEW_GUIDE/10-AZURE-AD-IDENTITY-INTEGRATION.md
 */
@Injectable({ providedIn: 'root' })
export class AzureAdAuthService {
  private loggedInSubject = new BehaviorSubject<boolean>(false);
  private userSubject = new BehaviorSubject<{ name?: string; email?: string; [key: string]: any } | null>(null);

  isAuthenticated$: Observable<boolean> = this.loggedInSubject.asObservable();
  user$: Observable<{ name?: string; email?: string } | null> = this.userSubject.asObservable();

  get loggedIn(): boolean {
    return this.loggedInSubject.value;
  }

  constructor(
    private msal: MsalService,
    private router: Router
  ) {
    this.syncAccountFromMsal();
  }

  /** Sync internal state from MSAL account cache */
  private syncAccountFromMsal(): void {
    try {
      const account = this.msal.getAccount();
      this.loggedInSubject.next(!!account);
      this.userSubject.next(account ? { name: (account as any).name || undefined, ...(account as any) } : null);
    } catch {
      this.loggedInSubject.next(false);
      this.userSubject.next(null);
    }
  }

  /** Initiate Azure AD login (redirect flow). */
  login(targetPath: string = '/pagination-azure'): void {
    sessionStorage.setItem('azureAdRedirectTarget', targetPath);
    const opts = azureAdConfig.scopes?.length ? { scopes: azureAdConfig.scopes } : {};
    (this.msal as any).loginRedirect(opts);
  }

  /** Sign out and redirect to post-logout URI */
  logout(): void {
    (this.msal as any).logout?.();
    this.loggedInSubject.next(false);
    this.userSubject.next(null);
  }

  /**
   * Handle redirect callback after Azure AD redirects back to app.
   * Call from CallbackAzureComponent (ngOnInit).
   * MSAL 2.x: handleRedirectPromise(); MSAL 1.x: handleRedirectCallback(success, error).
   */
  handleRedirectCallback(): Observable<boolean> {
    const msalAny = this.msal as any;
    if (typeof msalAny.handleRedirectPromise === 'function') {
      return from(msalAny.handleRedirectPromise()).pipe(
        tap((res: any) => {
          if (res?.account) {
            this.loggedInSubject.next(true);
            this.userSubject.next({ name: res.account.name, ...res.account });
          } else {
            this.syncAccountFromMsal();
          }
        }),
        map(() => this.loggedInSubject.value),
        catchError(() => {
          this.syncAccountFromMsal();
          return of(false);
        })
      );
    }
    if (typeof msalAny.handleRedirectCallback === 'function') {
      return new Observable<boolean>((obs) => {
        try {
          msalAny.handleRedirectCallback(
            () => {
              this.syncAccountFromMsal();
              obs.next(this.loggedInSubject.value);
              obs.complete();
            },
            () => {
              this.syncAccountFromMsal();
              obs.next(false);
              obs.complete();
            }
          );
        } catch {
          this.syncAccountFromMsal();
          obs.next(false);
          obs.complete();
        }
      });
    }
    this.syncAccountFromMsal();
    return of(this.loggedInSubject.value);
  }

  /** Navigate to stored redirect target (used after callback) */
  navigateToRedirectTarget(): void {
    const target = sessionStorage.getItem('azureAdRedirectTarget') || '/pagination-azure';
    sessionStorage.removeItem('azureAdRedirectTarget');
    this.router.navigateByUrl(target);
  }
}
