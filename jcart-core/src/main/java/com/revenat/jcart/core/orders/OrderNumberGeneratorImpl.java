package com.revenat.jcart.core.orders;

import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class OrderNumberGeneratorImpl implements OrderNumberGenerator {
    @Override
    public String generate() {
        return UUID.randomUUID().toString();
    }
}
