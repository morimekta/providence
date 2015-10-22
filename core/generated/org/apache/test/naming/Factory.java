package org.apache.test.naming;

import java.io.Serializable;

import android.os.Parcel;
import android.os.Parcelable;

import org.apache.thrift.j2.TMessage;
import org.apache.thrift.j2.TMessageBuilder;
import org.apache.thrift.j2.TMessageBuilderFactory;
import org.apache.thrift.j2.TType;
import org.apache.thrift.j2.descriptor.TDescriptor;
import org.apache.thrift.j2.descriptor.TDescriptorProvider;
import org.apache.thrift.j2.descriptor.TField;
import org.apache.thrift.j2.descriptor.TUnionDescriptor;
import org.apache.thrift.j2.descriptor.TUnionDescriptorProvider;
import org.apache.thrift.j2.descriptor.TValueProvider;
import org.apache.thrift.j2.util.TTypeUtils;

public class Factory
        implements TMessage<Factory>, Serializable, Parcelable {
    private final Provider mFactory;
    private final _Field tUnionField;


    private Factory(_Builder builder) {
        mFactory = builder.mFactory;

        tUnionField = builder.tUnionField;
    }

    public boolean hasFactory() {
        return mFactory != null;
    }

    public Provider getFactory() {
        return mFactory;
    }

    public _Field unionField() {
        return tUnionField;
    }

    @Override
    public boolean has(int key) {
        switch(key) {
            case 1: return hasFactory();
            default: return false;
        }
    }

    @Override
    public int num(int key) {
        switch(key) {
            case 1: return hasFactory() ? 1 : 0;
            default: return 0;
        }
    }

    @Override
    public Object get(int key) {
        switch(key) {
            case 1: return getFactory();
            default: return null;
        }
    }

    @Override
    public boolean isCompact() {
        return false;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || !(o instanceof Factory)) return false;
        Factory other = (Factory) o;
        return TTypeUtils.equals(mFactory, other.mFactory);
    }

    @Override
    public int hashCode() {
        return Factory.class.hashCode() +
               TTypeUtils.hashCode(mFactory);
    }

    @Override
    public String toString() {
        return getDescriptor().getQualifiedName(null) + TTypeUtils.toString(this);
    }

    @Override
    public boolean isValid() {
        return (mFactory != null ? 1 : 0) == 1;
    }

    public enum _Field implements TField {
        FACTORY(1, false, "Factory", Provider.provider(), null),
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
            builder.append(Factory.class.getSimpleName())
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
                case 1: return _Field.FACTORY;
                default: return null;
            }
        }

        public static _Field forName(String name) {
            switch (name) {
                case "Factory": return _Field.FACTORY;
            }
            return null;
        }
    }

    @Override
    public TUnionDescriptor<Factory> getDescriptor() {
        return sDescriptor;
    }

    public static TUnionDescriptor<Factory> descriptor() {
        return sDescriptor;
    }

    public static final TUnionDescriptor<Factory> sDescriptor;

    private final static class _Factory
            extends TMessageBuilderFactory<Factory> {
        @Override
        public _Builder builder() {
            return new _Builder();
        }
    }

    static {
        sDescriptor = new TUnionDescriptor<>(null, "naming", "Factory", _Field.values(), new _Factory());
    }

    public static TUnionDescriptorProvider<Factory> provider() {
        return new TUnionDescriptorProvider<Factory>() {
            @Override
            public TUnionDescriptor<Factory> descriptor() {
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
        if (hasFactory()) {
            dest.writeInt(1);
            dest.writeParcelable(mFactory, 0);
        }
        dest.writeInt(0);
    }

    public static final Parcelable.Creator<Factory> CREATOR = new Parcelable.Creator<Factory>() {
        @Override
        public Factory createFromParcel(Parcel source) {
            _Builder builder = new _Builder();
            loop: while (source.dataAvail() > 0) {
                int field = source.readInt();
                switch (field) {
                    case 0: break loop;
                    case 1:
                        builder.setFactory((Provider) source.readParcelable(Provider.class.getClassLoader()));
                        break;
                    default: throw new IllegalArgumentException("Unknown field ID: " + field);
                }
            }

            return builder.build();
        }

        @Override
        public Factory[] newArray(int size) {
            return new Factory[size];
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
            extends TMessageBuilder<Factory> {
        private Provider mFactory;
        private _Field tUnionField;


        public _Builder() {
        }

        public _Builder(Factory base) {
            this();

            mFactory = base.mFactory;

            tUnionField = base.tUnionField;
        }

        public _Builder setFactory(Provider value) {
            tUnionField = _Field.FACTORY;
            mFactory = value;
            return this;
        }

        public _Builder clearFactory() {
            if (mFactory != null) tUnionField = null;
            mFactory = null;
            return this;
        }

        @Override
        public _Builder set(int key, Object value) {
            switch (key) {
                case 1: setFactory((Provider) value); break;
            }
            return this;
        }

        @Override
        public boolean isValid() {
            return (mFactory != null ? 1 : 0) == 1;
        }

        @Override
        public Factory build() {
            return new Factory(this);
        }
    }
}
