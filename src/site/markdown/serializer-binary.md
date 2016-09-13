Binary Serializer Format
========================

This is the original thrift binary protocol implementation for providence.
The serializer serializes all numbers as big-endian, using no zig-zag encoding
etc. It has a "versioning" system, but there is only one version of the original
protocol; v1.

Per default the binary serializer will write the versioned data stream, but accept
both versioned with v1 protocol and unversioned when deserializing. Setting the
serializer to strict mode will only accept the desired protocol version.

## Service calls

Service calls comes in two versions, versioned and unversioned.

```
CALL            :== VERSIONED | UNVERSIONED

VERSIONED       :== VERSION 0x00 CALL_TYPE [32-bit name-size] [name] [32-bit sequence] MESSAGE

UNVERSIONED     :== [32-bit name-size] [name] CALL_TYPE [32-bit sequence] MESSAGE

VERSION         :== 0x80 VERSION_NO

VERSION_NO      :== 0x01

CALL_TYPE       :== 0x01 | 0x02 | 0x03 | 0x04
```

The type of call is detected by reading the first 4 bytes as a signed int. If
the value is less than 0, it is versioned (hence the 0x80 byte), otherwise it
is unversioned. The number is split into version and call type by:

* `version   = (0x7FFF0000 & num) >>> 16`
* `call_type = (byte) (0x000000FF & num)`

The 'middle' 0 byte is currently not used. Each call type also determines what
the 'message' field can be, and if the call expects a reply or not.

```
CALL       0x01 :== method request wrapper

REPLY      0x02 :== method response wrapper

EXCEPTION  0x03 :== application exception

ONEWAY     0x04 :== message request wrapper (no reply expected)
```

The call and oneway types are both method calls, where the oneway type does not
expect, or require a reply. The call require a relply eve in the return type is
'void'. This is where a 'field' type can be void.

*__TODO:__ Research original thrift reply-type handling of void return types.*

_**TODO:** Update thrift-serializers or client / server to use this behavior._

## Messages

Messages are a stream of `fields`, and terminated with a 0-byte. Each field
is self-contained. The null byte is in the place of the field type-id.

```idl
MESSAGE         :== [FIELD]* STOP
```

## Fields

Each field is encoded as follows:

```
STOP            :== 0x00

FIELD           :== TYPE FIELD_ID VALUE

FIELD_ID        :== 32-bit integer (the field ID)
```

### Encoding of values

In the binary protocol, each thrift fields value type is mapped to a type ID.
It is mostly a 1-to-1 mapping, but with a few exceptions.

The field types are:

```
stop            :== 0

void            :== 1

bool            :== 2

byte            :== 3

double          :== 4

i16             :== 6

i32 | enum      :== 8

i64             :== 10

string | binary :== 11

message         :== 12

map             :== 13

set             :== 14

list            :== 15
```

And the values are encoded as:

```
VALUE           :== VOID | BOOL | BYTE | I16 | I32 | I64 | DOUBLE | ENUM |
                    STRING | BINARY | MESSAGE | MAP | SET | LIST

VOID            :== [empty]

BOOL            :== (8-bit: 0 for false, 1 for true)

BYTE            :== (8-bit signed)

I16             :== (16-bit signed)

I32             :== (32-bit signed)

ENUM            :== (32-bit signed of value)

I64             :== (64-bit signed)

DOUBLE          :== (64-bit 1:11:52 encoded double, 64-bit float)

STRING          :== (32-bit size) (utf-8 encoded data)

BINARY          :== (32-bit size) (raw data)

MESSAGE         :== as a message

MAP             :== (8-bit key type) (8-bit item type) (32-bit size) N * ([key] [item])

LIST | SET      :== (8-bit item type) (32-bit size) N * (item)
```
