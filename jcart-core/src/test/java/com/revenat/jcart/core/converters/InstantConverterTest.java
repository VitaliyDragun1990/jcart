package com.revenat.jcart.core.converters;

import org.junit.Test;

import java.sql.Timestamp;
import java.time.Instant;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.*;

public class InstantConverterTest {

    private static final Instant INSTANT = Instant.now();
    private static final Timestamp TIMESTAMP = new Timestamp(INSTANT.toEpochMilli());

    private InstantConverter converter = new InstantConverter();

    @Test
    public void convertToDatabaseColumn_NotNullArgument_TimestampReturned() {
        Timestamp column = converter.convertToDatabaseColumn(INSTANT);

        assertThat(column, equalTo(TIMESTAMP));
    }

    @Test
    public void convertToDatabaseColumn_NullArgument_NullReturned() {
        Timestamp column = converter.convertToDatabaseColumn(null);

        assertNull(column);
    }

    @Test
    public void convertToEntityAttribute_NotNullArgument_InstantReturned() {
        Instant attribute = converter.convertToEntityAttribute(TIMESTAMP);

        assertThat(attribute, equalTo(INSTANT));
    }

    @Test
    public void convertToEntityAttribute_NullArgument_NullReturned() {
        Instant attribute = converter.convertToEntityAttribute(null);

        assertNull(attribute);
    }
}