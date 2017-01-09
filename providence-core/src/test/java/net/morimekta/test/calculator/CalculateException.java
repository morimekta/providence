package net.morimekta.test.calculator;

@SuppressWarnings("unused")
public class CalculateException
        extends Exception
        implements net.morimekta.providence.PMessage<CalculateException,CalculateException._Field>,
                   net.morimekta.providence.PException,
                   Comparable<CalculateException>,
                   net.morimekta.providence.serializer.rw.BinaryWriter {
    private final static long serialVersionUID = -3144631929815376595L;

    private final String mMessage;
    private final net.morimekta.test.calculator.Operation mOperation;

    private volatile int tHashCode;

    public CalculateException(String pMessage,
                              net.morimekta.test.calculator.Operation pOperation) {
        super(pMessage);

        mMessage = pMessage;
        mOperation = pOperation;
    }

    private CalculateException(_Builder builder) {
        super(builder.mMessage);

        mMessage = builder.mMessage;
        mOperation = builder.mOperation_builder != null ? builder.mOperation_builder.build() : builder.mOperation;
    }

    public boolean hasMessage() {
        return mMessage != null;
    }

    /**
     * @return The field value
     */
    public String getMessage() {
        return mMessage;
    }

    public boolean hasOperation() {
        return mOperation != null;
    }

    /**
     * @return The field value
     */
    public net.morimekta.test.calculator.Operation getOperation() {
        return mOperation;
    }

    @Override
    public boolean has(int key) {
        switch(key) {
            case 1: return hasMessage();
            case 2: return hasOperation();
            default: return false;
        }
    }

    @Override
    public int num(int key) {
        switch(key) {
            case 1: return hasMessage() ? 1 : 0;
            case 2: return hasOperation() ? 1 : 0;
            default: return 0;
        }
    }

    @Override
    public Object get(int key) {
        switch(key) {
            case 1: return getMessage();
            case 2: return getOperation();
            default: return null;
        }
    }

    @Override
    public boolean compact() {
        return false;
    }

    @Override
    public String origGetMessage() {
        return super.getMessage();
    }

    @Override
    public String origGetLocalizedMessage() {
        return super.getLocalizedMessage();
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        if (o == null || !(o instanceof CalculateException)) return false;
        CalculateException other = (CalculateException) o;
        return java.util.Objects.equals(mMessage, other.mMessage) &&
               java.util.Objects.equals(mOperation, other.mOperation);
    }

    @Override
    public int hashCode() {
        if (tHashCode == 0) {
            tHashCode = java.util.Objects.hash(
                    CalculateException.class,
                    _Field.MESSAGE, mMessage,
                    _Field.OPERATION, mOperation);
        }
        return tHashCode;
    }

    @Override
    public String toString() {
        return "calculator.CalculateException" + asString();
    }

    @Override
    public String asString() {
        StringBuilder out = new StringBuilder();
        out.append("{");

        boolean first = true;
        if (hasMessage()) {
            first = false;
            out.append("message:")
               .append('\"')
               .append(net.morimekta.util.Strings.escape(mMessage))
               .append('\"');
        }
        if (hasOperation()) {
            if (!first) out.append(',');
            out.append("operation:")
               .append(mOperation.asString());
        }
        out.append('}');
        return out.toString();
    }

    @Override
    public int compareTo(CalculateException other) {
        int c;

        c = Boolean.compare(mMessage != null, other.mMessage != null);
        if (c != 0) return c;
        if (mMessage != null) {
            c = mMessage.compareTo(other.mMessage);
            if (c != 0) return c;
        }

        c = Boolean.compare(mOperation != null, other.mOperation != null);
        if (c != 0) return c;
        if (mOperation != null) {
            c = mOperation.compareTo(other.mOperation);
            if (c != 0) return c;
        }

        return 0;
    }

    @Override
    public int writeBinary(net.morimekta.util.io.BigEndianBinaryWriter writer) throws java.io.IOException {
        int length = 0;

        if (hasMessage()) {
            length += writer.writeByte((byte) 11);
            length += writer.writeShort((short) 1);
            net.morimekta.util.Binary tmp_1 = net.morimekta.util.Binary.wrap(mMessage.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            length += writer.writeUInt32(tmp_1.length());
            length += writer.writeBinary(tmp_1);
        }

        if (hasOperation()) {
            length += writer.writeByte((byte) 12);
            length += writer.writeShort((short) 2);
            length += net.morimekta.providence.serializer.rw.BinaryFormatUtils.writeMessage(writer, mOperation);
        }

        length += writer.writeByte((byte) 0);
        return length;
    }

    @Override
    public _Builder mutate() {
        return new _Builder(this);
    }

    public enum _Field implements net.morimekta.providence.descriptor.PField {
        MESSAGE(1, net.morimekta.providence.descriptor.PRequirement.REQUIRED, "message", net.morimekta.providence.descriptor.PPrimitive.STRING.provider(), null),
        OPERATION(2, net.morimekta.providence.descriptor.PRequirement.DEFAULT, "operation", net.morimekta.test.calculator.Operation.provider(), null),
        ;

        private final int mKey;
        private final net.morimekta.providence.descriptor.PRequirement mRequired;
        private final String mName;
        private final net.morimekta.providence.descriptor.PDescriptorProvider mTypeProvider;
        private final net.morimekta.providence.descriptor.PValueProvider<?> mDefaultValue;

        _Field(int key, net.morimekta.providence.descriptor.PRequirement required, String name, net.morimekta.providence.descriptor.PDescriptorProvider typeProvider, net.morimekta.providence.descriptor.PValueProvider<?> defaultValue) {
            mKey = key;
            mRequired = required;
            mName = name;
            mTypeProvider = typeProvider;
            mDefaultValue = defaultValue;
        }

        @Override
        public int getKey() { return mKey; }

        @Override
        public net.morimekta.providence.descriptor.PRequirement getRequirement() { return mRequired; }

        @Override
        public net.morimekta.providence.descriptor.PDescriptor getDescriptor() { return mTypeProvider.descriptor(); }

        @Override
        public String getName() { return mName; }

        @Override
        public boolean hasDefaultValue() { return mDefaultValue != null; }

        @Override
        public Object getDefaultValue() {
            return hasDefaultValue() ? mDefaultValue.get() : null;
        }

        @Override
        public String toString() {
            return net.morimekta.providence.descriptor.PField.toString(this);
        }

        public static _Field forKey(int key) {
            switch (key) {
                case 1: return _Field.MESSAGE;
                case 2: return _Field.OPERATION;
            }
            return null;
        }

        public static _Field forName(String name) {
            switch (name) {
                case "message": return _Field.MESSAGE;
                case "operation": return _Field.OPERATION;
            }
            return null;
        }
    }

    public static net.morimekta.providence.descriptor.PExceptionDescriptorProvider<CalculateException,_Field> provider() {
        return new _Provider();
    }

    @Override
    public net.morimekta.providence.descriptor.PExceptionDescriptor<CalculateException,_Field> descriptor() {
        return kDescriptor;
    }

    public static final net.morimekta.providence.descriptor.PExceptionDescriptor<CalculateException,_Field> kDescriptor;

    private static class _Descriptor
            extends net.morimekta.providence.descriptor.PExceptionDescriptor<CalculateException,_Field> {
        public _Descriptor() {
            super("calculator", "CalculateException", new _Factory(), false);
        }

        @Override
        public _Field[] getFields() {
            return _Field.values();
        }

        @Override
        public _Field getField(String name) {
            return _Field.forName(name);
        }

        @Override
        public _Field getField(int key) {
            return _Field.forKey(key);
        }
    }

    static {
        kDescriptor = new _Descriptor();
    }

    private final static class _Provider extends net.morimekta.providence.descriptor.PExceptionDescriptorProvider<CalculateException,_Field> {
        @Override
        public net.morimekta.providence.descriptor.PExceptionDescriptor<CalculateException,_Field> descriptor() {
            return kDescriptor;
        }
    }

    private final static class _Factory
            extends net.morimekta.providence.PMessageBuilderFactory<CalculateException,_Field> {
        @Override
        public _Builder builder() {
            return new _Builder();
        }
    }

    /**
     * Make a calculator.CalculateException builder.
     * @return The builder instance.
     */
    public static _Builder builder() {
        return new _Builder();
    }

    public static class _Builder
            extends net.morimekta.providence.PMessageBuilder<CalculateException,_Field>
            implements net.morimekta.providence.serializer.rw.BinaryReader {
        private java.util.BitSet optionals;

        private String mMessage;
        private net.morimekta.test.calculator.Operation mOperation;
        private net.morimekta.test.calculator.Operation._Builder mOperation_builder;

        /**
         * Make a calculator.CalculateException builder.
         */
        public _Builder() {
            optionals = new java.util.BitSet(2);
        }

        /**
         * Make a mutating builder off a base calculator.CalculateException.
         *
         * @param base The base CalculateException
         */
        public _Builder(CalculateException base) {
            this();

            if (base.hasMessage()) {
                optionals.set(0);
                mMessage = base.mMessage;
            }
            if (base.hasOperation()) {
                optionals.set(1);
                mOperation = base.mOperation;
            }
        }

        @Override
        public _Builder merge(CalculateException from) {
            if (from.hasMessage()) {
                optionals.set(0);
                mMessage = from.getMessage();
            }

            if (from.hasOperation()) {
                optionals.set(1);
                if (mOperation_builder != null) {
                    mOperation_builder.merge(from.getOperation());
                } else if (mOperation != null) {
                    mOperation_builder = mOperation.mutate().merge(from.getOperation());
                    mOperation = null;
                } else {
                    mOperation = from.getOperation();
                }
            }
            return this;
        }

        /**
         * Sets the value of message.
         *
         * @param value The new value
         * @return The builder
         */
        public _Builder setMessage(String value) {
            optionals.set(0);
            mMessage = value;
            return this;
        }

        /**
         * Checks for presence of the message field.
         *
         * @return True iff message has been set.
         */
        public boolean isSetMessage() {
            return optionals.get(0);
        }

        /**
         * Clears the message field.
         *
         * @return The builder
         */
        public _Builder clearMessage() {
            optionals.clear(0);
            mMessage = null;
            return this;
        }

        /**
         * Gets the value of the contained message.
         *
         * @return The field value
         */
        public String getMessage() {
            return mMessage;
        }

        /**
         * Sets the value of operation.
         *
         * @param value The new value
         * @return The builder
         */
        public _Builder setOperation(net.morimekta.test.calculator.Operation value) {
            optionals.set(1);
            mOperation_builder = null;
            mOperation = value;
            return this;
        }

        /**
         * Checks for presence of the operation field.
         *
         * @return True iff operation has been set.
         */
        public boolean isSetOperation() {
            return optionals.get(1);
        }

        /**
         * Clears the operation field.
         *
         * @return The builder
         */
        public _Builder clearOperation() {
            optionals.clear(1);
            mOperation = null;
            mOperation_builder = null;
            return this;
        }

        /**
         * Gets the builder for the contained operation.
         *
         * @return The field builder
         */
        public net.morimekta.test.calculator.Operation._Builder mutableOperation() {
            optionals.set(1);

            if (mOperation != null) {
                mOperation_builder = mOperation.mutate();
                mOperation = null;
            } else if (mOperation_builder == null) {
                mOperation_builder = net.morimekta.test.calculator.Operation.builder();
            }
            return mOperation_builder;
        }

        @Override
        @SuppressWarnings("unchecked")
        public net.morimekta.providence.PMessageBuilder mutator(int key) {
            switch (key) {
                case 2: return mutableOperation();
                default: throw new IllegalArgumentException("Not a message field ID: " + key);
            }
        }

        @Override
        @SuppressWarnings("unchecked")
        public _Builder set(int key, Object value) {
            if (value == null) return clear(key);
            switch (key) {
                case 1: setMessage((String) value); break;
                case 2: setOperation((net.morimekta.test.calculator.Operation) value); break;
                default: break;
            }
            return this;
        }

        @Override
        public boolean isSet(int key) {
            switch (key) {
                case 1: return optionals.get(0);
                case 2: return optionals.get(1);
                default: break;
            }
            return false;
        }

        @Override
        public _Builder addTo(int key, Object value) {
            switch (key) {
                default: break;
            }
            return this;
        }

        @Override
        public _Builder clear(int key) {
            switch (key) {
                case 1: clearMessage(); break;
                case 2: clearOperation(); break;
                default: break;
            }
            return this;
        }

        @Override
        public boolean valid() {
            return optionals.get(0);
        }

        @Override
        public void validate() {
            if (!valid()) {
                java.util.LinkedList<String> missing = new java.util.LinkedList<>();

                if (!optionals.get(0)) {
                    missing.add("message");
                }

                throw new java.lang.IllegalStateException(
                        "Missing required fields " +
                        String.join(",", missing) +
                        " in message calculator.CalculateException");
            }
        }

        @Override
        public net.morimekta.providence.descriptor.PExceptionDescriptor<CalculateException,_Field> descriptor() {
            return kDescriptor;
        }

        @Override
        public void readBinary(net.morimekta.util.io.BigEndianBinaryReader reader, boolean strict) throws java.io.IOException {
            byte type = reader.expectByte();
            while (type != 0) {
                int field = reader.expectShort();
                switch (field) {
                    case 1: {
                        if (type == 11) {
                            int len_1 = reader.expectUInt32();
                            mMessage = new String(reader.expectBytes(len_1), java.nio.charset.StandardCharsets.UTF_8);
                            optionals.set(0);
                        } else {
                            throw new net.morimekta.providence.serializer.SerializerException("Wrong type " + type + " for calculator.CalculateException.message, should be 12");
                        }
                        break;
                    }
                    case 2: {
                        if (type == 12) {
                            mOperation = net.morimekta.providence.serializer.rw.BinaryFormatUtils.readMessage(reader, net.morimekta.test.calculator.Operation.kDescriptor, strict);
                            optionals.set(1);
                        } else {
                            throw new net.morimekta.providence.serializer.SerializerException("Wrong type " + type + " for calculator.CalculateException.operation, should be 12");
                        }
                        break;
                    }
                    default: {
                        if (strict) {
                            throw new net.morimekta.providence.serializer.SerializerException("No field with id " + field + " exists in calculator.CalculateException");
                        } else {
                            net.morimekta.providence.serializer.rw.BinaryFormatUtils.readFieldValue(reader, new net.morimekta.providence.serializer.rw.BinaryFormatUtils.FieldInfo(field, type), null, false);
                        }
                        break;
                    }
                }
                type = reader.expectByte();
            }
        }

        @Override
        public CalculateException build() {
            return new CalculateException(this);
        }
    }
}
