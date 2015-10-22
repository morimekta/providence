package org.apache.test.primitives;

import java.util.LinkedList;
import java.util.List;

import org.apache.thrift.j2.TEnumBuilder;
import org.apache.thrift.j2.TEnumBuilderFactory;
import org.apache.thrift.j2.TEnumValue;
import org.apache.thrift.j2.descriptor.TEnumDescriptor;
import org.apache.thrift.j2.descriptor.TEnumDescriptorProvider;

public enum Value implements TEnumValue<Value> {
    FIRST(1, "FIRST"),
    SECOND(2, "SECOND"),
    ;

    private final int mValue;
    private final String mName;

    Value(int value, String name) {
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

    public static Value forValue(int value) {
        for (Value e : values()) {
            if (e.mValue == value) return e;
        }
        return null;
    }

    public static Value forName(String name) {
        for (Value e : values()) {
            if (e.mName.equals(name)) return e;
        }
        return null;
    }

    public static class Builder extends TEnumBuilder<Value> {
        Value mValue;

        @Override
        public Builder setByValue(int value) {
            mValue = Value.forValue(value);
            return this;
        }

        @Override
        public Builder setByName(String name) {
            mValue = Value.forName(name);
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

    private static final TEnumDescriptor<Value> sDescriptor;

    @Override
    public TEnumDescriptor<Value> getDescriptor() {
        return sDescriptor;
    }

    public static TEnumDescriptor<Value> descriptor() {
        return sDescriptor;
    }

    public static TEnumDescriptorProvider<Value> provider() {
        return new TEnumDescriptorProvider<Value>(sDescriptor);
    }

    private static class Factory
            extends TEnumBuilderFactory<Value> {
        @Override
        public Value.Builder builder() {
            return new Value.Builder();
        }
    }

    static {
        sDescriptor = new TEnumDescriptor<>(null, "primitives", "Value", Value.values(), new Factory());
    }
}
