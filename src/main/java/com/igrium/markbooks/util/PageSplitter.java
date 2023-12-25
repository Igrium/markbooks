package com.igrium.markbooks.util;

import java.util.LinkedList;

public final class PageSplitter {

    /**
     * Split a string into its lines, wrapping when necessary.
     * @param string String to split.
     * @param maxWidth Max number of chars in a line before wrapping.
     * @return All the lines from the string.
     */
    public static String[] splitLines(String string, int maxWidth) {
        String[] segments = string.split("\\R");
        LinkedList<StringBuilder> lines = new LinkedList<>();

        for (String segment : segments) {
            // Always add a new line at the beginning of a segment.
            lines.add(new StringBuilder(maxWidth));
            String[] words = segment.split(" ");

            for (String word : words) {
                int available = maxWidth - lines.getLast().length();

                // If the word is too long for a single line, break it up.
                if (word.length() > maxWidth) {
                    String[] wordLines = splitString(string, maxWidth);
                    for (String wordLine : wordLines) {
                        lines.add(new StringBuilder(wordLine));
                    }
                    lines.getLast().append(" ");
                    continue;
                }

                // Simply add to the end of the line if there's space. If not, create a new line
                // and put it there.
                if (available >= word.length()) {
                    lines.getLast().append(word);
                } else {
                    lines.add(new StringBuilder(maxWidth));
                    lines.getLast().append(word);
                }

                lines.getLast().append(" ");
            }
        }

        return lines.stream().map(b -> b.toString().stripTrailing()).toArray(String[]::new);
    }

    /**
     * Split a string into a list of segments of equal sizes, based on a set number
     * of characters per segment.
     * 
     * @param string      String to split.
     * @param charsPerStr Number of characters per segment.
     * @return The split segments.
     */
    public static String[] splitString(String string, int charsPerStr) {
        if (charsPerStr <= 0) {
            throw new IllegalArgumentException("charsPerString must be > 0");
        }
        if (charsPerStr >= string.length()) {
            return new String[] { string };
        }
        int size = ceilDiv(string.length(), charsPerStr);

        String[] list = new String[size];

        for (int i = 0; i < size; i++) {
            int start = i * charsPerStr;
            int end = Math.min((i + 1) * charsPerStr, string.length());
            list[i] = string.substring(start, end);
        }

        return list;
    }

    private static int ceilDiv(int a, int b) {
        return -Math.floorDiv(-a, b);
    }
}
