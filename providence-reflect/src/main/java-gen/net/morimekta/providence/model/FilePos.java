package net.morimekta.providence.model;

/**
 * Describes
 */
@SuppressWarnings("unused")
@javax.annotation.Generated("providence-maven-plugin")
@javax.annotation.concurrent.Immutable
public class FilePos
        implements net.morimekta.providence.PMessage<FilePos,FilePos._Field>,
                   Comparable<FilePos>,
                   java.io.Serializable,
                   net.morimekta.providence.serializer.binary.BinaryWriter {
    private final static long serialVersionUID = -2424032802527346597L;

    private final static int kDefaultLineNo = 0;
    private final static int kDefaultLinePos = 0;

    private final transient int mLineNo;
    private final transient int mLinePos;

    private volatile transient int tHashCode;

    // Transient object used during java deserialization.
    private transient FilePos tSerializeInstance;

    public FilePos(int pLineNo,
                   int pLinePos) {
        mLineNo = pLineNo;
        mLinePos = pLinePos;
    }

    private FilePos(_Builder builder) {
        mLineNo = builder.mLineNo;
        mLinePos = builder.mLinePos;
    }

    public boolean hasLineNo() {
        return true;
    }

    /**
     * The line no in the file. The first line is 1
     *
     * @return The <code>line_no</code> value
     */
    public int getLineNo() {
        return mLineNo;
    }

    public boolean hasLinePos() {
        return true;
    }

    /**
     * The character porisiotn in the line. The first char is 0.
     *
     * @return The <code>line_pos</code> value
     */
    public int getLinePos() {
        return mLinePos;
    }

    @Override
    public boolean has(int key) {
        switch(key) {
            case 1: return true;
            case 2: return true;
            default: return false;
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T get(int key) {
        switch(key) {
            case 1: return (T) (Integer) mLineNo;
            case 2: return (T) (Integer) mLinePos;
            default: return null;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        if (o == null || !o.getClass().equals(getClass())) return false;
        FilePos other = (FilePos) o;
        return java.util.Objects.equals(mLineNo, other.mLineNo) &&
               java.util.Objects.equals(mLinePos, other.mLinePos);
    }

    @Override
    public int hashCode() {
        if (tHashCode == 0) {
            tHashCode = java.util.Objects.hash(
                    FilePos.class,
                    _Field.LINE_NO, mLineNo,
                    _Field.LINE_POS, mLinePos);
        }
        return tHashCode;
    }

    @Override
    public String toString() {
        return "pmodel.FilePos" + asString();
    }

    @Override
    @javax.annotation.Nonnull
    public String asString() {
        StringBuilder out = new StringBuilder();
        out.append("{");

        out.append("line_no:")
           .append(mLineNo);
        out.append(',');
        out.append("line_pos:")
           .append(mLinePos);
        out.append('}');
        return out.toString();
    }

    @Override
    public int compareTo(FilePos other) {
        int c;

        c = Integer.compare(mLineNo, other.mLineNo);
        if (c != 0) return c;

        c = Integer.compare(mLinePos, other.mLinePos);
        if (c != 0) return c;

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

        length += writer.writeByte((byte) 8);
        length += writer.writeShort((short) 1);
        length += writer.writeInt(mLineNo);

        length += writer.writeByte((byte) 8);
        length += writer.writeShort((short) 2);
        length += writer.writeInt(mLinePos);

        length += writer.writeByte((byte) 0);
        return length;
    }

    @javax.annotation.Nonnull
    @Override
    public _Builder mutate() {
        return new _Builder(this);
    }

    public enum _Field implements net.morimekta.providence.descriptor.PField {
        LINE_NO(1, net.morimekta.providence.descriptor.PRequirement.REQUIRED, "line_no", net.morimekta.providence.descriptor.PPrimitive.I32.provider(), null),
        LINE_POS(2, net.morimekta.providence.descriptor.PRequirement.REQUIRED, "line_pos", net.morimekta.providence.descriptor.PPrimitive.I32.provider(), null),
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
                case 1: return _Field.LINE_NO;
                case 2: return _Field.LINE_POS;
            }
            return null;
        }

        /**
         * @param name Field name
         * @return The named field or null
         */
        public static _Field findByName(String name) {
            switch (name) {
                case "line_no": return _Field.LINE_NO;
                case "line_pos": return _Field.LINE_POS;
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
                throw new IllegalArgumentException("No such field id " + id + " in pmodel.FilePos");
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
                throw new IllegalArgumentException("No such field \"" + name + "\" in pmodel.FilePos");
            }
            return field;
        }

    }

    @javax.annotation.Nonnull
    public static net.morimekta.providence.descriptor.PStructDescriptorProvider<FilePos,_Field> provider() {
        return new _Provider();
    }

    @Override
    @javax.annotation.Nonnull
    public net.morimekta.providence.descriptor.PStructDescriptor<FilePos,_Field> descriptor() {
        return kDescriptor;
    }

    public static final net.morimekta.providence.descriptor.PStructDescriptor<FilePos,_Field> kDescriptor;

    private static class _Descriptor
            extends net.morimekta.providence.descriptor.PStructDescriptor<FilePos,_Field> {
        public _Descriptor() {
            super("pmodel", "FilePos", _Builder::new, true);
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

    private final static class _Provider extends net.morimekta.providence.descriptor.PStructDescriptorProvider<FilePos,_Field> {
        @Override
        public net.morimekta.providence.descriptor.PStructDescriptor<FilePos,_Field> descriptor() {
            return kDescriptor;
        }
    }

    /**
     * Make a <code>pmodel.FilePos</code> builder.
     * @return The builder instance.
     */
    public static _Builder builder() {
        return new _Builder();
    }

    /**
     * Describes
     */
    public static class _Builder
            extends net.morimekta.providence.PMessageBuilder<FilePos,_Field>
            implements net.morimekta.providence.serializer.binary.BinaryReader {
        private java.util.BitSet optionals;
        private java.util.BitSet modified;

        private int mLineNo;
        private int mLinePos;

        /**
         * Make a pmodel.FilePos builder instance.
         */
        public _Builder() {
            optionals = new java.util.BitSet(2);
            modified = new java.util.BitSet(2);
            mLineNo = kDefaultLineNo;
            mLinePos = kDefaultLinePos;
        }

        /**
         * Make a mutating builder off a base pmodel.FilePos.
         *
         * @param base The base FilePos
         */
        public _Builder(FilePos base) {
            this();

            optionals.set(0);
            mLineNo = base.mLineNo;
            optionals.set(1);
            mLinePos = base.mLinePos;
        }

        @javax.annotation.Nonnull
        @Override
        public _Builder merge(FilePos from) {
            optionals.set(0);
            modified.set(0);
            mLineNo = from.getLineNo();

            optionals.set(1);
            modified.set(1);
            mLinePos = from.getLinePos();
            return this;
        }

        /**
         * Set the <code>line_no</code> field value.
         * <p>
         * The line no in the file. The first line is 1
         *
         * @param value The new value
         * @return The builder
         */
        @javax.annotation.Nonnull
        public _Builder setLineNo(int value) {
            optionals.set(0);
            modified.set(0);
            mLineNo = value;
            return this;
        }

        /**
         * Checks for presence of the <code>line_no</code> field.
         *
         * @return True if line_no has been set.
         */
        public boolean isSetLineNo() {
            return optionals.get(0);
        }

        /**
         * Checks if the <code>line_no</code> field has been modified since the
         * builder was created.
         *
         * @return True if line_no has been modified.
         */
        public boolean isModifiedLineNo() {
            return modified.get(0);
        }

        /**
         * Clear the <code>line_no</code> field.
         *
         * @return The builder
         */
        @javax.annotation.Nonnull
        public _Builder clearLineNo() {
            optionals.clear(0);
            modified.set(0);
            mLineNo = kDefaultLineNo;
            return this;
        }

        /**
         * The line no in the file. The first line is 1
         *
         * @return The <code>line_no</code> field value
         */
        public int getLineNo() {
            return mLineNo;
        }

        /**
         * Set the <code>line_pos</code> field value.
         * <p>
         * The character porisiotn in the line. The first char is 0.
         *
         * @param value The new value
         * @return The builder
         */
        @javax.annotation.Nonnull
        public _Builder setLinePos(int value) {
            optionals.set(1);
            modified.set(1);
            mLinePos = value;
            return this;
        }

        /**
         * Checks for presence of the <code>line_pos</code> field.
         *
         * @return True if line_pos has been set.
         */
        public boolean isSetLinePos() {
            return optionals.get(1);
        }

        /**
         * Checks if the <code>line_pos</code> field has been modified since the
         * builder was created.
         *
         * @return True if line_pos has been modified.
         */
        public boolean isModifiedLinePos() {
            return modified.get(1);
        }

        /**
         * Clear the <code>line_pos</code> field.
         *
         * @return The builder
         */
        @javax.annotation.Nonnull
        public _Builder clearLinePos() {
            optionals.clear(1);
            modified.set(1);
            mLinePos = kDefaultLinePos;
            return this;
        }

        /**
         * The character porisiotn in the line. The first char is 0.
         *
         * @return The <code>line_pos</code> field value
         */
        public int getLinePos() {
            return mLinePos;
        }

        @Override
        public boolean equals(Object o) {
            if (o == this) return true;
            if (o == null || !o.getClass().equals(getClass())) return false;
            FilePos._Builder other = (FilePos._Builder) o;
            return java.util.Objects.equals(optionals, other.optionals) &&
                   java.util.Objects.equals(mLineNo, other.mLineNo) &&
                   java.util.Objects.equals(mLinePos, other.mLinePos);
        }

        @Override
        public int hashCode() {
            return java.util.Objects.hash(
                    FilePos.class, optionals,
                    _Field.LINE_NO, mLineNo,
                    _Field.LINE_POS, mLinePos);
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
                case 1: setLineNo((int) value); break;
                case 2: setLinePos((int) value); break;
                default: break;
            }
            return this;
        }

        @Override
        public boolean isSet(int key) {
            switch (key) {
                case 1: return optionals.get(0);
                case 2: return optionals.get(1);
                default: break;
            }
            return false;
        }

        @Override
        public boolean isModified(int key) {
            switch (key) {
                case 1: return modified.get(0);
                case 2: return modified.get(1);
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
                case 1: clearLineNo(); break;
                case 2: clearLinePos(); break;
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
                java.util.ArrayList<String> missing = new java.util.ArrayList<>();

                if (!optionals.get(0)) {
                    missing.add("line_no");
                }

                if (!optionals.get(1)) {
                    missing.add("line_pos");
                }

                throw new java.lang.IllegalStateException(
                        "Missing required fields " +
                        String.join(",", missing) +
                        " in message pmodel.FilePos");
            }
        }

        @javax.annotation.Nonnull
        @Override
        public net.morimekta.providence.descriptor.PStructDescriptor<FilePos,_Field> descriptor() {
            return kDescriptor;
        }

        @Override
        public void readBinary(net.morimekta.util.io.BigEndianBinaryReader reader, boolean strict) throws java.io.IOException {
            byte type = reader.expectByte();
            while (type != 0) {
                int field = reader.expectShort();
                switch (field) {
                    case 1: {
                        if (type == 8) {
                            mLineNo = reader.expectInt();
                            optionals.set(0);
                        } else {
                            throw new net.morimekta.providence.serializer.SerializerException("Wrong type " + net.morimekta.providence.serializer.binary.BinaryType.asString(type) + " for pmodel.FilePos.line_no, should be struct(12)");
                        }
                        break;
                    }
                    case 2: {
                        if (type == 8) {
                            mLinePos = reader.expectInt();
                            optionals.set(1);
                        } else {
                            throw new net.morimekta.providence.serializer.SerializerException("Wrong type " + net.morimekta.providence.serializer.binary.BinaryType.asString(type) + " for pmodel.FilePos.line_pos, should be struct(12)");
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
        public FilePos build() {
            return new FilePos(this);
        }
    }
}
