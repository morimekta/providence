Compact Binary Format
=====================

The compact binary serialization format is designed to be binary compact, which
requires the deserializer to be pretty aware of the model structure to format
data correctly. It is still self-describing, so unknown field can be skipped
safely.

# IDL - Data format

<pre>
&lt;message&gt;            :== &lt;field-spec&gt;* &lt;message-end&gt;

&lt;message-end&gt;        :== UNSIGNED:16 = 0x0000

&lt;field-spec&gt;         :== &lt;field-id&gt; &lt;field-opts&gt; &lt;field-value-length&gt;? &lt;field-value&gt;?

&lt;field-id&gt;           :== UNSIGNED:16

&lt;field-opts&gt;         :== &lt;field-type&gt; &lt;field-flags&gt;

&lt;field-type&gt;         :== UNSIGNED:4

&lt;field-flags&gt;        :== UNSIGNED:4

&lt;field-value-length&gt; :== UNSIGNED:8 | UNSIGNED:16 | UNSIGNED:24 | UNSIGNED:32

&lt;field-value&gt;        :== &lt;number&gt; | &lt;binary&gt; | &lt;message&gt; | &lt;collection&gt;

&lt;collection&gt;         :== &lt;list&gt; | &lt;map&gt;

&lt;list&gt;               :== (&lt;collection-entry&gt;)* &lt;collection-end&gt;

&lt;map&gt;                :== (&lt;map-key&gt; &lt;map-value&gt;)* &lt;ollection-end&gt;

&lt;map-key&gt;            :== &lt;collection-entry&gt;

&lt;map-value&gt;          :== &lt;collection-entry&gt;

&lt;collection-entry&gt;   :== &lt;field-opts&gt; &lt;field-value-length&gt;? &lt;field-value&gt;?

&lt;collection-end&gt;     :== UNSIGNED:8 = 0x00

&lt;number&gt;             :== SIGNED:8 | SIGNED:16 | SIGNED:32 | SIGNED:64 | DOUBLE:64

&lt;binary&gt;             :== BYTE*
</pre>

# Field Type spec.

Abailable field types:

1. `BOOLEAN`    - Store a single bit of data in the 'flags' portion.
2. `INTEGER`    - Store 1, 2, 4 or 8 bytes of signed integer data.
3. `DOUBLE`     - Store 8 bytes of floating point data.
4. `BINARY`     - Store N bytes of binary data.
5. `MESSAGE`    - Store a message (reflective).
6. `COLLECTION` - Store a collection of data items.
7. `[unused]`   - Reserved for "ANY" which is a type-agnostic message holder with associated metadata.

Field type IDs 8 to 15 are not used (yet).

## BOOLEAN

* flags = `[...t]` where `[t]` is the boolean value.

## INTEGER

* flags = `[..ll]` where `[l]` determines the value size:
  - 0: 1 byte  (byte)
  - 1: 2 bytes (i16 / short)
  - 2: 4 bytes (i32 / int)
  - 3: 8 bytes (i64 / long)

## DOUBLE

The double is standard `[1:11:52]` encoded then little-endian serialized onto the byte stream.

## BINARY

* flags = `[.ell]` where `[ll] + 1` is the value-length byte count (1..4 bytes), and `[e]` is encoding info:
  - 0: `ISO-8859-1` (used for binary)
  - 1: `UTF-8` (used for strings)

## MESSAGE

The contained message is encoded same format as the whole message.

## COLLECTION

 * flags `[...t]` where if `[t]` is 1, then it's a map, otherwise a list or set
   (binary compatible).
