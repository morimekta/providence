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

/** <name> (= <value>) */
public class EnumValue
        implements TMessage<EnumValue>, Serializable {
    private final static int kDefaultValue = 0;

    private final String mComment;
    private final String mName;
    private final Integer mValue;

    private EnumValue(Builder builder) {
        mComment = builder.mComment;
        mName = builder.mName;
        mValue = builder.mValue;
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

    public boolean hasValue() {
        return mValue != null;
    }

    public int getValue() {
        return hasValue() ? mValue : kDefaultValue;
    }

    @Override
    public boolean has(int key) {
        switch(key) {
            case 1: return hasComment();
            case 2: return hasName();
            case 3: return hasValue();
            default: return false;
        }
    }

    @Override
    public int num(int key) {
        switch(key) {
            case 1: return hasComment() ? 1 : 0;
            case 2: return hasName() ? 1 : 0;
            case 3: return hasValue() ? 1 : 0;
            default: return 0;
        }
    }

    @Override
    public Object get(int key) {
        switch(key) {
            case 1: return getComment();
            case 2: return getName();
            case 3: return getValue();
            default: return null;
        }
    }

    @Override
    public boolean isCompact() {
        return false;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || !(o instanceof EnumValue)) return false;
        EnumValue other = (EnumValue) o;
        return TTypeUtils.equals(mComment, other.mComment) &&
               TTypeUtils.equals(mName, other.mName) &&
               TTypeUtils.equals(mValue, other.mValue);
    }

    @Override
    public int hashCode() {
        return EnumValue.class.hashCode() +
               TTypeUtils.hashCode(mComment) +
               TTypeUtils.hashCode(mName) +
               TTypeUtils.hashCode(mValue);
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
        VALUE(3, false, "value", TPrimitive.I32.provider(), null),
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
            builder.append(EnumValue.class.getSimpleName())
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
    public TStructDescriptor<EnumValue> getDescriptor() {
        return sDescriptor;
    }

    public static TStructDescriptor<EnumValue> descriptor() {
        return sDescriptor;
    }

    public static final TStructDescriptor<EnumValue> sDescriptor;

    private final static class Factory
            extends TMessageBuilderFactory<EnumValue> {
        @Override
        public EnumValue.Builder builder() {
            return new EnumValue.Builder();
        }
    }

    static {
        sDescriptor = new TStructDescriptor<>(null, "model", "EnumValue", EnumValue.Field.values(), new Factory(), false);
    }

    public static TStructDescriptorProvider<EnumValue> provider() {
        return new TStructDescriptorProvider<EnumValue>() {
            @Override
            public TStructDescriptor<EnumValue> descriptor() {
                return sDescriptor;
            }
        };
    }

    @Override
    public EnumValue.Builder mutate() {
        return new EnumValue.Builder(this);
    }

    public static EnumValue.Builder builder() {
        return new EnumValue.Builder();
    }

    public static class Builder
            extends TMessageBuilder<EnumValue> {
        private String mComment;
        private String mName;
        private Integer mValue;

        public Builder() {
        }

        public Builder(EnumValue base) {
            this();

            mComment = base.mComment;
            mName = base.mName;
            mValue = base.mValue;
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

        public Builder setValue(int value) {
            mValue = value;
            return this;
        }

        public Builder clearValue() {
            mValue = null;
            return this;
        }

        @Override
        public Builder set(int key, Object value) {
            switch (key) {
                case 1: setComment((String) value); break;
                case 2: setName((String) value); break;
                case 3: setValue((int) value); break;
            }
            return this;
        }

        @Override
        public boolean isValid() {
            return mName != null;
        }

        @Override
        public EnumValue build() {
            return new EnumValue(this);
        }
    }
}
