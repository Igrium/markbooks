package com.igrium.markbooks.filebin;

import java.util.concurrent.CompletableFuture;

import com.igrium.markbooks.filebin.FilebinBinMeta.FilebinFileMeta;
import com.igrium.markbooks.filebin.FilebinException.NoFilesException;
import com.igrium.markbooks.loader.BookLoader;

public class FilebinHandle implements BookLoader {
    private final String binId;
    private final FilebinAPI api;

    public FilebinHandle(String binId, FilebinAPI api) {
        this.binId = binId;
        this.api = api;
    }

    @Override
    public CompletableFuture<String> load() {
        return api.getBinMeta(binId).thenApply(meta -> {
            if (meta.files == null || meta.files.isEmpty()) {
                throw new NoFilesException();
            }
            // Search for files with .md extension first.
            for (FilebinFileMeta f : meta.files) {
                if (f.filename.endsWith(".md") && f.contentType.contains("text")) {
                    return f;
                }
            }
            for (FilebinFileMeta f : meta.files) {
                if (f.contentType.contains("text")) {
                    return f;
                }
            }
            throw new FilebinException("No eligable files were found.");
        }).thenCompose(meta -> api.getFileContents(binId, meta.filename));
    }
}
