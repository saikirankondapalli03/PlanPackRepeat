# Angular Interview Guide - Part 8: Advanced Topics

## 🎯 Overview

Advanced Angular concepts that often come up in senior-level interviews.

---

## 1️⃣ CHANGE DETECTION

### What is Change Detection?

Angular checks if data has changed and updates the DOM accordingly.

### Change Detection Strategies:

#### A. Default Strategy (CheckAlways)

```typescript
@Component({
  selector: 'app-default',
  // changeDetection: ChangeDetectionStrategy.Default  ← Default
})
export class DefaultComponent {
  // Angular checks this component on every change detection cycle
}
```

#### B. OnPush Strategy (Performance Optimization)

```typescript
import { ChangeDetectionStrategy } from '@angular/core';

@Component({
  selector: 'app-onpush',
  changeDetection: ChangeDetectionStrategy.OnPush  // ✅ Optimized!
})
export class OnPushComponent {
  // Angular only checks when:
  // 1. @Input reference changes
  // 2. Event from component
  // 3. Observable emits (with async pipe)
}
```

**Benefits:**
- ✅ Better performance
- ✅ Fewer checks
- ✅ Forces immutable data patterns

---

## 2️⃣ VIEWCHILD & CONTENTCHILD

### @ViewChild - Access Child Component/DOM

```typescript
import { Component, ViewChild, ElementRef, AfterViewInit } from '@angular/core';

@Component({
  selector: 'app-parent',
  template: `
    <input #myInput type="text">
    <app-child #childComponent></app-child>
  `
})
export class ParentComponent implements AfterViewInit {
  // Access DOM element
  @ViewChild('myInput') inputElement: ElementRef;
  
  // Access child component
  @ViewChild('childComponent') child: ChildComponent;
  
  ngAfterViewInit() {
    // ✅ View is ready, can access now
    this.inputElement.nativeElement.focus();
    this.child.doSomething();
  }
}
```

### @ContentChild - Access Projected Content

```typescript
// Parent component
@Component({
  selector: 'app-card',
  template: `
    <div class="card-header">
      <ng-content select="[card-header]"></ng-content>
    </div>
    <div class="card-body">
      <ng-content></ng-content>
    </div>
  `
})
export class CardComponent implements AfterContentInit {
  @ContentChild('header') headerElement: ElementRef;
  
  ngAfterContentInit() {
    // ✅ Projected content is ready
    console.log(this.headerElement);
  }
}
```

---

## 3️⃣ CUSTOM PIPES

### Creating a Pipe

```typescript
import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'capitalize'
})
export class CapitalizePipe implements PipeTransform {
  transform(value: string): string {
    if (!value) return '';
    return value.charAt(0).toUpperCase() + value.slice(1).toLowerCase();
  }
}
```

### Usage:

```html
<p>{{ 'hello world' | capitalize }}</p>
<!-- Output: Hello world -->
```

### Pipe with Parameters:

```typescript
@Pipe({
  name: 'truncate'
})
export class TruncatePipe implements PipeTransform {
  transform(value: string, limit: number = 20): string {
    if (!value) return '';
    if (value.length <= limit) return value;
    return value.substring(0, limit) + '...';
  }
}
```

```html
<p>{{ longText | truncate:50 }}</p>
```

---

## 4️⃣ HTTP INTERCEPTORS

### What are Interceptors?

Interceptors can **modify HTTP requests/responses** before they're sent/received.

### Creating an Interceptor:

```typescript
import { Injectable } from '@angular/core';
import { 
  HttpRequest, 
  HttpHandler, 
  HttpEvent, 
  HttpInterceptor 
} from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable()
export class AuthInterceptor implements HttpInterceptor {
  
  intercept(
    request: HttpRequest<any>,
    next: HttpHandler
  ): Observable<HttpEvent<any>> {
    // Get token
    const token = localStorage.getItem('token');
    
    // Clone request and add header
    if (token) {
      request = request.clone({
        setHeaders: {
          Authorization: `Bearer ${token}`
        }
      });
    }
    
    // Continue with modified request
    return next.handle(request);
  }
}
```

### Registering Interceptor:

```typescript
// app.module.ts
import { HTTP_INTERCEPTORS } from '@angular/common/http';

@NgModule({
  providers: [
    {
      provide: HTTP_INTERCEPTORS,
      useClass: AuthInterceptor,
      multi: true  // ✅ Allow multiple interceptors
    }
  ]
})
```

### Common Use Cases:

1. **Add Auth Token**
2. **Logging**
3. **Error Handling**
4. **Loading Indicators**

---

## 5️⃣ ERROR HANDLING

### Global Error Handler

```typescript
import { ErrorHandler, Injectable } from '@angular/core';

@Injectable()
export class GlobalErrorHandler implements ErrorHandler {
  handleError(error: any): void {
    console.error('Global error:', error);
    // Send to error logging service
    // this.errorLoggingService.logError(error);
  }
}
```

### Register:

```typescript
@NgModule({
  providers: [
    { provide: ErrorHandler, useClass: GlobalErrorHandler }
  ]
})
```

### HTTP Error Interceptor:

```typescript
@Injectable()
export class ErrorInterceptor implements HttpInterceptor {
  intercept(
    request: HttpRequest<any>,
    next: HttpHandler
  ): Observable<HttpEvent<any>> {
    return next.handle(request).pipe(
      catchError((error: HttpErrorResponse) => {
        if (error.status === 401) {
          // Unauthorized - redirect to login
          this.router.navigate(['/login']);
        } else if (error.status === 500) {
          // Server error
          this.toastr.error('Server error occurred');
        }
        return throwError(() => error);
      })
    );
  }
}
```

---

## 6️⃣ DYNAMIC COMPONENTS

### Creating Components Dynamically

```typescript
import { Component, ViewContainerRef, ComponentFactoryResolver } from '@angular/core';

export class DynamicComponentLoader {
  constructor(
    private viewContainerRef: ViewContainerRef,
    private componentFactoryResolver: ComponentFactoryResolver
  ) {}
  
  loadComponent() {
    // Clear existing
    this.viewContainerRef.clear();
    
    // Create component factory
    const factory = this.componentFactoryResolver
      .resolveComponentFactory(MyDynamicComponent);
    
    // Create component
    const componentRef = this.viewContainerRef.createComponent(factory);
    
    // Set input
    componentRef.instance.data = { name: 'John' };
  }
}
```

---

## 7️⃣ MODULE ORGANIZATION

### Feature Modules

```typescript
// itinerary.module.ts
@NgModule({
  declarations: [
    InfoComponent,
    DestinationsComponent,
    BudgetComponent
  ],
  imports: [
    CommonModule,
    ReactiveFormsModule,
    ItineraryRoutesModule
  ],
  providers: [ItineraryService]
})
export class ItineraryModule { }
```

### Shared Module

```typescript
// shared.module.ts
@NgModule({
  declarations: [
    LoadingComponent,
    UploaderComponent
  ],
  exports: [  // ✅ Export to make available
    LoadingComponent,
    UploaderComponent
  ],
  imports: [CommonModule]
})
export class SharedModule { }
```

---

## 8️⃣ PERFORMANCE OPTIMIZATION

### 1. OnPush Change Detection

```typescript
@Component({
  changeDetection: ChangeDetectionStrategy.OnPush
})
```

### 2. TrackBy in *ngFor

```typescript
trackByFn(index: number, item: any): any {
  return item.id;
}
```

```html
<div *ngFor="let item of items; trackBy: trackByFn">
```

### 3. Lazy Loading

```typescript
{
  path: 'feature',
  loadChildren: () => import('./feature/feature.module')
    .then(m => m.FeatureModule)
}
```

### 4. Async Pipe (Auto-unsubscribe)

```html
<div *ngIf="data$ | async as data">
  {{ data.name }}
</div>
```

---

## 📝 Interview Questions & Answers

### Q1: What is Change Detection and OnPush strategy?

**Answer:**
- **Change Detection** = Angular checks if data changed and updates DOM
- **OnPush** = Only checks when:
  - @Input reference changes
  - Event from component
  - Observable emits (with async pipe)

**Benefits**: Better performance, fewer checks

### Q2: What are HTTP Interceptors?

**Answer:**
- Modify HTTP requests/responses
- Common uses: Add auth tokens, error handling, logging

```typescript
intercept(request: HttpRequest<any>, next: HttpHandler) {
  const modified = request.clone({
    setHeaders: { Authorization: `Bearer ${token}` }
  });
  return next.handle(modified);
}
```

### Q3: What's the difference between @ViewChild and @ContentChild?

**Answer:**
- **@ViewChild**: Access child component/DOM in component's template
- **@ContentChild**: Access projected content (ng-content)

### Q4: How do you handle errors globally?

**Answer:**
- **GlobalErrorHandler**: Catches all errors
- **HTTP Interceptor**: Handles HTTP errors
- **Component level**: try-catch in specific components

### Q5: What is lazy loading and why use it?

**Answer:**
- Loads modules **on-demand**
- **Benefits**: Faster initial load, smaller bundle, better performance

```typescript
{
  path: 'feature',
  loadChildren: () => import('./feature/feature.module')
    .then(m => m.FeatureModule)
}
```

### Q6: How do you optimize Angular performance?

**Answer:**
1. **OnPush change detection**
2. **TrackBy in *ngFor**
3. **Lazy loading modules**
4. **Async pipe** (auto-unsubscribe)
5. **Avoid unnecessary subscriptions**
6. **Use pure pipes**

---

## ✅ Key Takeaways

1. **Change Detection** = Angular checks for changes
2. **OnPush** = Performance optimization strategy
3. **Interceptors** = Modify HTTP requests/responses
4. **ViewChild/ContentChild** = Access child elements
5. **Custom Pipes** = Transform data in templates
6. **Error Handling** = Global + HTTP interceptors
7. **Performance** = OnPush, lazy loading, trackBy, async pipe

---

## 🎓 Final Review

You've now covered:
- ✅ Components & Modules
- ✅ Data Binding & Directives
- ✅ Services & DI
- ✅ HTTP & Observables
- ✅ Forms & Validation
- ✅ Routing & Guards
- ✅ Lifecycle Hooks
- ✅ Advanced Topics

**You're ready for your interview! 🚀**

---

**Back to**: [Main Guide](./README.md)
