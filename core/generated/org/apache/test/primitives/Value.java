package org.apache.test.primitives;

import java.util.LinkedList;
import java.util.List;

import org.apache.thrift.j2.TEnumBuilder;
import org.apache.thrift.j2.TEnumBuilderFactory;
import org.apache.thrift.j2.TEnumValue;
import org.apache.thrift.j2.descriptor.TEnumDescriptor;
import org.apache.thrift.j2.descriptor.TEnumDescriptorProvider;

public enum Value implements TEnumValue<Value> {
    FIRST(1),
    SECOND(2),
    ;

    private final int mValue;

    Value(int value) {
        mValue = value;
    }

    @Override
    public int getValue() {
        return mValue;
    }

    public static Value valueOf(int value) {
        for (Value e : values()) {
            if (e.mValue == value) return e;
        }
        return null;
    }

    public static class Builder extends TEnumBuilder<Value> {
        Value mValue;

        @Override
        public Builder setByValue(int value) {
            mValue = Value.valueOf(value);
            return this;
        }

        @Override
        public Builder setByName(String name) {
            mValue = Value.valueOf(name);
            return this;
        }

        @Override
        public boolean isValid() {
            return mValue != null;
        }

        @Override
        public Value build() {
            return mValue;
        }
    }

    public static final TEnumDescriptor<Value> DESCRIPTOR = _createDescriptor();

    @Override
    public TEnumDescriptor<Value> descriptor() {
        return DESCRIPTOR;
    }

    public static TEnumDescriptorProvider<Value> provider() {
        return new TEnumDescriptorProvider<Value>(DESCRIPTOR);
    }

    private static class _Factory
            extends TEnumBuilderFactory<Value> {
        @Override
        public Value.Builder builder() {
            return new Value.Builder();
        }
    }

    private static TEnumDescriptor<Value> _createDescriptor() {
        List<TEnumDescriptor.Value> enumValues = new LinkedList<>();
        enumValues.add(new TEnumDescriptor.Value(null, "FIRST", 1));
        enumValues.add(new TEnumDescriptor.Value(null, "SECOND", 2));
        return new TEnumDescriptor<>(null, "primitives", "Value", enumValues, new _Factory());
    }
}
