package net.morimekta.test.providence.reflect;

@SuppressWarnings("unused")
@javax.annotation.Generated("providence-maven-plugin")
@javax.annotation.concurrent.Immutable
public class CompactFields
        implements net.morimekta.providence.PMessage<CompactFields,CompactFields._Field>,
                   net.morimekta.providence.serializer.json.JsonCompactible,
                   Comparable<CompactFields>,
                   java.io.Serializable,
                   net.morimekta.providence.serializer.binary.BinaryWriter {
    private final static long serialVersionUID = -8473304196623780023L;

    private final static String kDefaultName = "";
    private final static int kDefaultId = 0;

    private final transient String mName;
    private final transient int mId;
    private final transient String mLabel;

    private volatile transient int tHashCode;

    // Transient object used during java deserialization.
    private transient CompactFields tSerializeInstance;

    public CompactFields(String pName,
                         int pId,
                         String pLabel) {
        if (pName != null) {
            mName = pName;
        } else {
            mName = kDefaultName;
        }
        mId = pId;
        mLabel = pLabel;
    }

    private CompactFields(_Builder builder) {
        if (builder.isSetName()) {
            mName = builder.mName;
        } else {
            mName = kDefaultName;
        }
        mId = builder.mId;
        mLabel = builder.mLabel;
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
            case 1: return true;
            case 2: return true;
            case 3: return mLabel != null;
            default: return false;
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T get(int key) {
        switch(key) {
            case 1: return (T) mName;
            case 2: return (T) (Integer) mId;
            case 3: return (T) mLabel;
            default: return null;
        }
    }

    @Override
    public boolean jsonCompact() {
        boolean missing = false;
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
        if (o == null || !o.getClass().equals(getClass())) return false;
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
    @javax.annotation.Nonnull
    public String asString() {
        StringBuilder out = new StringBuilder();
        out.append("{");

        out.append("name:")
           .append('\"')
           .append(net.morimekta.util.Strings.escape(mName))
           .append('\"');
        out.append(',');
        out.append("id:")
           .append(mId);
        if (hasLabel()) {
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

        c = mName.compareTo(other.mName);
        if (c != 0) return c;

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

        length += writer.writeByte((byte) 11);
        length += writer.writeShort((short) 1);
        net.morimekta.util.Binary tmp_1 = net.morimekta.util.Binary.wrap(mName.getBytes(java.nio.charset.StandardCharsets.UTF_8));
        length += writer.writeUInt32(tmp_1.length());
        length += writer.writeBinary(tmp_1);

        length += writer.writeByte((byte) 8);
        length += writer.writeShort((short) 2);
        length += writer.writeInt(mId);

        if (hasLabel()) {
            length += writer.writeByte((byte) 11);
            length += writer.writeShort((short) 3);
            net.morimekta.util.Binary tmp_2 = net.morimekta.util.Binary.wrap(mLabel.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            length += writer.writeUInt32(tmp_2.length());
            length += writer.writeBinary(tmp_2);
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
        NAME(1, net.morimekta.providence.descriptor.PRequirement.REQUIRED, "name", net.morimekta.providence.descriptor.PPrimitive.STRING.provider(), null),
        ID(2, net.morimekta.providence.descriptor.PRequirement.REQUIRED, "id", net.morimekta.providence.descriptor.PPrimitive.I32.provider(), null),
        LABEL(3, net.morimekta.providence.descriptor.PRequirement.OPTIONAL, "label", net.morimekta.providence.descriptor.PPrimitive.STRING.provider(), null),
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
                case 1: return _Field.NAME;
                case 2: return _Field.ID;
                case 3: return _Field.LABEL;
            }
            return null;
        }

        /**
         * @param name Field name
         * @return The named field or null
         */
        public static _Field findByName(String name) {
            switch (name) {
                case "name": return _Field.NAME;
                case "id": return _Field.ID;
                case "label": return _Field.LABEL;
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
                throw new IllegalArgumentException("No such field id " + id + " in providence.CompactFields");
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
                throw new IllegalArgumentException("No such field \"" + name + "\" in providence.CompactFields");
            }
            return field;
        }

    }

    @javax.annotation.Nonnull
    public static net.morimekta.providence.descriptor.PStructDescriptorProvider<CompactFields,_Field> provider() {
        return new _Provider();
    }

    @Override
    @javax.annotation.Nonnull
    public net.morimekta.providence.descriptor.PStructDescriptor<CompactFields,_Field> descriptor() {
        return kDescriptor;
    }

    public static final net.morimekta.providence.descriptor.PStructDescriptor<CompactFields,_Field> kDescriptor;

    private static class _Descriptor
            extends net.morimekta.providence.descriptor.PStructDescriptor<CompactFields,_Field> implements net.morimekta.providence.serializer.json.JsonCompactibleDescriptor {
        public _Descriptor() {
            super("providence", "CompactFields", _Builder::new, true);
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

    private final static class _Provider extends net.morimekta.providence.descriptor.PStructDescriptorProvider<CompactFields,_Field> {
        @Override
        public net.morimekta.providence.descriptor.PStructDescriptor<CompactFields,_Field> descriptor() {
            return kDescriptor;
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
            extends net.morimekta.providence.PMessageBuilder<CompactFields,_Field>
            implements net.morimekta.providence.serializer.binary.BinaryReader {
        private java.util.BitSet optionals;
        private java.util.BitSet modified;

        private String mName;
        private int mId;
        private String mLabel;

        /**
         * Make a providence.CompactFields builder.
         */
        public _Builder() {
            optionals = new java.util.BitSet(3);
            modified = new java.util.BitSet(3);
            mName = kDefaultName;
            mId = kDefaultId;
        }

        /**
         * Make a mutating builder off a base providence.CompactFields.
         *
         * @param base The base CompactFields
         */
        public _Builder(CompactFields base) {
            this();

            optionals.set(0);
            mName = base.mName;
            optionals.set(1);
            mId = base.mId;
            if (base.hasLabel()) {
                optionals.set(2);
                mLabel = base.mLabel;
            }
        }

        @javax.annotation.Nonnull
        @Override
        public _Builder merge(CompactFields from) {
            optionals.set(0);
            modified.set(0);
            mName = from.getName();

            optionals.set(1);
            modified.set(1);
            mId = from.getId();

            if (from.hasLabel()) {
                optionals.set(2);
                modified.set(2);
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
        @javax.annotation.Nonnull
        public _Builder setName(String value) {
            if (value == null) {
                return clearName();
            }

            optionals.set(0);
            modified.set(0);
            mName = value;
            return this;
        }

        /**
         * Checks for presence of the name field.
         *
         * @return True if name has been set.
         */
        public boolean isSetName() {
            return optionals.get(0);
        }

        /**
         * Checks if name has been modified since the _Builder was created.
         *
         * @return True if name has been modified.
         */
        public boolean isModifiedName() {
            return modified.get(0);
        }

        /**
         * Clears the name field.
         *
         * @return The builder
         */
        @javax.annotation.Nonnull
        public _Builder clearName() {
            optionals.clear(0);
            modified.set(0);
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
            optionals.set(1);
            modified.set(1);
            mId = value;
            return this;
        }

        /**
         * Checks for presence of the id field.
         *
         * @return True if id has been set.
         */
        public boolean isSetId() {
            return optionals.get(1);
        }

        /**
         * Checks if id has been modified since the _Builder was created.
         *
         * @return True if id has been modified.
         */
        public boolean isModifiedId() {
            return modified.get(1);
        }

        /**
         * Clears the id field.
         *
         * @return The builder
         */
        @javax.annotation.Nonnull
        public _Builder clearId() {
            optionals.clear(1);
            modified.set(1);
            mId = kDefaultId;
            return this;
        }

        /**
         * Gets the value of the contained id.
         *
         * @return The field value
         */
        public int getId() {
            return mId;
        }

        /**
         * Sets the value of label.
         *
         * @param value The new value
         * @return The builder
         */
        @javax.annotation.Nonnull
        public _Builder setLabel(String value) {
            if (value == null) {
                return clearLabel();
            }

            optionals.set(2);
            modified.set(2);
            mLabel = value;
            return this;
        }

        /**
         * Checks for presence of the label field.
         *
         * @return True if label has been set.
         */
        public boolean isSetLabel() {
            return optionals.get(2);
        }

        /**
         * Checks if label has been modified since the _Builder was created.
         *
         * @return True if label has been modified.
         */
        public boolean isModifiedLabel() {
            return modified.get(2);
        }

        /**
         * Clears the label field.
         *
         * @return The builder
         */
        @javax.annotation.Nonnull
        public _Builder clearLabel() {
            optionals.clear(2);
            modified.set(2);
            mLabel = null;
            return this;
        }

        /**
         * Gets the value of the contained label.
         *
         * @return The field value
         */
        public String getLabel() {
            return mLabel;
        }

        @Override
        public boolean equals(Object o) {
            if (o == this) return true;
            if (o == null || !o.getClass().equals(getClass())) return false;
            CompactFields._Builder other = (CompactFields._Builder) o;
            return java.util.Objects.equals(optionals, other.optionals) &&
                   java.util.Objects.equals(mName, other.mName) &&
                   java.util.Objects.equals(mId, other.mId) &&
                   java.util.Objects.equals(mLabel, other.mLabel);
        }

        @Override
        public int hashCode() {
            return java.util.Objects.hash(
                    CompactFields.class, optionals,
                    _Field.NAME, mName,
                    _Field.ID, mId,
                    _Field.LABEL, mLabel);
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
        public boolean isModified(int key) {
            switch (key) {
                case 1: return modified.get(0);
                case 2: return modified.get(1);
                case 3: return modified.get(2);
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
                case 1: clearName(); break;
                case 2: clearId(); break;
                case 3: clearLabel(); break;
                default: break;
            }
            return this;
        }

        @Override
        public boolean valid() {
            return optionals.get(0) &&
                   optionals.get(1);
        }

        @Override
        public void validate() {
            if (!valid()) {
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

        @javax.annotation.Nonnull
        @Override
        public net.morimekta.providence.descriptor.PStructDescriptor<CompactFields,_Field> descriptor() {
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
                            mName = new String(reader.expectBytes(len_1), java.nio.charset.StandardCharsets.UTF_8);
                            optionals.set(0);
                        } else {
                            throw new net.morimekta.providence.serializer.SerializerException("Wrong type " + net.morimekta.providence.serializer.binary.BinaryType.asString(type) + " for providence.CompactFields.name, should be struct(12)");
                        }
                        break;
                    }
                    case 2: {
                        if (type == 8) {
                            mId = reader.expectInt();
                            optionals.set(1);
                        } else {
                            throw new net.morimekta.providence.serializer.SerializerException("Wrong type " + net.morimekta.providence.serializer.binary.BinaryType.asString(type) + " for providence.CompactFields.id, should be struct(12)");
                        }
                        break;
                    }
                    case 3: {
                        if (type == 11) {
                            int len_2 = reader.expectUInt32();
                            mLabel = new String(reader.expectBytes(len_2), java.nio.charset.StandardCharsets.UTF_8);
                            optionals.set(2);
                        } else {
                            throw new net.morimekta.providence.serializer.SerializerException("Wrong type " + net.morimekta.providence.serializer.binary.BinaryType.asString(type) + " for providence.CompactFields.label, should be struct(12)");
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
        public CompactFields build() {
            return new CompactFields(this);
        }
    }
}
