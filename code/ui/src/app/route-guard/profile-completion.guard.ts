import { Injectable } from '@angular/core';
import {
  ActivatedRouteSnapshot,
  RouterStateSnapshot,
  UrlTree,
  CanActivate,
  Router
} from '@angular/router';
import { Observable, of } from 'rxjs';
import { map } from 'rxjs/operators';
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
    let userEmail: string;
    return true;
    return this.userService.getUserInfo('contact.saransh29@gmail.com').pipe(map(
          localUserInfoRef => {
            if (!localUserInfoRef.firstName || !localUserInfoRef.lastName) {
              this.navigateToProfilePage()
              return false;
            }
            return true;
          },
          error => {
            this.navigateToProfilePage()
            return false;
          }
        ));
  }

  private navigateToProfilePage(): void {
    this.router.navigateByUrl('/profile');
  }
  }
