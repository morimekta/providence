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
@javax.annotation.Generated("providence-maven-plugin")
@javax.annotation.concurrent.Immutable
public class FieldType
        implements net.morimekta.providence.PMessage<FieldType,FieldType._Field>,
                   Comparable<FieldType>,
                   java.io.Serializable,
                   net.morimekta.providence.serializer.binary.BinaryWriter {
    private final static long serialVersionUID = -5885640707344801505L;

    private final static int kDefaultId = 0;
    private final static net.morimekta.providence.model.FieldRequirement kDefaultRequirement = net.morimekta.providence.model.FieldRequirement.DEFAULT;
    private final static String kDefaultType = "";
    private final static String kDefaultName = "";
    private final static int kDefaultStartLineNo = 0;
    private final static int kDefaultStartLinePos = 0;

    private final transient String mDocumentation;
    private final transient int mId;
    private final transient net.morimekta.providence.model.FieldRequirement mRequirement;
    private final transient String mType;
    private final transient String mName;
    private final transient String mDefaultValue;
    private final transient java.util.Map<String,String> mAnnotations;
    private final transient Integer mStartLineNo;
    private final transient Integer mStartLinePos;

    private volatile transient int tHashCode;

    // Transient object used during java deserialization.
    private transient FieldType tSerializeInstance;

    private FieldType(_Builder builder) {
        mDocumentation = builder.mDocumentation;
        mId = builder.mId;
        mRequirement = builder.mRequirement;
        if (builder.isSetType()) {
            mType = builder.mType;
        } else {
            mType = kDefaultType;
        }
        if (builder.isSetName()) {
            mName = builder.mName;
        } else {
            mName = kDefaultName;
        }
        mDefaultValue = builder.mDefaultValue;
        if (builder.isSetAnnotations()) {
            mAnnotations = com.google.common.collect.ImmutableSortedMap.copyOf(builder.mAnnotations);
        } else {
            mAnnotations = null;
        }
        mStartLineNo = builder.mStartLineNo;
        mStartLinePos = builder.mStartLinePos;
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

    public boolean hasId() {
        return true;
    }

    /**
     * @return The field value
     */
    public int getId() {
        return mId;
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
        return true;
    }

    /**
     * @return The field value
     */
    @javax.annotation.Nonnull
    public String getType() {
        return mType;
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

    public boolean hasStartLineNo() {
        return mStartLineNo != null;
    }

    /**
     * Note the start of the default value in the parsed thrift file, this
     * can be used for making more accurate exception / parse data from the
     * const parser.
     *
     * @return The field value
     */
    public int getStartLineNo() {
        return hasStartLineNo() ? mStartLineNo : kDefaultStartLineNo;
    }

    public boolean hasStartLinePos() {
        return mStartLinePos != null;
    }

    /**
     * @return The field value
     */
    public int getStartLinePos() {
        return hasStartLinePos() ? mStartLinePos : kDefaultStartLinePos;
    }

    @Override
    public boolean has(int key) {
        switch(key) {
            case 1: return hasDocumentation();
            case 2: return true;
            case 3: return hasRequirement();
            case 4: return true;
            case 5: return true;
            case 6: return hasDefaultValue();
            case 7: return hasAnnotations();
            case 10: return hasStartLineNo();
            case 11: return hasStartLinePos();
            default: return false;
        }
    }

    @Override
    public int num(int key) {
        switch(key) {
            case 1: return hasDocumentation() ? 1 : 0;
            case 2: return 1;
            case 3: return hasRequirement() ? 1 : 0;
            case 4: return 1;
            case 5: return 1;
            case 6: return hasDefaultValue() ? 1 : 0;
            case 7: return numAnnotations();
            case 10: return hasStartLineNo() ? 1 : 0;
            case 11: return hasStartLinePos() ? 1 : 0;
            default: return 0;
        }
    }

    @Override
    public Object get(int key) {
        switch(key) {
            case 1: return getDocumentation();
            case 2: return getId();
            case 3: return getRequirement();
            case 4: return getType();
            case 5: return getName();
            case 6: return getDefaultValue();
            case 7: return getAnnotations();
            case 10: return getStartLineNo();
            case 11: return getStartLinePos();
            default: return null;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        if (o == null || !o.getClass().equals(getClass())) return false;
        FieldType other = (FieldType) o;
        return java.util.Objects.equals(mDocumentation, other.mDocumentation) &&
               java.util.Objects.equals(mId, other.mId) &&
               java.util.Objects.equals(mRequirement, other.mRequirement) &&
               java.util.Objects.equals(mType, other.mType) &&
               java.util.Objects.equals(mName, other.mName) &&
               java.util.Objects.equals(mDefaultValue, other.mDefaultValue) &&
               java.util.Objects.equals(mAnnotations, other.mAnnotations) &&
               java.util.Objects.equals(mStartLineNo, other.mStartLineNo) &&
               java.util.Objects.equals(mStartLinePos, other.mStartLinePos);
    }

    @Override
    public int hashCode() {
        if (tHashCode == 0) {
            tHashCode = java.util.Objects.hash(
                    FieldType.class,
                    _Field.DOCUMENTATION, mDocumentation,
                    _Field.ID, mId,
                    _Field.REQUIREMENT, mRequirement,
                    _Field.TYPE, mType,
                    _Field.NAME, mName,
                    _Field.DEFAULT_VALUE, mDefaultValue,
                    _Field.ANNOTATIONS, mAnnotations,
                    _Field.START_LINE_NO, mStartLineNo,
                    _Field.START_LINE_POS, mStartLinePos);
        }
        return tHashCode;
    }

    @Override
    public String toString() {
        return "model.FieldType" + asString();
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
        out.append("id:")
           .append(mId);
        if (hasRequirement()) {
            out.append(',');
            out.append("requirement:")
               .append(mRequirement.asString());
        }
        out.append(',');
        out.append("type:")
           .append('\"')
           .append(net.morimekta.util.Strings.escape(mType))
           .append('\"');
        out.append(',');
        out.append("name:")
           .append('\"')
           .append(net.morimekta.util.Strings.escape(mName))
           .append('\"');
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
        if (hasStartLineNo()) {
            out.append(',');
            out.append("start_line_no:")
               .append(mStartLineNo);
        }
        if (hasStartLinePos()) {
            out.append(',');
            out.append("start_line_pos:")
               .append(mStartLinePos);
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

        c = Integer.compare(mId, other.mId);
        if (c != 0) return c;

        c = Boolean.compare(mRequirement != null, other.mRequirement != null);
        if (c != 0) return c;
        if (mRequirement != null) {
            c = Integer.compare(mRequirement.ordinal(), mRequirement.ordinal());
            if (c != 0) return c;
        }

        c = mType.compareTo(other.mType);
        if (c != 0) return c;

        c = mName.compareTo(other.mName);
        if (c != 0) return c;

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

        c = Boolean.compare(mStartLineNo != null, other.mStartLineNo != null);
        if (c != 0) return c;
        if (mStartLineNo != null) {
            c = Integer.compare(mStartLineNo, other.mStartLineNo);
            if (c != 0) return c;
        }

        c = Boolean.compare(mStartLinePos != null, other.mStartLinePos != null);
        if (c != 0) return c;
        if (mStartLinePos != null) {
            c = Integer.compare(mStartLinePos, other.mStartLinePos);
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

        length += writer.writeByte((byte) 8);
        length += writer.writeShort((short) 2);
        length += writer.writeInt(mId);

        if (hasRequirement()) {
            length += writer.writeByte((byte) 8);
            length += writer.writeShort((short) 3);
            length += writer.writeInt(mRequirement.asInteger());
        }

        length += writer.writeByte((byte) 11);
        length += writer.writeShort((short) 4);
        net.morimekta.util.Binary tmp_2 = net.morimekta.util.Binary.wrap(mType.getBytes(java.nio.charset.StandardCharsets.UTF_8));
        length += writer.writeUInt32(tmp_2.length());
        length += writer.writeBinary(tmp_2);

        length += writer.writeByte((byte) 11);
        length += writer.writeShort((short) 5);
        net.morimekta.util.Binary tmp_3 = net.morimekta.util.Binary.wrap(mName.getBytes(java.nio.charset.StandardCharsets.UTF_8));
        length += writer.writeUInt32(tmp_3.length());
        length += writer.writeBinary(tmp_3);

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

        if (hasStartLineNo()) {
            length += writer.writeByte((byte) 8);
            length += writer.writeShort((short) 10);
            length += writer.writeInt(mStartLineNo);
        }

        if (hasStartLinePos()) {
            length += writer.writeByte((byte) 8);
            length += writer.writeShort((short) 11);
            length += writer.writeInt(mStartLinePos);
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
        ID(2, net.morimekta.providence.descriptor.PRequirement.REQUIRED, "id", net.morimekta.providence.descriptor.PPrimitive.I32.provider(), null),
        REQUIREMENT(3, net.morimekta.providence.descriptor.PRequirement.OPTIONAL, "requirement", net.morimekta.providence.model.FieldRequirement.provider(), new net.morimekta.providence.descriptor.PDefaultValueProvider<>(kDefaultRequirement)),
        TYPE(4, net.morimekta.providence.descriptor.PRequirement.REQUIRED, "type", net.morimekta.providence.descriptor.PPrimitive.STRING.provider(), null),
        NAME(5, net.morimekta.providence.descriptor.PRequirement.REQUIRED, "name", net.morimekta.providence.descriptor.PPrimitive.STRING.provider(), null),
        DEFAULT_VALUE(6, net.morimekta.providence.descriptor.PRequirement.OPTIONAL, "default_value", net.morimekta.providence.descriptor.PPrimitive.STRING.provider(), null),
        ANNOTATIONS(7, net.morimekta.providence.descriptor.PRequirement.OPTIONAL, "annotations", net.morimekta.providence.descriptor.PMap.sortedProvider(net.morimekta.providence.descriptor.PPrimitive.STRING.provider(),net.morimekta.providence.descriptor.PPrimitive.STRING.provider()), null),
        START_LINE_NO(10, net.morimekta.providence.descriptor.PRequirement.OPTIONAL, "start_line_no", net.morimekta.providence.descriptor.PPrimitive.I32.provider(), null),
        START_LINE_POS(11, net.morimekta.providence.descriptor.PRequirement.OPTIONAL, "start_line_pos", net.morimekta.providence.descriptor.PPrimitive.I32.provider(), null),
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
                case 2: return _Field.ID;
                case 3: return _Field.REQUIREMENT;
                case 4: return _Field.TYPE;
                case 5: return _Field.NAME;
                case 6: return _Field.DEFAULT_VALUE;
                case 7: return _Field.ANNOTATIONS;
                case 10: return _Field.START_LINE_NO;
                case 11: return _Field.START_LINE_POS;
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
                case "id": return _Field.ID;
                case "requirement": return _Field.REQUIREMENT;
                case "type": return _Field.TYPE;
                case "name": return _Field.NAME;
                case "default_value": return _Field.DEFAULT_VALUE;
                case "annotations": return _Field.ANNOTATIONS;
                case "start_line_no": return _Field.START_LINE_NO;
                case "start_line_pos": return _Field.START_LINE_POS;
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
                throw new IllegalArgumentException("No such field id " + id + " in model.FieldType");
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
                throw new IllegalArgumentException("No such field \"" + name + "\" in model.FieldType");
            }
            return field;
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
            super("model", "FieldType", _Builder::new, false);
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

    private final static class _Provider extends net.morimekta.providence.descriptor.PStructDescriptorProvider<FieldType,_Field> {
        @Override
        public net.morimekta.providence.descriptor.PStructDescriptor<FieldType,_Field> descriptor() {
            return kDescriptor;
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
            implements net.morimekta.providence.serializer.binary.BinaryReader {
        private java.util.BitSet optionals;
        private java.util.BitSet modified;

        private String mDocumentation;
        private int mId;
        private net.morimekta.providence.model.FieldRequirement mRequirement;
        private String mType;
        private String mName;
        private String mDefaultValue;
        private java.util.Map<String,String> mAnnotations;
        private Integer mStartLineNo;
        private Integer mStartLinePos;

        /**
         * Make a model.FieldType builder.
         */
        public _Builder() {
            optionals = new java.util.BitSet(9);
            modified = new java.util.BitSet(9);
            mId = kDefaultId;
            mType = kDefaultType;
            mName = kDefaultName;
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
            mId = base.mId;
            if (base.hasRequirement()) {
                optionals.set(2);
                mRequirement = base.mRequirement;
            }
            optionals.set(3);
            mType = base.mType;
            optionals.set(4);
            mName = base.mName;
            if (base.hasDefaultValue()) {
                optionals.set(5);
                mDefaultValue = base.mDefaultValue;
            }
            if (base.hasAnnotations()) {
                optionals.set(6);
                mAnnotations = base.mAnnotations;
            }
            if (base.hasStartLineNo()) {
                optionals.set(7);
                mStartLineNo = base.mStartLineNo;
            }
            if (base.hasStartLinePos()) {
                optionals.set(8);
                mStartLinePos = base.mStartLinePos;
            }
        }

        @javax.annotation.Nonnull
        @Override
        public _Builder merge(FieldType from) {
            if (from.hasDocumentation()) {
                optionals.set(0);
                modified.set(0);
                mDocumentation = from.getDocumentation();
            }

            optionals.set(1);
            modified.set(1);
            mId = from.getId();

            if (from.hasRequirement()) {
                optionals.set(2);
                modified.set(2);
                mRequirement = from.getRequirement();
            }

            optionals.set(3);
            modified.set(3);
            mType = from.getType();

            optionals.set(4);
            modified.set(4);
            mName = from.getName();

            if (from.hasDefaultValue()) {
                optionals.set(5);
                modified.set(5);
                mDefaultValue = from.getDefaultValue();
            }

            if (from.hasAnnotations()) {
                optionals.set(6);
                modified.set(6);
                mutableAnnotations().putAll(from.getAnnotations());
            }

            if (from.hasStartLineNo()) {
                optionals.set(7);
                modified.set(7);
                mStartLineNo = from.getStartLineNo();
            }

            if (from.hasStartLinePos()) {
                optionals.set(8);
                modified.set(8);
                mStartLinePos = from.getStartLinePos();
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
         * Sets the value of requirement.
         *
         * @param value The new value
         * @return The builder
         */
        @javax.annotation.Nonnull
        public _Builder setRequirement(net.morimekta.providence.model.FieldRequirement value) {
            if (value == null) {
                return clearRequirement();
            }

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
        @javax.annotation.Nonnull
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
        @javax.annotation.Nonnull
        public _Builder setType(String value) {
            if (value == null) {
                return clearType();
            }

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
        @javax.annotation.Nonnull
        public _Builder clearType() {
            optionals.clear(3);
            modified.set(3);
            mType = kDefaultType;
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
        @javax.annotation.Nonnull
        public _Builder setName(String value) {
            if (value == null) {
                return clearName();
            }

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
        @javax.annotation.Nonnull
        public _Builder clearName() {
            optionals.clear(4);
            modified.set(4);
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
         * Sets the value of default_value.
         *
         * @param value The new value
         * @return The builder
         */
        @javax.annotation.Nonnull
        public _Builder setDefaultValue(String value) {
            if (value == null) {
                return clearDefaultValue();
            }

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
        @javax.annotation.Nonnull
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
        @javax.annotation.Nonnull
        public _Builder setAnnotations(java.util.Map<String,String> value) {
            if (value == null) {
                return clearAnnotations();
            }

            optionals.set(6);
            modified.set(6);
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
            optionals.set(6);
            modified.set(6);
            mutableAnnotations().put(key, value);
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
        @javax.annotation.Nonnull
        public _Builder clearAnnotations() {
            optionals.clear(6);
            modified.set(6);
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
            optionals.set(6);
            modified.set(6);

            if (mAnnotations == null) {
                mAnnotations = new java.util.TreeMap<>();
            } else if (!(mAnnotations instanceof java.util.TreeMap)) {
                mAnnotations = new java.util.TreeMap<>(mAnnotations);
            }
            return mAnnotations;
        }

        /**
         * Note the start of the default value in the parsed thrift file, this
         * can be used for making more accurate exception / parse data from the
         * const parser.
         *
         * @param value The new value
         * @return The builder
         */
        @javax.annotation.Nonnull
        public _Builder setStartLineNo(int value) {
            optionals.set(7);
            modified.set(7);
            mStartLineNo = value;
            return this;
        }

        /**
         * Note the start of the default value in the parsed thrift file, this
         * can be used for making more accurate exception / parse data from the
         * const parser.
         *
         * @return True if start_line_no has been set.
         */
        public boolean isSetStartLineNo() {
            return optionals.get(7);
        }

        /**
         * Note the start of the default value in the parsed thrift file, this
         * can be used for making more accurate exception / parse data from the
         * const parser.
         *
         * @return True if start_line_no has been modified.
         */
        public boolean isModifiedStartLineNo() {
            return modified.get(7);
        }

        /**
         * Note the start of the default value in the parsed thrift file, this
         * can be used for making more accurate exception / parse data from the
         * const parser.
         *
         * @return The builder
         */
        @javax.annotation.Nonnull
        public _Builder clearStartLineNo() {
            optionals.clear(7);
            modified.set(7);
            mStartLineNo = null;
            return this;
        }

        /**
         * Note the start of the default value in the parsed thrift file, this
         * can be used for making more accurate exception / parse data from the
         * const parser.
         *
         * @return The field value
         */
        public int getStartLineNo() {
            return isSetStartLineNo() ? mStartLineNo : kDefaultStartLineNo;
        }

        /**
         * Sets the value of start_line_pos.
         *
         * @param value The new value
         * @return The builder
         */
        @javax.annotation.Nonnull
        public _Builder setStartLinePos(int value) {
            optionals.set(8);
            modified.set(8);
            mStartLinePos = value;
            return this;
        }

        /**
         * Checks for presence of the start_line_pos field.
         *
         * @return True if start_line_pos has been set.
         */
        public boolean isSetStartLinePos() {
            return optionals.get(8);
        }

        /**
         * Checks if start_line_pos has been modified since the _Builder was created.
         *
         * @return True if start_line_pos has been modified.
         */
        public boolean isModifiedStartLinePos() {
            return modified.get(8);
        }

        /**
         * Clears the start_line_pos field.
         *
         * @return The builder
         */
        @javax.annotation.Nonnull
        public _Builder clearStartLinePos() {
            optionals.clear(8);
            modified.set(8);
            mStartLinePos = null;
            return this;
        }

        /**
         * Gets the value of the contained start_line_pos.
         *
         * @return The field value
         */
        public int getStartLinePos() {
            return isSetStartLinePos() ? mStartLinePos : kDefaultStartLinePos;
        }

        @Override
        public boolean equals(Object o) {
            if (o == this) return true;
            if (o == null || !o.getClass().equals(getClass())) return false;
            FieldType._Builder other = (FieldType._Builder) o;
            return java.util.Objects.equals(optionals, other.optionals) &&
                   java.util.Objects.equals(mDocumentation, other.mDocumentation) &&
                   java.util.Objects.equals(mId, other.mId) &&
                   java.util.Objects.equals(mRequirement, other.mRequirement) &&
                   java.util.Objects.equals(mType, other.mType) &&
                   java.util.Objects.equals(mName, other.mName) &&
                   java.util.Objects.equals(mDefaultValue, other.mDefaultValue) &&
                   java.util.Objects.equals(mAnnotations, other.mAnnotations) &&
                   java.util.Objects.equals(mStartLineNo, other.mStartLineNo) &&
                   java.util.Objects.equals(mStartLinePos, other.mStartLinePos);
        }

        @Override
        public int hashCode() {
            return java.util.Objects.hash(
                    FieldType.class, optionals,
                    _Field.DOCUMENTATION, mDocumentation,
                    _Field.ID, mId,
                    _Field.REQUIREMENT, mRequirement,
                    _Field.TYPE, mType,
                    _Field.NAME, mName,
                    _Field.DEFAULT_VALUE, mDefaultValue,
                    _Field.ANNOTATIONS, mAnnotations,
                    _Field.START_LINE_NO, mStartLineNo,
                    _Field.START_LINE_POS, mStartLinePos);
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
                case 2: setId((int) value); break;
                case 3: setRequirement((net.morimekta.providence.model.FieldRequirement) value); break;
                case 4: setType((String) value); break;
                case 5: setName((String) value); break;
                case 6: setDefaultValue((String) value); break;
                case 7: setAnnotations((java.util.Map<String,String>) value); break;
                case 10: setStartLineNo((int) value); break;
                case 11: setStartLinePos((int) value); break;
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
                case 10: return optionals.get(7);
                case 11: return optionals.get(8);
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
                case 10: return modified.get(7);
                case 11: return modified.get(8);
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
                case 2: clearId(); break;
                case 3: clearRequirement(); break;
                case 4: clearType(); break;
                case 5: clearName(); break;
                case 6: clearDefaultValue(); break;
                case 7: clearAnnotations(); break;
                case 10: clearStartLineNo(); break;
                case 11: clearStartLinePos(); break;
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
                    missing.add("id");
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

        @javax.annotation.Nonnull
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
                            throw new net.morimekta.providence.serializer.SerializerException("Wrong type " + net.morimekta.providence.serializer.binary.BinaryType.asString(type) + " for model.FieldType.documentation, should be struct(12)");
                        }
                        break;
                    }
                    case 2: {
                        if (type == 8) {
                            mId = reader.expectInt();
                            optionals.set(1);
                        } else {
                            throw new net.morimekta.providence.serializer.SerializerException("Wrong type " + net.morimekta.providence.serializer.binary.BinaryType.asString(type) + " for model.FieldType.id, should be struct(12)");
                        }
                        break;
                    }
                    case 3: {
                        if (type == 8) {
                            mRequirement = net.morimekta.providence.model.FieldRequirement.findById(reader.expectInt());
                            optionals.set(2);
                        } else {
                            throw new net.morimekta.providence.serializer.SerializerException("Wrong type " + net.morimekta.providence.serializer.binary.BinaryType.asString(type) + " for model.FieldType.requirement, should be struct(12)");
                        }
                        break;
                    }
                    case 4: {
                        if (type == 11) {
                            int len_2 = reader.expectUInt32();
                            mType = new String(reader.expectBytes(len_2), java.nio.charset.StandardCharsets.UTF_8);
                            optionals.set(3);
                        } else {
                            throw new net.morimekta.providence.serializer.SerializerException("Wrong type " + net.morimekta.providence.serializer.binary.BinaryType.asString(type) + " for model.FieldType.type, should be struct(12)");
                        }
                        break;
                    }
                    case 5: {
                        if (type == 11) {
                            int len_3 = reader.expectUInt32();
                            mName = new String(reader.expectBytes(len_3), java.nio.charset.StandardCharsets.UTF_8);
                            optionals.set(4);
                        } else {
                            throw new net.morimekta.providence.serializer.SerializerException("Wrong type " + net.morimekta.providence.serializer.binary.BinaryType.asString(type) + " for model.FieldType.name, should be struct(12)");
                        }
                        break;
                    }
                    case 6: {
                        if (type == 11) {
                            int len_4 = reader.expectUInt32();
                            mDefaultValue = new String(reader.expectBytes(len_4), java.nio.charset.StandardCharsets.UTF_8);
                            optionals.set(5);
                        } else {
                            throw new net.morimekta.providence.serializer.SerializerException("Wrong type " + net.morimekta.providence.serializer.binary.BinaryType.asString(type) + " for model.FieldType.default_value, should be struct(12)");
                        }
                        break;
                    }
                    case 7: {
                        if (type == 13) {
                            net.morimekta.providence.descriptor.PMap.SortedBuilder<String,String> b_5 = new net.morimekta.providence.descriptor.PMap.SortedBuilder<>();
                            byte t_7 = reader.expectByte();
                            byte t_8 = reader.expectByte();
                            if (t_7 == 11 && t_8 == 11) {
                                final int len_6 = reader.expectUInt32();
                                for (int i_9 = 0; i_9 < len_6; ++i_9) {
                                    int len_12 = reader.expectUInt32();
                                    String key_10 = new String(reader.expectBytes(len_12), java.nio.charset.StandardCharsets.UTF_8);
                                    int len_13 = reader.expectUInt32();
                                    String val_11 = new String(reader.expectBytes(len_13), java.nio.charset.StandardCharsets.UTF_8);
                                    b_5.put(key_10, val_11);
                                }
                                mAnnotations = b_5.build();
                            } else {
                                throw new net.morimekta.providence.serializer.SerializerException(
                                        "Wrong key type " + net.morimekta.providence.serializer.binary.BinaryType.asString(t_7) +
                                        " or value type " + net.morimekta.providence.serializer.binary.BinaryType.asString(t_8) +
                                        " for model.FieldType.annotations, should be string(11) and string(11)");
                            }
                            optionals.set(6);
                        } else {
                            throw new net.morimekta.providence.serializer.SerializerException("Wrong type " + net.morimekta.providence.serializer.binary.BinaryType.asString(type) + " for model.FieldType.annotations, should be struct(12)");
                        }
                        break;
                    }
                    case 10: {
                        if (type == 8) {
                            mStartLineNo = reader.expectInt();
                            optionals.set(7);
                        } else {
                            throw new net.morimekta.providence.serializer.SerializerException("Wrong type " + net.morimekta.providence.serializer.binary.BinaryType.asString(type) + " for model.FieldType.start_line_no, should be struct(12)");
                        }
                        break;
                    }
                    case 11: {
                        if (type == 8) {
                            mStartLinePos = reader.expectInt();
                            optionals.set(8);
                        } else {
                            throw new net.morimekta.providence.serializer.SerializerException("Wrong type " + net.morimekta.providence.serializer.binary.BinaryType.asString(type) + " for model.FieldType.start_line_pos, should be struct(12)");
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
        public FieldType build() {
            return new FieldType(this);
        }
    }
}
