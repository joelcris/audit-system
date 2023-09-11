package org.example;

import org.example.diff.ChangeType;
import org.example.diff.DiffTool;
import org.example.model.Car;
import org.example.model.Person;

import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        //For quick testing only
        List<Car> previousCars = new ArrayList<>();
        previousCars.add(new Car("1", "Honda", "Civic", 2015));
        previousCars.add(new Car("2", "Toyota", "Corolla", 2015));

        List<Car> currentCars = new ArrayList<>();
        currentCars.add(new Car("1", "Honda", "Civic", 2016));
        currentCars.add(new Car("3", "Toyota", "Corolla", 2010));

        Person previous = new Person("John", "Doe", previousCars);
        Person current = new Person("John", "Doe", currentCars);

        try {
            List<ChangeType> changes = DiffTool.diff(previous, current);
            for (ChangeType change : changes) {
                System.out.println(change);
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}



