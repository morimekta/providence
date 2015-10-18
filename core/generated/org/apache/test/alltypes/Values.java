package org.apache.test.alltypes;

import java.util.LinkedList;
import java.util.List;

import org.apache.thrift2.TEnumBuilder;
import org.apache.thrift2.TEnumBuilderFactory;
import org.apache.thrift2.TEnumValue;
import org.apache.thrift2.descriptor.TEnumDescriptor;
import org.apache.thrift2.descriptor.TEnumDescriptorProvider;

public enum Values implements TEnumValue<Values> {
    FIRST(5),
    SECOND(3),
    THIRD(4),
    FOURTH(1),
    FIFTH(2),
    ;

    private final int mValue;

    Values(int value) {
        mValue = value;
    }

    @Override
    public int getValue() {
        return mValue;
    }

    public static Values valueOf(int value) {
        for (Values e : values()) {
            if (e.mValue == value) return e;
        }
        return null;
    }

    public static class Builder extends TEnumBuilder<Values> {
        Values mValue;

        @Override
        public Builder setByValue(int value) {
            mValue = Values.valueOf(value);
            return this;
        }

        @Override
        public Builder setByName(String name) {
            mValue = Values.valueOf(name);
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

    public static final TEnumDescriptor<Values> DESCRIPTOR = _createDescriptor();

    @Override
    public TEnumDescriptor<Values> descriptor() {
        return DESCRIPTOR;
    }

    public static TEnumDescriptorProvider<Values> provider() {
        return new TEnumDescriptorProvider<Values>(DESCRIPTOR);
    }

    private static class _Factory
            extends TEnumBuilderFactory<Values> {
        @Override
        public Values.Builder builder() {
            return new Values.Builder();
        }
    }

    private static TEnumDescriptor<Values> _createDescriptor() {
        List<TEnumDescriptor.Value> enumValues = new LinkedList<>();
        enumValues.add(new TEnumDescriptor.Value(null, "FIRST", 5));
        enumValues.add(new TEnumDescriptor.Value(null, "SECOND", 3));
        enumValues.add(new TEnumDescriptor.Value(null, "THIRD", 4));
        enumValues.add(new TEnumDescriptor.Value(null, "FOURTH", 1));
        enumValues.add(new TEnumDescriptor.Value(null, "FIFTH", 2));
        return new TEnumDescriptor<>(null, "alltypes", "Values", enumValues, new _Factory());
    }
}
