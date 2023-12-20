package com.igrium.markbooks.loader;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import com.igrium.markbooks.util.FutureUtils;

public class UrlLoader {
    public static String loadString(URL url) throws IOException {
        try(InputStream in = new BufferedInputStream(url.openStream())) {
            byte[] bytes = in.readAllBytes();
            return new String(bytes);
        }
    }

    public static CompletableFuture<String> loadStringAsync(URL url, Executor executor) {
        return FutureUtils.supplyAsync(() -> loadString(url), executor);
    }
}
