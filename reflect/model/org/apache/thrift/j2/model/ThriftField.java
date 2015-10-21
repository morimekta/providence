package org.apache.thrift.j2.model;

import java.io.Serializable;
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
import org.apache.thrift.j2.descriptor.TPrimitive;
import org.apache.thrift.j2.descriptor.TStructDescriptor;
import org.apache.thrift.j2.descriptor.TStructDescriptorProvider;
import org.apache.thrift.j2.descriptor.TValueProvider;
import org.apache.thrift.j2.util.TTypeUtils;

/**
 * For fields:
 *   (<key>:)? (required|optional)? <type> <name> (= <default_value>)?
 * For const:
 *   const <type> <name> = <default_value>
 * 
 * Fields without key is assigned values ranging from 65335 and down (2^16-1)
 * in order of appearance. Because of the "in order of appearance" the field
 * *must* be filled by the IDL parser.
 * 
 * Consts are always given the key '0'.
 */
public class ThriftField
        implements TMessage<ThriftField>, Serializable {
    private final static int kDefaultKey = 0;
    private final static boolean kDefaultIsRequired = false;

    private final String mComment;
    private final Integer mKey;
    private final Boolean mIsRequired;
    private final String mType;
    private final String mName;
    private final String mDefaultValue;

    private ThriftField(Builder builder) {
        mComment = builder.mComment;
        mKey = builder.mKey;
        mIsRequired = builder.mIsRequired;
        mType = builder.mType;
        mName = builder.mName;
        mDefaultValue = builder.mDefaultValue;
    }

    public boolean hasComment() {
        return mComment != null;
    }

    public String getComment() {
        return mComment;
    }

    public boolean hasKey() {
        return mKey != null;
    }

    public int getKey() {
        return hasKey() ? mKey : kDefaultKey;
    }

    public boolean hasIsRequired() {
        return mIsRequired != null;
    }

    public boolean getIsRequired() {
        return hasIsRequired() ? mIsRequired : kDefaultIsRequired;
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

    public boolean hasDefaultValue() {
        return mDefaultValue != null;
    }

    public String getDefaultValue() {
        return mDefaultValue;
    }

    @Override
    public boolean has(int key) {
        switch(key) {
            case 1: return hasComment();
            case 2: return hasKey();
            case 3: return hasIsRequired();
            case 4: return hasType();
            case 5: return hasName();
            case 6: return hasDefaultValue();
            default: return false;
        }
    }

    @Override
    public int num(int key) {
        switch(key) {
            case 1: return hasComment() ? 1 : 0;
            case 2: return hasKey() ? 1 : 0;
            case 3: return hasIsRequired() ? 1 : 0;
            case 4: return hasType() ? 1 : 0;
            case 5: return hasName() ? 1 : 0;
            case 6: return hasDefaultValue() ? 1 : 0;
            default: return 0;
        }
    }

    @Override
    public Object get(int key) {
        switch(key) {
            case 1: return getComment();
            case 2: return getKey();
            case 3: return getIsRequired();
            case 4: return getType();
            case 5: return getName();
            case 6: return getDefaultValue();
            default: return null;
        }
    }

    @Override
    public boolean compact() {
        return false;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || !(o instanceof ThriftField)) return false;
        ThriftField other = (ThriftField) o;
        return TTypeUtils.equals(mComment, other.mComment) &&
               TTypeUtils.equals(mKey, other.mKey) &&
               TTypeUtils.equals(mIsRequired, other.mIsRequired) &&
               TTypeUtils.equals(mType, other.mType) &&
               TTypeUtils.equals(mName, other.mName) &&
               TTypeUtils.equals(mDefaultValue, other.mDefaultValue);
    }

    @Override
    public int hashCode() {
        return ThriftField.class.hashCode() +
               TTypeUtils.hashCode(mComment) +
               TTypeUtils.hashCode(mKey) +
               TTypeUtils.hashCode(mIsRequired) +
               TTypeUtils.hashCode(mType) +
               TTypeUtils.hashCode(mName) +
               TTypeUtils.hashCode(mDefaultValue);
    }

    @Override
    public String toString() {
        return descriptor().getQualifiedName(null) + TTypeUtils.toString(this);
    }

    @Override
    public boolean isValid() {
        return mKey != null &&
               mType != null &&
               mName != null;
    }

    public enum Field implements TField {
        COMMENT(1, false, "comment", TPrimitive.STRING.provider(), null),
        KEY(2, true, "key", TPrimitive.I32.provider(), null),
        IS_REQUIRED(3, false, "is_required", TPrimitive.BOOL.provider(), new TDefaultValueProvider<>(kDefaultIsRequired)),
        TYPE(4, true, "type", TPrimitive.STRING.provider(), null),
        NAME(5, true, "name", TPrimitive.STRING.provider(), null),
        DEFAULT_VALUE(6, false, "default_value", TPrimitive.STRING.provider(), null),
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
            builder.append(ThriftField.class.getSimpleName())
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
    public TStructDescriptor<ThriftField> descriptor() {
        return DESCRIPTOR;
    }

    public static final TStructDescriptor<ThriftField> DESCRIPTOR;

    private final static class Factory
            extends TMessageBuilderFactory<ThriftField> {
        @Override
        public ThriftField.Builder builder() {
            return new ThriftField.Builder();
        }
    }

    static {
        DESCRIPTOR = new TStructDescriptor<>(null, "model", "ThriftField", ThriftField.Field.values(), new Factory(), false);
    }

    public static TStructDescriptorProvider<ThriftField> provider() {
        return new TStructDescriptorProvider<ThriftField>() {
            @Override
            public TStructDescriptor<ThriftField> descriptor() {
                return DESCRIPTOR;
            }
        };
    }

    @Override
    public ThriftField.Builder mutate() {
        return new ThriftField.Builder(this);
    }

    public static ThriftField.Builder builder() {
        return new ThriftField.Builder();
    }

    public static class Builder
            extends TMessageBuilder<ThriftField> {
        private String mComment;
        private Integer mKey;
        private Boolean mIsRequired;
        private String mType;
        private String mName;
        private String mDefaultValue;

        public Builder() {
        }

        public Builder(ThriftField base) {
            this();

            mComment = base.mComment;
            mKey = base.mKey;
            mIsRequired = base.mIsRequired;
            mType = base.mType;
            mName = base.mName;
            mDefaultValue = base.mDefaultValue;
        }

        public Builder setComment(String value) {
            mComment = value;
            return this;
        }

        public Builder clearComment() {
            mComment = null;
            return this;
        }

        public Builder setKey(int value) {
            mKey = value;
            return this;
        }

        public Builder clearKey() {
            mKey = null;
            return this;
        }

        public Builder setIsRequired(boolean value) {
            mIsRequired = value;
            return this;
        }

        public Builder clearIsRequired() {
            mIsRequired = null;
            return this;
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

        public Builder setDefaultValue(String value) {
            mDefaultValue = value;
            return this;
        }

        public Builder clearDefaultValue() {
            mDefaultValue = null;
            return this;
        }

        @Override
        public Builder set(int key, Object value) {
            switch (key) {
                case 1: setComment((String) value); break;
                case 2: setKey((int) value); break;
                case 3: setIsRequired((boolean) value); break;
                case 4: setType((String) value); break;
                case 5: setName((String) value); break;
                case 6: setDefaultValue((String) value); break;
            }
            return this;
        }

        @Override
        public boolean isValid() {
            return mKey != null &&
                   mType != null &&
                   mName != null;
        }

        @Override
        public ThriftField build() {
            return new ThriftField(this);
        }
    }
}
