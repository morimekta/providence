package net.morimekta.providence.model;

/**
 * Struct variant for StructType. The lower-case of the enum value is the
 * thrift keyword.
 * 
 * struct: No 'required' fields must be present (set to non-null value).
 * UNION: No required fields. Only one field set to be valid.
 * EXCEPTION: No 'cause' field, 'message' field *must* be a string (java).
 */
public enum StructVariant implements net.morimekta.providence.PEnumValue<StructVariant> {
    STRUCT(1, "STRUCT"),
    UNION(2, "UNION"),
    EXCEPTION(3, "EXCEPTION"),
    ;

    private final int mValue;
    private final String mName;

    StructVariant(int value, String name) {
        mValue = value;
        mName = name;
    }

    @Override
    public String getComment() {
        return null;
    }

    @Override
    public int getValue() {
        return mValue;
    }

    @Override
    public String getName() {
        return mName;
    }

    public static StructVariant forValue(int value) {
        switch (value) {
            case 1: return StructVariant.STRUCT;
            case 2: return StructVariant.UNION;
            case 3: return StructVariant.EXCEPTION;
            default: return null;
        }
    }

    public static StructVariant forName(String name) {
        switch (name) {
            case "STRUCT": return StructVariant.STRUCT;
            case "UNION": return StructVariant.UNION;
            case "EXCEPTION": return StructVariant.EXCEPTION;
            default: return null;
        }
    }

    public static class _Builder extends net.morimekta.providence.PEnumBuilder<StructVariant> {
        StructVariant mValue;

        @Override
        public _Builder setByValue(int value) {
            mValue = StructVariant.forValue(value);
            return this;
        }

        @Override
        public _Builder setByName(String name) {
            mValue = StructVariant.forName(name);
            return this;
        }

        @Override
        public boolean isValid() {
            return mValue != null;
        }

        @Override
        public StructVariant build() {
            return mValue;
        }
    }

    public static final net.morimekta.providence.descriptor.PEnumDescriptor<StructVariant> kDescriptor;

    @Override
    public net.morimekta.providence.descriptor.PEnumDescriptor<StructVariant> descriptor() {
        return kDescriptor;
    }

    public static net.morimekta.providence.descriptor.PEnumDescriptorProvider<StructVariant> provider() {
        return new net.morimekta.providence.descriptor.PEnumDescriptorProvider<StructVariant>(kDescriptor);
    }

    private static class _Factory
            extends net.morimekta.providence.PEnumBuilderFactory<StructVariant> {
        @Override
        public StructVariant._Builder builder() {
            return new StructVariant._Builder();
        }
    }

    private static class _Descriptor
            extends net.morimekta.providence.descriptor.PEnumDescriptor<StructVariant> {
        public _Descriptor() {
            super(null, "model", "StructVariant", new _Factory());
        }

        @Override
        public StructVariant[] getValues() {
            return StructVariant.values();
        }

        @Override
        public StructVariant getValueById(int id) {
            return StructVariant.forValue(id);
        }

        @Override
        public StructVariant getValueByName(String name) {
            return StructVariant.forName(name);
        }
    }

    static {
        kDescriptor = new _Descriptor();
    }
}
