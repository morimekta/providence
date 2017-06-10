package net.morimekta.providence.model;

/**
 * The requirement of the field.
 */
@javax.annotation.Generated("providence java generator")
public enum FieldRequirement
        implements net.morimekta.providence.PEnumValue<FieldRequirement> {
    DEFAULT(0, "DEFAULT"),
    OPTIONAL(1, "OPTIONAL"),
    REQUIRED(2, "REQUIRED"),
    ;

    private final int mValue;
    private final String mName;

    FieldRequirement(int value, String name) {
        mValue = value;
        mName = name;
    }

    @Override
    public int getValue() {
        return mValue;
    }

    @Override
    public String getName() {
        return mName;
    }

    @Override
    public int asInteger() {
        return mValue;
    }

    @Override
    public String asString() {
        return mName;
    }

    public static FieldRequirement forValue(int value) {
        switch (value) {
            case 0: return FieldRequirement.DEFAULT;
            case 1: return FieldRequirement.OPTIONAL;
            case 2: return FieldRequirement.REQUIRED;
            default: return null;
        }
    }

    public static FieldRequirement forName(String name) {
        switch (name) {
            case "DEFAULT": return FieldRequirement.DEFAULT;
            case "OPTIONAL": return FieldRequirement.OPTIONAL;
            case "REQUIRED": return FieldRequirement.REQUIRED;
            default: return null;
        }
    }

    public static class _Builder extends net.morimekta.providence.PEnumBuilder<FieldRequirement> {
        FieldRequirement mValue;

        @Override
        public _Builder setByValue(int value) {
            mValue = FieldRequirement.forValue(value);
            return this;
        }

        @Override
        public _Builder setByName(String name) {
            mValue = FieldRequirement.forName(name);
            return this;
        }

        @Override
        public boolean valid() {
            return mValue != null;
        }

        @Override
        public FieldRequirement build() {
            return mValue;
        }
    }

    public static final net.morimekta.providence.descriptor.PEnumDescriptor<FieldRequirement> kDescriptor;

    @Override
    public net.morimekta.providence.descriptor.PEnumDescriptor<FieldRequirement> descriptor() {
        return kDescriptor;
    }

    public static net.morimekta.providence.descriptor.PEnumDescriptorProvider<FieldRequirement> provider() {
        return new net.morimekta.providence.descriptor.PEnumDescriptorProvider<FieldRequirement>(kDescriptor);
    }

    private static class _Factory
            extends net.morimekta.providence.PEnumBuilderFactory<FieldRequirement> {
        @Override
        public FieldRequirement._Builder builder() {
            return new FieldRequirement._Builder();
        }
    }

    private static class _Descriptor
            extends net.morimekta.providence.descriptor.PEnumDescriptor<FieldRequirement> {
        public _Descriptor() {
            super("model", "FieldRequirement", new _Factory());
        }

        @Override
        public FieldRequirement[] getValues() {
            return FieldRequirement.values();
        }

        @Override
        public FieldRequirement getValueById(int id) {
            return FieldRequirement.forValue(id);
        }

        @Override
        public FieldRequirement getValueByName(String name) {
            return FieldRequirement.forName(name);
        }
    }

    static {
        kDescriptor = new _Descriptor();
    }
}
