package org.apache.test.calculator;

import java.io.Serializable;

import android.os.Parcel;
import android.os.Parcelable;

import org.apache.test.number.Imaginary;
import org.apache.thrift.j2.TMessage;
import org.apache.thrift.j2.TMessageBuilder;
import org.apache.thrift.j2.TMessageBuilderFactory;
import org.apache.thrift.j2.TType;
import org.apache.thrift.j2.descriptor.TDescriptor;
import org.apache.thrift.j2.descriptor.TDescriptorProvider;
import org.apache.thrift.j2.descriptor.TField;
import org.apache.thrift.j2.descriptor.TPrimitive;
import org.apache.thrift.j2.descriptor.TUnionDescriptor;
import org.apache.thrift.j2.descriptor.TUnionDescriptorProvider;
import org.apache.thrift.j2.descriptor.TValueProvider;
import org.apache.thrift.j2.util.TTypeUtils;

public class Operand
        implements TMessage<Operand>, Serializable, Parcelable {
    private final static double kDefaultNumber = 0.0d;

    private final Operation mOperation;
    private final Double mNumber;
    private final Imaginary mImaginary;
    private final _Field tUnionField;


    private Operand(_Builder builder) {
        mOperation = builder.mOperation;
        mNumber = builder.mNumber;
        mImaginary = builder.mImaginary;

        tUnionField = builder.tUnionField;
    }

    public boolean hasOperation() {
        return mOperation != null;
    }

    public Operation getOperation() {
        return mOperation;
    }

    public boolean hasNumber() {
        return mNumber != null;
    }

    public double getNumber() {
        return hasNumber() ? mNumber : kDefaultNumber;
    }

    public boolean hasImaginary() {
        return mImaginary != null;
    }

    public Imaginary getImaginary() {
        return mImaginary;
    }

    public _Field unionField() {
        return tUnionField;
    }

    @Override
    public boolean has(int key) {
        switch(key) {
            case 1: return hasOperation();
            case 2: return hasNumber();
            case 3: return hasImaginary();
            default: return false;
        }
    }

    @Override
    public int num(int key) {
        switch(key) {
            case 1: return hasOperation() ? 1 : 0;
            case 2: return hasNumber() ? 1 : 0;
            case 3: return hasImaginary() ? 1 : 0;
            default: return 0;
        }
    }

    @Override
    public Object get(int key) {
        switch(key) {
            case 1: return getOperation();
            case 2: return getNumber();
            case 3: return getImaginary();
            default: return null;
        }
    }

    @Override
    public boolean isCompact() {
        return false;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || !(o instanceof Operand)) return false;
        Operand other = (Operand) o;
        return TTypeUtils.equals(mOperation, other.mOperation) &&
               TTypeUtils.equals(mNumber, other.mNumber) &&
               TTypeUtils.equals(mImaginary, other.mImaginary);
    }

    @Override
    public int hashCode() {
        return Operand.class.hashCode() +
               TTypeUtils.hashCode(mOperation) +
               TTypeUtils.hashCode(mNumber) +
               TTypeUtils.hashCode(mImaginary);
    }

    @Override
    public String toString() {
        return getDescriptor().getQualifiedName(null) + TTypeUtils.toString(this);
    }

    @Override
    public boolean isValid() {
        return (mOperation != null ? 1 : 0) +
               (mNumber != null ? 1 : 0) +
               (mImaginary != null ? 1 : 0) == 1;
    }

    public enum _Field implements TField {
        OPERATION(1, false, "operation", Operation.provider(), null),
        NUMBER(2, false, "number", TPrimitive.DOUBLE.provider(), null),
        IMAGINARY(3, false, "imaginary", Imaginary.provider(), null),
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
            builder.append(Operand.class.getSimpleName())
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
                case 1: return _Field.OPERATION;
                case 2: return _Field.NUMBER;
                case 3: return _Field.IMAGINARY;
                default: return null;
            }
        }

        public static _Field forName(String name) {
            switch (name) {
                case "operation": return _Field.OPERATION;
                case "number": return _Field.NUMBER;
                case "imaginary": return _Field.IMAGINARY;
            }
            return null;
        }
    }

    @Override
    public TUnionDescriptor<Operand> getDescriptor() {
        return sDescriptor;
    }

    public static TUnionDescriptor<Operand> descriptor() {
        return sDescriptor;
    }

    public static final TUnionDescriptor<Operand> sDescriptor;

    private final static class _Factory
            extends TMessageBuilderFactory<Operand> {
        @Override
        public _Builder builder() {
            return new _Builder();
        }
    }

    static {
        sDescriptor = new TUnionDescriptor<>(null, "calculator", "Operand", _Field.values(), new _Factory());
    }

    public static TUnionDescriptorProvider<Operand> provider() {
        return new TUnionDescriptorProvider<Operand>() {
            @Override
            public TUnionDescriptor<Operand> descriptor() {
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
        if (hasOperation()) {
            dest.writeInt(1);
            dest.writeParcelable(mOperation, 0);
        }
        if (hasNumber()) {
            dest.writeInt(2);
            dest.writeDouble(mNumber);
        }
        if (hasImaginary()) {
            dest.writeInt(3);
            dest.writeParcelable(mImaginary, 0);
        }
        dest.writeInt(0);
    }

    public static final Parcelable.Creator<Operand> CREATOR = new Parcelable.Creator<Operand>() {
        @Override
        public Operand createFromParcel(Parcel source) {
            _Builder builder = new _Builder();
            loop: while (source.dataAvail() > 0) {
                int field = source.readInt();
                switch (field) {
                    case 0: break loop;
                    case 1:
                        builder.setOperation((Operation) source.readParcelable(Operation.class.getClassLoader()));
                        break;
                    case 2:
                        builder.setNumber(source.readDouble());
                        break;
                    case 3:
                        builder.setImaginary((Imaginary) source.readParcelable(Imaginary.class.getClassLoader()));
                        break;
                    default: throw new IllegalArgumentException("Unknown field ID: " + field);
                }
            }

            return builder.build();
        }

        @Override
        public Operand[] newArray(int size) {
            return new Operand[size];
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
            extends TMessageBuilder<Operand> {
        private Operation mOperation;
        private Double mNumber;
        private Imaginary mImaginary;
        private _Field tUnionField;


        public _Builder() {
        }

        public _Builder(Operand base) {
            this();

            mOperation = base.mOperation;
            mNumber = base.mNumber;
            mImaginary = base.mImaginary;

            tUnionField = base.tUnionField;
        }

        public _Builder setOperation(Operation value) {
            tUnionField = _Field.OPERATION;
            mOperation = value;
            return this;
        }

        public _Builder clearOperation() {
            if (mOperation != null) tUnionField = null;
            mOperation = null;
            return this;
        }

        public _Builder setNumber(double value) {
            tUnionField = _Field.NUMBER;
            mNumber = value;
            return this;
        }

        public _Builder clearNumber() {
            if (mNumber != null) tUnionField = null;
            mNumber = null;
            return this;
        }

        public _Builder setImaginary(Imaginary value) {
            tUnionField = _Field.IMAGINARY;
            mImaginary = value;
            return this;
        }

        public _Builder clearImaginary() {
            if (mImaginary != null) tUnionField = null;
            mImaginary = null;
            return this;
        }

        @Override
        public _Builder set(int key, Object value) {
            switch (key) {
                case 1: setOperation((Operation) value); break;
                case 2: setNumber((double) value); break;
                case 3: setImaginary((Imaginary) value); break;
            }
            return this;
        }

        @Override
        public boolean isValid() {
            return (mOperation != null ? 1 : 0) +
                   (mNumber != null ? 1 : 0) +
                   (mImaginary != null ? 1 : 0) == 1;
        }

        @Override
        public Operand build() {
            return new Operand(this);
        }
    }
}
