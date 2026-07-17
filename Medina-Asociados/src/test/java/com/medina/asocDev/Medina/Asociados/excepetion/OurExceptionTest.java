package com.medina.asocDev.Medina.Asociados.excepetion;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class OurExceptionTest {

    @Test
    void constructor_setsMessage() {
        String message = "Test error message";
        OurException exception = new OurException(message);
        assertEquals(message, exception.getMessage());
    }

    @Test
    void getMessage_returnsCorrectMessage() {
        OurException exception = new OurException("Something went wrong");
        assertEquals("Something went wrong", exception.getMessage());
    }

    @Test
    void isRuntimeException() {
        OurException exception = new OurException("test");
        assertInstanceOf(RuntimeException.class, exception);
    }
}
