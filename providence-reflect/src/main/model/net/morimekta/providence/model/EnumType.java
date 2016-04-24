package net.morimekta.providence.model;

import net.morimekta.providence.util.TypeUtils;

/**
 * enum {
 *   (&lt;value&gt; ([;,])?)*
 * }
 */
@SuppressWarnings("unused")
public class EnumType
        implements net.morimekta.providence.PMessage<EnumType>, java.io.Serializable, Comparable<EnumType> {
    private final static long serialVersionUID = 5720337451968926862L;

    private final String mComment;
    private final String mName;
    private final java.util.List<net.morimekta.providence.model.EnumValue> mValues;
    private final java.util.Map<String,String> mAnnotations;
    
    private volatile int tHashCode;

    private EnumType(_Builder builder) {
        mComment = builder.mComment;
        mName = builder.mName;
        if (builder.isSetValues()) {
            mValues = builder.mValues.build();
        } else {
            mValues = null;
        }
        if (builder.isSetAnnotations()) {
            mAnnotations = builder.mAnnotations.build();
        } else {
            mAnnotations = null;
        }
    }

    public EnumType(String pComment,
                    String pName,
                    java.util.List<net.morimekta.providence.model.EnumValue> pValues,
                    java.util.Map<String,String> pAnnotations) {
        mComment = pComment;
        mName = pName;
        if (pValues != null) {
            mValues = com.google.common.collect.ImmutableList.copyOf(pValues);
        } else {
            mValues = null;
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

    public int numValues() {
        return mValues != null ? mValues.size() : 0;
    }

    public java.util.List<net.morimekta.providence.model.EnumValue> getValues() {
        return mValues;
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
            case 3: return numValues() > 0;
            case 4: return numAnnotations() > 0;
            default: return false;
        }
    }

    @Override
    public int num(int key) {
        switch(key) {
            case 1: return hasComment() ? 1 : 0;
            case 2: return hasName() ? 1 : 0;
            case 3: return numValues();
            case 4: return numAnnotations();
            default: return 0;
        }
    }

    @Override
    public Object get(int key) {
        switch(key) {
            case 1: return getComment();
            case 2: return getName();
            case 3: return getValues();
            case 4: return getAnnotations();
            default: return null;
        }
    }

    @Override
    public boolean compact() {
        return false;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || !(o instanceof EnumType)) return false;
        EnumType other = (EnumType) o;
        return java.util.Objects.equals(mComment, other.mComment) &&
               java.util.Objects.equals(mName, other.mName) &&
               TypeUtils.equals(mValues, other.mValues) &&
               TypeUtils.equals(mAnnotations, other.mAnnotations);
    }

    @Override
    public int hashCode() {
        if (tHashCode == 0) {
            tHashCode = java.util.Objects.hash(
                    EnumType.class,
                    _Field.COMMENT, mComment,
                    _Field.NAME, mName,
                    _Field.VALUES, TypeUtils.hashCode(mValues),
                    _Field.ANNOTATIONS, TypeUtils.hashCode(mAnnotations));
        }
        return tHashCode;
    }

    @Override
    public String toString() {
        return "model.EnumType" + asString();
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
        if (numValues() > 0) {
            if (!first) out.append(',');
            first = false;
            out.append("values:");
            out.append(TypeUtils.toString(mValues));
        }
        if (numAnnotations() > 0) {
            if (!first) out.append(',');
            first = false;
            out.append("annotations:");
            out.append(TypeUtils.toString(mAnnotations));
        }
        out.append('}');
        return out.toString();
    }

    @Override
    public int compareTo(EnumType other) {
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

        c = Boolean.compare(mValues != null, other.mValues != null);
        if (c != 0) return c;
        if (mValues != null) {
            c = Integer.compare(mValues.hashCode(), other.mValues.hashCode());
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
        VALUES(3, net.morimekta.providence.descriptor.PRequirement.DEFAULT, "values", net.morimekta.providence.descriptor.PList.provider(net.morimekta.providence.model.EnumValue.provider()), null),
        ANNOTATIONS(4, net.morimekta.providence.descriptor.PRequirement.DEFAULT, "annotations", net.morimekta.providence.descriptor.PMap.provider(net.morimekta.providence.descriptor.PPrimitive.STRING.provider(),net.morimekta.providence.descriptor.PPrimitive.STRING.provider()), null),
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
            builder.append("EnumType._Field(")
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
                case 3: return _Field.VALUES;
                case 4: return _Field.ANNOTATIONS;
                default: return null;
            }
        }

        public static _Field forName(String name) {
            switch (name) {
                case "comment": return _Field.COMMENT;
                case "name": return _Field.NAME;
                case "values": return _Field.VALUES;
                case "annotations": return _Field.ANNOTATIONS;
            }
            return null;
        }
    }

    public static net.morimekta.providence.descriptor.PStructDescriptorProvider<EnumType,_Field> provider() {
        return new _Provider();
    }

    @Override
    public net.morimekta.providence.descriptor.PStructDescriptor<EnumType,_Field> descriptor() {
        return kDescriptor;
    }

    public static final net.morimekta.providence.descriptor.PStructDescriptor<EnumType,_Field> kDescriptor;

    private static class _Descriptor
            extends net.morimekta.providence.descriptor.PStructDescriptor<EnumType,_Field> {
        public _Descriptor() {
            super("model", "EnumType", new _Factory(), false, false);
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

    private final static class _Provider extends net.morimekta.providence.descriptor.PStructDescriptorProvider<EnumType,_Field> {
        @Override
        public net.morimekta.providence.descriptor.PStructDescriptor<EnumType,_Field> descriptor() {
            return kDescriptor;
        }
    }

    private final static class _Factory
            extends net.morimekta.providence.PMessageBuilderFactory<EnumType> {
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
            extends net.morimekta.providence.PMessageBuilder<EnumType> {
        private java.util.BitSet optionals;

        private String mComment;
        private String mName;
        private net.morimekta.providence.descriptor.PList.Builder mValues;
        private net.morimekta.providence.descriptor.PMap.Builder mAnnotations;


        public _Builder() {
            optionals = new java.util.BitSet(4);
            mValues = new net.morimekta.providence.descriptor.PList.ImmutableListBuilder<>();
            mAnnotations = new net.morimekta.providence.descriptor.PMap.ImmutableMapBuilder<>();
        }

        public _Builder(EnumType base) {
            this();

            if (base.hasComment()) {
                optionals.set(0);
                mComment = base.mComment;
            }
            if (base.hasName()) {
                optionals.set(1);
                mName = base.mName;
            }
            if (base.numValues() > 0) {
                optionals.set(2);
                mValues.addAll(base.mValues);
            }
            if (base.numAnnotations() > 0) {
                optionals.set(3);
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
        public _Builder setValues(java.util.Collection<net.morimekta.providence.model.EnumValue> value) {
            optionals.set(2);
            mValues.clear();
            mValues.addAll(value);
            return this;
        }
        public _Builder addToValues(net.morimekta.providence.model.EnumValue... values) {
            optionals.set(2);
            for (net.morimekta.providence.model.EnumValue item : values) {
                mValues.add(item);
            }
            return this;
        }

        public boolean isSetValues() {
            return optionals.get(2);
        }
        public _Builder clearValues() {
            optionals.set(2, false);
            mValues.clear();
            return this;
        }
        public _Builder setAnnotations(java.util.Map<String,String> value) {
            optionals.set(3);
            mAnnotations.clear();
            mAnnotations.putAll(value);
            return this;
        }
        public _Builder putInAnnotations(String key, String value) {
            optionals.set(3);
            mAnnotations.put(key, value);
            return this;
        }

        public boolean isSetAnnotations() {
            return optionals.get(3);
        }
        public _Builder clearAnnotations() {
            optionals.set(3, false);
            mAnnotations.clear();
            return this;
        }
        @Override
        public _Builder set(int key, Object value) {
            if (value == null) return clear(key);
            switch (key) {
                case 1: setComment((String) value); break;
                case 2: setName((String) value); break;
                case 3: setValues((java.util.List<net.morimekta.providence.model.EnumValue>) value); break;
                case 4: setAnnotations((java.util.Map<String,String>) value); break;
            }
            return this;
        }

        @Override
        public _Builder addTo(int key, Object value) {
            switch (key) {
                case 3: addToValues((net.morimekta.providence.model.EnumValue) value); break;
                default: break;
            }
            return this;
        }

        @Override
        public _Builder clear(int key) {
            switch (key) {
                case 1: clearComment(); break;
                case 2: clearName(); break;
                case 3: clearValues(); break;
                case 4: clearAnnotations(); break;
            }
            return this;
        }

        @Override
        public boolean isValid() {
            return optionals.get(1);
        }

        @Override
        public EnumType build() {
            return new EnumType(this);
        }
    }
}
