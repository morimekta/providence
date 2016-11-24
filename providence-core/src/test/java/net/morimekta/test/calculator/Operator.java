package net.morimekta.test.calculator;

public enum Operator
        implements net.morimekta.providence.PEnumValue<Operator> {
    IDENTITY(1, "IDENTITY"),
    ADD(2, "ADD"),
    SUBTRACT(3, "SUBTRACT"),
    MULTIPLY(4, "MULTIPLY"),
    DIVIDE(5, "DIVIDE"),
    ;

    private final int mValue;
    private final String mName;

    Operator(int value, String name) {
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

    public static Operator forValue(int value) {
        switch (value) {
            case 1: return Operator.IDENTITY;
            case 2: return Operator.ADD;
            case 3: return Operator.SUBTRACT;
            case 4: return Operator.MULTIPLY;
            case 5: return Operator.DIVIDE;
            default: return null;
        }
    }

    public static Operator forName(String name) {
        switch (name) {
            case "IDENTITY": return Operator.IDENTITY;
            case "ADD": return Operator.ADD;
            case "SUBTRACT": return Operator.SUBTRACT;
            case "MULTIPLY": return Operator.MULTIPLY;
            case "DIVIDE": return Operator.DIVIDE;
            default: return null;
        }
    }

    public static class _Builder extends net.morimekta.providence.PEnumBuilder<Operator> {
        Operator mValue;

        @Override
        public _Builder setByValue(int value) {
            mValue = Operator.forValue(value);
            return this;
        }

        @Override
        public _Builder setByName(String name) {
            mValue = Operator.forName(name);
            return this;
        }

        @Override
        public boolean isValid() {
            return mValue != null;
        }

        @Override
        public Operator build() {
            return mValue;
        }
    }

    public static final net.morimekta.providence.descriptor.PEnumDescriptor<Operator> kDescriptor;

    @Override
    public net.morimekta.providence.descriptor.PEnumDescriptor<Operator> descriptor() {
        return kDescriptor;
    }

    public static net.morimekta.providence.descriptor.PEnumDescriptorProvider<Operator> provider() {
        return new net.morimekta.providence.descriptor.PEnumDescriptorProvider<Operator>(kDescriptor);
    }

    private static class _Factory
            extends net.morimekta.providence.PEnumBuilderFactory<Operator> {
        @Override
        public Operator._Builder builder() {
            return new Operator._Builder();
        }
    }

    private static class _Descriptor
            extends net.morimekta.providence.descriptor.PEnumDescriptor<Operator> {
        public _Descriptor() {
            super("calculator", "Operator", new _Factory());
        }

        @Override
        public Operator[] getValues() {
            return Operator.values();
        }

        @Override
        public Operator getValueById(int id) {
            return Operator.forValue(id);
        }

        @Override
        public Operator getValueByName(String name) {
            return Operator.forName(name);
        }
    }

    static {
        kDescriptor = new _Descriptor();
    }
}
