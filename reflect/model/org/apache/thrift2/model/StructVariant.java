package org.apache.thrift2.model;

import java.util.LinkedList;
import java.util.List;

import org.apache.thrift2.TEnumBuilder;
import org.apache.thrift2.TEnumBuilderFactory;
import org.apache.thrift2.TEnumValue;
import org.apache.thrift2.descriptor.TEnumDescriptor;
import org.apache.thrift2.descriptor.TEnumDescriptorProvider;

/**
 * Struct variant for StructType. The lower-case of the enum value is the
 * thrift keyword.
 * 
 * struct: No 'required' fields must be present (set to non-null value).
 * UNION: No required fields. Only one field set to be valid.
 * EXCEPTION: No 'cause' field, 'message' field *must* be a string (java).
 */
public enum StructVariant implements TEnumValue<StructVariant> {
    STRUCT(1),
    UNION(2),
    EXCEPTION(3),
    ;

    private final int mValue;

    StructVariant(int value) {
        mValue = value;
    }

    @Override
    public int getValue() {
        return mValue;
    }

    public static StructVariant valueOf(int value) {
        for (StructVariant e : values()) {
            if (e.mValue == value) return e;
        }
        return null;
    }

    public static class Builder extends TEnumBuilder<StructVariant> {
        StructVariant mValue;

        @Override
        public Builder setByValue(int value) {
            mValue = StructVariant.valueOf(value);
            return this;
        }

        @Override
        public Builder setByName(String name) {
            mValue = StructVariant.valueOf(name);
            return this;
        }

        @Override
        public boolean isValid() {
            return mValue != null;
        }

        @Override
        public StructVariant build() {
            return mValue;
        }
    }

    public static final TEnumDescriptor<StructVariant> DESCRIPTOR = _createDescriptor();

    @Override
    public TEnumDescriptor<StructVariant> descriptor() {
        return DESCRIPTOR;
    }

    public static TEnumDescriptorProvider<StructVariant> provider() {
        return new TEnumDescriptorProvider<StructVariant>(DESCRIPTOR);
    }

    private static class _Factory
            extends TEnumBuilderFactory<StructVariant> {
        @Override
        public StructVariant.Builder builder() {
            return new StructVariant.Builder();
        }
    }

    private static TEnumDescriptor<StructVariant> _createDescriptor() {
        List<TEnumDescriptor.Value> enumValues = new LinkedList<>();
        enumValues.add(new TEnumDescriptor.Value(null, "STRUCT", 1));
        enumValues.add(new TEnumDescriptor.Value(null, "UNION", 2));
        enumValues.add(new TEnumDescriptor.Value(null, "EXCEPTION", 3));
        return new TEnumDescriptor<>(null, "model", "StructVariant", enumValues, new _Factory());
    }
}
