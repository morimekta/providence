package net.morimekta.providence.model;

import java.io.Serializable;
import java.util.BitSet;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import net.morimekta.providence.PMessage;
import net.morimekta.providence.PMessageBuilder;
import net.morimekta.providence.PMessageBuilderFactory;
import net.morimekta.providence.PType;
import net.morimekta.providence.descriptor.PDefaultValueProvider;
import net.morimekta.providence.descriptor.PDescriptor;
import net.morimekta.providence.descriptor.PDescriptorProvider;
import net.morimekta.providence.descriptor.PField;
import net.morimekta.providence.descriptor.PList;
import net.morimekta.providence.descriptor.PPrimitive;
import net.morimekta.providence.descriptor.PRequirement;
import net.morimekta.providence.descriptor.PStructDescriptor;
import net.morimekta.providence.descriptor.PStructDescriptorProvider;
import net.morimekta.providence.descriptor.PValueProvider;
import net.morimekta.providence.util.PTypeUtils;

/** (oneway)? <return_type> <name>'('<param>*')' (throws '(' <exception>+ ')')? */
@SuppressWarnings("unused")
public class ServiceMethod
        implements PMessage<ServiceMethod>, Serializable {
    private final static long serialVersionUID = -8952857258512990537L;

    private final static boolean kDefaultOneWay = false;

    private final String mComment;
    private final boolean mOneWay;
    private final String mReturnType;
    private final String mName;
    private final List<ThriftField> mParams;
    private final List<ThriftField> mExceptions;
    private final int tHashCode;

    private ServiceMethod(_Builder builder) {
        mComment = builder.mComment;
        mOneWay = builder.mOneWay;
        mReturnType = builder.mReturnType;
        mName = builder.mName;
        mParams = Collections.unmodifiableList(new LinkedList<>(builder.mParams));
        mExceptions = Collections.unmodifiableList(new LinkedList<>(builder.mExceptions));

        tHashCode = Objects.hash(
                ServiceMethod.class,
                _Field.COMMENT, mComment,
                _Field.ONE_WAY, mOneWay,
                _Field.RETURN_TYPE, mReturnType,
                _Field.NAME, mName,
                _Field.PARAMS, PTypeUtils.hashCode(mParams),
                _Field.EXCEPTIONS, PTypeUtils.hashCode(mExceptions));
    }

    public ServiceMethod(String pComment,
                         boolean pOneWay,
                         String pReturnType,
                         String pName,
                         List<ThriftField> pParams,
                         List<ThriftField> pExceptions) {
        mComment = pComment;
        mOneWay = pOneWay;
        mReturnType = pReturnType;
        mName = pName;
        mParams = Collections.unmodifiableList(new LinkedList<>(pParams));
        mExceptions = Collections.unmodifiableList(new LinkedList<>(pExceptions));

        tHashCode = Objects.hash(
                ServiceMethod.class,
                _Field.COMMENT, mComment,
                _Field.ONE_WAY, mOneWay,
                _Field.RETURN_TYPE, mReturnType,
                _Field.NAME, mName,
                _Field.PARAMS, PTypeUtils.hashCode(mParams),
                _Field.EXCEPTIONS, PTypeUtils.hashCode(mExceptions));
    }

    public boolean hasComment() {
        return mComment != null;
    }

    public String getComment() {
        return mComment;
    }

    public boolean hasOneWay() {
        return true;
    }

    public boolean isOneWay() {
        return mOneWay;
    }

    public boolean hasReturnType() {
        return mReturnType != null;
    }

    public String getReturnType() {
        return mReturnType;
    }

    public boolean hasName() {
        return mName != null;
    }

    public String getName() {
        return mName;
    }

    public int numParams() {
        return mParams != null ? mParams.size() : 0;
    }

    public List<ThriftField> getParams() {
        return mParams;
    }

    public int numExceptions() {
        return mExceptions != null ? mExceptions.size() : 0;
    }

    public List<ThriftField> getExceptions() {
        return mExceptions;
    }

    @Override
    public boolean has(int key) {
        switch(key) {
            case 1: return hasComment();
            case 2: return true;
            case 3: return hasReturnType();
            case 4: return hasName();
            case 5: return numParams() > 0;
            case 6: return numExceptions() > 0;
            default: return false;
        }
    }

    @Override
    public int num(int key) {
        switch(key) {
            case 1: return hasComment() ? 1 : 0;
            case 2: return 1;
            case 3: return hasReturnType() ? 1 : 0;
            case 4: return hasName() ? 1 : 0;
            case 5: return numParams();
            case 6: return numExceptions();
            default: return 0;
        }
    }

    @Override
    public Object get(int key) {
        switch(key) {
            case 1: return getComment();
            case 2: return isOneWay();
            case 3: return getReturnType();
            case 4: return getName();
            case 5: return getParams();
            case 6: return getExceptions();
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
        if (o == null || !(o instanceof ServiceMethod)) return false;
        ServiceMethod other = (ServiceMethod) o;
        return Objects.equals(mComment, other.mComment) &&
               Objects.equals(mOneWay, other.mOneWay) &&
               Objects.equals(mReturnType, other.mReturnType) &&
               Objects.equals(mName, other.mName) &&
               PTypeUtils.equals(mParams, other.mParams) &&
               PTypeUtils.equals(mExceptions, other.mExceptions);
    }

    @Override
    public int hashCode() {
        return tHashCode;
    }

    @Override
    public String toString() {
        return "model.ServiceMethod" + asString();
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
        if (hasOneWay()) {
            if (!first) out.append(',');
            first = false;
            out.append("one_way:");
            out.append(mOneWay ? "true" : "false");
        }
        if (hasReturnType()) {
            if (!first) out.append(',');
            first = false;
            out.append("return_type:");
            out.append('\"').append(mReturnType).append('\"');
        }
        if (hasName()) {
            if (!first) out.append(',');
            first = false;
            out.append("name:");
            out.append('\"').append(mName).append('\"');
        }
        if (numParams() > 0) {
            if (!first) out.append(',');
            first = false;
            out.append("params:");
            out.append(PTypeUtils.toString(mParams));
        }
        if (numExceptions() > 0) {
            if (!first) out.append(',');
            first = false;
            out.append("exceptions:");
            out.append(PTypeUtils.toString(mExceptions));
        }
        out.append('}');
        return out.toString();
    }

    public enum _Field implements PField {
        COMMENT(1, PRequirement.DEFAULT, "comment", PPrimitive.STRING.provider(), null),
        ONE_WAY(2, PRequirement.DEFAULT, "one_way", PPrimitive.BOOL.provider(), new PDefaultValueProvider<>(kDefaultOneWay)),
        RETURN_TYPE(3, PRequirement.DEFAULT, "return_type", PPrimitive.STRING.provider(), null),
        NAME(4, PRequirement.REQUIRED, "name", PPrimitive.STRING.provider(), null),
        PARAMS(5, PRequirement.DEFAULT, "params", PList.provider(ThriftField.provider()), null),
        EXCEPTIONS(6, PRequirement.DEFAULT, "exceptions", PList.provider(ThriftField.provider()), null),
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
            builder.append("ServiceMethod._Field(")
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
                case 2: return _Field.ONE_WAY;
                case 3: return _Field.RETURN_TYPE;
                case 4: return _Field.NAME;
                case 5: return _Field.PARAMS;
                case 6: return _Field.EXCEPTIONS;
                default: return null;
            }
        }

        public static _Field forName(String name) {
            switch (name) {
                case "comment": return _Field.COMMENT;
                case "one_way": return _Field.ONE_WAY;
                case "return_type": return _Field.RETURN_TYPE;
                case "name": return _Field.NAME;
                case "params": return _Field.PARAMS;
                case "exceptions": return _Field.EXCEPTIONS;
            }
            return null;
        }
    }

    public static PStructDescriptorProvider<ServiceMethod,_Field> provider() {
        return new _Provider();
    }

    @Override
    public PStructDescriptor<ServiceMethod,_Field> descriptor() {
        return kDescriptor;
    }

    public static final PStructDescriptor<ServiceMethod,_Field> kDescriptor;

    private static class _Descriptor
            extends PStructDescriptor<ServiceMethod,_Field> {
        public _Descriptor() {
            super(null, "model", "ServiceMethod", new _Factory(), false, false);
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

    private final static class _Provider extends PStructDescriptorProvider<ServiceMethod,_Field> {
        @Override
        public PStructDescriptor<ServiceMethod,_Field> descriptor() {
            return kDescriptor;
        }
    }

    private final static class _Factory
            extends PMessageBuilderFactory<ServiceMethod> {
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
            extends PMessageBuilder<ServiceMethod> {
        private BitSet optionals;

        private String mComment;
        private boolean mOneWay;
        private String mReturnType;
        private String mName;
        private List<ThriftField> mParams;
        private List<ThriftField> mExceptions;


        public _Builder() {
            optionals = new BitSet(6);
            mOneWay = kDefaultOneWay;
            mParams = new LinkedList<>();
            mExceptions = new LinkedList<>();
        }

        public _Builder(ServiceMethod base) {
            this();

            if (base.hasComment()) {
                optionals.set(0);
                mComment = base.mComment;
            }
            optionals.set(1);
            mOneWay = base.mOneWay;
            if (base.hasReturnType()) {
                optionals.set(2);
                mReturnType = base.mReturnType;
            }
            if (base.hasName()) {
                optionals.set(3);
                mName = base.mName;
            }
            if (base.numParams() > 0) {
                optionals.set(4);
                mParams.addAll(base.mParams);
            }
            if (base.numExceptions() > 0) {
                optionals.set(5);
                mExceptions.addAll(base.mExceptions);
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
        public _Builder setOneWay(boolean value) {
            optionals.set(1);
            mOneWay = value;
            return this;
        }
        public _Builder clearOneWay() {
            optionals.set(1, false);
            mOneWay = kDefaultOneWay;
            return this;
        }
        public _Builder setReturnType(String value) {
            optionals.set(2);
            mReturnType = value;
            return this;
        }
        public _Builder clearReturnType() {
            optionals.set(2, false);
            mReturnType = null;
            return this;
        }
        public _Builder setName(String value) {
            optionals.set(3);
            mName = value;
            return this;
        }
        public _Builder clearName() {
            optionals.set(3, false);
            mName = null;
            return this;
        }
        public _Builder setParams(Collection<ThriftField> value) {
            optionals.set(4);
            mParams.clear();
            mParams.addAll(value);
            return this;
        }
        public _Builder addToParams(ThriftField... values) {
            optionals.set(4);
            for (ThriftField item : values) {
                mParams.add(item);
            }
            return this;
        }

        public _Builder clearParams() {
            optionals.set(4, false);
            mParams.clear();
            return this;
        }
        public _Builder setExceptions(Collection<ThriftField> value) {
            optionals.set(5);
            mExceptions.clear();
            mExceptions.addAll(value);
            return this;
        }
        public _Builder addToExceptions(ThriftField... values) {
            optionals.set(5);
            for (ThriftField item : values) {
                mExceptions.add(item);
            }
            return this;
        }

        public _Builder clearExceptions() {
            optionals.set(5, false);
            mExceptions.clear();
            return this;
        }
        @Override
        public _Builder set(int key, Object value) {
            if (value == null) return clear(key);
            switch (key) {
                case 1: setComment((String) value); break;
                case 2: setOneWay((boolean) value); break;
                case 3: setReturnType((String) value); break;
                case 4: setName((String) value); break;
                case 5: setParams((List<ThriftField>) value); break;
                case 6: setExceptions((List<ThriftField>) value); break;
            }
            return this;
        }

        @Override
        public _Builder clear(int key) {
            switch (key) {
                case 1: clearComment(); break;
                case 2: clearOneWay(); break;
                case 3: clearReturnType(); break;
                case 4: clearName(); break;
                case 5: clearParams(); break;
                case 6: clearExceptions(); break;
            }
            return this;
        }

        @Override
        public boolean isValid() {
            return optionals.get(3);
        }

        @Override
        public ServiceMethod build() {
            return new ServiceMethod(this);
        }
    }
}
