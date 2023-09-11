package org.example;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

import org.example.diff.ChangeType;
import org.example.diff.DiffTool;
import org.example.model.Role;
import org.example.model.User;
import org.junit.Test;

public class UserAndRoleTest {

    private String convertToJson(List<ChangeType> changes) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(changes);
    }

    @Test
    public void testRoleDescriptionChange() throws IllegalAccessException, JsonProcessingException {
        User previousUser = new User("1", "John Doe", Arrays.asList(new Role("admin", "Administrator")));
        User currentUser = new User("1", "John Doe", Arrays.asList(new Role("admin", "System Administrator")));
        List<ChangeType> changes = DiffTool.diff(previousUser, currentUser);
        String actualJson = convertToJson(changes);
        String expectedJson = "[{\"property\":\"roles[admin].description\",\"previous\":\"Administrator\",\"current\":\"System Administrator\"}]";
        assertEquals(expectedJson, actualJson);
    }

    @Test
    public void testRoleAddition() throws IllegalAccessException, JsonProcessingException {
        User previousUser = new User("1", "John Doe", Arrays.asList(new Role("admin", "Administrator")));
        User currentUser = new User("1", "John Doe", Arrays.asList(new Role("admin", "Administrator"), new Role("user", "User")));
        List<ChangeType> changes = DiffTool.diff(previousUser, currentUser);
        String actualJson = convertToJson(changes);
        String expectedJson = "[{\"property\":\"roles\",\"added\":[{\"name\":\"user\",\"description\":\"User\"}],\"removed\":[]}]";
        assertEquals(expectedJson, actualJson);
    }

    @Test
    public void testRoleRemoval() throws IllegalAccessException, JsonProcessingException {
        User previousUser = new User("1", "John Doe", Arrays.asList(new Role("admin", "Administrator"), new Role("user", "User")));
        User currentUser = new User("1", "John Doe", Arrays.asList(new Role("admin", "Administrator")));
        List<ChangeType> changes = DiffTool.diff(previousUser, currentUser);
        String actualJson = convertToJson(changes);
        String expectedJson = "[{\"property\":\"roles\",\"added\":[],\"removed\":[{\"name\":\"user\",\"description\":\"User\"}]}]";
        assertEquals(expectedJson, actualJson);
    }

    @Test
    public void testMultipleAttributeChange() throws IllegalAccessException, JsonProcessingException {
        User previous = new User("1", "John Doe", Arrays.asList(new Role("user", "User")));
        User current = new User("1", "Jonathan Doe", Arrays.asList(new Role("admin", "Admin")));
        List<ChangeType> changes = DiffTool.diff(previous, current);
        String actualJson = convertToJson(changes);
        String expectedJson = "[{\"property\":\"name\",\"previous\":\"John Doe\",\"current\":\"Jonathan Doe\"}," +
                "{\"property\":\"roles\",\"added\":[{\"name\":\"admin\",\"description\":\"Admin\"}],\"removed\":[{\"name\":\"user\",\"description\":\"User\"}]}]";
        assertEquals(expectedJson, actualJson);
    }

}
