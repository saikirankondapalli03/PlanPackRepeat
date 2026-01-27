# 💡 Component Communication - Complete Examples

## Example 1: Parent → Child with @Input

### Child Component (child.component.ts)
```typescript
import { Component, Input } from '@angular/core';

@Component({
  selector: 'app-child',
  template: `
    <div>
      <h3>Child Component</h3>
      <p>Message from parent: {{ messageFromParent }}</p>
      <p>Age: {{ age }}</p>
    </div>
  `
})
export class ChildComponent {
  @Input() messageFromParent: string;  // Parent can give me this
  @Input() age: number;                 // Parent can give me this too
}
```

### Parent Component (parent.component.ts)
```typescript
import { Component } from '@angular/core';

@Component({
  selector: 'app-parent',
  template: `
    <div>
      <h2>Parent Component</h2>
      <app-child 
        [messageFromParent]="parentMessage"
        [age]="parentAge">
      </app-child>
    </div>
  `
})
export class ParentComponent {
  parentMessage = "Hello from parent!";
  parentAge = 25;
}
```

**What happens:**
- Parent has `parentMessage = "Hello from parent!"`
- Parent sends it to child: `[messageFromParent]="parentMessage"`
- Child receives it: `@Input() messageFromParent`
- Child displays it: `{{ messageFromParent }}`

---

## Example 2: Child → Parent with @Output

### Child Component (child.component.ts)
```typescript
import { Component, Output, EventEmitter } from '@angular/core';

@Component({
  selector: 'app-child',
  template: `
    <div>
      <h3>Child Component</h3>
      <button (click)="sendMessageToParent()">Click to tell parent!</button>
    </div>
  `
})
export class ChildComponent {
  @Output() childSaidSomething = new EventEmitter<string>();

  sendMessageToParent() {
    this.childSaidSomething.emit("Hello parent! I was clicked!");
  }
}
```

### Parent Component (parent.component.ts)
```typescript
import { Component } from '@angular/core';

@Component({
  selector: 'app-parent',
  template: `
    <div>
      <h2>Parent Component</h2>
      <p>Message from child: {{ messageFromChild }}</p>
      <app-child 
        (childSaidSomething)="handleChildMessage($event)">
      </app-child>
    </div>
  `
})
export class ParentComponent {
  messageFromChild: string = "";

  handleChildMessage(message: string) {
    this.messageFromChild = message;
    console.log("Child said:", message);
  }
}
```

**What happens:**
- Child has button that calls `sendMessageToParent()`
- Child emits event: `this.childSaidSomething.emit("Hello parent!")`
- Parent listens: `(childSaidSomething)="handleChildMessage($event)"`
- Parent receives: `handleChildMessage(message)` gets called
- Parent updates: `this.messageFromChild = message`

---

## Example 3: Service Communication (Any → Any)

### Service (data.service.ts)
```typescript
import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class DataService {
  // Private subject - the actual mailbox
  private dataSubject = new BehaviorSubject<string>('Initial value');
  
  // Public observable - what components subscribe to
  data$: Observable<string> = this.dataSubject.asObservable();

  // Method to update the data
  updateData(newValue: string) {
    this.dataSubject.next(newValue);
  }
}
```

### Component A (component-a.component.ts) - Sends data
```typescript
import { Component } from '@angular/core';
import { DataService } from './data.service';

@Component({
  selector: 'app-component-a',
  template: `
    <div>
      <h3>Component A</h3>
      <button (click)="sendData()">Send Data to Service</button>
    </div>
  `
})
export class ComponentA {
  constructor(private dataService: DataService) {}

  sendData() {
    this.dataService.updateData('Hello from Component A!');
  }
}
```

### Component B (component-b.component.ts) - Receives data
```typescript
import { Component, OnInit, OnDestroy } from '@angular/core';
import { Subscription } from 'rxjs';
import { DataService } from './data.service';

@Component({
  selector: 'app-component-b',
  template: `
    <div>
      <h3>Component B</h3>
      <p>Data from service: {{ receivedData }}</p>
    </div>
  `
})
export class ComponentB implements OnInit, OnDestroy {
  receivedData: string = '';
  private subscription = new Subscription();

  constructor(private dataService: DataService) {}

  ngOnInit() {
    // Subscribe to service - listen for changes
    this.subscription.add(
      this.dataService.data$.subscribe(data => {
        this.receivedData = data;
        console.log('Component B received:', data);
      })
    );
  }

  ngOnDestroy() {
    // IMPORTANT: Always unsubscribe!
    this.subscription.unsubscribe();
  }
}
```

**What happens:**
- Component A calls `dataService.updateData("Hello")`
- Service updates: `this.dataSubject.next("Hello")`
- Component B's subscription receives: `data => { this.receivedData = data }`
- Component B displays the new value

---

## Example 4: Complete Parent-Child with Both Directions

### Child Component (counter-child.component.ts)
```typescript
import { Component, Input, Output, EventEmitter } from '@angular/core';

@Component({
  selector: 'app-counter-child',
  template: `
    <div>
      <h3>Child Counter</h3>
      <p>Parent gave me this number: {{ parentNumber }}</p>
      <button (click)="increment()">Increment</button>
      <button (click)="decrement()">Decrement</button>
    </div>
  `
})
export class CounterChildComponent {
  @Input() parentNumber: number;  // Receive from parent
  @Output() numberChanged = new EventEmitter<number>();  // Send to parent

  increment() {
    const newValue = this.parentNumber + 1;
    this.numberChanged.emit(newValue);  // Tell parent the new value
  }

  decrement() {
    const newValue = this.parentNumber - 1;
    this.numberChanged.emit(newValue);  // Tell parent the new value
  }
}
```

### Parent Component (counter-parent.component.ts)
```typescript
import { Component } from '@angular/core';

@Component({
  selector: 'app-counter-parent',
  template: `
    <div>
      <h2>Parent Counter</h2>
      <p>Current count: {{ count }}</p>
      <app-counter-child 
        [parentNumber]="count"
        (numberChanged)="updateCount($event)">
      </app-counter-child>
    </div>
  `
})
export class CounterParentComponent {
  count: number = 0;

  updateCount(newCount: number) {
    this.count = newCount;  // Update when child tells us
  }
}
```

**What happens:**
1. Parent has `count = 0`
2. Parent sends to child: `[parentNumber]="count"` (child receives 0)
3. Child displays: `{{ parentNumber }}` (shows 0)
4. User clicks "Increment" in child
5. Child calculates: `newValue = 0 + 1 = 1`
6. Child emits: `this.numberChanged.emit(1)`
7. Parent receives: `(numberChanged)="updateCount($event)"` calls `updateCount(1)`
8. Parent updates: `this.count = 1`
9. Parent sends new value back: `[parentNumber]="count"` (now child receives 1)
10. Child displays: `{{ parentNumber }}` (now shows 1)

---

## 🎯 Real Example from Your Codebase

### Your Uploader Component (uploader.component.ts)
```typescript
@Input() renderParentContent: boolean;  // Receives from parent
@Output() completedUploadEvent = new EventEmitter<Array<string>>();  // Sends to parent

// When upload finishes:
this.completedUploadEvent.emit(this.selectedFilesSource);
```

### Parent Using Uploader (budget.component.html)
```html
<app-uploader
  (completedUploadEvent)="addPicturesSource($event)"
></app-uploader>
```

**Flow:**
1. Uploader finishes uploading files
2. Uploader emits: `completedUploadEvent.emit(fileUrls)`
3. Parent receives: `(completedUploadEvent)="addPicturesSource($event)"`
4. Parent's `addPicturesSource()` function gets called with the file URLs
5. Parent can now use those URLs (e.g., save them to itinerary)

---

## 🔑 Key Takeaways

1. **@Input**: Parent → Child (one-way, parent controls)
2. **@Output**: Child → Parent (one-way, child initiates)
3. **Service**: Any → Any (two-way, shared state)
4. **Always unsubscribe** from service subscriptions!

---

## 🧪 Try It Yourself

Create these files and see how they work together:

1. Create a simple `parent.component.ts` and `child.component.ts`
2. Use `@Input` to pass a message from parent to child
3. Use `@Output` to send a button click from child to parent
4. Create a service to share data between them

This will help you understand the concepts better!
