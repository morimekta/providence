package net.morimekta.test.providence;

@SuppressWarnings("unused")
public class CompactFields
        implements net.morimekta.providence.PMessage<CompactFields,CompactFields._Field>,
                   Comparable<CompactFields>,
                   java.io.Serializable {
    private final static long serialVersionUID = -8473304196623780023L;

    private final static int kDefaultId = 0;

    private final String mName;
    private final int mId;
    private final String mLabel;

    private volatile int tHashCode;

    public CompactFields(String pName,
                         int pId,
                         String pLabel) {
        mName = pName;
        mId = pId;
        mLabel = pLabel;
    }

    private CompactFields(_Builder builder) {
        mName = builder.mName;
        mId = builder.mId;
        mLabel = builder.mLabel;
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

    public boolean hasId() {
        return true;
    }

    /**
     * @return The field value
     */
    public int getId() {
        return mId;
    }

    public boolean hasLabel() {
        return mLabel != null;
    }

    /**
     * @return The field value
     */
    public String getLabel() {
        return mLabel;
    }

    @Override
    public boolean has(int key) {
        switch(key) {
            case 1: return hasName();
            case 2: return true;
            case 3: return hasLabel();
            default: return false;
        }
    }

    @Override
    public int num(int key) {
        switch(key) {
            case 1: return hasName() ? 1 : 0;
            case 2: return 1;
            case 3: return hasLabel() ? 1 : 0;
            default: return 0;
        }
    }

    @Override
    public Object get(int key) {
        switch(key) {
            case 1: return getName();
            case 2: return getId();
            case 3: return getLabel();
            default: return null;
        }
    }

    @Override
    public boolean compact() {
        boolean missing = false;
        if (hasName()) {
            if (missing) return false;
        } else {
            missing = true;
        }
        if (missing) return false;
        if (hasLabel()) {
            if (missing) return false;
        } else {
            missing = true;
        }
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        if (o == null || !(o instanceof CompactFields)) return false;
        CompactFields other = (CompactFields) o;
        return java.util.Objects.equals(mName, other.mName) &&
               java.util.Objects.equals(mId, other.mId) &&
               java.util.Objects.equals(mLabel, other.mLabel);
    }

    @Override
    public int hashCode() {
        if (tHashCode == 0) {
            tHashCode = java.util.Objects.hash(
                    CompactFields.class,
                    _Field.NAME, mName,
                    _Field.ID, mId,
                    _Field.LABEL, mLabel);
        }
        return tHashCode;
    }

    @Override
    public String toString() {
        return "providence.CompactFields" + asString();
    }

    @Override
    public String asString() {
        StringBuilder out = new StringBuilder();
        out.append("{");

        boolean first = true;
        if (mName != null) {
            first = false;
            out.append("name:")
               .append('\"')
               .append(net.morimekta.util.Strings.escape(mName))
               .append('\"');
        }
        out.append("id:")
           .append(mId);
        if (mLabel != null) {
            out.append(',');
            out.append("label:")
               .append('\"')
               .append(net.morimekta.util.Strings.escape(mLabel))
               .append('\"');
        }
        out.append('}');
        return out.toString();
    }

    @Override
    public int compareTo(CompactFields other) {
        int c;

        c = Boolean.compare(mName != null, other.mName != null);
        if (c != 0) return c;
        if (mName != null) {
            c = mName.compareTo(other.mName);
            if (c != 0) return c;
        }

        c = Integer.compare(mId, other.mId);
        if (c != 0) return c;

        c = Boolean.compare(mLabel != null, other.mLabel != null);
        if (c != 0) return c;
        if (mLabel != null) {
            c = mLabel.compareTo(other.mLabel);
            if (c != 0) return c;
        }

        return 0;
    }

    @Override
    public _Builder mutate() {
        return new _Builder(this);
    }

    public enum _Field implements net.morimekta.providence.descriptor.PField {
        NAME(1, net.morimekta.providence.descriptor.PRequirement.REQUIRED, "name", net.morimekta.providence.descriptor.PPrimitive.STRING.provider(), null),
        ID(2, net.morimekta.providence.descriptor.PRequirement.REQUIRED, "id", net.morimekta.providence.descriptor.PPrimitive.I32.provider(), null),
        LABEL(3, net.morimekta.providence.descriptor.PRequirement.DEFAULT, "label", net.morimekta.providence.descriptor.PPrimitive.STRING.provider(), null),
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
                case 1: return _Field.NAME;
                case 2: return _Field.ID;
                case 3: return _Field.LABEL;
            }
            return null;
        }

        public static _Field forName(String name) {
            switch (name) {
                case "name": return _Field.NAME;
                case "id": return _Field.ID;
                case "label": return _Field.LABEL;
            }
            return null;
        }
    }

    public static net.morimekta.providence.descriptor.PStructDescriptorProvider<CompactFields,_Field> provider() {
        return new _Provider();
    }

    @Override
    public net.morimekta.providence.descriptor.PStructDescriptor<CompactFields,_Field> descriptor() {
        return kDescriptor;
    }

    public static final net.morimekta.providence.descriptor.PStructDescriptor<CompactFields,_Field> kDescriptor;

    private static class _Descriptor
            extends net.morimekta.providence.descriptor.PStructDescriptor<CompactFields,_Field> {
        public _Descriptor() {
            super("providence", "CompactFields", new _Factory(), true, true);
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

    private final static class _Provider extends net.morimekta.providence.descriptor.PStructDescriptorProvider<CompactFields,_Field> {
        @Override
        public net.morimekta.providence.descriptor.PStructDescriptor<CompactFields,_Field> descriptor() {
            return kDescriptor;
        }
    }

    private final static class _Factory
            extends net.morimekta.providence.PMessageBuilderFactory<CompactFields,_Field> {
        @Override
        public _Builder builder() {
            return new _Builder();
        }
    }

    /**
     * Make a providence.CompactFields builder.
     * @return The builder instance.
     */
    public static _Builder builder() {
        return new _Builder();
    }

    public static class _Builder
            extends net.morimekta.providence.PMessageBuilder<CompactFields,_Field> {
        private java.util.BitSet optionals;

        private String mName;
        private int mId;
        private String mLabel;

        /**
         * Make a providence.CompactFields builder.
         */
        public _Builder() {
            optionals = new java.util.BitSet(3);
            mId = kDefaultId;
        }

        /**
         * Make a mutating builder off a base providence.CompactFields.
         *
         * @param base The base CompactFields
         */
        public _Builder(CompactFields base) {
            this();

            if (base.hasName()) {
                optionals.set(0);
                mName = base.mName;
            }
            optionals.set(1);
            mId = base.mId;
            if (base.hasLabel()) {
                optionals.set(2);
                mLabel = base.mLabel;
            }
        }

        @Override
        public _Builder merge(CompactFields from) {
            if (from.hasName()) {
                optionals.set(0);
                mName = from.getName();
            }

            optionals.set(1);
            mId = from.getId();

            if (from.hasLabel()) {
                optionals.set(2);
                mLabel = from.getLabel();
            }
            return this;
        }

        /**
         * Sets the value of name.
         *
         * @param value The new value
         * @return The builder
         */
        public _Builder setName(String value) {
            optionals.set(0);
            mName = value;
            return this;
        }

        /**
         * Checks for presence of the name field.
         *
         * @return True iff name has been set.
         */
        public boolean isSetName() {
            return optionals.get(0);
        }

        /**
         * Clears the name field.
         *
         * @return The builder
         */
        public _Builder clearName() {
            optionals.clear(0);
            mName = null;
            return this;
        }

        /**
         * Sets the value of id.
         *
         * @param value The new value
         * @return The builder
         */
        public _Builder setId(int value) {
            optionals.set(1);
            mId = value;
            return this;
        }

        /**
         * Checks for presence of the id field.
         *
         * @return True iff id has been set.
         */
        public boolean isSetId() {
            return optionals.get(1);
        }

        /**
         * Clears the id field.
         *
         * @return The builder
         */
        public _Builder clearId() {
            optionals.clear(1);
            mId = kDefaultId;
            return this;
        }

        /**
         * Sets the value of label.
         *
         * @param value The new value
         * @return The builder
         */
        public _Builder setLabel(String value) {
            optionals.set(2);
            mLabel = value;
            return this;
        }

        /**
         * Checks for presence of the label field.
         *
         * @return True iff label has been set.
         */
        public boolean isSetLabel() {
            return optionals.get(2);
        }

        /**
         * Clears the label field.
         *
         * @return The builder
         */
        public _Builder clearLabel() {
            optionals.clear(2);
            mLabel = null;
            return this;
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
                case 1: setName((String) value); break;
                case 2: setId((int) value); break;
                case 3: setLabel((String) value); break;
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
                case 1: clearName(); break;
                case 2: clearId(); break;
                case 3: clearLabel(); break;
                default: break;
            }
            return this;
        }

        @Override
        public boolean isValid() {
            return optionals.get(0) &&
                   optionals.get(1);
        }

        @Override
        public void validate() {
            if (!isValid()) {
                java.util.LinkedList<String> missing = new java.util.LinkedList<>();

                if (!optionals.get(0)) {
                    missing.add("name");
                }

                if (!optionals.get(1)) {
                    missing.add("id");
                }

                throw new java.lang.IllegalStateException(
                        "Missing required fields " +
                        String.join(",", missing) +
                        " in message providence.CompactFields");
            }
        }

        @Override
        public net.morimekta.providence.descriptor.PStructDescriptor<CompactFields,_Field> descriptor() {
            return kDescriptor;
        }

        @Override
        public CompactFields build() {
            return new CompactFields(this);
        }
    }
}
