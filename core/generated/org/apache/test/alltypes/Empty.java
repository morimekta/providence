package org.apache.test.alltypes;

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

public class Empty
        implements TMessage<Empty>, Serializable, Parcelable {

    private Empty(_Builder builder) {
    }

    @Override
    public boolean has(int key) {
        switch(key) {
            default: return false;
        }
    }

    @Override
    public int num(int key) {
        switch(key) {
            default: return 0;
        }
    }

    @Override
    public Object get(int key) {
        switch(key) {
            default: return null;
        }
    }

    @Override
    public boolean isCompact() {
        return false;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || !(o instanceof Empty)) return false;
        return true;
    }

    @Override
    public int hashCode() {
        return Empty.class.hashCode();
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
            builder.append(Empty.class.getSimpleName())
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
                default: return null;
            }
        }

        public static _Field forName(String name) {
            switch (name) {
            }
            return null;
        }
    }

    @Override
    public TStructDescriptor<Empty> getDescriptor() {
        return sDescriptor;
    }

    public static TStructDescriptor<Empty> descriptor() {
        return sDescriptor;
    }

    public static final TStructDescriptor<Empty> sDescriptor;

    private final static class _Factory
            extends TMessageBuilderFactory<Empty> {
        @Override
        public _Builder builder() {
            return new _Builder();
        }
    }

    static {
        sDescriptor = new TStructDescriptor<>(null, "alltypes", "Empty", _Field.values(), new _Factory(), false);
    }

    public static TStructDescriptorProvider<Empty> provider() {
        return new TStructDescriptorProvider<Empty>() {
            @Override
            public TStructDescriptor<Empty> descriptor() {
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
        dest.writeInt(0);
    }

    public static final Parcelable.Creator<Empty> CREATOR = new Parcelable.Creator<Empty>() {
        @Override
        public Empty createFromParcel(Parcel source) {
            _Builder builder = new _Builder();
            loop: while (source.dataAvail() > 0) {
                int field = source.readInt();
                switch (field) {
                    case 0: break loop;
                    default: throw new IllegalArgumentException("Unknown field ID: " + field);
                }
            }

            return builder.build();
        }

        @Override
        public Empty[] newArray(int size) {
            return new Empty[size];
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
            extends TMessageBuilder<Empty> {

        public _Builder() {
        }

        public _Builder(Empty base) {
            this();

        }

        @Override
        public _Builder set(int key, Object value) {
            switch (key) {
            }
            return this;
        }

        @Override
        public boolean isValid() {
            return true;
        }

        @Override
        public Empty build() {
            return new Empty(this);
        }
    }
}
