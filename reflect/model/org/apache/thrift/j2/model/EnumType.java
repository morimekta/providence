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
@SuppressWarnings("unused")
public class EnumType
        implements TMessage<EnumType>, Serializable {
    private final String mComment;
    private final String mName;
    private final List<EnumValue> mValues;

    private EnumType(_Builder builder) {
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
               TTypeUtils.hashCode(_Field.COMMENT,mComment) +
               TTypeUtils.hashCode(_Field.NAME,mName) +
               TTypeUtils.hashCode(_Field.VALUES,mValues);
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
        NAME(2, true, "name", TPrimitive.STRING.provider(), null),
        VALUES(3, false, "values", TList.provider(EnumValue.provider()), null),
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

        public static _Field forKey(int key) {
            switch (key) {
                case 1: return _Field.COMMENT;
                case 2: return _Field.NAME;
                case 3: return _Field.VALUES;
                default: return null;
            }
        }

        public static _Field forName(String name) {
            switch (name) {
                case "comment": return _Field.COMMENT;
                case "name": return _Field.NAME;
                case "values": return _Field.VALUES;
            }
            return null;
        }
    }

    public static TStructDescriptorProvider<EnumType> provider() {
        return new _Provider();
    }

    @Override
    public TStructDescriptor<EnumType> descriptor() {
        return kDescriptor;
    }

    public static final TStructDescriptor<EnumType> kDescriptor;

    static {
        kDescriptor = new TStructDescriptor<>(null, "model", "EnumType", _Field.values(), new _Factory(), false);
    }

    private final static class _Provider extends TStructDescriptorProvider<EnumType> {
        @Override
        public TStructDescriptor<EnumType> descriptor() {
            return kDescriptor;
        }
    }

    private final static class _Factory
            extends TMessageBuilderFactory<EnumType> {
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
            extends TMessageBuilder<EnumType> {
        private String mComment;
        private String mName;
        private List<EnumValue> mValues;

        public _Builder() {
            mValues = new LinkedList<>();
        }

        public _Builder(EnumType base) {
            this();

            mComment = base.mComment;
            mName = base.mName;
            mValues.addAll(base.mValues);
        }

        public _Builder setComment(String value) {
            mComment = value;
            return this;
        }

        public _Builder clearComment() {
            mComment = null;
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

        public _Builder setValues(Collection<EnumValue> value) {
            mValues.clear();
            mValues.addAll(value);
            return this;
        }

        public _Builder addToValues(EnumValue... values) {
            for (EnumValue item : values) {
                mValues.add(item);
            }
            return this;
        }

        public _Builder clearValues() {
            mValues.clear();
            return this;
        }

        @Override
        public _Builder set(int key, Object value) {
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
