package com.revenat.jcart.admin.security;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class PostAuthorizationFilterTest {
    private static final String HOME_ATTRIBUTE = "Home";
    private static final String URI = "/home";

    @Mock
    private HttpServletRequest mockRequest;
    @Mock
    private HttpServletResponse mockResponse;
    @Mock
    private FilterChain mockChain;

    private PostAuthorizationFilter filter = new PostAuthorizationFilter();

    @Test
    public void doFilterInternal_MatchingUri_ShouldAddRequestAttribute() throws ServletException, IOException {
        when(mockRequest.getRequestURI()).thenReturn(URI);

        filter.doFilterInternal(mockRequest, mockResponse, mockChain);

        verify(mockRequest, times(1)).setAttribute("CURRENT_MENU", HOME_ATTRIBUTE);
    }

    @Test
    public void doFilterInternal_IgnoredUri_ShouldNotAddRequestAttribute() throws ServletException, IOException {
        when(mockRequest.getRequestURI()).thenReturn(filter.IGNORE_URIS[0]);

        filter.doFilterInternal(mockRequest, mockResponse, mockChain);

        verify(mockRequest, never()).setAttribute(anyString(), any());
    }
}