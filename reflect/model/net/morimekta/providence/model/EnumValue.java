package net.morimekta.providence.model;

import java.io.Serializable;
import java.util.BitSet;

import net.morimekta.providence.PMessage;
import net.morimekta.providence.PMessageBuilder;
import net.morimekta.providence.PMessageBuilderFactory;
import net.morimekta.providence.PType;
import net.morimekta.providence.descriptor.PDescriptor;
import net.morimekta.providence.descriptor.PDescriptorProvider;
import net.morimekta.providence.descriptor.PField;
import net.morimekta.providence.descriptor.PPrimitive;
import net.morimekta.providence.descriptor.PRequirement;
import net.morimekta.providence.descriptor.PStructDescriptor;
import net.morimekta.providence.descriptor.PStructDescriptorProvider;
import net.morimekta.providence.descriptor.PValueProvider;
import net.morimekta.providence.util.PTypeUtils;

/** <name> (= <value>) */
@SuppressWarnings("unused")
public class EnumValue
        implements PMessage<EnumValue>, Serializable {
    private final static long serialVersionUID = -4079600082644582517L;

    private final static int kDefaultValue = 0;

    private final String mComment;
    private final String mName;
    private final int mValue;

    private EnumValue(_Builder builder) {
        mComment = builder.mComment;
        mName = builder.mName;
        mValue = builder.mValue;
    }

    public EnumValue(String pComment,
                     String pName,
                     int pValue) {
        mComment = pComment;
        mName = pName;
        mValue = pValue;
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
        return mValue != kDefaultValue;
    }

    public int getValue() {
        return mValue;
    }

    @Override
    public boolean has(int key) {
        switch(key) {
            case 1: return hasComment();
            case 2: return hasName();
            case 3: return true;
            default: return false;
        }
    }

    @Override
    public int num(int key) {
        switch(key) {
            case 1: return hasComment() ? 1 : 0;
            case 2: return hasName() ? 1 : 0;
            case 3: return 1;
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
    public boolean isSimple() {
        return descriptor().isSimple();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || !(o instanceof EnumValue)) return false;
        EnumValue other = (EnumValue) o;
        return PTypeUtils.equals(mComment, other.mComment) &&
               PTypeUtils.equals(mName, other.mName) &&
               PTypeUtils.equals(mValue, other.mValue);
    }

    @Override
    public int hashCode() {
        return EnumValue.class.hashCode() +
               PTypeUtils.hashCode(_Field.COMMENT, mComment) +
               PTypeUtils.hashCode(_Field.NAME, mName) +
               PTypeUtils.hashCode(_Field.VALUE, mValue);
    }

    @Override
    public String toString() {
        return "model.EnumValue" + asString();
    }

    @Override
    public String asString() {
        StringBuilder out = new StringBuilder();
        out.append("{");

        boolean first = true;
        if (hasComment()) {
            first = false;
            out.append("comment:");
            out.append('\"').append(mComment).append('\"');
        }
        if (hasName()) {
            if (!first) out.append(',');
            first = false;
            out.append("name:");
            out.append('\"').append(mName).append('\"');
        }
        if (hasValue()) {
            if (!first) out.append(',');
            first = false;
            out.append("value:");
            out.append(Integer.toString(mValue));
        }
        out.append('}');
        return out.toString();
    }

    public enum _Field implements PField {
        COMMENT(1, PRequirement.DEFAULT, "comment", PPrimitive.STRING.provider(), null),
        NAME(2, PRequirement.REQUIRED, "name", PPrimitive.STRING.provider(), null),
        VALUE(3, PRequirement.DEFAULT, "value", PPrimitive.I32.provider(), null),
        ;

        private final int mKey;
        private final PRequirement mRequired;
        private final String mName;
        private final PDescriptorProvider<?> mTypeProvider;
        private final PValueProvider<?> mDefaultValue;

        _Field(int key, PRequirement required, String name, PDescriptorProvider<?> typeProvider, PValueProvider<?> defaultValue) {
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
        public PRequirement getRequirement() { return mRequired; }

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
            builder.append(EnumValue.class.getSimpleName())
                   .append('{')
                   .append(mKey)
                   .append(": ");
            if (mRequired != PRequirement.DEFAULT) {
                builder.append(mRequired.label).append(" ");
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
                case 3: return _Field.VALUE;
                default: return null;
            }
        }

        public static _Field forName(String name) {
            switch (name) {
                case "comment": return _Field.COMMENT;
                case "name": return _Field.NAME;
                case "value": return _Field.VALUE;
            }
            return null;
        }
    }

    public static PStructDescriptorProvider<EnumValue,_Field> provider() {
        return new _Provider();
    }

    @Override
    public PStructDescriptor<EnumValue,_Field> descriptor() {
        return kDescriptor;
    }

    public static final PStructDescriptor<EnumValue,_Field> kDescriptor;

    private static class _Descriptor
            extends PStructDescriptor<EnumValue,_Field> {
        public _Descriptor() {
            super(null, "model", "EnumValue", new _Factory(), true, false);
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

    private final static class _Provider extends PStructDescriptorProvider<EnumValue,_Field> {
        @Override
        public PStructDescriptor<EnumValue,_Field> descriptor() {
            return kDescriptor;
        }
    }

    private final static class _Factory
            extends PMessageBuilderFactory<EnumValue> {
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
            extends PMessageBuilder<EnumValue> {
        private BitSet optionals;

        private String mComment;
        private String mName;
        private int mValue;


        public _Builder() {
            optionals = new BitSet(3);
            mValue = kDefaultValue;
        }

        public _Builder(EnumValue base) {
            this();

            if (base.hasComment()) {
                optionals.set(0);
                mComment = base.mComment;
            }
            if (base.hasName()) {
                optionals.set(1);
                mName = base.mName;
            }
            optionals.set(2);
            mValue = base.mValue;
        }

        public _Builder setComment(String value) {
            optionals.set(0);
            mComment = value;
            return this;
        }
        public _Builder clearComment() {
            optionals.set(0, false);
            mComment = null;
            return this;
        }
        public _Builder setName(String value) {
            optionals.set(1);
            mName = value;
            return this;
        }
        public _Builder clearName() {
            optionals.set(1, false);
            mName = null;
            return this;
        }
        public _Builder setValue(int value) {
            optionals.set(2);
            mValue = value;
            return this;
        }
        public _Builder clearValue() {
            optionals.set(2, false);
            mValue = kDefaultValue;
            return this;
        }
        @Override
        public _Builder set(int key, Object value) {
            if (value == null) return clear(key);
            switch (key) {
                case 1: setComment((String) value); break;
                case 2: setName((String) value); break;
                case 3: setValue((int) value); break;
            }
            return this;
        }

        @Override
        public _Builder clear(int key) {
            switch (key) {
                case 1: clearComment(); break;
                case 2: clearName(); break;
                case 3: clearValue(); break;
            }
            return this;
        }

        @Override
        public boolean isValid() {
            return optionals.get(1);
        }

        @Override
        public EnumValue build() {
            return new EnumValue(this);
        }
    }
}
