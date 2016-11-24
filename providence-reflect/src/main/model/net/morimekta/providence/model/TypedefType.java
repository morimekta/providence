package net.morimekta.providence.model;

/**
 * typedef &lt;type&gt; &lt;name&gt;
 */
@SuppressWarnings("unused")
public class TypedefType
        implements Comparable<TypedefType>,
                   java.io.Serializable,
                   net.morimekta.providence.PMessage<TypedefType,TypedefType._Field> {
    private final static long serialVersionUID = 5431583053440540554L;


    private final String mComment;
    private final String mType;
    private final String mName;

    private volatile int tHashCode;

    public TypedefType(String pComment,
                       String pType,
                       String pName) {
        mComment = pComment;
        mType = pType;
        mName = pName;
    }

    private TypedefType(_Builder builder) {
        mComment = builder.mComment;
        mType = builder.mType;
        mName = builder.mName;
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

    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        if (o == null || !(o instanceof TypedefType)) return false;
        TypedefType other = (TypedefType) o;
        return java.util.Objects.equals(mComment, other.mComment) &&
               java.util.Objects.equals(mType, other.mType) &&
               java.util.Objects.equals(mName, other.mName);
    }

    @Override
    public int hashCode() {
        if (tHashCode == 0) {
            tHashCode = java.util.Objects.hash(
                    TypedefType.class,
                    _Field.COMMENT, mComment,
                    _Field.TYPE, mType,
                    _Field.NAME, mName);
        }
        return tHashCode;
    }

    @Override
    public String toString() {
        return "model.TypedefType" + asString();
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
        if (mType != null) {
            if (first) first = false;
            else out.append(',');
            out.append("type:")
               .append('\"')
               .append(net.morimekta.util.Strings.escape(mType))
               .append('\"');
        }
        if (mName != null) {
            if (!first) out.append(',');
            out.append("name:")
               .append('\"')
               .append(net.morimekta.util.Strings.escape(mName))
               .append('\"');
        }
        out.append('}');
        return out.toString();
    }

    @Override
    public int compareTo(TypedefType other) {
        int c;

        c = Boolean.compare(mComment != null, other.mComment != null);
        if (c != 0) return c;
        if (mComment != null) {
            c = mComment.compareTo(other.mComment);
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

        return 0;
    }

    @Override
    public boolean has(int key) {
        switch(key) {
            case 1: return hasComment();
            case 2: return hasType();
            case 3: return hasName();
            default: return false;
        }
    }

    @Override
    public int num(int key) {
        switch(key) {
            case 1: return hasComment() ? 1 : 0;
            case 2: return hasType() ? 1 : 0;
            case 3: return hasName() ? 1 : 0;
            default: return 0;
        }
    }

    @Override
    public Object get(int key) {
        switch(key) {
            case 1: return getComment();
            case 2: return getType();
            case 3: return getName();
            default: return null;
        }
    }

    @Override
    public boolean compact() {
        return false;
    }

    @Override
    public _Builder mutate() {
        return new _Builder(this);
    }

    public enum _Field implements net.morimekta.providence.descriptor.PField {
        COMMENT(1, net.morimekta.providence.descriptor.PRequirement.DEFAULT, "comment", net.morimekta.providence.descriptor.PPrimitive.STRING.provider(), null),
        TYPE(2, net.morimekta.providence.descriptor.PRequirement.DEFAULT, "type", net.morimekta.providence.descriptor.PPrimitive.STRING.provider(), null),
        NAME(3, net.morimekta.providence.descriptor.PRequirement.DEFAULT, "name", net.morimekta.providence.descriptor.PPrimitive.STRING.provider(), null),
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
                case 2: return _Field.TYPE;
                case 3: return _Field.NAME;
            }
            return null;
        }

        public static _Field forName(String name) {
            switch (name) {
                case "comment": return _Field.COMMENT;
                case "type": return _Field.TYPE;
                case "name": return _Field.NAME;
            }
            return null;
        }
    }

    public static net.morimekta.providence.descriptor.PStructDescriptorProvider<TypedefType,_Field> provider() {
        return new _Provider();
    }

    @Override
    public net.morimekta.providence.descriptor.PStructDescriptor<TypedefType,_Field> descriptor() {
        return kDescriptor;
    }

    public static final net.morimekta.providence.descriptor.PStructDescriptor<TypedefType,_Field> kDescriptor;

    private static class _Descriptor
            extends net.morimekta.providence.descriptor.PStructDescriptor<TypedefType,_Field> {
        public _Descriptor() {
            super("model", "TypedefType", new _Factory(), true, false);
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

    private final static class _Provider extends net.morimekta.providence.descriptor.PStructDescriptorProvider<TypedefType,_Field> {
        @Override
        public net.morimekta.providence.descriptor.PStructDescriptor<TypedefType,_Field> descriptor() {
            return kDescriptor;
        }
    }

    private final static class _Factory
            extends net.morimekta.providence.PMessageBuilderFactory<TypedefType,_Field> {
        @Override
        public _Builder builder() {
            return new _Builder();
        }
    }

    /**
     * Make a model.TypedefType builder.
     * @return The builder instance.
     */
    public static _Builder builder() {
        return new _Builder();
    }

    /**
     * typedef &lt;type&gt; &lt;name&gt;
     */
    public static class _Builder
            extends net.morimekta.providence.PMessageBuilder<TypedefType,_Field> {
        private java.util.BitSet optionals;

        private String mComment;
        private String mType;
        private String mName;

        /**
         * Make a model.TypedefType builder.
         */
        public _Builder() {
            optionals = new java.util.BitSet(3);
        }

        /**
         * Make a mutating builder off a base model.TypedefType.
         *
         * @param base The base TypedefType
         */
        public _Builder(TypedefType base) {
            this();

            if (base.hasComment()) {
                optionals.set(0);
                mComment = base.mComment;
            }
            if (base.hasType()) {
                optionals.set(1);
                mType = base.mType;
            }
            if (base.hasName()) {
                optionals.set(2);
                mName = base.mName;
            }
        }

        @Override
        public _Builder merge(TypedefType from) {
            if (from.hasComment()) {
                optionals.set(0);
                mComment = from.getComment();
            }

            if (from.hasType()) {
                optionals.set(1);
                mType = from.getType();
            }

            if (from.hasName()) {
                optionals.set(2);
                mName = from.getName();
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
                case 2: setType((String) value); break;
                case 3: setName((String) value); break;
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
                case 2: clearType(); break;
                case 3: clearName(); break;
            }
            return this;
        }

        @Override
        public boolean isValid() {
            return true;
        }

        @Override
        public void validate() {
        }

        @Override
        public net.morimekta.providence.descriptor.PStructDescriptor<TypedefType,_Field> descriptor() {
            return kDescriptor;
        }

        @Override
        public TypedefType build() {
            return new TypedefType(this);
        }
    }
}