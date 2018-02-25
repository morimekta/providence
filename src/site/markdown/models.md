Providence Models
=================

The generated java interface is trying to follow the core java standard as
much as possible. There are some exceptions, but these are designed to be
non-conflicting with any other possible generated method name, and to
separate static and non-static methods without weird naming schemes.

## Enum Definitions

Thrift enums are mapped as java enums in `providence`, where each enum
value get's a java enum value. E.g.:

```thrift
enum MyEnum {
    FIRST = 1,
    SECOND,
    THIRD
}
```

Corresponds to the java enum:

```java
enum MyEnum {
    FIRST(1),
    SECOND(2),
    THIRD(3);

    private final int value;

    public int getId() {
        return value;
    }

    MyEnum(int value) {
        this.value = value;
    }

    @Nullable
    public static MyEnum findById(int id) {}

    @Nonnull
    public static MyEnum valueForId(int id) {}
}
```

## Message Definitions

There are three types of messages in providence:

- **[struct]**: Simple data structure containing a list of fields
  corresponding to a java model class. See struct definitions below
  for details.
- **[exception]**: A struct that also extends the java Exception
  class and can be thrown as exception from service methods. Exceptions
  have some extra methods to get the original `message` from the
  exception `origGetMessage()` and `origGetLocalizedMessage()`, and
  an overridden `initCause(Throwable)` method that returns the correct
  exception type.
- **[union]**: A struct that only allows one of it's fields to be
  set. Unions have two extra methods for managing the single set
  field: `unionField()` and `unionFieldIsSet()`.

Example:

```thrift
struct MyStruct {
    1: i32 my_field;
    2: list<string> other_field;
}
```

This roughly corresponds to a java POJO class that looks like:

```java
class MyStruct {
    public int myField;
    public List<String> otherField;
}
```

Except that with providence, you get an immutable object with a builder to help
setting it up. So more like:

```java
/**
 * The actual 'struct' class.
 */
class MyStruct {
    private final int myField;
    private final List<String> otherField;

    public MyStruct(int myField, List<String> otherField) {
        this.myField = myField;
        this.otherField = otherField;
    }

    public int getMyField() {
        return myField;
    }

    public List<String> getOtherfield() {
        return otherField;
    }

    /**
     * MyStruct building helper class.
     */
    public static class _Builder {
        private int myField;
        private ArrayList<String> otherField;

        public _Builder setMyField(int value) {
            this.myField = value;
            return this;
        }

        public _Builder setOtherField(List value) {
            this.otherField = value;
            return this;
        }

        public MyStruct build() {
            return new MyStruct(myField, otherField);
        }
    }
}
```

## Generated Classes

All message types implements the `PMessage` interface,
and contains a number of generated methods for accessing content and building
new messages. All the objects are generated to be `immutable`, but with use
of deeply nested containers that may be broken to some extent. The concept
of `simple` messages was introduced to distinguish between structs that
completely guarantee immutability, and those that don't.

### Field Access

In addition to the methods from `PMessage` interface, a number of accessor
methods are generated:

```java
public class MyMessage {
    // Scalar values (all primitive / built-in types plus enums and messages)
    // have a getter and a presence check.

    /**
     * The getter will return the default value if the field is not present.
     * Even required fields that are missing will return a value here for
     * primitive values (except string and binary). The primitive defaults
     * are:
     *
     * bool: false,
     * i8 - i64: 0,
     * double: 0.0
     *
     * Note that boolean fields are prefixed with 'is', not 'get'.
     */
    public int getMyField();

    /**
     * The presence check will behave differently based on the presence
     * requirement to field type match. Required values always return true,
     * because in the built object, they are always present. Optional values
     * will return true if and only of the value was set in the builder.
     * And default values (no requirement indicator) will always be present
     * if it's a primitive value, and behave as optional if non-trivial
     * value (enum and message) unless an explicit default was set.
     */
    public boolean hasMyField();

    // ---- CONTAINERS ----

    /**
     * Containers will also have an extra method for getting number of entries.
     *
     * The entry count returns the number of entries in the container, or 0
     * if the container is not present.
     */
    public int numMyContainer();
}
```

Note that bool fields use `is` prefix instead of `get`, so the generated getter
method is `isMyBool()`. That is **not** the case in the builder, as a number of
`is*` methods are defined for other purposes, and using `get` there avoids
generating potential conflicts.

### Unions

Unions have a special accessor method called `unionField()`. The `unionField()`
method returns the field that was set for the union, or null if there was none.
It will return one of the `MyMessage._Field` enum values, so it is possible to
make a switch statement like:

```java
switch(union.unionField()) {
    case FIRST:
        // ... Do something.
        break;
    case SECOND:
        // ... Do something else.
        break;
}
```

### Building and Mutating Messages

Since the generated messages are immutable (they are never proper Java beans),
new messages has to be built using the associated `_Builder`. The builder can
be instantiated in three ways:

```java
// Use these:
MyMessage._Builder builder = MyMessage.builder();
MyMessage._Builder mutator = my_message.mutate();

// This is primarily there to let serializers etc have access to the message
// builders.
MyMessage._Builder builder = MyMessage.kDescriptor.builder();
```

The `Builder` extends the PMessageBuilder class, and will in addition have a
set of methods generated as field setters.

```java
// This is an inner class of MyMessage.
public static class _Builder {
    /**
     * The field setter will set the field value. For non-primitive values,
     * setting it to null will be the same as clearing the field. Native
     * fields will only use the primitive value type, and explicitly set
     * the value.
     */
    public _Builder setMyField(int value);

    /**
     * Clearing the field value back to null or the default value. A cleared
     * field is explicitly not set after the call.
     */
    public _Builder clearMyField();

    // -- primitives --

    /**
     * For convenience, a simple getter for primitive values and enums is
     * provided. Note that it is prefixed 'get' even for boolean fields.
     */
    public String getMyField();

    // -- messages --

    /**
     * Message fields on the other hand can return the message builder for
     * that field. Changes to the given builder will be reflected in the
     * built message.
     */
    public MyMessage._Builder mutableMyField();

    // ---- CONTAINERS ----

    /**
     * Explicitly setting the value and content of the container. This will
     * have as argument the most generic interface available for the desired
     * container (java.util.Collection for list and set, and java.util.Map
     * for map fields). Content is always replaced, and a null value is equal
     * to clearMyContainer().
     *
     * Note that both list and set containers have <code>Collection&lt;T&gt;</code>
     * as the input type here.
     */
    public _Builder setMyContainer(Collection<Integer> value);

    /**
     * Same as the default version.
     */
    public _Builder clearMyContainer();

    /**
     * For containers the mutable collection instance can be fetched with
     * the mutable* method. It will transform an immutable instance into
     * the appropriate mutable variant if needed.
     */
    public List<String> mutableMyContainer();

    // -- lists and sets --

    /**
     * Add entries to the list or set. Note that when adding by generic
     * collection there is no item type checking. And adding with the
     * (Type... values) will instantiate the container builder and
     * set the field even if there are not values, as if the subsequent
     * method was called with an empty collection.
     */
    public _Builder addToMyContainer(int... values);
    public _Builder addToMyContainer(Collection<Integer> values);

    // -- maps --

    /**
     * Put the key / value pair into the map.
     */
    public _Builder putInMyContainer(int key, int value);
}
```

When the `Builder` has been updated, you can call `build()` which builds and
returns the message regardless. Calling `valid()` will return true if and
only if the requirement of every field has been met (required fields must have
been set, others are not checked). Note that this makes it possible to build
invalid messages.

When setting a field value in a union builder, then that field becomes the
current field of the union, thus effectively unsets all other fields. This way
the *last* field set on a union builder, becomes it's value.
