import { Injectable } from '@angular/core';
import {
  CanActivate,
  ActivatedRouteSnapshot,
  RouterStateSnapshot
} from '@angular/router';

/**
 * Interview: Part 6 - Routing & Guards
 * CanActivate guard: runs before route activation.
 * This demo guard always allows access; real guards check auth/permissions.
 */
@Injectable({ providedIn: 'root' })
export class PaginationGuard implements CanActivate {
  canActivate(
    _route: ActivatedRouteSnapshot,
    _state: RouterStateSnapshot
  ): boolean {
    return true;
  }
}
