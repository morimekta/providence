Providence Utils : Config
=========================

Providence config is a special config format for generating
providence message objects. The reason for having a specific
config format in this way is to solve the "structured + modular"
problem. If you're not familiar here is a short wrap-up.

#### The Problem of Structured Config

When choosing a config language (or config markup language), you
need to consider what you use the config for. But what we would really
like have four main properties:

1. `schema-defined`: The config has a strict schema that can be used
   to validate it, e.g. in testing pipelines. But the schema needs to
   be at least as modular as the config itself, and possible to
   define in a remote / distributed fashion.
2. `structured`: The config must have structure that can be utilized to
   group and pass a part of the whole. E.g. if you need 3 database
   connections, but each should just produce a DBI instance, that is
   an example of a good use for structured config: Each has a part of
   the config that is internally identical configuring that DB connection.
3. `type-safe`: The code that use the config should be type-safe, so
   at compile-time, I know if the value I look at is an integer or a
   string.
4. `modular`: The config must support to be be split into multiple files,
   that can be combined or merged into a single "config" without messing up
   the "type" something in the config is.

In addition we want to avoid a couple of dogmas that makes it difficult to
follow what goes on with the config: `arithmetics` and `scripting`. If you
need logic to generate your config, you should do that in a proper programming
or scripting language, and not bake it into the config itself. But if you
really need scripting, you should use pure `java`, `groovy` or similar.

- PS: This is one of the pitfalls of the `bcl` / `gcl` configuration language
  of Google (see
  [here](http://alexandria.tue.nl/extra1/afstversl/wsk-i/bokharouss2008.pdf)
  for example), which gives full arithmetic and scripting control to the
  config writer, which in turns had a tendency of making the config files
  almost unreadable and a pretty unpopular task of managing.

So what options do you have? None of the popular config markup languages
support all of these four requirements.

- `YAML`: Is a real structural config markup language. But is does not
  have native support for `1.` and `3.` And though it is no problem in merging
  multiple .yaml files, there is no real type-checking of the config
  input, which is pointing back to the problem in `1.`.
- `JSON`: Is just a structured data markup language, so though `2.` is a given,
  there is no option for commenting, which makes is a bad option for config in the
  first place. But with `JsonMerge` and `JsonPatch` you get a pretty
  wide toolset for solving `4.`. And with `JsonSchema` the problem in `1.`
  can be partially solved. So unless you can generate `jackson` POJO classes
  from the JSON schema, this is a no-go.
- `TOML`: Is similar to the windows INI files, but has more value typing in
  the config files, it is structured and has types in the config files themselves,
  but other than that falls pretty short.
- If you want to have a schema-defined, structured, type-safe config, you can
  choose `thrift`, `protobuf` or `providence` serialized formats. All of these
  fills `1.`, `2.` and `3.` natively. And they all support a "simple" modularization
  by merging or "overloading" messages. Ignoring the "readable and writable config
  format" part, there is the problem of modularizing a schema-defined system like
  that: **Where is the definition for what module, and how do you merge them?**
  And here lies the whole reason for `providence-config`.

If you want to have a config that is type-safe both in the written config file
input to the parser, to the code that uses that config, using a model builder
like providence should be a no-brainer. But when the config is this strictly
defined, making it truly modular becomes a non-trivial problem: We simply don't
want to have a single central definition of "the config", but be able to
build up the config for each application by modules used by the various parts
that it contains: it's libraries and local code.

This is the `providence` project, so guess what, it is the base definition.
And since providence support modular schema definitions, it's config system
needs to support modularity of the config in the same way.

### Config file Syntax

In general the config files use the suffix `.cfg`, but `.pvd` should be fine
too. In practice it should be irrelevant. But here is an overview over the
providence config file syntax:

- Comments follow the 'shell comment' syntax. Starts with a
  `#`, and ends in a newline. In that line, everything is allowed.
  The comment can start anywhere except inside a string literal.
- The config has three parts, which _must_ come in this order,
  where the two first are optional.
    - The `params`: A set of 'simple' values that can be referenced
      in the config.
    - The `includes`: Other config files included with an alias, so
      they too can be referenced from the config.
    - The `message`: The 'content' of the config itself.
- The `params` follow a simple 'map' syntax where the key must be
  a simple identifier `/[_a-z][_a-zA-Z0-9]/`, and a simple value (number,
  string, enum, boolean). The params value may be a declared (known)
  enum, with the double qualified identifier syntax `package.Name.VALUE`.
- The `include` section is a set of similarly included config files.
  Each file is given an `alias`. E.g. `include "other.cfg" as o` will
  make the 'o' reference point to the content of the "other.cfg" config.
- the `message` is a providence message, and is declared with the
  qualified typename (package.Name), and the content, following this
  syntax: `TYPENAME (':' EXTEND)? '{' FIELD_VALUE* '}'`, where the
  `FIELD_VALUE` part follows the 'pretty' serializer syntax (using the '='
  field-value separator), with added support for references for values.
  The references can only reference params or content from imported
  config files. Messages has four specific modes of value specification,
  specified with what comes after the field name.
    - '=' means "overwrite with", otherwise the parent (extended) message
      values will be used as the base message.
    - After the '=' there can be an optional reference to use as the base
      message instead of the default one.
    - The extension content is delimited by the '{' and '}' chars.
    - One of the reference or extend content *must* be present.

#### A note on field values

Note that both messages and maps can be extended, but lists and sets can
not (yet at least). This is because managing lists and sets is a bit more
complicated in the form of how to make modifications explicit, truly visible
and not confusing. E.g. if you need to remove a single element from a list
e.g. with `slice()`, there is no way of showing which element is actually removed
without referencing it with `.remove(value)`, and with syntax like that we
get into the whole  world of "scripting".

Example of config syntax:

```
params {
  name1 = "value"
  number = 12345.6789
}

include "filepath" as alias

package.Struct : alias {
    key = params.name1
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
    # Reaplcing with a reference that is extended.
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

### Java Interface

The interface for using this config in code should be fairly easy to use.
In order to read a simple config structure, you can use a code snippet like
this:

```java
class Loader {
    public Named load() {
        Map<String,String> params = new HashMap<>();
        // E.g. load params from flags here.
        
        TypeRegistry reg = new TypeRegistry();
        // sadly all types needs to be registered, so a utility to register all
        // subtypes are needed. 
        reg.putRecursiveType(Named.kDescriptor);
        reg.putRecursiveType(From.kDescriptor);
    
        ProvidenceConfig cfg = new ProvidenceConfig(reg, params);
        return cfg.load("myfile.cfg");
    }
}
```

### Sources Root

The providence config can be given a "sources root", each file included form the
config can be found in one of three ways:

- Absolute path: `/...`
- File in a source root: `${source-root}/...`.
- Relative to CWD of the running program: `...`

With this path inclusion with ways of overriding, it should be possible to change
what files are included based on a set of config source root directories, and swapping
out directories based on the current "mode" of operation.

Note that it is currently **not** possible to swap out individual config files without
using symlinks or similar, and it is not possible to load config directly from resources.
In those cases, you should use the "compiled" config output instead of the raw config
files.
