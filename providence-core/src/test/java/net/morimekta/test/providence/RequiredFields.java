package net.morimekta.test.providence;

@SuppressWarnings("unused")
public class RequiredFields
        implements net.morimekta.providence.PMessage<RequiredFields>, java.io.Serializable, Comparable<RequiredFields> {
    private final static long serialVersionUID = -7378845554576050657L;

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
    
    private volatile int tHashCode;

    private RequiredFields(_Builder builder) {
        mBooleanValue = builder.mBooleanValue;
        mByteValue = builder.mByteValue;
        mShortValue = builder.mShortValue;
        mIntegerValue = builder.mIntegerValue;
        mLongValue = builder.mLongValue;
        mDoubleValue = builder.mDoubleValue;
        mStringValue = builder.mStringValue;
        mBinaryValue = builder.mBinaryValue;
        mEnumValue = builder.mEnumValue;
        mCompactValue = builder.mCompactValue;
    }

    public RequiredFields(boolean pBooleanValue,
                          byte pByteValue,
                          short pShortValue,
                          int pIntegerValue,
                          long pLongValue,
                          double pDoubleValue,
                          String pStringValue,
                          net.morimekta.util.Binary pBinaryValue,
                          net.morimekta.test.providence.Value pEnumValue,
                          net.morimekta.test.providence.CompactFields pCompactValue) {
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

    public boolean hasBooleanValue() {
        return true;
    }

    public boolean isBooleanValue() {
        return mBooleanValue;
    }

    public boolean hasByteValue() {
        return true;
    }

    public byte getByteValue() {
        return mByteValue;
    }

    public boolean hasShortValue() {
        return true;
    }

    public short getShortValue() {
        return mShortValue;
    }

    public boolean hasIntegerValue() {
        return true;
    }

    public int getIntegerValue() {
        return mIntegerValue;
    }

    public boolean hasLongValue() {
        return true;
    }

    public long getLongValue() {
        return mLongValue;
    }

    public boolean hasDoubleValue() {
        return true;
    }

    public double getDoubleValue() {
        return mDoubleValue;
    }

    public boolean hasStringValue() {
        return mStringValue != null;
    }

    public String getStringValue() {
        return mStringValue;
    }

    public boolean hasBinaryValue() {
        return mBinaryValue != null;
    }

    public net.morimekta.util.Binary getBinaryValue() {
        return mBinaryValue;
    }

    public boolean hasEnumValue() {
        return mEnumValue != null;
    }

    public net.morimekta.test.providence.Value getEnumValue() {
        return mEnumValue;
    }

    public boolean hasCompactValue() {
        return mCompactValue != null;
    }

    public net.morimekta.test.providence.CompactFields getCompactValue() {
        return mCompactValue;
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
            case 1: return 1;
            case 2: return 1;
            case 3: return 1;
            case 4: return 1;
            case 5: return 1;
            case 6: return 1;
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
        if (o == null || !(o instanceof RequiredFields)) return false;
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
    public String asString() {
        StringBuilder out = new StringBuilder();
        out.append("{");

        boolean first = true;
        if (hasBooleanValue()) {
            first = false;
            out.append("booleanValue:");
            out.append(mBooleanValue ? "true" : "false");
        }
        if (hasByteValue()) {
            if (!first) out.append(',');
            first = false;
            out.append("byteValue:");
            out.append(Byte.toString(mByteValue));
        }
        if (hasShortValue()) {
            if (!first) out.append(',');
            first = false;
            out.append("shortValue:");
            out.append(Short.toString(mShortValue));
        }
        if (hasIntegerValue()) {
            if (!first) out.append(',');
            first = false;
            out.append("integerValue:");
            out.append(Integer.toString(mIntegerValue));
        }
        if (hasLongValue()) {
            if (!first) out.append(',');
            first = false;
            out.append("longValue:");
            out.append(Long.toString(mLongValue));
        }
        if (hasDoubleValue()) {
            if (!first) out.append(',');
            first = false;
            out.append("doubleValue:");
            out.append(net.morimekta.util.Strings.asString(mDoubleValue));
        }
        if (hasStringValue()) {
            if (!first) out.append(',');
            first = false;
            out.append("stringValue:");
            out.append('\"').append(mStringValue).append('\"');
        }
        if (hasBinaryValue()) {
            if (!first) out.append(',');
            first = false;
            out.append("binaryValue:");
            out.append("hex(").append(mBinaryValue.toHexString()).append(')');
        }
        if (hasEnumValue()) {
            if (!first) out.append(',');
            first = false;
            out.append("enumValue:");
            out.append(mEnumValue.getName());
        }
        if (hasCompactValue()) {
            if (!first) out.append(',');
            first = false;
            out.append("compactValue:");
            out.append(mCompactValue.asString());
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
            c = Integer.compare(mEnumValue.getValue(), mEnumValue.getValue());
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

    public enum _Field implements net.morimekta.providence.descriptor.PField {
        BOOLEAN_VALUE(1, net.morimekta.providence.descriptor.PRequirement.REQUIRED, "booleanValue", net.morimekta.providence.descriptor.PPrimitive.BOOL.provider(), null),
        BYTE_VALUE(2, net.morimekta.providence.descriptor.PRequirement.REQUIRED, "byteValue", net.morimekta.providence.descriptor.PPrimitive.BYTE.provider(), null),
        SHORT_VALUE(3, net.morimekta.providence.descriptor.PRequirement.REQUIRED, "shortValue", net.morimekta.providence.descriptor.PPrimitive.I16.provider(), null),
        INTEGER_VALUE(4, net.morimekta.providence.descriptor.PRequirement.REQUIRED, "integerValue", net.morimekta.providence.descriptor.PPrimitive.I32.provider(), null),
        LONG_VALUE(5, net.morimekta.providence.descriptor.PRequirement.REQUIRED, "longValue", net.morimekta.providence.descriptor.PPrimitive.I64.provider(), null),
        DOUBLE_VALUE(6, net.morimekta.providence.descriptor.PRequirement.REQUIRED, "doubleValue", net.morimekta.providence.descriptor.PPrimitive.DOUBLE.provider(), null),
        STRING_VALUE(7, net.morimekta.providence.descriptor.PRequirement.REQUIRED, "stringValue", net.morimekta.providence.descriptor.PPrimitive.STRING.provider(), null),
        BINARY_VALUE(8, net.morimekta.providence.descriptor.PRequirement.REQUIRED, "binaryValue", net.morimekta.providence.descriptor.PPrimitive.BINARY.provider(), null),
        ENUM_VALUE(9, net.morimekta.providence.descriptor.PRequirement.REQUIRED, "enumValue", net.morimekta.test.providence.Value.provider(), null),
        COMPACT_VALUE(10, net.morimekta.providence.descriptor.PRequirement.REQUIRED, "compactValue", net.morimekta.test.providence.CompactFields.provider(), null),
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
            builder.append("RequiredFields._Field(")
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

    public static net.morimekta.providence.descriptor.PStructDescriptorProvider<RequiredFields,_Field> provider() {
        return new _Provider();
    }

    @Override
    public net.morimekta.providence.descriptor.PStructDescriptor<RequiredFields,_Field> descriptor() {
        return kDescriptor;
    }

    public static final net.morimekta.providence.descriptor.PStructDescriptor<RequiredFields,_Field> kDescriptor;

    private static class _Descriptor
            extends net.morimekta.providence.descriptor.PStructDescriptor<RequiredFields,_Field> {
        public _Descriptor() {
            super("providence", "RequiredFields", new _Factory(), false, false);
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

    private final static class _Provider extends net.morimekta.providence.descriptor.PStructDescriptorProvider<RequiredFields,_Field> {
        @Override
        public net.morimekta.providence.descriptor.PStructDescriptor<RequiredFields,_Field> descriptor() {
            return kDescriptor;
        }
    }

    private final static class _Factory
            extends net.morimekta.providence.PMessageBuilderFactory<RequiredFields> {
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
            extends net.morimekta.providence.PMessageBuilder<RequiredFields> {
        private java.util.BitSet optionals;

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
            optionals = new java.util.BitSet(10);
            mBooleanValue = kDefaultBooleanValue;
            mByteValue = kDefaultByteValue;
            mShortValue = kDefaultShortValue;
            mIntegerValue = kDefaultIntegerValue;
            mLongValue = kDefaultLongValue;
            mDoubleValue = kDefaultDoubleValue;
        }

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

        public _Builder setBooleanValue(boolean value) {
            optionals.set(0);
            mBooleanValue = value;
            return this;
        }
        public boolean isSetBooleanValue() {
            return optionals.get(0);
        }
        public _Builder clearBooleanValue() {
            optionals.set(0, false);
            mBooleanValue = kDefaultBooleanValue;
            return this;
        }
        public _Builder setByteValue(byte value) {
            optionals.set(1);
            mByteValue = value;
            return this;
        }
        public boolean isSetByteValue() {
            return optionals.get(1);
        }
        public _Builder clearByteValue() {
            optionals.set(1, false);
            mByteValue = kDefaultByteValue;
            return this;
        }
        public _Builder setShortValue(short value) {
            optionals.set(2);
            mShortValue = value;
            return this;
        }
        public boolean isSetShortValue() {
            return optionals.get(2);
        }
        public _Builder clearShortValue() {
            optionals.set(2, false);
            mShortValue = kDefaultShortValue;
            return this;
        }
        public _Builder setIntegerValue(int value) {
            optionals.set(3);
            mIntegerValue = value;
            return this;
        }
        public boolean isSetIntegerValue() {
            return optionals.get(3);
        }
        public _Builder clearIntegerValue() {
            optionals.set(3, false);
            mIntegerValue = kDefaultIntegerValue;
            return this;
        }
        public _Builder setLongValue(long value) {
            optionals.set(4);
            mLongValue = value;
            return this;
        }
        public boolean isSetLongValue() {
            return optionals.get(4);
        }
        public _Builder clearLongValue() {
            optionals.set(4, false);
            mLongValue = kDefaultLongValue;
            return this;
        }
        public _Builder setDoubleValue(double value) {
            optionals.set(5);
            mDoubleValue = value;
            return this;
        }
        public boolean isSetDoubleValue() {
            return optionals.get(5);
        }
        public _Builder clearDoubleValue() {
            optionals.set(5, false);
            mDoubleValue = kDefaultDoubleValue;
            return this;
        }
        public _Builder setStringValue(String value) {
            optionals.set(6);
            mStringValue = value;
            return this;
        }
        public boolean isSetStringValue() {
            return optionals.get(6);
        }
        public _Builder clearStringValue() {
            optionals.set(6, false);
            mStringValue = null;
            return this;
        }
        public _Builder setBinaryValue(net.morimekta.util.Binary value) {
            optionals.set(7);
            mBinaryValue = value;
            return this;
        }
        public boolean isSetBinaryValue() {
            return optionals.get(7);
        }
        public _Builder clearBinaryValue() {
            optionals.set(7, false);
            mBinaryValue = null;
            return this;
        }
        public _Builder setEnumValue(net.morimekta.test.providence.Value value) {
            optionals.set(8);
            mEnumValue = value;
            return this;
        }
        public boolean isSetEnumValue() {
            return optionals.get(8);
        }
        public _Builder clearEnumValue() {
            optionals.set(8, false);
            mEnumValue = null;
            return this;
        }
        public _Builder setCompactValue(net.morimekta.test.providence.CompactFields value) {
            optionals.set(9);
            mCompactValue = value;
            return this;
        }
        public boolean isSetCompactValue() {
            return optionals.get(9);
        }
        public _Builder clearCompactValue() {
            optionals.set(9, false);
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
        public RequiredFields build() {
            return new RequiredFields(this);
        }
    }
}
