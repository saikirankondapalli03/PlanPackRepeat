/**
 * Azure AD / Microsoft Entra ID configuration for MSAL.
 * Register an app in Azure Portal > App registrations to get clientId and tenantId.
 * @see ANGULAR_INTERVIEW_GUIDE/10-AZURE-AD-IDENTITY-INTEGRATION.md
 */
export const azureAdConfig = {
  /** Application (client) ID from Azure App registration */
  clientId: 'YOUR_AZURE_AD_CLIENT_ID',
  /** Directory (tenant) ID, or 'common' for multi-tenant */
  tenantId: 'common',
  /** Authority URL; use 'common' for any Azure AD / MSA */
  authority: 'https://login.microsoftonline.com/common',
  /** Redirect URI after sign-in; must match Azure app registration */
  redirectUri: typeof window !== 'undefined' ? `${window.location.origin}/callback-azure` : 'http://localhost:4200/callback-azure',
  /** Where to send user after logout */
  postLogoutRedirectUri: typeof window !== 'undefined' ? `${window.location.origin}/login` : 'http://localhost:4200/login',
  /** Scopes to request (e.g. User.Read for profile) */
  scopes: ['user.read']
};
