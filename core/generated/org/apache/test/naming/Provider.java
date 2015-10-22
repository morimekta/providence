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
import org.apache.thrift.j2.descriptor.TStructDescriptor;
import org.apache.thrift.j2.descriptor.TStructDescriptorProvider;
import org.apache.thrift.j2.descriptor.TValueProvider;
import org.apache.thrift.j2.util.TTypeUtils;

public class Provider
        implements TMessage<Provider>, Serializable, Parcelable {
    private final Provider mProvider;
    private final Factory mFactory;
    private final Builder mBuilder;
    private final Fields mFields;

    private Provider(_Builder builder) {
        mProvider = builder.mProvider;
        mFactory = builder.mFactory;
        mBuilder = builder.mBuilder;
        mFields = builder.mFields;
    }

    public boolean hasProvider() {
        return mProvider != null;
    }

    public Provider getProvider() {
        return mProvider;
    }

    public boolean hasFactory() {
        return mFactory != null;
    }

    public Factory getFactory() {
        return mFactory;
    }

    public boolean hasBuilder() {
        return mBuilder != null;
    }

    public Builder getBuilder() {
        return mBuilder;
    }

    public boolean hasFields() {
        return mFields != null;
    }

    public Fields getFields() {
        return mFields;
    }

    @Override
    public boolean has(int key) {
        switch(key) {
            case 1: return hasProvider();
            case 2: return hasFactory();
            case 3: return hasBuilder();
            case 4: return hasFields();
            default: return false;
        }
    }

    @Override
    public int num(int key) {
        switch(key) {
            case 1: return hasProvider() ? 1 : 0;
            case 2: return hasFactory() ? 1 : 0;
            case 3: return hasBuilder() ? 1 : 0;
            case 4: return hasFields() ? 1 : 0;
            default: return 0;
        }
    }

    @Override
    public Object get(int key) {
        switch(key) {
            case 1: return getProvider();
            case 2: return getFactory();
            case 3: return getBuilder();
            case 4: return getFields();
            default: return null;
        }
    }

    @Override
    public boolean isCompact() {
        return false;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || !(o instanceof Provider)) return false;
        Provider other = (Provider) o;
        return TTypeUtils.equals(mProvider, other.mProvider) &&
               TTypeUtils.equals(mFactory, other.mFactory) &&
               TTypeUtils.equals(mBuilder, other.mBuilder) &&
               TTypeUtils.equals(mFields, other.mFields);
    }

    @Override
    public int hashCode() {
        return Provider.class.hashCode() +
               TTypeUtils.hashCode(mProvider) +
               TTypeUtils.hashCode(mFactory) +
               TTypeUtils.hashCode(mBuilder) +
               TTypeUtils.hashCode(mFields);
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
        PROVIDER(1, false, "Provider", Provider.provider(), null),
        FACTORY(2, false, "Factory", Factory.provider(), null),
        BUILDER(3, false, "Builder", Builder.provider(), null),
        FIELDS(4, false, "Fields", Fields.provider(), null),
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
            builder.append(Provider.class.getSimpleName())
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
                case 1: return _Field.PROVIDER;
                case 2: return _Field.FACTORY;
                case 3: return _Field.BUILDER;
                case 4: return _Field.FIELDS;
                default: return null;
            }
        }

        public static _Field forName(String name) {
            switch (name) {
                case "Provider": return _Field.PROVIDER;
                case "Factory": return _Field.FACTORY;
                case "Builder": return _Field.BUILDER;
                case "Fields": return _Field.FIELDS;
            }
            return null;
        }
    }

    @Override
    public TStructDescriptor<Provider> getDescriptor() {
        return sDescriptor;
    }

    public static TStructDescriptor<Provider> descriptor() {
        return sDescriptor;
    }

    public static final TStructDescriptor<Provider> sDescriptor;

    private final static class _Factory
            extends TMessageBuilderFactory<Provider> {
        @Override
        public _Builder builder() {
            return new _Builder();
        }
    }

    static {
        sDescriptor = new TStructDescriptor<>(null, "naming", "Provider", _Field.values(), new _Factory(), false);
    }

    public static TStructDescriptorProvider<Provider> provider() {
        return new TStructDescriptorProvider<Provider>() {
            @Override
            public TStructDescriptor<Provider> descriptor() {
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
        if (hasProvider()) {
            dest.writeInt(1);
            dest.writeParcelable(mProvider, 0);
        }
        if (hasFactory()) {
            dest.writeInt(2);
            dest.writeParcelable(mFactory, 0);
        }
        if (hasBuilder()) {
            dest.writeInt(3);
            dest.writeParcelable(mBuilder, 0);
        }
        if (hasFields()) {
            dest.writeInt(4);
            dest.writeInt(mFields.getValue());
        }
        dest.writeInt(0);
    }

    public static final Parcelable.Creator<Provider> CREATOR = new Parcelable.Creator<Provider>() {
        @Override
        public Provider createFromParcel(Parcel source) {
            _Builder builder = new _Builder();
            loop: while (source.dataAvail() > 0) {
                int field = source.readInt();
                switch (field) {
                    case 0: break loop;
                    case 1:
                        builder.setProvider((Provider) source.readParcelable(Provider.class.getClassLoader()));
                        break;
                    case 2:
                        builder.setFactory((Factory) source.readParcelable(Factory.class.getClassLoader()));
                        break;
                    case 3:
                        builder.setBuilder((Builder) source.readParcelable(Builder.class.getClassLoader()));
                        break;
                    case 4:
                        builder.setFields(Fields.forValue(source.readInt()));
                        break;
                    default: throw new IllegalArgumentException("Unknown field ID: " + field);
                }
            }

            return builder.build();
        }

        @Override
        public Provider[] newArray(int size) {
            return new Provider[size];
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
            extends TMessageBuilder<Provider> {
        private Provider mProvider;
        private Factory mFactory;
        private Builder mBuilder;
        private Fields mFields;

        public _Builder() {
        }

        public _Builder(Provider base) {
            this();

            mProvider = base.mProvider;
            mFactory = base.mFactory;
            mBuilder = base.mBuilder;
            mFields = base.mFields;
        }

        public _Builder setProvider(Provider value) {
            mProvider = value;
            return this;
        }

        public _Builder clearProvider() {
            mProvider = null;
            return this;
        }

        public _Builder setFactory(Factory value) {
            mFactory = value;
            return this;
        }

        public _Builder clearFactory() {
            mFactory = null;
            return this;
        }

        public _Builder setBuilder(Builder value) {
            mBuilder = value;
            return this;
        }

        public _Builder clearBuilder() {
            mBuilder = null;
            return this;
        }

        public _Builder setFields(Fields value) {
            mFields = value;
            return this;
        }

        public _Builder clearFields() {
            mFields = null;
            return this;
        }

        @Override
        public _Builder set(int key, Object value) {
            switch (key) {
                case 1: setProvider((Provider) value); break;
                case 2: setFactory((Factory) value); break;
                case 3: setBuilder((Builder) value); break;
                case 4: setFields((Fields) value); break;
            }
            return this;
        }

        @Override
        public boolean isValid() {
            return true;
        }

        @Override
        public Provider build() {
            return new Provider(this);
        }
    }
}
