package com.revenat.jcart.core.orders;

import org.hamcrest.Matchers;
import org.junit.Test;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.*;

public class OrderNumberGeneratorImplTest {

    private OrderNumberGenerator orderNumberGenerator = new OrderNumberGeneratorImpl();

    @Test
    public void generate_ShouldGenerateNotEmptyStrings() {
        String orderNumberA = orderNumberGenerator.generate();

        assertNotNull(orderNumberA);
        assertThat(orderNumberA.trim().length(), Matchers.greaterThan(1));
    }

    @Test
    public void generate_ShouldGenerateUniqueStrings() {
        String orderNumberA = orderNumberGenerator.generate();
        String orderNumberB = orderNumberGenerator.generate();

        assertThat(orderNumberA, not(equalTo(orderNumberB)));
    }
}