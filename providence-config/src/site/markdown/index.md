Providence Utils : Config
=========================

Providence config is a special config format for generating providence message
objects. The reason to have a special config format is to solve the "structured
+ modular" problem. If you're not familiar here is a short wrap-up.

#### The Problem of Strongly Typed Structured Config

When choosing a config language (or config markup language), you
need to consider what you use the config for. But we would like to have four
main properties:

1. `schema-defined`: The config has a strict schema that can be used to
   validate it, e.g. in testing pipelines. But the schema needs to be at least
   as modular as the config itself, and possible to define in a remote /
   distributed fashion, e.g. different git repositories contains different parts
   of the schema.
2. `structured`: The config must have structure that can be utilized to group
   and pass a part of the whole. E.g. if you need 3 database connections, but
   each should just produce a DBI instance, that is an example of a good use
   for structured config: Each has a part of the config that is internally
   identical configuring that DB connection.
3. `type-safe`: The code that uses the config should be type-safe, so I know at
   compile-time if the value I look at is an integer or a string. This is also
   a way to ensure that config values must be defined in the schema before use.
4. `modular`: The config must support being split into multiple files that
   can be combined or merged into a single "config" while respecting the
   "type" of the config items. The modular part has two variants; include
   and extend. Included config is something that is included as a part of the
   parsed config, and extended config is modifications on the same structure.

In addition we want to avoid a couple of misfeatures that make it difficult to
follow what is going on with the config: `arithmetics` and `scripting`. If you
need logic to generate your config, you should do that in a proper programming
or scripting language, and not bake it into the config itself. But if you
really need scripting, you should use `java`, `groovy` or similar.

- PS: This is one of the pitfalls of the `bcl` / `gcl` configuration language
  of Google (see
  [here](http://alexandria.tue.nl/extra1/afstversl/wsk-i/bokharouss2008.pdf)
  for example), which gives full arithmetic and scripting control to the
  config writer, which in turn had a tendency to make the config files
  almost unreadable and a pretty unpopular task of managing.

If you want to have a schema-defined, structured, type-safe config, you can
choose `thrift`, `protobuf` or `providence` serialized formats. All of these
fills `1.`, `2.` and `3.` natively. And they all support a "simple" modularization
by merging or "overloading" messages. Ignoring the "readable and writable config
format" part, there is the problem of modularizing a schema-defined system like
that: **Where is the definition for what module, and how do you merge them?**
And here lies the whole reason for `providence-config`.

If you want to have a config that is type-safe both in the written config file
input to the parser, to the code that uses that config, using a model builder
like providence should be a no-brainer. But when the config is this strictly
defined, making it truly modular becomes a non-trivial problem: We don't want
to simply have a single central definition of "the config", but to build up the
config for each application by modules used by the various parts that it
contains: its libraries and local code.

This is the `providence` project, so guess what, it is the base definition.
And since providence supports modular schema definitions, its config system
needs to support modularity of the config in the same way.

### Config compatibility

Providence is by its very definition backward compatible, and if reading in
non-strict modes, it is also *forward* compatible. Forward compatible means that
you can add new fields and references, and update the config files with content
from those fields without breaking older users of the same config files.

## Config file Syntax

In general the config files use the suffix `.cfg`, but `.pvd` should be fine
too. In practice it should be irrelevant. But here is an overview over the
providence config file syntax:

- Comments follow the 'shell comment' syntax. Starts with a `#`, and ends in a
  newline. In that line, everything is allowed.  The comment can start anywhere
  except inside a string literal.
- The config has three parts, which _must_ come in this order, where the two
  first are optional.
    - The `includes`: Other config files included with an alias, so they can be
      referenced from the config.
    - The `defines`: A set of values that can also be referenced in the config.
    - The `message`: The 'content' of the config itself.
- The `includes` section is a set of recursively included config files.  Each
  file is given an `alias`. E.g. `include "other.cfg" as o` will make the 'o'
  reference point to the content of the "other.cfg" config.  Files referenced
  in the include statements **MUST** be relative to the directory of the
  including file.
- The `defines` follow a simple 'map' syntax where the key must be a simple
  identifier `/[_a-zA-Z][_a-zA-Z0-9]/`, and a simple value (number, string, enum,
  boolean). The params value may be a declared (known) enum, with the double
  qualified identifier syntax `package.Name.VALUE`.
- The `message` is a providence message, and is declared with the qualified
  typename (package.Name), and the content, following this syntax: `TYPENAME
  (':' EXTEND)? '{' FIELD_VALUE* '}'`, where the `FIELD_VALUE` part follows the
  'pretty' serializer syntax (using the '=' field-value separator), with added
  support for references for values.  The references can only reference params
  or content from imported config files. Messages has four specific modes of
  value specification, specified with what comes after the field name.
    - '=' means "overwrite with", otherwise the parent (extended) message
      values will be used as the base message.
    - After the '=' there can be an optional reference to use as the base
      message instead of the default one.
    - The extension content is delimited by the '{' and '}' chars.
    - One of the reference or extend content *must* be present.

#### A note on field values

Note that both messages and maps can be extended, but lists and sets can not
(yet at least). This is because managing lists and sets is a bit more
complicated in the form of how to make modifications explicit, truly visible
and not confusing. E.g. if you need to remove a single element from a list e.g.
with `slice()`, there is no way of showing which element is actually removed
without referencing it with `.remove(value)`, and with syntax like that we get
into the whole  world of "scripting".

Example of config syntax:

```
include "filepath" as alias

def {
  name1 = "value"
  number = 12345.6789
}

def other_num = 4321
def alias = package.Struct {
  key = "value"
}

package.Struct : alias {
    key = name1
    key2 = 321

    # Extending the existing message
    substruct {
       sub = "value"
    }
    # Overwriting with a new message
    substruct = {
       sub = "value"
    }
    # Replacing with a reference
    substruct = alias.sub2
    # Replacing with a reference that is extended.
    substruct = alias.sub2 {
       sub = "value"
    }
}
```

And using the command, output matching the pretty-print output from
providence.

```sh
$ pvdcfg -I . print myfile.cfg
{
  key = "value"
  key2 = 321
  substruct = {
    sub = "value"
  }
}
```

Note that when using the `resolveConfig("name", parent)` method, the config
system does not support that the main struct in the config inherits (or
extends) anything itself, and the config system will not keep a reference to
the config supplier, as it does not really know if this is the same inherited
or a different inherited config compared to other `resolveConfig` calls for the
same file. In this case the config supplier must be cached by the caller, who
probably only needs to set this up once at program startup.

## Java Interface

The interface for using this config in code should be fairly easy to use.
In order to read a simple config structure, you can use a code snippet like
this:

```java
class Loader {
    public Named load() {
        WritableTypeRegistry reg = new SimpleTypeRegistry();
        // sadly all types needs to be registered, so a utility to register all
        // subtypes are needed. 
        reg.registerRecursive(Named.kDescriptor);
        reg.registerRecursive(From.kDescriptor);
        
        ProvidenceConfig cfg = new ProvidenceConfig(reg);
        return cfg.getConfig("myfile.cfg");
    }
}
```

### Includes

Files referenced in the include statements must be relative to the directory
of the including file.

## Advanced Usage

It is possible to get more out of the configs by handling the config suppliers
directly. This enables the program to react to config updates, and to always
have the latest version of the config available.

```java
class Program implements ConfigListener<Service,Service._Field> {
    ProvidenceConfig providenceConfig;
    Service service;
    
    public Program() {
        WritableTypeRegistry reg = new SimpleTypeRegistry();
        // sadly all types needs to be registered, so a utility to register all
        // subtypes are needed. 
        reg.registerRecursive(Named.kDescriptor);
        reg.registerRecursive(From.kDescriptor);
        reg.registerRecursive(Service.kDescriptor);
        
        this.providenceConfig = new ProvidenceConfig(reg);
        
        ConfigSupplier<Service,Service._Field> serviceSupplier = providenceConfig.resolveConfig("my_service.cfg");
        serviceSupplier.addListener(this);
        this.service = serviceSupplier.get();
    }
    
    @Override
    public void onConfigChange(@Nonnull Service update) {
        this.service = update;
        // and react to the actual changes...
    }
}
```

There are also other config suppliers available to make the providence config
system more powerful.

- **[FixedConfigSupplier]:** Just provides some config message as a config
  supplier. Will never change, and never trigger config listeners.
- **[ResourceConfigSupplier]:** Loads a system resource and provides it as a
  config supplier.  The config never changes, and never triggers config
  listeners.
- **[ReferenceConfigSupplier]:** Uses a parent config and finds a reference
  (contained) message within the parent using a reference path. the path is the
  '.' concatenation of the field names.  This supplier will forward changes in
  the parent config, but will not check for local changes.
- **[OverrideConfigSupplier]:** Takes a parent config and overrides it with
  values based on an override value map. Can only override "leaf" values, not
  whole messages. Uses the same reference path as the
  `ReferenceConfigSupplier`, and tries as best it can to parse the string value
  given.  Handy to be able to override some values based on command line args
  or similar.
  
And in addition a config supplier meant to be used in testing called
`TestConfigSupplier`. It exposes a `testUpdate` method that triggers updates
the same way as the other updating configs.
