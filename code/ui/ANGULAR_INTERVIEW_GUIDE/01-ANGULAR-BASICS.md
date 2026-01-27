# Angular Interview Guide - Part 1: The Basics

## 🎯 What is Angular?

Angular is a **TypeScript-based framework** for building **single-page applications (SPAs)**. It uses:
- **Components** - Building blocks of UI
- **Modules** - Organize code
- **Services** - Share data/logic
- **Dependency Injection** - Manage dependencies

---

## 📁 File Structure Overview

```
src/app/
├── app.component.ts          ← Root component (entry point)
├── app.component.html        ← Root template
├── app.module.ts             ← Root module (configures app)
├── app-routing.module.ts     ← Routing configuration
├── components/               ← Reusable UI components
├── pages/                    ← Page-level components
├── services/                 ← Business logic & API calls
├── models/                   ← TypeScript interfaces/types
├── auth/                     ← Authentication logic
└── shared/                   ← Shared modules/components
```

---

## 1️⃣ COMPONENTS - The Building Blocks

### What is a Component?

A component is a **class + template + styles** that controls a part of the screen.

### Your Project Example: `app.component.ts`

```typescript
import { Component, OnInit, ViewEncapsulation } from '@angular/core';
import { AuthService } from './auth/auth.service';

@Component({
  selector: 'app-root',              // How to use: <app-root></app-root>
  templateUrl: './app.component.html', // HTML template
  styleUrls: ['./app.component.css'], // CSS styles
  encapsulation: ViewEncapsulation.None
})
export class AppComponent implements OnInit {
  
  constructor(private auth: AuthService) {}
  
  ngOnInit() {
    // Lifecycle hook - runs after component is initialized
    this.auth.localAuthSetup();
  }
}
```

### 🔑 Key Concepts:

**1. `@Component` Decorator**
- Tells Angular this class is a component
- `selector`: HTML tag name (`<app-root>`)
- `templateUrl`: Path to HTML file
- `styleUrls`: Array of CSS files

**2. `implements OnInit`**
- Lifecycle hook interface
- `ngOnInit()` runs once after component initialization
- Perfect for initialization logic

**3. `constructor(private auth: AuthService)`**
- **Dependency Injection** - Angular provides AuthService automatically
- `private` = shorthand for `this.auth = auth`

---

## 2️⃣ TEMPLATES - The View

### Your Project Example: `app.component.html`

```html
<div class="d-flex flex-column h-100">
  <!-- Custom component -->
  <app-home-page-header></app-home-page-header>
  
  <!-- Toast notifications container -->
  <div toastContainer></div>
  
  <!-- Router outlet - where routed components appear -->
  <div class="flex-grow-1 flex-shrink-1">
    <router-outlet></router-outlet>
  </div>
  
  <!-- Another custom component -->
  <app-footer></app-footer>
</div>

<!-- Loading spinner component -->
<app-loading></app-loading>
```

### 🔑 Key Concepts:

**1. Component Selectors**
- `<app-home-page-header>` = uses `HomePageHeaderComponent`
- Angular matches selector to component

**2. `<router-outlet>`**
- Placeholder where routed components render
- When you navigate, Angular swaps components here

**3. Data Binding** (we'll cover in detail later)
- `{{ variable }}` - Display data
- `[property]="value"` - Property binding
- `(event)="handler()"` - Event binding

---

## 3️⃣ MODULES - Organizing Your App

### What is a Module?

A module **groups related components, services, and other code** together.

### Your Project Example: `app.module.ts`

```typescript
import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { AppComponent } from './app.component';
import { HomeComponent } from './pages/home/home.component';
// ... more imports

@NgModule({
  declarations: [
    AppComponent,      // Components that belong to this module
    HomeComponent,
    ProfileComponent,
    // ... more components
  ],
  imports: [
    BrowserModule,    // Angular core modules
    AppRoutingModule, // Your routing module
    FormsModule,      // For forms
    HttpClientModule, // For HTTP requests
    // ... third-party modules
  ],
  providers: [
    UserService,      // Services available to this module
    LoadingService,
    // ... more services
  ],
  bootstrap: [AppComponent] // Root component to start app
})
export class AppModule { }
```

### 🔑 Key Concepts:

**1. `declarations`**
- Components, directives, pipes that belong to this module
- Can only be used within this module

**2. `imports`**
- Other modules this module needs
- Import Angular modules (BrowserModule, FormsModule)
- Import your feature modules

**3. `providers`**
- Services available to this module and its children
- Services are **singletons** by default

**4. `bootstrap`**
- Root component that starts the app
- Usually `AppComponent`

---

## 4️⃣ ROUTING - Navigation

### What is Routing?

Routing lets you **navigate between different views** without page reloads.

### Your Project Example: `app-routing.module.ts`

```typescript
import { Routes, RouterModule } from '@angular/router';
import { HomeComponent } from './pages/home/home.component';
import { AuthGuard } from './auth/auth.guard';

const routes: Routes = [
  {
    path: 'dashboard',
    component: HomeComponent,
    canActivate: [ProfileCompletionGuard]  // Route guard
  },
  {
    path: 'itinerary',
    loadChildren: () => import('./itinerary/itinerary.module')
      .then(m => m.ItineraryModule),  // Lazy loading!
    canActivate: [ProfileCompletionGuard]
  },
  {
    path: '',
    redirectTo: '/login',
    pathMatch: 'full'  // Exact match
  },
  {
    path: '**',
    redirectTo: '/login'  // Wildcard - catch all
  }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
```

### 🔑 Key Concepts:

**1. Route Configuration**
- `path`: URL path (`/dashboard`)
- `component`: Component to display
- `redirectTo`: Redirect to another route

**2. Lazy Loading**
```typescript
loadChildren: () => import('./itinerary/itinerary.module')
  .then(m => m.ItineraryModule)
```
- Loads module **only when needed**
- Improves initial load time
- Code splitting!

**3. Route Guards**
- `canActivate`: Check if user can access route
- Returns `true` = allow, `false` = block
- Used for authentication/authorization

**4. Wildcard Route (`**`)**
- Catches all unmatched routes
- Usually redirects to 404 or home

---

## 5️⃣ LAZY LOADING - Performance Optimization

### What is Lazy Loading?

**Lazy loading** is a technique where Angular loads feature modules **on-demand** (when the user navigates to that route) instead of loading everything upfront when the app starts.

### Why Use Lazy Loading?

**Without Lazy Loading (Eager Loading):**
```typescript
// ❌ All modules loaded at startup
import { ItineraryModule } from './itinerary/itinerary.module';
import { PaginationModule } from './pagination/pagination.module';

const routes: Routes = [
  { path: 'itinerary', component: ItineraryComponent },  // Loaded immediately
  { path: 'pagination', component: PaginationComponent } // Loaded immediately
];
```

**Problems:**
- 🐌 **Slow initial load** - All code downloaded at once
- 📦 **Large bundle size** - Everything in main.js
- ⏱️ **Poor Time to Interactive (TTI)** - User waits longer
- 💾 **Wasted bandwidth** - User might never visit some routes

**With Lazy Loading:**
```typescript
// ✅ Modules loaded only when needed
const routes: Routes = [
  {
    path: 'itinerary',
    loadChildren: () => import('./itinerary/itinerary.module')
      .then(m => m.ItineraryModule)  // Loaded when user navigates to /itinerary
  }
];
```

**Benefits:**
- ⚡ **Faster initial load** - Only essential code loaded first
- 📦 **Smaller initial bundle** - Code split into chunks
- 🎯 **Better performance** - Load what you need, when you need it
- 💰 **Reduced bandwidth** - Users only download what they use

---

### How Lazy Loading Works

**Step 1: Configure Route with `loadChildren`**

In `app-routing.module.ts`:

```typescript
const routes: Routes = [
  // Eager loading (loaded immediately)
  {
    path: 'dashboard',
    component: HomeComponent  // Component imported at top
  },
  
  // Lazy loading (loaded on-demand)
  {
    path: 'itinerary',
    loadChildren: () => import('./itinerary/itinerary.module')
      .then(m => m.ItineraryModule)  // Dynamic import
  },
  
  {
    path: 'pagination',
    loadChildren: () =>
      import('./pagination/pagination.module')
        .then(m => m.PaginationModule)
  }
];
```

**Key Points:**
- Use `loadChildren` instead of `component`
- Use **arrow function** with `import()` - this is a dynamic import
- `.then(m => m.ModuleName)` - Returns the module class
- **Don't import** the module at the top of the file!

**Step 2: Create Feature Module with Child Routes**

In `itinerary.module.ts`:

```typescript
import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Routes } from '@angular/router';
import { ItineraryBaseComponent } from './itinerary-base/itinerary-base.component';
import { InfoComponent } from './info/info.component';

// Child routes for this feature module
const routes: Routes = [
  {
    path: '',  // Empty path = default route for /itinerary
    component: ItineraryBaseComponent
  },
  {
    path: 'info',
    component: InfoComponent  // Accessible at /itinerary/info
  }
];

@NgModule({
  declarations: [
    ItineraryBaseComponent,
    InfoComponent
  ],
  imports: [
    CommonModule,
    RouterModule.forChild(routes)  // ⚠️ Use forChild, NOT forRoot!
  ]
})
export class ItineraryModule { }
```

**Critical Difference:**
- `RouterModule.forRoot(routes)` - Used in **root** routing module (app-routing.module.ts)
- `RouterModule.forChild(routes)` - Used in **feature** modules (lazy loaded modules)

---

### Real Example from Your Project

**Your `app-routing.module.ts` has multiple lazy loaded modules:**

```typescript
const routes: Routes = [
  // Eager loaded routes
  {
    path: 'dashboard',
    component: HomeComponent,  // Loaded immediately
    canActivate: [ProfileCompletionGuard]
  },
  
  // Lazy loaded - Itinerary Module
  {
    path: 'itinerary',
    loadChildren: () => import('./itinerary/itinerary.module')
      .then(m => m.ItineraryModule),
    canActivate: [ProfileCompletionGuard]
  },
  
  // Lazy loaded - Pagination Module
  {
    path: 'pagination',
    loadChildren: () =>
      import('./pagination/pagination.module')
        .then(m => m.PaginationModule),
    canActivate: [PaginationGuard]
  },
  
  // Same module, different route
  {
    path: 'pagination-azure',
    loadChildren: () =>
      import('./pagination/pagination.module')
        .then(m => m.PaginationModule),  // Same module, different guard
    canActivate: [AzureAdGuard]
  }
];
```

**What happens when user navigates to `/itinerary`?**

1. User clicks link or types `/itinerary` in browser
2. Angular checks route configuration
3. Sees `loadChildren` - knows it needs to lazy load
4. **Downloads** `itinerary.module.js` chunk (separate file)
5. **Loads** the module
6. **Renders** the component
7. Module is **cached** - won't download again on subsequent visits

---

### Visual Comparison

**Eager Loading Bundle:**
```
main.js (2.5 MB)
├── AppModule
├── HomeComponent
├── ItineraryModule      ← Loaded even if user never visits
├── PaginationModule     ← Loaded even if user never visits
└── All dependencies
```

**Lazy Loading Bundles:**
```
main.js (800 KB)          ← Smaller initial bundle
├── AppModule
├── HomeComponent
└── Core dependencies

itinerary.module.js (900 KB)  ← Loaded only when needed
└── ItineraryModule + components

pagination.module.js (800 KB)  ← Loaded only when needed
└── PaginationModule + components
```

---

### Best Practices

**1. Use Lazy Loading for Feature Modules**
```typescript
// ✅ Good - Feature modules should be lazy loaded
{
  path: 'admin',
  loadChildren: () => import('./admin/admin.module').then(m => m.AdminModule)
}

// ❌ Bad - Don't lazy load tiny modules
{
  path: 'about',
  component: AboutComponent  // Small component, eager load is fine
}
```

**2. Don't Import Lazy Modules in AppModule**
```typescript
// ❌ BAD - This defeats lazy loading!
import { ItineraryModule } from './itinerary/itinerary.module';

@NgModule({
  imports: [ItineraryModule]  // Now it's eager loaded!
})
export class AppModule { }
```

**3. Use `forChild` in Feature Modules**
```typescript
// ✅ Correct
@NgModule({
  imports: [RouterModule.forChild(routes)]  // forChild for feature modules
})
export class ItineraryModule { }
```

**4. Preloading Strategy (Optional)**
```typescript
// Preload all lazy modules in background after initial load
RouterModule.forRoot(routes, {
  preloadingStrategy: PreloadAllModules
})
```

---

### Common Mistakes

**❌ Mistake 1: Importing the module**
```typescript
// ❌ WRONG - This makes it eager loaded
import { ItineraryModule } from './itinerary/itinerary.module';

const routes: Routes = [
  {
    path: 'itinerary',
    loadChildren: () => ItineraryModule  // Still eager loaded!
  }
];
```

**✅ Correct:**
```typescript
// ✅ CORRECT - No import, use dynamic import
const routes: Routes = [
  {
    path: 'itinerary',
    loadChildren: () => import('./itinerary/itinerary.module')
      .then(m => m.ItineraryModule)
  }
];
```

**❌ Mistake 2: Using `forRoot` in feature module**
```typescript
// ❌ WRONG
@NgModule({
  imports: [RouterModule.forRoot(routes)]  // Only one forRoot allowed!
})
export class ItineraryModule { }
```

**✅ Correct:**
```typescript
// ✅ CORRECT
@NgModule({
  imports: [RouterModule.forChild(routes)]  // Use forChild
})
export class ItineraryModule { }
```

---

### How to Verify Lazy Loading Works

**1. Check Network Tab in Browser DevTools:**
- Open DevTools → Network tab
- Navigate to lazy loaded route
- You should see a new `.js` file downloaded (e.g., `itinerary-itinerary-module.js`)

**2. Check Bundle Analysis:**
```bash
ng build --stats-json
npx webpack-bundle-analyzer dist/stats.json
```

You should see separate chunks for each lazy loaded module.

---

## 📝 Interview Questions & Answers

### Q1: What is the difference between a Component and a Module?

**Answer:**
- **Component**: Controls a piece of UI (view + logic)
- **Module**: Groups components, services, and other code together
- **Analogy**: Component = room, Module = building

### Q2: What is lazy loading and why use it?

**Answer:**
- Loads modules **on-demand** (when user navigates to route) instead of loading everything at startup
- **Benefits**:
  - ⚡ **Faster initial load** - Only essential code loaded first
  - 📦 **Smaller initial bundle** - Code split into separate chunks
  - 🎯 **Better performance** - Load what you need, when you need it
  - 💰 **Reduced bandwidth** - Users only download what they use
- **How it works**:
  ```typescript
  {
    path: 'itinerary',
    loadChildren: () => import('./itinerary/itinerary.module')
      .then(m => m.ItineraryModule)  // Dynamic import
  }
  ```
- **Key points**:
  - Use `loadChildren` instead of `component` in route config
  - Use `RouterModule.forChild()` in feature module (not `forRoot`)
  - Don't import the module at the top of routing file
- **Example**: When user navigates to `/itinerary`, Angular downloads `itinerary.module.js` chunk and loads it on-demand
- See [Lazy Loading section](#5️⃣-lazy-loading---performance-optimization) above for detailed explanation and examples

### Q3: What is `ngOnInit` and when is it called?

**Answer:**
- Lifecycle hook that runs **once** after component initialization
- Use for: API calls, setup logic, subscriptions
- Called **after** constructor and **after** first change detection

### Q4: What is Dependency Injection?

**Answer:**
- Angular **automatically provides** dependencies to components/services
- Instead of `new Service()`, you inject via constructor
- **Benefits**: Testable, maintainable, flexible

```typescript
// ❌ Bad - manual creation
constructor() {
  this.auth = new AuthService();
}

// ✅ Good - dependency injection
constructor(private auth: AuthService) {}
```

---

## 🎯 Practice Exercise

Look at your `home.component.ts`:

```typescript
export class HomeComponent implements OnInit {
  constructor(public auth: AuthService) {}
  ngOnInit() {}
}
```

**Questions to think about:**
1. Why does it implement `OnInit`?
2. What's the difference between `private` and `public` in constructor?
3. Where would this component be displayed? (Check routing)

---

## ✅ Key Takeaways

1. **Components** = UI building blocks (class + template + styles)
2. **Modules** = Organize related code
3. **Routing** = Navigate between views
4. **Dependency Injection** = Angular provides dependencies automatically
5. **Lifecycle Hooks** = `ngOnInit` runs after initialization

---

**Next**: [Part 2 - Data Binding & Directives](./02-DATA-BINDING-DIRECTIVES.md)
