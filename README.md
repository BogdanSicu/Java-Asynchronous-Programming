# Java-Asynchronous-Programming

tutorial link: https://www.youtube.com/watch?v=JcPezFvtLuE&list=PL40GtTTY6_-kmdeUznc1XeaTAb3zLlO6K&index=3

helping resource: https://medium.com/@anil.java.story/list/asynchronous-programming-in-java-76095ff1c0d6


Yes. **`CompletableFuture` can use multithreading, but it is not the same thing as multithreading itself.**

A useful way to think about it is:

* **Multithreading** is the general concept of running multiple threads concurrently.
* **`CompletableFuture`** is a high-level API for asynchronous programming. It lets you describe tasks and how they depend on each other, while the underlying execution may use one or more threads.

## Traditional multithreading

Before `CompletableFuture`, you typically created threads directly or used an `ExecutorService`.

Example with a thread:

```java
Thread thread = new Thread(() -> {
    System.out.println("Running in: " + Thread.currentThread().getName());
});

thread.start();
```

Or using an `ExecutorService`:

```java
ExecutorService executor = Executors.newFixedThreadPool(4);

executor.submit(() -> {
    System.out.println("Running task");
});

executor.shutdown();
```

Here, **you are managing the threads** (or at least the thread pool).

---

## CompletableFuture

`CompletableFuture` lets you focus on **what should happen**, rather than how the threads are managed.

Example:

```java
CompletableFuture<String> future =
    CompletableFuture.supplyAsync(() -> {
        return "Hello";
    });

System.out.println(future.join());
```

The task runs asynchronously, usually in the **ForkJoinPool.commonPool()** unless you provide your own executor.

---

## Is CompletableFuture multithreading?

**Often yes, but not always.**

For example:

```java
CompletableFuture<String> future =
    CompletableFuture.completedFuture("Hello");

future.thenApply(s -> s + " World");
```

This doesn't create another thread. Everything happens immediately in the current thread.

Another example:

```java
CompletableFuture.supplyAsync(() -> {
    // Runs on another thread
    return 10;
});
```

Here another thread is used.

So:

* `completedFuture()` → no extra thread
* `thenApply()` → often runs on the same thread that completed the previous stage
* `thenApplyAsync()` → explicitly schedules the next stage on an executor (another thread)

---

## thenApply vs thenApplyAsync

### thenApply

```java
CompletableFuture<Integer> future =
    CompletableFuture.supplyAsync(() -> 5)
        .thenApply(x -> x * 2);
```

Possible execution:

```
Thread-1:
    supplyAsync()
    thenApply()
```

The continuation may execute on the same thread that completed the previous stage.

---

### thenApplyAsync

```java
CompletableFuture<Integer> future =
    CompletableFuture.supplyAsync(() -> 5)
        .thenApplyAsync(x -> x * 2);
```

Possible execution:

```
Thread-1:
    supplyAsync()

Thread-2:
    thenApplyAsync()
```

The second stage is submitted to an executor.

---

## Why use CompletableFuture?

Imagine you need to:

1. Call a user service.
2. Call an order service.
3. Combine the results.
4. Save them.
5. Handle errors.

With threads, you'd need synchronization, waiting (`join()`), shared variables, and error handling.

With `CompletableFuture`:

```java
CompletableFuture<User> user =
    CompletableFuture.supplyAsync(() -> getUser());

CompletableFuture<List<Order>> orders =
    CompletableFuture.supplyAsync(() -> getOrders());

CompletableFuture<Result> result =
    user.thenCombine(orders, (u, o) ->
        new Result(u, o)
    );

Result r = result.join();
```

The code clearly expresses that:

* these two operations can run in parallel,
* combine them when both finish,
* then continue.

---

## Thread management comparison

### Using Threads

```java
Thread t1 = new Thread(() -> downloadFile());
Thread t2 = new Thread(() -> processData());

t1.start();
t2.start();

t1.join();
t2.join();
```

You are responsible for:

* creating threads,
* starting them,
* waiting for them,
* handling exceptions,
* coordinating their work.

---

### Using CompletableFuture

```java
CompletableFuture<Void> f1 =
    CompletableFuture.runAsync(() -> downloadFile());

CompletableFuture<Void> f2 =
    CompletableFuture.runAsync(() -> processData());

CompletableFuture.allOf(f1, f2).join();
```

No explicit thread management is needed.

---

## Relationship with ExecutorService

`CompletableFuture` doesn't create threads by itself. It delegates execution to an executor.

By default:

```java
CompletableFuture.supplyAsync(task);
```

uses

```java
ForkJoinPool.commonPool()
```

Or you can provide your own executor:

```java
ExecutorService executor =
    Executors.newFixedThreadPool(8);

CompletableFuture<String> future =
    CompletableFuture.supplyAsync(() -> {
        return "Hello";
    }, executor);
```

This is often recommended in production applications because it gives you control over the number of threads and avoids overloading the shared common pool.

---

## Key differences

| Traditional Multithreading                | CompletableFuture                                                              |
| ----------------------------------------- | ------------------------------------------------------------------------------ |
| Low-level concurrency mechanism           | High-level asynchronous API                                                    |
| You manage threads or thread pools        | Usually delegates thread management to an executor                             |
| Requires synchronization for coordination | Built-in composition methods (`thenApply`, `thenCompose`, `thenCombine`, etc.) |
| More boilerplate                          | More concise and expressive                                                    |
| Harder error handling                     | Built-in exception handling (`exceptionally`, `handle`, `whenComplete`)        |
| Better for fine-grained control           | Better for asynchronous workflows                                              |

## When to use each

* Use **threads** directly only when you need very low-level control over thread behavior. In modern Java, this is relatively uncommon.
* Use **`ExecutorService`** when you want explicit control over a pool of worker threads.
* Use **`CompletableFuture`** when you're building asynchronous workflows, especially for I/O-bound operations like database queries, REST API calls, or file operations. It lets you express dependencies between tasks without manually coordinating threads.

In practice, `CompletableFuture` and multithreading are complementary: a `CompletableFuture` often runs its work on threads provided by an `ExecutorService` or the common `ForkJoinPool`, but it provides a much higher-level way to compose and manage asynchronous tasks.
