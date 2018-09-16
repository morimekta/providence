package net.morimekta.providence.model;

/**
 * A meta object describing a parsed program file. This may include the
 * included programs as their own meta file. The lines of the original
 * program .thrift file is also included.
 */
@SuppressWarnings("unused")
@javax.annotation.Generated("providence-maven-plugin")
@javax.annotation.concurrent.Immutable
public class ProgramMeta
        implements net.morimekta.providence.PMessage<ProgramMeta,ProgramMeta._Field>,
                   Comparable<ProgramMeta>,
                   java.io.Serializable,
                   net.morimekta.providence.serializer.binary.BinaryWriter {
    private final static long serialVersionUID = 2728474224781124924L;

    private final transient net.morimekta.providence.model.ProgramType mProgram;
    private final transient java.util.List<String> mLines;
    private final transient java.util.Map<String,net.morimekta.providence.model.ProgramMeta> mIncludes;

    private volatile transient int tHashCode;

    // Transient object used during java deserialization.
    private transient ProgramMeta tSerializeInstance;

    private ProgramMeta(_Builder builder) {
        mProgram = builder.mProgram_builder != null ? builder.mProgram_builder.build() : builder.mProgram;
        if (builder.isSetLines()) {
            mLines = com.google.common.collect.ImmutableList.copyOf(builder.mLines);
        } else {
            mLines = null;
        }
        if (builder.isSetIncludes()) {
            mIncludes = com.google.common.collect.ImmutableMap.copyOf(builder.mIncludes);
        } else {
            mIncludes = null;
        }
    }

    public boolean hasProgram() {
        return mProgram != null;
    }

    /**
     * The program type definition
     *
     * @return The field value
     */
    public net.morimekta.providence.model.ProgramType getProgram() {
        return mProgram;
    }

    /**
     * The program type definition
     *
     * @return Optional field value
     */
    @javax.annotation.Nonnull
    public java.util.Optional<net.morimekta.providence.model.ProgramType> optionalProgram() {
        return java.util.Optional.ofNullable(mProgram);
    }

    public int numLines() {
        return mLines != null ? mLines.size() : 0;
    }

    public boolean hasLines() {
        return mLines != null;
    }

    /**
     * The lines of the program file
     *
     * @return The field value
     */
    public java.util.List<String> getLines() {
        return mLines;
    }

    /**
     * The lines of the program file
     *
     * @return Optional field value
     */
    @javax.annotation.Nonnull
    public java.util.Optional<java.util.List<String>> optionalLines() {
        return java.util.Optional.ofNullable(mLines);
    }

    public int numIncludes() {
        return mIncludes != null ? mIncludes.size() : 0;
    }

    public boolean hasIncludes() {
        return mIncludes != null;
    }

    /**
     * Map of program name to meta of included programs
     *
     * @return The field value
     */
    public java.util.Map<String,net.morimekta.providence.model.ProgramMeta> getIncludes() {
        return mIncludes;
    }

    /**
     * Map of program name to meta of included programs
     *
     * @return Optional field value
     */
    @javax.annotation.Nonnull
    public java.util.Optional<java.util.Map<String,net.morimekta.providence.model.ProgramMeta>> optionalIncludes() {
        return java.util.Optional.ofNullable(mIncludes);
    }

    @Override
    public boolean has(int key) {
        switch(key) {
            case 1: return mProgram != null;
            case 2: return mLines != null;
            case 3: return mIncludes != null;
            default: return false;
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T get(int key) {
        switch(key) {
            case 1: return (T) mProgram;
            case 2: return (T) mLines;
            case 3: return (T) mIncludes;
            default: return null;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        if (o == null || !o.getClass().equals(getClass())) return false;
        ProgramMeta other = (ProgramMeta) o;
        return java.util.Objects.equals(mProgram, other.mProgram) &&
               java.util.Objects.equals(mLines, other.mLines) &&
               java.util.Objects.equals(mIncludes, other.mIncludes);
    }

    @Override
    public int hashCode() {
        if (tHashCode == 0) {
            tHashCode = java.util.Objects.hash(
                    ProgramMeta.class,
                    _Field.PROGRAM, mProgram,
                    _Field.LINES, mLines,
                    _Field.INCLUDES, mIncludes);
        }
        return tHashCode;
    }

    @Override
    public String toString() {
        return "providence_model.ProgramMeta" + asString();
    }

    @Override
    @javax.annotation.Nonnull
    public String asString() {
        StringBuilder out = new StringBuilder();
        out.append("{");

        boolean first = true;
        if (hasProgram()) {
            first = false;
            out.append("program:")
               .append(mProgram.asString());
        }
        if (hasLines()) {
            if (first) first = false;
            else out.append(',');
            out.append("lines:")
               .append(net.morimekta.util.Strings.asString(mLines));
        }
        if (hasIncludes()) {
            if (!first) out.append(',');
            out.append("includes:")
               .append(net.morimekta.util.Strings.asString(mIncludes));
        }
        out.append('}');
        return out.toString();
    }

    @Override
    public int compareTo(ProgramMeta other) {
        int c;

        c = Boolean.compare(mProgram != null, other.mProgram != null);
        if (c != 0) return c;
        if (mProgram != null) {
            c = mProgram.compareTo(other.mProgram);
            if (c != 0) return c;
        }

        c = Boolean.compare(mLines != null, other.mLines != null);
        if (c != 0) return c;
        if (mLines != null) {
            c = Integer.compare(mLines.hashCode(), other.mLines.hashCode());
            if (c != 0) return c;
        }

        c = Boolean.compare(mIncludes != null, other.mIncludes != null);
        if (c != 0) return c;
        if (mIncludes != null) {
            c = Integer.compare(mIncludes.hashCode(), other.mIncludes.hashCode());
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

        if (hasProgram()) {
            length += writer.writeByte((byte) 12);
            length += writer.writeShort((short) 1);
            length += net.morimekta.providence.serializer.binary.BinaryFormatUtils.writeMessage(writer, mProgram);
        }

        if (hasLines()) {
            length += writer.writeByte((byte) 15);
            length += writer.writeShort((short) 2);
            length += writer.writeByte((byte) 11);
            length += writer.writeUInt32(mLines.size());
            for (String entry_1 : mLines) {
                net.morimekta.util.Binary tmp_2 = net.morimekta.util.Binary.wrap(entry_1.getBytes(java.nio.charset.StandardCharsets.UTF_8));
                length += writer.writeUInt32(tmp_2.length());
                length += writer.writeBinary(tmp_2);
            }
        }

        if (hasIncludes()) {
            length += writer.writeByte((byte) 13);
            length += writer.writeShort((short) 3);
            length += writer.writeByte((byte) 11);
            length += writer.writeByte((byte) 12);
            length += writer.writeUInt32(mIncludes.size());
            for (java.util.Map.Entry<String,net.morimekta.providence.model.ProgramMeta> entry_3 : mIncludes.entrySet()) {
                net.morimekta.util.Binary tmp_4 = net.morimekta.util.Binary.wrap(entry_3.getKey().getBytes(java.nio.charset.StandardCharsets.UTF_8));
                length += writer.writeUInt32(tmp_4.length());
                length += writer.writeBinary(tmp_4);
                length += net.morimekta.providence.serializer.binary.BinaryFormatUtils.writeMessage(writer, entry_3.getValue());
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
        PROGRAM(1, net.morimekta.providence.descriptor.PRequirement.REQUIRED, "program", net.morimekta.providence.model.ProgramType.provider(), null),
        LINES(2, net.morimekta.providence.descriptor.PRequirement.OPTIONAL, "lines", net.morimekta.providence.descriptor.PList.provider(net.morimekta.providence.descriptor.PPrimitive.STRING.provider()), null),
        INCLUDES(3, net.morimekta.providence.descriptor.PRequirement.OPTIONAL, "includes", net.morimekta.providence.descriptor.PMap.provider(net.morimekta.providence.descriptor.PPrimitive.STRING.provider(),net.morimekta.providence.model.ProgramMeta.provider()), null),
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
                case 1: return _Field.PROGRAM;
                case 2: return _Field.LINES;
                case 3: return _Field.INCLUDES;
            }
            return null;
        }

        /**
         * @param name Field name
         * @return The named field or null
         */
        public static _Field findByName(String name) {
            switch (name) {
                case "program": return _Field.PROGRAM;
                case "lines": return _Field.LINES;
                case "includes": return _Field.INCLUDES;
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
                throw new IllegalArgumentException("No such field id " + id + " in providence_model.ProgramMeta");
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
                throw new IllegalArgumentException("No such field \"" + name + "\" in providence_model.ProgramMeta");
            }
            return field;
        }

    }

    @javax.annotation.Nonnull
    public static net.morimekta.providence.descriptor.PStructDescriptorProvider<ProgramMeta,_Field> provider() {
        return new _Provider();
    }

    @Override
    @javax.annotation.Nonnull
    public net.morimekta.providence.descriptor.PStructDescriptor<ProgramMeta,_Field> descriptor() {
        return kDescriptor;
    }

    public static final net.morimekta.providence.descriptor.PStructDescriptor<ProgramMeta,_Field> kDescriptor;

    private static class _Descriptor
            extends net.morimekta.providence.descriptor.PStructDescriptor<ProgramMeta,_Field> {
        public _Descriptor() {
            super("providence_model", "ProgramMeta", _Builder::new, false);
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

    private final static class _Provider extends net.morimekta.providence.descriptor.PStructDescriptorProvider<ProgramMeta,_Field> {
        @Override
        public net.morimekta.providence.descriptor.PStructDescriptor<ProgramMeta,_Field> descriptor() {
            return kDescriptor;
        }
    }

    /**
     * Make a providence_model.ProgramMeta builder.
     * @return The builder instance.
     */
    public static _Builder builder() {
        return new _Builder();
    }

    /**
     * A meta object describing a parsed program file. This may include the
     * included programs as their own meta file. The lines of the original
     * program .thrift file is also included.
     */
    public static class _Builder
            extends net.morimekta.providence.PMessageBuilder<ProgramMeta,_Field>
            implements net.morimekta.providence.serializer.binary.BinaryReader {
        private java.util.BitSet optionals;
        private java.util.BitSet modified;

        private net.morimekta.providence.model.ProgramType mProgram;
        private net.morimekta.providence.model.ProgramType._Builder mProgram_builder;
        private java.util.List<String> mLines;
        private java.util.Map<String,net.morimekta.providence.model.ProgramMeta> mIncludes;

        /**
         * Make a providence_model.ProgramMeta builder.
         */
        public _Builder() {
            optionals = new java.util.BitSet(3);
            modified = new java.util.BitSet(3);
        }

        /**
         * Make a mutating builder off a base providence_model.ProgramMeta.
         *
         * @param base The base ProgramMeta
         */
        public _Builder(ProgramMeta base) {
            this();

            if (base.hasProgram()) {
                optionals.set(0);
                mProgram = base.mProgram;
            }
            if (base.hasLines()) {
                optionals.set(1);
                mLines = base.mLines;
            }
            if (base.hasIncludes()) {
                optionals.set(2);
                mIncludes = base.mIncludes;
            }
        }

        @javax.annotation.Nonnull
        @Override
        public _Builder merge(ProgramMeta from) {
            if (from.hasProgram()) {
                optionals.set(0);
                modified.set(0);
                if (mProgram_builder != null) {
                    mProgram_builder.merge(from.getProgram());
                } else if (mProgram != null) {
                    mProgram_builder = mProgram.mutate().merge(from.getProgram());
                    mProgram = null;
                } else {
                    mProgram = from.getProgram();
                }
            }

            if (from.hasLines()) {
                optionals.set(1);
                modified.set(1);
                mLines = from.getLines();
            }

            if (from.hasIncludes()) {
                optionals.set(2);
                modified.set(2);
                mutableIncludes().putAll(from.getIncludes());
            }
            return this;
        }

        /**
         * The program type definition
         *
         * @param value The new value
         * @return The builder
         */
        @javax.annotation.Nonnull
        public _Builder setProgram(net.morimekta.providence.model.ProgramType value) {
            if (value == null) {
                return clearProgram();
            }

            optionals.set(0);
            modified.set(0);
            mProgram = value;
            mProgram_builder = null;
            return this;
        }

        /**
         * The program type definition
         *
         * @param builder builder for the new value
         * @return The builder
         */
        @javax.annotation.Nonnull
        public _Builder setProgram(net.morimekta.providence.model.ProgramType._Builder builder) {
          return setProgram(builder == null ? null : builder.build());
        }

        /**
         * The program type definition
         *
         * @return True if program has been set.
         */
        public boolean isSetProgram() {
            return optionals.get(0);
        }

        /**
         * The program type definition
         *
         * @return True if program has been modified.
         */
        public boolean isModifiedProgram() {
            return modified.get(0);
        }

        /**
         * The program type definition
         *
         * @return The builder
         */
        @javax.annotation.Nonnull
        public _Builder clearProgram() {
            optionals.clear(0);
            modified.set(0);
            mProgram = null;
            mProgram_builder = null;
            return this;
        }

        /**
         * The program type definition
         *
         * @return The field builder
         */
        @javax.annotation.Nonnull
        public net.morimekta.providence.model.ProgramType._Builder mutableProgram() {
            optionals.set(0);
            modified.set(0);

            if (mProgram != null) {
                mProgram_builder = mProgram.mutate();
                mProgram = null;
            } else if (mProgram_builder == null) {
                mProgram_builder = net.morimekta.providence.model.ProgramType.builder();
            }
            return mProgram_builder;
        }

        /**
         * The program type definition
         *
         * @return The field value
         */
        public net.morimekta.providence.model.ProgramType getProgram() {

            if (mProgram_builder != null) {
                return mProgram_builder.build();
            }
            return mProgram;
        }

        /**
         * The lines of the program file
         *
         * @param value The new value
         * @return The builder
         */
        @javax.annotation.Nonnull
        public _Builder setLines(java.util.Collection<String> value) {
            if (value == null) {
                return clearLines();
            }

            optionals.set(1);
            modified.set(1);
            mLines = com.google.common.collect.ImmutableList.copyOf(value);
            return this;
        }

        /**
         * The lines of the program file
         *
         * @param values The added value
         * @return The builder
         */
        @javax.annotation.Nonnull
        public _Builder addToLines(String... values) {
            optionals.set(1);
            modified.set(1);
            java.util.List<String> _container = mutableLines();
            for (String item : values) {
                _container.add(item);
            }
            return this;
        }

        /**
         * The lines of the program file
         *
         * @return True if lines has been set.
         */
        public boolean isSetLines() {
            return optionals.get(1);
        }

        /**
         * The lines of the program file
         *
         * @return True if lines has been modified.
         */
        public boolean isModifiedLines() {
            return modified.get(1);
        }

        /**
         * The lines of the program file
         *
         * @return The builder
         */
        @javax.annotation.Nonnull
        public _Builder clearLines() {
            optionals.clear(1);
            modified.set(1);
            mLines = null;
            return this;
        }

        /**
         * The lines of the program file
         *
         * @return The field builder
         */
        @javax.annotation.Nonnull
        public java.util.List<String> mutableLines() {
            optionals.set(1);
            modified.set(1);

            if (mLines == null) {
                mLines = new java.util.ArrayList<>();
            } else if (!(mLines instanceof java.util.ArrayList)) {
                mLines = new java.util.ArrayList<>(mLines);
            }
            return mLines;
        }

        /**
         * Map of program name to meta of included programs
         *
         * @param value The new value
         * @return The builder
         */
        @javax.annotation.Nonnull
        public _Builder setIncludes(java.util.Map<String,net.morimekta.providence.model.ProgramMeta> value) {
            if (value == null) {
                return clearIncludes();
            }

            optionals.set(2);
            modified.set(2);
            mIncludes = com.google.common.collect.ImmutableMap.copyOf(value);
            return this;
        }

        /**
         * Map of program name to meta of included programs
         *
         * @param key The inserted key
         * @param value The inserted value
         * @return The builder
         */
        @javax.annotation.Nonnull
        public _Builder putInIncludes(String key, net.morimekta.providence.model.ProgramMeta value) {
            optionals.set(2);
            modified.set(2);
            mutableIncludes().put(key, value);
            return this;
        }

        /**
         * Map of program name to meta of included programs
         *
         * @return True if includes has been set.
         */
        public boolean isSetIncludes() {
            return optionals.get(2);
        }

        /**
         * Map of program name to meta of included programs
         *
         * @return True if includes has been modified.
         */
        public boolean isModifiedIncludes() {
            return modified.get(2);
        }

        /**
         * Map of program name to meta of included programs
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
         * Map of program name to meta of included programs
         *
         * @return The field builder
         */
        @javax.annotation.Nonnull
        public java.util.Map<String,net.morimekta.providence.model.ProgramMeta> mutableIncludes() {
            optionals.set(2);
            modified.set(2);

            if (mIncludes == null) {
                mIncludes = new java.util.HashMap<>();
            } else if (!(mIncludes instanceof java.util.HashMap)) {
                mIncludes = new java.util.HashMap<>(mIncludes);
            }
            return mIncludes;
        }

        @Override
        public boolean equals(Object o) {
            if (o == this) return true;
            if (o == null || !o.getClass().equals(getClass())) return false;
            ProgramMeta._Builder other = (ProgramMeta._Builder) o;
            return java.util.Objects.equals(optionals, other.optionals) &&
                   java.util.Objects.equals(getProgram(), other.getProgram()) &&
                   java.util.Objects.equals(mLines, other.mLines) &&
                   java.util.Objects.equals(mIncludes, other.mIncludes);
        }

        @Override
        public int hashCode() {
            return java.util.Objects.hash(
                    ProgramMeta.class, optionals,
                    _Field.PROGRAM, getProgram(),
                    _Field.LINES, mLines,
                    _Field.INCLUDES, mIncludes);
        }

        @Override
        @SuppressWarnings("unchecked")
        public net.morimekta.providence.PMessageBuilder mutator(int key) {
            switch (key) {
                case 1: return mutableProgram();
                default: throw new IllegalArgumentException("Not a message field ID: " + key);
            }
        }

        @javax.annotation.Nonnull
        @Override
        @SuppressWarnings("unchecked")
        public _Builder set(int key, Object value) {
            if (value == null) return clear(key);
            switch (key) {
                case 1: setProgram((net.morimekta.providence.model.ProgramType) value); break;
                case 2: setLines((java.util.List<String>) value); break;
                case 3: setIncludes((java.util.Map<String,net.morimekta.providence.model.ProgramMeta>) value); break;
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
                case 2: addToLines((String) value); break;
                default: break;
            }
            return this;
        }

        @javax.annotation.Nonnull
        @Override
        public _Builder clear(int key) {
            switch (key) {
                case 1: clearProgram(); break;
                case 2: clearLines(); break;
                case 3: clearIncludes(); break;
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
                    missing.add("program");
                }

                throw new java.lang.IllegalStateException(
                        "Missing required fields " +
                        String.join(",", missing) +
                        " in message providence_model.ProgramMeta");
            }
        }

        @javax.annotation.Nonnull
        @Override
        public net.morimekta.providence.descriptor.PStructDescriptor<ProgramMeta,_Field> descriptor() {
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
                            mProgram = net.morimekta.providence.serializer.binary.BinaryFormatUtils.readMessage(reader, net.morimekta.providence.model.ProgramType.kDescriptor, strict);
                            optionals.set(0);
                        } else {
                            throw new net.morimekta.providence.serializer.SerializerException("Wrong type " + net.morimekta.providence.serializer.binary.BinaryType.asString(type) + " for providence_model.ProgramMeta.program, should be struct(12)");
                        }
                        break;
                    }
                    case 2: {
                        if (type == 15) {
                            net.morimekta.providence.descriptor.PList.DefaultBuilder<String> b_1 = new net.morimekta.providence.descriptor.PList.DefaultBuilder<>();
                            byte t_3 = reader.expectByte();
                            if (t_3 == 11) {
                                final int len_2 = reader.expectUInt32();
                                for (int i_4 = 0; i_4 < len_2; ++i_4) {
                                    int len_6 = reader.expectUInt32();
                                    String key_5 = new String(reader.expectBytes(len_6), java.nio.charset.StandardCharsets.UTF_8);
                                    b_1.add(key_5);
                                }
                                mLines = b_1.build();
                            } else {
                                throw new net.morimekta.providence.serializer.SerializerException("Wrong item type " + net.morimekta.providence.serializer.binary.BinaryType.asString(t_3) + " for providence_model.ProgramMeta.lines, should be string(11)");
                            }
                            optionals.set(1);
                        } else {
                            throw new net.morimekta.providence.serializer.SerializerException("Wrong type " + net.morimekta.providence.serializer.binary.BinaryType.asString(type) + " for providence_model.ProgramMeta.lines, should be struct(12)");
                        }
                        break;
                    }
                    case 3: {
                        if (type == 13) {
                            net.morimekta.providence.descriptor.PMap.DefaultBuilder<String,net.morimekta.providence.model.ProgramMeta> b_7 = new net.morimekta.providence.descriptor.PMap.DefaultBuilder<>();
                            byte t_9 = reader.expectByte();
                            byte t_10 = reader.expectByte();
                            if (t_9 == 11 && t_10 == 12) {
                                final int len_8 = reader.expectUInt32();
                                for (int i_11 = 0; i_11 < len_8; ++i_11) {
                                    int len_14 = reader.expectUInt32();
                                    String key_12 = new String(reader.expectBytes(len_14), java.nio.charset.StandardCharsets.UTF_8);
                                    net.morimekta.providence.model.ProgramMeta val_13 = net.morimekta.providence.serializer.binary.BinaryFormatUtils.readMessage(reader, net.morimekta.providence.model.ProgramMeta.kDescriptor, strict);
                                    b_7.put(key_12, val_13);
                                }
                                mIncludes = b_7.build();
                            } else {
                                throw new net.morimekta.providence.serializer.SerializerException(
                                        "Wrong key type " + net.morimekta.providence.serializer.binary.BinaryType.asString(t_9) +
                                        " or value type " + net.morimekta.providence.serializer.binary.BinaryType.asString(t_10) +
                                        " for providence_model.ProgramMeta.includes, should be string(11) and struct(12)");
                            }
                            optionals.set(2);
                        } else {
                            throw new net.morimekta.providence.serializer.SerializerException("Wrong type " + net.morimekta.providence.serializer.binary.BinaryType.asString(type) + " for providence_model.ProgramMeta.includes, should be struct(12)");
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
        public ProgramMeta build() {
            return new ProgramMeta(this);
        }
    }
}
