package org.apache.test.naming;

import java.io.Serializable;

import android.os.Parcel;
import android.os.Parcelable;

import org.apache.thrift.j2.TException;
import org.apache.thrift.j2.TMessage;
import org.apache.thrift.j2.TMessageBuilder;
import org.apache.thrift.j2.TMessageBuilderFactory;
import org.apache.thrift.j2.TType;
import org.apache.thrift.j2.descriptor.TDescriptor;
import org.apache.thrift.j2.descriptor.TDescriptorProvider;
import org.apache.thrift.j2.descriptor.TExceptionDescriptor;
import org.apache.thrift.j2.descriptor.TExceptionDescriptorProvider;
import org.apache.thrift.j2.descriptor.TField;
import org.apache.thrift.j2.descriptor.TValueProvider;
import org.apache.thrift.j2.util.TTypeUtils;

public class Builder
        extends TException
        implements TMessage<Builder>, Serializable, Parcelable {
    private final static long serialVersionUID = 642363033467540783L;

    private final Builder mBuilder;

    private Builder(_Builder builder) {
        super(builder.createMessage());

        mBuilder = builder.mBuilder;
    }

    public boolean hasBuilder() {
        return mBuilder != null;
    }

    public Builder getBuilder() {
        return mBuilder;
    }

    @Override
    public boolean has(int key) {
        switch(key) {
            case 1: return hasBuilder();
            default: return false;
        }
    }

    @Override
    public int num(int key) {
        switch(key) {
            case 1: return hasBuilder() ? 1 : 0;
            default: return 0;
        }
    }

    @Override
    public Object get(int key) {
        switch(key) {
            case 1: return getBuilder();
            default: return null;
        }
    }

    @Override
    public boolean isCompact() {
        return false;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || !(o instanceof Builder)) return false;
        Builder other = (Builder) o;
        return TTypeUtils.equals(mBuilder, other.mBuilder);
    }

    @Override
    public int hashCode() {
        return Builder.class.hashCode() +
               TTypeUtils.hashCode(mBuilder);
    }

    @Override
    public String toString() {
        return getDescriptor().getQualifiedName(null) + TTypeUtils.toString(this);
    }

    @Override
    public boolean isValid() {
        return true;
    }

    public enum _Field implements TField {
        BUILDER(1, false, "Builder", Builder.provider(), null),
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
            builder.append(Builder.class.getSimpleName())
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
                case 1: return _Field.BUILDER;
                default: return null;
            }
        }

        public static _Field forName(String name) {
            switch (name) {
                case "Builder": return _Field.BUILDER;
            }
            return null;
        }
    }

    @Override
    public TExceptionDescriptor<Builder> getDescriptor() {
        return sDescriptor;
    }

    public static TExceptionDescriptor<Builder> descriptor() {
        return sDescriptor;
    }

    public static final TExceptionDescriptor<Builder> sDescriptor;

    private final static class _Factory
            extends TMessageBuilderFactory<Builder> {
        @Override
        public _Builder builder() {
            return new _Builder();
        }
    }

    static {
        sDescriptor = new TExceptionDescriptor<>(null, "naming", "Builder", _Field.values(), new _Factory());
    }

    public static TExceptionDescriptorProvider<Builder> provider() {
        return new TExceptionDescriptorProvider<Builder>() {
            @Override
            public TExceptionDescriptor<Builder> descriptor() {
                return sDescriptor;
            }
        };
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (hasBuilder()) {
            dest.writeInt(1);
            dest.writeParcelable(mBuilder, 0);
        }
        dest.writeInt(0);
    }

    public static final Parcelable.Creator<Builder> CREATOR = new Parcelable.Creator<Builder>() {
        @Override
        public Builder createFromParcel(Parcel source) {
            _Builder builder = new _Builder();
            loop: while (source.dataAvail() > 0) {
                int field = source.readInt();
                switch (field) {
                    case 0: break loop;
                    case 1:
                        builder.setBuilder((Builder) source.readParcelable(Builder.class.getClassLoader()));
                        break;
                    default: throw new IllegalArgumentException("Unknown field ID: " + field);
                }
            }

            return builder.build();
        }

        @Override
        public Builder[] newArray(int size) {
            return new Builder[size];
        }
    };

    @Override
    public _Builder mutate() {
        return new _Builder(this);
    }

    public static _Builder builder() {
        return new _Builder();
    }

    public static class _Builder
            extends TMessageBuilder<Builder> {
        private Builder mBuilder;

        public _Builder() {
        }

        public _Builder(Builder base) {
            this();

            mBuilder = base.mBuilder;
        }

        public _Builder setBuilder(Builder value) {
            mBuilder = value;
            return this;
        }

        public _Builder clearBuilder() {
            mBuilder = null;
            return this;
        }

        @Override
        public _Builder set(int key, Object value) {
            switch (key) {
                case 1: setBuilder((Builder) value); break;
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
            if (mBuilder != null) {
                if (first) first = false;
                else builder.append(',');
                builder.append("Builder:");
                builder.append(TTypeUtils.toString(mBuilder));
            }
            builder.append('}');
            return builder.toString();
        }

        @Override
        public Builder build() {
            return new Builder(this);
        }
    }
}
