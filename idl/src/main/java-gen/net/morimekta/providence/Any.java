package net.morimekta.providence;

/**
 * A message containing anything.
 */
@SuppressWarnings("unused")
@javax.annotation.Generated("providence-maven-plugin")
@javax.annotation.concurrent.Immutable
public class Any
        implements net.morimekta.providence.PMessage<Any,Any._Field>,
                   Comparable<Any>,
                   java.io.Serializable,
                   net.morimekta.providence.serializer.binary.BinaryWriter {
    private final static long serialVersionUID = 8306176226829184051L;

    private final static String kDefaultType = "";
    private final static String kDefaultMediaType = "application/vnd.apache.thrift.binary";

    private final transient String mType;
    private final transient String mMediaType;
    private final transient net.morimekta.util.Binary mBin;
    private final transient String mStr;

    private volatile transient int tHashCode;

    // Transient object used during java deserialization.
    private transient Any tSerializeInstance;

    private Any(_Builder builder) {
        if (builder.isSetType()) {
            mType = builder.mType;
        } else {
            mType = kDefaultType;
        }
        mMediaType = builder.mMediaType;
        mBin = builder.mBin;
        mStr = builder.mStr;
    }

    public boolean hasType() {
        return true;
    }

    /**
     * The thrift / providence program + message type name. This should refer to a
     * message type. Enums will need to be wrapped in a message to be contained.
     *
     * @return The field value
     */
    @javax.annotation.Nonnull
    public String getType() {
        return mType;
    }

    public boolean hasMediaType() {
        return mMediaType != null;
    }

    /**
     * The media type used for encoding. There will need to exist a serializer
     * registered for this. If the media type is not set, it is assumed to be
     * &#39;application/vnd.apache.thrift.binary&#39;, the default thrift serialization.
     *
     * @return The field value
     */
    public String getMediaType() {
        return hasMediaType() ? mMediaType : kDefaultMediaType;
    }

    /**
     * The media type used for encoding. There will need to exist a serializer
     * registered for this. If the media type is not set, it is assumed to be
     * &#39;application/vnd.apache.thrift.binary&#39;, the default thrift serialization.
     *
     * @return Optional field value
     */
    @javax.annotation.Nonnull
    public java.util.Optional<String> optionalMediaType() {
        return java.util.Optional.ofNullable(mMediaType);
    }

    public boolean hasBin() {
        return mBin != null;
    }

    /**
     * The actual content binary data.
     *
     * @return The field value
     */
    public net.morimekta.util.Binary getBin() {
        return mBin;
    }

    /**
     * The actual content binary data.
     *
     * @return Optional field value
     */
    @javax.annotation.Nonnull
    public java.util.Optional<net.morimekta.util.Binary> optionalBin() {
        return java.util.Optional.ofNullable(mBin);
    }

    public boolean hasStr() {
        return mStr != null;
    }

    /**
     * Optional string encoded content for non-binary media types. If this is filled
     * in, then the &#39;bin&#39; field is not needed.
     *
     * @return The field value
     */
    public String getStr() {
        return mStr;
    }

    /**
     * Optional string encoded content for non-binary media types. If this is filled
     * in, then the &#39;bin&#39; field is not needed.
     *
     * @return Optional field value
     */
    @javax.annotation.Nonnull
    public java.util.Optional<String> optionalStr() {
        return java.util.Optional.ofNullable(mStr);
    }

    @Override
    public boolean has(int key) {
        switch(key) {
            case 1: return true;
            case 2: return mMediaType != null;
            case 4: return mBin != null;
            case 5: return mStr != null;
            default: return false;
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T get(int key) {
        switch(key) {
            case 1: return (T) mType;
            case 2: return (T) mMediaType;
            case 4: return (T) mBin;
            case 5: return (T) mStr;
            default: return null;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        if (o == null || !o.getClass().equals(getClass())) return false;
        Any other = (Any) o;
        return java.util.Objects.equals(mType, other.mType) &&
               java.util.Objects.equals(mMediaType, other.mMediaType) &&
               java.util.Objects.equals(mBin, other.mBin) &&
               java.util.Objects.equals(mStr, other.mStr);
    }

    @Override
    public int hashCode() {
        if (tHashCode == 0) {
            tHashCode = java.util.Objects.hash(
                    Any.class,
                    _Field.TYPE, mType,
                    _Field.MEDIA_TYPE, mMediaType,
                    _Field.BIN, mBin,
                    _Field.STR, mStr);
        }
        return tHashCode;
    }

    @Override
    public String toString() {
        return "providence.Any" + asString();
    }

    @Override
    @javax.annotation.Nonnull
    public String asString() {
        StringBuilder out = new StringBuilder();
        out.append("{");

        out.append("type:")
           .append('\"')
           .append(net.morimekta.util.Strings.escape(mType))
           .append('\"');
        if (hasMediaType()) {
            out.append(',');
            out.append("media_type:")
               .append('\"')
               .append(net.morimekta.util.Strings.escape(mMediaType))
               .append('\"');
        }
        if (hasBin()) {
            out.append(',');
            out.append("bin:")
               .append("b64(")
               .append(mBin.toBase64())
               .append(')');
        }
        if (hasStr()) {
            out.append(',');
            out.append("str:")
               .append('\"')
               .append(net.morimekta.util.Strings.escape(mStr))
               .append('\"');
        }
        out.append('}');
        return out.toString();
    }

    @Override
    public int compareTo(Any other) {
        int c;

        c = mType.compareTo(other.mType);
        if (c != 0) return c;

        c = Boolean.compare(mMediaType != null, other.mMediaType != null);
        if (c != 0) return c;
        if (mMediaType != null) {
            c = mMediaType.compareTo(other.mMediaType);
            if (c != 0) return c;
        }

        c = Boolean.compare(mBin != null, other.mBin != null);
        if (c != 0) return c;
        if (mBin != null) {
            c = mBin.compareTo(other.mBin);
            if (c != 0) return c;
        }

        c = Boolean.compare(mStr != null, other.mStr != null);
        if (c != 0) return c;
        if (mStr != null) {
            c = mStr.compareTo(other.mStr);
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
        net.morimekta.util.Binary tmp_1 = net.morimekta.util.Binary.wrap(mType.getBytes(java.nio.charset.StandardCharsets.UTF_8));
        length += writer.writeUInt32(tmp_1.length());
        length += writer.writeBinary(tmp_1);

        if (hasMediaType()) {
            length += writer.writeByte((byte) 11);
            length += writer.writeShort((short) 2);
            net.morimekta.util.Binary tmp_2 = net.morimekta.util.Binary.wrap(mMediaType.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            length += writer.writeUInt32(tmp_2.length());
            length += writer.writeBinary(tmp_2);
        }

        if (hasBin()) {
            length += writer.writeByte((byte) 11);
            length += writer.writeShort((short) 4);
            length += writer.writeUInt32(mBin.length());
            length += writer.writeBinary(mBin);
        }

        if (hasStr()) {
            length += writer.writeByte((byte) 11);
            length += writer.writeShort((short) 5);
            net.morimekta.util.Binary tmp_3 = net.morimekta.util.Binary.wrap(mStr.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            length += writer.writeUInt32(tmp_3.length());
            length += writer.writeBinary(tmp_3);
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
        TYPE(1, net.morimekta.providence.descriptor.PRequirement.REQUIRED, "type", net.morimekta.providence.descriptor.PPrimitive.STRING.provider(), null),
        MEDIA_TYPE(2, net.morimekta.providence.descriptor.PRequirement.OPTIONAL, "media_type", net.morimekta.providence.descriptor.PPrimitive.STRING.provider(), new net.morimekta.providence.descriptor.PDefaultValueProvider<>(kDefaultMediaType)),
        BIN(4, net.morimekta.providence.descriptor.PRequirement.OPTIONAL, "bin", net.morimekta.providence.descriptor.PPrimitive.BINARY.provider(), null),
        STR(5, net.morimekta.providence.descriptor.PRequirement.OPTIONAL, "str", net.morimekta.providence.descriptor.PPrimitive.STRING.provider(), null),
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
                case 1: return _Field.TYPE;
                case 2: return _Field.MEDIA_TYPE;
                case 4: return _Field.BIN;
                case 5: return _Field.STR;
            }
            return null;
        }

        /**
         * @param name Field name
         * @return The named field or null
         */
        public static _Field findByName(String name) {
            switch (name) {
                case "type": return _Field.TYPE;
                case "media_type": return _Field.MEDIA_TYPE;
                case "bin": return _Field.BIN;
                case "str": return _Field.STR;
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
                throw new IllegalArgumentException("No such field id " + id + " in providence.Any");
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
                throw new IllegalArgumentException("No such field \"" + name + "\" in providence.Any");
            }
            return field;
        }

    }

    @javax.annotation.Nonnull
    public static net.morimekta.providence.descriptor.PStructDescriptorProvider<Any,_Field> provider() {
        return new _Provider();
    }

    @Override
    @javax.annotation.Nonnull
    public net.morimekta.providence.descriptor.PStructDescriptor<Any,_Field> descriptor() {
        return kDescriptor;
    }

    public static final net.morimekta.providence.descriptor.PStructDescriptor<Any,_Field> kDescriptor;

    private static class _Descriptor
            extends net.morimekta.providence.descriptor.PStructDescriptor<Any,_Field> {
        public _Descriptor() {
            super("providence", "Any", _Builder::new, true);
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

    private final static class _Provider extends net.morimekta.providence.descriptor.PStructDescriptorProvider<Any,_Field> {
        @Override
        public net.morimekta.providence.descriptor.PStructDescriptor<Any,_Field> descriptor() {
            return kDescriptor;
        }
    }

    /**
     * Make a providence.Any builder.
     * @return The builder instance.
     */
    public static _Builder builder() {
        return new _Builder();
    }

    /**
     * A message containing anything.
     */
    public static class _Builder
            extends net.morimekta.providence.PMessageBuilder<Any,_Field>
            implements net.morimekta.providence.serializer.binary.BinaryReader {
        private java.util.BitSet optionals;
        private java.util.BitSet modified;

        private String mType;
        private String mMediaType;
        private net.morimekta.util.Binary mBin;
        private String mStr;

        /**
         * Make a providence.Any builder.
         */
        public _Builder() {
            optionals = new java.util.BitSet(4);
            modified = new java.util.BitSet(4);
            mType = kDefaultType;
        }

        /**
         * Make a mutating builder off a base providence.Any.
         *
         * @param base The base Any
         */
        public _Builder(Any base) {
            this();

            optionals.set(0);
            mType = base.mType;
            if (base.hasMediaType()) {
                optionals.set(1);
                mMediaType = base.mMediaType;
            }
            if (base.hasBin()) {
                optionals.set(2);
                mBin = base.mBin;
            }
            if (base.hasStr()) {
                optionals.set(3);
                mStr = base.mStr;
            }
        }

        @javax.annotation.Nonnull
        @Override
        public _Builder merge(Any from) {
            optionals.set(0);
            modified.set(0);
            mType = from.getType();

            if (from.hasMediaType()) {
                optionals.set(1);
                modified.set(1);
                mMediaType = from.getMediaType();
            }

            if (from.hasBin()) {
                optionals.set(2);
                modified.set(2);
                mBin = from.getBin();
            }

            if (from.hasStr()) {
                optionals.set(3);
                modified.set(3);
                mStr = from.getStr();
            }
            return this;
        }

        /**
         * The thrift / providence program + message type name. This should refer to a
         * message type. Enums will need to be wrapped in a message to be contained.
         *
         * @param value The new value
         * @return The builder
         */
        @javax.annotation.Nonnull
        public _Builder setType(String value) {
            if (value == null) {
                return clearType();
            }

            optionals.set(0);
            modified.set(0);
            mType = value;
            return this;
        }

        /**
         * The thrift / providence program + message type name. This should refer to a
         * message type. Enums will need to be wrapped in a message to be contained.
         *
         * @return True if type has been set.
         */
        public boolean isSetType() {
            return optionals.get(0);
        }

        /**
         * The thrift / providence program + message type name. This should refer to a
         * message type. Enums will need to be wrapped in a message to be contained.
         *
         * @return True if type has been modified.
         */
        public boolean isModifiedType() {
            return modified.get(0);
        }

        /**
         * The thrift / providence program + message type name. This should refer to a
         * message type. Enums will need to be wrapped in a message to be contained.
         *
         * @return The builder
         */
        @javax.annotation.Nonnull
        public _Builder clearType() {
            optionals.clear(0);
            modified.set(0);
            mType = kDefaultType;
            return this;
        }

        /**
         * The thrift / providence program + message type name. This should refer to a
         * message type. Enums will need to be wrapped in a message to be contained.
         *
         * @return The field value
         */
        public String getType() {
            return mType;
        }

        /**
         * The media type used for encoding. There will need to exist a serializer
         * registered for this. If the media type is not set, it is assumed to be
         * &#39;application/vnd.apache.thrift.binary&#39;, the default thrift serialization.
         *
         * @param value The new value
         * @return The builder
         */
        @javax.annotation.Nonnull
        public _Builder setMediaType(String value) {
            if (value == null) {
                return clearMediaType();
            }

            optionals.set(1);
            modified.set(1);
            mMediaType = value;
            return this;
        }

        /**
         * The media type used for encoding. There will need to exist a serializer
         * registered for this. If the media type is not set, it is assumed to be
         * &#39;application/vnd.apache.thrift.binary&#39;, the default thrift serialization.
         *
         * @return True if media_type has been set.
         */
        public boolean isSetMediaType() {
            return optionals.get(1);
        }

        /**
         * The media type used for encoding. There will need to exist a serializer
         * registered for this. If the media type is not set, it is assumed to be
         * &#39;application/vnd.apache.thrift.binary&#39;, the default thrift serialization.
         *
         * @return True if media_type has been modified.
         */
        public boolean isModifiedMediaType() {
            return modified.get(1);
        }

        /**
         * The media type used for encoding. There will need to exist a serializer
         * registered for this. If the media type is not set, it is assumed to be
         * &#39;application/vnd.apache.thrift.binary&#39;, the default thrift serialization.
         *
         * @return The builder
         */
        @javax.annotation.Nonnull
        public _Builder clearMediaType() {
            optionals.clear(1);
            modified.set(1);
            mMediaType = null;
            return this;
        }

        /**
         * The media type used for encoding. There will need to exist a serializer
         * registered for this. If the media type is not set, it is assumed to be
         * &#39;application/vnd.apache.thrift.binary&#39;, the default thrift serialization.
         *
         * @return The field value
         */
        public String getMediaType() {
            return isSetMediaType() ? mMediaType : kDefaultMediaType;
        }

        /**
         * The actual content binary data.
         *
         * @param value The new value
         * @return The builder
         */
        @javax.annotation.Nonnull
        public _Builder setBin(net.morimekta.util.Binary value) {
            if (value == null) {
                return clearBin();
            }

            optionals.set(2);
            modified.set(2);
            mBin = value;
            return this;
        }

        /**
         * The actual content binary data.
         *
         * @return True if bin has been set.
         */
        public boolean isSetBin() {
            return optionals.get(2);
        }

        /**
         * The actual content binary data.
         *
         * @return True if bin has been modified.
         */
        public boolean isModifiedBin() {
            return modified.get(2);
        }

        /**
         * The actual content binary data.
         *
         * @return The builder
         */
        @javax.annotation.Nonnull
        public _Builder clearBin() {
            optionals.clear(2);
            modified.set(2);
            mBin = null;
            return this;
        }

        /**
         * The actual content binary data.
         *
         * @return The field value
         */
        public net.morimekta.util.Binary getBin() {
            return mBin;
        }

        /**
         * Optional string encoded content for non-binary media types. If this is filled
         * in, then the &#39;bin&#39; field is not needed.
         *
         * @param value The new value
         * @return The builder
         */
        @javax.annotation.Nonnull
        public _Builder setStr(String value) {
            if (value == null) {
                return clearStr();
            }

            optionals.set(3);
            modified.set(3);
            mStr = value;
            return this;
        }

        /**
         * Optional string encoded content for non-binary media types. If this is filled
         * in, then the &#39;bin&#39; field is not needed.
         *
         * @return True if str has been set.
         */
        public boolean isSetStr() {
            return optionals.get(3);
        }

        /**
         * Optional string encoded content for non-binary media types. If this is filled
         * in, then the &#39;bin&#39; field is not needed.
         *
         * @return True if str has been modified.
         */
        public boolean isModifiedStr() {
            return modified.get(3);
        }

        /**
         * Optional string encoded content for non-binary media types. If this is filled
         * in, then the &#39;bin&#39; field is not needed.
         *
         * @return The builder
         */
        @javax.annotation.Nonnull
        public _Builder clearStr() {
            optionals.clear(3);
            modified.set(3);
            mStr = null;
            return this;
        }

        /**
         * Optional string encoded content for non-binary media types. If this is filled
         * in, then the &#39;bin&#39; field is not needed.
         *
         * @return The field value
         */
        public String getStr() {
            return mStr;
        }

        @Override
        public boolean equals(Object o) {
            if (o == this) return true;
            if (o == null || !o.getClass().equals(getClass())) return false;
            Any._Builder other = (Any._Builder) o;
            return java.util.Objects.equals(optionals, other.optionals) &&
                   java.util.Objects.equals(mType, other.mType) &&
                   java.util.Objects.equals(mMediaType, other.mMediaType) &&
                   java.util.Objects.equals(mBin, other.mBin) &&
                   java.util.Objects.equals(mStr, other.mStr);
        }

        @Override
        public int hashCode() {
            return java.util.Objects.hash(
                    Any.class, optionals,
                    _Field.TYPE, mType,
                    _Field.MEDIA_TYPE, mMediaType,
                    _Field.BIN, mBin,
                    _Field.STR, mStr);
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
                case 1: setType((String) value); break;
                case 2: setMediaType((String) value); break;
                case 4: setBin((net.morimekta.util.Binary) value); break;
                case 5: setStr((String) value); break;
                default: break;
            }
            return this;
        }

        @Override
        public boolean isSet(int key) {
            switch (key) {
                case 1: return optionals.get(0);
                case 2: return optionals.get(1);
                case 4: return optionals.get(2);
                case 5: return optionals.get(3);
                default: break;
            }
            return false;
        }

        @Override
        public boolean isModified(int key) {
            switch (key) {
                case 1: return modified.get(0);
                case 2: return modified.get(1);
                case 4: return modified.get(2);
                case 5: return modified.get(3);
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
                case 1: clearType(); break;
                case 2: clearMediaType(); break;
                case 4: clearBin(); break;
                case 5: clearStr(); break;
                default: break;
            }
            return this;
        }

        @Override
        public boolean valid() {
            return optionals.get(0);
        }

        @Override
        public void validate() {
            if (!valid()) {
                java.util.ArrayList<String> missing = new java.util.ArrayList<>();

                if (!optionals.get(0)) {
                    missing.add("type");
                }

                throw new java.lang.IllegalStateException(
                        "Missing required fields " +
                        String.join(",", missing) +
                        " in message providence.Any");
            }
        }

        @javax.annotation.Nonnull
        @Override
        public net.morimekta.providence.descriptor.PStructDescriptor<Any,_Field> descriptor() {
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
                            mType = new String(reader.expectBytes(len_1), java.nio.charset.StandardCharsets.UTF_8);
                            optionals.set(0);
                        } else {
                            throw new net.morimekta.providence.serializer.SerializerException("Wrong type " + net.morimekta.providence.serializer.binary.BinaryType.asString(type) + " for providence.Any.type, should be struct(12)");
                        }
                        break;
                    }
                    case 2: {
                        if (type == 11) {
                            int len_2 = reader.expectUInt32();
                            mMediaType = new String(reader.expectBytes(len_2), java.nio.charset.StandardCharsets.UTF_8);
                            optionals.set(1);
                        } else {
                            throw new net.morimekta.providence.serializer.SerializerException("Wrong type " + net.morimekta.providence.serializer.binary.BinaryType.asString(type) + " for providence.Any.media_type, should be struct(12)");
                        }
                        break;
                    }
                    case 4: {
                        if (type == 11) {
                            int len_3 = reader.expectUInt32();
                            mBin = reader.expectBinary(len_3);
                            optionals.set(2);
                        } else {
                            throw new net.morimekta.providence.serializer.SerializerException("Wrong type " + net.morimekta.providence.serializer.binary.BinaryType.asString(type) + " for providence.Any.bin, should be struct(12)");
                        }
                        break;
                    }
                    case 5: {
                        if (type == 11) {
                            int len_4 = reader.expectUInt32();
                            mStr = new String(reader.expectBytes(len_4), java.nio.charset.StandardCharsets.UTF_8);
                            optionals.set(3);
                        } else {
                            throw new net.morimekta.providence.serializer.SerializerException("Wrong type " + net.morimekta.providence.serializer.binary.BinaryType.asString(type) + " for providence.Any.str, should be struct(12)");
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
        public Any build() {
            return new Any(this);
        }
    }
}
