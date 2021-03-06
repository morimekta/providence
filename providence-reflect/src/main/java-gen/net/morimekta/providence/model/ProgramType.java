package net.morimekta.providence.model;

/**
 * &lt;namespace&gt;* &lt;include&gt;* &lt;declataion&gt;*
 */
@SuppressWarnings("unused")
@javax.annotation.Generated("providence-maven-plugin")
@javax.annotation.concurrent.Immutable
public class ProgramType
        implements net.morimekta.providence.PMessage<ProgramType,ProgramType._Field>,
                   Comparable<ProgramType>,
                   java.io.Serializable,
                   net.morimekta.providence.serializer.binary.BinaryWriter {
    private final static long serialVersionUID = 2746126011608457567L;

    private final static String kDefaultProgramName = "";
    private final static java.util.List<String> kDefaultIncludes = new net.morimekta.providence.descriptor.PList.DefaultBuilder<String>()
                .build();
    private final static java.util.Map<String,String> kDefaultNamespaces = new net.morimekta.providence.descriptor.PMap.DefaultBuilder<String,String>()
                .build();
    private final static java.util.List<net.morimekta.providence.model.Declaration> kDefaultDecl = new net.morimekta.providence.descriptor.PList.DefaultBuilder<net.morimekta.providence.model.Declaration>()
                .build();

    private final transient String mDocumentation;
    private final transient String mProgramName;
    private final transient java.util.List<String> mIncludes;
    private final transient java.util.Map<String,String> mNamespaces;
    private final transient java.util.List<net.morimekta.providence.model.Declaration> mDecl;

    private volatile transient int tHashCode;

    // Transient object used during java deserialization.
    private transient ProgramType tSerializeInstance;

    private ProgramType(_Builder builder) {
        mDocumentation = builder.mDocumentation;
        if (builder.isSetProgramName()) {
            mProgramName = builder.mProgramName;
        } else {
            mProgramName = kDefaultProgramName;
        }
        if (builder.isSetIncludes()) {
            mIncludes = com.google.common.collect.ImmutableList.copyOf(builder.mIncludes);
        } else {
            mIncludes = null;
        }
        if (builder.isSetNamespaces()) {
            mNamespaces = com.google.common.collect.ImmutableSortedMap.copyOf(builder.mNamespaces);
        } else {
            mNamespaces = null;
        }
        if (builder.isSetDecl()) {
            mDecl = com.google.common.collect.ImmutableList.copyOf(builder.mDecl);
        } else {
            mDecl = null;
        }
    }

    public boolean hasDocumentation() {
        return mDocumentation != null;
    }

    /**
     * Program documentation must come before the first statement of the header.
     *
     * @return The <code>documentation</code> value
     */
    public String getDocumentation() {
        return mDocumentation;
    }

    /**
     * Program documentation must come before the first statement of the header.
     *
     * @return Optional of the <code>documentation</code> field value.
     */
    @javax.annotation.Nonnull
    public java.util.Optional<String> optionalDocumentation() {
        return java.util.Optional.ofNullable(mDocumentation);
    }

    public boolean hasProgramName() {
        return true;
    }

    /**
     * The program name, deducted from the .thrift IDL file name.
     *
     * @return The <code>program_name</code> value
     */
    @javax.annotation.Nonnull
    public String getProgramName() {
        return mProgramName;
    }

    public int numIncludes() {
        return mIncludes != null ? mIncludes.size() : 0;
    }

    public boolean hasIncludes() {
        return mIncludes != null;
    }

    /**
     * List of included thrift files. Same as from the actual thrift file.
     * <p>
     * include &quot;&lt;program&gt;.thrift&quot;
     *
     * @return The <code>includes</code> value
     */
    public java.util.List<String> getIncludes() {
        return hasIncludes() ? mIncludes : kDefaultIncludes;
    }

    /**
     * List of included thrift files. Same as from the actual thrift file.
     * <p>
     * include &quot;&lt;program&gt;.thrift&quot;
     *
     * @return Optional of the <code>includes</code> field value.
     */
    @javax.annotation.Nonnull
    public java.util.Optional<java.util.List<String>> optionalIncludes() {
        return java.util.Optional.ofNullable(mIncludes);
    }

    public int numNamespaces() {
        return mNamespaces != null ? mNamespaces.size() : 0;
    }

    public boolean hasNamespaces() {
        return mNamespaces != null;
    }

    /**
     * Map of language to laguage dependent namespace identifier.
     * <p>
     * namespace &lt;key&gt; &lt;value&gt;
     *
     * @return The <code>namespaces</code> value
     */
    public java.util.Map<String,String> getNamespaces() {
        return hasNamespaces() ? mNamespaces : kDefaultNamespaces;
    }

    /**
     * Map of language to laguage dependent namespace identifier.
     * <p>
     * namespace &lt;key&gt; &lt;value&gt;
     *
     * @return Optional of the <code>namespaces</code> field value.
     */
    @javax.annotation.Nonnull
    public java.util.Optional<java.util.Map<String,String>> optionalNamespaces() {
        return java.util.Optional.ofNullable(mNamespaces);
    }

    public int numDecl() {
        return mDecl != null ? mDecl.size() : 0;
    }

    public boolean hasDecl() {
        return mDecl != null;
    }

    /**
     * List of declarations in the program file. Same order as in the thrift file.
     *
     * @return The <code>decl</code> value
     */
    public java.util.List<net.morimekta.providence.model.Declaration> getDecl() {
        return hasDecl() ? mDecl : kDefaultDecl;
    }

    /**
     * List of declarations in the program file. Same order as in the thrift file.
     *
     * @return Optional of the <code>decl</code> field value.
     */
    @javax.annotation.Nonnull
    public java.util.Optional<java.util.List<net.morimekta.providence.model.Declaration>> optionalDecl() {
        return java.util.Optional.ofNullable(mDecl);
    }

    @Override
    public boolean has(int key) {
        switch(key) {
            case 1: return mDocumentation != null;
            case 2: return true;
            case 3: return mIncludes != null;
            case 4: return mNamespaces != null;
            case 5: return mDecl != null;
            default: return false;
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T get(int key) {
        switch(key) {
            case 1: return (T) mDocumentation;
            case 2: return (T) mProgramName;
            case 3: return (T) mIncludes;
            case 4: return (T) mNamespaces;
            case 5: return (T) mDecl;
            default: return null;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        if (o == null || !o.getClass().equals(getClass())) return false;
        ProgramType other = (ProgramType) o;
        return java.util.Objects.equals(mDocumentation, other.mDocumentation) &&
               java.util.Objects.equals(mProgramName, other.mProgramName) &&
               java.util.Objects.equals(mIncludes, other.mIncludes) &&
               java.util.Objects.equals(mNamespaces, other.mNamespaces) &&
               java.util.Objects.equals(mDecl, other.mDecl);
    }

    @Override
    public int hashCode() {
        if (tHashCode == 0) {
            tHashCode = java.util.Objects.hash(
                    ProgramType.class,
                    _Field.DOCUMENTATION, mDocumentation,
                    _Field.PROGRAM_NAME, mProgramName,
                    _Field.INCLUDES, mIncludes,
                    _Field.NAMESPACES, mNamespaces,
                    _Field.DECL, mDecl);
        }
        return tHashCode;
    }

    @Override
    public String toString() {
        return "pmodel.ProgramType" + asString();
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
        out.append("program_name:")
           .append('\"')
           .append(net.morimekta.util.Strings.escape(mProgramName))
           .append('\"');
        if (hasIncludes()) {
            out.append(',');
            out.append("includes:")
               .append(net.morimekta.util.Strings.asString(mIncludes));
        }
        if (hasNamespaces()) {
            out.append(',');
            out.append("namespaces:")
               .append(net.morimekta.util.Strings.asString(mNamespaces));
        }
        if (hasDecl()) {
            out.append(',');
            out.append("decl:")
               .append(net.morimekta.util.Strings.asString(mDecl));
        }
        out.append('}');
        return out.toString();
    }

    @Override
    public int compareTo(ProgramType other) {
        int c;

        c = Boolean.compare(mDocumentation != null, other.mDocumentation != null);
        if (c != 0) return c;
        if (mDocumentation != null) {
            c = mDocumentation.compareTo(other.mDocumentation);
            if (c != 0) return c;
        }

        c = mProgramName.compareTo(other.mProgramName);
        if (c != 0) return c;

        c = Boolean.compare(mIncludes != null, other.mIncludes != null);
        if (c != 0) return c;
        if (mIncludes != null) {
            c = Integer.compare(mIncludes.hashCode(), other.mIncludes.hashCode());
            if (c != 0) return c;
        }

        c = Boolean.compare(mNamespaces != null, other.mNamespaces != null);
        if (c != 0) return c;
        if (mNamespaces != null) {
            c = Integer.compare(mNamespaces.hashCode(), other.mNamespaces.hashCode());
            if (c != 0) return c;
        }

        c = Boolean.compare(mDecl != null, other.mDecl != null);
        if (c != 0) return c;
        if (mDecl != null) {
            c = Integer.compare(mDecl.hashCode(), other.mDecl.hashCode());
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
        net.morimekta.util.Binary tmp_2 = net.morimekta.util.Binary.wrap(mProgramName.getBytes(java.nio.charset.StandardCharsets.UTF_8));
        length += writer.writeUInt32(tmp_2.length());
        length += writer.writeBinary(tmp_2);

        if (hasIncludes()) {
            length += writer.writeByte((byte) 15);
            length += writer.writeShort((short) 3);
            length += writer.writeByte((byte) 11);
            length += writer.writeUInt32(mIncludes.size());
            for (String entry_3 : mIncludes) {
                net.morimekta.util.Binary tmp_4 = net.morimekta.util.Binary.wrap(entry_3.getBytes(java.nio.charset.StandardCharsets.UTF_8));
                length += writer.writeUInt32(tmp_4.length());
                length += writer.writeBinary(tmp_4);
            }
        }

        if (hasNamespaces()) {
            length += writer.writeByte((byte) 13);
            length += writer.writeShort((short) 4);
            length += writer.writeByte((byte) 11);
            length += writer.writeByte((byte) 11);
            length += writer.writeUInt32(mNamespaces.size());
            for (java.util.Map.Entry<String,String> entry_5 : mNamespaces.entrySet()) {
                net.morimekta.util.Binary tmp_6 = net.morimekta.util.Binary.wrap(entry_5.getKey().getBytes(java.nio.charset.StandardCharsets.UTF_8));
                length += writer.writeUInt32(tmp_6.length());
                length += writer.writeBinary(tmp_6);
                net.morimekta.util.Binary tmp_7 = net.morimekta.util.Binary.wrap(entry_5.getValue().getBytes(java.nio.charset.StandardCharsets.UTF_8));
                length += writer.writeUInt32(tmp_7.length());
                length += writer.writeBinary(tmp_7);
            }
        }

        if (hasDecl()) {
            length += writer.writeByte((byte) 15);
            length += writer.writeShort((short) 5);
            length += writer.writeByte((byte) 12);
            length += writer.writeUInt32(mDecl.size());
            for (net.morimekta.providence.model.Declaration entry_8 : mDecl) {
                length += net.morimekta.providence.serializer.binary.BinaryFormatUtils.writeMessage(writer, entry_8);
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
        DOCUMENTATION(1, net.morimekta.providence.descriptor.PRequirement.OPTIONAL, "documentation", net.morimekta.providence.descriptor.PPrimitive.STRING.provider(), null),
        PROGRAM_NAME(2, net.morimekta.providence.descriptor.PRequirement.REQUIRED, "program_name", net.morimekta.providence.descriptor.PPrimitive.STRING.provider(), null),
        INCLUDES(3, net.morimekta.providence.descriptor.PRequirement.OPTIONAL, "includes", net.morimekta.providence.descriptor.PList.provider(net.morimekta.providence.descriptor.PPrimitive.STRING.provider()), new net.morimekta.providence.descriptor.PDefaultValueProvider<>(kDefaultIncludes)),
        NAMESPACES(4, net.morimekta.providence.descriptor.PRequirement.OPTIONAL, "namespaces", net.morimekta.providence.descriptor.PMap.sortedProvider(net.morimekta.providence.descriptor.PPrimitive.STRING.provider(),net.morimekta.providence.descriptor.PPrimitive.STRING.provider()), new net.morimekta.providence.descriptor.PDefaultValueProvider<>(kDefaultNamespaces)),
        DECL(5, net.morimekta.providence.descriptor.PRequirement.OPTIONAL, "decl", net.morimekta.providence.descriptor.PList.provider(net.morimekta.providence.model.Declaration.provider()), new net.morimekta.providence.descriptor.PDefaultValueProvider<>(kDefaultDecl)),
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
                case 2: return _Field.PROGRAM_NAME;
                case 3: return _Field.INCLUDES;
                case 4: return _Field.NAMESPACES;
                case 5: return _Field.DECL;
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
                case "program_name": return _Field.PROGRAM_NAME;
                case "includes": return _Field.INCLUDES;
                case "namespaces": return _Field.NAMESPACES;
                case "decl": return _Field.DECL;
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
                throw new IllegalArgumentException("No such field id " + id + " in pmodel.ProgramType");
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
                throw new IllegalArgumentException("No such field \"" + name + "\" in pmodel.ProgramType");
            }
            return field;
        }

    }

    @javax.annotation.Nonnull
    public static net.morimekta.providence.descriptor.PStructDescriptorProvider<ProgramType,_Field> provider() {
        return new _Provider();
    }

    @Override
    @javax.annotation.Nonnull
    public net.morimekta.providence.descriptor.PStructDescriptor<ProgramType,_Field> descriptor() {
        return kDescriptor;
    }

    public static final net.morimekta.providence.descriptor.PStructDescriptor<ProgramType,_Field> kDescriptor;

    private static class _Descriptor
            extends net.morimekta.providence.descriptor.PStructDescriptor<ProgramType,_Field> {
        public _Descriptor() {
            super("pmodel", "ProgramType", _Builder::new, false);
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

    private final static class _Provider extends net.morimekta.providence.descriptor.PStructDescriptorProvider<ProgramType,_Field> {
        @Override
        public net.morimekta.providence.descriptor.PStructDescriptor<ProgramType,_Field> descriptor() {
            return kDescriptor;
        }
    }

    /**
     * Make a <code>pmodel.ProgramType</code> builder.
     * @return The builder instance.
     */
    public static _Builder builder() {
        return new _Builder();
    }

    /**
     * &lt;namespace&gt;* &lt;include&gt;* &lt;declataion&gt;*
     */
    public static class _Builder
            extends net.morimekta.providence.PMessageBuilder<ProgramType,_Field>
            implements net.morimekta.providence.serializer.binary.BinaryReader {
        private java.util.BitSet optionals;
        private java.util.BitSet modified;

        private String mDocumentation;
        private String mProgramName;
        private java.util.List<String> mIncludes;
        private java.util.Map<String,String> mNamespaces;
        private java.util.List<net.morimekta.providence.model.Declaration> mDecl;

        /**
         * Make a pmodel.ProgramType builder instance.
         */
        public _Builder() {
            optionals = new java.util.BitSet(5);
            modified = new java.util.BitSet(5);
            mProgramName = kDefaultProgramName;
        }

        /**
         * Make a mutating builder off a base pmodel.ProgramType.
         *
         * @param base The base ProgramType
         */
        public _Builder(ProgramType base) {
            this();

            if (base.hasDocumentation()) {
                optionals.set(0);
                mDocumentation = base.mDocumentation;
            }
            optionals.set(1);
            mProgramName = base.mProgramName;
            if (base.hasIncludes()) {
                optionals.set(2);
                mIncludes = base.mIncludes;
            }
            if (base.hasNamespaces()) {
                optionals.set(3);
                mNamespaces = base.mNamespaces;
            }
            if (base.hasDecl()) {
                optionals.set(4);
                mDecl = base.mDecl;
            }
        }

        @javax.annotation.Nonnull
        @Override
        public _Builder merge(ProgramType from) {
            if (from.hasDocumentation()) {
                optionals.set(0);
                modified.set(0);
                mDocumentation = from.getDocumentation();
            }

            optionals.set(1);
            modified.set(1);
            mProgramName = from.getProgramName();

            if (from.hasIncludes()) {
                optionals.set(2);
                modified.set(2);
                mIncludes = from.getIncludes();
            }

            if (from.hasNamespaces()) {
                optionals.set(3);
                modified.set(3);
                mutableNamespaces().putAll(from.getNamespaces());
            }

            if (from.hasDecl()) {
                optionals.set(4);
                modified.set(4);
                mDecl = from.getDecl();
            }
            return this;
        }

        /**
         * Set the <code>documentation</code> field value.
         * <p>
         * Program documentation must come before the first statement of the header.
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
         * Program documentation must come before the first statement of the header.
         *
         * @return The <code>documentation</code> field value
         */
        public String getDocumentation() {
            return mDocumentation;
        }

        /**
         * Set the <code>program_name</code> field value.
         * <p>
         * The program name, deducted from the .thrift IDL file name.
         *
         * @param value The new value
         * @return The builder
         */
        @javax.annotation.Nonnull
        public _Builder setProgramName(String value) {
            if (value == null) {
                return clearProgramName();
            }

            optionals.set(1);
            modified.set(1);
            mProgramName = value;
            return this;
        }

        /**
         * Checks for presence of the <code>program_name</code> field.
         *
         * @return True if program_name has been set.
         */
        public boolean isSetProgramName() {
            return optionals.get(1);
        }

        /**
         * Checks if the <code>program_name</code> field has been modified since the
         * builder was created.
         *
         * @return True if program_name has been modified.
         */
        public boolean isModifiedProgramName() {
            return modified.get(1);
        }

        /**
         * Clear the <code>program_name</code> field.
         *
         * @return The builder
         */
        @javax.annotation.Nonnull
        public _Builder clearProgramName() {
            optionals.clear(1);
            modified.set(1);
            mProgramName = kDefaultProgramName;
            return this;
        }

        /**
         * The program name, deducted from the .thrift IDL file name.
         *
         * @return The <code>program_name</code> field value
         */
        public String getProgramName() {
            return mProgramName;
        }

        /**
         * Set the <code>includes</code> field value.
         * <p>
         * List of included thrift files. Same as from the actual thrift file.
         * <p>
         * include &quot;&lt;program&gt;.thrift&quot;
         *
         * @param value The new value
         * @return The builder
         */
        @javax.annotation.Nonnull
        public _Builder setIncludes(java.util.Collection<String> value) {
            if (value == null) {
                return clearIncludes();
            }

            optionals.set(2);
            modified.set(2);
            mIncludes = com.google.common.collect.ImmutableList.copyOf(value);
            return this;
        }

        /**
         * Adds entries to the <code>includes</code> list.
         * <p>
         * List of included thrift files. Same as from the actual thrift file.
         * <p>
         * include &quot;&lt;program&gt;.thrift&quot;
         *
         * @param values The added value
         * @return The builder
         */
        @javax.annotation.Nonnull
        public _Builder addToIncludes(String... values) {
            optionals.set(2);
            modified.set(2);
            java.util.List<String> _container = mutableIncludes();
            for (String item : values) {
                _container.add(item);
            }
            return this;
        }

        /**
         * Checks for presence of the <code>includes</code> field.
         *
         * @return True if includes has been set.
         */
        public boolean isSetIncludes() {
            return optionals.get(2);
        }

        /**
         * Checks if the <code>includes</code> field has been modified since the
         * builder was created.
         *
         * @return True if includes has been modified.
         */
        public boolean isModifiedIncludes() {
            return modified.get(2);
        }

        /**
         * Clear the <code>includes</code> field.
         *
         * @return The builder
         */
        @javax.annotation.Nonnull
        public _Builder clearIncludes() {
            optionals.clear(2);
            modified.set(2);
            mIncludes = null;
            return this;
        }

        /**
         * Get the builder for the contained <code>includes</code> message field.
         * <p>
         * List of included thrift files. Same as from the actual thrift file.
         * <p>
         * include &quot;&lt;program&gt;.thrift&quot;
         *
         * @return The field message builder
         */
        @javax.annotation.Nonnull
        /**
         * List of included thrift files. Same as from the actual thrift file.
         * <p>
         * include &quot;&lt;program&gt;.thrift&quot;
         *
         * @return The mutable <code>includes</code> container
         */
        public java.util.List<String> mutableIncludes() {
            optionals.set(2);
            modified.set(2);

            if (mIncludes == null) {
                mIncludes = new java.util.ArrayList<>();
            } else if (!(mIncludes instanceof java.util.ArrayList)) {
                mIncludes = new java.util.ArrayList<>(mIncludes);
            }
            return mIncludes;
        }

        /**
         * Set the <code>namespaces</code> field value.
         * <p>
         * Map of language to laguage dependent namespace identifier.
         * <p>
         * namespace &lt;key&gt; &lt;value&gt;
         *
         * @param value The new value
         * @return The builder
         */
        @javax.annotation.Nonnull
        public _Builder setNamespaces(java.util.Map<String,String> value) {
            if (value == null) {
                return clearNamespaces();
            }

            optionals.set(3);
            modified.set(3);
            mNamespaces = com.google.common.collect.ImmutableSortedMap.copyOf(value);
            return this;
        }

        /**
         * Adds a mapping to the <code>namespaces</code> map.
         * <p>
         * Map of language to laguage dependent namespace identifier.
         * <p>
         * namespace &lt;key&gt; &lt;value&gt;
         *
         * @param key The inserted key
         * @param value The inserted value
         * @return The builder
         */
        @javax.annotation.Nonnull
        public _Builder putInNamespaces(String key, String value) {
            optionals.set(3);
            modified.set(3);
            mutableNamespaces().put(key, value);
            return this;
        }

        /**
         * Checks for presence of the <code>namespaces</code> field.
         *
         * @return True if namespaces has been set.
         */
        public boolean isSetNamespaces() {
            return optionals.get(3);
        }

        /**
         * Checks if the <code>namespaces</code> field has been modified since the
         * builder was created.
         *
         * @return True if namespaces has been modified.
         */
        public boolean isModifiedNamespaces() {
            return modified.get(3);
        }

        /**
         * Clear the <code>namespaces</code> field.
         *
         * @return The builder
         */
        @javax.annotation.Nonnull
        public _Builder clearNamespaces() {
            optionals.clear(3);
            modified.set(3);
            mNamespaces = null;
            return this;
        }

        /**
         * Get the builder for the contained <code>namespaces</code> message field.
         * <p>
         * Map of language to laguage dependent namespace identifier.
         * <p>
         * namespace &lt;key&gt; &lt;value&gt;
         *
         * @return The field message builder
         */
        @javax.annotation.Nonnull
        /**
         * Map of language to laguage dependent namespace identifier.
         * <p>
         * namespace &lt;key&gt; &lt;value&gt;
         *
         * @return The mutable <code>namespaces</code> container
         */
        public java.util.Map<String,String> mutableNamespaces() {
            optionals.set(3);
            modified.set(3);

            if (mNamespaces == null) {
                mNamespaces = new java.util.TreeMap<>();
            } else if (!(mNamespaces instanceof java.util.TreeMap)) {
                mNamespaces = new java.util.TreeMap<>(mNamespaces);
            }
            return mNamespaces;
        }

        /**
         * Set the <code>decl</code> field value.
         * <p>
         * List of declarations in the program file. Same order as in the thrift file.
         *
         * @param value The new value
         * @return The builder
         */
        @javax.annotation.Nonnull
        public _Builder setDecl(java.util.Collection<net.morimekta.providence.model.Declaration> value) {
            if (value == null) {
                return clearDecl();
            }

            optionals.set(4);
            modified.set(4);
            mDecl = com.google.common.collect.ImmutableList.copyOf(value);
            return this;
        }

        /**
         * Adds entries to the <code>decl</code> list.
         * <p>
         * List of declarations in the program file. Same order as in the thrift file.
         *
         * @param values The added value
         * @return The builder
         */
        @javax.annotation.Nonnull
        public _Builder addToDecl(net.morimekta.providence.model.Declaration... values) {
            optionals.set(4);
            modified.set(4);
            java.util.List<net.morimekta.providence.model.Declaration> _container = mutableDecl();
            for (net.morimekta.providence.model.Declaration item : values) {
                _container.add(item);
            }
            return this;
        }

        /**
         * Checks for presence of the <code>decl</code> field.
         *
         * @return True if decl has been set.
         */
        public boolean isSetDecl() {
            return optionals.get(4);
        }

        /**
         * Checks if the <code>decl</code> field has been modified since the
         * builder was created.
         *
         * @return True if decl has been modified.
         */
        public boolean isModifiedDecl() {
            return modified.get(4);
        }

        /**
         * Clear the <code>decl</code> field.
         *
         * @return The builder
         */
        @javax.annotation.Nonnull
        public _Builder clearDecl() {
            optionals.clear(4);
            modified.set(4);
            mDecl = null;
            return this;
        }

        /**
         * Get the builder for the contained <code>decl</code> message field.
         * <p>
         * List of declarations in the program file. Same order as in the thrift file.
         *
         * @return The field message builder
         */
        @javax.annotation.Nonnull
        /**
         * List of declarations in the program file. Same order as in the thrift file.
         *
         * @return The mutable <code>decl</code> container
         */
        public java.util.List<net.morimekta.providence.model.Declaration> mutableDecl() {
            optionals.set(4);
            modified.set(4);

            if (mDecl == null) {
                mDecl = new java.util.ArrayList<>();
            } else if (!(mDecl instanceof java.util.ArrayList)) {
                mDecl = new java.util.ArrayList<>(mDecl);
            }
            return mDecl;
        }

        @Override
        public boolean equals(Object o) {
            if (o == this) return true;
            if (o == null || !o.getClass().equals(getClass())) return false;
            ProgramType._Builder other = (ProgramType._Builder) o;
            return java.util.Objects.equals(optionals, other.optionals) &&
                   java.util.Objects.equals(mDocumentation, other.mDocumentation) &&
                   java.util.Objects.equals(mProgramName, other.mProgramName) &&
                   java.util.Objects.equals(mIncludes, other.mIncludes) &&
                   java.util.Objects.equals(mNamespaces, other.mNamespaces) &&
                   java.util.Objects.equals(mDecl, other.mDecl);
        }

        @Override
        public int hashCode() {
            return java.util.Objects.hash(
                    ProgramType.class, optionals,
                    _Field.DOCUMENTATION, mDocumentation,
                    _Field.PROGRAM_NAME, mProgramName,
                    _Field.INCLUDES, mIncludes,
                    _Field.NAMESPACES, mNamespaces,
                    _Field.DECL, mDecl);
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
                case 2: setProgramName((String) value); break;
                case 3: setIncludes((java.util.List<String>) value); break;
                case 4: setNamespaces((java.util.Map<String,String>) value); break;
                case 5: setDecl((java.util.List<net.morimekta.providence.model.Declaration>) value); break;
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
                case 3: addToIncludes((String) value); break;
                case 5: addToDecl((net.morimekta.providence.model.Declaration) value); break;
                default: break;
            }
            return this;
        }

        @javax.annotation.Nonnull
        @Override
        public _Builder clear(int key) {
            switch (key) {
                case 1: clearDocumentation(); break;
                case 2: clearProgramName(); break;
                case 3: clearIncludes(); break;
                case 4: clearNamespaces(); break;
                case 5: clearDecl(); break;
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
                    missing.add("program_name");
                }

                throw new java.lang.IllegalStateException(
                        "Missing required fields " +
                        String.join(",", missing) +
                        " in message pmodel.ProgramType");
            }
        }

        @javax.annotation.Nonnull
        @Override
        public net.morimekta.providence.descriptor.PStructDescriptor<ProgramType,_Field> descriptor() {
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
                            throw new net.morimekta.providence.serializer.SerializerException("Wrong type " + net.morimekta.providence.serializer.binary.BinaryType.asString(type) + " for pmodel.ProgramType.documentation, should be struct(12)");
                        }
                        break;
                    }
                    case 2: {
                        if (type == 11) {
                            int len_2 = reader.expectUInt32();
                            mProgramName = new String(reader.expectBytes(len_2), java.nio.charset.StandardCharsets.UTF_8);
                            optionals.set(1);
                        } else {
                            throw new net.morimekta.providence.serializer.SerializerException("Wrong type " + net.morimekta.providence.serializer.binary.BinaryType.asString(type) + " for pmodel.ProgramType.program_name, should be struct(12)");
                        }
                        break;
                    }
                    case 3: {
                        if (type == 15) {
                            net.morimekta.providence.descriptor.PList.DefaultBuilder<String> b_3 = new net.morimekta.providence.descriptor.PList.DefaultBuilder<>();
                            byte t_5 = reader.expectByte();
                            if (t_5 == 11) {
                                final int len_4 = reader.expectUInt32();
                                for (int i_6 = 0; i_6 < len_4; ++i_6) {
                                    int len_8 = reader.expectUInt32();
                                    String key_7 = new String(reader.expectBytes(len_8), java.nio.charset.StandardCharsets.UTF_8);
                                    b_3.add(key_7);
                                }
                                mIncludes = b_3.build();
                            } else {
                                throw new net.morimekta.providence.serializer.SerializerException("Wrong item type " + net.morimekta.providence.serializer.binary.BinaryType.asString(t_5) + " for pmodel.ProgramType.includes, should be string(11)");
                            }
                            optionals.set(2);
                        } else {
                            throw new net.morimekta.providence.serializer.SerializerException("Wrong type " + net.morimekta.providence.serializer.binary.BinaryType.asString(type) + " for pmodel.ProgramType.includes, should be struct(12)");
                        }
                        break;
                    }
                    case 4: {
                        if (type == 13) {
                            net.morimekta.providence.descriptor.PMap.SortedBuilder<String,String> b_9 = new net.morimekta.providence.descriptor.PMap.SortedBuilder<>();
                            byte t_11 = reader.expectByte();
                            byte t_12 = reader.expectByte();
                            if (t_11 == 11 && t_12 == 11) {
                                final int len_10 = reader.expectUInt32();
                                for (int i_13 = 0; i_13 < len_10; ++i_13) {
                                    int len_16 = reader.expectUInt32();
                                    String key_14 = new String(reader.expectBytes(len_16), java.nio.charset.StandardCharsets.UTF_8);
                                    int len_17 = reader.expectUInt32();
                                    String val_15 = new String(reader.expectBytes(len_17), java.nio.charset.StandardCharsets.UTF_8);
                                    b_9.put(key_14, val_15);
                                }
                                mNamespaces = b_9.build();
                            } else {
                                throw new net.morimekta.providence.serializer.SerializerException(
                                        "Wrong key type " + net.morimekta.providence.serializer.binary.BinaryType.asString(t_11) +
                                        " or value type " + net.morimekta.providence.serializer.binary.BinaryType.asString(t_12) +
                                        " for pmodel.ProgramType.namespaces, should be string(11) and string(11)");
                            }
                            optionals.set(3);
                        } else {
                            throw new net.morimekta.providence.serializer.SerializerException("Wrong type " + net.morimekta.providence.serializer.binary.BinaryType.asString(type) + " for pmodel.ProgramType.namespaces, should be struct(12)");
                        }
                        break;
                    }
                    case 5: {
                        if (type == 15) {
                            net.morimekta.providence.descriptor.PList.DefaultBuilder<net.morimekta.providence.model.Declaration> b_18 = new net.morimekta.providence.descriptor.PList.DefaultBuilder<>();
                            byte t_20 = reader.expectByte();
                            if (t_20 == 12) {
                                final int len_19 = reader.expectUInt32();
                                for (int i_21 = 0; i_21 < len_19; ++i_21) {
                                    net.morimekta.providence.model.Declaration key_22 = net.morimekta.providence.serializer.binary.BinaryFormatUtils.readMessage(reader, net.morimekta.providence.model.Declaration.kDescriptor, strict);
                                    b_18.add(key_22);
                                }
                                mDecl = b_18.build();
                            } else {
                                throw new net.morimekta.providence.serializer.SerializerException("Wrong item type " + net.morimekta.providence.serializer.binary.BinaryType.asString(t_20) + " for pmodel.ProgramType.decl, should be struct(12)");
                            }
                            optionals.set(4);
                        } else {
                            throw new net.morimekta.providence.serializer.SerializerException("Wrong type " + net.morimekta.providence.serializer.binary.BinaryType.asString(type) + " for pmodel.ProgramType.decl, should be struct(12)");
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
        public ProgramType build() {
            return new ProgramType(this);
        }
    }
}
