package com.testeddatasystem.flights;

public class Passenger {
    private final String identifier;
    private final String name;

    public Passenger(String identifier, String name) {
        this.identifier = identifier;
        this.name = name;
    }

    public String getIdentifier() {
        return identifier;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "Passenger " + getName() +", with identifier: " + getIdentifier();
    }

    public static void main(String[] args) {
        final Passenger passenger = new Passenger("123-456-789", "John Smith");
        System.out.println(passenger);
    }
}
