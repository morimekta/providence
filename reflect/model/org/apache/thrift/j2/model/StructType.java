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

/**
 * <variant> {
 *   (<field> ([,;])?)*
 * }
 */
@SuppressWarnings("unused")
public class StructType
        implements TMessage<StructType>, Serializable {
    private final static StructVariant kDefaultVariant = StructVariant.STRUCT;

    private final String mComment;
    private final StructVariant mVariant;
    private final String mName;
    private final List<ThriftField> mFields;

    private StructType(_Builder builder) {
        mComment = builder.mComment;
        mVariant = builder.mVariant;
        mName = builder.mName;
        mFields = Collections.unmodifiableList(new LinkedList<>(builder.mFields));
    }

    public boolean hasComment() {
        return mComment != null;
    }

    public String getComment() {
        return mComment;
    }

    public boolean hasVariant() {
        return mVariant != null;
    }

    public StructVariant getVariant() {
        return hasVariant() ? mVariant : kDefaultVariant;
    }

    public boolean hasName() {
        return mName != null;
    }

    public String getName() {
        return mName;
    }

    public int numFields() {
        return mFields.size();
    }

    public List<ThriftField> getFields() {
        return mFields;
    }

    @Override
    public boolean has(int key) {
        switch(key) {
            case 1: return hasComment();
            case 2: return hasVariant();
            case 3: return hasName();
            case 4: return numFields() > 0;
            default: return false;
        }
    }

    @Override
    public int num(int key) {
        switch(key) {
            case 1: return hasComment() ? 1 : 0;
            case 2: return hasVariant() ? 1 : 0;
            case 3: return hasName() ? 1 : 0;
            case 4: return numFields();
            default: return 0;
        }
    }

    @Override
    public Object get(int key) {
        switch(key) {
            case 1: return getComment();
            case 2: return getVariant();
            case 3: return getName();
            case 4: return getFields();
            default: return null;
        }
    }

    @Override
    public boolean isCompact() {
        return false;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || !(o instanceof StructType)) return false;
        StructType other = (StructType) o;
        return TTypeUtils.equals(mComment, other.mComment) &&
               TTypeUtils.equals(mVariant, other.mVariant) &&
               TTypeUtils.equals(mName, other.mName) &&
               TTypeUtils.equals(mFields, other.mFields);
    }

    @Override
    public int hashCode() {
        return StructType.class.hashCode() +
               TTypeUtils.hashCode(_Field.COMMENT,mComment) +
               TTypeUtils.hashCode(_Field.VARIANT,mVariant) +
               TTypeUtils.hashCode(_Field.NAME,mName) +
               TTypeUtils.hashCode(_Field.FIELDS,mFields);
    }

    @Override
    public String toString() {
        return descriptor().getQualifiedName(null) + TTypeUtils.toString(this);
    }

    @Override
    public boolean isValid() {
        return mName != null;
    }

    public enum _Field implements TField {
        COMMENT(1, false, "comment", TPrimitive.STRING.provider(), null),
        VARIANT(2, false, "variant", StructVariant.provider(), new TDefaultValueProvider<>(kDefaultVariant)),
        NAME(3, true, "name", TPrimitive.STRING.provider(), null),
        FIELDS(4, false, "fields", TList.provider(ThriftField.provider()), null),
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
            builder.append(StructType.class.getSimpleName())
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
                case 2: return _Field.VARIANT;
                case 3: return _Field.NAME;
                case 4: return _Field.FIELDS;
                default: return null;
            }
        }

        public static _Field forName(String name) {
            switch (name) {
                case "comment": return _Field.COMMENT;
                case "variant": return _Field.VARIANT;
                case "name": return _Field.NAME;
                case "fields": return _Field.FIELDS;
            }
            return null;
        }
    }

    public static TStructDescriptorProvider<StructType> provider() {
        return new _Provider();
    }

    @Override
    public TStructDescriptor<StructType> descriptor() {
        return kDescriptor;
    }

    public static final TStructDescriptor<StructType> kDescriptor;

    static {
        kDescriptor = new TStructDescriptor<>(null, "model", "StructType", _Field.values(), new _Factory(), false);
    }

    private final static class _Provider extends TStructDescriptorProvider<StructType> {
        @Override
        public TStructDescriptor<StructType> descriptor() {
            return kDescriptor;
        }
    }

    private final static class _Factory
            extends TMessageBuilderFactory<StructType> {
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
            extends TMessageBuilder<StructType> {
        private String mComment;
        private StructVariant mVariant;
        private String mName;
        private List<ThriftField> mFields;

        public _Builder() {
            mFields = new LinkedList<>();
        }

        public _Builder(StructType base) {
            this();

            mComment = base.mComment;
            mVariant = base.mVariant;
            mName = base.mName;
            mFields.addAll(base.mFields);
        }

        public _Builder setComment(String value) {
            mComment = value;
            return this;
        }

        public _Builder clearComment() {
            mComment = null;
            return this;
        }

        public _Builder setVariant(StructVariant value) {
            mVariant = value;
            return this;
        }

        public _Builder clearVariant() {
            mVariant = null;
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

        public _Builder setFields(Collection<ThriftField> value) {
            mFields.clear();
            mFields.addAll(value);
            return this;
        }

        public _Builder addToFields(ThriftField... values) {
            for (ThriftField item : values) {
                mFields.add(item);
            }
            return this;
        }

        public _Builder clearFields() {
            mFields.clear();
            return this;
        }

        @Override
        public _Builder set(int key, Object value) {
            switch (key) {
                case 1: setComment((String) value); break;
                case 2: setVariant((StructVariant) value); break;
                case 3: setName((String) value); break;
                case 4: setFields((List<ThriftField>) value); break;
            }
            return this;
        }

        @Override
        public boolean isValid() {
            return mName != null;
        }

        @Override
        public StructType build() {
            return new StructType(this);
        }
    }
}
