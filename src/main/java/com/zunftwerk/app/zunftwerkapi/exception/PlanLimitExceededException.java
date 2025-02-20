package com.zunftwerk.app.zunftwerkapi.exception;

public class PlanLimitExceededException extends Exception {
    public PlanLimitExceededException(String message) {
        super(message);
    }
}

