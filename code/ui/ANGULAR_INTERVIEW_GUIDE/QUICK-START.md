# 🚀 Quick Start Guide - Angular Interview Prep

## ⚡ 30-Minute Crash Course

If you're short on time, focus on these **essential concepts**:

### 1. Components (5 min)
- Component = Class + Template + Styles
- `@Component` decorator
- `ngOnInit()` for initialization

**Your Example:**
```typescript
// app.component.ts
@Component({
  selector: 'app-root',
  templateUrl: './app.component.html'
})
export class AppComponent implements OnInit {
  constructor(private auth: AuthService) {}
  ngOnInit() { this.auth.localAuthSetup(); }
}
```

### 2. Data Binding (5 min)
- `{{ value }}` - Display data
- `[property]="value"` - Property binding
- `(event)="handler()"` - Event binding
- `[(ngModel)]="value"` - Two-way binding

### 3. Services & DI (5 min)
- Service = Business logic
- Dependency Injection = Angular provides dependencies
- `providedIn: 'root'` = Singleton

**Your Example:**
```typescript
@Injectable({ providedIn: 'root' })
export class UserService {
  constructor(private http: HttpClient) {}
  getUserInfo(email: string) {
    return this.http.get(`/api/users/${email}`);
  }
}
```

### 4. HTTP & Observables (5 min)
- HTTP returns Observables
- Must **subscribe** to execute
- **Unsubscribe** in `ngOnDestroy` (or use async pipe)

**Your Example:**
```typescript
this.userService.getUserInfo(email)
  .subscribe(
    data => console.log(data),
    error => console.error(error)
  );
```

### 5. Forms (5 min)
- Reactive Forms = `FormGroup` + `FormControl`
- Validators for validation
- `form.value` to get data

**Your Example:**
```typescript
myForm = new FormGroup({
  name: new FormControl('', Validators.required)
});
```

### 6. Routing (5 min)
- Routes map URLs to components
- `<router-outlet>` renders components
- Guards protect routes

**Your Example:**
```typescript
{
  path: 'dashboard',
  component: HomeComponent,
  canActivate: [AuthGuard]
}
```

---

## 🎯 Top 10 Interview Questions

### 1. What is a Component?
**Answer:** Class + Template + Styles that controls part of UI

### 2. What is Dependency Injection?
**Answer:** Angular automatically provides dependencies via constructor

### 3. What are Observables?
**Answer:** Streams of data over time, must subscribe to receive values

### 4. What is lazy loading?
**Answer:** Load modules on-demand for better performance

### 5. What are Route Guards?
**Answer:** Control access to routes (auth, permissions)

### 6. What is ngOnInit?
**Answer:** Lifecycle hook called once after component initialization

### 7. Why unsubscribe from Observables?
**Answer:** Prevent memory leaks

### 8. What's the difference between Subject and BehaviorSubject?
**Answer:** BehaviorSubject has initial value, Subject doesn't

### 9. What are Reactive Forms?
**Answer:** Form logic in component using FormGroup/FormControl

### 10. What is Change Detection?
**Answer:** Angular checks if data changed and updates DOM

---

## 📚 Full Study Path

1. **Day 1**: Read Parts 1-2 (Basics, Data Binding)
2. **Day 2**: Read Parts 3-4 (Services, HTTP)
3. **Day 3**: Read Parts 5-6 (Forms, Routing)
4. **Day 4**: Read Parts 7-8 (Lifecycle, Advanced)
5. **Day 5**: Review your project, practice questions

---

## ✅ Pre-Interview Checklist

- [ ] Can explain what a Component is
- [ ] Can explain Dependency Injection
- [ ] Understand Observables and subscriptions
- [ ] Know lifecycle hooks (ngOnInit, ngOnDestroy)
- [ ] Understand routing and guards
- [ ] Can explain your project structure
- [ ] Fixed critical bugs (API keys, console.logs)
- [ ] Tested application end-to-end

---

**Ready? Start with [Part 1](./01-ANGULAR-BASICS.md)!**
