package net.morimekta.test.providence;

@SuppressWarnings("unused")
public class UnionFields
        implements net.morimekta.providence.PUnion<UnionFields,UnionFields._Field>,
                   Comparable<UnionFields>,
                   java.io.Serializable,
                   net.morimekta.providence.serializer.rw.BinaryWriter {
    private final static long serialVersionUID = -4125227148631020921L;

    private final static boolean kDefaultBooleanValue = false;
    private final static byte kDefaultByteValue = (byte)0;
    private final static short kDefaultShortValue = (short)0;
    private final static int kDefaultIntegerValue = 0;
    private final static long kDefaultLongValue = 0L;
    private final static double kDefaultDoubleValue = 0.0d;

    private final boolean mBooleanValue;
    private final byte mByteValue;
    private final short mShortValue;
    private final int mIntegerValue;
    private final long mLongValue;
    private final double mDoubleValue;
    private final String mStringValue;
    private final net.morimekta.util.Binary mBinaryValue;
    private final net.morimekta.test.providence.Value mEnumValue;
    private final net.morimekta.test.providence.CompactFields mCompactValue;

    private final _Field tUnionField;

    private volatile int tHashCode;

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
    public static UnionFields withEnumValue(net.morimekta.test.providence.Value value) {
        return new _Builder().setEnumValue(value).build();
    }

    /**
     * @param value The union value
     * @return The created union.
     */
    public static UnionFields withCompactValue(net.morimekta.test.providence.CompactFields value) {
        return new _Builder().setCompactValue(value).build();
    }

    private UnionFields(_Builder builder) {
        tUnionField = builder.tUnionField;

        mBooleanValue = tUnionField == _Field.BOOLEAN_VALUE ? builder.mBooleanValue : kDefaultBooleanValue;
        mByteValue = tUnionField == _Field.BYTE_VALUE ? builder.mByteValue : kDefaultByteValue;
        mShortValue = tUnionField == _Field.SHORT_VALUE ? builder.mShortValue : kDefaultShortValue;
        mIntegerValue = tUnionField == _Field.INTEGER_VALUE ? builder.mIntegerValue : kDefaultIntegerValue;
        mLongValue = tUnionField == _Field.LONG_VALUE ? builder.mLongValue : kDefaultLongValue;
        mDoubleValue = tUnionField == _Field.DOUBLE_VALUE ? builder.mDoubleValue : kDefaultDoubleValue;
        mStringValue = tUnionField == _Field.STRING_VALUE ? builder.mStringValue : null;
        mBinaryValue = tUnionField == _Field.BINARY_VALUE ? builder.mBinaryValue : null;
        mEnumValue = tUnionField == _Field.ENUM_VALUE ? builder.mEnumValue : null;
        mCompactValue = tUnionField != _Field.COMPACT_VALUE
                ? null
                : builder.mCompactValue_builder != null ? builder.mCompactValue_builder.build() : builder.mCompactValue;
    }

    public boolean hasBooleanValue() {
        return tUnionField == _Field.BOOLEAN_VALUE;
    }

    /**
     * @return The field value
     */
    public boolean isBooleanValue() {
        return mBooleanValue;
    }

    public boolean hasByteValue() {
        return tUnionField == _Field.BYTE_VALUE;
    }

    /**
     * @return The field value
     */
    public byte getByteValue() {
        return mByteValue;
    }

    public boolean hasShortValue() {
        return tUnionField == _Field.SHORT_VALUE;
    }

    /**
     * @return The field value
     */
    public short getShortValue() {
        return mShortValue;
    }

    public boolean hasIntegerValue() {
        return tUnionField == _Field.INTEGER_VALUE;
    }

    /**
     * @return The field value
     */
    public int getIntegerValue() {
        return mIntegerValue;
    }

    public boolean hasLongValue() {
        return tUnionField == _Field.LONG_VALUE;
    }

    /**
     * @return The field value
     */
    public long getLongValue() {
        return mLongValue;
    }

    public boolean hasDoubleValue() {
        return tUnionField == _Field.DOUBLE_VALUE;
    }

    /**
     * @return The field value
     */
    public double getDoubleValue() {
        return mDoubleValue;
    }

    public boolean hasStringValue() {
        return tUnionField == _Field.STRING_VALUE && mStringValue != null;
    }

    /**
     * @return The field value
     */
    public String getStringValue() {
        return mStringValue;
    }

    public boolean hasBinaryValue() {
        return tUnionField == _Field.BINARY_VALUE && mBinaryValue != null;
    }

    /**
     * @return The field value
     */
    public net.morimekta.util.Binary getBinaryValue() {
        return mBinaryValue;
    }

    public boolean hasEnumValue() {
        return tUnionField == _Field.ENUM_VALUE && mEnumValue != null;
    }

    /**
     * @return The field value
     */
    public net.morimekta.test.providence.Value getEnumValue() {
        return mEnumValue;
    }

    public boolean hasCompactValue() {
        return tUnionField == _Field.COMPACT_VALUE && mCompactValue != null;
    }

    /**
     * @return The field value
     */
    public net.morimekta.test.providence.CompactFields getCompactValue() {
        return mCompactValue;
    }

    @Override
    public boolean has(int key) {
        switch(key) {
            case 1: return hasBooleanValue();
            case 2: return hasByteValue();
            case 3: return hasShortValue();
            case 4: return hasIntegerValue();
            case 5: return hasLongValue();
            case 6: return hasDoubleValue();
            case 7: return hasStringValue();
            case 8: return hasBinaryValue();
            case 9: return hasEnumValue();
            case 10: return hasCompactValue();
            default: return false;
        }
    }

    @Override
    public int num(int key) {
        switch(key) {
            case 1: return hasBooleanValue() ? 1 : 0;
            case 2: return hasByteValue() ? 1 : 0;
            case 3: return hasShortValue() ? 1 : 0;
            case 4: return hasIntegerValue() ? 1 : 0;
            case 5: return hasLongValue() ? 1 : 0;
            case 6: return hasDoubleValue() ? 1 : 0;
            case 7: return hasStringValue() ? 1 : 0;
            case 8: return hasBinaryValue() ? 1 : 0;
            case 9: return hasEnumValue() ? 1 : 0;
            case 10: return hasCompactValue() ? 1 : 0;
            default: return 0;
        }
    }

    @Override
    public Object get(int key) {
        switch(key) {
            case 1: return isBooleanValue();
            case 2: return getByteValue();
            case 3: return getShortValue();
            case 4: return getIntegerValue();
            case 5: return getLongValue();
            case 6: return getDoubleValue();
            case 7: return getStringValue();
            case 8: return getBinaryValue();
            case 9: return getEnumValue();
            case 10: return getCompactValue();
            default: return null;
        }
    }

    @Override
    public boolean compact() {
        return false;
    }

    @Override
    public _Field unionField() {
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
                return Integer.compare(mEnumValue.getValue(), other.mEnumValue.getValue());
            case COMPACT_VALUE:
                return mCompactValue.compareTo(other.mCompactValue);
            default: return 0;
        }
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
                    length += writer.writeInt(mEnumValue.getValue());
                    break;
                }
                case COMPACT_VALUE: {
                    length += writer.writeByte((byte) 12);
                    length += writer.writeShort((short) 10);
                    length += net.morimekta.providence.serializer.rw.BinaryFormatUtils.writeMessage(writer, mCompactValue);
                    break;
                }
                default: break;
            }
        }
        length += writer.writeByte((byte) 0);
        return length;
    }

    @Override
    public _Builder mutate() {
        return new _Builder(this);
    }

    public enum _Field implements net.morimekta.providence.descriptor.PField {
        BOOLEAN_VALUE(1, net.morimekta.providence.descriptor.PRequirement.DEFAULT, "booleanValue", net.morimekta.providence.descriptor.PPrimitive.BOOL.provider(), null),
        BYTE_VALUE(2, net.morimekta.providence.descriptor.PRequirement.DEFAULT, "byteValue", net.morimekta.providence.descriptor.PPrimitive.BYTE.provider(), null),
        SHORT_VALUE(3, net.morimekta.providence.descriptor.PRequirement.DEFAULT, "shortValue", net.morimekta.providence.descriptor.PPrimitive.I16.provider(), null),
        INTEGER_VALUE(4, net.morimekta.providence.descriptor.PRequirement.DEFAULT, "integerValue", net.morimekta.providence.descriptor.PPrimitive.I32.provider(), null),
        LONG_VALUE(5, net.morimekta.providence.descriptor.PRequirement.DEFAULT, "longValue", net.morimekta.providence.descriptor.PPrimitive.I64.provider(), null),
        DOUBLE_VALUE(6, net.morimekta.providence.descriptor.PRequirement.DEFAULT, "doubleValue", net.morimekta.providence.descriptor.PPrimitive.DOUBLE.provider(), null),
        STRING_VALUE(7, net.morimekta.providence.descriptor.PRequirement.DEFAULT, "stringValue", net.morimekta.providence.descriptor.PPrimitive.STRING.provider(), null),
        BINARY_VALUE(8, net.morimekta.providence.descriptor.PRequirement.DEFAULT, "binaryValue", net.morimekta.providence.descriptor.PPrimitive.BINARY.provider(), null),
        ENUM_VALUE(9, net.morimekta.providence.descriptor.PRequirement.DEFAULT, "enumValue", net.morimekta.test.providence.Value.provider(), null),
        COMPACT_VALUE(10, net.morimekta.providence.descriptor.PRequirement.DEFAULT, "compactValue", net.morimekta.test.providence.CompactFields.provider(), null),
        ;

        private final int mKey;
        private final net.morimekta.providence.descriptor.PRequirement mRequired;
        private final String mName;
        private final net.morimekta.providence.descriptor.PDescriptorProvider mTypeProvider;
        private final net.morimekta.providence.descriptor.PValueProvider<?> mDefaultValue;

        _Field(int key, net.morimekta.providence.descriptor.PRequirement required, String name, net.morimekta.providence.descriptor.PDescriptorProvider typeProvider, net.morimekta.providence.descriptor.PValueProvider<?> defaultValue) {
            mKey = key;
            mRequired = required;
            mName = name;
            mTypeProvider = typeProvider;
            mDefaultValue = defaultValue;
        }

        @Override
        public int getKey() { return mKey; }

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
            return net.morimekta.providence.descriptor.PField.toString(this);
        }

        public static _Field forKey(int key) {
            switch (key) {
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

        public static _Field forName(String name) {
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
    }

    public static net.morimekta.providence.descriptor.PUnionDescriptorProvider<UnionFields,_Field> provider() {
        return new _Provider();
    }

    @Override
    public net.morimekta.providence.descriptor.PUnionDescriptor<UnionFields,_Field> descriptor() {
        return kDescriptor;
    }

    public static final net.morimekta.providence.descriptor.PUnionDescriptor<UnionFields,_Field> kDescriptor;

    private static class _Descriptor
            extends net.morimekta.providence.descriptor.PUnionDescriptor<UnionFields,_Field> {
        public _Descriptor() {
            super("providence", "UnionFields", new _Factory(), false);
        }

        @Override
        public _Field[] getFields() {
            return _Field.values();
        }

        @Override
        public _Field getField(String name) {
            return _Field.forName(name);
        }

        @Override
        public _Field getField(int key) {
            return _Field.forKey(key);
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

    private final static class _Factory
            extends net.morimekta.providence.PMessageBuilderFactory<UnionFields,_Field> {
        @Override
        public _Builder builder() {
            return new _Builder();
        }
    }

    /**
     * Make a providence.UnionFields builder.
     * @return The builder instance.
     */
    public static _Builder builder() {
        return new _Builder();
    }

    public static class _Builder
            extends net.morimekta.providence.PMessageBuilder<UnionFields,_Field>
            implements net.morimekta.providence.serializer.rw.BinaryReader {
        private _Field tUnionField;

        private boolean modified;

        private boolean mBooleanValue;
        private byte mByteValue;
        private short mShortValue;
        private int mIntegerValue;
        private long mLongValue;
        private double mDoubleValue;
        private String mStringValue;
        private net.morimekta.util.Binary mBinaryValue;
        private net.morimekta.test.providence.Value mEnumValue;
        private net.morimekta.test.providence.CompactFields mCompactValue;
        private net.morimekta.test.providence.CompactFields._Builder mCompactValue_builder;

        /**
         * Make a providence.UnionFields builder.
         */
        public _Builder() {
            modified = false;
            mBooleanValue = kDefaultBooleanValue;
            mByteValue = kDefaultByteValue;
            mShortValue = kDefaultShortValue;
            mIntegerValue = kDefaultIntegerValue;
            mLongValue = kDefaultLongValue;
            mDoubleValue = kDefaultDoubleValue;
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

        @Override
        public _Builder merge(UnionFields from) {
            if (from.unionField() == null) {
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
         * Sets the value of booleanValue.
         *
         * @param value The new value
         * @return The builder
         */
        public _Builder setBooleanValue(boolean value) {
            tUnionField = _Field.BOOLEAN_VALUE;
            modified = true;
            mBooleanValue = value;
            return this;
        }

        /**
         * Checks for presence of the booleanValue field.
         *
         * @return True if booleanValue has been set.
         */
        public boolean isSetBooleanValue() {
            return tUnionField == _Field.BOOLEAN_VALUE;
        }

        /**
         * Clears the booleanValue field.
         *
         * @return The builder
         */
        public _Builder clearBooleanValue() {
            if (tUnionField == _Field.BOOLEAN_VALUE) tUnionField = null;
            modified = true;
            mBooleanValue = kDefaultBooleanValue;
            return this;
        }

        /**
         * Gets the value of the contained booleanValue.
         *
         * @return The field value
         */
        public boolean isBooleanValue() {
            return mBooleanValue;
        }

        /**
         * Sets the value of byteValue.
         *
         * @param value The new value
         * @return The builder
         */
        public _Builder setByteValue(byte value) {
            tUnionField = _Field.BYTE_VALUE;
            modified = true;
            mByteValue = value;
            return this;
        }

        /**
         * Checks for presence of the byteValue field.
         *
         * @return True if byteValue has been set.
         */
        public boolean isSetByteValue() {
            return tUnionField == _Field.BYTE_VALUE;
        }

        /**
         * Clears the byteValue field.
         *
         * @return The builder
         */
        public _Builder clearByteValue() {
            if (tUnionField == _Field.BYTE_VALUE) tUnionField = null;
            modified = true;
            mByteValue = kDefaultByteValue;
            return this;
        }

        /**
         * Gets the value of the contained byteValue.
         *
         * @return The field value
         */
        public byte getByteValue() {
            return mByteValue;
        }

        /**
         * Sets the value of shortValue.
         *
         * @param value The new value
         * @return The builder
         */
        public _Builder setShortValue(short value) {
            tUnionField = _Field.SHORT_VALUE;
            modified = true;
            mShortValue = value;
            return this;
        }

        /**
         * Checks for presence of the shortValue field.
         *
         * @return True if shortValue has been set.
         */
        public boolean isSetShortValue() {
            return tUnionField == _Field.SHORT_VALUE;
        }

        /**
         * Clears the shortValue field.
         *
         * @return The builder
         */
        public _Builder clearShortValue() {
            if (tUnionField == _Field.SHORT_VALUE) tUnionField = null;
            modified = true;
            mShortValue = kDefaultShortValue;
            return this;
        }

        /**
         * Gets the value of the contained shortValue.
         *
         * @return The field value
         */
        public short getShortValue() {
            return mShortValue;
        }

        /**
         * Sets the value of integerValue.
         *
         * @param value The new value
         * @return The builder
         */
        public _Builder setIntegerValue(int value) {
            tUnionField = _Field.INTEGER_VALUE;
            modified = true;
            mIntegerValue = value;
            return this;
        }

        /**
         * Checks for presence of the integerValue field.
         *
         * @return True if integerValue has been set.
         */
        public boolean isSetIntegerValue() {
            return tUnionField == _Field.INTEGER_VALUE;
        }

        /**
         * Clears the integerValue field.
         *
         * @return The builder
         */
        public _Builder clearIntegerValue() {
            if (tUnionField == _Field.INTEGER_VALUE) tUnionField = null;
            modified = true;
            mIntegerValue = kDefaultIntegerValue;
            return this;
        }

        /**
         * Gets the value of the contained integerValue.
         *
         * @return The field value
         */
        public int getIntegerValue() {
            return mIntegerValue;
        }

        /**
         * Sets the value of longValue.
         *
         * @param value The new value
         * @return The builder
         */
        public _Builder setLongValue(long value) {
            tUnionField = _Field.LONG_VALUE;
            modified = true;
            mLongValue = value;
            return this;
        }

        /**
         * Checks for presence of the longValue field.
         *
         * @return True if longValue has been set.
         */
        public boolean isSetLongValue() {
            return tUnionField == _Field.LONG_VALUE;
        }

        /**
         * Clears the longValue field.
         *
         * @return The builder
         */
        public _Builder clearLongValue() {
            if (tUnionField == _Field.LONG_VALUE) tUnionField = null;
            modified = true;
            mLongValue = kDefaultLongValue;
            return this;
        }

        /**
         * Gets the value of the contained longValue.
         *
         * @return The field value
         */
        public long getLongValue() {
            return mLongValue;
        }

        /**
         * Sets the value of doubleValue.
         *
         * @param value The new value
         * @return The builder
         */
        public _Builder setDoubleValue(double value) {
            tUnionField = _Field.DOUBLE_VALUE;
            modified = true;
            mDoubleValue = value;
            return this;
        }

        /**
         * Checks for presence of the doubleValue field.
         *
         * @return True if doubleValue has been set.
         */
        public boolean isSetDoubleValue() {
            return tUnionField == _Field.DOUBLE_VALUE;
        }

        /**
         * Clears the doubleValue field.
         *
         * @return The builder
         */
        public _Builder clearDoubleValue() {
            if (tUnionField == _Field.DOUBLE_VALUE) tUnionField = null;
            modified = true;
            mDoubleValue = kDefaultDoubleValue;
            return this;
        }

        /**
         * Gets the value of the contained doubleValue.
         *
         * @return The field value
         */
        public double getDoubleValue() {
            return mDoubleValue;
        }

        /**
         * Sets the value of stringValue.
         *
         * @param value The new value
         * @return The builder
         */
        public _Builder setStringValue(String value) {
            tUnionField = _Field.STRING_VALUE;
            modified = true;
            mStringValue = value;
            return this;
        }

        /**
         * Checks for presence of the stringValue field.
         *
         * @return True if stringValue has been set.
         */
        public boolean isSetStringValue() {
            return tUnionField == _Field.STRING_VALUE;
        }

        /**
         * Clears the stringValue field.
         *
         * @return The builder
         */
        public _Builder clearStringValue() {
            if (tUnionField == _Field.STRING_VALUE) tUnionField = null;
            modified = true;
            mStringValue = null;
            return this;
        }

        /**
         * Gets the value of the contained stringValue.
         *
         * @return The field value
         */
        public String getStringValue() {
            return mStringValue;
        }

        /**
         * Sets the value of binaryValue.
         *
         * @param value The new value
         * @return The builder
         */
        public _Builder setBinaryValue(net.morimekta.util.Binary value) {
            tUnionField = _Field.BINARY_VALUE;
            modified = true;
            mBinaryValue = value;
            return this;
        }

        /**
         * Checks for presence of the binaryValue field.
         *
         * @return True if binaryValue has been set.
         */
        public boolean isSetBinaryValue() {
            return tUnionField == _Field.BINARY_VALUE;
        }

        /**
         * Clears the binaryValue field.
         *
         * @return The builder
         */
        public _Builder clearBinaryValue() {
            if (tUnionField == _Field.BINARY_VALUE) tUnionField = null;
            modified = true;
            mBinaryValue = null;
            return this;
        }

        /**
         * Gets the value of the contained binaryValue.
         *
         * @return The field value
         */
        public net.morimekta.util.Binary getBinaryValue() {
            return mBinaryValue;
        }

        /**
         * Sets the value of enumValue.
         *
         * @param value The new value
         * @return The builder
         */
        public _Builder setEnumValue(net.morimekta.test.providence.Value value) {
            tUnionField = _Field.ENUM_VALUE;
            modified = true;
            mEnumValue = value;
            return this;
        }

        /**
         * Checks for presence of the enumValue field.
         *
         * @return True if enumValue has been set.
         */
        public boolean isSetEnumValue() {
            return tUnionField == _Field.ENUM_VALUE;
        }

        /**
         * Clears the enumValue field.
         *
         * @return The builder
         */
        public _Builder clearEnumValue() {
            if (tUnionField == _Field.ENUM_VALUE) tUnionField = null;
            modified = true;
            mEnumValue = null;
            return this;
        }

        /**
         * Gets the value of the contained enumValue.
         *
         * @return The field value
         */
        public net.morimekta.test.providence.Value getEnumValue() {
            return mEnumValue;
        }

        /**
         * Sets the value of compactValue.
         *
         * @param value The new value
         * @return The builder
         */
        public _Builder setCompactValue(net.morimekta.test.providence.CompactFields value) {
            tUnionField = _Field.COMPACT_VALUE;
            modified = true;
            mCompactValue_builder = null;
            mCompactValue = value;
            return this;
        }

        /**
         * Checks for presence of the compactValue field.
         *
         * @return True if compactValue has been set.
         */
        public boolean isSetCompactValue() {
            return tUnionField == _Field.COMPACT_VALUE;
        }

        /**
         * Clears the compactValue field.
         *
         * @return The builder
         */
        public _Builder clearCompactValue() {
            if (tUnionField == _Field.COMPACT_VALUE) tUnionField = null;
            modified = true;
            mCompactValue = null;
            mCompactValue_builder = null;
            return this;
        }

        /**
         * Gets the builder for the contained compactValue.
         *
         * @return The field builder
         */
        public net.morimekta.test.providence.CompactFields._Builder mutableCompactValue() {
            if (tUnionField != _Field.COMPACT_VALUE) {
                clearCompactValue();
            }
            tUnionField = _Field.COMPACT_VALUE;
            modified = true;

            if (mCompactValue != null) {
                mCompactValue_builder = mCompactValue.mutate();
                mCompactValue = null;
            } else if (mCompactValue_builder == null) {
                mCompactValue_builder = net.morimekta.test.providence.CompactFields.builder();
            }
            return mCompactValue_builder;
        }

        /**
         * Checks if UnionFields has been modified since the _Builder was created.
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
                   java.util.Objects.equals(mCompactValue, other.mCompactValue);
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
                    _Field.COMPACT_VALUE, mCompactValue);
        }

        @Override
        @SuppressWarnings("unchecked")
        public net.morimekta.providence.PMessageBuilder mutator(int key) {
            switch (key) {
                case 10: return mutableCompactValue();
                default: throw new IllegalArgumentException("Not a message field ID: " + key);
            }
        }

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
                case 9: setEnumValue((net.morimekta.test.providence.Value) value); break;
                case 10: setCompactValue((net.morimekta.test.providence.CompactFields) value); break;
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
                            throw new net.morimekta.providence.serializer.SerializerException("Wrong type " + net.morimekta.providence.PType.nameForId(type) + "(" + type + ") for providence.UnionFields.booleanValue, should be message(12)");
                        }
                        break;
                    }
                    case 2: {
                        if (type == 3) {
                            mByteValue = reader.expectByte();
                            tUnionField = _Field.BYTE_VALUE;
                        } else {
                            throw new net.morimekta.providence.serializer.SerializerException("Wrong type " + net.morimekta.providence.PType.nameForId(type) + "(" + type + ") for providence.UnionFields.byteValue, should be message(12)");
                        }
                        break;
                    }
                    case 3: {
                        if (type == 6) {
                            mShortValue = reader.expectShort();
                            tUnionField = _Field.SHORT_VALUE;
                        } else {
                            throw new net.morimekta.providence.serializer.SerializerException("Wrong type " + net.morimekta.providence.PType.nameForId(type) + "(" + type + ") for providence.UnionFields.shortValue, should be message(12)");
                        }
                        break;
                    }
                    case 4: {
                        if (type == 8) {
                            mIntegerValue = reader.expectInt();
                            tUnionField = _Field.INTEGER_VALUE;
                        } else {
                            throw new net.morimekta.providence.serializer.SerializerException("Wrong type " + net.morimekta.providence.PType.nameForId(type) + "(" + type + ") for providence.UnionFields.integerValue, should be message(12)");
                        }
                        break;
                    }
                    case 5: {
                        if (type == 10) {
                            mLongValue = reader.expectLong();
                            tUnionField = _Field.LONG_VALUE;
                        } else {
                            throw new net.morimekta.providence.serializer.SerializerException("Wrong type " + net.morimekta.providence.PType.nameForId(type) + "(" + type + ") for providence.UnionFields.longValue, should be message(12)");
                        }
                        break;
                    }
                    case 6: {
                        if (type == 4) {
                            mDoubleValue = reader.expectDouble();
                            tUnionField = _Field.DOUBLE_VALUE;
                        } else {
                            throw new net.morimekta.providence.serializer.SerializerException("Wrong type " + net.morimekta.providence.PType.nameForId(type) + "(" + type + ") for providence.UnionFields.doubleValue, should be message(12)");
                        }
                        break;
                    }
                    case 7: {
                        if (type == 11) {
                            int len_1 = reader.expectUInt32();
                            mStringValue = new String(reader.expectBytes(len_1), java.nio.charset.StandardCharsets.UTF_8);
                            tUnionField = _Field.STRING_VALUE;
                        } else {
                            throw new net.morimekta.providence.serializer.SerializerException("Wrong type " + net.morimekta.providence.PType.nameForId(type) + "(" + type + ") for providence.UnionFields.stringValue, should be message(12)");
                        }
                        break;
                    }
                    case 8: {
                        if (type == 11) {
                            int len_2 = reader.expectUInt32();
                            mBinaryValue = reader.expectBinary(len_2);
                            tUnionField = _Field.BINARY_VALUE;
                        } else {
                            throw new net.morimekta.providence.serializer.SerializerException("Wrong type " + net.morimekta.providence.PType.nameForId(type) + "(" + type + ") for providence.UnionFields.binaryValue, should be message(12)");
                        }
                        break;
                    }
                    case 9: {
                        if (type == 8) {
                            mEnumValue = net.morimekta.test.providence.Value.forValue(reader.expectInt());
                            tUnionField = _Field.ENUM_VALUE;
                        } else {
                            throw new net.morimekta.providence.serializer.SerializerException("Wrong type " + net.morimekta.providence.PType.nameForId(type) + "(" + type + ") for providence.UnionFields.enumValue, should be message(12)");
                        }
                        break;
                    }
                    case 10: {
                        if (type == 12) {
                            mCompactValue = net.morimekta.providence.serializer.rw.BinaryFormatUtils.readMessage(reader, net.morimekta.test.providence.CompactFields.kDescriptor, strict);
                            tUnionField = _Field.COMPACT_VALUE;
                        } else {
                            throw new net.morimekta.providence.serializer.SerializerException("Wrong type " + net.morimekta.providence.PType.nameForId(type) + "(" + type + ") for providence.UnionFields.compactValue, should be message(12)");
                        }
                        break;
                    }
                    default: {
                        if (strict) {
                            throw new net.morimekta.providence.serializer.SerializerException("No field with id " + field + " exists in providence.UnionFields");
                        } else {
                            net.morimekta.providence.serializer.rw.BinaryFormatUtils.readFieldValue(reader, new net.morimekta.providence.serializer.rw.BinaryFormatUtils.FieldInfo(field, type), null, false);
                        }
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
