package com.igrium.markbooks.loader;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class UrlBookLoader implements BookLoader {

    public static final int MAX_BYTES = 0x400000; // 4096 kibibytes

    private final URL url;

    public UrlBookLoader(URL url) {
        this.url = url;
    }

    public URL getUrl() {
        return url;
    }

    @Override
    public String load() throws IOException {
        try(InputStream in = new BufferedInputStream(url.openStream())) {
            byte[] bytes = in.readAllBytes();
            return new String(bytes);
        }
    }
    
}
