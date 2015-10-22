package org.apache.test.alltypes;

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
        switch (value) {
            case 5: return Values.FIRST;
            case 3: return Values.SECOND;
            case 4: return Values.THIRD;
            case 1: return Values.FOURTH;
            case 2: return Values.FIFTH;
            default: return null;
        }
    }

    public static Values forName(String name) {
        switch (name) {
            case "FIRST": return Values.FIRST;
            case "SECOND": return Values.SECOND;
            case "THIRD": return Values.THIRD;
            case "FOURTH": return Values.FOURTH;
            case "FIFTH": return Values.FIFTH;
            default: return null;
        }
    }

    public static class _Builder extends TEnumBuilder<Values> {
        Values mValue;

        @Override
        public _Builder setByValue(int value) {
            mValue = Values.forValue(value);
            return this;
        }

        @Override
        public _Builder setByName(String name) {
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

    private static class _Factory
            extends TEnumBuilderFactory<Values> {
        @Override
        public Values._Builder builder() {
            return new Values._Builder();
        }
    }

    static {
        sDescriptor = new TEnumDescriptor<>(null, "alltypes", "Values", Values.values(), new _Factory());
    }
}
