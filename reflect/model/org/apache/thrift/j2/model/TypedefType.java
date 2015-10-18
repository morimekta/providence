package org.apache.thrift.j2.model;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import org.apache.thrift.j2.TMessage;
import org.apache.thrift.j2.TMessageBuilder;
import org.apache.thrift.j2.TMessageBuilderFactory;
import org.apache.thrift.j2.descriptor.TField;
import org.apache.thrift.j2.descriptor.TPrimitive;
import org.apache.thrift.j2.descriptor.TStructDescriptor;
import org.apache.thrift.j2.descriptor.TStructDescriptorProvider;
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

    @Override
    public TStructDescriptor<TypedefType> descriptor() {
        return DESCRIPTOR;
    }

    public static final TStructDescriptor<TypedefType> DESCRIPTOR = _createDescriptor();

    private final static class _Factory
            extends TMessageBuilderFactory<TypedefType> {
        @Override
        public TypedefType.Builder builder() {
            return new TypedefType.Builder();
        }
    }

    private static TStructDescriptor<TypedefType> _createDescriptor() {
        List<TField<?>> fieldList = new LinkedList<>();
        fieldList.add(new TField<>(null, 1, false, "type", TPrimitive.STRING.provider(), null));
        fieldList.add(new TField<>(null, 2, false, "name", TPrimitive.STRING.provider(), null));
        return new TStructDescriptor<>(null, "model", "TypedefType", fieldList, new _Factory(), false);
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
