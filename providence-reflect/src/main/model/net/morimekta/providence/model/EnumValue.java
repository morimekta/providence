package net.morimekta.providence.model;

/**
 * &lt;name&gt; (= &lt;value&gt;)
 */
@SuppressWarnings("unused")
public class EnumValue
        implements net.morimekta.providence.PMessage<EnumValue,EnumValue._Field>,
                   Comparable<EnumValue>,
                   java.io.Serializable,
                   net.morimekta.providence.serializer.rw.BinaryWriter {
    private final static long serialVersionUID = -4079600082644582517L;

    private final static int kDefaultValue = 0;

    private final String mDocumentation;
    private final String mName;
    private final int mValue;
    private final java.util.Map<String,String> mAnnotations;

    private volatile int tHashCode;

    public EnumValue(String pDocumentation,
                     String pName,
                     Integer pValue,
                     java.util.Map<String,String> pAnnotations) {
        mDocumentation = pDocumentation;
        mName = pName;
        if (pValue != null) {
            mValue = pValue;
        } else {
            mValue = kDefaultValue;
        }
        if (pAnnotations != null) {
            mAnnotations = com.google.common.collect.ImmutableMap.copyOf(pAnnotations);
        } else {
            mAnnotations = null;
        }
    }

    private EnumValue(_Builder builder) {
        mDocumentation = builder.mDocumentation;
        mName = builder.mName;
        mValue = builder.mValue;
        if (builder.isSetAnnotations()) {
            mAnnotations = builder.mAnnotations.build();
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
        return mName != null;
    }

    /**
     * @return The field value
     */
    public String getName() {
        return mName;
    }

    public boolean hasValue() {
        return true;
    }

    /**
     * @return The field value
     */
    public int getValue() {
        return mValue;
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
            case 2: return hasName();
            case 3: return true;
            case 4: return hasAnnotations();
            default: return false;
        }
    }

    @Override
    public int num(int key) {
        switch(key) {
            case 1: return hasDocumentation() ? 1 : 0;
            case 2: return hasName() ? 1 : 0;
            case 3: return 1;
            case 4: return numAnnotations();
            default: return 0;
        }
    }

    @Override
    public Object get(int key) {
        switch(key) {
            case 1: return getDocumentation();
            case 2: return getName();
            case 3: return getValue();
            case 4: return getAnnotations();
            default: return null;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        if (o == null || !o.getClass().equals(getClass())) return false;
        EnumValue other = (EnumValue) o;
        return java.util.Objects.equals(mDocumentation, other.mDocumentation) &&
               java.util.Objects.equals(mName, other.mName) &&
               java.util.Objects.equals(mValue, other.mValue) &&
               java.util.Objects.equals(mAnnotations, other.mAnnotations);
    }

    @Override
    public int hashCode() {
        if (tHashCode == 0) {
            tHashCode = java.util.Objects.hash(
                    EnumValue.class,
                    _Field.DOCUMENTATION, mDocumentation,
                    _Field.NAME, mName,
                    _Field.VALUE, mValue,
                    _Field.ANNOTATIONS, mAnnotations);
        }
        return tHashCode;
    }

    @Override
    public String toString() {
        return "model.EnumValue" + asString();
    }

    @Override
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
        if (hasName()) {
            if (first) first = false;
            else out.append(',');
            out.append("name:")
               .append('\"')
               .append(net.morimekta.util.Strings.escape(mName))
               .append('\"');
        }
        out.append("value:")
           .append(mValue);
        if (hasAnnotations()) {
            out.append(',');
            out.append("annotations:")
               .append(net.morimekta.util.Strings.asString(mAnnotations));
        }
        out.append('}');
        return out.toString();
    }

    @Override
    public int compareTo(EnumValue other) {
        int c;

        c = Boolean.compare(mDocumentation != null, other.mDocumentation != null);
        if (c != 0) return c;
        if (mDocumentation != null) {
            c = mDocumentation.compareTo(other.mDocumentation);
            if (c != 0) return c;
        }

        c = Boolean.compare(mName != null, other.mName != null);
        if (c != 0) return c;
        if (mName != null) {
            c = mName.compareTo(other.mName);
            if (c != 0) return c;
        }

        c = Integer.compare(mValue, other.mValue);
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

        if (hasName()) {
            length += writer.writeByte((byte) 11);
            length += writer.writeShort((short) 2);
            net.morimekta.util.Binary tmp_2 = net.morimekta.util.Binary.wrap(mName.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            length += writer.writeUInt32(tmp_2.length());
            length += writer.writeBinary(tmp_2);
        }

        length += writer.writeByte((byte) 8);
        length += writer.writeShort((short) 3);
        length += writer.writeInt(mValue);

        if (hasAnnotations()) {
            length += writer.writeByte((byte) 13);
            length += writer.writeShort((short) 4);
            length += writer.writeByte((byte) 11);
            length += writer.writeByte((byte) 11);
            length += writer.writeUInt32(mAnnotations.size());
            for (java.util.Map.Entry<String,String> entry_3 : mAnnotations.entrySet()) {
                net.morimekta.util.Binary tmp_4 = net.morimekta.util.Binary.wrap(entry_3.getKey().getBytes(java.nio.charset.StandardCharsets.UTF_8));
                length += writer.writeUInt32(tmp_4.length());
                length += writer.writeBinary(tmp_4);
                net.morimekta.util.Binary tmp_5 = net.morimekta.util.Binary.wrap(entry_3.getValue().getBytes(java.nio.charset.StandardCharsets.UTF_8));
                length += writer.writeUInt32(tmp_5.length());
                length += writer.writeBinary(tmp_5);
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
        DOCUMENTATION(1, net.morimekta.providence.descriptor.PRequirement.DEFAULT, "documentation", net.morimekta.providence.descriptor.PPrimitive.STRING.provider(), null),
        NAME(2, net.morimekta.providence.descriptor.PRequirement.REQUIRED, "name", net.morimekta.providence.descriptor.PPrimitive.STRING.provider(), null),
        VALUE(3, net.morimekta.providence.descriptor.PRequirement.DEFAULT, "value", net.morimekta.providence.descriptor.PPrimitive.I32.provider(), null),
        ANNOTATIONS(4, net.morimekta.providence.descriptor.PRequirement.DEFAULT, "annotations", net.morimekta.providence.descriptor.PMap.provider(net.morimekta.providence.descriptor.PPrimitive.STRING.provider(),net.morimekta.providence.descriptor.PPrimitive.STRING.provider()), null),
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
                case 3: return _Field.VALUE;
                case 4: return _Field.ANNOTATIONS;
            }
            return null;
        }

        public static _Field forName(String name) {
            switch (name) {
                case "documentation": return _Field.DOCUMENTATION;
                case "name": return _Field.NAME;
                case "value": return _Field.VALUE;
                case "annotations": return _Field.ANNOTATIONS;
            }
            return null;
        }
    }

    public static net.morimekta.providence.descriptor.PStructDescriptorProvider<EnumValue,_Field> provider() {
        return new _Provider();
    }

    @Override
    public net.morimekta.providence.descriptor.PStructDescriptor<EnumValue,_Field> descriptor() {
        return kDescriptor;
    }

    public static final net.morimekta.providence.descriptor.PStructDescriptor<EnumValue,_Field> kDescriptor;

    private static class _Descriptor
            extends net.morimekta.providence.descriptor.PStructDescriptor<EnumValue,_Field> {
        public _Descriptor() {
            super("model", "EnumValue", new _Factory(), false);
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

    private final static class _Provider extends net.morimekta.providence.descriptor.PStructDescriptorProvider<EnumValue,_Field> {
        @Override
        public net.morimekta.providence.descriptor.PStructDescriptor<EnumValue,_Field> descriptor() {
            return kDescriptor;
        }
    }

    private final static class _Factory
            extends net.morimekta.providence.PMessageBuilderFactory<EnumValue,_Field> {
        @Override
        public _Builder builder() {
            return new _Builder();
        }
    }

    /**
     * Make a model.EnumValue builder.
     * @return The builder instance.
     */
    public static _Builder builder() {
        return new _Builder();
    }

    /**
     * &lt;name&gt; (= &lt;value&gt;)
     */
    public static class _Builder
            extends net.morimekta.providence.PMessageBuilder<EnumValue,_Field>
            implements net.morimekta.providence.serializer.rw.BinaryReader {
        private java.util.BitSet optionals;
        private java.util.BitSet modified;

        private String mDocumentation;
        private String mName;
        private int mValue;
        private net.morimekta.providence.descriptor.PMap.Builder<String,String> mAnnotations;

        /**
         * Make a model.EnumValue builder.
         */
        public _Builder() {
            optionals = new java.util.BitSet(4);
            modified = new java.util.BitSet(4);
            mValue = kDefaultValue;
            mAnnotations = new net.morimekta.providence.descriptor.PMap.ImmutableMapBuilder<>();
        }

        /**
         * Make a mutating builder off a base model.EnumValue.
         *
         * @param base The base EnumValue
         */
        public _Builder(EnumValue base) {
            this();

            if (base.hasDocumentation()) {
                optionals.set(0);
                mDocumentation = base.mDocumentation;
            }
            if (base.hasName()) {
                optionals.set(1);
                mName = base.mName;
            }
            optionals.set(2);
            mValue = base.mValue;
            if (base.hasAnnotations()) {
                optionals.set(3);
                mAnnotations.putAll(base.mAnnotations);
            }
        }

        @javax.annotation.Nonnull
        @Override
        public _Builder merge(EnumValue from) {
            if (from.hasDocumentation()) {
                optionals.set(0);
                modified.set(0);
                mDocumentation = from.getDocumentation();
            }

            if (from.hasName()) {
                optionals.set(1);
                modified.set(1);
                mName = from.getName();
            }

            optionals.set(2);
            modified.set(2);
            mValue = from.getValue();

            if (from.hasAnnotations()) {
                optionals.set(3);
                modified.set(3);
                mAnnotations.putAll(from.getAnnotations());
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
            mName = null;
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
         * Sets the value of value.
         *
         * @param value The new value
         * @return The builder
         */
        @javax.annotation.Nonnull
        public _Builder setValue(int value) {
            optionals.set(2);
            modified.set(2);
            mValue = value;
            return this;
        }

        /**
         * Checks for presence of the value field.
         *
         * @return True if value has been set.
         */
        public boolean isSetValue() {
            return optionals.get(2);
        }

        /**
         * Checks if value has been modified since the _Builder was created.
         *
         * @return True if value has been modified.
         */
        public boolean isModifiedValue() {
            return modified.get(2);
        }

        /**
         * Clears the value field.
         *
         * @return The builder
         */
        @javax.annotation.Nonnull
        public _Builder clearValue() {
            optionals.clear(2);
            modified.set(2);
            mValue = kDefaultValue;
            return this;
        }

        /**
         * Gets the value of the contained value.
         *
         * @return The field value
         */
        public int getValue() {
            return mValue;
        }

        /**
         * Sets the value of annotations.
         *
         * @param value The new value
         * @return The builder
         */
        @javax.annotation.Nonnull
        public _Builder setAnnotations(java.util.Map<String,String> value) {
            optionals.set(3);
            modified.set(3);
            mAnnotations.clear();
            mAnnotations.putAll(value);
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
            mAnnotations.put(key, value);
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
            mAnnotations.clear();
            return this;
        }

        /**
         * Gets the builder for the contained annotations.
         *
         * @return The field builder
         */
        public net.morimekta.providence.descriptor.PMap.Builder<String,String> mutableAnnotations() {
            optionals.set(3);
            modified.set(3);
            return mAnnotations;
        }

        @Override
        public boolean equals(Object o) {
            if (o == this) return true;
            if (o == null || !o.getClass().equals(getClass())) return false;
            EnumValue._Builder other = (EnumValue._Builder) o;
            return java.util.Objects.equals(optionals, other.optionals) &&
                   java.util.Objects.equals(mDocumentation, other.mDocumentation) &&
                   java.util.Objects.equals(mName, other.mName) &&
                   java.util.Objects.equals(mValue, other.mValue) &&
                   java.util.Objects.equals(mAnnotations, other.mAnnotations);
        }

        @Override
        public int hashCode() {
            return java.util.Objects.hash(
                    EnumValue.class, optionals,
                    _Field.DOCUMENTATION, mDocumentation,
                    _Field.NAME, mName,
                    _Field.VALUE, mValue,
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
                case 3: setValue((int) value); break;
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
                case 3: clearValue(); break;
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
                        " in message model.EnumValue");
            }
        }

        @javax.annotation.Nonnull
        @Override
        public net.morimekta.providence.descriptor.PStructDescriptor<EnumValue,_Field> descriptor() {
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
                            throw new net.morimekta.providence.serializer.SerializerException("Wrong type " + net.morimekta.providence.serializer.rw.BinaryType.asString(type) + " for model.EnumValue.documentation, should be struct(12)");
                        }
                        break;
                    }
                    case 2: {
                        if (type == 11) {
                            int len_2 = reader.expectUInt32();
                            mName = new String(reader.expectBytes(len_2), java.nio.charset.StandardCharsets.UTF_8);
                            optionals.set(1);
                        } else {
                            throw new net.morimekta.providence.serializer.SerializerException("Wrong type " + net.morimekta.providence.serializer.rw.BinaryType.asString(type) + " for model.EnumValue.name, should be struct(12)");
                        }
                        break;
                    }
                    case 3: {
                        if (type == 8) {
                            mValue = reader.expectInt();
                            optionals.set(2);
                        } else {
                            throw new net.morimekta.providence.serializer.SerializerException("Wrong type " + net.morimekta.providence.serializer.rw.BinaryType.asString(type) + " for model.EnumValue.value, should be struct(12)");
                        }
                        break;
                    }
                    case 4: {
                        if (type == 13) {
                            byte t_4 = reader.expectByte();
                            byte t_5 = reader.expectByte();
                            if (t_4 == 11 && t_5 == 11) {
                                final int len_3 = reader.expectUInt32();
                                for (int i_6 = 0; i_6 < len_3; ++i_6) {
                                    int len_9 = reader.expectUInt32();
                                    String key_7 = new String(reader.expectBytes(len_9), java.nio.charset.StandardCharsets.UTF_8);
                                    int len_10 = reader.expectUInt32();
                                    String val_8 = new String(reader.expectBytes(len_10), java.nio.charset.StandardCharsets.UTF_8);
                                    mAnnotations.put(key_7, val_8);
                                }
                            } else {
                                throw new net.morimekta.providence.serializer.SerializerException(
                                        "Wrong key type " + net.morimekta.providence.serializer.rw.BinaryType.asString(t_4) +
                                        " or value type " + net.morimekta.providence.serializer.rw.BinaryType.asString(t_5) +
                                        " for model.EnumValue.annotations, should be string(11) and string(11)");
                            }
                            optionals.set(3);
                        } else {
                            throw new net.morimekta.providence.serializer.SerializerException("Wrong type " + net.morimekta.providence.serializer.rw.BinaryType.asString(type) + " for model.EnumValue.annotations, should be struct(12)");
                        }
                        break;
                    }
                    default: {
                        if (strict) {
                            throw new net.morimekta.providence.serializer.SerializerException("No field with id " + field + " exists in model.EnumValue");
                        } else {
                            net.morimekta.providence.serializer.rw.BinaryFormatUtils.readFieldValue(reader, new net.morimekta.providence.serializer.rw.BinaryFormatUtils.FieldInfo(field, type), null, false);
                        }
                        break;
                    }
                }
                type = reader.expectByte();
            }
        }

        @Override
        public EnumValue build() {
            return new EnumValue(this);
        }
    }
}
