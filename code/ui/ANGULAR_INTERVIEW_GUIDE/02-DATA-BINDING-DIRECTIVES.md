# Angular Interview Guide - Part 2: Data Binding & Directives

## 🎯 Overview

Data binding connects your **TypeScript code** with your **HTML template**. Angular has 4 types of binding.

---

## 1️⃣ INTERPOLATION - Display Data

### Syntax: `{{ expression }}`

**Example from your project:**

```typescript
// component.ts
export class ProfileComponent {
  userName = 'John Doe';
  email = 'john@example.com';
}
```

```html
<!-- component.html -->
<h1>Welcome, {{ userName }}!</h1>
<p>Email: {{ email }}</p>
```

**What happens:**
- Angular evaluates `{{ userName }}` and displays "John Doe"
- Updates automatically when `userName` changes

---

## 2️⃣ PROPERTY BINDING - Set Element Properties

### Syntax: `[property]="expression"`

**Example:**

```typescript
// component.ts
export class MyComponent {
  imageUrl = 'https://example.com/image.jpg';
  isDisabled = false;
}
```

```html
<!-- component.html -->
<img [src]="imageUrl">
<button [disabled]="isDisabled">Click me</button>
```

**What happens:**
- `[src]` sets the `src` attribute to `imageUrl`
- `[disabled]` sets button's disabled state

**Your Project Example from `info.component.html`:**

```html
<input 
  type="date" 
  formControlName="startDate" 
  [ngModel]="itineraryInfo.startDate"
  [min]="today"  <!-- ✅ Property binding -->
  required>
```

---

## 3️⃣ EVENT BINDING - Handle User Actions

### Syntax: `(event)="handler()"`

**Example:**

```typescript
// component.ts
export class LoginComponent {
  login() {
    console.log('Login clicked!');
    // Login logic here
  }
  
  onInputChange(value: string) {
    console.log('Input changed:', value);
  }
}
```

```html
<!-- component.html -->
<button (click)="login()">Login</button>
<input (input)="onInputChange($event.target.value)">
```

**What happens:**
- `(click)` calls `login()` when button is clicked
- `$event` = event object with details

**Your Project Example:**

```typescript
// login.component.ts
export class LoginComponent {
  login(): void {
    this.authService.login('/dashboard');
  }
}
```

```html
<!-- login.component.html -->
<button (click)="login()">Sign In</button>
```

**Your Project Example from `info.component.html`:**

```html
<form [formGroup]="itineraryInfoForm" (ngSubmit)="onSubmit()">
  <!-- ✅ Event binding for form submission -->
</form>
```

---

## 4️⃣ TWO-WAY BINDING - Bidirectional Data Flow

### Syntax: `[(ngModel)]="property"`

**Requires:** `FormsModule` (already in your app!)

**Example:**

```typescript
// component.ts
export class ProfileComponent {
  firstName = '';
  lastName = '';
}
```

```html
<!-- component.html -->
<input [(ngModel)]="firstName" placeholder="First Name">
<input [(ngModel)]="lastName" placeholder="Last Name">

<p>Full Name: {{ firstName }} {{ lastName }}</p>
```

**What happens:**
- User types → updates `firstName`/`lastName`
- Code changes `firstName` → updates input
- **Two-way** = both directions!

**Your Project Example from `info.component.html`:**

```html
<input 
  type="text" 
  formControlName="name"
  [ngModel]="itineraryInfo.name"  <!-- ✅ Two-way binding -->
  placeholder="Enter itinerary name">
```

---

## 5️⃣ DIRECTIVES - Modify DOM Behavior

### Built-in Directives

#### A. `*ngIf` - Conditionally Show/Hide

```typescript
export class MyComponent {
  isLoggedIn = true;
  items = ['item1', 'item2'];
}
```

```html
<!-- Show only if condition is true -->
<div *ngIf="isLoggedIn">
  <p>Welcome back!</p>
</div>

<!-- Alternative syntax -->
<div *ngIf="isLoggedIn; else notLoggedIn">
  <p>Welcome!</p>
</div>
<ng-template #notLoggedIn>
  <p>Please log in</p>
</ng-template>
```

**Your Project Example:**

```html
<!-- Show loading only when loading -->
<app-loading *ngIf="isLoading"></app-loading>
```

---

#### B. `*ngFor` - Loop Through Arrays

```typescript
export class MyComponent {
  destinations = [
    { name: 'Paris', date: '2024-01-01' },
    { name: 'London', date: '2024-01-05' }
  ];
}
```

```html
<!-- Loop through array -->
<ul>
  <li *ngFor="let destination of destinations">
    {{ destination.name }} - {{ destination.date }}
  </li>
</ul>

<!-- With index -->
<ul>
  <li *ngFor="let destination of destinations; let i = index">
    {{ i + 1 }}. {{ destination.name }}
  </li>
</ul>

<!-- Track by for performance -->
<ul>
  <li *ngFor="let destination of destinations; trackBy: trackByFn">
    {{ destination.name }}
  </li>
</ul>
```

**Your Project Example:**

```typescript
// destinations.component.ts
destinations: Array<Destinations> = [];
```

```html
<!-- destinations.component.html -->
<div *ngFor="let destination of destinations">
  <h3>{{ destination.name }}</h3>
  <p>{{ destination.streetAddress }}</p>
</div>
```

---

#### C. `[ngClass]` - Dynamic CSS Classes

```typescript
export class MyComponent {
  isActive = true;
  isError = false;
}
```

```html
<!-- Single class -->
<div [ngClass]="{ 'active': isActive }">Content</div>

<!-- Multiple classes -->
<div [ngClass]="{ 
  'active': isActive, 
  'error': isError,
  'highlight': true 
}">Content</div>

<!-- Array syntax -->
<div [ngClass]="['class1', 'class2']">Content</div>
```

**Your Project Example from `info.component.html`:**

```html
<button 
  type="submit" 
  class="btn btn-primary next pull-right" 
  [disabled]="!isFormValid"
  [ngClass]="{'disabled': !isFormValid}">  <!-- ✅ Dynamic class -->
  Next
</button>
```

---

#### D. `[ngStyle]` - Dynamic Inline Styles

```typescript
export class MyComponent {
  fontSize = 16;
  color = 'blue';
}
```

```html
<div [ngStyle]="{
  'font-size.px': fontSize,
  'color': color,
  'font-weight': 'bold'
}">Styled text</div>
```

---

#### E. `[ngSwitch]` - Multiple Conditions

```typescript
export class MyComponent {
  status = 'loading';
}
```

```html
<div [ngSwitch]="status">
  <p *ngSwitchCase="'loading'">Loading...</p>
  <p *ngSwitchCase="'success'">Success!</p>
  <p *ngSwitchCase="'error'">Error occurred</p>
  <p *ngSwitchDefault>Unknown status</p>
</div>
```

---

## 6️⃣ REAL-WORLD EXAMPLE FROM YOUR PROJECT

### Your `info.component.ts`:

```typescript
export class InfoComponent implements OnInit {
  itineraryInfoForm = new FormGroup({
    name: new FormControl('', Validators.required),
    startDate: new FormControl('', Validators.required),
    endDate: new FormControl('', Validators.required),
    visibility: new FormControl('Public')
  });
  
  isFormValid: boolean;
  today: string;
  
  constructor(
    private router: Router, 
    private itineraryService: ItineraryService
  ) {}
  
  ngOnInit() {
    this.itineraryInfoForm.valueChanges.subscribe((data) => {
      this.isFormValid = this.itineraryInfoForm.valid;
    });
    this.setTodaysDate();
  }
  
  onSubmit() {
    this.itineraryInfo = {
      name: this.itineraryInfoForm.value.name,
      startDate: this.itineraryInfoForm.value.startDate,
      endDate: this.itineraryInfoForm.value.endDate,
      visiblity: this.itineraryInfoForm.value.visibility
    };
    this.router.navigateByUrl('itinerary/create-itinerary/destinations');
  }
}
```

### Corresponding Template (`info.component.html`):

```html
<form [formGroup]="itineraryInfoForm" (ngSubmit)="onSubmit()">
  
  <!-- Property binding for form group -->
  <input 
    type="text"
    formControlName="name"
    [ngModel]="itineraryInfo.name"
    [class.error]="!itineraryInfoForm.get('name')?.valid">
  
  <!-- Two-way binding with validation -->
  <input 
    type="date"
    formControlName="startDate"
    [ngModel]="itineraryInfo.startDate"
    [min]="today">  <!-- ✅ Property binding -->
  
  <!-- Conditional rendering -->
  <div *ngIf="!isFormValid" class="error">
    Please fill all required fields
  </div>
  
  <!-- Event binding + Property binding -->
  <button 
    type="submit" 
    [disabled]="!isFormValid"  <!-- ✅ Property binding -->
    [ngClass]="{'disabled': !isFormValid}">  <!-- ✅ Dynamic class -->
    Next Step
  </button>
  
</form>
```

---

## 📝 Interview Questions & Answers

### Q1: What are the 4 types of data binding in Angular?

**Answer:**
1. **Interpolation**: `{{ value }}` - Display data
2. **Property Binding**: `[property]="value"` - Set element properties
3. **Event Binding**: `(event)="handler()"` - Handle user actions
4. **Two-way Binding**: `[(ngModel)]="value"` - Bidirectional data flow

### Q2: What's the difference between `*ngIf` and `[hidden]`?

**Answer:**
- **`*ngIf`**: Removes element from DOM (better for performance)
- **`[hidden]`**: Hides element with CSS (element still in DOM)

```html
<!-- Element not in DOM when false -->
<div *ngIf="show">Content</div>

<!-- Element in DOM but hidden -->
<div [hidden]="!show">Content</div>
```

**Use `*ngIf`** when element is rarely shown (better performance).

### Q3: What is `trackBy` in `*ngFor` and why use it?

**Answer:**
- Helps Angular identify items in a list
- **Performance optimization** - Angular doesn't recreate DOM elements unnecessarily

```typescript
trackByFn(index: number, item: any): any {
  return item.id; // Unique identifier
}
```

```html
<div *ngFor="let item of items; trackBy: trackByFn">
  {{ item.name }}
</div>
```

### Q4: What's the difference between `(click)` and `(ngSubmit)`?

**Answer:**
- **`(click)`**: Fires on any click
- **`(ngSubmit)`**: Fires when form is submitted (Enter key or submit button)

```html
<!-- Click event -->
<button (click)="handleClick()">Click</button>

<!-- Form submit event -->
<form (ngSubmit)="handleSubmit()">
  <button type="submit">Submit</button>
</form>
```

### Q5: Explain two-way binding. What's happening under the hood?

**Answer:**
- `[(ngModel)]` is **syntactic sugar** for:
  - Property binding: `[ngModel]="value"`
  - Event binding: `(ngModelChange)="value = $event"`

```html
<!-- These are equivalent -->
<input [(ngModel)]="name">
<input [ngModel]="name" (ngModelChange)="name = $event">
```

---

## 🎯 Practice Exercise

Look at your `info.component.html` and identify:
1. All interpolation bindings `{{ }}`
2. All property bindings `[ ]`
3. All event bindings `( )`
4. All structural directives `*ngIf`, `*ngFor`

---

## ✅ Key Takeaways

1. **Interpolation** = Display data: `{{ value }}`
2. **Property Binding** = Set properties: `[property]="value"`
3. **Event Binding** = Handle events: `(event)="handler()"`
4. **Two-way Binding** = Both ways: `[(ngModel)]="value"`
5. **`*ngIf`** = Conditionally render (removes from DOM)
6. **`*ngFor`** = Loop through arrays
7. **`[ngClass]`** = Dynamic CSS classes
8. **`[ngStyle]`** = Dynamic inline styles

---

**Next**: [Part 3 - Services & Dependency Injection](./03-SERVICES-DEPENDENCY-INJECTION.md)
