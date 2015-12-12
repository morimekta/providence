package org.apache.thrift.j2.model;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.thrift.j2.TMessage;
import org.apache.thrift.j2.TMessageBuilder;
import org.apache.thrift.j2.TMessageBuilderFactory;
import org.apache.thrift.j2.TType;
import org.apache.thrift.j2.descriptor.TDescriptor;
import org.apache.thrift.j2.descriptor.TDescriptorProvider;
import org.apache.thrift.j2.descriptor.TField;
import org.apache.thrift.j2.descriptor.TList;
import org.apache.thrift.j2.descriptor.TMap;
import org.apache.thrift.j2.descriptor.TPrimitive;
import org.apache.thrift.j2.descriptor.TStructDescriptor;
import org.apache.thrift.j2.descriptor.TStructDescriptorProvider;
import org.apache.thrift.j2.descriptor.TValueProvider;
import org.apache.thrift.j2.util.TTypeUtils;

/** <namespace>* <include>* <declataion>* */
@SuppressWarnings("unused")
public class ThriftDocument
        implements TMessage<ThriftDocument>, Serializable {
    private final String mComment;
    private final String mPackage;
    private final List<String> mIncludes;
    private final Map<String,String> mNamespaces;
    private final List<Declaration> mDecl;

    private ThriftDocument(_Builder builder) {
        mComment = builder.mComment;
        mPackage = builder.mPackage;
        mIncludes = Collections.unmodifiableList(new LinkedList<>(builder.mIncludes));
        mNamespaces = Collections.unmodifiableMap(new LinkedHashMap<>(builder.mNamespaces));
        mDecl = Collections.unmodifiableList(new LinkedList<>(builder.mDecl));
    }

    public boolean hasComment() {
        return mComment != null;
    }

    /** Must come before the first statement of the header. */
    public String getComment() {
        return mComment;
    }

    public boolean hasPackage() {
        return mPackage != null;
    }

    /** Deducted from filename in .thrift IDL files. */
    public String getPackage() {
        return mPackage;
    }

    public int numIncludes() {
        return mIncludes.size();
    }

    /** include "<package>.thrift" */
    public List<String> getIncludes() {
        return mIncludes;
    }

    public int numNamespaces() {
        return mNamespaces.size();
    }

    /** namespace <key> <value> */
    public Map<String,String> getNamespaces() {
        return mNamespaces;
    }

    public int numDecl() {
        return mDecl.size();
    }

    public List<Declaration> getDecl() {
        return mDecl;
    }

    @Override
    public boolean has(int key) {
        switch(key) {
            case 1: return hasComment();
            case 2: return hasPackage();
            case 3: return numIncludes() > 0;
            case 4: return numNamespaces() > 0;
            case 5: return numDecl() > 0;
            default: return false;
        }
    }

    @Override
    public int num(int key) {
        switch(key) {
            case 1: return hasComment() ? 1 : 0;
            case 2: return hasPackage() ? 1 : 0;
            case 3: return numIncludes();
            case 4: return numNamespaces();
            case 5: return numDecl();
            default: return 0;
        }
    }

    @Override
    public Object get(int key) {
        switch(key) {
            case 1: return getComment();
            case 2: return getPackage();
            case 3: return getIncludes();
            case 4: return getNamespaces();
            case 5: return getDecl();
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
        if (o == null || !(o instanceof ThriftDocument)) return false;
        ThriftDocument other = (ThriftDocument) o;
        return TTypeUtils.equals(mComment, other.mComment) &&
               TTypeUtils.equals(mPackage, other.mPackage) &&
               TTypeUtils.equals(mIncludes, other.mIncludes) &&
               TTypeUtils.equals(mNamespaces, other.mNamespaces) &&
               TTypeUtils.equals(mDecl, other.mDecl);
    }

    @Override
    public int hashCode() {
        return ThriftDocument.class.hashCode() +
               TTypeUtils.hashCode(_Field.COMMENT,mComment) +
               TTypeUtils.hashCode(_Field.PACKAGE,mPackage) +
               TTypeUtils.hashCode(_Field.INCLUDES,mIncludes) +
               TTypeUtils.hashCode(_Field.NAMESPACES,mNamespaces) +
               TTypeUtils.hashCode(_Field.DECL,mDecl);
    }

    @Override
    public String toString() {
        return descriptor().getQualifiedName(null) + TTypeUtils.toString(this);
    }

    @Override
    public boolean isValid() {
        return mPackage != null;
    }

    public enum _Field implements TField {
        COMMENT(1, false, "comment", TPrimitive.STRING.provider(), null),
        PACKAGE(2, true, "package", TPrimitive.STRING.provider(), null),
        INCLUDES(3, false, "includes", TList.provider(TPrimitive.STRING.provider()), null),
        NAMESPACES(4, false, "namespaces", TMap.provider(TPrimitive.STRING.provider(),TPrimitive.STRING.provider()), null),
        DECL(5, false, "decl", TList.provider(Declaration.provider()), null),
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
        public TType getType() { return getDescriptor().getType(); }

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
            builder.append(ThriftDocument.class.getSimpleName())
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
                case 1: return _Field.COMMENT;
                case 2: return _Field.PACKAGE;
                case 3: return _Field.INCLUDES;
                case 4: return _Field.NAMESPACES;
                case 5: return _Field.DECL;
                default: return null;
            }
        }

        public static _Field forName(String name) {
            switch (name) {
                case "comment": return _Field.COMMENT;
                case "package": return _Field.PACKAGE;
                case "includes": return _Field.INCLUDES;
                case "namespaces": return _Field.NAMESPACES;
                case "decl": return _Field.DECL;
            }
            return null;
        }
    }

    public static TStructDescriptorProvider<ThriftDocument,_Field> provider() {
        return new _Provider();
    }

    @Override
    public TStructDescriptor<ThriftDocument,_Field> descriptor() {
        return kDescriptor;
    }

    public static final TStructDescriptor<ThriftDocument,_Field> kDescriptor;

    private static class _Descriptor
            extends TStructDescriptor<ThriftDocument,_Field> {
        public _Descriptor() {
            super(null, "model", "ThriftDocument", new _Factory(), false, false);
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

    private final static class _Provider extends TStructDescriptorProvider<ThriftDocument,_Field> {
        @Override
        public TStructDescriptor<ThriftDocument,_Field> descriptor() {
            return kDescriptor;
        }
    }

    private final static class _Factory
            extends TMessageBuilderFactory<ThriftDocument> {
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
            extends TMessageBuilder<ThriftDocument> {
        private String mComment;
        private String mPackage;
        private List<String> mIncludes;
        private Map<String,String> mNamespaces;
        private List<Declaration> mDecl;

        public _Builder() {
            mIncludes = new LinkedList<>();
            mNamespaces = new LinkedHashMap<>();
            mDecl = new LinkedList<>();
        }

        public _Builder(ThriftDocument base) {
            this();

            mComment = base.mComment;
            mPackage = base.mPackage;
            mIncludes.addAll(base.mIncludes);
            mNamespaces.putAll(base.mNamespaces);
            mDecl.addAll(base.mDecl);
        }

        /** Must come before the first statement of the header. */
        public _Builder setComment(String value) {
            mComment = value;
            return this;
        }

        public _Builder clearComment() {
            mComment = null;
            return this;
        }

        /** Deducted from filename in .thrift IDL files. */
        public _Builder setPackage(String value) {
            mPackage = value;
            return this;
        }

        public _Builder clearPackage() {
            mPackage = null;
            return this;
        }

        /** include "<package>.thrift" */
        public _Builder setIncludes(Collection<String> value) {
            mIncludes.clear();
            mIncludes.addAll(value);
            return this;
        }

        /** include "<package>.thrift" */
        public _Builder addToIncludes(String... values) {
            for (String item : values) {
                mIncludes.add(item);
            }
            return this;
        }

        public _Builder clearIncludes() {
            mIncludes.clear();
            return this;
        }

        /** namespace <key> <value> */
        public _Builder setNamespaces(Map<String,String> value) {
            mNamespaces.clear();
            mNamespaces.putAll(value);
            return this;
        }

        /** namespace <key> <value> */
        public _Builder addToNamespaces(String key, String value) {
            mNamespaces.put(key, value);
            return this;
        }

        public _Builder clearNamespaces() {
            mNamespaces.clear();
            return this;
        }

        public _Builder setDecl(Collection<Declaration> value) {
            mDecl.clear();
            mDecl.addAll(value);
            return this;
        }

        public _Builder addToDecl(Declaration... values) {
            for (Declaration item : values) {
                mDecl.add(item);
            }
            return this;
        }

        public _Builder clearDecl() {
            mDecl.clear();
            return this;
        }

        @Override
        public _Builder set(int key, Object value) {
            switch (key) {
                case 1: setComment((String) value); break;
                case 2: setPackage((String) value); break;
                case 3: setIncludes((List<String>) value); break;
                case 4: setNamespaces((Map<String,String>) value); break;
                case 5: setDecl((List<Declaration>) value); break;
            }
            return this;
        }

        @Override
        public boolean isValid() {
            return mPackage != null;
        }

        @Override
        public ThriftDocument build() {
            return new ThriftDocument(this);
        }
    }
}
