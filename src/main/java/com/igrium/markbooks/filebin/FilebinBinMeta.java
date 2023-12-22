package com.igrium.markbooks.filebin;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;

public class FilebinBinMeta {
    
    public static class FilebinFileMeta {
        public String filename = "";

        @SerializedName("content-type")
        public String contentType;

        public long bytes;

        @SerializedName("bytes_readable")
        public String bytesReadable;

        public String md5;

        public String sha256;

        @SerializedName("updated_at")
        public String updatedAt;

        @SerializedName("updated_at_relative")
        public String updatedAtRelative;

        @SerializedName("created_at")
        public String createdAt;

        @SerializedName("created_at_relative")
        public String createdAtRelative;
    }

    public JsonObject bin = new JsonObject();
    public List<FilebinFileMeta> files = new ArrayList<>();

    // For gson
    public FilebinBinMeta() {};
}
