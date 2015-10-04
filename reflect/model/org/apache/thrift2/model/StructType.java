package org.apache.thrift2.model;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.apache.thrift2.TMessage;
import org.apache.thrift2.TMessageBuilder;
import org.apache.thrift2.TMessageBuilderFactory;
import org.apache.thrift2.descriptor.TDefaultValueProvider;
import org.apache.thrift2.descriptor.TField;
import org.apache.thrift2.descriptor.TList;
import org.apache.thrift2.descriptor.TPrimitive;
import org.apache.thrift2.descriptor.TStructDescriptor;
import org.apache.thrift2.descriptor.TStructDescriptorProvider;
import org.apache.thrift2.util.TTypeUtils;

/**
 * <variant> {
 *   (<field> ([,;])?)*
 * }
 */
public class StructType
        implements TMessage<StructType>, Serializable {
    private final static StructVariant kDefaultVariant = StructVariant.STRUCT;

    private final String mComment;
    private final StructVariant mVariant;
    private final String mName;
    private final List<ThriftField> mFields;

    private StructType(Builder builder) {
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
               TTypeUtils.hashCode(mComment) +
               TTypeUtils.hashCode(mVariant) +
               TTypeUtils.hashCode(mName) +
               TTypeUtils.hashCode(mFields);
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
    public TStructDescriptor<StructType> descriptor() {
        return DESCRIPTOR;
    }

    public static final TStructDescriptor<StructType> DESCRIPTOR = _createDescriptor();

    private final static class _Factory
            extends TMessageBuilderFactory<StructType> {
        @Override
        public StructType.Builder builder() {
            return new StructType.Builder();
        }
    }

    private static TStructDescriptor<StructType> _createDescriptor() {
        List<TField<?>> fieldList = new LinkedList<>();
        fieldList.add(new TField<>(null, 1, false, "comment", TPrimitive.STRING.provider(), null));
        fieldList.add(new TField<>(null, 2, false, "variant", StructVariant.provider(), new TDefaultValueProvider<>(kDefaultVariant)));
        fieldList.add(new TField<>(null, 3, true, "name", TPrimitive.STRING.provider(), null));
        fieldList.add(new TField<>(null, 4, false, "fields", TList.provider(ThriftField.provider()), null));
        return new TStructDescriptor<>(null, "model", "StructType", fieldList, new _Factory());
    }

    public static TStructDescriptorProvider<StructType> provider() {
        return new TStructDescriptorProvider<StructType>() {
            @Override
            public TStructDescriptor<StructType> descriptor() {
                return DESCRIPTOR;
            }
        };
    }

    @Override
    public StructType.Builder mutate() {
        return new StructType.Builder(this);
    }

    public static StructType.Builder builder() {
        return new StructType.Builder();
    }

    public static class Builder
            extends TMessageBuilder<StructType> {
        private String mComment;
        private StructVariant mVariant;
        private String mName;
        private List<ThriftField> mFields;

        public Builder() {
            mFields = new LinkedList<>();
        }

        public Builder(StructType base) {
            this();

            mComment = base.mComment;
            mVariant = base.mVariant;
            mName = base.mName;
            mFields.addAll(base.mFields);
        }

        public Builder setComment(String value) {
            mComment = value;
            return this;
        }

        public Builder clearComment() {
            mComment = null;
            return this;
        }

        public Builder setVariant(StructVariant value) {
            mVariant = value;
            return this;
        }

        public Builder clearVariant() {
            mVariant = null;
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

        public Builder setFields(Collection<ThriftField> value) {
            mFields.clear();
            mFields.addAll(value);
            return this;
        }

        public Builder addToFields(ThriftField... values) {
            for (ThriftField item : values) {
                mFields.add(item);
            }
            return this;
        }

        public Builder clearFields() {
            mFields.clear();
            return this;
        }

        @Override
        public Builder set(int key, Object value) {
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
