# Angular Interview Guide - Part 5: Forms & Validation

## 🎯 Overview

Angular offers **two** form approaches:

| Approach | Module | Model lives in | Best for |
|----------|--------|----------------|----------|
| **Template-driven** | `FormsModule` | Template (ngModel) | Simple forms, less logic |
| **Reactive** | `ReactiveFormsModule` | Component (FormGroup) | Complex forms, validation, testing |

This guide focuses on **Reactive Forms** — the approach used in your project and commonly asked in interviews.

---

## 1️⃣ REACTIVE FORMS: FormGroup & FormControl

### What Are They?

- **FormControl** = One form field (value + validation state).
- **FormGroup** = A group of FormControls (e.g. a whole form).

You define the **model in TypeScript**; the template binds to it with `formGroup` and `formControlName`.

### Basic Setup

```typescript
import { FormGroup, FormControl, Validators } from '@angular/forms';

export class MyFormComponent {
  myForm = new FormGroup({
    name: new FormControl('', Validators.required),
    email: new FormControl('', [Validators.required, Validators.email]),
    age: new FormControl(25)
  });
}
```

```html
<form [formGroup]="myForm" (ngSubmit)="onSubmit()">
  <input formControlName="name" placeholder="Name">
  <input formControlName="email" type="email" placeholder="Email">
  <input formControlName="age" type="number" placeholder="Age">
  <button type="submit" [disabled]="!myForm.valid">Submit</button>
</form>
```

**Key bindings:**
- `[formGroup]="myForm"` — attach the FormGroup to the `<form>`.
- `formControlName="name"` — link each input to a FormControl (must be inside a form with `formGroup`).

---

## 2️⃣ MODULE SETUP

### Import ReactiveFormsModule (and FormsModule if using ngModel)

```typescript
// app.module.ts or your feature module
import { ReactiveFormsModule, FormsModule } from '@angular/forms';

@NgModule({
  imports: [
    ReactiveFormsModule,  // ✅ For formGroup, formControlName
    FormsModule           // ✅ If you use ngModel (e.g. with formControlName)
  ]
})
export class AppModule { }
```

**Your project** (`itinerary.module.ts`):

```typescript
import { ReactiveFormsModule, FormsModule } from '@angular/forms';

@NgModule({
  imports: [
    ReactiveFormsModule,
    FormsModule,
    // ...
  ]
})
```

---

## 3️⃣ YOUR PROJECT EXAMPLES

### Example 1: `info.component` — Itinerary Info Form

**Component:**

```typescript
import { FormGroup, FormControl, Validators } from '@angular/forms';

itineraryInfoForm = new FormGroup({
  name: new FormControl('', Validators.required),
  startDate: new FormControl('', Validators.required),
  endDate: new FormControl('', Validators.required),
  visibility: new FormControl('Public')
});
```

**Template:**

```html
<form [formGroup]="itineraryInfoForm" (ngSubmit)="onSubmit()">
  <input formControlName="name" placeholder="Enter itinerary name">
  <input type="date" formControlName="startDate" [min]="today">
  <input type="date" formControlName="endDate">
  <input type="radio" formControlName="visibility" value="Public"> Public
  <input type="radio" formControlName="visibility" value="Private"> Private
  <button type="submit" [disabled]="!isFormValid">Next</button>
</form>
```

### Example 2: `distinations.component` — Destinations Form

```typescript
itineraryDestinationsForm = new FormGroup({
  name: new FormControl('', Validators.required),
  streetAddress: new FormControl('', Validators.required),
  date: new FormControl('', Validators.required),
  time: new FormControl('', Validators.required),
  budget: new FormControl('', Validators.required)
});
```

---

## 4️⃣ BUILT-IN VALIDATORS

Use `Validators` from `@angular/forms`:

| Validator | Usage | Example |
|-----------|--------|---------|
| `required` | Non-empty | `Validators.required` |
| `minLength(n)` | Min string length | `Validators.minLength(3)` |
| `maxLength(n)` | Max string length | `Validators.maxLength(100)` |
| `min(n)` | Min number | `Validators.min(0)` |
| `max(n)` | Max number | `Validators.max(999)` |
| `email` | Valid email | `Validators.email` |
| `pattern(regex)` | Regex match | `Validators.pattern(/^[a-z]+$/)` |

**Multiple validators** — pass an array:

```typescript
new FormControl('', [
  Validators.required,
  Validators.minLength(3),
  Validators.maxLength(50)
]);
```

---

## 5️⃣ CUSTOM VALIDATORS

A validator is a **function** that returns `null` (valid) or an **error object** (invalid).

### Custom validator function

```typescript
import { AbstractControl, ValidationErrors, ValidatorFn } from '@angular/forms';

function noSpaces(): ValidatorFn {
  return (control: AbstractControl): ValidationErrors | null => {
    const hasSpaces = /\s/.test(control.value || '');
    return hasSpaces ? { noSpaces: { value: control.value } } : null;
  };
}

// Usage
name: new FormControl('', [Validators.required, noSpaces()])
```

### Async validator (e.g. API check)

```typescript
import { AbstractControl, AsyncValidatorFn, ValidationErrors } from '@angular/forms';
import { map, catchError, of } from 'rxjs';

function uniqueEmailValidator(userService: UserService): AsyncValidatorFn {
  return (control: AbstractControl) => {
    return userService.checkEmail(control.value).pipe(
      map(taken => (taken ? { emailTaken: true } : null)),
      catchError(() => of(null))
    );
  };
}

// Usage (3rd arg = async validators)
email: new FormControl('', Validators.required, uniqueEmailValidator(this.userService))
```

---

## 6️⃣ FORMARRAY — DYNAMIC LISTS

Use **FormArray** when you have a **variable number** of repeated controls (e.g. multiple phone numbers, addresses).

```typescript
import { FormGroup, FormControl, FormArray, Validators } from '@angular/forms';

export class MyComponent {
  form = new FormGroup({
    phones: new FormArray([
      new FormControl('', Validators.required)
    ])
  });

  get phones(): FormArray {
    return this.form.get('phones') as FormArray;
  }

  addPhone() {
    this.phones.push(new FormControl('', Validators.required));
  }

  removePhone(i: number) {
    this.phones.removeAt(i);
  }
}
```

```html
<form [formGroup]="form">
  <div formArrayName="phones">
    <div *ngFor="let c of phones.controls; let i = index">
      <input [formControlName]="i" placeholder="Phone">
      <button type="button" (click)="removePhone(i)">Remove</button>
    </div>
    <button type="button" (click)="addPhone()">Add Phone</button>
  </div>
</form>
```

**Key:** `formArrayName="phones"` + `[formControlName]="i"` (index) when the array holds **FormControl**s. Use `[formGroupName]="i"` when each item is a **FormGroup**.

---

## 7️⃣ FORM VALUE CHANGES & STATUS

### valueChanges

Emitted whenever any control value changes. **Use subscribe + unsubscribe** (or `takeUntil`).

**Your project** (`info.component`):

```typescript
ngOnInit() {
  this.subscriptions.add(
    this.itineraryInfoForm.valueChanges.subscribe((data) => {
      this.updateInput();
      this.isFormValid = this.itineraryInfoForm.valid;
    })
  );
}
```

### statusChanges

Emitted when validation status changes (`VALID`, `INVALID`, `PENDING` for async validators).

```typescript
this.myForm.statusChanges.subscribe(status => {
  console.log('Form status:', status);  // 'VALID' | 'INVALID' | 'PENDING'
});
```

### Accessing values

```typescript
this.myForm.value;           // { name: '...', email: '...' }
this.myForm.get('name').value;
this.myForm.valid;           // true | false
this.myForm.get('name').valid;
this.myForm.get('name').errors;  // { required: true } or null
```

---

## 8️⃣ SUBMISSION & DISABLING SUBMIT

### Submit handler

```typescript
onSubmit() {
  if (this.myForm.valid) {
    const data = this.myForm.value;
    // send to API, navigate, etc.
  }
}
```

### Disable submit until valid

```html
<button type="submit" [disabled]="!myForm.valid">Submit</button>
```

**Your project** (`info.component.html`):

```html
<button type="submit" [disabled]="!isFormValid">Next</button>
```

Where `isFormValid` is derived from `this.itineraryInfoForm.valid` (e.g. in `valueChanges`).

---

## 9️⃣ DISPLAYING VALIDATION ERRORS

Show errors only after the user has interacted (**touched**) or **submitted**:

```html
<input formControlName="email" type="email">
<div *ngIf="myForm.get('email').invalid && myForm.get('email').touched">
  <span *ngIf="myForm.get('email').errors?.required">Email is required.</span>
  <span *ngIf="myForm.get('email').errors?.email">Invalid email.</span>
</div>
```

Optional helper in the component:

```typescript
getControl(name: string) {
  return this.myForm.get(name);
}
```

```html
<div *ngIf="getControl('email').invalid && getControl('email').touched">
  ...
</div>
```

---

## 🔟 FORMBUILDER (SHORTER SYNTAX)

**FormBuilder** reduces repetition when creating FormGroups:

```typescript
import { FormBuilder, Validators } from '@angular/forms';

export class MyComponent {
  constructor(private fb: FormBuilder) {}

  myForm = this.fb.group({
    name: ['', Validators.required],
    email: ['', [Validators.required, Validators.email]],
    age: [25]
  });
}
```

- `['']` = default value.
- `['', Validators.required]` = default + sync validators.
- `['', Validators.required, asyncValidator]` = default + sync + async.

Same idea as `new FormGroup` / `new FormControl`, just more concise.

---

## 1️⃣1️⃣ TEMPLATE-DRIVEN VS REACTIVE (QUICK COMPARISON)

| | Template-driven | Reactive |
|--|------------------|----------|
| Model | In template (`ngModel`) | In component (`FormGroup`) |
| Validation | Template + directives | Component + `Validators` |
| Dynamic controls | Harder | FormArray, straightforward |
| Testing | More DOM-dependent | Easy to unit-test |
| Your project | Mixed (ngModel + formControlName) | Primarily Reactive |

---

## 📋 Interview Questions & Answers

### Q1: What is the difference between Template-driven and Reactive forms?

**Answer:** Template-driven forms use `ngModel` and directives; the model lives in the template. Reactive forms use `FormGroup` / `FormControl` in the component; the template binds with `formGroup` and `formControlName`. Reactive forms give more control, are easier to test, and work better for complex or dynamic forms.

### Q2: What are FormGroup, FormControl, and FormArray?

**Answer:** **FormControl** = one field (value + validation). **FormGroup** = group of controls (e.g. whole form). **FormArray** = array of controls (or groups) for dynamic lists. All extend `AbstractControl` and have `value`, `valid`, `errors`, `valueChanges`, etc.

### Q3: How do you add validation to a Reactive form?

**Answer:** Use **built-in validators** (`Validators.required`, `Validators.email`, etc.) as the second argument to `FormControl`, or **custom validators**: a function `(control) => null | ValidationErrors`. For async checks (e.g. API), use **async validators** as the third argument.

### Q4: How do you react to form value changes?

**Answer:** Subscribe to `form.valueChanges` (or `control.valueChanges`). Remember to **unsubscribe** in `ngOnDestroy` (e.g. with a `Subscription` or `takeUntil(destroy$)`).

### Q5: When would you use FormArray?

**Answer:** When the number of repeated fields is **dynamic** (e.g. add/remove phone numbers, addresses, line items). You push/remove `FormControl` or `FormGroup` in the array and use `formArrayName` + `formGroupName` / `formControlName` in the template.

---

## ✅ Key Takeaways

1. **Reactive forms** = model in component (`FormGroup` / `FormControl`).
2. **`[formGroup]`** on `<form>`, **`formControlName`** on inputs.
3. **Validators**: built-in (`required`, `email`, `minLength`, etc.) or custom (function returning `null` or `ValidationErrors`).
4. **FormArray** = dynamic list of controls; use `formArrayName` and `[formGroupName]` / `formControlName`.
5. **`valueChanges`** / **`statusChanges`** = react to updates; **unsubscribe** in `ngOnDestroy`.
6. **`form.valid`** and **`form.value`** for submit logic and disabling the submit button.
7. **FormBuilder** = shorter syntax for defining groups and controls.

---

**Next**: [Part 6 - Routing & Guards](./06-ROUTING-GUARDS.md)
