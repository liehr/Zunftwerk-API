package com.zunftwerk.app.zunftwerkapi.exception;

public class LimitReachedException extends Exception {
    public LimitReachedException(String message) {
        super(message);
    }
}
