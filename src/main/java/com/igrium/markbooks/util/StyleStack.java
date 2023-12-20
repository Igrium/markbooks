package com.igrium.markbooks.util;

import java.util.Stack;
import java.util.function.Function;

import net.minecraft.text.Style;

public class StyleStack {
    private final Stack<Style> styles = new Stack<>();

    public StyleStack(Style root) {
        styles.add(root);
    }

    public StyleStack() {
        styles.add(Style.EMPTY);
    }

    public Style push(Function<Style, Style> modifier) {
        Style newStyle = modifier.apply(styles.peek());
        return styles.push(newStyle);
    }

    public Style peek() {
        return styles.peek();
    }

    public Style pop() {
        if (styles.size() <= 1) {
            throw new IllegalStateException("Cannot pop root style.");
        }
        return styles.pop();
    }

    public int size() {
        return styles.size();
    }
}
