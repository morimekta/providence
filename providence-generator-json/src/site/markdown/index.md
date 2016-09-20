Providence Generator : JSON
===========================

The JSON generator generates .json files that match with the thrift definition.
The model is identical to the reflection model used (see `providence-reflect` for
reference). It uses the `JsonSerializer` with named fields and enums. It simply
prints out the ThriftDocument verbatim. Example (excerpt from the json output of
the thrift definition itself):

```json
{
  "package": "net.morimekta.providence",
  "includes": [
    "other_package"
  ],
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
