package com.manning.junitbook.ch02.assertions;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AssertThrows1Test {

    private final SUT sut = new SUT("demo system");

    @Test
    @DisplayName("An exception is expected")
    void assertThrowsTest() {
        assertThrows(NoJobException.class, sut::run);
    }

    @Test
    @DisplayName("An exception is caught")
    void assertThrowsTest2() {
        final NoJobException ex = assertThrows(NoJobException.class, () -> sut.run(500));
        assertEquals("No jobs on the execution list!", ex.getMessage());
    }
}
