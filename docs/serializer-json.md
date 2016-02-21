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

CHAR        :== BYTE = [0x20 .. 0x7E]

NUMBER      :== [0-9]+ | ([0-9]+ | [0-9]* ('.' [0-9]+)) (('e' | 'E') '-'? [0-9]+)?
```
