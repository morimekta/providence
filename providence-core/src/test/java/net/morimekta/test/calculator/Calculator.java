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
        private final net.morimekta.providence.PClientHandler handler;

        public Client(net.morimekta.providence.PClientHandler handler) {
            this.handler = handler;
        }

        @Override
        public net.morimekta.test.calculator.Operand calculate(
                net.morimekta.test.calculator.Operation pOp)
                throws java.io.IOException,
                       net.morimekta.test.calculator.CalculateException {
            try {
                Calculate_request._Builder rq = Calculate_request.builder();
                rq.setOp(pOp);

                net.morimekta.providence.PServiceCall call = new net.morimekta.providence.PServiceCall("calculate", net.morimekta.providence.PServiceCallType.CALL, getNextSequenceId(), rq.build());
                net.morimekta.providence.PServiceCall resp = handler.handleCall(call, Calculator.kDescriptor);
                Calculate_response msg = (Calculate_response) resp.getMessage();

                if (resp.getType() == net.morimekta.providence.PServiceCallType.EXCEPTION) {
                    switch (msg.unionField()) {
                        case CE:
                            throw msg.getCe();
                        default: throw new java.io.IOException("Unknown exception field: " + msg.unionField().toString());
                    }
                }

                return msg.getSuccess();
            } catch (net.morimekta.providence.serializer.SerializerException e) {
                throw new java.io.IOException(e);
            }
        }

        @Override
        public void iamalive()
                throws java.io.IOException {
            try {
                Iamalive_request._Builder rq = Iamalive_request.builder();

                net.morimekta.providence.PServiceCall call = new net.morimekta.providence.PServiceCall("iamalive", net.morimekta.providence.PServiceCallType.ONEWAY, getNextSequenceId(), rq.build());
                handler.handleCall(call, Calculator.kDescriptor);
            } catch (net.morimekta.providence.serializer.SerializerException e) {
                throw new java.io.IOException(e);
            }
        }
    }

    public static class Processor implements net.morimekta.providence.PProcessor {
        private final Iface impl;
        public Processor(Iface impl) {
            this.impl = impl;
        }

        @Override
        public boolean process(net.morimekta.providence.mio.MessageReader reader, net.morimekta.providence.mio.MessageWriter writer) throws java.io.IOException {
            try {
                net.morimekta.providence.PServiceCallType type = net.morimekta.providence.PServiceCallType.EXCEPTION;
                net.morimekta.providence.PServiceCall call = reader.read(Calculator.kDescriptor);

                switch(call.getMethod()) {
                    case "calculate": {
                        Calculate_response._Builder rsp = Calculate_response.builder();
                        try {
                            Calculate_request req = (Calculate_request) call.getMessage();
                            net.morimekta.test.calculator.Operand result =
                                    impl.calculate(req.getOp());
                            rsp.setSuccess(result);
                            type = net.morimekta.providence.PServiceCallType.REPLY;
                        } catch (net.morimekta.test.calculator.CalculateException e) {
                            rsp.setCe(e);
                        }
                        net.morimekta.providence.PServiceCall reply = new net.morimekta.providence.PServiceCall(call.getMethod(), type, call.getSequence(), rsp.build());
                        writer.write(reply);
                        break;
                    }
                    case "iamalive": {
                        Iamalive_request req = (Iamalive_request) call.getMessage();
                        impl.iamalive();
                        type = net.morimekta.providence.PServiceCallType.REPLY;
                        break;
                    }
                    default: throw new java.io.IOException("Method call not handled: " + call.getMethod());
                }
                return true;
            } catch (net.morimekta.providence.serializer.SerializerException se) {
                throw new java.io.IOException(se);
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

    public static final net.morimekta.providence.descriptor.PService kDescriptor = new _Descriptor();

    // type --> calculate___request
    @SuppressWarnings("unused")
    public static class Calculate_request
            implements net.morimekta.providence.PMessage<Calculate_request>, java.io.Serializable, Comparable<Calculate_request> {
        private final static long serialVersionUID = -2850591557621395232L;

        private final net.morimekta.test.calculator.Operation mOp;
        
        private volatile int tHashCode;

        private Calculate_request(_Builder builder) {
            mOp = builder.mOp;
        }

        public Calculate_request(net.morimekta.test.calculator.Operation pOp) {
            mOp = pOp;
        }

        public boolean hasOp() {
            return mOp != null;
        }

        public net.morimekta.test.calculator.Operation getOp() {
            return mOp;
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
        public boolean equals(Object o) {
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

            boolean first = true;
            if (hasOp()) {
                first = false;
                out.append("op:");
                out.append(mOp.asString());
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
            public net.morimekta.providence.PType getType() { return getDescriptor().getType(); }

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
                StringBuilder builder = new StringBuilder();
                builder.append("Calculate_request._Field(")
                       .append(mKey)
                       .append(": ");
                if (mRequired != net.morimekta.providence.descriptor.PRequirement.DEFAULT) {
                    builder.append(mRequired.label).append(" ");
                }
                builder.append(getDescriptor().getQualifiedName(null))
                       .append(' ')
                       .append(mName)
                       .append(')');
                return builder.toString();
            }

            public static _Field forKey(int key) {
                switch (key) {
                    case 1: return _Field.OP;
                    default: return null;
                }
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
                extends net.morimekta.providence.PMessageBuilderFactory<Calculate_request> {
            @Override
            public _Builder builder() {
                return new _Builder();
            }
        }

        @Override
        public _Builder mutate() {
            return new _Builder(this);
        }

        public static _Builder builder() {
            return new _Builder();
        }

        public static class _Builder
                extends net.morimekta.providence.PMessageBuilder<Calculate_request> {
            private java.util.BitSet optionals;

            private net.morimekta.test.calculator.Operation mOp;


            public _Builder() {
                optionals = new java.util.BitSet(1);
            }

            public _Builder(Calculate_request base) {
                this();

                if (base.hasOp()) {
                    optionals.set(0);
                    mOp = base.mOp;
                }
            }

            public _Builder setOp(net.morimekta.test.calculator.Operation value) {
                optionals.set(0);
                mOp = value;
                return this;
            }
            public boolean isSetOp() {
                return optionals.get(0);
            }
            public _Builder clearOp() {
                optionals.set(0, false);
                mOp = null;
                return this;
            }
            @Override
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
            public Calculate_request build() {
                return new Calculate_request(this);
            }
        }
    }

    // type <-- calculate___response
    @SuppressWarnings("unused")
    public static class Calculate_response
            implements net.morimekta.providence.PUnion<Calculate_response>, java.io.Serializable, Comparable<Calculate_response> {
        private final static long serialVersionUID = 3839355577455995570L;

        private final net.morimekta.test.calculator.Operand mSuccess;
        private final net.morimekta.test.calculator.CalculateException mCe;

        private final _Field tUnionField;
        
        private volatile int tHashCode;

        private Calculate_response(_Builder builder) {
            tUnionField = builder.tUnionField;

            mSuccess = tUnionField == _Field.SUCCESS ? builder.mSuccess : null;
            mCe = tUnionField == _Field.CE ? builder.mCe : null;
        }

        public static Calculate_response withSuccess(net.morimekta.test.calculator.Operand value) {
            return new _Builder().setSuccess(value).build();
        }

        public static Calculate_response withCe(net.morimekta.test.calculator.CalculateException value) {
            return new _Builder().setCe(value).build();
        }

        public boolean hasSuccess() {
            return tUnionField == _Field.SUCCESS && mSuccess != null;
        }

        public net.morimekta.test.calculator.Operand getSuccess() {
            return mSuccess;
        }

        public boolean hasCe() {
            return tUnionField == _Field.CE && mCe != null;
        }

        public net.morimekta.test.calculator.CalculateException getCe() {
            return mCe;
        }

        @Override
        public _Field unionField() {
            return tUnionField;
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
        public boolean equals(Object o) {
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
                    out.append("success:");
                    out.append(mSuccess.asString());
                    break;
                }
                case CE: {
                    out.append("ce:");
                    out.append(mCe.asString());
                    break;
                }
            }
            out.append('}');
            return out.toString();
        }

        @Override
        public int compareTo(Calculate_response other) {
            int c = Integer.compare(tUnionField.getKey(), other.tUnionField.getKey());
            if (c != 0) return c;

            switch (tUnionField) {
                case SUCCESS:
                    return mSuccess.compareTo(other.mSuccess);
                case CE:
                    return mCe.compareTo(other.mCe);
                default: return 0;
            }
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
            public net.morimekta.providence.PType getType() { return getDescriptor().getType(); }

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
                StringBuilder builder = new StringBuilder();
                builder.append("Calculate_response._Field(")
                       .append(mKey)
                       .append(": ");
                if (mRequired != net.morimekta.providence.descriptor.PRequirement.DEFAULT) {
                    builder.append(mRequired.label).append(" ");
                }
                builder.append(getDescriptor().getQualifiedName(null))
                       .append(' ')
                       .append(mName)
                       .append(')');
                return builder.toString();
            }

            public static _Field forKey(int key) {
                switch (key) {
                    case 0: return _Field.SUCCESS;
                    case 1: return _Field.CE;
                    default: return null;
                }
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
                extends net.morimekta.providence.PMessageBuilderFactory<Calculate_response> {
            @Override
            public _Builder builder() {
                return new _Builder();
            }
        }

        @Override
        public _Builder mutate() {
            return new _Builder(this);
        }

        public static _Builder builder() {
            return new _Builder();
        }

        public static class _Builder
                extends net.morimekta.providence.PMessageBuilder<Calculate_response> {
            private _Field tUnionField;

            private net.morimekta.test.calculator.Operand mSuccess;
            private net.morimekta.test.calculator.CalculateException mCe;


            public _Builder() {
            }

            public _Builder(Calculate_response base) {
                this();

                tUnionField = base.tUnionField;

                mSuccess = base.mSuccess;
                mCe = base.mCe;
            }

            public _Builder setSuccess(net.morimekta.test.calculator.Operand value) {
                tUnionField = _Field.SUCCESS;
                mSuccess = value;
                return this;
            }
            public boolean isSetSuccess() {
                return tUnionField == _Field.SUCCESS;
            }
            public _Builder clearSuccess() {
                if (tUnionField == _Field.SUCCESS) tUnionField = null;
                mSuccess = null;
                return this;
            }
            public _Builder setCe(net.morimekta.test.calculator.CalculateException value) {
                tUnionField = _Field.CE;
                mCe = value;
                return this;
            }
            public boolean isSetCe() {
                return tUnionField == _Field.CE;
            }
            public _Builder clearCe() {
                if (tUnionField == _Field.CE) tUnionField = null;
                mCe = null;
                return this;
            }
            @Override
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
                return tUnionField != null;
            }

            @Override
            public Calculate_response build() {
                return new Calculate_response(this);
            }
        }
    }

    // type --> iamalive___request
    @SuppressWarnings("unused")
    public static class Iamalive_request
            implements net.morimekta.providence.PMessage<Iamalive_request>, java.io.Serializable, Comparable<Iamalive_request> {
        private final static long serialVersionUID = 7912890008187182926L;

        
        private volatile int tHashCode;

        private Iamalive_request(_Builder builder) {
        }

        public Iamalive_request() {
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
        public boolean equals(Object o) {
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

            boolean first = true;
            out.append('}');
            return out.toString();
        }

        @Override
        public int compareTo(Iamalive_request other) {
            int c;

            return 0;
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
            public net.morimekta.providence.PType getType() { return getDescriptor().getType(); }

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
                StringBuilder builder = new StringBuilder();
                builder.append("Iamalive_request._Field(")
                       .append(mKey)
                       .append(": ");
                if (mRequired != net.morimekta.providence.descriptor.PRequirement.DEFAULT) {
                    builder.append(mRequired.label).append(" ");
                }
                builder.append(getDescriptor().getQualifiedName(null))
                       .append(' ')
                       .append(mName)
                       .append(')');
                return builder.toString();
            }

            public static _Field forKey(int key) {
                switch (key) {
                    default: return null;
                }
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
                extends net.morimekta.providence.PMessageBuilderFactory<Iamalive_request> {
            @Override
            public _Builder builder() {
                return new _Builder();
            }
        }

        @Override
        public _Builder mutate() {
            return new _Builder(this);
        }

        public static _Builder builder() {
            return new _Builder();
        }

        public static class _Builder
                extends net.morimekta.providence.PMessageBuilder<Iamalive_request> {
            private java.util.BitSet optionals;


            public _Builder() {
                optionals = new java.util.BitSet(0);
            }

            public _Builder(Iamalive_request base) {
                this();

            }

            @Override
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
            public Iamalive_request build() {
                return new Iamalive_request(this);
            }
        }
    }

    private Calculator() {}
}