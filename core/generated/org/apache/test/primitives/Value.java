package org.apache.test.primitives;

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
        switch (value) {
            case 1: return Value.FIRST;
            case 2: return Value.SECOND;
            default: return null;
        }
    }

    public static Value forName(String name) {
        switch (name) {
            case "FIRST": return Value.FIRST;
            case "SECOND": return Value.SECOND;
            default: return null;
        }
    }

    public static class _Builder extends TEnumBuilder<Value> {
        Value mValue;

        @Override
        public _Builder setByValue(int value) {
            mValue = Value.forValue(value);
            return this;
        }

        @Override
        public _Builder setByName(String name) {
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

    private static class _Factory
            extends TEnumBuilderFactory<Value> {
        @Override
        public Value._Builder builder() {
            return new Value._Builder();
        }
    }

    static {
        sDescriptor = new TEnumDescriptor<>(null, "primitives", "Value", Value.values(), new _Factory());
    }
}
