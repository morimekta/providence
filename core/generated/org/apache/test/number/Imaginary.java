package org.apache.test.number;

import java.io.Serializable;

import android.os.Parcel;
import android.os.Parcelable;

import org.apache.thrift.j2.TMessage;
import org.apache.thrift.j2.TMessageBuilder;
import org.apache.thrift.j2.TMessageBuilderFactory;
import org.apache.thrift.j2.TType;
import org.apache.thrift.j2.descriptor.TDefaultValueProvider;
import org.apache.thrift.j2.descriptor.TDescriptor;
import org.apache.thrift.j2.descriptor.TDescriptorProvider;
import org.apache.thrift.j2.descriptor.TField;
import org.apache.thrift.j2.descriptor.TPrimitive;
import org.apache.thrift.j2.descriptor.TStructDescriptor;
import org.apache.thrift.j2.descriptor.TStructDescriptorProvider;
import org.apache.thrift.j2.descriptor.TValueProvider;
import org.apache.thrift.j2.util.TTypeUtils;

public class Imaginary
        implements TMessage<Imaginary>, Serializable, Parcelable {
    private final static double kDefaultV = 0.0d;
    private final static double kDefaultI = 0.0d;

    private final Double mV;
    private final Double mI;

    private Imaginary(_Builder builder) {
        mV = builder.mV;
        mI = builder.mI;
    }

    public boolean hasV() {
        return mV != null;
    }

    public double getV() {
        return hasV() ? mV : kDefaultV;
    }

    public boolean hasI() {
        return mI != null;
    }

    public double getI() {
        return hasI() ? mI : kDefaultI;
    }

    @Override
    public boolean has(int key) {
        switch(key) {
            case 1: return hasV();
            case 2: return hasI();
            default: return false;
        }
    }

    @Override
    public int num(int key) {
        switch(key) {
            case 1: return hasV() ? 1 : 0;
            case 2: return hasI() ? 1 : 0;
            default: return 0;
        }
    }

    @Override
    public Object get(int key) {
        switch(key) {
            case 1: return getV();
            case 2: return getI();
            default: return null;
        }
    }

    @Override
    public boolean isCompact() {
        return false;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || !(o instanceof Imaginary)) return false;
        Imaginary other = (Imaginary) o;
        return TTypeUtils.equals(mV, other.mV) &&
               TTypeUtils.equals(mI, other.mI);
    }

    @Override
    public int hashCode() {
        return Imaginary.class.hashCode() +
               TTypeUtils.hashCode(mV) +
               TTypeUtils.hashCode(mI);
    }

    @Override
    public String toString() {
        return getDescriptor().getQualifiedName(null) + TTypeUtils.toString(this);
    }

    @Override
    public boolean isValid() {
        return mV != null;
    }

    public enum _Field implements TField {
        V(1, true, "v", TPrimitive.DOUBLE.provider(), null),
        I(2, false, "i", TPrimitive.DOUBLE.provider(), new TDefaultValueProvider<>(kDefaultI)),
        ;

        private final int mKey;
        private final boolean mRequired;
        private final String mName;
        private final TDescriptorProvider<?> mTypeProvider;
        private final TValueProvider<?> mDefaultValue;

        _Field(int key, boolean required, String name, TDescriptorProvider<?> typeProvider, TValueProvider<?> defaultValue) {
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
            builder.append(Imaginary.class.getSimpleName())
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

        public static _Field forKey(int key) {
            switch (key) {
                case 1: return _Field.V;
                case 2: return _Field.I;
                default: return null;
            }
        }

        public static _Field forName(String name) {
            switch (name) {
                case "v": return _Field.V;
                case "i": return _Field.I;
            }
            return null;
        }
    }

    @Override
    public TStructDescriptor<Imaginary> getDescriptor() {
        return sDescriptor;
    }

    public static TStructDescriptor<Imaginary> descriptor() {
        return sDescriptor;
    }

    public static final TStructDescriptor<Imaginary> sDescriptor;

    private final static class _Factory
            extends TMessageBuilderFactory<Imaginary> {
        @Override
        public _Builder builder() {
            return new _Builder();
        }
    }

    static {
        sDescriptor = new TStructDescriptor<>(null, "number", "Imaginary", _Field.values(), new _Factory(), false);
    }

    public static TStructDescriptorProvider<Imaginary> provider() {
        return new TStructDescriptorProvider<Imaginary>() {
            @Override
            public TStructDescriptor<Imaginary> descriptor() {
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
            dest.writeDouble(mV);
        }
        if (hasI()) {
            dest.writeInt(2);
            dest.writeDouble(mI);
        }
        dest.writeInt(0);
    }

    public static final Parcelable.Creator<Imaginary> CREATOR = new Parcelable.Creator<Imaginary>() {
        @Override
        public Imaginary createFromParcel(Parcel source) {
            _Builder builder = new _Builder();
            loop: while (source.dataAvail() > 0) {
                int field = source.readInt();
                switch (field) {
                    case 0: break loop;
                    case 1:
                        builder.setV(source.readDouble());
                        break;
                    case 2:
                        builder.setI(source.readDouble());
                        break;
                    default: throw new IllegalArgumentException("Unknown field ID: " + field);
                }
            }

            return builder.build();
        }

        @Override
        public Imaginary[] newArray(int size) {
            return new Imaginary[size];
        }
    };

    @Override
    public _Builder mutate() {
        return new _Builder(this);
    }

    public static _Builder builder() {
        return new _Builder();
    }

    public static class _Builder
            extends TMessageBuilder<Imaginary> {
        private Double mV;
        private Double mI;

        public _Builder() {
        }

        public _Builder(Imaginary base) {
            this();

            mV = base.mV;
            mI = base.mI;
        }

        public _Builder setV(double value) {
            mV = value;
            return this;
        }

        public _Builder clearV() {
            mV = null;
            return this;
        }

        public _Builder setI(double value) {
            mI = value;
            return this;
        }

        public _Builder clearI() {
            mI = null;
            return this;
        }

        @Override
        public _Builder set(int key, Object value) {
            switch (key) {
                case 1: setV((double) value); break;
                case 2: setI((double) value); break;
            }
            return this;
        }

        @Override
        public boolean isValid() {
            return mV != null;
        }

        @Override
        public Imaginary build() {
            return new Imaginary(this);
        }
    }
}
