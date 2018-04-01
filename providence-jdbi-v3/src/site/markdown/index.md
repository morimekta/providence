Providence Utils : Storage - JDBI v3
====================================

Module expanding the providence to utilize the java jdbi v3
interface. Note that there is an implicit contract between the
IDL field types and how they are stored.

Showing thrift field type on left side and SQL column type on right side.

| Thrift    | SQL        | Note                                                  |
|-----------|------------|-------------------------------------------------------|
| `bool`    | `BIT`      |                                                       |
| `byte`    | `TINYINT`  |                                                       |
| `i16`     | `SMALLINT` |                                                       |
| `i32`     | `INT`      |                                                       |
| `i64`     | `BIGINT`   |                                                       |
| `double`  | `DOUBLE`   |                                                       |
| `string`  | `VARCHAR`  |                                                       |
| `binary`  | `BINARY`   |                                                       |
| `enum`    | `INT`      | Using the enum value.                                 |
| `message` | `VARCHAR`  | Stored as JSON, handy for using with MySQL JSON type. |
| `list`    |            | Will fail in helpers .                                |
| `set`     |            | Will fail in helpers.                                 |
| `map`     |            | Will fail in helpers.                                 |

It is possible to use "better" types for most of the fields, but that
may also cause other problems down the road if the thrift messages are
also used in other wire transfer.

Some of the thrift types also support storing with other types. But using these
types will require you to specify the field to SQL type mapping when writing
values. Reading (see `MessageRowMapper`) will work fine regardless:

| Thrift    | SQL         | Note                                                    |
|-----------|-------------|---------------------------------------------------------|
| `i32`     | `TIMESTAMP` | Field value as `seconds` since epoch.                   |
| `i64`     | `TIMESTAMP` | Field value as `milliseconds` since epoch.              |
| `binary`  | `BLOB`      | Using blob storage for better handling of large values. |
| `binary`  | `VARCHAR`   | Encoded to string using `base64`.                       |
| `message` | `VARBINARY` | Serialized as `binary`.                                 |
| `message` | `BLOB`      | Serialized as `binary`.                                 |
| `message` | `CLOB`      | Serialized as `json`.                                   |

There are a number of helpers available, shown in approximate order
they would be used.

### MessageInserter

The MessageInserter is a helper class to handle inserting content from
messages into an SQL relation. The helper will only select values from
the message itself, not using nested structure or anything like that.

The MessageInserter is built in such a way that you can create it (even as a
static field), and use it any number of times with a handle
to do the pre-programmed insert. The execute method is thread safe, as
long as none of the modification methods are called.

```java
class MyInserter {
    private static final MessageInserter<MyMessage, MyMessage._Field> INSERTER =
            new MessageInserter.Builder<>("my_message")
                    .set(MyMessage.UUID, MyMessage.NAME)
                    .set("content", MyMessage.VALUE, Types.VARCHAR)  // E.g. binary to string.
                    .onDuplicateKeyUpdate(MyMessage.VALUE)
                    .build();

    private final Jdbi dbi;

    public MyInserter(Jdbi dbi) {
        this.dbi = dbi;
    }

    int insert(HandleMyMessage... messages) {
        try (Handle handle = dbi.open()) {
            return INSERTER.execute(handle, messages);
        }
    }
}
```

Or it can be handled in line where needed. The building process is pretty cheap,
so this should not be a problem unless it is called _a lot_ for very small
message.

```java
class MyInserter {
    int insert(HandleMyMessage... messages) {
        try (Handle handle = dbi.open()) {
            return new MessageInserter.Builder<MyMessage, MyMessage._Field>("my_message")
                    .set(MyMessage.UUID, MyMessage.NAME)
                    .set("content", MyMessage.VALUE, Types.VARCHAR)  // E.g. binary to string.
                    .onDuplicateKeyUpdateAllExcept(MyMessage.UUID)
                    .build()
                    .execute(handle, messages);
        }
    }
}
```

The rules for using this is pretty simple:

- All fields set must be specified before onDuplicateKey* behavior.
- Only one of `onDuplicateKeyIgnore` and `onDuplicateKeyUpdate*`
  methods can be called.
- `execute(...)` can be called any number of times, and is thread safe.

### MessageNamedArgumentFinder

When creating custom SQL queries and updates with JDBI, it is common
to use named arguments. This helper makes it possible to look up
arguments, and get proper argument handling (see `MessageFieldArgument` below)
using the unmodified field names. The named argument finder even
supports getting field values from contained messages.

```java
class MyFinder {
    List<User> findUsersNewerThan(Entity entity) {
         try (Handle handle = dbi.open()) {
             return handle.createQuery("SELECT * FROM users " +
                                       "WHERE created_ts > :e.user.created_ms")
                          .bindNamedArgumentFinder(new MessageNamedArgumentFinder("e", entity),
                                                   withType(User._Field.CREATED_MS, Types.TIMESTAMP))
                          .map(User.class)
                          .collect(Collectors.toList());
         }
    }
}
```

It is possible to specify field typing, but that is preferred to be set
through the `ProvidenceJdbi` helper class described toward the end here.

### MessageFieldArgument

What the `MessageNamedArgumentFinder` actually does in the background,
is finding a containing message, and creates a `MessageFieldArgument`
for it. The message field argument takes care of things like how to
encode a field value to JDBC values, or if it's truly not set.

```java
class MyFinder {
    List<User> findUsersNewerThan(Entity entity) {
         try (Handle handle = dbi.open()) {
             return handle.createQuery("SELECT * FROM users " +
                                       "WHERE created_ts > :created_ts")
                          .bind("created_ts", new MessageFieldArgument(entity.getUser(),
                                                                       User._Field.CREATED_MS,
                                                                       Types.TIMESTAMP))
                          .map(User.class)
                          .collect(Collectors.toList());
         }
    }
}
```


### MessageRowMapper

In the examples above it was assumed that JDBI automagically knew how
to map a result set to the User class. And in the case above, that would
**not** be the case. Instead you can use the `MessageRowMapper`
class that can mostly automatically map columns to fields, if both
names and types match the table at the top of this document.

The message-row mapper maps column names to fields (it does not need type
info, as that comes for free by the JDBC result set). And there is a magic
"all columns" mapping, `"*"` that will try to match all columns with a
field of the matching name. Note that since SQL is inherently case insensitive,
this may make some weird column to field name matching.

```java
class MyFinder {
    List<User> findUsersNewerThan(Entity entity) {
         try (Handle handle = dbi.open()) {
             return handle.createQuery("SELECT * FROM users " +
                                       "WHERE created_ts > :created_ts")
                          .bind("created_ts", new MessageFieldArgument(entity.getUser(),
                                                                       User._Field.CREATED_MS,
                                                                       Types.TIMESTAMP))
                          .map(new MessageRowMapper<>(User.kDescriptor, ImmutableMap.of(
                                  "*", null,  // match any column to field of same name.
                                  "created_ts", User._Field.CREATED_MS)  // But `created_ts` maps to `created_ms` field.
                          ))
                          .collect(Collectors.toList());
         }
    }
}

```

### ProvidenceJdbi

Helper class that has methods shortening the amount of written code
for using most of the helpers above. The names of the methods may seem
a bit unnatural unless you see it in context of where it should be used.
The point is to get as close to fluent programming when it comes to
using providence with JDBI as possible without requiring to generate
code or writing elaborate helpers yourself (then you'd just use JDBI).

```java
 class MyFinder {
     User findFindUpdatedUser(User user, Entity entity) {
          try (Handle handle = dbi.open()) {
              return handle.createQuery("SELECT * FROM mappings.default_mappings " +
                                        "WHERE id = :id AND updated_ts > :e.updated_ms")
                           .bind("id", toField(user, ID))
                           .bindNamedArgumentFinder(toMessage("e", entity,
                                                              withType(Enity._Field.UPDATED_MS, Types.TIMESTAMP))
                           .map(toMessage(User.kDescriptor,
                                          columnsFromAllFields(),
                                          withColumn("created_ts", User._Field.CREATED_MS),
                                          withColumn("updated_ts", User._Field.UPDATED_MS)))
                           .findFirst()
                           .orElse(null);
         }
    }
}
```

But note the same statement if all the field names and types match with
default:

```java
 class MyFinder {
     User findFindUpdatedUser(User user, Entity entity) {
          try (Handle handle = dbi.open()) {
              return handle.createQuery("SELECT * FROM mappings.default_mappings " +
                                        "WHERE id = :id AND updated_ms > :e.updated_ms")
                           .bind("id", toField(user, ID))
                           .bindNamedArgumentFinder(toMessage("e", entity)
                           .map(toMessage(User.kDescriptor))
                           .findFirst()
                           .orElse(null);
         }
    }
}
```
