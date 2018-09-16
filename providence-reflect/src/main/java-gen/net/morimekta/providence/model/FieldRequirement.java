package net.morimekta.providence.model;

/**
 * The requirement of the field.
 */
@javax.annotation.Generated("providence-maven-plugin")
public enum FieldRequirement
        implements net.morimekta.providence.PEnumValue<FieldRequirement> {
    DEFAULT(0, "DEFAULT"),
    OPTIONAL(1, "OPTIONAL"),
    REQUIRED(2, "REQUIRED"),
    ;

    private final int    mId;
    private final String mName;

    FieldRequirement(int id, String name) {
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
    public static FieldRequirement findById(int id) {
        switch (id) {
            case 0: return FieldRequirement.DEFAULT;
            case 1: return FieldRequirement.OPTIONAL;
            case 2: return FieldRequirement.REQUIRED;
            default: return null;
        }
    }

    /**
     * Find a value based in its ID
     *
     * @param id Id of value
     * @return Value found or null
     */
    public static FieldRequirement findById(Integer id) {
        return id == null ? null : findById(id.intValue());
    }

    /**
     * Find a value based in its name
     *
     * @param name Name of value
     * @return Value found or null
     */
    public static FieldRequirement findByName(String name) {
        if (name == null) {
            throw new IllegalArgumentException("Null name given");
        }
        switch (name) {
            case "DEFAULT": return FieldRequirement.DEFAULT;
            case "OPTIONAL": return FieldRequirement.OPTIONAL;
            case "REQUIRED": return FieldRequirement.REQUIRED;
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
    public static FieldRequirement valueForId(int id) {
        FieldRequirement value = findById(id);
        if (value == null) {
            throw new IllegalArgumentException("No providence_model.FieldRequirement for id " + id);
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
    public static FieldRequirement valueForName(String name) {
        FieldRequirement value = findByName(name);
        if (value == null) {
            throw new IllegalArgumentException("No providence_model.FieldRequirement for name \"" + name + "\"");
        }
        return value;
    }

    public static class _Builder extends net.morimekta.providence.PEnumBuilder<FieldRequirement> {
        private FieldRequirement mValue;

        @Override
        @javax.annotation.Nonnull
        public _Builder setById(int value) {
            mValue = FieldRequirement.findById(value);
            return this;
        }

        @Override
        @javax.annotation.Nonnull
        public _Builder setByName(String name) {
            mValue = FieldRequirement.findByName(name);
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

    private static class _Descriptor
            extends net.morimekta.providence.descriptor.PEnumDescriptor<FieldRequirement> {
        public _Descriptor() {
            super("providence_model", "FieldRequirement", _Builder::new);
        }

        @Override
        @javax.annotation.Nonnull
        public FieldRequirement[] getValues() {
            return FieldRequirement.values();
        }

        @Override
        @javax.annotation.Nullable
        public FieldRequirement findById(int id) {
            return FieldRequirement.findById(id);
        }

        @Override
        @javax.annotation.Nullable
        public FieldRequirement findByName(String name) {
            return FieldRequirement.findByName(name);
        }
    }

    static {
        kDescriptor = new _Descriptor();
    }
}
