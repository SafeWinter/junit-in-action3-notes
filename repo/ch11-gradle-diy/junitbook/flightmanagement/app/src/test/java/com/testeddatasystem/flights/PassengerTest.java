package com.testeddatasystem.flights;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PassengerTest {

    @Test
    @DisplayName("toString() method should match the desired content")
    void testToString() {
        final Passenger passenger = new Passenger("123-456-789", "John Smith");
        assertEquals("Passenger John Smith, with identifier: 123-456-789",
                passenger.toString());
    }
}
