package com.revenat.jcart.admin.web.utils;

import javax.servlet.http.HttpServletRequest;

public class WebUtils {
    public static final String IMAGES_PREFIX = "/products/images/";
    public static final String IMAGES_DIR = "E:/jcart/products";

    private WebUtils() {}

    public static String getURLWithContextPath(HttpServletRequest request) {
        return request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort()
                + request.getContextPath();
    }


}
