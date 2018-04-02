---
layout: page
toc_title: "JSON Serializer"
title: "JSON Serializer Format"
category: dev
date: 2018-07-12 12:00:00
order: 12
---

The compact JSON serialization format is designed to be a simple format to
import / export data to and from other JSON based structures. It follows the
JSON data standard, and does not contain any unnecessary info per field.

There are two dimension variants when serializing the JSON:

- Field ID type:   `NAME` vs `ID`.
- Enum value type: `NAME` vs `ID`.

If field ID type is NAME the thrift field name will be used. Otherwise the
string value of the ID will be used. E.g. "my_field" vs "4";

If enum value type is NAME the enum will be serialized based on the value name.
Otherwise the value number will be used. E.g. "ENUM_VALUE" vs 5.

Binary values are encoded as strings using Base64 url safe encoding with no
padding. Strings are encoded using UTF-8 encoding.

## JSON format

The `JsonSerializer` tries as best to conform to standard JSON formatting, so that
each message (struct, exception, union) are encoded as a JSON object, where the
field keys are the field names (or ID), and the values are the field value.

* Missing fields are omitted.
* Maps **must** have it's key encoded in a string. Strings and binaries use the normal
  formatting, and all others are JSON serialized values and then string escaped and
  quoted.

With the `NAME` type encoding, string names are used for field and enum values. This
can be used to read classical JSON files or use (and serve) REST services.

```json
{
  "my_string": "my-string",
  "my_number": 13579,
  "my_boolean": false,
  "my_enum": "ENUM_VALUE",
  "map": {
     "12": 12
  }
}
```

With the `ID` type encoding, numeric values are used for field and enum values.
The format is pretty compact, but is not supported by any other libraries (AFAIK).
The format can be mixed with `NAME`, e.g. using named fields and enum ID values.

```json
{
  "1": "my-string",
  "2": 13579,
  "3": false,
  "4": 42,
  "5": {
    "12": 12
  }
}
```
### Specifics on map keys

Since map keys have to be encoded as strings in JSON, the JSON serializer
will only accept primitive types, enums and so-called `simple` messages
as map keys. A `simple` message is a message that:

* No field is a container; list, set, map.
* No field is a message; struct, union, exception.

The actual content of the message is irrelevant when it comes to simplicity.

### Compact messages

Compact JSON messages is a concept made to be able to use way less space for small
(compact) messages. The criteria for a message to be `json.compact` is:

- Only structs may be compact (not union or exception).
- May have a maximum of 10 fields.
- Fields **must** be numbered 1 .. N.
- A required field **may not** come after an optional field.

When the compact struct is serialized the serializer may choose to use a
different serialization format that serializes the first M fields of the struct
in order. E.g. in JSON a compact struct may be serialized as an array if (and
only if).

Compactible messages will have a `jsonCompact()` method that determines if the
message is compact for serialization. The descriptor will have a similar
`isJsonCompactible()` method which determines if the message can be
deserialized with the compact format.

To make a struct compact, add the `json.compact = ""` annotation to the struct.
This will still allow thrift compiler to parse the .thrift files. This applies
both to using the `JsonSerializer` and using the `jackson` java generator
option with a jackson JSON serializer and deserializer.

```thrift
struct Compact {
   1: string my_string;
   2: i32 my_number;
   3: bool my_boolean;
} (json.compact = "")
```

#### Compact chances to serialization

Messages in providence are per default encoded as JSON objects. But in modes
where Field-Name encoding mode is required, lists of many small messages with
the same fields can have a huge overhead of field names.

- For the 'compact' serialization to take effect, the first M fields must be set,
  and no other fields may be set. E.g.:
    * If 1, 2 abd 3 are set, and 4, 5 are not set, then compact is used.
    * If 1, 2 and 4 are set, and 3, 5 are not set, compact is **not** used.

When encoding the message as `compact`, array notation is used instead of
object notation. So the message of 62 significant bytes:

```json
{
  "my_string": "my-string",
  "my_number": 13579,
  "my_boolean": false
}
```

Can be encoded as 24 significant bytes instead:

```json
[
  "my-string",
  1357,
  false
]
```

The compact message notation works regardless of the Field-ID mode of the
serializer.

## Service Calls

JSON Service calls are done in a pretty compact way. It follows the 'compact'
struct definition, representing the call with a struct like:

```thrift
enum CallType {
  call = 1,
  reply = 2,
  exception = 3,
  oneway = 4
}

struct Call {
  1: required string name
  2: required CallType type
  3: required i32 sequence
  4: required struct message
} (json.compact = "")
```

Where *what* the message struct is is determined by the call type. See definition
of `fast-binary` and `binary` for details on service calls. An example service
call would look like:

```json
[
  "myMethod",
  "call",
  79,
  {
    "param1": 42
  }
]
```

## JSON IDL - Data format

```
MESSAGE     ::= MESSAGE_OBJ | MESSAGE_ARR

MESSAGE_OBJ ::= '{' (FIELD_SPEC ','?)* '}'

MESSAGE_ARR ::= '[' (FIELD_VALUE ','?)* ']'

FIELD_SPEC  ::= '"' FIELD_ID '"' ':' FIELD_VALUE

FIELD_ID    ::= STRING | NUMBER

FIELD_VALUE ::= '"' STRING '"' | BOOLEAN | NUMBER | LIST | MAP | MESSAGE

LIST        ::= '[' ((VALUE (',' VALUE )*)? ']'

MAP         ::= '{' (MAP_ENTRY (',' MAP_ENTRY)*)? '}'

MAP_ENTRY   ::= MAP-KEY ':' FIELD-VALUE

MAP_KEY     ::= STRING | '"' (BOOLEAN | NUMBER) '"'

STRING      ::= CHAR*

CHAR        ::= [0x20 .. 0x7E] | [0xC1 - 0xFD] [0x81 .. 0xBF]+

NUMBER      ::= [0-9]+ | ([0-9]+ | [0-9]* ('.' [0-9]+)) (('e' | 'E') '-'? [0-9]+)?
```
