package net.morimekta.providence.model;

/**
 * enum {
 *   (&lt;value&gt; ([;,])?)*
 * }
 */
@SuppressWarnings("unused")
public class EnumType
        implements net.morimekta.providence.PMessage<EnumType,EnumType._Field>,
                   Comparable<EnumType>,
                   java.io.Serializable {
    private final static long serialVersionUID = 5720337451968926862L;

    private final String mDocumentation;
    private final String mName;
    private final java.util.List<net.morimekta.providence.model.EnumValue> mValues;
    private final java.util.Map<String,String> mAnnotations;

    private volatile int tHashCode;

    public EnumType(String pDocumentation,
                    String pName,
                    java.util.List<net.morimekta.providence.model.EnumValue> pValues,
                    java.util.Map<String,String> pAnnotations) {
        mDocumentation = pDocumentation;
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

    private EnumType(_Builder builder) {
        mDocumentation = builder.mDocumentation;
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

    public boolean hasDocumentation() {
        return mDocumentation != null;
    }

    /**
     * @return The field value
     */
    public String getDocumentation() {
        return mDocumentation;
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

    public int numValues() {
        return mValues != null ? mValues.size() : 0;
    }

    public boolean hasValues() {
        return mValues != null;
    }

    /**
     * @return The field value
     */
    public java.util.List<net.morimekta.providence.model.EnumValue> getValues() {
        return mValues;
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
            case 2: return hasName();
            case 3: return hasValues();
            case 4: return hasAnnotations();
            default: return false;
        }
    }

    @Override
    public int num(int key) {
        switch(key) {
            case 1: return hasDocumentation() ? 1 : 0;
            case 2: return hasName() ? 1 : 0;
            case 3: return numValues();
            case 4: return numAnnotations();
            default: return 0;
        }
    }

    @Override
    public Object get(int key) {
        switch(key) {
            case 1: return getDocumentation();
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
        if (o == this) return true;
        if (o == null || !(o instanceof EnumType)) return false;
        EnumType other = (EnumType) o;
        return java.util.Objects.equals(mDocumentation, other.mDocumentation) &&
               java.util.Objects.equals(mName, other.mName) &&
               java.util.Objects.equals(mValues, other.mValues) &&
               java.util.Objects.equals(mAnnotations, other.mAnnotations);
    }

    @Override
    public int hashCode() {
        if (tHashCode == 0) {
            tHashCode = java.util.Objects.hash(
                    EnumType.class,
                    _Field.DOCUMENTATION, mDocumentation,
                    _Field.NAME, mName,
                    _Field.VALUES, mValues,
                    _Field.ANNOTATIONS, mAnnotations);
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
        if (mDocumentation != null) {
            first = false;
            out.append("documentation:")
               .append('\"')
               .append(net.morimekta.util.Strings.escape(mDocumentation))
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
        if (mValues != null && mValues.size() > 0) {
            if (first) first = false;
            else out.append(',');
            out.append("values:")
               .append(net.morimekta.util.Strings.asString(mValues));
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
    public int compareTo(EnumType other) {
        int c;

        c = Boolean.compare(mDocumentation != null, other.mDocumentation != null);
        if (c != 0) return c;
        if (mDocumentation != null) {
            c = mDocumentation.compareTo(other.mDocumentation);
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

    @Override
    public _Builder mutate() {
        return new _Builder(this);
    }

    public enum _Field implements net.morimekta.providence.descriptor.PField {
        DOCUMENTATION(1, net.morimekta.providence.descriptor.PRequirement.DEFAULT, "documentation", net.morimekta.providence.descriptor.PPrimitive.STRING.provider(), null),
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
                case 2: return _Field.NAME;
                case 3: return _Field.VALUES;
                case 4: return _Field.ANNOTATIONS;
            }
            return null;
        }

        public static _Field forName(String name) {
            switch (name) {
                case "documentation": return _Field.DOCUMENTATION;
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
            extends net.morimekta.providence.PMessageBuilderFactory<EnumType,_Field> {
        @Override
        public _Builder builder() {
            return new _Builder();
        }
    }

    /**
     * Make a model.EnumType builder.
     * @return The builder instance.
     */
    public static _Builder builder() {
        return new _Builder();
    }

    /**
     * enum {
     *   (&lt;value&gt; ([;,])?)*
     * }
     */
    public static class _Builder
            extends net.morimekta.providence.PMessageBuilder<EnumType,_Field> {
        private java.util.BitSet optionals;

        private String mDocumentation;
        private String mName;
        private net.morimekta.providence.descriptor.PList.Builder<net.morimekta.providence.model.EnumValue> mValues;
        private net.morimekta.providence.descriptor.PMap.Builder<String,String> mAnnotations;

        /**
         * Make a model.EnumType builder.
         */
        public _Builder() {
            optionals = new java.util.BitSet(4);
            mValues = new net.morimekta.providence.descriptor.PList.ImmutableListBuilder<>();
            mAnnotations = new net.morimekta.providence.descriptor.PMap.ImmutableMapBuilder<>();
        }

        /**
         * Make a mutating builder off a base model.EnumType.
         *
         * @param base The base EnumType
         */
        public _Builder(EnumType base) {
            this();

            if (base.hasDocumentation()) {
                optionals.set(0);
                mDocumentation = base.mDocumentation;
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

        @Override
        public _Builder merge(EnumType from) {
            if (from.hasDocumentation()) {
                optionals.set(0);
                mDocumentation = from.getDocumentation();
            }

            if (from.hasName()) {
                optionals.set(1);
                mName = from.getName();
            }

            if (from.hasValues()) {
                optionals.set(2);
                mValues.clear();
                mValues.addAll(from.getValues());
            }

            if (from.hasAnnotations()) {
                optionals.set(3);
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
         * Sets the value of values.
         *
         * @param value The new value
         * @return The builder
         */
        public _Builder setValues(java.util.Collection<net.morimekta.providence.model.EnumValue> value) {
            optionals.set(2);
            mValues.clear();
            mValues.addAll(value);
            return this;
        }

        /**
         * Adds entries to values.
         *
         * @param values The added value
         * @return The builder
         */
        public _Builder addToValues(net.morimekta.providence.model.EnumValue... values) {
            optionals.set(2);
            for (net.morimekta.providence.model.EnumValue item : values) {
                mValues.add(item);
            }
            return this;
        }

        /**
         * Checks for presence of the values field.
         *
         * @return True iff values has been set.
         */
        public boolean isSetValues() {
            return optionals.get(2);
        }

        /**
         * Clears the values field.
         *
         * @return The builder
         */
        public _Builder clearValues() {
            optionals.clear(2);
            mValues.clear();
            return this;
        }

        /**
         * Gets the builder for the contained values.
         *
         * @return The field builder
         */
        public net.morimekta.providence.descriptor.PList.Builder<net.morimekta.providence.model.EnumValue> mutableValues() {
            optionals.set(2);
            return mValues;
        }

        /**
         * Sets the value of annotations.
         *
         * @param value The new value
         * @return The builder
         */
        public _Builder setAnnotations(java.util.Map<String,String> value) {
            optionals.set(3);
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
            optionals.set(3);
            mAnnotations.put(key, value);
            return this;
        }

        /**
         * Checks for presence of the annotations field.
         *
         * @return True iff annotations has been set.
         */
        public boolean isSetAnnotations() {
            return optionals.get(3);
        }

        /**
         * Clears the annotations field.
         *
         * @return The builder
         */
        public _Builder clearAnnotations() {
            optionals.clear(3);
            mAnnotations.clear();
            return this;
        }

        /**
         * Gets the builder for the contained annotations.
         *
         * @return The field builder
         */
        public net.morimekta.providence.descriptor.PMap.Builder<String,String> mutableAnnotations() {
            optionals.set(3);
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
                case 2: setName((String) value); break;
                case 3: setValues((java.util.List<net.morimekta.providence.model.EnumValue>) value); break;
                case 4: setAnnotations((java.util.Map<String,String>) value); break;
                default: break;
            }
            return this;
        }

        @Override
        public boolean isSet(int key) {
            switch (key) {
                case 1: return optionals.get(0);
                case 2: return optionals.get(1);
                case 3: return optionals.get(2);
                case 4: return optionals.get(3);
                default: break;
            }
            return false;
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
                case 1: clearDocumentation(); break;
                case 2: clearName(); break;
                case 3: clearValues(); break;
                case 4: clearAnnotations(); break;
                default: break;
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
                        " in message model.EnumType");
            }
        }

        @Override
        public net.morimekta.providence.descriptor.PStructDescriptor<EnumType,_Field> descriptor() {
            return kDescriptor;
        }

        @Override
        public EnumType build() {
            return new EnumType(this);
        }
    }
}
