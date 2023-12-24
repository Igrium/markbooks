package com.igrium.markbooks.util;

import java.util.Iterator;

import org.commonmark.node.Node;

/**
 * A wrapper around {@link Node} that makes it iterable.
 */
public class IterableMarkdownNode implements Iterable<Node> {

    private final Node parent;

    public IterableMarkdownNode(Node parent) {
        this.parent = parent;
    }

    @Override
    public Iterator<Node> iterator() {
        return new NodeIterator(parent.getFirstChild());
    }

    public Node getParent() {
        return parent;
    }

    private static class NodeIterator implements Iterator<Node> {

        Node current;
        Node next;

        public NodeIterator(Node first) {
            current = first;
            next = current != null ? current.getNext() : null;
        }

        @Override
        public boolean hasNext() {
            return next != null;
        }

        @Override
        public Node next() {
            current = next;
            next = current.getNext();
            return current;
        }
        
    }

}
