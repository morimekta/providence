package net.morimekta.test.providence.reflect.calculator;

@SuppressWarnings("unused")
@javax.annotation.Generated("providence-maven-plugin")
public class Calculator {
    public interface Iface {
        /**
         * @param pOp The op value.
         * @return The calculate result.
         * @throws net.morimekta.test.providence.reflect.calculator.CalculateException The ce exception.
         * @throws java.io.IOException On providence or non-declared exceptions.
         */
        net.morimekta.test.providence.reflect.calculator.Operand calculate(
                net.morimekta.test.providence.reflect.calculator.Operation pOp)
                throws java.io.IOException,
                       net.morimekta.test.providence.reflect.calculator.CalculateException;

        /**
         * @throws java.io.IOException On providence or non-declared exceptions.
         */
        void iamalive()
                throws java.io.IOException;

        /**
         * @throws java.io.IOException On providence or non-declared exceptions.
         */
        void ping()
                throws java.io.IOException;
    }

    /**
     * Client implementation for calculator.Calculator
     */
    public static class Client
            extends net.morimekta.providence.PClient
            implements Iface {
        private final net.morimekta.providence.PServiceCallHandler handler;

        /**
         * Create calculator.Calculator service client.
         *
         * @param handler The client handler.
         */
        public Client(net.morimekta.providence.PServiceCallHandler handler) {
            this.handler = handler;
        }

        @Override
        public net.morimekta.test.providence.reflect.calculator.Operand calculate(
                net.morimekta.test.providence.reflect.calculator.Operation pOp)
                throws java.io.IOException,
                       net.morimekta.test.providence.reflect.calculator.CalculateException {
            net.morimekta.test.providence.reflect.calculator.Calculator._calculate_request._Builder rq = net.morimekta.test.providence.reflect.calculator.Calculator._calculate_request.builder();
            if (pOp != null) {
                rq.setOp(pOp);
            }

            net.morimekta.providence.PServiceCall call = new net.morimekta.providence.PServiceCall<>("calculate", net.morimekta.providence.PServiceCallType.CALL, getNextSequenceId(), rq.build());
            net.morimekta.providence.PServiceCall resp = handler.handleCall(call, Calculator.kDescriptor);

            if (resp.getType() == net.morimekta.providence.PServiceCallType.EXCEPTION) {
                throw (net.morimekta.providence.PApplicationException) resp.getMessage();
            }

            net.morimekta.test.providence.reflect.calculator.Calculator._calculate_response msg = (net.morimekta.test.providence.reflect.calculator.Calculator._calculate_response) resp.getMessage();
            if (msg.unionField() != null) {
                switch (msg.unionField()) {
                    case CE:
                        throw msg.getCe();
                    case SUCCESS:
                        return msg.getSuccess();
                }
            }

            throw new net.morimekta.providence.PApplicationException("Result field for calculator.Calculator.calculate() not set",
                                                                     net.morimekta.providence.PApplicationExceptionType.MISSING_RESULT);
        }

        @Override
        public void iamalive()
                throws java.io.IOException {
            net.morimekta.test.providence.reflect.calculator.Calculator._iamalive_request._Builder rq = net.morimekta.test.providence.reflect.calculator.Calculator._iamalive_request.builder();

            net.morimekta.providence.PServiceCall call = new net.morimekta.providence.PServiceCall<>("iamalive", net.morimekta.providence.PServiceCallType.ONEWAY, getNextSequenceId(), rq.build());
            handler.handleCall(call, Calculator.kDescriptor);
        }

        @Override
        public void ping()
                throws java.io.IOException {
            net.morimekta.test.providence.reflect.calculator.Calculator._ping_request._Builder rq = net.morimekta.test.providence.reflect.calculator.Calculator._ping_request.builder();

            net.morimekta.providence.PServiceCall call = new net.morimekta.providence.PServiceCall<>("ping", net.morimekta.providence.PServiceCallType.CALL, getNextSequenceId(), rq.build());
            net.morimekta.providence.PServiceCall resp = handler.handleCall(call, Calculator.kDescriptor);

            if (resp.getType() == net.morimekta.providence.PServiceCallType.EXCEPTION) {
                throw (net.morimekta.providence.PApplicationException) resp.getMessage();
            }

            net.morimekta.test.providence.reflect.calculator.Calculator._ping_response msg = (net.morimekta.test.providence.reflect.calculator.Calculator._ping_response) resp.getMessage();
            if (msg.unionField() != null) {
                switch (msg.unionField()) {
                    case SUCCESS:
                        return;
                }
            }

            throw new net.morimekta.providence.PApplicationException("Result field for calculator.Calculator.ping() not set",
                                                                     net.morimekta.providence.PApplicationExceptionType.MISSING_RESULT);
        }
    }

    public static class Processor implements net.morimekta.providence.PProcessor {
        private final Iface impl;
        public Processor(Iface impl) {
            this.impl = impl;
        }

        @Override
        public net.morimekta.providence.descriptor.PService getDescriptor() {
            return kDescriptor;
        }

        @Override
        public <Request extends net.morimekta.providence.PMessage<Request, RequestField>,
                Response extends net.morimekta.providence.PMessage<Response, ResponseField>,
                RequestField extends net.morimekta.providence.descriptor.PField,
                ResponseField extends net.morimekta.providence.descriptor.PField>
        net.morimekta.providence.PServiceCall<Response, ResponseField> handleCall(
                net.morimekta.providence.PServiceCall<Request, RequestField> call,
                net.morimekta.providence.descriptor.PService service)
                throws java.io.IOException,
                       net.morimekta.providence.serializer.SerializerException {
            switch(call.getMethod()) {
                case "calculate": {
                    net.morimekta.test.providence.reflect.calculator.Calculator._calculate_response._Builder rsp = net.morimekta.test.providence.reflect.calculator.Calculator._calculate_response.builder();
                    try {
                        net.morimekta.test.providence.reflect.calculator.Calculator._calculate_request req = (net.morimekta.test.providence.reflect.calculator.Calculator._calculate_request) call.getMessage();
                        net.morimekta.test.providence.reflect.calculator.Operand result =
                                impl.calculate(req.getOp());
                        rsp.setSuccess(result);
                    } catch (net.morimekta.test.providence.reflect.calculator.CalculateException e) {
                        rsp.setCe(e);
                    }
                    net.morimekta.providence.PServiceCall reply =
                            new net.morimekta.providence.PServiceCall<>(call.getMethod(),
                                                                        net.morimekta.providence.PServiceCallType.REPLY,
                                                                        call.getSequence(),
                                                                        rsp.build());
                    return reply;
                }
                case "iamalive": {
                    net.morimekta.test.providence.reflect.calculator.Calculator._iamalive_request req = (net.morimekta.test.providence.reflect.calculator.Calculator._iamalive_request) call.getMessage();
                    impl.iamalive();
                    return null;
                }
                case "ping": {
                    net.morimekta.test.providence.reflect.calculator.Calculator._ping_response._Builder rsp = net.morimekta.test.providence.reflect.calculator.Calculator._ping_response.builder();
                    net.morimekta.test.providence.reflect.calculator.Calculator._ping_request req = (net.morimekta.test.providence.reflect.calculator.Calculator._ping_request) call.getMessage();
                    impl.ping();
                    rsp.setSuccess();
                    net.morimekta.providence.PServiceCall reply =
                            new net.morimekta.providence.PServiceCall<>(call.getMethod(),
                                                                        net.morimekta.providence.PServiceCallType.REPLY,
                                                                        call.getSequence(),
                                                                        rsp.build());
                    return reply;
                }
                default: {
                    net.morimekta.providence.PApplicationException ex =
                            new net.morimekta.providence.PApplicationException(
                                    "Unknown method \"" + call.getMethod() + "\" on calculator.Calculator.",
                                    net.morimekta.providence.PApplicationExceptionType.UNKNOWN_METHOD);
                    net.morimekta.providence.PServiceCall reply =
                            new net.morimekta.providence.PServiceCall(call.getMethod(),
                                                                      net.morimekta.providence.PServiceCallType.EXCEPTION,
                                                                      call.getSequence(),
                                                                      ex);
                    return reply;
                }
            }
        }
    }

    public enum Method implements net.morimekta.providence.descriptor.PServiceMethod {
        CALCULATE("calculate", false, net.morimekta.test.providence.reflect.calculator.Calculator._calculate_request.kDescriptor, net.morimekta.test.providence.reflect.calculator.Calculator._calculate_response.kDescriptor),
        IAMALIVE("iamalive", true, net.morimekta.test.providence.reflect.calculator.Calculator._iamalive_request.kDescriptor, null),
        PING("ping", false, net.morimekta.test.providence.reflect.calculator.Calculator._ping_request.kDescriptor, net.morimekta.test.providence.reflect.calculator.Calculator._ping_response.kDescriptor),
        ;

        private final String name;
        private final boolean oneway;
        private final net.morimekta.providence.descriptor.PStructDescriptor request;
        private final net.morimekta.providence.descriptor.PUnionDescriptor response;

        private Method(String name, boolean oneway, net.morimekta.providence.descriptor.PStructDescriptor request, net.morimekta.providence.descriptor.PUnionDescriptor response) {
            this.name = name;
            this.oneway = oneway;
            this.request = request;
            this.response = response;
        }

        public String getName() {
            return name;
        }

        public boolean isOneway() {
            return oneway;
        }

        public net.morimekta.providence.descriptor.PStructDescriptor getRequestType() {
            return request;
        }

        public net.morimekta.providence.descriptor.PUnionDescriptor getResponseType() {
            return response;
        }

        public static Method findByName(String name) {
            switch (name) {
                case "calculate": return CALCULATE;
                case "iamalive": return IAMALIVE;
                case "ping": return PING;
            }
            return null;
        }
        @javax.annotation.Nonnull
        public static Method methodForName(String name) {
            Method method = findByName(name);
            if (method == null) {
                throw new IllegalArgumentException("No such method \"" + name + "\" in service calculator.Calculator");
            }
            return method;
        }
    }

    private static class _Descriptor extends net.morimekta.providence.descriptor.PService {
        private _Descriptor() {
            super("calculator", "Calculator", null, Method.values());
        }

        @Override
        public Method getMethod(String name) {
            return Method.findByName(name);
        }
    }

    private static class _Provider implements net.morimekta.providence.descriptor.PServiceProvider {
        @Override
        public net.morimekta.providence.descriptor.PService getService() {
            return kDescriptor;
        }
    }

    public static final net.morimekta.providence.descriptor.PService kDescriptor = new _Descriptor();

    public static net.morimekta.providence.descriptor.PServiceProvider provider() {
        return new _Provider();
    }

    // type --> Calculator.calculate.request
    @SuppressWarnings("unused")
    @javax.annotation.Generated("providence-maven-plugin")
    @javax.annotation.concurrent.Immutable
    protected static class _calculate_request
            implements net.morimekta.providence.PMessage<_calculate_request,_calculate_request._Field>,
                       Comparable<_calculate_request>,
                       java.io.Serializable,
                       net.morimekta.providence.serializer.binary.BinaryWriter {
        private final static long serialVersionUID = 5385883517742336295L;

        private final transient net.morimekta.test.providence.reflect.calculator.Operation mOp;

        private volatile transient int tHashCode;

        // Transient object used during java deserialization.
        private transient _calculate_request tSerializeInstance;

        private _calculate_request(_Builder builder) {
            mOp = builder.mOp_builder != null ? builder.mOp_builder.build() : builder.mOp;
        }

        public boolean hasOp() {
            return mOp != null;
        }

        /**
         * @return The field value
         */
        public net.morimekta.test.providence.reflect.calculator.Operation getOp() {
            return mOp;
        }

        @Override
        public boolean has(int key) {
            switch(key) {
                case 1: return mOp != null;
                default: return false;
            }
        }

        @Override
        @SuppressWarnings("unchecked")
        public <T> T get(int key) {
            switch(key) {
                case 1: return (T) mOp;
                default: return null;
            }
        }

        @Override
        public boolean equals(Object o) {
            if (o == this) return true;
            if (o == null || !o.getClass().equals(getClass())) return false;
            _calculate_request other = (_calculate_request) o;
            return java.util.Objects.equals(mOp, other.mOp);
        }

        @Override
        public int hashCode() {
            if (tHashCode == 0) {
                tHashCode = java.util.Objects.hash(
                        _calculate_request.class,
                        _Field.OP, mOp);
            }
            return tHashCode;
        }

        @Override
        public String toString() {
            return "calculator.Calculator.calculate.request" + asString();
        }

        @Override
        @javax.annotation.Nonnull
        public String asString() {
            StringBuilder out = new StringBuilder();
            out.append("{");

            if (hasOp()) {
                out.append("op:")
                   .append(mOp.asString());
            }
            out.append('}');
            return out.toString();
        }

        @Override
        public int compareTo(_calculate_request other) {
            int c;

            c = Boolean.compare(mOp != null, other.mOp != null);
            if (c != 0) return c;
            if (mOp != null) {
                c = mOp.compareTo(other.mOp);
                if (c != 0) return c;
            }

            return 0;
        }

        private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException {
            oos.defaultWriteObject();
            net.morimekta.providence.serializer.BinarySerializer serializer = new net.morimekta.providence.serializer.BinarySerializer(false);
            serializer.serialize(oos, this);
        }

        private void readObject(java.io.ObjectInputStream ois)
                throws java.io.IOException, ClassNotFoundException {
            ois.defaultReadObject();
            net.morimekta.providence.serializer.BinarySerializer serializer = new net.morimekta.providence.serializer.BinarySerializer(false);
            tSerializeInstance = serializer.deserialize(ois, kDescriptor);
        }

        private Object readResolve() throws java.io.ObjectStreamException {
            return tSerializeInstance;
        }

        @Override
        public int writeBinary(net.morimekta.util.io.BigEndianBinaryWriter writer) throws java.io.IOException {
            int length = 0;

            if (hasOp()) {
                length += writer.writeByte((byte) 12);
                length += writer.writeShort((short) 1);
                length += net.morimekta.providence.serializer.binary.BinaryFormatUtils.writeMessage(writer, mOp);
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
            OP(1, net.morimekta.providence.descriptor.PRequirement.DEFAULT, "op", net.morimekta.test.providence.reflect.calculator.Operation.provider(), null),
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
                    case 1: return _Field.OP;
                }
                return null;
            }

            /**
             * @param name Field name
             * @return The named field or null
             */
            public static _Field findByName(String name) {
                switch (name) {
                    case "op": return _Field.OP;
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
                    throw new IllegalArgumentException("No such field id " + id + " in calculator.Calculator.calculate.request");
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
                    throw new IllegalArgumentException("No such field \"" + name + "\" in calculator.Calculator.calculate.request");
                }
                return field;
            }

        }

        @javax.annotation.Nonnull
        public static net.morimekta.providence.descriptor.PStructDescriptorProvider<_calculate_request,_Field> provider() {
            return new _Provider();
        }

        @Override
        @javax.annotation.Nonnull
        public net.morimekta.providence.descriptor.PStructDescriptor<_calculate_request,_Field> descriptor() {
            return kDescriptor;
        }

        public static final net.morimekta.providence.descriptor.PStructDescriptor<_calculate_request,_Field> kDescriptor;

        private static class _Descriptor
                extends net.morimekta.providence.descriptor.PStructDescriptor<_calculate_request,_Field> {
            public _Descriptor() {
                super("calculator", "Calculator.calculate.request", _Builder::new, false);
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

        private final static class _Provider extends net.morimekta.providence.descriptor.PStructDescriptorProvider<_calculate_request,_Field> {
            @Override
            public net.morimekta.providence.descriptor.PStructDescriptor<_calculate_request,_Field> descriptor() {
                return kDescriptor;
            }
        }

        /**
         * Make a calculator.Calculator.calculate.request builder.
         * @return The builder instance.
         */
        public static _Builder builder() {
            return new _Builder();
        }

        public static class _Builder
                extends net.morimekta.providence.PMessageBuilder<_calculate_request,_Field>
                implements net.morimekta.providence.serializer.binary.BinaryReader {
            private java.util.BitSet optionals;
            private java.util.BitSet modified;

            private net.morimekta.test.providence.reflect.calculator.Operation mOp;
            private net.morimekta.test.providence.reflect.calculator.Operation._Builder mOp_builder;

            /**
             * Make a calculator.Calculator.calculate.request builder.
             */
            public _Builder() {
                optionals = new java.util.BitSet(1);
                modified = new java.util.BitSet(1);
            }

            /**
             * Make a mutating builder off a base calculator.Calculator.calculate.request.
             *
             * @param base The base Calculator.calculate.request
             */
            public _Builder(_calculate_request base) {
                this();

                if (base.hasOp()) {
                    optionals.set(0);
                    mOp = base.mOp;
                }
            }

            @javax.annotation.Nonnull
            @Override
            public _Builder merge(_calculate_request from) {
                if (from.hasOp()) {
                    optionals.set(0);
                    modified.set(0);
                    if (mOp_builder != null) {
                        mOp_builder.merge(from.getOp());
                    } else if (mOp != null) {
                        mOp_builder = mOp.mutate().merge(from.getOp());
                        mOp = null;
                    } else {
                        mOp = from.getOp();
                    }
                }
                return this;
            }

            /**
             * Sets the value of op.
             *
             * @param value The new value
             * @return The builder
             */
            @javax.annotation.Nonnull
            public _Builder setOp(net.morimekta.test.providence.reflect.calculator.Operation value) {
                if (value == null) {
                    return clearOp();
                }

                optionals.set(0);
                modified.set(0);
                mOp = value;
                mOp_builder = null;
                return this;
            }

            /**
             * Sets the value of op.
             *
             * @param builder builder for the new value
             * @return The builder
             */
            @javax.annotation.Nonnull
            public _Builder setOp(net.morimekta.test.providence.reflect.calculator.Operation._Builder builder) {
              return setOp(builder == null ? null : builder.build());
            }

            /**
             * Checks for presence of the op field.
             *
             * @return True if op has been set.
             */
            public boolean isSetOp() {
                return optionals.get(0);
            }

            /**
             * Checks if op has been modified since the _Builder was created.
             *
             * @return True if op has been modified.
             */
            public boolean isModifiedOp() {
                return modified.get(0);
            }

            /**
             * Clears the op field.
             *
             * @return The builder
             */
            @javax.annotation.Nonnull
            public _Builder clearOp() {
                optionals.clear(0);
                modified.set(0);
                mOp = null;
                mOp_builder = null;
                return this;
            }

            /**
             * Gets the builder for the contained op.
             *
             * @return The field builder
             */
            @javax.annotation.Nonnull
            public net.morimekta.test.providence.reflect.calculator.Operation._Builder mutableOp() {
                optionals.set(0);
                modified.set(0);

                if (mOp != null) {
                    mOp_builder = mOp.mutate();
                    mOp = null;
                } else if (mOp_builder == null) {
                    mOp_builder = net.morimekta.test.providence.reflect.calculator.Operation.builder();
                }
                return mOp_builder;
            }

            /**
             * Gets the value for the contained op.
             *
             * @return The field value
             */
            public net.morimekta.test.providence.reflect.calculator.Operation getOp() {

                if (mOp_builder != null) {
                    return mOp_builder.build();
                }
                return mOp;
            }

            @Override
            public boolean equals(Object o) {
                if (o == this) return true;
                if (o == null || !o.getClass().equals(getClass())) return false;
                _calculate_request._Builder other = (_calculate_request._Builder) o;
                return java.util.Objects.equals(optionals, other.optionals) &&
                       java.util.Objects.equals(getOp(), other.getOp());
            }

            @Override
            public int hashCode() {
                return java.util.Objects.hash(
                        _calculate_request.class, optionals,
                        _Field.OP, getOp());
            }

            @Override
            @SuppressWarnings("unchecked")
            public net.morimekta.providence.PMessageBuilder mutator(int key) {
                switch (key) {
                    case 1: return mutableOp();
                    default: throw new IllegalArgumentException("Not a message field ID: " + key);
                }
            }

            @javax.annotation.Nonnull
            @Override
            @SuppressWarnings("unchecked")
            public _Builder set(int key, Object value) {
                if (value == null) return clear(key);
                switch (key) {
                    case 1: setOp((net.morimekta.test.providence.reflect.calculator.Operation) value); break;
                    default: break;
                }
                return this;
            }

            @Override
            public boolean isSet(int key) {
                switch (key) {
                    case 1: return optionals.get(0);
                    default: break;
                }
                return false;
            }

            @Override
            public boolean isModified(int key) {
                switch (key) {
                    case 1: return modified.get(0);
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
                    case 1: clearOp(); break;
                    default: break;
                }
                return this;
            }

            @Override
            public boolean valid() {
                return true;
            }

            @Override
            public void validate() {
            }

            @javax.annotation.Nonnull
            @Override
            public net.morimekta.providence.descriptor.PStructDescriptor<_calculate_request,_Field> descriptor() {
                return kDescriptor;
            }

            @Override
            public void readBinary(net.morimekta.util.io.BigEndianBinaryReader reader, boolean strict) throws java.io.IOException {
                byte type = reader.expectByte();
                while (type != 0) {
                    int field = reader.expectShort();
                    switch (field) {
                        case 1: {
                            if (type == 12) {
                                mOp = net.morimekta.providence.serializer.binary.BinaryFormatUtils.readMessage(reader, net.morimekta.test.providence.reflect.calculator.Operation.kDescriptor, strict);
                                optionals.set(0);
                            } else {
                                throw new net.morimekta.providence.serializer.SerializerException("Wrong type " + net.morimekta.providence.serializer.binary.BinaryType.asString(type) + " for calculator.Calculator.calculate.request.op, should be struct(12)");
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
            public _calculate_request build() {
                return new _calculate_request(this);
            }
        }
    }

    // type <-- Calculator.calculate.response
    @SuppressWarnings("unused")
    @javax.annotation.Generated("providence-maven-plugin")
    @javax.annotation.concurrent.Immutable
    protected static class _calculate_response
            implements net.morimekta.providence.PUnion<_calculate_response,_calculate_response._Field>,
                       Comparable<_calculate_response>,
                       java.io.Serializable,
                       net.morimekta.providence.serializer.binary.BinaryWriter {
        private final static long serialVersionUID = -1787619653444046051L;

        private final transient net.morimekta.test.providence.reflect.calculator.Operand mSuccess;
        private final transient net.morimekta.test.providence.reflect.calculator.CalculateException mCe;

        private transient final _Field tUnionField;

        private volatile transient int tHashCode;

        // Transient object used during java deserialization.
        private transient _calculate_response tSerializeInstance;

        /**
         * @param value The union value
         * @return The created union.
         */
        public static _calculate_response withSuccess(net.morimekta.test.providence.reflect.calculator.Operand value) {
            return new _Builder().setSuccess(value).build();
        }

        /**
         * @param value The union value
         * @return The created union.
         */
        public static _calculate_response withSuccess(net.morimekta.test.providence.reflect.calculator.Operand._Builder value) {
            return withSuccess(value == null ? null : value.build());
        }

        /**
         * @param value The union value
         * @return The created union.
         */
        public static _calculate_response withCe(net.morimekta.test.providence.reflect.calculator.CalculateException value) {
            return new _Builder().setCe(value).build();
        }

        /**
         * @param value The union value
         * @return The created union.
         */
        public static _calculate_response withCe(net.morimekta.test.providence.reflect.calculator.CalculateException._Builder value) {
            return withCe(value == null ? null : value.build());
        }

        private _calculate_response(_Builder builder) {
            tUnionField = builder.tUnionField;

            mSuccess = tUnionField != _Field.SUCCESS
                    ? null
                    : builder.mSuccess_builder != null ? builder.mSuccess_builder.build() : builder.mSuccess;
            mCe = tUnionField != _Field.CE
                    ? null
                    : builder.mCe_builder != null ? builder.mCe_builder.build() : builder.mCe;
        }

        public boolean hasSuccess() {
            return tUnionField == _Field.SUCCESS && mSuccess != null;
        }

        /**
         * @return The field value
         */
        public net.morimekta.test.providence.reflect.calculator.Operand getSuccess() {
            return mSuccess;
        }

        public boolean hasCe() {
            return tUnionField == _Field.CE && mCe != null;
        }

        /**
         * @return The field value
         */
        public net.morimekta.test.providence.reflect.calculator.CalculateException getCe() {
            return mCe;
        }

        @Override
        public boolean has(int key) {
            switch(key) {
                case 0: return tUnionField == _Field.SUCCESS;
                case 1: return tUnionField == _Field.CE;
                default: return false;
            }
        }

        @Override
        @SuppressWarnings("unchecked")
        public <T> T get(int key) {
            switch(key) {
                case 0: return (T) mSuccess;
                case 1: return (T) mCe;
                default: return null;
            }
        }

        @Override
        public boolean unionFieldIsSet() {
            return tUnionField != null;
        }

        @Override
        @javax.annotation.Nonnull
        public _Field unionField() {
            if (tUnionField == null) throw new IllegalStateException("No union field set in calculator.Calculator.calculate.response");
            return tUnionField;
        }

        @Override
        public boolean equals(Object o) {
            if (o == this) return true;
            if (o == null || !o.getClass().equals(getClass())) return false;
            _calculate_response other = (_calculate_response) o;
            return java.util.Objects.equals(tUnionField, other.tUnionField) &&
                   java.util.Objects.equals(mSuccess, other.mSuccess) &&
                   java.util.Objects.equals(mCe, other.mCe);
        }

        @Override
        public int hashCode() {
            if (tHashCode == 0) {
                tHashCode = java.util.Objects.hash(
                        _calculate_response.class,
                        _Field.SUCCESS, mSuccess,
                        _Field.CE, mCe);
            }
            return tHashCode;
        }

        @Override
        public String toString() {
            return "calculator.Calculator.calculate.response" + asString();
        }

        @Override
        @javax.annotation.Nonnull
        public String asString() {
            StringBuilder out = new StringBuilder();
            out.append("{");

            switch (tUnionField) {
                case SUCCESS: {
                    out.append("success:")
                       .append(mSuccess.asString());
                    break;
                }
                case CE: {
                    out.append("ce:")
                       .append(mCe.asString());
                    break;
                }
            }
            out.append('}');
            return out.toString();
        }

        @Override
        public int compareTo(_calculate_response other) {
            if (tUnionField == null || other.tUnionField == null) return Boolean.compare(tUnionField != null, other.tUnionField != null);
            int c = tUnionField.compareTo(other.tUnionField);
            if (c != 0) return c;

            switch (tUnionField) {
                case SUCCESS:
                    return mSuccess.compareTo(other.mSuccess);
                case CE:
                    return mCe.compareTo(other.mCe);
                default: return 0;
            }
        }

        private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException {
            oos.defaultWriteObject();
            net.morimekta.providence.serializer.BinarySerializer serializer = new net.morimekta.providence.serializer.BinarySerializer(false);
            serializer.serialize(oos, this);
        }

        private void readObject(java.io.ObjectInputStream ois)
                throws java.io.IOException, ClassNotFoundException {
            ois.defaultReadObject();
            net.morimekta.providence.serializer.BinarySerializer serializer = new net.morimekta.providence.serializer.BinarySerializer(false);
            tSerializeInstance = serializer.deserialize(ois, kDescriptor);
        }

        private Object readResolve() throws java.io.ObjectStreamException {
            return tSerializeInstance;
        }

        @Override
        public int writeBinary(net.morimekta.util.io.BigEndianBinaryWriter writer) throws java.io.IOException {
            int length = 0;

            if (tUnionField != null) {
                switch (tUnionField) {
                    case SUCCESS: {
                        length += writer.writeByte((byte) 12);
                        length += writer.writeShort((short) 0);
                        length += net.morimekta.providence.serializer.binary.BinaryFormatUtils.writeMessage(writer, mSuccess);
                        break;
                    }
                    case CE: {
                        length += writer.writeByte((byte) 12);
                        length += writer.writeShort((short) 1);
                        length += net.morimekta.providence.serializer.binary.BinaryFormatUtils.writeMessage(writer, mCe);
                        break;
                    }
                    default: break;
                }
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
            SUCCESS(0, net.morimekta.providence.descriptor.PRequirement.OPTIONAL, "success", net.morimekta.test.providence.reflect.calculator.Operand.provider(), null),
            CE(1, net.morimekta.providence.descriptor.PRequirement.OPTIONAL, "ce", net.morimekta.test.providence.reflect.calculator.CalculateException.provider(), null),
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
                    case 0: return _Field.SUCCESS;
                    case 1: return _Field.CE;
                }
                return null;
            }

            /**
             * @param name Field name
             * @return The named field or null
             */
            public static _Field findByName(String name) {
                switch (name) {
                    case "success": return _Field.SUCCESS;
                    case "ce": return _Field.CE;
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
                    throw new IllegalArgumentException("No such field id " + id + " in calculator.Calculator.calculate.response");
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
                    throw new IllegalArgumentException("No such field \"" + name + "\" in calculator.Calculator.calculate.response");
                }
                return field;
            }

        }

        @javax.annotation.Nonnull
        public static net.morimekta.providence.descriptor.PUnionDescriptorProvider<_calculate_response,_Field> provider() {
            return new _Provider();
        }

        @Override
        @javax.annotation.Nonnull
        public net.morimekta.providence.descriptor.PUnionDescriptor<_calculate_response,_Field> descriptor() {
            return kDescriptor;
        }

        public static final net.morimekta.providence.descriptor.PUnionDescriptor<_calculate_response,_Field> kDescriptor;

        private static class _Descriptor
                extends net.morimekta.providence.descriptor.PUnionDescriptor<_calculate_response,_Field> {
            public _Descriptor() {
                super("calculator", "Calculator.calculate.response", _Builder::new, false);
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

        private final static class _Provider extends net.morimekta.providence.descriptor.PUnionDescriptorProvider<_calculate_response,_Field> {
            @Override
            public net.morimekta.providence.descriptor.PUnionDescriptor<_calculate_response,_Field> descriptor() {
                return kDescriptor;
            }
        }

        /**
         * Make a calculator.Calculator.calculate.response builder.
         * @return The builder instance.
         */
        public static _Builder builder() {
            return new _Builder();
        }

        public static class _Builder
                extends net.morimekta.providence.PMessageBuilder<_calculate_response,_Field>
                implements net.morimekta.providence.serializer.binary.BinaryReader {
            private _Field tUnionField;

            private boolean modified;

            private net.morimekta.test.providence.reflect.calculator.Operand mSuccess;
            private net.morimekta.test.providence.reflect.calculator.Operand._Builder mSuccess_builder;
            private net.morimekta.test.providence.reflect.calculator.CalculateException mCe;
            private net.morimekta.test.providence.reflect.calculator.CalculateException._Builder mCe_builder;

            /**
             * Make a calculator.Calculator.calculate.response builder.
             */
            public _Builder() {
                modified = false;
            }

            /**
             * Make a mutating builder off a base calculator.Calculator.calculate.response.
             *
             * @param base The base Calculator.calculate.response
             */
            public _Builder(_calculate_response base) {
                this();

                tUnionField = base.tUnionField;

                mSuccess = base.mSuccess;
                mCe = base.mCe;
            }

            @javax.annotation.Nonnull
            @Override
            public _Builder merge(_calculate_response from) {
                if (!from.unionFieldIsSet()) {
                    return this;
                }

                switch (from.unionField()) {
                    case SUCCESS: {
                        if (tUnionField == _Field.SUCCESS && mSuccess != null) {
                            mSuccess = mSuccess.mutate().merge(from.getSuccess()).build();
                        } else {
                            setSuccess(from.getSuccess());
                        }
                        break;
                    }
                    case CE: {
                        if (tUnionField == _Field.CE && mCe != null) {
                            mCe = mCe.mutate().merge(from.getCe()).build();
                        } else {
                            setCe(from.getCe());
                        }
                        break;
                    }
                }
                return this;
            }

            /**
             * Sets the value of success.
             *
             * @param value The new value
             * @return The builder
             */
            @javax.annotation.Nonnull
            public _Builder setSuccess(net.morimekta.test.providence.reflect.calculator.Operand value) {
                if (value == null) {
                    return clearSuccess();
                }

                tUnionField = _Field.SUCCESS;
                modified = true;
                mSuccess = value;
                mSuccess_builder = null;
                return this;
            }

            /**
             * Sets the value of success.
             *
             * @param builder builder for the new value
             * @return The builder
             */
            @javax.annotation.Nonnull
            public _Builder setSuccess(net.morimekta.test.providence.reflect.calculator.Operand._Builder builder) {
              return setSuccess(builder == null ? null : builder.build());
            }

            /**
             * Checks for presence of the success field.
             *
             * @return True if success has been set.
             */
            public boolean isSetSuccess() {
                return tUnionField == _Field.SUCCESS;
            }

            /**
             * Clears the success field.
             *
             * @return The builder
             */
            @javax.annotation.Nonnull
            public _Builder clearSuccess() {
                if (tUnionField == _Field.SUCCESS) tUnionField = null;
                modified = true;
                mSuccess = null;
                mSuccess_builder = null;
                return this;
            }

            /**
             * Gets the builder for the contained success.
             *
             * @return The field builder
             */
            @javax.annotation.Nonnull
            public net.morimekta.test.providence.reflect.calculator.Operand._Builder mutableSuccess() {
                if (tUnionField != _Field.SUCCESS) {
                    clearSuccess();
                }
                tUnionField = _Field.SUCCESS;
                modified = true;

                if (mSuccess != null) {
                    mSuccess_builder = mSuccess.mutate();
                    mSuccess = null;
                } else if (mSuccess_builder == null) {
                    mSuccess_builder = net.morimekta.test.providence.reflect.calculator.Operand.builder();
                }
                return mSuccess_builder;
            }

            /**
             * Gets the value for the contained success.
             *
             * @return The field value
             */
            public net.morimekta.test.providence.reflect.calculator.Operand getSuccess() {
                if (tUnionField != _Field.SUCCESS) {
                    return null;
                }

                if (mSuccess_builder != null) {
                    return mSuccess_builder.build();
                }
                return mSuccess;
            }

            /**
             * Sets the value of ce.
             *
             * @param value The new value
             * @return The builder
             */
            @javax.annotation.Nonnull
            public _Builder setCe(net.morimekta.test.providence.reflect.calculator.CalculateException value) {
                if (value == null) {
                    return clearCe();
                }

                tUnionField = _Field.CE;
                modified = true;
                mCe = value;
                mCe_builder = null;
                return this;
            }

            /**
             * Sets the value of ce.
             *
             * @param builder builder for the new value
             * @return The builder
             */
            @javax.annotation.Nonnull
            public _Builder setCe(net.morimekta.test.providence.reflect.calculator.CalculateException._Builder builder) {
              return setCe(builder == null ? null : builder.build());
            }

            /**
             * Checks for presence of the ce field.
             *
             * @return True if ce has been set.
             */
            public boolean isSetCe() {
                return tUnionField == _Field.CE;
            }

            /**
             * Clears the ce field.
             *
             * @return The builder
             */
            @javax.annotation.Nonnull
            public _Builder clearCe() {
                if (tUnionField == _Field.CE) tUnionField = null;
                modified = true;
                mCe = null;
                mCe_builder = null;
                return this;
            }

            /**
             * Gets the builder for the contained ce.
             *
             * @return The field builder
             */
            @javax.annotation.Nonnull
            public net.morimekta.test.providence.reflect.calculator.CalculateException._Builder mutableCe() {
                if (tUnionField != _Field.CE) {
                    clearCe();
                }
                tUnionField = _Field.CE;
                modified = true;

                if (mCe != null) {
                    mCe_builder = mCe.mutate();
                    mCe = null;
                } else if (mCe_builder == null) {
                    mCe_builder = net.morimekta.test.providence.reflect.calculator.CalculateException.builder();
                }
                return mCe_builder;
            }

            /**
             * Gets the value for the contained ce.
             *
             * @return The field value
             */
            public net.morimekta.test.providence.reflect.calculator.CalculateException getCe() {
                if (tUnionField != _Field.CE) {
                    return null;
                }

                if (mCe_builder != null) {
                    return mCe_builder.build();
                }
                return mCe;
            }

            /**
             * Checks if Calculator.calculate.response has been modified since the _Builder was created.
             *
             * @return True if Calculator.calculate.response has been modified.
             */
            public boolean isUnionModified() {
                return modified;
            }

            @Override
            public boolean equals(Object o) {
                if (o == this) return true;
                if (o == null || !o.getClass().equals(getClass())) return false;
                _calculate_response._Builder other = (_calculate_response._Builder) o;
                return java.util.Objects.equals(tUnionField, other.tUnionField) &&
                       java.util.Objects.equals(getSuccess(), other.getSuccess()) &&
                       java.util.Objects.equals(getCe(), other.getCe());
            }

            @Override
            public int hashCode() {
                return java.util.Objects.hash(
                        _calculate_response.class,
                        _Field.SUCCESS, getSuccess(),
                        _Field.CE, getCe());
            }

            @Override
            @SuppressWarnings("unchecked")
            public net.morimekta.providence.PMessageBuilder mutator(int key) {
                switch (key) {
                    case 0: return mutableSuccess();
                    case 1: return mutableCe();
                    default: throw new IllegalArgumentException("Not a message field ID: " + key);
                }
            }

            @javax.annotation.Nonnull
            @Override
            @SuppressWarnings("unchecked")
            public _Builder set(int key, Object value) {
                if (value == null) return clear(key);
                switch (key) {
                    case 0: setSuccess((net.morimekta.test.providence.reflect.calculator.Operand) value); break;
                    case 1: setCe((net.morimekta.test.providence.reflect.calculator.CalculateException) value); break;
                    default: break;
                }
                return this;
            }

            @Override
            public boolean isSet(int key) {
                switch (key) {
                    case 0: return tUnionField == _Field.SUCCESS;
                    case 1: return tUnionField == _Field.CE;
                    default: break;
                }
                return false;
            }

            @Override
            public boolean isModified(int key) {
                return modified;
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
                    case 0: clearSuccess(); break;
                    case 1: clearCe(); break;
                    default: break;
                }
                return this;
            }

            @Override
            public boolean valid() {
                if (tUnionField == null) {
                    return false;
                }

                switch (tUnionField) {
                    case SUCCESS: return mSuccess != null || mSuccess_builder != null;
                    case CE: return mCe != null || mCe_builder != null;
                    default: return true;
                }
            }

            @Override
            public void validate() {
                if (!valid()) {
                    throw new java.lang.IllegalStateException("No union field set in calculator.Calculator.calculate.response");
                }
            }

            @javax.annotation.Nonnull
            @Override
            public net.morimekta.providence.descriptor.PUnionDescriptor<_calculate_response,_Field> descriptor() {
                return kDescriptor;
            }

            @Override
            public void readBinary(net.morimekta.util.io.BigEndianBinaryReader reader, boolean strict) throws java.io.IOException {
                byte type = reader.expectByte();
                while (type != 0) {
                    int field = reader.expectShort();
                    switch (field) {
                        case 0: {
                            if (type == 12) {
                                mSuccess = net.morimekta.providence.serializer.binary.BinaryFormatUtils.readMessage(reader, net.morimekta.test.providence.reflect.calculator.Operand.kDescriptor, strict);
                                tUnionField = _Field.SUCCESS;
                            } else {
                                throw new net.morimekta.providence.serializer.SerializerException("Wrong type " + net.morimekta.providence.serializer.binary.BinaryType.asString(type) + " for calculator.Calculator.calculate.response.success, should be struct(12)");
                            }
                            break;
                        }
                        case 1: {
                            if (type == 12) {
                                mCe = net.morimekta.providence.serializer.binary.BinaryFormatUtils.readMessage(reader, net.morimekta.test.providence.reflect.calculator.CalculateException.kDescriptor, strict);
                                tUnionField = _Field.CE;
                            } else {
                                throw new net.morimekta.providence.serializer.SerializerException("Wrong type " + net.morimekta.providence.serializer.binary.BinaryType.asString(type) + " for calculator.Calculator.calculate.response.ce, should be struct(12)");
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
            public _calculate_response build() {
                return new _calculate_response(this);
            }
        }
    }

    // type --> Calculator.iamalive.request
    @SuppressWarnings("unused")
    @javax.annotation.Generated("providence-maven-plugin")
    @javax.annotation.concurrent.Immutable
    protected static class _iamalive_request
            implements net.morimekta.providence.PMessage<_iamalive_request,_iamalive_request._Field>,
                       Comparable<_iamalive_request>,
                       java.io.Serializable,
                       net.morimekta.providence.serializer.binary.BinaryWriter {
        private final static long serialVersionUID = -4737575730674403867L;


        private volatile transient int tHashCode;

        // Transient object used during java deserialization.
        private transient _iamalive_request tSerializeInstance;

        private _iamalive_request(_Builder builder) {
        }

        @Override
        public boolean has(int key) {
            switch(key) {
                default: return false;
            }
        }

        @Override
        @SuppressWarnings("unchecked")
        public <T> T get(int key) {
            switch(key) {
                default: return null;
            }
        }

        @Override
        public boolean equals(Object o) {
            if (o == this) return true;
            if (o == null || !o.getClass().equals(getClass())) return false;
            return true;
        }

        @Override
        public int hashCode() {
            if (tHashCode == 0) {
                tHashCode = java.util.Objects.hash(
                        _iamalive_request.class);
            }
            return tHashCode;
        }

        @Override
        public String toString() {
            return "calculator.Calculator.iamalive.request" + asString();
        }

        @Override
        @javax.annotation.Nonnull
        public String asString() {
            StringBuilder out = new StringBuilder();
            out.append("{");

            out.append('}');
            return out.toString();
        }

        @Override
        public int compareTo(_iamalive_request other) {
            int c;

            return 0;
        }

        private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException {
            oos.defaultWriteObject();
            net.morimekta.providence.serializer.BinarySerializer serializer = new net.morimekta.providence.serializer.BinarySerializer(false);
            serializer.serialize(oos, this);
        }

        private void readObject(java.io.ObjectInputStream ois)
                throws java.io.IOException, ClassNotFoundException {
            ois.defaultReadObject();
            net.morimekta.providence.serializer.BinarySerializer serializer = new net.morimekta.providence.serializer.BinarySerializer(false);
            tSerializeInstance = serializer.deserialize(ois, kDescriptor);
        }

        private Object readResolve() throws java.io.ObjectStreamException {
            return tSerializeInstance;
        }

        @Override
        public int writeBinary(net.morimekta.util.io.BigEndianBinaryWriter writer) throws java.io.IOException {
            int length = 0;

            length += writer.writeByte((byte) 0);
            return length;
        }

        @javax.annotation.Nonnull
        @Override
        public _Builder mutate() {
            return new _Builder(this);
        }

        public enum _Field implements net.morimekta.providence.descriptor.PField {
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
                }
                return null;
            }

            /**
             * @param name Field name
             * @return The named field or null
             */
            public static _Field findByName(String name) {
                switch (name) {
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
                    throw new IllegalArgumentException("No such field id " + id + " in calculator.Calculator.iamalive.request");
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
                    throw new IllegalArgumentException("No such field \"" + name + "\" in calculator.Calculator.iamalive.request");
                }
                return field;
            }

        }

        @javax.annotation.Nonnull
        public static net.morimekta.providence.descriptor.PStructDescriptorProvider<_iamalive_request,_Field> provider() {
            return new _Provider();
        }

        @Override
        @javax.annotation.Nonnull
        public net.morimekta.providence.descriptor.PStructDescriptor<_iamalive_request,_Field> descriptor() {
            return kDescriptor;
        }

        public static final net.morimekta.providence.descriptor.PStructDescriptor<_iamalive_request,_Field> kDescriptor;

        private static class _Descriptor
                extends net.morimekta.providence.descriptor.PStructDescriptor<_iamalive_request,_Field> {
            public _Descriptor() {
                super("calculator", "Calculator.iamalive.request", _Builder::new, true);
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

        private final static class _Provider extends net.morimekta.providence.descriptor.PStructDescriptorProvider<_iamalive_request,_Field> {
            @Override
            public net.morimekta.providence.descriptor.PStructDescriptor<_iamalive_request,_Field> descriptor() {
                return kDescriptor;
            }
        }

        /**
         * Make a calculator.Calculator.iamalive.request builder.
         * @return The builder instance.
         */
        public static _Builder builder() {
            return new _Builder();
        }

        public static class _Builder
                extends net.morimekta.providence.PMessageBuilder<_iamalive_request,_Field>
                implements net.morimekta.providence.serializer.binary.BinaryReader {
            private java.util.BitSet optionals;
            private java.util.BitSet modified;

            /**
             * Make a calculator.Calculator.iamalive.request builder.
             */
            public _Builder() {
                optionals = new java.util.BitSet(0);
                modified = new java.util.BitSet(0);
            }

            /**
             * Make a mutating builder off a base calculator.Calculator.iamalive.request.
             *
             * @param base The base Calculator.iamalive.request
             */
            public _Builder(_iamalive_request base) {
                this();

            }

            @javax.annotation.Nonnull
            @Override
            public _Builder merge(_iamalive_request from) {
                return this;
            }

            @Override
            public boolean equals(Object o) {
                if (o == this) return true;
                if (o == null || !o.getClass().equals(getClass())) return false;
                return true;
            }

            @Override
            public int hashCode() {
                return java.util.Objects.hash(
                        _iamalive_request.class, optionals);
            }

            @Override
            @SuppressWarnings("unchecked")
            public net.morimekta.providence.PMessageBuilder mutator(int key) {
                switch (key) {
                    default: throw new IllegalArgumentException("Not a message field ID: " + key);
                }
            }

            @javax.annotation.Nonnull
            @Override
            @SuppressWarnings("unchecked")
            public _Builder set(int key, Object value) {
                if (value == null) return clear(key);
                switch (key) {
                    default: break;
                }
                return this;
            }

            @Override
            public boolean isSet(int key) {
                switch (key) {
                    default: break;
                }
                return false;
            }

            @Override
            public boolean isModified(int key) {
                switch (key) {
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
                    default: break;
                }
                return this;
            }

            @Override
            public boolean valid() {
                return true;
            }

            @Override
            public void validate() {
            }

            @javax.annotation.Nonnull
            @Override
            public net.morimekta.providence.descriptor.PStructDescriptor<_iamalive_request,_Field> descriptor() {
                return kDescriptor;
            }

            @Override
            public void readBinary(net.morimekta.util.io.BigEndianBinaryReader reader, boolean strict) throws java.io.IOException {
                byte type = reader.expectByte();
                while (type != 0) {
                    int field = reader.expectShort();
                    switch (field) {
                        default: {
                            net.morimekta.providence.serializer.binary.BinaryFormatUtils.readFieldValue(reader, new net.morimekta.providence.serializer.binary.BinaryFormatUtils.FieldInfo(field, type), null, false);
                            break;
                        }
                    }
                    type = reader.expectByte();
                }
            }

            @Override
            public _iamalive_request build() {
                return new _iamalive_request(this);
            }
        }
    }

    // type --> Calculator.ping.request
    @SuppressWarnings("unused")
    @javax.annotation.Generated("providence-maven-plugin")
    @javax.annotation.concurrent.Immutable
    protected static class _ping_request
            implements net.morimekta.providence.PMessage<_ping_request,_ping_request._Field>,
                       Comparable<_ping_request>,
                       java.io.Serializable,
                       net.morimekta.providence.serializer.binary.BinaryWriter {
        private final static long serialVersionUID = -4600906282490783449L;


        private volatile transient int tHashCode;

        // Transient object used during java deserialization.
        private transient _ping_request tSerializeInstance;

        private _ping_request(_Builder builder) {
        }

        @Override
        public boolean has(int key) {
            switch(key) {
                default: return false;
            }
        }

        @Override
        @SuppressWarnings("unchecked")
        public <T> T get(int key) {
            switch(key) {
                default: return null;
            }
        }

        @Override
        public boolean equals(Object o) {
            if (o == this) return true;
            if (o == null || !o.getClass().equals(getClass())) return false;
            return true;
        }

        @Override
        public int hashCode() {
            if (tHashCode == 0) {
                tHashCode = java.util.Objects.hash(
                        _ping_request.class);
            }
            return tHashCode;
        }

        @Override
        public String toString() {
            return "calculator.Calculator.ping.request" + asString();
        }

        @Override
        @javax.annotation.Nonnull
        public String asString() {
            StringBuilder out = new StringBuilder();
            out.append("{");

            out.append('}');
            return out.toString();
        }

        @Override
        public int compareTo(_ping_request other) {
            int c;

            return 0;
        }

        private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException {
            oos.defaultWriteObject();
            net.morimekta.providence.serializer.BinarySerializer serializer = new net.morimekta.providence.serializer.BinarySerializer(false);
            serializer.serialize(oos, this);
        }

        private void readObject(java.io.ObjectInputStream ois)
                throws java.io.IOException, ClassNotFoundException {
            ois.defaultReadObject();
            net.morimekta.providence.serializer.BinarySerializer serializer = new net.morimekta.providence.serializer.BinarySerializer(false);
            tSerializeInstance = serializer.deserialize(ois, kDescriptor);
        }

        private Object readResolve() throws java.io.ObjectStreamException {
            return tSerializeInstance;
        }

        @Override
        public int writeBinary(net.morimekta.util.io.BigEndianBinaryWriter writer) throws java.io.IOException {
            int length = 0;

            length += writer.writeByte((byte) 0);
            return length;
        }

        @javax.annotation.Nonnull
        @Override
        public _Builder mutate() {
            return new _Builder(this);
        }

        public enum _Field implements net.morimekta.providence.descriptor.PField {
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
                }
                return null;
            }

            /**
             * @param name Field name
             * @return The named field or null
             */
            public static _Field findByName(String name) {
                switch (name) {
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
                    throw new IllegalArgumentException("No such field id " + id + " in calculator.Calculator.ping.request");
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
                    throw new IllegalArgumentException("No such field \"" + name + "\" in calculator.Calculator.ping.request");
                }
                return field;
            }

        }

        @javax.annotation.Nonnull
        public static net.morimekta.providence.descriptor.PStructDescriptorProvider<_ping_request,_Field> provider() {
            return new _Provider();
        }

        @Override
        @javax.annotation.Nonnull
        public net.morimekta.providence.descriptor.PStructDescriptor<_ping_request,_Field> descriptor() {
            return kDescriptor;
        }

        public static final net.morimekta.providence.descriptor.PStructDescriptor<_ping_request,_Field> kDescriptor;

        private static class _Descriptor
                extends net.morimekta.providence.descriptor.PStructDescriptor<_ping_request,_Field> {
            public _Descriptor() {
                super("calculator", "Calculator.ping.request", _Builder::new, true);
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

        private final static class _Provider extends net.morimekta.providence.descriptor.PStructDescriptorProvider<_ping_request,_Field> {
            @Override
            public net.morimekta.providence.descriptor.PStructDescriptor<_ping_request,_Field> descriptor() {
                return kDescriptor;
            }
        }

        /**
         * Make a calculator.Calculator.ping.request builder.
         * @return The builder instance.
         */
        public static _Builder builder() {
            return new _Builder();
        }

        public static class _Builder
                extends net.morimekta.providence.PMessageBuilder<_ping_request,_Field>
                implements net.morimekta.providence.serializer.binary.BinaryReader {
            private java.util.BitSet optionals;
            private java.util.BitSet modified;

            /**
             * Make a calculator.Calculator.ping.request builder.
             */
            public _Builder() {
                optionals = new java.util.BitSet(0);
                modified = new java.util.BitSet(0);
            }

            /**
             * Make a mutating builder off a base calculator.Calculator.ping.request.
             *
             * @param base The base Calculator.ping.request
             */
            public _Builder(_ping_request base) {
                this();

            }

            @javax.annotation.Nonnull
            @Override
            public _Builder merge(_ping_request from) {
                return this;
            }

            @Override
            public boolean equals(Object o) {
                if (o == this) return true;
                if (o == null || !o.getClass().equals(getClass())) return false;
                return true;
            }

            @Override
            public int hashCode() {
                return java.util.Objects.hash(
                        _ping_request.class, optionals);
            }

            @Override
            @SuppressWarnings("unchecked")
            public net.morimekta.providence.PMessageBuilder mutator(int key) {
                switch (key) {
                    default: throw new IllegalArgumentException("Not a message field ID: " + key);
                }
            }

            @javax.annotation.Nonnull
            @Override
            @SuppressWarnings("unchecked")
            public _Builder set(int key, Object value) {
                if (value == null) return clear(key);
                switch (key) {
                    default: break;
                }
                return this;
            }

            @Override
            public boolean isSet(int key) {
                switch (key) {
                    default: break;
                }
                return false;
            }

            @Override
            public boolean isModified(int key) {
                switch (key) {
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
                    default: break;
                }
                return this;
            }

            @Override
            public boolean valid() {
                return true;
            }

            @Override
            public void validate() {
            }

            @javax.annotation.Nonnull
            @Override
            public net.morimekta.providence.descriptor.PStructDescriptor<_ping_request,_Field> descriptor() {
                return kDescriptor;
            }

            @Override
            public void readBinary(net.morimekta.util.io.BigEndianBinaryReader reader, boolean strict) throws java.io.IOException {
                byte type = reader.expectByte();
                while (type != 0) {
                    int field = reader.expectShort();
                    switch (field) {
                        default: {
                            net.morimekta.providence.serializer.binary.BinaryFormatUtils.readFieldValue(reader, new net.morimekta.providence.serializer.binary.BinaryFormatUtils.FieldInfo(field, type), null, false);
                            break;
                        }
                    }
                    type = reader.expectByte();
                }
            }

            @Override
            public _ping_request build() {
                return new _ping_request(this);
            }
        }
    }

    // type <-- Calculator.ping.response
    @SuppressWarnings("unused")
    @javax.annotation.Generated("providence-maven-plugin")
    @javax.annotation.concurrent.Immutable
    protected static class _ping_response
            implements net.morimekta.providence.PUnion<_ping_response,_ping_response._Field>,
                       Comparable<_ping_response>,
                       java.io.Serializable,
                       net.morimekta.providence.serializer.binary.BinaryWriter {
        private final static long serialVersionUID = -1098386840489696915L;


        private transient final _Field tUnionField;

        private volatile transient int tHashCode;

        // Transient object used during java deserialization.
        private transient _ping_response tSerializeInstance;

        /**
         * @return The created union.
         */
        public static _ping_response withSuccess() {
            return new _Builder().setSuccess().build();
        }

        private _ping_response(_Builder builder) {
            tUnionField = builder.tUnionField;

        }

        public boolean hasSuccess() {
            return tUnionField == _Field.SUCCESS;
        }

        @Override
        public boolean has(int key) {
            switch(key) {
                case 0: return tUnionField == _Field.SUCCESS;
                default: return false;
            }
        }

        @Override
        @SuppressWarnings("unchecked")
        public <T> T get(int key) {
            switch(key) {
                case 0: return hasSuccess() ? (T) Boolean.TRUE : null;
                default: return null;
            }
        }

        @Override
        public boolean unionFieldIsSet() {
            return tUnionField != null;
        }

        @Override
        @javax.annotation.Nonnull
        public _Field unionField() {
            if (tUnionField == null) throw new IllegalStateException("No union field set in calculator.Calculator.ping.response");
            return tUnionField;
        }

        @Override
        public boolean equals(Object o) {
            if (o == this) return true;
            if (o == null || !o.getClass().equals(getClass())) return false;
            _ping_response other = (_ping_response) o;
            return java.util.Objects.equals(tUnionField, other.tUnionField);
        }

        @Override
        public int hashCode() {
            if (tHashCode == 0) {
                tHashCode = java.util.Objects.hash(
                        _ping_response.class);
            }
            return tHashCode;
        }

        @Override
        public String toString() {
            return "calculator.Calculator.ping.response" + asString();
        }

        @Override
        @javax.annotation.Nonnull
        public String asString() {
            StringBuilder out = new StringBuilder();
            out.append("{");

            switch (tUnionField) {
                case SUCCESS: {
                    out.append("success:")
                       .append("true");
                    break;
                }
            }
            out.append('}');
            return out.toString();
        }

        @Override
        public int compareTo(_ping_response other) {
            if (tUnionField == null || other.tUnionField == null) return Boolean.compare(tUnionField != null, other.tUnionField != null);
            int c = tUnionField.compareTo(other.tUnionField);
            if (c != 0) return c;

            switch (tUnionField) {
                case SUCCESS:
                default: return 0;
            }
        }

        private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException {
            oos.defaultWriteObject();
            net.morimekta.providence.serializer.BinarySerializer serializer = new net.morimekta.providence.serializer.BinarySerializer(false);
            serializer.serialize(oos, this);
        }

        private void readObject(java.io.ObjectInputStream ois)
                throws java.io.IOException, ClassNotFoundException {
            ois.defaultReadObject();
            net.morimekta.providence.serializer.BinarySerializer serializer = new net.morimekta.providence.serializer.BinarySerializer(false);
            tSerializeInstance = serializer.deserialize(ois, kDescriptor);
        }

        private Object readResolve() throws java.io.ObjectStreamException {
            return tSerializeInstance;
        }

        @Override
        public int writeBinary(net.morimekta.util.io.BigEndianBinaryWriter writer) throws java.io.IOException {
            int length = 0;

            if (tUnionField != null) {
                switch (tUnionField) {
                    case SUCCESS: {
                        length += writer.writeByte((byte) 1);
                        length += writer.writeShort((short) 0);
                        break;
                    }
                    default: break;
                }
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
            SUCCESS(0, net.morimekta.providence.descriptor.PRequirement.OPTIONAL, "success", net.morimekta.providence.descriptor.PPrimitive.VOID.provider(), null),
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
                    case 0: return _Field.SUCCESS;
                }
                return null;
            }

            /**
             * @param name Field name
             * @return The named field or null
             */
            public static _Field findByName(String name) {
                switch (name) {
                    case "success": return _Field.SUCCESS;
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
                    throw new IllegalArgumentException("No such field id " + id + " in calculator.Calculator.ping.response");
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
                    throw new IllegalArgumentException("No such field \"" + name + "\" in calculator.Calculator.ping.response");
                }
                return field;
            }

        }

        @javax.annotation.Nonnull
        public static net.morimekta.providence.descriptor.PUnionDescriptorProvider<_ping_response,_Field> provider() {
            return new _Provider();
        }

        @Override
        @javax.annotation.Nonnull
        public net.morimekta.providence.descriptor.PUnionDescriptor<_ping_response,_Field> descriptor() {
            return kDescriptor;
        }

        public static final net.morimekta.providence.descriptor.PUnionDescriptor<_ping_response,_Field> kDescriptor;

        private static class _Descriptor
                extends net.morimekta.providence.descriptor.PUnionDescriptor<_ping_response,_Field> {
            public _Descriptor() {
                super("calculator", "Calculator.ping.response", _Builder::new, true);
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

        private final static class _Provider extends net.morimekta.providence.descriptor.PUnionDescriptorProvider<_ping_response,_Field> {
            @Override
            public net.morimekta.providence.descriptor.PUnionDescriptor<_ping_response,_Field> descriptor() {
                return kDescriptor;
            }
        }

        /**
         * Make a calculator.Calculator.ping.response builder.
         * @return The builder instance.
         */
        public static _Builder builder() {
            return new _Builder();
        }

        public static class _Builder
                extends net.morimekta.providence.PMessageBuilder<_ping_response,_Field>
                implements net.morimekta.providence.serializer.binary.BinaryReader {
            private _Field tUnionField;

            private boolean modified;


            /**
             * Make a calculator.Calculator.ping.response builder.
             */
            public _Builder() {
                modified = false;
            }

            /**
             * Make a mutating builder off a base calculator.Calculator.ping.response.
             *
             * @param base The base Calculator.ping.response
             */
            public _Builder(_ping_response base) {
                this();

                tUnionField = base.tUnionField;

            }

            @javax.annotation.Nonnull
            @Override
            public _Builder merge(_ping_response from) {
                if (!from.unionFieldIsSet()) {
                    return this;
                }

                switch (from.unionField()) {
                    case SUCCESS: {
                        tUnionField = _Field.SUCCESS;
                        break;
                    }
                }
                return this;
            }

            /**
             * Sets the value of success.
             *
             * @return The builder
             */
            @javax.annotation.Nonnull
            public _Builder setSuccess() {
                tUnionField = _Field.SUCCESS;
                modified = true;
                return this;
            }

            /**
             * Checks for presence of the success field.
             *
             * @return True if success has been set.
             */
            public boolean isSetSuccess() {
                return tUnionField == _Field.SUCCESS;
            }

            /**
             * Clears the success field.
             *
             * @return The builder
             */
            @javax.annotation.Nonnull
            public _Builder clearSuccess() {
                if (tUnionField == _Field.SUCCESS) tUnionField = null;
                modified = true;
                return this;
            }

            /**
             * Checks if Calculator.ping.response has been modified since the _Builder was created.
             *
             * @return True if Calculator.ping.response has been modified.
             */
            public boolean isUnionModified() {
                return modified;
            }

            @Override
            public boolean equals(Object o) {
                if (o == this) return true;
                if (o == null || !o.getClass().equals(getClass())) return false;
                _ping_response._Builder other = (_ping_response._Builder) o;
                return java.util.Objects.equals(tUnionField, other.tUnionField);
            }

            @Override
            public int hashCode() {
                return java.util.Objects.hash(
                        _ping_response.class);
            }

            @Override
            @SuppressWarnings("unchecked")
            public net.morimekta.providence.PMessageBuilder mutator(int key) {
                switch (key) {
                    default: throw new IllegalArgumentException("Not a message field ID: " + key);
                }
            }

            @javax.annotation.Nonnull
            @Override
            @SuppressWarnings("unchecked")
            public _Builder set(int key, Object value) {
                if (value == null) return clear(key);
                switch (key) {
                    case 0: setSuccess(); break;
                    default: break;
                }
                return this;
            }

            @Override
            public boolean isSet(int key) {
                switch (key) {
                    case 0: return tUnionField == _Field.SUCCESS;
                    default: break;
                }
                return false;
            }

            @Override
            public boolean isModified(int key) {
                return modified;
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
                    case 0: clearSuccess(); break;
                    default: break;
                }
                return this;
            }

            @Override
            public boolean valid() {
                if (tUnionField == null) {
                    return false;
                }

                switch (tUnionField) {
                    case SUCCESS: return true;
                    default: return true;
                }
            }

            @Override
            public void validate() {
                if (!valid()) {
                    throw new java.lang.IllegalStateException("No union field set in calculator.Calculator.ping.response");
                }
            }

            @javax.annotation.Nonnull
            @Override
            public net.morimekta.providence.descriptor.PUnionDescriptor<_ping_response,_Field> descriptor() {
                return kDescriptor;
            }

            @Override
            public void readBinary(net.morimekta.util.io.BigEndianBinaryReader reader, boolean strict) throws java.io.IOException {
                byte type = reader.expectByte();
                while (type != 0) {
                    int field = reader.expectShort();
                    switch (field) {
                        case 0: {
                            if (type == 1) {
                                tUnionField = _Field.SUCCESS;
                            } else {
                                throw new net.morimekta.providence.serializer.SerializerException("Wrong type " + net.morimekta.providence.serializer.binary.BinaryType.asString(type) + " for calculator.Calculator.ping.response.success, should be struct(12)");
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
            public _ping_response build() {
                return new _ping_response(this);
            }
        }
    }

    protected Calculator() {}
}