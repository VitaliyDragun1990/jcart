package com.revenat.jcart.site.web.controllers;

import com.revenat.jcart.core.exceptions.JCartException;
import com.revenat.jcart.core.exceptions.NotFoundException;
import org.junit.Test;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;

public class ViewExceptionProcessorTest {

    private static final String SERVER_ERROR_VIEW_NAME = "error/500";
    private static final String NOT_FOUND_MESSAGE = "Resource can not be found.";
    private static final String NOT_FOUND_VIEW_NAME = "error/404";
    private static final String APPLICATION_EXCEPTION_MESSAGE = "Exception in the application occurred.";

    private ViewExceptionProcessor exceptionProcessor = new ViewExceptionProcessor();

    @Test
    public void handleNotFound_ShouldAddExceptionEntryToModel() {
        Model model = new ExtendedModelMap();
        NotFoundException exception = new NotFoundException(NOT_FOUND_MESSAGE);

        exceptionProcessor.handleNotFound(exception, model);

        Object exceptionFromModel = model.asMap().get("exception");
        assertThat(exceptionFromModel, instanceOf(NotFoundException.class));
        String exceptionMessage = ((NotFoundException) exceptionFromModel).getMessage();
        assertThat(exceptionMessage, is(NOT_FOUND_MESSAGE));
    }

    @Test
    public void handleNotFound_ShouldReturnNotFoundViewName() {
        Model model = new ExtendedModelMap();
        NotFoundException exception = new NotFoundException(NOT_FOUND_MESSAGE);

        String viewName = exceptionProcessor.handleNotFound(exception, model);

        assertThat(viewName, equalTo(NOT_FOUND_VIEW_NAME));
    }

    @Test
    public void handleJCartException_ShouldAddExceptionEntryToModel() {
        Model model = new ExtendedModelMap();
        JCartException exception = new JCartException(APPLICATION_EXCEPTION_MESSAGE);

        exceptionProcessor.handleJCartException(exception, model);

        Object exceptionFromModel = model.asMap().get("exception");
        assertThat(exceptionFromModel, instanceOf(JCartException.class));
        String exceptionMessage = ((JCartException) exceptionFromModel).getMessage();
        assertThat(exceptionMessage, is(APPLICATION_EXCEPTION_MESSAGE));
    }

    @Test
    public void handleJCartException_ShouldReturnServerErrorViewName() {
        Model model = new ExtendedModelMap();
        JCartException exception = new JCartException(APPLICATION_EXCEPTION_MESSAGE);

        String viewName = exceptionProcessor.handleJCartException(exception, model);

        assertThat(viewName, equalTo(SERVER_ERROR_VIEW_NAME));
    }
}