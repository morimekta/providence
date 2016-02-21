Fast Binary Serializer Format
=============================

The fast binary serializer format is designed to be fast and compact. It is
based upon the [Protocol Buffers](https://developers.google.com/protocol-buffers/docs/encoding)
wire format, which does a couple of tricks to be both fast and compact.
Note that this format is **not** compatible with protocol buffers. And in
order to fix that, the type numbers have been shifted to avoid half-baked
in-betweens.

My personal goal is to make this powerful enough to be the default encoding
for providence and possibly also for thrift proper. The reason the latter is
not very likely is a design-flaw in the thrift protocol libraries, which locks
the wire format to have to know the exact data type it represent (i.e. i16 vs
i64), even though it could be encoded the exact same way (the thrift JSON
protocol is a monstrosity for this reason alone).

## Number Encoding

Numbers are mostly encoded using the base 128 varint as described in the
protocol buffers encoding doc. The exception is `double` values which are
encoded using a fixed 64-bit buffer, with 1:11:52 (sign:exp:dec), and then
little-endian encoded unto the stream.

### Base 128 Varints

Most integer numbers are encoded with varint to ensure it uses a small
wire footprint.

### Zigzag Encoded Integers

All the scalar integer values are encoded with zigzag encoding as negative
numbers would all be encoded with 5 bytes in the simple base-128 format (for
i32, 10 bytes for i64). This is compact and efficient in most cases (except
when using 64-bit random numbers, though 10 bytes is not too much more than 8,
so the loss is still small.

| Original Value | Encoded Value |
|---------------:|--------------:|
|              0 |             0 |
|             -1 |             1 |
|              1 |             2 |
|             -2 |             3 |
|              2 |             4 |
|            ... |           ... |
|    -2147483646 |    4294967291 |
|     2147483646 |    4294967292 |
|    -2147483647 |    4294967293 |
|     2147483647 |    4294967294 |
|    -2147483648 |    4294967295 |

The encoding is done using the formula `(number << 1) ^ (number >> 31)` for i32
and `(number << 1) ^ (number >> 63)` for i64. Decoding is done using
`(value & 1) != 0 ? ~(value >>> 1) : value >>> 1`. Note
that `>>` is an arithmetic shift (keeping the sign bit), while `>>>` is a
logical shift (not keeping the sign bit).

## Message Encoding

The message is encoded as a stream of fields, ending in a field with ID 0
(type ignored), combined with a 3-bit field type. Note that the field type
only describes which *wire format* the value is encoded as.

* **0x00 = NONE**: No value; Boolean false ( *bool* ).
* **0x01 = TRUE**: No value; Boolean true ( *bool* ).
* **0x02 = VARINT**: Zigzag encoded base-128 number ( *byte*, *i8*, *i16*, *i32*, *i64* ).
* **0x03 = FIXED_64**: 8 bytes, little endian encoded ( *double* ).
* **0x04 = BINARY**: base-128 encoded length + binary data ( *string*, *binary* ).
* **0x05 = MESSAGE**: enclosed message, terminated with field-ID 0 ( *struct*, *union*, *exception* ).
* **0x06 = COLLECTION**: base-128 encoded length N + N * (tag + field) ( *list*, *set*, *map* ).
* **0x07** Unused.

The two values are combined as: `tag :== (field-id << 3) | type`, written as a
base-128 varint (not zigzag encoded), followed by the value as described.

### Container Encoding

Containers are encoded as a repeated set of fields, one for each value. In the
case of maps, there are 2 values per entry, one for the key and one for the
value, which also makes the length / size double that of the actual number of
entries.
