package com.igrium.markbooks.page;

import java.util.List;

import org.commonmark.node.Document;

/**
 * Handles the seperation of markdown files into multiple pages.
 */
public interface PageSplitter {
    public List<Document> splitPages(Document doc);
}
