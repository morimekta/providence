package org.apache.test.alltypes;

import java.util.LinkedList;
import java.util.List;

import org.apache.thrift.j2.TEnumBuilder;
import org.apache.thrift.j2.TEnumBuilderFactory;
import org.apache.thrift.j2.TEnumValue;
import org.apache.thrift.j2.descriptor.TEnumDescriptor;
import org.apache.thrift.j2.descriptor.TEnumDescriptorProvider;

public enum Values implements TEnumValue<Values> {
    FIRST(5, "FIRST"),
    SECOND(3, "SECOND"),
    THIRD(4, "THIRD"),
    FOURTH(1, "FOURTH"),
    FIFTH(2, "FIFTH"),
    ;

    private final int mValue;
    private final String mName;

    Values(int value, String name) {
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

    public static Values forValue(int value) {
        for (Values e : values()) {
            if (e.mValue == value) return e;
        }
        return null;
    }

    public static Values forName(String name) {
        for (Values e : values()) {
            if (e.mName.equals(name)) return e;
        }
        return null;
    }

    public static class Builder extends TEnumBuilder<Values> {
        Values mValue;

        @Override
        public Builder setByValue(int value) {
            mValue = Values.forValue(value);
            return this;
        }

        @Override
        public Builder setByName(String name) {
            mValue = Values.forName(name);
            return this;
        }

        @Override
        public boolean isValid() {
            return mValue != null;
        }

        @Override
        public Values build() {
            return mValue;
        }
    }

    private static final TEnumDescriptor<Values> sDescriptor;

    @Override
    public TEnumDescriptor<Values> getDescriptor() {
        return sDescriptor;
    }

    public static TEnumDescriptor<Values> descriptor() {
        return sDescriptor;
    }

    public static TEnumDescriptorProvider<Values> provider() {
        return new TEnumDescriptorProvider<Values>(sDescriptor);
    }

    private static class Factory
            extends TEnumBuilderFactory<Values> {
        @Override
        public Values.Builder builder() {
            return new Values.Builder();
        }
    }

    static {
        sDescriptor = new TEnumDescriptor<>(null, "alltypes", "Values", Values.values(), new Factory());
    }
}
