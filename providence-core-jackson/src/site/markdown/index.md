Providence Core : Jackson
=========================

Code for handling extra types used in providence to help with jackson
serialization and deserlialization. It is really only needed to be
able to handle the `Binary` class from`net.morimekta.utils:ui-utils`.

In order to use enable the extra classes to be serialized, include
the `providence-core-jackson` module, and call:

```java
ObjectMapper mapper = new ObjectMapper();
ProvidenceModule.register(mapper);
```

Using the object mapper after this should just work even with binary
fields.