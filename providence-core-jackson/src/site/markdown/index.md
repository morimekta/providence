Providence Core : Jackson
=========================

Code for handling extra types used in providence to help with jackson
serialization and deserialization. It is really only needed to handle the
`Binary` class from `net.morimekta.utils:ui-utils`.

In order to enable the extra classes to be serialized, include
the `providence-core-jackson` module, and call:

```java
class MyModule {
    public static final ObjectMapper MAPPER = new ObjectMapper();
    static {
        ProvidenceModule.register(mapper);
    }
}
```

Using the object mapper after this should just work even with binary
fields.
