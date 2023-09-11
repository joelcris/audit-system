package org.example.diff;

import com.google.gson.Gson;

/**
 * A class representing an update to a property.
 */
public class PropertyUpdate implements ChangeType {

    private String property;
    private Object previous;
    private Object current;

    public PropertyUpdate(String property, Object previous, Object current) {
        this.property = property;
        this.previous = previous;
        this.current = current;
    }

    public String getProperty() {
        return property;
    }

    public void setProperty(String property) {
        this.property = property;
    }

    public Object getPrevious() {
        return previous;
    }

    public void setPrevious(Object previous) {
        this.previous = previous;
    }

    public Object getCurrent() {
        return current;
    }

    public void setCurrent(Object current) {
        this.current = current;
    }

    @Override
    public String toString() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }
}



