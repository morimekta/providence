package org.apache.test.calculator;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;

import org.apache.thrift2.TMessage;
import org.apache.thrift2.TMessageBuilder;
import org.apache.thrift2.TMessageBuilderFactory;
import org.apache.thrift2.descriptor.TField;
import org.apache.thrift2.descriptor.TList;
import org.apache.thrift2.descriptor.TStructDescriptor;
import org.apache.thrift2.descriptor.TStructDescriptorProvider;
import org.apache.thrift2.util.TTypeUtils;

public class Operation
        implements TMessage<Operation>, Serializable, Parcelable {
    private final Operator mOperator;
    private final List<Operand> mOperands;

    private Operation(Builder builder) {
        mOperator = builder.mOperator;
        mOperands = Collections.unmodifiableList(new LinkedList<>(builder.mOperands));
    }

    public boolean hasOperator() {
        return mOperator != null;
    }

    public Operator getOperator() {
        return mOperator;
    }

    public int numOperands() {
        return mOperands.size();
    }

    public List<Operand> getOperands() {
        return mOperands;
    }

    @Override
    public boolean has(int key) {
        switch(key) {
            case 1: return hasOperator();
            case 2: return numOperands() > 0;
            default: return false;
        }
    }

    @Override
    public int num(int key) {
        switch(key) {
            case 1: return hasOperator() ? 1 : 0;
            case 2: return numOperands();
            default: return 0;
        }
    }

    @Override
    public Object get(int key) {
        switch(key) {
            case 1: return getOperator();
            case 2: return getOperands();
            default: return null;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || !(o instanceof Operation)) return false;
        Operation other = (Operation) o;
        return TTypeUtils.equals(mOperator, other.mOperator) &&
               TTypeUtils.equals(mOperands, other.mOperands);
    }

    @Override
    public int hashCode() {
        return Operation.class.hashCode() +
               TTypeUtils.hashCode(mOperator) +
               TTypeUtils.hashCode(mOperands);
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
    public TStructDescriptor<Operation> descriptor() {
        return DESCRIPTOR;
    }

    public static final TStructDescriptor<Operation> DESCRIPTOR = _createDescriptor();

    private final static class _Factory
            extends TMessageBuilderFactory<Operation> {
        @Override
        public Operation.Builder builder() {
            return new Operation.Builder();
        }
    }

    private static TStructDescriptor<Operation> _createDescriptor() {
        List<TField<?>> fieldList = new LinkedList<>();
        fieldList.add(new TField<>(null, 1, false, "operator", Operator.provider(), null));
        fieldList.add(new TField<>(null, 2, false, "operands", TList.provider(Operand.provider()), null));
        return new TStructDescriptor<>(null, "calculator", "Operation", fieldList, new _Factory());
    }

    public static TStructDescriptorProvider<Operation> provider() {
        return new TStructDescriptorProvider<Operation>() {
            @Override
            public TStructDescriptor<Operation> descriptor() {
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
        if (hasOperator()) {
            dest.writeInt(1);
            dest.writeInt(mOperator.getValue());
        }
        if (numOperands() > 0) {
            dest.writeInt(2);
            dest.writeParcelableArray(mOperands.toArray(new Operand[mOperands.size()]), mOperands.size());
        }
        dest.writeInt(0);
    }

    public static final Parcelable.Creator<Operation> CREATOR = new Parcelable.Creator<Operation>() {
        @Override
        public Operation createFromParcel(Parcel source) {
            Operation.Builder builder = new Operation.Builder();
            loop: while (source.dataAvail() > 0) {
                int field = source.readInt();
                switch (field) {
                    case 0: break loop;
                    case 1:
                        builder.setOperator(Operator.valueOf(source.readInt()));
                        break;
                    case 2:
                        builder.addToOperands((Operand[]) source.readParcelableArray(Operand.class.getClassLoader()));
                        break;
                    default: throw new IllegalArgumentException("Unknown field ID: " + field);
                }
            }

            return builder.build();
        }

        @Override
        public Operation[] newArray(int size) {
            return new Operation[size];
        }
    };

    @Override
    public Operation.Builder mutate() {
        return new Operation.Builder(this);
    }

    public static Operation.Builder builder() {
        return new Operation.Builder();
    }

    public static class Builder
            extends TMessageBuilder<Operation> {
        private Operator mOperator;
        private List<Operand> mOperands;

        public Builder() {
            mOperands = new LinkedList<>();
        }

        public Builder(Operation base) {
            this();

            mOperator = base.mOperator;
            mOperands.addAll(base.mOperands);
        }

        public Builder setOperator(Operator value) {
            mOperator = value;
            return this;
        }

        public Builder clearOperator() {
            mOperator = null;
            return this;
        }

        public Builder setOperands(Collection<Operand> value) {
            mOperands.clear();
            mOperands.addAll(value);
            return this;
        }

        public Builder addToOperands(Operand... values) {
            for (Operand item : values) {
                mOperands.add(item);
            }
            return this;
        }

        public Builder clearOperands() {
            mOperands.clear();
            return this;
        }

        @Override
        public Builder set(int key, Object value) {
            switch (key) {
                case 1: setOperator((Operator) value); break;
                case 2: setOperands((List<Operand>) value); break;
            }
            return this;
        }

        @Override
        public boolean isValid() {
            return true;
        }

        @Override
        public Operation build() {
            return new Operation(this);
        }
    }
}
