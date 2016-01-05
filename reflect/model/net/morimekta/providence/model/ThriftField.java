package net.morimekta.providence.model;

import java.io.Serializable;

import net.morimekta.providence.PMessage;
import net.morimekta.providence.PMessageBuilderFactory;
import net.morimekta.providence.PType;
import net.morimekta.providence.descriptor.*;
import net.morimekta.providence.util.PTypeUtils;
import net.morimekta.providence.PMessageBuilder;
import net.morimekta.providence.descriptor.PStructDescriptor;

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
@SuppressWarnings("unused")
public class ThriftField
        implements PMessage<ThriftField>, Serializable {
    private final static int kDefaultKey = 0;
    private final static boolean kDefaultIsRequired = false;

    private final String mComment;
    private final Integer mKey;
    private final Boolean mIsRequired;
    private final String mType;
    private final String mName;
    private final String mDefaultValue;

    private ThriftField(_Builder builder) {
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
    public boolean isCompact() {
        return false;
    }

    @Override
    public boolean isSimple() {
        return descriptor().isSimple();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || !(o instanceof ThriftField)) return false;
        ThriftField other = (ThriftField) o;
        return PTypeUtils.equals(mComment, other.mComment) &&
               PTypeUtils.equals(mKey, other.mKey) &&
               PTypeUtils.equals(mIsRequired, other.mIsRequired) &&
               PTypeUtils.equals(mType, other.mType) &&
               PTypeUtils.equals(mName, other.mName) &&
               PTypeUtils.equals(mDefaultValue, other.mDefaultValue);
    }

    @Override
    public int hashCode() {
        return ThriftField.class.hashCode() +
               PTypeUtils.hashCode(_Field.COMMENT,mComment) +
               PTypeUtils.hashCode(_Field.KEY,mKey) +
               PTypeUtils.hashCode(_Field.IS_REQUIRED,mIsRequired) +
               PTypeUtils.hashCode(_Field.TYPE,mType) +
               PTypeUtils.hashCode(_Field.NAME,mName) +
               PTypeUtils.hashCode(_Field.DEFAULT_VALUE,mDefaultValue);
    }

    @Override
    public String toString() {
        return descriptor().getQualifiedName(null) + PTypeUtils.toString(this);
    }

    @Override
    public boolean isValid() {
        return mKey != null &&
               mType != null &&
               mName != null;
    }

    public enum _Field implements PField {
        COMMENT(1, false, "comment", PPrimitive.STRING.provider(), null),
        KEY(2, true, "key", PPrimitive.I32.provider(), null),
        IS_REQUIRED(3, false, "is_required", PPrimitive.BOOL.provider(), new PDefaultValueProvider<>(kDefaultIsRequired)),
        TYPE(4, true, "type", PPrimitive.STRING.provider(), null),
        NAME(5, true, "name", PPrimitive.STRING.provider(), null),
        DEFAULT_VALUE(6, false, "default_value", PPrimitive.STRING.provider(), null),
        ;

        private final int mKey;
        private final boolean mRequired;
        private final String mName;
        private final PDescriptorProvider<?> mTypeProvider;
        private final PValueProvider<?> mDefaultValue;

        _Field(int key, boolean required, String name, PDescriptorProvider<?> typeProvider, PValueProvider<?> defaultValue) {
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
        public PType getType() { return getDescriptor().getType(); }

        @Override
        public PDescriptor<?> getDescriptor() { return mTypeProvider.descriptor(); }

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
            builder.append(getDescriptor().getQualifiedName(null))
                   .append(' ')
                   .append(mName)
                   .append('}');
            return builder.toString();
        }

        public static _Field forKey(int key) {
            switch (key) {
                case 1: return _Field.COMMENT;
                case 2: return _Field.KEY;
                case 3: return _Field.IS_REQUIRED;
                case 4: return _Field.TYPE;
                case 5: return _Field.NAME;
                case 6: return _Field.DEFAULT_VALUE;
                default: return null;
            }
        }

        public static _Field forName(String name) {
            switch (name) {
                case "comment": return _Field.COMMENT;
                case "key": return _Field.KEY;
                case "is_required": return _Field.IS_REQUIRED;
                case "type": return _Field.TYPE;
                case "name": return _Field.NAME;
                case "default_value": return _Field.DEFAULT_VALUE;
            }
            return null;
        }
    }

    public static PStructDescriptorProvider<ThriftField,_Field> provider() {
        return new _Provider();
    }

    @Override
    public PStructDescriptor<ThriftField,_Field> descriptor() {
        return kDescriptor;
    }

    public static final PStructDescriptor<ThriftField,_Field> kDescriptor;

    private static class _Descriptor
            extends PStructDescriptor<ThriftField,_Field> {
        public _Descriptor() {
            super(null, "model", "ThriftField", new _Factory(), true, false);
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

    private final static class _Provider extends PStructDescriptorProvider<ThriftField,_Field> {
        @Override
        public PStructDescriptor<ThriftField,_Field> descriptor() {
            return kDescriptor;
        }
    }

    private final static class _Factory
            extends PMessageBuilderFactory<ThriftField> {
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
            extends PMessageBuilder<ThriftField> {
        private String mComment;
        private Integer mKey;
        private Boolean mIsRequired;
        private String mType;
        private String mName;
        private String mDefaultValue;

        public _Builder() {
        }

        public _Builder(ThriftField base) {
            this();

            mComment = base.mComment;
            mKey = base.mKey;
            mIsRequired = base.mIsRequired;
            mType = base.mType;
            mName = base.mName;
            mDefaultValue = base.mDefaultValue;
        }

        public _Builder setComment(String value) {
            mComment = value;
            return this;
        }

        public _Builder clearComment() {
            mComment = null;
            return this;
        }

        public _Builder setKey(int value) {
            mKey = value;
            return this;
        }

        public _Builder clearKey() {
            mKey = null;
            return this;
        }

        public _Builder setIsRequired(boolean value) {
            mIsRequired = value;
            return this;
        }

        public _Builder clearIsRequired() {
            mIsRequired = null;
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

        public _Builder setDefaultValue(String value) {
            mDefaultValue = value;
            return this;
        }

        public _Builder clearDefaultValue() {
            mDefaultValue = null;
            return this;
        }

        @Override
        public _Builder set(int key, Object value) {
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
