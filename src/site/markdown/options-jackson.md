Providence Jackson Serializable
===============================

Jackson is supported by using the Jackson 2+ `JsonSerialize` and `JsonDeserialize` annotations
on messages, and setting the `JsonValue` annotation for getting the numeric value on enums.
The serialized content from the jackson serialization is compatible with the `JsonSerializer`
protocol, and it can parse anything the providence `JsonSerializer` (and also the
`TSimpleProtocolSerializer`) can generate, including supporting the `json.compact` annotation.

It is triggered with the `jackson` generator option.

The generated code will now also require maven dependencies to jackson `2.8.+`:

```xml
<dependencies>
    <dependency>
        <groupId>com.fasterxml.jackson.core</groupId>
        <artifactId>jackson-databind</artifactId>
    </dependency>
    <dependency>
        <groupId>com.fasterxml.jackson.core</groupId>
        <artifactId>jackson-annotations</artifactId>
    </dependency>
    <dependency>
        <groupId>com.fasterxml.jackson.core</groupId>
        <artifactId>jackson-core</artifactId>
    </dependency>
</dependencies>
```
