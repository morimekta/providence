package org.apache.test.alltypes;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;

import org.apache.thrift.j2.TMessage;
import org.apache.thrift.j2.TMessageBuilder;
import org.apache.thrift.j2.TMessageBuilderFactory;
import org.apache.thrift.j2.descriptor.TField;
import org.apache.thrift.j2.descriptor.TStructDescriptor;
import org.apache.thrift.j2.descriptor.TStructDescriptorProvider;
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
    public boolean compact() {
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
        return descriptor().getQualifiedName(null) + TTypeUtils.toString(this);
    }

    @Override
    public boolean isValid() {
        return true;
    }

    @Override
    public TStructDescriptor<Other> descriptor() {
        return DESCRIPTOR;
    }

    public static final TStructDescriptor<Other> DESCRIPTOR = _createDescriptor();

    private final static class _Factory
            extends TMessageBuilderFactory<Other> {
        @Override
        public Other.Builder builder() {
            return new Other.Builder();
        }
    }

    private static TStructDescriptor<Other> _createDescriptor() {
        List<TField<?>> fieldList = new LinkedList<>();
        fieldList.add(new TField<>(null, 1, false, "v", Values.provider(), null));
        return new TStructDescriptor<>(null, "alltypes", "Other", fieldList, new _Factory(), false);
    }

    public static TStructDescriptorProvider<Other> provider() {
        return new TStructDescriptorProvider<Other>() {
            @Override
            public TStructDescriptor<Other> descriptor() {
                return DESCRIPTOR;
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
                        builder.setV(Values.valueOf(source.readInt()));
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
