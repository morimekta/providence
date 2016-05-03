package net.morimekta.providence.serializer;

/**
 * Base exception thrown on non-declared exceptions on a service call, and
 * other server-side service call issues.
 */
@SuppressWarnings("unused")
public class ApplicationException
        extends net.morimekta.providence.PException
        implements net.morimekta.providence.PMessage<ApplicationException>, java.io.Serializable, Comparable<ApplicationException> {
    private final static long serialVersionUID = 6590039153455193300L;

    private final static int kDefaultId = 0;

    private final String mMessage;
    private final int mId;
    
    private volatile int tHashCode;

    private ApplicationException(_Builder builder) {
        super(builder.createMessage());

        mMessage = builder.mMessage;
        mId = builder.mId;
    }

    public boolean hasMessage() {
        return mMessage != null;
    }

    public String getMessage() {
        return mMessage;
    }

    public boolean hasId() {
        return true;
    }

    public int getId() {
        return mId;
    }

    @Override
    public boolean has(int key) {
        switch(key) {
            case 1: return hasMessage();
            case 2: return true;
            default: return false;
        }
    }

    @Override
    public int num(int key) {
        switch(key) {
            case 1: return hasMessage() ? 1 : 0;
            case 2: return 1;
            default: return 0;
        }
    }

    @Override
    public Object get(int key) {
        switch(key) {
            case 1: return getMessage();
            case 2: return getId();
            default: return null;
        }
    }

    @Override
    public boolean compact() {
        return false;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || !(o instanceof ApplicationException)) return false;
        ApplicationException other = (ApplicationException) o;
        return java.util.Objects.equals(mMessage, other.mMessage) &&
               java.util.Objects.equals(mId, other.mId);
    }

    @Override
    public int hashCode() {
        if (tHashCode == 0) {
            tHashCode = java.util.Objects.hash(
                    ApplicationException.class,
                    _Field.MESSAGE, mMessage,
                    _Field.ID, mId);
        }
        return tHashCode;
    }

    @Override
    public String toString() {
        return "service.ApplicationException" + asString();
    }

    @Override
    public String asString() {
        StringBuilder out = new StringBuilder();
        out.append("{");

        boolean first = true;
        if (hasMessage()) {
            first = false;
            out.append("message:");
            out.append('\"').append(mMessage).append('\"');
        }
        if (hasId()) {
            if (!first) out.append(',');
            first = false;
            out.append("id:");
            out.append(Integer.toString(mId));
        }
        out.append('}');
        return out.toString();
    }

    @Override
    public int compareTo(ApplicationException other) {
        int c;

        c = Boolean.compare(mMessage != null, other.mMessage != null);
        if (c != 0) return c;
        if (mMessage != null) {
            c = mMessage.compareTo(other.mMessage);
            if (c != 0) return c;
        }

        c = Integer.compare(mId, other.mId);
        if (c != 0) return c;

        return 0;
    }

    public enum _Field implements net.morimekta.providence.descriptor.PField {
        MESSAGE(1, net.morimekta.providence.descriptor.PRequirement.DEFAULT, "message", net.morimekta.providence.descriptor.PPrimitive.STRING.provider(), null),
        ID(2, net.morimekta.providence.descriptor.PRequirement.DEFAULT, "id", net.morimekta.providence.descriptor.PPrimitive.I32.provider(), new net.morimekta.providence.descriptor.PDefaultValueProvider<>(kDefaultId)),
        ;

        private final int mKey;
        private final net.morimekta.providence.descriptor.PRequirement mRequired;
        private final String mName;
        private final net.morimekta.providence.descriptor.PDescriptorProvider mTypeProvider;
        private final net.morimekta.providence.descriptor.PValueProvider<?> mDefaultValue;

        _Field(int key, net.morimekta.providence.descriptor.PRequirement required, String name, net.morimekta.providence.descriptor.PDescriptorProvider typeProvider, net.morimekta.providence.descriptor.PValueProvider<?> defaultValue) {
            mKey = key;
            mRequired = required;
            mName = name;
            mTypeProvider = typeProvider;
            mDefaultValue = defaultValue;
        }

        @Override
        public int getKey() { return mKey; }

        @Override
        public net.morimekta.providence.descriptor.PRequirement getRequirement() { return mRequired; }

        @Override
        public net.morimekta.providence.PType getType() { return getDescriptor().getType(); }

        @Override
        public net.morimekta.providence.descriptor.PDescriptor getDescriptor() { return mTypeProvider.descriptor(); }

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
            builder.append("ApplicationException._Field(")
                   .append(mKey)
                   .append(": ");
            if (mRequired != net.morimekta.providence.descriptor.PRequirement.DEFAULT) {
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
                case 1: return _Field.MESSAGE;
                case 2: return _Field.ID;
                default: return null;
            }
        }

        public static _Field forName(String name) {
            switch (name) {
                case "message": return _Field.MESSAGE;
                case "id": return _Field.ID;
            }
            return null;
        }
    }

    public static net.morimekta.providence.descriptor.PExceptionDescriptorProvider<ApplicationException,_Field> provider() {
        return new _Provider();
    }

    @Override
    public net.morimekta.providence.descriptor.PExceptionDescriptor<ApplicationException,_Field> descriptor() {
        return kDescriptor;
    }

    public static final net.morimekta.providence.descriptor.PExceptionDescriptor<ApplicationException,_Field> kDescriptor;

    private static class _Descriptor
            extends net.morimekta.providence.descriptor.PExceptionDescriptor<ApplicationException,_Field> {
        public _Descriptor() {
            super("service", "ApplicationException", new _Factory(), true);
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

    private final static class _Provider extends net.morimekta.providence.descriptor.PExceptionDescriptorProvider<ApplicationException,_Field> {
        @Override
        public net.morimekta.providence.descriptor.PExceptionDescriptor<ApplicationException,_Field> descriptor() {
            return kDescriptor;
        }
    }

    private final static class _Factory
            extends net.morimekta.providence.PMessageBuilderFactory<ApplicationException> {
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
            extends net.morimekta.providence.PMessageBuilder<ApplicationException> {
        private java.util.BitSet optionals;

        private String mMessage;
        private int mId;


        public _Builder() {
            optionals = new java.util.BitSet(2);
            mId = kDefaultId;
        }

        public _Builder(ApplicationException base) {
            this();

            if (base.hasMessage()) {
                optionals.set(0);
                mMessage = base.mMessage;
            }
            optionals.set(1);
            mId = base.mId;
        }

        public _Builder setMessage(String value) {
            optionals.set(0);
            mMessage = value;
            return this;
        }
        public boolean isSetMessage() {
            return optionals.get(0);
        }
        public _Builder clearMessage() {
            optionals.set(0, false);
            mMessage = null;
            return this;
        }
        public _Builder setId(int value) {
            optionals.set(1);
            mId = value;
            return this;
        }
        public boolean isSetId() {
            return optionals.get(1);
        }
        public _Builder clearId() {
            optionals.set(1, false);
            mId = kDefaultId;
            return this;
        }
        @Override
        public _Builder set(int key, Object value) {
            if (value == null) return clear(key);
            switch (key) {
                case 1: setMessage((String) value); break;
                case 2: setId((int) value); break;
            }
            return this;
        }

        @Override
        public _Builder addTo(int key, Object value) {
            switch (key) {
                default: break;
            }
            return this;
        }

        @Override
        public _Builder clear(int key) {
            switch (key) {
                case 1: clearMessage(); break;
                case 2: clearId(); break;
            }
            return this;
        }

        @Override
        public boolean isValid() {
            return true;
        }

        protected String createMessage() {
            StringBuilder builder = new StringBuilder();
            builder.append('{');
            boolean first = true;
            if (mMessage != null) {
                if (first) first = false;
                else builder.append(',');
                builder.append("message:")
                       .append(mMessage);
            }
            if (first) first = false;
            else builder.append(',');
            builder.append("id:")
                   .append(mId);
            builder.append('}');
            return builder.toString();
        }

        @Override
        public ApplicationException build() {
            return new ApplicationException(this);
        }
    }
}
