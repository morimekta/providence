package net.morimekta.providence.model;

/**
 * service (extends &lt;extend&gt;)? {
 *   (&lt;method&gt; [;,]?)*
 * }
 */
@SuppressWarnings("unused")
@javax.annotation.Generated("providence-maven-plugin")
@javax.annotation.concurrent.Immutable
public class ServiceType
        implements net.morimekta.providence.PMessage<ServiceType,ServiceType._Field>,
                   Comparable<ServiceType>,
                   java.io.Serializable,
                   net.morimekta.providence.serializer.binary.BinaryWriter {
    private final static long serialVersionUID = 789757775761432238L;

    private final static String kDefaultName = "";
    private final static java.util.List<net.morimekta.providence.model.FunctionType> kDefaultMethods = new net.morimekta.providence.descriptor.PList.DefaultBuilder<net.morimekta.providence.model.FunctionType>()
                .build();

    private final transient String mDocumentation;
    private final transient String mName;
    private final transient String mExtend;
    private final transient java.util.List<net.morimekta.providence.model.FunctionType> mMethods;
    private final transient java.util.Map<String,String> mAnnotations;

    private volatile transient int tHashCode;

    // Transient object used during java deserialization.
    private transient ServiceType tSerializeInstance;

    private ServiceType(_Builder builder) {
        mDocumentation = builder.mDocumentation;
        if (builder.isSetName()) {
            mName = builder.mName;
        } else {
            mName = kDefaultName;
        }
        mExtend = builder.mExtend;
        if (builder.isSetMethods()) {
            mMethods = com.google.common.collect.ImmutableList.copyOf(builder.mMethods);
        } else {
            mMethods = kDefaultMethods;
        }
        if (builder.isSetAnnotations()) {
            mAnnotations = com.google.common.collect.ImmutableSortedMap.copyOf(builder.mAnnotations);
        } else {
            mAnnotations = null;
        }
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

    public boolean hasExtend() {
        return mExtend != null;
    }

    /**
     * @return The field value
     */
    public String getExtend() {
        return mExtend;
    }

    public int numMethods() {
        return mMethods != null ? mMethods.size() : 0;
    }

    public boolean hasMethods() {
        return true;
    }

    /**
     * @return The field value
     */
    @javax.annotation.Nonnull
    public java.util.List<net.morimekta.providence.model.FunctionType> getMethods() {
        return mMethods;
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

    @Override
    public boolean has(int key) {
        switch(key) {
            case 1: return mDocumentation != null;
            case 2: return true;
            case 3: return mExtend != null;
            case 4: return true;
            case 5: return mAnnotations != null;
            default: return false;
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T get(int key) {
        switch(key) {
            case 1: return (T) mDocumentation;
            case 2: return (T) mName;
            case 3: return (T) mExtend;
            case 4: return (T) mMethods;
            case 5: return (T) mAnnotations;
            default: return null;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        if (o == null || !o.getClass().equals(getClass())) return false;
        ServiceType other = (ServiceType) o;
        return java.util.Objects.equals(mDocumentation, other.mDocumentation) &&
               java.util.Objects.equals(mName, other.mName) &&
               java.util.Objects.equals(mExtend, other.mExtend) &&
               java.util.Objects.equals(mMethods, other.mMethods) &&
               java.util.Objects.equals(mAnnotations, other.mAnnotations);
    }

    @Override
    public int hashCode() {
        if (tHashCode == 0) {
            tHashCode = java.util.Objects.hash(
                    ServiceType.class,
                    _Field.DOCUMENTATION, mDocumentation,
                    _Field.NAME, mName,
                    _Field.EXTEND, mExtend,
                    _Field.METHODS, mMethods,
                    _Field.ANNOTATIONS, mAnnotations);
        }
        return tHashCode;
    }

    @Override
    public String toString() {
        return "model.ServiceType" + asString();
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
        if (hasExtend()) {
            out.append(',');
            out.append("extend:")
               .append('\"')
               .append(net.morimekta.util.Strings.escape(mExtend))
               .append('\"');
        }
        out.append(',');
        out.append("methods:")
           .append(net.morimekta.util.Strings.asString(mMethods));
        if (hasAnnotations()) {
            out.append(',');
            out.append("annotations:")
               .append(net.morimekta.util.Strings.asString(mAnnotations));
        }
        out.append('}');
        return out.toString();
    }

    @Override
    public int compareTo(ServiceType other) {
        int c;

        c = Boolean.compare(mDocumentation != null, other.mDocumentation != null);
        if (c != 0) return c;
        if (mDocumentation != null) {
            c = mDocumentation.compareTo(other.mDocumentation);
            if (c != 0) return c;
        }

        c = mName.compareTo(other.mName);
        if (c != 0) return c;

        c = Boolean.compare(mExtend != null, other.mExtend != null);
        if (c != 0) return c;
        if (mExtend != null) {
            c = mExtend.compareTo(other.mExtend);
            if (c != 0) return c;
        }

        c = Integer.compare(mMethods.hashCode(), other.mMethods.hashCode());
        if (c != 0) return c;

        c = Boolean.compare(mAnnotations != null, other.mAnnotations != null);
        if (c != 0) return c;
        if (mAnnotations != null) {
            c = Integer.compare(mAnnotations.hashCode(), other.mAnnotations.hashCode());
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

        if (hasExtend()) {
            length += writer.writeByte((byte) 11);
            length += writer.writeShort((short) 3);
            net.morimekta.util.Binary tmp_3 = net.morimekta.util.Binary.wrap(mExtend.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            length += writer.writeUInt32(tmp_3.length());
            length += writer.writeBinary(tmp_3);
        }

        length += writer.writeByte((byte) 15);
        length += writer.writeShort((short) 4);
        length += writer.writeByte((byte) 12);
        length += writer.writeUInt32(mMethods.size());
        for (net.morimekta.providence.model.FunctionType entry_4 : mMethods) {
            length += net.morimekta.providence.serializer.binary.BinaryFormatUtils.writeMessage(writer, entry_4);
        }

        if (hasAnnotations()) {
            length += writer.writeByte((byte) 13);
            length += writer.writeShort((short) 5);
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
        EXTEND(3, net.morimekta.providence.descriptor.PRequirement.OPTIONAL, "extend", net.morimekta.providence.descriptor.PPrimitive.STRING.provider(), null),
        METHODS(4, net.morimekta.providence.descriptor.PRequirement.DEFAULT, "methods", net.morimekta.providence.descriptor.PList.provider(net.morimekta.providence.model.FunctionType.provider()), null),
        ANNOTATIONS(5, net.morimekta.providence.descriptor.PRequirement.OPTIONAL, "annotations", net.morimekta.providence.descriptor.PMap.sortedProvider(net.morimekta.providence.descriptor.PPrimitive.STRING.provider(),net.morimekta.providence.descriptor.PPrimitive.STRING.provider()), null),
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
                case 3: return _Field.EXTEND;
                case 4: return _Field.METHODS;
                case 5: return _Field.ANNOTATIONS;
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
                case "extend": return _Field.EXTEND;
                case "methods": return _Field.METHODS;
                case "annotations": return _Field.ANNOTATIONS;
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
                throw new IllegalArgumentException("No such field id " + id + " in model.ServiceType");
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
                throw new IllegalArgumentException("No such field \"" + name + "\" in model.ServiceType");
            }
            return field;
        }

    }

    @javax.annotation.Nonnull
    public static net.morimekta.providence.descriptor.PStructDescriptorProvider<ServiceType,_Field> provider() {
        return new _Provider();
    }

    @Override
    @javax.annotation.Nonnull
    public net.morimekta.providence.descriptor.PStructDescriptor<ServiceType,_Field> descriptor() {
        return kDescriptor;
    }

    public static final net.morimekta.providence.descriptor.PStructDescriptor<ServiceType,_Field> kDescriptor;

    private static class _Descriptor
            extends net.morimekta.providence.descriptor.PStructDescriptor<ServiceType,_Field> {
        public _Descriptor() {
            super("model", "ServiceType", _Builder::new, false);
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

    private final static class _Provider extends net.morimekta.providence.descriptor.PStructDescriptorProvider<ServiceType,_Field> {
        @Override
        public net.morimekta.providence.descriptor.PStructDescriptor<ServiceType,_Field> descriptor() {
            return kDescriptor;
        }
    }

    /**
     * Make a model.ServiceType builder.
     * @return The builder instance.
     */
    public static _Builder builder() {
        return new _Builder();
    }

    /**
     * service (extends &lt;extend&gt;)? {
     *   (&lt;method&gt; [;,]?)*
     * }
     */
    public static class _Builder
            extends net.morimekta.providence.PMessageBuilder<ServiceType,_Field>
            implements net.morimekta.providence.serializer.binary.BinaryReader {
        private java.util.BitSet optionals;
        private java.util.BitSet modified;

        private String mDocumentation;
        private String mName;
        private String mExtend;
        private java.util.List<net.morimekta.providence.model.FunctionType> mMethods;
        private java.util.Map<String,String> mAnnotations;

        /**
         * Make a model.ServiceType builder.
         */
        public _Builder() {
            optionals = new java.util.BitSet(5);
            modified = new java.util.BitSet(5);
            mName = kDefaultName;
            mMethods = kDefaultMethods;
        }

        /**
         * Make a mutating builder off a base model.ServiceType.
         *
         * @param base The base ServiceType
         */
        public _Builder(ServiceType base) {
            this();

            if (base.hasDocumentation()) {
                optionals.set(0);
                mDocumentation = base.mDocumentation;
            }
            optionals.set(1);
            mName = base.mName;
            if (base.hasExtend()) {
                optionals.set(2);
                mExtend = base.mExtend;
            }
            optionals.set(3);
            mMethods = base.mMethods;
            if (base.hasAnnotations()) {
                optionals.set(4);
                mAnnotations = base.mAnnotations;
            }
        }

        @javax.annotation.Nonnull
        @Override
        public _Builder merge(ServiceType from) {
            if (from.hasDocumentation()) {
                optionals.set(0);
                modified.set(0);
                mDocumentation = from.getDocumentation();
            }

            optionals.set(1);
            modified.set(1);
            mName = from.getName();

            if (from.hasExtend()) {
                optionals.set(2);
                modified.set(2);
                mExtend = from.getExtend();
            }

            optionals.set(3);
            modified.set(3);
            mMethods = from.getMethods();

            if (from.hasAnnotations()) {
                optionals.set(4);
                modified.set(4);
                mutableAnnotations().putAll(from.getAnnotations());
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
         * Sets the value of extend.
         *
         * @param value The new value
         * @return The builder
         */
        @javax.annotation.Nonnull
        public _Builder setExtend(String value) {
            if (value == null) {
                return clearExtend();
            }

            optionals.set(2);
            modified.set(2);
            mExtend = value;
            return this;
        }

        /**
         * Checks for presence of the extend field.
         *
         * @return True if extend has been set.
         */
        public boolean isSetExtend() {
            return optionals.get(2);
        }

        /**
         * Checks if extend has been modified since the _Builder was created.
         *
         * @return True if extend has been modified.
         */
        public boolean isModifiedExtend() {
            return modified.get(2);
        }

        /**
         * Clears the extend field.
         *
         * @return The builder
         */
        @javax.annotation.Nonnull
        public _Builder clearExtend() {
            optionals.clear(2);
            modified.set(2);
            mExtend = null;
            return this;
        }

        /**
         * Gets the value of the contained extend.
         *
         * @return The field value
         */
        public String getExtend() {
            return mExtend;
        }

        /**
         * Sets the value of methods.
         *
         * @param value The new value
         * @return The builder
         */
        @javax.annotation.Nonnull
        public _Builder setMethods(java.util.Collection<net.morimekta.providence.model.FunctionType> value) {
            if (value == null) {
                return clearMethods();
            }

            optionals.set(3);
            modified.set(3);
            mMethods = com.google.common.collect.ImmutableList.copyOf(value);
            return this;
        }

        /**
         * Adds entries to methods.
         *
         * @param values The added value
         * @return The builder
         */
        @javax.annotation.Nonnull
        public _Builder addToMethods(net.morimekta.providence.model.FunctionType... values) {
            optionals.set(3);
            modified.set(3);
            java.util.List<net.morimekta.providence.model.FunctionType> _container = mutableMethods();
            for (net.morimekta.providence.model.FunctionType item : values) {
                _container.add(item);
            }
            return this;
        }

        /**
         * Checks for presence of the methods field.
         *
         * @return True if methods has been set.
         */
        public boolean isSetMethods() {
            return optionals.get(3);
        }

        /**
         * Checks if methods has been modified since the _Builder was created.
         *
         * @return True if methods has been modified.
         */
        public boolean isModifiedMethods() {
            return modified.get(3);
        }

        /**
         * Clears the methods field.
         *
         * @return The builder
         */
        @javax.annotation.Nonnull
        public _Builder clearMethods() {
            optionals.clear(3);
            modified.set(3);
            mMethods = kDefaultMethods;
            return this;
        }

        /**
         * Gets the builder for the contained methods.
         *
         * @return The field builder
         */
        @javax.annotation.Nonnull
        public java.util.List<net.morimekta.providence.model.FunctionType> mutableMethods() {
            optionals.set(3);
            modified.set(3);

            if (mMethods == null) {
                mMethods = new java.util.ArrayList<>();
            } else if (!(mMethods instanceof java.util.ArrayList)) {
                mMethods = new java.util.ArrayList<>(mMethods);
            }
            return mMethods;
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

            optionals.set(4);
            modified.set(4);
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
            optionals.set(4);
            modified.set(4);
            mutableAnnotations().put(key, value);
            return this;
        }

        /**
         * Checks for presence of the annotations field.
         *
         * @return True if annotations has been set.
         */
        public boolean isSetAnnotations() {
            return optionals.get(4);
        }

        /**
         * Checks if annotations has been modified since the _Builder was created.
         *
         * @return True if annotations has been modified.
         */
        public boolean isModifiedAnnotations() {
            return modified.get(4);
        }

        /**
         * Clears the annotations field.
         *
         * @return The builder
         */
        @javax.annotation.Nonnull
        public _Builder clearAnnotations() {
            optionals.clear(4);
            modified.set(4);
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
            optionals.set(4);
            modified.set(4);

            if (mAnnotations == null) {
                mAnnotations = new java.util.TreeMap<>();
            } else if (!(mAnnotations instanceof java.util.TreeMap)) {
                mAnnotations = new java.util.TreeMap<>(mAnnotations);
            }
            return mAnnotations;
        }

        @Override
        public boolean equals(Object o) {
            if (o == this) return true;
            if (o == null || !o.getClass().equals(getClass())) return false;
            ServiceType._Builder other = (ServiceType._Builder) o;
            return java.util.Objects.equals(optionals, other.optionals) &&
                   java.util.Objects.equals(mDocumentation, other.mDocumentation) &&
                   java.util.Objects.equals(mName, other.mName) &&
                   java.util.Objects.equals(mExtend, other.mExtend) &&
                   java.util.Objects.equals(mMethods, other.mMethods) &&
                   java.util.Objects.equals(mAnnotations, other.mAnnotations);
        }

        @Override
        public int hashCode() {
            return java.util.Objects.hash(
                    ServiceType.class, optionals,
                    _Field.DOCUMENTATION, mDocumentation,
                    _Field.NAME, mName,
                    _Field.EXTEND, mExtend,
                    _Field.METHODS, mMethods,
                    _Field.ANNOTATIONS, mAnnotations);
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
                case 2: setName((String) value); break;
                case 3: setExtend((String) value); break;
                case 4: setMethods((java.util.List<net.morimekta.providence.model.FunctionType>) value); break;
                case 5: setAnnotations((java.util.Map<String,String>) value); break;
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
                case 4: addToMethods((net.morimekta.providence.model.FunctionType) value); break;
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
                case 3: clearExtend(); break;
                case 4: clearMethods(); break;
                case 5: clearAnnotations(); break;
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
                        " in message model.ServiceType");
            }
        }

        @javax.annotation.Nonnull
        @Override
        public net.morimekta.providence.descriptor.PStructDescriptor<ServiceType,_Field> descriptor() {
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
                            throw new net.morimekta.providence.serializer.SerializerException("Wrong type " + net.morimekta.providence.serializer.binary.BinaryType.asString(type) + " for model.ServiceType.documentation, should be struct(12)");
                        }
                        break;
                    }
                    case 2: {
                        if (type == 11) {
                            int len_2 = reader.expectUInt32();
                            mName = new String(reader.expectBytes(len_2), java.nio.charset.StandardCharsets.UTF_8);
                            optionals.set(1);
                        } else {
                            throw new net.morimekta.providence.serializer.SerializerException("Wrong type " + net.morimekta.providence.serializer.binary.BinaryType.asString(type) + " for model.ServiceType.name, should be struct(12)");
                        }
                        break;
                    }
                    case 3: {
                        if (type == 11) {
                            int len_3 = reader.expectUInt32();
                            mExtend = new String(reader.expectBytes(len_3), java.nio.charset.StandardCharsets.UTF_8);
                            optionals.set(2);
                        } else {
                            throw new net.morimekta.providence.serializer.SerializerException("Wrong type " + net.morimekta.providence.serializer.binary.BinaryType.asString(type) + " for model.ServiceType.extend, should be struct(12)");
                        }
                        break;
                    }
                    case 4: {
                        if (type == 15) {
                            net.morimekta.providence.descriptor.PList.DefaultBuilder<net.morimekta.providence.model.FunctionType> b_4 = new net.morimekta.providence.descriptor.PList.DefaultBuilder<>();
                            byte t_6 = reader.expectByte();
                            if (t_6 == 12) {
                                final int len_5 = reader.expectUInt32();
                                for (int i_7 = 0; i_7 < len_5; ++i_7) {
                                    net.morimekta.providence.model.FunctionType key_8 = net.morimekta.providence.serializer.binary.BinaryFormatUtils.readMessage(reader, net.morimekta.providence.model.FunctionType.kDescriptor, strict);
                                    b_4.add(key_8);
                                }
                                mMethods = b_4.build();
                            } else {
                                throw new net.morimekta.providence.serializer.SerializerException("Wrong item type " + net.morimekta.providence.serializer.binary.BinaryType.asString(t_6) + " for model.ServiceType.methods, should be struct(12)");
                            }
                            optionals.set(3);
                        } else {
                            throw new net.morimekta.providence.serializer.SerializerException("Wrong type " + net.morimekta.providence.serializer.binary.BinaryType.asString(type) + " for model.ServiceType.methods, should be struct(12)");
                        }
                        break;
                    }
                    case 5: {
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
                                mAnnotations = b_9.build();
                            } else {
                                throw new net.morimekta.providence.serializer.SerializerException(
                                        "Wrong key type " + net.morimekta.providence.serializer.binary.BinaryType.asString(t_11) +
                                        " or value type " + net.morimekta.providence.serializer.binary.BinaryType.asString(t_12) +
                                        " for model.ServiceType.annotations, should be string(11) and string(11)");
                            }
                            optionals.set(4);
                        } else {
                            throw new net.morimekta.providence.serializer.SerializerException("Wrong type " + net.morimekta.providence.serializer.binary.BinaryType.asString(type) + " for model.ServiceType.annotations, should be struct(12)");
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
        public ServiceType build() {
            return new ServiceType(this);
        }
    }
}
