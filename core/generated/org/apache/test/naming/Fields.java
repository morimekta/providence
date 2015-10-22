package org.apache.test.naming;

import org.apache.thrift.j2.TEnumBuilder;
import org.apache.thrift.j2.TEnumBuilderFactory;
import org.apache.thrift.j2.TEnumValue;
import org.apache.thrift.j2.descriptor.TEnumDescriptor;
import org.apache.thrift.j2.descriptor.TEnumDescriptorProvider;

public enum Fields implements TEnumValue<Fields> {
    SDESCRIPTOR(0, "sDescriptor"),
    MNAME(1, "mName"),
    MVALUE(2, "mValue"),
    FIELD(3, "Field"),
    ;

    private final int mValue;
    private final String mName;

    Fields(int value, String name) {
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

    public static Fields forValue(int value) {
        switch (value) {
            case 0: return Fields.SDESCRIPTOR;
            case 1: return Fields.MNAME;
            case 2: return Fields.MVALUE;
            case 3: return Fields.FIELD;
            default: return null;
        }
    }

    public static Fields forName(String name) {
        switch (name) {
            case "sDescriptor": return Fields.SDESCRIPTOR;
            case "mName": return Fields.MNAME;
            case "mValue": return Fields.MVALUE;
            case "Field": return Fields.FIELD;
            default: return null;
        }
    }

    public static class _Builder extends TEnumBuilder<Fields> {
        Fields mValue;

        @Override
        public _Builder setByValue(int value) {
            mValue = Fields.forValue(value);
            return this;
        }

        @Override
        public _Builder setByName(String name) {
            mValue = Fields.forName(name);
            return this;
        }

        @Override
        public boolean isValid() {
            return mValue != null;
        }

        @Override
        public Fields build() {
            return mValue;
        }
    }

    private static final TEnumDescriptor<Fields> sDescriptor;

    @Override
    public TEnumDescriptor<Fields> getDescriptor() {
        return sDescriptor;
    }

    public static TEnumDescriptor<Fields> descriptor() {
        return sDescriptor;
    }

    public static TEnumDescriptorProvider<Fields> provider() {
        return new TEnumDescriptorProvider<Fields>(sDescriptor);
    }

    private static class _Factory
            extends TEnumBuilderFactory<Fields> {
        @Override
        public Fields._Builder builder() {
            return new Fields._Builder();
        }
    }

    static {
        sDescriptor = new TEnumDescriptor<>(null, "naming", "Fields", Fields.values(), new _Factory());
    }
}
