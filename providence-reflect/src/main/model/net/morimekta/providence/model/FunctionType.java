package net.morimekta.providence.model;

/**
 * (oneway)? &lt;return_type&gt; &lt;name&gt;&#39;(&#39;&lt;param&gt;*&#39;)&#39; (throws &#39;(&#39; &lt;exception&gt;+ &#39;)&#39;)?
 */
@SuppressWarnings("unused")
public class FunctionType
        implements net.morimekta.providence.PMessage<FunctionType,FunctionType._Field>,
                   Comparable<FunctionType>,
                   java.io.Serializable,
                   net.morimekta.providence.serializer.rw.BinaryWriter {
    private final static long serialVersionUID = 6135149631417317609L;

    private final static boolean kDefaultOneWay = false;

    private final String mDocumentation;
    private final boolean mOneWay;
    private final String mReturnType;
    private final String mName;
    private final java.util.List<net.morimekta.providence.model.FieldType> mParams;
    private final java.util.List<net.morimekta.providence.model.FieldType> mExceptions;
    private final java.util.Map<String,String> mAnnotations;

    private volatile int tHashCode;

    public FunctionType(String pDocumentation,
                        Boolean pOneWay,
                        String pReturnType,
                        String pName,
                        java.util.List<net.morimekta.providence.model.FieldType> pParams,
                        java.util.List<net.morimekta.providence.model.FieldType> pExceptions,
                        java.util.Map<String,String> pAnnotations) {
        mDocumentation = pDocumentation;
        if (pOneWay != null) {
            mOneWay = pOneWay;
        } else {
            mOneWay = kDefaultOneWay;
        }
        mReturnType = pReturnType;
        mName = pName;
        if (pParams != null) {
            mParams = com.google.common.collect.ImmutableList.copyOf(pParams);
        } else {
            mParams = null;
        }
        if (pExceptions != null) {
            mExceptions = com.google.common.collect.ImmutableList.copyOf(pExceptions);
        } else {
            mExceptions = null;
        }
        if (pAnnotations != null) {
            mAnnotations = com.google.common.collect.ImmutableMap.copyOf(pAnnotations);
        } else {
            mAnnotations = null;
        }
    }

    private FunctionType(_Builder builder) {
        mDocumentation = builder.mDocumentation;
        mOneWay = builder.mOneWay;
        mReturnType = builder.mReturnType;
        mName = builder.mName;
        if (builder.isSetParams()) {
            mParams = builder.mParams.build();
        } else {
            mParams = null;
        }
        if (builder.isSetExceptions()) {
            mExceptions = builder.mExceptions.build();
        } else {
            mExceptions = null;
        }
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

    public boolean hasOneWay() {
        return true;
    }

    /**
     * @return The field value
     */
    public boolean isOneWay() {
        return mOneWay;
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

    public boolean hasName() {
        return mName != null;
    }

    /**
     * @return The field value
     */
    public String getName() {
        return mName;
    }

    public int numParams() {
        return mParams != null ? mParams.size() : 0;
    }

    public boolean hasParams() {
        return mParams != null;
    }

    /**
     * @return The field value
     */
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
        return mExceptions;
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
            case 3: return hasReturnType();
            case 4: return hasName();
            case 5: return hasParams();
            case 6: return hasExceptions();
            case 7: return hasAnnotations();
            default: return false;
        }
    }

    @Override
    public int num(int key) {
        switch(key) {
            case 1: return hasDocumentation() ? 1 : 0;
            case 2: return 1;
            case 3: return hasReturnType() ? 1 : 0;
            case 4: return hasName() ? 1 : 0;
            case 5: return numParams();
            case 6: return numExceptions();
            case 7: return numAnnotations();
            default: return 0;
        }
    }

    @Override
    public Object get(int key) {
        switch(key) {
            case 1: return getDocumentation();
            case 2: return isOneWay();
            case 3: return getReturnType();
            case 4: return getName();
            case 5: return getParams();
            case 6: return getExceptions();
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
        if (o == null || !(o instanceof FunctionType)) return false;
        FunctionType other = (FunctionType) o;
        return java.util.Objects.equals(mDocumentation, other.mDocumentation) &&
               java.util.Objects.equals(mOneWay, other.mOneWay) &&
               java.util.Objects.equals(mReturnType, other.mReturnType) &&
               java.util.Objects.equals(mName, other.mName) &&
               java.util.Objects.equals(mParams, other.mParams) &&
               java.util.Objects.equals(mExceptions, other.mExceptions) &&
               java.util.Objects.equals(mAnnotations, other.mAnnotations);
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
                    _Field.ANNOTATIONS, mAnnotations);
        }
        return tHashCode;
    }

    @Override
    public String toString() {
        return "model.FunctionType" + asString();
    }

    @Override
    public String asString() {
        StringBuilder out = new StringBuilder();
        out.append("{");

        boolean first = true;
        if (mDocumentation != null) {
            first = false;
            out.append("documentation:")
               .append('\"')
               .append(net.morimekta.util.Strings.escape(mDocumentation))
               .append('\"');
        }
        out.append("one_way:")
           .append(mOneWay);
        if (mReturnType != null) {
            out.append(',');
            out.append("return_type:")
               .append('\"')
               .append(net.morimekta.util.Strings.escape(mReturnType))
               .append('\"');
        }
        if (mName != null) {
            out.append(',');
            out.append("name:")
               .append('\"')
               .append(net.morimekta.util.Strings.escape(mName))
               .append('\"');
        }
        if (mParams != null && mParams.size() > 0) {
            out.append(',');
            out.append("params:")
               .append(net.morimekta.util.Strings.asString(mParams));
        }
        if (mExceptions != null && mExceptions.size() > 0) {
            out.append(',');
            out.append("exceptions:")
               .append(net.morimekta.util.Strings.asString(mExceptions));
        }
        if (mAnnotations != null && mAnnotations.size() > 0) {
            out.append(',');
            out.append("annotations:")
               .append(net.morimekta.util.Strings.asString(mAnnotations));
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

        c = Boolean.compare(mOneWay, other.mOneWay);
        if (c != 0) return c;

        c = Boolean.compare(mReturnType != null, other.mReturnType != null);
        if (c != 0) return c;
        if (mReturnType != null) {
            c = mReturnType.compareTo(other.mReturnType);
            if (c != 0) return c;
        }

        c = Boolean.compare(mName != null, other.mName != null);
        if (c != 0) return c;
        if (mName != null) {
            c = mName.compareTo(other.mName);
            if (c != 0) return c;
        }

        c = Boolean.compare(mParams != null, other.mParams != null);
        if (c != 0) return c;
        if (mParams != null) {
            c = Integer.compare(mParams.hashCode(), other.mParams.hashCode());
            if (c != 0) return c;
        }

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

        length += writer.writeByte((byte) 2);
        length += writer.writeShort((short) 2);
        length += writer.writeUInt8(mOneWay ? (byte) 1 : (byte) 0);

        if (hasReturnType()) {
            length += writer.writeByte((byte) 11);
            length += writer.writeShort((short) 3);
            net.morimekta.util.Binary tmp_2 = net.morimekta.util.Binary.wrap(mReturnType.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            length += writer.writeUInt32(tmp_2.length());
            length += writer.writeBinary(tmp_2);
        }

        if (hasName()) {
            length += writer.writeByte((byte) 11);
            length += writer.writeShort((short) 4);
            net.morimekta.util.Binary tmp_3 = net.morimekta.util.Binary.wrap(mName.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            length += writer.writeUInt32(tmp_3.length());
            length += writer.writeBinary(tmp_3);
        }

        if (hasParams()) {
            length += writer.writeByte((byte) 15);
            length += writer.writeShort((short) 5);
            length += writer.writeByte((byte) 12);
            length += writer.writeUInt32(mParams.size());
            for (net.morimekta.providence.model.FieldType entry_4 : mParams) {
                length += net.morimekta.providence.serializer.rw.BinaryFormatUtils.writeMessage(writer, entry_4);
            }
        }

        if (hasExceptions()) {
            length += writer.writeByte((byte) 15);
            length += writer.writeShort((short) 6);
            length += writer.writeByte((byte) 12);
            length += writer.writeUInt32(mExceptions.size());
            for (net.morimekta.providence.model.FieldType entry_5 : mExceptions) {
                length += net.morimekta.providence.serializer.rw.BinaryFormatUtils.writeMessage(writer, entry_5);
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

        length += writer.writeByte((byte) 0);
        return length;
    }

    @Override
    public _Builder mutate() {
        return new _Builder(this);
    }

    public enum _Field implements net.morimekta.providence.descriptor.PField {
        DOCUMENTATION(1, net.morimekta.providence.descriptor.PRequirement.DEFAULT, "documentation", net.morimekta.providence.descriptor.PPrimitive.STRING.provider(), null),
        ONE_WAY(2, net.morimekta.providence.descriptor.PRequirement.DEFAULT, "one_way", net.morimekta.providence.descriptor.PPrimitive.BOOL.provider(), new net.morimekta.providence.descriptor.PDefaultValueProvider<>(kDefaultOneWay)),
        RETURN_TYPE(3, net.morimekta.providence.descriptor.PRequirement.DEFAULT, "return_type", net.morimekta.providence.descriptor.PPrimitive.STRING.provider(), null),
        NAME(4, net.morimekta.providence.descriptor.PRequirement.REQUIRED, "name", net.morimekta.providence.descriptor.PPrimitive.STRING.provider(), null),
        PARAMS(5, net.morimekta.providence.descriptor.PRequirement.DEFAULT, "params", net.morimekta.providence.descriptor.PList.provider(net.morimekta.providence.model.FieldType.provider()), null),
        EXCEPTIONS(6, net.morimekta.providence.descriptor.PRequirement.DEFAULT, "exceptions", net.morimekta.providence.descriptor.PList.provider(net.morimekta.providence.model.FieldType.provider()), null),
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
                case 2: return _Field.ONE_WAY;
                case 3: return _Field.RETURN_TYPE;
                case 4: return _Field.NAME;
                case 5: return _Field.PARAMS;
                case 6: return _Field.EXCEPTIONS;
                case 7: return _Field.ANNOTATIONS;
            }
            return null;
        }

        public static _Field forName(String name) {
            switch (name) {
                case "documentation": return _Field.DOCUMENTATION;
                case "one_way": return _Field.ONE_WAY;
                case "return_type": return _Field.RETURN_TYPE;
                case "name": return _Field.NAME;
                case "params": return _Field.PARAMS;
                case "exceptions": return _Field.EXCEPTIONS;
                case "annotations": return _Field.ANNOTATIONS;
            }
            return null;
        }
    }

    public static net.morimekta.providence.descriptor.PStructDescriptorProvider<FunctionType,_Field> provider() {
        return new _Provider();
    }

    @Override
    public net.morimekta.providence.descriptor.PStructDescriptor<FunctionType,_Field> descriptor() {
        return kDescriptor;
    }

    public static final net.morimekta.providence.descriptor.PStructDescriptor<FunctionType,_Field> kDescriptor;

    private static class _Descriptor
            extends net.morimekta.providence.descriptor.PStructDescriptor<FunctionType,_Field> {
        public _Descriptor() {
            super("model", "FunctionType", new _Factory(), false, false);
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

    private final static class _Provider extends net.morimekta.providence.descriptor.PStructDescriptorProvider<FunctionType,_Field> {
        @Override
        public net.morimekta.providence.descriptor.PStructDescriptor<FunctionType,_Field> descriptor() {
            return kDescriptor;
        }
    }

    private final static class _Factory
            extends net.morimekta.providence.PMessageBuilderFactory<FunctionType,_Field> {
        @Override
        public _Builder builder() {
            return new _Builder();
        }
    }

    /**
     * Make a model.FunctionType builder.
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
            implements net.morimekta.providence.serializer.rw.BinaryReader {
        private java.util.BitSet optionals;

        private String mDocumentation;
        private boolean mOneWay;
        private String mReturnType;
        private String mName;
        private net.morimekta.providence.descriptor.PList.Builder<net.morimekta.providence.model.FieldType> mParams;
        private net.morimekta.providence.descriptor.PList.Builder<net.morimekta.providence.model.FieldType> mExceptions;
        private net.morimekta.providence.descriptor.PMap.Builder<String,String> mAnnotations;

        /**
         * Make a model.FunctionType builder.
         */
        public _Builder() {
            optionals = new java.util.BitSet(7);
            mOneWay = kDefaultOneWay;
            mParams = new net.morimekta.providence.descriptor.PList.ImmutableListBuilder<>();
            mExceptions = new net.morimekta.providence.descriptor.PList.ImmutableListBuilder<>();
            mAnnotations = new net.morimekta.providence.descriptor.PMap.ImmutableMapBuilder<>();
        }

        /**
         * Make a mutating builder off a base model.FunctionType.
         *
         * @param base The base FunctionType
         */
        public _Builder(FunctionType base) {
            this();

            if (base.hasDocumentation()) {
                optionals.set(0);
                mDocumentation = base.mDocumentation;
            }
            optionals.set(1);
            mOneWay = base.mOneWay;
            if (base.hasReturnType()) {
                optionals.set(2);
                mReturnType = base.mReturnType;
            }
            if (base.hasName()) {
                optionals.set(3);
                mName = base.mName;
            }
            if (base.hasParams()) {
                optionals.set(4);
                mParams.addAll(base.mParams);
            }
            if (base.hasExceptions()) {
                optionals.set(5);
                mExceptions.addAll(base.mExceptions);
            }
            if (base.hasAnnotations()) {
                optionals.set(6);
                mAnnotations.putAll(base.mAnnotations);
            }
        }

        @Override
        public _Builder merge(FunctionType from) {
            if (from.hasDocumentation()) {
                optionals.set(0);
                mDocumentation = from.getDocumentation();
            }

            optionals.set(1);
            mOneWay = from.isOneWay();

            if (from.hasReturnType()) {
                optionals.set(2);
                mReturnType = from.getReturnType();
            }

            if (from.hasName()) {
                optionals.set(3);
                mName = from.getName();
            }

            if (from.hasParams()) {
                optionals.set(4);
                mParams.clear();
                mParams.addAll(from.getParams());
            }

            if (from.hasExceptions()) {
                optionals.set(5);
                mExceptions.clear();
                mExceptions.addAll(from.getExceptions());
            }

            if (from.hasAnnotations()) {
                optionals.set(6);
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
         * Sets the value of one_way.
         *
         * @param value The new value
         * @return The builder
         */
        public _Builder setOneWay(boolean value) {
            optionals.set(1);
            mOneWay = value;
            return this;
        }

        /**
         * Checks for presence of the one_way field.
         *
         * @return True iff one_way has been set.
         */
        public boolean isSetOneWay() {
            return optionals.get(1);
        }

        /**
         * Clears the one_way field.
         *
         * @return The builder
         */
        public _Builder clearOneWay() {
            optionals.clear(1);
            mOneWay = kDefaultOneWay;
            return this;
        }

        /**
         * Gets the value of the contained one_way.
         *
         * @return The field value
         */
        public boolean isOneWay() {
            return mOneWay;
        }

        /**
         * Sets the value of return_type.
         *
         * @param value The new value
         * @return The builder
         */
        public _Builder setReturnType(String value) {
            optionals.set(2);
            mReturnType = value;
            return this;
        }

        /**
         * Checks for presence of the return_type field.
         *
         * @return True iff return_type has been set.
         */
        public boolean isSetReturnType() {
            return optionals.get(2);
        }

        /**
         * Clears the return_type field.
         *
         * @return The builder
         */
        public _Builder clearReturnType() {
            optionals.clear(2);
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
        public _Builder setName(String value) {
            optionals.set(3);
            mName = value;
            return this;
        }

        /**
         * Checks for presence of the name field.
         *
         * @return True iff name has been set.
         */
        public boolean isSetName() {
            return optionals.get(3);
        }

        /**
         * Clears the name field.
         *
         * @return The builder
         */
        public _Builder clearName() {
            optionals.clear(3);
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
         * Sets the value of params.
         *
         * @param value The new value
         * @return The builder
         */
        public _Builder setParams(java.util.Collection<net.morimekta.providence.model.FieldType> value) {
            optionals.set(4);
            mParams.clear();
            mParams.addAll(value);
            return this;
        }

        /**
         * Adds entries to params.
         *
         * @param values The added value
         * @return The builder
         */
        public _Builder addToParams(net.morimekta.providence.model.FieldType... values) {
            optionals.set(4);
            for (net.morimekta.providence.model.FieldType item : values) {
                mParams.add(item);
            }
            return this;
        }

        /**
         * Checks for presence of the params field.
         *
         * @return True iff params has been set.
         */
        public boolean isSetParams() {
            return optionals.get(4);
        }

        /**
         * Clears the params field.
         *
         * @return The builder
         */
        public _Builder clearParams() {
            optionals.clear(4);
            mParams.clear();
            return this;
        }

        /**
         * Gets the builder for the contained params.
         *
         * @return The field builder
         */
        public net.morimekta.providence.descriptor.PList.Builder<net.morimekta.providence.model.FieldType> mutableParams() {
            optionals.set(4);
            return mParams;
        }

        /**
         * Sets the value of exceptions.
         *
         * @param value The new value
         * @return The builder
         */
        public _Builder setExceptions(java.util.Collection<net.morimekta.providence.model.FieldType> value) {
            optionals.set(5);
            mExceptions.clear();
            mExceptions.addAll(value);
            return this;
        }

        /**
         * Adds entries to exceptions.
         *
         * @param values The added value
         * @return The builder
         */
        public _Builder addToExceptions(net.morimekta.providence.model.FieldType... values) {
            optionals.set(5);
            for (net.morimekta.providence.model.FieldType item : values) {
                mExceptions.add(item);
            }
            return this;
        }

        /**
         * Checks for presence of the exceptions field.
         *
         * @return True iff exceptions has been set.
         */
        public boolean isSetExceptions() {
            return optionals.get(5);
        }

        /**
         * Clears the exceptions field.
         *
         * @return The builder
         */
        public _Builder clearExceptions() {
            optionals.clear(5);
            mExceptions.clear();
            return this;
        }

        /**
         * Gets the builder for the contained exceptions.
         *
         * @return The field builder
         */
        public net.morimekta.providence.descriptor.PList.Builder<net.morimekta.providence.model.FieldType> mutableExceptions() {
            optionals.set(5);
            return mExceptions;
        }

        /**
         * Sets the value of annotations.
         *
         * @param value The new value
         * @return The builder
         */
        public _Builder setAnnotations(java.util.Map<String,String> value) {
            optionals.set(6);
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
            optionals.set(6);
            mAnnotations.put(key, value);
            return this;
        }

        /**
         * Checks for presence of the annotations field.
         *
         * @return True iff annotations has been set.
         */
        public boolean isSetAnnotations() {
            return optionals.get(6);
        }

        /**
         * Clears the annotations field.
         *
         * @return The builder
         */
        public _Builder clearAnnotations() {
            optionals.clear(6);
            mAnnotations.clear();
            return this;
        }

        /**
         * Gets the builder for the contained annotations.
         *
         * @return The field builder
         */
        public net.morimekta.providence.descriptor.PMap.Builder<String,String> mutableAnnotations() {
            optionals.set(6);
            return mAnnotations;
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
                case 2: setOneWay((boolean) value); break;
                case 3: setReturnType((String) value); break;
                case 4: setName((String) value); break;
                case 5: setParams((java.util.List<net.morimekta.providence.model.FieldType>) value); break;
                case 6: setExceptions((java.util.List<net.morimekta.providence.model.FieldType>) value); break;
                case 7: setAnnotations((java.util.Map<String,String>) value); break;
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
                java.util.LinkedList<String> missing = new java.util.LinkedList<>();

                if (!optionals.get(3)) {
                    missing.add("name");
                }

                throw new java.lang.IllegalStateException(
                        "Missing required fields " +
                        String.join(",", missing) +
                        " in message model.FunctionType");
            }
        }

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
                            throw new net.morimekta.providence.serializer.SerializerException("Wrong type " + type + " for model.FunctionType.documentation, should be 12");
                        }
                        break;
                    }
                    case 2: {
                        if (type == 2) {
                            mOneWay = reader.expectUInt8() == 1;
                            optionals.set(1);
                        } else {
                            throw new net.morimekta.providence.serializer.SerializerException("Wrong type " + type + " for model.FunctionType.one_way, should be 12");
                        }
                        break;
                    }
                    case 3: {
                        if (type == 11) {
                            int len_2 = reader.expectUInt32();
                            mReturnType = new String(reader.expectBytes(len_2), java.nio.charset.StandardCharsets.UTF_8);
                            optionals.set(2);
                        } else {
                            throw new net.morimekta.providence.serializer.SerializerException("Wrong type " + type + " for model.FunctionType.return_type, should be 12");
                        }
                        break;
                    }
                    case 4: {
                        if (type == 11) {
                            int len_3 = reader.expectUInt32();
                            mName = new String(reader.expectBytes(len_3), java.nio.charset.StandardCharsets.UTF_8);
                            optionals.set(3);
                        } else {
                            throw new net.morimekta.providence.serializer.SerializerException("Wrong type " + type + " for model.FunctionType.name, should be 12");
                        }
                        break;
                    }
                    case 5: {
                        if (type == 15) {
                            byte t_5 = reader.expectByte();
                            if (t_5 == 12) {
                                final int len_4 = reader.expectUInt32();
                                for (int i_6 = 0; i_6 < len_4; ++i_6) {
                                    net.morimekta.providence.model.FieldType key_7 = net.morimekta.providence.serializer.rw.BinaryFormatUtils.readMessage(reader, net.morimekta.providence.model.FieldType.kDescriptor, strict);
                                    mParams.add(key_7);
                                }
                            } else {
                                throw new net.morimekta.providence.serializer.SerializerException("Wrong item type " + t_5 + " for model.FunctionType.params, should be 12");
                            }
                            optionals.set(4);
                        } else {
                            throw new net.morimekta.providence.serializer.SerializerException("Wrong type " + type + " for model.FunctionType.params, should be 12");
                        }
                        break;
                    }
                    case 6: {
                        if (type == 15) {
                            byte t_9 = reader.expectByte();
                            if (t_9 == 12) {
                                final int len_8 = reader.expectUInt32();
                                for (int i_10 = 0; i_10 < len_8; ++i_10) {
                                    net.morimekta.providence.model.FieldType key_11 = net.morimekta.providence.serializer.rw.BinaryFormatUtils.readMessage(reader, net.morimekta.providence.model.FieldType.kDescriptor, strict);
                                    mExceptions.add(key_11);
                                }
                            } else {
                                throw new net.morimekta.providence.serializer.SerializerException("Wrong item type " + t_9 + " for model.FunctionType.exceptions, should be 12");
                            }
                            optionals.set(5);
                        } else {
                            throw new net.morimekta.providence.serializer.SerializerException("Wrong type " + type + " for model.FunctionType.exceptions, should be 12");
                        }
                        break;
                    }
                    case 7: {
                        if (type == 13) {
                            byte t_13 = reader.expectByte();
                            byte t_14 = reader.expectByte();
                            if (t_13 == 11 && t_14 == 11) {
                                final int len_12 = reader.expectUInt32();
                                for (int i_15 = 0; i_15 < len_12; ++i_15) {
                                    int len_18 = reader.expectUInt32();
                                    String key_16 = new String(reader.expectBytes(len_18), java.nio.charset.StandardCharsets.UTF_8);
                                    int len_19 = reader.expectUInt32();
                                    String val_17 = new String(reader.expectBytes(len_19), java.nio.charset.StandardCharsets.UTF_8);
                                    mAnnotations.put(key_16, val_17);
                                }
                            } else {
                                throw new net.morimekta.providence.serializer.SerializerException("Wrong key type " + t_13 + " or value type " + t_14 + " for model.FunctionType.annotations, should be 11 and 11");
                            }
                            optionals.set(6);
                        } else {
                            throw new net.morimekta.providence.serializer.SerializerException("Wrong type " + type + " for model.FunctionType.annotations, should be 12");
                        }
                        break;
                    }
                    default: {
                        if (strict) {
                            throw new net.morimekta.providence.serializer.SerializerException("No field with id " + field + " exists in model.FunctionType");
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
        public FunctionType build() {
            return new FunctionType(this);
        }
    }
}
