package net.morimekta.providence.model;

/**
 * ( &lt;enum&gt; | &lt;typedef&gt; | &lt;struct&gt; | &lt;service&gt; | &lt;const&gt; )
 */
@SuppressWarnings("unused")
public class Declaration
        implements net.morimekta.providence.PUnion<Declaration,Declaration._Field>,
                   Comparable<Declaration>,
                   java.io.Serializable,
                   net.morimekta.providence.serializer.rw.BinaryWriter {
    private final static long serialVersionUID = -6998763195276182553L;

    private final net.morimekta.providence.model.EnumType mDeclEnum;
    private final net.morimekta.providence.model.TypedefType mDeclTypedef;
    private final net.morimekta.providence.model.MessageType mDeclStruct;
    private final net.morimekta.providence.model.ServiceType mDeclService;
    private final net.morimekta.providence.model.ConstType mDeclConst;

    private final _Field tUnionField;

    private volatile int tHashCode;

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
    public static Declaration withDeclTypedef(net.morimekta.providence.model.TypedefType value) {
        return new _Builder().setDeclTypedef(value).build();
    }

    /**
     * @param value The union value
     * @return The created union.
     */
    public static Declaration withDeclStruct(net.morimekta.providence.model.MessageType value) {
        return new _Builder().setDeclStruct(value).build();
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
    public static Declaration withDeclConst(net.morimekta.providence.model.ConstType value) {
        return new _Builder().setDeclConst(value).build();
    }

    private Declaration(_Builder builder) {
        tUnionField = builder.tUnionField;

        mDeclEnum = tUnionField != _Field.DECL_ENUM
                ? null
                : builder.mDeclEnum_builder != null ? builder.mDeclEnum_builder.build() : builder.mDeclEnum;
        mDeclTypedef = tUnionField != _Field.DECL_TYPEDEF
                ? null
                : builder.mDeclTypedef_builder != null ? builder.mDeclTypedef_builder.build() : builder.mDeclTypedef;
        mDeclStruct = tUnionField != _Field.DECL_STRUCT
                ? null
                : builder.mDeclStruct_builder != null ? builder.mDeclStruct_builder.build() : builder.mDeclStruct;
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

    public boolean hasDeclTypedef() {
        return tUnionField == _Field.DECL_TYPEDEF && mDeclTypedef != null;
    }

    /**
     * @return The field value
     */
    public net.morimekta.providence.model.TypedefType getDeclTypedef() {
        return mDeclTypedef;
    }

    public boolean hasDeclStruct() {
        return tUnionField == _Field.DECL_STRUCT && mDeclStruct != null;
    }

    /**
     * @return The field value
     */
    public net.morimekta.providence.model.MessageType getDeclStruct() {
        return mDeclStruct;
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

    public boolean hasDeclConst() {
        return tUnionField == _Field.DECL_CONST && mDeclConst != null;
    }

    /**
     * @return The field value
     */
    public net.morimekta.providence.model.ConstType getDeclConst() {
        return mDeclConst;
    }

    @Override
    public boolean has(int key) {
        switch(key) {
            case 1: return hasDeclEnum();
            case 2: return hasDeclTypedef();
            case 3: return hasDeclStruct();
            case 4: return hasDeclService();
            case 5: return hasDeclConst();
            default: return false;
        }
    }

    @Override
    public int num(int key) {
        switch(key) {
            case 1: return hasDeclEnum() ? 1 : 0;
            case 2: return hasDeclTypedef() ? 1 : 0;
            case 3: return hasDeclStruct() ? 1 : 0;
            case 4: return hasDeclService() ? 1 : 0;
            case 5: return hasDeclConst() ? 1 : 0;
            default: return 0;
        }
    }

    @Override
    public Object get(int key) {
        switch(key) {
            case 1: return getDeclEnum();
            case 2: return getDeclTypedef();
            case 3: return getDeclStruct();
            case 4: return getDeclService();
            case 5: return getDeclConst();
            default: return null;
        }
    }

    @Override
    public _Field unionField() {
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
               java.util.Objects.equals(mDeclStruct, other.mDeclStruct) &&
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
                    _Field.DECL_STRUCT, mDeclStruct,
                    _Field.DECL_SERVICE, mDeclService,
                    _Field.DECL_CONST, mDeclConst);
        }
        return tHashCode;
    }

    @Override
    public String toString() {
        return "model.Declaration" + asString();
    }

    @Override
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
            case DECL_STRUCT: {
                out.append("decl_struct:")
                   .append(mDeclStruct.asString());
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
        int c = tUnionField.compareTo(other.tUnionField);
        if (c != 0) return c;

        switch (tUnionField) {
            case DECL_ENUM:
                return mDeclEnum.compareTo(other.mDeclEnum);
            case DECL_TYPEDEF:
                return mDeclTypedef.compareTo(other.mDeclTypedef);
            case DECL_STRUCT:
                return mDeclStruct.compareTo(other.mDeclStruct);
            case DECL_SERVICE:
                return mDeclService.compareTo(other.mDeclService);
            case DECL_CONST:
                return mDeclConst.compareTo(other.mDeclConst);
            default: return 0;
        }
    }

    @Override
    public int writeBinary(net.morimekta.util.io.BigEndianBinaryWriter writer) throws java.io.IOException {
        int length = 0;

        if (tUnionField != null) {
            switch (tUnionField) {
                case DECL_ENUM: {
                    length += writer.writeByte((byte) 12);
                    length += writer.writeShort((short) 1);
                    length += net.morimekta.providence.serializer.rw.BinaryFormatUtils.writeMessage(writer, mDeclEnum);
                    break;
                }
                case DECL_TYPEDEF: {
                    length += writer.writeByte((byte) 12);
                    length += writer.writeShort((short) 2);
                    length += net.morimekta.providence.serializer.rw.BinaryFormatUtils.writeMessage(writer, mDeclTypedef);
                    break;
                }
                case DECL_STRUCT: {
                    length += writer.writeByte((byte) 12);
                    length += writer.writeShort((short) 3);
                    length += net.morimekta.providence.serializer.rw.BinaryFormatUtils.writeMessage(writer, mDeclStruct);
                    break;
                }
                case DECL_SERVICE: {
                    length += writer.writeByte((byte) 12);
                    length += writer.writeShort((short) 4);
                    length += net.morimekta.providence.serializer.rw.BinaryFormatUtils.writeMessage(writer, mDeclService);
                    break;
                }
                case DECL_CONST: {
                    length += writer.writeByte((byte) 12);
                    length += writer.writeShort((short) 5);
                    length += net.morimekta.providence.serializer.rw.BinaryFormatUtils.writeMessage(writer, mDeclConst);
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
        DECL_ENUM(1, net.morimekta.providence.descriptor.PRequirement.DEFAULT, "decl_enum", net.morimekta.providence.model.EnumType.provider(), null),
        DECL_TYPEDEF(2, net.morimekta.providence.descriptor.PRequirement.DEFAULT, "decl_typedef", net.morimekta.providence.model.TypedefType.provider(), null),
        DECL_STRUCT(3, net.morimekta.providence.descriptor.PRequirement.DEFAULT, "decl_struct", net.morimekta.providence.model.MessageType.provider(), null),
        DECL_SERVICE(4, net.morimekta.providence.descriptor.PRequirement.DEFAULT, "decl_service", net.morimekta.providence.model.ServiceType.provider(), null),
        DECL_CONST(5, net.morimekta.providence.descriptor.PRequirement.DEFAULT, "decl_const", net.morimekta.providence.model.ConstType.provider(), null),
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
                case 1: return _Field.DECL_ENUM;
                case 2: return _Field.DECL_TYPEDEF;
                case 3: return _Field.DECL_STRUCT;
                case 4: return _Field.DECL_SERVICE;
                case 5: return _Field.DECL_CONST;
            }
            return null;
        }

        public static _Field forName(String name) {
            switch (name) {
                case "decl_enum": return _Field.DECL_ENUM;
                case "decl_typedef": return _Field.DECL_TYPEDEF;
                case "decl_struct": return _Field.DECL_STRUCT;
                case "decl_service": return _Field.DECL_SERVICE;
                case "decl_const": return _Field.DECL_CONST;
            }
            return null;
        }
    }

    public static net.morimekta.providence.descriptor.PUnionDescriptorProvider<Declaration,_Field> provider() {
        return new _Provider();
    }

    @Override
    public net.morimekta.providence.descriptor.PUnionDescriptor<Declaration,_Field> descriptor() {
        return kDescriptor;
    }

    public static final net.morimekta.providence.descriptor.PUnionDescriptor<Declaration,_Field> kDescriptor;

    private static class _Descriptor
            extends net.morimekta.providence.descriptor.PUnionDescriptor<Declaration,_Field> {
        public _Descriptor() {
            super("model", "Declaration", new _Factory(), false);
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

    private final static class _Provider extends net.morimekta.providence.descriptor.PUnionDescriptorProvider<Declaration,_Field> {
        @Override
        public net.morimekta.providence.descriptor.PUnionDescriptor<Declaration,_Field> descriptor() {
            return kDescriptor;
        }
    }

    private final static class _Factory
            extends net.morimekta.providence.PMessageBuilderFactory<Declaration,_Field> {
        @Override
        public _Builder builder() {
            return new _Builder();
        }
    }

    /**
     * Make a model.Declaration builder.
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
            implements net.morimekta.providence.serializer.rw.BinaryReader {
        private _Field tUnionField;

        private boolean modified;

        private net.morimekta.providence.model.EnumType mDeclEnum;
        private net.morimekta.providence.model.EnumType._Builder mDeclEnum_builder;
        private net.morimekta.providence.model.TypedefType mDeclTypedef;
        private net.morimekta.providence.model.TypedefType._Builder mDeclTypedef_builder;
        private net.morimekta.providence.model.MessageType mDeclStruct;
        private net.morimekta.providence.model.MessageType._Builder mDeclStruct_builder;
        private net.morimekta.providence.model.ServiceType mDeclService;
        private net.morimekta.providence.model.ServiceType._Builder mDeclService_builder;
        private net.morimekta.providence.model.ConstType mDeclConst;
        private net.morimekta.providence.model.ConstType._Builder mDeclConst_builder;

        /**
         * Make a model.Declaration builder.
         */
        public _Builder() {
            modified = false;
        }

        /**
         * Make a mutating builder off a base model.Declaration.
         *
         * @param base The base Declaration
         */
        public _Builder(Declaration base) {
            this();

            tUnionField = base.tUnionField;

            mDeclEnum = base.mDeclEnum;
            mDeclTypedef = base.mDeclTypedef;
            mDeclStruct = base.mDeclStruct;
            mDeclService = base.mDeclService;
            mDeclConst = base.mDeclConst;
        }

        @javax.annotation.Nonnull
        @Override
        public _Builder merge(Declaration from) {
            if (from.unionField() == null) {
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
                case DECL_STRUCT: {
                    if (tUnionField == _Field.DECL_STRUCT && mDeclStruct != null) {
                        mDeclStruct = mDeclStruct.mutate().merge(from.getDeclStruct()).build();
                    } else {
                        setDeclStruct(from.getDeclStruct());
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
            tUnionField = _Field.DECL_ENUM;
            modified = true;
            mDeclEnum_builder = null;
            mDeclEnum = value;
            return this;
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
         * Sets the value of decl_typedef.
         *
         * @param value The new value
         * @return The builder
         */
        @javax.annotation.Nonnull
        public _Builder setDeclTypedef(net.morimekta.providence.model.TypedefType value) {
            tUnionField = _Field.DECL_TYPEDEF;
            modified = true;
            mDeclTypedef_builder = null;
            mDeclTypedef = value;
            return this;
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
         * Sets the value of decl_struct.
         *
         * @param value The new value
         * @return The builder
         */
        @javax.annotation.Nonnull
        public _Builder setDeclStruct(net.morimekta.providence.model.MessageType value) {
            tUnionField = _Field.DECL_STRUCT;
            modified = true;
            mDeclStruct_builder = null;
            mDeclStruct = value;
            return this;
        }

        /**
         * Checks for presence of the decl_struct field.
         *
         * @return True if decl_struct has been set.
         */
        public boolean isSetDeclStruct() {
            return tUnionField == _Field.DECL_STRUCT;
        }

        /**
         * Clears the decl_struct field.
         *
         * @return The builder
         */
        @javax.annotation.Nonnull
        public _Builder clearDeclStruct() {
            if (tUnionField == _Field.DECL_STRUCT) tUnionField = null;
            modified = true;
            mDeclStruct = null;
            mDeclStruct_builder = null;
            return this;
        }

        /**
         * Gets the builder for the contained decl_struct.
         *
         * @return The field builder
         */
        public net.morimekta.providence.model.MessageType._Builder mutableDeclStruct() {
            if (tUnionField != _Field.DECL_STRUCT) {
                clearDeclStruct();
            }
            tUnionField = _Field.DECL_STRUCT;
            modified = true;

            if (mDeclStruct != null) {
                mDeclStruct_builder = mDeclStruct.mutate();
                mDeclStruct = null;
            } else if (mDeclStruct_builder == null) {
                mDeclStruct_builder = net.morimekta.providence.model.MessageType.builder();
            }
            return mDeclStruct_builder;
        }

        /**
         * Sets the value of decl_service.
         *
         * @param value The new value
         * @return The builder
         */
        @javax.annotation.Nonnull
        public _Builder setDeclService(net.morimekta.providence.model.ServiceType value) {
            tUnionField = _Field.DECL_SERVICE;
            modified = true;
            mDeclService_builder = null;
            mDeclService = value;
            return this;
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
         * Sets the value of decl_const.
         *
         * @param value The new value
         * @return The builder
         */
        @javax.annotation.Nonnull
        public _Builder setDeclConst(net.morimekta.providence.model.ConstType value) {
            tUnionField = _Field.DECL_CONST;
            modified = true;
            mDeclConst_builder = null;
            mDeclConst = value;
            return this;
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
                   java.util.Objects.equals(mDeclEnum, other.mDeclEnum) &&
                   java.util.Objects.equals(mDeclTypedef, other.mDeclTypedef) &&
                   java.util.Objects.equals(mDeclStruct, other.mDeclStruct) &&
                   java.util.Objects.equals(mDeclService, other.mDeclService) &&
                   java.util.Objects.equals(mDeclConst, other.mDeclConst);
        }

        @Override
        public int hashCode() {
            return java.util.Objects.hash(
                    Declaration.class,
                    _Field.DECL_ENUM, mDeclEnum,
                    _Field.DECL_TYPEDEF, mDeclTypedef,
                    _Field.DECL_STRUCT, mDeclStruct,
                    _Field.DECL_SERVICE, mDeclService,
                    _Field.DECL_CONST, mDeclConst);
        }

        @Override
        @SuppressWarnings("unchecked")
        public net.morimekta.providence.PMessageBuilder mutator(int key) {
            switch (key) {
                case 1: return mutableDeclEnum();
                case 2: return mutableDeclTypedef();
                case 3: return mutableDeclStruct();
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
                case 3: setDeclStruct((net.morimekta.providence.model.MessageType) value); break;
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
                case 3: return tUnionField == _Field.DECL_STRUCT;
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
                case 3: clearDeclStruct(); break;
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
                case DECL_STRUCT: return mDeclStruct != null || mDeclStruct_builder != null;
                case DECL_SERVICE: return mDeclService != null || mDeclService_builder != null;
                case DECL_CONST: return mDeclConst != null || mDeclConst_builder != null;
                default: return true;
            }
        }

        @Override
        public void validate() {
            if (!valid()) {
                throw new java.lang.IllegalStateException("No union field set in model.Declaration");
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
                            mDeclEnum = net.morimekta.providence.serializer.rw.BinaryFormatUtils.readMessage(reader, net.morimekta.providence.model.EnumType.kDescriptor, strict);
                            tUnionField = _Field.DECL_ENUM;
                        } else {
                            throw new net.morimekta.providence.serializer.SerializerException("Wrong type " + net.morimekta.providence.serializer.rw.BinaryType.asString(type) + " for model.Declaration.decl_enum, should be struct(12)");
                        }
                        break;
                    }
                    case 2: {
                        if (type == 12) {
                            mDeclTypedef = net.morimekta.providence.serializer.rw.BinaryFormatUtils.readMessage(reader, net.morimekta.providence.model.TypedefType.kDescriptor, strict);
                            tUnionField = _Field.DECL_TYPEDEF;
                        } else {
                            throw new net.morimekta.providence.serializer.SerializerException("Wrong type " + net.morimekta.providence.serializer.rw.BinaryType.asString(type) + " for model.Declaration.decl_typedef, should be struct(12)");
                        }
                        break;
                    }
                    case 3: {
                        if (type == 12) {
                            mDeclStruct = net.morimekta.providence.serializer.rw.BinaryFormatUtils.readMessage(reader, net.morimekta.providence.model.MessageType.kDescriptor, strict);
                            tUnionField = _Field.DECL_STRUCT;
                        } else {
                            throw new net.morimekta.providence.serializer.SerializerException("Wrong type " + net.morimekta.providence.serializer.rw.BinaryType.asString(type) + " for model.Declaration.decl_struct, should be struct(12)");
                        }
                        break;
                    }
                    case 4: {
                        if (type == 12) {
                            mDeclService = net.morimekta.providence.serializer.rw.BinaryFormatUtils.readMessage(reader, net.morimekta.providence.model.ServiceType.kDescriptor, strict);
                            tUnionField = _Field.DECL_SERVICE;
                        } else {
                            throw new net.morimekta.providence.serializer.SerializerException("Wrong type " + net.morimekta.providence.serializer.rw.BinaryType.asString(type) + " for model.Declaration.decl_service, should be struct(12)");
                        }
                        break;
                    }
                    case 5: {
                        if (type == 12) {
                            mDeclConst = net.morimekta.providence.serializer.rw.BinaryFormatUtils.readMessage(reader, net.morimekta.providence.model.ConstType.kDescriptor, strict);
                            tUnionField = _Field.DECL_CONST;
                        } else {
                            throw new net.morimekta.providence.serializer.SerializerException("Wrong type " + net.morimekta.providence.serializer.rw.BinaryType.asString(type) + " for model.Declaration.decl_const, should be struct(12)");
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
        public Declaration build() {
            return new Declaration(this);
        }
    }
}
