package com.hiflink.common.utils.exceptions;

public class NotImplementedYetException extends Exception{


    public NotImplementedYetException() {
        super("Not implemented yet");

    }

    public NotImplementedYetException(String message ) {
        super(message);
    }

    public NotImplementedYetException(Throwable cause ) {
        super(cause);
    }

    public NotImplementedYetException(String message, Throwable cause ) {
        super(message, cause);
    }


}