package com.igrium.markbooks.util;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class FutureUtils {
    public static interface DangerousRunnable<E extends Throwable> {
        public void run() throws E;
    }

    public static interface DangerousSupplier<T, E extends Throwable> {
        public T get() throws E;
    }

    public CompletableFuture<Void> runAsync(DangerousRunnable<?> runnable, Executor executor) {
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

    public <T> CompletableFuture<T> supplyAsync(DangerousSupplier<T, ?> supplier, Executor executor) {
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
}
