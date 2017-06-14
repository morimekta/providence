package net.morimekta.test.providence.reflect.calculator;

@SuppressWarnings("unused")
@javax.annotation.Generated("providence java generator")
public class Operation
        implements net.morimekta.providence.PMessage<Operation,Operation._Field>,
                   Comparable<Operation>,
                   java.io.Serializable,
                   net.morimekta.providence.serializer.binary.BinaryWriter {
    private final static long serialVersionUID = -2122462501055525645L;

    private final net.morimekta.test.providence.reflect.calculator.Operator mOperator;
    private final java.util.List<net.morimekta.test.providence.reflect.calculator.Operand> mOperands;

    private volatile int tHashCode;

    private Operation(_Builder builder) {
        mOperator = builder.mOperator;
        if (builder.isSetOperands()) {
            mOperands = com.google.common.collect.ImmutableList.copyOf(builder.mOperands);
        } else {
            mOperands = null;
        }
    }

    public boolean hasOperator() {
        return mOperator != null;
    }

    /**
     * @return The field value
     */
    public net.morimekta.test.providence.reflect.calculator.Operator getOperator() {
        return mOperator;
    }

    public int numOperands() {
        return mOperands != null ? mOperands.size() : 0;
    }

    public boolean hasOperands() {
        return mOperands != null;
    }

    /**
     * @return The field value
     */
    public java.util.List<net.morimekta.test.providence.reflect.calculator.Operand> getOperands() {
        return mOperands;
    }

    @Override
    public boolean has(int key) {
        switch(key) {
            case 1: return hasOperator();
            case 2: return hasOperands();
            default: return false;
        }
    }

    @Override
    public int num(int key) {
        switch(key) {
            case 1: return hasOperator() ? 1 : 0;
            case 2: return numOperands();
            default: return 0;
        }
    }

    @Override
    public Object get(int key) {
        switch(key) {
            case 1: return getOperator();
            case 2: return getOperands();
            default: return null;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        if (o == null || !o.getClass().equals(getClass())) return false;
        Operation other = (Operation) o;
        return java.util.Objects.equals(mOperator, other.mOperator) &&
               java.util.Objects.equals(mOperands, other.mOperands);
    }

    @Override
    public int hashCode() {
        if (tHashCode == 0) {
            tHashCode = java.util.Objects.hash(
                    Operation.class,
                    _Field.OPERATOR, mOperator,
                    _Field.OPERANDS, mOperands);
        }
        return tHashCode;
    }

    @Override
    public String toString() {
        return "calculator.Operation" + asString();
    }

    @Override
    @javax.annotation.Nonnull
    public String asString() {
        StringBuilder out = new StringBuilder();
        out.append("{");

        boolean first = true;
        if (hasOperator()) {
            first = false;
            out.append("operator:")
               .append(mOperator.asString());
        }
        if (hasOperands()) {
            if (!first) out.append(',');
            out.append("operands:")
               .append(net.morimekta.util.Strings.asString(mOperands));
        }
        out.append('}');
        return out.toString();
    }

    @Override
    public int compareTo(Operation other) {
        int c;

        c = Boolean.compare(mOperator != null, other.mOperator != null);
        if (c != 0) return c;
        if (mOperator != null) {
            c = Integer.compare(mOperator.ordinal(), mOperator.ordinal());
            if (c != 0) return c;
        }

        c = Boolean.compare(mOperands != null, other.mOperands != null);
        if (c != 0) return c;
        if (mOperands != null) {
            c = Integer.compare(mOperands.hashCode(), other.mOperands.hashCode());
            if (c != 0) return c;
        }

        return 0;
    }

    @Override
    public int writeBinary(net.morimekta.util.io.BigEndianBinaryWriter writer) throws java.io.IOException {
        int length = 0;

        if (hasOperator()) {
            length += writer.writeByte((byte) 8);
            length += writer.writeShort((short) 1);
            length += writer.writeInt(mOperator.asInteger());
        }

        if (hasOperands()) {
            length += writer.writeByte((byte) 15);
            length += writer.writeShort((short) 2);
            length += writer.writeByte((byte) 12);
            length += writer.writeUInt32(mOperands.size());
            for (net.morimekta.test.providence.reflect.calculator.Operand entry_1 : mOperands) {
                length += net.morimekta.providence.serializer.binary.BinaryFormatUtils.writeMessage(writer, entry_1);
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
        OPERATOR(1, net.morimekta.providence.descriptor.PRequirement.OPTIONAL, "operator", net.morimekta.test.providence.reflect.calculator.Operator.provider(), null),
        OPERANDS(2, net.morimekta.providence.descriptor.PRequirement.OPTIONAL, "operands", net.morimekta.providence.descriptor.PList.provider(net.morimekta.test.providence.reflect.calculator.Operand.provider()), null),
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
                case 1: return _Field.OPERATOR;
                case 2: return _Field.OPERANDS;
            }
            return null;
        }

        /**
         * @param name Field name
         * @return The named field or null
         */
        public static _Field findByName(String name) {
            switch (name) {
                case "operator": return _Field.OPERATOR;
                case "operands": return _Field.OPERANDS;
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
                throw new IllegalArgumentException("No such field id " + id + " in calculator.Operation");
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
                throw new IllegalArgumentException("No such field \"" + name + "\" in calculator.Operation");
            }
            return field;
        }

    }

    public static net.morimekta.providence.descriptor.PStructDescriptorProvider<Operation,_Field> provider() {
        return new _Provider();
    }

    @Override
    public net.morimekta.providence.descriptor.PStructDescriptor<Operation,_Field> descriptor() {
        return kDescriptor;
    }

    public static final net.morimekta.providence.descriptor.PStructDescriptor<Operation,_Field> kDescriptor;

    private static class _Descriptor
            extends net.morimekta.providence.descriptor.PStructDescriptor<Operation,_Field> {
        public _Descriptor() {
            super("calculator", "Operation", _Builder::new, false);
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

    private final static class _Provider extends net.morimekta.providence.descriptor.PStructDescriptorProvider<Operation,_Field> {
        @Override
        public net.morimekta.providence.descriptor.PStructDescriptor<Operation,_Field> descriptor() {
            return kDescriptor;
        }
    }

    /**
     * Make a calculator.Operation builder.
     * @return The builder instance.
     */
    public static _Builder builder() {
        return new _Builder();
    }

    public static class _Builder
            extends net.morimekta.providence.PMessageBuilder<Operation,_Field>
            implements net.morimekta.providence.serializer.binary.BinaryReader {
        private java.util.BitSet optionals;
        private java.util.BitSet modified;

        private net.morimekta.test.providence.reflect.calculator.Operator mOperator;
        private java.util.List<net.morimekta.test.providence.reflect.calculator.Operand> mOperands;

        /**
         * Make a calculator.Operation builder.
         */
        public _Builder() {
            optionals = new java.util.BitSet(2);
            modified = new java.util.BitSet(2);
        }

        /**
         * Make a mutating builder off a base calculator.Operation.
         *
         * @param base The base Operation
         */
        public _Builder(Operation base) {
            this();

            if (base.hasOperator()) {
                optionals.set(0);
                mOperator = base.mOperator;
            }
            if (base.hasOperands()) {
                optionals.set(1);
                mOperands = base.mOperands;
            }
        }

        @javax.annotation.Nonnull
        @Override
        public _Builder merge(Operation from) {
            if (from.hasOperator()) {
                optionals.set(0);
                modified.set(0);
                mOperator = from.getOperator();
            }

            if (from.hasOperands()) {
                optionals.set(1);
                modified.set(1);
                mOperands = from.getOperands();
            }
            return this;
        }

        /**
         * Sets the value of operator.
         *
         * @param value The new value
         * @return The builder
         */
        @javax.annotation.Nonnull
        public _Builder setOperator(net.morimekta.test.providence.reflect.calculator.Operator value) {
            if (value == null) {
                return clearOperator();
            }

            optionals.set(0);
            modified.set(0);
            mOperator = value;
            return this;
        }

        /**
         * Checks for presence of the operator field.
         *
         * @return True if operator has been set.
         */
        public boolean isSetOperator() {
            return optionals.get(0);
        }

        /**
         * Checks if operator has been modified since the _Builder was created.
         *
         * @return True if operator has been modified.
         */
        public boolean isModifiedOperator() {
            return modified.get(0);
        }

        /**
         * Clears the operator field.
         *
         * @return The builder
         */
        @javax.annotation.Nonnull
        public _Builder clearOperator() {
            optionals.clear(0);
            modified.set(0);
            mOperator = null;
            return this;
        }

        /**
         * Gets the value of the contained operator.
         *
         * @return The field value
         */
        public net.morimekta.test.providence.reflect.calculator.Operator getOperator() {
            return mOperator;
        }

        /**
         * Sets the value of operands.
         *
         * @param value The new value
         * @return The builder
         */
        @javax.annotation.Nonnull
        public _Builder setOperands(java.util.Collection<net.morimekta.test.providence.reflect.calculator.Operand> value) {
            if (value == null) {
                return clearOperands();
            }

            optionals.set(1);
            modified.set(1);
            mOperands = com.google.common.collect.ImmutableList.copyOf(value);
            return this;
        }

        /**
         * Adds entries to operands.
         *
         * @param values The added value
         * @return The builder
         */
        @javax.annotation.Nonnull
        public _Builder addToOperands(net.morimekta.test.providence.reflect.calculator.Operand... values) {
            optionals.set(1);
            modified.set(1);
            java.util.List<net.morimekta.test.providence.reflect.calculator.Operand> _container = mutableOperands();
            for (net.morimekta.test.providence.reflect.calculator.Operand item : values) {
                _container.add(item);
            }
            return this;
        }

        /**
         * Checks for presence of the operands field.
         *
         * @return True if operands has been set.
         */
        public boolean isSetOperands() {
            return optionals.get(1);
        }

        /**
         * Checks if operands has been modified since the _Builder was created.
         *
         * @return True if operands has been modified.
         */
        public boolean isModifiedOperands() {
            return modified.get(1);
        }

        /**
         * Clears the operands field.
         *
         * @return The builder
         */
        @javax.annotation.Nonnull
        public _Builder clearOperands() {
            optionals.clear(1);
            modified.set(1);
            mOperands = null;
            return this;
        }

        /**
         * Gets the builder for the contained operands.
         *
         * @return The field builder
         */
        @javax.annotation.Nonnull
        public java.util.List<net.morimekta.test.providence.reflect.calculator.Operand> mutableOperands() {
            optionals.set(1);
            modified.set(1);

            if (mOperands == null) {
                mOperands = new java.util.LinkedList<>();
            } else if (!(mOperands instanceof java.util.LinkedList)) {
                mOperands = new java.util.LinkedList<>(mOperands);
            }
            return mOperands;
        }

        @Override
        public boolean equals(Object o) {
            if (o == this) return true;
            if (o == null || !o.getClass().equals(getClass())) return false;
            Operation._Builder other = (Operation._Builder) o;
            return java.util.Objects.equals(optionals, other.optionals) &&
                   java.util.Objects.equals(mOperator, other.mOperator) &&
                   java.util.Objects.equals(mOperands, other.mOperands);
        }

        @Override
        public int hashCode() {
            return java.util.Objects.hash(
                    Operation.class, optionals,
                    _Field.OPERATOR, mOperator,
                    _Field.OPERANDS, mOperands);
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
                case 1: setOperator((net.morimekta.test.providence.reflect.calculator.Operator) value); break;
                case 2: setOperands((java.util.List<net.morimekta.test.providence.reflect.calculator.Operand>) value); break;
                default: break;
            }
            return this;
        }

        @Override
        public boolean isSet(int key) {
            switch (key) {
                case 1: return optionals.get(0);
                case 2: return optionals.get(1);
                default: break;
            }
            return false;
        }

        @Override
        public boolean isModified(int key) {
            switch (key) {
                case 1: return modified.get(0);
                case 2: return modified.get(1);
                default: break;
            }
            return false;
        }

        @Override
        public _Builder addTo(int key, Object value) {
            switch (key) {
                case 2: addToOperands((net.morimekta.test.providence.reflect.calculator.Operand) value); break;
                default: break;
            }
            return this;
        }

        @javax.annotation.Nonnull
        @Override
        public _Builder clear(int key) {
            switch (key) {
                case 1: clearOperator(); break;
                case 2: clearOperands(); break;
                default: break;
            }
            return this;
        }

        @Override
        public boolean valid() {
            return true;
        }

        @Override
        public void validate() {
        }

        @javax.annotation.Nonnull
        @Override
        public net.morimekta.providence.descriptor.PStructDescriptor<Operation,_Field> descriptor() {
            return kDescriptor;
        }

        @Override
        public void readBinary(net.morimekta.util.io.BigEndianBinaryReader reader, boolean strict) throws java.io.IOException {
            byte type = reader.expectByte();
            while (type != 0) {
                int field = reader.expectShort();
                switch (field) {
                    case 1: {
                        if (type == 8) {
                            mOperator = net.morimekta.test.providence.reflect.calculator.Operator.findById(reader.expectInt());
                            optionals.set(0);
                        } else {
                            throw new net.morimekta.providence.serializer.SerializerException("Wrong type " + net.morimekta.providence.serializer.binary.BinaryType.asString(type) + " for calculator.Operation.operator, should be struct(12)");
                        }
                        break;
                    }
                    case 2: {
                        if (type == 15) {
                            net.morimekta.providence.descriptor.PList.DefaultBuilder<net.morimekta.test.providence.reflect.calculator.Operand> b_1 = new net.morimekta.providence.descriptor.PList.DefaultBuilder<>();
                            byte t_3 = reader.expectByte();
                            if (t_3 == 12) {
                                final int len_2 = reader.expectUInt32();
                                for (int i_4 = 0; i_4 < len_2; ++i_4) {
                                    net.morimekta.test.providence.reflect.calculator.Operand key_5 = net.morimekta.providence.serializer.binary.BinaryFormatUtils.readMessage(reader, net.morimekta.test.providence.reflect.calculator.Operand.kDescriptor, strict);
                                    b_1.add(key_5);
                                }
                                mOperands = b_1.build();
                            } else {
                                throw new net.morimekta.providence.serializer.SerializerException("Wrong item type " + net.morimekta.providence.serializer.binary.BinaryType.asString(t_3) + " for calculator.Operation.operands, should be struct(12)");
                            }
                            optionals.set(1);
                        } else {
                            throw new net.morimekta.providence.serializer.SerializerException("Wrong type " + net.morimekta.providence.serializer.binary.BinaryType.asString(type) + " for calculator.Operation.operands, should be struct(12)");
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
        public Operation build() {
            return new Operation(this);
        }
    }
}
