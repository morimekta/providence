package net.morimekta.test.providence.reflect;

@javax.annotation.Generated("providence-maven-plugin")
public enum Value
        implements net.morimekta.providence.PEnumValue<Value> {
    FIRST(1, "FIRST"),
    SECOND(2, "SECOND"),
    THIRD(3, "THIRD"),
    FOURTH(5, "FOURTH"),
    FIFTH(8, "FIFTH"),
    SIXTH(13, "SIXTH"),
    SEVENTH(21, "SEVENTH"),
    EIGHTH(34, "EIGHTH"),
    NINTH(55, "NINTH"),
    TENTH(89, "TENTH"),
    ELEVENTH(144, "ELEVENTH"),
    TWELWETH(233, "TWELWETH"),
    /**
     * &#64;Deprecated
     */
    THIRTEENTH(377, "THIRTEENTH"),
    FOURTEENTH(610, "FOURTEENTH"),
    FIFTEENTH(987, "FIFTEENTH"),
    SIXTEENTH(1597, "SIXTEENTH"),
    SEVENTEENTH(2584, "SEVENTEENTH"),
    EIGHTEENTH(4181, "EIGHTEENTH"),
    NINTEENTH(6765, "NINTEENTH"),
    TWENTIETH(10946, "TWENTIETH"),
    ;

    private final int    mId;
    private final String mName;

    Value(int id, String name) {
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
    public static Value findById(int id) {
        switch (id) {
            case 1: return Value.FIRST;
            case 2: return Value.SECOND;
            case 3: return Value.THIRD;
            case 5: return Value.FOURTH;
            case 8: return Value.FIFTH;
            case 13: return Value.SIXTH;
            case 21: return Value.SEVENTH;
            case 34: return Value.EIGHTH;
            case 55: return Value.NINTH;
            case 89: return Value.TENTH;
            case 144: return Value.ELEVENTH;
            case 233: return Value.TWELWETH;
            case 377: return Value.THIRTEENTH;
            case 610: return Value.FOURTEENTH;
            case 987: return Value.FIFTEENTH;
            case 1597: return Value.SIXTEENTH;
            case 2584: return Value.SEVENTEENTH;
            case 4181: return Value.EIGHTEENTH;
            case 6765: return Value.NINTEENTH;
            case 10946: return Value.TWENTIETH;
            default: return null;
        }
    }

    /**
     * Find a value based in its ID
     *
     * @param id Id of value
     * @return Value found or null
     */
    public static Value findById(Integer id) {
        return id == null ? null : findById(id.intValue());
    }

    /**
     * Find a value based in its name
     *
     * @param name Name of value
     * @return Value found or null
     */
    public static Value findByName(String name) {
        if (name == null) {
            throw new IllegalArgumentException("Null name given");
        }
        switch (name) {
            case "FIRST": return Value.FIRST;
            case "SECOND": return Value.SECOND;
            case "THIRD": return Value.THIRD;
            case "FOURTH": return Value.FOURTH;
            case "FIFTH": return Value.FIFTH;
            case "SIXTH": return Value.SIXTH;
            case "SEVENTH": return Value.SEVENTH;
            case "EIGHTH": return Value.EIGHTH;
            case "NINTH": return Value.NINTH;
            case "TENTH": return Value.TENTH;
            case "ELEVENTH": return Value.ELEVENTH;
            case "TWELWETH": return Value.TWELWETH;
            case "THIRTEENTH": return Value.THIRTEENTH;
            case "FOURTEENTH": return Value.FOURTEENTH;
            case "FIFTEENTH": return Value.FIFTEENTH;
            case "SIXTEENTH": return Value.SIXTEENTH;
            case "SEVENTEENTH": return Value.SEVENTEENTH;
            case "EIGHTEENTH": return Value.EIGHTEENTH;
            case "NINTEENTH": return Value.NINTEENTH;
            case "TWENTIETH": return Value.TWENTIETH;
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
    public static Value valueForId(int id) {
        Value value = findById(id);
        if (value == null) {
            throw new IllegalArgumentException("No providence.Value for id " + id);
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
    public static Value valueForName(String name) {
        Value value = findByName(name);
        if (value == null) {
            throw new IllegalArgumentException("No providence.Value for name \"" + name + "\"");
        }
        return value;
    }

    public static class _Builder extends net.morimekta.providence.PEnumBuilder<Value> {
        private Value mValue;

        @Override
        @javax.annotation.Nonnull
        public _Builder setById(int value) {
            mValue = Value.findById(value);
            return this;
        }

        @Override
        @javax.annotation.Nonnull
        public _Builder setByName(String name) {
            mValue = Value.findByName(name);
            return this;
        }

        @Override
        public boolean valid() {
            return mValue != null;
        }

        @Override
        public Value build() {
            return mValue;
        }
    }

    public static final net.morimekta.providence.descriptor.PEnumDescriptor<Value> kDescriptor;

    @Override
    public net.morimekta.providence.descriptor.PEnumDescriptor<Value> descriptor() {
        return kDescriptor;
    }

    public static net.morimekta.providence.descriptor.PEnumDescriptorProvider<Value> provider() {
        return new net.morimekta.providence.descriptor.PEnumDescriptorProvider<Value>(kDescriptor);
    }

    private static class _Descriptor
            extends net.morimekta.providence.descriptor.PEnumDescriptor<Value> {
        public _Descriptor() {
            super("providence", "Value", _Builder::new);
        }

        @Override
        @javax.annotation.Nonnull
        public Value[] getValues() {
            return Value.values();
        }

        @Override
        @javax.annotation.Nullable
        public Value findById(int id) {
            return Value.findById(id);
        }

        @Override
        @javax.annotation.Nullable
        public Value findByName(String name) {
            return Value.findByName(name);
        }
    }

    static {
        kDescriptor = new _Descriptor();
    }
}
