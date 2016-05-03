package net.morimekta.test.providence;

@SuppressWarnings("unused")
public class UnionFields
        implements net.morimekta.providence.PUnion<UnionFields>, java.io.Serializable, Comparable<UnionFields> {
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
        mCompactValue = tUnionField == _Field.COMPACT_VALUE ? builder.mCompactValue : null;
    }

    public static UnionFields withBooleanValue(boolean value) {
        return new _Builder().setBooleanValue(value).build();
    }

    public static UnionFields withByteValue(byte value) {
        return new _Builder().setByteValue(value).build();
    }

    public static UnionFields withShortValue(short value) {
        return new _Builder().setShortValue(value).build();
    }

    public static UnionFields withIntegerValue(int value) {
        return new _Builder().setIntegerValue(value).build();
    }

    public static UnionFields withLongValue(long value) {
        return new _Builder().setLongValue(value).build();
    }

    public static UnionFields withDoubleValue(double value) {
        return new _Builder().setDoubleValue(value).build();
    }

    public static UnionFields withStringValue(String value) {
        return new _Builder().setStringValue(value).build();
    }

    public static UnionFields withBinaryValue(net.morimekta.util.Binary value) {
        return new _Builder().setBinaryValue(value).build();
    }

    public static UnionFields withEnumValue(net.morimekta.test.providence.Value value) {
        return new _Builder().setEnumValue(value).build();
    }

    public static UnionFields withCompactValue(net.morimekta.test.providence.CompactFields value) {
        return new _Builder().setCompactValue(value).build();
    }

    public boolean hasBooleanValue() {
        return tUnionField == _Field.BOOLEAN_VALUE;
    }

    public boolean isBooleanValue() {
        return mBooleanValue;
    }

    public boolean hasByteValue() {
        return tUnionField == _Field.BYTE_VALUE;
    }

    public byte getByteValue() {
        return mByteValue;
    }

    public boolean hasShortValue() {
        return tUnionField == _Field.SHORT_VALUE;
    }

    public short getShortValue() {
        return mShortValue;
    }

    public boolean hasIntegerValue() {
        return tUnionField == _Field.INTEGER_VALUE;
    }

    public int getIntegerValue() {
        return mIntegerValue;
    }

    public boolean hasLongValue() {
        return tUnionField == _Field.LONG_VALUE;
    }

    public long getLongValue() {
        return mLongValue;
    }

    public boolean hasDoubleValue() {
        return tUnionField == _Field.DOUBLE_VALUE;
    }

    public double getDoubleValue() {
        return mDoubleValue;
    }

    public boolean hasStringValue() {
        return tUnionField == _Field.STRING_VALUE && mStringValue != null;
    }

    public String getStringValue() {
        return mStringValue;
    }

    public boolean hasBinaryValue() {
        return tUnionField == _Field.BINARY_VALUE && mBinaryValue != null;
    }

    public net.morimekta.util.Binary getBinaryValue() {
        return mBinaryValue;
    }

    public boolean hasEnumValue() {
        return tUnionField == _Field.ENUM_VALUE && mEnumValue != null;
    }

    public net.morimekta.test.providence.Value getEnumValue() {
        return mEnumValue;
    }

    public boolean hasCompactValue() {
        return tUnionField == _Field.COMPACT_VALUE && mCompactValue != null;
    }

    public net.morimekta.test.providence.CompactFields getCompactValue() {
        return mCompactValue;
    }

    @Override
    public _Field unionField() {
        return tUnionField;
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
    public boolean equals(Object o) {
        if (o == null || !(o instanceof UnionFields)) return false;
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
                out.append("booleanValue:");
                out.append(mBooleanValue);
                break;
            }
            case BYTE_VALUE: {
                out.append("byteValue:");
                out.append(mByteValue);
                break;
            }
            case SHORT_VALUE: {
                out.append("shortValue:");
                out.append(mShortValue);
                break;
            }
            case INTEGER_VALUE: {
                out.append("integerValue:");
                out.append(mIntegerValue);
                break;
            }
            case LONG_VALUE: {
                out.append("longValue:");
                out.append(mLongValue);
                break;
            }
            case DOUBLE_VALUE: {
                out.append("doubleValue:");
                out.append(net.morimekta.util.Strings.asString(mDoubleValue));
                break;
            }
            case STRING_VALUE: {
                out.append("stringValue:");
                out.append('\"').append(mStringValue).append('\"');
                break;
            }
            case BINARY_VALUE: {
                out.append("binaryValue:");
                out.append("b64(").append(mBinaryValue.toBase64()).append(')');
                break;
            }
            case ENUM_VALUE: {
                out.append("enumValue:");
                out.append(mEnumValue.getName());
                break;
            }
            case COMPACT_VALUE: {
                out.append("compactValue:");
                out.append(mCompactValue.asString());
                break;
            }
        }
        out.append('}');
        return out.toString();
    }

    @Override
    public int compareTo(UnionFields other) {
        int c = Integer.compare(tUnionField.getKey(), other.tUnionField.getKey());
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
        public net.morimekta.providence.PType getType() { return getDescriptor().getType(); }

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
            StringBuilder builder = new StringBuilder();
            builder.append("UnionFields._Field(")
                   .append(mKey)
                   .append(": ");
            if (mRequired != net.morimekta.providence.descriptor.PRequirement.DEFAULT) {
                builder.append(mRequired.label).append(" ");
            }
            builder.append(getDescriptor().getQualifiedName(null))
                   .append(' ')
                   .append(mName)
                   .append(')');
            return builder.toString();
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
                default: return null;
            }
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
            extends net.morimekta.providence.PMessageBuilderFactory<UnionFields> {
        @Override
        public _Builder builder() {
            return new _Builder();
        }
    }

    @Override
    public _Builder mutate() {
        return new _Builder(this);
    }

    public static _Builder builder() {
        return new _Builder();
    }

    public static class _Builder
            extends net.morimekta.providence.PMessageBuilder<UnionFields> {
        private _Field tUnionField;

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


        public _Builder() {
            mBooleanValue = kDefaultBooleanValue;
            mByteValue = kDefaultByteValue;
            mShortValue = kDefaultShortValue;
            mIntegerValue = kDefaultIntegerValue;
            mLongValue = kDefaultLongValue;
            mDoubleValue = kDefaultDoubleValue;
        }

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

        public _Builder setBooleanValue(boolean value) {
            tUnionField = _Field.BOOLEAN_VALUE;
            mBooleanValue = value;
            return this;
        }
        public boolean isSetBooleanValue() {
            return tUnionField == _Field.BOOLEAN_VALUE;
        }
        public _Builder clearBooleanValue() {
            if (tUnionField == _Field.BOOLEAN_VALUE) tUnionField = null;
            mBooleanValue = kDefaultBooleanValue;
            return this;
        }
        public _Builder setByteValue(byte value) {
            tUnionField = _Field.BYTE_VALUE;
            mByteValue = value;
            return this;
        }
        public boolean isSetByteValue() {
            return tUnionField == _Field.BYTE_VALUE;
        }
        public _Builder clearByteValue() {
            if (tUnionField == _Field.BYTE_VALUE) tUnionField = null;
            mByteValue = kDefaultByteValue;
            return this;
        }
        public _Builder setShortValue(short value) {
            tUnionField = _Field.SHORT_VALUE;
            mShortValue = value;
            return this;
        }
        public boolean isSetShortValue() {
            return tUnionField == _Field.SHORT_VALUE;
        }
        public _Builder clearShortValue() {
            if (tUnionField == _Field.SHORT_VALUE) tUnionField = null;
            mShortValue = kDefaultShortValue;
            return this;
        }
        public _Builder setIntegerValue(int value) {
            tUnionField = _Field.INTEGER_VALUE;
            mIntegerValue = value;
            return this;
        }
        public boolean isSetIntegerValue() {
            return tUnionField == _Field.INTEGER_VALUE;
        }
        public _Builder clearIntegerValue() {
            if (tUnionField == _Field.INTEGER_VALUE) tUnionField = null;
            mIntegerValue = kDefaultIntegerValue;
            return this;
        }
        public _Builder setLongValue(long value) {
            tUnionField = _Field.LONG_VALUE;
            mLongValue = value;
            return this;
        }
        public boolean isSetLongValue() {
            return tUnionField == _Field.LONG_VALUE;
        }
        public _Builder clearLongValue() {
            if (tUnionField == _Field.LONG_VALUE) tUnionField = null;
            mLongValue = kDefaultLongValue;
            return this;
        }
        public _Builder setDoubleValue(double value) {
            tUnionField = _Field.DOUBLE_VALUE;
            mDoubleValue = value;
            return this;
        }
        public boolean isSetDoubleValue() {
            return tUnionField == _Field.DOUBLE_VALUE;
        }
        public _Builder clearDoubleValue() {
            if (tUnionField == _Field.DOUBLE_VALUE) tUnionField = null;
            mDoubleValue = kDefaultDoubleValue;
            return this;
        }
        public _Builder setStringValue(String value) {
            tUnionField = _Field.STRING_VALUE;
            mStringValue = value;
            return this;
        }
        public boolean isSetStringValue() {
            return tUnionField == _Field.STRING_VALUE;
        }
        public _Builder clearStringValue() {
            if (tUnionField == _Field.STRING_VALUE) tUnionField = null;
            mStringValue = null;
            return this;
        }
        public _Builder setBinaryValue(net.morimekta.util.Binary value) {
            tUnionField = _Field.BINARY_VALUE;
            mBinaryValue = value;
            return this;
        }
        public boolean isSetBinaryValue() {
            return tUnionField == _Field.BINARY_VALUE;
        }
        public _Builder clearBinaryValue() {
            if (tUnionField == _Field.BINARY_VALUE) tUnionField = null;
            mBinaryValue = null;
            return this;
        }
        public _Builder setEnumValue(net.morimekta.test.providence.Value value) {
            tUnionField = _Field.ENUM_VALUE;
            mEnumValue = value;
            return this;
        }
        public boolean isSetEnumValue() {
            return tUnionField == _Field.ENUM_VALUE;
        }
        public _Builder clearEnumValue() {
            if (tUnionField == _Field.ENUM_VALUE) tUnionField = null;
            mEnumValue = null;
            return this;
        }
        public _Builder setCompactValue(net.morimekta.test.providence.CompactFields value) {
            tUnionField = _Field.COMPACT_VALUE;
            mCompactValue = value;
            return this;
        }
        public boolean isSetCompactValue() {
            return tUnionField == _Field.COMPACT_VALUE;
        }
        public _Builder clearCompactValue() {
            if (tUnionField == _Field.COMPACT_VALUE) tUnionField = null;
            mCompactValue = null;
            return this;
        }
        @Override
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
            }
            return this;
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
            }
            return this;
        }

        @Override
        public boolean isValid() {
            return tUnionField != null;
        }

        @Override
        public UnionFields build() {
            return new UnionFields(this);
        }
    }
}
