package com.igrium.markbooks.loader;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import com.igrium.markbooks.util.FutureUtils;

import net.minecraft.util.Util;

/**
 * Loads a book from a specific source.
 */
public interface BookLoader {

    /**
     * Load this book.
     * @return Book markdown contents.
     * @throws IOException If an IO exception occurs loading the book.
     */
    public String load() throws IOException;

    public static CompletableFuture<String> loadAsync(BookLoader loader, Executor executor) {
        return FutureUtils.supplyAsync(loader::load, executor);
    }

    public static CompletableFuture<String> loadAsync(BookLoader loader) {
        return FutureUtils.supplyAsync(loader::load, Util.getIoWorkerExecutor());
    }
}
