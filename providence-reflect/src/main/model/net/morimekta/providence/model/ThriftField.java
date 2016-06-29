package net.morimekta.providence.model;

/**
 * For fields:
 *   (&lt;key&gt;:)? (required|optional)? &lt;type&gt; &lt;name&gt; (= &lt;default_value&gt;)?
 * For const:
 *   const &lt;type&gt; &lt;name&gt; = &lt;default_value&gt;
 * <p>
 * Fields without key is assigned values ranging from 65335 and down (2^16-1)
 * in order of appearance. Because of the &quot;in order of appearance&quot; the field
 * *must* be filled by the IDL parser.
 * <p>
 * Consts are always given the key &#39;0&#39;.
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

    /**
     * @return The field value
     */
    public String getComment() {
        return mComment;
    }

    public boolean hasKey() {
        return true;
    }

    /**
     * @return The field value
     */
    public int getKey() {
        return mKey;
    }

    public boolean hasRequirement() {
        return mRequirement != null;
    }

    /**
     * @return The field value
     */
    public net.morimekta.providence.model.Requirement getRequirement() {
        return hasRequirement() ? mRequirement : kDefaultRequirement;
    }

    public boolean hasType() {
        return mType != null;
    }

    /**
     * @return The field value
     */
    public String getType() {
        return mType;
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

    public boolean hasDefaultValue() {
        return mDefaultValue != null;
    }

    /**
     * @return The field value
     */
    public String getDefaultValue() {
        return mDefaultValue;
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
    public boolean compact() {
        return false;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        if (o == null || !(o instanceof ThriftField)) return false;
        ThriftField other = (ThriftField) o;
        return java.util.Objects.equals(mComment, other.mComment) &&
               java.util.Objects.equals(mKey, other.mKey) &&
               java.util.Objects.equals(mRequirement, other.mRequirement) &&
               java.util.Objects.equals(mType, other.mType) &&
               java.util.Objects.equals(mName, other.mName) &&
               java.util.Objects.equals(mDefaultValue, other.mDefaultValue) &&
               java.util.Objects.equals(mAnnotations, other.mAnnotations);
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
                    _Field.ANNOTATIONS, mAnnotations);
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
        if (mComment != null) {
            first = false;
            out.append("comment:")
               .append('\"')
               .append(net.morimekta.util.Strings.escape(mComment))
               .append('\"');
        }
        out.append("key:")
           .append(mKey);
        if (mRequirement != null) {
            out.append(',');
            out.append("requirement:")
               .append(mRequirement.toString());
        }
        if (mType != null) {
            out.append(',');
            out.append("type:")
               .append('\"')
               .append(net.morimekta.util.Strings.escape(mType))
               .append('\"');
        }
        if (mName != null) {
            out.append(',');
            out.append("name:")
               .append('\"')
               .append(net.morimekta.util.Strings.escape(mName))
               .append('\"');
        }
        if (mDefaultValue != null) {
            out.append(',');
            out.append("default_value:")
               .append('\"')
               .append(net.morimekta.util.Strings.escape(mDefaultValue))
               .append('\"');
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
                case 2: return _Field.KEY;
                case 3: return _Field.REQUIREMENT;
                case 4: return _Field.TYPE;
                case 5: return _Field.NAME;
                case 6: return _Field.DEFAULT_VALUE;
                case 7: return _Field.ANNOTATIONS;
            }
            return null;
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
            super("model", "ThriftField", new _Factory(), false, false);
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

    /**
     * Make a model.ThriftField builder.
     * @return The builder instance.
     */
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
        private net.morimekta.providence.descriptor.PMap.Builder<String,String> mAnnotations;

        /**
         * Make a model.ThriftField builder.
         */
        public _Builder() {
            optionals = new java.util.BitSet(7);
            mKey = kDefaultKey;
            mAnnotations = new net.morimekta.providence.descriptor.PMap.ImmutableMapBuilder<>();
        }

        /**
         * Make a mutating builder off a base model.ThriftField.
         *
         * @param base The base ThriftField
         */
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

        @Override
        public _Builder merge(ThriftField from) {
            if (from.hasComment()) {
                optionals.set(0);
                mComment = from.getComment();
            }

            optionals.set(1);
            mKey = from.getKey();

            if (from.hasRequirement()) {
                optionals.set(2);
                mRequirement = from.getRequirement();
            }

            if (from.hasType()) {
                optionals.set(3);
                mType = from.getType();
            }

            if (from.hasName()) {
                optionals.set(4);
                mName = from.getName();
            }

            if (from.hasDefaultValue()) {
                optionals.set(5);
                mDefaultValue = from.getDefaultValue();
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
         * Sets the value of key.
         *
         * @param value The new value
         * @return The builder
         */
        public _Builder setKey(int value) {
            optionals.set(1);
            mKey = value;
            return this;
        }

        /**
         * Checks for presence of the key field.
         *
         * @return True iff key has been set.
         */
        public boolean isSetKey() {
            return optionals.get(1);
        }

        /**
         * Clears the key field.
         *
         * @return The builder
         */
        public _Builder clearKey() {
            optionals.clear(1);
            mKey = kDefaultKey;
            return this;
        }

        /**
         * Sets the value of requirement.
         *
         * @param value The new value
         * @return The builder
         */
        public _Builder setRequirement(net.morimekta.providence.model.Requirement value) {
            optionals.set(2);
            mRequirement = value;
            return this;
        }

        /**
         * Checks for presence of the requirement field.
         *
         * @return True iff requirement has been set.
         */
        public boolean isSetRequirement() {
            return optionals.get(2);
        }

        /**
         * Clears the requirement field.
         *
         * @return The builder
         */
        public _Builder clearRequirement() {
            optionals.clear(2);
            mRequirement = null;
            return this;
        }

        /**
         * Sets the value of type.
         *
         * @param value The new value
         * @return The builder
         */
        public _Builder setType(String value) {
            optionals.set(3);
            mType = value;
            return this;
        }

        /**
         * Checks for presence of the type field.
         *
         * @return True iff type has been set.
         */
        public boolean isSetType() {
            return optionals.get(3);
        }

        /**
         * Clears the type field.
         *
         * @return The builder
         */
        public _Builder clearType() {
            optionals.clear(3);
            mType = null;
            return this;
        }

        /**
         * Sets the value of name.
         *
         * @param value The new value
         * @return The builder
         */
        public _Builder setName(String value) {
            optionals.set(4);
            mName = value;
            return this;
        }

        /**
         * Checks for presence of the name field.
         *
         * @return True iff name has been set.
         */
        public boolean isSetName() {
            return optionals.get(4);
        }

        /**
         * Clears the name field.
         *
         * @return The builder
         */
        public _Builder clearName() {
            optionals.clear(4);
            mName = null;
            return this;
        }

        /**
         * Sets the value of default_value.
         *
         * @param value The new value
         * @return The builder
         */
        public _Builder setDefaultValue(String value) {
            optionals.set(5);
            mDefaultValue = value;
            return this;
        }

        /**
         * Checks for presence of the default_value field.
         *
         * @return True iff default_value has been set.
         */
        public boolean isSetDefaultValue() {
            return optionals.get(5);
        }

        /**
         * Clears the default_value field.
         *
         * @return The builder
         */
        public _Builder clearDefaultValue() {
            optionals.clear(5);
            mDefaultValue = null;
            return this;
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
            return mAnnotations;
        }

        @Override
        @SuppressWarnings("unchecked")
        public <MT extends net.morimekta.providence.PMessage<MT>> net.morimekta.providence.PMessageBuilder<MT> mutator(int key) {
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
        public net.morimekta.providence.descriptor.PStructDescriptor<ThriftField,_Field> descriptor() {
            return kDescriptor;
        }

        @Override
        public ThriftField build() {
            return new ThriftField(this);
        }
    }
}
