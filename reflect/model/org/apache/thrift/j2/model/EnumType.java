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
 * enum {
 *   (<value> ([;,])?)*
 * }
 */
public class EnumType
        implements TMessage<EnumType>, Serializable {
    private final String mComment;
    private final String mName;
    private final List<EnumValue> mValues;

    private EnumType(Builder builder) {
        mComment = builder.mComment;
        mName = builder.mName;
        mValues = Collections.unmodifiableList(new LinkedList<>(builder.mValues));
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

    public int numValues() {
        return mValues.size();
    }

    public List<EnumValue> getValues() {
        return mValues;
    }

    @Override
    public boolean has(int key) {
        switch(key) {
            case 1: return hasComment();
            case 2: return hasName();
            case 3: return numValues() > 0;
            default: return false;
        }
    }

    @Override
    public int num(int key) {
        switch(key) {
            case 1: return hasComment() ? 1 : 0;
            case 2: return hasName() ? 1 : 0;
            case 3: return numValues();
            default: return 0;
        }
    }

    @Override
    public Object get(int key) {
        switch(key) {
            case 1: return getComment();
            case 2: return getName();
            case 3: return getValues();
            default: return null;
        }
    }

    @Override
    public boolean isCompact() {
        return false;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || !(o instanceof EnumType)) return false;
        EnumType other = (EnumType) o;
        return TTypeUtils.equals(mComment, other.mComment) &&
               TTypeUtils.equals(mName, other.mName) &&
               TTypeUtils.equals(mValues, other.mValues);
    }

    @Override
    public int hashCode() {
        return EnumType.class.hashCode() +
               TTypeUtils.hashCode(mComment) +
               TTypeUtils.hashCode(mName) +
               TTypeUtils.hashCode(mValues);
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
        VALUES(3, false, "values", TList.provider(EnumValue.provider()), null),
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
            builder.append(EnumType.class.getSimpleName())
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
    public TStructDescriptor<EnumType> getDescriptor() {
        return sDescriptor;
    }

    public static TStructDescriptor<EnumType> descriptor() {
        return sDescriptor;
    }

    public static final TStructDescriptor<EnumType> sDescriptor;

    private final static class Factory
            extends TMessageBuilderFactory<EnumType> {
        @Override
        public EnumType.Builder builder() {
            return new EnumType.Builder();
        }
    }

    static {
        sDescriptor = new TStructDescriptor<>(null, "model", "EnumType", EnumType.Field.values(), new Factory(), false);
    }

    public static TStructDescriptorProvider<EnumType> provider() {
        return new TStructDescriptorProvider<EnumType>() {
            @Override
            public TStructDescriptor<EnumType> descriptor() {
                return sDescriptor;
            }
        };
    }

    @Override
    public EnumType.Builder mutate() {
        return new EnumType.Builder(this);
    }

    public static EnumType.Builder builder() {
        return new EnumType.Builder();
    }

    public static class Builder
            extends TMessageBuilder<EnumType> {
        private String mComment;
        private String mName;
        private List<EnumValue> mValues;

        public Builder() {
            mValues = new LinkedList<>();
        }

        public Builder(EnumType base) {
            this();

            mComment = base.mComment;
            mName = base.mName;
            mValues.addAll(base.mValues);
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

        public Builder setValues(Collection<EnumValue> value) {
            mValues.clear();
            mValues.addAll(value);
            return this;
        }

        public Builder addToValues(EnumValue... values) {
            for (EnumValue item : values) {
                mValues.add(item);
            }
            return this;
        }

        public Builder clearValues() {
            mValues.clear();
            return this;
        }

        @Override
        public Builder set(int key, Object value) {
            switch (key) {
                case 1: setComment((String) value); break;
                case 2: setName((String) value); break;
                case 3: setValues((List<EnumValue>) value); break;
            }
            return this;
        }

        @Override
        public boolean isValid() {
            return mName != null;
        }

        @Override
        public EnumType build() {
            return new EnumType(this);
        }
    }
}
