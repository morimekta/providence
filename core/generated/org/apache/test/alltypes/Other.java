package org.apache.test.alltypes;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;

import org.apache.thrift.j2.TMessage;
import org.apache.thrift.j2.TMessageBuilder;
import org.apache.thrift.j2.TMessageBuilderFactory;
import org.apache.thrift.j2.TType;
import org.apache.thrift.j2.descriptor.TDescriptor;
import org.apache.thrift.j2.descriptor.TDescriptorProvider;
import org.apache.thrift.j2.descriptor.TField;
import org.apache.thrift.j2.descriptor.TStructDescriptor;
import org.apache.thrift.j2.descriptor.TStructDescriptorProvider;
import org.apache.thrift.j2.descriptor.TValueProvider;
import org.apache.thrift.j2.util.TTypeUtils;

public class Other
        implements TMessage<Other>, Serializable, Parcelable {
    private final Values mV;

    private Other(Builder builder) {
        mV = builder.mV;
    }

    public boolean hasV() {
        return mV != null;
    }

    public Values getV() {
        return mV;
    }

    @Override
    public boolean has(int key) {
        switch(key) {
            case 1: return hasV();
            default: return false;
        }
    }

    @Override
    public int num(int key) {
        switch(key) {
            case 1: return hasV() ? 1 : 0;
            default: return 0;
        }
    }

    @Override
    public Object get(int key) {
        switch(key) {
            case 1: return getV();
            default: return null;
        }
    }

    @Override
    public boolean isCompact() {
        return false;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || !(o instanceof Other)) return false;
        Other other = (Other) o;
        return TTypeUtils.equals(mV, other.mV);
    }

    @Override
    public int hashCode() {
        return Other.class.hashCode() +
               TTypeUtils.hashCode(mV);
    }

    @Override
    public String toString() {
        return getDescriptor().getQualifiedName(null) + TTypeUtils.toString(this);
    }

    @Override
    public boolean isValid() {
        return true;
    }

    public enum Field implements TField {
        V(1, false, "v", Values.provider(), null),
        ;

        private final int mKey;
        private final boolean mRequired;
        private final String mName;
        private final TDescriptorProvider<?> mTypeProvider;
        private final TValueProvider<?> mDefaultValue;

        Field(int key, boolean required, String name, TDescriptorProvider<?> typeProvider, TValueProvider<?> defaultValue) {
            mKey = key;
            mRequired = required;
            mName = name;
            mTypeProvider = typeProvider;
            mDefaultValue = defaultValue;
        }

        @Override
        public String getComment() { return null; }

        @Override
        public int getKey() { return mKey; }

        @Override
        public boolean getRequired() { return mRequired; }

        @Override
        public TType getType() { return mTypeProvider.descriptor().getType(); }

        @Override
        public TDescriptor<?> getDescriptor() { return mTypeProvider.descriptor(); }

        @Override
        public String getName() { return mName; }

        @Override
        public boolean hasDefaultValue() { return mDefaultValue != null; }

        @Override
        public Object getDefaultValue() {
            return hasDefaultValue() ? mDefaultValue.get() : null;
        }

        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();
            builder.append(Other.class.getSimpleName())
                   .append('{')
                   .append(mKey)
                   .append(": ");
            if (mRequired) {
                builder.append("required ");
            }
            builder.append(getDescriptor().getQualifiedName(null))
                   .append(' ')
                   .append(mName)
                   .append('}');
            return builder.toString();
        }

        public static Field forKey(int key) {
            for (Field field : values()) {
                if (field.mKey == key) return field;
            }
            return null;
        }

        public static Field forName(String name) {
            for (Field field : values()) {
                if (field.mName.equals(name)) return field;
            }
            return null;
        }
    }

    @Override
    public TStructDescriptor<Other> getDescriptor() {
        return sDescriptor;
    }

    public static TStructDescriptor<Other> descriptor() {
        return sDescriptor;
    }

    public static final TStructDescriptor<Other> sDescriptor;

    private final static class Factory
            extends TMessageBuilderFactory<Other> {
        @Override
        public Other.Builder builder() {
            return new Other.Builder();
        }
    }

    static {
        sDescriptor = new TStructDescriptor<>(null, "alltypes", "Other", Other.Field.values(), new Factory(), false);
    }

    public static TStructDescriptorProvider<Other> provider() {
        return new TStructDescriptorProvider<Other>() {
            @Override
            public TStructDescriptor<Other> descriptor() {
                return sDescriptor;
            }
        };
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (hasV()) {
            dest.writeInt(1);
            dest.writeInt(mV.getValue());
        }
        dest.writeInt(0);
    }

    public static final Parcelable.Creator<Other> CREATOR = new Parcelable.Creator<Other>() {
        @Override
        public Other createFromParcel(Parcel source) {
            Other.Builder builder = new Other.Builder();
            loop: while (source.dataAvail() > 0) {
                int field = source.readInt();
                switch (field) {
                    case 0: break loop;
                    case 1:
                        builder.setV(Values.forValue(source.readInt()));
                        break;
                    default: throw new IllegalArgumentException("Unknown field ID: " + field);
                }
            }

            return builder.build();
        }

        @Override
        public Other[] newArray(int size) {
            return new Other[size];
        }
    };

    @Override
    public Other.Builder mutate() {
        return new Other.Builder(this);
    }

    public static Other.Builder builder() {
        return new Other.Builder();
    }

    public static class Builder
            extends TMessageBuilder<Other> {
        private Values mV;

        public Builder() {
        }

        public Builder(Other base) {
            this();

            mV = base.mV;
        }

        public Builder setV(Values value) {
            mV = value;
            return this;
        }

        public Builder clearV() {
            mV = null;
            return this;
        }

        @Override
        public Builder set(int key, Object value) {
            switch (key) {
                case 1: setV((Values) value); break;
            }
            return this;
        }

        @Override
        public boolean isValid() {
            return true;
        }

        @Override
        public Other build() {
            return new Other(this);
        }
    }
}
