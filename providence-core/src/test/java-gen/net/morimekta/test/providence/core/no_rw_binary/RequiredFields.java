package net.morimekta.test.providence.core.no_rw_binary;

@SuppressWarnings("unused")
@javax.annotation.Generated("providence-maven-plugin")
@javax.annotation.concurrent.Immutable
public class RequiredFields
        implements net.morimekta.providence.PMessage<RequiredFields,RequiredFields._Field>,
                   Comparable<RequiredFields>,
                   java.io.Serializable {
    private final static long serialVersionUID = -7378845554576050657L;

    private final static boolean kDefaultBooleanValue = false;
    private final static byte kDefaultByteValue = (byte)0;
    private final static short kDefaultShortValue = (short)0;
    private final static int kDefaultIntegerValue = 0;
    private final static long kDefaultLongValue = 0L;
    private final static double kDefaultDoubleValue = 0.0d;
    private final static String kDefaultStringValue = "";
    private final static net.morimekta.util.Binary kDefaultBinaryValue = net.morimekta.util.Binary.wrap(new byte[]{});

    private final transient boolean mBooleanValue;
    private final transient byte mByteValue;
    private final transient short mShortValue;
    private final transient int mIntegerValue;
    private final transient long mLongValue;
    private final transient double mDoubleValue;
    private final transient String mStringValue;
    private final transient net.morimekta.util.Binary mBinaryValue;
    private final transient net.morimekta.test.providence.core.no_rw_binary.Value mEnumValue;
    private final transient net.morimekta.test.providence.core.no_rw_binary.CompactFields mCompactValue;

    private volatile transient int tHashCode;

    // Transient object used during java deserialization.
    private transient RequiredFields tSerializeInstance;

    private RequiredFields(_Builder builder) {
        mBooleanValue = builder.mBooleanValue;
        mByteValue = builder.mByteValue;
        mShortValue = builder.mShortValue;
        mIntegerValue = builder.mIntegerValue;
        mLongValue = builder.mLongValue;
        mDoubleValue = builder.mDoubleValue;
        if (builder.isSetStringValue()) {
            mStringValue = builder.mStringValue;
        } else {
            mStringValue = kDefaultStringValue;
        }
        if (builder.isSetBinaryValue()) {
            mBinaryValue = builder.mBinaryValue;
        } else {
            mBinaryValue = kDefaultBinaryValue;
        }
        mEnumValue = builder.mEnumValue;
        mCompactValue = builder.mCompactValue_builder != null ? builder.mCompactValue_builder.build() : builder.mCompactValue;
    }

    public boolean hasBooleanValue() {
        return true;
    }

    /**
     * @return The <code>booleanValue</code> value
     */
    public boolean isBooleanValue() {
        return mBooleanValue;
    }

    public boolean hasByteValue() {
        return true;
    }

    /**
     * @return The <code>byteValue</code> value
     */
    public byte getByteValue() {
        return mByteValue;
    }

    public boolean hasShortValue() {
        return true;
    }

    /**
     * @return The <code>shortValue</code> value
     */
    public short getShortValue() {
        return mShortValue;
    }

    public boolean hasIntegerValue() {
        return true;
    }

    /**
     * @return The <code>integerValue</code> value
     */
    public int getIntegerValue() {
        return mIntegerValue;
    }

    public boolean hasLongValue() {
        return true;
    }

    /**
     * @return The <code>longValue</code> value
     */
    public long getLongValue() {
        return mLongValue;
    }

    public boolean hasDoubleValue() {
        return true;
    }

    /**
     * @return The <code>doubleValue</code> value
     */
    public double getDoubleValue() {
        return mDoubleValue;
    }

    public boolean hasStringValue() {
        return true;
    }

    /**
     * @return The <code>stringValue</code> value
     */
    @javax.annotation.Nonnull
    public String getStringValue() {
        return mStringValue;
    }

    public boolean hasBinaryValue() {
        return true;
    }

    /**
     * @return The <code>binaryValue</code> value
     */
    @javax.annotation.Nonnull
    public net.morimekta.util.Binary getBinaryValue() {
        return mBinaryValue;
    }

    public boolean hasEnumValue() {
        return mEnumValue != null;
    }

    /**
     * @return The <code>enumValue</code> value
     */
    public net.morimekta.test.providence.core.no_rw_binary.Value getEnumValue() {
        return mEnumValue;
    }

    /**
     * @return Optional of the <code>enumValue</code> field value.
     */
    @javax.annotation.Nonnull
    public java.util.Optional<net.morimekta.test.providence.core.no_rw_binary.Value> optionalEnumValue() {
        return java.util.Optional.ofNullable(mEnumValue);
    }

    public boolean hasCompactValue() {
        return mCompactValue != null;
    }

    /**
     * @return The <code>compactValue</code> value
     */
    public net.morimekta.test.providence.core.no_rw_binary.CompactFields getCompactValue() {
        return mCompactValue;
    }

    /**
     * @return Optional of the <code>compactValue</code> field value.
     */
    @javax.annotation.Nonnull
    public java.util.Optional<net.morimekta.test.providence.core.no_rw_binary.CompactFields> optionalCompactValue() {
        return java.util.Optional.ofNullable(mCompactValue);
    }

    @Override
    public boolean has(int key) {
        switch(key) {
            case 1: return true;
            case 2: return true;
            case 3: return true;
            case 4: return true;
            case 5: return true;
            case 6: return true;
            case 7: return true;
            case 8: return true;
            case 9: return mEnumValue != null;
            case 10: return mCompactValue != null;
            default: return false;
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T get(int key) {
        switch(key) {
            case 1: return (T) (Boolean) mBooleanValue;
            case 2: return (T) (Byte) mByteValue;
            case 3: return (T) (Short) mShortValue;
            case 4: return (T) (Integer) mIntegerValue;
            case 5: return (T) (Long) mLongValue;
            case 6: return (T) (Double) mDoubleValue;
            case 7: return (T) mStringValue;
            case 8: return (T) mBinaryValue;
            case 9: return (T) mEnumValue;
            case 10: return (T) mCompactValue;
            default: return null;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        if (o == null || !o.getClass().equals(getClass())) return false;
        RequiredFields other = (RequiredFields) o;
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
                    RequiredFields.class,
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
        return "providence.RequiredFields" + asString();
    }

    @Override
    @javax.annotation.Nonnull
    public String asString() {
        StringBuilder out = new StringBuilder();
        out.append("{");

        out.append("booleanValue:")
           .append(mBooleanValue);
        out.append(',');
        out.append("byteValue:")
           .append((int) mByteValue);
        out.append(',');
        out.append("shortValue:")
           .append((int) mShortValue);
        out.append(',');
        out.append("integerValue:")
           .append(mIntegerValue);
        out.append(',');
        out.append("longValue:")
           .append(mLongValue);
        out.append(',');
        out.append("doubleValue:")
           .append(net.morimekta.util.Strings.asString(mDoubleValue));
        out.append(',');
        out.append("stringValue:")
           .append('\"')
           .append(net.morimekta.util.Strings.escape(mStringValue))
           .append('\"');
        out.append(',');
        out.append("binaryValue:")
           .append("b64(")
           .append(mBinaryValue.toBase64())
           .append(')');
        if (hasEnumValue()) {
            out.append(',');
            out.append("enumValue:")
               .append(mEnumValue.asString());
        }
        if (hasCompactValue()) {
            out.append(',');
            out.append("compactValue:")
               .append(mCompactValue.asString());
        }
        out.append('}');
        return out.toString();
    }

    @Override
    public int compareTo(RequiredFields other) {
        int c;

        c = Boolean.compare(mBooleanValue, other.mBooleanValue);
        if (c != 0) return c;

        c = Byte.compare(mByteValue, other.mByteValue);
        if (c != 0) return c;

        c = Short.compare(mShortValue, other.mShortValue);
        if (c != 0) return c;

        c = Integer.compare(mIntegerValue, other.mIntegerValue);
        if (c != 0) return c;

        c = Long.compare(mLongValue, other.mLongValue);
        if (c != 0) return c;

        c = Double.compare(mDoubleValue, other.mDoubleValue);
        if (c != 0) return c;

        c = mStringValue.compareTo(other.mStringValue);
        if (c != 0) return c;

        c = mBinaryValue.compareTo(other.mBinaryValue);
        if (c != 0) return c;

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

    @javax.annotation.Nonnull
    @Override
    public _Builder mutate() {
        return new _Builder(this);
    }

    public enum _Field implements net.morimekta.providence.descriptor.PField {
        BOOLEAN_VALUE(1, net.morimekta.providence.descriptor.PRequirement.REQUIRED, "booleanValue", net.morimekta.providence.descriptor.PPrimitive.BOOL.provider(), null),
        BYTE_VALUE(2, net.morimekta.providence.descriptor.PRequirement.REQUIRED, "byteValue", net.morimekta.providence.descriptor.PPrimitive.BYTE.provider(), null),
        SHORT_VALUE(3, net.morimekta.providence.descriptor.PRequirement.REQUIRED, "shortValue", net.morimekta.providence.descriptor.PPrimitive.I16.provider(), null),
        INTEGER_VALUE(4, net.morimekta.providence.descriptor.PRequirement.REQUIRED, "integerValue", net.morimekta.providence.descriptor.PPrimitive.I32.provider(), null),
        LONG_VALUE(5, net.morimekta.providence.descriptor.PRequirement.REQUIRED, "longValue", net.morimekta.providence.descriptor.PPrimitive.I64.provider(), null),
        DOUBLE_VALUE(6, net.morimekta.providence.descriptor.PRequirement.REQUIRED, "doubleValue", net.morimekta.providence.descriptor.PPrimitive.DOUBLE.provider(), null),
        STRING_VALUE(7, net.morimekta.providence.descriptor.PRequirement.REQUIRED, "stringValue", net.morimekta.providence.descriptor.PPrimitive.STRING.provider(), null),
        BINARY_VALUE(8, net.morimekta.providence.descriptor.PRequirement.REQUIRED, "binaryValue", net.morimekta.providence.descriptor.PPrimitive.BINARY.provider(), null),
        ENUM_VALUE(9, net.morimekta.providence.descriptor.PRequirement.REQUIRED, "enumValue", net.morimekta.test.providence.core.no_rw_binary.Value.provider(), null),
        COMPACT_VALUE(10, net.morimekta.providence.descriptor.PRequirement.REQUIRED, "compactValue", net.morimekta.test.providence.core.no_rw_binary.CompactFields.provider(), null),
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
                throw new IllegalArgumentException("No such field id " + id + " in providence.RequiredFields");
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
                throw new IllegalArgumentException("No such field \"" + name + "\" in providence.RequiredFields");
            }
            return field;
        }

    }

    @javax.annotation.Nonnull
    public static net.morimekta.providence.descriptor.PStructDescriptorProvider<RequiredFields,_Field> provider() {
        return new _Provider();
    }

    @Override
    @javax.annotation.Nonnull
    public net.morimekta.providence.descriptor.PStructDescriptor<RequiredFields,_Field> descriptor() {
        return kDescriptor;
    }

    public static final net.morimekta.providence.descriptor.PStructDescriptor<RequiredFields,_Field> kDescriptor;

    private static class _Descriptor
            extends net.morimekta.providence.descriptor.PStructDescriptor<RequiredFields,_Field> {
        public _Descriptor() {
            super("providence", "RequiredFields", _Builder::new, false);
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

    private final static class _Provider extends net.morimekta.providence.descriptor.PStructDescriptorProvider<RequiredFields,_Field> {
        @Override
        public net.morimekta.providence.descriptor.PStructDescriptor<RequiredFields,_Field> descriptor() {
            return kDescriptor;
        }
    }

    /**
     * Make a <code>providence.RequiredFields</code> builder.
     * @return The builder instance.
     */
    public static _Builder builder() {
        return new _Builder();
    }

    public static class _Builder
            extends net.morimekta.providence.PMessageBuilder<RequiredFields,_Field> {
        private java.util.BitSet optionals;
        private java.util.BitSet modified;

        private boolean mBooleanValue;
        private byte mByteValue;
        private short mShortValue;
        private int mIntegerValue;
        private long mLongValue;
        private double mDoubleValue;
        private String mStringValue;
        private net.morimekta.util.Binary mBinaryValue;
        private net.morimekta.test.providence.core.no_rw_binary.Value mEnumValue;
        private net.morimekta.test.providence.core.no_rw_binary.CompactFields mCompactValue;
        private net.morimekta.test.providence.core.no_rw_binary.CompactFields._Builder mCompactValue_builder;

        /**
         * Make a providence.RequiredFields builder instance.
         */
        public _Builder() {
            optionals = new java.util.BitSet(10);
            modified = new java.util.BitSet(10);
            mBooleanValue = kDefaultBooleanValue;
            mByteValue = kDefaultByteValue;
            mShortValue = kDefaultShortValue;
            mIntegerValue = kDefaultIntegerValue;
            mLongValue = kDefaultLongValue;
            mDoubleValue = kDefaultDoubleValue;
            mStringValue = kDefaultStringValue;
            mBinaryValue = kDefaultBinaryValue;
        }

        /**
         * Make a mutating builder off a base providence.RequiredFields.
         *
         * @param base The base RequiredFields
         */
        public _Builder(RequiredFields base) {
            this();

            optionals.set(0);
            mBooleanValue = base.mBooleanValue;
            optionals.set(1);
            mByteValue = base.mByteValue;
            optionals.set(2);
            mShortValue = base.mShortValue;
            optionals.set(3);
            mIntegerValue = base.mIntegerValue;
            optionals.set(4);
            mLongValue = base.mLongValue;
            optionals.set(5);
            mDoubleValue = base.mDoubleValue;
            optionals.set(6);
            mStringValue = base.mStringValue;
            optionals.set(7);
            mBinaryValue = base.mBinaryValue;
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
        public _Builder merge(RequiredFields from) {
            optionals.set(0);
            modified.set(0);
            mBooleanValue = from.isBooleanValue();

            optionals.set(1);
            modified.set(1);
            mByteValue = from.getByteValue();

            optionals.set(2);
            modified.set(2);
            mShortValue = from.getShortValue();

            optionals.set(3);
            modified.set(3);
            mIntegerValue = from.getIntegerValue();

            optionals.set(4);
            modified.set(4);
            mLongValue = from.getLongValue();

            optionals.set(5);
            modified.set(5);
            mDoubleValue = from.getDoubleValue();

            optionals.set(6);
            modified.set(6);
            mStringValue = from.getStringValue();

            optionals.set(7);
            modified.set(7);
            mBinaryValue = from.getBinaryValue();

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
         * Set the <code>booleanValue</code> field value.
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
         * Checks for presence of the <code>booleanValue</code> field.
         *
         * @return True if booleanValue has been set.
         */
        public boolean isSetBooleanValue() {
            return optionals.get(0);
        }

        /**
         * Checks if the <code>booleanValue</code> field has been modified since the
         * builder was created.
         *
         * @return True if booleanValue has been modified.
         */
        public boolean isModifiedBooleanValue() {
            return modified.get(0);
        }

        /**
         * Clear the <code>booleanValue</code> field.
         *
         * @return The builder
         */
        @javax.annotation.Nonnull
        public _Builder clearBooleanValue() {
            optionals.clear(0);
            modified.set(0);
            mBooleanValue = kDefaultBooleanValue;
            return this;
        }

        /**
         * @return The <code>booleanValue</code> field value
         */
        public boolean getBooleanValue() {
            return mBooleanValue;
        }

        /**
         * Set the <code>byteValue</code> field value.
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
         * Checks for presence of the <code>byteValue</code> field.
         *
         * @return True if byteValue has been set.
         */
        public boolean isSetByteValue() {
            return optionals.get(1);
        }

        /**
         * Checks if the <code>byteValue</code> field has been modified since the
         * builder was created.
         *
         * @return True if byteValue has been modified.
         */
        public boolean isModifiedByteValue() {
            return modified.get(1);
        }

        /**
         * Clear the <code>byteValue</code> field.
         *
         * @return The builder
         */
        @javax.annotation.Nonnull
        public _Builder clearByteValue() {
            optionals.clear(1);
            modified.set(1);
            mByteValue = kDefaultByteValue;
            return this;
        }

        /**
         * @return The <code>byteValue</code> field value
         */
        public byte getByteValue() {
            return mByteValue;
        }

        /**
         * Set the <code>shortValue</code> field value.
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
         * Checks for presence of the <code>shortValue</code> field.
         *
         * @return True if shortValue has been set.
         */
        public boolean isSetShortValue() {
            return optionals.get(2);
        }

        /**
         * Checks if the <code>shortValue</code> field has been modified since the
         * builder was created.
         *
         * @return True if shortValue has been modified.
         */
        public boolean isModifiedShortValue() {
            return modified.get(2);
        }

        /**
         * Clear the <code>shortValue</code> field.
         *
         * @return The builder
         */
        @javax.annotation.Nonnull
        public _Builder clearShortValue() {
            optionals.clear(2);
            modified.set(2);
            mShortValue = kDefaultShortValue;
            return this;
        }

        /**
         * @return The <code>shortValue</code> field value
         */
        public short getShortValue() {
            return mShortValue;
        }

        /**
         * Set the <code>integerValue</code> field value.
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
         * Checks for presence of the <code>integerValue</code> field.
         *
         * @return True if integerValue has been set.
         */
        public boolean isSetIntegerValue() {
            return optionals.get(3);
        }

        /**
         * Checks if the <code>integerValue</code> field has been modified since the
         * builder was created.
         *
         * @return True if integerValue has been modified.
         */
        public boolean isModifiedIntegerValue() {
            return modified.get(3);
        }

        /**
         * Clear the <code>integerValue</code> field.
         *
         * @return The builder
         */
        @javax.annotation.Nonnull
        public _Builder clearIntegerValue() {
            optionals.clear(3);
            modified.set(3);
            mIntegerValue = kDefaultIntegerValue;
            return this;
        }

        /**
         * @return The <code>integerValue</code> field value
         */
        public int getIntegerValue() {
            return mIntegerValue;
        }

        /**
         * Set the <code>longValue</code> field value.
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
         * Checks for presence of the <code>longValue</code> field.
         *
         * @return True if longValue has been set.
         */
        public boolean isSetLongValue() {
            return optionals.get(4);
        }

        /**
         * Checks if the <code>longValue</code> field has been modified since the
         * builder was created.
         *
         * @return True if longValue has been modified.
         */
        public boolean isModifiedLongValue() {
            return modified.get(4);
        }

        /**
         * Clear the <code>longValue</code> field.
         *
         * @return The builder
         */
        @javax.annotation.Nonnull
        public _Builder clearLongValue() {
            optionals.clear(4);
            modified.set(4);
            mLongValue = kDefaultLongValue;
            return this;
        }

        /**
         * @return The <code>longValue</code> field value
         */
        public long getLongValue() {
            return mLongValue;
        }

        /**
         * Set the <code>doubleValue</code> field value.
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
         * Checks for presence of the <code>doubleValue</code> field.
         *
         * @return True if doubleValue has been set.
         */
        public boolean isSetDoubleValue() {
            return optionals.get(5);
        }

        /**
         * Checks if the <code>doubleValue</code> field has been modified since the
         * builder was created.
         *
         * @return True if doubleValue has been modified.
         */
        public boolean isModifiedDoubleValue() {
            return modified.get(5);
        }

        /**
         * Clear the <code>doubleValue</code> field.
         *
         * @return The builder
         */
        @javax.annotation.Nonnull
        public _Builder clearDoubleValue() {
            optionals.clear(5);
            modified.set(5);
            mDoubleValue = kDefaultDoubleValue;
            return this;
        }

        /**
         * @return The <code>doubleValue</code> field value
         */
        public double getDoubleValue() {
            return mDoubleValue;
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

            optionals.set(6);
            modified.set(6);
            mStringValue = value;
            return this;
        }

        /**
         * Checks for presence of the <code>stringValue</code> field.
         *
         * @return True if stringValue has been set.
         */
        public boolean isSetStringValue() {
            return optionals.get(6);
        }

        /**
         * Checks if the <code>stringValue</code> field has been modified since the
         * builder was created.
         *
         * @return True if stringValue has been modified.
         */
        public boolean isModifiedStringValue() {
            return modified.get(6);
        }

        /**
         * Clear the <code>stringValue</code> field.
         *
         * @return The builder
         */
        @javax.annotation.Nonnull
        public _Builder clearStringValue() {
            optionals.clear(6);
            modified.set(6);
            mStringValue = kDefaultStringValue;
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

            optionals.set(7);
            modified.set(7);
            mBinaryValue = value;
            return this;
        }

        /**
         * Checks for presence of the <code>binaryValue</code> field.
         *
         * @return True if binaryValue has been set.
         */
        public boolean isSetBinaryValue() {
            return optionals.get(7);
        }

        /**
         * Checks if the <code>binaryValue</code> field has been modified since the
         * builder was created.
         *
         * @return True if binaryValue has been modified.
         */
        public boolean isModifiedBinaryValue() {
            return modified.get(7);
        }

        /**
         * Clear the <code>binaryValue</code> field.
         *
         * @return The builder
         */
        @javax.annotation.Nonnull
        public _Builder clearBinaryValue() {
            optionals.clear(7);
            modified.set(7);
            mBinaryValue = kDefaultBinaryValue;
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
        public _Builder setEnumValue(net.morimekta.test.providence.core.no_rw_binary.Value value) {
            if (value == null) {
                return clearEnumValue();
            }

            optionals.set(8);
            modified.set(8);
            mEnumValue = value;
            return this;
        }

        /**
         * Checks for presence of the <code>enumValue</code> field.
         *
         * @return True if enumValue has been set.
         */
        public boolean isSetEnumValue() {
            return optionals.get(8);
        }

        /**
         * Checks if the <code>enumValue</code> field has been modified since the
         * builder was created.
         *
         * @return True if enumValue has been modified.
         */
        public boolean isModifiedEnumValue() {
            return modified.get(8);
        }

        /**
         * Clear the <code>enumValue</code> field.
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
         * @return The <code>enumValue</code> field value
         */
        public net.morimekta.test.providence.core.no_rw_binary.Value getEnumValue() {
            return mEnumValue;
        }

        /**
         * Set the <code>compactValue</code> field value.
         *
         * @param value The new value
         * @return The builder
         */
        @javax.annotation.Nonnull
        public _Builder setCompactValue(net.morimekta.test.providence.core.no_rw_binary.CompactFields value) {
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
         * Set the <code>compactValue</code> field value.
         *
         * @param builder builder for the new value
         * @return The builder
         */
        @javax.annotation.Nonnull
        public _Builder setCompactValue(net.morimekta.test.providence.core.no_rw_binary.CompactFields._Builder builder) {
          return setCompactValue(builder == null ? null : builder.build());
        }

        /**
         * Checks for presence of the <code>compactValue</code> field.
         *
         * @return True if compactValue has been set.
         */
        public boolean isSetCompactValue() {
            return optionals.get(9);
        }

        /**
         * Checks if the <code>compactValue</code> field has been modified since the
         * builder was created.
         *
         * @return True if compactValue has been modified.
         */
        public boolean isModifiedCompactValue() {
            return modified.get(9);
        }

        /**
         * Clear the <code>compactValue</code> field.
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
         * Get the builder for the contained <code>compactValue</code> message field.
         *
         * @return The field message builder
         */
        @javax.annotation.Nonnull
        public net.morimekta.test.providence.core.no_rw_binary.CompactFields._Builder mutableCompactValue() {
            optionals.set(9);
            modified.set(9);

            if (mCompactValue != null) {
                mCompactValue_builder = mCompactValue.mutate();
                mCompactValue = null;
            } else if (mCompactValue_builder == null) {
                mCompactValue_builder = net.morimekta.test.providence.core.no_rw_binary.CompactFields.builder();
            }
            return mCompactValue_builder;
        }

        /**
         * @return The field value
         */
        public net.morimekta.test.providence.core.no_rw_binary.CompactFields getCompactValue() {

            if (mCompactValue_builder != null) {
                return mCompactValue_builder.build();
            }
            return mCompactValue;
        }

        @Override
        public boolean equals(Object o) {
            if (o == this) return true;
            if (o == null || !o.getClass().equals(getClass())) return false;
            RequiredFields._Builder other = (RequiredFields._Builder) o;
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
                   java.util.Objects.equals(getCompactValue(), other.getCompactValue());
        }

        @Override
        public int hashCode() {
            return java.util.Objects.hash(
                    RequiredFields.class, optionals,
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
                case 9: setEnumValue((net.morimekta.test.providence.core.no_rw_binary.Value) value); break;
                case 10: setCompactValue((net.morimekta.test.providence.core.no_rw_binary.CompactFields) value); break;
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
            return optionals.get(0) &&
                   optionals.get(1) &&
                   optionals.get(2) &&
                   optionals.get(3) &&
                   optionals.get(4) &&
                   optionals.get(5) &&
                   optionals.get(6) &&
                   optionals.get(7) &&
                   optionals.get(8) &&
                   optionals.get(9);
        }

        @Override
        public void validate() {
            if (!valid()) {
                java.util.ArrayList<String> missing = new java.util.ArrayList<>();

                if (!optionals.get(0)) {
                    missing.add("booleanValue");
                }

                if (!optionals.get(1)) {
                    missing.add("byteValue");
                }

                if (!optionals.get(2)) {
                    missing.add("shortValue");
                }

                if (!optionals.get(3)) {
                    missing.add("integerValue");
                }

                if (!optionals.get(4)) {
                    missing.add("longValue");
                }

                if (!optionals.get(5)) {
                    missing.add("doubleValue");
                }

                if (!optionals.get(6)) {
                    missing.add("stringValue");
                }

                if (!optionals.get(7)) {
                    missing.add("binaryValue");
                }

                if (!optionals.get(8)) {
                    missing.add("enumValue");
                }

                if (!optionals.get(9)) {
                    missing.add("compactValue");
                }

                throw new java.lang.IllegalStateException(
                        "Missing required fields " +
                        String.join(",", missing) +
                        " in message providence.RequiredFields");
            }
        }

        @javax.annotation.Nonnull
        @Override
        public net.morimekta.providence.descriptor.PStructDescriptor<RequiredFields,_Field> descriptor() {
            return kDescriptor;
        }

        @Override
        public RequiredFields build() {
            return new RequiredFields(this);
        }
    }
}
