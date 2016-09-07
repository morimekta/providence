Providence Utils : Config
=========================

Utilities for handling providence models together with the config-util
library.

### PROPOSAL -- Providence Config

Providence Config aka `pvdcfg`.

The big question: own syntax : or figuring out groovy + providence.

Own syntax could be like:

```
include "filepath" as alias

name = package.Struct {
    key = "value"
    substruct {
       sub = "value"
    }
}

copy = alias.From

name2 = package.Struct(alias.name) {
    key = "value"

    // extending existing value, or creating new if not existing.
    // using getMutable("substruct") ...
    substruct {
    }

    // extending existing value.
    // using set("substruct", ...)
    substruct = {
    }

    // overwriting with extension of external.
    // using set("substruct", alias.get("other").mutate() ...)
    substruct = alias.other {
       // overwriting locally
       sub = "other"
    }
}
```

And using the command, output matching the pretty-print output from
providence.

```sh
$ pvdcfg -I . myfile.pvd print
name : {
  key : "value"
  substruct : {
    sub : "value"
  }
}
copy : {
  the_exact : "alias value"
}
name2 : {
  from_alias : "aliased"
  key : "value"
  substruct : {
    from_alias2 : "from alias"
    sub : "other"
  }
}
```

The interface is in itself pretty simple:

```java
TypeRegistry reg = new TypeRegistry();
// sadly all types needs to be registered, so a utility to register all
// subtypes are needed. 
reg.putRecursiveType(Named.kDescriptor);
reg.putRecursiveType(From.kDescriptor);

ProvidenceConfig cfg = new ProvidenceConfig(reg);
cfg.load("myfile.pvd");

Named name2 = cfg.get("name2");
```
