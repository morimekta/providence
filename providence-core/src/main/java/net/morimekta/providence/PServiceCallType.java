package net.morimekta.providence;

/**
 * The service call type is a base distinction of what the message means, and
 * lets the server or client select the proper message to be serialized or
 * deserialized from the service method descriptor.
 */
@javax.annotation.Generated("providence-maven-plugin")
public enum PServiceCallType
        implements net.morimekta.providence.PEnumValue<PServiceCallType> {
    /**
     * The service method request.
     */
    CALL(1, "call"),
    /**
     * Normal method call reply. This includes declared exceptions on the
     * service method.
     */
    REPLY(2, "reply"),
    /**
     * An application exception, i.e. either a non-declared exception, or a
     * providence service or serialization exception. This is also happens when
     * such exceptions happen on the server side, it will try to send an
     * application exception back to the client.
     */
    EXCEPTION(3, "exception"),
    /**
     * A one-way call is a request that does not expect a response at all. The
     * client will return as soon as the request is sent.
     */
    ONEWAY(4, "oneway"),
    ;

    private final int    mId;
    private final String mName;

    PServiceCallType(int id, String name) {
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
    public static PServiceCallType findById(int id) {
        switch (id) {
            case 1: return PServiceCallType.CALL;
            case 2: return PServiceCallType.REPLY;
            case 3: return PServiceCallType.EXCEPTION;
            case 4: return PServiceCallType.ONEWAY;
            default: return null;
        }
    }

    /**
     * Find a value based in its name
     *
     * @param name Name of value
     * @return Value found or null
     */
    public static PServiceCallType findByName(String name) {
        if (name == null) {
            throw new IllegalArgumentException("Null name given");
        }
        switch (name) {
            case "call": return PServiceCallType.CALL;
            case "reply": return PServiceCallType.REPLY;
            case "exception": return PServiceCallType.EXCEPTION;
            case "oneway": return PServiceCallType.ONEWAY;
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
    public static PServiceCallType valueForId(int id) {
        PServiceCallType value = findById(id);
        if (value == null) {
            throw new IllegalArgumentException("No service.PServiceCallType for id " + id);
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
    public static PServiceCallType valueForName(String name) {
        PServiceCallType value = findByName(name);
        if (value == null) {
            throw new IllegalArgumentException("No service.PServiceCallType for name \"" + name + "\"");
        }
        return value;
    }

    public static class _Builder extends net.morimekta.providence.PEnumBuilder<PServiceCallType> {
        private PServiceCallType mValue;

        @Override
        @javax.annotation.Nonnull
        public _Builder setById(int value) {
            mValue = PServiceCallType.findById(value);
            return this;
        }

        @Override
        @javax.annotation.Nonnull
        public _Builder setByName(String name) {
            mValue = PServiceCallType.findByName(name);
            return this;
        }

        @Override
        public boolean valid() {
            return mValue != null;
        }

        @Override
        public PServiceCallType build() {
            return mValue;
        }
    }

    public static final net.morimekta.providence.descriptor.PEnumDescriptor<PServiceCallType> kDescriptor;

    @Override
    public net.morimekta.providence.descriptor.PEnumDescriptor<PServiceCallType> descriptor() {
        return kDescriptor;
    }

    public static net.morimekta.providence.descriptor.PEnumDescriptorProvider<PServiceCallType> provider() {
        return new net.morimekta.providence.descriptor.PEnumDescriptorProvider<PServiceCallType>(kDescriptor);
    }

    private static class _Descriptor
            extends net.morimekta.providence.descriptor.PEnumDescriptor<PServiceCallType> {
        public _Descriptor() {
            super("service", "PServiceCallType", _Builder::new);
        }

        @Override
        @javax.annotation.Nonnull
        public PServiceCallType[] getValues() {
            return PServiceCallType.values();
        }

        @Override
        @javax.annotation.Nullable
        public PServiceCallType findById(int id) {
            return PServiceCallType.findById(id);
        }

        @Override
        @javax.annotation.Nullable
        public PServiceCallType findByName(String name) {
            return PServiceCallType.findByName(name);
        }
    }

    static {
        kDescriptor = new _Descriptor();
    }
}
