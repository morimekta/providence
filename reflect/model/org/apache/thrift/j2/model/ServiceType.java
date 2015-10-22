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
import org.apache.thrift.j2.descriptor.TDescriptor;
import org.apache.thrift.j2.descriptor.TDescriptorProvider;
import org.apache.thrift.j2.descriptor.TField;
import org.apache.thrift.j2.descriptor.TList;
import org.apache.thrift.j2.descriptor.TPrimitive;
import org.apache.thrift.j2.descriptor.TStructDescriptor;
import org.apache.thrift.j2.descriptor.TStructDescriptorProvider;
import org.apache.thrift.j2.descriptor.TValueProvider;
import org.apache.thrift.j2.util.TTypeUtils;

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
    public boolean isCompact() {
        return false;
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
        return getDescriptor().getQualifiedName(null) + TTypeUtils.toString(this);
    }

    @Override
    public boolean isValid() {
        return mName != null;
    }

    public enum Field implements TField {
        COMMENT(1, false, "comment", TPrimitive.STRING.provider(), null),
        NAME(2, true, "name", TPrimitive.STRING.provider(), null),
        EXTEND(3, false, "extend", TPrimitive.STRING.provider(), null),
        METHODS(4, false, "methods", TList.provider(ServiceMethod.provider()), null),
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
            builder.append(ServiceType.class.getSimpleName())
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
    public TStructDescriptor<ServiceType> getDescriptor() {
        return sDescriptor;
    }

    public static TStructDescriptor<ServiceType> descriptor() {
        return sDescriptor;
    }

    public static final TStructDescriptor<ServiceType> sDescriptor;

    private final static class Factory
            extends TMessageBuilderFactory<ServiceType> {
        @Override
        public ServiceType.Builder builder() {
            return new ServiceType.Builder();
        }
    }

    static {
        sDescriptor = new TStructDescriptor<>(null, "model", "ServiceType", ServiceType.Field.values(), new Factory(), false);
    }

    public static TStructDescriptorProvider<ServiceType> provider() {
        return new TStructDescriptorProvider<ServiceType>() {
            @Override
            public TStructDescriptor<ServiceType> descriptor() {
                return sDescriptor;
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
