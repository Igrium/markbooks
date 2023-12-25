package com.igrium.markbooks;

import java.io.Reader;
import java.net.URI;
import java.net.URISyntaxException;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.stream.JsonReader;
import com.igrium.markbooks.util.TextColorJsonAdapter;

import net.minecraft.text.TextColor;

public final class MarkBooksConfig {

    public static MarkBooksConfig get() {
        return MarkBooks.getInstance().getConfig();
    }

    public static enum LinkPermissionLevel {
        ALWAYS, ADMINS, NEVER
    }

    public final static class FormatConfig {

        // For gson
        public FormatConfig() {};

        private String ulPrefix = "• ";

        public String getUlPrefix() {
            return ulPrefix;
        }

        public void setUlPrefix(String ulPrefix) {
            this.ulPrefix = ulPrefix;
        }

        private String olPrefix = "%d. ";

        public String getOlPrefix() {
            return olPrefix;
        }

        public void setOlPrefix(String olPrefix) {
            this.olPrefix = olPrefix;
        }

        private TextColor[] headingColors = new TextColor[] { TextColor.parse("dark_red") };

        public TextColor[] getHeadingColors() {
            return headingColors;
        }

        public void setHeadingColors(TextColor[] headingColors) {
            this.headingColors = headingColors;
        }
        
        @JsonAdapter(TextColorJsonAdapter.class)
        private TextColor linkColor = TextColor.parse("blue");

        public TextColor getLinkColor() {
            return linkColor;
        }

        public void setLinkColor(TextColor linkColor) {
            this.linkColor = linkColor;
        }

        public void copyFrom(FormatConfig other) {
            ulPrefix = other.ulPrefix;
            olPrefix = other.olPrefix;

            headingColors = other.headingColors.clone();
            linkColor = other.linkColor;
        }
    }

    // For gson
    public MarkBooksConfig() {

    }

    private long maxFileLength = 0x400000; // 4096 kib

    public long getMaxFileLength() {
        return maxFileLength;
    }

    public void setMaxFileLength(long maxFileLength) {
        this.maxFileLength = maxFileLength;
    }

    private FormatConfig formatting = new FormatConfig();

    public FormatConfig formatting() {
        return formatting;
    }
    
    private LinkPermissionLevel allowLinks = LinkPermissionLevel.ADMINS;

    public LinkPermissionLevel getAllowLinks() {
        return allowLinks;
    }

    public void setAllowLinks(LinkPermissionLevel allowLinks) {
        this.allowLinks = allowLinks;
    }

    private boolean restrictUrlDownload = true;

    public boolean restrictUrlDownload() {
        return restrictUrlDownload;
    }

    public void setRestrictUrlDownload(boolean restrictUrls) {
        this.restrictUrlDownload = restrictUrls;
    }

    private boolean requireWritableBook = true;

    public boolean requireWritableBook() {
        return requireWritableBook;
    }

    public void setRequireWritableBook(boolean requireWritableBook) {
        this.requireWritableBook = requireWritableBook;
    }

    private URI filebinUrl = getUriUnchecked("https://filebin.net");

    public URI getFilebinUrl() {
        return filebinUrl;
    }

    public void setFilebinUrl(URI filebinUrl) {
        this.filebinUrl = filebinUrl;
    }

    public void copyFrom(MarkBooksConfig other) {
        this.formatting.copyFrom(other.formatting);
        maxFileLength = other.maxFileLength;
        allowLinks = other.allowLinks;
        restrictUrlDownload = other.restrictUrlDownload;
        requireWritableBook = other.requireWritableBook;
    }

    private static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .registerTypeAdapter(TextColor.class, new TextColorJsonAdapter())
            .create();

    public static String toJson(MarkBooksConfig config) {
        return GSON.toJson(config);
    }

    public static MarkBooksConfig fromJson(String json) {
        return GSON.fromJson(json, MarkBooksConfig.class);
    }

    public static MarkBooksConfig fromJson(Reader reader) {
        return GSON.fromJson(reader, MarkBooksConfig.class);
    }

    public static MarkBooksConfig fromJson(JsonReader reader) {
        return GSON.fromJson(reader, MarkBooksConfig.class);
    }

    private static URI getUriUnchecked(String uri) {
        try {
            return new URI(uri);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
}
