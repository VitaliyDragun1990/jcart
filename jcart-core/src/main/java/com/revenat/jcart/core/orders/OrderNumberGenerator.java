package com.revenat.jcart.core.orders;

/**
 * Responsible for generating unique {@link String} instances representing order numbers.
 */
public interface OrderNumberGenerator {

    /**
     * @return Unique order number.
     */
    String generate();
}
