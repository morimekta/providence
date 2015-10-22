package org.apache.thrift.j2.model;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.apache.thrift.j2.TMessage;
import org.apache.thrift.j2.TMessageBuilder;
import org.apache.thrift.j2.TMessageBuilderFactory;
import org.apache.thrift.j2.TType;
import org.apache.thrift.j2.descriptor.TDefaultValueProvider;
import org.apache.thrift.j2.descriptor.TDescriptor;
import org.apache.thrift.j2.descriptor.TDescriptorProvider;
import org.apache.thrift.j2.descriptor.TField;
import org.apache.thrift.j2.descriptor.TList;
import org.apache.thrift.j2.descriptor.TPrimitive;
import org.apache.thrift.j2.descriptor.TStructDescriptor;
import org.apache.thrift.j2.descriptor.TStructDescriptorProvider;
import org.apache.thrift.j2.descriptor.TValueProvider;
import org.apache.thrift.j2.util.TTypeUtils;

/** (oneway)? <return_type> <name>'('<param>*')' (throws '(' <exception>+ ')')? */
public class ServiceMethod
        implements TMessage<ServiceMethod>, Serializable {
    private final static boolean kDefaultIsOneway = false;

    private final String mComment;
    private final Boolean mIsOneway;
    private final String mReturnType;
    private final String mName;
    private final List<ThriftField> mParams;
    private final List<ThriftField> mExceptions;

    private ServiceMethod(_Builder builder) {
        mComment = builder.mComment;
        mIsOneway = builder.mIsOneway;
        mReturnType = builder.mReturnType;
        mName = builder.mName;
        mParams = Collections.unmodifiableList(new LinkedList<>(builder.mParams));
        mExceptions = Collections.unmodifiableList(new LinkedList<>(builder.mExceptions));
    }

    public boolean hasComment() {
        return mComment != null;
    }

    public String getComment() {
        return mComment;
    }

    public boolean hasIsOneway() {
        return mIsOneway != null;
    }

    public boolean getIsOneway() {
        return hasIsOneway() ? mIsOneway : kDefaultIsOneway;
    }

    public boolean hasReturnType() {
        return mReturnType != null;
    }

    public String getReturnType() {
        return mReturnType;
    }

    public boolean hasName() {
        return mName != null;
    }

    public String getName() {
        return mName;
    }

    public int numParams() {
        return mParams.size();
    }

    public List<ThriftField> getParams() {
        return mParams;
    }

    public int numExceptions() {
        return mExceptions.size();
    }

    public List<ThriftField> getExceptions() {
        return mExceptions;
    }

    @Override
    public boolean has(int key) {
        switch(key) {
            case 1: return hasComment();
            case 2: return hasIsOneway();
            case 3: return hasReturnType();
            case 4: return hasName();
            case 5: return numParams() > 0;
            case 6: return numExceptions() > 0;
            default: return false;
        }
    }

    @Override
    public int num(int key) {
        switch(key) {
            case 1: return hasComment() ? 1 : 0;
            case 2: return hasIsOneway() ? 1 : 0;
            case 3: return hasReturnType() ? 1 : 0;
            case 4: return hasName() ? 1 : 0;
            case 5: return numParams();
            case 6: return numExceptions();
            default: return 0;
        }
    }

    @Override
    public Object get(int key) {
        switch(key) {
            case 1: return getComment();
            case 2: return getIsOneway();
            case 3: return getReturnType();
            case 4: return getName();
            case 5: return getParams();
            case 6: return getExceptions();
            default: return null;
        }
    }

    @Override
    public boolean isCompact() {
        return false;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || !(o instanceof ServiceMethod)) return false;
        ServiceMethod other = (ServiceMethod) o;
        return TTypeUtils.equals(mComment, other.mComment) &&
               TTypeUtils.equals(mIsOneway, other.mIsOneway) &&
               TTypeUtils.equals(mReturnType, other.mReturnType) &&
               TTypeUtils.equals(mName, other.mName) &&
               TTypeUtils.equals(mParams, other.mParams) &&
               TTypeUtils.equals(mExceptions, other.mExceptions);
    }

    @Override
    public int hashCode() {
        return ServiceMethod.class.hashCode() +
               TTypeUtils.hashCode(mComment) +
               TTypeUtils.hashCode(mIsOneway) +
               TTypeUtils.hashCode(mReturnType) +
               TTypeUtils.hashCode(mName) +
               TTypeUtils.hashCode(mParams) +
               TTypeUtils.hashCode(mExceptions);
    }

    @Override
    public String toString() {
        return getDescriptor().getQualifiedName(null) + TTypeUtils.toString(this);
    }

    @Override
    public boolean isValid() {
        return mName != null;
    }

    public enum _Field implements TField {
        COMMENT(1, false, "comment", TPrimitive.STRING.provider(), null),
        IS_ONEWAY(2, false, "is_oneway", TPrimitive.BOOL.provider(), new TDefaultValueProvider<>(kDefaultIsOneway)),
        RETURN_TYPE(3, false, "return_type", TPrimitive.STRING.provider(), null),
        NAME(4, true, "name", TPrimitive.STRING.provider(), null),
        PARAMS(5, false, "params", TList.provider(ThriftField.provider()), null),
        EXCEPTIONS(6, false, "exceptions", TList.provider(ThriftField.provider()), null),
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
            builder.append(ServiceMethod.class.getSimpleName())
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
                case 1: return _Field.COMMENT;
                case 2: return _Field.IS_ONEWAY;
                case 3: return _Field.RETURN_TYPE;
                case 4: return _Field.NAME;
                case 5: return _Field.PARAMS;
                case 6: return _Field.EXCEPTIONS;
                default: return null;
            }
        }

        public static _Field forName(String name) {
            switch (name) {
                case "comment": return _Field.COMMENT;
                case "is_oneway": return _Field.IS_ONEWAY;
                case "return_type": return _Field.RETURN_TYPE;
                case "name": return _Field.NAME;
                case "params": return _Field.PARAMS;
                case "exceptions": return _Field.EXCEPTIONS;
            }
            return null;
        }
    }

    @Override
    public TStructDescriptor<ServiceMethod> getDescriptor() {
        return sDescriptor;
    }

    public static TStructDescriptor<ServiceMethod> descriptor() {
        return sDescriptor;
    }

    public static final TStructDescriptor<ServiceMethod> sDescriptor;

    private final static class _Factory
            extends TMessageBuilderFactory<ServiceMethod> {
        @Override
        public _Builder builder() {
            return new _Builder();
        }
    }

    static {
        sDescriptor = new TStructDescriptor<>(null, "model", "ServiceMethod", _Field.values(), new _Factory(), false);
    }

    public static TStructDescriptorProvider<ServiceMethod> provider() {
        return new TStructDescriptorProvider<ServiceMethod>() {
            @Override
            public TStructDescriptor<ServiceMethod> descriptor() {
                return sDescriptor;
            }
        };
    }

    @Override
    public _Builder mutate() {
        return new _Builder(this);
    }

    public static _Builder builder() {
        return new _Builder();
    }

    public static class _Builder
            extends TMessageBuilder<ServiceMethod> {
        private String mComment;
        private Boolean mIsOneway;
        private String mReturnType;
        private String mName;
        private List<ThriftField> mParams;
        private List<ThriftField> mExceptions;

        public _Builder() {
            mParams = new LinkedList<>();
            mExceptions = new LinkedList<>();
        }

        public _Builder(ServiceMethod base) {
            this();

            mComment = base.mComment;
            mIsOneway = base.mIsOneway;
            mReturnType = base.mReturnType;
            mName = base.mName;
            mParams.addAll(base.mParams);
            mExceptions.addAll(base.mExceptions);
        }

        public _Builder setComment(String value) {
            mComment = value;
            return this;
        }

        public _Builder clearComment() {
            mComment = null;
            return this;
        }

        public _Builder setIsOneway(boolean value) {
            mIsOneway = value;
            return this;
        }

        public _Builder clearIsOneway() {
            mIsOneway = null;
            return this;
        }

        public _Builder setReturnType(String value) {
            mReturnType = value;
            return this;
        }

        public _Builder clearReturnType() {
            mReturnType = null;
            return this;
        }

        public _Builder setName(String value) {
            mName = value;
            return this;
        }

        public _Builder clearName() {
            mName = null;
            return this;
        }

        public _Builder setParams(Collection<ThriftField> value) {
            mParams.clear();
            mParams.addAll(value);
            return this;
        }

        public _Builder addToParams(ThriftField... values) {
            for (ThriftField item : values) {
                mParams.add(item);
            }
            return this;
        }

        public _Builder clearParams() {
            mParams.clear();
            return this;
        }

        public _Builder setExceptions(Collection<ThriftField> value) {
            mExceptions.clear();
            mExceptions.addAll(value);
            return this;
        }

        public _Builder addToExceptions(ThriftField... values) {
            for (ThriftField item : values) {
                mExceptions.add(item);
            }
            return this;
        }

        public _Builder clearExceptions() {
            mExceptions.clear();
            return this;
        }

        @Override
        public _Builder set(int key, Object value) {
            switch (key) {
                case 1: setComment((String) value); break;
                case 2: setIsOneway((boolean) value); break;
                case 3: setReturnType((String) value); break;
                case 4: setName((String) value); break;
                case 5: setParams((List<ThriftField>) value); break;
                case 6: setExceptions((List<ThriftField>) value); break;
            }
            return this;
        }

        @Override
        public boolean isValid() {
            return mName != null;
        }

        @Override
        public ServiceMethod build() {
            return new ServiceMethod(this);
        }
    }
}
