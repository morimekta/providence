package net.morimekta.providence.model;

import java.io.Serializable;
import java.util.BitSet;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import net.morimekta.providence.PMessage;
import net.morimekta.providence.PMessageBuilder;
import net.morimekta.providence.PMessageBuilderFactory;
import net.morimekta.providence.PType;
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

/** <namespace>* <include>* <declataion>* */
@SuppressWarnings("unused")
public class ThriftDocument
        implements PMessage<ThriftDocument>, Serializable, Comparable<ThriftDocument> {
    private final static long serialVersionUID = -5731994850994905187L;

    private final String mComment;
    private final String mPackage;
    private final List<String> mIncludes;
    private final Map<String,String> mNamespaces;
    private final List<Declaration> mDecl;
    
    private volatile int tHashCode;

    private ThriftDocument(_Builder builder) {
        mComment = builder.mComment;
        mPackage = builder.mPackage;
        mIncludes = Collections.unmodifiableList(new LinkedList<>(builder.mIncludes));
        mNamespaces = Collections.unmodifiableMap(new LinkedHashMap<>(builder.mNamespaces));
        mDecl = Collections.unmodifiableList(new LinkedList<>(builder.mDecl));
    }

    public ThriftDocument(String pComment,
                          String pPackage,
                          List<String> pIncludes,
                          Map<String,String> pNamespaces,
                          List<Declaration> pDecl) {
        mComment = pComment;
        mPackage = pPackage;
        mIncludes = Collections.unmodifiableList(new LinkedList<>(pIncludes));
        mNamespaces = Collections.unmodifiableMap(new LinkedHashMap<>(pNamespaces));
        mDecl = Collections.unmodifiableList(new LinkedList<>(pDecl));
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
        return mIncludes != null ? mIncludes.size() : 0;
    }

    /** include "<package>.thrift" */
    public List<String> getIncludes() {
        return mIncludes;
    }

    public int numNamespaces() {
        return mNamespaces != null ? mNamespaces.size() : 0;
    }

    /** namespace <key> <value> */
    public Map<String,String> getNamespaces() {
        return mNamespaces;
    }

    public int numDecl() {
        return mDecl != null ? mDecl.size() : 0;
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
        return Objects.equals(mComment, other.mComment) &&
               Objects.equals(mPackage, other.mPackage) &&
               PTypeUtils.equals(mIncludes, other.mIncludes) &&
               PTypeUtils.equals(mNamespaces, other.mNamespaces) &&
               PTypeUtils.equals(mDecl, other.mDecl);
    }

    @Override
    public int hashCode() {
        if (tHashCode == 0) {
            tHashCode = Objects.hash(
                    ThriftDocument.class,
                    _Field.COMMENT, mComment,
                    _Field.PACKAGE, mPackage,
                    _Field.INCLUDES, PTypeUtils.hashCode(mIncludes),
                    _Field.NAMESPACES, PTypeUtils.hashCode(mNamespaces),
                    _Field.DECL, PTypeUtils.hashCode(mDecl));
        }
        return tHashCode;
    }

    @Override
    public String toString() {
        return "model.ThriftDocument" + asString();
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
        if (hasPackage()) {
            if (!first) out.append(',');
            first = false;
            out.append("package:");
            out.append('\"').append(mPackage).append('\"');
        }
        if (numIncludes() > 0) {
            if (!first) out.append(',');
            first = false;
            out.append("includes:");
            out.append(PTypeUtils.toString(mIncludes));
        }
        if (numNamespaces() > 0) {
            if (!first) out.append(',');
            first = false;
            out.append("namespaces:");
            out.append(PTypeUtils.toString(mNamespaces));
        }
        if (numDecl() > 0) {
            if (!first) out.append(',');
            first = false;
            out.append("decl:");
            out.append(PTypeUtils.toString(mDecl));
        }
        out.append('}');
        return out.toString();
    }

    @Override
    public int compareTo(ThriftDocument other) {
        int c;

        c = Boolean.compare(mComment != null, other.mComment != null);
        if (c != 0) return c;
        if (mComment != null) {
            c = mComment.compareTo(other.mComment);
            if (c != 0) return c;
        }

        c = Boolean.compare(mPackage != null, other.mPackage != null);
        if (c != 0) return c;
        if (mPackage != null) {
            c = mPackage.compareTo(other.mPackage);
            if (c != 0) return c;
        }

        c = Boolean.compare(mIncludes != null, other.mIncludes != null);
        if (c != 0) return c;
        if (mIncludes != null) {
            c = Integer.compare(mIncludes.hashCode(), other.mIncludes.hashCode());
            if (c != 0) return c;
        }

        c = Boolean.compare(mNamespaces != null, other.mNamespaces != null);
        if (c != 0) return c;
        if (mNamespaces != null) {
            c = Integer.compare(mNamespaces.hashCode(), other.mNamespaces.hashCode());
            if (c != 0) return c;
        }

        c = Boolean.compare(mDecl != null, other.mDecl != null);
        if (c != 0) return c;
        if (mDecl != null) {
            c = Integer.compare(mDecl.hashCode(), other.mDecl.hashCode());
            if (c != 0) return c;
        }

        return 0;
    }

    public enum _Field implements PField {
        COMMENT(1, PRequirement.DEFAULT, "comment", PPrimitive.STRING.provider(), null),
        PACKAGE(2, PRequirement.REQUIRED, "package", PPrimitive.STRING.provider(), null),
        INCLUDES(3, PRequirement.DEFAULT, "includes", PList.provider(PPrimitive.STRING.provider()), null),
        NAMESPACES(4, PRequirement.DEFAULT, "namespaces", PMap.provider(PPrimitive.STRING.provider(),PPrimitive.STRING.provider()), null),
        DECL(5, PRequirement.DEFAULT, "decl", PList.provider(Declaration.provider()), null),
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
            builder.append("ThriftDocument._Field(")
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

    public static PStructDescriptorProvider<ThriftDocument,_Field> provider() {
        return new _Provider();
    }

    @Override
    public PStructDescriptor<ThriftDocument,_Field> descriptor() {
        return kDescriptor;
    }

    public static final PStructDescriptor<ThriftDocument,_Field> kDescriptor;

    private static class _Descriptor
            extends PStructDescriptor<ThriftDocument,_Field> {
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

    private final static class _Provider extends PStructDescriptorProvider<ThriftDocument,_Field> {
        @Override
        public PStructDescriptor<ThriftDocument,_Field> descriptor() {
            return kDescriptor;
        }
    }

    private final static class _Factory
            extends PMessageBuilderFactory<ThriftDocument> {
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
            extends PMessageBuilder<ThriftDocument> {
        private BitSet optionals;

        private String mComment;
        private String mPackage;
        private List<String> mIncludes;
        private Map<String,String> mNamespaces;
        private List<Declaration> mDecl;


        public _Builder() {
            optionals = new BitSet(5);
            mIncludes = new LinkedList<>();
            mNamespaces = new LinkedHashMap<>();
            mDecl = new LinkedList<>();
        }

        public _Builder(ThriftDocument base) {
            this();

            if (base.hasComment()) {
                optionals.set(0);
                mComment = base.mComment;
            }
            if (base.hasPackage()) {
                optionals.set(1);
                mPackage = base.mPackage;
            }
            if (base.numIncludes() > 0) {
                optionals.set(2);
                mIncludes.addAll(base.mIncludes);
            }
            if (base.numNamespaces() > 0) {
                optionals.set(3);
                mNamespaces.putAll(base.mNamespaces);
            }
            if (base.numDecl() > 0) {
                optionals.set(4);
                mDecl.addAll(base.mDecl);
            }
        }

        /** Must come before the first statement of the header. */
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
        /** Deducted from filename in .thrift IDL files. */
        public _Builder setPackage(String value) {
            optionals.set(1);
            mPackage = value;
            return this;
        }
        public _Builder clearPackage() {
            optionals.set(1, false);
            mPackage = null;
            return this;
        }
        /** include "<package>.thrift" */
        public _Builder setIncludes(Collection<String> value) {
            optionals.set(2);
            mIncludes.clear();
            mIncludes.addAll(value);
            return this;
        }
        /** include "<package>.thrift" */
        public _Builder addToIncludes(String... values) {
            optionals.set(2);
            for (String item : values) {
                mIncludes.add(item);
            }
            return this;
        }

        public _Builder clearIncludes() {
            optionals.set(2, false);
            mIncludes.clear();
            return this;
        }
        /** namespace <key> <value> */
        public _Builder setNamespaces(Map<String,String> value) {
            optionals.set(3);
            mNamespaces.clear();
            mNamespaces.putAll(value);
            return this;
        }
        /** namespace <key> <value> */
        public _Builder putInNamespaces(String key, String value) {
            optionals.set(3);
            mNamespaces.put(key, value);
            return this;
        }

        public _Builder clearNamespaces() {
            optionals.set(3, false);
            mNamespaces.clear();
            return this;
        }
        public _Builder setDecl(Collection<Declaration> value) {
            optionals.set(4);
            mDecl.clear();
            mDecl.addAll(value);
            return this;
        }
        public _Builder addToDecl(Declaration... values) {
            optionals.set(4);
            for (Declaration item : values) {
                mDecl.add(item);
            }
            return this;
        }

        public _Builder clearDecl() {
            optionals.set(4, false);
            mDecl.clear();
            return this;
        }
        @Override
        public _Builder set(int key, Object value) {
            if (value == null) return clear(key);
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
        public _Builder addTo(int key, Object value) {
            switch (key) {
                case 3: addToIncludes((String) value); break;
                case 5: addToDecl((Declaration) value); break;
                default: break;
            }
            return this;
        }

        @Override
        public _Builder clear(int key) {
            switch (key) {
                case 1: clearComment(); break;
                case 2: clearPackage(); break;
                case 3: clearIncludes(); break;
                case 4: clearNamespaces(); break;
                case 5: clearDecl(); break;
            }
            return this;
        }

        @Override
        public boolean isValid() {
            return optionals.get(1);
        }

        @Override
        public ThriftDocument build() {
            return new ThriftDocument(this);
        }
    }
}
