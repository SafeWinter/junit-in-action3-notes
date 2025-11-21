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
package com.manning.junitbook.airport;

import org.junit.jupiter.api.*;

import java.util.ArrayList;
import java.util.Set;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

public class AirportTest {

    @DisplayName("Given there is an economy flight")
    @Nested
    class EconomyFlightTest {

        private Flight economyFlight;
        private Passenger mike;
        private Passenger james;

        @BeforeEach
        void setUp() {
            economyFlight = new EconomyFlight("1");
            mike = new Passenger("Mike", false);
            james = new Passenger("James", true);
        }

        @Nested
        @DisplayName("When we have a regular passenger")
        class RegularPassenger {
            @Test
            @DisplayName("Then you cannot add or remove him from a business flight")
            public void testEconomyFlightRegularPassenger() {
                final Set<Passenger> passengers = economyFlight.getPassengersSet();
                assertAll("Verify all conditions for a regular passenger and an economy flight",
                        () -> assertEquals("1", economyFlight.getId()),
                        () -> assertTrue(economyFlight.addPassenger(mike)),
                        () -> assertEquals(1, passengers.size()),
                        () -> assertEquals("Mike", new ArrayList<>(passengers).get(0).getName()),

                        () -> assertTrue(economyFlight.removePassenger(mike)),
                        () -> assertEquals(0, passengers.size()));
            }

            @DisplayName("Then you cannot add him to an economy flight more than once")
            @RepeatedTest(5)
            public void testEconomyFlightRegularPassengerAddedOnlyOnce(RepetitionInfo repetitionInfo) {
                IntStream.range(0, repetitionInfo.getTotalRepetitions())
                        .forEach(i -> economyFlight.addPassenger(mike));
                final Set<Passenger> passengers = economyFlight.getPassengersSet();
                assertAll("Verify a regular passenger can be added to an economy flight only once",
                        () -> assertEquals(1, passengers.size()),
                        () -> assertTrue(passengers.contains(mike)),
                        () -> assertEquals("Mike", new ArrayList<>(passengers).get(0).getName())
                );
            }
        }

        @Nested
        @DisplayName("When we have a VIP passenger")
        class VipPassenger {

            @Test
            @DisplayName("Then you can add him but cannot remove him from an economy flight")
            public void testEconomyFlightVipPassenger() {
                final Set<Passenger> passengers = economyFlight.getPassengersSet();
                assertAll("Verify all conditions for a VIP passenger and an economy flight",
                        () -> assertEquals("1", economyFlight.getId()),
                        () -> assertTrue(economyFlight.addPassenger(james)),
                        () -> assertEquals(1, passengers.size()),
                        () -> assertEquals("James", new ArrayList<>(passengers).get(0).getName()),

                        () -> assertFalse(economyFlight.removePassenger(james)),
                        () -> assertEquals(1, passengers.size()));
            }

            @DisplayName("Then you cannot add him to an economy flight more than once")
            @RepeatedTest(5)
            public void testEconomyFlightVipPassengerAddedOnlyOnce(RepetitionInfo repetitionInfo) {
                IntStream.range(0, repetitionInfo.getTotalRepetitions())
                        .forEach(i -> economyFlight.addPassenger(james));
                final Set<Passenger> passengers = economyFlight.getPassengersSet();
                assertAll("Verify a VIP passenger can be added to an economy flight only once",
                        () -> assertEquals(1, passengers.size()),
                        () -> assertTrue(passengers.contains(james)),
                        () -> assertEquals("James", new ArrayList<>(passengers).get(0).getName())
                );
            }
        }
    }

    @DisplayName("Given there is a business flight")
    @Nested
    class BusinessFlightTest {
        private Flight businessFlight;
        private Passenger mike;
        private Passenger james;

        @BeforeEach
        void setUp() {
            businessFlight = new BusinessFlight("2");
            mike = new Passenger("Mike", false);
            james = new Passenger("James", true);
        }

        @Nested
        @DisplayName("When we have a regular passenger")
        class RegularPassenger {
            @Test
            @DisplayName("Then you cannot add or remove him from a business flight")
            public void testBusinessFlightRegularPassenger() {
                final Set<Passenger> passengers = businessFlight.getPassengersSet();
                assertAll("",
                        () -> assertFalse(businessFlight.addPassenger(mike)),
                        () -> assertEquals(0, passengers.size()),
                        () -> assertFalse(businessFlight.removePassenger(mike)),
                        () -> assertEquals(0, passengers.size()));
            }
        }

        @Nested
        @DisplayName("When we have a VIP passenger")
        class VipPassenger {
            @Test
            @DisplayName("Then you can add him but cannot remove him from a business flight")
            public void testBusinessFlightVipPassenger() {
                final Set<Passenger> passengers = businessFlight.getPassengersSet();
                assertAll("",
                        () -> assertTrue(businessFlight.addPassenger(james)),
                        () -> assertEquals(1, passengers.size()),
                        () -> assertFalse(businessFlight.removePassenger(james)),
                        () -> assertEquals(1, passengers.size()));
            }

            @DisplayName("Then you cannot add him to a business flight more than once")
            @RepeatedTest(5)
            public void testBusinessFlightVipPassengerAddedOnlyOnce(RepetitionInfo repetitionInfo) {
                IntStream.range(0, repetitionInfo.getTotalRepetitions())
                        .forEach(i -> businessFlight.addPassenger(james));
                final Set<Passenger> passengers = businessFlight.getPassengersSet();
                assertAll("Verify a VIP passenger can be added to a business flight only once",
                        () -> assertEquals(1, passengers.size()),
                        () -> assertTrue(passengers.contains(james)),
                        () -> assertEquals("James", new ArrayList<>(passengers).get(0).getName())
                );
            }
        }
    }

    @Nested
    @DisplayName("Given there is a premium flight")
    class PremiumFlightTest {
        private Flight premiumFlight;
        private Passenger mike;
        private Passenger james;

        @BeforeEach
        void setUp() {
            premiumFlight = new PremiumFlight("3");
            mike = new Passenger("Mike", false);
            james = new Passenger("James", true);
        }

        @Nested
        @DisplayName("When we have a regular passenger")
        class RegularPassenger {
            @Test
            @DisplayName("Then you cannot add or remove him from a premium flight")
            public void testPremiumFlightRegularPassenger() {
                final Set<Passenger> passengers = premiumFlight.getPassengersSet();
                assertAll("Verify all conditions for a regular passenger and a premium flight",
                        () -> assertFalse(premiumFlight.addPassenger(mike)),
                        () -> assertEquals(0, passengers.size()),
                        () -> assertFalse(premiumFlight.removePassenger(mike)),
                        () -> assertEquals(0, passengers.size()));
            }
        }

        @Nested
        @DisplayName("When we have a VIP passenger")
        class VipPassenger {
            @Test
            @DisplayName("Verify all conditions for a VIP passenger and a premium flight")
            public void testPremiumFlightVipPassenger() {
                final Set<Passenger> passengers = premiumFlight.getPassengersSet();
                assertAll("Verify all conditions for a VIP passenger and a premium flight",
                        () -> assertTrue(premiumFlight.addPassenger(james)),
                        () -> assertEquals(1, passengers.size()),
                        () -> assertTrue(premiumFlight.removePassenger(james)),
                        () -> assertEquals(0, passengers.size()));
            }

            @DisplayName("Then you cannot add him to a premium flight more than once")
            @RepeatedTest(5)
            public void testPremiumFlightVipPassengerAddedOnlyOnce(RepetitionInfo repetitionInfo) {
                IntStream.range(0, repetitionInfo.getTotalRepetitions())
                        .forEach(i -> premiumFlight.addPassenger(james));
                final Set<Passenger> passengers = premiumFlight.getPassengersSet();
                assertAll("Verify a VIP passenger can be added to a premium flight only once",
                        () -> assertEquals(1, passengers.size()),
                        () -> assertTrue(passengers.contains(james)),
                        () -> assertEquals("James", new ArrayList<>(passengers).get(0).getName())
                );
            }
        }
    }
}
