package net.morimekta.test.providence.core.number;

@SuppressWarnings("unused")
@javax.annotation.Generated("providence-maven-plugin")
public class Imaginary
        implements net.morimekta.providence.PMessage<Imaginary,Imaginary._Field>,
                   Comparable<Imaginary>,
                   java.io.Serializable,
                   net.morimekta.providence.serializer.binary.BinaryWriter {
    private final static long serialVersionUID = 7869796731524194936L;

    private final static double kDefaultV = 0.0d;
    private final static double kDefaultI = 0.0d;

    private final double mV;
    private final double mI;

    private volatile int tHashCode;

    public Imaginary(double pV,
                     Double pI) {
        mV = pV;
        if (pI != null) {
            mI = pI;
        } else {
            mI = kDefaultI;
        }
    }

    private Imaginary(_Builder builder) {
        mV = builder.mV;
        mI = builder.mI;
    }

    public boolean hasV() {
        return true;
    }

    /**
     * @return The field value
     */
    public double getV() {
        return mV;
    }

    public boolean hasI() {
        return true;
    }

    /**
     * @return The field value
     */
    public double getI() {
        return mI;
    }

    @Override
    public boolean has(int key) {
        switch(key) {
            case 1: return true;
            case 2: return true;
            default: return false;
        }
    }

    @Override
    public int num(int key) {
        switch(key) {
            case 1: return 1;
            case 2: return 1;
            default: return 0;
        }
    }

    @Override
    public Object get(int key) {
        switch(key) {
            case 1: return getV();
            case 2: return getI();
            default: return null;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        if (o == null || !o.getClass().equals(getClass())) return false;
        Imaginary other = (Imaginary) o;
        return java.util.Objects.equals(mV, other.mV) &&
               java.util.Objects.equals(mI, other.mI);
    }

    @Override
    public int hashCode() {
        if (tHashCode == 0) {
            tHashCode = java.util.Objects.hash(
                    Imaginary.class,
                    _Field.V, mV,
                    _Field.I, mI);
        }
        return tHashCode;
    }

    @Override
    public String toString() {
        return "number.Imaginary" + asString();
    }

    @Override
    @javax.annotation.Nonnull
    public String asString() {
        StringBuilder out = new StringBuilder();
        out.append("{");

        out.append("v:")
           .append(net.morimekta.util.Strings.asString(mV));
        out.append(',');
        out.append("i:")
           .append(net.morimekta.util.Strings.asString(mI));
        out.append('}');
        return out.toString();
    }

    @Override
    public int compareTo(Imaginary other) {
        int c;

        c = Double.compare(mV, other.mV);
        if (c != 0) return c;

        c = Double.compare(mI, other.mI);
        if (c != 0) return c;

        return 0;
    }

    @Override
    public int writeBinary(net.morimekta.util.io.BigEndianBinaryWriter writer) throws java.io.IOException {
        int length = 0;

        length += writer.writeByte((byte) 4);
        length += writer.writeShort((short) 1);
        length += writer.writeDouble(mV);

        length += writer.writeByte((byte) 4);
        length += writer.writeShort((short) 2);
        length += writer.writeDouble(mI);

        length += writer.writeByte((byte) 0);
        return length;
    }

    @javax.annotation.Nonnull
    @Override
    public _Builder mutate() {
        return new _Builder(this);
    }

    public enum _Field implements net.morimekta.providence.descriptor.PField {
        V(1, net.morimekta.providence.descriptor.PRequirement.REQUIRED, "v", net.morimekta.providence.descriptor.PPrimitive.DOUBLE.provider(), null),
        I(2, net.morimekta.providence.descriptor.PRequirement.DEFAULT, "i", net.morimekta.providence.descriptor.PPrimitive.DOUBLE.provider(), new net.morimekta.providence.descriptor.PDefaultValueProvider<>(kDefaultI)),
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
                case 1: return _Field.V;
                case 2: return _Field.I;
            }
            return null;
        }

        /**
         * @param name Field name
         * @return The named field or null
         */
        public static _Field findByName(String name) {
            switch (name) {
                case "v": return _Field.V;
                case "i": return _Field.I;
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
                throw new IllegalArgumentException("No such field id " + id + " in number.Imaginary");
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
                throw new IllegalArgumentException("No such field \"" + name + "\" in number.Imaginary");
            }
            return field;
        }

    }

    public static net.morimekta.providence.descriptor.PStructDescriptorProvider<Imaginary,_Field> provider() {
        return new _Provider();
    }

    @Override
    public net.morimekta.providence.descriptor.PStructDescriptor<Imaginary,_Field> descriptor() {
        return kDescriptor;
    }

    public static final net.morimekta.providence.descriptor.PStructDescriptor<Imaginary,_Field> kDescriptor;

    private static class _Descriptor
            extends net.morimekta.providence.descriptor.PStructDescriptor<Imaginary,_Field> {
        public _Descriptor() {
            super("number", "Imaginary", _Builder::new, true);
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

    private final static class _Provider extends net.morimekta.providence.descriptor.PStructDescriptorProvider<Imaginary,_Field> {
        @Override
        public net.morimekta.providence.descriptor.PStructDescriptor<Imaginary,_Field> descriptor() {
            return kDescriptor;
        }
    }

    /**
     * Make a number.Imaginary builder.
     * @return The builder instance.
     */
    public static _Builder builder() {
        return new _Builder();
    }

    public static class _Builder
            extends net.morimekta.providence.PMessageBuilder<Imaginary,_Field>
            implements net.morimekta.providence.serializer.binary.BinaryReader {
        private java.util.BitSet optionals;
        private java.util.BitSet modified;

        private double mV;
        private double mI;

        /**
         * Make a number.Imaginary builder.
         */
        public _Builder() {
            optionals = new java.util.BitSet(2);
            modified = new java.util.BitSet(2);
            mV = kDefaultV;
            mI = kDefaultI;
        }

        /**
         * Make a mutating builder off a base number.Imaginary.
         *
         * @param base The base Imaginary
         */
        public _Builder(Imaginary base) {
            this();

            optionals.set(0);
            mV = base.mV;
            optionals.set(1);
            mI = base.mI;
        }

        @javax.annotation.Nonnull
        @Override
        public _Builder merge(Imaginary from) {
            optionals.set(0);
            modified.set(0);
            mV = from.getV();

            optionals.set(1);
            modified.set(1);
            mI = from.getI();
            return this;
        }

        /**
         * Sets the value of v.
         *
         * @param value The new value
         * @return The builder
         */
        @javax.annotation.Nonnull
        public _Builder setV(double value) {
            optionals.set(0);
            modified.set(0);
            mV = value;
            return this;
        }

        /**
         * Checks for presence of the v field.
         *
         * @return True if v has been set.
         */
        public boolean isSetV() {
            return optionals.get(0);
        }

        /**
         * Checks if v has been modified since the _Builder was created.
         *
         * @return True if v has been modified.
         */
        public boolean isModifiedV() {
            return modified.get(0);
        }

        /**
         * Clears the v field.
         *
         * @return The builder
         */
        @javax.annotation.Nonnull
        public _Builder clearV() {
            optionals.clear(0);
            modified.set(0);
            mV = kDefaultV;
            return this;
        }

        /**
         * Gets the value of the contained v.
         *
         * @return The field value
         */
        public double getV() {
            return mV;
        }

        /**
         * Sets the value of i.
         *
         * @param value The new value
         * @return The builder
         */
        @javax.annotation.Nonnull
        public _Builder setI(double value) {
            optionals.set(1);
            modified.set(1);
            mI = value;
            return this;
        }

        /**
         * Checks for presence of the i field.
         *
         * @return True if i has been set.
         */
        public boolean isSetI() {
            return optionals.get(1);
        }

        /**
         * Checks if i has been modified since the _Builder was created.
         *
         * @return True if i has been modified.
         */
        public boolean isModifiedI() {
            return modified.get(1);
        }

        /**
         * Clears the i field.
         *
         * @return The builder
         */
        @javax.annotation.Nonnull
        public _Builder clearI() {
            optionals.clear(1);
            modified.set(1);
            mI = kDefaultI;
            return this;
        }

        /**
         * Gets the value of the contained i.
         *
         * @return The field value
         */
        public double getI() {
            return mI;
        }

        @Override
        public boolean equals(Object o) {
            if (o == this) return true;
            if (o == null || !o.getClass().equals(getClass())) return false;
            Imaginary._Builder other = (Imaginary._Builder) o;
            return java.util.Objects.equals(optionals, other.optionals) &&
                   java.util.Objects.equals(mV, other.mV) &&
                   java.util.Objects.equals(mI, other.mI);
        }

        @Override
        public int hashCode() {
            return java.util.Objects.hash(
                    Imaginary.class, optionals,
                    _Field.V, mV,
                    _Field.I, mI);
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
                case 1: setV((double) value); break;
                case 2: setI((double) value); break;
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
                default: break;
            }
            return this;
        }

        @javax.annotation.Nonnull
        @Override
        public _Builder clear(int key) {
            switch (key) {
                case 1: clearV(); break;
                case 2: clearI(); break;
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
                java.util.LinkedList<String> missing = new java.util.LinkedList<>();

                if (!optionals.get(0)) {
                    missing.add("v");
                }

                throw new java.lang.IllegalStateException(
                        "Missing required fields " +
                        String.join(",", missing) +
                        " in message number.Imaginary");
            }
        }

        @javax.annotation.Nonnull
        @Override
        public net.morimekta.providence.descriptor.PStructDescriptor<Imaginary,_Field> descriptor() {
            return kDescriptor;
        }

        @Override
        public void readBinary(net.morimekta.util.io.BigEndianBinaryReader reader, boolean strict) throws java.io.IOException {
            byte type = reader.expectByte();
            while (type != 0) {
                int field = reader.expectShort();
                switch (field) {
                    case 1: {
                        if (type == 4) {
                            mV = reader.expectDouble();
                            optionals.set(0);
                        } else {
                            throw new net.morimekta.providence.serializer.SerializerException("Wrong type " + net.morimekta.providence.serializer.binary.BinaryType.asString(type) + " for number.Imaginary.v, should be struct(12)");
                        }
                        break;
                    }
                    case 2: {
                        if (type == 4) {
                            mI = reader.expectDouble();
                            optionals.set(1);
                        } else {
                            throw new net.morimekta.providence.serializer.SerializerException("Wrong type " + net.morimekta.providence.serializer.binary.BinaryType.asString(type) + " for number.Imaginary.i, should be struct(12)");
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
        public Imaginary build() {
            return new Imaginary(this);
        }
    }
}
