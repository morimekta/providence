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
| `string`  | `VARCHAR`  | Always UTF-8 encoded when sent to the SQL server      |
| `binary`  | `BINARY`   |                                                       |
| `enum`    | `INT`      |                                                       |
| `message` | `VARCHAR`  | Stored as JSON, handy for using with MySQL JSON type. |
| `list`    |            | Will fail in helpers                                  |
| `set`     |            | Will fail in helpers                                  |
| `map`     |            | Will fail in helpers                                  |

It is possible to use "better" types for most of the fields, but that
may also cause other problems down the road if the thrift messages are
also used in other wire transfer.

Some of the thrift types also support storing with other types. But using these
types will require you to specify the field to SQL type mapping when writing
values. Reading (see `MessageRowMapper`) will work fine regardless:

| Thrift    | SQL         | Note                                           |
|-----------|-------------|------------------------------------------------|
| `i32`     | `TIMESTAMP` | Field value as `seconds` since epoch.          |
| `i64`     | `TIMESTAMP` | Field value as `milliseconds` since epoch.     |
| `binary`  | `BLOB`      |                                                |
| `binary`  | `VARCHAR`   | Encoded with `base64`                          |
| `message` | `VARBINARY` | Serialized as `binary`                         |
| `message` | `BLOB`      | Serialized as `binary`                         |
| `message` | `CLOB`      | Serialized as `json`.                          |

There are a number of helpers available, shown in approximate order
they would be used.

### MessageInserter

TODO: Write

### MessageNamedArgumentFinder

TODO: Write

### MessageFieldArgument

TODO: Write

### MessageRowMapper

TODO: Write

### ProvidenceJdbi

Helper class that has methods shortening the amount of written code
for using most of the helpers above.