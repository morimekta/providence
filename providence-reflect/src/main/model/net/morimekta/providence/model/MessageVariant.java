package net.morimekta.providence.model;

/**
 * Struct variant for StructType. The lower-case of the enum value is the
 * thrift keyword.
 * <p>
 * struct: No &#39;required&#39; fields must be present (set to non-null value).
 * UNION: No required fields. Only one field set to be valid.
 * EXCEPTION: No &#39;cause&#39; field, &#39;message&#39; field *must* be a string (java).
 */
@javax.annotation.Generated("providence java generator")
public enum MessageVariant
        implements net.morimekta.providence.PEnumValue<MessageVariant> {
    STRUCT(1, "STRUCT"),
    UNION(2, "UNION"),
    EXCEPTION(3, "EXCEPTION"),
    ;

    private final int mValue;
    private final String mName;

    MessageVariant(int value, String name) {
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

    public static MessageVariant forValue(int value) {
        switch (value) {
            case 1: return MessageVariant.STRUCT;
            case 2: return MessageVariant.UNION;
            case 3: return MessageVariant.EXCEPTION;
            default: return null;
        }
    }

    public static MessageVariant forName(String name) {
        switch (name) {
            case "STRUCT": return MessageVariant.STRUCT;
            case "UNION": return MessageVariant.UNION;
            case "EXCEPTION": return MessageVariant.EXCEPTION;
            default: return null;
        }
    }

    public static class _Builder extends net.morimekta.providence.PEnumBuilder<MessageVariant> {
        MessageVariant mValue;

        @Override
        public _Builder setByValue(int value) {
            mValue = MessageVariant.forValue(value);
            return this;
        }

        @Override
        public _Builder setByName(String name) {
            mValue = MessageVariant.forName(name);
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

    private static class _Factory
            extends net.morimekta.providence.PEnumBuilderFactory<MessageVariant> {
        @Override
        public MessageVariant._Builder builder() {
            return new MessageVariant._Builder();
        }
    }

    private static class _Descriptor
            extends net.morimekta.providence.descriptor.PEnumDescriptor<MessageVariant> {
        public _Descriptor() {
            super("model", "MessageVariant", new _Factory());
        }

        @Override
        public MessageVariant[] getValues() {
            return MessageVariant.values();
        }

        @Override
        public MessageVariant getValueById(int id) {
            return MessageVariant.forValue(id);
        }

        @Override
        public MessageVariant getValueByName(String name) {
            return MessageVariant.forName(name);
        }
    }

    static {
        kDescriptor = new _Descriptor();
    }
}
