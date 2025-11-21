/*
 * ========================================================================
 *
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * ========================================================================
 */
package com.manning.junitbook.spring;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.Objects;

public class PassengerUtil {

    public static Passenger getExpectedPassenger() {
        Passenger passenger = new Passenger("John Smith");

        Country country = new Country("USA", "US");
        passenger.setCountry(country);

        return passenger;
    }

    public static Passenger getActualPassenger() {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(
                "classpath:application-context.xml");
        return context.getBean("passenger", Passenger.class);
    }

    public static void main(String[] args) {
        Passenger expected = getExpectedPassenger();
        Passenger actual = getActualPassenger();
        System.out.println(Objects.equals(expected, actual));
    }
}
