import { azureAdConfig } from '../../models/azure-ad-config';

/**
 * MSAL configuration for MsalModule.forRoot().
 * @see ANGULAR_INTERVIEW_GUIDE/10-AZURE-AD-IDENTITY-INTEGRATION.md
 */

const redirectUri = typeof window !== 'undefined'
  ? `${window.location.origin}/callback-azure`
  : 'http://localhost:4200/callback-azure';
const postLogoutUri = typeof window !== 'undefined'
  ? `${window.location.origin}/login`
  : 'http://localhost:4200/login';

/** MSAL Angular 1.x – auth + optional cache */
export const msalConfig = {
  auth: {
    clientId: azureAdConfig.clientId,
    authority: azureAdConfig.authority,
    redirectUri,
    validateAuthority: true
  },
  cache: {
    cacheLocation: 'localStorage' as const,
    storeAuthStateInCookie: false
  }
};

/** Optional guard/config: consent scopes, protected resources */
export const msalGuardConfig = {
  consentScopes: azureAdConfig.scopes,
  unprotectedResources: [] as string[],
  protectedResourceMap: [['https://graph.microsoft.com/v1.0/me', azureAdConfig.scopes]] as [string, string[]][]
};
