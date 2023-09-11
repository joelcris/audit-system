#### Time to working version
This project took several hours spread across multiple days. The total amount of time was approximately: `10-12 hrs`

# DiffTool

DiffTool is a utility that compares two objects of the same type and produces a list of changes between them. It can compare simple properties, as well as lists and nested objects.

## How It Works

- **Reflection:** The DiffTool class uses reflection to access the properties of the objects being compared. It iterates over all the fields of the objects, including private fields, and compares their values.

- **Comparison:** For each field, it compares the value in the previous object with the value in the current object.

    - If the values are simple properties (e.g., String, Integer, etc.), it directly compares the values and, if they are different, adds a `PropertyUpdate` change to the list of changes.

    - If the values are lists, it compares the lists item by item. It finds items that were added, items that were removed, and items that were updated. It adds a `ListUpdate` change to the list of changes for each added or removed item. For updated items, it recursively compares the properties of the items and adds `PropertyUpdate` changes for each different property.

    - If the values are objects, it recursively compares the properties of the objects and adds `PropertyUpdate` changes for each different property.

- **AuditKey:** The `@AuditKey` annotation can be used to specify which field should be used as the key for comparing objects in a list. If an `@AuditKey` is not specified, the id field will be used by default. This is useful for cases where the objects in a list do not have an id field or where a different field should be used as the key.

- **Change Types:** There are two types of changes that the DiffTool can produce:

    - `PropertyUpdate`: This change is produced when a simple property or an object property has changed. It contains the name of the property, the previous value, and the current value.

    - `ListUpdate`: This change is produced when an item has been added or removed from a list. It contains the name of the list, the added items, and the removed items.

- **Exceptions :**
`AuditException`:This exception is thrown when the DiffTool is trying to compare items in a list, but the items in the list do not have a field with the @AuditKey annotation or a field named id.

## Usage

To use the DiffTool, simply call the `diff` method, passing the previous object and the current object:

```java
List<ChangeType> changes = DiffTool.diff(previous, current);
```

The `diff` method will return a list of `ChangeType` changes.

## Example

```java
Person previous = new Person("John", "Doe", Arrays.asList(new Car("1", "Honda", "Civic", 2015)));
Person current = new Person("John", "Doe", Arrays.asList(new Car("1", "Honda", "Civic", 2016)));
List<ChangeType> changes = DiffTool.diff(previous, current);

// changes will contain one PropertyUpdate change:
// {"property":"cars[1].year","previous":2015,"current":2016}
```

In this example, the DiffTool compares two `Person` objects. The only difference between them is the year of the `Car` in the `cars` list. Therefore, the `changes` list will contain one `PropertyUpdate` change with the property "cars[1].year", the previous value 2015, and the current value 2016.

## More examples

```java

// Example 1: Changing a Property
Person previous = new Person("John", "Doe", null);
Person current = new Person("John", "Smith", null);
List<ChangeType> changes = DiffTool.diff(previous, current);

// changes will contain one PropertyUpdate change:
// {"property":"lastName","previous":"Doe","current":"Smith"}


// Example 2: Adding and Removing Items from a List
previous = new Person("John", "Doe", Arrays.asList(new Car("1", "Honda", "Civic", 2015)));
current = new Person("John", "Doe", Arrays.asList(new Car("1", "Honda", "Civic", 2016), new Car("3", "Toyota", "Corolla", 2010)));
changes = DiffTool.diff(previous, current);

// changes will contain two changes:
// 1. PropertyUpdate change: {"property":"cars[1].year","previous":2015,"current":2016}
// 2. ListUpdate change: {"property":"cars","added":[{"id":"3","make":"Toyota","model":"Corolla","year":2010}],"removed":[]}


// Example 3: Changing a Property of an Item in a List
previous = new Person("John", "Doe", Arrays.asList(new Role("admin", "Administrator")));
current = new Person("John", "Doe", Arrays.asList(new Role("admin", "Super Administrator")));
changes = DiffTool.diff(previous, current);

// changes will contain one PropertyUpdate change:
// {"property":"roles[admin].description","previous":"Administrator","current":"Super Administrator"}


// Example 4: Removing an Item from a List
previous = new Person("John", "Doe", Arrays.asList(new Role("admin", "Administrator"), new Role("user", "User")));
current = new Person("John", "Doe", Arrays.asList(new Role("admin", "Administrator")));
changes = DiffTool.diff(previous, current);

// changes will contain one ListUpdate change:
// {"property":"roles","added":[],"removed":[{"roleId":"user","description":"User"}]}
```

