package net.morimekta.providence.model;

/**
 * enum {
 *   (&lt;value&gt; ([;,])?)*
 * }
 */
@SuppressWarnings("unused")
@javax.annotation.Generated("providence java generator")
public class EnumType
        implements net.morimekta.providence.PMessage<EnumType,EnumType._Field>,
                   Comparable<EnumType>,
                   java.io.Serializable,
                   net.morimekta.providence.serializer.rw.BinaryWriter {
    private final static long serialVersionUID = 5720337451968926862L;

    private final static String kDefaultName = "";
    private final static java.util.List<net.morimekta.providence.model.EnumValue> kDefaultValues = new net.morimekta.providence.descriptor.PList.DefaultBuilder<net.morimekta.providence.model.EnumValue>()
                .build();

    private final String mDocumentation;
    private final String mName;
    private final java.util.List<net.morimekta.providence.model.EnumValue> mValues;
    private final java.util.Map<String,String> mAnnotations;

    private volatile int tHashCode;

    public EnumType(String pDocumentation,
                    String pName,
                    java.util.List<net.morimekta.providence.model.EnumValue> pValues,
                    java.util.Map<String,String> pAnnotations) {
        mDocumentation = pDocumentation;
        if (pName != null) {
            mName = pName;
        } else {
            mName = kDefaultName;
        }
        if (pValues != null) {
            mValues = com.google.common.collect.ImmutableList.copyOf(pValues);
        } else {
            mValues = kDefaultValues;
        }
        if (pAnnotations != null) {
            mAnnotations = com.google.common.collect.ImmutableSortedMap.copyOf(pAnnotations);
        } else {
            mAnnotations = null;
        }
    }

    private EnumType(_Builder builder) {
        mDocumentation = builder.mDocumentation;
        if (builder.isSetName()) {
            mName = builder.mName;
        } else {
            mName = kDefaultName;
        }
        if (builder.isSetValues()) {
            mValues = com.google.common.collect.ImmutableList.copyOf(builder.mValues);
        } else {
            mValues = kDefaultValues;
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

    public int numValues() {
        return mValues != null ? mValues.size() : 0;
    }

    public boolean hasValues() {
        return true;
    }

    /**
     * @return The field value
     */
    @javax.annotation.Nonnull
    public java.util.List<net.morimekta.providence.model.EnumValue> getValues() {
        return mValues;
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
            case 1: return hasDocumentation();
            case 2: return true;
            case 3: return true;
            case 4: return hasAnnotations();
            default: return false;
        }
    }

    @Override
    public int num(int key) {
        switch(key) {
            case 1: return hasDocumentation() ? 1 : 0;
            case 2: return 1;
            case 3: return numValues();
            case 4: return numAnnotations();
            default: return 0;
        }
    }

    @Override
    public Object get(int key) {
        switch(key) {
            case 1: return getDocumentation();
            case 2: return getName();
            case 3: return getValues();
            case 4: return getAnnotations();
            default: return null;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        if (o == null || !o.getClass().equals(getClass())) return false;
        EnumType other = (EnumType) o;
        return java.util.Objects.equals(mDocumentation, other.mDocumentation) &&
               java.util.Objects.equals(mName, other.mName) &&
               java.util.Objects.equals(mValues, other.mValues) &&
               java.util.Objects.equals(mAnnotations, other.mAnnotations);
    }

    @Override
    public int hashCode() {
        if (tHashCode == 0) {
            tHashCode = java.util.Objects.hash(
                    EnumType.class,
                    _Field.DOCUMENTATION, mDocumentation,
                    _Field.NAME, mName,
                    _Field.VALUES, mValues,
                    _Field.ANNOTATIONS, mAnnotations);
        }
        return tHashCode;
    }

    @Override
    public String toString() {
        return "model.EnumType" + asString();
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
        out.append(',');
        out.append("values:")
           .append(net.morimekta.util.Strings.asString(mValues));
        if (hasAnnotations()) {
            out.append(',');
            out.append("annotations:")
               .append(net.morimekta.util.Strings.asString(mAnnotations));
        }
        out.append('}');
        return out.toString();
    }

    @Override
    public int compareTo(EnumType other) {
        int c;

        c = Boolean.compare(mDocumentation != null, other.mDocumentation != null);
        if (c != 0) return c;
        if (mDocumentation != null) {
            c = mDocumentation.compareTo(other.mDocumentation);
            if (c != 0) return c;
        }

        c = mName.compareTo(other.mName);
        if (c != 0) return c;

        c = Integer.compare(mValues.hashCode(), other.mValues.hashCode());
        if (c != 0) return c;

        c = Boolean.compare(mAnnotations != null, other.mAnnotations != null);
        if (c != 0) return c;
        if (mAnnotations != null) {
            c = Integer.compare(mAnnotations.hashCode(), other.mAnnotations.hashCode());
            if (c != 0) return c;
        }

        return 0;
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

        length += writer.writeByte((byte) 15);
        length += writer.writeShort((short) 3);
        length += writer.writeByte((byte) 12);
        length += writer.writeUInt32(mValues.size());
        for (net.morimekta.providence.model.EnumValue entry_3 : mValues) {
            length += net.morimekta.providence.serializer.rw.BinaryFormatUtils.writeMessage(writer, entry_3);
        }

        if (hasAnnotations()) {
            length += writer.writeByte((byte) 13);
            length += writer.writeShort((short) 4);
            length += writer.writeByte((byte) 11);
            length += writer.writeByte((byte) 11);
            length += writer.writeUInt32(mAnnotations.size());
            for (java.util.Map.Entry<String,String> entry_4 : mAnnotations.entrySet()) {
                net.morimekta.util.Binary tmp_5 = net.morimekta.util.Binary.wrap(entry_4.getKey().getBytes(java.nio.charset.StandardCharsets.UTF_8));
                length += writer.writeUInt32(tmp_5.length());
                length += writer.writeBinary(tmp_5);
                net.morimekta.util.Binary tmp_6 = net.morimekta.util.Binary.wrap(entry_4.getValue().getBytes(java.nio.charset.StandardCharsets.UTF_8));
                length += writer.writeUInt32(tmp_6.length());
                length += writer.writeBinary(tmp_6);
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
        VALUES(3, net.morimekta.providence.descriptor.PRequirement.DEFAULT, "values", net.morimekta.providence.descriptor.PList.provider(net.morimekta.providence.model.EnumValue.provider()), null),
        ANNOTATIONS(4, net.morimekta.providence.descriptor.PRequirement.OPTIONAL, "annotations", net.morimekta.providence.descriptor.PMap.sortedProvider(net.morimekta.providence.descriptor.PPrimitive.STRING.provider(),net.morimekta.providence.descriptor.PPrimitive.STRING.provider()), null),
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
                case 1: return _Field.DOCUMENTATION;
                case 2: return _Field.NAME;
                case 3: return _Field.VALUES;
                case 4: return _Field.ANNOTATIONS;
            }
            return null;
        }

        public static _Field forName(String name) {
            switch (name) {
                case "documentation": return _Field.DOCUMENTATION;
                case "name": return _Field.NAME;
                case "values": return _Field.VALUES;
                case "annotations": return _Field.ANNOTATIONS;
            }
            return null;
        }
    }

    public static net.morimekta.providence.descriptor.PStructDescriptorProvider<EnumType,_Field> provider() {
        return new _Provider();
    }

    @Override
    public net.morimekta.providence.descriptor.PStructDescriptor<EnumType,_Field> descriptor() {
        return kDescriptor;
    }

    public static final net.morimekta.providence.descriptor.PStructDescriptor<EnumType,_Field> kDescriptor;

    private static class _Descriptor
            extends net.morimekta.providence.descriptor.PStructDescriptor<EnumType,_Field> {
        public _Descriptor() {
            super("model", "EnumType", _Builder::new, false);
        }

        @Override
        public _Field[] getFields() {
            return _Field.values();
        }

        @Override
        public _Field findFieldByName(String name) {
            return _Field.forName(name);
        }

        @Override
        public _Field findFieldById(int id) {
            return _Field.forKey(id);
        }
    }

    static {
        kDescriptor = new _Descriptor();
    }

    private final static class _Provider extends net.morimekta.providence.descriptor.PStructDescriptorProvider<EnumType,_Field> {
        @Override
        public net.morimekta.providence.descriptor.PStructDescriptor<EnumType,_Field> descriptor() {
            return kDescriptor;
        }
    }

    /**
     * Make a model.EnumType builder.
     * @return The builder instance.
     */
    public static _Builder builder() {
        return new _Builder();
    }

    /**
     * enum {
     *   (&lt;value&gt; ([;,])?)*
     * }
     */
    public static class _Builder
            extends net.morimekta.providence.PMessageBuilder<EnumType,_Field>
            implements net.morimekta.providence.serializer.rw.BinaryReader {
        private java.util.BitSet optionals;
        private java.util.BitSet modified;

        private String mDocumentation;
        private String mName;
        private java.util.List<net.morimekta.providence.model.EnumValue> mValues;
        private java.util.Map<String,String> mAnnotations;

        /**
         * Make a model.EnumType builder.
         */
        public _Builder() {
            optionals = new java.util.BitSet(4);
            modified = new java.util.BitSet(4);
            mName = kDefaultName;
            mValues = kDefaultValues;
        }

        /**
         * Make a mutating builder off a base model.EnumType.
         *
         * @param base The base EnumType
         */
        public _Builder(EnumType base) {
            this();

            if (base.hasDocumentation()) {
                optionals.set(0);
                mDocumentation = base.mDocumentation;
            }
            optionals.set(1);
            mName = base.mName;
            optionals.set(2);
            mValues = base.mValues;
            if (base.hasAnnotations()) {
                optionals.set(3);
                mAnnotations = base.mAnnotations;
            }
        }

        @javax.annotation.Nonnull
        @Override
        public _Builder merge(EnumType from) {
            if (from.hasDocumentation()) {
                optionals.set(0);
                modified.set(0);
                mDocumentation = from.getDocumentation();
            }

            optionals.set(1);
            modified.set(1);
            mName = from.getName();

            optionals.set(2);
            modified.set(2);
            mValues = from.getValues();

            if (from.hasAnnotations()) {
                optionals.set(3);
                modified.set(3);
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
         * Sets the value of values.
         *
         * @param value The new value
         * @return The builder
         */
        @javax.annotation.Nonnull
        public _Builder setValues(java.util.Collection<net.morimekta.providence.model.EnumValue> value) {
            if (value == null) {
                return clearValues();
            }

            optionals.set(2);
            modified.set(2);
            mValues = com.google.common.collect.ImmutableList.copyOf(value);
            return this;
        }

        /**
         * Adds entries to values.
         *
         * @param values The added value
         * @return The builder
         */
        @javax.annotation.Nonnull
        public _Builder addToValues(net.morimekta.providence.model.EnumValue... values) {
            optionals.set(2);
            modified.set(2);
            java.util.List<net.morimekta.providence.model.EnumValue> _container = mutableValues();
            for (net.morimekta.providence.model.EnumValue item : values) {
                _container.add(item);
            }
            return this;
        }

        /**
         * Checks for presence of the values field.
         *
         * @return True if values has been set.
         */
        public boolean isSetValues() {
            return optionals.get(2);
        }

        /**
         * Checks if values has been modified since the _Builder was created.
         *
         * @return True if values has been modified.
         */
        public boolean isModifiedValues() {
            return modified.get(2);
        }

        /**
         * Clears the values field.
         *
         * @return The builder
         */
        @javax.annotation.Nonnull
        public _Builder clearValues() {
            optionals.clear(2);
            modified.set(2);
            mValues = kDefaultValues;
            return this;
        }

        /**
         * Gets the builder for the contained values.
         *
         * @return The field builder
         */
        @javax.annotation.Nonnull
        public java.util.List<net.morimekta.providence.model.EnumValue> mutableValues() {
            optionals.set(2);
            modified.set(2);

            if (mValues == null) {
                mValues = new java.util.LinkedList<>();
            } else if (!(mValues instanceof java.util.LinkedList)) {
                mValues = new java.util.LinkedList<>(mValues);
            }
            return mValues;
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

            optionals.set(3);
            modified.set(3);
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
            optionals.set(3);
            modified.set(3);
            mutableAnnotations().put(key, value);
            return this;
        }

        /**
         * Checks for presence of the annotations field.
         *
         * @return True if annotations has been set.
         */
        public boolean isSetAnnotations() {
            return optionals.get(3);
        }

        /**
         * Checks if annotations has been modified since the _Builder was created.
         *
         * @return True if annotations has been modified.
         */
        public boolean isModifiedAnnotations() {
            return modified.get(3);
        }

        /**
         * Clears the annotations field.
         *
         * @return The builder
         */
        @javax.annotation.Nonnull
        public _Builder clearAnnotations() {
            optionals.clear(3);
            modified.set(3);
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
            optionals.set(3);
            modified.set(3);

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
            EnumType._Builder other = (EnumType._Builder) o;
            return java.util.Objects.equals(optionals, other.optionals) &&
                   java.util.Objects.equals(mDocumentation, other.mDocumentation) &&
                   java.util.Objects.equals(mName, other.mName) &&
                   java.util.Objects.equals(mValues, other.mValues) &&
                   java.util.Objects.equals(mAnnotations, other.mAnnotations);
        }

        @Override
        public int hashCode() {
            return java.util.Objects.hash(
                    EnumType.class, optionals,
                    _Field.DOCUMENTATION, mDocumentation,
                    _Field.NAME, mName,
                    _Field.VALUES, mValues,
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
                case 3: setValues((java.util.List<net.morimekta.providence.model.EnumValue>) value); break;
                case 4: setAnnotations((java.util.Map<String,String>) value); break;
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
                default: break;
            }
            return false;
        }

        @Override
        public _Builder addTo(int key, Object value) {
            switch (key) {
                case 3: addToValues((net.morimekta.providence.model.EnumValue) value); break;
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
                case 3: clearValues(); break;
                case 4: clearAnnotations(); break;
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
                java.util.LinkedList<String> missing = new java.util.LinkedList<>();

                if (!optionals.get(1)) {
                    missing.add("name");
                }

                throw new java.lang.IllegalStateException(
                        "Missing required fields " +
                        String.join(",", missing) +
                        " in message model.EnumType");
            }
        }

        @javax.annotation.Nonnull
        @Override
        public net.morimekta.providence.descriptor.PStructDescriptor<EnumType,_Field> descriptor() {
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
                            throw new net.morimekta.providence.serializer.SerializerException("Wrong type " + net.morimekta.providence.serializer.rw.BinaryType.asString(type) + " for model.EnumType.documentation, should be struct(12)");
                        }
                        break;
                    }
                    case 2: {
                        if (type == 11) {
                            int len_2 = reader.expectUInt32();
                            mName = new String(reader.expectBytes(len_2), java.nio.charset.StandardCharsets.UTF_8);
                            optionals.set(1);
                        } else {
                            throw new net.morimekta.providence.serializer.SerializerException("Wrong type " + net.morimekta.providence.serializer.rw.BinaryType.asString(type) + " for model.EnumType.name, should be struct(12)");
                        }
                        break;
                    }
                    case 3: {
                        if (type == 15) {
                            net.morimekta.providence.descriptor.PList.DefaultBuilder<net.morimekta.providence.model.EnumValue> b_3 = new net.morimekta.providence.descriptor.PList.DefaultBuilder<>();
                            byte t_5 = reader.expectByte();
                            if (t_5 == 12) {
                                final int len_4 = reader.expectUInt32();
                                for (int i_6 = 0; i_6 < len_4; ++i_6) {
                                    net.morimekta.providence.model.EnumValue key_7 = net.morimekta.providence.serializer.rw.BinaryFormatUtils.readMessage(reader, net.morimekta.providence.model.EnumValue.kDescriptor, strict);
                                    b_3.add(key_7);
                                }
                                mValues = b_3.build();
                            } else {
                                throw new net.morimekta.providence.serializer.SerializerException("Wrong item type " + net.morimekta.providence.serializer.rw.BinaryType.asString(t_5) + " for model.EnumType.values, should be struct(12)");
                            }
                            optionals.set(2);
                        } else {
                            throw new net.morimekta.providence.serializer.SerializerException("Wrong type " + net.morimekta.providence.serializer.rw.BinaryType.asString(type) + " for model.EnumType.values, should be struct(12)");
                        }
                        break;
                    }
                    case 4: {
                        if (type == 13) {
                            net.morimekta.providence.descriptor.PMap.SortedBuilder<String,String> b_8 = new net.morimekta.providence.descriptor.PMap.SortedBuilder<>();
                            byte t_10 = reader.expectByte();
                            byte t_11 = reader.expectByte();
                            if (t_10 == 11 && t_11 == 11) {
                                final int len_9 = reader.expectUInt32();
                                for (int i_12 = 0; i_12 < len_9; ++i_12) {
                                    int len_15 = reader.expectUInt32();
                                    String key_13 = new String(reader.expectBytes(len_15), java.nio.charset.StandardCharsets.UTF_8);
                                    int len_16 = reader.expectUInt32();
                                    String val_14 = new String(reader.expectBytes(len_16), java.nio.charset.StandardCharsets.UTF_8);
                                    b_8.put(key_13, val_14);
                                }
                                mAnnotations = b_8.build();
                            } else {
                                throw new net.morimekta.providence.serializer.SerializerException(
                                        "Wrong key type " + net.morimekta.providence.serializer.rw.BinaryType.asString(t_10) +
                                        " or value type " + net.morimekta.providence.serializer.rw.BinaryType.asString(t_11) +
                                        " for model.EnumType.annotations, should be string(11) and string(11)");
                            }
                            optionals.set(3);
                        } else {
                            throw new net.morimekta.providence.serializer.SerializerException("Wrong type " + net.morimekta.providence.serializer.rw.BinaryType.asString(type) + " for model.EnumType.annotations, should be struct(12)");
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
        public EnumType build() {
            return new EnumType(this);
        }
    }
}
