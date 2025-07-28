package com.transvoz.shared.exception;

public class TransVozException extends RuntimeException {
    public TransVozException(String message) {
        super(message);
    }

    public TransVozException(String message, Throwable cause) {
        super(message, cause);
    }
}
