package net.morimekta.providence.model;

/**
 * ( &lt;enum&gt; | &lt;typedef&gt; | &lt;struct&gt; | &lt;service&gt; | &lt;const&gt; )
 */
@SuppressWarnings("unused")
@javax.annotation.Generated("providence-maven-plugin")
@javax.annotation.concurrent.Immutable
public class Declaration
        implements net.morimekta.providence.PUnion<Declaration,Declaration._Field>,
                   Comparable<Declaration>,
                   java.io.Serializable,
                   net.morimekta.providence.serializer.binary.BinaryWriter {
    private final static long serialVersionUID = 6735853549980473725L;

    private final transient net.morimekta.providence.model.EnumType mDeclEnum;
    private final transient net.morimekta.providence.model.TypedefType mDeclTypedef;
    private final transient net.morimekta.providence.model.MessageType mDeclMessage;
    private final transient net.morimekta.providence.model.ServiceType mDeclService;
    private final transient net.morimekta.providence.model.ConstType mDeclConst;

    private transient final _Field tUnionField;

    private volatile transient int tHashCode;

    // Transient object used during java deserialization.
    private transient Declaration tSerializeInstance;

    /**
     * @param value The union value
     * @return The created union.
     */
    public static Declaration withDeclEnum(net.morimekta.providence.model.EnumType value) {
        return new _Builder().setDeclEnum(value).build();
    }

    /**
     * @param value The union value
     * @return The created union.
     */
    public static Declaration withDeclEnum(net.morimekta.providence.model.EnumType._Builder value) {
        return withDeclEnum(value == null ? null : value.build());
    }

    /**
     * @param value The union value
     * @return The created union.
     */
    public static Declaration withDeclTypedef(net.morimekta.providence.model.TypedefType value) {
        return new _Builder().setDeclTypedef(value).build();
    }

    /**
     * @param value The union value
     * @return The created union.
     */
    public static Declaration withDeclTypedef(net.morimekta.providence.model.TypedefType._Builder value) {
        return withDeclTypedef(value == null ? null : value.build());
    }

    /**
     * @param value The union value
     * @return The created union.
     */
    public static Declaration withDeclMessage(net.morimekta.providence.model.MessageType value) {
        return new _Builder().setDeclMessage(value).build();
    }

    /**
     * @param value The union value
     * @return The created union.
     */
    public static Declaration withDeclMessage(net.morimekta.providence.model.MessageType._Builder value) {
        return withDeclMessage(value == null ? null : value.build());
    }

    /**
     * @param value The union value
     * @return The created union.
     */
    public static Declaration withDeclService(net.morimekta.providence.model.ServiceType value) {
        return new _Builder().setDeclService(value).build();
    }

    /**
     * @param value The union value
     * @return The created union.
     */
    public static Declaration withDeclService(net.morimekta.providence.model.ServiceType._Builder value) {
        return withDeclService(value == null ? null : value.build());
    }

    /**
     * @param value The union value
     * @return The created union.
     */
    public static Declaration withDeclConst(net.morimekta.providence.model.ConstType value) {
        return new _Builder().setDeclConst(value).build();
    }

    /**
     * @param value The union value
     * @return The created union.
     */
    public static Declaration withDeclConst(net.morimekta.providence.model.ConstType._Builder value) {
        return withDeclConst(value == null ? null : value.build());
    }

    private Declaration(_Builder builder) {
        tUnionField = builder.tUnionField;

        mDeclEnum = tUnionField != _Field.DECL_ENUM
                ? null
                : builder.mDeclEnum_builder != null ? builder.mDeclEnum_builder.build() : builder.mDeclEnum;
        mDeclTypedef = tUnionField != _Field.DECL_TYPEDEF
                ? null
                : builder.mDeclTypedef_builder != null ? builder.mDeclTypedef_builder.build() : builder.mDeclTypedef;
        mDeclMessage = tUnionField != _Field.DECL_MESSAGE
                ? null
                : builder.mDeclMessage_builder != null ? builder.mDeclMessage_builder.build() : builder.mDeclMessage;
        mDeclService = tUnionField != _Field.DECL_SERVICE
                ? null
                : builder.mDeclService_builder != null ? builder.mDeclService_builder.build() : builder.mDeclService;
        mDeclConst = tUnionField != _Field.DECL_CONST
                ? null
                : builder.mDeclConst_builder != null ? builder.mDeclConst_builder.build() : builder.mDeclConst;
    }

    public boolean hasDeclEnum() {
        return tUnionField == _Field.DECL_ENUM && mDeclEnum != null;
    }

    /**
     * @return The field value
     */
    public net.morimekta.providence.model.EnumType getDeclEnum() {
        return mDeclEnum;
    }

    /**
     * @return Optional field value
     */
    @javax.annotation.Nonnull
    public java.util.Optional<net.morimekta.providence.model.EnumType> optionalDeclEnum() {
        return java.util.Optional.ofNullable(mDeclEnum);
    }

    public boolean hasDeclTypedef() {
        return tUnionField == _Field.DECL_TYPEDEF && mDeclTypedef != null;
    }

    /**
     * @return The field value
     */
    public net.morimekta.providence.model.TypedefType getDeclTypedef() {
        return mDeclTypedef;
    }

    /**
     * @return Optional field value
     */
    @javax.annotation.Nonnull
    public java.util.Optional<net.morimekta.providence.model.TypedefType> optionalDeclTypedef() {
        return java.util.Optional.ofNullable(mDeclTypedef);
    }

    public boolean hasDeclMessage() {
        return tUnionField == _Field.DECL_MESSAGE && mDeclMessage != null;
    }

    /**
     * @return The field value
     */
    public net.morimekta.providence.model.MessageType getDeclMessage() {
        return mDeclMessage;
    }

    /**
     * @return Optional field value
     */
    @javax.annotation.Nonnull
    public java.util.Optional<net.morimekta.providence.model.MessageType> optionalDeclMessage() {
        return java.util.Optional.ofNullable(mDeclMessage);
    }

    public boolean hasDeclService() {
        return tUnionField == _Field.DECL_SERVICE && mDeclService != null;
    }

    /**
     * @return The field value
     */
    public net.morimekta.providence.model.ServiceType getDeclService() {
        return mDeclService;
    }

    /**
     * @return Optional field value
     */
    @javax.annotation.Nonnull
    public java.util.Optional<net.morimekta.providence.model.ServiceType> optionalDeclService() {
        return java.util.Optional.ofNullable(mDeclService);
    }

    public boolean hasDeclConst() {
        return tUnionField == _Field.DECL_CONST && mDeclConst != null;
    }

    /**
     * @return The field value
     */
    public net.morimekta.providence.model.ConstType getDeclConst() {
        return mDeclConst;
    }

    /**
     * @return Optional field value
     */
    @javax.annotation.Nonnull
    public java.util.Optional<net.morimekta.providence.model.ConstType> optionalDeclConst() {
        return java.util.Optional.ofNullable(mDeclConst);
    }

    @Override
    public boolean has(int key) {
        switch(key) {
            case 1: return tUnionField == _Field.DECL_ENUM;
            case 2: return tUnionField == _Field.DECL_TYPEDEF;
            case 3: return tUnionField == _Field.DECL_MESSAGE;
            case 4: return tUnionField == _Field.DECL_SERVICE;
            case 5: return tUnionField == _Field.DECL_CONST;
            default: return false;
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T get(int key) {
        switch(key) {
            case 1: return (T) mDeclEnum;
            case 2: return (T) mDeclTypedef;
            case 3: return (T) mDeclMessage;
            case 4: return (T) mDeclService;
            case 5: return (T) mDeclConst;
            default: return null;
        }
    }

    @Override
    public boolean unionFieldIsSet() {
        return tUnionField != null;
    }

    @Override
    @javax.annotation.Nonnull
    public _Field unionField() {
        if (tUnionField == null) throw new IllegalStateException("No union field set in providence_model.Declaration");
        return tUnionField;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        if (o == null || !o.getClass().equals(getClass())) return false;
        Declaration other = (Declaration) o;
        return java.util.Objects.equals(tUnionField, other.tUnionField) &&
               java.util.Objects.equals(mDeclEnum, other.mDeclEnum) &&
               java.util.Objects.equals(mDeclTypedef, other.mDeclTypedef) &&
               java.util.Objects.equals(mDeclMessage, other.mDeclMessage) &&
               java.util.Objects.equals(mDeclService, other.mDeclService) &&
               java.util.Objects.equals(mDeclConst, other.mDeclConst);
    }

    @Override
    public int hashCode() {
        if (tHashCode == 0) {
            tHashCode = java.util.Objects.hash(
                    Declaration.class,
                    _Field.DECL_ENUM, mDeclEnum,
                    _Field.DECL_TYPEDEF, mDeclTypedef,
                    _Field.DECL_MESSAGE, mDeclMessage,
                    _Field.DECL_SERVICE, mDeclService,
                    _Field.DECL_CONST, mDeclConst);
        }
        return tHashCode;
    }

    @Override
    public String toString() {
        return "providence_model.Declaration" + asString();
    }

    @Override
    @javax.annotation.Nonnull
    public String asString() {
        StringBuilder out = new StringBuilder();
        out.append("{");

        switch (tUnionField) {
            case DECL_ENUM: {
                out.append("decl_enum:")
                   .append(mDeclEnum.asString());
                break;
            }
            case DECL_TYPEDEF: {
                out.append("decl_typedef:")
                   .append(mDeclTypedef.asString());
                break;
            }
            case DECL_MESSAGE: {
                out.append("decl_message:")
                   .append(mDeclMessage.asString());
                break;
            }
            case DECL_SERVICE: {
                out.append("decl_service:")
                   .append(mDeclService.asString());
                break;
            }
            case DECL_CONST: {
                out.append("decl_const:")
                   .append(mDeclConst.asString());
                break;
            }
        }
        out.append('}');
        return out.toString();
    }

    @Override
    public int compareTo(Declaration other) {
        if (tUnionField == null || other.tUnionField == null) return Boolean.compare(tUnionField != null, other.tUnionField != null);
        int c = tUnionField.compareTo(other.tUnionField);
        if (c != 0) return c;

        switch (tUnionField) {
            case DECL_ENUM:
                return mDeclEnum.compareTo(other.mDeclEnum);
            case DECL_TYPEDEF:
                return mDeclTypedef.compareTo(other.mDeclTypedef);
            case DECL_MESSAGE:
                return mDeclMessage.compareTo(other.mDeclMessage);
            case DECL_SERVICE:
                return mDeclService.compareTo(other.mDeclService);
            case DECL_CONST:
                return mDeclConst.compareTo(other.mDeclConst);
            default: return 0;
        }
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

        if (tUnionField != null) {
            switch (tUnionField) {
                case DECL_ENUM: {
                    length += writer.writeByte((byte) 12);
                    length += writer.writeShort((short) 1);
                    length += net.morimekta.providence.serializer.binary.BinaryFormatUtils.writeMessage(writer, mDeclEnum);
                    break;
                }
                case DECL_TYPEDEF: {
                    length += writer.writeByte((byte) 12);
                    length += writer.writeShort((short) 2);
                    length += net.morimekta.providence.serializer.binary.BinaryFormatUtils.writeMessage(writer, mDeclTypedef);
                    break;
                }
                case DECL_MESSAGE: {
                    length += writer.writeByte((byte) 12);
                    length += writer.writeShort((short) 3);
                    length += net.morimekta.providence.serializer.binary.BinaryFormatUtils.writeMessage(writer, mDeclMessage);
                    break;
                }
                case DECL_SERVICE: {
                    length += writer.writeByte((byte) 12);
                    length += writer.writeShort((short) 4);
                    length += net.morimekta.providence.serializer.binary.BinaryFormatUtils.writeMessage(writer, mDeclService);
                    break;
                }
                case DECL_CONST: {
                    length += writer.writeByte((byte) 12);
                    length += writer.writeShort((short) 5);
                    length += net.morimekta.providence.serializer.binary.BinaryFormatUtils.writeMessage(writer, mDeclConst);
                    break;
                }
                default: break;
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
        DECL_ENUM(1, net.morimekta.providence.descriptor.PRequirement.OPTIONAL, "decl_enum", net.morimekta.providence.model.EnumType.provider(), null),
        DECL_TYPEDEF(2, net.morimekta.providence.descriptor.PRequirement.OPTIONAL, "decl_typedef", net.morimekta.providence.model.TypedefType.provider(), null),
        DECL_MESSAGE(3, net.morimekta.providence.descriptor.PRequirement.OPTIONAL, "decl_message", net.morimekta.providence.model.MessageType.provider(), null),
        DECL_SERVICE(4, net.morimekta.providence.descriptor.PRequirement.OPTIONAL, "decl_service", net.morimekta.providence.model.ServiceType.provider(), null),
        DECL_CONST(5, net.morimekta.providence.descriptor.PRequirement.OPTIONAL, "decl_const", net.morimekta.providence.model.ConstType.provider(), null),
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
                case 1: return _Field.DECL_ENUM;
                case 2: return _Field.DECL_TYPEDEF;
                case 3: return _Field.DECL_MESSAGE;
                case 4: return _Field.DECL_SERVICE;
                case 5: return _Field.DECL_CONST;
            }
            return null;
        }

        /**
         * @param name Field name
         * @return The named field or null
         */
        public static _Field findByName(String name) {
            switch (name) {
                case "decl_enum": return _Field.DECL_ENUM;
                case "decl_typedef": return _Field.DECL_TYPEDEF;
                case "decl_message": return _Field.DECL_MESSAGE;
                case "decl_service": return _Field.DECL_SERVICE;
                case "decl_const": return _Field.DECL_CONST;
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
                throw new IllegalArgumentException("No such field id " + id + " in providence_model.Declaration");
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
                throw new IllegalArgumentException("No such field \"" + name + "\" in providence_model.Declaration");
            }
            return field;
        }

    }

    @javax.annotation.Nonnull
    public static net.morimekta.providence.descriptor.PUnionDescriptorProvider<Declaration,_Field> provider() {
        return new _Provider();
    }

    @Override
    @javax.annotation.Nonnull
    public net.morimekta.providence.descriptor.PUnionDescriptor<Declaration,_Field> descriptor() {
        return kDescriptor;
    }

    public static final net.morimekta.providence.descriptor.PUnionDescriptor<Declaration,_Field> kDescriptor;

    private static class _Descriptor
            extends net.morimekta.providence.descriptor.PUnionDescriptor<Declaration,_Field> {
        public _Descriptor() {
            super("providence_model", "Declaration", _Builder::new, false);
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

    private final static class _Provider extends net.morimekta.providence.descriptor.PUnionDescriptorProvider<Declaration,_Field> {
        @Override
        public net.morimekta.providence.descriptor.PUnionDescriptor<Declaration,_Field> descriptor() {
            return kDescriptor;
        }
    }

    /**
     * Make a providence_model.Declaration builder.
     * @return The builder instance.
     */
    public static _Builder builder() {
        return new _Builder();
    }

    /**
     * ( &lt;enum&gt; | &lt;typedef&gt; | &lt;struct&gt; | &lt;service&gt; | &lt;const&gt; )
     */
    public static class _Builder
            extends net.morimekta.providence.PMessageBuilder<Declaration,_Field>
            implements net.morimekta.providence.serializer.binary.BinaryReader {
        private _Field tUnionField;

        private boolean modified;

        private net.morimekta.providence.model.EnumType mDeclEnum;
        private net.morimekta.providence.model.EnumType._Builder mDeclEnum_builder;
        private net.morimekta.providence.model.TypedefType mDeclTypedef;
        private net.morimekta.providence.model.TypedefType._Builder mDeclTypedef_builder;
        private net.morimekta.providence.model.MessageType mDeclMessage;
        private net.morimekta.providence.model.MessageType._Builder mDeclMessage_builder;
        private net.morimekta.providence.model.ServiceType mDeclService;
        private net.morimekta.providence.model.ServiceType._Builder mDeclService_builder;
        private net.morimekta.providence.model.ConstType mDeclConst;
        private net.morimekta.providence.model.ConstType._Builder mDeclConst_builder;

        /**
         * Make a providence_model.Declaration builder.
         */
        public _Builder() {
            modified = false;
        }

        /**
         * Make a mutating builder off a base providence_model.Declaration.
         *
         * @param base The base Declaration
         */
        public _Builder(Declaration base) {
            this();

            tUnionField = base.tUnionField;

            mDeclEnum = base.mDeclEnum;
            mDeclTypedef = base.mDeclTypedef;
            mDeclMessage = base.mDeclMessage;
            mDeclService = base.mDeclService;
            mDeclConst = base.mDeclConst;
        }

        @javax.annotation.Nonnull
        @Override
        public _Builder merge(Declaration from) {
            if (!from.unionFieldIsSet()) {
                return this;
            }

            switch (from.unionField()) {
                case DECL_ENUM: {
                    if (tUnionField == _Field.DECL_ENUM && mDeclEnum != null) {
                        mDeclEnum = mDeclEnum.mutate().merge(from.getDeclEnum()).build();
                    } else {
                        setDeclEnum(from.getDeclEnum());
                    }
                    break;
                }
                case DECL_TYPEDEF: {
                    if (tUnionField == _Field.DECL_TYPEDEF && mDeclTypedef != null) {
                        mDeclTypedef = mDeclTypedef.mutate().merge(from.getDeclTypedef()).build();
                    } else {
                        setDeclTypedef(from.getDeclTypedef());
                    }
                    break;
                }
                case DECL_MESSAGE: {
                    if (tUnionField == _Field.DECL_MESSAGE && mDeclMessage != null) {
                        mDeclMessage = mDeclMessage.mutate().merge(from.getDeclMessage()).build();
                    } else {
                        setDeclMessage(from.getDeclMessage());
                    }
                    break;
                }
                case DECL_SERVICE: {
                    if (tUnionField == _Field.DECL_SERVICE && mDeclService != null) {
                        mDeclService = mDeclService.mutate().merge(from.getDeclService()).build();
                    } else {
                        setDeclService(from.getDeclService());
                    }
                    break;
                }
                case DECL_CONST: {
                    if (tUnionField == _Field.DECL_CONST && mDeclConst != null) {
                        mDeclConst = mDeclConst.mutate().merge(from.getDeclConst()).build();
                    } else {
                        setDeclConst(from.getDeclConst());
                    }
                    break;
                }
            }
            return this;
        }

        /**
         * Sets the value of decl_enum.
         *
         * @param value The new value
         * @return The builder
         */
        @javax.annotation.Nonnull
        public _Builder setDeclEnum(net.morimekta.providence.model.EnumType value) {
            if (value == null) {
                return clearDeclEnum();
            }

            tUnionField = _Field.DECL_ENUM;
            modified = true;
            mDeclEnum = value;
            mDeclEnum_builder = null;
            return this;
        }

        /**
         * Sets the value of decl_enum.
         *
         * @param builder builder for the new value
         * @return The builder
         */
        @javax.annotation.Nonnull
        public _Builder setDeclEnum(net.morimekta.providence.model.EnumType._Builder builder) {
          return setDeclEnum(builder == null ? null : builder.build());
        }

        /**
         * Checks for presence of the decl_enum field.
         *
         * @return True if decl_enum has been set.
         */
        public boolean isSetDeclEnum() {
            return tUnionField == _Field.DECL_ENUM;
        }

        /**
         * Clears the decl_enum field.
         *
         * @return The builder
         */
        @javax.annotation.Nonnull
        public _Builder clearDeclEnum() {
            if (tUnionField == _Field.DECL_ENUM) tUnionField = null;
            modified = true;
            mDeclEnum = null;
            mDeclEnum_builder = null;
            return this;
        }

        /**
         * Gets the builder for the contained decl_enum.
         *
         * @return The field builder
         */
        @javax.annotation.Nonnull
        public net.morimekta.providence.model.EnumType._Builder mutableDeclEnum() {
            if (tUnionField != _Field.DECL_ENUM) {
                clearDeclEnum();
            }
            tUnionField = _Field.DECL_ENUM;
            modified = true;

            if (mDeclEnum != null) {
                mDeclEnum_builder = mDeclEnum.mutate();
                mDeclEnum = null;
            } else if (mDeclEnum_builder == null) {
                mDeclEnum_builder = net.morimekta.providence.model.EnumType.builder();
            }
            return mDeclEnum_builder;
        }

        /**
         * Gets the value for the contained decl_enum.
         *
         * @return The field value
         */
        public net.morimekta.providence.model.EnumType getDeclEnum() {
            if (tUnionField != _Field.DECL_ENUM) {
                return null;
            }

            if (mDeclEnum_builder != null) {
                return mDeclEnum_builder.build();
            }
            return mDeclEnum;
        }

        /**
         * Sets the value of decl_typedef.
         *
         * @param value The new value
         * @return The builder
         */
        @javax.annotation.Nonnull
        public _Builder setDeclTypedef(net.morimekta.providence.model.TypedefType value) {
            if (value == null) {
                return clearDeclTypedef();
            }

            tUnionField = _Field.DECL_TYPEDEF;
            modified = true;
            mDeclTypedef = value;
            mDeclTypedef_builder = null;
            return this;
        }

        /**
         * Sets the value of decl_typedef.
         *
         * @param builder builder for the new value
         * @return The builder
         */
        @javax.annotation.Nonnull
        public _Builder setDeclTypedef(net.morimekta.providence.model.TypedefType._Builder builder) {
          return setDeclTypedef(builder == null ? null : builder.build());
        }

        /**
         * Checks for presence of the decl_typedef field.
         *
         * @return True if decl_typedef has been set.
         */
        public boolean isSetDeclTypedef() {
            return tUnionField == _Field.DECL_TYPEDEF;
        }

        /**
         * Clears the decl_typedef field.
         *
         * @return The builder
         */
        @javax.annotation.Nonnull
        public _Builder clearDeclTypedef() {
            if (tUnionField == _Field.DECL_TYPEDEF) tUnionField = null;
            modified = true;
            mDeclTypedef = null;
            mDeclTypedef_builder = null;
            return this;
        }

        /**
         * Gets the builder for the contained decl_typedef.
         *
         * @return The field builder
         */
        @javax.annotation.Nonnull
        public net.morimekta.providence.model.TypedefType._Builder mutableDeclTypedef() {
            if (tUnionField != _Field.DECL_TYPEDEF) {
                clearDeclTypedef();
            }
            tUnionField = _Field.DECL_TYPEDEF;
            modified = true;

            if (mDeclTypedef != null) {
                mDeclTypedef_builder = mDeclTypedef.mutate();
                mDeclTypedef = null;
            } else if (mDeclTypedef_builder == null) {
                mDeclTypedef_builder = net.morimekta.providence.model.TypedefType.builder();
            }
            return mDeclTypedef_builder;
        }

        /**
         * Gets the value for the contained decl_typedef.
         *
         * @return The field value
         */
        public net.morimekta.providence.model.TypedefType getDeclTypedef() {
            if (tUnionField != _Field.DECL_TYPEDEF) {
                return null;
            }

            if (mDeclTypedef_builder != null) {
                return mDeclTypedef_builder.build();
            }
            return mDeclTypedef;
        }

        /**
         * Sets the value of decl_message.
         *
         * @param value The new value
         * @return The builder
         */
        @javax.annotation.Nonnull
        public _Builder setDeclMessage(net.morimekta.providence.model.MessageType value) {
            if (value == null) {
                return clearDeclMessage();
            }

            tUnionField = _Field.DECL_MESSAGE;
            modified = true;
            mDeclMessage = value;
            mDeclMessage_builder = null;
            return this;
        }

        /**
         * Sets the value of decl_message.
         *
         * @param builder builder for the new value
         * @return The builder
         */
        @javax.annotation.Nonnull
        public _Builder setDeclMessage(net.morimekta.providence.model.MessageType._Builder builder) {
          return setDeclMessage(builder == null ? null : builder.build());
        }

        /**
         * Checks for presence of the decl_message field.
         *
         * @return True if decl_message has been set.
         */
        public boolean isSetDeclMessage() {
            return tUnionField == _Field.DECL_MESSAGE;
        }

        /**
         * Clears the decl_message field.
         *
         * @return The builder
         */
        @javax.annotation.Nonnull
        public _Builder clearDeclMessage() {
            if (tUnionField == _Field.DECL_MESSAGE) tUnionField = null;
            modified = true;
            mDeclMessage = null;
            mDeclMessage_builder = null;
            return this;
        }

        /**
         * Gets the builder for the contained decl_message.
         *
         * @return The field builder
         */
        @javax.annotation.Nonnull
        public net.morimekta.providence.model.MessageType._Builder mutableDeclMessage() {
            if (tUnionField != _Field.DECL_MESSAGE) {
                clearDeclMessage();
            }
            tUnionField = _Field.DECL_MESSAGE;
            modified = true;

            if (mDeclMessage != null) {
                mDeclMessage_builder = mDeclMessage.mutate();
                mDeclMessage = null;
            } else if (mDeclMessage_builder == null) {
                mDeclMessage_builder = net.morimekta.providence.model.MessageType.builder();
            }
            return mDeclMessage_builder;
        }

        /**
         * Gets the value for the contained decl_message.
         *
         * @return The field value
         */
        public net.morimekta.providence.model.MessageType getDeclMessage() {
            if (tUnionField != _Field.DECL_MESSAGE) {
                return null;
            }

            if (mDeclMessage_builder != null) {
                return mDeclMessage_builder.build();
            }
            return mDeclMessage;
        }

        /**
         * Sets the value of decl_service.
         *
         * @param value The new value
         * @return The builder
         */
        @javax.annotation.Nonnull
        public _Builder setDeclService(net.morimekta.providence.model.ServiceType value) {
            if (value == null) {
                return clearDeclService();
            }

            tUnionField = _Field.DECL_SERVICE;
            modified = true;
            mDeclService = value;
            mDeclService_builder = null;
            return this;
        }

        /**
         * Sets the value of decl_service.
         *
         * @param builder builder for the new value
         * @return The builder
         */
        @javax.annotation.Nonnull
        public _Builder setDeclService(net.morimekta.providence.model.ServiceType._Builder builder) {
          return setDeclService(builder == null ? null : builder.build());
        }

        /**
         * Checks for presence of the decl_service field.
         *
         * @return True if decl_service has been set.
         */
        public boolean isSetDeclService() {
            return tUnionField == _Field.DECL_SERVICE;
        }

        /**
         * Clears the decl_service field.
         *
         * @return The builder
         */
        @javax.annotation.Nonnull
        public _Builder clearDeclService() {
            if (tUnionField == _Field.DECL_SERVICE) tUnionField = null;
            modified = true;
            mDeclService = null;
            mDeclService_builder = null;
            return this;
        }

        /**
         * Gets the builder for the contained decl_service.
         *
         * @return The field builder
         */
        @javax.annotation.Nonnull
        public net.morimekta.providence.model.ServiceType._Builder mutableDeclService() {
            if (tUnionField != _Field.DECL_SERVICE) {
                clearDeclService();
            }
            tUnionField = _Field.DECL_SERVICE;
            modified = true;

            if (mDeclService != null) {
                mDeclService_builder = mDeclService.mutate();
                mDeclService = null;
            } else if (mDeclService_builder == null) {
                mDeclService_builder = net.morimekta.providence.model.ServiceType.builder();
            }
            return mDeclService_builder;
        }

        /**
         * Gets the value for the contained decl_service.
         *
         * @return The field value
         */
        public net.morimekta.providence.model.ServiceType getDeclService() {
            if (tUnionField != _Field.DECL_SERVICE) {
                return null;
            }

            if (mDeclService_builder != null) {
                return mDeclService_builder.build();
            }
            return mDeclService;
        }

        /**
         * Sets the value of decl_const.
         *
         * @param value The new value
         * @return The builder
         */
        @javax.annotation.Nonnull
        public _Builder setDeclConst(net.morimekta.providence.model.ConstType value) {
            if (value == null) {
                return clearDeclConst();
            }

            tUnionField = _Field.DECL_CONST;
            modified = true;
            mDeclConst = value;
            mDeclConst_builder = null;
            return this;
        }

        /**
         * Sets the value of decl_const.
         *
         * @param builder builder for the new value
         * @return The builder
         */
        @javax.annotation.Nonnull
        public _Builder setDeclConst(net.morimekta.providence.model.ConstType._Builder builder) {
          return setDeclConst(builder == null ? null : builder.build());
        }

        /**
         * Checks for presence of the decl_const field.
         *
         * @return True if decl_const has been set.
         */
        public boolean isSetDeclConst() {
            return tUnionField == _Field.DECL_CONST;
        }

        /**
         * Clears the decl_const field.
         *
         * @return The builder
         */
        @javax.annotation.Nonnull
        public _Builder clearDeclConst() {
            if (tUnionField == _Field.DECL_CONST) tUnionField = null;
            modified = true;
            mDeclConst = null;
            mDeclConst_builder = null;
            return this;
        }

        /**
         * Gets the builder for the contained decl_const.
         *
         * @return The field builder
         */
        @javax.annotation.Nonnull
        public net.morimekta.providence.model.ConstType._Builder mutableDeclConst() {
            if (tUnionField != _Field.DECL_CONST) {
                clearDeclConst();
            }
            tUnionField = _Field.DECL_CONST;
            modified = true;

            if (mDeclConst != null) {
                mDeclConst_builder = mDeclConst.mutate();
                mDeclConst = null;
            } else if (mDeclConst_builder == null) {
                mDeclConst_builder = net.morimekta.providence.model.ConstType.builder();
            }
            return mDeclConst_builder;
        }

        /**
         * Gets the value for the contained decl_const.
         *
         * @return The field value
         */
        public net.morimekta.providence.model.ConstType getDeclConst() {
            if (tUnionField != _Field.DECL_CONST) {
                return null;
            }

            if (mDeclConst_builder != null) {
                return mDeclConst_builder.build();
            }
            return mDeclConst;
        }

        /**
         * Checks if Declaration has been modified since the _Builder was created.
         *
         * @return True if Declaration has been modified.
         */
        public boolean isUnionModified() {
            return modified;
        }

        @Override
        public boolean equals(Object o) {
            if (o == this) return true;
            if (o == null || !o.getClass().equals(getClass())) return false;
            Declaration._Builder other = (Declaration._Builder) o;
            return java.util.Objects.equals(tUnionField, other.tUnionField) &&
                   java.util.Objects.equals(getDeclEnum(), other.getDeclEnum()) &&
                   java.util.Objects.equals(getDeclTypedef(), other.getDeclTypedef()) &&
                   java.util.Objects.equals(getDeclMessage(), other.getDeclMessage()) &&
                   java.util.Objects.equals(getDeclService(), other.getDeclService()) &&
                   java.util.Objects.equals(getDeclConst(), other.getDeclConst());
        }

        @Override
        public int hashCode() {
            return java.util.Objects.hash(
                    Declaration.class,
                    _Field.DECL_ENUM, getDeclEnum(),
                    _Field.DECL_TYPEDEF, getDeclTypedef(),
                    _Field.DECL_MESSAGE, getDeclMessage(),
                    _Field.DECL_SERVICE, getDeclService(),
                    _Field.DECL_CONST, getDeclConst());
        }

        @Override
        @SuppressWarnings("unchecked")
        public net.morimekta.providence.PMessageBuilder mutator(int key) {
            switch (key) {
                case 1: return mutableDeclEnum();
                case 2: return mutableDeclTypedef();
                case 3: return mutableDeclMessage();
                case 4: return mutableDeclService();
                case 5: return mutableDeclConst();
                default: throw new IllegalArgumentException("Not a message field ID: " + key);
            }
        }

        @javax.annotation.Nonnull
        @Override
        @SuppressWarnings("unchecked")
        public _Builder set(int key, Object value) {
            if (value == null) return clear(key);
            switch (key) {
                case 1: setDeclEnum((net.morimekta.providence.model.EnumType) value); break;
                case 2: setDeclTypedef((net.morimekta.providence.model.TypedefType) value); break;
                case 3: setDeclMessage((net.morimekta.providence.model.MessageType) value); break;
                case 4: setDeclService((net.morimekta.providence.model.ServiceType) value); break;
                case 5: setDeclConst((net.morimekta.providence.model.ConstType) value); break;
                default: break;
            }
            return this;
        }

        @Override
        public boolean isSet(int key) {
            switch (key) {
                case 1: return tUnionField == _Field.DECL_ENUM;
                case 2: return tUnionField == _Field.DECL_TYPEDEF;
                case 3: return tUnionField == _Field.DECL_MESSAGE;
                case 4: return tUnionField == _Field.DECL_SERVICE;
                case 5: return tUnionField == _Field.DECL_CONST;
                default: break;
            }
            return false;
        }

        @Override
        public boolean isModified(int key) {
            return modified;
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
                case 1: clearDeclEnum(); break;
                case 2: clearDeclTypedef(); break;
                case 3: clearDeclMessage(); break;
                case 4: clearDeclService(); break;
                case 5: clearDeclConst(); break;
                default: break;
            }
            return this;
        }

        @Override
        public boolean valid() {
            if (tUnionField == null) {
                return false;
            }

            switch (tUnionField) {
                case DECL_ENUM: return mDeclEnum != null || mDeclEnum_builder != null;
                case DECL_TYPEDEF: return mDeclTypedef != null || mDeclTypedef_builder != null;
                case DECL_MESSAGE: return mDeclMessage != null || mDeclMessage_builder != null;
                case DECL_SERVICE: return mDeclService != null || mDeclService_builder != null;
                case DECL_CONST: return mDeclConst != null || mDeclConst_builder != null;
                default: return true;
            }
        }

        @Override
        public void validate() {
            if (!valid()) {
                throw new java.lang.IllegalStateException("No union field set in providence_model.Declaration");
            }
        }

        @javax.annotation.Nonnull
        @Override
        public net.morimekta.providence.descriptor.PUnionDescriptor<Declaration,_Field> descriptor() {
            return kDescriptor;
        }

        @Override
        public void readBinary(net.morimekta.util.io.BigEndianBinaryReader reader, boolean strict) throws java.io.IOException {
            byte type = reader.expectByte();
            while (type != 0) {
                int field = reader.expectShort();
                switch (field) {
                    case 1: {
                        if (type == 12) {
                            mDeclEnum = net.morimekta.providence.serializer.binary.BinaryFormatUtils.readMessage(reader, net.morimekta.providence.model.EnumType.kDescriptor, strict);
                            tUnionField = _Field.DECL_ENUM;
                        } else {
                            throw new net.morimekta.providence.serializer.SerializerException("Wrong type " + net.morimekta.providence.serializer.binary.BinaryType.asString(type) + " for providence_model.Declaration.decl_enum, should be struct(12)");
                        }
                        break;
                    }
                    case 2: {
                        if (type == 12) {
                            mDeclTypedef = net.morimekta.providence.serializer.binary.BinaryFormatUtils.readMessage(reader, net.morimekta.providence.model.TypedefType.kDescriptor, strict);
                            tUnionField = _Field.DECL_TYPEDEF;
                        } else {
                            throw new net.morimekta.providence.serializer.SerializerException("Wrong type " + net.morimekta.providence.serializer.binary.BinaryType.asString(type) + " for providence_model.Declaration.decl_typedef, should be struct(12)");
                        }
                        break;
                    }
                    case 3: {
                        if (type == 12) {
                            mDeclMessage = net.morimekta.providence.serializer.binary.BinaryFormatUtils.readMessage(reader, net.morimekta.providence.model.MessageType.kDescriptor, strict);
                            tUnionField = _Field.DECL_MESSAGE;
                        } else {
                            throw new net.morimekta.providence.serializer.SerializerException("Wrong type " + net.morimekta.providence.serializer.binary.BinaryType.asString(type) + " for providence_model.Declaration.decl_message, should be struct(12)");
                        }
                        break;
                    }
                    case 4: {
                        if (type == 12) {
                            mDeclService = net.morimekta.providence.serializer.binary.BinaryFormatUtils.readMessage(reader, net.morimekta.providence.model.ServiceType.kDescriptor, strict);
                            tUnionField = _Field.DECL_SERVICE;
                        } else {
                            throw new net.morimekta.providence.serializer.SerializerException("Wrong type " + net.morimekta.providence.serializer.binary.BinaryType.asString(type) + " for providence_model.Declaration.decl_service, should be struct(12)");
                        }
                        break;
                    }
                    case 5: {
                        if (type == 12) {
                            mDeclConst = net.morimekta.providence.serializer.binary.BinaryFormatUtils.readMessage(reader, net.morimekta.providence.model.ConstType.kDescriptor, strict);
                            tUnionField = _Field.DECL_CONST;
                        } else {
                            throw new net.morimekta.providence.serializer.SerializerException("Wrong type " + net.morimekta.providence.serializer.binary.BinaryType.asString(type) + " for providence_model.Declaration.decl_const, should be struct(12)");
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
        public Declaration build() {
            return new Declaration(this);
        }
    }
}
