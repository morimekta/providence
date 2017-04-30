package net.morimekta.providence.model;

/**
 * &lt;variant&gt; {
 *   (&lt;field&gt; ([,;])?)*
 * }
 */
@SuppressWarnings("unused")
public class MessageType
        implements net.morimekta.providence.PMessage<MessageType,MessageType._Field>,
                   Comparable<MessageType>,
                   java.io.Serializable,
                   net.morimekta.providence.serializer.rw.BinaryWriter {
    private final static long serialVersionUID = -7041659190974449690L;

    private final static net.morimekta.providence.model.MessageVariant kDefaultVariant = net.morimekta.providence.model.MessageVariant.STRUCT;

    private final String mDocumentation;
    private final net.morimekta.providence.model.MessageVariant mVariant;
    private final String mName;
    private final java.util.List<net.morimekta.providence.model.FieldType> mFields;
    private final java.util.Map<String,String> mAnnotations;

    private volatile int tHashCode;

    public MessageType(String pDocumentation,
                       net.morimekta.providence.model.MessageVariant pVariant,
                       String pName,
                       java.util.List<net.morimekta.providence.model.FieldType> pFields,
                       java.util.Map<String,String> pAnnotations) {
        mDocumentation = pDocumentation;
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

    private MessageType(_Builder builder) {
        mDocumentation = builder.mDocumentation;
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

    public boolean hasDocumentation() {
        return mDocumentation != null;
    }

    /**
     * @return The field value
     */
    public String getDocumentation() {
        return mDocumentation;
    }

    public boolean hasVariant() {
        return mVariant != null;
    }

    /**
     * @return The field value
     */
    public net.morimekta.providence.model.MessageVariant getVariant() {
        return hasVariant() ? mVariant : kDefaultVariant;
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

    public int numFields() {
        return mFields != null ? mFields.size() : 0;
    }

    public boolean hasFields() {
        return mFields != null;
    }

    /**
     * @return The field value
     */
    public java.util.List<net.morimekta.providence.model.FieldType> getFields() {
        return mFields;
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
            case 2: return hasVariant();
            case 3: return hasName();
            case 4: return hasFields();
            case 5: return hasAnnotations();
            default: return false;
        }
    }

    @Override
    public int num(int key) {
        switch(key) {
            case 1: return hasDocumentation() ? 1 : 0;
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
            case 1: return getDocumentation();
            case 2: return getVariant();
            case 3: return getName();
            case 4: return getFields();
            case 5: return getAnnotations();
            default: return null;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        if (o == null || !o.getClass().equals(getClass())) return false;
        MessageType other = (MessageType) o;
        return java.util.Objects.equals(mDocumentation, other.mDocumentation) &&
               java.util.Objects.equals(mVariant, other.mVariant) &&
               java.util.Objects.equals(mName, other.mName) &&
               java.util.Objects.equals(mFields, other.mFields) &&
               java.util.Objects.equals(mAnnotations, other.mAnnotations);
    }

    @Override
    public int hashCode() {
        if (tHashCode == 0) {
            tHashCode = java.util.Objects.hash(
                    MessageType.class,
                    _Field.DOCUMENTATION, mDocumentation,
                    _Field.VARIANT, mVariant,
                    _Field.NAME, mName,
                    _Field.FIELDS, mFields,
                    _Field.ANNOTATIONS, mAnnotations);
        }
        return tHashCode;
    }

    @Override
    public String toString() {
        return "model.MessageType" + asString();
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
        if (hasVariant()) {
            if (first) first = false;
            else out.append(',');
            out.append("variant:")
               .append(mVariant.asString());
        }
        if (hasName()) {
            if (first) first = false;
            else out.append(',');
            out.append("name:")
               .append('\"')
               .append(net.morimekta.util.Strings.escape(mName))
               .append('\"');
        }
        if (hasFields()) {
            if (first) first = false;
            else out.append(',');
            out.append("fields:")
               .append(net.morimekta.util.Strings.asString(mFields));
        }
        if (hasAnnotations()) {
            if (!first) out.append(',');
            out.append("annotations:")
               .append(net.morimekta.util.Strings.asString(mAnnotations));
        }
        out.append('}');
        return out.toString();
    }

    @Override
    public int compareTo(MessageType other) {
        int c;

        c = Boolean.compare(mDocumentation != null, other.mDocumentation != null);
        if (c != 0) return c;
        if (mDocumentation != null) {
            c = mDocumentation.compareTo(other.mDocumentation);
            if (c != 0) return c;
        }

        c = Boolean.compare(mVariant != null, other.mVariant != null);
        if (c != 0) return c;
        if (mVariant != null) {
            c = Integer.compare(mVariant.ordinal(), mVariant.ordinal());
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

        if (hasVariant()) {
            length += writer.writeByte((byte) 8);
            length += writer.writeShort((short) 2);
            length += writer.writeInt(mVariant.getValue());
        }

        if (hasName()) {
            length += writer.writeByte((byte) 11);
            length += writer.writeShort((short) 3);
            net.morimekta.util.Binary tmp_2 = net.morimekta.util.Binary.wrap(mName.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            length += writer.writeUInt32(tmp_2.length());
            length += writer.writeBinary(tmp_2);
        }

        if (hasFields()) {
            length += writer.writeByte((byte) 15);
            length += writer.writeShort((short) 4);
            length += writer.writeByte((byte) 12);
            length += writer.writeUInt32(mFields.size());
            for (net.morimekta.providence.model.FieldType entry_3 : mFields) {
                length += net.morimekta.providence.serializer.rw.BinaryFormatUtils.writeMessage(writer, entry_3);
            }
        }

        if (hasAnnotations()) {
            length += writer.writeByte((byte) 13);
            length += writer.writeShort((short) 5);
            length += writer.writeByte((byte) 11);
            length += writer.writeByte((byte) 11);
            length += writer.writeUInt32(mAnnotations.size());
            for (java.util.Map.Entry<String,String> entry_4 : mAnnotations.entrySet()) {
                net.morimekta.util.Binary tmp_5 = net.morimekta.util.Binary.wrap(entry_4.getKey().getBytes(java.nio.charset.StandardCharsets.UTF_8));
                length += writer.writeUInt32(tmp_5.length());
                length += writer.writeBinary(tmp_5);
                net.morimekta.util.Binary tmp_6 = net.morimekta.util.Binary.wrap(entry_4.getValue().getBytes(java.nio.charset.StandardCharsets.UTF_8));
                length += writer.writeUInt32(tmp_6.length());
                length += writer.writeBinary(tmp_6);
            }
        }

        length += writer.writeByte((byte) 0);
        return length;
    }

    @javax.annotation.Nonnull
    @Override
    public _Builder mutate() {
        return new _Builder(this);
    }

    public enum _Field implements net.morimekta.providence.descriptor.PField {
        DOCUMENTATION(1, net.morimekta.providence.descriptor.PRequirement.DEFAULT, "documentation", net.morimekta.providence.descriptor.PPrimitive.STRING.provider(), null),
        VARIANT(2, net.morimekta.providence.descriptor.PRequirement.DEFAULT, "variant", net.morimekta.providence.model.MessageVariant.provider(), new net.morimekta.providence.descriptor.PDefaultValueProvider<>(kDefaultVariant)),
        NAME(3, net.morimekta.providence.descriptor.PRequirement.REQUIRED, "name", net.morimekta.providence.descriptor.PPrimitive.STRING.provider(), null),
        FIELDS(4, net.morimekta.providence.descriptor.PRequirement.DEFAULT, "fields", net.morimekta.providence.descriptor.PList.provider(net.morimekta.providence.model.FieldType.provider()), null),
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
            return net.morimekta.providence.descriptor.PField.asString(this);
        }

        public static _Field forKey(int key) {
            switch (key) {
                case 1: return _Field.DOCUMENTATION;
                case 2: return _Field.VARIANT;
                case 3: return _Field.NAME;
                case 4: return _Field.FIELDS;
                case 5: return _Field.ANNOTATIONS;
            }
            return null;
        }

        public static _Field forName(String name) {
            switch (name) {
                case "documentation": return _Field.DOCUMENTATION;
                case "variant": return _Field.VARIANT;
                case "name": return _Field.NAME;
                case "fields": return _Field.FIELDS;
                case "annotations": return _Field.ANNOTATIONS;
            }
            return null;
        }
    }

    public static net.morimekta.providence.descriptor.PStructDescriptorProvider<MessageType,_Field> provider() {
        return new _Provider();
    }

    @Override
    public net.morimekta.providence.descriptor.PStructDescriptor<MessageType,_Field> descriptor() {
        return kDescriptor;
    }

    public static final net.morimekta.providence.descriptor.PStructDescriptor<MessageType,_Field> kDescriptor;

    private static class _Descriptor
            extends net.morimekta.providence.descriptor.PStructDescriptor<MessageType,_Field> {
        public _Descriptor() {
            super("model", "MessageType", new _Factory(), false);
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

    private final static class _Provider extends net.morimekta.providence.descriptor.PStructDescriptorProvider<MessageType,_Field> {
        @Override
        public net.morimekta.providence.descriptor.PStructDescriptor<MessageType,_Field> descriptor() {
            return kDescriptor;
        }
    }

    private final static class _Factory
            extends net.morimekta.providence.PMessageBuilderFactory<MessageType,_Field> {
        @Override
        public _Builder builder() {
            return new _Builder();
        }
    }

    /**
     * Make a model.MessageType builder.
     * @return The builder instance.
     */
    public static _Builder builder() {
        return new _Builder();
    }

    /**
     * &lt;variant&gt; {
     *   (&lt;field&gt; ([,;])?)*
     * }
     */
    public static class _Builder
            extends net.morimekta.providence.PMessageBuilder<MessageType,_Field>
            implements net.morimekta.providence.serializer.rw.BinaryReader {
        private java.util.BitSet optionals;
        private java.util.BitSet modified;

        private String mDocumentation;
        private net.morimekta.providence.model.MessageVariant mVariant;
        private String mName;
        private net.morimekta.providence.descriptor.PList.Builder<net.morimekta.providence.model.FieldType> mFields;
        private net.morimekta.providence.descriptor.PMap.Builder<String,String> mAnnotations;

        /**
         * Make a model.MessageType builder.
         */
        public _Builder() {
            optionals = new java.util.BitSet(5);
            modified = new java.util.BitSet(5);
            mFields = new net.morimekta.providence.descriptor.PList.ImmutableListBuilder<>();
            mAnnotations = new net.morimekta.providence.descriptor.PMap.ImmutableMapBuilder<>();
        }

        /**
         * Make a mutating builder off a base model.MessageType.
         *
         * @param base The base MessageType
         */
        public _Builder(MessageType base) {
            this();

            if (base.hasDocumentation()) {
                optionals.set(0);
                mDocumentation = base.mDocumentation;
            }
            if (base.hasVariant()) {
                optionals.set(1);
                mVariant = base.mVariant;
            }
            if (base.hasName()) {
                optionals.set(2);
                mName = base.mName;
            }
            if (base.hasFields()) {
                optionals.set(3);
                mFields.addAll(base.mFields);
            }
            if (base.hasAnnotations()) {
                optionals.set(4);
                mAnnotations.putAll(base.mAnnotations);
            }
        }

        @javax.annotation.Nonnull
        @Override
        public _Builder merge(MessageType from) {
            if (from.hasDocumentation()) {
                optionals.set(0);
                modified.set(0);
                mDocumentation = from.getDocumentation();
            }

            if (from.hasVariant()) {
                optionals.set(1);
                modified.set(1);
                mVariant = from.getVariant();
            }

            if (from.hasName()) {
                optionals.set(2);
                modified.set(2);
                mName = from.getName();
            }

            if (from.hasFields()) {
                optionals.set(3);
                modified.set(3);
                mFields.clear();
                mFields.addAll(from.getFields());
            }

            if (from.hasAnnotations()) {
                optionals.set(4);
                modified.set(4);
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
        @javax.annotation.Nonnull
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
        @javax.annotation.Nonnull
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
         * Sets the value of variant.
         *
         * @param value The new value
         * @return The builder
         */
        @javax.annotation.Nonnull
        public _Builder setVariant(net.morimekta.providence.model.MessageVariant value) {
            optionals.set(1);
            modified.set(1);
            mVariant = value;
            return this;
        }

        /**
         * Checks for presence of the variant field.
         *
         * @return True if variant has been set.
         */
        public boolean isSetVariant() {
            return optionals.get(1);
        }

        /**
         * Checks if variant has been modified since the _Builder was created.
         *
         * @return True if variant has been modified.
         */
        public boolean isModifiedVariant() {
            return modified.get(1);
        }

        /**
         * Clears the variant field.
         *
         * @return The builder
         */
        @javax.annotation.Nonnull
        public _Builder clearVariant() {
            optionals.clear(1);
            modified.set(1);
            mVariant = null;
            return this;
        }

        /**
         * Gets the value of the contained variant.
         *
         * @return The field value
         */
        public net.morimekta.providence.model.MessageVariant getVariant() {
            return isSetVariant() ? mVariant : kDefaultVariant;
        }

        /**
         * Sets the value of name.
         *
         * @param value The new value
         * @return The builder
         */
        @javax.annotation.Nonnull
        public _Builder setName(String value) {
            optionals.set(2);
            modified.set(2);
            mName = value;
            return this;
        }

        /**
         * Checks for presence of the name field.
         *
         * @return True if name has been set.
         */
        public boolean isSetName() {
            return optionals.get(2);
        }

        /**
         * Checks if name has been modified since the _Builder was created.
         *
         * @return True if name has been modified.
         */
        public boolean isModifiedName() {
            return modified.get(2);
        }

        /**
         * Clears the name field.
         *
         * @return The builder
         */
        @javax.annotation.Nonnull
        public _Builder clearName() {
            optionals.clear(2);
            modified.set(2);
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
         * Sets the value of fields.
         *
         * @param value The new value
         * @return The builder
         */
        @javax.annotation.Nonnull
        public _Builder setFields(java.util.Collection<net.morimekta.providence.model.FieldType> value) {
            optionals.set(3);
            modified.set(3);
            mFields.clear();
            mFields.addAll(value);
            return this;
        }

        /**
         * Adds entries to fields.
         *
         * @param values The added value
         * @return The builder
         */
        @javax.annotation.Nonnull
        public _Builder addToFields(net.morimekta.providence.model.FieldType... values) {
            optionals.set(3);
            modified.set(3);
            for (net.morimekta.providence.model.FieldType item : values) {
                mFields.add(item);
            }
            return this;
        }

        /**
         * Checks for presence of the fields field.
         *
         * @return True if fields has been set.
         */
        public boolean isSetFields() {
            return optionals.get(3);
        }

        /**
         * Checks if fields has been modified since the _Builder was created.
         *
         * @return True if fields has been modified.
         */
        public boolean isModifiedFields() {
            return modified.get(3);
        }

        /**
         * Clears the fields field.
         *
         * @return The builder
         */
        @javax.annotation.Nonnull
        public _Builder clearFields() {
            optionals.clear(3);
            modified.set(3);
            mFields.clear();
            return this;
        }

        /**
         * Gets the builder for the contained fields.
         *
         * @return The field builder
         */
        public net.morimekta.providence.descriptor.PList.Builder<net.morimekta.providence.model.FieldType> mutableFields() {
            optionals.set(3);
            modified.set(3);
            return mFields;
        }

        /**
         * Sets the value of annotations.
         *
         * @param value The new value
         * @return The builder
         */
        @javax.annotation.Nonnull
        public _Builder setAnnotations(java.util.Map<String,String> value) {
            optionals.set(4);
            modified.set(4);
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
        @javax.annotation.Nonnull
        public _Builder putInAnnotations(String key, String value) {
            optionals.set(4);
            modified.set(4);
            mAnnotations.put(key, value);
            return this;
        }

        /**
         * Checks for presence of the annotations field.
         *
         * @return True if annotations has been set.
         */
        public boolean isSetAnnotations() {
            return optionals.get(4);
        }

        /**
         * Checks if annotations has been modified since the _Builder was created.
         *
         * @return True if annotations has been modified.
         */
        public boolean isModifiedAnnotations() {
            return modified.get(4);
        }

        /**
         * Clears the annotations field.
         *
         * @return The builder
         */
        @javax.annotation.Nonnull
        public _Builder clearAnnotations() {
            optionals.clear(4);
            modified.set(4);
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
            modified.set(4);
            return mAnnotations;
        }

        @Override
        public boolean equals(Object o) {
            if (o == this) return true;
            if (o == null || !o.getClass().equals(getClass())) return false;
            MessageType._Builder other = (MessageType._Builder) o;
            return java.util.Objects.equals(optionals, other.optionals) &&
                   java.util.Objects.equals(mDocumentation, other.mDocumentation) &&
                   java.util.Objects.equals(mVariant, other.mVariant) &&
                   java.util.Objects.equals(mName, other.mName) &&
                   java.util.Objects.equals(mFields, other.mFields) &&
                   java.util.Objects.equals(mAnnotations, other.mAnnotations);
        }

        @Override
        public int hashCode() {
            return java.util.Objects.hash(
                    MessageType.class, optionals,
                    _Field.DOCUMENTATION, mDocumentation,
                    _Field.VARIANT, mVariant,
                    _Field.NAME, mName,
                    _Field.FIELDS, mFields,
                    _Field.ANNOTATIONS, mAnnotations);
        }

        @Override
        @SuppressWarnings("unchecked")
        public net.morimekta.providence.PMessageBuilder mutator(int key) {
            switch (key) {
                default: throw new IllegalArgumentException("Not a message field ID: " + key);
            }
        }

        @javax.annotation.Nonnull
        @Override
        @SuppressWarnings("unchecked")
        public _Builder set(int key, Object value) {
            if (value == null) return clear(key);
            switch (key) {
                case 1: setDocumentation((String) value); break;
                case 2: setVariant((net.morimekta.providence.model.MessageVariant) value); break;
                case 3: setName((String) value); break;
                case 4: setFields((java.util.List<net.morimekta.providence.model.FieldType>) value); break;
                case 5: setAnnotations((java.util.Map<String,String>) value); break;
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
                default: break;
            }
            return false;
        }

        @Override
        public _Builder addTo(int key, Object value) {
            switch (key) {
                case 4: addToFields((net.morimekta.providence.model.FieldType) value); break;
                default: break;
            }
            return this;
        }

        @javax.annotation.Nonnull
        @Override
        public _Builder clear(int key) {
            switch (key) {
                case 1: clearDocumentation(); break;
                case 2: clearVariant(); break;
                case 3: clearName(); break;
                case 4: clearFields(); break;
                case 5: clearAnnotations(); break;
                default: break;
            }
            return this;
        }

        @Override
        public boolean valid() {
            return optionals.get(2);
        }

        @Override
        public void validate() {
            if (!valid()) {
                java.util.LinkedList<String> missing = new java.util.LinkedList<>();

                if (!optionals.get(2)) {
                    missing.add("name");
                }

                throw new java.lang.IllegalStateException(
                        "Missing required fields " +
                        String.join(",", missing) +
                        " in message model.MessageType");
            }
        }

        @javax.annotation.Nonnull
        @Override
        public net.morimekta.providence.descriptor.PStructDescriptor<MessageType,_Field> descriptor() {
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
                            throw new net.morimekta.providence.serializer.SerializerException("Wrong type " + net.morimekta.providence.serializer.rw.BinaryType.asString(type) + " for model.MessageType.documentation, should be struct(12)");
                        }
                        break;
                    }
                    case 2: {
                        if (type == 8) {
                            mVariant = net.morimekta.providence.model.MessageVariant.forValue(reader.expectInt());
                            optionals.set(1);
                        } else {
                            throw new net.morimekta.providence.serializer.SerializerException("Wrong type " + net.morimekta.providence.serializer.rw.BinaryType.asString(type) + " for model.MessageType.variant, should be struct(12)");
                        }
                        break;
                    }
                    case 3: {
                        if (type == 11) {
                            int len_2 = reader.expectUInt32();
                            mName = new String(reader.expectBytes(len_2), java.nio.charset.StandardCharsets.UTF_8);
                            optionals.set(2);
                        } else {
                            throw new net.morimekta.providence.serializer.SerializerException("Wrong type " + net.morimekta.providence.serializer.rw.BinaryType.asString(type) + " for model.MessageType.name, should be struct(12)");
                        }
                        break;
                    }
                    case 4: {
                        if (type == 15) {
                            byte t_4 = reader.expectByte();
                            if (t_4 == 12) {
                                final int len_3 = reader.expectUInt32();
                                for (int i_5 = 0; i_5 < len_3; ++i_5) {
                                    net.morimekta.providence.model.FieldType key_6 = net.morimekta.providence.serializer.rw.BinaryFormatUtils.readMessage(reader, net.morimekta.providence.model.FieldType.kDescriptor, strict);
                                    mFields.add(key_6);
                                }
                            } else {
                                throw new net.morimekta.providence.serializer.SerializerException("Wrong item type " + net.morimekta.providence.serializer.rw.BinaryType.asString(t_4) + " for model.MessageType.fields, should be struct(12)");
                            }
                            optionals.set(3);
                        } else {
                            throw new net.morimekta.providence.serializer.SerializerException("Wrong type " + net.morimekta.providence.serializer.rw.BinaryType.asString(type) + " for model.MessageType.fields, should be struct(12)");
                        }
                        break;
                    }
                    case 5: {
                        if (type == 13) {
                            byte t_8 = reader.expectByte();
                            byte t_9 = reader.expectByte();
                            if (t_8 == 11 && t_9 == 11) {
                                final int len_7 = reader.expectUInt32();
                                for (int i_10 = 0; i_10 < len_7; ++i_10) {
                                    int len_13 = reader.expectUInt32();
                                    String key_11 = new String(reader.expectBytes(len_13), java.nio.charset.StandardCharsets.UTF_8);
                                    int len_14 = reader.expectUInt32();
                                    String val_12 = new String(reader.expectBytes(len_14), java.nio.charset.StandardCharsets.UTF_8);
                                    mAnnotations.put(key_11, val_12);
                                }
                            } else {
                                throw new net.morimekta.providence.serializer.SerializerException(
                                        "Wrong key type " + net.morimekta.providence.serializer.rw.BinaryType.asString(t_8) +
                                        " or value type " + net.morimekta.providence.serializer.rw.BinaryType.asString(t_9) +
                                        " for model.MessageType.annotations, should be string(11) and string(11)");
                            }
                            optionals.set(4);
                        } else {
                            throw new net.morimekta.providence.serializer.SerializerException("Wrong type " + net.morimekta.providence.serializer.rw.BinaryType.asString(type) + " for model.MessageType.annotations, should be struct(12)");
                        }
                        break;
                    }
                    default: {
                        net.morimekta.providence.serializer.rw.BinaryFormatUtils.readFieldValue(reader, new net.morimekta.providence.serializer.rw.BinaryFormatUtils.FieldInfo(field, type), null, false);
                        break;
                    }
                }
                type = reader.expectByte();
            }
        }

        @Override
        public MessageType build() {
            return new MessageType(this);
        }
    }
}
