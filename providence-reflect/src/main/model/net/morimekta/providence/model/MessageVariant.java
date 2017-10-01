package net.morimekta.providence.model;

/**
 * Struct variant for StructType. The lower-case of the enum value is the
 * thrift keyword.
 * <p>
 * struct: No &#39;required&#39; fields must be present (set to non-null value).
 * UNION: No required fields. Only one field set to be valid.
 * EXCEPTION: No &#39;cause&#39; field, &#39;message&#39; field *must* be a string (java).
 */
@javax.annotation.Generated("providence-maven-plugin")
public enum MessageVariant
        implements net.morimekta.providence.PEnumValue<MessageVariant> {
    STRUCT(1, "STRUCT"),
    UNION(2, "UNION"),
    EXCEPTION(3, "EXCEPTION"),
    ;

    private final int    mId;
    private final String mName;

    MessageVariant(int id, String name) {
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
    public static MessageVariant findById(int id) {
        switch (id) {
            case 1: return MessageVariant.STRUCT;
            case 2: return MessageVariant.UNION;
            case 3: return MessageVariant.EXCEPTION;
            default: return null;
        }
    }

    /**
     * Find a value based in its name
     *
     * @param name Name of value
     * @return Value found or null
     */
    public static MessageVariant findByName(String name) {
        if (name == null) {
            throw new IllegalArgumentException("Null name given");
        }
        switch (name) {
            case "STRUCT": return MessageVariant.STRUCT;
            case "UNION": return MessageVariant.UNION;
            case "EXCEPTION": return MessageVariant.EXCEPTION;
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
    public static MessageVariant valueForId(int id) {
        MessageVariant value = findById(id);
        if (value == null) {
            throw new IllegalArgumentException("No model.MessageVariant for id " + id);
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
    public static MessageVariant valueForName(String name) {
        MessageVariant value = findByName(name);
        if (value == null) {
            throw new IllegalArgumentException("No model.MessageVariant for name \"" + name + "\"");
        }
        return value;
    }

    public static class _Builder extends net.morimekta.providence.PEnumBuilder<MessageVariant> {
        MessageVariant mValue;

        @Override
        @javax.annotation.Nonnull
        public _Builder setById(int value) {
            mValue = MessageVariant.findById(value);
            return this;
        }

        @Override
        @javax.annotation.Nonnull
        public _Builder setByName(String name) {
            mValue = MessageVariant.findByName(name);
            return this;
        }

        @Override
        public boolean valid() {
            return mValue != null;
        }

        @Override
        public MessageVariant build() {
            return mValue;
        }
    }

    public static final net.morimekta.providence.descriptor.PEnumDescriptor<MessageVariant> kDescriptor;

    @Override
    public net.morimekta.providence.descriptor.PEnumDescriptor<MessageVariant> descriptor() {
        return kDescriptor;
    }

    public static net.morimekta.providence.descriptor.PEnumDescriptorProvider<MessageVariant> provider() {
        return new net.morimekta.providence.descriptor.PEnumDescriptorProvider<MessageVariant>(kDescriptor);
    }

    private static class _Descriptor
            extends net.morimekta.providence.descriptor.PEnumDescriptor<MessageVariant> {
        public _Descriptor() {
            super("model", "MessageVariant", _Builder::new);
        }

        @Override
        @javax.annotation.Nonnull
        public MessageVariant[] getValues() {
            return MessageVariant.values();
        }

        @Override
        @javax.annotation.Nullable
        public MessageVariant findById(int id) {
            return MessageVariant.findById(id);
        }

        @Override
        @javax.annotation.Nullable
        public MessageVariant findByName(String name) {
            return MessageVariant.findByName(name);
        }
    }

    static {
        kDescriptor = new _Descriptor();
    }
}
