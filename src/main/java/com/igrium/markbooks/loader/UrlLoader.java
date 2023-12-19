package com.igrium.markbooks.loader;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class UrlLoader {
    public static String loadString(URL url) throws IOException {
        try(InputStream in = new BufferedInputStream(url.openStream())) {
            byte[] bytes = in.readAllBytes();
            return new String(bytes);
        }
    }
}
