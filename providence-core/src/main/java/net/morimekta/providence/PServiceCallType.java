package net.morimekta.providence;

/**
 * The service call type is a base distinction of what the message means, and
 * lets the server or client select the proper message to be serialized or
 * deserialized from the service method descriptor.
 */
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
     * application exception back to the cliend.
     */
    EXCEPTION(3, "exception"),
    /**
     * A one-way call is a request that does not expect a response at all. The
     * client will return as soon as the request is sent.
     */
    ONEWAY(4, "oneway"),
    ;

    private final int mValue;
    private final String mName;

    PServiceCallType(int value, String name) {
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

    public static PServiceCallType forValue(int value) {
        switch (value) {
            case 1: return PServiceCallType.CALL;
            case 2: return PServiceCallType.REPLY;
            case 3: return PServiceCallType.EXCEPTION;
            case 4: return PServiceCallType.ONEWAY;
            default: return null;
        }
    }

    public static PServiceCallType forName(String name) {
        switch (name) {
            case "call": return PServiceCallType.CALL;
            case "reply": return PServiceCallType.REPLY;
            case "exception": return PServiceCallType.EXCEPTION;
            case "oneway": return PServiceCallType.ONEWAY;
            default: return null;
        }
    }

    public static class _Builder extends net.morimekta.providence.PEnumBuilder<PServiceCallType> {
        PServiceCallType mValue;

        @Override
        public _Builder setByValue(int value) {
            mValue = PServiceCallType.forValue(value);
            return this;
        }

        @Override
        public _Builder setByName(String name) {
            mValue = PServiceCallType.forName(name);
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

    private static class _Factory
            extends net.morimekta.providence.PEnumBuilderFactory<PServiceCallType> {
        @Override
        public PServiceCallType._Builder builder() {
            return new PServiceCallType._Builder();
        }
    }

    private static class _Descriptor
            extends net.morimekta.providence.descriptor.PEnumDescriptor<PServiceCallType> {
        public _Descriptor() {
            super("service", "PServiceCallType", new _Factory());
        }

        @Override
        public PServiceCallType[] getValues() {
            return PServiceCallType.values();
        }

        @Override
        public PServiceCallType getValueById(int id) {
            return PServiceCallType.forValue(id);
        }

        @Override
        public PServiceCallType getValueByName(String name) {
            return PServiceCallType.forName(name);
        }
    }

    static {
        kDescriptor = new _Descriptor();
    }
}
