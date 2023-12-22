package com.igrium.markbooks.util;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.Executor;
import java.util.function.Function;
import java.util.function.Supplier;

public class FutureUtils {
    public static interface DangerousRunnable<E extends Throwable> {
        public Runnable run() throws E;
    }

    public static interface DangerousSupplier<T, E extends Throwable> {
        public T get() throws E;
    }

    public static interface DangerousFunction<T, R, E extends Throwable> {
        public R apply(T val) throws E;
    }

    public static CompletableFuture<Void> runAsync(DangerousRunnable<?> runnable, Executor executor) {
        CompletableFuture<Void> future = new CompletableFuture<>();
        executor.execute(() -> {
            try {
                runnable.run();
                future.complete(null);
            } catch (Throwable e) {
                future.completeExceptionally(e);
            }
        });
        return future;
    }

    public static <T> CompletableFuture<T> supplyAsync(DangerousSupplier<T, ?> supplier, Executor executor) {
        CompletableFuture<T> future = new CompletableFuture<>();
        executor.execute(() -> {
            try {
                future.complete(supplier.get());
            } catch (Throwable t) {
                future.completeExceptionally(t);
            }
        });
        return future;
    }

    public static Runnable wrapDangerous(DangerousRunnable<?> dangerous) {
        return wrapDangerous(dangerous, CompletionException::new);
    }

    public static Runnable wrapDangerous(DangerousRunnable<?> dangerous, Function<? super Throwable, ? extends RuntimeException> exceptionFactory) {
        return () -> {
            try {
                dangerous.run();
            } catch (Throwable e) {
                throw exceptionFactory.apply(e);
            }
        };
    }

    public static <T> Supplier<T> wrapDangerous(DangerousSupplier<T, ?> dangerous) {
        return wrapDangerous(dangerous, CompletionException::new);
    }

    public static <T> Supplier<T> wrapDangerous(DangerousSupplier<T, ?> dangerous, Function<? super Throwable, ? extends RuntimeException> exceptionFactory) {
        return () -> {
            try {
                return dangerous.get();
            } catch (Throwable e) {
                throw exceptionFactory.apply(e);
            }
        };
    }

    public static <T, R> Function<T, R> wrapDangerous(DangerousFunction<T, R, ?> dangerous) {
        return wrapDangerous(dangerous, CompletionException::new);
    }

    public static <T, R> Function<T, R> wrapDangerous(DangerousFunction<T, R, ?> dangerous, Function<? super Throwable, ? extends RuntimeException> exceptionFactory) {
        return (val) -> {
            try {
                return dangerous.apply(val);
            } catch (Throwable e) {
                throw exceptionFactory.apply(e);
            }
        };
    }
}
