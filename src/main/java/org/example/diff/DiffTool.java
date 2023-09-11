package org.example.diff;

import org.example.exception.AuditException;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A utility class for computing the difference between two objects.
 */
public class DiffTool {

    /**
     * Computes the difference between two objects.
     *
     * @param previous the previous state of the object
     * @param current  the current state of the object
     * @return a list of changes between the previous and current states of the object
     * @throws IllegalAccessException if the object's fields are not accessible
     */
    public static List<ChangeType> diff(Object previous, Object current) throws IllegalAccessException {
        return diff(previous, current, "");
    }

    private static List<ChangeType> diff(Object previous, Object current, String parentProperty) throws IllegalAccessException {
        List<ChangeType> changes = new ArrayList<>();

        if (previous == null && current == null) {
            return changes;
        }

        if (previous == null) {
            changes.add(new PropertyUpdate(parentProperty, null, current));
            return changes;
        }

        if (current == null) {
            changes.add(new PropertyUpdate(parentProperty, previous, null));
            return changes;
        }

        Class<?> clazz = previous.getClass();

        for (Field field : clazz.getDeclaredFields()) {
            field.setAccessible(true);
            Object previousValue = field.get(previous);
            Object currentValue = field.get(current);

            String propertyName = parentProperty.isEmpty() ? field.getName() : parentProperty + "." + field.getName();

            if (previousValue instanceof List && currentValue instanceof List) {
                List<Object> previousList = (List<Object>) previousValue;
                List<Object> currentList = (List<Object>) currentValue;

                List<Object> addedItems = new ArrayList<>();
                List<Object> removedItems = new ArrayList<>();

                for (Object previousItem : previousList) {
                    Object currentItem = findItemWithSameId(previousItem, currentList);
                    if (currentItem == null) {
                        removedItems.add(previousItem);
                    } else {
                        String itemPropertyPrefix = propertyName + "[" + getAuditKeyId(currentItem) + "]";
                        List<ChangeType> itemChanges = diff(previousItem, currentItem, itemPropertyPrefix);
                        changes.addAll(itemChanges);
                    }
                }

                for (Object currentItem : currentList) {
                    Object previousItem = findItemWithSameId(currentItem, previousList);
                    if (previousItem == null) {
                        addedItems.add(currentItem);
                    }
                }

                if (!addedItems.isEmpty() || !removedItems.isEmpty()) {
                    changes.add(new ListUpdate(propertyName, addedItems, removedItems));
                }

            } else if (previousValue instanceof List || currentValue instanceof List) {
                List<Object> previousList = previousValue == null ? Collections.emptyList() : (List<Object>) previousValue;
                List<Object> currentList = currentValue == null ? Collections.emptyList() : (List<Object>) currentValue;
                List<Object> addedItems = new ArrayList<>(currentList);
                List<Object> removedItems = new ArrayList<>(previousList);
                changes.add(new ListUpdate(propertyName, addedItems, removedItems));
            } else if (previousValue != null && !previousValue.equals(currentValue)
                    || currentValue != null && !currentValue.equals(previousValue)) {
                changes.add(new PropertyUpdate(propertyName, previousValue, currentValue));
            }
        }

        return changes;
    }

    private static Object findItemWithSameId(Object item, List<Object> list) throws IllegalAccessException {
        for (Object listItem : list) {
            if (getAuditKeyId(item).equals(getAuditKeyId(listItem))) {
                return listItem;
            }
        }
        return null;
    }

    private static String getAuditKeyId(Object object) throws IllegalAccessException {
        for (Field field : object.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            if (field.isAnnotationPresent(AuditKey.class) || field.getName().equals("id")) {
                return field.get(object).toString();
            }
        }
        throw new AuditException("No field with @AuditKey annotation or 'id' name found in class " + object.getClass().getName());
    }
}
