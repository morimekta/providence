package net.morimekta.providence.model;

/**
 * &lt;namespace&gt;* &lt;include&gt;* &lt;declataion&gt;*
 */
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

    /**
     * Must come before the first statement of the header.
     *
     * @return The field value
     */
    public String getComment() {
        return mComment;
    }

    public boolean hasPackage() {
        return mPackage != null;
    }

    /**
     * Deducted from filename in .thrift IDL files.
     *
     * @return The field value
     */
    public String getPackage() {
        return mPackage;
    }

    public int numIncludes() {
        return mIncludes != null ? mIncludes.size() : 0;
    }

    public boolean hasIncludes() {
        return mIncludes != null;
    }

    /**
     * include &quot;&lt;package&gt;.thrift&quot;
     *
     * @return The field value
     */
    public java.util.List<String> getIncludes() {
        return mIncludes;
    }

    public int numNamespaces() {
        return mNamespaces != null ? mNamespaces.size() : 0;
    }

    public boolean hasNamespaces() {
        return mNamespaces != null;
    }

    /**
     * namespace &lt;key&gt; &lt;value&gt;
     *
     * @return The field value
     */
    public java.util.Map<String,String> getNamespaces() {
        return mNamespaces;
    }

    public int numDecl() {
        return mDecl != null ? mDecl.size() : 0;
    }

    public boolean hasDecl() {
        return mDecl != null;
    }

    /**
     * @return The field value
     */
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
    public boolean compact() {
        return false;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        if (o == null || !(o instanceof ThriftDocument)) return false;
        ThriftDocument other = (ThriftDocument) o;
        return java.util.Objects.equals(mComment, other.mComment) &&
               java.util.Objects.equals(mPackage, other.mPackage) &&
               java.util.Objects.equals(mIncludes, other.mIncludes) &&
               java.util.Objects.equals(mNamespaces, other.mNamespaces) &&
               java.util.Objects.equals(mDecl, other.mDecl);
    }

    @Override
    public int hashCode() {
        if (tHashCode == 0) {
            tHashCode = java.util.Objects.hash(
                    ThriftDocument.class,
                    _Field.COMMENT, mComment,
                    _Field.PACKAGE, mPackage,
                    _Field.INCLUDES, mIncludes,
                    _Field.NAMESPACES, mNamespaces,
                    _Field.DECL, mDecl);
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
        if (mComment != null) {
            first = false;
            out.append("comment:")
               .append('\"')
               .append(net.morimekta.util.Strings.escape(mComment))
               .append('\"');
        }
        if (mPackage != null) {
            if (first) first = false;
            else out.append(',');
            out.append("package:")
               .append('\"')
               .append(net.morimekta.util.Strings.escape(mPackage))
               .append('\"');
        }
        if (mIncludes != null && mIncludes.size() > 0) {
            if (first) first = false;
            else out.append(',');
            out.append("includes:")
               .append(net.morimekta.util.Strings.asString(mIncludes));
        }
        if (mNamespaces != null && mNamespaces.size() > 0) {
            if (first) first = false;
            else out.append(',');
            out.append("namespaces:")
               .append(net.morimekta.util.Strings.asString(mNamespaces));
        }
        if (mDecl != null && mDecl.size() > 0) {
            if (!first) out.append(',');
            out.append("decl:")
               .append(net.morimekta.util.Strings.asString(mDecl));
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
            return net.morimekta.providence.descriptor.PField.toString(this);
        }

        public static _Field forKey(int key) {
            switch (key) {
                case 1: return _Field.COMMENT;
                case 2: return _Field.PACKAGE;
                case 3: return _Field.INCLUDES;
                case 4: return _Field.NAMESPACES;
                case 5: return _Field.DECL;
            }
            return null;
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
            super("model", "ThriftDocument", new _Factory(), false, false);
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

    /**
     * Make a model.ThriftDocument builder.
     * @return The builder instance.
     */
    public static _Builder builder() {
        return new _Builder();
    }

    public static class _Builder
            extends net.morimekta.providence.PMessageBuilder<ThriftDocument> {
        private java.util.BitSet optionals;

        private String mComment;
        private String mPackage;
        private net.morimekta.providence.descriptor.PList.Builder<String> mIncludes;
        private net.morimekta.providence.descriptor.PMap.Builder<String,String> mNamespaces;
        private net.morimekta.providence.descriptor.PList.Builder<net.morimekta.providence.model.Declaration> mDecl;

        /**
         * Make a model.ThriftDocument builder.
         */
        public _Builder() {
            optionals = new java.util.BitSet(5);
            mIncludes = new net.morimekta.providence.descriptor.PList.ImmutableListBuilder<>();
            mNamespaces = new net.morimekta.providence.descriptor.PMap.ImmutableMapBuilder<>();
            mDecl = new net.morimekta.providence.descriptor.PList.ImmutableListBuilder<>();
        }

        /**
         * Make a mutating builder off a base model.ThriftDocument.
         *
         * @param base The base ThriftDocument
         */
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

        @Override
        public _Builder merge(ThriftDocument from) {
            if (from.hasComment()) {
                optionals.set(0);
                mComment = from.getComment();
            }

            if (from.hasPackage()) {
                optionals.set(1);
                mPackage = from.getPackage();
            }

            if (from.hasIncludes()) {
                optionals.set(2);
                mIncludes.clear();
                mIncludes.addAll(from.getIncludes());
            }

            if (from.hasNamespaces()) {
                optionals.set(3);
                mNamespaces.putAll(from.getNamespaces());
            }

            if (from.hasDecl()) {
                optionals.set(4);
                mDecl.clear();
                mDecl.addAll(from.getDecl());
            }
            return this;
        }

        /**
         * Sets the value of comment.
         *
         * Must come before the first statement of the header.
         *
         * @param value The new value
         * @return The builder
         */
        public _Builder setComment(String value) {
            optionals.set(0);
            mComment = value;
            return this;
        }

        /**
         * Checks for presence of the comment field.
         *
         * Must come before the first statement of the header.
         *
         * @return True iff comment has been set.
         */
        public boolean isSetComment() {
            return optionals.get(0);
        }

        /**
         * Clears the comment field.
         *
         * Must come before the first statement of the header.
         *
         * @return The builder
         */
        public _Builder clearComment() {
            optionals.clear(0);
            mComment = null;
            return this;
        }

        /**
         * Sets the value of package.
         *
         * Deducted from filename in .thrift IDL files.
         *
         * @param value The new value
         * @return The builder
         */
        public _Builder setPackage(String value) {
            optionals.set(1);
            mPackage = value;
            return this;
        }

        /**
         * Checks for presence of the package field.
         *
         * Deducted from filename in .thrift IDL files.
         *
         * @return True iff package has been set.
         */
        public boolean isSetPackage() {
            return optionals.get(1);
        }

        /**
         * Clears the package field.
         *
         * Deducted from filename in .thrift IDL files.
         *
         * @return The builder
         */
        public _Builder clearPackage() {
            optionals.clear(1);
            mPackage = null;
            return this;
        }

        /**
         * Sets the value of includes.
         *
         * include &quot;&lt;package&gt;.thrift&quot;
         *
         * @param value The new value
         * @return The builder
         */
        public _Builder setIncludes(java.util.Collection<String> value) {
            optionals.set(2);
            mIncludes.clear();
            mIncludes.addAll(value);
            return this;
        }

        /**
         * Adds entries to includes.
         *
         * include &quot;&lt;package&gt;.thrift&quot;
         *
         * @param values The added value
         * @return The builder
         */
        public _Builder addToIncludes(String... values) {
            optionals.set(2);
            for (String item : values) {
                mIncludes.add(item);
            }
            return this;
        }

        /**
         * Checks for presence of the includes field.
         *
         * include &quot;&lt;package&gt;.thrift&quot;
         *
         * @return True iff includes has been set.
         */
        public boolean isSetIncludes() {
            return optionals.get(2);
        }

        /**
         * Clears the includes field.
         *
         * include &quot;&lt;package&gt;.thrift&quot;
         *
         * @return The builder
         */
        public _Builder clearIncludes() {
            optionals.clear(2);
            mIncludes.clear();
            return this;
        }

        /**
         * Sets the value of namespaces.
         *
         * namespace &lt;key&gt; &lt;value&gt;
         *
         * @param value The new value
         * @return The builder
         */
        public _Builder setNamespaces(java.util.Map<String,String> value) {
            optionals.set(3);
            mNamespaces.clear();
            mNamespaces.putAll(value);
            return this;
        }

        /**
         * Adds a mapping to namespaces.
         *
         * namespace &lt;key&gt; &lt;value&gt;
         *
         * @param key The inserted key
         * @param value The inserted value
         * @return The builder
         */
        public _Builder putInNamespaces(String key, String value) {
            optionals.set(3);
            mNamespaces.put(key, value);
            return this;
        }

        /**
         * Checks for presence of the namespaces field.
         *
         * namespace &lt;key&gt; &lt;value&gt;
         *
         * @return True iff namespaces has been set.
         */
        public boolean isSetNamespaces() {
            return optionals.get(3);
        }

        /**
         * Clears the namespaces field.
         *
         * namespace &lt;key&gt; &lt;value&gt;
         *
         * @return The builder
         */
        public _Builder clearNamespaces() {
            optionals.clear(3);
            mNamespaces.clear();
            return this;
        }

        /**
         * Sets the value of decl.
         *
         * @param value The new value
         * @return The builder
         */
        public _Builder setDecl(java.util.Collection<net.morimekta.providence.model.Declaration> value) {
            optionals.set(4);
            mDecl.clear();
            mDecl.addAll(value);
            return this;
        }

        /**
         * Adds entries to decl.
         *
         * @param values The added value
         * @return The builder
         */
        public _Builder addToDecl(net.morimekta.providence.model.Declaration... values) {
            optionals.set(4);
            for (net.morimekta.providence.model.Declaration item : values) {
                mDecl.add(item);
            }
            return this;
        }

        /**
         * Checks for presence of the decl field.
         *
         * @return True iff decl has been set.
         */
        public boolean isSetDecl() {
            return optionals.get(4);
        }

        /**
         * Clears the decl field.
         *
         * @return The builder
         */
        public _Builder clearDecl() {
            optionals.clear(4);
            mDecl.clear();
            return this;
        }

        @Override
        @SuppressWarnings("unchecked")
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
