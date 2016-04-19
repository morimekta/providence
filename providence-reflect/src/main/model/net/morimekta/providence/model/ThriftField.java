package net.morimekta.providence.model;

/**
 * For fields:
 *   (<key>:)? (required|optional)? <type> <name> (= <default_value>)?
 * For const:
 *   const <type> <name> = <default_value>
 * 
 * Fields without key is assigned values ranging from 65335 and down (2^16-1)
 * in order of appearance. Because of the "in order of appearance" the field
 * *must* be filled by the IDL parser.
 * 
 * Consts are always given the key '0'.
 */
@SuppressWarnings("unused")
public class ThriftField
        implements net.morimekta.providence.PMessage<ThriftField>, java.io.Serializable, Comparable<ThriftField> {
    private final static long serialVersionUID = 5114028868232611868L;

    private final static int kDefaultKey = 0;
    private final static net.morimekta.providence.model.Requirement kDefaultRequirement = net.morimekta.providence.model.Requirement.DEFAULT;

    private final String mComment;
    private final int mKey;
    private final net.morimekta.providence.model.Requirement mRequirement;
    private final String mType;
    private final String mName;
    private final String mDefaultValue;
    private final java.util.Map<String,String> mAnnotations;
    
    private volatile int tHashCode;

    private ThriftField(_Builder builder) {
        mComment = builder.mComment;
        mKey = builder.mKey;
        mRequirement = builder.mRequirement;
        mType = builder.mType;
        mName = builder.mName;
        mDefaultValue = builder.mDefaultValue;
        if (builder.isSetAnnotations()) {
            mAnnotations = builder.mAnnotations.build();
        } else {
            mAnnotations = null;
        }
    }

    public ThriftField(String pComment,
                       int pKey,
                       net.morimekta.providence.model.Requirement pRequirement,
                       String pType,
                       String pName,
                       String pDefaultValue,
                       java.util.Map<String,String> pAnnotations) {
        mComment = pComment;
        mKey = pKey;
        mRequirement = pRequirement;
        mType = pType;
        mName = pName;
        mDefaultValue = pDefaultValue;
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

    public boolean hasKey() {
        return true;
    }

    public int getKey() {
        return mKey;
    }

    public boolean hasRequirement() {
        return mRequirement != null;
    }

    public net.morimekta.providence.model.Requirement getRequirement() {
        return hasRequirement() ? mRequirement : kDefaultRequirement;
    }

    public boolean hasType() {
        return mType != null;
    }

    public String getType() {
        return mType;
    }

    public boolean hasName() {
        return mName != null;
    }

    public String getName() {
        return mName;
    }

    public boolean hasDefaultValue() {
        return mDefaultValue != null;
    }

    public String getDefaultValue() {
        return mDefaultValue;
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
            case 3: return hasRequirement();
            case 4: return hasType();
            case 5: return hasName();
            case 6: return hasDefaultValue();
            case 7: return numAnnotations() > 0;
            default: return false;
        }
    }

    @Override
    public int num(int key) {
        switch(key) {
            case 1: return hasComment() ? 1 : 0;
            case 2: return 1;
            case 3: return hasRequirement() ? 1 : 0;
            case 4: return hasType() ? 1 : 0;
            case 5: return hasName() ? 1 : 0;
            case 6: return hasDefaultValue() ? 1 : 0;
            case 7: return numAnnotations();
            default: return 0;
        }
    }

    @Override
    public Object get(int key) {
        switch(key) {
            case 1: return getComment();
            case 2: return getKey();
            case 3: return getRequirement();
            case 4: return getType();
            case 5: return getName();
            case 6: return getDefaultValue();
            case 7: return getAnnotations();
            default: return null;
        }
    }

    @Override
    public boolean isCompact() {
        return false;
    }

    @Override
    public boolean isSimple() {
        return descriptor().isSimple();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || !(o instanceof ThriftField)) return false;
        ThriftField other = (ThriftField) o;
        return java.util.Objects.equals(mComment, other.mComment) &&
               java.util.Objects.equals(mKey, other.mKey) &&
               java.util.Objects.equals(mRequirement, other.mRequirement) &&
               java.util.Objects.equals(mType, other.mType) &&
               java.util.Objects.equals(mName, other.mName) &&
               java.util.Objects.equals(mDefaultValue, other.mDefaultValue) &&
               net.morimekta.providence.util.PTypeUtils.equals(mAnnotations, other.mAnnotations);
    }

    @Override
    public int hashCode() {
        if (tHashCode == 0) {
            tHashCode = java.util.Objects.hash(
                    ThriftField.class,
                    _Field.COMMENT, mComment,
                    _Field.KEY, mKey,
                    _Field.REQUIREMENT, mRequirement,
                    _Field.TYPE, mType,
                    _Field.NAME, mName,
                    _Field.DEFAULT_VALUE, mDefaultValue,
                    _Field.ANNOTATIONS, net.morimekta.providence.util.PTypeUtils.hashCode(mAnnotations));
        }
        return tHashCode;
    }

    @Override
    public String toString() {
        return "model.ThriftField" + asString();
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
        if (hasKey()) {
            if (!first) out.append(',');
            first = false;
            out.append("key:");
            out.append(Integer.toString(mKey));
        }
        if (hasRequirement()) {
            if (!first) out.append(',');
            first = false;
            out.append("requirement:");
            out.append(mRequirement.getName());
        }
        if (hasType()) {
            if (!first) out.append(',');
            first = false;
            out.append("type:");
            out.append('\"').append(mType).append('\"');
        }
        if (hasName()) {
            if (!first) out.append(',');
            first = false;
            out.append("name:");
            out.append('\"').append(mName).append('\"');
        }
        if (hasDefaultValue()) {
            if (!first) out.append(',');
            first = false;
            out.append("default_value:");
            out.append('\"').append(mDefaultValue).append('\"');
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
    public int compareTo(ThriftField other) {
        int c;

        c = Boolean.compare(mComment != null, other.mComment != null);
        if (c != 0) return c;
        if (mComment != null) {
            c = mComment.compareTo(other.mComment);
            if (c != 0) return c;
        }

        c = Integer.compare(mKey, other.mKey);
        if (c != 0) return c;

        c = Boolean.compare(mRequirement != null, other.mRequirement != null);
        if (c != 0) return c;
        if (mRequirement != null) {
            c = Integer.compare(mRequirement.getValue(), mRequirement.getValue());
            if (c != 0) return c;
        }

        c = Boolean.compare(mType != null, other.mType != null);
        if (c != 0) return c;
        if (mType != null) {
            c = mType.compareTo(other.mType);
            if (c != 0) return c;
        }

        c = Boolean.compare(mName != null, other.mName != null);
        if (c != 0) return c;
        if (mName != null) {
            c = mName.compareTo(other.mName);
            if (c != 0) return c;
        }

        c = Boolean.compare(mDefaultValue != null, other.mDefaultValue != null);
        if (c != 0) return c;
        if (mDefaultValue != null) {
            c = mDefaultValue.compareTo(other.mDefaultValue);
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
        KEY(2, net.morimekta.providence.descriptor.PRequirement.REQUIRED, "key", net.morimekta.providence.descriptor.PPrimitive.I32.provider(), null),
        REQUIREMENT(3, net.morimekta.providence.descriptor.PRequirement.DEFAULT, "requirement", net.morimekta.providence.model.Requirement.provider(), new net.morimekta.providence.descriptor.PDefaultValueProvider<>(kDefaultRequirement)),
        TYPE(4, net.morimekta.providence.descriptor.PRequirement.REQUIRED, "type", net.morimekta.providence.descriptor.PPrimitive.STRING.provider(), null),
        NAME(5, net.morimekta.providence.descriptor.PRequirement.REQUIRED, "name", net.morimekta.providence.descriptor.PPrimitive.STRING.provider(), null),
        DEFAULT_VALUE(6, net.morimekta.providence.descriptor.PRequirement.DEFAULT, "default_value", net.morimekta.providence.descriptor.PPrimitive.STRING.provider(), null),
        ANNOTATIONS(7, net.morimekta.providence.descriptor.PRequirement.DEFAULT, "annotations", net.morimekta.providence.descriptor.PMap.provider(net.morimekta.providence.descriptor.PPrimitive.STRING.provider(),net.morimekta.providence.descriptor.PPrimitive.STRING.provider()), null),
        ;

        private final int mKey;
        private final net.morimekta.providence.descriptor.PRequirement mRequired;
        private final String mName;
        private final net.morimekta.providence.descriptor.PDescriptorProvider<?> mTypeProvider;
        private final net.morimekta.providence.descriptor.PValueProvider<?> mDefaultValue;

        _Field(int key, net.morimekta.providence.descriptor.PRequirement required, String name, net.morimekta.providence.descriptor.PDescriptorProvider<?> typeProvider, net.morimekta.providence.descriptor.PValueProvider<?> defaultValue) {
            mKey = key;
            mRequired = required;
            mName = name;
            mTypeProvider = typeProvider;
            mDefaultValue = defaultValue;
        }

        @Override
        public String getComment() { return null; }

        @Override
        public int getKey() { return mKey; }

        @Override
        public net.morimekta.providence.descriptor.PRequirement getRequirement() { return mRequired; }

        @Override
        public net.morimekta.providence.PType getType() { return getDescriptor().getType(); }

        @Override
        public net.morimekta.providence.descriptor.PDescriptor<?> getDescriptor() { return mTypeProvider.descriptor(); }

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
            builder.append("ThriftField._Field(")
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
                case 2: return _Field.KEY;
                case 3: return _Field.REQUIREMENT;
                case 4: return _Field.TYPE;
                case 5: return _Field.NAME;
                case 6: return _Field.DEFAULT_VALUE;
                case 7: return _Field.ANNOTATIONS;
                default: return null;
            }
        }

        public static _Field forName(String name) {
            switch (name) {
                case "comment": return _Field.COMMENT;
                case "key": return _Field.KEY;
                case "requirement": return _Field.REQUIREMENT;
                case "type": return _Field.TYPE;
                case "name": return _Field.NAME;
                case "default_value": return _Field.DEFAULT_VALUE;
                case "annotations": return _Field.ANNOTATIONS;
            }
            return null;
        }
    }

    public static net.morimekta.providence.descriptor.PStructDescriptorProvider<ThriftField,_Field> provider() {
        return new _Provider();
    }

    @Override
    public net.morimekta.providence.descriptor.PStructDescriptor<ThriftField,_Field> descriptor() {
        return kDescriptor;
    }

    public static final net.morimekta.providence.descriptor.PStructDescriptor<ThriftField,_Field> kDescriptor;

    private static class _Descriptor
            extends net.morimekta.providence.descriptor.PStructDescriptor<ThriftField,_Field> {
        public _Descriptor() {
            super(null, "model", "ThriftField", new _Factory(), false, false);
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

    private final static class _Provider extends net.morimekta.providence.descriptor.PStructDescriptorProvider<ThriftField,_Field> {
        @Override
        public net.morimekta.providence.descriptor.PStructDescriptor<ThriftField,_Field> descriptor() {
            return kDescriptor;
        }
    }

    private final static class _Factory
            extends net.morimekta.providence.PMessageBuilderFactory<ThriftField> {
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
            extends net.morimekta.providence.PMessageBuilder<ThriftField> {
        private java.util.BitSet optionals;

        private String mComment;
        private int mKey;
        private net.morimekta.providence.model.Requirement mRequirement;
        private String mType;
        private String mName;
        private String mDefaultValue;
        private net.morimekta.providence.descriptor.PMap.Builder mAnnotations;


        public _Builder() {
            optionals = new java.util.BitSet(7);
            mKey = kDefaultKey;
            mAnnotations = new net.morimekta.providence.descriptor.PMap.ImmutableMapBuilder<>();
        }

        public _Builder(ThriftField base) {
            this();

            if (base.hasComment()) {
                optionals.set(0);
                mComment = base.mComment;
            }
            optionals.set(1);
            mKey = base.mKey;
            if (base.hasRequirement()) {
                optionals.set(2);
                mRequirement = base.mRequirement;
            }
            if (base.hasType()) {
                optionals.set(3);
                mType = base.mType;
            }
            if (base.hasName()) {
                optionals.set(4);
                mName = base.mName;
            }
            if (base.hasDefaultValue()) {
                optionals.set(5);
                mDefaultValue = base.mDefaultValue;
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
        public _Builder setKey(int value) {
            optionals.set(1);
            mKey = value;
            return this;
        }
        public boolean isSetKey() {
            return optionals.get(1);
        }
        public _Builder clearKey() {
            optionals.set(1, false);
            mKey = kDefaultKey;
            return this;
        }
        public _Builder setRequirement(net.morimekta.providence.model.Requirement value) {
            optionals.set(2);
            mRequirement = value;
            return this;
        }
        public boolean isSetRequirement() {
            return optionals.get(2);
        }
        public _Builder clearRequirement() {
            optionals.set(2, false);
            mRequirement = null;
            return this;
        }
        public _Builder setType(String value) {
            optionals.set(3);
            mType = value;
            return this;
        }
        public boolean isSetType() {
            return optionals.get(3);
        }
        public _Builder clearType() {
            optionals.set(3, false);
            mType = null;
            return this;
        }
        public _Builder setName(String value) {
            optionals.set(4);
            mName = value;
            return this;
        }
        public boolean isSetName() {
            return optionals.get(4);
        }
        public _Builder clearName() {
            optionals.set(4, false);
            mName = null;
            return this;
        }
        public _Builder setDefaultValue(String value) {
            optionals.set(5);
            mDefaultValue = value;
            return this;
        }
        public boolean isSetDefaultValue() {
            return optionals.get(5);
        }
        public _Builder clearDefaultValue() {
            optionals.set(5, false);
            mDefaultValue = null;
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
                case 2: setKey((int) value); break;
                case 3: setRequirement((net.morimekta.providence.model.Requirement) value); break;
                case 4: setType((String) value); break;
                case 5: setName((String) value); break;
                case 6: setDefaultValue((String) value); break;
                case 7: setAnnotations((java.util.Map<String,String>) value); break;
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
                case 1: clearComment(); break;
                case 2: clearKey(); break;
                case 3: clearRequirement(); break;
                case 4: clearType(); break;
                case 5: clearName(); break;
                case 6: clearDefaultValue(); break;
                case 7: clearAnnotations(); break;
            }
            return this;
        }

        @Override
        public boolean isValid() {
            return optionals.get(1) &&
                   optionals.get(3) &&
                   optionals.get(4);
        }

        @Override
        public ThriftField build() {
            return new ThriftField(this);
        }
    }
}
