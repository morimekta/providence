package net.morimekta.providence.model;

/**
 * <variant> {
 *   (<field> ([,;])?)*
 * }
 */
@SuppressWarnings("unused")
public class StructType
        implements net.morimekta.providence.PMessage<StructType>, java.io.Serializable, Comparable<StructType> {
    private final static long serialVersionUID = -7531050363059752370L;

    private final static net.morimekta.providence.model.StructVariant kDefaultVariant = net.morimekta.providence.model.StructVariant.STRUCT;

    private final String mComment;
    private final net.morimekta.providence.model.StructVariant mVariant;
    private final String mName;
    private final java.util.List<net.morimekta.providence.model.ThriftField> mFields;
    private final java.util.Map<String,String> mAnnotations;
    
    private volatile int tHashCode;

    private StructType(_Builder builder) {
        mComment = builder.mComment;
        mVariant = builder.mVariant;
        mName = builder.mName;
        if (builder.isSetFields()) {
            mFields = builder.mFields.build();
        } else {
            mFields = null;
        }
        if (builder.isSetAnnotations()) {
            mAnnotations = builder.mAnnotations.build();
        } else {
            mAnnotations = null;
        }
    }

    public StructType(String pComment,
                      net.morimekta.providence.model.StructVariant pVariant,
                      String pName,
                      java.util.List<net.morimekta.providence.model.ThriftField> pFields,
                      java.util.Map<String,String> pAnnotations) {
        mComment = pComment;
        mVariant = pVariant;
        mName = pName;
        if (pFields != null) {
            mFields = com.google.common.collect.ImmutableList.copyOf(pFields);
        } else {
            mFields = null;
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

    public boolean hasVariant() {
        return mVariant != null;
    }

    public net.morimekta.providence.model.StructVariant getVariant() {
        return hasVariant() ? mVariant : kDefaultVariant;
    }

    public boolean hasName() {
        return mName != null;
    }

    public String getName() {
        return mName;
    }

    public int numFields() {
        return mFields != null ? mFields.size() : 0;
    }

    public java.util.List<net.morimekta.providence.model.ThriftField> getFields() {
        return mFields;
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
            case 2: return hasVariant();
            case 3: return hasName();
            case 4: return numFields() > 0;
            case 5: return numAnnotations() > 0;
            default: return false;
        }
    }

    @Override
    public int num(int key) {
        switch(key) {
            case 1: return hasComment() ? 1 : 0;
            case 2: return hasVariant() ? 1 : 0;
            case 3: return hasName() ? 1 : 0;
            case 4: return numFields();
            case 5: return numAnnotations();
            default: return 0;
        }
    }

    @Override
    public Object get(int key) {
        switch(key) {
            case 1: return getComment();
            case 2: return getVariant();
            case 3: return getName();
            case 4: return getFields();
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
        if (o == null || !(o instanceof StructType)) return false;
        StructType other = (StructType) o;
        return java.util.Objects.equals(mComment, other.mComment) &&
               java.util.Objects.equals(mVariant, other.mVariant) &&
               java.util.Objects.equals(mName, other.mName) &&
               net.morimekta.providence.util.PTypeUtils.equals(mFields, other.mFields) &&
               net.morimekta.providence.util.PTypeUtils.equals(mAnnotations, other.mAnnotations);
    }

    @Override
    public int hashCode() {
        if (tHashCode == 0) {
            tHashCode = java.util.Objects.hash(
                    StructType.class,
                    _Field.COMMENT, mComment,
                    _Field.VARIANT, mVariant,
                    _Field.NAME, mName,
                    _Field.FIELDS, net.morimekta.providence.util.PTypeUtils.hashCode(mFields),
                    _Field.ANNOTATIONS, net.morimekta.providence.util.PTypeUtils.hashCode(mAnnotations));
        }
        return tHashCode;
    }

    @Override
    public String toString() {
        return "model.StructType" + asString();
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
        if (hasVariant()) {
            if (!first) out.append(',');
            first = false;
            out.append("variant:");
            out.append(mVariant.getName());
        }
        if (hasName()) {
            if (!first) out.append(',');
            first = false;
            out.append("name:");
            out.append('\"').append(mName).append('\"');
        }
        if (numFields() > 0) {
            if (!first) out.append(',');
            first = false;
            out.append("fields:");
            out.append(net.morimekta.providence.util.PTypeUtils.toString(mFields));
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
    public int compareTo(StructType other) {
        int c;

        c = Boolean.compare(mComment != null, other.mComment != null);
        if (c != 0) return c;
        if (mComment != null) {
            c = mComment.compareTo(other.mComment);
            if (c != 0) return c;
        }

        c = Boolean.compare(mVariant != null, other.mVariant != null);
        if (c != 0) return c;
        if (mVariant != null) {
            c = Integer.compare(mVariant.getValue(), mVariant.getValue());
            if (c != 0) return c;
        }

        c = Boolean.compare(mName != null, other.mName != null);
        if (c != 0) return c;
        if (mName != null) {
            c = mName.compareTo(other.mName);
            if (c != 0) return c;
        }

        c = Boolean.compare(mFields != null, other.mFields != null);
        if (c != 0) return c;
        if (mFields != null) {
            c = Integer.compare(mFields.hashCode(), other.mFields.hashCode());
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
        VARIANT(2, net.morimekta.providence.descriptor.PRequirement.DEFAULT, "variant", net.morimekta.providence.model.StructVariant.provider(), new net.morimekta.providence.descriptor.PDefaultValueProvider<>(kDefaultVariant)),
        NAME(3, net.morimekta.providence.descriptor.PRequirement.REQUIRED, "name", net.morimekta.providence.descriptor.PPrimitive.STRING.provider(), null),
        FIELDS(4, net.morimekta.providence.descriptor.PRequirement.DEFAULT, "fields", net.morimekta.providence.descriptor.PList.provider(net.morimekta.providence.model.ThriftField.provider()), null),
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
            builder.append("StructType._Field(")
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
                case 2: return _Field.VARIANT;
                case 3: return _Field.NAME;
                case 4: return _Field.FIELDS;
                case 5: return _Field.ANNOTATIONS;
                default: return null;
            }
        }

        public static _Field forName(String name) {
            switch (name) {
                case "comment": return _Field.COMMENT;
                case "variant": return _Field.VARIANT;
                case "name": return _Field.NAME;
                case "fields": return _Field.FIELDS;
                case "annotations": return _Field.ANNOTATIONS;
            }
            return null;
        }
    }

    public static net.morimekta.providence.descriptor.PStructDescriptorProvider<StructType,_Field> provider() {
        return new _Provider();
    }

    @Override
    public net.morimekta.providence.descriptor.PStructDescriptor<StructType,_Field> descriptor() {
        return kDescriptor;
    }

    public static final net.morimekta.providence.descriptor.PStructDescriptor<StructType,_Field> kDescriptor;

    private static class _Descriptor
            extends net.morimekta.providence.descriptor.PStructDescriptor<StructType,_Field> {
        public _Descriptor() {
            super("model", "StructType", new _Factory(), false, false);
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

    private final static class _Provider extends net.morimekta.providence.descriptor.PStructDescriptorProvider<StructType,_Field> {
        @Override
        public net.morimekta.providence.descriptor.PStructDescriptor<StructType,_Field> descriptor() {
            return kDescriptor;
        }
    }

    private final static class _Factory
            extends net.morimekta.providence.PMessageBuilderFactory<StructType> {
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
            extends net.morimekta.providence.PMessageBuilder<StructType> {
        private java.util.BitSet optionals;

        private String mComment;
        private net.morimekta.providence.model.StructVariant mVariant;
        private String mName;
        private net.morimekta.providence.descriptor.PList.Builder mFields;
        private net.morimekta.providence.descriptor.PMap.Builder mAnnotations;


        public _Builder() {
            optionals = new java.util.BitSet(5);
            mFields = new net.morimekta.providence.descriptor.PList.ImmutableListBuilder<>();
            mAnnotations = new net.morimekta.providence.descriptor.PMap.ImmutableMapBuilder<>();
        }

        public _Builder(StructType base) {
            this();

            if (base.hasComment()) {
                optionals.set(0);
                mComment = base.mComment;
            }
            if (base.hasVariant()) {
                optionals.set(1);
                mVariant = base.mVariant;
            }
            if (base.hasName()) {
                optionals.set(2);
                mName = base.mName;
            }
            if (base.numFields() > 0) {
                optionals.set(3);
                mFields.addAll(base.mFields);
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
        public _Builder setVariant(net.morimekta.providence.model.StructVariant value) {
            optionals.set(1);
            mVariant = value;
            return this;
        }
        public boolean isSetVariant() {
            return optionals.get(1);
        }
        public _Builder clearVariant() {
            optionals.set(1, false);
            mVariant = null;
            return this;
        }
        public _Builder setName(String value) {
            optionals.set(2);
            mName = value;
            return this;
        }
        public boolean isSetName() {
            return optionals.get(2);
        }
        public _Builder clearName() {
            optionals.set(2, false);
            mName = null;
            return this;
        }
        public _Builder setFields(java.util.Collection<net.morimekta.providence.model.ThriftField> value) {
            optionals.set(3);
            mFields.clear();
            mFields.addAll(value);
            return this;
        }
        public _Builder addToFields(net.morimekta.providence.model.ThriftField... values) {
            optionals.set(3);
            for (net.morimekta.providence.model.ThriftField item : values) {
                mFields.add(item);
            }
            return this;
        }

        public boolean isSetFields() {
            return optionals.get(3);
        }
        public _Builder clearFields() {
            optionals.set(3, false);
            mFields.clear();
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
                case 2: setVariant((net.morimekta.providence.model.StructVariant) value); break;
                case 3: setName((String) value); break;
                case 4: setFields((java.util.List<net.morimekta.providence.model.ThriftField>) value); break;
                case 5: setAnnotations((java.util.Map<String,String>) value); break;
            }
            return this;
        }

        @Override
        public _Builder addTo(int key, Object value) {
            switch (key) {
                case 4: addToFields((net.morimekta.providence.model.ThriftField) value); break;
                default: break;
            }
            return this;
        }

        @Override
        public _Builder clear(int key) {
            switch (key) {
                case 1: clearComment(); break;
                case 2: clearVariant(); break;
                case 3: clearName(); break;
                case 4: clearFields(); break;
                case 5: clearAnnotations(); break;
            }
            return this;
        }

        @Override
        public boolean isValid() {
            return optionals.get(2);
        }

        @Override
        public StructType build() {
            return new StructType(this);
        }
    }
}
