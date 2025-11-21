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

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class PassengerPolicy {
    private Flight economyFlight;
    private Flight businessFlight;
    private Flight premiumFlight;
    private Passenger mike;
    private Passenger john;

    @Given("^there is an economy flight$")
    public void there_is_an_economy_flight() {
        economyFlight = new EconomyFlight("1");
    }

    @When("^we have a regular passenger$")
    public void we_have_a_regular_passenger() {
        mike = new Passenger("Mike", false);
    }

    @Then("^you can add and remove him from an economy flight$")
    public void you_can_add_and_remove_him_from_an_economy_flight() {
        assertAll("Verify all conditions for a regular passenger and an economy flight",
                () -> assertEquals("1", economyFlight.getId()),
                () -> assertTrue(economyFlight.addPassenger(mike)),
                () -> assertEquals(1, economyFlight.getPassengersSet().size()),
                () -> assertTrue(economyFlight.getPassengersSet().contains(mike)),
                () -> assertTrue(economyFlight.removePassenger(mike)),
                () -> assertEquals(0, economyFlight.getPassengersSet().size())
        );
    }

    @Then("^you cannot add a regular passenger to an economy flight more than once$")
    public void add_a_regular_passenger_to_an_economy_flight_more_than_once() {
        for (int i = 0; i < 10; i++) {
            economyFlight.addPassenger(mike);
        }
        assertAll("Verify a regular passenger can be added to an economy flight only once",
                () -> assertEquals(1, economyFlight.getPassengersSet().size()),
                () -> assertTrue(economyFlight.getPassengersSet().contains(mike)),
                () -> assertEquals("Mike", new ArrayList<>(economyFlight.getPassengersSet()).get(0).getName())
        );
    }

    @When("^we have a VIP passenger$")
    public void we_have_a_VIP_passenger() {
        john = new Passenger("John", true);
    }

    @Then("^you can add him but cannot remove him from an economy flight$")
    public void you_can_add_him_but_cannot_remove_him_from_an_economy_flight() {
        assertAll("Verify all conditions for a VIP passenger and an economy flight",
                () -> assertEquals("1", economyFlight.getId()),
                () -> assertTrue(economyFlight.addPassenger(john)),
                () -> assertEquals(1, economyFlight.getPassengersSet().size()),
                () -> assertTrue(economyFlight.getPassengersSet().contains(john)),
                () -> assertFalse(economyFlight.removePassenger(john)),
                () -> assertEquals(1, economyFlight.getPassengersSet().size())
        );
    }

    @Then("^you cannot add a VIP passenger to an economy flight more than once$")
    public void you_cannot_add_a_VIP_passenger_to_an_economy_flight_more_than_once() {
        for (int i = 0; i < 10; i++) {
            economyFlight.addPassenger(john);
        }

        assertAll("Verify a VIP passenger can be added to an economy flight only once",
                () -> assertEquals(1, economyFlight.getPassengersSet().size()),
                () -> assertTrue(economyFlight.getPassengersSet().contains(john)),
                () -> assertEquals("John", new ArrayList<>(economyFlight.getPassengersSet()).get(0).getName())
        );
    }

    @Given("^there is a business flight$")
    public void there_is_an_business_flight() {
        businessFlight = new BusinessFlight("2");
    }

    @Then("^you cannot add or remove him from a business flight$")
    public void you_cannot_add_or_remove_him_from_a_business_flight() {
        assertAll("Verify all conditions for a regular passenger and a business flight",
                () -> assertFalse(businessFlight.addPassenger(mike)),
                () -> assertEquals(0, businessFlight.getPassengersSet().size()),
                () -> assertFalse(businessFlight.removePassenger(mike)),
                () -> assertEquals(0, businessFlight.getPassengersSet().size())
        );
    }

    @Then("^you can add him but cannot remove him from a business flight$")
    public void you_can_add_him_but_cannot_remove_him_from_a_business_flight() {
        assertAll("Verify all conditions for a VIP passenger and a business flight",
                () -> assertTrue(businessFlight.addPassenger(john)),
                () -> assertEquals(1, businessFlight.getPassengersSet().size()),
                () -> assertFalse(businessFlight.removePassenger(john)),
                () -> assertEquals(1, businessFlight.getPassengersSet().size())
        );
    }

    @Then("^you cannot add a VIP passenger to a business flight more than once$")
    public void you_cannot_add_a_VIP_passenger_to_a_business_flight_more_than_once() {
        for (int i = 0; i < 10; i++) {
            businessFlight.addPassenger(john);
        }

        assertAll("Verify a VIP passenger can be added to a business flight only once",
                () -> assertEquals(1, businessFlight.getPassengersSet().size()),
                () -> assertTrue(businessFlight.getPassengersSet().contains(john)),
                () -> assertEquals("John", new ArrayList<>(businessFlight.getPassengersSet()).get(0).getName())
        );
    }

    @Given("^there is a premium flight$")
    public void thereIsAnPremiumFlight() {
        premiumFlight = new PremiumFlight("3");
    }

    @Then("^you cannot add or remove him from a premium flight$")
    public void there_is_an_premium_flight() {
        assertAll("Verify all conditions for a regular passenger and a premium flight",
                () -> assertFalse(premiumFlight.addPassenger(mike)),
                () -> assertEquals(0, premiumFlight.getPassengersSet().size()),
                () -> assertFalse(premiumFlight.removePassenger(mike)),
                () -> assertEquals(0, premiumFlight.getPassengersSet().size())
        );
    }

    @Then("^you can add and remove him from a premium flight$")
    public void you_cannot_add_or_remove_him_from_a_premium_flight() {
        assertAll("Verify all conditions for a VIP passenger and a premium flight",
                () -> assertTrue(premiumFlight.addPassenger(john)),
                () -> assertEquals(1, premiumFlight.getPassengersSet().size()),
                () -> assertTrue(premiumFlight.removePassenger(john)),
                () -> assertEquals(0, premiumFlight.getPassengersSet().size())
        );
    }

    @Then("^you cannot add a VIP passenger to a premium flight more than once$")
    public void you_cannot_add_a_VIP_passenger_to_a_premium_flight_more_than_once() {
        for (int i = 0; i < 10; i++) {
            premiumFlight.addPassenger(john);
        }

        assertAll("Verify a VIP passenger can be added to a premium flight only once",
                () -> assertEquals(1, premiumFlight.getPassengersSet().size()),
                () -> assertTrue(premiumFlight.getPassengersSet().contains(john)),
                () -> assertEquals("John", new ArrayList<>(premiumFlight.getPassengersSet()).get(0).getName())
        );
    }
}
