package com.manning.junitbook.ch14;

import com.manning.junitbook.ch14.extensions.ExecutionContextExtension;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PassengerDemoTest {

    private static void runTestLogics() {
        Passenger passenger = new Passenger("123-456-789", "John Smith");
        assertEquals("Passenger John Smith with identifier: 123-456-789", passenger.toString());
    }

    @Test
    @ExtendWith(ExecutionContextExtension.class)
    @DisplayName("marked with conditional extension (1)")
    void testPassenger1() {
        runTestLogics();
    }

    @Test
    @ExtendWith(ExecutionContextExtension.class)
    @DisplayName("marked with conditional extension (2)")
    void testPassenger2() {
        runTestLogics();
    }

    @Test
    @DisplayName("no conditional extension")
    void testPassenger3() {
        runTestLogics();
    }
}
