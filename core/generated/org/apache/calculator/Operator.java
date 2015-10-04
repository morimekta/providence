package org.apache.calculator;

import java.util.LinkedList;
import java.util.List;

import org.apache.thrift2.TEnumBuilder;
import org.apache.thrift2.TEnumBuilderFactory;
import org.apache.thrift2.TEnumValue;
import org.apache.thrift2.descriptor.TEnumDescriptor;
import org.apache.thrift2.descriptor.TEnumDescriptorProvider;

public enum Operator implements TEnumValue<Operator> {
    IDENTITY(1),
    ADD(2),
    SUBTRACT(3),
    MULTIPLY(4),
    DIVIDE(5),
    ;

    private final int mValue;

    Operator(int value) {
        mValue = value;
    }

    @Override
    public int getValue() {
        return mValue;
    }

    public static Operator valueOf(int value) {
        for (Operator e : values()) {
            if (e.mValue == value) return e;
        }
        return null;
    }

    public static class Builder extends TEnumBuilder<Operator> {
        Operator mValue;

        @Override
        public Builder setByValue(int value) {
            mValue = Operator.valueOf(value);
            return this;
        }

        @Override
        public Builder setByName(String name) {
            mValue = Operator.valueOf(name);
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

    public static final TEnumDescriptor<Operator> DESCRIPTOR = _createDescriptor();

    @Override
    public TEnumDescriptor<Operator> descriptor() {
        return DESCRIPTOR;
    }

    public static TEnumDescriptorProvider<Operator> provider() {
        return new TEnumDescriptorProvider<Operator>(DESCRIPTOR);
    }

    private static class _Factory
            extends TEnumBuilderFactory<Operator> {
        @Override
        public Operator.Builder builder() {
            return new Operator.Builder();
        }
    }

    private static TEnumDescriptor<Operator> _createDescriptor() {
        List<TEnumDescriptor.Value> enumValues = new LinkedList<>();
        enumValues.add(new TEnumDescriptor.Value(null, "IDENTITY", 1));
        enumValues.add(new TEnumDescriptor.Value(null, "ADD", 2));
        enumValues.add(new TEnumDescriptor.Value(null, "SUBTRACT", 3));
        enumValues.add(new TEnumDescriptor.Value(null, "MULTIPLY", 4));
        enumValues.add(new TEnumDescriptor.Value(null, "DIVIDE", 5));
        return new TEnumDescriptor<>(null, "calculator", "Operator", enumValues, new _Factory());
    }
}
