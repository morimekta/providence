package net.morimekta.providence.model;

/**
 * &lt;variant&gt; {
 *   (&lt;field&gt; ([,;])?)*
 * }
 */
@SuppressWarnings("unused")
@javax.annotation.Generated("providence-maven-plugin")
@javax.annotation.concurrent.Immutable
public class MessageType
        implements net.morimekta.providence.PMessage<MessageType,MessageType._Field>,
                   Comparable<MessageType>,
                   java.io.Serializable,
                   net.morimekta.providence.serializer.binary.BinaryWriter {
    private final static long serialVersionUID = -6520335138583998154L;

    private final static net.morimekta.providence.model.MessageVariant kDefaultVariant = net.morimekta.providence.model.MessageVariant.STRUCT;
    private final static String kDefaultName = "";
    private final static java.util.List<net.morimekta.providence.model.FieldType> kDefaultFields = new net.morimekta.providence.descriptor.PList.DefaultBuilder<net.morimekta.providence.model.FieldType>()
                .build();

    private final transient String mDocumentation;
    private final transient net.morimekta.providence.model.MessageVariant mVariant;
    private final transient String mName;
    private final transient java.util.List<net.morimekta.providence.model.FieldType> mFields;
    private final transient java.util.Map<String,String> mAnnotations;
    private final transient net.morimekta.providence.model.FilePos mStartPos;
    private final transient net.morimekta.providence.model.FilePos mEndPos;

    private volatile transient int tHashCode;

    // Transient object used during java deserialization.
    private transient MessageType tSerializeInstance;

    private MessageType(_Builder builder) {
        mDocumentation = builder.mDocumentation;
        mVariant = builder.mVariant;
        if (builder.isSetName()) {
            mName = builder.mName;
        } else {
            mName = kDefaultName;
        }
        if (builder.isSetFields()) {
            mFields = com.google.common.collect.ImmutableList.copyOf(builder.mFields);
        } else {
            mFields = kDefaultFields;
        }
        if (builder.isSetAnnotations()) {
            mAnnotations = com.google.common.collect.ImmutableSortedMap.copyOf(builder.mAnnotations);
        } else {
            mAnnotations = null;
        }
        mStartPos = builder.mStartPos_builder != null ? builder.mStartPos_builder.build() : builder.mStartPos;
        mEndPos = builder.mEndPos_builder != null ? builder.mEndPos_builder.build() : builder.mEndPos;
    }

    public boolean hasDocumentation() {
        return mDocumentation != null;
    }

    /**
     * @return The <code>documentation</code> value
     */
    public String getDocumentation() {
        return mDocumentation;
    }

    /**
     * @return Optional of the <code>documentation</code> field value.
     */
    @javax.annotation.Nonnull
    public java.util.Optional<String> optionalDocumentation() {
        return java.util.Optional.ofNullable(mDocumentation);
    }

    public boolean hasVariant() {
        return mVariant != null;
    }

    /**
     * @return The <code>variant</code> value
     */
    public net.morimekta.providence.model.MessageVariant getVariant() {
        return hasVariant() ? mVariant : kDefaultVariant;
    }

    /**
     * @return Optional of the <code>variant</code> field value.
     */
    @javax.annotation.Nonnull
    public java.util.Optional<net.morimekta.providence.model.MessageVariant> optionalVariant() {
        return java.util.Optional.ofNullable(mVariant);
    }

    public boolean hasName() {
        return true;
    }

    /**
     * @return The <code>name</code> value
     */
    @javax.annotation.Nonnull
    public String getName() {
        return mName;
    }

    public int numFields() {
        return mFields != null ? mFields.size() : 0;
    }

    public boolean hasFields() {
        return true;
    }

    /**
     * @return The <code>fields</code> value
     */
    @javax.annotation.Nonnull
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
     * @return The <code>annotations</code> value
     */
    public java.util.Map<String,String> getAnnotations() {
        return mAnnotations;
    }

    /**
     * @return Optional of the <code>annotations</code> field value.
     */
    @javax.annotation.Nonnull
    public java.util.Optional<java.util.Map<String,String>> optionalAnnotations() {
        return java.util.Optional.ofNullable(mAnnotations);
    }

    public boolean hasStartPos() {
        return mStartPos != null;
    }

    /**
     * The start of the definition (position of &#39;struct&#39; / message type)
     *
     * @return The <code>start_pos</code> value
     */
    public net.morimekta.providence.model.FilePos getStartPos() {
        return mStartPos;
    }

    /**
     * The start of the definition (position of &#39;struct&#39; / message type)
     *
     * @return Optional of the <code>start_pos</code> field value.
     */
    @javax.annotation.Nonnull
    public java.util.Optional<net.morimekta.providence.model.FilePos> optionalStartPos() {
        return java.util.Optional.ofNullable(mStartPos);
    }

    public boolean hasEndPos() {
        return mEndPos != null;
    }

    /**
     * The end of the definition (position of &#39;}&#39;)
     *
     * @return The <code>end_pos</code> value
     */
    public net.morimekta.providence.model.FilePos getEndPos() {
        return mEndPos;
    }

    /**
     * The end of the definition (position of &#39;}&#39;)
     *
     * @return Optional of the <code>end_pos</code> field value.
     */
    @javax.annotation.Nonnull
    public java.util.Optional<net.morimekta.providence.model.FilePos> optionalEndPos() {
        return java.util.Optional.ofNullable(mEndPos);
    }

    @Override
    public boolean has(int key) {
        switch(key) {
            case 1: return mDocumentation != null;
            case 2: return mVariant != null;
            case 3: return true;
            case 4: return true;
            case 5: return mAnnotations != null;
            case 10: return mStartPos != null;
            case 11: return mEndPos != null;
            default: return false;
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T get(int key) {
        switch(key) {
            case 1: return (T) mDocumentation;
            case 2: return (T) mVariant;
            case 3: return (T) mName;
            case 4: return (T) mFields;
            case 5: return (T) mAnnotations;
            case 10: return (T) mStartPos;
            case 11: return (T) mEndPos;
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
               java.util.Objects.equals(mAnnotations, other.mAnnotations) &&
               java.util.Objects.equals(mStartPos, other.mStartPos) &&
               java.util.Objects.equals(mEndPos, other.mEndPos);
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
                    _Field.ANNOTATIONS, mAnnotations,
                    _Field.START_POS, mStartPos,
                    _Field.END_POS, mEndPos);
        }
        return tHashCode;
    }

    @Override
    public String toString() {
        return "pmodel.MessageType" + asString();
    }

    @Override
    @javax.annotation.Nonnull
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
        if (!first) out.append(',');
        out.append("name:")
           .append('\"')
           .append(net.morimekta.util.Strings.escape(mName))
           .append('\"');
        out.append(',');
        out.append("fields:")
           .append(net.morimekta.util.Strings.asString(mFields));
        if (hasAnnotations()) {
            out.append(',');
            out.append("annotations:")
               .append(net.morimekta.util.Strings.asString(mAnnotations));
        }
        if (hasStartPos()) {
            out.append(',');
            out.append("start_pos:")
               .append(mStartPos.asString());
        }
        if (hasEndPos()) {
            out.append(',');
            out.append("end_pos:")
               .append(mEndPos.asString());
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

        c = mName.compareTo(other.mName);
        if (c != 0) return c;

        c = Integer.compare(mFields.hashCode(), other.mFields.hashCode());
        if (c != 0) return c;

        c = Boolean.compare(mAnnotations != null, other.mAnnotations != null);
        if (c != 0) return c;
        if (mAnnotations != null) {
            c = Integer.compare(mAnnotations.hashCode(), other.mAnnotations.hashCode());
            if (c != 0) return c;
        }

        c = Boolean.compare(mStartPos != null, other.mStartPos != null);
        if (c != 0) return c;
        if (mStartPos != null) {
            c = mStartPos.compareTo(other.mStartPos);
            if (c != 0) return c;
        }

        c = Boolean.compare(mEndPos != null, other.mEndPos != null);
        if (c != 0) return c;
        if (mEndPos != null) {
            c = mEndPos.compareTo(other.mEndPos);
            if (c != 0) return c;
        }

        return 0;
    }

    private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException {
        oos.defaultWriteObject();
        net.morimekta.providence.serializer.BinarySerializer serializer = new net.morimekta.providence.serializer.BinarySerializer(false);
        serializer.serialize(oos, this);
    }

    private void readObject(java.io.ObjectInputStream ois)
            throws java.io.IOException, ClassNotFoundException {
        ois.defaultReadObject();
        net.morimekta.providence.serializer.BinarySerializer serializer = new net.morimekta.providence.serializer.BinarySerializer(false);
        tSerializeInstance = serializer.deserialize(ois, kDescriptor);
    }

    private Object readResolve() throws java.io.ObjectStreamException {
        return tSerializeInstance;
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
            length += writer.writeInt(mVariant.asInteger());
        }

        length += writer.writeByte((byte) 11);
        length += writer.writeShort((short) 3);
        net.morimekta.util.Binary tmp_2 = net.morimekta.util.Binary.wrap(mName.getBytes(java.nio.charset.StandardCharsets.UTF_8));
        length += writer.writeUInt32(tmp_2.length());
        length += writer.writeBinary(tmp_2);

        length += writer.writeByte((byte) 15);
        length += writer.writeShort((short) 4);
        length += writer.writeByte((byte) 12);
        length += writer.writeUInt32(mFields.size());
        for (net.morimekta.providence.model.FieldType entry_3 : mFields) {
            length += net.morimekta.providence.serializer.binary.BinaryFormatUtils.writeMessage(writer, entry_3);
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

        if (hasStartPos()) {
            length += writer.writeByte((byte) 12);
            length += writer.writeShort((short) 10);
            length += net.morimekta.providence.serializer.binary.BinaryFormatUtils.writeMessage(writer, mStartPos);
        }

        if (hasEndPos()) {
            length += writer.writeByte((byte) 12);
            length += writer.writeShort((short) 11);
            length += net.morimekta.providence.serializer.binary.BinaryFormatUtils.writeMessage(writer, mEndPos);
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
        DOCUMENTATION(1, net.morimekta.providence.descriptor.PRequirement.OPTIONAL, "documentation", net.morimekta.providence.descriptor.PPrimitive.STRING.provider(), null),
        VARIANT(2, net.morimekta.providence.descriptor.PRequirement.OPTIONAL, "variant", net.morimekta.providence.model.MessageVariant.provider(), new net.morimekta.providence.descriptor.PDefaultValueProvider<>(kDefaultVariant)),
        NAME(3, net.morimekta.providence.descriptor.PRequirement.REQUIRED, "name", net.morimekta.providence.descriptor.PPrimitive.STRING.provider(), null),
        FIELDS(4, net.morimekta.providence.descriptor.PRequirement.DEFAULT, "fields", net.morimekta.providence.descriptor.PList.provider(net.morimekta.providence.model.FieldType.provider()), null),
        ANNOTATIONS(5, net.morimekta.providence.descriptor.PRequirement.OPTIONAL, "annotations", net.morimekta.providence.descriptor.PMap.sortedProvider(net.morimekta.providence.descriptor.PPrimitive.STRING.provider(),net.morimekta.providence.descriptor.PPrimitive.STRING.provider()), null),
        START_POS(10, net.morimekta.providence.descriptor.PRequirement.OPTIONAL, "start_pos", net.morimekta.providence.model.FilePos.provider(), null),
        END_POS(11, net.morimekta.providence.descriptor.PRequirement.OPTIONAL, "end_pos", net.morimekta.providence.model.FilePos.provider(), null),
        ;

        private final int mId;
        private final net.morimekta.providence.descriptor.PRequirement mRequired;
        private final String mName;
        private final net.morimekta.providence.descriptor.PDescriptorProvider mTypeProvider;
        private final net.morimekta.providence.descriptor.PValueProvider<?> mDefaultValue;

        _Field(int id, net.morimekta.providence.descriptor.PRequirement required, String name, net.morimekta.providence.descriptor.PDescriptorProvider typeProvider, net.morimekta.providence.descriptor.PValueProvider<?> defaultValue) {
            mId = id;
            mRequired = required;
            mName = name;
            mTypeProvider = typeProvider;
            mDefaultValue = defaultValue;
        }

        @Override
        public int getId() { return mId; }

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

        /**
         * @param id Field name
         * @return The identified field or null
         */
        public static _Field findById(int id) {
            switch (id) {
                case 1: return _Field.DOCUMENTATION;
                case 2: return _Field.VARIANT;
                case 3: return _Field.NAME;
                case 4: return _Field.FIELDS;
                case 5: return _Field.ANNOTATIONS;
                case 10: return _Field.START_POS;
                case 11: return _Field.END_POS;
            }
            return null;
        }

        /**
         * @param name Field name
         * @return The named field or null
         */
        public static _Field findByName(String name) {
            switch (name) {
                case "documentation": return _Field.DOCUMENTATION;
                case "variant": return _Field.VARIANT;
                case "name": return _Field.NAME;
                case "fields": return _Field.FIELDS;
                case "annotations": return _Field.ANNOTATIONS;
                case "start_pos": return _Field.START_POS;
                case "end_pos": return _Field.END_POS;
            }
            return null;
        }
        /**
         * @param id Field name
         * @return The identified field
         * @throws IllegalArgumentException If no such field
         */
        public static _Field fieldForId(int id) {
            _Field field = findById(id);
            if (field == null) {
                throw new IllegalArgumentException("No such field id " + id + " in pmodel.MessageType");
            }
            return field;
        }

        /**
         * @param name Field name
         * @return The named field
         * @throws IllegalArgumentException If no such field
         */
        public static _Field fieldForName(String name) {
            _Field field = findByName(name);
            if (field == null) {
                throw new IllegalArgumentException("No such field \"" + name + "\" in pmodel.MessageType");
            }
            return field;
        }

    }

    @javax.annotation.Nonnull
    public static net.morimekta.providence.descriptor.PStructDescriptorProvider<MessageType,_Field> provider() {
        return new _Provider();
    }

    @Override
    @javax.annotation.Nonnull
    public net.morimekta.providence.descriptor.PStructDescriptor<MessageType,_Field> descriptor() {
        return kDescriptor;
    }

    public static final net.morimekta.providence.descriptor.PStructDescriptor<MessageType,_Field> kDescriptor;

    private static class _Descriptor
            extends net.morimekta.providence.descriptor.PStructDescriptor<MessageType,_Field> {
        public _Descriptor() {
            super("pmodel", "MessageType", _Builder::new, false);
        }

        @Override
        @javax.annotation.Nonnull
        public _Field[] getFields() {
            return _Field.values();
        }

        @Override
        @javax.annotation.Nullable
        public _Field findFieldByName(String name) {
            return _Field.findByName(name);
        }

        @Override
        @javax.annotation.Nullable
        public _Field findFieldById(int id) {
            return _Field.findById(id);
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

    /**
     * Make a <code>pmodel.MessageType</code> builder.
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
            implements net.morimekta.providence.serializer.binary.BinaryReader {
        private java.util.BitSet optionals;
        private java.util.BitSet modified;

        private String mDocumentation;
        private net.morimekta.providence.model.MessageVariant mVariant;
        private String mName;
        private java.util.List<net.morimekta.providence.model.FieldType> mFields;
        private java.util.Map<String,String> mAnnotations;
        private net.morimekta.providence.model.FilePos mStartPos;
        private net.morimekta.providence.model.FilePos._Builder mStartPos_builder;
        private net.morimekta.providence.model.FilePos mEndPos;
        private net.morimekta.providence.model.FilePos._Builder mEndPos_builder;

        /**
         * Make a pmodel.MessageType builder instance.
         */
        public _Builder() {
            optionals = new java.util.BitSet(7);
            modified = new java.util.BitSet(7);
            mName = kDefaultName;
            mFields = kDefaultFields;
        }

        /**
         * Make a mutating builder off a base pmodel.MessageType.
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
            optionals.set(2);
            mName = base.mName;
            optionals.set(3);
            mFields = base.mFields;
            if (base.hasAnnotations()) {
                optionals.set(4);
                mAnnotations = base.mAnnotations;
            }
            if (base.hasStartPos()) {
                optionals.set(5);
                mStartPos = base.mStartPos;
            }
            if (base.hasEndPos()) {
                optionals.set(6);
                mEndPos = base.mEndPos;
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

            optionals.set(2);
            modified.set(2);
            mName = from.getName();

            optionals.set(3);
            modified.set(3);
            mFields = from.getFields();

            if (from.hasAnnotations()) {
                optionals.set(4);
                modified.set(4);
                mutableAnnotations().putAll(from.getAnnotations());
            }

            if (from.hasStartPos()) {
                optionals.set(5);
                modified.set(5);
                if (mStartPos_builder != null) {
                    mStartPos_builder.merge(from.getStartPos());
                } else if (mStartPos != null) {
                    mStartPos_builder = mStartPos.mutate().merge(from.getStartPos());
                    mStartPos = null;
                } else {
                    mStartPos = from.getStartPos();
                }
            }

            if (from.hasEndPos()) {
                optionals.set(6);
                modified.set(6);
                if (mEndPos_builder != null) {
                    mEndPos_builder.merge(from.getEndPos());
                } else if (mEndPos != null) {
                    mEndPos_builder = mEndPos.mutate().merge(from.getEndPos());
                    mEndPos = null;
                } else {
                    mEndPos = from.getEndPos();
                }
            }
            return this;
        }

        /**
         * Set the <code>documentation</code> field value.
         *
         * @param value The new value
         * @return The builder
         */
        @javax.annotation.Nonnull
        public _Builder setDocumentation(String value) {
            if (value == null) {
                return clearDocumentation();
            }

            optionals.set(0);
            modified.set(0);
            mDocumentation = value;
            return this;
        }

        /**
         * Checks for presence of the <code>documentation</code> field.
         *
         * @return True if documentation has been set.
         */
        public boolean isSetDocumentation() {
            return optionals.get(0);
        }

        /**
         * Checks if the <code>documentation</code> field has been modified since the
         * builder was created.
         *
         * @return True if documentation has been modified.
         */
        public boolean isModifiedDocumentation() {
            return modified.get(0);
        }

        /**
         * Clear the <code>documentation</code> field.
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
         * @return The <code>documentation</code> field value
         */
        public String getDocumentation() {
            return mDocumentation;
        }

        /**
         * Set the <code>variant</code> field value.
         *
         * @param value The new value
         * @return The builder
         */
        @javax.annotation.Nonnull
        public _Builder setVariant(net.morimekta.providence.model.MessageVariant value) {
            if (value == null) {
                return clearVariant();
            }

            optionals.set(1);
            modified.set(1);
            mVariant = value;
            return this;
        }

        /**
         * Checks for presence of the <code>variant</code> field.
         *
         * @return True if variant has been set.
         */
        public boolean isSetVariant() {
            return optionals.get(1);
        }

        /**
         * Checks if the <code>variant</code> field has been modified since the
         * builder was created.
         *
         * @return True if variant has been modified.
         */
        public boolean isModifiedVariant() {
            return modified.get(1);
        }

        /**
         * Clear the <code>variant</code> field.
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
         * @return The <code>variant</code> field value
         */
        public net.morimekta.providence.model.MessageVariant getVariant() {
            return isSetVariant() ? mVariant : kDefaultVariant;
        }

        /**
         * Set the <code>name</code> field value.
         *
         * @param value The new value
         * @return The builder
         */
        @javax.annotation.Nonnull
        public _Builder setName(String value) {
            if (value == null) {
                return clearName();
            }

            optionals.set(2);
            modified.set(2);
            mName = value;
            return this;
        }

        /**
         * Checks for presence of the <code>name</code> field.
         *
         * @return True if name has been set.
         */
        public boolean isSetName() {
            return optionals.get(2);
        }

        /**
         * Checks if the <code>name</code> field has been modified since the
         * builder was created.
         *
         * @return True if name has been modified.
         */
        public boolean isModifiedName() {
            return modified.get(2);
        }

        /**
         * Clear the <code>name</code> field.
         *
         * @return The builder
         */
        @javax.annotation.Nonnull
        public _Builder clearName() {
            optionals.clear(2);
            modified.set(2);
            mName = kDefaultName;
            return this;
        }

        /**
         * @return The <code>name</code> field value
         */
        public String getName() {
            return mName;
        }

        /**
         * Set the <code>fields</code> field value.
         *
         * @param value The new value
         * @return The builder
         */
        @javax.annotation.Nonnull
        public _Builder setFields(java.util.Collection<net.morimekta.providence.model.FieldType> value) {
            if (value == null) {
                return clearFields();
            }

            optionals.set(3);
            modified.set(3);
            mFields = com.google.common.collect.ImmutableList.copyOf(value);
            return this;
        }

        /**
         * Adds entries to the <code>fields</code> list.
         *
         * @param values The added value
         * @return The builder
         */
        @javax.annotation.Nonnull
        public _Builder addToFields(net.morimekta.providence.model.FieldType... values) {
            optionals.set(3);
            modified.set(3);
            java.util.List<net.morimekta.providence.model.FieldType> _container = mutableFields();
            for (net.morimekta.providence.model.FieldType item : values) {
                _container.add(item);
            }
            return this;
        }

        /**
         * Checks for presence of the <code>fields</code> field.
         *
         * @return True if fields has been set.
         */
        public boolean isSetFields() {
            return optionals.get(3);
        }

        /**
         * Checks if the <code>fields</code> field has been modified since the
         * builder was created.
         *
         * @return True if fields has been modified.
         */
        public boolean isModifiedFields() {
            return modified.get(3);
        }

        /**
         * Clear the <code>fields</code> field.
         *
         * @return The builder
         */
        @javax.annotation.Nonnull
        public _Builder clearFields() {
            optionals.clear(3);
            modified.set(3);
            mFields = kDefaultFields;
            return this;
        }

        /**
         * Get the builder for the contained <code>fields</code> message field.
         *
         * @return The field message builder
         */
        @javax.annotation.Nonnull
        /**
         * @return The mutable <code>fields</code> container
         */
        public java.util.List<net.morimekta.providence.model.FieldType> mutableFields() {
            optionals.set(3);
            modified.set(3);

            if (mFields == null) {
                mFields = new java.util.ArrayList<>();
            } else if (!(mFields instanceof java.util.ArrayList)) {
                mFields = new java.util.ArrayList<>(mFields);
            }
            return mFields;
        }

        /**
         * Set the <code>annotations</code> field value.
         *
         * @param value The new value
         * @return The builder
         */
        @javax.annotation.Nonnull
        public _Builder setAnnotations(java.util.Map<String,String> value) {
            if (value == null) {
                return clearAnnotations();
            }

            optionals.set(4);
            modified.set(4);
            mAnnotations = com.google.common.collect.ImmutableSortedMap.copyOf(value);
            return this;
        }

        /**
         * Adds a mapping to the <code>annotations</code> map.
         *
         * @param key The inserted key
         * @param value The inserted value
         * @return The builder
         */
        @javax.annotation.Nonnull
        public _Builder putInAnnotations(String key, String value) {
            optionals.set(4);
            modified.set(4);
            mutableAnnotations().put(key, value);
            return this;
        }

        /**
         * Checks for presence of the <code>annotations</code> field.
         *
         * @return True if annotations has been set.
         */
        public boolean isSetAnnotations() {
            return optionals.get(4);
        }

        /**
         * Checks if the <code>annotations</code> field has been modified since the
         * builder was created.
         *
         * @return True if annotations has been modified.
         */
        public boolean isModifiedAnnotations() {
            return modified.get(4);
        }

        /**
         * Clear the <code>annotations</code> field.
         *
         * @return The builder
         */
        @javax.annotation.Nonnull
        public _Builder clearAnnotations() {
            optionals.clear(4);
            modified.set(4);
            mAnnotations = null;
            return this;
        }

        /**
         * Get the builder for the contained <code>annotations</code> message field.
         *
         * @return The field message builder
         */
        @javax.annotation.Nonnull
        /**
         * @return The mutable <code>annotations</code> container
         */
        public java.util.Map<String,String> mutableAnnotations() {
            optionals.set(4);
            modified.set(4);

            if (mAnnotations == null) {
                mAnnotations = new java.util.TreeMap<>();
            } else if (!(mAnnotations instanceof java.util.TreeMap)) {
                mAnnotations = new java.util.TreeMap<>(mAnnotations);
            }
            return mAnnotations;
        }

        /**
         * Set the <code>start_pos</code> field value.
         * <p>
         * The start of the definition (position of &#39;struct&#39; / message type)
         *
         * @param value The new value
         * @return The builder
         */
        @javax.annotation.Nonnull
        public _Builder setStartPos(net.morimekta.providence.model.FilePos value) {
            if (value == null) {
                return clearStartPos();
            }

            optionals.set(5);
            modified.set(5);
            mStartPos = value;
            mStartPos_builder = null;
            return this;
        }

        /**
         * Set the <code>start_pos</code> field value.
         * <p>
         * The start of the definition (position of &#39;struct&#39; / message type)
         *
         * @param builder builder for the new value
         * @return The builder
         */
        @javax.annotation.Nonnull
        public _Builder setStartPos(net.morimekta.providence.model.FilePos._Builder builder) {
          return setStartPos(builder == null ? null : builder.build());
        }

        /**
         * Checks for presence of the <code>start_pos</code> field.
         *
         * @return True if start_pos has been set.
         */
        public boolean isSetStartPos() {
            return optionals.get(5);
        }

        /**
         * Checks if the <code>start_pos</code> field has been modified since the
         * builder was created.
         *
         * @return True if start_pos has been modified.
         */
        public boolean isModifiedStartPos() {
            return modified.get(5);
        }

        /**
         * Clear the <code>start_pos</code> field.
         *
         * @return The builder
         */
        @javax.annotation.Nonnull
        public _Builder clearStartPos() {
            optionals.clear(5);
            modified.set(5);
            mStartPos = null;
            mStartPos_builder = null;
            return this;
        }

        /**
         * Get the builder for the contained <code>start_pos</code> message field.
         * <p>
         * The start of the definition (position of &#39;struct&#39; / message type)
         *
         * @return The field message builder
         */
        @javax.annotation.Nonnull
        public net.morimekta.providence.model.FilePos._Builder mutableStartPos() {
            optionals.set(5);
            modified.set(5);

            if (mStartPos != null) {
                mStartPos_builder = mStartPos.mutate();
                mStartPos = null;
            } else if (mStartPos_builder == null) {
                mStartPos_builder = net.morimekta.providence.model.FilePos.builder();
            }
            return mStartPos_builder;
        }

        /**
         * The start of the definition (position of &#39;struct&#39; / message type)
         *
         * @return The field value
         */
        public net.morimekta.providence.model.FilePos getStartPos() {

            if (mStartPos_builder != null) {
                return mStartPos_builder.build();
            }
            return mStartPos;
        }

        /**
         * Set the <code>end_pos</code> field value.
         * <p>
         * The end of the definition (position of &#39;}&#39;)
         *
         * @param value The new value
         * @return The builder
         */
        @javax.annotation.Nonnull
        public _Builder setEndPos(net.morimekta.providence.model.FilePos value) {
            if (value == null) {
                return clearEndPos();
            }

            optionals.set(6);
            modified.set(6);
            mEndPos = value;
            mEndPos_builder = null;
            return this;
        }

        /**
         * Set the <code>end_pos</code> field value.
         * <p>
         * The end of the definition (position of &#39;}&#39;)
         *
         * @param builder builder for the new value
         * @return The builder
         */
        @javax.annotation.Nonnull
        public _Builder setEndPos(net.morimekta.providence.model.FilePos._Builder builder) {
          return setEndPos(builder == null ? null : builder.build());
        }

        /**
         * Checks for presence of the <code>end_pos</code> field.
         *
         * @return True if end_pos has been set.
         */
        public boolean isSetEndPos() {
            return optionals.get(6);
        }

        /**
         * Checks if the <code>end_pos</code> field has been modified since the
         * builder was created.
         *
         * @return True if end_pos has been modified.
         */
        public boolean isModifiedEndPos() {
            return modified.get(6);
        }

        /**
         * Clear the <code>end_pos</code> field.
         *
         * @return The builder
         */
        @javax.annotation.Nonnull
        public _Builder clearEndPos() {
            optionals.clear(6);
            modified.set(6);
            mEndPos = null;
            mEndPos_builder = null;
            return this;
        }

        /**
         * Get the builder for the contained <code>end_pos</code> message field.
         * <p>
         * The end of the definition (position of &#39;}&#39;)
         *
         * @return The field message builder
         */
        @javax.annotation.Nonnull
        public net.morimekta.providence.model.FilePos._Builder mutableEndPos() {
            optionals.set(6);
            modified.set(6);

            if (mEndPos != null) {
                mEndPos_builder = mEndPos.mutate();
                mEndPos = null;
            } else if (mEndPos_builder == null) {
                mEndPos_builder = net.morimekta.providence.model.FilePos.builder();
            }
            return mEndPos_builder;
        }

        /**
         * The end of the definition (position of &#39;}&#39;)
         *
         * @return The field value
         */
        public net.morimekta.providence.model.FilePos getEndPos() {

            if (mEndPos_builder != null) {
                return mEndPos_builder.build();
            }
            return mEndPos;
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
                   java.util.Objects.equals(mAnnotations, other.mAnnotations) &&
                   java.util.Objects.equals(getStartPos(), other.getStartPos()) &&
                   java.util.Objects.equals(getEndPos(), other.getEndPos());
        }

        @Override
        public int hashCode() {
            return java.util.Objects.hash(
                    MessageType.class, optionals,
                    _Field.DOCUMENTATION, mDocumentation,
                    _Field.VARIANT, mVariant,
                    _Field.NAME, mName,
                    _Field.FIELDS, mFields,
                    _Field.ANNOTATIONS, mAnnotations,
                    _Field.START_POS, getStartPos(),
                    _Field.END_POS, getEndPos());
        }

        @Override
        @SuppressWarnings("unchecked")
        public net.morimekta.providence.PMessageBuilder mutator(int key) {
            switch (key) {
                case 10: return mutableStartPos();
                case 11: return mutableEndPos();
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
                case 10: setStartPos((net.morimekta.providence.model.FilePos) value); break;
                case 11: setEndPos((net.morimekta.providence.model.FilePos) value); break;
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
                case 10: return optionals.get(5);
                case 11: return optionals.get(6);
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
                case 10: return modified.get(5);
                case 11: return modified.get(6);
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
                case 10: clearStartPos(); break;
                case 11: clearEndPos(); break;
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
                java.util.ArrayList<String> missing = new java.util.ArrayList<>();

                if (!optionals.get(2)) {
                    missing.add("name");
                }

                throw new java.lang.IllegalStateException(
                        "Missing required fields " +
                        String.join(",", missing) +
                        " in message pmodel.MessageType");
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
                            throw new net.morimekta.providence.serializer.SerializerException("Wrong type " + net.morimekta.providence.serializer.binary.BinaryType.asString(type) + " for pmodel.MessageType.documentation, should be struct(12)");
                        }
                        break;
                    }
                    case 2: {
                        if (type == 8) {
                            mVariant = net.morimekta.providence.model.MessageVariant.findById(reader.expectInt());
                            optionals.set(1);
                        } else {
                            throw new net.morimekta.providence.serializer.SerializerException("Wrong type " + net.morimekta.providence.serializer.binary.BinaryType.asString(type) + " for pmodel.MessageType.variant, should be struct(12)");
                        }
                        break;
                    }
                    case 3: {
                        if (type == 11) {
                            int len_2 = reader.expectUInt32();
                            mName = new String(reader.expectBytes(len_2), java.nio.charset.StandardCharsets.UTF_8);
                            optionals.set(2);
                        } else {
                            throw new net.morimekta.providence.serializer.SerializerException("Wrong type " + net.morimekta.providence.serializer.binary.BinaryType.asString(type) + " for pmodel.MessageType.name, should be struct(12)");
                        }
                        break;
                    }
                    case 4: {
                        if (type == 15) {
                            net.morimekta.providence.descriptor.PList.DefaultBuilder<net.morimekta.providence.model.FieldType> b_3 = new net.morimekta.providence.descriptor.PList.DefaultBuilder<>();
                            byte t_5 = reader.expectByte();
                            if (t_5 == 12) {
                                final int len_4 = reader.expectUInt32();
                                for (int i_6 = 0; i_6 < len_4; ++i_6) {
                                    net.morimekta.providence.model.FieldType key_7 = net.morimekta.providence.serializer.binary.BinaryFormatUtils.readMessage(reader, net.morimekta.providence.model.FieldType.kDescriptor, strict);
                                    b_3.add(key_7);
                                }
                                mFields = b_3.build();
                            } else {
                                throw new net.morimekta.providence.serializer.SerializerException("Wrong item type " + net.morimekta.providence.serializer.binary.BinaryType.asString(t_5) + " for pmodel.MessageType.fields, should be struct(12)");
                            }
                            optionals.set(3);
                        } else {
                            throw new net.morimekta.providence.serializer.SerializerException("Wrong type " + net.morimekta.providence.serializer.binary.BinaryType.asString(type) + " for pmodel.MessageType.fields, should be struct(12)");
                        }
                        break;
                    }
                    case 5: {
                        if (type == 13) {
                            net.morimekta.providence.descriptor.PMap.SortedBuilder<String,String> b_8 = new net.morimekta.providence.descriptor.PMap.SortedBuilder<>();
                            byte t_10 = reader.expectByte();
                            byte t_11 = reader.expectByte();
                            if (t_10 == 11 && t_11 == 11) {
                                final int len_9 = reader.expectUInt32();
                                for (int i_12 = 0; i_12 < len_9; ++i_12) {
                                    int len_15 = reader.expectUInt32();
                                    String key_13 = new String(reader.expectBytes(len_15), java.nio.charset.StandardCharsets.UTF_8);
                                    int len_16 = reader.expectUInt32();
                                    String val_14 = new String(reader.expectBytes(len_16), java.nio.charset.StandardCharsets.UTF_8);
                                    b_8.put(key_13, val_14);
                                }
                                mAnnotations = b_8.build();
                            } else {
                                throw new net.morimekta.providence.serializer.SerializerException(
                                        "Wrong key type " + net.morimekta.providence.serializer.binary.BinaryType.asString(t_10) +
                                        " or value type " + net.morimekta.providence.serializer.binary.BinaryType.asString(t_11) +
                                        " for pmodel.MessageType.annotations, should be string(11) and string(11)");
                            }
                            optionals.set(4);
                        } else {
                            throw new net.morimekta.providence.serializer.SerializerException("Wrong type " + net.morimekta.providence.serializer.binary.BinaryType.asString(type) + " for pmodel.MessageType.annotations, should be struct(12)");
                        }
                        break;
                    }
                    case 10: {
                        if (type == 12) {
                            mStartPos = net.morimekta.providence.serializer.binary.BinaryFormatUtils.readMessage(reader, net.morimekta.providence.model.FilePos.kDescriptor, strict);
                            optionals.set(5);
                        } else {
                            throw new net.morimekta.providence.serializer.SerializerException("Wrong type " + net.morimekta.providence.serializer.binary.BinaryType.asString(type) + " for pmodel.MessageType.start_pos, should be struct(12)");
                        }
                        break;
                    }
                    case 11: {
                        if (type == 12) {
                            mEndPos = net.morimekta.providence.serializer.binary.BinaryFormatUtils.readMessage(reader, net.morimekta.providence.model.FilePos.kDescriptor, strict);
                            optionals.set(6);
                        } else {
                            throw new net.morimekta.providence.serializer.SerializerException("Wrong type " + net.morimekta.providence.serializer.binary.BinaryType.asString(type) + " for pmodel.MessageType.end_pos, should be struct(12)");
                        }
                        break;
                    }
                    default: {
                        net.morimekta.providence.serializer.binary.BinaryFormatUtils.readFieldValue(reader, new net.morimekta.providence.serializer.binary.BinaryFormatUtils.FieldInfo(field, type), null, false);
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
