package com.igrium.markbooks.util;

import java.util.LinkedList;
import java.util.List;

public final class StringUtils {

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

    public static String[] splitLines(String string, int maxWidth, int startOffset) {
        if (string.isEmpty()) return new String[0];

        string = "F".repeat(startOffset) + string;
        String[] lines = splitLines(string, maxWidth);
        lines[0] = lines[0].substring(startOffset);
        return lines;
    }

    /**
     * Identify the indices at which a string of text should contain a line break,
     * either because of auto-wrapping or a newline character.
     * 
     * @param string       Text to scan.
     * @param maxWidth     The number of characters in a line before it auto-wraps.
     * @param countNewline If <code>true</code>, returned indices are based on the
     *                     original string, meaning an index could coincide
     *                     with an existing newline character. If
     *                     <code>false</code>, returned indices are based on a
     *                     version of the string with all newline characters
     *                     removed.
     * @return An array with the indices at which line breaks should be added.
     */
    public static int[] identifyLineBreaks(String string, int maxWidth, boolean countNewline) {

        List<Integer> lineBreaks = new LinkedList<>();

        char[] array = string.toCharArray();
        int localIndex = 0;
        int i = 0;
        char c;

        // Count the number of newline chars so we can subtract it from the total.
        int newlineChars = 0;

        while (i < array.length) {
            c = array[i];

            if (c == '\n' || c == '\r') {
                lineBreaks.add(countNewline ? i : i - newlineChars);
                i++;
                newlineChars++;

                // Skip a char if this is '\r\n'
                if (i < array.length && array[i] == '\n') {
                    i++;
                    newlineChars++;
                }
                localIndex = 0;
                continue;
            }

            if (Character.isWhitespace(c)) {
                i++;
                localIndex++;
                continue;
            }

            // Scan to the end of the word.
            int wordStart = i;
            while (i < array.length) {
                c = array[i];
                if (Character.isWhitespace(c)) break;
                i++;
                localIndex++;
            }

            if (localIndex > maxWidth) {
                // Jump to new line
                lineBreaks.add(countNewline ? wordStart : wordStart - newlineChars);
                localIndex = i - wordStart;
            }

            // TODO: deal with words that are longer than the line width.

        }

        return lineBreaks.stream().mapToInt(v -> v).toArray();
    }
 
    /**
     * Identify the indices at which a string of text should contain a page break.
     * 
     * @param string       Text to scan.
     * @param maxWidth     The number of characters in a line before it auto-wraps.
     * @param maxHeight    Number of lines in a page.
     * @param heightOffset A line "offset" for the first page. The beginning of the
     *                     string is treated to have started at this index on the
     *                     first page.
     * @param countNewline Whether to include newline characters while counting
     *                     indices. See {@link #identifyLineBreaks} for details.
     * @return The indices at which page breaks should be added.
     * @see #identifyLineBreaks(String, int, boolean)
     */
    public static int[] identifyPageBreaks(String string, int maxWidth, int maxHeight, int heightOffset, boolean countNewline) {
        int[] lineBreaks = identifyLineBreaks(string, maxWidth, countNewline);
        List<Integer> pageBreaks = new LinkedList<>();

        int currentLine = heightOffset;
        for (int i = 0; i < lineBreaks.length; i++) {
            if (currentLine >= maxHeight) {
                pageBreaks.add(lineBreaks[i]);
                currentLine = 0;
            } else {
                currentLine++;
            }
        }

        return pageBreaks.stream().mapToInt(i -> i).toArray();
    }

    /**
     * Split a string on a collection of indices.
     * 
     * @param string       String to split.
     * @param splitIndices Indices to split on.
     * @return Split string parts.
     */
    public static String[] splitString(String string, int... splitIndices) {
        if (splitIndices.length == 0) {
            return new String[] { string };
        }

        String[] split = new String[splitIndices.length + 1];

        int startIndex = 0;
        for (int i = 0; i < splitIndices.length; i++) {
            split[i] = string.substring(startIndex, splitIndices[i]);
            startIndex = splitIndices[i];
        }
        split[splitIndices.length] = string.substring(splitIndices[splitIndices.length - 1]);
        return split;
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
