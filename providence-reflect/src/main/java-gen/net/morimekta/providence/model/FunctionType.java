package net.morimekta.providence.model;

/**
 * (oneway)? &lt;return_type&gt; &lt;name&gt;&#39;(&#39;&lt;param&gt;*&#39;)&#39; (throws &#39;(&#39; &lt;exception&gt;+ &#39;)&#39;)?
 */
@SuppressWarnings("unused")
@javax.annotation.Generated("providence-maven-plugin")
@javax.annotation.concurrent.Immutable
public class FunctionType
        implements net.morimekta.providence.PMessage<FunctionType,FunctionType._Field>,
                   Comparable<FunctionType>,
                   java.io.Serializable,
                   net.morimekta.providence.serializer.binary.BinaryWriter {
    private final static long serialVersionUID = 2115290551649370231L;

    private final static boolean kDefaultOneWay = false;
    private final static String kDefaultName = "";
    private final static java.util.List<net.morimekta.providence.model.FieldType> kDefaultParams = new net.morimekta.providence.descriptor.PList.DefaultBuilder<net.morimekta.providence.model.FieldType>()
                .build();
    private final static java.util.List<net.morimekta.providence.model.FieldType> kDefaultExceptions = new net.morimekta.providence.descriptor.PList.DefaultBuilder<net.morimekta.providence.model.FieldType>()
                .build();

    private final transient String mDocumentation;
    private final transient Boolean mOneWay;
    private final transient String mReturnType;
    private final transient String mName;
    private final transient java.util.List<net.morimekta.providence.model.FieldType> mParams;
    private final transient java.util.List<net.morimekta.providence.model.FieldType> mExceptions;
    private final transient java.util.Map<String,String> mAnnotations;
    private final transient net.morimekta.providence.model.FilePos mStartPos;
    private final transient net.morimekta.providence.model.FilePos mEndPos;

    private volatile transient int tHashCode;

    // Transient object used during java deserialization.
    private transient FunctionType tSerializeInstance;

    private FunctionType(_Builder builder) {
        mDocumentation = builder.mDocumentation;
        mOneWay = builder.mOneWay;
        mReturnType = builder.mReturnType;
        if (builder.isSetName()) {
            mName = builder.mName;
        } else {
            mName = kDefaultName;
        }
        if (builder.isSetParams()) {
            mParams = com.google.common.collect.ImmutableList.copyOf(builder.mParams);
        } else {
            mParams = kDefaultParams;
        }
        if (builder.isSetExceptions()) {
            mExceptions = com.google.common.collect.ImmutableList.copyOf(builder.mExceptions);
        } else {
            mExceptions = null;
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

    public boolean hasOneWay() {
        return mOneWay != null;
    }

    /**
     * @return The field value
     */
    public boolean isOneWay() {
        return hasOneWay() ? mOneWay : kDefaultOneWay;
    }

    /**
     * @return Optional field value
     */
    @javax.annotation.Nonnull
    public java.util.Optional<Boolean> optionalOneWay() {
        return java.util.Optional.ofNullable(mOneWay);
    }

    public boolean hasReturnType() {
        return mReturnType != null;
    }

    /**
     * @return The field value
     */
    public String getReturnType() {
        return mReturnType;
    }

    /**
     * @return Optional field value
     */
    @javax.annotation.Nonnull
    public java.util.Optional<String> optionalReturnType() {
        return java.util.Optional.ofNullable(mReturnType);
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

    public int numParams() {
        return mParams != null ? mParams.size() : 0;
    }

    public boolean hasParams() {
        return true;
    }

    /**
     * @return The field value
     */
    @javax.annotation.Nonnull
    public java.util.List<net.morimekta.providence.model.FieldType> getParams() {
        return mParams;
    }

    public int numExceptions() {
        return mExceptions != null ? mExceptions.size() : 0;
    }

    public boolean hasExceptions() {
        return mExceptions != null;
    }

    /**
     * @return The field value
     */
    public java.util.List<net.morimekta.providence.model.FieldType> getExceptions() {
        return hasExceptions() ? mExceptions : kDefaultExceptions;
    }

    /**
     * @return Optional field value
     */
    @javax.annotation.Nonnull
    public java.util.Optional<java.util.List<net.morimekta.providence.model.FieldType>> optionalExceptions() {
        return java.util.Optional.ofNullable(mExceptions);
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
     * The start of the definition (position of return type)
     *
     * @return The field value
     */
    public net.morimekta.providence.model.FilePos getStartPos() {
        return mStartPos;
    }

    /**
     * The start of the definition (position of return type)
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
     * The end of the definition
     *
     * @return The field value
     */
    public net.morimekta.providence.model.FilePos getEndPos() {
        return mEndPos;
    }

    /**
     * The end of the definition
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
            case 2: return mOneWay != null;
            case 3: return mReturnType != null;
            case 4: return true;
            case 5: return true;
            case 6: return mExceptions != null;
            case 7: return mAnnotations != null;
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
            case 2: return (T) mOneWay;
            case 3: return (T) mReturnType;
            case 4: return (T) mName;
            case 5: return (T) mParams;
            case 6: return (T) mExceptions;
            case 7: return (T) mAnnotations;
            case 10: return (T) mStartPos;
            case 11: return (T) mEndPos;
            default: return null;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        if (o == null || !o.getClass().equals(getClass())) return false;
        FunctionType other = (FunctionType) o;
        return java.util.Objects.equals(mDocumentation, other.mDocumentation) &&
               java.util.Objects.equals(mOneWay, other.mOneWay) &&
               java.util.Objects.equals(mReturnType, other.mReturnType) &&
               java.util.Objects.equals(mName, other.mName) &&
               java.util.Objects.equals(mParams, other.mParams) &&
               java.util.Objects.equals(mExceptions, other.mExceptions) &&
               java.util.Objects.equals(mAnnotations, other.mAnnotations) &&
               java.util.Objects.equals(mStartPos, other.mStartPos) &&
               java.util.Objects.equals(mEndPos, other.mEndPos);
    }

    @Override
    public int hashCode() {
        if (tHashCode == 0) {
            tHashCode = java.util.Objects.hash(
                    FunctionType.class,
                    _Field.DOCUMENTATION, mDocumentation,
                    _Field.ONE_WAY, mOneWay,
                    _Field.RETURN_TYPE, mReturnType,
                    _Field.NAME, mName,
                    _Field.PARAMS, mParams,
                    _Field.EXCEPTIONS, mExceptions,
                    _Field.ANNOTATIONS, mAnnotations,
                    _Field.START_POS, mStartPos,
                    _Field.END_POS, mEndPos);
        }
        return tHashCode;
    }

    @Override
    public String toString() {
        return "providence_model.FunctionType" + asString();
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
        if (hasOneWay()) {
            if (first) first = false;
            else out.append(',');
            out.append("one_way:")
               .append(mOneWay);
        }
        if (hasReturnType()) {
            if (first) first = false;
            else out.append(',');
            out.append("return_type:")
               .append('\"')
               .append(net.morimekta.util.Strings.escape(mReturnType))
               .append('\"');
        }
        if (!first) out.append(',');
        out.append("name:")
           .append('\"')
           .append(net.morimekta.util.Strings.escape(mName))
           .append('\"');
        out.append(',');
        out.append("params:")
           .append(net.morimekta.util.Strings.asString(mParams));
        if (hasExceptions()) {
            out.append(',');
            out.append("exceptions:")
               .append(net.morimekta.util.Strings.asString(mExceptions));
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
    public int compareTo(FunctionType other) {
        int c;

        c = Boolean.compare(mDocumentation != null, other.mDocumentation != null);
        if (c != 0) return c;
        if (mDocumentation != null) {
            c = mDocumentation.compareTo(other.mDocumentation);
            if (c != 0) return c;
        }

        c = Boolean.compare(mOneWay != null, other.mOneWay != null);
        if (c != 0) return c;
        if (mOneWay != null) {
            c = Boolean.compare(mOneWay, other.mOneWay);
            if (c != 0) return c;
        }

        c = Boolean.compare(mReturnType != null, other.mReturnType != null);
        if (c != 0) return c;
        if (mReturnType != null) {
            c = mReturnType.compareTo(other.mReturnType);
            if (c != 0) return c;
        }

        c = mName.compareTo(other.mName);
        if (c != 0) return c;

        c = Integer.compare(mParams.hashCode(), other.mParams.hashCode());
        if (c != 0) return c;

        c = Boolean.compare(mExceptions != null, other.mExceptions != null);
        if (c != 0) return c;
        if (mExceptions != null) {
            c = Integer.compare(mExceptions.hashCode(), other.mExceptions.hashCode());
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

        if (hasOneWay()) {
            length += writer.writeByte((byte) 2);
            length += writer.writeShort((short) 2);
            length += writer.writeUInt8(mOneWay ? (byte) 1 : (byte) 0);
        }

        if (hasReturnType()) {
            length += writer.writeByte((byte) 11);
            length += writer.writeShort((short) 3);
            net.morimekta.util.Binary tmp_2 = net.morimekta.util.Binary.wrap(mReturnType.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            length += writer.writeUInt32(tmp_2.length());
            length += writer.writeBinary(tmp_2);
        }

        length += writer.writeByte((byte) 11);
        length += writer.writeShort((short) 4);
        net.morimekta.util.Binary tmp_3 = net.morimekta.util.Binary.wrap(mName.getBytes(java.nio.charset.StandardCharsets.UTF_8));
        length += writer.writeUInt32(tmp_3.length());
        length += writer.writeBinary(tmp_3);

        length += writer.writeByte((byte) 15);
        length += writer.writeShort((short) 5);
        length += writer.writeByte((byte) 12);
        length += writer.writeUInt32(mParams.size());
        for (net.morimekta.providence.model.FieldType entry_4 : mParams) {
            length += net.morimekta.providence.serializer.binary.BinaryFormatUtils.writeMessage(writer, entry_4);
        }

        if (hasExceptions()) {
            length += writer.writeByte((byte) 15);
            length += writer.writeShort((short) 6);
            length += writer.writeByte((byte) 12);
            length += writer.writeUInt32(mExceptions.size());
            for (net.morimekta.providence.model.FieldType entry_5 : mExceptions) {
                length += net.morimekta.providence.serializer.binary.BinaryFormatUtils.writeMessage(writer, entry_5);
            }
        }

        if (hasAnnotations()) {
            length += writer.writeByte((byte) 13);
            length += writer.writeShort((short) 7);
            length += writer.writeByte((byte) 11);
            length += writer.writeByte((byte) 11);
            length += writer.writeUInt32(mAnnotations.size());
            for (java.util.Map.Entry<String,String> entry_6 : mAnnotations.entrySet()) {
                net.morimekta.util.Binary tmp_7 = net.morimekta.util.Binary.wrap(entry_6.getKey().getBytes(java.nio.charset.StandardCharsets.UTF_8));
                length += writer.writeUInt32(tmp_7.length());
                length += writer.writeBinary(tmp_7);
                net.morimekta.util.Binary tmp_8 = net.morimekta.util.Binary.wrap(entry_6.getValue().getBytes(java.nio.charset.StandardCharsets.UTF_8));
                length += writer.writeUInt32(tmp_8.length());
                length += writer.writeBinary(tmp_8);
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
        ONE_WAY(2, net.morimekta.providence.descriptor.PRequirement.OPTIONAL, "one_way", net.morimekta.providence.descriptor.PPrimitive.BOOL.provider(), new net.morimekta.providence.descriptor.PDefaultValueProvider<>(kDefaultOneWay)),
        RETURN_TYPE(3, net.morimekta.providence.descriptor.PRequirement.OPTIONAL, "return_type", net.morimekta.providence.descriptor.PPrimitive.STRING.provider(), null),
        NAME(4, net.morimekta.providence.descriptor.PRequirement.REQUIRED, "name", net.morimekta.providence.descriptor.PPrimitive.STRING.provider(), null),
        PARAMS(5, net.morimekta.providence.descriptor.PRequirement.DEFAULT, "params", net.morimekta.providence.descriptor.PList.provider(net.morimekta.providence.model.FieldType.provider()), new net.morimekta.providence.descriptor.PDefaultValueProvider<>(kDefaultParams)),
        EXCEPTIONS(6, net.morimekta.providence.descriptor.PRequirement.OPTIONAL, "exceptions", net.morimekta.providence.descriptor.PList.provider(net.morimekta.providence.model.FieldType.provider()), new net.morimekta.providence.descriptor.PDefaultValueProvider<>(kDefaultExceptions)),
        ANNOTATIONS(7, net.morimekta.providence.descriptor.PRequirement.OPTIONAL, "annotations", net.morimekta.providence.descriptor.PMap.sortedProvider(net.morimekta.providence.descriptor.PPrimitive.STRING.provider(),net.morimekta.providence.descriptor.PPrimitive.STRING.provider()), null),
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
                case 2: return _Field.ONE_WAY;
                case 3: return _Field.RETURN_TYPE;
                case 4: return _Field.NAME;
                case 5: return _Field.PARAMS;
                case 6: return _Field.EXCEPTIONS;
                case 7: return _Field.ANNOTATIONS;
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
                case "one_way": return _Field.ONE_WAY;
                case "return_type": return _Field.RETURN_TYPE;
                case "name": return _Field.NAME;
                case "params": return _Field.PARAMS;
                case "exceptions": return _Field.EXCEPTIONS;
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
                throw new IllegalArgumentException("No such field id " + id + " in providence_model.FunctionType");
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
                throw new IllegalArgumentException("No such field \"" + name + "\" in providence_model.FunctionType");
            }
            return field;
        }

    }

    @javax.annotation.Nonnull
    public static net.morimekta.providence.descriptor.PStructDescriptorProvider<FunctionType,_Field> provider() {
        return new _Provider();
    }

    @Override
    @javax.annotation.Nonnull
    public net.morimekta.providence.descriptor.PStructDescriptor<FunctionType,_Field> descriptor() {
        return kDescriptor;
    }

    public static final net.morimekta.providence.descriptor.PStructDescriptor<FunctionType,_Field> kDescriptor;

    private static class _Descriptor
            extends net.morimekta.providence.descriptor.PStructDescriptor<FunctionType,_Field> {
        public _Descriptor() {
            super("providence_model", "FunctionType", _Builder::new, false);
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

    private final static class _Provider extends net.morimekta.providence.descriptor.PStructDescriptorProvider<FunctionType,_Field> {
        @Override
        public net.morimekta.providence.descriptor.PStructDescriptor<FunctionType,_Field> descriptor() {
            return kDescriptor;
        }
    }

    /**
     * Make a providence_model.FunctionType builder.
     * @return The builder instance.
     */
    public static _Builder builder() {
        return new _Builder();
    }

    /**
     * (oneway)? &lt;return_type&gt; &lt;name&gt;&#39;(&#39;&lt;param&gt;*&#39;)&#39; (throws &#39;(&#39; &lt;exception&gt;+ &#39;)&#39;)?
     */
    public static class _Builder
            extends net.morimekta.providence.PMessageBuilder<FunctionType,_Field>
            implements net.morimekta.providence.serializer.binary.BinaryReader {
        private java.util.BitSet optionals;
        private java.util.BitSet modified;

        private String mDocumentation;
        private Boolean mOneWay;
        private String mReturnType;
        private String mName;
        private java.util.List<net.morimekta.providence.model.FieldType> mParams;
        private java.util.List<net.morimekta.providence.model.FieldType> mExceptions;
        private java.util.Map<String,String> mAnnotations;
        private net.morimekta.providence.model.FilePos mStartPos;
        private net.morimekta.providence.model.FilePos._Builder mStartPos_builder;
        private net.morimekta.providence.model.FilePos mEndPos;
        private net.morimekta.providence.model.FilePos._Builder mEndPos_builder;

        /**
         * Make a providence_model.FunctionType builder.
         */
        public _Builder() {
            optionals = new java.util.BitSet(9);
            modified = new java.util.BitSet(9);
            mName = kDefaultName;
            mParams = kDefaultParams;
        }

        /**
         * Make a mutating builder off a base providence_model.FunctionType.
         *
         * @param base The base FunctionType
         */
        public _Builder(FunctionType base) {
            this();

            if (base.hasDocumentation()) {
                optionals.set(0);
                mDocumentation = base.mDocumentation;
            }
            if (base.hasOneWay()) {
                optionals.set(1);
                mOneWay = base.mOneWay;
            }
            if (base.hasReturnType()) {
                optionals.set(2);
                mReturnType = base.mReturnType;
            }
            optionals.set(3);
            mName = base.mName;
            optionals.set(4);
            mParams = base.mParams;
            if (base.hasExceptions()) {
                optionals.set(5);
                mExceptions = base.mExceptions;
            }
            if (base.hasAnnotations()) {
                optionals.set(6);
                mAnnotations = base.mAnnotations;
            }
            if (base.hasStartPos()) {
                optionals.set(7);
                mStartPos = base.mStartPos;
            }
            if (base.hasEndPos()) {
                optionals.set(8);
                mEndPos = base.mEndPos;
            }
        }

        @javax.annotation.Nonnull
        @Override
        public _Builder merge(FunctionType from) {
            if (from.hasDocumentation()) {
                optionals.set(0);
                modified.set(0);
                mDocumentation = from.getDocumentation();
            }

            if (from.hasOneWay()) {
                optionals.set(1);
                modified.set(1);
                mOneWay = from.isOneWay();
            }

            if (from.hasReturnType()) {
                optionals.set(2);
                modified.set(2);
                mReturnType = from.getReturnType();
            }

            optionals.set(3);
            modified.set(3);
            mName = from.getName();

            optionals.set(4);
            modified.set(4);
            mParams = from.getParams();

            if (from.hasExceptions()) {
                optionals.set(5);
                modified.set(5);
                mExceptions = from.getExceptions();
            }

            if (from.hasAnnotations()) {
                optionals.set(6);
                modified.set(6);
                mutableAnnotations().putAll(from.getAnnotations());
            }

            if (from.hasStartPos()) {
                optionals.set(7);
                modified.set(7);
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
                optionals.set(8);
                modified.set(8);
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
         * Sets the value of one_way.
         *
         * @param value The new value
         * @return The builder
         */
        @javax.annotation.Nonnull
        public _Builder setOneWay(boolean value) {
            optionals.set(1);
            modified.set(1);
            mOneWay = value;
            return this;
        }

        /**
         * Checks for presence of the one_way field.
         *
         * @return True if one_way has been set.
         */
        public boolean isSetOneWay() {
            return optionals.get(1);
        }

        /**
         * Checks if one_way has been modified since the _Builder was created.
         *
         * @return True if one_way has been modified.
         */
        public boolean isModifiedOneWay() {
            return modified.get(1);
        }

        /**
         * Clears the one_way field.
         *
         * @return The builder
         */
        @javax.annotation.Nonnull
        public _Builder clearOneWay() {
            optionals.clear(1);
            modified.set(1);
            mOneWay = null;
            return this;
        }

        /**
         * Gets the value of the contained one_way.
         *
         * @return The field value
         */
        public boolean getOneWay() {
            return isSetOneWay() ? mOneWay : kDefaultOneWay;
        }

        /**
         * Sets the value of return_type.
         *
         * @param value The new value
         * @return The builder
         */
        @javax.annotation.Nonnull
        public _Builder setReturnType(String value) {
            if (value == null) {
                return clearReturnType();
            }

            optionals.set(2);
            modified.set(2);
            mReturnType = value;
            return this;
        }

        /**
         * Checks for presence of the return_type field.
         *
         * @return True if return_type has been set.
         */
        public boolean isSetReturnType() {
            return optionals.get(2);
        }

        /**
         * Checks if return_type has been modified since the _Builder was created.
         *
         * @return True if return_type has been modified.
         */
        public boolean isModifiedReturnType() {
            return modified.get(2);
        }

        /**
         * Clears the return_type field.
         *
         * @return The builder
         */
        @javax.annotation.Nonnull
        public _Builder clearReturnType() {
            optionals.clear(2);
            modified.set(2);
            mReturnType = null;
            return this;
        }

        /**
         * Gets the value of the contained return_type.
         *
         * @return The field value
         */
        public String getReturnType() {
            return mReturnType;
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

            optionals.set(3);
            modified.set(3);
            mName = value;
            return this;
        }

        /**
         * Checks for presence of the name field.
         *
         * @return True if name has been set.
         */
        public boolean isSetName() {
            return optionals.get(3);
        }

        /**
         * Checks if name has been modified since the _Builder was created.
         *
         * @return True if name has been modified.
         */
        public boolean isModifiedName() {
            return modified.get(3);
        }

        /**
         * Clears the name field.
         *
         * @return The builder
         */
        @javax.annotation.Nonnull
        public _Builder clearName() {
            optionals.clear(3);
            modified.set(3);
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
         * Sets the value of params.
         *
         * @param value The new value
         * @return The builder
         */
        @javax.annotation.Nonnull
        public _Builder setParams(java.util.Collection<net.morimekta.providence.model.FieldType> value) {
            if (value == null) {
                return clearParams();
            }

            optionals.set(4);
            modified.set(4);
            mParams = com.google.common.collect.ImmutableList.copyOf(value);
            return this;
        }

        /**
         * Adds entries to params.
         *
         * @param values The added value
         * @return The builder
         */
        @javax.annotation.Nonnull
        public _Builder addToParams(net.morimekta.providence.model.FieldType... values) {
            optionals.set(4);
            modified.set(4);
            java.util.List<net.morimekta.providence.model.FieldType> _container = mutableParams();
            for (net.morimekta.providence.model.FieldType item : values) {
                _container.add(item);
            }
            return this;
        }

        /**
         * Checks for presence of the params field.
         *
         * @return True if params has been set.
         */
        public boolean isSetParams() {
            return optionals.get(4);
        }

        /**
         * Checks if params has been modified since the _Builder was created.
         *
         * @return True if params has been modified.
         */
        public boolean isModifiedParams() {
            return modified.get(4);
        }

        /**
         * Clears the params field.
         *
         * @return The builder
         */
        @javax.annotation.Nonnull
        public _Builder clearParams() {
            optionals.clear(4);
            modified.set(4);
            mParams = kDefaultParams;
            return this;
        }

        /**
         * Gets the builder for the contained params.
         *
         * @return The field builder
         */
        @javax.annotation.Nonnull
        public java.util.List<net.morimekta.providence.model.FieldType> mutableParams() {
            optionals.set(4);
            modified.set(4);

            if (mParams == null) {
                mParams = new java.util.ArrayList<>();
            } else if (!(mParams instanceof java.util.ArrayList)) {
                mParams = new java.util.ArrayList<>(mParams);
            }
            return mParams;
        }

        /**
         * Sets the value of exceptions.
         *
         * @param value The new value
         * @return The builder
         */
        @javax.annotation.Nonnull
        public _Builder setExceptions(java.util.Collection<net.morimekta.providence.model.FieldType> value) {
            if (value == null) {
                return clearExceptions();
            }

            optionals.set(5);
            modified.set(5);
            mExceptions = com.google.common.collect.ImmutableList.copyOf(value);
            return this;
        }

        /**
         * Adds entries to exceptions.
         *
         * @param values The added value
         * @return The builder
         */
        @javax.annotation.Nonnull
        public _Builder addToExceptions(net.morimekta.providence.model.FieldType... values) {
            optionals.set(5);
            modified.set(5);
            java.util.List<net.morimekta.providence.model.FieldType> _container = mutableExceptions();
            for (net.morimekta.providence.model.FieldType item : values) {
                _container.add(item);
            }
            return this;
        }

        /**
         * Checks for presence of the exceptions field.
         *
         * @return True if exceptions has been set.
         */
        public boolean isSetExceptions() {
            return optionals.get(5);
        }

        /**
         * Checks if exceptions has been modified since the _Builder was created.
         *
         * @return True if exceptions has been modified.
         */
        public boolean isModifiedExceptions() {
            return modified.get(5);
        }

        /**
         * Clears the exceptions field.
         *
         * @return The builder
         */
        @javax.annotation.Nonnull
        public _Builder clearExceptions() {
            optionals.clear(5);
            modified.set(5);
            mExceptions = null;
            return this;
        }

        /**
         * Gets the builder for the contained exceptions.
         *
         * @return The field builder
         */
        @javax.annotation.Nonnull
        public java.util.List<net.morimekta.providence.model.FieldType> mutableExceptions() {
            optionals.set(5);
            modified.set(5);

            if (mExceptions == null) {
                mExceptions = new java.util.ArrayList<>();
            } else if (!(mExceptions instanceof java.util.ArrayList)) {
                mExceptions = new java.util.ArrayList<>(mExceptions);
            }
            return mExceptions;
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
         * The start of the definition (position of return type)
         *
         * @param value The new value
         * @return The builder
         */
        @javax.annotation.Nonnull
        public _Builder setStartPos(net.morimekta.providence.model.FilePos value) {
            if (value == null) {
                return clearStartPos();
            }

            optionals.set(7);
            modified.set(7);
            mStartPos = value;
            mStartPos_builder = null;
            return this;
        }

        /**
         * The start of the definition (position of return type)
         *
         * @param builder builder for the new value
         * @return The builder
         */
        @javax.annotation.Nonnull
        public _Builder setStartPos(net.morimekta.providence.model.FilePos._Builder builder) {
          return setStartPos(builder == null ? null : builder.build());
        }

        /**
         * The start of the definition (position of return type)
         *
         * @return True if start_pos has been set.
         */
        public boolean isSetStartPos() {
            return optionals.get(7);
        }

        /**
         * The start of the definition (position of return type)
         *
         * @return True if start_pos has been modified.
         */
        public boolean isModifiedStartPos() {
            return modified.get(7);
        }

        /**
         * The start of the definition (position of return type)
         *
         * @return The builder
         */
        @javax.annotation.Nonnull
        public _Builder clearStartPos() {
            optionals.clear(7);
            modified.set(7);
            mStartPos = null;
            mStartPos_builder = null;
            return this;
        }

        /**
         * The start of the definition (position of return type)
         *
         * @return The field builder
         */
        @javax.annotation.Nonnull
        public net.morimekta.providence.model.FilePos._Builder mutableStartPos() {
            optionals.set(7);
            modified.set(7);

            if (mStartPos != null) {
                mStartPos_builder = mStartPos.mutate();
                mStartPos = null;
            } else if (mStartPos_builder == null) {
                mStartPos_builder = net.morimekta.providence.model.FilePos.builder();
            }
            return mStartPos_builder;
        }

        /**
         * The start of the definition (position of return type)
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
         * The end of the definition
         *
         * @param value The new value
         * @return The builder
         */
        @javax.annotation.Nonnull
        public _Builder setEndPos(net.morimekta.providence.model.FilePos value) {
            if (value == null) {
                return clearEndPos();
            }

            optionals.set(8);
            modified.set(8);
            mEndPos = value;
            mEndPos_builder = null;
            return this;
        }

        /**
         * The end of the definition
         *
         * @param builder builder for the new value
         * @return The builder
         */
        @javax.annotation.Nonnull
        public _Builder setEndPos(net.morimekta.providence.model.FilePos._Builder builder) {
          return setEndPos(builder == null ? null : builder.build());
        }

        /**
         * The end of the definition
         *
         * @return True if end_pos has been set.
         */
        public boolean isSetEndPos() {
            return optionals.get(8);
        }

        /**
         * The end of the definition
         *
         * @return True if end_pos has been modified.
         */
        public boolean isModifiedEndPos() {
            return modified.get(8);
        }

        /**
         * The end of the definition
         *
         * @return The builder
         */
        @javax.annotation.Nonnull
        public _Builder clearEndPos() {
            optionals.clear(8);
            modified.set(8);
            mEndPos = null;
            mEndPos_builder = null;
            return this;
        }

        /**
         * The end of the definition
         *
         * @return The field builder
         */
        @javax.annotation.Nonnull
        public net.morimekta.providence.model.FilePos._Builder mutableEndPos() {
            optionals.set(8);
            modified.set(8);

            if (mEndPos != null) {
                mEndPos_builder = mEndPos.mutate();
                mEndPos = null;
            } else if (mEndPos_builder == null) {
                mEndPos_builder = net.morimekta.providence.model.FilePos.builder();
            }
            return mEndPos_builder;
        }

        /**
         * The end of the definition
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
            FunctionType._Builder other = (FunctionType._Builder) o;
            return java.util.Objects.equals(optionals, other.optionals) &&
                   java.util.Objects.equals(mDocumentation, other.mDocumentation) &&
                   java.util.Objects.equals(mOneWay, other.mOneWay) &&
                   java.util.Objects.equals(mReturnType, other.mReturnType) &&
                   java.util.Objects.equals(mName, other.mName) &&
                   java.util.Objects.equals(mParams, other.mParams) &&
                   java.util.Objects.equals(mExceptions, other.mExceptions) &&
                   java.util.Objects.equals(mAnnotations, other.mAnnotations) &&
                   java.util.Objects.equals(getStartPos(), other.getStartPos()) &&
                   java.util.Objects.equals(getEndPos(), other.getEndPos());
        }

        @Override
        public int hashCode() {
            return java.util.Objects.hash(
                    FunctionType.class, optionals,
                    _Field.DOCUMENTATION, mDocumentation,
                    _Field.ONE_WAY, mOneWay,
                    _Field.RETURN_TYPE, mReturnType,
                    _Field.NAME, mName,
                    _Field.PARAMS, mParams,
                    _Field.EXCEPTIONS, mExceptions,
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
                case 2: setOneWay((boolean) value); break;
                case 3: setReturnType((String) value); break;
                case 4: setName((String) value); break;
                case 5: setParams((java.util.List<net.morimekta.providence.model.FieldType>) value); break;
                case 6: setExceptions((java.util.List<net.morimekta.providence.model.FieldType>) value); break;
                case 7: setAnnotations((java.util.Map<String,String>) value); break;
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
                case 5: addToParams((net.morimekta.providence.model.FieldType) value); break;
                case 6: addToExceptions((net.morimekta.providence.model.FieldType) value); break;
                default: break;
            }
            return this;
        }

        @javax.annotation.Nonnull
        @Override
        public _Builder clear(int key) {
            switch (key) {
                case 1: clearDocumentation(); break;
                case 2: clearOneWay(); break;
                case 3: clearReturnType(); break;
                case 4: clearName(); break;
                case 5: clearParams(); break;
                case 6: clearExceptions(); break;
                case 7: clearAnnotations(); break;
                case 10: clearStartPos(); break;
                case 11: clearEndPos(); break;
                default: break;
            }
            return this;
        }

        @Override
        public boolean valid() {
            return optionals.get(3);
        }

        @Override
        public void validate() {
            if (!valid()) {
                java.util.ArrayList<String> missing = new java.util.ArrayList<>();

                if (!optionals.get(3)) {
                    missing.add("name");
                }

                throw new java.lang.IllegalStateException(
                        "Missing required fields " +
                        String.join(",", missing) +
                        " in message providence_model.FunctionType");
            }
        }

        @javax.annotation.Nonnull
        @Override
        public net.morimekta.providence.descriptor.PStructDescriptor<FunctionType,_Field> descriptor() {
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
                            throw new net.morimekta.providence.serializer.SerializerException("Wrong type " + net.morimekta.providence.serializer.binary.BinaryType.asString(type) + " for providence_model.FunctionType.documentation, should be struct(12)");
                        }
                        break;
                    }
                    case 2: {
                        if (type == 2) {
                            mOneWay = reader.expectUInt8() == 1;
                            optionals.set(1);
                        } else {
                            throw new net.morimekta.providence.serializer.SerializerException("Wrong type " + net.morimekta.providence.serializer.binary.BinaryType.asString(type) + " for providence_model.FunctionType.one_way, should be struct(12)");
                        }
                        break;
                    }
                    case 3: {
                        if (type == 11) {
                            int len_2 = reader.expectUInt32();
                            mReturnType = new String(reader.expectBytes(len_2), java.nio.charset.StandardCharsets.UTF_8);
                            optionals.set(2);
                        } else {
                            throw new net.morimekta.providence.serializer.SerializerException("Wrong type " + net.morimekta.providence.serializer.binary.BinaryType.asString(type) + " for providence_model.FunctionType.return_type, should be struct(12)");
                        }
                        break;
                    }
                    case 4: {
                        if (type == 11) {
                            int len_3 = reader.expectUInt32();
                            mName = new String(reader.expectBytes(len_3), java.nio.charset.StandardCharsets.UTF_8);
                            optionals.set(3);
                        } else {
                            throw new net.morimekta.providence.serializer.SerializerException("Wrong type " + net.morimekta.providence.serializer.binary.BinaryType.asString(type) + " for providence_model.FunctionType.name, should be struct(12)");
                        }
                        break;
                    }
                    case 5: {
                        if (type == 15) {
                            net.morimekta.providence.descriptor.PList.DefaultBuilder<net.morimekta.providence.model.FieldType> b_4 = new net.morimekta.providence.descriptor.PList.DefaultBuilder<>();
                            byte t_6 = reader.expectByte();
                            if (t_6 == 12) {
                                final int len_5 = reader.expectUInt32();
                                for (int i_7 = 0; i_7 < len_5; ++i_7) {
                                    net.morimekta.providence.model.FieldType key_8 = net.morimekta.providence.serializer.binary.BinaryFormatUtils.readMessage(reader, net.morimekta.providence.model.FieldType.kDescriptor, strict);
                                    b_4.add(key_8);
                                }
                                mParams = b_4.build();
                            } else {
                                throw new net.morimekta.providence.serializer.SerializerException("Wrong item type " + net.morimekta.providence.serializer.binary.BinaryType.asString(t_6) + " for providence_model.FunctionType.params, should be struct(12)");
                            }
                            optionals.set(4);
                        } else {
                            throw new net.morimekta.providence.serializer.SerializerException("Wrong type " + net.morimekta.providence.serializer.binary.BinaryType.asString(type) + " for providence_model.FunctionType.params, should be struct(12)");
                        }
                        break;
                    }
                    case 6: {
                        if (type == 15) {
                            net.morimekta.providence.descriptor.PList.DefaultBuilder<net.morimekta.providence.model.FieldType> b_9 = new net.morimekta.providence.descriptor.PList.DefaultBuilder<>();
                            byte t_11 = reader.expectByte();
                            if (t_11 == 12) {
                                final int len_10 = reader.expectUInt32();
                                for (int i_12 = 0; i_12 < len_10; ++i_12) {
                                    net.morimekta.providence.model.FieldType key_13 = net.morimekta.providence.serializer.binary.BinaryFormatUtils.readMessage(reader, net.morimekta.providence.model.FieldType.kDescriptor, strict);
                                    b_9.add(key_13);
                                }
                                mExceptions = b_9.build();
                            } else {
                                throw new net.morimekta.providence.serializer.SerializerException("Wrong item type " + net.morimekta.providence.serializer.binary.BinaryType.asString(t_11) + " for providence_model.FunctionType.exceptions, should be struct(12)");
                            }
                            optionals.set(5);
                        } else {
                            throw new net.morimekta.providence.serializer.SerializerException("Wrong type " + net.morimekta.providence.serializer.binary.BinaryType.asString(type) + " for providence_model.FunctionType.exceptions, should be struct(12)");
                        }
                        break;
                    }
                    case 7: {
                        if (type == 13) {
                            net.morimekta.providence.descriptor.PMap.SortedBuilder<String,String> b_14 = new net.morimekta.providence.descriptor.PMap.SortedBuilder<>();
                            byte t_16 = reader.expectByte();
                            byte t_17 = reader.expectByte();
                            if (t_16 == 11 && t_17 == 11) {
                                final int len_15 = reader.expectUInt32();
                                for (int i_18 = 0; i_18 < len_15; ++i_18) {
                                    int len_21 = reader.expectUInt32();
                                    String key_19 = new String(reader.expectBytes(len_21), java.nio.charset.StandardCharsets.UTF_8);
                                    int len_22 = reader.expectUInt32();
                                    String val_20 = new String(reader.expectBytes(len_22), java.nio.charset.StandardCharsets.UTF_8);
                                    b_14.put(key_19, val_20);
                                }
                                mAnnotations = b_14.build();
                            } else {
                                throw new net.morimekta.providence.serializer.SerializerException(
                                        "Wrong key type " + net.morimekta.providence.serializer.binary.BinaryType.asString(t_16) +
                                        " or value type " + net.morimekta.providence.serializer.binary.BinaryType.asString(t_17) +
                                        " for providence_model.FunctionType.annotations, should be string(11) and string(11)");
                            }
                            optionals.set(6);
                        } else {
                            throw new net.morimekta.providence.serializer.SerializerException("Wrong type " + net.morimekta.providence.serializer.binary.BinaryType.asString(type) + " for providence_model.FunctionType.annotations, should be struct(12)");
                        }
                        break;
                    }
                    case 10: {
                        if (type == 12) {
                            mStartPos = net.morimekta.providence.serializer.binary.BinaryFormatUtils.readMessage(reader, net.morimekta.providence.model.FilePos.kDescriptor, strict);
                            optionals.set(7);
                        } else {
                            throw new net.morimekta.providence.serializer.SerializerException("Wrong type " + net.morimekta.providence.serializer.binary.BinaryType.asString(type) + " for providence_model.FunctionType.start_pos, should be struct(12)");
                        }
                        break;
                    }
                    case 11: {
                        if (type == 12) {
                            mEndPos = net.morimekta.providence.serializer.binary.BinaryFormatUtils.readMessage(reader, net.morimekta.providence.model.FilePos.kDescriptor, strict);
                            optionals.set(8);
                        } else {
                            throw new net.morimekta.providence.serializer.SerializerException("Wrong type " + net.morimekta.providence.serializer.binary.BinaryType.asString(type) + " for providence_model.FunctionType.end_pos, should be struct(12)");
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
        public FunctionType build() {
            return new FunctionType(this);
        }
    }
}
