package org.apache.test.compact;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;

import org.apache.thrift.j2.TMessage;
import org.apache.thrift.j2.TMessageBuilder;
import org.apache.thrift.j2.TMessageBuilderFactory;
import org.apache.thrift.j2.TType;
import org.apache.thrift.j2.descriptor.TDescriptor;
import org.apache.thrift.j2.descriptor.TDescriptorProvider;
import org.apache.thrift.j2.descriptor.TField;
import org.apache.thrift.j2.descriptor.TPrimitive;
import org.apache.thrift.j2.descriptor.TStructDescriptor;
import org.apache.thrift.j2.descriptor.TStructDescriptorProvider;
import org.apache.thrift.j2.descriptor.TValueProvider;
import org.apache.thrift.j2.util.TTypeUtils;

/** @compact */
public class Category
        implements TMessage<Category>, Serializable, Parcelable {
    private final static int kDefaultId = 0;

    private final String mName;
    private final Integer mId;
    private final String mLabel;

    private Category(Builder builder) {
        mName = builder.mName;
        mId = builder.mId;
        mLabel = builder.mLabel;
    }

    public boolean hasName() {
        return mName != null;
    }

    public String getName() {
        return mName;
    }

    public boolean hasId() {
        return mId != null;
    }

    public int getId() {
        return hasId() ? mId : kDefaultId;
    }

    public boolean hasLabel() {
        return mLabel != null;
    }

    public String getLabel() {
        return mLabel;
    }

    @Override
    public boolean has(int key) {
        switch(key) {
            case 1: return hasName();
            case 2: return hasId();
            case 3: return hasLabel();
            default: return false;
        }
    }

    @Override
    public int num(int key) {
        switch(key) {
            case 1: return hasName() ? 1 : 0;
            case 2: return hasId() ? 1 : 0;
            case 3: return hasLabel() ? 1 : 0;
            default: return 0;
        }
    }

    @Override
    public Object get(int key) {
        switch(key) {
            case 1: return getName();
            case 2: return getId();
            case 3: return getLabel();
            default: return null;
        }
    }

    @Override
    public boolean isCompact() {
        boolean missing = false;
        if (hasName()) {
            if (missing) return false;
        } else {
            missing = true;
        }
        if (hasId()) {
            if (missing) return false;
        } else {
            missing = true;
        }
        if (hasLabel()) {
            if (missing) return false;
        } else {
            missing = true;
        }
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || !(o instanceof Category)) return false;
        Category other = (Category) o;
        return TTypeUtils.equals(mName, other.mName) &&
               TTypeUtils.equals(mId, other.mId) &&
               TTypeUtils.equals(mLabel, other.mLabel);
    }

    @Override
    public int hashCode() {
        return Category.class.hashCode() +
               TTypeUtils.hashCode(mName) +
               TTypeUtils.hashCode(mId) +
               TTypeUtils.hashCode(mLabel);
    }

    @Override
    public String toString() {
        return getDescriptor().getQualifiedName(null) + TTypeUtils.toString(this);
    }

    @Override
    public boolean isValid() {
        return mName != null &&
               mId != null;
    }

    public enum Field implements TField {
        NAME(1, true, "name", TPrimitive.STRING.provider(), null),
        ID(2, true, "id", TPrimitive.I32.provider(), null),
        LABEL(3, false, "label", TPrimitive.STRING.provider(), null),
        ;

        private final int mKey;
        private final boolean mRequired;
        private final String mName;
        private final TDescriptorProvider<?> mTypeProvider;
        private final TValueProvider<?> mDefaultValue;

        Field(int key, boolean required, String name, TDescriptorProvider<?> typeProvider, TValueProvider<?> defaultValue) {
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
            builder.append(Category.class.getSimpleName())
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

        public static Field forKey(int key) {
            switch (key) {
                case 1: return Category.Field.NAME;
                case 2: return Category.Field.ID;
                case 3: return Category.Field.LABEL;
                default: return null;
            }
        }

        public static Field forName(String name) {
            switch (name) {
                case "name": return Category.Field.NAME;
                case "id": return Category.Field.ID;
                case "label": return Category.Field.LABEL;
            }
            return null;
        }
    }

    @Override
    public TStructDescriptor<Category> getDescriptor() {
        return sDescriptor;
    }

    public static TStructDescriptor<Category> descriptor() {
        return sDescriptor;
    }

    public static final TStructDescriptor<Category> sDescriptor;

    private final static class Factory
            extends TMessageBuilderFactory<Category> {
        @Override
        public Category.Builder builder() {
            return new Category.Builder();
        }
    }

    static {
        sDescriptor = new TStructDescriptor<>(null, "compact", "Category", Category.Field.values(), new Factory(), true);
    }

    public static TStructDescriptorProvider<Category> provider() {
        return new TStructDescriptorProvider<Category>() {
            @Override
            public TStructDescriptor<Category> descriptor() {
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
        if (hasName()) {
            dest.writeInt(1);
            dest.writeString(mName);
        }
        if (hasId()) {
            dest.writeInt(2);
            dest.writeInt(mId);
        }
        if (hasLabel()) {
            dest.writeInt(3);
            dest.writeString(mLabel);
        }
        dest.writeInt(0);
    }

    public static final Parcelable.Creator<Category> CREATOR = new Parcelable.Creator<Category>() {
        @Override
        public Category createFromParcel(Parcel source) {
            Category.Builder builder = new Category.Builder();
            loop: while (source.dataAvail() > 0) {
                int field = source.readInt();
                switch (field) {
                    case 0: break loop;
                    case 1:
                        builder.setName(source.readString());
                        break;
                    case 2:
                        builder.setId(source.readInt());
                        break;
                    case 3:
                        builder.setLabel(source.readString());
                        break;
                    default: throw new IllegalArgumentException("Unknown field ID: " + field);
                }
            }

            return builder.build();
        }

        @Override
        public Category[] newArray(int size) {
            return new Category[size];
        }
    };

    @Override
    public Category.Builder mutate() {
        return new Category.Builder(this);
    }

    public static Category.Builder builder() {
        return new Category.Builder();
    }

    public static class Builder
            extends TMessageBuilder<Category> {
        private String mName;
        private Integer mId;
        private String mLabel;

        public Builder() {
        }

        public Builder(Category base) {
            this();

            mName = base.mName;
            mId = base.mId;
            mLabel = base.mLabel;
        }

        public Builder setName(String value) {
            mName = value;
            return this;
        }

        public Builder clearName() {
            mName = null;
            return this;
        }

        public Builder setId(int value) {
            mId = value;
            return this;
        }

        public Builder clearId() {
            mId = null;
            return this;
        }

        public Builder setLabel(String value) {
            mLabel = value;
            return this;
        }

        public Builder clearLabel() {
            mLabel = null;
            return this;
        }

        @Override
        public Builder set(int key, Object value) {
            switch (key) {
                case 1: setName((String) value); break;
                case 2: setId((int) value); break;
                case 3: setLabel((String) value); break;
            }
            return this;
        }

        @Override
        public boolean isValid() {
            return mName != null &&
                   mId != null;
        }

        @Override
        public Category build() {
            return new Category(this);
        }
    }
}
