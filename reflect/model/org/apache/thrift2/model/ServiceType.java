package org.apache.thrift2.model;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.apache.thrift2.TMessage;
import org.apache.thrift2.TMessageBuilder;
import org.apache.thrift2.TMessageBuilderFactory;
import org.apache.thrift2.descriptor.TField;
import org.apache.thrift2.descriptor.TList;
import org.apache.thrift2.descriptor.TPrimitive;
import org.apache.thrift2.descriptor.TStructDescriptor;
import org.apache.thrift2.descriptor.TStructDescriptorProvider;
import org.apache.thrift2.util.TTypeUtils;

/**
 * service (extends <extend>)? {
 *   (<method> [;,]?)*
 * }
 */
public class ServiceType
        implements TMessage<ServiceType>, Serializable {
    private final String mComment;
    private final String mName;
    private final String mExtend;
    private final List<ServiceMethod> mMethods;

    private ServiceType(Builder builder) {
        mComment = builder.mComment;
        mName = builder.mName;
        mExtend = builder.mExtend;
        mMethods = Collections.unmodifiableList(new LinkedList<>(builder.mMethods));
    }

    public boolean hasComment() {
        return mComment != null;
    }

    public String getComment() {
        return mComment;
    }

    public boolean hasName() {
        return mName != null;
    }

    public String getName() {
        return mName;
    }

    public boolean hasExtend() {
        return mExtend != null;
    }

    public String getExtend() {
        return mExtend;
    }

    public int numMethods() {
        return mMethods.size();
    }

    public List<ServiceMethod> getMethods() {
        return mMethods;
    }

    @Override
    public boolean has(int key) {
        switch(key) {
            case 1: return hasComment();
            case 2: return hasName();
            case 3: return hasExtend();
            case 4: return numMethods() > 0;
            default: return false;
        }
    }

    @Override
    public int num(int key) {
        switch(key) {
            case 1: return hasComment() ? 1 : 0;
            case 2: return hasName() ? 1 : 0;
            case 3: return hasExtend() ? 1 : 0;
            case 4: return numMethods();
            default: return 0;
        }
    }

    @Override
    public Object get(int key) {
        switch(key) {
            case 1: return getComment();
            case 2: return getName();
            case 3: return getExtend();
            case 4: return getMethods();
            default: return null;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || !(o instanceof ServiceType)) return false;
        ServiceType other = (ServiceType) o;
        return TTypeUtils.equals(mComment, other.mComment) &&
               TTypeUtils.equals(mName, other.mName) &&
               TTypeUtils.equals(mExtend, other.mExtend) &&
               TTypeUtils.equals(mMethods, other.mMethods);
    }

    @Override
    public int hashCode() {
        return ServiceType.class.hashCode() +
               TTypeUtils.hashCode(mComment) +
               TTypeUtils.hashCode(mName) +
               TTypeUtils.hashCode(mExtend) +
               TTypeUtils.hashCode(mMethods);
    }

    @Override
    public String toString() {
        return descriptor().getQualifiedName(null) + TTypeUtils.toString(this);
    }

    @Override
    public boolean isValid() {
        return mName != null;
    }

    @Override
    public TStructDescriptor<ServiceType> descriptor() {
        return DESCRIPTOR;
    }

    public static final TStructDescriptor<ServiceType> DESCRIPTOR = _createDescriptor();

    private final static class _Factory
            extends TMessageBuilderFactory<ServiceType> {
        @Override
        public ServiceType.Builder builder() {
            return new ServiceType.Builder();
        }
    }

    private static TStructDescriptor<ServiceType> _createDescriptor() {
        List<TField<?>> fieldList = new LinkedList<>();
        fieldList.add(new TField<>(null, 1, false, "comment", TPrimitive.STRING.provider(), null));
        fieldList.add(new TField<>(null, 2, true, "name", TPrimitive.STRING.provider(), null));
        fieldList.add(new TField<>(null, 3, false, "extend", TPrimitive.STRING.provider(), null));
        fieldList.add(new TField<>(null, 4, false, "methods", TList.provider(ServiceMethod.provider()), null));
        return new TStructDescriptor<>(null, "model", "ServiceType", fieldList, new _Factory());
    }

    public static TStructDescriptorProvider<ServiceType> provider() {
        return new TStructDescriptorProvider<ServiceType>() {
            @Override
            public TStructDescriptor<ServiceType> descriptor() {
                return DESCRIPTOR;
            }
        };
    }

    @Override
    public ServiceType.Builder mutate() {
        return new ServiceType.Builder(this);
    }

    public static ServiceType.Builder builder() {
        return new ServiceType.Builder();
    }

    public static class Builder
            extends TMessageBuilder<ServiceType> {
        private String mComment;
        private String mName;
        private String mExtend;
        private List<ServiceMethod> mMethods;

        public Builder() {
            mMethods = new LinkedList<>();
        }

        public Builder(ServiceType base) {
            this();

            mComment = base.mComment;
            mName = base.mName;
            mExtend = base.mExtend;
            mMethods.addAll(base.mMethods);
        }

        public Builder setComment(String value) {
            mComment = value;
            return this;
        }

        public Builder clearComment() {
            mComment = null;
            return this;
        }

        public Builder setName(String value) {
            mName = value;
            return this;
        }

        public Builder clearName() {
            mName = null;
            return this;
        }

        public Builder setExtend(String value) {
            mExtend = value;
            return this;
        }

        public Builder clearExtend() {
            mExtend = null;
            return this;
        }

        public Builder setMethods(Collection<ServiceMethod> value) {
            mMethods.clear();
            mMethods.addAll(value);
            return this;
        }

        public Builder addToMethods(ServiceMethod... values) {
            for (ServiceMethod item : values) {
                mMethods.add(item);
            }
            return this;
        }

        public Builder clearMethods() {
            mMethods.clear();
            return this;
        }

        @Override
        public Builder set(int key, Object value) {
            switch (key) {
                case 1: setComment((String) value); break;
                case 2: setName((String) value); break;
                case 3: setExtend((String) value); break;
                case 4: setMethods((List<ServiceMethod>) value); break;
            }
            return this;
        }

        @Override
        public boolean isValid() {
            return mName != null;
        }

        @Override
        public ServiceType build() {
            return new ServiceType(this);
        }
    }
}
