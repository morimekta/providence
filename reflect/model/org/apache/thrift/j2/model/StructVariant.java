package org.apache.thrift.j2.model;

import java.util.LinkedList;
import java.util.List;

import org.apache.thrift.j2.TEnumBuilder;
import org.apache.thrift.j2.TEnumBuilderFactory;
import org.apache.thrift.j2.TEnumValue;
import org.apache.thrift.j2.descriptor.TEnumDescriptor;
import org.apache.thrift.j2.descriptor.TEnumDescriptorProvider;

/**
 * Struct variant for StructType. The lower-case of the enum value is the
 * thrift keyword.
 * 
 * struct: No 'required' fields must be present (set to non-null value).
 * UNION: No required fields. Only one field set to be valid.
 * EXCEPTION: No 'cause' field, 'message' field *must* be a string (java).
 */
public enum StructVariant implements TEnumValue<StructVariant> {
    STRUCT(1, "STRUCT"),
    UNION(2, "UNION"),
    EXCEPTION(3, "EXCEPTION"),
    ;

    private final int mValue;
    private final String mName;

    StructVariant(int value, String name) {
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

    public static StructVariant forValue(int value) {
        for (StructVariant e : values()) {
            if (e.mValue == value) return e;
        }
        return null;
    }

    public static StructVariant forName(String name) {
        for (StructVariant e : values()) {
            if (e.mName.equals(name)) return e;
        }
        return null;
    }

    public static class Builder extends TEnumBuilder<StructVariant> {
        StructVariant mValue;

        @Override
        public Builder setByValue(int value) {
            mValue = StructVariant.forValue(value);
            return this;
        }

        @Override
        public Builder setByName(String name) {
            mValue = StructVariant.forName(name);
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

    private static final TEnumDescriptor<StructVariant> sDescriptor;

    @Override
    public TEnumDescriptor<StructVariant> getDescriptor() {
        return sDescriptor;
    }

    public static TEnumDescriptor<StructVariant> descriptor() {
        return sDescriptor;
    }

    public static TEnumDescriptorProvider<StructVariant> provider() {
        return new TEnumDescriptorProvider<StructVariant>(sDescriptor);
    }

    private static class Factory
            extends TEnumBuilderFactory<StructVariant> {
        @Override
        public StructVariant.Builder builder() {
            return new StructVariant.Builder();
        }
    }

    static {
        sDescriptor = new TEnumDescriptor<>(null, "model", "StructVariant", StructVariant.values(), new Factory());
    }
}
