package com.david.bookms.exception;

public class NoBookFoundExcpetion extends RuntimeException {

    public NoBookFoundExcpetion() {
    }

    public NoBookFoundExcpetion(String message) {
        super(message);
    }
}
