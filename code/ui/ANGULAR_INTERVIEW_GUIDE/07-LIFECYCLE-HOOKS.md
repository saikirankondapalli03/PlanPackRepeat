# Angular Interview Guide - Part 7: Lifecycle Hooks

## 🎯 Overview

Lifecycle hooks are methods that Angular calls at specific points in a component's lifecycle. Understanding them is crucial for proper component management.

---

## 1️⃣ COMPONENT LIFECYCLE OVERVIEW

### Lifecycle Sequence:

```
1. constructor()           ← Not a hook, but runs first
2. ngOnChanges()           ← Input properties change
3. ngOnInit()              ← Component initialized (most common!)
4. ngDoCheck()             ← Custom change detection
5. ngAfterContentInit()    ← Content projected
6. ngAfterContentChecked() ← Content checked
7. ngAfterViewInit()       ← View initialized
8. ngAfterViewChecked()    ← View checked
9. ngOnDestroy()           ← Component destroyed (cleanup!)
```

---

## 2️⃣ CONSTRUCTOR

### What it is:
- **Not a lifecycle hook**, but runs first
- Used for dependency injection
- **Don't** put initialization logic here!

```typescript
export class MyComponent {
  constructor(
    private userService: UserService,  // ✅ DI
    private router: Router
  ) {
    // ❌ Don't do API calls here
    // ❌ Don't access @Input properties (not set yet!)
  }
}
```

---

## 3️⃣ ngOnInit() - Most Important!

### When it runs:
- **Once** after component initialization
- After first change detection
- **After** constructor and **after** inputs are set

### What to use it for:
- ✅ API calls
- ✅ Initialization logic
- ✅ Subscriptions
- ✅ Setting up form values

### Your Project Example:

```typescript
// app.component.ts
export class AppComponent implements OnInit {
  constructor(private auth: AuthService) {}
  
  ngOnInit() {
    // ✅ Perfect place for initialization
    this.auth.localAuthSetup();
  }
}
```

```typescript
// info.component.ts
export class InfoComponent implements OnInit {
  itineraryInfoForm = new FormGroup({...});
  
  ngOnInit() {
    // ✅ Subscribe to form changes
    this.subscriptions.add(
      this.itineraryInfoForm.valueChanges.subscribe((data) => {
        this.isFormValid = this.itineraryInfoForm.valid;
      })
    );
    
    // ✅ Load existing data if editing
    if (this.itineraryService.itineraryObj) {
      this.itineraryInfo = this.itineraryService.itineraryObj.info;
    }
  }
}
```

---

## 4️⃣ ngOnDestroy() - Cleanup!

### When it runs:
- **Before** component is destroyed
- When navigating away
- When component is removed from DOM

### What to use it for:
- ✅ **Unsubscribe** from observables
- ✅ Clear timers/intervals
- ✅ Clean up event listeners
- ✅ Cancel pending requests

### Your Project Example:

```typescript
// info.component.ts
export class InfoComponent implements OnInit, OnDestroy {
  subscriptions = new Subscription();
  itineraryUpdateTimeout: any;
  
  ngOnInit() {
    // Subscribe
    this.subscriptions.add(
      this.itineraryInfoForm.valueChanges.subscribe(...)
    );
  }
  
  ngOnDestroy() {
    // ✅ Unsubscribe to prevent memory leaks
    this.subscriptions.unsubscribe();
    
    // ✅ Clear timeout
    if (this.itineraryUpdateTimeout) {
      clearTimeout(this.itineraryUpdateTimeout);
    }
  }
}
```

### Common Pattern:

```typescript
import { Component, OnInit, OnDestroy } from '@angular/core';
import { Subscription } from 'rxjs';

export class MyComponent implements OnInit, OnDestroy {
  private subscriptions = new Subscription();
  
  ngOnInit() {
    // Add all subscriptions
    this.subscriptions.add(
      this.service.getData().subscribe(...)
    );
    this.subscriptions.add(
      this.service.getOtherData().subscribe(...)
    );
  }
  
  ngOnDestroy() {
    // ✅ Unsubscribe all at once
    this.subscriptions.unsubscribe();
  }
}
```

---

## 5️⃣ ngOnChanges() - Input Changes

### When it runs:
- **Every time** an `@Input()` property changes
- Before `ngOnInit()` on first change
- **Not** for object property changes (only reference changes)

### Usage:

```typescript
import { Component, Input, OnChanges, SimpleChanges } from '@angular/core';

export class UserProfileComponent implements OnChanges {
  @Input() userId: string;
  @Input() user: User;
  
  ngOnChanges(changes: SimpleChanges) {
    // Called when userId or user reference changes
    if (changes['userId']) {
      console.log('userId changed:', 
        changes['userId'].previousValue, 
        '->', 
        changes['userId'].currentValue
      );
      this.loadUser();
    }
    
    if (changes['user'] && !changes['user'].firstChange) {
      console.log('User object reference changed');
    }
  }
}
```

### Important Notes:
- Only detects **reference changes**, not object property changes
- `firstChange` = true on first call

---

## 6️⃣ ngAfterViewInit() - View Ready

### When it runs:
- **Once** after component's view is initialized
- After child views are initialized
- **Perfect** for accessing `@ViewChild` elements

### Usage:

```typescript
import { Component, ViewChild, ElementRef, AfterViewInit } from '@angular/core';

export class MyComponent implements AfterViewInit {
  @ViewChild('myInput') inputElement: ElementRef;
  
  ngAfterViewInit() {
    // ✅ View is ready, can access DOM elements
    this.inputElement.nativeElement.focus();
    
    // ✅ Can access ViewChild now
    console.log(this.inputElement.nativeElement);
  }
}
```

---

## 7️⃣ ngAfterViewChecked() - View Checked

### When it runs:
- **After every change detection cycle** that checks the component's view
- Runs **after** `ngAfterViewInit()` on first check
- Called frequently - **be careful with performance!**

### Usage:

```typescript
import { Component, AfterViewChecked } from '@angular/core';

export class MyComponent implements AfterViewChecked {
  ngAfterViewChecked() {
    // ⚠️ Called very frequently - avoid heavy operations!
    // Use for: DOM measurements, third-party library updates
  }
}
```

### ⚠️ Important:
- Called **after every change detection** (can be many times!)
- Avoid expensive operations here
- Rarely needed in practice

---

## 8️⃣ ngAfterContentInit() - Content Projected

### When it runs:
- **Once** after Angular projects external content into the component
- Related to **content projection** (`<ng-content>`)
- Runs **before** `ngAfterViewInit()`

### What is Content Projection?

```typescript
// parent.component.html
<app-child>
  <p>This content is projected!</p>  ← Projected content
</app-child>

// child.component.html
<div>
  <ng-content></ng-content>  ← Content appears here
</div>
```

### Usage:

```typescript
import { Component, ContentChild, AfterContentInit } from '@angular/core';

export class MyComponent implements AfterContentInit {
  @ContentChild('projectedContent') content: ElementRef;
  
  ngAfterContentInit() {
    // ✅ Projected content is now available
    // ✅ Can access @ContentChild now
    console.log(this.content);
  }
}
```

---

## 9️⃣ ngAfterContentChecked() - Content Checked

### When it runs:
- **After every change detection** that checks projected content
- Runs **after** `ngAfterContentInit()` on first check
- Called frequently - **be careful with performance!**

### Usage:

```typescript
import { Component, AfterContentChecked } from '@angular/core';

export class MyComponent implements AfterContentChecked {
  ngAfterContentChecked() {
    // ⚠️ Called very frequently - avoid heavy operations!
  }
}
```

### ⚠️ Important:
- Called **after every change detection** (can be many times!)
- Avoid expensive operations here
- Rarely needed in practice

---

## 🔟 ngDoCheck() - Custom Change Detection

### When it runs:
- **During every change detection cycle**
- Runs **after** `ngOnChanges()` and **after** `ngOnInit()`
- Allows you to implement **custom change detection logic**

### When to use:
- When you need to detect changes that Angular's default change detection misses
- For detecting **deep object changes** (not just reference changes)
- ⚠️ **Use sparingly** - can impact performance!

### Usage:

```typescript
import { Component, Input, DoCheck } from '@angular/core';

export class MyComponent implements DoCheck {
  @Input() user: User;
  private previousUser: User;
  
  ngDoCheck() {
    // Custom logic to detect deep changes
    if (this.user && this.user !== this.previousUser) {
      // Check if any property changed
      if (this.user.name !== this.previousUser?.name) {
        console.log('User name changed!');
      }
      this.previousUser = { ...this.user };
    }
  }
}
```

### ⚠️ Important:
- Called **very frequently** (every change detection cycle)
- Can impact performance if not used carefully
- Usually better to use `ngOnChanges()` or immutable data patterns

### ngDoCheck vs ngOnChanges:

| ngDoCheck | ngOnChanges |
|-----------|-------------|
| Every change detection | Only when @Input reference changes |
| Can detect deep changes | Only reference changes |
| More expensive | More efficient |
| Use for custom logic | Use for @Input changes |

---

## 📝 Interview Questions & Answers

### Q1: What is ngOnInit and when is it called?

**Answer:**
- Lifecycle hook called **once** after component initialization
- Runs **after** constructor and **after** inputs are set
- **Perfect for**: API calls, subscriptions, initialization logic

```typescript
ngOnInit() {
  // Component is ready, inputs are set
  this.loadData();
}
```

### Q2: Why is ngOnDestroy important?

**Answer:**
- Called **before** component is destroyed
- **Critical** for preventing memory leaks:
  - Unsubscribe from observables
  - Clear timers/intervals
  - Cancel pending requests

```typescript
ngOnDestroy() {
  this.subscription.unsubscribe();  // ✅ Prevent memory leak
}
```

### Q3: What's the difference between ngOnInit and constructor?

**Answer:**

| Constructor | ngOnInit |
|------------|----------|
| Runs first | Runs after inputs set |
| For DI only | For initialization |
| Inputs not available | Inputs available |
| Don't do API calls | Perfect for API calls |

### Q4: How do you prevent memory leaks with subscriptions?

**Answer:**
**Method 1: Unsubscribe in ngOnDestroy**
```typescript
private subscription: Subscription;

ngOnInit() {
  this.subscription = this.service.getData().subscribe(...);
}

ngOnDestroy() {
  this.subscription.unsubscribe();
}
```

**Method 2: Use Subscription container**
```typescript
private subscriptions = new Subscription();

ngOnInit() {
  this.subscriptions.add(this.service.getData().subscribe(...));
}

ngOnDestroy() {
  this.subscriptions.unsubscribe();
}
```

**Method 3: Use async pipe** (best!)
```typescript
data$ = this.service.getData();

// In template
<div *ngIf="data$ | async as data">
  {{ data.name }}
</div>
```

### Q5: What's the difference between ngAfterContentInit and ngAfterViewInit?

**Answer:**
- **ngAfterContentInit**: Runs when **projected content** (`<ng-content>`) is initialized
- **ngAfterViewInit**: Runs when the component's **own view** is initialized
- Content hooks run **before** view hooks

```typescript
// Content projection example
// parent.component.html
<app-child>
  <p>Projected content</p>  ← This triggers ngAfterContentInit
</app-child>

// child.component.html
<div>
  <ng-content></ng-content>  ← Content appears here
  <p>Component's own view</p>  ← This triggers ngAfterViewInit
</div>
```

### Q6: When would you use ngDoCheck?

**Answer:**
- To detect changes that Angular's default change detection misses
- For **deep object changes** (not just reference changes)
- ⚠️ **Use sparingly** - called every change detection cycle!

```typescript
ngDoCheck() {
  // Custom change detection logic
  if (this.hasDeepChanges()) {
    this.updateComponent();
  }
}
```

**Better alternatives:**
- Use `ngOnChanges()` for `@Input` changes
- Use immutable data patterns
- Use `OnPush` change detection strategy

### Q7: What's the execution order of lifecycle hooks?

**Answer:**
```
1. constructor()           ← DI setup
2. ngOnChanges()           ← @Input changes (if any)
3. ngOnInit()              ← Initialization
4. ngDoCheck()             ← Custom change detection
5. ngAfterContentInit()    ← Projected content ready
6. ngAfterContentChecked() ← Projected content checked
7. ngAfterViewInit()       ← View ready
8. ngAfterViewChecked()    ← View checked
9. ngOnDestroy()           ← Cleanup (on destroy)
```

**Note:** Hooks 4, 6, and 8 run on **every change detection cycle**, not just once!

---

## ✅ Key Takeaways

### Most Common Hooks (Use These Daily):
1. **ngOnInit** = Most common, for initialization (API calls, subscriptions)
2. **ngOnDestroy** = Critical for cleanup, prevent memory leaks (unsubscribe!)
3. **ngOnChanges** = React to @Input changes
4. **ngAfterViewInit** = Access ViewChild elements

### Less Common Hooks (Know These for Interviews):
5. **ngAfterContentInit** = Projected content (`<ng-content>`) ready
6. **ngAfterViewChecked** = After view checked (runs frequently - be careful!)
7. **ngAfterContentChecked** = After content checked (runs frequently - be careful!)
8. **ngDoCheck** = Custom change detection (use sparingly!)

### Important Rules:
- **Constructor** = Only for DI, not initialization
- **Always unsubscribe** in ngOnDestroy
- **Content hooks** run before **view hooks**
- **Checked hooks** (ngDoCheck, ngAfterContentChecked, ngAfterViewChecked) run on **every change detection** - avoid expensive operations!

---

**Next**: [Part 8 - Advanced Topics](./08-ADVANCED-TOPICS.md)
