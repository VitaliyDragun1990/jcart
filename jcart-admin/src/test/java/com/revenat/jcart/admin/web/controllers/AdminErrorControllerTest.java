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
    private static final String ERROR_PATH = "/error";
    private static final String NOT_FOUND = "404";
    private static final String SERVER_ERROR = "500";
    private static final String XXX_ERROR = "555";
    private static final String NOT_FOUND_VIEW = "error/404";
    private static final String SERVER_ERROR_VIEW = "error/500";

    private AdminErrorController controller = new AdminErrorController();

    @Mock
    private HttpServletRequest mockRequest;

    @Test
    public void getErrorPath_ShouldReturnErrorPath() {
        String errorPath = controller.getErrorPath();

        assertThat(errorPath, equalTo(ERROR_PATH));
    }

    @Test
    public void handleError_NotFound_NotFoundViewReturned() {
        when(mockRequest.getAttribute(ERROR_STATUS_CODE)).thenReturn(NOT_FOUND);

        String viewName = controller.handleError(mockRequest);

        assertThat(viewName, equalTo(NOT_FOUND_VIEW));
    }

    @Test
    public void handleError_ServerError_ServerErrorViewReturned() {
        when(mockRequest.getAttribute(ERROR_STATUS_CODE)).thenReturn(SERVER_ERROR);

        String viewName = controller.handleError(mockRequest);

        assertThat(viewName, equalTo(SERVER_ERROR_VIEW));
    }

    @Test
    public void handleError_AnyOtherError_ServerErrorViewReturned() {
        when(mockRequest.getAttribute(ERROR_STATUS_CODE)).thenReturn(XXX_ERROR);

        String viewName = controller.handleError(mockRequest);

        assertThat(viewName, equalTo(SERVER_ERROR_VIEW));
    }
}