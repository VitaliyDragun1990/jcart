package com.revenat.jcart.admin.web.controllers;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javax.servlet.http.HttpServletRequest;

import static javax.servlet.RequestDispatcher.ERROR_STATUS_CODE;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AdminErrorControllerTest {

    private AdminErrorController controller = new AdminErrorController();

    @Mock
    private HttpServletRequest mockRequest;

    @Test
    public void testGetErrorPath() {
        String errorPath = controller.getErrorPath();

        assertThat(errorPath, equalTo("/error"));
    }

    @Test
    public void testReturns404PageWhenNotFound() {
        when(mockRequest.getAttribute(ERROR_STATUS_CODE)).thenReturn("404");

        String viewName = controller.handleError(mockRequest);

        assertThat(viewName, equalTo("error/404"));
    }

    @Test
    public void testReturns500PageWhenServerError() {
        when(mockRequest.getAttribute(ERROR_STATUS_CODE)).thenReturn("500");

        String viewName = controller.handleError(mockRequest);

        assertThat(viewName, equalTo("error/500"));
    }

    @Test
    public void testReturns500PageWhenSomeOther() {
        String viewName = controller.handleError(mockRequest);

        assertThat(viewName, equalTo("error/500"));
    }
}