---
layout: page
toc_title: "Fast Binary Serializer"
title: "Fast Binary Serializer Format"
category: dev
date: 2018-07-11 12:00:00
order: 11
---

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

Most integer numbers are encoded with [base-128](https://en.wikipedia.org/wiki/LEB128)
varints to ensure it uses a small wire footprint. Unsigned integers are
encoded directly with base-128, and signed are zigzag-encoded before
base-128 encoding.

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

## Service Calls

Service calls are encoded as a fixed 5-tuple of:

1. base-128 varint of (method name length << 3 | call type). The call type
   will determine the content of the message field.
2. method name data (utf-8 encoded, of length from `1.`).
4. base-128 varint sequence number.
5. The method wrapper message or exception.

## Message Encoding

The message is encoded as a stream of fields, ending in a field with ID 0
(type ignored), combined with a 3-bit field type. Note that the field type
only describes which *wire format* the value is encoded as.

* **0x00 = STOP**: Stop reading fields.
* **0x01 = NONE**: No value; Boolean false ( *bool*, *void* ).
* **0x02 = TRUE**: No value; Boolean true ( *bool* ).
* **0x03 = VARINT**: Zigzag encoded base-128 number ( *byte*, *i8*, *i16*, *i32*, *i64* ).
* **0x04 = FIXED_64**: 8 bytes, little endian encoded ( *double* ).
* **0x05 = BINARY**: base-128 encoded length + binary data ( *string*, *binary* ).
* **0x06 = MESSAGE**: enclosed message, terminated with field-type STOP ( *struct*, *union*, *exception* ).
* **0x07 = COLLECTION**: (base-128 encoded length N) + (tags) N * (item) ( *list*, *set*, *map* ).

The two values are combined as: `tag :== (field-id << 3) | type`, written as a
base-128 varint (not zigzag encoded), followed by the value as described.

### Container Encoding

Containers are encoded as a repeated set of fields, one for each value. In the
case of maps, there are 2 values per entry, one for the key and one for the
value, which also makes the length / size double that of the actual number of
entries.

Lists and sets are encoded the same way.

`[$field-id << 3 | 0x06] [base-128 $length] [$item-type] ($length * [$item-value])`

Maps are a bit more complicated as it contains keys and values os possibly
different types, so they are encoded like:

`[$field-id << 3 | 0x06] [base-128 2 * $length] [$key-type << 3 | $item-type] ($length * ([$key-value] [$item-value]))`

This way it will appear as a list of `2 * N` elements with alternating key and
value types. Since the type is a 3-bit value, both types fit in a 7-bit int and
is double encoded the same way the field + type is encoded. In order to fit boolean
values into the collection as values, and not a type-determined value, it is
stored as a base-128 varint (type 3) with value 0 or 1.
