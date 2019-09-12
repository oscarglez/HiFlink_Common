package com.hiflink.common.utils.exceptions;

public class AvroFieldNotFoundException extends Exception {
    public AvroFieldNotFoundException() {
    }

    public AvroFieldNotFoundException(String message ) {
        super(message);
    }

    public AvroFieldNotFoundException(Throwable cause ) {
        super(cause);
    }

    public AvroFieldNotFoundException(String message, Throwable cause ) {
        super(message, cause);
    }
}
