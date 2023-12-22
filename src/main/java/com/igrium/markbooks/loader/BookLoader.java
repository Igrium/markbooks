package com.igrium.markbooks.loader;

import java.util.concurrent.CompletableFuture;

/**
 * Loads a book from a specific source.
 */
public interface BookLoader {

    /**
     * Load this book.
     * @return Book markdown contents.
     */
    public CompletableFuture<String> load();

}
