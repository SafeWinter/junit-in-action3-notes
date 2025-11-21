package com.manning.junitbook.ch02.nested;

import java.util.Date;

public class CustomerNew {
    private final String firstName;
    private final String lastName;
    private final String middleName;
    private final Date birthDate;
    private final Gender gender;

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public Date getBirthDate() {
        return birthDate;
    }

    public Gender getGender() {
        return gender;
    }

    public static class Builder {
        private final String firstName;
        private final String lastName;
        private final String middleName;
        private Date birthDate;
        private Gender gender;

        public Builder withBirthDate(Date birthDate) {
            this.birthDate = birthDate;
            return this;
        }

        public Builder withGender(Gender gender) {
            this.gender = gender;
            return this;
        }

        public Builder(String firstName, String lastName, String middleName) {
            this.firstName = firstName;
            this.lastName = lastName;
            this.middleName = middleName;
        }

        public CustomerNew build() {
            return new CustomerNew(this);
        }
    }

    private CustomerNew(Builder builder) {
        this.firstName = builder.firstName;
        this.lastName = builder.lastName;
        this.middleName = builder.middleName;
        this.birthDate = builder.birthDate;
        this.gender = builder.gender;
    }
}
