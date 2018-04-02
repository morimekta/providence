---
layout: page
toc_title: "Generator"
title: "Code Generator"
category: bld
date: 2018-05-03 12:00:00
order: 3
---

Common library code for the providence generator. This contains some common
utilities used when generating code, and the most basic generator that
just prints out the JSON representation of the thrift file.

## Providence Generator : JSON

The JSON generator generates .json files that match with the thrift definition.
The model is identical to the reflection model used (see `providence-reflect` for
reference). It uses the `JsonSerializer` with named fields and enums. It simply
prints out the ThriftDocument verbatim. Example (excerpt from the json output of
the thrift definition itself):

```json
{
  "program_name": "my_program",
  "includes": [
    "other_program.json"
  ],
  "namespaces": {
    "java": "net.morimekta.providence.test",
    "js": "morimekta.providence"
  },
  "decl": [
    {
      "decl_enum": {
        "name": "StructVariant",
        "comment": "...",
        "values": [
          {
            "name": "STRUCT",
            "id": 1
          },
          {
            "name": "UNION"
          },
          {
            "name": "EXCEPTION"
          }
        ]
      }
    },
    {
      "decl_struct": {
        "name": "EnumValue",
        "fields": [
          {
            "key": 1,
            "type": "string",
            "name": "comment"
          }
        ]
      }
    }
  ]
}
```
