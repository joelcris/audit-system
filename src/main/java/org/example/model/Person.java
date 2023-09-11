package org.example.model;

import java.util.List;

public class Person {

    private String firstName;
    private String lastName;
    private List<Car> cars;

    public Person(String firstName, String lastName, List<Car> cars) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.cars = cars;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public List<Car> getCars() {
        return cars;
    }

    public void setCars(List<Car> cars) {
        this.cars = cars;
    }
}



