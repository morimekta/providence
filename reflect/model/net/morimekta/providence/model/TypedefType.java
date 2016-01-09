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

/** typedef <type> <name> */
@SuppressWarnings("unused")
public class TypedefType
        implements PMessage<TypedefType>, Serializable {
    private final static long serialVersionUID = 5431583053440540554L;

    private final String mComment;
    private final String mType;
    private final String mName;

    private TypedefType(_Builder builder) {
        mComment = builder.mComment;
        mType = builder.mType;
        mName = builder.mName;
    }

    public TypedefType(String pComment,
                       String pType,
                       String pName) {
        mComment = pComment;
        mType = pType;
        mName = pName;
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
        return PTypeUtils.equals(mComment, other.mComment) &&
               PTypeUtils.equals(mType, other.mType) &&
               PTypeUtils.equals(mName, other.mName);
    }

    @Override
    public int hashCode() {
        return TypedefType.class.hashCode() +
               PTypeUtils.hashCode(_Field.COMMENT, mComment) +
               PTypeUtils.hashCode(_Field.TYPE, mType) +
               PTypeUtils.hashCode(_Field.NAME, mName);
    }

    @Override
    public String toString() {
        return descriptor().getQualifiedName(null) + PTypeUtils.toString(this);
    }

    public enum _Field implements PField {
        COMMENT(1, PRequirement.DEFAULT, "comment", PPrimitive.STRING.provider(), null),
        TYPE(2, PRequirement.DEFAULT, "type", PPrimitive.STRING.provider(), null),
        NAME(3, PRequirement.DEFAULT, "name", PPrimitive.STRING.provider(), null),
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
            builder.append(TypedefType.class.getSimpleName())
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

    public static PStructDescriptorProvider<TypedefType,_Field> provider() {
        return new _Provider();
    }

    @Override
    public PStructDescriptor<TypedefType,_Field> descriptor() {
        return kDescriptor;
    }

    public static final PStructDescriptor<TypedefType,_Field> kDescriptor;

    private static class _Descriptor
            extends PStructDescriptor<TypedefType,_Field> {
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

    private final static class _Provider extends PStructDescriptorProvider<TypedefType,_Field> {
        @Override
        public PStructDescriptor<TypedefType,_Field> descriptor() {
            return kDescriptor;
        }
    }

    private final static class _Factory
            extends PMessageBuilderFactory<TypedefType> {
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
            extends PMessageBuilder<TypedefType> {
        private BitSet optionals;

        private String mComment;
        private String mType;
        private String mName;


        public _Builder() {
            optionals = new BitSet(3);
        }

        public _Builder(TypedefType base) {
            this();

            if (base.hasComment()) {
                optionals.set(0);
                mComment = base.mComment;
            }
            if (base.hasType()) {
                optionals.set(1);
                mType = base.mType;
            }
            if (base.hasName()) {
                optionals.set(2);
                mName = base.mName;
            }
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
        public _Builder setType(String value) {
            optionals.set(1);
            mType = value;
            return this;
        }
        public _Builder clearType() {
            optionals.set(1, false);
            mType = null;
            return this;
        }
        public _Builder setName(String value) {
            optionals.set(2);
            mName = value;
            return this;
        }
        public _Builder clearName() {
            optionals.set(2, false);
            mName = null;
            return this;
        }
        @Override
        public _Builder set(int key, Object value) {
            if (value == null) return clear(key);
            switch (key) {
                case 1: setComment((String) value); break;
                case 2: setType((String) value); break;
                case 3: setName((String) value); break;
            }
            return this;
        }

        @Override
        public _Builder clear(int key) {
            switch (key) {
                case 1: clearComment(); break;
                case 2: clearType(); break;
                case 3: clearName(); break;
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
