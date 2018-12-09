package com.revenat.jcart.core.exceptions;

public class NotFoundException extends RuntimeException {

    public <T> NotFoundException(Class<T> clazz, Integer id) {
        super(clazz.getSimpleName() + " with id: " + id + " does not exists!");
    }

    public NotFoundException(String message) {
        super(message);
    }
}
