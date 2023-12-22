package com.igrium.markbooks.filebin;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpClient.Redirect;
import java.util.concurrent.CompletableFuture;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.igrium.markbooks.filebin.HttpException.HttpNotFoundException;

public class FilebinAPI {
    private final URI url;
    private final HttpClient httpClient;

    private int maxFileLength = 0x400000; // max file length

    private final Gson gson = new GsonBuilder().create();

    public FilebinAPI(URI url) {
        this.url = url;
        httpClient = HttpClient.newBuilder().followRedirects(Redirect.ALWAYS).build();
    }

    public FilebinAPI(String url) throws URISyntaxException {
        this(new URI(url));
    }

    public FilebinAPI(URI url, HttpClient httpClient) {
        this.url = url;
        this.httpClient = httpClient;
    }

    public HttpClient getHttpClient() {
        return httpClient;
    }

    /**
     * Get the base filebin url.
     */
    public URI getUrl() {
        return url;
    }

    public int getMaxFileLength() {
        return maxFileLength;
    }

    public void setMaxFileLength(int maxFileLength) {
        if (maxFileLength < 0) {
            throw new IllegalArgumentException("Max file length cannot be negative.");
        }
        this.maxFileLength = maxFileLength;
    }

    public URI getUrl(String bin, String filename) {
        return getUrl().resolve(bin + "/" + filename);
    }
    
    /**
     * Load the metadata of a bin on filebin.
     * @param bin Bin ID
     * @return A future that completes when the bin meta is received.
     */
    public CompletableFuture<FilebinBinMeta> getBinMeta(String bin) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url.resolve(bin))
                .GET()
                .header("accept", "application/json")
                .build();

        return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString()).thenApply(res -> {
            if (res.statusCode() >= 400) {
                if (res.statusCode() == 404) {
                    throw new HttpNotFoundException();
                } else {
                    throw new HttpException(res.statusCode());
                }
            }
            return gson.fromJson(res.body(), FilebinBinMeta.class);
        });
    }

    public CompletableFuture<String> getFileContents(String bin, String file) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(getUrl(bin, file))
                .GET()
                .build();
        
        return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString()).thenApply(res -> {
            if (res.statusCode() >= 400) {
                if (res.statusCode() == 404) {
                    throw new HttpNotFoundException();
                } else {
                    throw new HttpException(res.statusCode());
                }
            }
            return res.body();
        });
    }
}
