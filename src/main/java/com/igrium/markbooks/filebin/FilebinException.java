package com.igrium.markbooks.filebin;

public class FilebinException extends RuntimeException {
    public FilebinException() {
        super("Error receiving file from filebin.");
    }

    public FilebinException(String message) {
        super(message);
    }

    public static class IllegalContentTypeException extends FilebinException {
        public IllegalContentTypeException() {
            super("Unrecognized content type.");
        }
        
        public IllegalContentTypeException(String message) {
            super(message);
        }

        public static IllegalContentTypeException create(String format) {
            return new IllegalContentTypeException("Unrecognized content type: " + format);
        }
    }

    public static class IllegalLengthException extends FilebinException {
        public IllegalLengthException() {
            super("The file is too long.");
        }

        public IllegalLengthException(String message) {
            super(message);
        }
    }

    public static class NoFilesException extends FilebinException {
        public NoFilesException() {
            super("No eligable files were found.");
        }
        
        public NoFilesException(String message) {
            super(message);
        }
    }
}
