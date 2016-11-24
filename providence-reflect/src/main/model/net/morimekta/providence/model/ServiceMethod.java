package net.morimekta.providence.model;

/**
 * (oneway)? &lt;return_type&gt; &lt;name&gt;&#39;(&#39;&lt;param&gt;*&#39;)&#39; (throws &#39;(&#39; &lt;exception&gt;+ &#39;)&#39;)?
 */
@SuppressWarnings("unused")
public class ServiceMethod
        implements net.morimekta.providence.PMessage<ServiceMethod,ServiceMethod._Field>,
                   Comparable<ServiceMethod>,
                   java.io.Serializable {
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

    public ServiceMethod(String pComment,
                         Boolean pOneWay,
                         String pReturnType,
                         String pName,
                         java.util.List<net.morimekta.providence.model.ThriftField> pParams,
                         java.util.List<net.morimekta.providence.model.ThriftField> pExceptions,
                         java.util.Map<String,String> pAnnotations) {
        mComment = pComment;
        if (pOneWay != null) {
            mOneWay = pOneWay;
        } else {
            mOneWay = kDefaultOneWay;
        }
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

    public boolean hasComment() {
        return mComment != null;
    }

    /**
     * @return The field value
     */
    public String getComment() {
        return mComment;
    }

    public boolean hasOneWay() {
        return true;
    }

    /**
     * @return The field value
     */
    public boolean isOneWay() {
        return mOneWay;
    }

    public boolean hasReturnType() {
        return mReturnType != null;
    }

    /**
     * @return The field value
     */
    public String getReturnType() {
        return mReturnType;
    }

    public boolean hasName() {
        return mName != null;
    }

    /**
     * @return The field value
     */
    public String getName() {
        return mName;
    }

    public int numParams() {
        return mParams != null ? mParams.size() : 0;
    }

    public boolean hasParams() {
        return mParams != null;
    }

    /**
     * @return The field value
     */
    public java.util.List<net.morimekta.providence.model.ThriftField> getParams() {
        return mParams;
    }

    public int numExceptions() {
        return mExceptions != null ? mExceptions.size() : 0;
    }

    public boolean hasExceptions() {
        return mExceptions != null;
    }

    /**
     * @return The field value
     */
    public java.util.List<net.morimekta.providence.model.ThriftField> getExceptions() {
        return mExceptions;
    }

    public int numAnnotations() {
        return mAnnotations != null ? mAnnotations.size() : 0;
    }

    public boolean hasAnnotations() {
        return mAnnotations != null;
    }

    /**
     * @return The field value
     */
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
        if (o == this) return true;
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
                    _Field.PARAMS, mParams,
                    _Field.EXCEPTIONS, mExceptions,
                    _Field.ANNOTATIONS, mAnnotations);
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
        if (mComment != null) {
            first = false;
            out.append("comment:")
               .append('\"')
               .append(net.morimekta.util.Strings.escape(mComment))
               .append('\"');
        }
        out.append("one_way:")
           .append(mOneWay);
        if (mReturnType != null) {
            out.append(',');
            out.append("return_type:")
               .append('\"')
               .append(net.morimekta.util.Strings.escape(mReturnType))
               .append('\"');
        }
        if (mName != null) {
            out.append(',');
            out.append("name:")
               .append('\"')
               .append(net.morimekta.util.Strings.escape(mName))
               .append('\"');
        }
        if (mParams != null && mParams.size() > 0) {
            out.append(',');
            out.append("params:")
               .append(net.morimekta.util.Strings.asString(mParams));
        }
        if (mExceptions != null && mExceptions.size() > 0) {
            out.append(',');
            out.append("exceptions:")
               .append(net.morimekta.util.Strings.asString(mExceptions));
        }
        if (mAnnotations != null && mAnnotations.size() > 0) {
            out.append(',');
            out.append("annotations:")
               .append(net.morimekta.util.Strings.asString(mAnnotations));
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

    @Override
    public _Builder mutate() {
        return new _Builder(this);
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
                case 1: return _Field.COMMENT;
                case 2: return _Field.ONE_WAY;
                case 3: return _Field.RETURN_TYPE;
                case 4: return _Field.NAME;
                case 5: return _Field.PARAMS;
                case 6: return _Field.EXCEPTIONS;
                case 7: return _Field.ANNOTATIONS;
            }
            return null;
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
            extends net.morimekta.providence.PMessageBuilderFactory<ServiceMethod,_Field> {
        @Override
        public _Builder builder() {
            return new _Builder();
        }
    }

    /**
     * Make a model.ServiceMethod builder.
     * @return The builder instance.
     */
    public static _Builder builder() {
        return new _Builder();
    }

    /**
     * (oneway)? &lt;return_type&gt; &lt;name&gt;&#39;(&#39;&lt;param&gt;*&#39;)&#39; (throws &#39;(&#39; &lt;exception&gt;+ &#39;)&#39;)?
     */
    public static class _Builder
            extends net.morimekta.providence.PMessageBuilder<ServiceMethod,_Field> {
        private java.util.BitSet optionals;

        private String mComment;
        private boolean mOneWay;
        private String mReturnType;
        private String mName;
        private net.morimekta.providence.descriptor.PList.Builder<net.morimekta.providence.model.ThriftField> mParams;
        private net.morimekta.providence.descriptor.PList.Builder<net.morimekta.providence.model.ThriftField> mExceptions;
        private net.morimekta.providence.descriptor.PMap.Builder<String,String> mAnnotations;

        /**
         * Make a model.ServiceMethod builder.
         */
        public _Builder() {
            optionals = new java.util.BitSet(7);
            mOneWay = kDefaultOneWay;
            mParams = new net.morimekta.providence.descriptor.PList.ImmutableListBuilder<>();
            mExceptions = new net.morimekta.providence.descriptor.PList.ImmutableListBuilder<>();
            mAnnotations = new net.morimekta.providence.descriptor.PMap.ImmutableMapBuilder<>();
        }

        /**
         * Make a mutating builder off a base model.ServiceMethod.
         *
         * @param base The base ServiceMethod
         */
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

        @Override
        public _Builder merge(ServiceMethod from) {
            if (from.hasComment()) {
                optionals.set(0);
                mComment = from.getComment();
            }

            optionals.set(1);
            mOneWay = from.isOneWay();

            if (from.hasReturnType()) {
                optionals.set(2);
                mReturnType = from.getReturnType();
            }

            if (from.hasName()) {
                optionals.set(3);
                mName = from.getName();
            }

            if (from.hasParams()) {
                optionals.set(4);
                mParams.clear();
                mParams.addAll(from.getParams());
            }

            if (from.hasExceptions()) {
                optionals.set(5);
                mExceptions.clear();
                mExceptions.addAll(from.getExceptions());
            }

            if (from.hasAnnotations()) {
                optionals.set(6);
                mAnnotations.putAll(from.getAnnotations());
            }
            return this;
        }

        /**
         * Sets the value of comment.
         *
         * @param value The new value
         * @return The builder
         */
        public _Builder setComment(String value) {
            optionals.set(0);
            mComment = value;
            return this;
        }

        /**
         * Checks for presence of the comment field.
         *
         * @return True iff comment has been set.
         */
        public boolean isSetComment() {
            return optionals.get(0);
        }

        /**
         * Clears the comment field.
         *
         * @return The builder
         */
        public _Builder clearComment() {
            optionals.clear(0);
            mComment = null;
            return this;
        }

        /**
         * Sets the value of one_way.
         *
         * @param value The new value
         * @return The builder
         */
        public _Builder setOneWay(boolean value) {
            optionals.set(1);
            mOneWay = value;
            return this;
        }

        /**
         * Checks for presence of the one_way field.
         *
         * @return True iff one_way has been set.
         */
        public boolean isSetOneWay() {
            return optionals.get(1);
        }

        /**
         * Clears the one_way field.
         *
         * @return The builder
         */
        public _Builder clearOneWay() {
            optionals.clear(1);
            mOneWay = kDefaultOneWay;
            return this;
        }

        /**
         * Sets the value of return_type.
         *
         * @param value The new value
         * @return The builder
         */
        public _Builder setReturnType(String value) {
            optionals.set(2);
            mReturnType = value;
            return this;
        }

        /**
         * Checks for presence of the return_type field.
         *
         * @return True iff return_type has been set.
         */
        public boolean isSetReturnType() {
            return optionals.get(2);
        }

        /**
         * Clears the return_type field.
         *
         * @return The builder
         */
        public _Builder clearReturnType() {
            optionals.clear(2);
            mReturnType = null;
            return this;
        }

        /**
         * Sets the value of name.
         *
         * @param value The new value
         * @return The builder
         */
        public _Builder setName(String value) {
            optionals.set(3);
            mName = value;
            return this;
        }

        /**
         * Checks for presence of the name field.
         *
         * @return True iff name has been set.
         */
        public boolean isSetName() {
            return optionals.get(3);
        }

        /**
         * Clears the name field.
         *
         * @return The builder
         */
        public _Builder clearName() {
            optionals.clear(3);
            mName = null;
            return this;
        }

        /**
         * Sets the value of params.
         *
         * @param value The new value
         * @return The builder
         */
        public _Builder setParams(java.util.Collection<net.morimekta.providence.model.ThriftField> value) {
            optionals.set(4);
            mParams.clear();
            mParams.addAll(value);
            return this;
        }

        /**
         * Adds entries to params.
         *
         * @param values The added value
         * @return The builder
         */
        public _Builder addToParams(net.morimekta.providence.model.ThriftField... values) {
            optionals.set(4);
            for (net.morimekta.providence.model.ThriftField item : values) {
                mParams.add(item);
            }
            return this;
        }

        /**
         * Checks for presence of the params field.
         *
         * @return True iff params has been set.
         */
        public boolean isSetParams() {
            return optionals.get(4);
        }

        /**
         * Clears the params field.
         *
         * @return The builder
         */
        public _Builder clearParams() {
            optionals.clear(4);
            mParams.clear();
            return this;
        }

        /**
         * Gets the builder for the contained params.
         *
         * @return The field builder
         */
        public net.morimekta.providence.descriptor.PList.Builder<net.morimekta.providence.model.ThriftField> mutableParams() {
            optionals.set(4);
            return mParams;
        }

        /**
         * Sets the value of exceptions.
         *
         * @param value The new value
         * @return The builder
         */
        public _Builder setExceptions(java.util.Collection<net.morimekta.providence.model.ThriftField> value) {
            optionals.set(5);
            mExceptions.clear();
            mExceptions.addAll(value);
            return this;
        }

        /**
         * Adds entries to exceptions.
         *
         * @param values The added value
         * @return The builder
         */
        public _Builder addToExceptions(net.morimekta.providence.model.ThriftField... values) {
            optionals.set(5);
            for (net.morimekta.providence.model.ThriftField item : values) {
                mExceptions.add(item);
            }
            return this;
        }

        /**
         * Checks for presence of the exceptions field.
         *
         * @return True iff exceptions has been set.
         */
        public boolean isSetExceptions() {
            return optionals.get(5);
        }

        /**
         * Clears the exceptions field.
         *
         * @return The builder
         */
        public _Builder clearExceptions() {
            optionals.clear(5);
            mExceptions.clear();
            return this;
        }

        /**
         * Gets the builder for the contained exceptions.
         *
         * @return The field builder
         */
        public net.morimekta.providence.descriptor.PList.Builder<net.morimekta.providence.model.ThriftField> mutableExceptions() {
            optionals.set(5);
            return mExceptions;
        }

        /**
         * Sets the value of annotations.
         *
         * @param value The new value
         * @return The builder
         */
        public _Builder setAnnotations(java.util.Map<String,String> value) {
            optionals.set(6);
            mAnnotations.clear();
            mAnnotations.putAll(value);
            return this;
        }

        /**
         * Adds a mapping to annotations.
         *
         * @param key The inserted key
         * @param value The inserted value
         * @return The builder
         */
        public _Builder putInAnnotations(String key, String value) {
            optionals.set(6);
            mAnnotations.put(key, value);
            return this;
        }

        /**
         * Checks for presence of the annotations field.
         *
         * @return True iff annotations has been set.
         */
        public boolean isSetAnnotations() {
            return optionals.get(6);
        }

        /**
         * Clears the annotations field.
         *
         * @return The builder
         */
        public _Builder clearAnnotations() {
            optionals.clear(6);
            mAnnotations.clear();
            return this;
        }

        /**
         * Gets the builder for the contained annotations.
         *
         * @return The field builder
         */
        public net.morimekta.providence.descriptor.PMap.Builder<String,String> mutableAnnotations() {
            optionals.set(6);
            return mAnnotations;
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
        public void validate() {
            if (!isValid()) {
                java.util.LinkedList<String> missing = new java.util.LinkedList<>();

                if (!optionals.get(3)) {
                    missing.add("name");
                }

                throw new java.lang.IllegalStateException(
                        "Missing required fields " +
                        String.join(",", missing) +
                        " in message model.ServiceMethod");
            }
        }

        @Override
        public net.morimekta.providence.descriptor.PStructDescriptor<ServiceMethod,_Field> descriptor() {
            return kDescriptor;
        }

        @Override
        public ServiceMethod build() {
            return new ServiceMethod(this);
        }
    }
}
