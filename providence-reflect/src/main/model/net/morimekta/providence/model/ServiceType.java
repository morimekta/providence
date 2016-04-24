package net.morimekta.providence.model;

/**
 * service (extends <extend>)? {
 *   (<method> [;,]?)*
 * }
 */
@SuppressWarnings("unused")
public class ServiceType
        implements net.morimekta.providence.PMessage<ServiceType>, java.io.Serializable, Comparable<ServiceType> {
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

    public String getComment() {
        return mComment;
    }

    public boolean hasName() {
        return mName != null;
    }

    public String getName() {
        return mName;
    }

    public boolean hasExtend() {
        return mExtend != null;
    }

    public String getExtend() {
        return mExtend;
    }

    public int numMethods() {
        return mMethods != null ? mMethods.size() : 0;
    }

    public java.util.List<net.morimekta.providence.model.ServiceMethod> getMethods() {
        return mMethods;
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
        if (o == null || !(o instanceof ServiceType)) return false;
        ServiceType other = (ServiceType) o;
        return java.util.Objects.equals(mComment, other.mComment) &&
               java.util.Objects.equals(mName, other.mName) &&
               java.util.Objects.equals(mExtend, other.mExtend) &&
               net.morimekta.providence.util.PTypeUtils.equals(mMethods, other.mMethods) &&
               net.morimekta.providence.util.PTypeUtils.equals(mAnnotations, other.mAnnotations);
    }

    @Override
    public int hashCode() {
        if (tHashCode == 0) {
            tHashCode = java.util.Objects.hash(
                    ServiceType.class,
                    _Field.COMMENT, mComment,
                    _Field.NAME, mName,
                    _Field.EXTEND, mExtend,
                    _Field.METHODS, net.morimekta.providence.util.PTypeUtils.hashCode(mMethods),
                    _Field.ANNOTATIONS, net.morimekta.providence.util.PTypeUtils.hashCode(mAnnotations));
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
        if (hasComment()) {
            first = false;
            out.append("comment:");
            out.append('\"').append(mComment).append('\"');
        }
        if (hasName()) {
            if (!first) out.append(',');
            first = false;
            out.append("name:");
            out.append('\"').append(mName).append('\"');
        }
        if (hasExtend()) {
            if (!first) out.append(',');
            first = false;
            out.append("extend:");
            out.append('\"').append(mExtend).append('\"');
        }
        if (numMethods() > 0) {
            if (!first) out.append(',');
            first = false;
            out.append("methods:");
            out.append(net.morimekta.providence.util.PTypeUtils.toString(mMethods));
        }
        if (numAnnotations() > 0) {
            if (!first) out.append(',');
            first = false;
            out.append("annotations:");
            out.append(net.morimekta.providence.util.PTypeUtils.toString(mAnnotations));
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
            builder.append("ServiceType._Field(")
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
                case 2: return _Field.NAME;
                case 3: return _Field.EXTEND;
                case 4: return _Field.METHODS;
                case 5: return _Field.ANNOTATIONS;
                default: return null;
            }
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
            extends net.morimekta.providence.PMessageBuilderFactory<ServiceType> {
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
            extends net.morimekta.providence.PMessageBuilder<ServiceType> {
        private java.util.BitSet optionals;

        private String mComment;
        private String mName;
        private String mExtend;
        private net.morimekta.providence.descriptor.PList.Builder mMethods;
        private net.morimekta.providence.descriptor.PMap.Builder mAnnotations;


        public _Builder() {
            optionals = new java.util.BitSet(5);
            mMethods = new net.morimekta.providence.descriptor.PList.ImmutableListBuilder<>();
            mAnnotations = new net.morimekta.providence.descriptor.PMap.ImmutableMapBuilder<>();
        }

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
        public _Builder setName(String value) {
            optionals.set(1);
            mName = value;
            return this;
        }
        public boolean isSetName() {
            return optionals.get(1);
        }
        public _Builder clearName() {
            optionals.set(1, false);
            mName = null;
            return this;
        }
        public _Builder setExtend(String value) {
            optionals.set(2);
            mExtend = value;
            return this;
        }
        public boolean isSetExtend() {
            return optionals.get(2);
        }
        public _Builder clearExtend() {
            optionals.set(2, false);
            mExtend = null;
            return this;
        }
        public _Builder setMethods(java.util.Collection<net.morimekta.providence.model.ServiceMethod> value) {
            optionals.set(3);
            mMethods.clear();
            mMethods.addAll(value);
            return this;
        }
        public _Builder addToMethods(net.morimekta.providence.model.ServiceMethod... values) {
            optionals.set(3);
            for (net.morimekta.providence.model.ServiceMethod item : values) {
                mMethods.add(item);
            }
            return this;
        }

        public boolean isSetMethods() {
            return optionals.get(3);
        }
        public _Builder clearMethods() {
            optionals.set(3, false);
            mMethods.clear();
            return this;
        }
        public _Builder setAnnotations(java.util.Map<String,String> value) {
            optionals.set(4);
            mAnnotations.clear();
            mAnnotations.putAll(value);
            return this;
        }
        public _Builder putInAnnotations(String key, String value) {
            optionals.set(4);
            mAnnotations.put(key, value);
            return this;
        }

        public boolean isSetAnnotations() {
            return optionals.get(4);
        }
        public _Builder clearAnnotations() {
            optionals.set(4, false);
            mAnnotations.clear();
            return this;
        }
        @Override
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
        public ServiceType build() {
            return new ServiceType(this);
        }
    }
}
