package net.morimekta.providence.model;

/** (oneway)? <return_type> <name>'('<param>*')' (throws '(' <exception>+ ')')? */
@SuppressWarnings("unused")
public class ServiceMethod
        implements net.morimekta.providence.PMessage<ServiceMethod>, java.io.Serializable, Comparable<ServiceMethod> {
    private final static long serialVersionUID = -8952857258512990537L;

    private final static boolean kDefaultOneWay = false;

    private final String mComment;
    private final boolean mOneWay;
    private final String mReturnType;
    private final String mName;
    private final java.util.List<net.morimekta.providence.model.ThriftField> mParams;
    private final java.util.List<net.morimekta.providence.model.ThriftField> mExceptions;
    private final java.util.Map<String,String> mAnnotations;
    
    private volatile int tHashCode;

    private ServiceMethod(_Builder builder) {
        mComment = builder.mComment;
        mOneWay = builder.mOneWay;
        mReturnType = builder.mReturnType;
        mName = builder.mName;
        if (builder.isSetParams()) {
            mParams = builder.mParams.build();
        } else {
            mParams = null;
        }
        if (builder.isSetExceptions()) {
            mExceptions = builder.mExceptions.build();
        } else {
            mExceptions = null;
        }
        if (builder.isSetAnnotations()) {
            mAnnotations = builder.mAnnotations.build();
        } else {
            mAnnotations = null;
        }
    }

    public ServiceMethod(String pComment,
                         boolean pOneWay,
                         String pReturnType,
                         String pName,
                         java.util.List<net.morimekta.providence.model.ThriftField> pParams,
                         java.util.List<net.morimekta.providence.model.ThriftField> pExceptions,
                         java.util.Map<String,String> pAnnotations) {
        mComment = pComment;
        mOneWay = pOneWay;
        mReturnType = pReturnType;
        mName = pName;
        if (pParams != null) {
            mParams = com.google.common.collect.ImmutableList.copyOf(pParams);
        } else {
            mParams = null;
        }
        if (pExceptions != null) {
            mExceptions = com.google.common.collect.ImmutableList.copyOf(pExceptions);
        } else {
            mExceptions = null;
        }
        if (pAnnotations != null) {
            mAnnotations = com.google.common.collect.ImmutableMap.copyOf(pAnnotations);
        } else {
            mAnnotations = null;
        }
    }

    public boolean hasComment() {
        return mComment != null;
    }

    public String getComment() {
        return mComment;
    }

    public boolean hasOneWay() {
        return true;
    }

    public boolean isOneWay() {
        return mOneWay;
    }

    public boolean hasReturnType() {
        return mReturnType != null;
    }

    public String getReturnType() {
        return mReturnType;
    }

    public boolean hasName() {
        return mName != null;
    }

    public String getName() {
        return mName;
    }

    public int numParams() {
        return mParams != null ? mParams.size() : 0;
    }

    public java.util.List<net.morimekta.providence.model.ThriftField> getParams() {
        return mParams;
    }

    public int numExceptions() {
        return mExceptions != null ? mExceptions.size() : 0;
    }

    public java.util.List<net.morimekta.providence.model.ThriftField> getExceptions() {
        return mExceptions;
    }

    public int numAnnotations() {
        return mAnnotations != null ? mAnnotations.size() : 0;
    }

    public java.util.Map<String,String> getAnnotations() {
        return mAnnotations;
    }

    @Override
    public boolean has(int key) {
        switch(key) {
            case 1: return hasComment();
            case 2: return true;
            case 3: return hasReturnType();
            case 4: return hasName();
            case 5: return numParams() > 0;
            case 6: return numExceptions() > 0;
            case 7: return numAnnotations() > 0;
            default: return false;
        }
    }

    @Override
    public int num(int key) {
        switch(key) {
            case 1: return hasComment() ? 1 : 0;
            case 2: return 1;
            case 3: return hasReturnType() ? 1 : 0;
            case 4: return hasName() ? 1 : 0;
            case 5: return numParams();
            case 6: return numExceptions();
            case 7: return numAnnotations();
            default: return 0;
        }
    }

    @Override
    public Object get(int key) {
        switch(key) {
            case 1: return getComment();
            case 2: return isOneWay();
            case 3: return getReturnType();
            case 4: return getName();
            case 5: return getParams();
            case 6: return getExceptions();
            case 7: return getAnnotations();
            default: return null;
        }
    }

    @Override
    public boolean compact() {
        return false;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || !(o instanceof ServiceMethod)) return false;
        ServiceMethod other = (ServiceMethod) o;
        return java.util.Objects.equals(mComment, other.mComment) &&
               java.util.Objects.equals(mOneWay, other.mOneWay) &&
               java.util.Objects.equals(mReturnType, other.mReturnType) &&
               java.util.Objects.equals(mName, other.mName) &&
               java.util.Objects.equals(mParams, other.mParams) &&
               java.util.Objects.equals(mExceptions, other.mExceptions) &&
               java.util.Objects.equals(mAnnotations, other.mAnnotations);
    }

    @Override
    public int hashCode() {
        if (tHashCode == 0) {
            tHashCode = java.util.Objects.hash(
                    ServiceMethod.class,
                    _Field.COMMENT, mComment,
                    _Field.ONE_WAY, mOneWay,
                    _Field.RETURN_TYPE, mReturnType,
                    _Field.NAME, mName,
                    _Field.PARAMS, java.util.Objects.hashCode(mParams),
                    _Field.EXCEPTIONS, java.util.Objects.hashCode(mExceptions),
                    _Field.ANNOTATIONS, java.util.Objects.hashCode(mAnnotations));
        }
        return tHashCode;
    }

    @Override
    public String toString() {
        return "model.ServiceMethod" + asString();
    }

    @Override
    public String asString() {
        StringBuilder out = new StringBuilder();
        out.append("{");

        boolean first = true;
        if (hasComment()) {
            first = false;
            out.append("comment:");
            out.append('\"').append(mComment).append('\"');
        }
        if (hasOneWay()) {
            if (!first) out.append(',');
            first = false;
            out.append("one_way:");
            out.append(mOneWay ? "true" : "false");
        }
        if (hasReturnType()) {
            if (!first) out.append(',');
            first = false;
            out.append("return_type:");
            out.append('\"').append(mReturnType).append('\"');
        }
        if (hasName()) {
            if (!first) out.append(',');
            first = false;
            out.append("name:");
            out.append('\"').append(mName).append('\"');
        }
        if (numParams() > 0) {
            if (!first) out.append(',');
            first = false;
            out.append("params:");
            out.append(net.morimekta.providence.util.TypeUtils.asString(mParams));
        }
        if (numExceptions() > 0) {
            if (!first) out.append(',');
            first = false;
            out.append("exceptions:");
            out.append(net.morimekta.providence.util.TypeUtils.asString(mExceptions));
        }
        if (numAnnotations() > 0) {
            if (!first) out.append(',');
            first = false;
            out.append("annotations:");
            out.append(net.morimekta.providence.util.TypeUtils.asString(mAnnotations));
        }
        out.append('}');
        return out.toString();
    }

    @Override
    public int compareTo(ServiceMethod other) {
        int c;

        c = Boolean.compare(mComment != null, other.mComment != null);
        if (c != 0) return c;
        if (mComment != null) {
            c = mComment.compareTo(other.mComment);
            if (c != 0) return c;
        }

        c = Boolean.compare(mOneWay, other.mOneWay);
        if (c != 0) return c;

        c = Boolean.compare(mReturnType != null, other.mReturnType != null);
        if (c != 0) return c;
        if (mReturnType != null) {
            c = mReturnType.compareTo(other.mReturnType);
            if (c != 0) return c;
        }

        c = Boolean.compare(mName != null, other.mName != null);
        if (c != 0) return c;
        if (mName != null) {
            c = mName.compareTo(other.mName);
            if (c != 0) return c;
        }

        c = Boolean.compare(mParams != null, other.mParams != null);
        if (c != 0) return c;
        if (mParams != null) {
            c = Integer.compare(mParams.hashCode(), other.mParams.hashCode());
            if (c != 0) return c;
        }

        c = Boolean.compare(mExceptions != null, other.mExceptions != null);
        if (c != 0) return c;
        if (mExceptions != null) {
            c = Integer.compare(mExceptions.hashCode(), other.mExceptions.hashCode());
            if (c != 0) return c;
        }

        c = Boolean.compare(mAnnotations != null, other.mAnnotations != null);
        if (c != 0) return c;
        if (mAnnotations != null) {
            c = Integer.compare(mAnnotations.hashCode(), other.mAnnotations.hashCode());
            if (c != 0) return c;
        }

        return 0;
    }

    public enum _Field implements net.morimekta.providence.descriptor.PField {
        COMMENT(1, net.morimekta.providence.descriptor.PRequirement.DEFAULT, "comment", net.morimekta.providence.descriptor.PPrimitive.STRING.provider(), null),
        ONE_WAY(2, net.morimekta.providence.descriptor.PRequirement.DEFAULT, "one_way", net.morimekta.providence.descriptor.PPrimitive.BOOL.provider(), new net.morimekta.providence.descriptor.PDefaultValueProvider<>(kDefaultOneWay)),
        RETURN_TYPE(3, net.morimekta.providence.descriptor.PRequirement.DEFAULT, "return_type", net.morimekta.providence.descriptor.PPrimitive.STRING.provider(), null),
        NAME(4, net.morimekta.providence.descriptor.PRequirement.REQUIRED, "name", net.morimekta.providence.descriptor.PPrimitive.STRING.provider(), null),
        PARAMS(5, net.morimekta.providence.descriptor.PRequirement.DEFAULT, "params", net.morimekta.providence.descriptor.PList.provider(net.morimekta.providence.model.ThriftField.provider()), null),
        EXCEPTIONS(6, net.morimekta.providence.descriptor.PRequirement.DEFAULT, "exceptions", net.morimekta.providence.descriptor.PList.provider(net.morimekta.providence.model.ThriftField.provider()), null),
        ANNOTATIONS(7, net.morimekta.providence.descriptor.PRequirement.DEFAULT, "annotations", net.morimekta.providence.descriptor.PMap.provider(net.morimekta.providence.descriptor.PPrimitive.STRING.provider(),net.morimekta.providence.descriptor.PPrimitive.STRING.provider()), null),
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
            builder.append("ServiceMethod._Field(")
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
                case 1: return _Field.COMMENT;
                case 2: return _Field.ONE_WAY;
                case 3: return _Field.RETURN_TYPE;
                case 4: return _Field.NAME;
                case 5: return _Field.PARAMS;
                case 6: return _Field.EXCEPTIONS;
                case 7: return _Field.ANNOTATIONS;
                default: return null;
            }
        }

        public static _Field forName(String name) {
            switch (name) {
                case "comment": return _Field.COMMENT;
                case "one_way": return _Field.ONE_WAY;
                case "return_type": return _Field.RETURN_TYPE;
                case "name": return _Field.NAME;
                case "params": return _Field.PARAMS;
                case "exceptions": return _Field.EXCEPTIONS;
                case "annotations": return _Field.ANNOTATIONS;
            }
            return null;
        }
    }

    public static net.morimekta.providence.descriptor.PStructDescriptorProvider<ServiceMethod,_Field> provider() {
        return new _Provider();
    }

    @Override
    public net.morimekta.providence.descriptor.PStructDescriptor<ServiceMethod,_Field> descriptor() {
        return kDescriptor;
    }

    public static final net.morimekta.providence.descriptor.PStructDescriptor<ServiceMethod,_Field> kDescriptor;

    private static class _Descriptor
            extends net.morimekta.providence.descriptor.PStructDescriptor<ServiceMethod,_Field> {
        public _Descriptor() {
            super("model", "ServiceMethod", new _Factory(), false, false);
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

    private final static class _Provider extends net.morimekta.providence.descriptor.PStructDescriptorProvider<ServiceMethod,_Field> {
        @Override
        public net.morimekta.providence.descriptor.PStructDescriptor<ServiceMethod,_Field> descriptor() {
            return kDescriptor;
        }
    }

    private final static class _Factory
            extends net.morimekta.providence.PMessageBuilderFactory<ServiceMethod> {
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
            extends net.morimekta.providence.PMessageBuilder<ServiceMethod> {
        private java.util.BitSet optionals;

        private String mComment;
        private boolean mOneWay;
        private String mReturnType;
        private String mName;
        private net.morimekta.providence.descriptor.PList.Builder mParams;
        private net.morimekta.providence.descriptor.PList.Builder mExceptions;
        private net.morimekta.providence.descriptor.PMap.Builder mAnnotations;


        public _Builder() {
            optionals = new java.util.BitSet(7);
            mOneWay = kDefaultOneWay;
            mParams = new net.morimekta.providence.descriptor.PList.ImmutableListBuilder<>();
            mExceptions = new net.morimekta.providence.descriptor.PList.ImmutableListBuilder<>();
            mAnnotations = new net.morimekta.providence.descriptor.PMap.ImmutableMapBuilder<>();
        }

        public _Builder(ServiceMethod base) {
            this();

            if (base.hasComment()) {
                optionals.set(0);
                mComment = base.mComment;
            }
            optionals.set(1);
            mOneWay = base.mOneWay;
            if (base.hasReturnType()) {
                optionals.set(2);
                mReturnType = base.mReturnType;
            }
            if (base.hasName()) {
                optionals.set(3);
                mName = base.mName;
            }
            if (base.numParams() > 0) {
                optionals.set(4);
                mParams.addAll(base.mParams);
            }
            if (base.numExceptions() > 0) {
                optionals.set(5);
                mExceptions.addAll(base.mExceptions);
            }
            if (base.numAnnotations() > 0) {
                optionals.set(6);
                mAnnotations.putAll(base.mAnnotations);
            }
        }

        public _Builder setComment(String value) {
            optionals.set(0);
            mComment = value;
            return this;
        }
        public boolean isSetComment() {
            return optionals.get(0);
        }
        public _Builder clearComment() {
            optionals.set(0, false);
            mComment = null;
            return this;
        }
        public _Builder setOneWay(boolean value) {
            optionals.set(1);
            mOneWay = value;
            return this;
        }
        public boolean isSetOneWay() {
            return optionals.get(1);
        }
        public _Builder clearOneWay() {
            optionals.set(1, false);
            mOneWay = kDefaultOneWay;
            return this;
        }
        public _Builder setReturnType(String value) {
            optionals.set(2);
            mReturnType = value;
            return this;
        }
        public boolean isSetReturnType() {
            return optionals.get(2);
        }
        public _Builder clearReturnType() {
            optionals.set(2, false);
            mReturnType = null;
            return this;
        }
        public _Builder setName(String value) {
            optionals.set(3);
            mName = value;
            return this;
        }
        public boolean isSetName() {
            return optionals.get(3);
        }
        public _Builder clearName() {
            optionals.set(3, false);
            mName = null;
            return this;
        }
        public _Builder setParams(java.util.Collection<net.morimekta.providence.model.ThriftField> value) {
            optionals.set(4);
            mParams.clear();
            mParams.addAll(value);
            return this;
        }
        public _Builder addToParams(net.morimekta.providence.model.ThriftField... values) {
            optionals.set(4);
            for (net.morimekta.providence.model.ThriftField item : values) {
                mParams.add(item);
            }
            return this;
        }

        public boolean isSetParams() {
            return optionals.get(4);
        }
        public _Builder clearParams() {
            optionals.set(4, false);
            mParams.clear();
            return this;
        }
        public _Builder setExceptions(java.util.Collection<net.morimekta.providence.model.ThriftField> value) {
            optionals.set(5);
            mExceptions.clear();
            mExceptions.addAll(value);
            return this;
        }
        public _Builder addToExceptions(net.morimekta.providence.model.ThriftField... values) {
            optionals.set(5);
            for (net.morimekta.providence.model.ThriftField item : values) {
                mExceptions.add(item);
            }
            return this;
        }

        public boolean isSetExceptions() {
            return optionals.get(5);
        }
        public _Builder clearExceptions() {
            optionals.set(5, false);
            mExceptions.clear();
            return this;
        }
        public _Builder setAnnotations(java.util.Map<String,String> value) {
            optionals.set(6);
            mAnnotations.clear();
            mAnnotations.putAll(value);
            return this;
        }
        public _Builder putInAnnotations(String key, String value) {
            optionals.set(6);
            mAnnotations.put(key, value);
            return this;
        }

        public boolean isSetAnnotations() {
            return optionals.get(6);
        }
        public _Builder clearAnnotations() {
            optionals.set(6, false);
            mAnnotations.clear();
            return this;
        }
        @Override
        public _Builder set(int key, Object value) {
            if (value == null) return clear(key);
            switch (key) {
                case 1: setComment((String) value); break;
                case 2: setOneWay((boolean) value); break;
                case 3: setReturnType((String) value); break;
                case 4: setName((String) value); break;
                case 5: setParams((java.util.List<net.morimekta.providence.model.ThriftField>) value); break;
                case 6: setExceptions((java.util.List<net.morimekta.providence.model.ThriftField>) value); break;
                case 7: setAnnotations((java.util.Map<String,String>) value); break;
            }
            return this;
        }

        @Override
        public _Builder addTo(int key, Object value) {
            switch (key) {
                case 5: addToParams((net.morimekta.providence.model.ThriftField) value); break;
                case 6: addToExceptions((net.morimekta.providence.model.ThriftField) value); break;
                default: break;
            }
            return this;
        }

        @Override
        public _Builder clear(int key) {
            switch (key) {
                case 1: clearComment(); break;
                case 2: clearOneWay(); break;
                case 3: clearReturnType(); break;
                case 4: clearName(); break;
                case 5: clearParams(); break;
                case 6: clearExceptions(); break;
                case 7: clearAnnotations(); break;
            }
            return this;
        }

        @Override
        public boolean isValid() {
            return optionals.get(3);
        }

        @Override
        public ServiceMethod build() {
            return new ServiceMethod(this);
        }
    }
}
