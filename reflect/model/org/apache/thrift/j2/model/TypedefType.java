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
    private final String mComment;
    private final String mType;
    private final String mName;

    private TypedefType(_Builder builder) {
        mComment = builder.mComment;
        mType = builder.mType;
        mName = builder.mName;
    }

    public boolean hasComment() {
        return mComment != null;
    }

    public String getComment() {
        return mComment;
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
            case 1: return hasComment();
            case 2: return hasType();
            case 3: return hasName();
            default: return false;
        }
    }

    @Override
    public int num(int key) {
        switch(key) {
            case 1: return hasComment() ? 1 : 0;
            case 2: return hasType() ? 1 : 0;
            case 3: return hasName() ? 1 : 0;
            default: return 0;
        }
    }

    @Override
    public Object get(int key) {
        switch(key) {
            case 1: return getComment();
            case 2: return getType();
            case 3: return getName();
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
        return TTypeUtils.equals(mComment, other.mComment) &&
               TTypeUtils.equals(mType, other.mType) &&
               TTypeUtils.equals(mName, other.mName);
    }

    @Override
    public int hashCode() {
        return TypedefType.class.hashCode() +
               TTypeUtils.hashCode(_Field.COMMENT,mComment) +
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
        COMMENT(1, false, "comment", TPrimitive.STRING.provider(), null),
        TYPE(2, false, "type", TPrimitive.STRING.provider(), null),
        NAME(3, false, "name", TPrimitive.STRING.provider(), null),
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
                case 1: return _Field.COMMENT;
                case 2: return _Field.TYPE;
                case 3: return _Field.NAME;
                default: return null;
            }
        }

        public static _Field forName(String name) {
            switch (name) {
                case "comment": return _Field.COMMENT;
                case "type": return _Field.TYPE;
                case "name": return _Field.NAME;
            }
            return null;
        }
    }

    public static TStructDescriptorProvider<TypedefType,_Field> provider() {
        return new _Provider();
    }

    @Override
    public TStructDescriptor<TypedefType,_Field> descriptor() {
        return kDescriptor;
    }

    public static final TStructDescriptor<TypedefType,_Field> kDescriptor;

    private static class _Descriptor
            extends TStructDescriptor<TypedefType,_Field> {
        public _Descriptor() {
            super(null, "model", "TypedefType", new _Factory(), true, false);
        }

        @Override
        public _Field[] getFields() {
            return _Field.values();
        }

        @Override
        public _Field getField(String name) {
            return _Field.forName(name);
        }

        @Override
        public _Field getField(int key) {
            return _Field.forKey(key);
        }
    }

    static {
        kDescriptor = new _Descriptor();
    }

    private final static class _Provider extends TStructDescriptorProvider<TypedefType,_Field> {
        @Override
        public TStructDescriptor<TypedefType,_Field> descriptor() {
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
        private String mComment;
        private String mType;
        private String mName;

        public _Builder() {
        }

        public _Builder(TypedefType base) {
            this();

            mComment = base.mComment;
            mType = base.mType;
            mName = base.mName;
        }

        public _Builder setComment(String value) {
            mComment = value;
            return this;
        }

        public _Builder clearComment() {
            mComment = null;
            return this;
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
                case 1: setComment((String) value); break;
                case 2: setType((String) value); break;
                case 3: setName((String) value); break;
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
