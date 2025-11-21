package com.manning.junitbook.airport;

import com.manning.junitbook.mileage.Mileage;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BonusPolicy {
    private Passenger mike;
    private Passenger john;
    private Mileage mileage;

    @Given("^we have a regular passenger with a mileage$")
    public void we_have_a_regular_passenger_with_a_mileage() {
        mike = new Passenger("Mike", false);
        mileage = new Mileage();
    }

    @When("^the regular passenger travels (\\d+) and (\\d+) and (\\d+)$")
    public void the_regular_passenger_travels_and_and(int mileage1, int mileage2, int mileage3) {
        mileage.addMileage(mike, mileage1);
        mileage.addMileage(mike, mileage2);
        mileage.addMileage(mike, mileage3);
    }

    @Then("^the bonus points of the regular passenger should be (\\d+)$")
    public void the_bonus_points_of_the_regular_passenger_should_be(int points) {
        mileage.calculateGivenPoints();
        assertEquals(points, mileage.getPassengersPointsMap().get(mike).intValue());
    }

    @Given("^we have a VIP passenger with a mileage$")
    public void we_have_a_VIP_passenger_with_a_mileage() {
        john = new Passenger("John", true);
        mileage = new Mileage();
    }

    @When("^the VIP passenger travels (\\d+) and (\\d+) and (\\d+)$")
    public void the_VIP_passenger_travels_and_and(int mileage1, int mileage2, int mileage3) {
        mileage.addMileage(john, mileage1);
        mileage.addMileage(john, mileage2);
        mileage.addMileage(john, mileage3);
    }

    @Then("^the bonus points of the VIP passenger should be (\\d+)$")
    public void the_bonus_points_of_the_VIP_passenger_should_be(int points) {
        mileage.calculateGivenPoints();
        assertEquals(points, mileage.getPassengersPointsMap().get(john).intValue());
    }
}
