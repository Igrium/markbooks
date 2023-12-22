package com.igrium.markbooks.filebin;

public class HttpException extends RuntimeException {
    private final int statusCode;

    public HttpException(int statusCode) {
        super("HTTP status code " + statusCode);
        this.statusCode = statusCode;
    }

    public HttpException(int statusCode, String message) {
        super(message);
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public static class HttpNotFoundException extends HttpException {
        public HttpNotFoundException() {
            super(404, "Resource not found.");
        }

        public HttpNotFoundException(String message) {
            super(404, message);
        }
    }
}
