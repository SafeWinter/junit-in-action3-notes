package com.manning.junitbook.ch02.custom;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(RandomNumberResolver.class)
public class RandomNumberTest {

    @Test
    @DisplayName("Test with a random int number")
    void testWithRandomNumber(@RandomAnnotation int randomNumber) {
        // JUnit 5 会使用我们的 RandomNumberResolver 来提供 randomNumber 的值
        System.out.println("Injected random number: " + randomNumber);
        assertTrue(randomNumber >= 0 && randomNumber < 100);
    }

    @Test
    @DisplayName("Test with two random integers and compare ")
    void testWithMultipleRandomParameters(@RandomAnnotation int number1, @RandomAnnotation int number2) {
        // 每个被 @Random 注解的 int 参数都会触发一次解析
        System.out.println("Injected random numbers: " + number1 + ", " + number2);
        assertTrue(number1 != number2); // 有很大概率通过，但非绝对，这里仅作演示
    }
}
