package com.revenat.jcart.core.common.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JCLogger {

    private Logger logger;

    private JCLogger(Class<?> clazz) {
        this.logger = LoggerFactory.getLogger(clazz);
    }

    public static JCLogger getLogger(Class<?> clazz) {
        return new JCLogger(clazz);
    }

    public void info(String message) {
        this.logger.info(message);
    }

    public void info(String format, Object...args) {
        this.logger.info(format, args);
    }

    public void debug(String message) {
        this.logger.debug(message);
    }

    public void debug(String format, Object...args) {
        this.logger.debug(format, args);
    }

    public void warn(String message) {
        this.logger.warn(message);
    }

    public void warn(String format, Object...args) {
        this.logger.warn(format, args);
    }

    public void error(String message) {
        this.logger.error(message);
    }

    public void error(String format, Object...args) {
        this.logger.error(format, args);
    }

    public void error(Throwable t) {
        this.logger.error(t.getMessage(), t);
    }

    public void error(String message, Throwable t) {
        this.logger.error(message, t);
    }
}
