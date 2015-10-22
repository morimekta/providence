package org.apache.test.calculator;

import java.util.LinkedList;
import java.util.List;

import org.apache.thrift.j2.TEnumBuilder;
import org.apache.thrift.j2.TEnumBuilderFactory;
import org.apache.thrift.j2.TEnumValue;
import org.apache.thrift.j2.descriptor.TEnumDescriptor;
import org.apache.thrift.j2.descriptor.TEnumDescriptorProvider;

public enum Operator implements TEnumValue<Operator> {
    IDENTITY(1, "IDENTITY"),
    ADD(2, "ADD"),
    SUBTRACT(3, "SUBTRACT"),
    MULTIPLY(4, "MULTIPLY"),
    DIVIDE(5, "DIVIDE"),
    ;

    private final int mValue;
    private final String mName;

    Operator(int value, String name) {
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

    public static Operator forValue(int value) {
        for (Operator e : values()) {
            if (e.mValue == value) return e;
        }
        return null;
    }

    public static Operator forName(String name) {
        for (Operator e : values()) {
            if (e.mName.equals(name)) return e;
        }
        return null;
    }

    public static class Builder extends TEnumBuilder<Operator> {
        Operator mValue;

        @Override
        public Builder setByValue(int value) {
            mValue = Operator.forValue(value);
            return this;
        }

        @Override
        public Builder setByName(String name) {
            mValue = Operator.forName(name);
            return this;
        }

        @Override
        public boolean isValid() {
            return mValue != null;
        }

        @Override
        public Operator build() {
            return mValue;
        }
    }

    private static final TEnumDescriptor<Operator> sDescriptor;

    @Override
    public TEnumDescriptor<Operator> getDescriptor() {
        return sDescriptor;
    }

    public static TEnumDescriptor<Operator> descriptor() {
        return sDescriptor;
    }

    public static TEnumDescriptorProvider<Operator> provider() {
        return new TEnumDescriptorProvider<Operator>(sDescriptor);
    }

    private static class Factory
            extends TEnumBuilderFactory<Operator> {
        @Override
        public Operator.Builder builder() {
            return new Operator.Builder();
        }
    }

    static {
        sDescriptor = new TEnumDescriptor<>(null, "calculator", "Operator", Operator.values(), new Factory());
    }
}
