package net.morimekta.test.providence.reflect.calculator;

@SuppressWarnings("unused")
@javax.annotation.Generated("providence-maven-plugin")
public class CalculateException
        extends Exception
        implements net.morimekta.providence.PMessage<CalculateException,CalculateException._Field>,
                   net.morimekta.providence.PException,
                   Comparable<CalculateException>,
                   net.morimekta.providence.serializer.binary.BinaryWriter {
    private final static long serialVersionUID = -3144631929815376595L;

    private final static String kDefaultMessage = "";

    private final String mMessage;
    private final net.morimekta.test.providence.reflect.calculator.Operation mOperation;

    private volatile int tHashCode;

    private CalculateException(_Builder builder) {
        super(builder.mMessage);

        if (builder.isSetMessage()) {
            mMessage = builder.mMessage;
        } else {
            mMessage = kDefaultMessage;
        }
        mOperation = builder.mOperation_builder != null ? builder.mOperation_builder.build() : builder.mOperation;
    }

    public boolean hasMessage() {
        return true;
    }

    /**
     * @return The field value
     */
    @javax.annotation.Nonnull
    public String getMessage() {
        return mMessage;
    }

    public boolean hasOperation() {
        return mOperation != null;
    }

    /**
     * @return The field value
     */
    public net.morimekta.test.providence.reflect.calculator.Operation getOperation() {
        return mOperation;
    }

    @Override
    public boolean has(int key) {
        switch(key) {
            case 1: return true;
            case 2: return hasOperation();
            default: return false;
        }
    }

    @Override
    public int num(int key) {
        switch(key) {
            case 1: return 1;
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
    public String origGetMessage() {
        return super.getMessage();
    }

    @Override
    public String origGetLocalizedMessage() {
        return super.getLocalizedMessage();
    }

    @Override
    public CalculateException initCause(Throwable cause) {
        return (CalculateException) super.initCause(cause);
    }

    @Override
    public CalculateException fillInStackTrace() {
        return (CalculateException) super.fillInStackTrace();
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        if (o == null || !o.getClass().equals(getClass())) return false;
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
    @javax.annotation.Nonnull
    public String asString() {
        StringBuilder out = new StringBuilder();
        out.append("{");

        out.append("message:")
           .append('\"')
           .append(net.morimekta.util.Strings.escape(mMessage))
           .append('\"');
        if (hasOperation()) {
            out.append(',');
            out.append("operation:")
               .append(mOperation.asString());
        }
        out.append('}');
        return out.toString();
    }

    @Override
    public int compareTo(CalculateException other) {
        int c;

        c = mMessage.compareTo(other.mMessage);
        if (c != 0) return c;

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

        length += writer.writeByte((byte) 11);
        length += writer.writeShort((short) 1);
        net.morimekta.util.Binary tmp_1 = net.morimekta.util.Binary.wrap(mMessage.getBytes(java.nio.charset.StandardCharsets.UTF_8));
        length += writer.writeUInt32(tmp_1.length());
        length += writer.writeBinary(tmp_1);

        if (hasOperation()) {
            length += writer.writeByte((byte) 12);
            length += writer.writeShort((short) 2);
            length += net.morimekta.providence.serializer.binary.BinaryFormatUtils.writeMessage(writer, mOperation);
        }

        length += writer.writeByte((byte) 0);
        return length;
    }

    @javax.annotation.Nonnull
    @Override
    public _Builder mutate() {
        return new _Builder(this);
    }

    public enum _Field implements net.morimekta.providence.descriptor.PField {
        MESSAGE(1, net.morimekta.providence.descriptor.PRequirement.REQUIRED, "message", net.morimekta.providence.descriptor.PPrimitive.STRING.provider(), null),
        OPERATION(2, net.morimekta.providence.descriptor.PRequirement.OPTIONAL, "operation", net.morimekta.test.providence.reflect.calculator.Operation.provider(), null),
        ;

        private final int mId;
        private final net.morimekta.providence.descriptor.PRequirement mRequired;
        private final String mName;
        private final net.morimekta.providence.descriptor.PDescriptorProvider mTypeProvider;
        private final net.morimekta.providence.descriptor.PValueProvider<?> mDefaultValue;

        _Field(int id, net.morimekta.providence.descriptor.PRequirement required, String name, net.morimekta.providence.descriptor.PDescriptorProvider typeProvider, net.morimekta.providence.descriptor.PValueProvider<?> defaultValue) {
            mId = id;
            mRequired = required;
            mName = name;
            mTypeProvider = typeProvider;
            mDefaultValue = defaultValue;
        }

        @Override
        public int getId() { return mId; }

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
            return net.morimekta.providence.descriptor.PField.asString(this);
        }

        /**
         * @param id Field name
         * @return The identified field or null
         */
        public static _Field findById(int id) {
            switch (id) {
                case 1: return _Field.MESSAGE;
                case 2: return _Field.OPERATION;
            }
            return null;
        }

        /**
         * @param name Field name
         * @return The named field or null
         */
        public static _Field findByName(String name) {
            switch (name) {
                case "message": return _Field.MESSAGE;
                case "operation": return _Field.OPERATION;
            }
            return null;
        }
        /**
         * @param id Field name
         * @return The identified field
         * @throws IllegalArgumentException If no such field
         */
        public static _Field fieldForId(int id) {
            _Field field = findById(id);
            if (field == null) {
                throw new IllegalArgumentException("No such field id " + id + " in calculator.CalculateException");
            }
            return field;
        }

        /**
         * @param name Field name
         * @return The named field
         * @throws IllegalArgumentException If no such field
         */
        public static _Field fieldForName(String name) {
            _Field field = findByName(name);
            if (field == null) {
                throw new IllegalArgumentException("No such field \"" + name + "\" in calculator.CalculateException");
            }
            return field;
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
            super("calculator", "CalculateException", _Builder::new, false);
        }

        @Override
        @javax.annotation.Nonnull
        public _Field[] getFields() {
            return _Field.values();
        }

        @Override
        @javax.annotation.Nullable
        public _Field findFieldByName(String name) {
            return _Field.findByName(name);
        }

        @Override
        @javax.annotation.Nullable
        public _Field findFieldById(int id) {
            return _Field.findById(id);
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

    /**
     * Make a calculator.CalculateException builder.
     * @return The builder instance.
     */
    public static _Builder builder() {
        return new _Builder();
    }

    public static class _Builder
            extends net.morimekta.providence.PMessageBuilder<CalculateException,_Field>
            implements net.morimekta.providence.serializer.binary.BinaryReader {
        private Throwable cause;
        private java.util.BitSet optionals;
        private java.util.BitSet modified;

        private String mMessage;
        private net.morimekta.test.providence.reflect.calculator.Operation mOperation;
        private net.morimekta.test.providence.reflect.calculator.Operation._Builder mOperation_builder;

        /**
         * Make a calculator.CalculateException builder.
         */
        public _Builder() {
            optionals = new java.util.BitSet(2);
            modified = new java.util.BitSet(2);
            mMessage = kDefaultMessage;
        }

        /**
         * Make a mutating builder off a base calculator.CalculateException.
         *
         * @param base The base CalculateException
         */
        public _Builder(CalculateException base) {
            this();

            optionals.set(0);
            mMessage = base.mMessage;
            if (base.hasOperation()) {
                optionals.set(1);
                mOperation = base.mOperation;
            }
        }

        @javax.annotation.Nonnull
        @Override
        public _Builder merge(CalculateException from) {
            optionals.set(0);
            modified.set(0);
            mMessage = from.getMessage();

            if (from.hasOperation()) {
                optionals.set(1);
                modified.set(1);
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
        @javax.annotation.Nonnull
        public _Builder setMessage(String value) {
            if (value == null) {
                return clearMessage();
            }

            optionals.set(0);
            modified.set(0);
            mMessage = value;
            return this;
        }

        /**
         * Checks for presence of the message field.
         *
         * @return True if message has been set.
         */
        public boolean isSetMessage() {
            return optionals.get(0);
        }

        /**
         * Checks if message has been modified since the _Builder was created.
         *
         * @return True if message has been modified.
         */
        public boolean isModifiedMessage() {
            return modified.get(0);
        }

        /**
         * Clears the message field.
         *
         * @return The builder
         */
        @javax.annotation.Nonnull
        public _Builder clearMessage() {
            optionals.clear(0);
            modified.set(0);
            mMessage = kDefaultMessage;
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
        @javax.annotation.Nonnull
        public _Builder setOperation(net.morimekta.test.providence.reflect.calculator.Operation value) {
            if (value == null) {
                return clearOperation();
            }

            optionals.set(1);
            modified.set(1);
            mOperation = value;
            mOperation_builder = null;
            return this;
        }

        /**
         * Checks for presence of the operation field.
         *
         * @return True if operation has been set.
         */
        public boolean isSetOperation() {
            return optionals.get(1);
        }

        /**
         * Checks if operation has been modified since the _Builder was created.
         *
         * @return True if operation has been modified.
         */
        public boolean isModifiedOperation() {
            return modified.get(1);
        }

        /**
         * Clears the operation field.
         *
         * @return The builder
         */
        @javax.annotation.Nonnull
        public _Builder clearOperation() {
            optionals.clear(1);
            modified.set(1);
            mOperation = null;
            mOperation_builder = null;
            return this;
        }

        /**
         * Gets the builder for the contained operation.
         *
         * @return The field builder
         */
        @javax.annotation.Nonnull
        public net.morimekta.test.providence.reflect.calculator.Operation._Builder mutableOperation() {
            optionals.set(1);
            modified.set(1);

            if (mOperation != null) {
                mOperation_builder = mOperation.mutate();
                mOperation = null;
            } else if (mOperation_builder == null) {
                mOperation_builder = net.morimekta.test.providence.reflect.calculator.Operation.builder();
            }
            return mOperation_builder;
        }

        /**
         * Initializes the cause of the calculator.CalculateException
         *
         * @param cause The cause
         * @return Builder instance
         */
        @javax.annotation.Nonnull
        public _Builder initCause(Throwable cause) {
            this.cause = cause;
            return this;
        }

        @Override
        public boolean equals(Object o) {
            if (o == this) return true;
            if (o == null || !o.getClass().equals(getClass())) return false;
            CalculateException._Builder other = (CalculateException._Builder) o;
            return java.util.Objects.equals(optionals, other.optionals) &&
                   java.util.Objects.equals(mMessage, other.mMessage) &&
                   java.util.Objects.equals(mOperation, other.mOperation);
        }

        @Override
        public int hashCode() {
            return java.util.Objects.hash(
                    CalculateException.class, optionals,
                    _Field.MESSAGE, mMessage,
                    _Field.OPERATION, mOperation);
        }

        @Override
        @SuppressWarnings("unchecked")
        public net.morimekta.providence.PMessageBuilder mutator(int key) {
            switch (key) {
                case 2: return mutableOperation();
                default: throw new IllegalArgumentException("Not a message field ID: " + key);
            }
        }

        @javax.annotation.Nonnull
        @Override
        @SuppressWarnings("unchecked")
        public _Builder set(int key, Object value) {
            if (value == null) return clear(key);
            switch (key) {
                case 1: setMessage((String) value); break;
                case 2: setOperation((net.morimekta.test.providence.reflect.calculator.Operation) value); break;
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
        public boolean isModified(int key) {
            switch (key) {
                case 1: return modified.get(0);
                case 2: return modified.get(1);
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

        @javax.annotation.Nonnull
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

        @javax.annotation.Nonnull
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
                            throw new net.morimekta.providence.serializer.SerializerException("Wrong type " + net.morimekta.providence.serializer.binary.BinaryType.asString(type) + " for calculator.CalculateException.message, should be struct(12)");
                        }
                        break;
                    }
                    case 2: {
                        if (type == 12) {
                            mOperation = net.morimekta.providence.serializer.binary.BinaryFormatUtils.readMessage(reader, net.morimekta.test.providence.reflect.calculator.Operation.kDescriptor, strict);
                            optionals.set(1);
                        } else {
                            throw new net.morimekta.providence.serializer.SerializerException("Wrong type " + net.morimekta.providence.serializer.binary.BinaryType.asString(type) + " for calculator.CalculateException.operation, should be struct(12)");
                        }
                        break;
                    }
                    default: {
                        net.morimekta.providence.serializer.binary.BinaryFormatUtils.readFieldValue(reader, new net.morimekta.providence.serializer.binary.BinaryFormatUtils.FieldInfo(field, type), null, false);
                        break;
                    }
                }
                type = reader.expectByte();
            }
        }

        @Override
        public CalculateException build() {
            CalculateException e = new CalculateException(this);

            try {
                StackTraceElement[] stackTrace = e.getStackTrace();
                StackTraceElement[] subTrace = new StackTraceElement[stackTrace.length - 1];
                System.arraycopy(stackTrace, 1, subTrace, 0, subTrace.length);
                e.setStackTrace(subTrace);
            } catch (Throwable ignored) {
            }

            if (cause != null) {
                e.initCause(cause);
            }

            return e;
        }
    }
}
