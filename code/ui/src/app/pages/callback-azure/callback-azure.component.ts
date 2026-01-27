import { Component, OnInit } from '@angular/core';
import { finalize } from 'rxjs/operators';
import { AzureAdAuthService } from '../../auth/azure-ad/azure-ad-auth.service';
import { LoadingService } from '../../shared/loading/loading.service';

/**
 * Handles Azure AD redirect callback after sign-in.
 * Interview: Part 6 (routing), Part 7 (ngOnInit).
 * @see ANGULAR_INTERVIEW_GUIDE/10-AZURE-AD-IDENTITY-INTEGRATION.md
 */
@Component({
  selector: 'app-callback-azure',
  templateUrl: './callback-azure.component.html',
  styleUrls: ['./callback-azure.component.css']
})
export class CallbackAzureComponent implements OnInit {
  constructor(
    private azureAd: AzureAdAuthService,
    private loadingService: LoadingService
  ) {}

  ngOnInit(): void {
    this.loadingService.enableLoadingMask();
    this.azureAd
      .handleRedirectCallback()
      .pipe(finalize(() => this.loadingService.disableLoadingMask()))
      .subscribe(
        () => this.azureAd.navigateToRedirectTarget(),
        () => this.azureAd.navigateToRedirectTarget()
      );
  }
}
