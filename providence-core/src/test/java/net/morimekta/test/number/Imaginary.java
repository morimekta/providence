package net.morimekta.test.number;

@SuppressWarnings("unused")
public class Imaginary
        implements net.morimekta.providence.PMessage<Imaginary>, java.io.Serializable, Comparable<Imaginary> {
    private final static long serialVersionUID = 7869796731524194936L;

    private final static double kDefaultV = 0.0d;
    private final static double kDefaultI = 0.0d;

    private final double mV;
    private final double mI;
    
    private volatile int tHashCode;

    private Imaginary(_Builder builder) {
        mV = builder.mV;
        mI = builder.mI;
    }

    public Imaginary(double pV,
                     double pI) {
        mV = pV;
        mI = pI;
    }

    public boolean hasV() {
        return true;
    }

    public double getV() {
        return mV;
    }

    public boolean hasI() {
        return true;
    }

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
    public boolean compact() {
        return false;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || !(o instanceof Imaginary)) return false;
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
    public String asString() {
        StringBuilder out = new StringBuilder();
        out.append("{");

        boolean first = true;
        if (hasV()) {
            first = false;
            out.append("v:");
            out.append(net.morimekta.util.Strings.asString(mV));
        }
        if (hasI()) {
            if (!first) out.append(',');
            first = false;
            out.append("i:");
            out.append(net.morimekta.util.Strings.asString(mI));
        }
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

    public enum _Field implements net.morimekta.providence.descriptor.PField {
        V(1, net.morimekta.providence.descriptor.PRequirement.REQUIRED, "v", net.morimekta.providence.descriptor.PPrimitive.DOUBLE.provider(), null),
        I(2, net.morimekta.providence.descriptor.PRequirement.DEFAULT, "i", net.morimekta.providence.descriptor.PPrimitive.DOUBLE.provider(), new net.morimekta.providence.descriptor.PDefaultValueProvider<>(kDefaultI)),
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
        public net.morimekta.providence.PType getType() { return getDescriptor().getType(); }

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
            StringBuilder builder = new StringBuilder();
            builder.append("Imaginary._Field(")
                   .append(mKey)
                   .append(": ");
            if (mRequired != net.morimekta.providence.descriptor.PRequirement.DEFAULT) {
                builder.append(mRequired.label).append(" ");
            }
            builder.append(getDescriptor().getQualifiedName(null))
                   .append(' ')
                   .append(mName)
                   .append(')');
            return builder.toString();
        }

        public static _Field forKey(int key) {
            switch (key) {
                case 1: return _Field.V;
                case 2: return _Field.I;
                default: return null;
            }
        }

        public static _Field forName(String name) {
            switch (name) {
                case "v": return _Field.V;
                case "i": return _Field.I;
            }
            return null;
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
            super("number", "Imaginary", new _Factory(), true, false);
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

    private final static class _Provider extends net.morimekta.providence.descriptor.PStructDescriptorProvider<Imaginary,_Field> {
        @Override
        public net.morimekta.providence.descriptor.PStructDescriptor<Imaginary,_Field> descriptor() {
            return kDescriptor;
        }
    }

    private final static class _Factory
            extends net.morimekta.providence.PMessageBuilderFactory<Imaginary> {
        @Override
        public _Builder builder() {
            return new _Builder();
        }
    }

    @Override
    public _Builder mutate() {
        return new _Builder(this);
    }

    public static _Builder builder() {
        return new _Builder();
    }

    public static class _Builder
            extends net.morimekta.providence.PMessageBuilder<Imaginary> {
        private java.util.BitSet optionals;

        private double mV;
        private double mI;


        public _Builder() {
            optionals = new java.util.BitSet(2);
            mV = kDefaultV;
            mI = kDefaultI;
        }

        public _Builder(Imaginary base) {
            this();

            optionals.set(0);
            mV = base.mV;
            optionals.set(1);
            mI = base.mI;
        }

        public _Builder setV(double value) {
            optionals.set(0);
            mV = value;
            return this;
        }
        public boolean isSetV() {
            return optionals.get(0);
        }
        public _Builder clearV() {
            optionals.set(0, false);
            mV = kDefaultV;
            return this;
        }
        public _Builder setI(double value) {
            optionals.set(1);
            mI = value;
            return this;
        }
        public boolean isSetI() {
            return optionals.get(1);
        }
        public _Builder clearI() {
            optionals.set(1, false);
            mI = kDefaultI;
            return this;
        }
        @Override
        public _Builder set(int key, Object value) {
            if (value == null) return clear(key);
            switch (key) {
                case 1: setV((double) value); break;
                case 2: setI((double) value); break;
            }
            return this;
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
                case 1: clearV(); break;
                case 2: clearI(); break;
            }
            return this;
        }

        @Override
        public boolean isValid() {
            return optionals.get(0);
        }

        @Override
        public Imaginary build() {
            return new Imaginary(this);
        }
    }
}
