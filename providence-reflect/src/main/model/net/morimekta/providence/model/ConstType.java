package net.morimekta.providence.model;

/**
 * const &lt;type&gt; &lt;name&gt; = &lt;value&gt;
 */
@SuppressWarnings("unused")
public class ConstType
        implements net.morimekta.providence.PMessage<ConstType,ConstType._Field>,
                   Comparable<ConstType>,
                   java.io.Serializable,
                   net.morimekta.providence.serializer.rw.BinaryWriter {
    private final static long serialVersionUID = -7030319550715264712L;

    private final String mDocumentation;
    private final String mType;
    private final String mName;
    private final String mValue;
    private final java.util.Map<String,String> mAnnotations;

    private volatile int tHashCode;

    public ConstType(String pDocumentation,
                     String pType,
                     String pName,
                     String pValue,
                     java.util.Map<String,String> pAnnotations) {
        mDocumentation = pDocumentation;
        mType = pType;
        mName = pName;
        mValue = pValue;
        if (pAnnotations != null) {
            mAnnotations = com.google.common.collect.ImmutableMap.copyOf(pAnnotations);
        } else {
            mAnnotations = null;
        }
    }

    private ConstType(_Builder builder) {
        mDocumentation = builder.mDocumentation;
        mType = builder.mType;
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

    public boolean hasType() {
        return mType != null;
    }

    /**
     * @return The field value
     */
    public String getType() {
        return mType;
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
        return mValue != null;
    }

    /**
     * @return The field value
     */
    public String getValue() {
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
            case 4: return hasType();
            case 5: return hasName();
            case 6: return hasValue();
            case 7: return hasAnnotations();
            default: return false;
        }
    }

    @Override
    public int num(int key) {
        switch(key) {
            case 1: return hasDocumentation() ? 1 : 0;
            case 4: return hasType() ? 1 : 0;
            case 5: return hasName() ? 1 : 0;
            case 6: return hasValue() ? 1 : 0;
            case 7: return numAnnotations();
            default: return 0;
        }
    }

    @Override
    public Object get(int key) {
        switch(key) {
            case 1: return getDocumentation();
            case 4: return getType();
            case 5: return getName();
            case 6: return getValue();
            case 7: return getAnnotations();
            default: return null;
        }
    }

    @Override
    public boolean compact() {
        return false;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        if (o == null || !o.getClass().equals(getClass())) return false;
        ConstType other = (ConstType) o;
        return java.util.Objects.equals(mDocumentation, other.mDocumentation) &&
               java.util.Objects.equals(mType, other.mType) &&
               java.util.Objects.equals(mName, other.mName) &&
               java.util.Objects.equals(mValue, other.mValue) &&
               java.util.Objects.equals(mAnnotations, other.mAnnotations);
    }

    @Override
    public int hashCode() {
        if (tHashCode == 0) {
            tHashCode = java.util.Objects.hash(
                    ConstType.class,
                    _Field.DOCUMENTATION, mDocumentation,
                    _Field.TYPE, mType,
                    _Field.NAME, mName,
                    _Field.VALUE, mValue,
                    _Field.ANNOTATIONS, mAnnotations);
        }
        return tHashCode;
    }

    @Override
    public String toString() {
        return "model.ConstType" + asString();
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
        if (hasType()) {
            if (first) first = false;
            else out.append(',');
            out.append("type:")
               .append('\"')
               .append(net.morimekta.util.Strings.escape(mType))
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
        if (hasValue()) {
            if (first) first = false;
            else out.append(',');
            out.append("value:")
               .append('\"')
               .append(net.morimekta.util.Strings.escape(mValue))
               .append('\"');
        }
        if (hasAnnotations()) {
            if (!first) out.append(',');
            out.append("annotations:")
               .append(net.morimekta.util.Strings.asString(mAnnotations));
        }
        out.append('}');
        return out.toString();
    }

    @Override
    public int compareTo(ConstType other) {
        int c;

        c = Boolean.compare(mDocumentation != null, other.mDocumentation != null);
        if (c != 0) return c;
        if (mDocumentation != null) {
            c = mDocumentation.compareTo(other.mDocumentation);
            if (c != 0) return c;
        }

        c = Boolean.compare(mType != null, other.mType != null);
        if (c != 0) return c;
        if (mType != null) {
            c = mType.compareTo(other.mType);
            if (c != 0) return c;
        }

        c = Boolean.compare(mName != null, other.mName != null);
        if (c != 0) return c;
        if (mName != null) {
            c = mName.compareTo(other.mName);
            if (c != 0) return c;
        }

        c = Boolean.compare(mValue != null, other.mValue != null);
        if (c != 0) return c;
        if (mValue != null) {
            c = mValue.compareTo(other.mValue);
            if (c != 0) return c;
        }

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

        if (hasType()) {
            length += writer.writeByte((byte) 11);
            length += writer.writeShort((short) 4);
            net.morimekta.util.Binary tmp_2 = net.morimekta.util.Binary.wrap(mType.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            length += writer.writeUInt32(tmp_2.length());
            length += writer.writeBinary(tmp_2);
        }

        if (hasName()) {
            length += writer.writeByte((byte) 11);
            length += writer.writeShort((short) 5);
            net.morimekta.util.Binary tmp_3 = net.morimekta.util.Binary.wrap(mName.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            length += writer.writeUInt32(tmp_3.length());
            length += writer.writeBinary(tmp_3);
        }

        if (hasValue()) {
            length += writer.writeByte((byte) 11);
            length += writer.writeShort((short) 6);
            net.morimekta.util.Binary tmp_4 = net.morimekta.util.Binary.wrap(mValue.getBytes(java.nio.charset.StandardCharsets.UTF_8));
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

        length += writer.writeByte((byte) 0);
        return length;
    }

    @Override
    public _Builder mutate() {
        return new _Builder(this);
    }

    public enum _Field implements net.morimekta.providence.descriptor.PField {
        DOCUMENTATION(1, net.morimekta.providence.descriptor.PRequirement.DEFAULT, "documentation", net.morimekta.providence.descriptor.PPrimitive.STRING.provider(), null),
        TYPE(4, net.morimekta.providence.descriptor.PRequirement.REQUIRED, "type", net.morimekta.providence.descriptor.PPrimitive.STRING.provider(), null),
        NAME(5, net.morimekta.providence.descriptor.PRequirement.REQUIRED, "name", net.morimekta.providence.descriptor.PPrimitive.STRING.provider(), null),
        VALUE(6, net.morimekta.providence.descriptor.PRequirement.REQUIRED, "value", net.morimekta.providence.descriptor.PPrimitive.STRING.provider(), null),
        ANNOTATIONS(7, net.morimekta.providence.descriptor.PRequirement.DEFAULT, "annotations", net.morimekta.providence.descriptor.PMap.provider(net.morimekta.providence.descriptor.PPrimitive.STRING.provider(),net.morimekta.providence.descriptor.PPrimitive.STRING.provider()), null),
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
            return net.morimekta.providence.descriptor.PField.toString(this);
        }

        public static _Field forKey(int key) {
            switch (key) {
                case 1: return _Field.DOCUMENTATION;
                case 4: return _Field.TYPE;
                case 5: return _Field.NAME;
                case 6: return _Field.VALUE;
                case 7: return _Field.ANNOTATIONS;
            }
            return null;
        }

        public static _Field forName(String name) {
            switch (name) {
                case "documentation": return _Field.DOCUMENTATION;
                case "type": return _Field.TYPE;
                case "name": return _Field.NAME;
                case "value": return _Field.VALUE;
                case "annotations": return _Field.ANNOTATIONS;
            }
            return null;
        }
    }

    public static net.morimekta.providence.descriptor.PStructDescriptorProvider<ConstType,_Field> provider() {
        return new _Provider();
    }

    @Override
    public net.morimekta.providence.descriptor.PStructDescriptor<ConstType,_Field> descriptor() {
        return kDescriptor;
    }

    public static final net.morimekta.providence.descriptor.PStructDescriptor<ConstType,_Field> kDescriptor;

    private static class _Descriptor
            extends net.morimekta.providence.descriptor.PStructDescriptor<ConstType,_Field> {
        public _Descriptor() {
            super("model", "ConstType", new _Factory(), false, false);
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

    private final static class _Provider extends net.morimekta.providence.descriptor.PStructDescriptorProvider<ConstType,_Field> {
        @Override
        public net.morimekta.providence.descriptor.PStructDescriptor<ConstType,_Field> descriptor() {
            return kDescriptor;
        }
    }

    private final static class _Factory
            extends net.morimekta.providence.PMessageBuilderFactory<ConstType,_Field> {
        @Override
        public _Builder builder() {
            return new _Builder();
        }
    }

    /**
     * Make a model.ConstType builder.
     * @return The builder instance.
     */
    public static _Builder builder() {
        return new _Builder();
    }

    /**
     * const &lt;type&gt; &lt;name&gt; = &lt;value&gt;
     */
    public static class _Builder
            extends net.morimekta.providence.PMessageBuilder<ConstType,_Field>
            implements net.morimekta.providence.serializer.rw.BinaryReader {
        private java.util.BitSet optionals;

        private String mDocumentation;
        private String mType;
        private String mName;
        private String mValue;
        private net.morimekta.providence.descriptor.PMap.Builder<String,String> mAnnotations;

        /**
         * Make a model.ConstType builder.
         */
        public _Builder() {
            optionals = new java.util.BitSet(5);
            mAnnotations = new net.morimekta.providence.descriptor.PMap.ImmutableMapBuilder<>();
        }

        /**
         * Make a mutating builder off a base model.ConstType.
         *
         * @param base The base ConstType
         */
        public _Builder(ConstType base) {
            this();

            if (base.hasDocumentation()) {
                optionals.set(0);
                mDocumentation = base.mDocumentation;
            }
            if (base.hasType()) {
                optionals.set(1);
                mType = base.mType;
            }
            if (base.hasName()) {
                optionals.set(2);
                mName = base.mName;
            }
            if (base.hasValue()) {
                optionals.set(3);
                mValue = base.mValue;
            }
            if (base.hasAnnotations()) {
                optionals.set(4);
                mAnnotations.putAll(base.mAnnotations);
            }
        }

        @Override
        public _Builder merge(ConstType from) {
            if (from.hasDocumentation()) {
                optionals.set(0);
                mDocumentation = from.getDocumentation();
            }

            if (from.hasType()) {
                optionals.set(1);
                mType = from.getType();
            }

            if (from.hasName()) {
                optionals.set(2);
                mName = from.getName();
            }

            if (from.hasValue()) {
                optionals.set(3);
                mValue = from.getValue();
            }

            if (from.hasAnnotations()) {
                optionals.set(4);
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
        public _Builder setDocumentation(String value) {
            optionals.set(0);
            mDocumentation = value;
            return this;
        }

        /**
         * Checks for presence of the documentation field.
         *
         * @return True iff documentation has been set.
         */
        public boolean isSetDocumentation() {
            return optionals.get(0);
        }

        /**
         * Clears the documentation field.
         *
         * @return The builder
         */
        public _Builder clearDocumentation() {
            optionals.clear(0);
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
         * Sets the value of type.
         *
         * @param value The new value
         * @return The builder
         */
        public _Builder setType(String value) {
            optionals.set(1);
            mType = value;
            return this;
        }

        /**
         * Checks for presence of the type field.
         *
         * @return True iff type has been set.
         */
        public boolean isSetType() {
            return optionals.get(1);
        }

        /**
         * Clears the type field.
         *
         * @return The builder
         */
        public _Builder clearType() {
            optionals.clear(1);
            mType = null;
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
        public _Builder setName(String value) {
            optionals.set(2);
            mName = value;
            return this;
        }

        /**
         * Checks for presence of the name field.
         *
         * @return True iff name has been set.
         */
        public boolean isSetName() {
            return optionals.get(2);
        }

        /**
         * Clears the name field.
         *
         * @return The builder
         */
        public _Builder clearName() {
            optionals.clear(2);
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
        public _Builder setValue(String value) {
            optionals.set(3);
            mValue = value;
            return this;
        }

        /**
         * Checks for presence of the value field.
         *
         * @return True iff value has been set.
         */
        public boolean isSetValue() {
            return optionals.get(3);
        }

        /**
         * Clears the value field.
         *
         * @return The builder
         */
        public _Builder clearValue() {
            optionals.clear(3);
            mValue = null;
            return this;
        }

        /**
         * Gets the value of the contained value.
         *
         * @return The field value
         */
        public String getValue() {
            return mValue;
        }

        /**
         * Sets the value of annotations.
         *
         * @param value The new value
         * @return The builder
         */
        public _Builder setAnnotations(java.util.Map<String,String> value) {
            optionals.set(4);
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
        public _Builder putInAnnotations(String key, String value) {
            optionals.set(4);
            mAnnotations.put(key, value);
            return this;
        }

        /**
         * Checks for presence of the annotations field.
         *
         * @return True iff annotations has been set.
         */
        public boolean isSetAnnotations() {
            return optionals.get(4);
        }

        /**
         * Clears the annotations field.
         *
         * @return The builder
         */
        public _Builder clearAnnotations() {
            optionals.clear(4);
            mAnnotations.clear();
            return this;
        }

        /**
         * Gets the builder for the contained annotations.
         *
         * @return The field builder
         */
        public net.morimekta.providence.descriptor.PMap.Builder<String,String> mutableAnnotations() {
            optionals.set(4);
            return mAnnotations;
        }

        @Override
        public boolean equals(Object o) {
            if (o == this) return true;
            if (o == null || !o.getClass().equals(getClass())) return false;
            ConstType._Builder other = (ConstType._Builder) o;
            return java.util.Objects.equals(optionals, other.optionals) &&
                   java.util.Objects.equals(mDocumentation, other.mDocumentation) &&
                   java.util.Objects.equals(mType, other.mType) &&
                   java.util.Objects.equals(mName, other.mName) &&
                   java.util.Objects.equals(mValue, other.mValue) &&
                   java.util.Objects.equals(mAnnotations, other.mAnnotations);
        }

        @Override
        public int hashCode() {
            return java.util.Objects.hash(
                    ConstType.class, optionals,
                    _Field.DOCUMENTATION, mDocumentation,
                    _Field.TYPE, mType,
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

        @Override
        @SuppressWarnings("unchecked")
        public _Builder set(int key, Object value) {
            if (value == null) return clear(key);
            switch (key) {
                case 1: setDocumentation((String) value); break;
                case 4: setType((String) value); break;
                case 5: setName((String) value); break;
                case 6: setValue((String) value); break;
                case 7: setAnnotations((java.util.Map<String,String>) value); break;
                default: break;
            }
            return this;
        }

        @Override
        public boolean isSet(int key) {
            switch (key) {
                case 1: return optionals.get(0);
                case 4: return optionals.get(1);
                case 5: return optionals.get(2);
                case 6: return optionals.get(3);
                case 7: return optionals.get(4);
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

        @Override
        public _Builder clear(int key) {
            switch (key) {
                case 1: clearDocumentation(); break;
                case 4: clearType(); break;
                case 5: clearName(); break;
                case 6: clearValue(); break;
                case 7: clearAnnotations(); break;
                default: break;
            }
            return this;
        }

        @Override
        public boolean valid() {
            return optionals.get(1) &&
                   optionals.get(2) &&
                   optionals.get(3);
        }

        @Override
        public void validate() {
            if (!valid()) {
                java.util.LinkedList<String> missing = new java.util.LinkedList<>();

                if (!optionals.get(1)) {
                    missing.add("type");
                }

                if (!optionals.get(2)) {
                    missing.add("name");
                }

                if (!optionals.get(3)) {
                    missing.add("value");
                }

                throw new java.lang.IllegalStateException(
                        "Missing required fields " +
                        String.join(",", missing) +
                        " in message model.ConstType");
            }
        }

        @Override
        public net.morimekta.providence.descriptor.PStructDescriptor<ConstType,_Field> descriptor() {
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
                            throw new net.morimekta.providence.serializer.SerializerException("Wrong type " + type + " for model.ConstType.documentation, should be 12");
                        }
                        break;
                    }
                    case 4: {
                        if (type == 11) {
                            int len_2 = reader.expectUInt32();
                            mType = new String(reader.expectBytes(len_2), java.nio.charset.StandardCharsets.UTF_8);
                            optionals.set(1);
                        } else {
                            throw new net.morimekta.providence.serializer.SerializerException("Wrong type " + type + " for model.ConstType.type, should be 12");
                        }
                        break;
                    }
                    case 5: {
                        if (type == 11) {
                            int len_3 = reader.expectUInt32();
                            mName = new String(reader.expectBytes(len_3), java.nio.charset.StandardCharsets.UTF_8);
                            optionals.set(2);
                        } else {
                            throw new net.morimekta.providence.serializer.SerializerException("Wrong type " + type + " for model.ConstType.name, should be 12");
                        }
                        break;
                    }
                    case 6: {
                        if (type == 11) {
                            int len_4 = reader.expectUInt32();
                            mValue = new String(reader.expectBytes(len_4), java.nio.charset.StandardCharsets.UTF_8);
                            optionals.set(3);
                        } else {
                            throw new net.morimekta.providence.serializer.SerializerException("Wrong type " + type + " for model.ConstType.value, should be 12");
                        }
                        break;
                    }
                    case 7: {
                        if (type == 13) {
                            byte t_6 = reader.expectByte();
                            byte t_7 = reader.expectByte();
                            if (t_6 == 11 && t_7 == 11) {
                                final int len_5 = reader.expectUInt32();
                                for (int i_8 = 0; i_8 < len_5; ++i_8) {
                                    int len_11 = reader.expectUInt32();
                                    String key_9 = new String(reader.expectBytes(len_11), java.nio.charset.StandardCharsets.UTF_8);
                                    int len_12 = reader.expectUInt32();
                                    String val_10 = new String(reader.expectBytes(len_12), java.nio.charset.StandardCharsets.UTF_8);
                                    mAnnotations.put(key_9, val_10);
                                }
                            } else {
                                throw new net.morimekta.providence.serializer.SerializerException("Wrong key type " + t_6 + " or value type " + t_7 + " for model.ConstType.annotations, should be 11 and 11");
                            }
                            optionals.set(4);
                        } else {
                            throw new net.morimekta.providence.serializer.SerializerException("Wrong type " + type + " for model.ConstType.annotations, should be 12");
                        }
                        break;
                    }
                    default: {
                        if (strict) {
                            throw new net.morimekta.providence.serializer.SerializerException("No field with id " + field + " exists in model.ConstType");
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
        public ConstType build() {
            return new ConstType(this);
        }
    }
}
