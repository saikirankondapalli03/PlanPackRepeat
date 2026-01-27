# RxJS Observables ↔ Java Reactive Programming — Correlation Guide

A detailed side‑by‑side comparison for developers coming from **Java reactive** (RxJava, Project Reactor, `java.util.concurrent.Flow`) to **RxJS** in Angular. No new concepts—just mapping what you already know.

---

## 1. Same Family: Reactive Extensions (Rx)

**RxJS** (JavaScript/TypeScript) and **RxJava** (Java) both come from **Reactive Extensions (Rx)**. The core idea is identical:

- A **producer** pushes values over time.
- A **consumer** subscribes and reacts.
- You **subscribe** and get a **Subscription** (or `Disposable`) to **unsubscribe** / cancel.

So: **event‑driven**, **asynchronous**, **publish → subscribe → Subscription** — same model as reactive Java.

---

## 2. Core Idea: Stream + Subscribe

**Java (RxJava)** — `Observable` = stream; you `subscribe` and get a `Disposable`:

```java
// RxJava
Observable.just(1, 2, 3)
    .subscribe(
        value -> System.out.println("Next: " + value),
        err -> System.err.println("Error: " + err),
        () -> System.out.println("Complete")
    );
```

**RxJS (TypeScript)** — same idea:

```typescript
// RxJS
import { of } from 'rxjs';

of(1, 2, 3).subscribe({
  next: (value) => console.log('Next:', value),
  error: (err) => console.error('Error:', err),
  complete: () => console.log('Complete')
});
```

**Java (Project Reactor)** — `Flux` instead of `Observable`:

```java
// Reactor - Flux
import reactor.core.publisher.Flux;

Flux.just(1, 2, 3)
    .subscribe(
        value -> System.out.println("Next: " + value),
        err -> System.err.println("Error: " + err),
        () -> System.out.println("Complete")
    );
```

Same flow: **source** → **subscribe** → **next / error / complete**.

---

## 3. Subscription / Cancellation

**Java (RxJava)** — `Disposable` lets you stop receiving and clean up:

```java
Disposable sub = Observable.interval(1, TimeUnit.SECONDS)
    .subscribe(n -> System.out.println(n));

// Later: cancel
sub.dispose();
```

**Java (Reactor)** — same with `Disposable`:

```java
Disposable sub = Flux.interval(Duration.ofSeconds(1))
    .subscribe(n -> System.out.println(n));

sub.dispose();
```

**RxJS** — `Subscription`:

```typescript
import { interval } from 'rxjs';

const sub = interval(1000).subscribe((n) => console.log(n));

sub.unsubscribe();
```

So: **subscribe** returns a handle; you **dispose** / **unsubscribe** to cancel. Same as “subscription” in reactive Java.

---

## 4. Creating Streams

| What | Java (RxJava) | Java (Reactor) | RxJS |
|------|----------------|----------------|------|
| Fixed values | `Observable.just(1,2,3)` | `Flux.just(1,2,3)` | `of(1,2,3)` |
| From array/list | `Observable.fromIterable(list)` | `Flux.fromIterable(list)` | `from([1,2,3])` |
| Interval | `Observable.interval(1, SECONDS)` | `Flux.interval(Duration.ofSeconds(1))` | `interval(1000)` |
| Single async | `Observable.fromFuture(future)` | `Mono.fromFuture(future)` | `from(promise)` |
| Custom stream | `Observable.create(emitter -> {...})` | `Flux.create(sink -> {...})` | `new Observable(subscriber => {...})` |

Conceptually identical: you’re defining a **source** that emits over time.

---

## 5. Operators: `map`, `filter`, `flatMap`

**Java (RxJava)**:

```java
Observable.just(1, 2, 3, 4, 5)
    .filter(x -> x % 2 == 0)
    .map(x -> x * 10)
    .subscribe(System.out::println);  // 20, 40
```

**Java (Reactor)**:

```java
Flux.just(1, 2, 3, 4, 5)
    .filter(x -> x % 2 == 0)
    .map(x -> x * 10)
    .subscribe(System.out::println);  // 20, 40
```

**RxJS** — use `pipe()` to chain operators:

```typescript
import { of } from 'rxjs';
import { filter, map } from 'rxjs/operators';

of(1, 2, 3, 4, 5).pipe(
  filter((x) => x % 2 === 0),
  map((x) => x * 10)
).subscribe((v) => console.log(v));  // 20, 40
```

Same semantics: **filter** → **map** → **subscribe**.

---

## 6. `flatMap` / Async Per Element

**Java (RxJava)** — flatten inner `Observable`s:

```java
Observable.just("a", "b", "c")
    .flatMap(s -> Observable.just(s.toUpperCase(), s + "!")
                          .delay(100, TimeUnit.MILLISECONDS))
    .subscribe(System.out::println);
```

**Java (Reactor)** — same with `Flux`:

```java
Flux.just("a", "b", "c")
    .flatMap(s -> Flux.just(s.toUpperCase(), s + "!")
                      .delayElements(Duration.ofMillis(100)))
    .subscribe(System.out::println);
```

**RxJS** — `mergeMap` (flatMap) / `switchMap` / `concatMap`:

```typescript
import { from, of } from 'rxjs';
import { mergeMap, delay } from 'rxjs/operators';

from(['a', 'b', 'c']).pipe(
  mergeMap((s) => of(s.toUpperCase(), s + '!').pipe(delay(100)))
).subscribe((v) => console.log(v));
```

“For each element, produce another stream and flatten” — same idea as Java’s `flatMap`.

---

## 7. HTTP / Async Single Value

**Java (Spring WebClient, Reactor)** — one response = `Mono`:

```java
Mono<User> user = webClient.get()
    .uri("/api/user/1")
    .retrieve()
    .bodyToMono(User.class);

user.subscribe(u -> System.out.println(u.getName()));
```

**RxJS (Angular `HttpClient`)** — one response = `Observable` (often single‑value):

```typescript
this.http.get<User>('/api/user/1').subscribe(
  (u) => console.log(u.name)
);
```

`Mono` ≈ “Observable that emits at most one value.”

---

## 8. Subjects / Multicast (Hot Stream)

**Java (RxJava)** — `Subject` = both Observable and Observer; many subscribers share same stream:

```java
Subject<Integer> subject = PublishSubject.create();

subject.subscribe(v -> System.out.println("A: " + v));
subject.subscribe(v -> System.out.println("B: " + v));

subject.onNext(1);  // A: 1, B: 1
subject.onNext(2);  // A: 2, B: 2
subject.onComplete();
```

**RxJS** — `Subject`:

```typescript
import { Subject } from 'rxjs';

const subject = new Subject<number>();

subject.subscribe((v) => console.log('A:', v));
subject.subscribe((v) => console.log('B:', v));

subject.next(1);   // A: 1, B: 1
subject.next(2);   // A: 2, B: 2
subject.complete();
```

Same idea: **publish** (`onNext` / `next`) and **subscribe**; multiple subscribers see the same events.

---

## 9. Cold vs Hot

**Cold** — each subscriber triggers its own “run” (e.g. new HTTP call):

```java
// Reactor: each subscribe() = new request
Flux<String> cold = Flux.defer(() ->
    webClient.get().uri("/api/data").retrieve().bodyToFlux(String.class));
cold.subscribe(...);  // request 1
cold.subscribe(...);  // request 2
```

```typescript
// RxJS: same
const cold = this.http.get('/api/data');
cold.subscribe(...);  // request 1
cold.subscribe(...);  // request 2
```

**Hot** — one shared stream; subscribers join late and see what’s emitted from then on. In both Java and RxJS you use **Subjects** or **connectable** operators (`publish()`, `share()`, etc.) to get that.

---

## 10. Error Handling

**Java (Reactor)**:

```java
Flux.just(1, 2, 3)
    .map(i -> { if (i == 2) throw new RuntimeException("oops"); return i; })
    .onErrorResume(e -> Flux.just(-1))
    .subscribe(System.out::println);  // 1, -1
```

**RxJS**:

```typescript
import { of } from 'rxjs';
import { map, catchError } from 'rxjs/operators';

of(1, 2, 3).pipe(
  map((i) => { if (i === 2) throw new Error('oops'); return i; }),
  catchError((e) => of(-1))
).subscribe((v) => console.log(v));  // 1, -1
```

`onErrorResume` ≈ `catchError`: handle error and continue with another stream.

---

## 11. Combining Streams: `zip`, `merge`, `combineLatest`

**Java (Reactor)** — `zip` (pair items by index):

```java
Flux<Integer> a = Flux.just(1, 2, 3);
Flux<String> b = Flux.just("a", "b", "c");

Flux.zip(a, b)
    .map(t -> t.getT1() + "-" + t.getT2())
    .subscribe(System.out::println);  // 1-a, 2-b, 3-c
```

**RxJS**:

```typescript
import { zip, of } from 'rxjs';
import { map } from 'rxjs/operators';

zip(of(1, 2, 3), of('a', 'b', 'c')).pipe(
  map(([n, s]) => `${n}-${s}`)
).subscribe((v) => console.log(v));  // 1-a, 2-b, 3-c
```

**`combineLatest`** — each emission from any source triggers with latest from all:

```java
// Reactor
Flux.combineLatest(
    Flux.interval(Duration.ofMillis(100)).take(3),
    Flux.interval(Duration.ofMillis(150)).take(3),
    (a, b) -> a + ", " + b
).subscribe(System.out::println);
```

```typescript
// RxJS
import { combineLatest, interval } from 'rxjs';
import { take, map } from 'rxjs/operators';

combineLatest([
  interval(100).pipe(take(3)),
  interval(150).pipe(take(3))
]).pipe(
  map(([a, b]) => `${a}, ${b}`)
).subscribe((v) => console.log(v));
```

**Merge** = interleave emissions. Same idea in both: `Flux.merge(a, b)` ↔ `merge(obsA, obsB)`.

---

## 12. `debounceTime` / `throttleTime` (Rate Limiting)

**Java (RxJava)**:

```java
Observable<String> search = searchInputSubject
    .debounce(300, TimeUnit.MILLISECONDS)
    .distinctUntilChanged();

search.subscribe(term -> callSearchApi(term));
```

**RxJS** (e.g. search box):

```typescript
import { debounceTime, distinctUntilChanged } from 'rxjs/operators';

searchInput$.pipe(
  debounceTime(300),
  distinctUntilChanged()
).subscribe((term) => callSearchApi(term));
```

**Throttle** = “at most one per window.” Reactor: `sample(Duration)`; RxJS: `throttleTime(ms)`.

---

## 13. `switchMap` vs `mergeMap` vs `concatMap`

All “map each value to an inner stream, then flatten.” The difference is how inner streams are handled when the outer keeps emitting.

| Operator    | Behavior |
|------------|----------|
| **mergeMap** | Start all inner streams, emit as they arrive (interleaved). |
| **concatMap** | Run inner streams one after another, in order. |
| **switchMap** | When outer emits, cancel previous inner, start new one. |

**Java (Reactor)** — `switchMap` for type‑ahead search:

```java
Flux<String> queries = Flux.just("re", "react", "reactor");
queries
    .delayElements(Duration.ofMillis(100))
    .switchMap(q -> searchApi(q).delayElements(Duration.ofMillis(200)))
    .subscribe(...);  // only "reactor" result may matter
```

**RxJS**:

```typescript
import { from, of } from 'rxjs';
import { concatMap, delay, switchMap } from 'rxjs/operators';

const queries$ = from(['re', 'react', 'reactor']).pipe(
  concatMap((q, i) => of(q).pipe(delay(100 * (i + 1))))
);
queries$.pipe(
  switchMap((q) => searchApi(q).pipe(delay(200)))
).subscribe(...);
```

`mergeMap` ≈ `flatMap` in Reactor; `concatMap` ≈ `concatMap`; `switchMap` ≈ `switchMap`.

---

## 14. `shareReplay` — Multicast + Replay Last N

**Java (Reactor)** — cache and replay:

```java
Flux<String> shared = callApiOnce()
    .cache(Duration.ofMinutes(5));  // or cache(1) for replay 1

shared.subscribe(...);  // triggers request
shared.subscribe(...);  // reuse cached
```

**RxJS** — `shareReplay(1)` = one shared execution, late subscribers get last value:

```typescript
import { shareReplay } from 'rxjs/operators';

const user$ = this.http.get<User>('/api/me').pipe(
  shareReplay(1)
);
user$.subscribe(...);  // HTTP call
user$.subscribe(...);  // no extra call, gets same result
```

Useful for “load once, share everywhere” (e.g. current user, config).

---

## 15. Unsubscribe / Cleanup in Angular

Subscriptions must be cleaned up when the component is destroyed to avoid leaks.

**Java (Spring / Reactor)** — typically cancel on context shutdown:

```java
Disposable sub = flux.subscribe(...);
// later, e.g. in @PreDestroy or request cleanup:
sub.dispose();
```

**RxJS (Angular)** — manual:

```typescript
import { Subscription } from 'rxjs';

private sub?: Subscription;

ngOnInit() {
  this.sub = this.myService.getData().subscribe((d) => this.data = d);
}

ngOnDestroy() {
  this.sub?.unsubscribe();
}
```

**Using `takeUntil(destroy$)`** — like taking until a “destroy” signal:

```typescript
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';

private destroy$ = new Subject<void>();

ngOnInit() {
  this.myService.getData()
    .pipe(takeUntil(this.destroy$))
    .subscribe((d) => this.data = d);
}

ngOnDestroy() {
  this.destroy$.next();
  this.destroy$.complete();
}
```

**AsyncPipe** (Angular) subscribes and unsubscribes for you:

```html
<div>{{ data$ | async }}</div>
```

```typescript
data$ = this.myService.getData();
```

No manual `unsubscribe` needed for that template subscription.

---

## 16. Single‑Value Async: `Mono` vs `Observable`

**Java** — `Mono<T>` = 0 or 1 value; `Flux<T>` = 0..N.

**RxJS** — only `Observable`. Single‑value is a convention:

- `first()` / `take(1)` to enforce “only first.”
- `HttpClient.get()` returns `Observable<T>` that emits once then completes.

```java
Mono<User> u = userRepo.findById(1);
```

```typescript
const u$ = this.http.get<User>('/api/user/1');  // emits once
```

---

## 17. Summary Mapping

| Concept | Java (RxJava / Reactor) | RxJS |
|--------|--------------------------|------|
| Stream | `Observable` / `Flux` / `Mono` | `Observable` |
| Subscribe | `subscribe(Consumer...)` | `subscribe({ next, error, complete })` |
| Cancel | `dispose()` | `unsubscribe()` |
| Emit | `onNext` (internal) | `next` (in Observer) |
| Operators | `map`, `filter`, `flatMap`, etc. | `pipe(map(...), filter(...), mergeMap(...))` |
| Multicast | `Subject`, `ConnectableObservable` | `Subject`, `share()`, `publish()` |
| Single async | `Mono` / `Single` | `Observable` (single‑value use) |

---

## 18. Quick Reference Cheat Sheet

| Concept | Java (Reactor/RxJava) | RxJS |
|--------|------------------------|------|
| Create from values | `Flux.just`, `Mono.just` | `of()` |
| Create from iterable | `Flux.fromIterable` | `from([])` |
| Interval | `Flux.interval` | `interval(ms)` |
| Map | `map` | `map` |
| Filter | `filter` | `filter` |
| Flatten | `flatMap`, `concatMap`, `switchMap` | `mergeMap`, `concatMap`, `switchMap` |
| Combine | `zip`, `merge`, `combineLatest` | `zip`, `merge`, `combineLatest` |
| Debounce | `debounce` (RxJava) | `debounceTime` |
| Throttle | `sample` (Reactor) | `throttleTime` |
| Catch error | `onErrorResume`, `onErrorReturn` | `catchError`, `retry` |
| Cancel | `dispose()` | `unsubscribe()` |
| Multicast + replay | `cache`, `replay` | `shareReplay` |
| Take until signal | `takeUntilOther` | `takeUntil` |

---

## Bottom Line

RxJS and Java reactive (Reactor/RxJava) use the **same event‑driven, publish–subscribe, stream-based model**. The flows (create → transform → subscribe → dispose) and the roles of **Publisher/Observable**, **Subscriber/Observer**, and **Subscription** map almost 1:1. The main differences are API names and that RxJS uses `pipe()` for operator chains. Your Java reactive mental model transfers directly to RxJS.

---

*Complements [Part 4: HTTP & Observables](./04-HTTP-OBSERVABLES.md).*
