package net.morimekta.providence.model;

/**
 * const &lt;type&gt; &lt;name&gt; = &lt;value&gt;
 */
@SuppressWarnings("unused")
public class ConstType
        implements net.morimekta.providence.PMessage<ConstType,ConstType._Field>,
                   Comparable<ConstType>,
                   java.io.Serializable {
    private final static long serialVersionUID = -7030319550715264712L;

    private final String mDocumentation;
    private final String mType;
    private final String mName;
    private final String mValue;
    private final java.util.Map<String,String> mAnnotations;

    private volatile int tHashCode;

    public ConstType(String pDocumentation,
                     String pType,
                     String pName,
                     String pValue,
                     java.util.Map<String,String> pAnnotations) {
        mDocumentation = pDocumentation;
        mType = pType;
        mName = pName;
        mValue = pValue;
        if (pAnnotations != null) {
            mAnnotations = com.google.common.collect.ImmutableMap.copyOf(pAnnotations);
        } else {
            mAnnotations = null;
        }
    }

    private ConstType(_Builder builder) {
        mDocumentation = builder.mDocumentation;
        mType = builder.mType;
        mName = builder.mName;
        mValue = builder.mValue;
        if (builder.isSetAnnotations()) {
            mAnnotations = builder.mAnnotations.build();
        } else {
            mAnnotations = null;
        }
    }

    public boolean hasDocumentation() {
        return mDocumentation != null;
    }

    /**
     * @return The field value
     */
    public String getDocumentation() {
        return mDocumentation;
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

    public boolean hasValue() {
        return mValue != null;
    }

    /**
     * @return The field value
     */
    public String getValue() {
        return mValue;
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
            case 1: return hasDocumentation();
            case 4: return hasType();
            case 5: return hasName();
            case 6: return hasValue();
            case 7: return hasAnnotations();
            default: return false;
        }
    }

    @Override
    public int num(int key) {
        switch(key) {
            case 1: return hasDocumentation() ? 1 : 0;
            case 4: return hasType() ? 1 : 0;
            case 5: return hasName() ? 1 : 0;
            case 6: return hasValue() ? 1 : 0;
            case 7: return numAnnotations();
            default: return 0;
        }
    }

    @Override
    public Object get(int key) {
        switch(key) {
            case 1: return getDocumentation();
            case 4: return getType();
            case 5: return getName();
            case 6: return getValue();
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
        if (o == null || !(o instanceof ConstType)) return false;
        ConstType other = (ConstType) o;
        return java.util.Objects.equals(mDocumentation, other.mDocumentation) &&
               java.util.Objects.equals(mType, other.mType) &&
               java.util.Objects.equals(mName, other.mName) &&
               java.util.Objects.equals(mValue, other.mValue) &&
               java.util.Objects.equals(mAnnotations, other.mAnnotations);
    }

    @Override
    public int hashCode() {
        if (tHashCode == 0) {
            tHashCode = java.util.Objects.hash(
                    ConstType.class,
                    _Field.DOCUMENTATION, mDocumentation,
                    _Field.TYPE, mType,
                    _Field.NAME, mName,
                    _Field.VALUE, mValue,
                    _Field.ANNOTATIONS, mAnnotations);
        }
        return tHashCode;
    }

    @Override
    public String toString() {
        return "model.ConstType" + asString();
    }

    @Override
    public String asString() {
        StringBuilder out = new StringBuilder();
        out.append("{");

        boolean first = true;
        if (mDocumentation != null) {
            first = false;
            out.append("documentation:")
               .append('\"')
               .append(net.morimekta.util.Strings.escape(mDocumentation))
               .append('\"');
        }
        if (mType != null) {
            if (first) first = false;
            else out.append(',');
            out.append("type:")
               .append('\"')
               .append(net.morimekta.util.Strings.escape(mType))
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
        if (mValue != null) {
            if (first) first = false;
            else out.append(',');
            out.append("value:")
               .append('\"')
               .append(net.morimekta.util.Strings.escape(mValue))
               .append('\"');
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
    public int compareTo(ConstType other) {
        int c;

        c = Boolean.compare(mDocumentation != null, other.mDocumentation != null);
        if (c != 0) return c;
        if (mDocumentation != null) {
            c = mDocumentation.compareTo(other.mDocumentation);
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

        c = Boolean.compare(mValue != null, other.mValue != null);
        if (c != 0) return c;
        if (mValue != null) {
            c = mValue.compareTo(other.mValue);
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
        DOCUMENTATION(1, net.morimekta.providence.descriptor.PRequirement.DEFAULT, "documentation", net.morimekta.providence.descriptor.PPrimitive.STRING.provider(), null),
        TYPE(4, net.morimekta.providence.descriptor.PRequirement.REQUIRED, "type", net.morimekta.providence.descriptor.PPrimitive.STRING.provider(), null),
        NAME(5, net.morimekta.providence.descriptor.PRequirement.REQUIRED, "name", net.morimekta.providence.descriptor.PPrimitive.STRING.provider(), null),
        VALUE(6, net.morimekta.providence.descriptor.PRequirement.REQUIRED, "value", net.morimekta.providence.descriptor.PPrimitive.STRING.provider(), null),
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
                case 1: return _Field.DOCUMENTATION;
                case 4: return _Field.TYPE;
                case 5: return _Field.NAME;
                case 6: return _Field.VALUE;
                case 7: return _Field.ANNOTATIONS;
            }
            return null;
        }

        public static _Field forName(String name) {
            switch (name) {
                case "documentation": return _Field.DOCUMENTATION;
                case "type": return _Field.TYPE;
                case "name": return _Field.NAME;
                case "value": return _Field.VALUE;
                case "annotations": return _Field.ANNOTATIONS;
            }
            return null;
        }
    }

    public static net.morimekta.providence.descriptor.PStructDescriptorProvider<ConstType,_Field> provider() {
        return new _Provider();
    }

    @Override
    public net.morimekta.providence.descriptor.PStructDescriptor<ConstType,_Field> descriptor() {
        return kDescriptor;
    }

    public static final net.morimekta.providence.descriptor.PStructDescriptor<ConstType,_Field> kDescriptor;

    private static class _Descriptor
            extends net.morimekta.providence.descriptor.PStructDescriptor<ConstType,_Field> {
        public _Descriptor() {
            super("model", "ConstType", new _Factory(), false, false);
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

    private final static class _Provider extends net.morimekta.providence.descriptor.PStructDescriptorProvider<ConstType,_Field> {
        @Override
        public net.morimekta.providence.descriptor.PStructDescriptor<ConstType,_Field> descriptor() {
            return kDescriptor;
        }
    }

    private final static class _Factory
            extends net.morimekta.providence.PMessageBuilderFactory<ConstType,_Field> {
        @Override
        public _Builder builder() {
            return new _Builder();
        }
    }

    /**
     * Make a model.ConstType builder.
     * @return The builder instance.
     */
    public static _Builder builder() {
        return new _Builder();
    }

    /**
     * const &lt;type&gt; &lt;name&gt; = &lt;value&gt;
     */
    public static class _Builder
            extends net.morimekta.providence.PMessageBuilder<ConstType,_Field> {
        private java.util.BitSet optionals;

        private String mDocumentation;
        private String mType;
        private String mName;
        private String mValue;
        private net.morimekta.providence.descriptor.PMap.Builder<String,String> mAnnotations;

        /**
         * Make a model.ConstType builder.
         */
        public _Builder() {
            optionals = new java.util.BitSet(5);
            mAnnotations = new net.morimekta.providence.descriptor.PMap.ImmutableMapBuilder<>();
        }

        /**
         * Make a mutating builder off a base model.ConstType.
         *
         * @param base The base ConstType
         */
        public _Builder(ConstType base) {
            this();

            if (base.hasDocumentation()) {
                optionals.set(0);
                mDocumentation = base.mDocumentation;
            }
            if (base.hasType()) {
                optionals.set(1);
                mType = base.mType;
            }
            if (base.hasName()) {
                optionals.set(2);
                mName = base.mName;
            }
            if (base.hasValue()) {
                optionals.set(3);
                mValue = base.mValue;
            }
            if (base.numAnnotations() > 0) {
                optionals.set(4);
                mAnnotations.putAll(base.mAnnotations);
            }
        }

        @Override
        public _Builder merge(ConstType from) {
            if (from.hasDocumentation()) {
                optionals.set(0);
                mDocumentation = from.getDocumentation();
            }

            if (from.hasType()) {
                optionals.set(1);
                mType = from.getType();
            }

            if (from.hasName()) {
                optionals.set(2);
                mName = from.getName();
            }

            if (from.hasValue()) {
                optionals.set(3);
                mValue = from.getValue();
            }

            if (from.hasAnnotations()) {
                optionals.set(4);
                mAnnotations.putAll(from.getAnnotations());
            }
            return this;
        }

        /**
         * Sets the value of documentation.
         *
         * @param value The new value
         * @return The builder
         */
        public _Builder setDocumentation(String value) {
            optionals.set(0);
            mDocumentation = value;
            return this;
        }

        /**
         * Checks for presence of the documentation field.
         *
         * @return True iff documentation has been set.
         */
        public boolean isSetDocumentation() {
            return optionals.get(0);
        }

        /**
         * Clears the documentation field.
         *
         * @return The builder
         */
        public _Builder clearDocumentation() {
            optionals.clear(0);
            mDocumentation = null;
            return this;
        }

        /**
         * Sets the value of type.
         *
         * @param value The new value
         * @return The builder
         */
        public _Builder setType(String value) {
            optionals.set(1);
            mType = value;
            return this;
        }

        /**
         * Checks for presence of the type field.
         *
         * @return True iff type has been set.
         */
        public boolean isSetType() {
            return optionals.get(1);
        }

        /**
         * Clears the type field.
         *
         * @return The builder
         */
        public _Builder clearType() {
            optionals.clear(1);
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
            optionals.set(2);
            mName = value;
            return this;
        }

        /**
         * Checks for presence of the name field.
         *
         * @return True iff name has been set.
         */
        public boolean isSetName() {
            return optionals.get(2);
        }

        /**
         * Clears the name field.
         *
         * @return The builder
         */
        public _Builder clearName() {
            optionals.clear(2);
            mName = null;
            return this;
        }

        /**
         * Sets the value of value.
         *
         * @param value The new value
         * @return The builder
         */
        public _Builder setValue(String value) {
            optionals.set(3);
            mValue = value;
            return this;
        }

        /**
         * Checks for presence of the value field.
         *
         * @return True iff value has been set.
         */
        public boolean isSetValue() {
            return optionals.get(3);
        }

        /**
         * Clears the value field.
         *
         * @return The builder
         */
        public _Builder clearValue() {
            optionals.clear(3);
            mValue = null;
            return this;
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
                case 1: setDocumentation((String) value); break;
                case 4: setType((String) value); break;
                case 5: setName((String) value); break;
                case 6: setValue((String) value); break;
                case 7: setAnnotations((java.util.Map<String,String>) value); break;
                default: break;
            }
            return this;
        }

        @Override
        public boolean isSet(int key) {
            switch (key) {
                case 1: return optionals.get(0);
                case 4: return optionals.get(1);
                case 5: return optionals.get(2);
                case 6: return optionals.get(3);
                case 7: return optionals.get(4);
                default: break;
            }
            return false;
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
                case 1: clearDocumentation(); break;
                case 4: clearType(); break;
                case 5: clearName(); break;
                case 6: clearValue(); break;
                case 7: clearAnnotations(); break;
                default: break;
            }
            return this;
        }

        @Override
        public boolean isValid() {
            return optionals.get(1) &&
                   optionals.get(2) &&
                   optionals.get(3);
        }

        @Override
        public void validate() {
            if (!isValid()) {
                java.util.LinkedList<String> missing = new java.util.LinkedList<>();

                if (!optionals.get(1)) {
                    missing.add("type");
                }

                if (!optionals.get(2)) {
                    missing.add("name");
                }

                if (!optionals.get(3)) {
                    missing.add("value");
                }

                throw new java.lang.IllegalStateException(
                        "Missing required fields " +
                        String.join(",", missing) +
                        " in message model.ConstType");
            }
        }

        @Override
        public net.morimekta.providence.descriptor.PStructDescriptor<ConstType,_Field> descriptor() {
            return kDescriptor;
        }

        @Override
        public ConstType build() {
            return new ConstType(this);
        }
    }
}
