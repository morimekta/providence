package net.morimekta.providence.test_internal;

import javax.annotation.Nonnull;

public enum Value
        implements net.morimekta.providence.PEnumValue<Value> {
    FIRST(1, "FIRST"),
    SECOND(2, "SECOND"),
    THIRD(3, "THIRD"),
    FOURTH(5, "FOURTH"),
    FIFTH(8, "FIFTH"),
    SIXTH(13, "SIXTH"),
    SEVENTH(21, "SEVENTH"),
    EIGHTH(34, "EIGHTH"),
    NINTH(55, "NINTH"),
    TENTH(89, "TENTH"),
    ELEVENTH(144, "ELEVENTH"),
    TWELWETH(233, "TWELWETH"),
    /**
     * &#64;Deprecated
     */
    THIRTEENTH(377, "THIRTEENTH"),
    FOURTEENTH(610, "FOURTEENTH"),
    FIFTEENTH(987, "FIFTEENTH"),
    SIXTEENTH(1597, "SIXTEENTH"),
    SEVENTEENTH(2584, "SEVENTEENTH"),
    EIGHTEENTH(4181, "EIGHTEENTH"),
    NINTEENTH(6765, "NINTEENTH"),
    TWENTIETH(10946, "TWENTIETH"),
    ;

    private final int mValue;
    private final String mName;

    Value(int value, String name) {
        mValue = value;
        mName = name;
    }

    @Override
    public int asInteger() {
        return mValue;
    }

    @Override
    public String asString() {
        return mName;
    }

    public static Value forValue(int value) {
        switch (value) {
            case 1: return Value.FIRST;
            case 2: return Value.SECOND;
            case 3: return Value.THIRD;
            case 5: return Value.FOURTH;
            case 8: return Value.FIFTH;
            case 13: return Value.SIXTH;
            case 21: return Value.SEVENTH;
            case 34: return Value.EIGHTH;
            case 55: return Value.NINTH;
            case 89: return Value.TENTH;
            case 144: return Value.ELEVENTH;
            case 233: return Value.TWELWETH;
            case 377: return Value.THIRTEENTH;
            case 610: return Value.FOURTEENTH;
            case 987: return Value.FIFTEENTH;
            case 1597: return Value.SIXTEENTH;
            case 2584: return Value.SEVENTEENTH;
            case 4181: return Value.EIGHTEENTH;
            case 6765: return Value.NINTEENTH;
            case 10946: return Value.TWENTIETH;
            default: return null;
        }
    }

    public static Value forName(String name) {
        switch (name) {
            case "FIRST": return Value.FIRST;
            case "SECOND": return Value.SECOND;
            case "THIRD": return Value.THIRD;
            case "FOURTH": return Value.FOURTH;
            case "FIFTH": return Value.FIFTH;
            case "SIXTH": return Value.SIXTH;
            case "SEVENTH": return Value.SEVENTH;
            case "EIGHTH": return Value.EIGHTH;
            case "NINTH": return Value.NINTH;
            case "TENTH": return Value.TENTH;
            case "ELEVENTH": return Value.ELEVENTH;
            case "TWELWETH": return Value.TWELWETH;
            case "THIRTEENTH": return Value.THIRTEENTH;
            case "FOURTEENTH": return Value.FOURTEENTH;
            case "FIFTEENTH": return Value.FIFTEENTH;
            case "SIXTEENTH": return Value.SIXTEENTH;
            case "SEVENTEENTH": return Value.SEVENTEENTH;
            case "EIGHTEENTH": return Value.EIGHTEENTH;
            case "NINTEENTH": return Value.NINTEENTH;
            case "TWENTIETH": return Value.TWENTIETH;
            default: return null;
        }
    }

    public static class _Builder extends net.morimekta.providence.PEnumBuilder<Value> {
        Value mValue;

        @Nonnull
        @Override
        public _Builder setById(int value) {
            mValue = Value.forValue(value);
            return this;
        }

        @Nonnull
        @Override
        public _Builder setByName(String name) {
            mValue = Value.forName(name);
            return this;
        }

        @Override
        public boolean valid() {
            return mValue != null;
        }

        @Override
        public Value build() {
            return mValue;
        }
    }

    public static final net.morimekta.providence.descriptor.PEnumDescriptor<Value> kDescriptor;

    @Override
    public net.morimekta.providence.descriptor.PEnumDescriptor<Value> descriptor() {
        return kDescriptor;
    }

    public static net.morimekta.providence.descriptor.PEnumDescriptorProvider<Value> provider() {
        return new net.morimekta.providence.descriptor.PEnumDescriptorProvider<Value>(kDescriptor);
    }

    private static class _Descriptor
            extends net.morimekta.providence.descriptor.PEnumDescriptor<Value> {
        public _Descriptor() {
            super("providence", "Value", _Builder::new);
        }

        @Nonnull
        @Override
        public Value[] getValues() {
            return Value.values();
        }

        @Override
        public Value findById(int id) {
            return Value.forValue(id);
        }

        @Override
        public Value findByName(String name) {
            return Value.forName(name);
        }
    }

    static {
        kDescriptor = new _Descriptor();
    }
}
