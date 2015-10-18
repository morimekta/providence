package org.apache.test.calculator;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;

import org.apache.thrift2.TException;
import org.apache.thrift2.TMessage;
import org.apache.thrift2.TMessageBuilder;
import org.apache.thrift2.TMessageBuilderFactory;
import org.apache.thrift2.descriptor.TExceptionDescriptor;
import org.apache.thrift2.descriptor.TExceptionDescriptorProvider;
import org.apache.thrift2.descriptor.TField;
import org.apache.thrift2.descriptor.TPrimitive;
import org.apache.thrift2.util.TTypeUtils;

public class CalculateException
        extends TException
        implements TMessage<CalculateException>, Serializable, Parcelable {
    private final static long serialVersionUID = -3144631929815376595L;

    private final String mMessage;
    private final Operation mOperation;

    private CalculateException(Builder builder) {
        super(builder.createMessage());

        mMessage = builder.mMessage;
        mOperation = builder.mOperation;
    }

    public boolean hasMessage() {
        return mMessage != null;
    }

    public String getMessage() {
        return mMessage;
    }

    public boolean hasOperation() {
        return mOperation != null;
    }

    public Operation getOperation() {
        return mOperation;
    }

    @Override
    public boolean has(int key) {
        switch(key) {
            case 1: return hasMessage();
            case 2: return hasOperation();
            default: return false;
        }
    }

    @Override
    public int num(int key) {
        switch(key) {
            case 1: return hasMessage() ? 1 : 0;
            case 2: return hasOperation() ? 1 : 0;
            default: return 0;
        }
    }

    @Override
    public Object get(int key) {
        switch(key) {
            case 1: return getMessage();
            case 2: return getOperation();
            default: return null;
        }
    }

    @Override
    public boolean compact() {
        return false;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || !(o instanceof CalculateException)) return false;
        CalculateException other = (CalculateException) o;
        return TTypeUtils.equals(mMessage, other.mMessage) &&
               TTypeUtils.equals(mOperation, other.mOperation);
    }

    @Override
    public int hashCode() {
        return CalculateException.class.hashCode() +
               TTypeUtils.hashCode(mMessage) +
               TTypeUtils.hashCode(mOperation);
    }

    @Override
    public String toString() {
        return descriptor().getQualifiedName(null) + TTypeUtils.toString(this);
    }

    @Override
    public boolean isValid() {
        return mMessage != null;
    }

    @Override
    public TExceptionDescriptor<CalculateException> descriptor() {
        return DESCRIPTOR;
    }

    public static final TExceptionDescriptor<CalculateException> DESCRIPTOR = _createDescriptor();

    private final static class _Factory
            extends TMessageBuilderFactory<CalculateException> {
        @Override
        public CalculateException.Builder builder() {
            return new CalculateException.Builder();
        }
    }

    private static TExceptionDescriptor<CalculateException> _createDescriptor() {
        List<TField<?>> fieldList = new LinkedList<>();
        fieldList.add(new TField<>(null, 1, true, "message", TPrimitive.STRING.provider(), null));
        fieldList.add(new TField<>(null, 2, false, "operation", Operation.provider(), null));
        return new TExceptionDescriptor<>(null, "calculator", "CalculateException", fieldList, new _Factory());
    }

    public static TExceptionDescriptorProvider<CalculateException> provider() {
        return new TExceptionDescriptorProvider<CalculateException>() {
            @Override
            public TExceptionDescriptor<CalculateException> descriptor() {
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
        if (hasMessage()) {
            dest.writeInt(1);
            dest.writeString(mMessage);
        }
        if (hasOperation()) {
            dest.writeInt(2);
            dest.writeParcelable(mOperation, 0);
        }
        dest.writeInt(0);
    }

    public static final Parcelable.Creator<CalculateException> CREATOR = new Parcelable.Creator<CalculateException>() {
        @Override
        public CalculateException createFromParcel(Parcel source) {
            CalculateException.Builder builder = new CalculateException.Builder();
            loop: while (source.dataAvail() > 0) {
                int field = source.readInt();
                switch (field) {
                    case 0: break loop;
                    case 1:
                        builder.setMessage(source.readString());
                        break;
                    case 2:
                        builder.setOperation((Operation) source.readParcelable(Operation.class.getClassLoader()));
                        break;
                    default: throw new IllegalArgumentException("Unknown field ID: " + field);
                }
            }

            return builder.build();
        }

        @Override
        public CalculateException[] newArray(int size) {
            return new CalculateException[size];
        }
    };

    @Override
    public CalculateException.Builder mutate() {
        return new CalculateException.Builder(this);
    }

    public static CalculateException.Builder builder() {
        return new CalculateException.Builder();
    }

    public static class Builder
            extends TMessageBuilder<CalculateException> {
        private String mMessage;
        private Operation mOperation;

        public Builder() {
        }

        public Builder(CalculateException base) {
            this();

            mMessage = base.mMessage;
            mOperation = base.mOperation;
        }

        public Builder setMessage(String value) {
            mMessage = value;
            return this;
        }

        public Builder clearMessage() {
            mMessage = null;
            return this;
        }

        public Builder setOperation(Operation value) {
            mOperation = value;
            return this;
        }

        public Builder clearOperation() {
            mOperation = null;
            return this;
        }

        @Override
        public Builder set(int key, Object value) {
            switch (key) {
                case 1: setMessage((String) value); break;
                case 2: setOperation((Operation) value); break;
            }
            return this;
        }

        @Override
        public boolean isValid() {
            return mMessage != null;
        }

        protected String createMessage() {
            StringBuilder builder = new StringBuilder();
            builder.append('{');
            boolean first = true;
            if (mMessage != null) {
                if (first) first = false;
                else builder.append(',');
                builder.append("message:");
                builder.append(mMessage);
            }
            if (mOperation != null) {
                if (first) first = false;
                else builder.append(',');
                builder.append("operation:");
                builder.append(TTypeUtils.toString(mOperation));
            }
            builder.append('}');
            return builder.toString();
        }

        @Override
        public CalculateException build() {
            return new CalculateException(this);
        }
    }
}
