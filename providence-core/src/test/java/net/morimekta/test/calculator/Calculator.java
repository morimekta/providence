package net.morimekta.test.calculator;

@SuppressWarnings("unused")
public class Calculator {
    public interface Iface {
        net.morimekta.test.calculator.Operand calculate(
                net.morimekta.test.calculator.Operation pOp)
                throws java.io.IOException,
                       net.morimekta.test.calculator.CalculateException;

        void iamalive()
                throws java.io.IOException;
    }

    public static class Client
            extends net.morimekta.providence.PClient
            implements Iface {
        private final net.morimekta.providence.PServiceCallHandler handler;

        public Client(net.morimekta.providence.PServiceCallHandler handler) {
            this.handler = handler;
        }

        @Override
        public net.morimekta.test.calculator.Operand calculate(
                net.morimekta.test.calculator.Operation pOp)
                throws java.io.IOException,
                       net.morimekta.test.calculator.CalculateException {
            Calculate_request._Builder rq = Calculate_request.builder();
            rq.setOp(pOp);

            net.morimekta.providence.PServiceCall call = new net.morimekta.providence.PServiceCall<>("calculate", net.morimekta.providence.PServiceCallType.CALL, getNextSequenceId(), rq.build());
            net.morimekta.providence.PServiceCall resp = handler.handleCall(call, Calculator.kDescriptor);
            Calculate_response msg = (Calculate_response) resp.getMessage();

            if (resp.getType() == net.morimekta.providence.PServiceCallType.EXCEPTION) {
                throw (net.morimekta.providence.PApplicationException) resp.getMessage();
            }
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
            Iamalive_request._Builder rq = Iamalive_request.builder();

            net.morimekta.providence.PServiceCall call = new net.morimekta.providence.PServiceCall<>("iamalive", net.morimekta.providence.PServiceCallType.ONEWAY, getNextSequenceId(), rq.build());
            handler.handleCall(call, Calculator.kDescriptor);
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
                    Calculate_response._Builder rsp = Calculate_response.builder();
                    try {
                        Calculate_request req = (Calculate_request) call.getMessage();
                        net.morimekta.test.calculator.Operand result =
                                impl.calculate(req.getOp());
                        rsp.setSuccess(result);
                    } catch (net.morimekta.test.calculator.CalculateException e) {
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
                    Iamalive_request req = (Iamalive_request) call.getMessage();
                    impl.iamalive();
                    return null;
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
        CALCULATE("calculate", false, Calculate_request.kDescriptor, Calculate_response.kDescriptor),
        IAMALIVE("iamalive", true, Iamalive_request.kDescriptor, null),
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

        public static Method forName(String name) {
            switch (name) {
                case "calculate": return CALCULATE;
                case "iamalive": return IAMALIVE;
            }
            return null;
        }
    }

    private static class _Descriptor extends net.morimekta.providence.descriptor.PService {
        private _Descriptor() {
            super("calculator", "Calculator", null, Method.values());
        }

        @Override
        public Method getMethod(String name) {
            return Method.forName(name);
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

    // type --> calculate___request
    @SuppressWarnings("unused")
    private static class Calculate_request
            implements Comparable<Calculate_request>,
                       java.io.Serializable,
                       net.morimekta.providence.PMessage<Calculate_request,Calculate_request._Field> {
        private final static long serialVersionUID = -2850591557621395232L;


        private final net.morimekta.test.calculator.Operation mOp;

        private volatile int tHashCode;

        public Calculate_request(net.morimekta.test.calculator.Operation pOp) {
            mOp = pOp;
        }

        private Calculate_request(_Builder builder) {
            mOp = builder.mOp_builder != null ? builder.mOp_builder.build() : builder.mOp;
        }

        public boolean hasOp() {
            return mOp != null;
        }

        /**
         * @return The field value
         */
        public net.morimekta.test.calculator.Operation getOp() {
            return mOp;
        }

        @Override
        public boolean equals(Object o) {
            if (o == this) return true;
            if (o == null || !(o instanceof Calculate_request)) return false;
            Calculate_request other = (Calculate_request) o;
            return java.util.Objects.equals(mOp, other.mOp);
        }

        @Override
        public int hashCode() {
            if (tHashCode == 0) {
                tHashCode = java.util.Objects.hash(
                        Calculate_request.class,
                        _Field.OP, mOp);
            }
            return tHashCode;
        }

        @Override
        public String toString() {
            return "calculator.calculate___request" + asString();
        }

        @Override
        public String asString() {
            StringBuilder out = new StringBuilder();
            out.append("{");

            if (mOp != null) {
                out.append("op:")
                   .append(mOp.asString());
            }
            out.append('}');
            return out.toString();
        }

        @Override
        public int compareTo(Calculate_request other) {
            int c;

            c = Boolean.compare(mOp != null, other.mOp != null);
            if (c != 0) return c;
            if (mOp != null) {
                c = mOp.compareTo(other.mOp);
                if (c != 0) return c;
            }

            return 0;
        }

        @Override
        public boolean has(int key) {
            switch(key) {
                case 1: return hasOp();
                default: return false;
            }
        }

        @Override
        public int num(int key) {
            switch(key) {
                case 1: return hasOp() ? 1 : 0;
                default: return 0;
            }
        }

        @Override
        public Object get(int key) {
            switch(key) {
                case 1: return getOp();
                default: return null;
            }
        }

        @Override
        public boolean compact() {
            return false;
        }

        @Override
        public _Builder mutate() {
            return new _Builder(this);
        }

        public enum _Field implements net.morimekta.providence.descriptor.PField {
            OP(1, net.morimekta.providence.descriptor.PRequirement.DEFAULT, "op", net.morimekta.test.calculator.Operation.provider(), null),
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
                    case 1: return _Field.OP;
                }
                return null;
            }

            public static _Field forName(String name) {
                switch (name) {
                    case "op": return _Field.OP;
                }
                return null;
            }
        }

        public static net.morimekta.providence.descriptor.PStructDescriptorProvider<Calculate_request,_Field> provider() {
            return new _Provider();
        }

        @Override
        public net.morimekta.providence.descriptor.PStructDescriptor<Calculate_request,_Field> descriptor() {
            return kDescriptor;
        }

        public static final net.morimekta.providence.descriptor.PStructDescriptor<Calculate_request,_Field> kDescriptor;

        private static class _Descriptor
                extends net.morimekta.providence.descriptor.PStructDescriptor<Calculate_request,_Field> {
            public _Descriptor() {
                super("calculator", "calculate___request", new _Factory(), false, false);
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

        private final static class _Provider extends net.morimekta.providence.descriptor.PStructDescriptorProvider<Calculate_request,_Field> {
            @Override
            public net.morimekta.providence.descriptor.PStructDescriptor<Calculate_request,_Field> descriptor() {
                return kDescriptor;
            }
        }

        private final static class _Factory
                extends net.morimekta.providence.PMessageBuilderFactory<Calculate_request,_Field> {
            @Override
            public _Builder builder() {
                return new _Builder();
            }
        }

        /**
         * Make a calculator.calculate___request builder.
         * @return The builder instance.
         */
        public static _Builder builder() {
            return new _Builder();
        }

        public static class _Builder
                extends net.morimekta.providence.PMessageBuilder<Calculate_request,_Field> {
            private java.util.BitSet optionals;

            private net.morimekta.test.calculator.Operation mOp;
            private net.morimekta.test.calculator.Operation._Builder mOp_builder;

            /**
             * Make a calculator.calculate___request builder.
             */
            public _Builder() {
                optionals = new java.util.BitSet(1);
            }

            /**
             * Make a mutating builder off a base calculator.calculate___request.
             *
             * @param base The base calculate___request
             */
            public _Builder(Calculate_request base) {
                this();

                if (base.hasOp()) {
                    optionals.set(0);
                    mOp = base.mOp;
                }
            }

            @Override
            public _Builder merge(Calculate_request from) {
                if (from.hasOp()) {
                    optionals.set(0);
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
            public _Builder setOp(net.morimekta.test.calculator.Operation value) {
                optionals.set(0);
                mOp_builder = null;
                mOp = value;
                return this;
            }

            /**
             * Checks for presence of the op field.
             *
             * @return True iff op has been set.
             */
            public boolean isSetOp() {
                return optionals.get(0);
            }

            /**
             * Clears the op field.
             *
             * @return The builder
             */
            public _Builder clearOp() {
                optionals.clear(0);
                mOp = null;
                mOp_builder = null;
                return this;
            }

            /**
             * Gets the builder for the contained op.
             *
             * @return The field builder
             */
            public net.morimekta.test.calculator.Operation._Builder mutableOp() {
                optionals.set(0);

                if (mOp != null) {
                    mOp_builder = mOp.mutate();
                    mOp = null;
                } else if (mOp_builder == null) {
                    mOp_builder = net.morimekta.test.calculator.Operation.builder();
                }
                return mOp_builder;
            }

            @Override
            @SuppressWarnings("unchecked")
            public net.morimekta.providence.PMessageBuilder mutator(int key) {
                switch (key) {
                    case 1: return mutableOp();
                    default: throw new IllegalArgumentException("Not a message field ID: " + key);
                }
            }

            @Override
            @SuppressWarnings("unchecked")
            public _Builder set(int key, Object value) {
                if (value == null) return clear(key);
                switch (key) {
                    case 1: setOp((net.morimekta.test.calculator.Operation) value); break;
                }
                return this;
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
                    case 1: clearOp(); break;
                }
                return this;
            }

            @Override
            public boolean isValid() {
                return true;
            }

            @Override
            public void validate() {
            }

            @Override
            public net.morimekta.providence.descriptor.PStructDescriptor<Calculate_request,_Field> descriptor() {
                return kDescriptor;
            }

            @Override
            public Calculate_request build() {
                return new Calculate_request(this);
            }
        }
    }
    // type <-- calculate___response
    @SuppressWarnings("unused")
    private static class Calculate_response
            implements Comparable<Calculate_response>,
                       java.io.Serializable,
                       net.morimekta.providence.PUnion<Calculate_response,Calculate_response._Field> {
        private final static long serialVersionUID = 3839355577455995570L;


        private final net.morimekta.test.calculator.Operand mSuccess;
        private final net.morimekta.test.calculator.CalculateException mCe;

        private volatile int tHashCode;

        private final _Field tUnionField;

        /**
         * @param value The union value
         * @return The created union.
         */
        public static Calculate_response withSuccess(net.morimekta.test.calculator.Operand value) {
            return new _Builder().setSuccess(value).build();
        }

        /**
         * @param value The union value
         * @return The created union.
         */
        public static Calculate_response withCe(net.morimekta.test.calculator.CalculateException value) {
            return new _Builder().setCe(value).build();
        }

        private Calculate_response(_Builder builder) {
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
        public net.morimekta.test.calculator.Operand getSuccess() {
            return mSuccess;
        }

        public boolean hasCe() {
            return tUnionField == _Field.CE && mCe != null;
        }

        /**
         * @return The field value
         */
        public net.morimekta.test.calculator.CalculateException getCe() {
            return mCe;
        }

        @Override
        public boolean equals(Object o) {
            if (o == this) return true;
            if (o == null || !(o instanceof Calculate_response)) return false;
            Calculate_response other = (Calculate_response) o;
            return java.util.Objects.equals(tUnionField, other.tUnionField) &&
                   java.util.Objects.equals(mSuccess, other.mSuccess) &&
                   java.util.Objects.equals(mCe, other.mCe);
        }

        @Override
        public int hashCode() {
            if (tHashCode == 0) {
                tHashCode = java.util.Objects.hash(
                        Calculate_response.class,
                        _Field.SUCCESS, mSuccess,
                        _Field.CE, mCe);
            }
            return tHashCode;
        }

        @Override
        public String toString() {
            return "calculator.calculate___response" + asString();
        }

        @Override
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
        public int compareTo(Calculate_response other) {
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

        @Override
        public boolean has(int key) {
            switch(key) {
                case 0: return hasSuccess();
                case 1: return hasCe();
                default: return false;
            }
        }

        @Override
        public int num(int key) {
            switch(key) {
                case 0: return hasSuccess() ? 1 : 0;
                case 1: return hasCe() ? 1 : 0;
                default: return 0;
            }
        }

        @Override
        public Object get(int key) {
            switch(key) {
                case 0: return getSuccess();
                case 1: return getCe();
                default: return null;
            }
        }

        @Override
        public boolean compact() {
            return false;
        }

        @Override
        public _Field unionField() {
            return tUnionField;
        }

        @Override
        public _Builder mutate() {
            return new _Builder(this);
        }

        public enum _Field implements net.morimekta.providence.descriptor.PField {
            SUCCESS(0, net.morimekta.providence.descriptor.PRequirement.OPTIONAL, "success", net.morimekta.test.calculator.Operand.provider(), null),
            CE(1, net.morimekta.providence.descriptor.PRequirement.DEFAULT, "ce", net.morimekta.test.calculator.CalculateException.provider(), null),
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
                    case 0: return _Field.SUCCESS;
                    case 1: return _Field.CE;
                }
                return null;
            }

            public static _Field forName(String name) {
                switch (name) {
                    case "success": return _Field.SUCCESS;
                    case "ce": return _Field.CE;
                }
                return null;
            }
        }

        public static net.morimekta.providence.descriptor.PUnionDescriptorProvider<Calculate_response,_Field> provider() {
            return new _Provider();
        }

        @Override
        public net.morimekta.providence.descriptor.PUnionDescriptor<Calculate_response,_Field> descriptor() {
            return kDescriptor;
        }

        public static final net.morimekta.providence.descriptor.PUnionDescriptor<Calculate_response,_Field> kDescriptor;

        private static class _Descriptor
                extends net.morimekta.providence.descriptor.PUnionDescriptor<Calculate_response,_Field> {
            public _Descriptor() {
                super("calculator", "calculate___response", new _Factory(), false);
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

        private final static class _Provider extends net.morimekta.providence.descriptor.PUnionDescriptorProvider<Calculate_response,_Field> {
            @Override
            public net.morimekta.providence.descriptor.PUnionDescriptor<Calculate_response,_Field> descriptor() {
                return kDescriptor;
            }
        }

        private final static class _Factory
                extends net.morimekta.providence.PMessageBuilderFactory<Calculate_response,_Field> {
            @Override
            public _Builder builder() {
                return new _Builder();
            }
        }

        /**
         * Make a calculator.calculate___response builder.
         * @return The builder instance.
         */
        public static _Builder builder() {
            return new _Builder();
        }

        public static class _Builder
                extends net.morimekta.providence.PMessageBuilder<Calculate_response,_Field> {
            private _Field tUnionField;

            private net.morimekta.test.calculator.Operand mSuccess;
            private net.morimekta.test.calculator.Operand._Builder mSuccess_builder;
            private net.morimekta.test.calculator.CalculateException mCe;
            private net.morimekta.test.calculator.CalculateException._Builder mCe_builder;

            /**
             * Make a calculator.calculate___response builder.
             */
            public _Builder() {
            }

            /**
             * Make a mutating builder off a base calculator.calculate___response.
             *
             * @param base The base calculate___response
             */
            public _Builder(Calculate_response base) {
                this();

                tUnionField = base.tUnionField;

                mSuccess = base.mSuccess;
                mCe = base.mCe;
            }

            @Override
            public _Builder merge(Calculate_response from) {
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
            public _Builder setSuccess(net.morimekta.test.calculator.Operand value) {
                tUnionField = _Field.SUCCESS;
                mSuccess_builder = null;
                mSuccess = value;
                return this;
            }

            /**
             * Checks for presence of the success field.
             *
             * @return True iff success has been set.
             */
            public boolean isSetSuccess() {
                return tUnionField == _Field.SUCCESS;
            }

            /**
             * Clears the success field.
             *
             * @return The builder
             */
            public _Builder clearSuccess() {
                if (tUnionField == _Field.SUCCESS) tUnionField = null;
                mSuccess = null;
                mSuccess_builder = null;
                return this;
            }

            /**
             * Gets the builder for the contained success.
             *
             * @return The field builder
             */
            public net.morimekta.test.calculator.Operand._Builder mutableSuccess() {
                if (tUnionField != _Field.SUCCESS) {
                    clearSuccess();
                }
                tUnionField = _Field.SUCCESS;

                if (mSuccess != null) {
                    mSuccess_builder = mSuccess.mutate();
                    mSuccess = null;
                } else if (mSuccess_builder == null) {
                    mSuccess_builder = net.morimekta.test.calculator.Operand.builder();
                }
                return mSuccess_builder;
            }

            /**
             * Sets the value of ce.
             *
             * @param value The new value
             * @return The builder
             */
            public _Builder setCe(net.morimekta.test.calculator.CalculateException value) {
                tUnionField = _Field.CE;
                mCe_builder = null;
                mCe = value;
                return this;
            }

            /**
             * Checks for presence of the ce field.
             *
             * @return True iff ce has been set.
             */
            public boolean isSetCe() {
                return tUnionField == _Field.CE;
            }

            /**
             * Clears the ce field.
             *
             * @return The builder
             */
            public _Builder clearCe() {
                if (tUnionField == _Field.CE) tUnionField = null;
                mCe = null;
                mCe_builder = null;
                return this;
            }

            /**
             * Gets the builder for the contained ce.
             *
             * @return The field builder
             */
            public net.morimekta.test.calculator.CalculateException._Builder mutableCe() {
                if (tUnionField != _Field.CE) {
                    clearCe();
                }
                tUnionField = _Field.CE;

                if (mCe != null) {
                    mCe_builder = mCe.mutate();
                    mCe = null;
                } else if (mCe_builder == null) {
                    mCe_builder = net.morimekta.test.calculator.CalculateException.builder();
                }
                return mCe_builder;
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

            @Override
            @SuppressWarnings("unchecked")
            public _Builder set(int key, Object value) {
                if (value == null) return clear(key);
                switch (key) {
                    case 0: setSuccess((net.morimekta.test.calculator.Operand) value); break;
                    case 1: setCe((net.morimekta.test.calculator.CalculateException) value); break;
                }
                return this;
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
                    case 0: clearSuccess(); break;
                    case 1: clearCe(); break;
                }
                return this;
            }

            @Override
            public boolean isValid() {
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
                if (!isValid()) {
                    throw new java.lang.IllegalStateException("No union field set in calculator.calculate___response");
                }
            }

            @Override
            public net.morimekta.providence.descriptor.PUnionDescriptor<Calculate_response,_Field> descriptor() {
                return kDescriptor;
            }

            @Override
            public Calculate_response build() {
                return new Calculate_response(this);
            }
        }
    }
    // type --> iamalive___request
    @SuppressWarnings("unused")
    private static class Iamalive_request
            implements Comparable<Iamalive_request>,
                       java.io.Serializable,
                       net.morimekta.providence.PMessage<Iamalive_request,Iamalive_request._Field> {
        private final static long serialVersionUID = 7912890008187182926L;



        private volatile int tHashCode;

        public Iamalive_request() {
        }

        private Iamalive_request(_Builder builder) {
        }

        @Override
        public boolean equals(Object o) {
            if (o == this) return true;
            if (o == null || !(o instanceof Iamalive_request)) return false;
            return true;
        }

        @Override
        public int hashCode() {
            if (tHashCode == 0) {
                tHashCode = java.util.Objects.hash(
                        Iamalive_request.class);
            }
            return tHashCode;
        }

        @Override
        public String toString() {
            return "calculator.iamalive___request" + asString();
        }

        @Override
        public String asString() {
            StringBuilder out = new StringBuilder();
            out.append("{");

            out.append('}');
            return out.toString();
        }

        @Override
        public int compareTo(Iamalive_request other) {
            int c;

            return 0;
        }

        @Override
        public boolean has(int key) {
            switch(key) {
                default: return false;
            }
        }

        @Override
        public int num(int key) {
            switch(key) {
                default: return 0;
            }
        }

        @Override
        public Object get(int key) {
            switch(key) {
                default: return null;
            }
        }

        @Override
        public boolean compact() {
            return false;
        }

        @Override
        public _Builder mutate() {
            return new _Builder(this);
        }

        public enum _Field implements net.morimekta.providence.descriptor.PField {
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
                }
                return null;
            }

            public static _Field forName(String name) {
                switch (name) {
                }
                return null;
            }
        }

        public static net.morimekta.providence.descriptor.PStructDescriptorProvider<Iamalive_request,_Field> provider() {
            return new _Provider();
        }

        @Override
        public net.morimekta.providence.descriptor.PStructDescriptor<Iamalive_request,_Field> descriptor() {
            return kDescriptor;
        }

        public static final net.morimekta.providence.descriptor.PStructDescriptor<Iamalive_request,_Field> kDescriptor;

        private static class _Descriptor
                extends net.morimekta.providence.descriptor.PStructDescriptor<Iamalive_request,_Field> {
            public _Descriptor() {
                super("calculator", "iamalive___request", new _Factory(), true, false);
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

        private final static class _Provider extends net.morimekta.providence.descriptor.PStructDescriptorProvider<Iamalive_request,_Field> {
            @Override
            public net.morimekta.providence.descriptor.PStructDescriptor<Iamalive_request,_Field> descriptor() {
                return kDescriptor;
            }
        }

        private final static class _Factory
                extends net.morimekta.providence.PMessageBuilderFactory<Iamalive_request,_Field> {
            @Override
            public _Builder builder() {
                return new _Builder();
            }
        }

        /**
         * Make a calculator.iamalive___request builder.
         * @return The builder instance.
         */
        public static _Builder builder() {
            return new _Builder();
        }

        public static class _Builder
                extends net.morimekta.providence.PMessageBuilder<Iamalive_request,_Field> {
            private java.util.BitSet optionals;

            /**
             * Make a calculator.iamalive___request builder.
             */
            public _Builder() {
                optionals = new java.util.BitSet(0);
            }

            /**
             * Make a mutating builder off a base calculator.iamalive___request.
             *
             * @param base The base iamalive___request
             */
            public _Builder(Iamalive_request base) {
                this();

            }

            @Override
            public _Builder merge(Iamalive_request from) {
                return this;
            }

            @Override
            @SuppressWarnings("unchecked")
            public net.morimekta.providence.PMessageBuilder mutator(int key) {
                switch (key) {
                    default: throw new IllegalArgumentException("Not a message field ID: " + key);
                }
            }

            @Override
            @SuppressWarnings("unchecked")
            public _Builder set(int key, Object value) {
                if (value == null) return clear(key);
                switch (key) {
                }
                return this;
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
                }
                return this;
            }

            @Override
            public boolean isValid() {
                return true;
            }

            @Override
            public void validate() {
            }

            @Override
            public net.morimekta.providence.descriptor.PStructDescriptor<Iamalive_request,_Field> descriptor() {
                return kDescriptor;
            }

            @Override
            public Iamalive_request build() {
                return new Iamalive_request(this);
            }
        }
    }
    private Calculator() {}
}