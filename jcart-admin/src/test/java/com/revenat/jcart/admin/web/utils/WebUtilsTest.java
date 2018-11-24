package com.revenat.jcart.admin.web.utils;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javax.servlet.http.HttpServletRequest;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class WebUtilsTest {
    private static final String SCHEMA = "http";
    private static final String SERVER_NAME = "localhost";
    private static final int SERVER_PORT = 80;
    private static final String CONTEXT_PATH = "/jcart";

    @Mock
    private HttpServletRequest mockRequest;

    @Before
    public void initMock() {
        when(mockRequest.getScheme()).thenReturn(SCHEMA);
        when(mockRequest.getServerName()).thenReturn(SERVER_NAME);
        when(mockRequest.getServerPort()).thenReturn(SERVER_PORT);
        when(mockRequest.getContextPath()).thenReturn(CONTEXT_PATH);
    }

    @Test
    public void testGetURLWithContextPath() {
        String expectedUrl = SCHEMA + "://" + SERVER_NAME + ":" + SERVER_PORT + CONTEXT_PATH;

        String url = WebUtils.getURLWithContextPath(mockRequest);

        assertThat(url, equalTo(expectedUrl));
    }
}