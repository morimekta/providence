package net.morimekta.test.providence.core;

@SuppressWarnings("unused")
@javax.annotation.Generated("providence java generator")
public class ContainerService {
    public interface Iface {
        /**
         * @param pC The c value.
         * @return The load result.
         * @throws net.morimekta.test.providence.core.ExceptionFields The ef exception.
         * @throws java.io.IOException On providence or non-declared exceptions.
         */
        net.morimekta.test.providence.core.CompactFields load(
                net.morimekta.test.providence.core.Containers pC)
                throws java.io.IOException,
                       net.morimekta.test.providence.core.ExceptionFields;
    }

    /**
     * Client implementation for providence.ContainerService
     */
    public static class Client
            extends net.morimekta.providence.PClient
            implements Iface {
        private final net.morimekta.providence.PServiceCallHandler handler;

        /**
         * Create providence.ContainerService service client.
         *
         * @param handler The client handler.
         */
        public Client(net.morimekta.providence.PServiceCallHandler handler) {
            this.handler = handler;
        }

        @Override
        public net.morimekta.test.providence.core.CompactFields load(
                net.morimekta.test.providence.core.Containers pC)
                throws java.io.IOException,
                       net.morimekta.test.providence.core.ExceptionFields {
            net.morimekta.test.providence.core.ContainerService._load_request._Builder rq = net.morimekta.test.providence.core.ContainerService._load_request.builder();
            rq.setC(pC);

            net.morimekta.providence.PServiceCall call = new net.morimekta.providence.PServiceCall<>("load", net.morimekta.providence.PServiceCallType.CALL, getNextSequenceId(), rq.build());
            net.morimekta.providence.PServiceCall resp = handler.handleCall(call, ContainerService.kDescriptor);

            if (resp.getType() == net.morimekta.providence.PServiceCallType.EXCEPTION) {
                throw (net.morimekta.providence.PApplicationException) resp.getMessage();
            }

            net.morimekta.test.providence.core.ContainerService._load_response msg = (net.morimekta.test.providence.core.ContainerService._load_response) resp.getMessage();
            if (msg.unionField() != null) {
                switch (msg.unionField()) {
                    case EF:
                        throw msg.getEf();
                    case SUCCESS:
                        return msg.getSuccess();
                }
            }

            throw new net.morimekta.providence.PApplicationException("Result field for providence.ContainerService.load() not set",
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
                case "load": {
                    net.morimekta.test.providence.core.ContainerService._load_response._Builder rsp = net.morimekta.test.providence.core.ContainerService._load_response.builder();
                    try {
                        net.morimekta.test.providence.core.ContainerService._load_request req = (net.morimekta.test.providence.core.ContainerService._load_request) call.getMessage();
                        net.morimekta.test.providence.core.CompactFields result =
                                impl.load(req.getC());
                        rsp.setSuccess(result);
                    } catch (net.morimekta.test.providence.core.ExceptionFields e) {
                        rsp.setEf(e);
                    }
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
                                    "Unknown method \"" + call.getMethod() + "\" on providence.ContainerService.",
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
        LOAD("load", false, net.morimekta.test.providence.core.ContainerService._load_request.kDescriptor, net.morimekta.test.providence.core.ContainerService._load_response.kDescriptor),
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
                case "load": return LOAD;
            }
            return null;
        }
        @javax.annotation.Nonnull
        public static Method methodForName(String name) {
            Method method = findByName(name);
            if (method == null) {
                throw new IllegalArgumentException("No such method \"" + name + "\" in service providence.ContainerService");
            }
            return method;
        }
    }

    private static class _Descriptor extends net.morimekta.providence.descriptor.PService {
        private _Descriptor() {
            super("providence", "ContainerService", null, Method.values());
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

    // type --> ContainerService.load.request
    @SuppressWarnings("unused")
    @javax.annotation.Generated("providence java generator")
    protected static class _load_request
            implements net.morimekta.providence.PMessage<_load_request,_load_request._Field>,
                       Comparable<_load_request>,
                       java.io.Serializable,
                       net.morimekta.providence.serializer.binary.BinaryWriter {
        private final static long serialVersionUID = 642175186578463330L;

        private final net.morimekta.test.providence.core.Containers mC;

        private volatile int tHashCode;

        private _load_request(_Builder builder) {
            mC = builder.mC_builder != null ? builder.mC_builder.build() : builder.mC;
        }

        public boolean hasC() {
            return mC != null;
        }

        /**
         * @return The field value
         */
        public net.morimekta.test.providence.core.Containers getC() {
            return mC;
        }

        @Override
        public boolean has(int key) {
            switch(key) {
                case 1: return hasC();
                default: return false;
            }
        }

        @Override
        public int num(int key) {
            switch(key) {
                case 1: return hasC() ? 1 : 0;
                default: return 0;
            }
        }

        @Override
        public Object get(int key) {
            switch(key) {
                case 1: return getC();
                default: return null;
            }
        }

        @Override
        public boolean equals(Object o) {
            if (o == this) return true;
            if (o == null || !o.getClass().equals(getClass())) return false;
            _load_request other = (_load_request) o;
            return java.util.Objects.equals(mC, other.mC);
        }

        @Override
        public int hashCode() {
            if (tHashCode == 0) {
                tHashCode = java.util.Objects.hash(
                        _load_request.class,
                        _Field.C, mC);
            }
            return tHashCode;
        }

        @Override
        public String toString() {
            return "providence.ContainerService.load.request" + asString();
        }

        @Override
        @javax.annotation.Nonnull
        public String asString() {
            StringBuilder out = new StringBuilder();
            out.append("{");

            if (hasC()) {
                out.append("c:")
                   .append(mC.asString());
            }
            out.append('}');
            return out.toString();
        }

        @Override
        public int compareTo(_load_request other) {
            int c;

            c = Boolean.compare(mC != null, other.mC != null);
            if (c != 0) return c;
            if (mC != null) {
                c = mC.compareTo(other.mC);
                if (c != 0) return c;
            }

            return 0;
        }

        @Override
        public int writeBinary(net.morimekta.util.io.BigEndianBinaryWriter writer) throws java.io.IOException {
            int length = 0;

            if (hasC()) {
                length += writer.writeByte((byte) 12);
                length += writer.writeShort((short) 1);
                length += net.morimekta.providence.serializer.binary.BinaryFormatUtils.writeMessage(writer, mC);
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
            C(1, net.morimekta.providence.descriptor.PRequirement.DEFAULT, "c", net.morimekta.test.providence.core.Containers.provider(), null),
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
                    case 1: return _Field.C;
                }
                return null;
            }

            /**
             * @param name Field name
             * @return The named field or null
             */
            public static _Field findByName(String name) {
                switch (name) {
                    case "c": return _Field.C;
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
                    throw new IllegalArgumentException("No such field id " + id + " in providence.ContainerService.load.request");
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
                    throw new IllegalArgumentException("No such field \"" + name + "\" in providence.ContainerService.load.request");
                }
                return field;
            }

        }

        public static net.morimekta.providence.descriptor.PStructDescriptorProvider<_load_request,_Field> provider() {
            return new _Provider();
        }

        @Override
        public net.morimekta.providence.descriptor.PStructDescriptor<_load_request,_Field> descriptor() {
            return kDescriptor;
        }

        public static final net.morimekta.providence.descriptor.PStructDescriptor<_load_request,_Field> kDescriptor;

        private static class _Descriptor
                extends net.morimekta.providence.descriptor.PStructDescriptor<_load_request,_Field> {
            public _Descriptor() {
                super("providence", "ContainerService.load.request", _Builder::new, false);
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

        private final static class _Provider extends net.morimekta.providence.descriptor.PStructDescriptorProvider<_load_request,_Field> {
            @Override
            public net.morimekta.providence.descriptor.PStructDescriptor<_load_request,_Field> descriptor() {
                return kDescriptor;
            }
        }

        /**
         * Make a providence.ContainerService.load.request builder.
         * @return The builder instance.
         */
        public static _Builder builder() {
            return new _Builder();
        }

        public static class _Builder
                extends net.morimekta.providence.PMessageBuilder<_load_request,_Field>
                implements net.morimekta.providence.serializer.binary.BinaryReader {
            private java.util.BitSet optionals;
            private java.util.BitSet modified;

            private net.morimekta.test.providence.core.Containers mC;
            private net.morimekta.test.providence.core.Containers._Builder mC_builder;

            /**
             * Make a providence.ContainerService.load.request builder.
             */
            public _Builder() {
                optionals = new java.util.BitSet(1);
                modified = new java.util.BitSet(1);
            }

            /**
             * Make a mutating builder off a base providence.ContainerService.load.request.
             *
             * @param base The base ContainerService.load.request
             */
            public _Builder(_load_request base) {
                this();

                if (base.hasC()) {
                    optionals.set(0);
                    mC = base.mC;
                }
            }

            @javax.annotation.Nonnull
            @Override
            public _Builder merge(_load_request from) {
                if (from.hasC()) {
                    optionals.set(0);
                    modified.set(0);
                    if (mC_builder != null) {
                        mC_builder.merge(from.getC());
                    } else if (mC != null) {
                        mC_builder = mC.mutate().merge(from.getC());
                        mC = null;
                    } else {
                        mC = from.getC();
                    }
                }
                return this;
            }

            /**
             * Sets the value of c.
             *
             * @param value The new value
             * @return The builder
             */
            @javax.annotation.Nonnull
            public _Builder setC(net.morimekta.test.providence.core.Containers value) {
                if (value == null) {
                    return clearC();
                }

                optionals.set(0);
                modified.set(0);
                mC = value;
                mC_builder = null;
                return this;
            }

            /**
             * Checks for presence of the c field.
             *
             * @return True if c has been set.
             */
            public boolean isSetC() {
                return optionals.get(0);
            }

            /**
             * Checks if c has been modified since the _Builder was created.
             *
             * @return True if c has been modified.
             */
            public boolean isModifiedC() {
                return modified.get(0);
            }

            /**
             * Clears the c field.
             *
             * @return The builder
             */
            @javax.annotation.Nonnull
            public _Builder clearC() {
                optionals.clear(0);
                modified.set(0);
                mC = null;
                mC_builder = null;
                return this;
            }

            /**
             * Gets the builder for the contained c.
             *
             * @return The field builder
             */
            @javax.annotation.Nonnull
            public net.morimekta.test.providence.core.Containers._Builder mutableC() {
                optionals.set(0);
                modified.set(0);

                if (mC != null) {
                    mC_builder = mC.mutate();
                    mC = null;
                } else if (mC_builder == null) {
                    mC_builder = net.morimekta.test.providence.core.Containers.builder();
                }
                return mC_builder;
            }

            @Override
            public boolean equals(Object o) {
                if (o == this) return true;
                if (o == null || !o.getClass().equals(getClass())) return false;
                _load_request._Builder other = (_load_request._Builder) o;
                return java.util.Objects.equals(optionals, other.optionals) &&
                       java.util.Objects.equals(mC, other.mC);
            }

            @Override
            public int hashCode() {
                return java.util.Objects.hash(
                        _load_request.class, optionals,
                        _Field.C, mC);
            }

            @Override
            @SuppressWarnings("unchecked")
            public net.morimekta.providence.PMessageBuilder mutator(int key) {
                switch (key) {
                    case 1: return mutableC();
                    default: throw new IllegalArgumentException("Not a message field ID: " + key);
                }
            }

            @javax.annotation.Nonnull
            @Override
            @SuppressWarnings("unchecked")
            public _Builder set(int key, Object value) {
                if (value == null) return clear(key);
                switch (key) {
                    case 1: setC((net.morimekta.test.providence.core.Containers) value); break;
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
                    case 1: clearC(); break;
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
            public net.morimekta.providence.descriptor.PStructDescriptor<_load_request,_Field> descriptor() {
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
                                mC = net.morimekta.providence.serializer.binary.BinaryFormatUtils.readMessage(reader, net.morimekta.test.providence.core.Containers.kDescriptor, strict);
                                optionals.set(0);
                            } else {
                                throw new net.morimekta.providence.serializer.SerializerException("Wrong type " + net.morimekta.providence.serializer.binary.BinaryType.asString(type) + " for providence.ContainerService.load.request.c, should be struct(12)");
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
            public _load_request build() {
                return new _load_request(this);
            }
        }
    }

    // type <-- ContainerService.load.response
    @SuppressWarnings("unused")
    @javax.annotation.Generated("providence java generator")
    protected static class _load_response
            implements net.morimekta.providence.PUnion<_load_response,_load_response._Field>,
                       Comparable<_load_response>,
                       java.io.Serializable,
                       net.morimekta.providence.serializer.binary.BinaryWriter {
        private final static long serialVersionUID = 4669041823902453548L;

        private final net.morimekta.test.providence.core.CompactFields mSuccess;
        private final net.morimekta.test.providence.core.ExceptionFields mEf;

        private final _Field tUnionField;

        private volatile int tHashCode;

        /**
         * @param value The union value
         * @return The created union.
         */
        public static _load_response withSuccess(net.morimekta.test.providence.core.CompactFields value) {
            return new _Builder().setSuccess(value).build();
        }

        /**
         * @param value The union value
         * @return The created union.
         */
        public static _load_response withEf(net.morimekta.test.providence.core.ExceptionFields value) {
            return new _Builder().setEf(value).build();
        }

        private _load_response(_Builder builder) {
            tUnionField = builder.tUnionField;

            mSuccess = tUnionField != _Field.SUCCESS
                    ? null
                    : builder.mSuccess_builder != null ? builder.mSuccess_builder.build() : builder.mSuccess;
            mEf = tUnionField != _Field.EF
                    ? null
                    : builder.mEf_builder != null ? builder.mEf_builder.build() : builder.mEf;
        }

        public boolean hasSuccess() {
            return tUnionField == _Field.SUCCESS && mSuccess != null;
        }

        /**
         * @return The field value
         */
        public net.morimekta.test.providence.core.CompactFields getSuccess() {
            return mSuccess;
        }

        public boolean hasEf() {
            return tUnionField == _Field.EF && mEf != null;
        }

        /**
         * @return The field value
         */
        public net.morimekta.test.providence.core.ExceptionFields getEf() {
            return mEf;
        }

        @Override
        public boolean has(int key) {
            switch(key) {
                case 0: return hasSuccess();
                case 1: return hasEf();
                default: return false;
            }
        }

        @Override
        public int num(int key) {
            switch(key) {
                case 0: return hasSuccess() ? 1 : 0;
                case 1: return hasEf() ? 1 : 0;
                default: return 0;
            }
        }

        @Override
        public Object get(int key) {
            switch(key) {
                case 0: return getSuccess();
                case 1: return getEf();
                default: return null;
            }
        }

        @Override
        public _Field unionField() {
            return tUnionField;
        }

        @Override
        public boolean equals(Object o) {
            if (o == this) return true;
            if (o == null || !o.getClass().equals(getClass())) return false;
            _load_response other = (_load_response) o;
            return java.util.Objects.equals(tUnionField, other.tUnionField) &&
                   java.util.Objects.equals(mSuccess, other.mSuccess) &&
                   java.util.Objects.equals(mEf, other.mEf);
        }

        @Override
        public int hashCode() {
            if (tHashCode == 0) {
                tHashCode = java.util.Objects.hash(
                        _load_response.class,
                        _Field.SUCCESS, mSuccess,
                        _Field.EF, mEf);
            }
            return tHashCode;
        }

        @Override
        public String toString() {
            return "providence.ContainerService.load.response" + asString();
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
                case EF: {
                    out.append("ef:")
                       .append(mEf.asString());
                    break;
                }
            }
            out.append('}');
            return out.toString();
        }

        @Override
        public int compareTo(_load_response other) {
            int c = tUnionField.compareTo(other.tUnionField);
            if (c != 0) return c;

            switch (tUnionField) {
                case SUCCESS:
                    return mSuccess.compareTo(other.mSuccess);
                case EF:
                    return mEf.compareTo(other.mEf);
                default: return 0;
            }
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
                    case EF: {
                        length += writer.writeByte((byte) 12);
                        length += writer.writeShort((short) 1);
                        length += net.morimekta.providence.serializer.binary.BinaryFormatUtils.writeMessage(writer, mEf);
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
            SUCCESS(0, net.morimekta.providence.descriptor.PRequirement.OPTIONAL, "success", net.morimekta.test.providence.core.CompactFields.provider(), null),
            EF(1, net.morimekta.providence.descriptor.PRequirement.OPTIONAL, "ef", net.morimekta.test.providence.core.ExceptionFields.provider(), null),
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
                    case 1: return _Field.EF;
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
                    case "ef": return _Field.EF;
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
                    throw new IllegalArgumentException("No such field id " + id + " in providence.ContainerService.load.response");
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
                    throw new IllegalArgumentException("No such field \"" + name + "\" in providence.ContainerService.load.response");
                }
                return field;
            }

        }

        public static net.morimekta.providence.descriptor.PUnionDescriptorProvider<_load_response,_Field> provider() {
            return new _Provider();
        }

        @Override
        public net.morimekta.providence.descriptor.PUnionDescriptor<_load_response,_Field> descriptor() {
            return kDescriptor;
        }

        public static final net.morimekta.providence.descriptor.PUnionDescriptor<_load_response,_Field> kDescriptor;

        private static class _Descriptor
                extends net.morimekta.providence.descriptor.PUnionDescriptor<_load_response,_Field> {
            public _Descriptor() {
                super("providence", "ContainerService.load.response", _Builder::new, false);
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

        private final static class _Provider extends net.morimekta.providence.descriptor.PUnionDescriptorProvider<_load_response,_Field> {
            @Override
            public net.morimekta.providence.descriptor.PUnionDescriptor<_load_response,_Field> descriptor() {
                return kDescriptor;
            }
        }

        /**
         * Make a providence.ContainerService.load.response builder.
         * @return The builder instance.
         */
        public static _Builder builder() {
            return new _Builder();
        }

        public static class _Builder
                extends net.morimekta.providence.PMessageBuilder<_load_response,_Field>
                implements net.morimekta.providence.serializer.binary.BinaryReader {
            private _Field tUnionField;

            private boolean modified;

            private net.morimekta.test.providence.core.CompactFields mSuccess;
            private net.morimekta.test.providence.core.CompactFields._Builder mSuccess_builder;
            private net.morimekta.test.providence.core.ExceptionFields mEf;
            private net.morimekta.test.providence.core.ExceptionFields._Builder mEf_builder;

            /**
             * Make a providence.ContainerService.load.response builder.
             */
            public _Builder() {
                modified = false;
            }

            /**
             * Make a mutating builder off a base providence.ContainerService.load.response.
             *
             * @param base The base ContainerService.load.response
             */
            public _Builder(_load_response base) {
                this();

                tUnionField = base.tUnionField;

                mSuccess = base.mSuccess;
                mEf = base.mEf;
            }

            @javax.annotation.Nonnull
            @Override
            public _Builder merge(_load_response from) {
                if (from.unionField() == null) {
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
                    case EF: {
                        if (tUnionField == _Field.EF && mEf != null) {
                            mEf = mEf.mutate().merge(from.getEf()).build();
                        } else {
                            setEf(from.getEf());
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
            public _Builder setSuccess(net.morimekta.test.providence.core.CompactFields value) {
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
            public net.morimekta.test.providence.core.CompactFields._Builder mutableSuccess() {
                if (tUnionField != _Field.SUCCESS) {
                    clearSuccess();
                }
                tUnionField = _Field.SUCCESS;
                modified = true;

                if (mSuccess != null) {
                    mSuccess_builder = mSuccess.mutate();
                    mSuccess = null;
                } else if (mSuccess_builder == null) {
                    mSuccess_builder = net.morimekta.test.providence.core.CompactFields.builder();
                }
                return mSuccess_builder;
            }

            /**
             * Sets the value of ef.
             *
             * @param value The new value
             * @return The builder
             */
            @javax.annotation.Nonnull
            public _Builder setEf(net.morimekta.test.providence.core.ExceptionFields value) {
                if (value == null) {
                    return clearEf();
                }

                tUnionField = _Field.EF;
                modified = true;
                mEf = value;
                mEf_builder = null;
                return this;
            }

            /**
             * Checks for presence of the ef field.
             *
             * @return True if ef has been set.
             */
            public boolean isSetEf() {
                return tUnionField == _Field.EF;
            }

            /**
             * Clears the ef field.
             *
             * @return The builder
             */
            @javax.annotation.Nonnull
            public _Builder clearEf() {
                if (tUnionField == _Field.EF) tUnionField = null;
                modified = true;
                mEf = null;
                mEf_builder = null;
                return this;
            }

            /**
             * Gets the builder for the contained ef.
             *
             * @return The field builder
             */
            @javax.annotation.Nonnull
            public net.morimekta.test.providence.core.ExceptionFields._Builder mutableEf() {
                if (tUnionField != _Field.EF) {
                    clearEf();
                }
                tUnionField = _Field.EF;
                modified = true;

                if (mEf != null) {
                    mEf_builder = mEf.mutate();
                    mEf = null;
                } else if (mEf_builder == null) {
                    mEf_builder = net.morimekta.test.providence.core.ExceptionFields.builder();
                }
                return mEf_builder;
            }

            /**
             * Checks if ContainerService.load.response has been modified since the _Builder was created.
             *
             * @return True if ContainerService.load.response has been modified.
             */
            public boolean isUnionModified() {
                return modified;
            }

            @Override
            public boolean equals(Object o) {
                if (o == this) return true;
                if (o == null || !o.getClass().equals(getClass())) return false;
                _load_response._Builder other = (_load_response._Builder) o;
                return java.util.Objects.equals(tUnionField, other.tUnionField) &&
                       java.util.Objects.equals(mSuccess, other.mSuccess) &&
                       java.util.Objects.equals(mEf, other.mEf);
            }

            @Override
            public int hashCode() {
                return java.util.Objects.hash(
                        _load_response.class,
                        _Field.SUCCESS, mSuccess,
                        _Field.EF, mEf);
            }

            @Override
            @SuppressWarnings("unchecked")
            public net.morimekta.providence.PMessageBuilder mutator(int key) {
                switch (key) {
                    case 0: return mutableSuccess();
                    case 1: return mutableEf();
                    default: throw new IllegalArgumentException("Not a message field ID: " + key);
                }
            }

            @javax.annotation.Nonnull
            @Override
            @SuppressWarnings("unchecked")
            public _Builder set(int key, Object value) {
                if (value == null) return clear(key);
                switch (key) {
                    case 0: setSuccess((net.morimekta.test.providence.core.CompactFields) value); break;
                    case 1: setEf((net.morimekta.test.providence.core.ExceptionFields) value); break;
                    default: break;
                }
                return this;
            }

            @Override
            public boolean isSet(int key) {
                switch (key) {
                    case 0: return tUnionField == _Field.SUCCESS;
                    case 1: return tUnionField == _Field.EF;
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
                    case 1: clearEf(); break;
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
                    case EF: return mEf != null || mEf_builder != null;
                    default: return true;
                }
            }

            @Override
            public void validate() {
                if (!valid()) {
                    throw new java.lang.IllegalStateException("No union field set in providence.ContainerService.load.response");
                }
            }

            @javax.annotation.Nonnull
            @Override
            public net.morimekta.providence.descriptor.PUnionDescriptor<_load_response,_Field> descriptor() {
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
                                mSuccess = net.morimekta.providence.serializer.binary.BinaryFormatUtils.readMessage(reader, net.morimekta.test.providence.core.CompactFields.kDescriptor, strict);
                                tUnionField = _Field.SUCCESS;
                            } else {
                                throw new net.morimekta.providence.serializer.SerializerException("Wrong type " + net.morimekta.providence.serializer.binary.BinaryType.asString(type) + " for providence.ContainerService.load.response.success, should be struct(12)");
                            }
                            break;
                        }
                        case 1: {
                            if (type == 12) {
                                mEf = net.morimekta.providence.serializer.binary.BinaryFormatUtils.readMessage(reader, net.morimekta.test.providence.core.ExceptionFields.kDescriptor, strict);
                                tUnionField = _Field.EF;
                            } else {
                                throw new net.morimekta.providence.serializer.SerializerException("Wrong type " + net.morimekta.providence.serializer.binary.BinaryType.asString(type) + " for providence.ContainerService.load.response.ef, should be struct(12)");
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
            public _load_response build() {
                return new _load_response(this);
            }
        }
    }

    protected ContainerService() {}
}