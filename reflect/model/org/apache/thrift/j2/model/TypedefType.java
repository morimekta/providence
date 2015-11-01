package org.apache.thrift.j2.model;

import java.io.Serializable;

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
@SuppressWarnings("unused")
public class TypedefType
        implements TMessage<TypedefType>, Serializable {
    private final String mType;
    private final String mName;

    private TypedefType(_Builder builder) {
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
    public boolean isCompact() {
        return false;
    }

    @Override
    public boolean isSimple() {
        return descriptor().isSimple();
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
               TTypeUtils.hashCode(_Field.TYPE,mType) +
               TTypeUtils.hashCode(_Field.NAME,mName);
    }

    @Override
    public String toString() {
        return descriptor().getQualifiedName(null) + TTypeUtils.toString(this);
    }

    @Override
    public boolean isValid() {
        return true;
    }

    public enum _Field implements TField {
        TYPE(1, false, "type", TPrimitive.STRING.provider(), null),
        NAME(2, false, "name", TPrimitive.STRING.provider(), null),
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
        public TType getType() { return getDescriptor().getType(); }

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
            builder.append(TypedefType.class.getSimpleName())
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
                case 1: return _Field.TYPE;
                case 2: return _Field.NAME;
                default: return null;
            }
        }

        public static _Field forName(String name) {
            switch (name) {
                case "type": return _Field.TYPE;
                case "name": return _Field.NAME;
            }
            return null;
        }
    }

    public static TStructDescriptorProvider<TypedefType> provider() {
        return new _Provider();
    }

    @Override
    public TStructDescriptor<TypedefType> descriptor() {
        return kDescriptor;
    }

    public static final TStructDescriptor<TypedefType> kDescriptor;

    static {
        kDescriptor = new TStructDescriptor<>(null, "model", "TypedefType", _Field.values(), new _Factory(), true, false);
    }

    private final static class _Provider extends TStructDescriptorProvider<TypedefType> {
        @Override
        public TStructDescriptor<TypedefType> descriptor() {
            return kDescriptor;
        }
    }

    private final static class _Factory
            extends TMessageBuilderFactory<TypedefType> {
        @Override
        public _Builder builder() {
            return new _Builder();
        }
    }

    @Override
    public _Builder mutate() {
        return new _Builder(this);
    }

    public static _Builder builder() {
        return new _Builder();
    }

    public static class _Builder
            extends TMessageBuilder<TypedefType> {
        private String mType;
        private String mName;

        public _Builder() {
        }

        public _Builder(TypedefType base) {
            this();

            mType = base.mType;
            mName = base.mName;
        }

        public _Builder setType(String value) {
            mType = value;
            return this;
        }

        public _Builder clearType() {
            mType = null;
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

        @Override
        public _Builder set(int key, Object value) {
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
