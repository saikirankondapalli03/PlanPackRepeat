# Angular Interview Guide - Part 10: Azure AD / Identity Integration

## 🎯 Overview

Many Angular apps integrate with **Azure AD** (Microsoft Entra ID) for single sign-on (SSO), work/school accounts, and securing APIs. This guide covers concepts and patterns used in this project’s Azure AD integration.

---

## 1️⃣ Why Azure AD?

- **Single sign-on (SSO)** for work/school accounts
- **OAuth 2.0 / OpenID Connect** – industry-standard flows
- **Microsoft Graph** and other Microsoft APIs
- **Centralized identity** – one directory for users and permissions

---

## 2️⃣ MSAL (Microsoft Authentication Library)

Angular apps typically use **@azure/msal-angular** (and **msal** or **@azure/msal-browser**):

- **MSAL Angular 1.x**: `msal` + `@azure/msal-angular` – Angular 6–9
- **MSAL Angular 2.x**: `@azure/msal-browser` + `@azure/msal-angular` – Angular 10+

This project uses **MSAL Angular 1.x** (Angular 8).

---

## 3️⃣ Configuration

### Azure AD app registration

1. **Azure Portal** → **App registrations** → **New registration**
2. Set **Redirect URI** (e.g. `http://localhost:4200/callback-azure`)
3. Copy **Application (client) ID** and optionally **Directory (tenant) ID**

### Config in code

**`models/azure-ad-config.ts`** – Central config:

```typescript
export const azureAdConfig = {
  clientId: 'YOUR_AZURE_AD_CLIENT_ID',
  tenantId: 'common',
  authority: 'https://login.microsoftonline.com/common',
  redirectUri: `${window.location.origin}/callback-azure`,
  postLogoutRedirectUri: `${window.location.origin}/login`,
  scopes: ['user.read']
};
```

**`auth/azure-ad/msal-config.ts`** – MSAL module config:

```typescript
export const msalConfig = {
  auth: {
    clientId: azureAdConfig.clientId,
    authority: azureAdConfig.authority,
    redirectUri: '...',
    validateAuthority: true
  },
  cache: { cacheLocation: 'localStorage', storeAuthStateInCookie: false }
};

export const msalGuardConfig = {
  consentScopes: azureAdConfig.scopes,
  unprotectedResources: [],
  protectedResourceMap: [['https://graph.microsoft.com/v1.0/me', ['user.read']]]
};
```

**Interview tip:** Redirect URI must match exactly what is registered in Azure. Use `window.location.origin` for port/origin flexibility.

---

## 4️⃣ Auth service (wrapper around MSAL)

**`auth/azure-ad/azure-ad-auth.service.ts`** – Wraps `MsalService`:

- **login(targetPath)** – Store redirect target, call `loginRedirect()`, user goes to Azure AD
- **logout()** – Sign out, clear local state
- **handleRedirectCallback()** – Process redirect after login (MSAL 1.x / 2.x compatible)
- **isAuthenticated$**, **user$** – Observables for auth state

### Concepts used

- **Part 3 – Services & DI**: Injectable service, `providedIn: 'root'`
- **Part 4 – Observables**: `BehaviorSubject`, `isAuthenticated$`, `user$`, subscribe in components
- **Part 6 – Guards**: Guard uses this service to check auth

```typescript
// Login: store target, redirect to Azure AD
login(targetPath: string = '/pagination-azure'): void {
  sessionStorage.setItem('azureAdRedirectTarget', targetPath);
  this.msal.loginRedirect({ scopes: azureAdConfig.scopes });
}

// Callback: handle redirect, then navigate to target
handleRedirectCallback(): Observable<boolean> { ... }
navigateToRedirectTarget(): void {
  const target = sessionStorage.getItem('azureAdRedirectTarget') || '/pagination-azure';
  sessionStorage.removeItem('azureAdRedirectTarget');
  this.router.navigateByUrl(target);
}
```

---

## 5️⃣ Route guard (CanActivate)

**`auth/azure-ad/azure-ad.guard.ts`** – Protects routes that require Azure AD sign-in:

```typescript
@Injectable({ providedIn: 'root' })
export class AzureAdGuard implements CanActivate {
  constructor(private azureAd: AzureAdAuthService, private router: Router) {}

  canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<boolean> {
    return this.azureAd.isAuthenticated$.pipe(
      take(1),
      map((loggedIn) => {
        if (loggedIn) return true;
        this.azureAd.login(state.url);  // Redirect to Azure AD, store target
        return false;
      }),
      catchError(() => {
        this.azureAd.login(state.url);
        return of(false);
      })
    );
  }
}
```

- If **logged in** → allow route.
- If **not** → call `login(state.url)` (redirect to Azure AD, store `state.url`), return `false`.

**Interview tip:** Guard returns `Observable<boolean>`. You can also return `UrlTree` to redirect without using `router.navigateByUrl` in the guard.

---

## 6️⃣ Redirect callback (callback route)

**`pages/callback-azure/callback-azure.component.ts`** – Handles return from Azure AD:

1. **Redirect URI** points to `/callback-azure`.
2. Azure AD redirects back with tokens in URL (hash or query).
3. Component calls `handleRedirectCallback()` → MSAL processes tokens, updates cache.
4. Then `navigateToRedirectTarget()` → go to stored route (e.g. `/pagination-azure`).

```typescript
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
```

**Interview tip:** Use `finalize` so loading is disabled even on error.

---

## 7️⃣ OAuth 2.0 / OpenID Connect flow (redirect)

1. User hits protected route → **Guard** runs → not logged in → **login(returnUrl)**.
2. App stores **returnUrl** (e.g. in `sessionStorage`), calls **loginRedirect()**.
3. User is **redirected** to Azure AD → signs in (and consents if needed).
4. Azure AD **redirects** back to **redirect URI** (`/callback-azure`) with tokens.
5. **Callback component** runs `handleRedirectCallback()` → MSAL parses tokens, caches them.
6. App **navigates** to **returnUrl** (e.g. `/pagination-azure`).
7. Guard runs again → **logged in** → route activates.

---

## 8️⃣ Module setup (MsalModule)

**`app.module.ts`**:

```typescript
import { MsalModule } from '@azure/msal-angular';
import { msalConfig, msalGuardConfig } from './auth/azure-ad/msal-config';

@NgModule({
  imports: [
    // ...
    MsalModule.forRoot(msalConfig, msalGuardConfig)
  ]
})
export class AppModule {}
```

- **MsalModule** provides `MsalService` (and optionally `MsalGuard`, `MsalInterceptor`).
- **Auth service** injects `MsalService` and wraps it.
- **Custom guard** uses the auth service; you can use `MsalGuard` instead if you prefer.

---

## 9️⃣ HTTP interceptor (attach tokens)

To send **access tokens** to APIs (e.g. Microsoft Graph, your backend):

- Use **MsalInterceptor** (from `@azure/msal-angular`) and register it as `HTTP_INTERCEPTORS`.
- Configure **protectedResourceMap** (e.g. in `msalGuardConfig`) so MSAL knows which URLs need which scopes.

**Concept (Part 8 – Interceptors):** Interceptors run for every HTTP request. They can add `Authorization: Bearer <token>` and handle 401s (e.g. trigger login).

```typescript
// Example: add MsalInterceptor
{ provide: HTTP_INTERCEPTORS, useClass: MsalInterceptor, multi: true }
```

---

## 🔟 Routes in this project

| Route | Purpose |
|-------|---------|
| `/callback-azure` | Azure AD redirect callback; no guard |
| `/pagination-azure` | Pagination demo; `canActivate: [AzureAdGuard]` |

- **Login page** has a “Pagination with Azure AD” link → `/pagination-azure`.
- Hitting `/pagination-azure` when not logged in → guard → Azure AD login → callback → back to pagination.

---

## 📝 Interview questions & answers

### Q1: What is MSAL and why use it with Angular?

**Answer:** MSAL (Microsoft Authentication Library) handles **Azure AD** (and MSA) sign-in in JavaScript/TypeScript. **@azure/msal-angular** integrates it with Angular (services, guards, interceptors). Use it for SSO, work/school accounts, and calling Microsoft APIs.

### Q2: What is the redirect flow vs popup flow?

**Answer:**

- **Redirect:** User is sent to Azure AD, signs in, then **redirected back** to your app. No popup; entire app navigates. Good for mobile and when popups are blocked.
- **Popup:** Sign-in happens in a **popup**; main app stays open. Better UX when you don’t want to lose app state, but popups can be blocked.

### Q3: Why use a callback route (e.g. `/callback-azure`)?

**Answer:** Azure AD redirects to a **redirect URI** after login. That URI must be a route in your app. The **callback component** runs there, calls `handleRedirectCallback()` so MSAL can process tokens, then navigates the user to the intended route (e.g. dashboard or `/pagination-azure`).

### Q4: How do you protect routes with Azure AD?

**Answer:** Use a **CanActivate guard** that checks auth (e.g. via `AzureAdAuthService.isAuthenticated$`). If not logged in, trigger login (e.g. `loginRedirect`) and return `false`. Alternatively, use **MsalGuard** from `@azure/msal-angular` for MSAL-backed protection.

### Q5: How do you attach Azure AD tokens to HTTP requests?

**Answer:** Use an **HTTP interceptor** (e.g. **MsalInterceptor**). It checks `protectedResourceMap`, acquires tokens for matching URLs, and adds `Authorization: Bearer <token>`. Register it in `HTTP_INTERCEPTORS`.

### Q6: What is `protectedResourceMap`?

**Answer:** A map of **API base URLs** → **scopes** (e.g. `['https://graph.microsoft.com/v1.0/me', ['user.read']]`). MSAL uses it to decide when to acquire tokens and which scopes to request for each API.

---

## ✅ Key takeaways

1. **Config** – `clientId`, `authority`, `redirectUri` (and optionally tenant) in Azure and in code.
2. **Auth service** – Wraps MSAL; exposes `login`, `logout`, `handleRedirectCallback`, `isAuthenticated$`, `user$`.
3. **Guard** – `CanActivate`; uses auth service; redirects to login when not authenticated.
4. **Callback route** – Handles redirect from Azure AD; runs `handleRedirectCallback`, then navigates to stored target.
5. **MsalModule** – `forRoot(msalConfig, msalGuardConfig)`; provides `MsalService` (and optionally guard/interceptor).
6. **Interceptors** – Attach tokens to API requests (e.g. MsalInterceptor + `protectedResourceMap`).

---

## 📁 Project layout (Azure AD)

```
src/app/
├── models/
│   └── azure-ad-config.ts        # clientId, authority, scopes, etc.
├── auth/
│   └── azure-ad/
│       ├── msal-config.ts        # MsalModule config
│       ├── azure-ad-auth.service.ts
│       └── azure-ad.guard.ts
└── pages/
    └── callback-azure/           # Redirect callback component
```

---

**Next:** Review [Part 6 – Routing & Guards](./06-ROUTING-GUARDS.md) for guards, and [Part 8 – Advanced](./08-ADVANCED-TOPICS.md) for interceptors.
