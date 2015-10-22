package org.apache.test.calculator;

import java.io.Serializable;

import android.os.Parcel;
import android.os.Parcelable;

import org.apache.thrift.j2.TException;
import org.apache.thrift.j2.TMessage;
import org.apache.thrift.j2.TMessageBuilder;
import org.apache.thrift.j2.TMessageBuilderFactory;
import org.apache.thrift.j2.TType;
import org.apache.thrift.j2.descriptor.TDescriptor;
import org.apache.thrift.j2.descriptor.TDescriptorProvider;
import org.apache.thrift.j2.descriptor.TExceptionDescriptor;
import org.apache.thrift.j2.descriptor.TExceptionDescriptorProvider;
import org.apache.thrift.j2.descriptor.TField;
import org.apache.thrift.j2.descriptor.TPrimitive;
import org.apache.thrift.j2.descriptor.TValueProvider;
import org.apache.thrift.j2.util.TTypeUtils;

public class CalculateException
        extends TException
        implements TMessage<CalculateException>, Serializable, Parcelable {
    private final static long serialVersionUID = -3144631929815376595L;

    private final String mMessage;
    private final Operation mOperation;

    private CalculateException(_Builder builder) {
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
    public boolean isCompact() {
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
        return getDescriptor().getQualifiedName(null) + TTypeUtils.toString(this);
    }

    @Override
    public boolean isValid() {
        return mMessage != null;
    }

    public enum _Field implements TField {
        MESSAGE(1, true, "message", TPrimitive.STRING.provider(), null),
        OPERATION(2, false, "operation", Operation.provider(), null),
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
            builder.append(CalculateException.class.getSimpleName())
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
                case 1: return _Field.MESSAGE;
                case 2: return _Field.OPERATION;
                default: return null;
            }
        }

        public static _Field forName(String name) {
            switch (name) {
                case "message": return _Field.MESSAGE;
                case "operation": return _Field.OPERATION;
            }
            return null;
        }
    }

    @Override
    public TExceptionDescriptor<CalculateException> getDescriptor() {
        return sDescriptor;
    }

    public static TExceptionDescriptor<CalculateException> descriptor() {
        return sDescriptor;
    }

    public static final TExceptionDescriptor<CalculateException> sDescriptor;

    private final static class _Factory
            extends TMessageBuilderFactory<CalculateException> {
        @Override
        public _Builder builder() {
            return new _Builder();
        }
    }

    static {
        sDescriptor = new TExceptionDescriptor<>(null, "calculator", "CalculateException", _Field.values(), new _Factory());
    }

    public static TExceptionDescriptorProvider<CalculateException> provider() {
        return new TExceptionDescriptorProvider<CalculateException>() {
            @Override
            public TExceptionDescriptor<CalculateException> descriptor() {
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
            _Builder builder = new _Builder();
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
    public _Builder mutate() {
        return new _Builder(this);
    }

    public static _Builder builder() {
        return new _Builder();
    }

    public static class _Builder
            extends TMessageBuilder<CalculateException> {
        private String mMessage;
        private Operation mOperation;

        public _Builder() {
        }

        public _Builder(CalculateException base) {
            this();

            mMessage = base.mMessage;
            mOperation = base.mOperation;
        }

        public _Builder setMessage(String value) {
            mMessage = value;
            return this;
        }

        public _Builder clearMessage() {
            mMessage = null;
            return this;
        }

        public _Builder setOperation(Operation value) {
            mOperation = value;
            return this;
        }

        public _Builder clearOperation() {
            mOperation = null;
            return this;
        }

        @Override
        public _Builder set(int key, Object value) {
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
