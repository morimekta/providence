package net.morimekta.providence.model;

/** <namespace>* <include>* <declataion>* */
@SuppressWarnings("unused")
public class ThriftDocument
        implements net.morimekta.providence.PMessage<ThriftDocument>, java.io.Serializable, Comparable<ThriftDocument> {
    private final static long serialVersionUID = -5731994850994905187L;

    private final String mComment;
    private final String mPackage;
    private final java.util.List<String> mIncludes;
    private final java.util.Map<String,String> mNamespaces;
    private final java.util.List<net.morimekta.providence.model.Declaration> mDecl;
    
    private volatile int tHashCode;

    private ThriftDocument(_Builder builder) {
        mComment = builder.mComment;
        mPackage = builder.mPackage;
        if (builder.isSetIncludes()) {
            mIncludes = builder.mIncludes.build();
        } else {
            mIncludes = null;
        }
        if (builder.isSetNamespaces()) {
            mNamespaces = builder.mNamespaces.build();
        } else {
            mNamespaces = null;
        }
        if (builder.isSetDecl()) {
            mDecl = builder.mDecl.build();
        } else {
            mDecl = null;
        }
    }

    public ThriftDocument(String pComment,
                          String pPackage,
                          java.util.List<String> pIncludes,
                          java.util.Map<String,String> pNamespaces,
                          java.util.List<net.morimekta.providence.model.Declaration> pDecl) {
        mComment = pComment;
        mPackage = pPackage;
        if (pIncludes != null) {
            mIncludes = com.google.common.collect.ImmutableList.copyOf(pIncludes);
        } else {
            mIncludes = null;
        }
        if (pNamespaces != null) {
            mNamespaces = com.google.common.collect.ImmutableMap.copyOf(pNamespaces);
        } else {
            mNamespaces = null;
        }
        if (pDecl != null) {
            mDecl = com.google.common.collect.ImmutableList.copyOf(pDecl);
        } else {
            mDecl = null;
        }
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
    public java.util.List<String> getIncludes() {
        return mIncludes;
    }

    public int numNamespaces() {
        return mNamespaces != null ? mNamespaces.size() : 0;
    }

    /** namespace <key> <value> */
    public java.util.Map<String,String> getNamespaces() {
        return mNamespaces;
    }

    public int numDecl() {
        return mDecl != null ? mDecl.size() : 0;
    }

    public java.util.List<net.morimekta.providence.model.Declaration> getDecl() {
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
        return java.util.Objects.equals(mComment, other.mComment) &&
               java.util.Objects.equals(mPackage, other.mPackage) &&
               net.morimekta.providence.util.PTypeUtils.equals(mIncludes, other.mIncludes) &&
               net.morimekta.providence.util.PTypeUtils.equals(mNamespaces, other.mNamespaces) &&
               net.morimekta.providence.util.PTypeUtils.equals(mDecl, other.mDecl);
    }

    @Override
    public int hashCode() {
        if (tHashCode == 0) {
            tHashCode = java.util.Objects.hash(
                    ThriftDocument.class,
                    _Field.COMMENT, mComment,
                    _Field.PACKAGE, mPackage,
                    _Field.INCLUDES, net.morimekta.providence.util.PTypeUtils.hashCode(mIncludes),
                    _Field.NAMESPACES, net.morimekta.providence.util.PTypeUtils.hashCode(mNamespaces),
                    _Field.DECL, net.morimekta.providence.util.PTypeUtils.hashCode(mDecl));
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
            out.append(net.morimekta.providence.util.PTypeUtils.toString(mIncludes));
        }
        if (numNamespaces() > 0) {
            if (!first) out.append(',');
            first = false;
            out.append("namespaces:");
            out.append(net.morimekta.providence.util.PTypeUtils.toString(mNamespaces));
        }
        if (numDecl() > 0) {
            if (!first) out.append(',');
            first = false;
            out.append("decl:");
            out.append(net.morimekta.providence.util.PTypeUtils.toString(mDecl));
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

    public enum _Field implements net.morimekta.providence.descriptor.PField {
        COMMENT(1, net.morimekta.providence.descriptor.PRequirement.DEFAULT, "comment", net.morimekta.providence.descriptor.PPrimitive.STRING.provider(), null),
        PACKAGE(2, net.morimekta.providence.descriptor.PRequirement.REQUIRED, "package", net.morimekta.providence.descriptor.PPrimitive.STRING.provider(), null),
        INCLUDES(3, net.morimekta.providence.descriptor.PRequirement.DEFAULT, "includes", net.morimekta.providence.descriptor.PList.provider(net.morimekta.providence.descriptor.PPrimitive.STRING.provider()), null),
        NAMESPACES(4, net.morimekta.providence.descriptor.PRequirement.DEFAULT, "namespaces", net.morimekta.providence.descriptor.PMap.provider(net.morimekta.providence.descriptor.PPrimitive.STRING.provider(),net.morimekta.providence.descriptor.PPrimitive.STRING.provider()), null),
        DECL(5, net.morimekta.providence.descriptor.PRequirement.DEFAULT, "decl", net.morimekta.providence.descriptor.PList.provider(net.morimekta.providence.model.Declaration.provider()), null),
        ;

        private final int mKey;
        private final net.morimekta.providence.descriptor.PRequirement mRequired;
        private final String mName;
        private final net.morimekta.providence.descriptor.PDescriptorProvider<?> mTypeProvider;
        private final net.morimekta.providence.descriptor.PValueProvider<?> mDefaultValue;

        _Field(int key, net.morimekta.providence.descriptor.PRequirement required, String name, net.morimekta.providence.descriptor.PDescriptorProvider<?> typeProvider, net.morimekta.providence.descriptor.PValueProvider<?> defaultValue) {
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
        public net.morimekta.providence.descriptor.PRequirement getRequirement() { return mRequired; }

        @Override
        public net.morimekta.providence.PType getType() { return getDescriptor().getType(); }

        @Override
        public net.morimekta.providence.descriptor.PDescriptor<?> getDescriptor() { return mTypeProvider.descriptor(); }

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

    public static net.morimekta.providence.descriptor.PStructDescriptorProvider<ThriftDocument,_Field> provider() {
        return new _Provider();
    }

    @Override
    public net.morimekta.providence.descriptor.PStructDescriptor<ThriftDocument,_Field> descriptor() {
        return kDescriptor;
    }

    public static final net.morimekta.providence.descriptor.PStructDescriptor<ThriftDocument,_Field> kDescriptor;

    private static class _Descriptor
            extends net.morimekta.providence.descriptor.PStructDescriptor<ThriftDocument,_Field> {
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

    private final static class _Provider extends net.morimekta.providence.descriptor.PStructDescriptorProvider<ThriftDocument,_Field> {
        @Override
        public net.morimekta.providence.descriptor.PStructDescriptor<ThriftDocument,_Field> descriptor() {
            return kDescriptor;
        }
    }

    private final static class _Factory
            extends net.morimekta.providence.PMessageBuilderFactory<ThriftDocument> {
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
            extends net.morimekta.providence.PMessageBuilder<ThriftDocument> {
        private java.util.BitSet optionals;

        private String mComment;
        private String mPackage;
        private net.morimekta.providence.descriptor.PList.Builder mIncludes;
        private net.morimekta.providence.descriptor.PMap.Builder mNamespaces;
        private net.morimekta.providence.descriptor.PList.Builder mDecl;


        public _Builder() {
            optionals = new java.util.BitSet(5);
            mIncludes = new net.morimekta.providence.descriptor.PList.ImmutableListBuilder<>();
            mNamespaces = new net.morimekta.providence.descriptor.PMap.ImmutableMapBuilder<>();
            mDecl = new net.morimekta.providence.descriptor.PList.ImmutableListBuilder<>();
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
        public boolean isSetComment() {
            return optionals.get(0);
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
        public boolean isSetPackage() {
            return optionals.get(1);
        }
        public _Builder clearPackage() {
            optionals.set(1, false);
            mPackage = null;
            return this;
        }
        /** include "<package>.thrift" */
        public _Builder setIncludes(java.util.Collection<String> value) {
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

        public boolean isSetIncludes() {
            return optionals.get(2);
        }
        public _Builder clearIncludes() {
            optionals.set(2, false);
            mIncludes.clear();
            return this;
        }
        /** namespace <key> <value> */
        public _Builder setNamespaces(java.util.Map<String,String> value) {
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

        public boolean isSetNamespaces() {
            return optionals.get(3);
        }
        public _Builder clearNamespaces() {
            optionals.set(3, false);
            mNamespaces.clear();
            return this;
        }
        public _Builder setDecl(java.util.Collection<net.morimekta.providence.model.Declaration> value) {
            optionals.set(4);
            mDecl.clear();
            mDecl.addAll(value);
            return this;
        }
        public _Builder addToDecl(net.morimekta.providence.model.Declaration... values) {
            optionals.set(4);
            for (net.morimekta.providence.model.Declaration item : values) {
                mDecl.add(item);
            }
            return this;
        }

        public boolean isSetDecl() {
            return optionals.get(4);
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
                case 3: setIncludes((java.util.List<String>) value); break;
                case 4: setNamespaces((java.util.Map<String,String>) value); break;
                case 5: setDecl((java.util.List<net.morimekta.providence.model.Declaration>) value); break;
            }
            return this;
        }

        @Override
        public _Builder addTo(int key, Object value) {
            switch (key) {
                case 3: addToIncludes((String) value); break;
                case 5: addToDecl((net.morimekta.providence.model.Declaration) value); break;
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
