package net.morimekta.test.providence.reflect.calculator;

@javax.annotation.Generated("providence-maven-plugin")
public enum Operator
        implements net.morimekta.providence.PEnumValue<Operator> {
    IDENTITY(1, "IDENTITY"),
    ADD(2, "ADD"),
    SUBTRACT(3, "SUBTRACT"),
    MULTIPLY(4, "MULTIPLY"),
    DIVIDE(5, "DIVIDE"),
    ;

    private final int    mId;
    private final String mName;

    Operator(int id, String name) {
        mId = id;
        mName = name;
    }

    @Override
    public int asInteger() {
        return mId;
    }

    @Override
    @javax.annotation.Nonnull
    public String asString() {
        return mName;
    }

    /**
     * Find a value based in its ID
     *
     * @param id Id of value
     * @return Value found or null
     */
    public static Operator findById(int id) {
        switch (id) {
            case 1: return Operator.IDENTITY;
            case 2: return Operator.ADD;
            case 3: return Operator.SUBTRACT;
            case 4: return Operator.MULTIPLY;
            case 5: return Operator.DIVIDE;
            default: return null;
        }
    }

    /**
     * Find a value based in its name
     *
     * @param name Name of value
     * @return Value found or null
     */
    public static Operator findByName(String name) {
        if (name == null) {
            throw new IllegalArgumentException("Null name given");
        }
        switch (name) {
            case "IDENTITY": return Operator.IDENTITY;
            case "ADD": return Operator.ADD;
            case "SUBTRACT": return Operator.SUBTRACT;
            case "MULTIPLY": return Operator.MULTIPLY;
            case "DIVIDE": return Operator.DIVIDE;
            default: return null;
        }
    }

    /**
     * Get a value based in its ID
     *
     * @param id Id of value
     * @return Value found
     * @throws IllegalArgumentException If no value for id is found
     */
    @javax.annotation.Nonnull
    public static Operator valueForId(int id) {
        Operator value = findById(id);
        if (value == null) {
            throw new IllegalArgumentException("No calculator.Operator for id " + id);
        }
        return value;
    }

    /**
     * Get a value based in its name
     *
     * @param name Name of value
     * @return Value found
     * @throws IllegalArgumentException If no value for name is found, or null name
     */
    @javax.annotation.Nonnull
    public static Operator valueForName(String name) {
        Operator value = findByName(name);
        if (value == null) {
            throw new IllegalArgumentException("No calculator.Operator for name \"" + name + "\"");
        }
        return value;
    }

    public static class _Builder extends net.morimekta.providence.PEnumBuilder<Operator> {
        Operator mValue;

        @Override
        @javax.annotation.Nonnull
        public _Builder setById(int value) {
            mValue = Operator.findById(value);
            return this;
        }

        @Override
        @javax.annotation.Nonnull
        public _Builder setByName(String name) {
            mValue = Operator.findByName(name);
            return this;
        }

        @Override
        public boolean valid() {
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

    private static class _Descriptor
            extends net.morimekta.providence.descriptor.PEnumDescriptor<Operator> {
        public _Descriptor() {
            super("calculator", "Operator", _Builder::new);
        }

        @Override
        @javax.annotation.Nonnull
        public Operator[] getValues() {
            return Operator.values();
        }

        @Override
        @javax.annotation.Nullable
        public Operator findById(int id) {
            return Operator.findById(id);
        }

        @Override
        @javax.annotation.Nullable
        public Operator findByName(String name) {
            return Operator.findByName(name);
        }
    }

    static {
        kDescriptor = new _Descriptor();
    }
}
