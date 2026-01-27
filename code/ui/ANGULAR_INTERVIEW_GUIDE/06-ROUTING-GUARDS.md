# Angular Interview Guide - Part 6: Routing & Guards

## 🎯 Overview

Angular Router controls **navigation** and **route activation**. **Route guards** run before (or after) navigation to allow or block access (e.g. auth, profile completion). This guide covers route configuration, navigation, params, guards, and lazy loading — with examples from your project.

---

## 1️⃣ ROUTE CONFIGURATION

### Basic Routes

Routes are defined as an array of **route config objects**. Each has `path`, and usually `component` or `redirectTo` / `loadChildren`.

```typescript
const routes: Routes = [
  { path: '', redirectTo: '/login', pathMatch: 'full' },
  { path: 'login', component: LoginComponent },
  { path: 'profile', component: ProfileComponent },
  { path: '**', redirectTo: '/login', pathMatch: 'full' }
];
```

**Key properties:**

| Property | Purpose |
|----------|---------|
| `path` | URL segment (e.g. `'login'`, `'profile'`) |
| `component` | Component to load for that route |
| `redirectTo` | Redirect to another path |
| `pathMatch` | `'full'` = match entire URL; `'prefix'` = match prefix |
| `loadChildren` | Lazy-load a feature module |
| `canActivate` | Array of guards that must pass before activating |

### Your Project: `app-routing.module.ts`

```typescript
const routes: Routes = [
  { path: 'callback', component: CallbackComponent },
  { path: 'callback-azure', component: CallbackAzureComponent },
  { path: 'profile', component: ProfileComponent },
  {
    path: 'dashboard',
    component: HomeComponent,
    canActivate: [ProfileCompletionGuard]
  },
  {
    path: 'itinerary',
    loadChildren: () => import('./itinerary/itinerary.module').then(m => m.ItineraryModule),
    canActivate: [ProfileCompletionGuard]
  },
  { path: 'login', component: LoginComponent },
  {
    path: 'pagination-azure',
    loadChildren: () => import('./pagination/pagination.module').then(m => m.PaginationModule),
    canActivate: [AzureAdGuard]
  },
  { path: '', redirectTo: '/login', pathMatch: 'full' },
  { path: '**', redirectTo: '/login', pathMatch: 'full' }
];
```

**Interview tip:** `**` is a wildcard; it matches any URL. Use it last for a “404” redirect.

---

## 2️⃣ NAVIGATION

### Router service

Inject `Router` to navigate programmatically:

```typescript
constructor(private router: Router) {}

goToProfile() {
  this.router.navigate(['/profile']);
}

goWithParams() {
  this.router.navigate(['/user', userId]);
}
```

### `navigate` vs `navigateByUrl`

| Method | Accepts | Example |
|--------|---------|---------|
| `router.navigate(['/profile'])` | **Array** of segments (link params) | `navigate(['/user', id, 'edit'])` |
| `router.navigateByUrl('/profile')` | **String** URL | `navigateByUrl(state.url)` |

Use `navigate` when building paths from segments; use `navigateByUrl` when you have a full URL (e.g. from `RouterStateSnapshot` in a guard).

### Your project: redirecting in guards

```typescript
// AuthGuard, ProfileCompletionGuard
this.router.navigateByUrl('/login');   // or '/profile'
```

```typescript
// AzureAdGuard — redirect back after login
this.azureAd.login(state.url);  // stores URL, redirects to IdP, then back
```

---

## 3️⃣ ROUTE PARAMETERS & QUERY PARAMS

### Route params (e.g. `/user/42`)

**Config:**

```typescript
{ path: 'user/:id', component: UserDetailComponent }
```

**Reading in component:**

```typescript
constructor(
  private route: ActivatedRoute
) {}

ngOnInit() {
  this.route.params.subscribe(params => {
    const id = params['id'];
  });
  // Or snapshot (no subscription):
  const id = this.route.snapshot.paramMap.get('id');
}
```

### Query params (e.g. `/search?q=angular`)

```typescript
this.route.queryParams.subscribe(params => {
  const q = params['q'];
});
// Or:
const q = this.route.snapshot.queryParamMap.get('q');
```

### Navigating with query params

```typescript
this.router.navigate(['/search'], { queryParams: { q: 'angular', page: 1 } });
```

---

## 4️⃣ ROUTE GUARDS — Overview

Guards determine whether a route can be **activated**, **deactivated**, or **loaded**. They implement interfaces like `CanActivate`, `CanDeactivate`, etc.

| Guard | When it runs | Typical use |
|-------|--------------|-------------|
| **CanActivate** | Before activating a route | Auth, profile completion |
| **CanActivateChild** | Before activating a **child** route | Auth for nested routes |
| **CanDeactivate** | Before **leaving** a route | “Unsaved changes?” |
| **CanLoad** | Before **lazy-loaded** module is fetched | Auth before loading code |
| **Resolve** | Before activation; fetches data | Pre-load detail data |

**Return type:** `boolean | UrlTree | Observable<boolean | UrlTree> | Promise<boolean | UrlTree>`.

- `true` → allow navigation.
- `false` → block (often redirect in guard).
- `UrlTree` → redirect to that route (alternative to `router.navigate`).

---

## 5️⃣ CanActivate — Auth & Access Control

### Simple boolean guard

```typescript
@Injectable({ providedIn: 'root' })
export class AuthGuard implements CanActivate {
  constructor(private router: Router, private auth: AuthService) {}

  canActivate(
    route: ActivatedRouteSnapshot,
    state: RouterStateSnapshot
  ): Observable<boolean> | Promise<boolean | UrlTree> | boolean {
    return this.auth.isAuthenticated$.pipe(
      tap(loggedIn => {
        if (!loggedIn) {
          this.router.navigateByUrl('/login');
        }
      })
    );
  }
}
```

- `ActivatedRouteSnapshot`: route being activated (params, data, etc.).
- `RouterStateSnapshot`: full router state; `state.url` is the requested URL.

### Your project: `AuthGuard`

```typescript
// auth/auth.guard.ts
return this.auth.isAuthenticated$.pipe(
  tap(loggedIn => {
    if (!loggedIn) {
      this.router.navigateByUrl('/login');
    }
  })
);
```

Uses `isAuthenticated$`; redirects to `/login` if not authenticated.

---

## 6️⃣ CanActivate + Async Logic (Profile Completion)

When you need **async** checks (e.g. API call), return `Observable<boolean>` or `Promise<boolean>`.

### Your project: `ProfileCompletionGuard`

```typescript
// route-guard/profile-completion.guard.ts
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
    catchError(() => {
      this.navigateToProfilePage();
      return of(false);
    })
  );
}

private navigateToProfilePage(): void {
  this.router.navigateByUrl('/profile');
}
```

**Interview takeaway:** Sync check first (e.g. `userEmail`); if you need API, return an `Observable` and use `map` / `catchError`. Always handle errors (e.g. redirect, `of(false)`).

---

## 7️⃣ CanActivate + Azure AD (`AzureAdGuard`)

### Your project: `AzureAdGuard`

```typescript
// auth/azure-ad/azure-ad.guard.ts
canActivate(
  route: ActivatedRouteSnapshot,
  state: RouterStateSnapshot
): Observable<boolean> {
  return this.azureAd.isAuthenticated$.pipe(
    take(1),
    map((loggedIn) => {
      if (loggedIn) return true;
      this.azureAd.login(state.url);  // redirect to IdP, then back to state.url
      return false;
    }),
    catchError(() => {
      this.azureAd.login(state.url);
      return of(false);
    })
  );
}
```

**Why `state.url`?** So after Azure AD login, the user is sent back to the **requested** URL (e.g. `/pagination-azure`), not a default page.

---

## 8️⃣ CanActivate — Minimal “Demo” Guard

### Your project: `PaginationGuard`

```typescript
// pagination/pagination.guard.ts
@Injectable({ providedIn: 'root' })
export class PaginationGuard implements CanActivate {
  canActivate(
    _route: ActivatedRouteSnapshot,
    _state: RouterStateSnapshot
  ): boolean {
    return true;
  }
}
```

Simple example of a guard that always allows access. Real guards would check auth/permissions.

---

## 9️⃣ CanDeactivate — “Unsaved Changes?”

**CanDeactivate** runs when the user **navigates away** from a route. Use it to ask “You have unsaved changes. Leave?”

```typescript
@Injectable({ providedIn: 'root' })
export class UnsavedChangesGuard implements CanDeactivate<MyFormComponent> {
  canDeactivate(component: MyFormComponent): boolean {
    if (component.myForm.dirty && !component.saved) {
      return window.confirm('Discard unsaved changes?');
    }
    return true;
  }
}
```

**Config:**

```typescript
{
  path: 'edit',
  component: MyFormComponent,
  canDeactivate: [UnsavedChangesGuard]
}
```

Your project doesn’t use `CanDeactivate`, but it’s commonly asked in interviews.

---

## 🔟 CanLoad — Guard Lazy-Loaded Modules

**CanLoad** runs **before** a lazy-loaded module is fetched. Use it to avoid loading code for routes the user isn’t allowed to access.

```typescript
@Injectable({ providedIn: 'root' })
export class AuthCanLoadGuard implements CanLoad {
  constructor(private auth: AuthService, private router: Router) {}

  canLoad(route: Route): boolean {
    if (this.auth.isLoggedIn()) return true;
    this.router.navigateByUrl('/login');
    return false;
  }
}
```

**Config:**

```typescript
{
  path: 'admin',
  loadChildren: () => import('./admin/admin.module').then(m => m.AdminModule),
  canLoad: [AuthCanLoadGuard]
}
```

**CanActivate vs CanLoad:** Both can protect lazy routes. `CanLoad` blocks the **module chunk** from loading; `CanActivate` runs when the route activates (chunk may already be loaded).

---

## 1️⃣1️⃣ Resolve — Pre-loading Data

A **Resolver** fetches data **before** the route activates. The component can read it from `ActivatedRoute.data`.

```typescript
@Injectable({ providedIn: 'root' })
export class UserResolver implements Resolve<User> {
  constructor(private userService: UserService) {}

  resolve(route: ActivatedRouteSnapshot): Observable<User> {
    const id = route.paramMap.get('id');
    return this.userService.getUser(id);
  }
}
```

**Config:**

```typescript
{
  path: 'user/:id',
  component: UserDetailComponent,
  resolve: { user: UserResolver }
}
```

**In component:**

```typescript
this.route.data.subscribe(data => {
  this.user = data['user'];
});
```

Your project doesn’t use resolvers, but they’re good to know for “load data before showing page” scenarios.

---

## 1️⃣2️⃣ LAZY LOADING

Lazy loading loads feature modules **only when** their routes are visited, which reduces initial bundle size.

### Syntax

```typescript
{
  path: 'itinerary',
  loadChildren: () =>
    import('./itinerary/itinerary.module').then(m => m.ItineraryModule)
}
```

The function returns a **Promise** that resolves to the module. The routing config expects a module with a **routing module** that defines `path: ''` (or other child paths) under `itinerary`.

### Your project

```typescript
{
  path: 'itinerary',
  loadChildren: () => import('./itinerary/itinerary.module').then(m => m.ItineraryModule),
  canActivate: [ProfileCompletionGuard]
},
{
  path: 'pagination-azure',
  loadChildren: () => import('./pagination/pagination.module').then(m => m.PaginationModule),
  canActivate: [AzureAdGuard]
}
```

**Interview tip:** Use `loadChildren` + `import()` for lazy loading; **never** add the feature module to `AppModule`’s `imports` if it’s lazy-loaded.

---

## 1️⃣3️⃣ APPLYING GUARDS IN YOUR PROJECT

| Route | Guard(s) | Purpose |
|-------|----------|---------|
| `/dashboard` | `ProfileCompletionGuard` | Ensure profile complete before dashboard |
| `/itinerary`, `/create-itinerary` | `ProfileCompletionGuard` | Same for itinerary flows |
| `/pagination` | `PaginationGuard` | Demo guard (always allows) |
| `/pagination-azure` | `AzureAdGuard` | Require Azure AD sign-in |
| `/profile`, `/login`, callbacks | None | No guard |

---

## 1️⃣4️⃣ REGISTERING THE ROUTER & GUARDS

### Router

```typescript
// app-routing.module.ts
@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule {}
```

### Guards

Guards are **injectable services**. Yours use `providedIn: 'root'`, so no need to add them to any module. You only reference them in `canActivate` / `canDeactivate` / `canLoad` in the route config.

---

## 📌 QUICK REFERENCE

- **Routes:** `path`, `component`, `redirectTo`, `loadChildren`, `canActivate`, `canDeactivate`, `canLoad`, `resolve`.
- **Navigate:** `router.navigate(['/path'])`, `router.navigateByUrl('/path')`.
- **Params:** `route.params`, `route.snapshot.paramMap`, `route.queryParams`.
- **Guards:** `CanActivate`, `CanDeactivate`, `CanLoad`, `Resolve`; return `boolean | UrlTree | Observable<...> | Promise<...>`.
- **Lazy loading:** `loadChildren: () => import('...').then(m => m.XModule)`.

---

## ❓ INTERVIEW QUESTIONS

1. **What is a route guard? When would you use one?**  
   A guard decides if a route can be activated, left, or loaded. Use for auth, profile completion, “unsaved changes” checks, etc.

2. **What’s the difference between `CanActivate` and `CanLoad`?**  
   `CanActivate` runs before activating a route; `CanLoad` runs before loading a lazy module. `CanLoad` can prevent the module chunk from being downloaded.

3. **How do you redirect from a guard?**  
   Either `this.router.navigateByUrl('/login')` (or `navigate`) and return `false`, or return a `UrlTree` (e.g. `this.router.parseUrl('/login')`).

4. **Can a guard return an `Observable`?**  
   Yes. `canActivate` can return `Observable<boolean | UrlTree>`. The router waits for it to complete before allowing or blocking navigation.

5. **What is lazy loading? How do you implement it?**  
   Loading a feature module only when its route is visited. Use `loadChildren: () => import('./feature/feature.module').then(m => m.FeatureModule)` and define child routes in the feature’s routing module.

6. **What’s the difference between `navigate` and `navigateByUrl`?**  
   `navigate` takes a **link params array** (e.g. `['/user', id]`); `navigateByUrl` takes a **full URL string**.

---

## ✅ CHECKLIST

- [ ] Explain route config: `path`, `component`, `redirectTo`, `loadChildren`, `canActivate`.
- [ ] Use `Router` for `navigate` / `navigateByUrl`.
- [ ] Read `route.params`, `route.queryParams`, or `snapshot.paramMap` / `queryParamMap`.
- [ ] Implement `CanActivate`: sync vs async (e.g. `Observable`), redirect, error handling.
- [ ] Describe `CanDeactivate` (“unsaved changes”) and `CanLoad` (lazy modules).
- [ ] Describe lazy loading with `loadChildren` and `import()`.
- [ ] Point to `AuthGuard`, `ProfileCompletionGuard`, `AzureAdGuard`, `PaginationGuard`, and `app-routing.module` in your project.
