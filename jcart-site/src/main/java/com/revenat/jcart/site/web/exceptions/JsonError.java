package com.revenat.jcart.site.web.exceptions;

public class JsonError {
    private String error;

    public JsonError() {
    }

    public JsonError(String error) {
        this.error = error;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
