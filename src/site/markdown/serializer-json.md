Json Serializer Format
======================

The compact JSON serialization format is designed to be a simple format to
import / export data to and from other JSON based structures. It follows the
JSON data standard, and does not contain any unnecessary info per field.

There are two dimension variants when serializing the JSON:

- Field ID type:   NAME vs ID.
- Enum value type: NAME vs ID.

If field ID type is NAME the thrift field name will be used. Otherwise the
string value of the ID will be used. E.g. "my_field" vs "4";

If enum value type is NAME the enum will be serialized based on the value name.
Otherwise the value number will be used. E.g. "ENUM_VALUE" vs 5.

Binary values are encoded as strings using Base64 url safe encoding with no
padding. Strings are encoded using UTF-8 encoding.

## IDL - Data format

```
MESSAGE     :== MESSAGE_OBJ | MESSAGE_ARR

MESSAGE_OBJ :== '{' (FIELD-SPEC ','?)* '}'

MESSAGE_ARR :== '[' (FIELD-VALUE ','?)* ']'

FIELD-SPEC  :== '"' FIELD-ID '"' ':' FIELD-VALUE

FIELD-ID    :== STRING | NUMBER

FIELD-VALUE :== '"' STRING '"' | BOOLEAN | NUMBER | LIST | MAP | MESSAGE

LIST        :== '[' (VALUE ','?)* ']'

MAP         :== '{' (MAP-ENTRY ','?)* '}'

MAP-ENTRY   :== MAP-KEY ':' FIELD-VALUE

MAP-KEY     :== STRING | '"' (BOOLEAN | NUMBER) '"'

STRING      :== CHAR*

CHAR        :== [0x20 .. 0x7E] | [0xC1 - 0xFD] [0x81 .. 0xBF]+

NUMBER      :== [0-9]+ | ([0-9]+ | [0-9]* ('.' [0-9]+)) (('e' | 'E') '-'? [0-9]+)?
```

## Compact messages

Messages in providence are per default encoded as JSON objects. But in modes
where Field-Name encoding mode is required, lists of many small messages with
the same fields can have a huge overhead of field names.

Compact messages is a concept made to be able to use way less space for small
(compact) messages. The criteria for a message to be `compact` is:

* The message fields must be continuously numbered for 1 .. N.
* There cannot be a required field after a non-required field.
* Maximum number of fields are `10`.

Then runtime you can check if a message can be encoded as compact by calling
`msg.isCompact()`. The extra requirements on runtime is:

* The set fields must be in a continuous range of 1 .. N. So there cannot be a
  set field after an unset field.
  
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
} (compact = "")
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

## Map Limitations

Since map keys have to be encoded as strings in JSON, the JSON serializer
will only accept primitive types, enums and so-called `simple` messages
as map keys. A `simple` message is a message that:

* No field is a container; list, set, map.
* No field is a message; struct, union, exception.

The actual content of the message is irrelevant when it comes to simplicity.
