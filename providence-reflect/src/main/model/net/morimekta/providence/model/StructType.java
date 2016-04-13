package net.morimekta.providence.model;

import net.morimekta.providence.PMessage;
import net.morimekta.providence.PMessageBuilder;
import net.morimekta.providence.PMessageBuilderFactory;
import net.morimekta.providence.PType;
import net.morimekta.providence.descriptor.PDefaultValueProvider;
import net.morimekta.providence.descriptor.PDescriptor;
import net.morimekta.providence.descriptor.PDescriptorProvider;
import net.morimekta.providence.descriptor.PField;
import net.morimekta.providence.descriptor.PList;
import net.morimekta.providence.descriptor.PMap;
import net.morimekta.providence.descriptor.PPrimitive;
import net.morimekta.providence.descriptor.PRequirement;
import net.morimekta.providence.descriptor.PStructDescriptor;
import net.morimekta.providence.descriptor.PStructDescriptorProvider;
import net.morimekta.providence.descriptor.PValueProvider;
import net.morimekta.providence.util.PTypeUtils;

import java.io.Serializable;
import java.util.BitSet;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * <variant> {
 *   (<field> ([,;])?)*
 * }
 */
@SuppressWarnings("unused")
public class StructType
        implements PMessage<StructType>, Serializable, Comparable<StructType> {
    private final static long serialVersionUID = -7531050363059752370L;

    private final static StructVariant kDefaultVariant = StructVariant.STRUCT;

    private final String mComment;
    private final StructVariant mVariant;
    private final String mName;
    private final List<ThriftField> mFields;
    private final Map<String,String> mAnnotations;
    
    private volatile int tHashCode;

    private StructType(_Builder builder) {
        mComment = builder.mComment;
        mVariant = builder.mVariant;
        mName = builder.mName;
        mFields = Collections.unmodifiableList(new LinkedList<>(builder.mFields));
        mAnnotations = Collections.unmodifiableMap(new LinkedHashMap<>(builder.mAnnotations));
    }

    public StructType(String pComment,
                      StructVariant pVariant,
                      String pName,
                      List<ThriftField> pFields,
                      Map<String,String> pAnnotations) {
        mComment = pComment;
        mVariant = pVariant;
        mName = pName;
        mFields = Collections.unmodifiableList(new LinkedList<>(pFields));
        mAnnotations = Collections.unmodifiableMap(new LinkedHashMap<>(pAnnotations));
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
        return mFields != null ? mFields.size() : 0;
    }

    public List<ThriftField> getFields() {
        return mFields;
    }

    public int numAnnotations() {
        return mAnnotations != null ? mAnnotations.size() : 0;
    }

    public Map<String,String> getAnnotations() {
        return mAnnotations;
    }

    @Override
    public boolean has(int key) {
        switch(key) {
            case 1: return hasComment();
            case 2: return hasVariant();
            case 3: return hasName();
            case 4: return numFields() > 0;
            case 5: return numAnnotations() > 0;
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
            case 5: return numAnnotations();
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
            case 5: return getAnnotations();
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
        if (o == null || !(o instanceof StructType)) return false;
        StructType other = (StructType) o;
        return Objects.equals(mComment, other.mComment) &&
               Objects.equals(mVariant, other.mVariant) &&
               Objects.equals(mName, other.mName) &&
               PTypeUtils.equals(mFields, other.mFields) &&
               PTypeUtils.equals(mAnnotations, other.mAnnotations);
    }

    @Override
    public int hashCode() {
        if (tHashCode == 0) {
            tHashCode = Objects.hash(
                    StructType.class,
                    _Field.COMMENT, mComment,
                    _Field.VARIANT, mVariant,
                    _Field.NAME, mName,
                    _Field.FIELDS, PTypeUtils.hashCode(mFields),
                    _Field.ANNOTATIONS, PTypeUtils.hashCode(mAnnotations));
        }
        return tHashCode;
    }

    @Override
    public String toString() {
        return "model.StructType" + asString();
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
        if (hasVariant()) {
            if (!first) out.append(',');
            first = false;
            out.append("variant:");
            out.append(mVariant.getName());
        }
        if (hasName()) {
            if (!first) out.append(',');
            first = false;
            out.append("name:");
            out.append('\"').append(mName).append('\"');
        }
        if (numFields() > 0) {
            if (!first) out.append(',');
            first = false;
            out.append("fields:");
            out.append(PTypeUtils.toString(mFields));
        }
        if (numAnnotations() > 0) {
            if (!first) out.append(',');
            first = false;
            out.append("annotations:");
            out.append(PTypeUtils.toString(mAnnotations));
        }
        out.append('}');
        return out.toString();
    }

    @Override
    public int compareTo(StructType other) {
        int c;

        c = Boolean.compare(mComment != null, other.mComment != null);
        if (c != 0) return c;
        if (mComment != null) {
            c = mComment.compareTo(other.mComment);
            if (c != 0) return c;
        }

        c = Boolean.compare(mVariant != null, other.mVariant != null);
        if (c != 0) return c;
        if (mVariant != null) {
            c = Integer.compare(mVariant.getValue(), mVariant.getValue());
            if (c != 0) return c;
        }

        c = Boolean.compare(mName != null, other.mName != null);
        if (c != 0) return c;
        if (mName != null) {
            c = mName.compareTo(other.mName);
            if (c != 0) return c;
        }

        c = Boolean.compare(mFields != null, other.mFields != null);
        if (c != 0) return c;
        if (mFields != null) {
            c = Integer.compare(mFields.hashCode(), other.mFields.hashCode());
            if (c != 0) return c;
        }

        c = Boolean.compare(mAnnotations != null, other.mAnnotations != null);
        if (c != 0) return c;
        if (mAnnotations != null) {
            c = Integer.compare(mAnnotations.hashCode(), other.mAnnotations.hashCode());
            if (c != 0) return c;
        }

        return 0;
    }

    public enum _Field implements PField {
        COMMENT(1, PRequirement.DEFAULT, "comment", PPrimitive.STRING.provider(), null),
        VARIANT(2, PRequirement.DEFAULT, "variant", StructVariant.provider(), new PDefaultValueProvider<>(kDefaultVariant)),
        NAME(3, PRequirement.REQUIRED, "name", PPrimitive.STRING.provider(), null),
        FIELDS(4, PRequirement.DEFAULT, "fields", PList.provider(ThriftField.provider()), null),
        ANNOTATIONS(5, PRequirement.DEFAULT, "annotations", PMap.provider(PPrimitive.STRING.provider(),PPrimitive.STRING.provider()), null),
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
            builder.append("StructType._Field(")
                   .append(mKey)
                   .append(": ");
            if (mRequired != PRequirement.DEFAULT) {
                builder.append(mRequired.label).append(" ");
            }
            builder.append(getDescriptor().getQualifiedName(null))
                   .append(' ')
                   .append(mName)
                   .append(')');
            return builder.toString();
        }

        public static _Field forKey(int key) {
            switch (key) {
                case 1: return _Field.COMMENT;
                case 2: return _Field.VARIANT;
                case 3: return _Field.NAME;
                case 4: return _Field.FIELDS;
                case 5: return _Field.ANNOTATIONS;
                default: return null;
            }
        }

        public static _Field forName(String name) {
            switch (name) {
                case "comment": return _Field.COMMENT;
                case "variant": return _Field.VARIANT;
                case "name": return _Field.NAME;
                case "fields": return _Field.FIELDS;
                case "annotations": return _Field.ANNOTATIONS;
            }
            return null;
        }
    }

    public static PStructDescriptorProvider<StructType,_Field> provider() {
        return new _Provider();
    }

    @Override
    public PStructDescriptor<StructType,_Field> descriptor() {
        return kDescriptor;
    }

    public static final PStructDescriptor<StructType,_Field> kDescriptor;

    private static class _Descriptor
            extends PStructDescriptor<StructType,_Field> {
        public _Descriptor() {
            super(null, "model", "StructType", new _Factory(), false, false);
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

    private final static class _Provider extends PStructDescriptorProvider<StructType,_Field> {
        @Override
        public PStructDescriptor<StructType,_Field> descriptor() {
            return kDescriptor;
        }
    }

    private final static class _Factory
            extends PMessageBuilderFactory<StructType> {
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
            extends PMessageBuilder<StructType> {
        private BitSet optionals;

        private String mComment;
        private StructVariant mVariant;
        private String mName;
        private List<ThriftField> mFields;
        private Map<String,String> mAnnotations;


        public _Builder() {
            optionals = new BitSet(5);
            mFields = new LinkedList<>();
            mAnnotations = new LinkedHashMap<>();
        }

        public _Builder(StructType base) {
            this();

            if (base.hasComment()) {
                optionals.set(0);
                mComment = base.mComment;
            }
            if (base.hasVariant()) {
                optionals.set(1);
                mVariant = base.mVariant;
            }
            if (base.hasName()) {
                optionals.set(2);
                mName = base.mName;
            }
            if (base.numFields() > 0) {
                optionals.set(3);
                mFields.addAll(base.mFields);
            }
            if (base.numAnnotations() > 0) {
                optionals.set(4);
                mAnnotations.putAll(base.mAnnotations);
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
        public _Builder setVariant(StructVariant value) {
            optionals.set(1);
            mVariant = value;
            return this;
        }
        public _Builder clearVariant() {
            optionals.set(1, false);
            mVariant = null;
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
        public _Builder setFields(Collection<ThriftField> value) {
            optionals.set(3);
            mFields.clear();
            mFields.addAll(value);
            return this;
        }
        public _Builder addToFields(ThriftField... values) {
            optionals.set(3);
            for (ThriftField item : values) {
                mFields.add(item);
            }
            return this;
        }

        public _Builder clearFields() {
            optionals.set(3, false);
            mFields.clear();
            return this;
        }
        public _Builder setAnnotations(Map<String,String> value) {
            optionals.set(4);
            mAnnotations.clear();
            mAnnotations.putAll(value);
            return this;
        }
        public _Builder putInAnnotations(String key, String value) {
            optionals.set(4);
            mAnnotations.put(key, value);
            return this;
        }

        public _Builder clearAnnotations() {
            optionals.set(4, false);
            mAnnotations.clear();
            return this;
        }
        @Override
        public _Builder set(int key, Object value) {
            if (value == null) return clear(key);
            switch (key) {
                case 1: setComment((String) value); break;
                case 2: setVariant((StructVariant) value); break;
                case 3: setName((String) value); break;
                case 4: setFields((List<ThriftField>) value); break;
                case 5: setAnnotations((Map<String,String>) value); break;
            }
            return this;
        }

        @Override
        public _Builder addTo(int key, Object value) {
            switch (key) {
                case 4: addToFields((ThriftField) value); break;
                default: break;
            }
            return this;
        }

        @Override
        public _Builder clear(int key) {
            switch (key) {
                case 1: clearComment(); break;
                case 2: clearVariant(); break;
                case 3: clearName(); break;
                case 4: clearFields(); break;
                case 5: clearAnnotations(); break;
            }
            return this;
        }

        @Override
        public boolean isValid() {
            return optionals.get(2);
        }

        @Override
        public StructType build() {
            return new StructType(this);
        }
    }
}
