package net.morimekta.providence.model;

/**
 * &lt;name&gt; (= &lt;value&gt;)
 */
@SuppressWarnings("unused")
@javax.annotation.Generated("providence-maven-plugin")
@javax.annotation.concurrent.Immutable
public class EnumValue
        implements net.morimekta.providence.PMessage<EnumValue,EnumValue._Field>,
                   Comparable<EnumValue>,
                   java.io.Serializable,
                   net.morimekta.providence.serializer.binary.BinaryWriter {
    private final static long serialVersionUID = 5378083387686562907L;

    private final static String kDefaultName = "";
    private final static int kDefaultId = 0;

    private final transient String mDocumentation;
    private final transient String mName;
    private final transient Integer mId;
    private final transient java.util.Map<String,String> mAnnotations;
    private final transient net.morimekta.providence.model.FilePos mStartPos;
    private final transient net.morimekta.providence.model.FilePos mEndPos;

    private volatile transient int tHashCode;

    // Transient object used during java deserialization.
    private transient EnumValue tSerializeInstance;

    private EnumValue(_Builder builder) {
        mDocumentation = builder.mDocumentation;
        if (builder.isSetName()) {
            mName = builder.mName;
        } else {
            mName = kDefaultName;
        }
        mId = builder.mId;
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
     * @return The field value
     */
    public String getDocumentation() {
        return mDocumentation;
    }

    /**
     * @return Optional field value
     */
    @javax.annotation.Nonnull
    public java.util.Optional<String> optionalDocumentation() {
        return java.util.Optional.ofNullable(mDocumentation);
    }

    public boolean hasName() {
        return true;
    }

    /**
     * @return The field value
     */
    @javax.annotation.Nonnull
    public String getName() {
        return mName;
    }

    public boolean hasId() {
        return mId != null;
    }

    /**
     * @return The field value
     */
    public int getId() {
        return hasId() ? mId : kDefaultId;
    }

    /**
     * @return Optional field value
     */
    @javax.annotation.Nonnull
    public java.util.OptionalInt optionalId() {
        return hasId() ? java.util.OptionalInt.of(mId) : java.util.OptionalInt.empty();
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

    /**
     * @return Optional field value
     */
    @javax.annotation.Nonnull
    public java.util.Optional<java.util.Map<String,String>> optionalAnnotations() {
        return java.util.Optional.ofNullable(mAnnotations);
    }

    public boolean hasStartPos() {
        return mStartPos != null;
    }

    /**
     * The start of the definition (position of &#39;name&#39;)
     *
     * @return The field value
     */
    public net.morimekta.providence.model.FilePos getStartPos() {
        return mStartPos;
    }

    /**
     * The start of the definition (position of &#39;name&#39;)
     *
     * @return Optional field value
     */
    @javax.annotation.Nonnull
    public java.util.Optional<net.morimekta.providence.model.FilePos> optionalStartPos() {
        return java.util.Optional.ofNullable(mStartPos);
    }

    public boolean hasEndPos() {
        return mEndPos != null;
    }

    /**
     * The end of the definition (position of &#39;;&#39; or last pos of value)
     *
     * @return The field value
     */
    public net.morimekta.providence.model.FilePos getEndPos() {
        return mEndPos;
    }

    /**
     * The end of the definition (position of &#39;;&#39; or last pos of value)
     *
     * @return Optional field value
     */
    @javax.annotation.Nonnull
    public java.util.Optional<net.morimekta.providence.model.FilePos> optionalEndPos() {
        return java.util.Optional.ofNullable(mEndPos);
    }

    @Override
    public boolean has(int key) {
        switch(key) {
            case 1: return mDocumentation != null;
            case 2: return true;
            case 3: return mId != null;
            case 4: return mAnnotations != null;
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
            case 2: return (T) mName;
            case 3: return (T) mId;
            case 4: return (T) mAnnotations;
            case 10: return (T) mStartPos;
            case 11: return (T) mEndPos;
            default: return null;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        if (o == null || !o.getClass().equals(getClass())) return false;
        EnumValue other = (EnumValue) o;
        return java.util.Objects.equals(mDocumentation, other.mDocumentation) &&
               java.util.Objects.equals(mName, other.mName) &&
               java.util.Objects.equals(mId, other.mId) &&
               java.util.Objects.equals(mAnnotations, other.mAnnotations) &&
               java.util.Objects.equals(mStartPos, other.mStartPos) &&
               java.util.Objects.equals(mEndPos, other.mEndPos);
    }

    @Override
    public int hashCode() {
        if (tHashCode == 0) {
            tHashCode = java.util.Objects.hash(
                    EnumValue.class,
                    _Field.DOCUMENTATION, mDocumentation,
                    _Field.NAME, mName,
                    _Field.ID, mId,
                    _Field.ANNOTATIONS, mAnnotations,
                    _Field.START_POS, mStartPos,
                    _Field.END_POS, mEndPos);
        }
        return tHashCode;
    }

    @Override
    public String toString() {
        return "pmodel.EnumValue" + asString();
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
        if (!first) out.append(',');
        out.append("name:")
           .append('\"')
           .append(net.morimekta.util.Strings.escape(mName))
           .append('\"');
        if (hasId()) {
            out.append(',');
            out.append("id:")
               .append(mId);
        }
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
    public int compareTo(EnumValue other) {
        int c;

        c = Boolean.compare(mDocumentation != null, other.mDocumentation != null);
        if (c != 0) return c;
        if (mDocumentation != null) {
            c = mDocumentation.compareTo(other.mDocumentation);
            if (c != 0) return c;
        }

        c = mName.compareTo(other.mName);
        if (c != 0) return c;

        c = Boolean.compare(mId != null, other.mId != null);
        if (c != 0) return c;
        if (mId != null) {
            c = Integer.compare(mId, other.mId);
            if (c != 0) return c;
        }

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

        length += writer.writeByte((byte) 11);
        length += writer.writeShort((short) 2);
        net.morimekta.util.Binary tmp_2 = net.morimekta.util.Binary.wrap(mName.getBytes(java.nio.charset.StandardCharsets.UTF_8));
        length += writer.writeUInt32(tmp_2.length());
        length += writer.writeBinary(tmp_2);

        if (hasId()) {
            length += writer.writeByte((byte) 8);
            length += writer.writeShort((short) 3);
            length += writer.writeInt(mId);
        }

        if (hasAnnotations()) {
            length += writer.writeByte((byte) 13);
            length += writer.writeShort((short) 4);
            length += writer.writeByte((byte) 11);
            length += writer.writeByte((byte) 11);
            length += writer.writeUInt32(mAnnotations.size());
            for (java.util.Map.Entry<String,String> entry_3 : mAnnotations.entrySet()) {
                net.morimekta.util.Binary tmp_4 = net.morimekta.util.Binary.wrap(entry_3.getKey().getBytes(java.nio.charset.StandardCharsets.UTF_8));
                length += writer.writeUInt32(tmp_4.length());
                length += writer.writeBinary(tmp_4);
                net.morimekta.util.Binary tmp_5 = net.morimekta.util.Binary.wrap(entry_3.getValue().getBytes(java.nio.charset.StandardCharsets.UTF_8));
                length += writer.writeUInt32(tmp_5.length());
                length += writer.writeBinary(tmp_5);
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
        NAME(2, net.morimekta.providence.descriptor.PRequirement.REQUIRED, "name", net.morimekta.providence.descriptor.PPrimitive.STRING.provider(), null),
        ID(3, net.morimekta.providence.descriptor.PRequirement.OPTIONAL, "id", net.morimekta.providence.descriptor.PPrimitive.I32.provider(), null),
        ANNOTATIONS(4, net.morimekta.providence.descriptor.PRequirement.OPTIONAL, "annotations", net.morimekta.providence.descriptor.PMap.sortedProvider(net.morimekta.providence.descriptor.PPrimitive.STRING.provider(),net.morimekta.providence.descriptor.PPrimitive.STRING.provider()), null),
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
                case 2: return _Field.NAME;
                case 3: return _Field.ID;
                case 4: return _Field.ANNOTATIONS;
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
                case "name": return _Field.NAME;
                case "id": return _Field.ID;
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
                throw new IllegalArgumentException("No such field id " + id + " in pmodel.EnumValue");
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
                throw new IllegalArgumentException("No such field \"" + name + "\" in pmodel.EnumValue");
            }
            return field;
        }

    }

    @javax.annotation.Nonnull
    public static net.morimekta.providence.descriptor.PStructDescriptorProvider<EnumValue,_Field> provider() {
        return new _Provider();
    }

    @Override
    @javax.annotation.Nonnull
    public net.morimekta.providence.descriptor.PStructDescriptor<EnumValue,_Field> descriptor() {
        return kDescriptor;
    }

    public static final net.morimekta.providence.descriptor.PStructDescriptor<EnumValue,_Field> kDescriptor;

    private static class _Descriptor
            extends net.morimekta.providence.descriptor.PStructDescriptor<EnumValue,_Field> {
        public _Descriptor() {
            super("pmodel", "EnumValue", _Builder::new, false);
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

    private final static class _Provider extends net.morimekta.providence.descriptor.PStructDescriptorProvider<EnumValue,_Field> {
        @Override
        public net.morimekta.providence.descriptor.PStructDescriptor<EnumValue,_Field> descriptor() {
            return kDescriptor;
        }
    }

    /**
     * Make a pmodel.EnumValue builder.
     * @return The builder instance.
     */
    public static _Builder builder() {
        return new _Builder();
    }

    /**
     * &lt;name&gt; (= &lt;value&gt;)
     */
    public static class _Builder
            extends net.morimekta.providence.PMessageBuilder<EnumValue,_Field>
            implements net.morimekta.providence.serializer.binary.BinaryReader {
        private java.util.BitSet optionals;
        private java.util.BitSet modified;

        private String mDocumentation;
        private String mName;
        private Integer mId;
        private java.util.Map<String,String> mAnnotations;
        private net.morimekta.providence.model.FilePos mStartPos;
        private net.morimekta.providence.model.FilePos._Builder mStartPos_builder;
        private net.morimekta.providence.model.FilePos mEndPos;
        private net.morimekta.providence.model.FilePos._Builder mEndPos_builder;

        /**
         * Make a pmodel.EnumValue builder.
         */
        public _Builder() {
            optionals = new java.util.BitSet(6);
            modified = new java.util.BitSet(6);
            mName = kDefaultName;
        }

        /**
         * Make a mutating builder off a base pmodel.EnumValue.
         *
         * @param base The base EnumValue
         */
        public _Builder(EnumValue base) {
            this();

            if (base.hasDocumentation()) {
                optionals.set(0);
                mDocumentation = base.mDocumentation;
            }
            optionals.set(1);
            mName = base.mName;
            if (base.hasId()) {
                optionals.set(2);
                mId = base.mId;
            }
            if (base.hasAnnotations()) {
                optionals.set(3);
                mAnnotations = base.mAnnotations;
            }
            if (base.hasStartPos()) {
                optionals.set(4);
                mStartPos = base.mStartPos;
            }
            if (base.hasEndPos()) {
                optionals.set(5);
                mEndPos = base.mEndPos;
            }
        }

        @javax.annotation.Nonnull
        @Override
        public _Builder merge(EnumValue from) {
            if (from.hasDocumentation()) {
                optionals.set(0);
                modified.set(0);
                mDocumentation = from.getDocumentation();
            }

            optionals.set(1);
            modified.set(1);
            mName = from.getName();

            if (from.hasId()) {
                optionals.set(2);
                modified.set(2);
                mId = from.getId();
            }

            if (from.hasAnnotations()) {
                optionals.set(3);
                modified.set(3);
                mutableAnnotations().putAll(from.getAnnotations());
            }

            if (from.hasStartPos()) {
                optionals.set(4);
                modified.set(4);
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
                optionals.set(5);
                modified.set(5);
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
         * Sets the value of documentation.
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
         * Sets the value of name.
         *
         * @param value The new value
         * @return The builder
         */
        @javax.annotation.Nonnull
        public _Builder setName(String value) {
            if (value == null) {
                return clearName();
            }

            optionals.set(1);
            modified.set(1);
            mName = value;
            return this;
        }

        /**
         * Checks for presence of the name field.
         *
         * @return True if name has been set.
         */
        public boolean isSetName() {
            return optionals.get(1);
        }

        /**
         * Checks if name has been modified since the _Builder was created.
         *
         * @return True if name has been modified.
         */
        public boolean isModifiedName() {
            return modified.get(1);
        }

        /**
         * Clears the name field.
         *
         * @return The builder
         */
        @javax.annotation.Nonnull
        public _Builder clearName() {
            optionals.clear(1);
            modified.set(1);
            mName = kDefaultName;
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
         * Sets the value of id.
         *
         * @param value The new value
         * @return The builder
         */
        @javax.annotation.Nonnull
        public _Builder setId(int value) {
            optionals.set(2);
            modified.set(2);
            mId = value;
            return this;
        }

        /**
         * Checks for presence of the id field.
         *
         * @return True if id has been set.
         */
        public boolean isSetId() {
            return optionals.get(2);
        }

        /**
         * Checks if id has been modified since the _Builder was created.
         *
         * @return True if id has been modified.
         */
        public boolean isModifiedId() {
            return modified.get(2);
        }

        /**
         * Clears the id field.
         *
         * @return The builder
         */
        @javax.annotation.Nonnull
        public _Builder clearId() {
            optionals.clear(2);
            modified.set(2);
            mId = null;
            return this;
        }

        /**
         * Gets the value of the contained id.
         *
         * @return The field value
         */
        public int getId() {
            return isSetId() ? mId : kDefaultId;
        }

        /**
         * Sets the value of annotations.
         *
         * @param value The new value
         * @return The builder
         */
        @javax.annotation.Nonnull
        public _Builder setAnnotations(java.util.Map<String,String> value) {
            if (value == null) {
                return clearAnnotations();
            }

            optionals.set(3);
            modified.set(3);
            mAnnotations = com.google.common.collect.ImmutableSortedMap.copyOf(value);
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
            optionals.set(3);
            modified.set(3);
            mutableAnnotations().put(key, value);
            return this;
        }

        /**
         * Checks for presence of the annotations field.
         *
         * @return True if annotations has been set.
         */
        public boolean isSetAnnotations() {
            return optionals.get(3);
        }

        /**
         * Checks if annotations has been modified since the _Builder was created.
         *
         * @return True if annotations has been modified.
         */
        public boolean isModifiedAnnotations() {
            return modified.get(3);
        }

        /**
         * Clears the annotations field.
         *
         * @return The builder
         */
        @javax.annotation.Nonnull
        public _Builder clearAnnotations() {
            optionals.clear(3);
            modified.set(3);
            mAnnotations = null;
            return this;
        }

        /**
         * Gets the builder for the contained annotations.
         *
         * @return The field builder
         */
        @javax.annotation.Nonnull
        public java.util.Map<String,String> mutableAnnotations() {
            optionals.set(3);
            modified.set(3);

            if (mAnnotations == null) {
                mAnnotations = new java.util.TreeMap<>();
            } else if (!(mAnnotations instanceof java.util.TreeMap)) {
                mAnnotations = new java.util.TreeMap<>(mAnnotations);
            }
            return mAnnotations;
        }

        /**
         * The start of the definition (position of &#39;name&#39;)
         *
         * @param value The new value
         * @return The builder
         */
        @javax.annotation.Nonnull
        public _Builder setStartPos(net.morimekta.providence.model.FilePos value) {
            if (value == null) {
                return clearStartPos();
            }

            optionals.set(4);
            modified.set(4);
            mStartPos = value;
            mStartPos_builder = null;
            return this;
        }

        /**
         * The start of the definition (position of &#39;name&#39;)
         *
         * @param builder builder for the new value
         * @return The builder
         */
        @javax.annotation.Nonnull
        public _Builder setStartPos(net.morimekta.providence.model.FilePos._Builder builder) {
          return setStartPos(builder == null ? null : builder.build());
        }

        /**
         * The start of the definition (position of &#39;name&#39;)
         *
         * @return True if start_pos has been set.
         */
        public boolean isSetStartPos() {
            return optionals.get(4);
        }

        /**
         * The start of the definition (position of &#39;name&#39;)
         *
         * @return True if start_pos has been modified.
         */
        public boolean isModifiedStartPos() {
            return modified.get(4);
        }

        /**
         * The start of the definition (position of &#39;name&#39;)
         *
         * @return The builder
         */
        @javax.annotation.Nonnull
        public _Builder clearStartPos() {
            optionals.clear(4);
            modified.set(4);
            mStartPos = null;
            mStartPos_builder = null;
            return this;
        }

        /**
         * The start of the definition (position of &#39;name&#39;)
         *
         * @return The field builder
         */
        @javax.annotation.Nonnull
        public net.morimekta.providence.model.FilePos._Builder mutableStartPos() {
            optionals.set(4);
            modified.set(4);

            if (mStartPos != null) {
                mStartPos_builder = mStartPos.mutate();
                mStartPos = null;
            } else if (mStartPos_builder == null) {
                mStartPos_builder = net.morimekta.providence.model.FilePos.builder();
            }
            return mStartPos_builder;
        }

        /**
         * The start of the definition (position of &#39;name&#39;)
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
         * The end of the definition (position of &#39;;&#39; or last pos of value)
         *
         * @param value The new value
         * @return The builder
         */
        @javax.annotation.Nonnull
        public _Builder setEndPos(net.morimekta.providence.model.FilePos value) {
            if (value == null) {
                return clearEndPos();
            }

            optionals.set(5);
            modified.set(5);
            mEndPos = value;
            mEndPos_builder = null;
            return this;
        }

        /**
         * The end of the definition (position of &#39;;&#39; or last pos of value)
         *
         * @param builder builder for the new value
         * @return The builder
         */
        @javax.annotation.Nonnull
        public _Builder setEndPos(net.morimekta.providence.model.FilePos._Builder builder) {
          return setEndPos(builder == null ? null : builder.build());
        }

        /**
         * The end of the definition (position of &#39;;&#39; or last pos of value)
         *
         * @return True if end_pos has been set.
         */
        public boolean isSetEndPos() {
            return optionals.get(5);
        }

        /**
         * The end of the definition (position of &#39;;&#39; or last pos of value)
         *
         * @return True if end_pos has been modified.
         */
        public boolean isModifiedEndPos() {
            return modified.get(5);
        }

        /**
         * The end of the definition (position of &#39;;&#39; or last pos of value)
         *
         * @return The builder
         */
        @javax.annotation.Nonnull
        public _Builder clearEndPos() {
            optionals.clear(5);
            modified.set(5);
            mEndPos = null;
            mEndPos_builder = null;
            return this;
        }

        /**
         * The end of the definition (position of &#39;;&#39; or last pos of value)
         *
         * @return The field builder
         */
        @javax.annotation.Nonnull
        public net.morimekta.providence.model.FilePos._Builder mutableEndPos() {
            optionals.set(5);
            modified.set(5);

            if (mEndPos != null) {
                mEndPos_builder = mEndPos.mutate();
                mEndPos = null;
            } else if (mEndPos_builder == null) {
                mEndPos_builder = net.morimekta.providence.model.FilePos.builder();
            }
            return mEndPos_builder;
        }

        /**
         * The end of the definition (position of &#39;;&#39; or last pos of value)
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
            EnumValue._Builder other = (EnumValue._Builder) o;
            return java.util.Objects.equals(optionals, other.optionals) &&
                   java.util.Objects.equals(mDocumentation, other.mDocumentation) &&
                   java.util.Objects.equals(mName, other.mName) &&
                   java.util.Objects.equals(mId, other.mId) &&
                   java.util.Objects.equals(mAnnotations, other.mAnnotations) &&
                   java.util.Objects.equals(getStartPos(), other.getStartPos()) &&
                   java.util.Objects.equals(getEndPos(), other.getEndPos());
        }

        @Override
        public int hashCode() {
            return java.util.Objects.hash(
                    EnumValue.class, optionals,
                    _Field.DOCUMENTATION, mDocumentation,
                    _Field.NAME, mName,
                    _Field.ID, mId,
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
                case 2: setName((String) value); break;
                case 3: setId((int) value); break;
                case 4: setAnnotations((java.util.Map<String,String>) value); break;
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
                case 10: return optionals.get(4);
                case 11: return optionals.get(5);
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
                case 10: return modified.get(4);
                case 11: return modified.get(5);
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

        @javax.annotation.Nonnull
        @Override
        public _Builder clear(int key) {
            switch (key) {
                case 1: clearDocumentation(); break;
                case 2: clearName(); break;
                case 3: clearId(); break;
                case 4: clearAnnotations(); break;
                case 10: clearStartPos(); break;
                case 11: clearEndPos(); break;
                default: break;
            }
            return this;
        }

        @Override
        public boolean valid() {
            return optionals.get(1);
        }

        @Override
        public void validate() {
            if (!valid()) {
                java.util.ArrayList<String> missing = new java.util.ArrayList<>();

                if (!optionals.get(1)) {
                    missing.add("name");
                }

                throw new java.lang.IllegalStateException(
                        "Missing required fields " +
                        String.join(",", missing) +
                        " in message pmodel.EnumValue");
            }
        }

        @javax.annotation.Nonnull
        @Override
        public net.morimekta.providence.descriptor.PStructDescriptor<EnumValue,_Field> descriptor() {
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
                            throw new net.morimekta.providence.serializer.SerializerException("Wrong type " + net.morimekta.providence.serializer.binary.BinaryType.asString(type) + " for pmodel.EnumValue.documentation, should be struct(12)");
                        }
                        break;
                    }
                    case 2: {
                        if (type == 11) {
                            int len_2 = reader.expectUInt32();
                            mName = new String(reader.expectBytes(len_2), java.nio.charset.StandardCharsets.UTF_8);
                            optionals.set(1);
                        } else {
                            throw new net.morimekta.providence.serializer.SerializerException("Wrong type " + net.morimekta.providence.serializer.binary.BinaryType.asString(type) + " for pmodel.EnumValue.name, should be struct(12)");
                        }
                        break;
                    }
                    case 3: {
                        if (type == 8) {
                            mId = reader.expectInt();
                            optionals.set(2);
                        } else {
                            throw new net.morimekta.providence.serializer.SerializerException("Wrong type " + net.morimekta.providence.serializer.binary.BinaryType.asString(type) + " for pmodel.EnumValue.id, should be struct(12)");
                        }
                        break;
                    }
                    case 4: {
                        if (type == 13) {
                            net.morimekta.providence.descriptor.PMap.SortedBuilder<String,String> b_3 = new net.morimekta.providence.descriptor.PMap.SortedBuilder<>();
                            byte t_5 = reader.expectByte();
                            byte t_6 = reader.expectByte();
                            if (t_5 == 11 && t_6 == 11) {
                                final int len_4 = reader.expectUInt32();
                                for (int i_7 = 0; i_7 < len_4; ++i_7) {
                                    int len_10 = reader.expectUInt32();
                                    String key_8 = new String(reader.expectBytes(len_10), java.nio.charset.StandardCharsets.UTF_8);
                                    int len_11 = reader.expectUInt32();
                                    String val_9 = new String(reader.expectBytes(len_11), java.nio.charset.StandardCharsets.UTF_8);
                                    b_3.put(key_8, val_9);
                                }
                                mAnnotations = b_3.build();
                            } else {
                                throw new net.morimekta.providence.serializer.SerializerException(
                                        "Wrong key type " + net.morimekta.providence.serializer.binary.BinaryType.asString(t_5) +
                                        " or value type " + net.morimekta.providence.serializer.binary.BinaryType.asString(t_6) +
                                        " for pmodel.EnumValue.annotations, should be string(11) and string(11)");
                            }
                            optionals.set(3);
                        } else {
                            throw new net.morimekta.providence.serializer.SerializerException("Wrong type " + net.morimekta.providence.serializer.binary.BinaryType.asString(type) + " for pmodel.EnumValue.annotations, should be struct(12)");
                        }
                        break;
                    }
                    case 10: {
                        if (type == 12) {
                            mStartPos = net.morimekta.providence.serializer.binary.BinaryFormatUtils.readMessage(reader, net.morimekta.providence.model.FilePos.kDescriptor, strict);
                            optionals.set(4);
                        } else {
                            throw new net.morimekta.providence.serializer.SerializerException("Wrong type " + net.morimekta.providence.serializer.binary.BinaryType.asString(type) + " for pmodel.EnumValue.start_pos, should be struct(12)");
                        }
                        break;
                    }
                    case 11: {
                        if (type == 12) {
                            mEndPos = net.morimekta.providence.serializer.binary.BinaryFormatUtils.readMessage(reader, net.morimekta.providence.model.FilePos.kDescriptor, strict);
                            optionals.set(5);
                        } else {
                            throw new net.morimekta.providence.serializer.SerializerException("Wrong type " + net.morimekta.providence.serializer.binary.BinaryType.asString(type) + " for pmodel.EnumValue.end_pos, should be struct(12)");
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
        public EnumValue build() {
            return new EnumValue(this);
        }
    }
}
