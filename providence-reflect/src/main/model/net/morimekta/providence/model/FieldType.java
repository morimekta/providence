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
public class FieldType
        implements net.morimekta.providence.PMessage<FieldType,FieldType._Field>,
                   Comparable<FieldType>,
                   java.io.Serializable,
                   net.morimekta.providence.serializer.rw.BinaryWriter {
    private final static long serialVersionUID = -5885640707344801505L;

    private final static int kDefaultKey = 0;
    private final static net.morimekta.providence.model.FieldRequirement kDefaultRequirement = net.morimekta.providence.model.FieldRequirement.DEFAULT;

    private final String mDocumentation;
    private final int mKey;
    private final net.morimekta.providence.model.FieldRequirement mRequirement;
    private final String mType;
    private final String mName;
    private final String mDefaultValue;
    private final java.util.Map<String,String> mAnnotations;

    private volatile int tHashCode;

    public FieldType(String pDocumentation,
                     int pKey,
                     net.morimekta.providence.model.FieldRequirement pRequirement,
                     String pType,
                     String pName,
                     String pDefaultValue,
                     java.util.Map<String,String> pAnnotations) {
        mDocumentation = pDocumentation;
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

    private FieldType(_Builder builder) {
        mDocumentation = builder.mDocumentation;
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

    public boolean hasDocumentation() {
        return mDocumentation != null;
    }

    /**
     * @return The field value
     */
    public String getDocumentation() {
        return mDocumentation;
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
    public net.morimekta.providence.model.FieldRequirement getRequirement() {
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
            case 1: return hasDocumentation();
            case 2: return true;
            case 3: return hasRequirement();
            case 4: return hasType();
            case 5: return hasName();
            case 6: return hasDefaultValue();
            case 7: return hasAnnotations();
            default: return false;
        }
    }

    @Override
    public int num(int key) {
        switch(key) {
            case 1: return hasDocumentation() ? 1 : 0;
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
            case 1: return getDocumentation();
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
        if (o == null || !o.getClass().equals(getClass())) return false;
        FieldType other = (FieldType) o;
        return java.util.Objects.equals(mDocumentation, other.mDocumentation) &&
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
                    FieldType.class,
                    _Field.DOCUMENTATION, mDocumentation,
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
        return "model.FieldType" + asString();
    }

    @Override
    public String asString() {
        StringBuilder out = new StringBuilder();
        out.append("{");

        boolean first = true;
        if (hasDocumentation()) {
            first = false;
            out.append("documentation:")
               .append('\"')
               .append(net.morimekta.util.Strings.escape(mDocumentation))
               .append('\"');
        }
        out.append("key:")
           .append(mKey);
        if (hasRequirement()) {
            out.append(',');
            out.append("requirement:")
               .append(mRequirement.asString());
        }
        if (hasType()) {
            out.append(',');
            out.append("type:")
               .append('\"')
               .append(net.morimekta.util.Strings.escape(mType))
               .append('\"');
        }
        if (hasName()) {
            out.append(',');
            out.append("name:")
               .append('\"')
               .append(net.morimekta.util.Strings.escape(mName))
               .append('\"');
        }
        if (hasDefaultValue()) {
            out.append(',');
            out.append("default_value:")
               .append('\"')
               .append(net.morimekta.util.Strings.escape(mDefaultValue))
               .append('\"');
        }
        if (hasAnnotations()) {
            out.append(',');
            out.append("annotations:")
               .append(net.morimekta.util.Strings.asString(mAnnotations));
        }
        out.append('}');
        return out.toString();
    }

    @Override
    public int compareTo(FieldType other) {
        int c;

        c = Boolean.compare(mDocumentation != null, other.mDocumentation != null);
        if (c != 0) return c;
        if (mDocumentation != null) {
            c = mDocumentation.compareTo(other.mDocumentation);
            if (c != 0) return c;
        }

        c = Integer.compare(mKey, other.mKey);
        if (c != 0) return c;

        c = Boolean.compare(mRequirement != null, other.mRequirement != null);
        if (c != 0) return c;
        if (mRequirement != null) {
            c = Integer.compare(mRequirement.ordinal(), mRequirement.ordinal());
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

    @Override
    public int writeBinary(net.morimekta.util.io.BigEndianBinaryWriter writer) throws java.io.IOException {
        int length = 0;

        if (hasDocumentation()) {
            length += writer.writeByte((byte) 11);
            length += writer.writeShort((short) 1);
            net.morimekta.util.Binary tmp_1 = net.morimekta.util.Binary.wrap(mDocumentation.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            length += writer.writeUInt32(tmp_1.length());
            length += writer.writeBinary(tmp_1);
        }

        length += writer.writeByte((byte) 8);
        length += writer.writeShort((short) 2);
        length += writer.writeInt(mKey);

        if (hasRequirement()) {
            length += writer.writeByte((byte) 8);
            length += writer.writeShort((short) 3);
            length += writer.writeInt(mRequirement.getValue());
        }

        if (hasType()) {
            length += writer.writeByte((byte) 11);
            length += writer.writeShort((short) 4);
            net.morimekta.util.Binary tmp_2 = net.morimekta.util.Binary.wrap(mType.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            length += writer.writeUInt32(tmp_2.length());
            length += writer.writeBinary(tmp_2);
        }

        if (hasName()) {
            length += writer.writeByte((byte) 11);
            length += writer.writeShort((short) 5);
            net.morimekta.util.Binary tmp_3 = net.morimekta.util.Binary.wrap(mName.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            length += writer.writeUInt32(tmp_3.length());
            length += writer.writeBinary(tmp_3);
        }

        if (hasDefaultValue()) {
            length += writer.writeByte((byte) 11);
            length += writer.writeShort((short) 6);
            net.morimekta.util.Binary tmp_4 = net.morimekta.util.Binary.wrap(mDefaultValue.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            length += writer.writeUInt32(tmp_4.length());
            length += writer.writeBinary(tmp_4);
        }

        if (hasAnnotations()) {
            length += writer.writeByte((byte) 13);
            length += writer.writeShort((short) 7);
            length += writer.writeByte((byte) 11);
            length += writer.writeByte((byte) 11);
            length += writer.writeUInt32(mAnnotations.size());
            for (java.util.Map.Entry<String,String> entry_5 : mAnnotations.entrySet()) {
                net.morimekta.util.Binary tmp_6 = net.morimekta.util.Binary.wrap(entry_5.getKey().getBytes(java.nio.charset.StandardCharsets.UTF_8));
                length += writer.writeUInt32(tmp_6.length());
                length += writer.writeBinary(tmp_6);
                net.morimekta.util.Binary tmp_7 = net.morimekta.util.Binary.wrap(entry_5.getValue().getBytes(java.nio.charset.StandardCharsets.UTF_8));
                length += writer.writeUInt32(tmp_7.length());
                length += writer.writeBinary(tmp_7);
            }
        }

        length += writer.writeByte((byte) 0);
        return length;
    }

    @Override
    public _Builder mutate() {
        return new _Builder(this);
    }

    public enum _Field implements net.morimekta.providence.descriptor.PField {
        DOCUMENTATION(1, net.morimekta.providence.descriptor.PRequirement.DEFAULT, "documentation", net.morimekta.providence.descriptor.PPrimitive.STRING.provider(), null),
        KEY(2, net.morimekta.providence.descriptor.PRequirement.REQUIRED, "key", net.morimekta.providence.descriptor.PPrimitive.I32.provider(), null),
        REQUIREMENT(3, net.morimekta.providence.descriptor.PRequirement.DEFAULT, "requirement", net.morimekta.providence.model.FieldRequirement.provider(), new net.morimekta.providence.descriptor.PDefaultValueProvider<>(kDefaultRequirement)),
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
                case 1: return _Field.DOCUMENTATION;
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
                case "documentation": return _Field.DOCUMENTATION;
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

    public static net.morimekta.providence.descriptor.PStructDescriptorProvider<FieldType,_Field> provider() {
        return new _Provider();
    }

    @Override
    public net.morimekta.providence.descriptor.PStructDescriptor<FieldType,_Field> descriptor() {
        return kDescriptor;
    }

    public static final net.morimekta.providence.descriptor.PStructDescriptor<FieldType,_Field> kDescriptor;

    private static class _Descriptor
            extends net.morimekta.providence.descriptor.PStructDescriptor<FieldType,_Field> {
        public _Descriptor() {
            super("model", "FieldType", new _Factory(), false, false);
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

    private final static class _Provider extends net.morimekta.providence.descriptor.PStructDescriptorProvider<FieldType,_Field> {
        @Override
        public net.morimekta.providence.descriptor.PStructDescriptor<FieldType,_Field> descriptor() {
            return kDescriptor;
        }
    }

    private final static class _Factory
            extends net.morimekta.providence.PMessageBuilderFactory<FieldType,_Field> {
        @Override
        public _Builder builder() {
            return new _Builder();
        }
    }

    /**
     * Make a model.FieldType builder.
     * @return The builder instance.
     */
    public static _Builder builder() {
        return new _Builder();
    }

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
    public static class _Builder
            extends net.morimekta.providence.PMessageBuilder<FieldType,_Field>
            implements net.morimekta.providence.serializer.rw.BinaryReader {
        private java.util.BitSet optionals;
        private java.util.BitSet modified;

        private String mDocumentation;
        private int mKey;
        private net.morimekta.providence.model.FieldRequirement mRequirement;
        private String mType;
        private String mName;
        private String mDefaultValue;
        private net.morimekta.providence.descriptor.PMap.Builder<String,String> mAnnotations;

        /**
         * Make a model.FieldType builder.
         */
        public _Builder() {
            optionals = new java.util.BitSet(7);
            modified = new java.util.BitSet(7);
            mKey = kDefaultKey;
            mAnnotations = new net.morimekta.providence.descriptor.PMap.ImmutableMapBuilder<>();
        }

        /**
         * Make a mutating builder off a base model.FieldType.
         *
         * @param base The base FieldType
         */
        public _Builder(FieldType base) {
            this();

            if (base.hasDocumentation()) {
                optionals.set(0);
                mDocumentation = base.mDocumentation;
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
            if (base.hasAnnotations()) {
                optionals.set(6);
                mAnnotations.putAll(base.mAnnotations);
            }
        }

        @Override
        public _Builder merge(FieldType from) {
            if (from.hasDocumentation()) {
                optionals.set(0);
                modified.set(0);
                mDocumentation = from.getDocumentation();
            }

            optionals.set(1);
            modified.set(1);
            mKey = from.getKey();

            if (from.hasRequirement()) {
                optionals.set(2);
                modified.set(2);
                mRequirement = from.getRequirement();
            }

            if (from.hasType()) {
                optionals.set(3);
                modified.set(3);
                mType = from.getType();
            }

            if (from.hasName()) {
                optionals.set(4);
                modified.set(4);
                mName = from.getName();
            }

            if (from.hasDefaultValue()) {
                optionals.set(5);
                modified.set(5);
                mDefaultValue = from.getDefaultValue();
            }

            if (from.hasAnnotations()) {
                optionals.set(6);
                modified.set(6);
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
            modified.set(0);
            mDocumentation = value;
            return this;
        }

        /**
         * Checks for presence of the documentation field.
         *
         * @return True if documentation has been set.
         */
        public boolean isSetDocumentation() {
            return optionals.get(0);
        }

        /**
         * Checks if documentation has been modified since the _Builder was created.
         *
         * @return True if documentation has been modified.
         */
        public boolean isModifiedDocumentation() {
            return modified.get(0);
        }

        /**
         * Clears the documentation field.
         *
         * @return The builder
         */
        public _Builder clearDocumentation() {
            optionals.clear(0);
            modified.set(0);
            mDocumentation = null;
            return this;
        }

        /**
         * Gets the value of the contained documentation.
         *
         * @return The field value
         */
        public String getDocumentation() {
            return mDocumentation;
        }

        /**
         * Sets the value of key.
         *
         * @param value The new value
         * @return The builder
         */
        public _Builder setKey(int value) {
            optionals.set(1);
            modified.set(1);
            mKey = value;
            return this;
        }

        /**
         * Checks for presence of the key field.
         *
         * @return True if key has been set.
         */
        public boolean isSetKey() {
            return optionals.get(1);
        }

        /**
         * Checks if key has been modified since the _Builder was created.
         *
         * @return True if key has been modified.
         */
        public boolean isModifiedKey() {
            return modified.get(1);
        }

        /**
         * Clears the key field.
         *
         * @return The builder
         */
        public _Builder clearKey() {
            optionals.clear(1);
            modified.set(1);
            mKey = kDefaultKey;
            return this;
        }

        /**
         * Gets the value of the contained key.
         *
         * @return The field value
         */
        public int getKey() {
            return mKey;
        }

        /**
         * Sets the value of requirement.
         *
         * @param value The new value
         * @return The builder
         */
        public _Builder setRequirement(net.morimekta.providence.model.FieldRequirement value) {
            optionals.set(2);
            modified.set(2);
            mRequirement = value;
            return this;
        }

        /**
         * Checks for presence of the requirement field.
         *
         * @return True if requirement has been set.
         */
        public boolean isSetRequirement() {
            return optionals.get(2);
        }

        /**
         * Checks if requirement has been modified since the _Builder was created.
         *
         * @return True if requirement has been modified.
         */
        public boolean isModifiedRequirement() {
            return modified.get(2);
        }

        /**
         * Clears the requirement field.
         *
         * @return The builder
         */
        public _Builder clearRequirement() {
            optionals.clear(2);
            modified.set(2);
            mRequirement = null;
            return this;
        }

        /**
         * Gets the value of the contained requirement.
         *
         * @return The field value
         */
        public net.morimekta.providence.model.FieldRequirement getRequirement() {
            return isSetRequirement() ? mRequirement : kDefaultRequirement;
        }

        /**
         * Sets the value of type.
         *
         * @param value The new value
         * @return The builder
         */
        public _Builder setType(String value) {
            optionals.set(3);
            modified.set(3);
            mType = value;
            return this;
        }

        /**
         * Checks for presence of the type field.
         *
         * @return True if type has been set.
         */
        public boolean isSetType() {
            return optionals.get(3);
        }

        /**
         * Checks if type has been modified since the _Builder was created.
         *
         * @return True if type has been modified.
         */
        public boolean isModifiedType() {
            return modified.get(3);
        }

        /**
         * Clears the type field.
         *
         * @return The builder
         */
        public _Builder clearType() {
            optionals.clear(3);
            modified.set(3);
            mType = null;
            return this;
        }

        /**
         * Gets the value of the contained type.
         *
         * @return The field value
         */
        public String getType() {
            return mType;
        }

        /**
         * Sets the value of name.
         *
         * @param value The new value
         * @return The builder
         */
        public _Builder setName(String value) {
            optionals.set(4);
            modified.set(4);
            mName = value;
            return this;
        }

        /**
         * Checks for presence of the name field.
         *
         * @return True if name has been set.
         */
        public boolean isSetName() {
            return optionals.get(4);
        }

        /**
         * Checks if name has been modified since the _Builder was created.
         *
         * @return True if name has been modified.
         */
        public boolean isModifiedName() {
            return modified.get(4);
        }

        /**
         * Clears the name field.
         *
         * @return The builder
         */
        public _Builder clearName() {
            optionals.clear(4);
            modified.set(4);
            mName = null;
            return this;
        }

        /**
         * Gets the value of the contained name.
         *
         * @return The field value
         */
        public String getName() {
            return mName;
        }

        /**
         * Sets the value of default_value.
         *
         * @param value The new value
         * @return The builder
         */
        public _Builder setDefaultValue(String value) {
            optionals.set(5);
            modified.set(5);
            mDefaultValue = value;
            return this;
        }

        /**
         * Checks for presence of the default_value field.
         *
         * @return True if default_value has been set.
         */
        public boolean isSetDefaultValue() {
            return optionals.get(5);
        }

        /**
         * Checks if default_value has been modified since the _Builder was created.
         *
         * @return True if default_value has been modified.
         */
        public boolean isModifiedDefaultValue() {
            return modified.get(5);
        }

        /**
         * Clears the default_value field.
         *
         * @return The builder
         */
        public _Builder clearDefaultValue() {
            optionals.clear(5);
            modified.set(5);
            mDefaultValue = null;
            return this;
        }

        /**
         * Gets the value of the contained default_value.
         *
         * @return The field value
         */
        public String getDefaultValue() {
            return mDefaultValue;
        }

        /**
         * Sets the value of annotations.
         *
         * @param value The new value
         * @return The builder
         */
        public _Builder setAnnotations(java.util.Map<String,String> value) {
            optionals.set(6);
            modified.set(6);
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
            modified.set(6);
            mAnnotations.put(key, value);
            return this;
        }

        /**
         * Checks for presence of the annotations field.
         *
         * @return True if annotations has been set.
         */
        public boolean isSetAnnotations() {
            return optionals.get(6);
        }

        /**
         * Checks if annotations has been modified since the _Builder was created.
         *
         * @return True if annotations has been modified.
         */
        public boolean isModifiedAnnotations() {
            return modified.get(6);
        }

        /**
         * Clears the annotations field.
         *
         * @return The builder
         */
        public _Builder clearAnnotations() {
            optionals.clear(6);
            modified.set(6);
            mAnnotations.clear();
            return this;
        }

        /**
         * Gets the builder for the contained annotations.
         *
         * @return The field builder
         */
        public net.morimekta.providence.descriptor.PMap.Builder<String,String> mutableAnnotations() {
            optionals.set(6);
            modified.set(6);
            return mAnnotations;
        }

        @Override
        public boolean equals(Object o) {
            if (o == this) return true;
            if (o == null || !o.getClass().equals(getClass())) return false;
            FieldType._Builder other = (FieldType._Builder) o;
            return java.util.Objects.equals(optionals, other.optionals) &&
                   java.util.Objects.equals(mDocumentation, other.mDocumentation) &&
                   java.util.Objects.equals(mKey, other.mKey) &&
                   java.util.Objects.equals(mRequirement, other.mRequirement) &&
                   java.util.Objects.equals(mType, other.mType) &&
                   java.util.Objects.equals(mName, other.mName) &&
                   java.util.Objects.equals(mDefaultValue, other.mDefaultValue) &&
                   java.util.Objects.equals(mAnnotations, other.mAnnotations);
        }

        @Override
        public int hashCode() {
            return java.util.Objects.hash(
                    FieldType.class, optionals,
                    _Field.DOCUMENTATION, mDocumentation,
                    _Field.KEY, mKey,
                    _Field.REQUIREMENT, mRequirement,
                    _Field.TYPE, mType,
                    _Field.NAME, mName,
                    _Field.DEFAULT_VALUE, mDefaultValue,
                    _Field.ANNOTATIONS, mAnnotations);
        }

        /**
         * Get a java.util.Collection with _Field.
         */
        public java.util.Collection<_Field> modifiedFields() {
            return java.util.Arrays.asList(kDescriptor.getFields())
                    .stream().filter(f -> isModified(f))
                    .collect(java.util.stream.Collectors.toList());
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
                case 2: setKey((int) value); break;
                case 3: setRequirement((net.morimekta.providence.model.FieldRequirement) value); break;
                case 4: setType((String) value); break;
                case 5: setName((String) value); break;
                case 6: setDefaultValue((String) value); break;
                case 7: setAnnotations((java.util.Map<String,String>) value); break;
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
                case 5: return optionals.get(4);
                case 6: return optionals.get(5);
                case 7: return optionals.get(6);
                default: break;
            }
            return false;
        }

        @Override
        public boolean isModified(int key) {
            switch (key) {
                case 1: return modified.get(0);
                case 2: return modified.get(1);
                case 3: return modified.get(2);
                case 4: return modified.get(3);
                case 5: return modified.get(4);
                case 6: return modified.get(5);
                case 7: return modified.get(6);
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
                case 2: clearKey(); break;
                case 3: clearRequirement(); break;
                case 4: clearType(); break;
                case 5: clearName(); break;
                case 6: clearDefaultValue(); break;
                case 7: clearAnnotations(); break;
                default: break;
            }
            return this;
        }

        @Override
        public boolean valid() {
            return optionals.get(1) &&
                   optionals.get(3) &&
                   optionals.get(4);
        }

        @Override
        public void validate() {
            if (!valid()) {
                java.util.LinkedList<String> missing = new java.util.LinkedList<>();

                if (!optionals.get(1)) {
                    missing.add("key");
                }

                if (!optionals.get(3)) {
                    missing.add("type");
                }

                if (!optionals.get(4)) {
                    missing.add("name");
                }

                throw new java.lang.IllegalStateException(
                        "Missing required fields " +
                        String.join(",", missing) +
                        " in message model.FieldType");
            }
        }

        @Override
        public net.morimekta.providence.descriptor.PStructDescriptor<FieldType,_Field> descriptor() {
            return kDescriptor;
        }

        @Override
        public void readBinary(net.morimekta.util.io.BigEndianBinaryReader reader, boolean strict) throws java.io.IOException {
            byte type = reader.expectByte();
            while (type != 0) {
                int field = reader.expectShort();
                switch (field) {
                    case 1: {
                        if (type == 11) {
                            int len_1 = reader.expectUInt32();
                            mDocumentation = new String(reader.expectBytes(len_1), java.nio.charset.StandardCharsets.UTF_8);
                            optionals.set(0);
                        } else {
                            throw new net.morimekta.providence.serializer.SerializerException("Wrong type " + type + " for model.FieldType.documentation, should be 12");
                        }
                        break;
                    }
                    case 2: {
                        if (type == 8) {
                            mKey = reader.expectInt();
                            optionals.set(1);
                        } else {
                            throw new net.morimekta.providence.serializer.SerializerException("Wrong type " + type + " for model.FieldType.key, should be 12");
                        }
                        break;
                    }
                    case 3: {
                        if (type == 8) {
                            mRequirement = net.morimekta.providence.model.FieldRequirement.forValue(reader.expectInt());
                            optionals.set(2);
                        } else {
                            throw new net.morimekta.providence.serializer.SerializerException("Wrong type " + type + " for model.FieldType.requirement, should be 12");
                        }
                        break;
                    }
                    case 4: {
                        if (type == 11) {
                            int len_2 = reader.expectUInt32();
                            mType = new String(reader.expectBytes(len_2), java.nio.charset.StandardCharsets.UTF_8);
                            optionals.set(3);
                        } else {
                            throw new net.morimekta.providence.serializer.SerializerException("Wrong type " + type + " for model.FieldType.type, should be 12");
                        }
                        break;
                    }
                    case 5: {
                        if (type == 11) {
                            int len_3 = reader.expectUInt32();
                            mName = new String(reader.expectBytes(len_3), java.nio.charset.StandardCharsets.UTF_8);
                            optionals.set(4);
                        } else {
                            throw new net.morimekta.providence.serializer.SerializerException("Wrong type " + type + " for model.FieldType.name, should be 12");
                        }
                        break;
                    }
                    case 6: {
                        if (type == 11) {
                            int len_4 = reader.expectUInt32();
                            mDefaultValue = new String(reader.expectBytes(len_4), java.nio.charset.StandardCharsets.UTF_8);
                            optionals.set(5);
                        } else {
                            throw new net.morimekta.providence.serializer.SerializerException("Wrong type " + type + " for model.FieldType.default_value, should be 12");
                        }
                        break;
                    }
                    case 7: {
                        if (type == 13) {
                            byte t_6 = reader.expectByte();
                            byte t_7 = reader.expectByte();
                            if (t_6 == 11 && t_7 == 11) {
                                final int len_5 = reader.expectUInt32();
                                for (int i_8 = 0; i_8 < len_5; ++i_8) {
                                    int len_11 = reader.expectUInt32();
                                    String key_9 = new String(reader.expectBytes(len_11), java.nio.charset.StandardCharsets.UTF_8);
                                    int len_12 = reader.expectUInt32();
                                    String val_10 = new String(reader.expectBytes(len_12), java.nio.charset.StandardCharsets.UTF_8);
                                    mAnnotations.put(key_9, val_10);
                                }
                            } else {
                                throw new net.morimekta.providence.serializer.SerializerException("Wrong key type " + t_6 + " or value type " + t_7 + " for model.FieldType.annotations, should be 11 and 11");
                            }
                            optionals.set(6);
                        } else {
                            throw new net.morimekta.providence.serializer.SerializerException("Wrong type " + type + " for model.FieldType.annotations, should be 12");
                        }
                        break;
                    }
                    default: {
                        if (strict) {
                            throw new net.morimekta.providence.serializer.SerializerException("No field with id " + field + " exists in model.FieldType");
                        } else {
                            net.morimekta.providence.serializer.rw.BinaryFormatUtils.readFieldValue(reader, new net.morimekta.providence.serializer.rw.BinaryFormatUtils.FieldInfo(field, type), null, false);
                        }
                        break;
                    }
                }
                type = reader.expectByte();
            }
        }

        @Override
        public FieldType build() {
            return new FieldType(this);
        }
    }
}
