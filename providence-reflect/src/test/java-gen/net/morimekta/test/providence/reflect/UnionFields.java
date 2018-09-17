package net.morimekta.test.providence.reflect;

@SuppressWarnings("unused")
@javax.annotation.Generated("providence-maven-plugin")
@javax.annotation.concurrent.Immutable
public class UnionFields
        implements net.morimekta.providence.PUnion<UnionFields,UnionFields._Field>,
                   Comparable<UnionFields>,
                   java.io.Serializable,
                   net.morimekta.providence.serializer.binary.BinaryWriter {
    private final static long serialVersionUID = -4125227148631020921L;

    private final static boolean kDefaultBooleanValue = false;
    private final static byte kDefaultByteValue = (byte)0;
    private final static short kDefaultShortValue = (short)0;
    private final static int kDefaultIntegerValue = 0;
    private final static long kDefaultLongValue = 0L;
    private final static double kDefaultDoubleValue = 0.0d;

    private final transient Boolean mBooleanValue;
    private final transient Byte mByteValue;
    private final transient Short mShortValue;
    private final transient Integer mIntegerValue;
    private final transient Long mLongValue;
    private final transient Double mDoubleValue;
    private final transient String mStringValue;
    private final transient net.morimekta.util.Binary mBinaryValue;
    private final transient net.morimekta.test.providence.reflect.Value mEnumValue;
    private final transient net.morimekta.test.providence.reflect.CompactFields mCompactValue;

    private transient final _Field tUnionField;

    private volatile transient int tHashCode;

    // Transient object used during java deserialization.
    private transient UnionFields tSerializeInstance;

    /**
     * @param value The union value
     * @return The created union.
     */
    public static UnionFields withBooleanValue(boolean value) {
        return new _Builder().setBooleanValue(value).build();
    }

    /**
     * @param value The union value
     * @return The created union.
     */
    public static UnionFields withByteValue(byte value) {
        return new _Builder().setByteValue(value).build();
    }

    /**
     * @param value The union value
     * @return The created union.
     */
    public static UnionFields withShortValue(short value) {
        return new _Builder().setShortValue(value).build();
    }

    /**
     * @param value The union value
     * @return The created union.
     */
    public static UnionFields withIntegerValue(int value) {
        return new _Builder().setIntegerValue(value).build();
    }

    /**
     * @param value The union value
     * @return The created union.
     */
    public static UnionFields withLongValue(long value) {
        return new _Builder().setLongValue(value).build();
    }

    /**
     * @param value The union value
     * @return The created union.
     */
    public static UnionFields withDoubleValue(double value) {
        return new _Builder().setDoubleValue(value).build();
    }

    /**
     * @param value The union value
     * @return The created union.
     */
    public static UnionFields withStringValue(String value) {
        return new _Builder().setStringValue(value).build();
    }

    /**
     * @param value The union value
     * @return The created union.
     */
    public static UnionFields withBinaryValue(net.morimekta.util.Binary value) {
        return new _Builder().setBinaryValue(value).build();
    }

    /**
     * @param value The union value
     * @return The created union.
     */
    public static UnionFields withEnumValue(net.morimekta.test.providence.reflect.Value value) {
        return new _Builder().setEnumValue(value).build();
    }

    /**
     * @param value The union value
     * @return The created union.
     */
    public static UnionFields withCompactValue(net.morimekta.test.providence.reflect.CompactFields value) {
        return new _Builder().setCompactValue(value).build();
    }

    /**
     * @param value The union value
     * @return The created union.
     */
    public static UnionFields withCompactValue(net.morimekta.test.providence.reflect.CompactFields._Builder value) {
        return withCompactValue(value == null ? null : value.build());
    }

    private UnionFields(_Builder builder) {
        tUnionField = builder.tUnionField;

        mBooleanValue = tUnionField == _Field.BOOLEAN_VALUE ? builder.mBooleanValue : null;
        mByteValue = tUnionField == _Field.BYTE_VALUE ? builder.mByteValue : null;
        mShortValue = tUnionField == _Field.SHORT_VALUE ? builder.mShortValue : null;
        mIntegerValue = tUnionField == _Field.INTEGER_VALUE ? builder.mIntegerValue : null;
        mLongValue = tUnionField == _Field.LONG_VALUE ? builder.mLongValue : null;
        mDoubleValue = tUnionField == _Field.DOUBLE_VALUE ? builder.mDoubleValue : null;
        mStringValue = tUnionField == _Field.STRING_VALUE ? builder.mStringValue : null;
        mBinaryValue = tUnionField == _Field.BINARY_VALUE ? builder.mBinaryValue : null;
        mEnumValue = tUnionField == _Field.ENUM_VALUE ? builder.mEnumValue : null;
        mCompactValue = tUnionField != _Field.COMPACT_VALUE
                ? null
                : builder.mCompactValue_builder != null ? builder.mCompactValue_builder.build() : builder.mCompactValue;
    }

    public boolean hasBooleanValue() {
        return tUnionField == _Field.BOOLEAN_VALUE && mBooleanValue != null;
    }

    /**
     * @return The <code>booleanValue</code> value
     */
    public boolean isBooleanValue() {
        return hasBooleanValue() ? mBooleanValue : kDefaultBooleanValue;
    }

    /**
     * @return Optional of the <code>booleanValue</code> field value.
     */
    @javax.annotation.Nonnull
    public java.util.Optional<Boolean> optionalBooleanValue() {
        return java.util.Optional.ofNullable(mBooleanValue);
    }

    public boolean hasByteValue() {
        return tUnionField == _Field.BYTE_VALUE && mByteValue != null;
    }

    /**
     * @return The <code>byteValue</code> value
     */
    public byte getByteValue() {
        return hasByteValue() ? mByteValue : kDefaultByteValue;
    }

    /**
     * @return Optional of the <code>byteValue</code> field value.
     */
    @javax.annotation.Nonnull
    public java.util.Optional<Byte> optionalByteValue() {
        return java.util.Optional.ofNullable(mByteValue);
    }

    public boolean hasShortValue() {
        return tUnionField == _Field.SHORT_VALUE && mShortValue != null;
    }

    /**
     * @return The <code>shortValue</code> value
     */
    public short getShortValue() {
        return hasShortValue() ? mShortValue : kDefaultShortValue;
    }

    /**
     * @return Optional of the <code>shortValue</code> field value.
     */
    @javax.annotation.Nonnull
    public java.util.Optional<Short> optionalShortValue() {
        return java.util.Optional.ofNullable(mShortValue);
    }

    public boolean hasIntegerValue() {
        return tUnionField == _Field.INTEGER_VALUE && mIntegerValue != null;
    }

    /**
     * @return The <code>integerValue</code> value
     */
    public int getIntegerValue() {
        return hasIntegerValue() ? mIntegerValue : kDefaultIntegerValue;
    }

    /**
     * @return Optional of the <code>integerValue</code> field value.
     */
    @javax.annotation.Nonnull
    public java.util.OptionalInt optionalIntegerValue() {
        return hasIntegerValue() ? java.util.OptionalInt.of(mIntegerValue) : java.util.OptionalInt.empty();
    }

    public boolean hasLongValue() {
        return tUnionField == _Field.LONG_VALUE && mLongValue != null;
    }

    /**
     * @return The <code>longValue</code> value
     */
    public long getLongValue() {
        return hasLongValue() ? mLongValue : kDefaultLongValue;
    }

    /**
     * @return Optional of the <code>longValue</code> field value.
     */
    @javax.annotation.Nonnull
    public java.util.OptionalLong optionalLongValue() {
        return hasLongValue() ? java.util.OptionalLong.of(mLongValue) : java.util.OptionalLong.empty();
    }

    public boolean hasDoubleValue() {
        return tUnionField == _Field.DOUBLE_VALUE && mDoubleValue != null;
    }

    /**
     * @return The <code>doubleValue</code> value
     */
    public double getDoubleValue() {
        return hasDoubleValue() ? mDoubleValue : kDefaultDoubleValue;
    }

    /**
     * @return Optional of the <code>doubleValue</code> field value.
     */
    @javax.annotation.Nonnull
    public java.util.OptionalDouble optionalDoubleValue() {
        return hasDoubleValue() ? java.util.OptionalDouble.of(mDoubleValue) : java.util.OptionalDouble.empty();
    }

    public boolean hasStringValue() {
        return tUnionField == _Field.STRING_VALUE && mStringValue != null;
    }

    /**
     * @return The <code>stringValue</code> value
     */
    public String getStringValue() {
        return mStringValue;
    }

    /**
     * @return Optional of the <code>stringValue</code> field value.
     */
    @javax.annotation.Nonnull
    public java.util.Optional<String> optionalStringValue() {
        return java.util.Optional.ofNullable(mStringValue);
    }

    public boolean hasBinaryValue() {
        return tUnionField == _Field.BINARY_VALUE && mBinaryValue != null;
    }

    /**
     * @return The <code>binaryValue</code> value
     */
    public net.morimekta.util.Binary getBinaryValue() {
        return mBinaryValue;
    }

    /**
     * @return Optional of the <code>binaryValue</code> field value.
     */
    @javax.annotation.Nonnull
    public java.util.Optional<net.morimekta.util.Binary> optionalBinaryValue() {
        return java.util.Optional.ofNullable(mBinaryValue);
    }

    public boolean hasEnumValue() {
        return tUnionField == _Field.ENUM_VALUE && mEnumValue != null;
    }

    /**
     * @return The <code>enumValue</code> value
     */
    public net.morimekta.test.providence.reflect.Value getEnumValue() {
        return mEnumValue;
    }

    /**
     * @return Optional of the <code>enumValue</code> field value.
     */
    @javax.annotation.Nonnull
    public java.util.Optional<net.morimekta.test.providence.reflect.Value> optionalEnumValue() {
        return java.util.Optional.ofNullable(mEnumValue);
    }

    public boolean hasCompactValue() {
        return tUnionField == _Field.COMPACT_VALUE && mCompactValue != null;
    }

    /**
     * @return The <code>compactValue</code> value
     */
    public net.morimekta.test.providence.reflect.CompactFields getCompactValue() {
        return mCompactValue;
    }

    /**
     * @return Optional of the <code>compactValue</code> field value.
     */
    @javax.annotation.Nonnull
    public java.util.Optional<net.morimekta.test.providence.reflect.CompactFields> optionalCompactValue() {
        return java.util.Optional.ofNullable(mCompactValue);
    }

    @Override
    public boolean has(int key) {
        switch(key) {
            case 1: return tUnionField == _Field.BOOLEAN_VALUE;
            case 2: return tUnionField == _Field.BYTE_VALUE;
            case 3: return tUnionField == _Field.SHORT_VALUE;
            case 4: return tUnionField == _Field.INTEGER_VALUE;
            case 5: return tUnionField == _Field.LONG_VALUE;
            case 6: return tUnionField == _Field.DOUBLE_VALUE;
            case 7: return tUnionField == _Field.STRING_VALUE;
            case 8: return tUnionField == _Field.BINARY_VALUE;
            case 9: return tUnionField == _Field.ENUM_VALUE;
            case 10: return tUnionField == _Field.COMPACT_VALUE;
            default: return false;
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T get(int key) {
        switch(key) {
            case 1: return (T) mBooleanValue;
            case 2: return (T) mByteValue;
            case 3: return (T) mShortValue;
            case 4: return (T) mIntegerValue;
            case 5: return (T) mLongValue;
            case 6: return (T) mDoubleValue;
            case 7: return (T) mStringValue;
            case 8: return (T) mBinaryValue;
            case 9: return (T) mEnumValue;
            case 10: return (T) mCompactValue;
            default: return null;
        }
    }

    @Override
    public boolean unionFieldIsSet() {
        return tUnionField != null;
    }

    @Override
    @javax.annotation.Nonnull
    public _Field unionField() {
        if (tUnionField == null) throw new IllegalStateException("No union field set in providence.UnionFields");
        return tUnionField;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        if (o == null || !o.getClass().equals(getClass())) return false;
        UnionFields other = (UnionFields) o;
        return java.util.Objects.equals(tUnionField, other.tUnionField) &&
               java.util.Objects.equals(mBooleanValue, other.mBooleanValue) &&
               java.util.Objects.equals(mByteValue, other.mByteValue) &&
               java.util.Objects.equals(mShortValue, other.mShortValue) &&
               java.util.Objects.equals(mIntegerValue, other.mIntegerValue) &&
               java.util.Objects.equals(mLongValue, other.mLongValue) &&
               java.util.Objects.equals(mDoubleValue, other.mDoubleValue) &&
               java.util.Objects.equals(mStringValue, other.mStringValue) &&
               java.util.Objects.equals(mBinaryValue, other.mBinaryValue) &&
               java.util.Objects.equals(mEnumValue, other.mEnumValue) &&
               java.util.Objects.equals(mCompactValue, other.mCompactValue);
    }

    @Override
    public int hashCode() {
        if (tHashCode == 0) {
            tHashCode = java.util.Objects.hash(
                    UnionFields.class,
                    _Field.BOOLEAN_VALUE, mBooleanValue,
                    _Field.BYTE_VALUE, mByteValue,
                    _Field.SHORT_VALUE, mShortValue,
                    _Field.INTEGER_VALUE, mIntegerValue,
                    _Field.LONG_VALUE, mLongValue,
                    _Field.DOUBLE_VALUE, mDoubleValue,
                    _Field.STRING_VALUE, mStringValue,
                    _Field.BINARY_VALUE, mBinaryValue,
                    _Field.ENUM_VALUE, mEnumValue,
                    _Field.COMPACT_VALUE, mCompactValue);
        }
        return tHashCode;
    }

    @Override
    public String toString() {
        return "providence.UnionFields" + asString();
    }

    @Override
    @javax.annotation.Nonnull
    public String asString() {
        StringBuilder out = new StringBuilder();
        out.append("{");

        switch (tUnionField) {
            case BOOLEAN_VALUE: {
                out.append("booleanValue:")
                   .append(mBooleanValue);
                break;
            }
            case BYTE_VALUE: {
                out.append("byteValue:")
                   .append((int) mByteValue);
                break;
            }
            case SHORT_VALUE: {
                out.append("shortValue:")
                   .append((int) mShortValue);
                break;
            }
            case INTEGER_VALUE: {
                out.append("integerValue:")
                   .append(mIntegerValue);
                break;
            }
            case LONG_VALUE: {
                out.append("longValue:")
                   .append(mLongValue);
                break;
            }
            case DOUBLE_VALUE: {
                out.append("doubleValue:")
                   .append(net.morimekta.util.Strings.asString(mDoubleValue));
                break;
            }
            case STRING_VALUE: {
                out.append("stringValue:")
                   .append('\"').append(net.morimekta.util.Strings.escape(mStringValue)).append('\"');
                break;
            }
            case BINARY_VALUE: {
                out.append("binaryValue:")
                   .append("b64(").append(mBinaryValue.toBase64()).append(')');
                break;
            }
            case ENUM_VALUE: {
                out.append("enumValue:")
                   .append(mEnumValue.asString());
                break;
            }
            case COMPACT_VALUE: {
                out.append("compactValue:")
                   .append(mCompactValue.asString());
                break;
            }
        }
        out.append('}');
        return out.toString();
    }

    @Override
    public int compareTo(UnionFields other) {
        if (tUnionField == null || other.tUnionField == null) return Boolean.compare(tUnionField != null, other.tUnionField != null);
        int c = tUnionField.compareTo(other.tUnionField);
        if (c != 0) return c;

        switch (tUnionField) {
            case BOOLEAN_VALUE:
                return Boolean.compare(mBooleanValue, other.mBooleanValue);
            case BYTE_VALUE:
                return Byte.compare(mByteValue, other.mByteValue);
            case SHORT_VALUE:
                return Short.compare(mShortValue, other.mShortValue);
            case INTEGER_VALUE:
                return Integer.compare(mIntegerValue, other.mIntegerValue);
            case LONG_VALUE:
                return Long.compare(mLongValue, other.mLongValue);
            case DOUBLE_VALUE:
                return Double.compare(mDoubleValue, other.mDoubleValue);
            case STRING_VALUE:
                return mStringValue.compareTo(other.mStringValue);
            case BINARY_VALUE:
                return mBinaryValue.compareTo(other.mBinaryValue);
            case ENUM_VALUE:
                return Integer.compare(mEnumValue.asInteger(), other.mEnumValue.asInteger());
            case COMPACT_VALUE:
                return mCompactValue.compareTo(other.mCompactValue);
            default: return 0;
        }
    }

    private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException {
        oos.defaultWriteObject();
        net.morimekta.providence.serializer.BinarySerializer serializer = new net.morimekta.providence.serializer.BinarySerializer(false);
        serializer.serialize(oos, this);
    }

    private void readObject(java.io.ObjectInputStream ois)
            throws java.io.IOException, ClassNotFoundException {
        ois.defaultReadObject();
        net.morimekta.providence.serializer.BinarySerializer serializer = new net.morimekta.providence.serializer.BinarySerializer(false);
        tSerializeInstance = serializer.deserialize(ois, kDescriptor);
    }

    private Object readResolve() throws java.io.ObjectStreamException {
        return tSerializeInstance;
    }

    @Override
    public int writeBinary(net.morimekta.util.io.BigEndianBinaryWriter writer) throws java.io.IOException {
        int length = 0;

        if (tUnionField != null) {
            switch (tUnionField) {
                case BOOLEAN_VALUE: {
                    length += writer.writeByte((byte) 2);
                    length += writer.writeShort((short) 1);
                    length += writer.writeUInt8(mBooleanValue ? (byte) 1 : (byte) 0);
                    break;
                }
                case BYTE_VALUE: {
                    length += writer.writeByte((byte) 3);
                    length += writer.writeShort((short) 2);
                    length += writer.writeByte(mByteValue);
                    break;
                }
                case SHORT_VALUE: {
                    length += writer.writeByte((byte) 6);
                    length += writer.writeShort((short) 3);
                    length += writer.writeShort(mShortValue);
                    break;
                }
                case INTEGER_VALUE: {
                    length += writer.writeByte((byte) 8);
                    length += writer.writeShort((short) 4);
                    length += writer.writeInt(mIntegerValue);
                    break;
                }
                case LONG_VALUE: {
                    length += writer.writeByte((byte) 10);
                    length += writer.writeShort((short) 5);
                    length += writer.writeLong(mLongValue);
                    break;
                }
                case DOUBLE_VALUE: {
                    length += writer.writeByte((byte) 4);
                    length += writer.writeShort((short) 6);
                    length += writer.writeDouble(mDoubleValue);
                    break;
                }
                case STRING_VALUE: {
                    length += writer.writeByte((byte) 11);
                    length += writer.writeShort((short) 7);
                    net.morimekta.util.Binary tmp_1 = net.morimekta.util.Binary.wrap(mStringValue.getBytes(java.nio.charset.StandardCharsets.UTF_8));
                    length += writer.writeUInt32(tmp_1.length());
                    length += writer.writeBinary(tmp_1);
                    break;
                }
                case BINARY_VALUE: {
                    length += writer.writeByte((byte) 11);
                    length += writer.writeShort((short) 8);
                    length += writer.writeUInt32(mBinaryValue.length());
                    length += writer.writeBinary(mBinaryValue);
                    break;
                }
                case ENUM_VALUE: {
                    length += writer.writeByte((byte) 8);
                    length += writer.writeShort((short) 9);
                    length += writer.writeInt(mEnumValue.asInteger());
                    break;
                }
                case COMPACT_VALUE: {
                    length += writer.writeByte((byte) 12);
                    length += writer.writeShort((short) 10);
                    length += net.morimekta.providence.serializer.binary.BinaryFormatUtils.writeMessage(writer, mCompactValue);
                    break;
                }
                default: break;
            }
        }
        length += writer.writeByte((byte) 0);
        return length;
    }

    @javax.annotation.Nonnull
    @Override
    public _Builder mutate() {
        return new _Builder(this);
    }

    public enum _Field implements net.morimekta.providence.descriptor.PField {
        BOOLEAN_VALUE(1, net.morimekta.providence.descriptor.PRequirement.OPTIONAL, "booleanValue", net.morimekta.providence.descriptor.PPrimitive.BOOL.provider(), null),
        BYTE_VALUE(2, net.morimekta.providence.descriptor.PRequirement.OPTIONAL, "byteValue", net.morimekta.providence.descriptor.PPrimitive.BYTE.provider(), null),
        SHORT_VALUE(3, net.morimekta.providence.descriptor.PRequirement.OPTIONAL, "shortValue", net.morimekta.providence.descriptor.PPrimitive.I16.provider(), null),
        INTEGER_VALUE(4, net.morimekta.providence.descriptor.PRequirement.OPTIONAL, "integerValue", net.morimekta.providence.descriptor.PPrimitive.I32.provider(), null),
        LONG_VALUE(5, net.morimekta.providence.descriptor.PRequirement.OPTIONAL, "longValue", net.morimekta.providence.descriptor.PPrimitive.I64.provider(), null),
        DOUBLE_VALUE(6, net.morimekta.providence.descriptor.PRequirement.OPTIONAL, "doubleValue", net.morimekta.providence.descriptor.PPrimitive.DOUBLE.provider(), null),
        STRING_VALUE(7, net.morimekta.providence.descriptor.PRequirement.OPTIONAL, "stringValue", net.morimekta.providence.descriptor.PPrimitive.STRING.provider(), null),
        BINARY_VALUE(8, net.morimekta.providence.descriptor.PRequirement.OPTIONAL, "binaryValue", net.morimekta.providence.descriptor.PPrimitive.BINARY.provider(), null),
        ENUM_VALUE(9, net.morimekta.providence.descriptor.PRequirement.OPTIONAL, "enumValue", net.morimekta.test.providence.reflect.Value.provider(), null),
        COMPACT_VALUE(10, net.morimekta.providence.descriptor.PRequirement.OPTIONAL, "compactValue", net.morimekta.test.providence.reflect.CompactFields.provider(), null),
        ;

        private final int mId;
        private final net.morimekta.providence.descriptor.PRequirement mRequired;
        private final String mName;
        private final net.morimekta.providence.descriptor.PDescriptorProvider mTypeProvider;
        private final net.morimekta.providence.descriptor.PValueProvider<?> mDefaultValue;

        _Field(int id, net.morimekta.providence.descriptor.PRequirement required, String name, net.morimekta.providence.descriptor.PDescriptorProvider typeProvider, net.morimekta.providence.descriptor.PValueProvider<?> defaultValue) {
            mId = id;
            mRequired = required;
            mName = name;
            mTypeProvider = typeProvider;
            mDefaultValue = defaultValue;
        }

        @Override
        public int getId() { return mId; }

        @Override
        public net.morimekta.providence.descriptor.PRequirement getRequirement() { return mRequired; }

        @Override
        public net.morimekta.providence.descriptor.PDescriptor getDescriptor() { return mTypeProvider.descriptor(); }

        @Override
        public String getName() { return mName; }

        @Override
        public boolean hasDefaultValue() { return mDefaultValue != null; }

        @Override
        public Object getDefaultValue() {
            return hasDefaultValue() ? mDefaultValue.get() : null;
        }

        @Override
        public String toString() {
            return net.morimekta.providence.descriptor.PField.asString(this);
        }

        /**
         * @param id Field name
         * @return The identified field or null
         */
        public static _Field findById(int id) {
            switch (id) {
                case 1: return _Field.BOOLEAN_VALUE;
                case 2: return _Field.BYTE_VALUE;
                case 3: return _Field.SHORT_VALUE;
                case 4: return _Field.INTEGER_VALUE;
                case 5: return _Field.LONG_VALUE;
                case 6: return _Field.DOUBLE_VALUE;
                case 7: return _Field.STRING_VALUE;
                case 8: return _Field.BINARY_VALUE;
                case 9: return _Field.ENUM_VALUE;
                case 10: return _Field.COMPACT_VALUE;
            }
            return null;
        }

        /**
         * @param name Field name
         * @return The named field or null
         */
        public static _Field findByName(String name) {
            switch (name) {
                case "booleanValue": return _Field.BOOLEAN_VALUE;
                case "byteValue": return _Field.BYTE_VALUE;
                case "shortValue": return _Field.SHORT_VALUE;
                case "integerValue": return _Field.INTEGER_VALUE;
                case "longValue": return _Field.LONG_VALUE;
                case "doubleValue": return _Field.DOUBLE_VALUE;
                case "stringValue": return _Field.STRING_VALUE;
                case "binaryValue": return _Field.BINARY_VALUE;
                case "enumValue": return _Field.ENUM_VALUE;
                case "compactValue": return _Field.COMPACT_VALUE;
            }
            return null;
        }
        /**
         * @param id Field name
         * @return The identified field
         * @throws IllegalArgumentException If no such field
         */
        public static _Field fieldForId(int id) {
            _Field field = findById(id);
            if (field == null) {
                throw new IllegalArgumentException("No such field id " + id + " in providence.UnionFields");
            }
            return field;
        }

        /**
         * @param name Field name
         * @return The named field
         * @throws IllegalArgumentException If no such field
         */
        public static _Field fieldForName(String name) {
            _Field field = findByName(name);
            if (field == null) {
                throw new IllegalArgumentException("No such field \"" + name + "\" in providence.UnionFields");
            }
            return field;
        }

    }

    @javax.annotation.Nonnull
    public static net.morimekta.providence.descriptor.PUnionDescriptorProvider<UnionFields,_Field> provider() {
        return new _Provider();
    }

    @Override
    @javax.annotation.Nonnull
    public net.morimekta.providence.descriptor.PUnionDescriptor<UnionFields,_Field> descriptor() {
        return kDescriptor;
    }

    public static final net.morimekta.providence.descriptor.PUnionDescriptor<UnionFields,_Field> kDescriptor;

    private static class _Descriptor
            extends net.morimekta.providence.descriptor.PUnionDescriptor<UnionFields,_Field> {
        public _Descriptor() {
            super("providence", "UnionFields", _Builder::new, false);
        }

        @Override
        @javax.annotation.Nonnull
        public _Field[] getFields() {
            return _Field.values();
        }

        @Override
        @javax.annotation.Nullable
        public _Field findFieldByName(String name) {
            return _Field.findByName(name);
        }

        @Override
        @javax.annotation.Nullable
        public _Field findFieldById(int id) {
            return _Field.findById(id);
        }
    }

    static {
        kDescriptor = new _Descriptor();
    }

    private final static class _Provider extends net.morimekta.providence.descriptor.PUnionDescriptorProvider<UnionFields,_Field> {
        @Override
        public net.morimekta.providence.descriptor.PUnionDescriptor<UnionFields,_Field> descriptor() {
            return kDescriptor;
        }
    }

    /**
     * Make a <code>providence.UnionFields</code> builder.
     * @return The builder instance.
     */
    public static _Builder builder() {
        return new _Builder();
    }

    public static class _Builder
            extends net.morimekta.providence.PMessageBuilder<UnionFields,_Field>
            implements net.morimekta.providence.serializer.binary.BinaryReader {
        private _Field tUnionField;

        private boolean modified;

        private Boolean mBooleanValue;
        private Byte mByteValue;
        private Short mShortValue;
        private Integer mIntegerValue;
        private Long mLongValue;
        private Double mDoubleValue;
        private String mStringValue;
        private net.morimekta.util.Binary mBinaryValue;
        private net.morimekta.test.providence.reflect.Value mEnumValue;
        private net.morimekta.test.providence.reflect.CompactFields mCompactValue;
        private net.morimekta.test.providence.reflect.CompactFields._Builder mCompactValue_builder;

        /**
         * Make a providence.UnionFields builder instance.
         */
        public _Builder() {
            modified = false;
        }

        /**
         * Make a mutating builder off a base providence.UnionFields.
         *
         * @param base The base UnionFields
         */
        public _Builder(UnionFields base) {
            this();

            tUnionField = base.tUnionField;

            mBooleanValue = base.mBooleanValue;
            mByteValue = base.mByteValue;
            mShortValue = base.mShortValue;
            mIntegerValue = base.mIntegerValue;
            mLongValue = base.mLongValue;
            mDoubleValue = base.mDoubleValue;
            mStringValue = base.mStringValue;
            mBinaryValue = base.mBinaryValue;
            mEnumValue = base.mEnumValue;
            mCompactValue = base.mCompactValue;
        }

        @javax.annotation.Nonnull
        @Override
        public _Builder merge(UnionFields from) {
            if (!from.unionFieldIsSet()) {
                return this;
            }

            switch (from.unionField()) {
                case BOOLEAN_VALUE: {
                    setBooleanValue(from.isBooleanValue());
                    break;
                }
                case BYTE_VALUE: {
                    setByteValue(from.getByteValue());
                    break;
                }
                case SHORT_VALUE: {
                    setShortValue(from.getShortValue());
                    break;
                }
                case INTEGER_VALUE: {
                    setIntegerValue(from.getIntegerValue());
                    break;
                }
                case LONG_VALUE: {
                    setLongValue(from.getLongValue());
                    break;
                }
                case DOUBLE_VALUE: {
                    setDoubleValue(from.getDoubleValue());
                    break;
                }
                case STRING_VALUE: {
                    setStringValue(from.getStringValue());
                    break;
                }
                case BINARY_VALUE: {
                    setBinaryValue(from.getBinaryValue());
                    break;
                }
                case ENUM_VALUE: {
                    setEnumValue(from.getEnumValue());
                    break;
                }
                case COMPACT_VALUE: {
                    if (tUnionField == _Field.COMPACT_VALUE && mCompactValue != null) {
                        mCompactValue = mCompactValue.mutate().merge(from.getCompactValue()).build();
                    } else {
                        setCompactValue(from.getCompactValue());
                    }
                    break;
                }
            }
            return this;
        }

        /**
         * Set the <code>booleanValue</code> field value.
         *
         * @param value The new value
         * @return The builder
         */
        @javax.annotation.Nonnull
        public _Builder setBooleanValue(boolean value) {
            tUnionField = _Field.BOOLEAN_VALUE;
            modified = true;
            mBooleanValue = value;
            return this;
        }

        /**
         * Checks for presence of the <code>booleanValue</code> field.
         *
         * @return True if booleanValue has been set.
         */
        public boolean isSetBooleanValue() {
            return tUnionField == _Field.BOOLEAN_VALUE;
        }

        /**
         * Clear the <code>booleanValue</code> field.
         *
         * @return The builder
         */
        @javax.annotation.Nonnull
        public _Builder clearBooleanValue() {
            if (tUnionField == _Field.BOOLEAN_VALUE) tUnionField = null;
            modified = true;
            mBooleanValue = null;
            return this;
        }

        /**
         * @return The <code>booleanValue</code> field value
         */
        public boolean getBooleanValue() {
            return isSetBooleanValue() ? mBooleanValue : kDefaultBooleanValue;
        }

        /**
         * Set the <code>byteValue</code> field value.
         *
         * @param value The new value
         * @return The builder
         */
        @javax.annotation.Nonnull
        public _Builder setByteValue(byte value) {
            tUnionField = _Field.BYTE_VALUE;
            modified = true;
            mByteValue = value;
            return this;
        }

        /**
         * Checks for presence of the <code>byteValue</code> field.
         *
         * @return True if byteValue has been set.
         */
        public boolean isSetByteValue() {
            return tUnionField == _Field.BYTE_VALUE;
        }

        /**
         * Clear the <code>byteValue</code> field.
         *
         * @return The builder
         */
        @javax.annotation.Nonnull
        public _Builder clearByteValue() {
            if (tUnionField == _Field.BYTE_VALUE) tUnionField = null;
            modified = true;
            mByteValue = null;
            return this;
        }

        /**
         * @return The <code>byteValue</code> field value
         */
        public byte getByteValue() {
            return isSetByteValue() ? mByteValue : kDefaultByteValue;
        }

        /**
         * Set the <code>shortValue</code> field value.
         *
         * @param value The new value
         * @return The builder
         */
        @javax.annotation.Nonnull
        public _Builder setShortValue(short value) {
            tUnionField = _Field.SHORT_VALUE;
            modified = true;
            mShortValue = value;
            return this;
        }

        /**
         * Checks for presence of the <code>shortValue</code> field.
         *
         * @return True if shortValue has been set.
         */
        public boolean isSetShortValue() {
            return tUnionField == _Field.SHORT_VALUE;
        }

        /**
         * Clear the <code>shortValue</code> field.
         *
         * @return The builder
         */
        @javax.annotation.Nonnull
        public _Builder clearShortValue() {
            if (tUnionField == _Field.SHORT_VALUE) tUnionField = null;
            modified = true;
            mShortValue = null;
            return this;
        }

        /**
         * @return The <code>shortValue</code> field value
         */
        public short getShortValue() {
            return isSetShortValue() ? mShortValue : kDefaultShortValue;
        }

        /**
         * Set the <code>integerValue</code> field value.
         *
         * @param value The new value
         * @return The builder
         */
        @javax.annotation.Nonnull
        public _Builder setIntegerValue(int value) {
            tUnionField = _Field.INTEGER_VALUE;
            modified = true;
            mIntegerValue = value;
            return this;
        }

        /**
         * Checks for presence of the <code>integerValue</code> field.
         *
         * @return True if integerValue has been set.
         */
        public boolean isSetIntegerValue() {
            return tUnionField == _Field.INTEGER_VALUE;
        }

        /**
         * Clear the <code>integerValue</code> field.
         *
         * @return The builder
         */
        @javax.annotation.Nonnull
        public _Builder clearIntegerValue() {
            if (tUnionField == _Field.INTEGER_VALUE) tUnionField = null;
            modified = true;
            mIntegerValue = null;
            return this;
        }

        /**
         * @return The <code>integerValue</code> field value
         */
        public int getIntegerValue() {
            return isSetIntegerValue() ? mIntegerValue : kDefaultIntegerValue;
        }

        /**
         * Set the <code>longValue</code> field value.
         *
         * @param value The new value
         * @return The builder
         */
        @javax.annotation.Nonnull
        public _Builder setLongValue(long value) {
            tUnionField = _Field.LONG_VALUE;
            modified = true;
            mLongValue = value;
            return this;
        }

        /**
         * Checks for presence of the <code>longValue</code> field.
         *
         * @return True if longValue has been set.
         */
        public boolean isSetLongValue() {
            return tUnionField == _Field.LONG_VALUE;
        }

        /**
         * Clear the <code>longValue</code> field.
         *
         * @return The builder
         */
        @javax.annotation.Nonnull
        public _Builder clearLongValue() {
            if (tUnionField == _Field.LONG_VALUE) tUnionField = null;
            modified = true;
            mLongValue = null;
            return this;
        }

        /**
         * @return The <code>longValue</code> field value
         */
        public long getLongValue() {
            return isSetLongValue() ? mLongValue : kDefaultLongValue;
        }

        /**
         * Set the <code>doubleValue</code> field value.
         *
         * @param value The new value
         * @return The builder
         */
        @javax.annotation.Nonnull
        public _Builder setDoubleValue(double value) {
            tUnionField = _Field.DOUBLE_VALUE;
            modified = true;
            mDoubleValue = value;
            return this;
        }

        /**
         * Checks for presence of the <code>doubleValue</code> field.
         *
         * @return True if doubleValue has been set.
         */
        public boolean isSetDoubleValue() {
            return tUnionField == _Field.DOUBLE_VALUE;
        }

        /**
         * Clear the <code>doubleValue</code> field.
         *
         * @return The builder
         */
        @javax.annotation.Nonnull
        public _Builder clearDoubleValue() {
            if (tUnionField == _Field.DOUBLE_VALUE) tUnionField = null;
            modified = true;
            mDoubleValue = null;
            return this;
        }

        /**
         * @return The <code>doubleValue</code> field value
         */
        public double getDoubleValue() {
            return isSetDoubleValue() ? mDoubleValue : kDefaultDoubleValue;
        }

        /**
         * Set the <code>stringValue</code> field value.
         *
         * @param value The new value
         * @return The builder
         */
        @javax.annotation.Nonnull
        public _Builder setStringValue(String value) {
            if (value == null) {
                return clearStringValue();
            }

            tUnionField = _Field.STRING_VALUE;
            modified = true;
            mStringValue = value;
            return this;
        }

        /**
         * Checks for presence of the <code>stringValue</code> field.
         *
         * @return True if stringValue has been set.
         */
        public boolean isSetStringValue() {
            return tUnionField == _Field.STRING_VALUE;
        }

        /**
         * Clear the <code>stringValue</code> field.
         *
         * @return The builder
         */
        @javax.annotation.Nonnull
        public _Builder clearStringValue() {
            if (tUnionField == _Field.STRING_VALUE) tUnionField = null;
            modified = true;
            mStringValue = null;
            return this;
        }

        /**
         * @return The <code>stringValue</code> field value
         */
        public String getStringValue() {
            return mStringValue;
        }

        /**
         * Set the <code>binaryValue</code> field value.
         *
         * @param value The new value
         * @return The builder
         */
        @javax.annotation.Nonnull
        public _Builder setBinaryValue(net.morimekta.util.Binary value) {
            if (value == null) {
                return clearBinaryValue();
            }

            tUnionField = _Field.BINARY_VALUE;
            modified = true;
            mBinaryValue = value;
            return this;
        }

        /**
         * Checks for presence of the <code>binaryValue</code> field.
         *
         * @return True if binaryValue has been set.
         */
        public boolean isSetBinaryValue() {
            return tUnionField == _Field.BINARY_VALUE;
        }

        /**
         * Clear the <code>binaryValue</code> field.
         *
         * @return The builder
         */
        @javax.annotation.Nonnull
        public _Builder clearBinaryValue() {
            if (tUnionField == _Field.BINARY_VALUE) tUnionField = null;
            modified = true;
            mBinaryValue = null;
            return this;
        }

        /**
         * @return The <code>binaryValue</code> field value
         */
        public net.morimekta.util.Binary getBinaryValue() {
            return mBinaryValue;
        }

        /**
         * Set the <code>enumValue</code> field value.
         *
         * @param value The new value
         * @return The builder
         */
        @javax.annotation.Nonnull
        public _Builder setEnumValue(net.morimekta.test.providence.reflect.Value value) {
            if (value == null) {
                return clearEnumValue();
            }

            tUnionField = _Field.ENUM_VALUE;
            modified = true;
            mEnumValue = value;
            return this;
        }

        /**
         * Checks for presence of the <code>enumValue</code> field.
         *
         * @return True if enumValue has been set.
         */
        public boolean isSetEnumValue() {
            return tUnionField == _Field.ENUM_VALUE;
        }

        /**
         * Clear the <code>enumValue</code> field.
         *
         * @return The builder
         */
        @javax.annotation.Nonnull
        public _Builder clearEnumValue() {
            if (tUnionField == _Field.ENUM_VALUE) tUnionField = null;
            modified = true;
            mEnumValue = null;
            return this;
        }

        /**
         * @return The <code>enumValue</code> field value
         */
        public net.morimekta.test.providence.reflect.Value getEnumValue() {
            return mEnumValue;
        }

        /**
         * Set the <code>compactValue</code> field value.
         *
         * @param value The new value
         * @return The builder
         */
        @javax.annotation.Nonnull
        public _Builder setCompactValue(net.morimekta.test.providence.reflect.CompactFields value) {
            if (value == null) {
                return clearCompactValue();
            }

            tUnionField = _Field.COMPACT_VALUE;
            modified = true;
            mCompactValue = value;
            mCompactValue_builder = null;
            return this;
        }

        /**
         * Set the <code>compactValue</code> field value.
         *
         * @param builder builder for the new value
         * @return The builder
         */
        @javax.annotation.Nonnull
        public _Builder setCompactValue(net.morimekta.test.providence.reflect.CompactFields._Builder builder) {
          return setCompactValue(builder == null ? null : builder.build());
        }

        /**
         * Checks for presence of the <code>compactValue</code> field.
         *
         * @return True if compactValue has been set.
         */
        public boolean isSetCompactValue() {
            return tUnionField == _Field.COMPACT_VALUE;
        }

        /**
         * Clear the <code>compactValue</code> field.
         *
         * @return The builder
         */
        @javax.annotation.Nonnull
        public _Builder clearCompactValue() {
            if (tUnionField == _Field.COMPACT_VALUE) tUnionField = null;
            modified = true;
            mCompactValue = null;
            mCompactValue_builder = null;
            return this;
        }

        /**
         * Get the builder for the contained <code>compactValue</code> message field.
         *
         * @return The field message builder
         */
        @javax.annotation.Nonnull
        public net.morimekta.test.providence.reflect.CompactFields._Builder mutableCompactValue() {
            if (tUnionField != _Field.COMPACT_VALUE) {
                clearCompactValue();
            }
            tUnionField = _Field.COMPACT_VALUE;
            modified = true;

            if (mCompactValue != null) {
                mCompactValue_builder = mCompactValue.mutate();
                mCompactValue = null;
            } else if (mCompactValue_builder == null) {
                mCompactValue_builder = net.morimekta.test.providence.reflect.CompactFields.builder();
            }
            return mCompactValue_builder;
        }

        /**
         * @return The field value
         */
        public net.morimekta.test.providence.reflect.CompactFields getCompactValue() {
            if (tUnionField != _Field.COMPACT_VALUE) {
                return null;
            }

            if (mCompactValue_builder != null) {
                return mCompactValue_builder.build();
            }
            return mCompactValue;
        }

        /**
         * Checks if the <code>UnionFields</code> union has been modified since the
         * builder was created.
         *
         * @return True if UnionFields has been modified.
         */
        public boolean isUnionModified() {
            return modified;
        }

        @Override
        public boolean equals(Object o) {
            if (o == this) return true;
            if (o == null || !o.getClass().equals(getClass())) return false;
            UnionFields._Builder other = (UnionFields._Builder) o;
            return java.util.Objects.equals(tUnionField, other.tUnionField) &&
                   java.util.Objects.equals(mBooleanValue, other.mBooleanValue) &&
                   java.util.Objects.equals(mByteValue, other.mByteValue) &&
                   java.util.Objects.equals(mShortValue, other.mShortValue) &&
                   java.util.Objects.equals(mIntegerValue, other.mIntegerValue) &&
                   java.util.Objects.equals(mLongValue, other.mLongValue) &&
                   java.util.Objects.equals(mDoubleValue, other.mDoubleValue) &&
                   java.util.Objects.equals(mStringValue, other.mStringValue) &&
                   java.util.Objects.equals(mBinaryValue, other.mBinaryValue) &&
                   java.util.Objects.equals(mEnumValue, other.mEnumValue) &&
                   java.util.Objects.equals(getCompactValue(), other.getCompactValue());
        }

        @Override
        public int hashCode() {
            return java.util.Objects.hash(
                    UnionFields.class,
                    _Field.BOOLEAN_VALUE, mBooleanValue,
                    _Field.BYTE_VALUE, mByteValue,
                    _Field.SHORT_VALUE, mShortValue,
                    _Field.INTEGER_VALUE, mIntegerValue,
                    _Field.LONG_VALUE, mLongValue,
                    _Field.DOUBLE_VALUE, mDoubleValue,
                    _Field.STRING_VALUE, mStringValue,
                    _Field.BINARY_VALUE, mBinaryValue,
                    _Field.ENUM_VALUE, mEnumValue,
                    _Field.COMPACT_VALUE, getCompactValue());
        }

        @Override
        @SuppressWarnings("unchecked")
        public net.morimekta.providence.PMessageBuilder mutator(int key) {
            switch (key) {
                case 10: return mutableCompactValue();
                default: throw new IllegalArgumentException("Not a message field ID: " + key);
            }
        }

        @javax.annotation.Nonnull
        @Override
        @SuppressWarnings("unchecked")
        public _Builder set(int key, Object value) {
            if (value == null) return clear(key);
            switch (key) {
                case 1: setBooleanValue((boolean) value); break;
                case 2: setByteValue((byte) value); break;
                case 3: setShortValue((short) value); break;
                case 4: setIntegerValue((int) value); break;
                case 5: setLongValue((long) value); break;
                case 6: setDoubleValue((double) value); break;
                case 7: setStringValue((String) value); break;
                case 8: setBinaryValue((net.morimekta.util.Binary) value); break;
                case 9: setEnumValue((net.morimekta.test.providence.reflect.Value) value); break;
                case 10: setCompactValue((net.morimekta.test.providence.reflect.CompactFields) value); break;
                default: break;
            }
            return this;
        }

        @Override
        public boolean isSet(int key) {
            switch (key) {
                case 1: return tUnionField == _Field.BOOLEAN_VALUE;
                case 2: return tUnionField == _Field.BYTE_VALUE;
                case 3: return tUnionField == _Field.SHORT_VALUE;
                case 4: return tUnionField == _Field.INTEGER_VALUE;
                case 5: return tUnionField == _Field.LONG_VALUE;
                case 6: return tUnionField == _Field.DOUBLE_VALUE;
                case 7: return tUnionField == _Field.STRING_VALUE;
                case 8: return tUnionField == _Field.BINARY_VALUE;
                case 9: return tUnionField == _Field.ENUM_VALUE;
                case 10: return tUnionField == _Field.COMPACT_VALUE;
                default: break;
            }
            return false;
        }

        @Override
        public boolean isModified(int key) {
            return modified;
        }

        @Override
        public _Builder addTo(int key, Object value) {
            switch (key) {
                default: break;
            }
            return this;
        }

        @javax.annotation.Nonnull
        @Override
        public _Builder clear(int key) {
            switch (key) {
                case 1: clearBooleanValue(); break;
                case 2: clearByteValue(); break;
                case 3: clearShortValue(); break;
                case 4: clearIntegerValue(); break;
                case 5: clearLongValue(); break;
                case 6: clearDoubleValue(); break;
                case 7: clearStringValue(); break;
                case 8: clearBinaryValue(); break;
                case 9: clearEnumValue(); break;
                case 10: clearCompactValue(); break;
                default: break;
            }
            return this;
        }

        @Override
        public boolean valid() {
            if (tUnionField == null) {
                return false;
            }

            switch (tUnionField) {
                case BOOLEAN_VALUE: return mBooleanValue != null;
                case BYTE_VALUE: return mByteValue != null;
                case SHORT_VALUE: return mShortValue != null;
                case INTEGER_VALUE: return mIntegerValue != null;
                case LONG_VALUE: return mLongValue != null;
                case DOUBLE_VALUE: return mDoubleValue != null;
                case STRING_VALUE: return mStringValue != null;
                case BINARY_VALUE: return mBinaryValue != null;
                case ENUM_VALUE: return mEnumValue != null;
                case COMPACT_VALUE: return mCompactValue != null || mCompactValue_builder != null;
                default: return true;
            }
        }

        @Override
        public void validate() {
            if (!valid()) {
                throw new java.lang.IllegalStateException("No union field set in providence.UnionFields");
            }
        }

        @javax.annotation.Nonnull
        @Override
        public net.morimekta.providence.descriptor.PUnionDescriptor<UnionFields,_Field> descriptor() {
            return kDescriptor;
        }

        @Override
        public void readBinary(net.morimekta.util.io.BigEndianBinaryReader reader, boolean strict) throws java.io.IOException {
            byte type = reader.expectByte();
            while (type != 0) {
                int field = reader.expectShort();
                switch (field) {
                    case 1: {
                        if (type == 2) {
                            mBooleanValue = reader.expectUInt8() == 1;
                            tUnionField = _Field.BOOLEAN_VALUE;
                        } else {
                            throw new net.morimekta.providence.serializer.SerializerException("Wrong type " + net.morimekta.providence.serializer.binary.BinaryType.asString(type) + " for providence.UnionFields.booleanValue, should be struct(12)");
                        }
                        break;
                    }
                    case 2: {
                        if (type == 3) {
                            mByteValue = reader.expectByte();
                            tUnionField = _Field.BYTE_VALUE;
                        } else {
                            throw new net.morimekta.providence.serializer.SerializerException("Wrong type " + net.morimekta.providence.serializer.binary.BinaryType.asString(type) + " for providence.UnionFields.byteValue, should be struct(12)");
                        }
                        break;
                    }
                    case 3: {
                        if (type == 6) {
                            mShortValue = reader.expectShort();
                            tUnionField = _Field.SHORT_VALUE;
                        } else {
                            throw new net.morimekta.providence.serializer.SerializerException("Wrong type " + net.morimekta.providence.serializer.binary.BinaryType.asString(type) + " for providence.UnionFields.shortValue, should be struct(12)");
                        }
                        break;
                    }
                    case 4: {
                        if (type == 8) {
                            mIntegerValue = reader.expectInt();
                            tUnionField = _Field.INTEGER_VALUE;
                        } else {
                            throw new net.morimekta.providence.serializer.SerializerException("Wrong type " + net.morimekta.providence.serializer.binary.BinaryType.asString(type) + " for providence.UnionFields.integerValue, should be struct(12)");
                        }
                        break;
                    }
                    case 5: {
                        if (type == 10) {
                            mLongValue = reader.expectLong();
                            tUnionField = _Field.LONG_VALUE;
                        } else {
                            throw new net.morimekta.providence.serializer.SerializerException("Wrong type " + net.morimekta.providence.serializer.binary.BinaryType.asString(type) + " for providence.UnionFields.longValue, should be struct(12)");
                        }
                        break;
                    }
                    case 6: {
                        if (type == 4) {
                            mDoubleValue = reader.expectDouble();
                            tUnionField = _Field.DOUBLE_VALUE;
                        } else {
                            throw new net.morimekta.providence.serializer.SerializerException("Wrong type " + net.morimekta.providence.serializer.binary.BinaryType.asString(type) + " for providence.UnionFields.doubleValue, should be struct(12)");
                        }
                        break;
                    }
                    case 7: {
                        if (type == 11) {
                            int len_1 = reader.expectUInt32();
                            mStringValue = new String(reader.expectBytes(len_1), java.nio.charset.StandardCharsets.UTF_8);
                            tUnionField = _Field.STRING_VALUE;
                        } else {
                            throw new net.morimekta.providence.serializer.SerializerException("Wrong type " + net.morimekta.providence.serializer.binary.BinaryType.asString(type) + " for providence.UnionFields.stringValue, should be struct(12)");
                        }
                        break;
                    }
                    case 8: {
                        if (type == 11) {
                            int len_2 = reader.expectUInt32();
                            mBinaryValue = reader.expectBinary(len_2);
                            tUnionField = _Field.BINARY_VALUE;
                        } else {
                            throw new net.morimekta.providence.serializer.SerializerException("Wrong type " + net.morimekta.providence.serializer.binary.BinaryType.asString(type) + " for providence.UnionFields.binaryValue, should be struct(12)");
                        }
                        break;
                    }
                    case 9: {
                        if (type == 8) {
                            mEnumValue = net.morimekta.test.providence.reflect.Value.findById(reader.expectInt());
                            tUnionField = _Field.ENUM_VALUE;
                        } else {
                            throw new net.morimekta.providence.serializer.SerializerException("Wrong type " + net.morimekta.providence.serializer.binary.BinaryType.asString(type) + " for providence.UnionFields.enumValue, should be struct(12)");
                        }
                        break;
                    }
                    case 10: {
                        if (type == 12) {
                            mCompactValue = net.morimekta.providence.serializer.binary.BinaryFormatUtils.readMessage(reader, net.morimekta.test.providence.reflect.CompactFields.kDescriptor, strict);
                            tUnionField = _Field.COMPACT_VALUE;
                        } else {
                            throw new net.morimekta.providence.serializer.SerializerException("Wrong type " + net.morimekta.providence.serializer.binary.BinaryType.asString(type) + " for providence.UnionFields.compactValue, should be struct(12)");
                        }
                        break;
                    }
                    default: {
                        net.morimekta.providence.serializer.binary.BinaryFormatUtils.readFieldValue(reader, new net.morimekta.providence.serializer.binary.BinaryFormatUtils.FieldInfo(field, type), null, false);
                        break;
                    }
                }
                type = reader.expectByte();
            }
        }

        @Override
        public UnionFields build() {
            return new UnionFields(this);
        }
    }
}
