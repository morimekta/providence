package net.morimekta.test.providence.core.calculator;

@SuppressWarnings("unused")
@javax.annotation.Generated("providence-maven-plugin")
public class Operand
        implements net.morimekta.providence.PUnion<Operand,Operand._Field>,
                   Comparable<Operand>,
                   java.io.Serializable,
                   net.morimekta.providence.serializer.binary.BinaryWriter {
    private final static long serialVersionUID = -7034870678901672325L;

    private final static double kDefaultNumber = 0.0d;

    private final net.morimekta.test.providence.core.calculator.Operation mOperation;
    private final Double mNumber;
    private final net.morimekta.test.providence.core.number.Imaginary mImaginary;

    private final _Field tUnionField;

    private volatile int tHashCode;

    /**
     * @param value The union value
     * @return The created union.
     */
    public static Operand withOperation(net.morimekta.test.providence.core.calculator.Operation value) {
        return new _Builder().setOperation(value).build();
    }

    /**
     * @param value The union value
     * @return The created union.
     */
    public static Operand withNumber(double value) {
        return new _Builder().setNumber(value).build();
    }

    /**
     * @param value The union value
     * @return The created union.
     */
    public static Operand withImaginary(net.morimekta.test.providence.core.number.Imaginary value) {
        return new _Builder().setImaginary(value).build();
    }

    private Operand(_Builder builder) {
        tUnionField = builder.tUnionField;

        mOperation = tUnionField != _Field.OPERATION
                ? null
                : builder.mOperation_builder != null ? builder.mOperation_builder.build() : builder.mOperation;
        mNumber = tUnionField == _Field.NUMBER ? builder.mNumber : null;
        mImaginary = tUnionField != _Field.IMAGINARY
                ? null
                : builder.mImaginary_builder != null ? builder.mImaginary_builder.build() : builder.mImaginary;
    }

    public boolean hasOperation() {
        return tUnionField == _Field.OPERATION && mOperation != null;
    }

    /**
     * @return The field value
     */
    public net.morimekta.test.providence.core.calculator.Operation getOperation() {
        return mOperation;
    }

    public boolean hasNumber() {
        return tUnionField == _Field.NUMBER && mNumber != null;
    }

    /**
     * @return The field value
     */
    public double getNumber() {
        return hasNumber() ? mNumber : kDefaultNumber;
    }

    public boolean hasImaginary() {
        return tUnionField == _Field.IMAGINARY && mImaginary != null;
    }

    /**
     * @return The field value
     */
    public net.morimekta.test.providence.core.number.Imaginary getImaginary() {
        return mImaginary;
    }

    @Override
    public boolean has(int key) {
        switch(key) {
            case 1: return hasOperation();
            case 2: return hasNumber();
            case 3: return hasImaginary();
            default: return false;
        }
    }

    @Override
    public int num(int key) {
        switch(key) {
            case 1: return hasOperation() ? 1 : 0;
            case 2: return hasNumber() ? 1 : 0;
            case 3: return hasImaginary() ? 1 : 0;
            default: return 0;
        }
    }

    @Override
    public Object get(int key) {
        switch(key) {
            case 1: return getOperation();
            case 2: return getNumber();
            case 3: return getImaginary();
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
        Operand other = (Operand) o;
        return java.util.Objects.equals(tUnionField, other.tUnionField) &&
               java.util.Objects.equals(mOperation, other.mOperation) &&
               java.util.Objects.equals(mNumber, other.mNumber) &&
               java.util.Objects.equals(mImaginary, other.mImaginary);
    }

    @Override
    public int hashCode() {
        if (tHashCode == 0) {
            tHashCode = java.util.Objects.hash(
                    Operand.class,
                    _Field.OPERATION, mOperation,
                    _Field.NUMBER, mNumber,
                    _Field.IMAGINARY, mImaginary);
        }
        return tHashCode;
    }

    @Override
    public String toString() {
        return "calculator.Operand" + asString();
    }

    @Override
    @javax.annotation.Nonnull
    public String asString() {
        StringBuilder out = new StringBuilder();
        out.append("{");

        switch (tUnionField) {
            case OPERATION: {
                out.append("operation:")
                   .append(mOperation.asString());
                break;
            }
            case NUMBER: {
                out.append("number:")
                   .append(net.morimekta.util.Strings.asString(mNumber));
                break;
            }
            case IMAGINARY: {
                out.append("imaginary:")
                   .append(mImaginary.asString());
                break;
            }
        }
        out.append('}');
        return out.toString();
    }

    @Override
    public int compareTo(Operand other) {
        int c = tUnionField.compareTo(other.tUnionField);
        if (c != 0) return c;

        switch (tUnionField) {
            case OPERATION:
                return mOperation.compareTo(other.mOperation);
            case NUMBER:
                return Double.compare(mNumber, other.mNumber);
            case IMAGINARY:
                return mImaginary.compareTo(other.mImaginary);
            default: return 0;
        }
    }

    @Override
    public int writeBinary(net.morimekta.util.io.BigEndianBinaryWriter writer) throws java.io.IOException {
        int length = 0;

        if (tUnionField != null) {
            switch (tUnionField) {
                case OPERATION: {
                    length += writer.writeByte((byte) 12);
                    length += writer.writeShort((short) 1);
                    length += net.morimekta.providence.serializer.binary.BinaryFormatUtils.writeMessage(writer, mOperation);
                    break;
                }
                case NUMBER: {
                    length += writer.writeByte((byte) 4);
                    length += writer.writeShort((short) 2);
                    length += writer.writeDouble(mNumber);
                    break;
                }
                case IMAGINARY: {
                    length += writer.writeByte((byte) 12);
                    length += writer.writeShort((short) 3);
                    length += net.morimekta.providence.serializer.binary.BinaryFormatUtils.writeMessage(writer, mImaginary);
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
        OPERATION(1, net.morimekta.providence.descriptor.PRequirement.OPTIONAL, "operation", net.morimekta.test.providence.core.calculator.Operation.provider(), null),
        NUMBER(2, net.morimekta.providence.descriptor.PRequirement.OPTIONAL, "number", net.morimekta.providence.descriptor.PPrimitive.DOUBLE.provider(), null),
        IMAGINARY(3, net.morimekta.providence.descriptor.PRequirement.OPTIONAL, "imaginary", net.morimekta.test.providence.core.number.Imaginary.provider(), null),
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
                case 1: return _Field.OPERATION;
                case 2: return _Field.NUMBER;
                case 3: return _Field.IMAGINARY;
            }
            return null;
        }

        /**
         * @param name Field name
         * @return The named field or null
         */
        public static _Field findByName(String name) {
            switch (name) {
                case "operation": return _Field.OPERATION;
                case "number": return _Field.NUMBER;
                case "imaginary": return _Field.IMAGINARY;
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
                throw new IllegalArgumentException("No such field id " + id + " in calculator.Operand");
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
                throw new IllegalArgumentException("No such field \"" + name + "\" in calculator.Operand");
            }
            return field;
        }

    }

    public static net.morimekta.providence.descriptor.PUnionDescriptorProvider<Operand,_Field> provider() {
        return new _Provider();
    }

    @Override
    public net.morimekta.providence.descriptor.PUnionDescriptor<Operand,_Field> descriptor() {
        return kDescriptor;
    }

    public static final net.morimekta.providence.descriptor.PUnionDescriptor<Operand,_Field> kDescriptor;

    private static class _Descriptor
            extends net.morimekta.providence.descriptor.PUnionDescriptor<Operand,_Field> {
        public _Descriptor() {
            super("calculator", "Operand", _Builder::new, false);
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

    private final static class _Provider extends net.morimekta.providence.descriptor.PUnionDescriptorProvider<Operand,_Field> {
        @Override
        public net.morimekta.providence.descriptor.PUnionDescriptor<Operand,_Field> descriptor() {
            return kDescriptor;
        }
    }

    /**
     * Make a calculator.Operand builder.
     * @return The builder instance.
     */
    public static _Builder builder() {
        return new _Builder();
    }

    public static class _Builder
            extends net.morimekta.providence.PMessageBuilder<Operand,_Field>
            implements net.morimekta.providence.serializer.binary.BinaryReader {
        private _Field tUnionField;

        private boolean modified;

        private net.morimekta.test.providence.core.calculator.Operation mOperation;
        private net.morimekta.test.providence.core.calculator.Operation._Builder mOperation_builder;
        private Double mNumber;
        private net.morimekta.test.providence.core.number.Imaginary mImaginary;
        private net.morimekta.test.providence.core.number.Imaginary._Builder mImaginary_builder;

        /**
         * Make a calculator.Operand builder.
         */
        public _Builder() {
            modified = false;
        }

        /**
         * Make a mutating builder off a base calculator.Operand.
         *
         * @param base The base Operand
         */
        public _Builder(Operand base) {
            this();

            tUnionField = base.tUnionField;

            mOperation = base.mOperation;
            mNumber = base.mNumber;
            mImaginary = base.mImaginary;
        }

        @javax.annotation.Nonnull
        @Override
        public _Builder merge(Operand from) {
            if (from.unionField() == null) {
                return this;
            }

            switch (from.unionField()) {
                case OPERATION: {
                    if (tUnionField == _Field.OPERATION && mOperation != null) {
                        mOperation = mOperation.mutate().merge(from.getOperation()).build();
                    } else {
                        setOperation(from.getOperation());
                    }
                    break;
                }
                case NUMBER: {
                    setNumber(from.getNumber());
                    break;
                }
                case IMAGINARY: {
                    if (tUnionField == _Field.IMAGINARY && mImaginary != null) {
                        mImaginary = mImaginary.mutate().merge(from.getImaginary()).build();
                    } else {
                        setImaginary(from.getImaginary());
                    }
                    break;
                }
            }
            return this;
        }

        /**
         * Sets the value of operation.
         *
         * @param value The new value
         * @return The builder
         */
        @javax.annotation.Nonnull
        public _Builder setOperation(net.morimekta.test.providence.core.calculator.Operation value) {
            if (value == null) {
                return clearOperation();
            }

            tUnionField = _Field.OPERATION;
            modified = true;
            mOperation = value;
            mOperation_builder = null;
            return this;
        }

        /**
         * Checks for presence of the operation field.
         *
         * @return True if operation has been set.
         */
        public boolean isSetOperation() {
            return tUnionField == _Field.OPERATION;
        }

        /**
         * Clears the operation field.
         *
         * @return The builder
         */
        @javax.annotation.Nonnull
        public _Builder clearOperation() {
            if (tUnionField == _Field.OPERATION) tUnionField = null;
            modified = true;
            mOperation = null;
            mOperation_builder = null;
            return this;
        }

        /**
         * Gets the builder for the contained operation.
         *
         * @return The field builder
         */
        @javax.annotation.Nonnull
        public net.morimekta.test.providence.core.calculator.Operation._Builder mutableOperation() {
            if (tUnionField != _Field.OPERATION) {
                clearOperation();
            }
            tUnionField = _Field.OPERATION;
            modified = true;

            if (mOperation != null) {
                mOperation_builder = mOperation.mutate();
                mOperation = null;
            } else if (mOperation_builder == null) {
                mOperation_builder = net.morimekta.test.providence.core.calculator.Operation.builder();
            }
            return mOperation_builder;
        }

        /**
         * Sets the value of number.
         *
         * @param value The new value
         * @return The builder
         */
        @javax.annotation.Nonnull
        public _Builder setNumber(double value) {
            tUnionField = _Field.NUMBER;
            modified = true;
            mNumber = value;
            return this;
        }

        /**
         * Checks for presence of the number field.
         *
         * @return True if number has been set.
         */
        public boolean isSetNumber() {
            return tUnionField == _Field.NUMBER;
        }

        /**
         * Clears the number field.
         *
         * @return The builder
         */
        @javax.annotation.Nonnull
        public _Builder clearNumber() {
            if (tUnionField == _Field.NUMBER) tUnionField = null;
            modified = true;
            mNumber = null;
            return this;
        }

        /**
         * Gets the value of the contained number.
         *
         * @return The field value
         */
        public double getNumber() {
            return isSetNumber() ? mNumber : kDefaultNumber;
        }

        /**
         * Sets the value of imaginary.
         *
         * @param value The new value
         * @return The builder
         */
        @javax.annotation.Nonnull
        public _Builder setImaginary(net.morimekta.test.providence.core.number.Imaginary value) {
            if (value == null) {
                return clearImaginary();
            }

            tUnionField = _Field.IMAGINARY;
            modified = true;
            mImaginary = value;
            mImaginary_builder = null;
            return this;
        }

        /**
         * Checks for presence of the imaginary field.
         *
         * @return True if imaginary has been set.
         */
        public boolean isSetImaginary() {
            return tUnionField == _Field.IMAGINARY;
        }

        /**
         * Clears the imaginary field.
         *
         * @return The builder
         */
        @javax.annotation.Nonnull
        public _Builder clearImaginary() {
            if (tUnionField == _Field.IMAGINARY) tUnionField = null;
            modified = true;
            mImaginary = null;
            mImaginary_builder = null;
            return this;
        }

        /**
         * Gets the builder for the contained imaginary.
         *
         * @return The field builder
         */
        @javax.annotation.Nonnull
        public net.morimekta.test.providence.core.number.Imaginary._Builder mutableImaginary() {
            if (tUnionField != _Field.IMAGINARY) {
                clearImaginary();
            }
            tUnionField = _Field.IMAGINARY;
            modified = true;

            if (mImaginary != null) {
                mImaginary_builder = mImaginary.mutate();
                mImaginary = null;
            } else if (mImaginary_builder == null) {
                mImaginary_builder = net.morimekta.test.providence.core.number.Imaginary.builder();
            }
            return mImaginary_builder;
        }

        /**
         * Checks if Operand has been modified since the _Builder was created.
         *
         * @return True if Operand has been modified.
         */
        public boolean isUnionModified() {
            return modified;
        }

        @Override
        public boolean equals(Object o) {
            if (o == this) return true;
            if (o == null || !o.getClass().equals(getClass())) return false;
            Operand._Builder other = (Operand._Builder) o;
            return java.util.Objects.equals(tUnionField, other.tUnionField) &&
                   java.util.Objects.equals(mOperation, other.mOperation) &&
                   java.util.Objects.equals(mNumber, other.mNumber) &&
                   java.util.Objects.equals(mImaginary, other.mImaginary);
        }

        @Override
        public int hashCode() {
            return java.util.Objects.hash(
                    Operand.class,
                    _Field.OPERATION, mOperation,
                    _Field.NUMBER, mNumber,
                    _Field.IMAGINARY, mImaginary);
        }

        @Override
        @SuppressWarnings("unchecked")
        public net.morimekta.providence.PMessageBuilder mutator(int key) {
            switch (key) {
                case 1: return mutableOperation();
                case 3: return mutableImaginary();
                default: throw new IllegalArgumentException("Not a message field ID: " + key);
            }
        }

        @javax.annotation.Nonnull
        @Override
        @SuppressWarnings("unchecked")
        public _Builder set(int key, Object value) {
            if (value == null) return clear(key);
            switch (key) {
                case 1: setOperation((net.morimekta.test.providence.core.calculator.Operation) value); break;
                case 2: setNumber((double) value); break;
                case 3: setImaginary((net.morimekta.test.providence.core.number.Imaginary) value); break;
                default: break;
            }
            return this;
        }

        @Override
        public boolean isSet(int key) {
            switch (key) {
                case 1: return tUnionField == _Field.OPERATION;
                case 2: return tUnionField == _Field.NUMBER;
                case 3: return tUnionField == _Field.IMAGINARY;
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
                case 1: clearOperation(); break;
                case 2: clearNumber(); break;
                case 3: clearImaginary(); break;
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
                case OPERATION: return mOperation != null || mOperation_builder != null;
                case NUMBER: return mNumber != null;
                case IMAGINARY: return mImaginary != null || mImaginary_builder != null;
                default: return true;
            }
        }

        @Override
        public void validate() {
            if (!valid()) {
                throw new java.lang.IllegalStateException("No union field set in calculator.Operand");
            }
        }

        @javax.annotation.Nonnull
        @Override
        public net.morimekta.providence.descriptor.PUnionDescriptor<Operand,_Field> descriptor() {
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
                            mOperation = net.morimekta.providence.serializer.binary.BinaryFormatUtils.readMessage(reader, net.morimekta.test.providence.core.calculator.Operation.kDescriptor, strict);
                            tUnionField = _Field.OPERATION;
                        } else {
                            throw new net.morimekta.providence.serializer.SerializerException("Wrong type " + net.morimekta.providence.serializer.binary.BinaryType.asString(type) + " for calculator.Operand.operation, should be struct(12)");
                        }
                        break;
                    }
                    case 2: {
                        if (type == 4) {
                            mNumber = reader.expectDouble();
                            tUnionField = _Field.NUMBER;
                        } else {
                            throw new net.morimekta.providence.serializer.SerializerException("Wrong type " + net.morimekta.providence.serializer.binary.BinaryType.asString(type) + " for calculator.Operand.number, should be struct(12)");
                        }
                        break;
                    }
                    case 3: {
                        if (type == 12) {
                            mImaginary = net.morimekta.providence.serializer.binary.BinaryFormatUtils.readMessage(reader, net.morimekta.test.providence.core.number.Imaginary.kDescriptor, strict);
                            tUnionField = _Field.IMAGINARY;
                        } else {
                            throw new net.morimekta.providence.serializer.SerializerException("Wrong type " + net.morimekta.providence.serializer.binary.BinaryType.asString(type) + " for calculator.Operand.imaginary, should be struct(12)");
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
        public Operand build() {
            return new Operand(this);
        }
    }
}
