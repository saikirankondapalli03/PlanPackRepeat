import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class LoadingService {

  showLoadingMask = false;
  loadingMessage = '';

  constructor() { }

  enableLoadingMask(message?: string): void {
    // setTimeout(() => {
    this.showLoadingMask = true;
    if (typeof message !== 'undefined') {
      this.loadingMessage = message;
    }
    // }, 500);
  }

  disableLoadingMask(): void {
    this.showLoadingMask = false;
    this.loadingMessage = '';
  }
}
