package org.apache.thrift.j2.model;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import org.apache.thrift.j2.TMessage;
import org.apache.thrift.j2.TMessageBuilder;
import org.apache.thrift.j2.TMessageBuilderFactory;
import org.apache.thrift.j2.TType;
import org.apache.thrift.j2.descriptor.TDescriptor;
import org.apache.thrift.j2.descriptor.TDescriptorProvider;
import org.apache.thrift.j2.descriptor.TField;
import org.apache.thrift.j2.descriptor.TPrimitive;
import org.apache.thrift.j2.descriptor.TStructDescriptor;
import org.apache.thrift.j2.descriptor.TStructDescriptorProvider;
import org.apache.thrift.j2.descriptor.TValueProvider;
import org.apache.thrift.j2.util.TTypeUtils;

/** typedef <type> <name> */
public class TypedefType
        implements TMessage<TypedefType>, Serializable {
    private final String mType;
    private final String mName;

    private TypedefType(Builder builder) {
        mType = builder.mType;
        mName = builder.mName;
    }

    public boolean hasType() {
        return mType != null;
    }

    public String getType() {
        return mType;
    }

    public boolean hasName() {
        return mName != null;
    }

    public String getName() {
        return mName;
    }

    @Override
    public boolean has(int key) {
        switch(key) {
            case 1: return hasType();
            case 2: return hasName();
            default: return false;
        }
    }

    @Override
    public int num(int key) {
        switch(key) {
            case 1: return hasType() ? 1 : 0;
            case 2: return hasName() ? 1 : 0;
            default: return 0;
        }
    }

    @Override
    public Object get(int key) {
        switch(key) {
            case 1: return getType();
            case 2: return getName();
            default: return null;
        }
    }

    @Override
    public boolean compact() {
        return false;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || !(o instanceof TypedefType)) return false;
        TypedefType other = (TypedefType) o;
        return TTypeUtils.equals(mType, other.mType) &&
               TTypeUtils.equals(mName, other.mName);
    }

    @Override
    public int hashCode() {
        return TypedefType.class.hashCode() +
               TTypeUtils.hashCode(mType) +
               TTypeUtils.hashCode(mName);
    }

    @Override
    public String toString() {
        return descriptor().getQualifiedName(null) + TTypeUtils.toString(this);
    }

    @Override
    public boolean isValid() {
        return true;
    }

    public enum Field implements TField {
        TYPE(1, false, "type", TPrimitive.STRING.provider(), null),
        NAME(2, false, "name", TPrimitive.STRING.provider(), null),
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
        public TDescriptor<?> descriptor() { return mTypeProvider.descriptor(); }

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
            builder.append(TypedefType.class.getSimpleName())
                   .append('{')
                   .append(mKey)
                   .append(": ");
            if (mRequired) {
                builder.append("required ");
            }
            builder.append(descriptor().getQualifiedName(null))
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
    public TStructDescriptor<TypedefType> descriptor() {
        return DESCRIPTOR;
    }

    public static final TStructDescriptor<TypedefType> DESCRIPTOR;

    private final static class Factory
            extends TMessageBuilderFactory<TypedefType> {
        @Override
        public TypedefType.Builder builder() {
            return new TypedefType.Builder();
        }
    }

    static {
        DESCRIPTOR = new TStructDescriptor<>(null, "model", "TypedefType", TypedefType.Field.values(), new Factory(), false);
    }

    public static TStructDescriptorProvider<TypedefType> provider() {
        return new TStructDescriptorProvider<TypedefType>() {
            @Override
            public TStructDescriptor<TypedefType> descriptor() {
                return DESCRIPTOR;
            }
        };
    }

    @Override
    public TypedefType.Builder mutate() {
        return new TypedefType.Builder(this);
    }

    public static TypedefType.Builder builder() {
        return new TypedefType.Builder();
    }

    public static class Builder
            extends TMessageBuilder<TypedefType> {
        private String mType;
        private String mName;

        public Builder() {
        }

        public Builder(TypedefType base) {
            this();

            mType = base.mType;
            mName = base.mName;
        }

        public Builder setType(String value) {
            mType = value;
            return this;
        }

        public Builder clearType() {
            mType = null;
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

        @Override
        public Builder set(int key, Object value) {
            switch (key) {
                case 1: setType((String) value); break;
                case 2: setName((String) value); break;
            }
            return this;
        }

        @Override
        public boolean isValid() {
            return true;
        }

        @Override
        public TypedefType build() {
            return new TypedefType(this);
        }
    }
}
