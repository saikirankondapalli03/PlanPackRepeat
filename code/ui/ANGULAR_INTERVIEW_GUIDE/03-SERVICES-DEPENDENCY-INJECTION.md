# Angular Interview Guide - Part 3: Services & Dependency Injection

## 🎯 What is a Service?

A **service** is a class that contains **business logic**, **data**, or **functionality** that can be shared across multiple components.

**Key Characteristics:**
- **Singleton** by default (one instance shared across app)
- Contains reusable logic
- Handles API calls, data manipulation, utilities

---

## 1️⃣ CREATING A SERVICE

### Basic Service Structure

```typescript
import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'  // Makes it available app-wide
})
export class UserService {
  
  private userEmail: string;
  
  getUserInfo(email: string) {
    // Business logic here
    return this.userEmail;
  }
  
  setUserEmail(email: string) {
    this.userEmail = email;
  }
}
```

### Your Project Example: `user.service.ts`

```typescript
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, ReplaySubject } from 'rxjs';

@Injectable({
  providedIn: 'root'  // ✅ Available everywhere
})
export class UserService {
  
  userEmail: string;
  isSuperUser: boolean;
  
  // Observable for user email updates
  private userEmailUpdate = new ReplaySubject<string>();
  userEmailObservable = this.userEmailUpdate.asObservable();
  
  constructor(private authHttp: HttpClient) { }  // ✅ Dependency Injection
  
  // Get user info from API
  getUserInfo(email: string): Observable<any> {
    this.userEmail = email;
    return this.authHttp.get(
      `https://travelapp-boot.cfapps.io/users/getUserByEmail/${email}`
    );
  }
  
  // Create new user
  createUser(userInfo: User) {
    return this.authHttp.post(
      `https://travelapp-boot.cfapps.io/users/createuser`, 
      userInfo
    );
  }
  
  // Update existing user
  updateUser(userInfo: User) {
    return this.authHttp.put(
      `https://travelapp-boot.cfapps.io/users/${userInfo.id}`, 
      userInfo
    );
  }
  
  // Set user email and notify subscribers
  setUserEmail(userEmail: string) {
    this.userEmail = userEmail;
    this.userEmailUpdate.next(this.userEmail);  // ✅ Emit to subscribers
  }
}
```

---

## 2️⃣ DEPENDENCY INJECTION (DI)

### What is Dependency Injection?

**Dependency Injection** is a design pattern where Angular **automatically provides** dependencies to classes instead of you creating them manually.

### How It Works:

**❌ Without DI (Manual Creation):**
```typescript
export class MyComponent {
  private userService: UserService;
  
  constructor() {
    this.userService = new UserService();  // ❌ Bad!
  }
}
```

**✅ With DI (Angular Provides):**
```typescript
export class MyComponent {
  constructor(private userService: UserService) {
    // ✅ Angular automatically creates and provides UserService
  }
}
```

### Your Project Example:

```typescript
// app.component.ts
export class AppComponent implements OnInit {
  constructor(private auth: AuthService) {}
  // Angular automatically creates and provides AuthService
}
```

---

## 3️⃣ PROVIDING SERVICES

### Three Ways to Provide Services:

#### A. `providedIn: 'root'` (Recommended - Your Project Uses This)

```typescript
@Injectable({
  providedIn: 'root'  // ✅ Singleton, available everywhere
})
export class UserService { }
```

**Benefits:**
- ✅ **Tree-shakeable** (removed if unused)
- ✅ Available app-wide
- ✅ One instance (singleton)

#### B. Module Providers

```typescript
@NgModule({
  providers: [UserService]  // Available to this module
})
export class AppModule { }
```

#### C. Component Providers

```typescript
@Component({
  providers: [UserService]  // New instance per component
})
export class MyComponent { }
```

**⚠️ Component-level providers create NEW instances!**

---

## 4️⃣ INJECTING SERVICES

### In Components

```typescript
import { Component } from '@angular/core';
import { UserService } from './services/user.service';

@Component({
  selector: 'app-profile',
  templateUrl: './profile.component.html'
})
export class ProfileComponent {
  
  // Inject via constructor
  constructor(private userService: UserService) {}
  
  ngOnInit() {
    // Use the service
    this.userService.getUserInfo('user@example.com')
      .subscribe(data => {
        console.log(data);
      });
  }
}
```

### In Other Services

```typescript
@Injectable({
  providedIn: 'root'
})
export class ItineraryService {
  
  // Inject another service
  constructor(
    private httpClient: HttpClient,
    private userService: UserService  // ✅ Inject service into service
  ) {}
  
  saveItinerary() {
    // Use userService
    const email = this.userService.userEmail;
    // ... rest of logic
  }
}
```

### Your Project Example:

```typescript
// itinerary.service.ts
export class ItineraryService {
  constructor(
    private httpClient: HttpClient,
    private userService: UserService  // ✅ Injected!
  ) {}
  
  saveItinerary(): Observable<any> {
    const payload = {
      email: this.userService.userEmail,  // ✅ Using injected service
      // ... rest
    };
    return this.httpClient.post(url, payload);
  }
}
```

---

## 5️⃣ SERVICE LIFECYCLE

### Singleton Behavior

```typescript
@Injectable({ providedIn: 'root' })
export class CounterService {
  count = 0;  // Shared across all components!
  
  increment() {
    this.count++;
  }
}
```

```typescript
// Component A
export class ComponentA {
  constructor(private counter: CounterService) {}
  
  onClick() {
    this.counter.increment();  // count = 1
  }
}

// Component B
export class ComponentB {
  constructor(private counter: CounterService) {}
  
  getCount() {
    return this.counter.count;  // Still 1! Same instance!
  }
}
```

---

## 6️⃣ REAL-WORLD PATTERNS

### Pattern 1: HTTP Service (Your Project)

```typescript
@Injectable({ providedIn: 'root' })
export class UserService {
  constructor(private http: HttpClient) {}
  
  getUserInfo(email: string): Observable<User> {
    return this.http.get<User>(`/api/users/${email}`);
  }
}
```

### Pattern 2: State Management Service

```typescript
@Injectable({ providedIn: 'root' })
export class ItineraryService {
  // Private subject - source of truth
  private itinerarySubject = new ReplaySubject<Itinerary>();
  
  // Public observable - components subscribe to this
  itineraryStream = this.itinerarySubject.asObservable();
  
  // Update state
  broadcastUpdates(itineraryData: Itinerary): void {
    this.itineraryObj = itineraryData;
    this.itinerarySubject.next(itineraryData);  // Notify subscribers
  }
}
```

**Usage in Component:**
```typescript
export class InfoComponent {
  constructor(private itineraryService: ItineraryService) {}
  
  ngOnInit() {
    // Subscribe to updates
    this.itineraryService.itineraryStream.subscribe(data => {
      console.log('Itinerary updated:', data);
    });
  }
  
  onSave() {
    // Update state
    this.itineraryService.broadcastUpdates(newData);
  }
}
```

---

## 📝 Interview Questions & Answers

### Q1: What is Dependency Injection and why use it?

**Answer:**
- **DI** = Angular automatically provides dependencies
- **Benefits**:
  - ✅ **Testable** - Easy to mock dependencies
  - ✅ **Maintainable** - Change implementation in one place
  - ✅ **Flexible** - Swap implementations easily
  - ✅ **Loose coupling** - Components don't create dependencies

```typescript
// ✅ Good - DI
constructor(private userService: UserService) {}

// ❌ Bad - Manual creation
constructor() {
  this.userService = new UserService();
}
```

### Q2: What's the difference between `providedIn: 'root'` and module providers?

**Answer:**

**`providedIn: 'root'`:**
- ✅ Singleton app-wide
- ✅ Tree-shakeable (removed if unused)
- ✅ Modern approach (Angular 6+)
- ✅ Available everywhere

**Module providers:**
- Available only to that module and children
- Not tree-shakeable
- Older approach

### Q3: Can you inject a service into another service?

**Answer:**
**Yes!** Services can inject other services:

```typescript
@Injectable({ providedIn: 'root' })
export class ServiceA { }

@Injectable({ providedIn: 'root' })
export class ServiceB {
  constructor(private serviceA: ServiceA) {}  // ✅ Yes!
}
```

### Q4: What happens if you provide a service at component level?

**Answer:**
- Creates a **new instance** for that component and its children
- **Not** a singleton anymore
- Each component gets its own instance

```typescript
@Component({
  providers: [UserService]  // New instance!
})
export class MyComponent { }
```

### Q5: How do you share data between components using services?

**Answer:**
Use **RxJS Subjects/Observables**:

```typescript
@Injectable({ providedIn: 'root' })
export class DataService {
  private dataSubject = new BehaviorSubject<string>('');
  data$ = this.dataSubject.asObservable();
  
  updateData(value: string) {
    this.dataSubject.next(value);
  }
}

// Component A - Updates data
export class ComponentA {
  constructor(private dataService: DataService) {}
  
  update() {
    this.dataService.updateData('new value');
  }
}

// Component B - Receives updates
export class ComponentB {
  data: string;
  
  constructor(private dataService: DataService) {}
  
  ngOnInit() {
    this.dataService.data$.subscribe(value => {
      this.data = value;  // Automatically updates!
    });
  }
}
```

---

## ✅ Key Takeaways

1. **Services** = Business logic, data, reusable functionality
2. **Dependency Injection** = Angular provides dependencies automatically
3. **`providedIn: 'root'`** = Singleton, app-wide, tree-shakeable
4. **Services can inject other services**
5. **Use RxJS Subjects** to share data between components
6. **Component-level providers** = new instance per component

---

**Next**: [Part 4 - HTTP & Observables](./04-HTTP-OBSERVABLES.md)
