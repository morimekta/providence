package net.morimekta.providence.model;

/**
 * service (extends &lt;extend&gt;)? {
 *   (&lt;method&gt; [;,]?)*
 * }
 */
@SuppressWarnings("unused")
public class ServiceType
        implements net.morimekta.providence.PMessage<ServiceType,ServiceType._Field>,
                   java.io.Serializable,
                   Comparable<ServiceType> {
    private final static long serialVersionUID = 789757775761432238L;

    private final String mComment;
    private final String mName;
    private final String mExtend;
    private final java.util.List<net.morimekta.providence.model.ServiceMethod> mMethods;
    private final java.util.Map<String,String> mAnnotations;
    
    private volatile int tHashCode;

    private ServiceType(_Builder builder) {
        mComment = builder.mComment;
        mName = builder.mName;
        mExtend = builder.mExtend;
        if (builder.isSetMethods()) {
            mMethods = builder.mMethods.build();
        } else {
            mMethods = null;
        }
        if (builder.isSetAnnotations()) {
            mAnnotations = builder.mAnnotations.build();
        } else {
            mAnnotations = null;
        }
    }

    public ServiceType(String pComment,
                       String pName,
                       String pExtend,
                       java.util.List<net.morimekta.providence.model.ServiceMethod> pMethods,
                       java.util.Map<String,String> pAnnotations) {
        mComment = pComment;
        mName = pName;
        mExtend = pExtend;
        if (pMethods != null) {
            mMethods = com.google.common.collect.ImmutableList.copyOf(pMethods);
        } else {
            mMethods = null;
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

    /**
     * @return The field value
     */
    public String getComment() {
        return mComment;
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

    public boolean hasExtend() {
        return mExtend != null;
    }

    /**
     * @return The field value
     */
    public String getExtend() {
        return mExtend;
    }

    public int numMethods() {
        return mMethods != null ? mMethods.size() : 0;
    }

    public boolean hasMethods() {
        return mMethods != null;
    }

    /**
     * @return The field value
     */
    public java.util.List<net.morimekta.providence.model.ServiceMethod> getMethods() {
        return mMethods;
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
            case 2: return hasName();
            case 3: return hasExtend();
            case 4: return numMethods() > 0;
            case 5: return numAnnotations() > 0;
            default: return false;
        }
    }

    @Override
    public int num(int key) {
        switch(key) {
            case 1: return hasComment() ? 1 : 0;
            case 2: return hasName() ? 1 : 0;
            case 3: return hasExtend() ? 1 : 0;
            case 4: return numMethods();
            case 5: return numAnnotations();
            default: return 0;
        }
    }

    @Override
    public Object get(int key) {
        switch(key) {
            case 1: return getComment();
            case 2: return getName();
            case 3: return getExtend();
            case 4: return getMethods();
            case 5: return getAnnotations();
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
        if (o == null || !(o instanceof ServiceType)) return false;
        ServiceType other = (ServiceType) o;
        return java.util.Objects.equals(mComment, other.mComment) &&
               java.util.Objects.equals(mName, other.mName) &&
               java.util.Objects.equals(mExtend, other.mExtend) &&
               java.util.Objects.equals(mMethods, other.mMethods) &&
               java.util.Objects.equals(mAnnotations, other.mAnnotations);
    }

    @Override
    public int hashCode() {
        if (tHashCode == 0) {
            tHashCode = java.util.Objects.hash(
                    ServiceType.class,
                    _Field.COMMENT, mComment,
                    _Field.NAME, mName,
                    _Field.EXTEND, mExtend,
                    _Field.METHODS, mMethods,
                    _Field.ANNOTATIONS, mAnnotations);
        }
        return tHashCode;
    }

    @Override
    public String toString() {
        return "model.ServiceType" + asString();
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
        if (mName != null) {
            if (first) first = false;
            else out.append(',');
            out.append("name:")
               .append('\"')
               .append(net.morimekta.util.Strings.escape(mName))
               .append('\"');
        }
        if (mExtend != null) {
            if (first) first = false;
            else out.append(',');
            out.append("extend:")
               .append('\"')
               .append(net.morimekta.util.Strings.escape(mExtend))
               .append('\"');
        }
        if (mMethods != null && mMethods.size() > 0) {
            if (first) first = false;
            else out.append(',');
            out.append("methods:")
               .append(net.morimekta.util.Strings.asString(mMethods));
        }
        if (mAnnotations != null && mAnnotations.size() > 0) {
            if (!first) out.append(',');
            out.append("annotations:")
               .append(net.morimekta.util.Strings.asString(mAnnotations));
        }
        out.append('}');
        return out.toString();
    }

    @Override
    public int compareTo(ServiceType other) {
        int c;

        c = Boolean.compare(mComment != null, other.mComment != null);
        if (c != 0) return c;
        if (mComment != null) {
            c = mComment.compareTo(other.mComment);
            if (c != 0) return c;
        }

        c = Boolean.compare(mName != null, other.mName != null);
        if (c != 0) return c;
        if (mName != null) {
            c = mName.compareTo(other.mName);
            if (c != 0) return c;
        }

        c = Boolean.compare(mExtend != null, other.mExtend != null);
        if (c != 0) return c;
        if (mExtend != null) {
            c = mExtend.compareTo(other.mExtend);
            if (c != 0) return c;
        }

        c = Boolean.compare(mMethods != null, other.mMethods != null);
        if (c != 0) return c;
        if (mMethods != null) {
            c = Integer.compare(mMethods.hashCode(), other.mMethods.hashCode());
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
        NAME(2, net.morimekta.providence.descriptor.PRequirement.REQUIRED, "name", net.morimekta.providence.descriptor.PPrimitive.STRING.provider(), null),
        EXTEND(3, net.morimekta.providence.descriptor.PRequirement.DEFAULT, "extend", net.morimekta.providence.descriptor.PPrimitive.STRING.provider(), null),
        METHODS(4, net.morimekta.providence.descriptor.PRequirement.DEFAULT, "methods", net.morimekta.providence.descriptor.PList.provider(net.morimekta.providence.model.ServiceMethod.provider()), null),
        ANNOTATIONS(5, net.morimekta.providence.descriptor.PRequirement.DEFAULT, "annotations", net.morimekta.providence.descriptor.PMap.provider(net.morimekta.providence.descriptor.PPrimitive.STRING.provider(),net.morimekta.providence.descriptor.PPrimitive.STRING.provider()), null),
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
                case 2: return _Field.NAME;
                case 3: return _Field.EXTEND;
                case 4: return _Field.METHODS;
                case 5: return _Field.ANNOTATIONS;
            }
            return null;
        }

        public static _Field forName(String name) {
            switch (name) {
                case "comment": return _Field.COMMENT;
                case "name": return _Field.NAME;
                case "extend": return _Field.EXTEND;
                case "methods": return _Field.METHODS;
                case "annotations": return _Field.ANNOTATIONS;
            }
            return null;
        }
    }

    public static net.morimekta.providence.descriptor.PStructDescriptorProvider<ServiceType,_Field> provider() {
        return new _Provider();
    }

    @Override
    public net.morimekta.providence.descriptor.PStructDescriptor<ServiceType,_Field> descriptor() {
        return kDescriptor;
    }

    public static final net.morimekta.providence.descriptor.PStructDescriptor<ServiceType,_Field> kDescriptor;

    private static class _Descriptor
            extends net.morimekta.providence.descriptor.PStructDescriptor<ServiceType,_Field> {
        public _Descriptor() {
            super("model", "ServiceType", new _Factory(), false, false);
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

    private final static class _Provider extends net.morimekta.providence.descriptor.PStructDescriptorProvider<ServiceType,_Field> {
        @Override
        public net.morimekta.providence.descriptor.PStructDescriptor<ServiceType,_Field> descriptor() {
            return kDescriptor;
        }
    }

    private final static class _Factory
            extends net.morimekta.providence.PMessageBuilderFactory<ServiceType,_Field> {
        @Override
        public _Builder builder() {
            return new _Builder();
        }
    }

    @Override
    public _Builder mutate() {
        return new _Builder(this);
    }

    /**
     * Make a model.ServiceType builder.
     * @return The builder instance.
     */
    public static _Builder builder() {
        return new _Builder();
    }

    public static class _Builder
            extends net.morimekta.providence.PMessageBuilder<ServiceType,_Field> {
        private java.util.BitSet optionals;

        private String mComment;
        private String mName;
        private String mExtend;
        private net.morimekta.providence.descriptor.PList.Builder<net.morimekta.providence.model.ServiceMethod> mMethods;
        private net.morimekta.providence.descriptor.PMap.Builder<String,String> mAnnotations;

        /**
         * Make a model.ServiceType builder.
         */
        public _Builder() {
            optionals = new java.util.BitSet(5);
            mMethods = new net.morimekta.providence.descriptor.PList.ImmutableListBuilder<>();
            mAnnotations = new net.morimekta.providence.descriptor.PMap.ImmutableMapBuilder<>();
        }

        /**
         * Make a mutating builder off a base model.ServiceType.
         *
         * @param base The base ServiceType
         */
        public _Builder(ServiceType base) {
            this();

            if (base.hasComment()) {
                optionals.set(0);
                mComment = base.mComment;
            }
            if (base.hasName()) {
                optionals.set(1);
                mName = base.mName;
            }
            if (base.hasExtend()) {
                optionals.set(2);
                mExtend = base.mExtend;
            }
            if (base.numMethods() > 0) {
                optionals.set(3);
                mMethods.addAll(base.mMethods);
            }
            if (base.numAnnotations() > 0) {
                optionals.set(4);
                mAnnotations.putAll(base.mAnnotations);
            }
        }

        @Override
        public _Builder merge(ServiceType from) {
            if (from.hasComment()) {
                optionals.set(0);
                mComment = from.getComment();
            }

            if (from.hasName()) {
                optionals.set(1);
                mName = from.getName();
            }

            if (from.hasExtend()) {
                optionals.set(2);
                mExtend = from.getExtend();
            }

            if (from.hasMethods()) {
                optionals.set(3);
                mMethods.clear();
                mMethods.addAll(from.getMethods());
            }

            if (from.hasAnnotations()) {
                optionals.set(4);
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
         * Sets the value of name.
         *
         * @param value The new value
         * @return The builder
         */
        public _Builder setName(String value) {
            optionals.set(1);
            mName = value;
            return this;
        }

        /**
         * Checks for presence of the name field.
         *
         * @return True iff name has been set.
         */
        public boolean isSetName() {
            return optionals.get(1);
        }

        /**
         * Clears the name field.
         *
         * @return The builder
         */
        public _Builder clearName() {
            optionals.clear(1);
            mName = null;
            return this;
        }

        /**
         * Sets the value of extend.
         *
         * @param value The new value
         * @return The builder
         */
        public _Builder setExtend(String value) {
            optionals.set(2);
            mExtend = value;
            return this;
        }

        /**
         * Checks for presence of the extend field.
         *
         * @return True iff extend has been set.
         */
        public boolean isSetExtend() {
            return optionals.get(2);
        }

        /**
         * Clears the extend field.
         *
         * @return The builder
         */
        public _Builder clearExtend() {
            optionals.clear(2);
            mExtend = null;
            return this;
        }

        /**
         * Sets the value of methods.
         *
         * @param value The new value
         * @return The builder
         */
        public _Builder setMethods(java.util.Collection<net.morimekta.providence.model.ServiceMethod> value) {
            optionals.set(3);
            mMethods.clear();
            mMethods.addAll(value);
            return this;
        }

        /**
         * Adds entries to methods.
         *
         * @param values The added value
         * @return The builder
         */
        public _Builder addToMethods(net.morimekta.providence.model.ServiceMethod... values) {
            optionals.set(3);
            for (net.morimekta.providence.model.ServiceMethod item : values) {
                mMethods.add(item);
            }
            return this;
        }

        /**
         * Checks for presence of the methods field.
         *
         * @return True iff methods has been set.
         */
        public boolean isSetMethods() {
            return optionals.get(3);
        }

        /**
         * Clears the methods field.
         *
         * @return The builder
         */
        public _Builder clearMethods() {
            optionals.clear(3);
            mMethods.clear();
            return this;
        }

        /**
         * Gets the builder for the contained methods.
         *
         * @return The field builder
         */
        public net.morimekta.providence.descriptor.PList.Builder<net.morimekta.providence.model.ServiceMethod> mutableMethods() {
            optionals.set(3);
            return mMethods;
        }

        /**
         * Sets the value of annotations.
         *
         * @param value The new value
         * @return The builder
         */
        public _Builder setAnnotations(java.util.Map<String,String> value) {
            optionals.set(4);
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
            optionals.set(4);
            mAnnotations.put(key, value);
            return this;
        }

        /**
         * Checks for presence of the annotations field.
         *
         * @return True iff annotations has been set.
         */
        public boolean isSetAnnotations() {
            return optionals.get(4);
        }

        /**
         * Clears the annotations field.
         *
         * @return The builder
         */
        public _Builder clearAnnotations() {
            optionals.clear(4);
            mAnnotations.clear();
            return this;
        }

        /**
         * Gets the builder for the contained annotations.
         *
         * @return The field builder
         */
        public net.morimekta.providence.descriptor.PMap.Builder<String,String> mutableAnnotations() {
            optionals.set(4);
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
                case 2: setName((String) value); break;
                case 3: setExtend((String) value); break;
                case 4: setMethods((java.util.List<net.morimekta.providence.model.ServiceMethod>) value); break;
                case 5: setAnnotations((java.util.Map<String,String>) value); break;
            }
            return this;
        }

        @Override
        public _Builder addTo(int key, Object value) {
            switch (key) {
                case 4: addToMethods((net.morimekta.providence.model.ServiceMethod) value); break;
                default: break;
            }
            return this;
        }

        @Override
        public _Builder clear(int key) {
            switch (key) {
                case 1: clearComment(); break;
                case 2: clearName(); break;
                case 3: clearExtend(); break;
                case 4: clearMethods(); break;
                case 5: clearAnnotations(); break;
            }
            return this;
        }

        @Override
        public boolean isValid() {
            return optionals.get(1);
        }

        @Override
        public void validate() {
            if (!isValid()) {
                java.util.LinkedList<String> missing = new java.util.LinkedList<>();

                if (!optionals.get(1)) {
                    missing.add("name");
                }

                throw new java.lang.IllegalStateException(
                        "Missing required fields " +
                        String.join(",", missing) +
                        " in message model.ServiceType");
            }
        }

        @Override
        public net.morimekta.providence.descriptor.PStructDescriptor<ServiceType,_Field> descriptor() {
            return kDescriptor;
        }

        @Override
        public ServiceType build() {
            return new ServiceType(this);
        }
    }
}
