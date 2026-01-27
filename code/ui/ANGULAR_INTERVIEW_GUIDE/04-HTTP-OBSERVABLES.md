# Angular Interview Guide - Part 4: HTTP & Observables (RxJS)

## 🎯 Overview

Angular uses **RxJS Observables** for handling asynchronous operations like HTTP requests. Understanding Observables is **critical** for Angular interviews.

---

## 1️⃣ WHAT ARE OBSERVABLES?

### Observable = Stream of Data Over Time

Think of it like a **newsletter subscription**:
- You **subscribe** to receive updates
- Publisher **emits** values over time
- You **unsubscribe** when done

### Basic Example:

```typescript
import { Observable } from 'rxjs';

// Create observable
const observable = new Observable(observer => {
  observer.next('Hello');
  observer.next('World');
  observer.complete();
});

// Subscribe to it
observable.subscribe(
  value => console.log(value),  // 'Hello', 'World'
  error => console.error(error),
  () => console.log('Complete')
);
```

---

## 2️⃣ HTTP REQUESTS WITH OBSERVABLES

### Setting Up HttpClient

**1. Import HttpClientModule in your module:**

```typescript
// app.module.ts
import { HttpClientModule } from '@angular/common/http';

@NgModule({
  imports: [
    HttpClientModule  // ✅ Enables HTTP
  ]
})
```

**2. Inject HttpClient in service:**

```typescript
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class UserService {
  constructor(private http: HttpClient) {}  // ✅ Inject HttpClient
}
```

### Your Project Example: `user.service.ts`

```typescript
@Injectable({ providedIn: 'root' })
export class UserService {
  constructor(private authHttp: HttpClient) {}
  
  // GET request
  getUserInfo(email: string): Observable<any> {
    return this.authHttp.get(
      `https://travelapp-boot.cfapps.io/users/getUserByEmail/${email}`
    );
  }
  
  // POST request
  createUser(userInfo: User) {
    return this.authHttp.post(
      `https://travelapp-boot.cfapps.io/users/createuser`,
      userInfo
    );
  }
  
  // PUT request
  updateUser(userInfo: User) {
    return this.authHttp.put(
      `https://travelapp-boot.cfapps.io/users/${userInfo.id}`,
      userInfo
    );
  }
}
```

---

## 3️⃣ SUBSCRIBING TO OBSERVABLES

### In Components

```typescript
import { Component, OnInit, OnDestroy } from '@angular/core';
import { Subscription } from 'rxjs';
import { UserService } from './services/user.service';

export class ProfileComponent implements OnInit, OnDestroy {
  userData: any;
  private subscription: Subscription;
  
  constructor(private userService: UserService) {}
  
  ngOnInit() {
    // Subscribe to HTTP request
    this.subscription = this.userService.getUserInfo('user@example.com')
      .subscribe(
        data => {
          this.userData = data;  // Success
        },
        error => {
          console.error('Error:', error);  // Error handling
        }
      );
  }
  
  ngOnDestroy() {
    // ✅ IMPORTANT: Unsubscribe to prevent memory leaks
    if (this.subscription) {
      this.subscription.unsubscribe();
    }
  }
}
```

### Your Project Example:

```typescript
// profile.component.ts
export class ProfileComponent implements OnInit, OnDestroy {
  subscription = new Subscription();
  
  constructor(private userService: UserService) {}
  
  ngOnInit() {
    this.subscription.add(
      this.userService.getUserInfo(this.profileForm.email)
        .subscribe(
          data => {
            console.log('User info:', data);
            // Handle success
          },
          error => {
            console.error('Error:', error);
            // Handle error
          }
        )
    );
  }
  
  ngOnDestroy() {
    this.subscription.unsubscribe();  // ✅ Clean up
  }
}
```

---

## 4️⃣ RXJS OPERATORS

### Common Operators

#### A. `map` - Transform Data

```typescript
import { map } from 'rxjs/operators';

this.userService.getUserInfo(email)
  .pipe(
    map(user => user.firstName + ' ' + user.lastName)  // Transform
  )
  .subscribe(fullName => {
    console.log(fullName);  // "John Doe"
  });
```

#### B. `catchError` - Handle Errors

```typescript
import { catchError } from 'rxjs/operators';
import { of } from 'rxjs';

this.userService.getUserInfo(email)
  .pipe(
    catchError(error => {
      console.error('Error:', error);
      return of(null);  // Return default value
    })
  )
  .subscribe(data => {
    // data is either user info or null
  });
```

**Your Project Example:**

```typescript
// profile-completion.guard.ts
return this.userService.getUserInfo(userEmail).pipe(
  map(localUserInfoRef => {
    if (!localUserInfoRef || !localUserInfoRef.firstName) {
      this.navigateToProfilePage();
      return false;
    }
    return true;
  }),
  catchError(error => {
    this.navigateToProfilePage();
    return of(false);  // ✅ Return observable of false
  })
);
```

#### C. `tap` - Side Effects (Logging, etc.)

```typescript
import { tap } from 'rxjs/operators';

this.userService.getUserInfo(email)
  .pipe(
    tap(data => console.log('Received:', data)),  // Side effect
    tap(data => this.isLoading = false)  // Update loading state
  )
  .subscribe();
```

**Your Project Example:**

```typescript
// auth.service.ts
getUser$(options?): Observable<any> {
  return this.auth0Client$.pipe(
    concatMap((client: Auth0Client) => from(client.getUser(options))),
    tap(user => this.userProfileSubject$.next(user))  // ✅ Side effect
  );
}
```

#### D. `shareReplay` - Share and Cache

```typescript
import { shareReplay } from 'rxjs/operators';

// Share result with multiple subscribers
this.userData$ = this.http.get('/api/user')
  .pipe(shareReplay(1));  // Cache last value
```

**Your Project Example:**

```typescript
// auth.service.ts
auth0Client$ = (from(createAuth0Client({...})) as Observable<Auth0Client>)
  .pipe(
    shareReplay(1)  // ✅ Share with all subscribers
  );
```

---

## 5️⃣ SUBJECTS - Special Observables

### BehaviorSubject - Has Initial Value

```typescript
import { BehaviorSubject } from 'rxjs';

export class DataService {
  private dataSubject = new BehaviorSubject<string>('initial');
  data$ = this.dataSubject.asObservable();
  
  updateData(value: string) {
    this.dataSubject.next(value);
  }
  
  getCurrentValue(): string {
    return this.dataSubject.value;  // Get current value
  }
}
```

**Your Project Example:**

```typescript
// auth.service.ts
private userProfileSubject$ = new BehaviorSubject<any>(null);
userProfile$ = this.userProfileSubject$.asObservable();
```

### ReplaySubject - Replay Last N Values

```typescript
import { ReplaySubject } from 'rxjs';

export class ItineraryService {
  // Replay last 1 value to new subscribers
  private itinerarySubject = new ReplaySubject<Itinerary>(1);
  itineraryStream = this.itinerarySubject.asObservable();
  
  broadcastUpdates(itineraryData: Itinerary): void {
    this.itinerarySubject.next(itineraryData);
  }
}
```

**Your Project Example:**

```typescript
// itinerary.service.ts
private itinerarySubject: ReplaySubject<Itinerary> = new ReplaySubject();
itineraryStream = this.itinerarySubject.asObservable();
```

---

## 6️⃣ ASYNC PIPE - Automatic Subscription

### Instead of Manual Subscription

**❌ Manual (Need to unsubscribe):**
```typescript
export class MyComponent implements OnInit, OnDestroy {
  data: any;
  subscription: Subscription;
  
  ngOnInit() {
    this.subscription = this.service.getData()
      .subscribe(data => this.data = data);
  }
  
  ngOnDestroy() {
    this.subscription.unsubscribe();
  }
}
```

**✅ Async Pipe (Automatic unsubscribe):**
```typescript
export class MyComponent {
  data$ = this.service.getData();  // Observable
}
```

```html
<!-- Template -->
<div *ngIf="data$ | async as data">
  {{ data.name }}
</div>
```

**Benefits:**
- ✅ Automatic unsubscribe
- ✅ No memory leaks
- ✅ Cleaner code

---

## 📝 Interview Questions & Answers

### Q1: What is an Observable?

**Answer:**
- **Observable** = Stream of data over time
- You **subscribe** to receive values
- Can emit multiple values asynchronously
- Must **unsubscribe** to prevent memory leaks

### Q2: What's the difference between Observable and Promise?

**Answer:**

| Observable | Promise |
|-----------|---------|
| Can emit multiple values | Emits single value |
| Lazy (only runs when subscribed) | Eager (runs immediately) |
| Can be cancelled | Cannot be cancelled |
| Has operators (map, filter, etc.) | Limited methods (then, catch) |

### Q3: Why unsubscribe from Observables?

**Answer:**
- Prevents **memory leaks**
- Stops unnecessary processing
- Clean up resources

```typescript
// ✅ Good - Unsubscribe
ngOnDestroy() {
  this.subscription.unsubscribe();
}

// ✅ Better - Use async pipe (auto-unsubscribes)
data$ = this.service.getData();
```

### Q4: What's the difference between Subject, BehaviorSubject, and ReplaySubject?

**Answer:**

**Subject:**
- No initial value
- Only emits values after subscription

**BehaviorSubject:**
- Has initial value
- New subscribers get current value immediately

**ReplaySubject:**
- Replays last N values to new subscribers
- No initial value required

### Q5: What is the async pipe?

**Answer:**
- Automatically subscribes and unsubscribes
- Prevents memory leaks
- Used in templates

```html
<div *ngIf="data$ | async as data">
  {{ data.name }}
</div>
```

---

## ✅ Key Takeaways

1. **Observables** = Streams of data over time
2. **Subscribe** to receive values
3. **Unsubscribe** to prevent memory leaks (or use async pipe)
4. **RxJS Operators** = Transform, filter, combine observables
5. **Subjects** = Special observables you can emit to
6. **Async Pipe** = Automatic subscription/unsubscription
7. **HTTP returns Observables** - must subscribe to execute

---

**Next**: [Part 5 - Forms & Validation](./05-FORMS-VALIDATION.md)
