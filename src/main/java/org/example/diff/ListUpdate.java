package org.example.diff;

import java.util.List;
import com.google.gson.Gson;

/**
 * A class representing an update to a list.
 */
public class ListUpdate implements ChangeType {

    private String property;
    private List<Object> added;
    private List<Object> removed;

    public ListUpdate(String property, List<Object> added, List<Object> removed) {
        this.property = property;
        this.added = added;
        this.removed = removed;
    }

    public String getProperty() {
        return property;
    }

    public void setProperty(String property) {
        this.property = property;
    }

    public List<Object> getAdded() {
        return added;
    }

    public void setAdded(List<Object> added) {
        this.added = added;
    }

    public List<Object> getRemoved() {
        return removed;
    }

    public void setRemoved(List<Object> removed) {
        this.removed = removed;
    }

    @Override
    public String toString() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }
}




