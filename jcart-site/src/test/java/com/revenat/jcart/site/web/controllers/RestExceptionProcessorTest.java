package com.revenat.jcart.site.web.controllers;

import com.revenat.jcart.core.exceptions.NotFoundException;
import com.revenat.jcart.site.web.exceptions.JsonError;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.*;

public class RestExceptionProcessorTest {

    private static final String MESSAGE = "test message";

    private RestExceptionProcessor processor;

    @Test
    public void handlesNotFoundException() {
        processor = new RestExceptionProcessor();
        NotFoundException exception = new NotFoundException(MESSAGE);

        JsonError jsonError = processor.handleNotFound(exception);

        assertThat(jsonError.getError(), equalTo(MESSAGE));
    }
}