package com.manning.junitbook.airport;

import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(
        plugin = {"pretty"},
        features = "classpath:features")
public class CucumberTest {

    /*
     * This class should be empty, step definitions should be in separate classes.
     */

}
