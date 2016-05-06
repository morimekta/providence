package net.morimekta.providence.serializer;

public enum ApplicationExceptionType implements net.morimekta.providence.PEnumValue<ApplicationExceptionType> {
    UNKNOWN(0, "UNKNOWN"),
    UNKNOWN_METHOD(1, "UNKNOWN_METHOD"),
    INVALID_MESSAGE_TYPE(2, "INVALID_MESSAGE_TYPE"),
    WRONG_METHOD_NAME(3, "WRONG_METHOD_NAME"),
    BAD_SEQUENCE_ID(4, "BAD_SEQUENCE_ID"),
    MISSING_RESULT(5, "MISSING_RESULT"),
    INTERNAL_ERROR(6, "INTERNAL_ERROR"),
    PROTOCOL_ERROR(7, "PROTOCOL_ERROR"),
    INVALID_TRANSFORM(8, "INVALID_TRANSFORM"),
    INVALID_PROTOCOL(9, "INVALID_PROTOCOL"),
    UNSUPPORTED_CLIENT_TYPE(10, "UNSUPPORTED_CLIENT_TYPE"),
    ;

    private final int mValue;
    private final String mName;

    ApplicationExceptionType(int value, String name) {
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
    public String asString() {
        return mName;
    }

    public static ApplicationExceptionType forValue(int value) {
        switch (value) {
            case 0: return ApplicationExceptionType.UNKNOWN;
            case 1: return ApplicationExceptionType.UNKNOWN_METHOD;
            case 2: return ApplicationExceptionType.INVALID_MESSAGE_TYPE;
            case 3: return ApplicationExceptionType.WRONG_METHOD_NAME;
            case 4: return ApplicationExceptionType.BAD_SEQUENCE_ID;
            case 5: return ApplicationExceptionType.MISSING_RESULT;
            case 6: return ApplicationExceptionType.INTERNAL_ERROR;
            case 7: return ApplicationExceptionType.PROTOCOL_ERROR;
            case 8: return ApplicationExceptionType.INVALID_TRANSFORM;
            case 9: return ApplicationExceptionType.INVALID_PROTOCOL;
            case 10: return ApplicationExceptionType.UNSUPPORTED_CLIENT_TYPE;
            default: return null;
        }
    }

    public static ApplicationExceptionType forName(String name) {
        switch (name) {
            case "UNKNOWN": return ApplicationExceptionType.UNKNOWN;
            case "UNKNOWN_METHOD": return ApplicationExceptionType.UNKNOWN_METHOD;
            case "INVALID_MESSAGE_TYPE": return ApplicationExceptionType.INVALID_MESSAGE_TYPE;
            case "WRONG_METHOD_NAME": return ApplicationExceptionType.WRONG_METHOD_NAME;
            case "BAD_SEQUENCE_ID": return ApplicationExceptionType.BAD_SEQUENCE_ID;
            case "MISSING_RESULT": return ApplicationExceptionType.MISSING_RESULT;
            case "INTERNAL_ERROR": return ApplicationExceptionType.INTERNAL_ERROR;
            case "PROTOCOL_ERROR": return ApplicationExceptionType.PROTOCOL_ERROR;
            case "INVALID_TRANSFORM": return ApplicationExceptionType.INVALID_TRANSFORM;
            case "INVALID_PROTOCOL": return ApplicationExceptionType.INVALID_PROTOCOL;
            case "UNSUPPORTED_CLIENT_TYPE": return ApplicationExceptionType.UNSUPPORTED_CLIENT_TYPE;
            default: return null;
        }
    }

    public static class _Builder extends net.morimekta.providence.PEnumBuilder<ApplicationExceptionType> {
        ApplicationExceptionType mValue;

        @Override
        public _Builder setByValue(int value) {
            mValue = ApplicationExceptionType.forValue(value);
            return this;
        }

        @Override
        public _Builder setByName(String name) {
            mValue = ApplicationExceptionType.forName(name);
            return this;
        }

        @Override
        public boolean isValid() {
            return mValue != null;
        }

        @Override
        public ApplicationExceptionType build() {
            return mValue;
        }
    }

    public static final net.morimekta.providence.descriptor.PEnumDescriptor<ApplicationExceptionType> kDescriptor;

    @Override
    public net.morimekta.providence.descriptor.PEnumDescriptor<ApplicationExceptionType> descriptor() {
        return kDescriptor;
    }

    public static net.morimekta.providence.descriptor.PEnumDescriptorProvider<ApplicationExceptionType> provider() {
        return new net.morimekta.providence.descriptor.PEnumDescriptorProvider<ApplicationExceptionType>(kDescriptor);
    }

    private static class _Factory
            extends net.morimekta.providence.PEnumBuilderFactory<ApplicationExceptionType> {
        @Override
        public ApplicationExceptionType._Builder builder() {
            return new ApplicationExceptionType._Builder();
        }
    }

    private static class _Descriptor
            extends net.morimekta.providence.descriptor.PEnumDescriptor<ApplicationExceptionType> {
        public _Descriptor() {
            super("service", "ApplicationExceptionType", new _Factory());
        }

        @Override
        public ApplicationExceptionType[] getValues() {
            return ApplicationExceptionType.values();
        }

        @Override
        public ApplicationExceptionType getValueById(int id) {
            return ApplicationExceptionType.forValue(id);
        }

        @Override
        public ApplicationExceptionType getValueByName(String name) {
            return ApplicationExceptionType.forName(name);
        }
    }

    static {
        kDescriptor = new _Descriptor();
    }
}
