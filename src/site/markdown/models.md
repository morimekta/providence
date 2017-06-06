Providence Models
=================

The generated java interface is trying to follow the core java standard as
much as possible. There are some exceptions, but these are designed to be
non-conflicting with any other possible generated method name, and to
separate static and non-static methods without weird naming schemes.

## Messages

All message typed implements the `PMessage` interface,
and contains a number of generated methods for accessing content and building
new messages. All the objects are generated to be `immutable`, but with use
of deeply nested containers that may be broken to some extent. The concept
of `simple` messages was introduces to distinguish between structs that
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
     * i8 - i64: 0
     * double: 0.0
     */
    public int getMyField();

    /**
     * The presence check will behave differently based on the presence
     * requirement to field type match. Required values always return true,
     * because in the built object, they are always present. Optional values
     * will return true if and only of the value was set in the builder.
     * And default values (no requirement indicator) will always be present
     * if it's a primitive value, and behave as optional if non-primitive
     * value (string, binary, enum and message).
     */
    public boolean hasMyField();

    // ---- CONTAINERS ----

    /**
     * Containers will also have an extra method.
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
new messages has to be built using the associated `Builder`. The builder can
be instantiated in three ways:

```java
// Use these:
MyMessage._Builder builder = MyMessage.builder();
MyMessage._Builder mutator = my_message.mutate();

// This is primarily there to let serializers etc have access to the message
// builders.
MyMessage._Builder builder = MyMessage.kDescriptor.factory().builder();
```

The `Builder` extends the PMessageBuilder class, and will in addition have a
set of methods generated as field setters.

```java
// This is an inner class of MyMessage.
public static class _Builder {
    /**
     * The field setter will set the field value. For non-primitive values,
     * setting it to null will be the same as clearing the field. Primitive
     * fields will only use the primitivy value type, and explicitly set
     * the value.
     */
    public _Builder setMyField(int value);

    /**
     * Clearing the field value back to null or the default value. A cleared
     * field is explicitly not set after the call.
     */
    public _Builder clearMyField();

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

    // -- lists and sets --

    /**
     * Add entries to the list or set. The field is present even if no values
     * were given, and is still empty. Values must be valid, otherwise
     * ClassCastException is thrown.
     */
    public _Builder addToMyContainer(int... values);

    // -- maps --

    /**
     * Put the key / value pair into the map. The field is marked as present
     * (key / value pair must be valid, otherwise ClassCastException is
     * thrown).
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
