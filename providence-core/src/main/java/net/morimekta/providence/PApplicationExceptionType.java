package net.morimekta.providence;

/**
 * General type of exception on the application level.
 */
public enum PApplicationExceptionType
        implements net.morimekta.providence.PEnumValue<PApplicationExceptionType> {
    /**
     * Unknown or unidentified exception, should usually not be uased.
     */
    UNKNOWN(0, "UNKNOWN"),
    /**
     * There is no such method defined on the service.
     */
    UNKNOWN_METHOD(1, "UNKNOWN_METHOD"),
    /**
     * The service call type does not make sense, or is plain wrong, e.g.
     * sending &#39;reply&#39; or &#39;exception&#39; as the request.
     */
    INVALID_MESSAGE_TYPE(2, "INVALID_MESSAGE_TYPE"),
    /**
     * The response came back with a non-matching method name.
     */
    WRONG_METHOD_NAME(3, "WRONG_METHOD_NAME"),
    /**
     * The response cambe back with a non-matching sequence ID.
     */
    BAD_SEQUENCE_ID(4, "BAD_SEQUENCE_ID"),
    /**
     * The response did not have a defined non-null result.
     * <p>
     * NOTE: This is the default behavior from thrift, and we may need to keep
     * it this way as long as thrift compatibility is expected.
     */
    MISSING_RESULT(5, "MISSING_RESULT"),
    /**
     * The service handler or client handler experienced internal problem.
     */
    INTERNAL_ERROR(6, "INTERNAL_ERROR"),
    /**
     * Serialization or deserialization failed or the deserialized content was
     * not valid for the requested message.
     * <p>
     * NOTE: In providence this is valid for server (processor) side
     * serialization errors.
     */
    PROTOCOL_ERROR(7, "PROTOCOL_ERROR"),
    /**
     * NOTE: This value is apparently not in use in thrift.
     */
    INVALID_TRANSFORM(8, "INVALID_TRANSFORM"),
    /**
     * The requested protocol (or version) is not supported.
     */
    INVALID_PROTOCOL(9, "INVALID_PROTOCOL"),
    /**
     * NOTE: This value is apparently not in use in thrift.
     */
    UNSUPPORTED_CLIENT_TYPE(10, "UNSUPPORTED_CLIENT_TYPE"),
    ;

    private final int mValue;
    private final String mName;

    PApplicationExceptionType(int value, String name) {
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

    public static PApplicationExceptionType forValue(int value) {
        switch (value) {
            case 0: return PApplicationExceptionType.UNKNOWN;
            case 1: return PApplicationExceptionType.UNKNOWN_METHOD;
            case 2: return PApplicationExceptionType.INVALID_MESSAGE_TYPE;
            case 3: return PApplicationExceptionType.WRONG_METHOD_NAME;
            case 4: return PApplicationExceptionType.BAD_SEQUENCE_ID;
            case 5: return PApplicationExceptionType.MISSING_RESULT;
            case 6: return PApplicationExceptionType.INTERNAL_ERROR;
            case 7: return PApplicationExceptionType.PROTOCOL_ERROR;
            case 8: return PApplicationExceptionType.INVALID_TRANSFORM;
            case 9: return PApplicationExceptionType.INVALID_PROTOCOL;
            case 10: return PApplicationExceptionType.UNSUPPORTED_CLIENT_TYPE;
            default: return null;
        }
    }

    public static PApplicationExceptionType forName(String name) {
        switch (name) {
            case "UNKNOWN": return PApplicationExceptionType.UNKNOWN;
            case "UNKNOWN_METHOD": return PApplicationExceptionType.UNKNOWN_METHOD;
            case "INVALID_MESSAGE_TYPE": return PApplicationExceptionType.INVALID_MESSAGE_TYPE;
            case "WRONG_METHOD_NAME": return PApplicationExceptionType.WRONG_METHOD_NAME;
            case "BAD_SEQUENCE_ID": return PApplicationExceptionType.BAD_SEQUENCE_ID;
            case "MISSING_RESULT": return PApplicationExceptionType.MISSING_RESULT;
            case "INTERNAL_ERROR": return PApplicationExceptionType.INTERNAL_ERROR;
            case "PROTOCOL_ERROR": return PApplicationExceptionType.PROTOCOL_ERROR;
            case "INVALID_TRANSFORM": return PApplicationExceptionType.INVALID_TRANSFORM;
            case "INVALID_PROTOCOL": return PApplicationExceptionType.INVALID_PROTOCOL;
            case "UNSUPPORTED_CLIENT_TYPE": return PApplicationExceptionType.UNSUPPORTED_CLIENT_TYPE;
            default: return null;
        }
    }

    public static class _Builder extends net.morimekta.providence.PEnumBuilder<PApplicationExceptionType> {
        PApplicationExceptionType mValue;

        @Override
        public _Builder setByValue(int value) {
            mValue = PApplicationExceptionType.forValue(value);
            return this;
        }

        @Override
        public _Builder setByName(String name) {
            mValue = PApplicationExceptionType.forName(name);
            return this;
        }

        @Override
        public boolean isValid() {
            return mValue != null;
        }

        @Override
        public PApplicationExceptionType build() {
            return mValue;
        }
    }

    public static final net.morimekta.providence.descriptor.PEnumDescriptor<PApplicationExceptionType> kDescriptor;

    @Override
    public net.morimekta.providence.descriptor.PEnumDescriptor<PApplicationExceptionType> descriptor() {
        return kDescriptor;
    }

    public static net.morimekta.providence.descriptor.PEnumDescriptorProvider<PApplicationExceptionType> provider() {
        return new net.morimekta.providence.descriptor.PEnumDescriptorProvider<PApplicationExceptionType>(kDescriptor);
    }

    private static class _Factory
            extends net.morimekta.providence.PEnumBuilderFactory<PApplicationExceptionType> {
        @Override
        public PApplicationExceptionType._Builder builder() {
            return new PApplicationExceptionType._Builder();
        }
    }

    private static class _Descriptor
            extends net.morimekta.providence.descriptor.PEnumDescriptor<PApplicationExceptionType> {
        public _Descriptor() {
            super("service", "PApplicationExceptionType", new _Factory());
        }

        @Override
        public PApplicationExceptionType[] getValues() {
            return PApplicationExceptionType.values();
        }

        @Override
        public PApplicationExceptionType getValueById(int id) {
            return PApplicationExceptionType.forValue(id);
        }

        @Override
        public PApplicationExceptionType getValueByName(String name) {
            return PApplicationExceptionType.forName(name);
        }
    }

    static {
        kDescriptor = new _Descriptor();
    }
}
