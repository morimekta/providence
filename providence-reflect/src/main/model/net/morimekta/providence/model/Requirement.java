package net.morimekta.providence.model;

/**
 * The requirement of the field.
 */
public enum Requirement
        implements net.morimekta.providence.PEnumValue<Requirement> {
    DEFAULT(0, "DEFAULT"),
    OPTIONAL(1, "OPTIONAL"),
    REQUIRED(2, "REQUIRED"),
    ;

    private final int mValue;
    private final String mName;

    Requirement(int value, String name) {
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

    public static Requirement forValue(int value) {
        switch (value) {
            case 0: return Requirement.DEFAULT;
            case 1: return Requirement.OPTIONAL;
            case 2: return Requirement.REQUIRED;
            default: return null;
        }
    }

    public static Requirement forName(String name) {
        switch (name) {
            case "DEFAULT": return Requirement.DEFAULT;
            case "OPTIONAL": return Requirement.OPTIONAL;
            case "REQUIRED": return Requirement.REQUIRED;
            default: return null;
        }
    }

    public static class _Builder extends net.morimekta.providence.PEnumBuilder<Requirement> {
        Requirement mValue;

        @Override
        public _Builder setByValue(int value) {
            mValue = Requirement.forValue(value);
            return this;
        }

        @Override
        public _Builder setByName(String name) {
            mValue = Requirement.forName(name);
            return this;
        }

        @Override
        public boolean isValid() {
            return mValue != null;
        }

        @Override
        public Requirement build() {
            return mValue;
        }
    }

    public static final net.morimekta.providence.descriptor.PEnumDescriptor<Requirement> kDescriptor;

    @Override
    public net.morimekta.providence.descriptor.PEnumDescriptor<Requirement> descriptor() {
        return kDescriptor;
    }

    public static net.morimekta.providence.descriptor.PEnumDescriptorProvider<Requirement> provider() {
        return new net.morimekta.providence.descriptor.PEnumDescriptorProvider<Requirement>(kDescriptor);
    }

    private static class _Factory
            extends net.morimekta.providence.PEnumBuilderFactory<Requirement> {
        @Override
        public Requirement._Builder builder() {
            return new Requirement._Builder();
        }
    }

    private static class _Descriptor
            extends net.morimekta.providence.descriptor.PEnumDescriptor<Requirement> {
        public _Descriptor() {
            super("model", "Requirement", new _Factory());
        }

        @Override
        public Requirement[] getValues() {
            return Requirement.values();
        }

        @Override
        public Requirement getValueById(int id) {
            return Requirement.forValue(id);
        }

        @Override
        public Requirement getValueByName(String name) {
            return Requirement.forName(name);
        }
    }

    static {
        kDescriptor = new _Descriptor();
    }
}