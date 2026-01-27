# Pagination Demo – Angular Interview Prep

A simple pagination app with **static data**, **4 columns** (ID, Name, Category, Status), and an **Apply** button per row. Built to cover concepts from the [ANGULAR_INTERVIEW_GUIDE](../ANGULAR_INTERVIEW_GUIDE).

## How to run

1. From project root: `npm install` then `npm start`
2. Open **http://localhost:4200/pagination** (or use the link on the login page)
3. **Azure AD:** Use **Pagination with Azure AD** → `/pagination-azure`. Requires Azure AD app registration; see [10-AZURE-AD-IDENTITY-INTEGRATION](../ANGULAR_INTERVIEW_GUIDE/10-AZURE-AD-IDENTITY-INTEGRATION.md).

## Features

- **Static data**: 15 items in `PaginationService`
- **4 columns**: ID, Name, Category, Status + **Action** (Apply button per row)
- **Pagination**: Previous/Next, page numbers, configurable rows-per-page (1–20)
- **Apply**: Clicking Apply stores the row; “Last applied” message and row highlight

---

## Concept mapping → Interview guide

| Guide part | Concept | Where in this app |
|------------|---------|-------------------|
| **01 Basics** | Component, Module, DI, Routing | `PaginationComponent`, `PaginationModule`, `PaginationService` in constructor, `/pagination` route, lazy-loaded module |
| **02 Data binding** | Interpolation `{{ }}`, property `[prop]`, event `(event)`, `*ngIf`, `*ngFor`, `[ngClass]`, `trackBy` | Table cells `{{ row.name }}`, `[formControl]`, `(click)="applyItem(row)"`, `*ngIf="lastApplied"`, `*ngFor` with `trackById`, `[ngClass]` on rows/pagination |
| **03 Services & DI** | Service, `providedIn: 'root'`, inject in component | `PaginationService`, singleton, injected in `PaginationComponent` |
| **04 HTTP & Observables** | Observables, BehaviorSubject, subscribe, unsubscribe | `lastApplied$` (BehaviorSubject), subscribe in component, unsubscribe in `ngOnDestroy` |
| **05 Forms** | Reactive Forms, FormControl, Validators | `pageSizeControl` (FormControl, min/max/required), `valueChanges` |
| **06 Routing & Guards** | Routes, lazy loading, CanActivate | `PaginationRoutingModule`, `loadChildren` in app routing, `PaginationGuard` |
| **07 Lifecycle** | `ngOnInit`, `ngOnDestroy`, `ngAfterViewInit` | Init/subscribe in `ngOnInit`, cleanup in `ngOnDestroy`, `ViewChild` usage in `ngAfterViewInit` |
| **08 Advanced** | Custom pipe, ViewChild, ChangeDetectionStrategy, ChangeDetectorRef | `StatusBadgePipe`, `@ViewChild('tableContainer')`, `OnPush`, `markForCheck()` |
| **09 Material / CSS** | Minimal styling | Component CSS + Bootstrap (table, badges, buttons). Guide uses Material; here we keep it simple. |
| **10 Azure AD** | MSAL, guards, callback | `/pagination-azure` uses `AzureAdGuard`; login via Azure AD, callback at `/callback-azure`. |

---

## File structure

```
src/app/pagination/
├── models/
│   └── pagination-item.interface.ts   # Row model
├── pipes/
│   └── status-badge.pipe.ts           # Custom pipe (status → badge class)
├── pagination.component.ts            # Main component
├── pagination.component.html
├── pagination.component.css
├── pagination.service.ts              # Static data, pager logic, BehaviorSubject
├── pagination.guard.ts                # CanActivate guard (demo)
├── pagination.module.ts
└── pagination-routing.module.ts
```

---

## Quick interview talking points

1. **Component vs Module**: Component = UI (template + class + styles); Module = group of components, pipes, imports.
2. **DI**: `PaginationService` is injected in the component constructor; Angular provides it.
3. **Four bindings**: `{{ }}` interpolation, `[prop]` property, `(event)` event, `[(ngModel)]` two-way (we use FormControl instead).
4. **Observables**: `lastApplied$` from BehaviorSubject; we subscribe in the component and unsubscribe in `ngOnDestroy`.
5. **Reactive Forms**: `FormControl` for page size with validators; we react via `valueChanges`.
6. **Guards**: `PaginationGuard` implements `CanActivate`; runs before route activation.
7. **Lifecycle**: Setup in `ngOnInit`, cleanup in `ngOnDestroy`, `ViewChild` ready in `ngAfterViewInit`.
8. **Custom pipe**: `StatusBadgePipe` transforms status string → CSS class for badges.
9. **OnPush**: Component uses `ChangeDetectionStrategy.OnPush`; we call `markForCheck()` when updating from subscriptions.

Good luck with your interview.
