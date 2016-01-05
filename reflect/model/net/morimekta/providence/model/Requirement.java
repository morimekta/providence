package net.morimekta.providence.model;

import net.morimekta.providence.PEnumBuilder;
import net.morimekta.providence.PEnumBuilderFactory;
import net.morimekta.providence.PEnumValue;
import net.morimekta.providence.descriptor.PEnumDescriptor;
import net.morimekta.providence.descriptor.PEnumDescriptorProvider;

/** The requirement of the field. */
public enum Requirement implements PEnumValue<Requirement> {
    DEFAULT(0, "DEFAULT"),
    OPTIONAL(1, "OPTIONAL"),
    REQUIRED(2, "REQUIRED"),
    ;

    private final int mValue;
    private final String mName;

    Requirement(int value, String name) {
        mValue = value;
        mName = name;
    }

    @Override
    public String getComment() {
        return null;
    }

    @Override
    public int getValue() {
        return mValue;
    }

    @Override
    public String getName() {
        return mName;
    }

    public static Requirement forValue(int value) {
        switch (value) {
            case 0: return Requirement.DEFAULT;
            case 1: return Requirement.OPTIONAL;
            case 2: return Requirement.REQUIRED;
            default: return null;
        }
    }

    public static Requirement forName(String name) {
        switch (name) {
            case "DEFAULT": return Requirement.DEFAULT;
            case "OPTIONAL": return Requirement.OPTIONAL;
            case "REQUIRED": return Requirement.REQUIRED;
            default: return null;
        }
    }

    public static class _Builder extends PEnumBuilder<Requirement> {
        Requirement mValue;

        @Override
        public _Builder setByValue(int value) {
            mValue = Requirement.forValue(value);
            return this;
        }

        @Override
        public _Builder setByName(String name) {
            mValue = Requirement.forName(name);
            return this;
        }

        @Override
        public boolean isValid() {
            return mValue != null;
        }

        @Override
        public Requirement build() {
            return mValue;
        }
    }

    public static final PEnumDescriptor<Requirement> kDescriptor;

    @Override
    public PEnumDescriptor<Requirement> descriptor() {
        return kDescriptor;
    }

    public static PEnumDescriptorProvider<Requirement> provider() {
        return new PEnumDescriptorProvider<Requirement>(kDescriptor);
    }

    private static class _Factory
            extends PEnumBuilderFactory<Requirement> {
        @Override
        public Requirement._Builder builder() {
            return new Requirement._Builder();
        }
    }

    private static class _Descriptor
            extends PEnumDescriptor<Requirement> {
        public _Descriptor() {
            super(null, "model", "Requirement", new _Factory());
        }

        @Override
        public Requirement[] getValues() {
            return Requirement.values();
        }

        @Override
        public Requirement getValueById(int id) {
            return Requirement.forValue(id);
        }

        @Override
        public Requirement getValueByName(String name) {
            return Requirement.forName(name);
        }
    }

    static {
        kDescriptor = new _Descriptor();
    }
}
