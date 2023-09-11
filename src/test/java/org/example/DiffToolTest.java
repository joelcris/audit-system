package org.example;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.diff.ChangeType;
import org.example.diff.DiffTool;
import org.example.exception.AuditException;
import org.example.model.Car;
import org.example.model.Person;
import org.junit.Test;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DiffToolTest {

    @Test
    public void testDiff() throws IllegalAccessException, JsonProcessingException {
        Person previous = new Person("John", "Doe", Arrays.asList(
                new Car("1", "Honda", "Civic", 2015),
                new Car("2", "Toyota", "Corolla", 2015)
        ));
        Person current = new Person("Jane", "Smith", Arrays.asList(
                new Car("1", "Honda", "Civic", 2015),
                new Car("2", "Toyota", "Corolla", 2016)
        ));
        List<ChangeType> changes = DiffTool.diff(previous, current);
        String actualJson = convertToJson(changes);
        String expectedJson = "[{\"property\":\"firstName\",\"previous\":\"John\",\"current\":\"Jane\"}," +
                "{\"property\":\"lastName\",\"previous\":\"Doe\",\"current\":\"Smith\"}," +
                "{\"property\":\"cars[2].year\",\"previous\":2015,\"current\":2016}]";
        assertEquals(expectedJson, actualJson);
    }

    //This test will check if the system can handle nested properties correctly.
    @Test
    public void testNestedProperties() throws IllegalAccessException, JsonProcessingException {
        Person previous = new Person("John", "Doe", Arrays.asList(new Car("1", "Honda", "Civic", 2015)));
        Person current = new Person("John", "Smith", Arrays.asList(new Car("1", "Honda", "Civic", 2015)));
        List<ChangeType> changes = DiffTool.diff(previous, current);
        String actualJson = convertToJson(changes);
        String expectedJson = "[{\"property\":\"lastName\",\"previous\":\"Doe\",\"current\":\"Smith\"}]";
        assertEquals(expectedJson, actualJson);
    }

    //This test will check if the system can handle adding and removing items from a list.
    @Test
    public void testListItemsAddedRemoved() throws IllegalAccessException, JsonProcessingException {
        Person previous = new Person("John", "Doe", Arrays.asList(
                new Car("1", "Honda", "Civic", 2015),
                new Car("2", "Toyota", "Corolla", 2015)
        ));
        Person current = new Person("John", "Doe", Arrays.asList(
                new Car("1", "Honda", "Civic", 2015),
                new Car("3", "Ford", "Mustang", 2016)
        ));
        List<ChangeType> changes = DiffTool.diff(previous, current);
        String actualJson = convertToJson(changes);
        String expectedJson = "[{\"property\":\"cars\",\"added\":[{\"id\":\"3\",\"make\":\"Ford\",\"model\":\"Mustang\",\"year\":2016}],\"removed\":[{\"id\":\"2\",\"make\":\"Toyota\",\"model\":\"Corolla\",\"year\":2015}]}]";
        assertEquals(expectedJson, actualJson);
    }

    //This test will check if the system can handle null values correctly.
    @Test
    public void testNullValues() throws IllegalAccessException, JsonProcessingException {
        Person previous = new Person("John", null, null);
        Person current = new Person("John", "Doe", Arrays.asList(new Car("1", "Honda", "Civic", 2015)));
        List<ChangeType> changes = DiffTool.diff(previous, current);
        String actualJson = convertToJson(changes);
        String expectedJson = "[{\"property\":\"lastName\",\"previous\":null,\"current\":\"Doe\"}," +
                "{\"property\":\"cars\",\"added\":[{\"id\":\"1\",\"make\":\"Honda\",\"model\":\"Civic\",\"year\":2015}],\"removed\":[]}]";
        assertEquals(expectedJson, actualJson);
    }

    //This test will throw an exception that indicates that the audit system lacks the information it needs to determine what has changed.
    @Test(expected = AuditException.class)
    public void testNoIdField() throws IllegalAccessException {
        List<NoIdFieldClass> previousList = new ArrayList<>();
        previousList.add(new NoIdFieldClass("Test1"));
        List<NoIdFieldClass> currentList = new ArrayList<>();
        currentList.add(new NoIdFieldClass("Test2"));
        TestClass previous = new TestClass(previousList);
        TestClass current = new TestClass(currentList);
        DiffTool.diff(previous, current);
    }

    public static class TestClass {

        private List<NoIdFieldClass> list;
        public TestClass(List<NoIdFieldClass> list) {
            this.list = list;
        }
        public List<NoIdFieldClass> getList() {
            return list;
        }

    }

    public static class NoIdFieldClass {

        private String name;
        public NoIdFieldClass(String name) {
            this.name = name;
        }
        public String getName() {
            return name;
        }

    }

    public static String convertToJson(List<ChangeType> changes) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(changes);
    }

}
