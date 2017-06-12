package net.morimekta.test.providence.reflect;

@SuppressWarnings("unused")
@javax.annotation.Generated("providence java generator")
public class DefaultValues
        implements net.morimekta.providence.PMessage<DefaultValues,DefaultValues._Field>,
                   Comparable<DefaultValues>,
                   java.io.Serializable,
                   net.morimekta.providence.serializer.binary.BinaryWriter {
    private final static long serialVersionUID = 1589448735484096354L;

    private final static boolean kDefaultBooleanValue = true;
    private final static byte kDefaultByteValue = (byte)-125;
    private final static short kDefaultShortValue = (short)13579;
    private final static int kDefaultIntegerValue = 1234567890;
    private final static long kDefaultLongValue = 1234567891L;
    private final static double kDefaultDoubleValue = 2.99792458E8d;
    private final static String kDefaultStringValue = "test\\twith escapes\\nand\\u00a0ũñı©ôðé.";
    private final static net.morimekta.test.providence.reflect.Value kDefaultEnumValue = net.morimekta.test.providence.reflect.Value.SECOND;

    private final Boolean mBooleanValue;
    private final Byte mByteValue;
    private final Short mShortValue;
    private final Integer mIntegerValue;
    private final Long mLongValue;
    private final Double mDoubleValue;
    private final String mStringValue;
    private final net.morimekta.util.Binary mBinaryValue;
    private final net.morimekta.test.providence.reflect.Value mEnumValue;
    private final net.morimekta.test.providence.reflect.CompactFields mCompactValue;

    private volatile int tHashCode;

    public DefaultValues(Boolean pBooleanValue,
                         Byte pByteValue,
                         Short pShortValue,
                         Integer pIntegerValue,
                         Long pLongValue,
                         Double pDoubleValue,
                         String pStringValue,
                         net.morimekta.util.Binary pBinaryValue,
                         net.morimekta.test.providence.reflect.Value pEnumValue,
                         net.morimekta.test.providence.reflect.CompactFields pCompactValue) {
        mBooleanValue = pBooleanValue;
        mByteValue = pByteValue;
        mShortValue = pShortValue;
        mIntegerValue = pIntegerValue;
        mLongValue = pLongValue;
        mDoubleValue = pDoubleValue;
        mStringValue = pStringValue;
        mBinaryValue = pBinaryValue;
        mEnumValue = pEnumValue;
        mCompactValue = pCompactValue;
    }

    private DefaultValues(_Builder builder) {
        mBooleanValue = builder.mBooleanValue;
        mByteValue = builder.mByteValue;
        mShortValue = builder.mShortValue;
        mIntegerValue = builder.mIntegerValue;
        mLongValue = builder.mLongValue;
        mDoubleValue = builder.mDoubleValue;
        mStringValue = builder.mStringValue;
        mBinaryValue = builder.mBinaryValue;
        mEnumValue = builder.mEnumValue;
        mCompactValue = builder.mCompactValue_builder != null ? builder.mCompactValue_builder.build() : builder.mCompactValue;
    }

    public boolean hasBooleanValue() {
        return mBooleanValue != null;
    }

    /**
     * @return The field value
     */
    public boolean isBooleanValue() {
        return hasBooleanValue() ? mBooleanValue : kDefaultBooleanValue;
    }

    public boolean hasByteValue() {
        return mByteValue != null;
    }

    /**
     * @return The field value
     */
    public byte getByteValue() {
        return hasByteValue() ? mByteValue : kDefaultByteValue;
    }

    public boolean hasShortValue() {
        return mShortValue != null;
    }

    /**
     * @return The field value
     */
    public short getShortValue() {
        return hasShortValue() ? mShortValue : kDefaultShortValue;
    }

    public boolean hasIntegerValue() {
        return mIntegerValue != null;
    }

    /**
     * @return The field value
     */
    public int getIntegerValue() {
        return hasIntegerValue() ? mIntegerValue : kDefaultIntegerValue;
    }

    public boolean hasLongValue() {
        return mLongValue != null;
    }

    /**
     * @return The field value
     */
    public long getLongValue() {
        return hasLongValue() ? mLongValue : kDefaultLongValue;
    }

    public boolean hasDoubleValue() {
        return mDoubleValue != null;
    }

    /**
     * @return The field value
     */
    public double getDoubleValue() {
        return hasDoubleValue() ? mDoubleValue : kDefaultDoubleValue;
    }

    public boolean hasStringValue() {
        return mStringValue != null;
    }

    /**
     * @return The field value
     */
    public String getStringValue() {
        return hasStringValue() ? mStringValue : kDefaultStringValue;
    }

    public boolean hasBinaryValue() {
        return mBinaryValue != null;
    }

    /**
     * @return The field value
     */
    public net.morimekta.util.Binary getBinaryValue() {
        return mBinaryValue;
    }

    public boolean hasEnumValue() {
        return mEnumValue != null;
    }

    /**
     * @return The field value
     */
    public net.morimekta.test.providence.reflect.Value getEnumValue() {
        return hasEnumValue() ? mEnumValue : kDefaultEnumValue;
    }

    public boolean hasCompactValue() {
        return mCompactValue != null;
    }

    /**
     * @return The field value
     */
    public net.morimekta.test.providence.reflect.CompactFields getCompactValue() {
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
    public boolean equals(Object o) {
        if (o == this) return true;
        if (o == null || !o.getClass().equals(getClass())) return false;
        DefaultValues other = (DefaultValues) o;
        return java.util.Objects.equals(mBooleanValue, other.mBooleanValue) &&
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
                    DefaultValues.class,
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
        return "providence.DefaultValues" + asString();
    }

    @Override
    @javax.annotation.Nonnull
    public String asString() {
        StringBuilder out = new StringBuilder();
        out.append("{");

        boolean first = true;
        if (hasBooleanValue()) {
            first = false;
            out.append("booleanValue:")
               .append(mBooleanValue);
        }
        if (hasByteValue()) {
            if (first) first = false;
            else out.append(',');
            out.append("byteValue:")
               .append((int) mByteValue);
        }
        if (hasShortValue()) {
            if (first) first = false;
            else out.append(',');
            out.append("shortValue:")
               .append((int) mShortValue);
        }
        if (hasIntegerValue()) {
            if (first) first = false;
            else out.append(',');
            out.append("integerValue:")
               .append(mIntegerValue);
        }
        if (hasLongValue()) {
            if (first) first = false;
            else out.append(',');
            out.append("longValue:")
               .append(mLongValue);
        }
        if (hasDoubleValue()) {
            if (first) first = false;
            else out.append(',');
            out.append("doubleValue:")
               .append(net.morimekta.util.Strings.asString(mDoubleValue));
        }
        if (hasStringValue()) {
            if (first) first = false;
            else out.append(',');
            out.append("stringValue:")
               .append('\"')
               .append(net.morimekta.util.Strings.escape(mStringValue))
               .append('\"');
        }
        if (hasBinaryValue()) {
            if (first) first = false;
            else out.append(',');
            out.append("binaryValue:")
               .append("b64(")
               .append(mBinaryValue.toBase64())
               .append(')');
        }
        if (hasEnumValue()) {
            if (first) first = false;
            else out.append(',');
            out.append("enumValue:")
               .append(mEnumValue.asString());
        }
        if (hasCompactValue()) {
            if (!first) out.append(',');
            out.append("compactValue:")
               .append(mCompactValue.asString());
        }
        out.append('}');
        return out.toString();
    }

    @Override
    public int compareTo(DefaultValues other) {
        int c;

        c = Boolean.compare(mBooleanValue != null, other.mBooleanValue != null);
        if (c != 0) return c;
        if (mBooleanValue != null) {
            c = Boolean.compare(mBooleanValue, other.mBooleanValue);
            if (c != 0) return c;
        }

        c = Boolean.compare(mByteValue != null, other.mByteValue != null);
        if (c != 0) return c;
        if (mByteValue != null) {
            c = Byte.compare(mByteValue, other.mByteValue);
            if (c != 0) return c;
        }

        c = Boolean.compare(mShortValue != null, other.mShortValue != null);
        if (c != 0) return c;
        if (mShortValue != null) {
            c = Short.compare(mShortValue, other.mShortValue);
            if (c != 0) return c;
        }

        c = Boolean.compare(mIntegerValue != null, other.mIntegerValue != null);
        if (c != 0) return c;
        if (mIntegerValue != null) {
            c = Integer.compare(mIntegerValue, other.mIntegerValue);
            if (c != 0) return c;
        }

        c = Boolean.compare(mLongValue != null, other.mLongValue != null);
        if (c != 0) return c;
        if (mLongValue != null) {
            c = Long.compare(mLongValue, other.mLongValue);
            if (c != 0) return c;
        }

        c = Boolean.compare(mDoubleValue != null, other.mDoubleValue != null);
        if (c != 0) return c;
        if (mDoubleValue != null) {
            c = Double.compare(mDoubleValue, other.mDoubleValue);
            if (c != 0) return c;
        }

        c = Boolean.compare(mStringValue != null, other.mStringValue != null);
        if (c != 0) return c;
        if (mStringValue != null) {
            c = mStringValue.compareTo(other.mStringValue);
            if (c != 0) return c;
        }

        c = Boolean.compare(mBinaryValue != null, other.mBinaryValue != null);
        if (c != 0) return c;
        if (mBinaryValue != null) {
            c = mBinaryValue.compareTo(other.mBinaryValue);
            if (c != 0) return c;
        }

        c = Boolean.compare(mEnumValue != null, other.mEnumValue != null);
        if (c != 0) return c;
        if (mEnumValue != null) {
            c = Integer.compare(mEnumValue.ordinal(), mEnumValue.ordinal());
            if (c != 0) return c;
        }

        c = Boolean.compare(mCompactValue != null, other.mCompactValue != null);
        if (c != 0) return c;
        if (mCompactValue != null) {
            c = mCompactValue.compareTo(other.mCompactValue);
            if (c != 0) return c;
        }

        return 0;
    }

    @Override
    public int writeBinary(net.morimekta.util.io.BigEndianBinaryWriter writer) throws java.io.IOException {
        int length = 0;

        if (hasBooleanValue()) {
            length += writer.writeByte((byte) 2);
            length += writer.writeShort((short) 1);
            length += writer.writeUInt8(mBooleanValue ? (byte) 1 : (byte) 0);
        }

        if (hasByteValue()) {
            length += writer.writeByte((byte) 3);
            length += writer.writeShort((short) 2);
            length += writer.writeByte(mByteValue);
        }

        if (hasShortValue()) {
            length += writer.writeByte((byte) 6);
            length += writer.writeShort((short) 3);
            length += writer.writeShort(mShortValue);
        }

        if (hasIntegerValue()) {
            length += writer.writeByte((byte) 8);
            length += writer.writeShort((short) 4);
            length += writer.writeInt(mIntegerValue);
        }

        if (hasLongValue()) {
            length += writer.writeByte((byte) 10);
            length += writer.writeShort((short) 5);
            length += writer.writeLong(mLongValue);
        }

        if (hasDoubleValue()) {
            length += writer.writeByte((byte) 4);
            length += writer.writeShort((short) 6);
            length += writer.writeDouble(mDoubleValue);
        }

        if (hasStringValue()) {
            length += writer.writeByte((byte) 11);
            length += writer.writeShort((short) 7);
            net.morimekta.util.Binary tmp_1 = net.morimekta.util.Binary.wrap(mStringValue.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            length += writer.writeUInt32(tmp_1.length());
            length += writer.writeBinary(tmp_1);
        }

        if (hasBinaryValue()) {
            length += writer.writeByte((byte) 11);
            length += writer.writeShort((short) 8);
            length += writer.writeUInt32(mBinaryValue.length());
            length += writer.writeBinary(mBinaryValue);
        }

        if (hasEnumValue()) {
            length += writer.writeByte((byte) 8);
            length += writer.writeShort((short) 9);
            length += writer.writeInt(mEnumValue.asInteger());
        }

        if (hasCompactValue()) {
            length += writer.writeByte((byte) 12);
            length += writer.writeShort((short) 10);
            length += net.morimekta.providence.serializer.binary.BinaryFormatUtils.writeMessage(writer, mCompactValue);
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
        BOOLEAN_VALUE(1, net.morimekta.providence.descriptor.PRequirement.OPTIONAL, "booleanValue", net.morimekta.providence.descriptor.PPrimitive.BOOL.provider(), new net.morimekta.providence.descriptor.PDefaultValueProvider<>(kDefaultBooleanValue)),
        BYTE_VALUE(2, net.morimekta.providence.descriptor.PRequirement.OPTIONAL, "byteValue", net.morimekta.providence.descriptor.PPrimitive.BYTE.provider(), new net.morimekta.providence.descriptor.PDefaultValueProvider<>(kDefaultByteValue)),
        SHORT_VALUE(3, net.morimekta.providence.descriptor.PRequirement.OPTIONAL, "shortValue", net.morimekta.providence.descriptor.PPrimitive.I16.provider(), new net.morimekta.providence.descriptor.PDefaultValueProvider<>(kDefaultShortValue)),
        INTEGER_VALUE(4, net.morimekta.providence.descriptor.PRequirement.OPTIONAL, "integerValue", net.morimekta.providence.descriptor.PPrimitive.I32.provider(), new net.morimekta.providence.descriptor.PDefaultValueProvider<>(kDefaultIntegerValue)),
        LONG_VALUE(5, net.morimekta.providence.descriptor.PRequirement.OPTIONAL, "longValue", net.morimekta.providence.descriptor.PPrimitive.I64.provider(), new net.morimekta.providence.descriptor.PDefaultValueProvider<>(kDefaultLongValue)),
        DOUBLE_VALUE(6, net.morimekta.providence.descriptor.PRequirement.OPTIONAL, "doubleValue", net.morimekta.providence.descriptor.PPrimitive.DOUBLE.provider(), new net.morimekta.providence.descriptor.PDefaultValueProvider<>(kDefaultDoubleValue)),
        STRING_VALUE(7, net.morimekta.providence.descriptor.PRequirement.OPTIONAL, "stringValue", net.morimekta.providence.descriptor.PPrimitive.STRING.provider(), new net.morimekta.providence.descriptor.PDefaultValueProvider<>(kDefaultStringValue)),
        BINARY_VALUE(8, net.morimekta.providence.descriptor.PRequirement.OPTIONAL, "binaryValue", net.morimekta.providence.descriptor.PPrimitive.BINARY.provider(), null),
        ENUM_VALUE(9, net.morimekta.providence.descriptor.PRequirement.OPTIONAL, "enumValue", net.morimekta.test.providence.reflect.Value.provider(), new net.morimekta.providence.descriptor.PDefaultValueProvider<>(kDefaultEnumValue)),
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
                throw new IllegalArgumentException("No such field id " + id + " in providence.DefaultValues");
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
                throw new IllegalArgumentException("No such field \"" + name + "\" in providence.DefaultValues");
            }
            return field;
        }

    }

    public static net.morimekta.providence.descriptor.PStructDescriptorProvider<DefaultValues,_Field> provider() {
        return new _Provider();
    }

    @Override
    public net.morimekta.providence.descriptor.PStructDescriptor<DefaultValues,_Field> descriptor() {
        return kDescriptor;
    }

    public static final net.morimekta.providence.descriptor.PStructDescriptor<DefaultValues,_Field> kDescriptor;

    private static class _Descriptor
            extends net.morimekta.providence.descriptor.PStructDescriptor<DefaultValues,_Field> {
        public _Descriptor() {
            super("providence", "DefaultValues", _Builder::new, false);
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

    private final static class _Provider extends net.morimekta.providence.descriptor.PStructDescriptorProvider<DefaultValues,_Field> {
        @Override
        public net.morimekta.providence.descriptor.PStructDescriptor<DefaultValues,_Field> descriptor() {
            return kDescriptor;
        }
    }

    /**
     * Make a providence.DefaultValues builder.
     * @return The builder instance.
     */
    public static _Builder builder() {
        return new _Builder();
    }

    public static class _Builder
            extends net.morimekta.providence.PMessageBuilder<DefaultValues,_Field>
            implements net.morimekta.providence.serializer.binary.BinaryReader {
        private java.util.BitSet optionals;
        private java.util.BitSet modified;

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
         * Make a providence.DefaultValues builder.
         */
        public _Builder() {
            optionals = new java.util.BitSet(10);
            modified = new java.util.BitSet(10);
        }

        /**
         * Make a mutating builder off a base providence.DefaultValues.
         *
         * @param base The base DefaultValues
         */
        public _Builder(DefaultValues base) {
            this();

            if (base.hasBooleanValue()) {
                optionals.set(0);
                mBooleanValue = base.mBooleanValue;
            }
            if (base.hasByteValue()) {
                optionals.set(1);
                mByteValue = base.mByteValue;
            }
            if (base.hasShortValue()) {
                optionals.set(2);
                mShortValue = base.mShortValue;
            }
            if (base.hasIntegerValue()) {
                optionals.set(3);
                mIntegerValue = base.mIntegerValue;
            }
            if (base.hasLongValue()) {
                optionals.set(4);
                mLongValue = base.mLongValue;
            }
            if (base.hasDoubleValue()) {
                optionals.set(5);
                mDoubleValue = base.mDoubleValue;
            }
            if (base.hasStringValue()) {
                optionals.set(6);
                mStringValue = base.mStringValue;
            }
            if (base.hasBinaryValue()) {
                optionals.set(7);
                mBinaryValue = base.mBinaryValue;
            }
            if (base.hasEnumValue()) {
                optionals.set(8);
                mEnumValue = base.mEnumValue;
            }
            if (base.hasCompactValue()) {
                optionals.set(9);
                mCompactValue = base.mCompactValue;
            }
        }

        @javax.annotation.Nonnull
        @Override
        public _Builder merge(DefaultValues from) {
            if (from.hasBooleanValue()) {
                optionals.set(0);
                modified.set(0);
                mBooleanValue = from.isBooleanValue();
            }

            if (from.hasByteValue()) {
                optionals.set(1);
                modified.set(1);
                mByteValue = from.getByteValue();
            }

            if (from.hasShortValue()) {
                optionals.set(2);
                modified.set(2);
                mShortValue = from.getShortValue();
            }

            if (from.hasIntegerValue()) {
                optionals.set(3);
                modified.set(3);
                mIntegerValue = from.getIntegerValue();
            }

            if (from.hasLongValue()) {
                optionals.set(4);
                modified.set(4);
                mLongValue = from.getLongValue();
            }

            if (from.hasDoubleValue()) {
                optionals.set(5);
                modified.set(5);
                mDoubleValue = from.getDoubleValue();
            }

            if (from.hasStringValue()) {
                optionals.set(6);
                modified.set(6);
                mStringValue = from.getStringValue();
            }

            if (from.hasBinaryValue()) {
                optionals.set(7);
                modified.set(7);
                mBinaryValue = from.getBinaryValue();
            }

            if (from.hasEnumValue()) {
                optionals.set(8);
                modified.set(8);
                mEnumValue = from.getEnumValue();
            }

            if (from.hasCompactValue()) {
                optionals.set(9);
                modified.set(9);
                if (mCompactValue_builder != null) {
                    mCompactValue_builder.merge(from.getCompactValue());
                } else if (mCompactValue != null) {
                    mCompactValue_builder = mCompactValue.mutate().merge(from.getCompactValue());
                    mCompactValue = null;
                } else {
                    mCompactValue = from.getCompactValue();
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
        @javax.annotation.Nonnull
        public _Builder setBooleanValue(boolean value) {
            optionals.set(0);
            modified.set(0);
            mBooleanValue = value;
            return this;
        }

        /**
         * Checks for presence of the booleanValue field.
         *
         * @return True if booleanValue has been set.
         */
        public boolean isSetBooleanValue() {
            return optionals.get(0);
        }

        /**
         * Checks if booleanValue has been modified since the _Builder was created.
         *
         * @return True if booleanValue has been modified.
         */
        public boolean isModifiedBooleanValue() {
            return modified.get(0);
        }

        /**
         * Clears the booleanValue field.
         *
         * @return The builder
         */
        @javax.annotation.Nonnull
        public _Builder clearBooleanValue() {
            optionals.clear(0);
            modified.set(0);
            mBooleanValue = null;
            return this;
        }

        /**
         * Gets the value of the contained booleanValue.
         *
         * @return The field value
         */
        public boolean getBooleanValue() {
            return isSetBooleanValue() ? mBooleanValue : kDefaultBooleanValue;
        }

        /**
         * Sets the value of byteValue.
         *
         * @param value The new value
         * @return The builder
         */
        @javax.annotation.Nonnull
        public _Builder setByteValue(byte value) {
            optionals.set(1);
            modified.set(1);
            mByteValue = value;
            return this;
        }

        /**
         * Checks for presence of the byteValue field.
         *
         * @return True if byteValue has been set.
         */
        public boolean isSetByteValue() {
            return optionals.get(1);
        }

        /**
         * Checks if byteValue has been modified since the _Builder was created.
         *
         * @return True if byteValue has been modified.
         */
        public boolean isModifiedByteValue() {
            return modified.get(1);
        }

        /**
         * Clears the byteValue field.
         *
         * @return The builder
         */
        @javax.annotation.Nonnull
        public _Builder clearByteValue() {
            optionals.clear(1);
            modified.set(1);
            mByteValue = null;
            return this;
        }

        /**
         * Gets the value of the contained byteValue.
         *
         * @return The field value
         */
        public byte getByteValue() {
            return isSetByteValue() ? mByteValue : kDefaultByteValue;
        }

        /**
         * Sets the value of shortValue.
         *
         * @param value The new value
         * @return The builder
         */
        @javax.annotation.Nonnull
        public _Builder setShortValue(short value) {
            optionals.set(2);
            modified.set(2);
            mShortValue = value;
            return this;
        }

        /**
         * Checks for presence of the shortValue field.
         *
         * @return True if shortValue has been set.
         */
        public boolean isSetShortValue() {
            return optionals.get(2);
        }

        /**
         * Checks if shortValue has been modified since the _Builder was created.
         *
         * @return True if shortValue has been modified.
         */
        public boolean isModifiedShortValue() {
            return modified.get(2);
        }

        /**
         * Clears the shortValue field.
         *
         * @return The builder
         */
        @javax.annotation.Nonnull
        public _Builder clearShortValue() {
            optionals.clear(2);
            modified.set(2);
            mShortValue = null;
            return this;
        }

        /**
         * Gets the value of the contained shortValue.
         *
         * @return The field value
         */
        public short getShortValue() {
            return isSetShortValue() ? mShortValue : kDefaultShortValue;
        }

        /**
         * Sets the value of integerValue.
         *
         * @param value The new value
         * @return The builder
         */
        @javax.annotation.Nonnull
        public _Builder setIntegerValue(int value) {
            optionals.set(3);
            modified.set(3);
            mIntegerValue = value;
            return this;
        }

        /**
         * Checks for presence of the integerValue field.
         *
         * @return True if integerValue has been set.
         */
        public boolean isSetIntegerValue() {
            return optionals.get(3);
        }

        /**
         * Checks if integerValue has been modified since the _Builder was created.
         *
         * @return True if integerValue has been modified.
         */
        public boolean isModifiedIntegerValue() {
            return modified.get(3);
        }

        /**
         * Clears the integerValue field.
         *
         * @return The builder
         */
        @javax.annotation.Nonnull
        public _Builder clearIntegerValue() {
            optionals.clear(3);
            modified.set(3);
            mIntegerValue = null;
            return this;
        }

        /**
         * Gets the value of the contained integerValue.
         *
         * @return The field value
         */
        public int getIntegerValue() {
            return isSetIntegerValue() ? mIntegerValue : kDefaultIntegerValue;
        }

        /**
         * Sets the value of longValue.
         *
         * @param value The new value
         * @return The builder
         */
        @javax.annotation.Nonnull
        public _Builder setLongValue(long value) {
            optionals.set(4);
            modified.set(4);
            mLongValue = value;
            return this;
        }

        /**
         * Checks for presence of the longValue field.
         *
         * @return True if longValue has been set.
         */
        public boolean isSetLongValue() {
            return optionals.get(4);
        }

        /**
         * Checks if longValue has been modified since the _Builder was created.
         *
         * @return True if longValue has been modified.
         */
        public boolean isModifiedLongValue() {
            return modified.get(4);
        }

        /**
         * Clears the longValue field.
         *
         * @return The builder
         */
        @javax.annotation.Nonnull
        public _Builder clearLongValue() {
            optionals.clear(4);
            modified.set(4);
            mLongValue = null;
            return this;
        }

        /**
         * Gets the value of the contained longValue.
         *
         * @return The field value
         */
        public long getLongValue() {
            return isSetLongValue() ? mLongValue : kDefaultLongValue;
        }

        /**
         * Sets the value of doubleValue.
         *
         * @param value The new value
         * @return The builder
         */
        @javax.annotation.Nonnull
        public _Builder setDoubleValue(double value) {
            optionals.set(5);
            modified.set(5);
            mDoubleValue = value;
            return this;
        }

        /**
         * Checks for presence of the doubleValue field.
         *
         * @return True if doubleValue has been set.
         */
        public boolean isSetDoubleValue() {
            return optionals.get(5);
        }

        /**
         * Checks if doubleValue has been modified since the _Builder was created.
         *
         * @return True if doubleValue has been modified.
         */
        public boolean isModifiedDoubleValue() {
            return modified.get(5);
        }

        /**
         * Clears the doubleValue field.
         *
         * @return The builder
         */
        @javax.annotation.Nonnull
        public _Builder clearDoubleValue() {
            optionals.clear(5);
            modified.set(5);
            mDoubleValue = null;
            return this;
        }

        /**
         * Gets the value of the contained doubleValue.
         *
         * @return The field value
         */
        public double getDoubleValue() {
            return isSetDoubleValue() ? mDoubleValue : kDefaultDoubleValue;
        }

        /**
         * Sets the value of stringValue.
         *
         * @param value The new value
         * @return The builder
         */
        @javax.annotation.Nonnull
        public _Builder setStringValue(String value) {
            if (value == null) {
                return clearStringValue();
            }

            optionals.set(6);
            modified.set(6);
            mStringValue = value;
            return this;
        }

        /**
         * Checks for presence of the stringValue field.
         *
         * @return True if stringValue has been set.
         */
        public boolean isSetStringValue() {
            return optionals.get(6);
        }

        /**
         * Checks if stringValue has been modified since the _Builder was created.
         *
         * @return True if stringValue has been modified.
         */
        public boolean isModifiedStringValue() {
            return modified.get(6);
        }

        /**
         * Clears the stringValue field.
         *
         * @return The builder
         */
        @javax.annotation.Nonnull
        public _Builder clearStringValue() {
            optionals.clear(6);
            modified.set(6);
            mStringValue = null;
            return this;
        }

        /**
         * Gets the value of the contained stringValue.
         *
         * @return The field value
         */
        public String getStringValue() {
            return isSetStringValue() ? mStringValue : kDefaultStringValue;
        }

        /**
         * Sets the value of binaryValue.
         *
         * @param value The new value
         * @return The builder
         */
        @javax.annotation.Nonnull
        public _Builder setBinaryValue(net.morimekta.util.Binary value) {
            if (value == null) {
                return clearBinaryValue();
            }

            optionals.set(7);
            modified.set(7);
            mBinaryValue = value;
            return this;
        }

        /**
         * Checks for presence of the binaryValue field.
         *
         * @return True if binaryValue has been set.
         */
        public boolean isSetBinaryValue() {
            return optionals.get(7);
        }

        /**
         * Checks if binaryValue has been modified since the _Builder was created.
         *
         * @return True if binaryValue has been modified.
         */
        public boolean isModifiedBinaryValue() {
            return modified.get(7);
        }

        /**
         * Clears the binaryValue field.
         *
         * @return The builder
         */
        @javax.annotation.Nonnull
        public _Builder clearBinaryValue() {
            optionals.clear(7);
            modified.set(7);
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
        @javax.annotation.Nonnull
        public _Builder setEnumValue(net.morimekta.test.providence.reflect.Value value) {
            if (value == null) {
                return clearEnumValue();
            }

            optionals.set(8);
            modified.set(8);
            mEnumValue = value;
            return this;
        }

        /**
         * Checks for presence of the enumValue field.
         *
         * @return True if enumValue has been set.
         */
        public boolean isSetEnumValue() {
            return optionals.get(8);
        }

        /**
         * Checks if enumValue has been modified since the _Builder was created.
         *
         * @return True if enumValue has been modified.
         */
        public boolean isModifiedEnumValue() {
            return modified.get(8);
        }

        /**
         * Clears the enumValue field.
         *
         * @return The builder
         */
        @javax.annotation.Nonnull
        public _Builder clearEnumValue() {
            optionals.clear(8);
            modified.set(8);
            mEnumValue = null;
            return this;
        }

        /**
         * Gets the value of the contained enumValue.
         *
         * @return The field value
         */
        public net.morimekta.test.providence.reflect.Value getEnumValue() {
            return isSetEnumValue() ? mEnumValue : kDefaultEnumValue;
        }

        /**
         * Sets the value of compactValue.
         *
         * @param value The new value
         * @return The builder
         */
        @javax.annotation.Nonnull
        public _Builder setCompactValue(net.morimekta.test.providence.reflect.CompactFields value) {
            if (value == null) {
                return clearCompactValue();
            }

            optionals.set(9);
            modified.set(9);
            mCompactValue = value;
            mCompactValue_builder = null;
            return this;
        }

        /**
         * Checks for presence of the compactValue field.
         *
         * @return True if compactValue has been set.
         */
        public boolean isSetCompactValue() {
            return optionals.get(9);
        }

        /**
         * Checks if compactValue has been modified since the _Builder was created.
         *
         * @return True if compactValue has been modified.
         */
        public boolean isModifiedCompactValue() {
            return modified.get(9);
        }

        /**
         * Clears the compactValue field.
         *
         * @return The builder
         */
        @javax.annotation.Nonnull
        public _Builder clearCompactValue() {
            optionals.clear(9);
            modified.set(9);
            mCompactValue = null;
            mCompactValue_builder = null;
            return this;
        }

        /**
         * Gets the builder for the contained compactValue.
         *
         * @return The field builder
         */
        @javax.annotation.Nonnull
        public net.morimekta.test.providence.reflect.CompactFields._Builder mutableCompactValue() {
            optionals.set(9);
            modified.set(9);

            if (mCompactValue != null) {
                mCompactValue_builder = mCompactValue.mutate();
                mCompactValue = null;
            } else if (mCompactValue_builder == null) {
                mCompactValue_builder = net.morimekta.test.providence.reflect.CompactFields.builder();
            }
            return mCompactValue_builder;
        }

        @Override
        public boolean equals(Object o) {
            if (o == this) return true;
            if (o == null || !o.getClass().equals(getClass())) return false;
            DefaultValues._Builder other = (DefaultValues._Builder) o;
            return java.util.Objects.equals(optionals, other.optionals) &&
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
                    DefaultValues.class, optionals,
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
                case 1: return optionals.get(0);
                case 2: return optionals.get(1);
                case 3: return optionals.get(2);
                case 4: return optionals.get(3);
                case 5: return optionals.get(4);
                case 6: return optionals.get(5);
                case 7: return optionals.get(6);
                case 8: return optionals.get(7);
                case 9: return optionals.get(8);
                case 10: return optionals.get(9);
                default: break;
            }
            return false;
        }

        @Override
        public boolean isModified(int key) {
            switch (key) {
                case 1: return modified.get(0);
                case 2: return modified.get(1);
                case 3: return modified.get(2);
                case 4: return modified.get(3);
                case 5: return modified.get(4);
                case 6: return modified.get(5);
                case 7: return modified.get(6);
                case 8: return modified.get(7);
                case 9: return modified.get(8);
                case 10: return modified.get(9);
                default: break;
            }
            return false;
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
            return true;
        }

        @Override
        public void validate() {
        }

        @javax.annotation.Nonnull
        @Override
        public net.morimekta.providence.descriptor.PStructDescriptor<DefaultValues,_Field> descriptor() {
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
                            optionals.set(0);
                        } else {
                            throw new net.morimekta.providence.serializer.SerializerException("Wrong type " + net.morimekta.providence.serializer.binary.BinaryType.asString(type) + " for providence.DefaultValues.booleanValue, should be struct(12)");
                        }
                        break;
                    }
                    case 2: {
                        if (type == 3) {
                            mByteValue = reader.expectByte();
                            optionals.set(1);
                        } else {
                            throw new net.morimekta.providence.serializer.SerializerException("Wrong type " + net.morimekta.providence.serializer.binary.BinaryType.asString(type) + " for providence.DefaultValues.byteValue, should be struct(12)");
                        }
                        break;
                    }
                    case 3: {
                        if (type == 6) {
                            mShortValue = reader.expectShort();
                            optionals.set(2);
                        } else {
                            throw new net.morimekta.providence.serializer.SerializerException("Wrong type " + net.morimekta.providence.serializer.binary.BinaryType.asString(type) + " for providence.DefaultValues.shortValue, should be struct(12)");
                        }
                        break;
                    }
                    case 4: {
                        if (type == 8) {
                            mIntegerValue = reader.expectInt();
                            optionals.set(3);
                        } else {
                            throw new net.morimekta.providence.serializer.SerializerException("Wrong type " + net.morimekta.providence.serializer.binary.BinaryType.asString(type) + " for providence.DefaultValues.integerValue, should be struct(12)");
                        }
                        break;
                    }
                    case 5: {
                        if (type == 10) {
                            mLongValue = reader.expectLong();
                            optionals.set(4);
                        } else {
                            throw new net.morimekta.providence.serializer.SerializerException("Wrong type " + net.morimekta.providence.serializer.binary.BinaryType.asString(type) + " for providence.DefaultValues.longValue, should be struct(12)");
                        }
                        break;
                    }
                    case 6: {
                        if (type == 4) {
                            mDoubleValue = reader.expectDouble();
                            optionals.set(5);
                        } else {
                            throw new net.morimekta.providence.serializer.SerializerException("Wrong type " + net.morimekta.providence.serializer.binary.BinaryType.asString(type) + " for providence.DefaultValues.doubleValue, should be struct(12)");
                        }
                        break;
                    }
                    case 7: {
                        if (type == 11) {
                            int len_1 = reader.expectUInt32();
                            mStringValue = new String(reader.expectBytes(len_1), java.nio.charset.StandardCharsets.UTF_8);
                            optionals.set(6);
                        } else {
                            throw new net.morimekta.providence.serializer.SerializerException("Wrong type " + net.morimekta.providence.serializer.binary.BinaryType.asString(type) + " for providence.DefaultValues.stringValue, should be struct(12)");
                        }
                        break;
                    }
                    case 8: {
                        if (type == 11) {
                            int len_2 = reader.expectUInt32();
                            mBinaryValue = reader.expectBinary(len_2);
                            optionals.set(7);
                        } else {
                            throw new net.morimekta.providence.serializer.SerializerException("Wrong type " + net.morimekta.providence.serializer.binary.BinaryType.asString(type) + " for providence.DefaultValues.binaryValue, should be struct(12)");
                        }
                        break;
                    }
                    case 9: {
                        if (type == 8) {
                            mEnumValue = net.morimekta.test.providence.reflect.Value.findById(reader.expectInt());
                            optionals.set(8);
                        } else {
                            throw new net.morimekta.providence.serializer.SerializerException("Wrong type " + net.morimekta.providence.serializer.binary.BinaryType.asString(type) + " for providence.DefaultValues.enumValue, should be struct(12)");
                        }
                        break;
                    }
                    case 10: {
                        if (type == 12) {
                            mCompactValue = net.morimekta.providence.serializer.binary.BinaryFormatUtils.readMessage(reader, net.morimekta.test.providence.reflect.CompactFields.kDescriptor, strict);
                            optionals.set(9);
                        } else {
                            throw new net.morimekta.providence.serializer.SerializerException("Wrong type " + net.morimekta.providence.serializer.binary.BinaryType.asString(type) + " for providence.DefaultValues.compactValue, should be struct(12)");
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
        public DefaultValues build() {
            return new DefaultValues(this);
        }
    }
}
