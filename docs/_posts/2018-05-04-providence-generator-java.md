---
layout: page
toc_title: "Java Generator"
title: "Code Generator for Java"
category: bld
date: 2018-05-04 12:00:00
order: 4
---

The java generator is the core of providence, and should act as the source of
truth for all other generated code. The generated code is described at depth
in [Providence Models](../pvd/models.html) and in [Providence Services](../pvd/services.html).

Options available in the Java generator:

- `android`: Adds [android.os.Parcelable]
  to messages.
- `jackson`: Adds [Jackson 2.x] serialization
  annotations (tested on Jackson 2.7.2 and later).
- `no_rw_binary`: Removes generated code to serialize and deserialize the
  [binary serializer protocol]. This can be done if
  not needed on limited platforms to reduce code size.
- `hazelcast_portable`: Adds support for
  [hazelcast portable] and associated functionality.
  This only adapts the annotated part of the declared thrift.

Providence Android Parcelable
=============================

Providence supports android `Parcelable` serialization. Adding the `android`
generator param will add the `android.os.Parcelable` interface to *all*
messages, and add a static `CREATOR` field for the parcelable creator.

```java
class MyMessage extends PMessage<MyMessage,MyMessage._Field> implements Parcelable {
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel) {
        // ...
    }

    public static final Parcelable.Creator<MyMessage> CREATOR = new Parcelable.Creator<>() {
        // ...
    };
}
```

This way the generated objects can be used as stateful objects when sending
intents between android processes.


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
