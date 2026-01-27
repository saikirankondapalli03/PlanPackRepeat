import { Injectable } from '@angular/core';
import {
  ActivatedRouteSnapshot,
  RouterStateSnapshot,
  UrlTree,
  CanActivate,
  Router
} from '@angular/router';
import { Observable, of } from 'rxjs';
import { map, catchError } from 'rxjs/operators';
import { AuthService } from '../auth/auth.service';
import { UserService } from '../services/user.service';

@Injectable({
  providedIn: 'root'
})
export class ProfileCompletionGuard implements CanActivate {

  constructor(private router: Router, private auth: AuthService, private userService: UserService) {}

  canActivate(
    next: ActivatedRouteSnapshot,
    state: RouterStateSnapshot
  ): Observable<boolean> | Promise<boolean|UrlTree> | boolean {
    const userEmail = this.userService.userEmail;
    
    if (!userEmail) {
      this.navigateToProfilePage();
      return false;
    }

    return this.userService.getUserInfo(userEmail).pipe(
      map(localUserInfoRef => {
        if (!localUserInfoRef || !localUserInfoRef.firstName || !localUserInfoRef.lastName) {
          this.navigateToProfilePage();
          return false;
        }
        return true;
      }),
      catchError(error => {
        this.navigateToProfilePage();
        return of(false);
      })
    );
  }

  private navigateToProfilePage(): void {
    this.router.navigateByUrl('/profile');
  }
}
