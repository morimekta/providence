package net.morimekta.test.providence;

@SuppressWarnings("unused")
public class Containers
        implements net.morimekta.providence.PMessage<Containers,Containers._Field>,
                   Comparable<Containers>,
                   java.io.Serializable,
                   net.morimekta.providence.serializer.rw.BinaryWriter {
    private final static long serialVersionUID = 3106880629763954881L;

    private final java.util.List<Boolean> mBooleanList;
    private final java.util.List<Byte> mByteList;
    private final java.util.List<Short> mShortList;
    private final java.util.List<Integer> mIntegerList;
    private final java.util.List<Long> mLongList;
    private final java.util.List<Double> mDoubleList;
    private final java.util.List<String> mStringList;
    private final java.util.List<net.morimekta.util.Binary> mBinaryList;
    private final java.util.Set<Boolean> mBooleanSet;
    private final java.util.Set<Byte> mByteSet;
    private final java.util.Set<Short> mShortSet;
    private final java.util.Set<Integer> mIntegerSet;
    private final java.util.Set<Long> mLongSet;
    private final java.util.Set<Double> mDoubleSet;
    private final java.util.Set<String> mStringSet;
    private final java.util.Set<net.morimekta.util.Binary> mBinarySet;
    private final java.util.Map<Boolean,Boolean> mBooleanMap;
    private final java.util.Map<Byte,Byte> mByteMap;
    private final java.util.Map<Short,Short> mShortMap;
    private final java.util.Map<Integer,Integer> mIntegerMap;
    private final java.util.Map<Long,Long> mLongMap;
    private final java.util.Map<Double,Double> mDoubleMap;
    private final java.util.Map<String,String> mStringMap;
    private final java.util.Map<net.morimekta.util.Binary,net.morimekta.util.Binary> mBinaryMap;
    private final java.util.List<net.morimekta.test.providence.Value> mEnumList;
    private final java.util.Set<net.morimekta.test.providence.Value> mEnumSet;
    private final java.util.Map<net.morimekta.test.providence.Value,net.morimekta.test.providence.Value> mEnumMap;
    private final java.util.List<net.morimekta.test.providence.DefaultFields> mMessageList;
    private final java.util.Set<net.morimekta.test.providence.DefaultFields> mMessageSet;
    private final java.util.Map<String,net.morimekta.test.providence.DefaultFields> mMessageMap;
    private final net.morimekta.test.providence.RequiredFields mRequiredFields;
    private final net.morimekta.test.providence.DefaultFields mDefaultFields;
    private final net.morimekta.test.providence.OptionalFields mOptionalFields;
    private final net.morimekta.test.providence.UnionFields mUnionFields;
    private final net.morimekta.test.providence.ExceptionFields mExceptionFields;
    private final net.morimekta.test.providence.DefaultValues mDefaultValues;

    private volatile int tHashCode;

    public Containers(java.util.List<Boolean> pBooleanList,
                      java.util.List<Byte> pByteList,
                      java.util.List<Short> pShortList,
                      java.util.List<Integer> pIntegerList,
                      java.util.List<Long> pLongList,
                      java.util.List<Double> pDoubleList,
                      java.util.List<String> pStringList,
                      java.util.List<net.morimekta.util.Binary> pBinaryList,
                      java.util.Set<Boolean> pBooleanSet,
                      java.util.Set<Byte> pByteSet,
                      java.util.Set<Short> pShortSet,
                      java.util.Set<Integer> pIntegerSet,
                      java.util.Set<Long> pLongSet,
                      java.util.Set<Double> pDoubleSet,
                      java.util.Set<String> pStringSet,
                      java.util.Set<net.morimekta.util.Binary> pBinarySet,
                      java.util.Map<Boolean,Boolean> pBooleanMap,
                      java.util.Map<Byte,Byte> pByteMap,
                      java.util.Map<Short,Short> pShortMap,
                      java.util.Map<Integer,Integer> pIntegerMap,
                      java.util.Map<Long,Long> pLongMap,
                      java.util.Map<Double,Double> pDoubleMap,
                      java.util.Map<String,String> pStringMap,
                      java.util.Map<net.morimekta.util.Binary,net.morimekta.util.Binary> pBinaryMap,
                      java.util.List<net.morimekta.test.providence.Value> pEnumList,
                      java.util.Set<net.morimekta.test.providence.Value> pEnumSet,
                      java.util.Map<net.morimekta.test.providence.Value,net.morimekta.test.providence.Value> pEnumMap,
                      java.util.List<net.morimekta.test.providence.DefaultFields> pMessageList,
                      java.util.Set<net.morimekta.test.providence.DefaultFields> pMessageSet,
                      java.util.Map<String,net.morimekta.test.providence.DefaultFields> pMessageMap,
                      net.morimekta.test.providence.RequiredFields pRequiredFields,
                      net.morimekta.test.providence.DefaultFields pDefaultFields,
                      net.morimekta.test.providence.OptionalFields pOptionalFields,
                      net.morimekta.test.providence.UnionFields pUnionFields,
                      net.morimekta.test.providence.ExceptionFields pExceptionFields,
                      net.morimekta.test.providence.DefaultValues pDefaultValues) {
        if (pBooleanList != null) {
            mBooleanList = com.google.common.collect.ImmutableList.copyOf(pBooleanList);
        } else {
            mBooleanList = null;
        }
        if (pByteList != null) {
            mByteList = com.google.common.collect.ImmutableList.copyOf(pByteList);
        } else {
            mByteList = null;
        }
        if (pShortList != null) {
            mShortList = com.google.common.collect.ImmutableList.copyOf(pShortList);
        } else {
            mShortList = null;
        }
        if (pIntegerList != null) {
            mIntegerList = com.google.common.collect.ImmutableList.copyOf(pIntegerList);
        } else {
            mIntegerList = null;
        }
        if (pLongList != null) {
            mLongList = com.google.common.collect.ImmutableList.copyOf(pLongList);
        } else {
            mLongList = null;
        }
        if (pDoubleList != null) {
            mDoubleList = com.google.common.collect.ImmutableList.copyOf(pDoubleList);
        } else {
            mDoubleList = null;
        }
        if (pStringList != null) {
            mStringList = com.google.common.collect.ImmutableList.copyOf(pStringList);
        } else {
            mStringList = null;
        }
        if (pBinaryList != null) {
            mBinaryList = com.google.common.collect.ImmutableList.copyOf(pBinaryList);
        } else {
            mBinaryList = null;
        }
        if (pBooleanSet != null) {
            mBooleanSet = com.google.common.collect.ImmutableSet.copyOf(pBooleanSet);
        } else {
            mBooleanSet = null;
        }
        if (pByteSet != null) {
            mByteSet = com.google.common.collect.ImmutableSortedSet.copyOf(pByteSet);
        } else {
            mByteSet = null;
        }
        if (pShortSet != null) {
            mShortSet = java.util.Collections.unmodifiableSet(pShortSet);
        } else {
            mShortSet = null;
        }
        if (pIntegerSet != null) {
            mIntegerSet = com.google.common.collect.ImmutableSet.copyOf(pIntegerSet);
        } else {
            mIntegerSet = null;
        }
        if (pLongSet != null) {
            mLongSet = com.google.common.collect.ImmutableSet.copyOf(pLongSet);
        } else {
            mLongSet = null;
        }
        if (pDoubleSet != null) {
            mDoubleSet = com.google.common.collect.ImmutableSet.copyOf(pDoubleSet);
        } else {
            mDoubleSet = null;
        }
        if (pStringSet != null) {
            mStringSet = com.google.common.collect.ImmutableSet.copyOf(pStringSet);
        } else {
            mStringSet = null;
        }
        if (pBinarySet != null) {
            mBinarySet = com.google.common.collect.ImmutableSet.copyOf(pBinarySet);
        } else {
            mBinarySet = null;
        }
        if (pBooleanMap != null) {
            mBooleanMap = com.google.common.collect.ImmutableMap.copyOf(pBooleanMap);
        } else {
            mBooleanMap = null;
        }
        if (pByteMap != null) {
            mByteMap = com.google.common.collect.ImmutableSortedMap.copyOf(pByteMap);
        } else {
            mByteMap = null;
        }
        if (pShortMap != null) {
            mShortMap = java.util.Collections.unmodifiableMap(pShortMap);
        } else {
            mShortMap = null;
        }
        if (pIntegerMap != null) {
            mIntegerMap = com.google.common.collect.ImmutableMap.copyOf(pIntegerMap);
        } else {
            mIntegerMap = null;
        }
        if (pLongMap != null) {
            mLongMap = com.google.common.collect.ImmutableMap.copyOf(pLongMap);
        } else {
            mLongMap = null;
        }
        if (pDoubleMap != null) {
            mDoubleMap = com.google.common.collect.ImmutableMap.copyOf(pDoubleMap);
        } else {
            mDoubleMap = null;
        }
        if (pStringMap != null) {
            mStringMap = com.google.common.collect.ImmutableMap.copyOf(pStringMap);
        } else {
            mStringMap = null;
        }
        if (pBinaryMap != null) {
            mBinaryMap = com.google.common.collect.ImmutableMap.copyOf(pBinaryMap);
        } else {
            mBinaryMap = null;
        }
        if (pEnumList != null) {
            mEnumList = com.google.common.collect.ImmutableList.copyOf(pEnumList);
        } else {
            mEnumList = null;
        }
        if (pEnumSet != null) {
            mEnumSet = com.google.common.collect.ImmutableSet.copyOf(pEnumSet);
        } else {
            mEnumSet = null;
        }
        if (pEnumMap != null) {
            mEnumMap = com.google.common.collect.ImmutableMap.copyOf(pEnumMap);
        } else {
            mEnumMap = null;
        }
        if (pMessageList != null) {
            mMessageList = com.google.common.collect.ImmutableList.copyOf(pMessageList);
        } else {
            mMessageList = null;
        }
        if (pMessageSet != null) {
            mMessageSet = com.google.common.collect.ImmutableSet.copyOf(pMessageSet);
        } else {
            mMessageSet = null;
        }
        if (pMessageMap != null) {
            mMessageMap = com.google.common.collect.ImmutableMap.copyOf(pMessageMap);
        } else {
            mMessageMap = null;
        }
        mRequiredFields = pRequiredFields;
        mDefaultFields = pDefaultFields;
        mOptionalFields = pOptionalFields;
        mUnionFields = pUnionFields;
        mExceptionFields = pExceptionFields;
        mDefaultValues = pDefaultValues;
    }

    private Containers(_Builder builder) {
        if (builder.isSetBooleanList()) {
            mBooleanList = builder.mBooleanList.build();
        } else {
            mBooleanList = null;
        }
        if (builder.isSetByteList()) {
            mByteList = builder.mByteList.build();
        } else {
            mByteList = null;
        }
        if (builder.isSetShortList()) {
            mShortList = builder.mShortList.build();
        } else {
            mShortList = null;
        }
        if (builder.isSetIntegerList()) {
            mIntegerList = builder.mIntegerList.build();
        } else {
            mIntegerList = null;
        }
        if (builder.isSetLongList()) {
            mLongList = builder.mLongList.build();
        } else {
            mLongList = null;
        }
        if (builder.isSetDoubleList()) {
            mDoubleList = builder.mDoubleList.build();
        } else {
            mDoubleList = null;
        }
        if (builder.isSetStringList()) {
            mStringList = builder.mStringList.build();
        } else {
            mStringList = null;
        }
        if (builder.isSetBinaryList()) {
            mBinaryList = builder.mBinaryList.build();
        } else {
            mBinaryList = null;
        }
        if (builder.isSetBooleanSet()) {
            mBooleanSet = builder.mBooleanSet.build();
        } else {
            mBooleanSet = null;
        }
        if (builder.isSetByteSet()) {
            mByteSet = builder.mByteSet.build();
        } else {
            mByteSet = null;
        }
        if (builder.isSetShortSet()) {
            mShortSet = builder.mShortSet.build();
        } else {
            mShortSet = null;
        }
        if (builder.isSetIntegerSet()) {
            mIntegerSet = builder.mIntegerSet.build();
        } else {
            mIntegerSet = null;
        }
        if (builder.isSetLongSet()) {
            mLongSet = builder.mLongSet.build();
        } else {
            mLongSet = null;
        }
        if (builder.isSetDoubleSet()) {
            mDoubleSet = builder.mDoubleSet.build();
        } else {
            mDoubleSet = null;
        }
        if (builder.isSetStringSet()) {
            mStringSet = builder.mStringSet.build();
        } else {
            mStringSet = null;
        }
        if (builder.isSetBinarySet()) {
            mBinarySet = builder.mBinarySet.build();
        } else {
            mBinarySet = null;
        }
        if (builder.isSetBooleanMap()) {
            mBooleanMap = builder.mBooleanMap.build();
        } else {
            mBooleanMap = null;
        }
        if (builder.isSetByteMap()) {
            mByteMap = builder.mByteMap.build();
        } else {
            mByteMap = null;
        }
        if (builder.isSetShortMap()) {
            mShortMap = builder.mShortMap.build();
        } else {
            mShortMap = null;
        }
        if (builder.isSetIntegerMap()) {
            mIntegerMap = builder.mIntegerMap.build();
        } else {
            mIntegerMap = null;
        }
        if (builder.isSetLongMap()) {
            mLongMap = builder.mLongMap.build();
        } else {
            mLongMap = null;
        }
        if (builder.isSetDoubleMap()) {
            mDoubleMap = builder.mDoubleMap.build();
        } else {
            mDoubleMap = null;
        }
        if (builder.isSetStringMap()) {
            mStringMap = builder.mStringMap.build();
        } else {
            mStringMap = null;
        }
        if (builder.isSetBinaryMap()) {
            mBinaryMap = builder.mBinaryMap.build();
        } else {
            mBinaryMap = null;
        }
        if (builder.isSetEnumList()) {
            mEnumList = builder.mEnumList.build();
        } else {
            mEnumList = null;
        }
        if (builder.isSetEnumSet()) {
            mEnumSet = builder.mEnumSet.build();
        } else {
            mEnumSet = null;
        }
        if (builder.isSetEnumMap()) {
            mEnumMap = builder.mEnumMap.build();
        } else {
            mEnumMap = null;
        }
        if (builder.isSetMessageList()) {
            mMessageList = builder.mMessageList.build();
        } else {
            mMessageList = null;
        }
        if (builder.isSetMessageSet()) {
            mMessageSet = builder.mMessageSet.build();
        } else {
            mMessageSet = null;
        }
        if (builder.isSetMessageMap()) {
            mMessageMap = builder.mMessageMap.build();
        } else {
            mMessageMap = null;
        }
        mRequiredFields = builder.mRequiredFields_builder != null ? builder.mRequiredFields_builder.build() : builder.mRequiredFields;
        mDefaultFields = builder.mDefaultFields_builder != null ? builder.mDefaultFields_builder.build() : builder.mDefaultFields;
        mOptionalFields = builder.mOptionalFields_builder != null ? builder.mOptionalFields_builder.build() : builder.mOptionalFields;
        mUnionFields = builder.mUnionFields_builder != null ? builder.mUnionFields_builder.build() : builder.mUnionFields;
        mExceptionFields = builder.mExceptionFields_builder != null ? builder.mExceptionFields_builder.build() : builder.mExceptionFields;
        mDefaultValues = builder.mDefaultValues_builder != null ? builder.mDefaultValues_builder.build() : builder.mDefaultValues;
    }

    public int numBooleanList() {
        return mBooleanList != null ? mBooleanList.size() : 0;
    }

    public boolean hasBooleanList() {
        return mBooleanList != null;
    }

    /**
     * all types as list&lt;x&gt;.
     *
     * @return The field value
     */
    public java.util.List<Boolean> getBooleanList() {
        return mBooleanList;
    }

    public int numByteList() {
        return mByteList != null ? mByteList.size() : 0;
    }

    public boolean hasByteList() {
        return mByteList != null;
    }

    /**
     * @return The field value
     */
    public java.util.List<Byte> getByteList() {
        return mByteList;
    }

    public int numShortList() {
        return mShortList != null ? mShortList.size() : 0;
    }

    public boolean hasShortList() {
        return mShortList != null;
    }

    /**
     * @return The field value
     */
    public java.util.List<Short> getShortList() {
        return mShortList;
    }

    public int numIntegerList() {
        return mIntegerList != null ? mIntegerList.size() : 0;
    }

    public boolean hasIntegerList() {
        return mIntegerList != null;
    }

    /**
     * @return The field value
     */
    public java.util.List<Integer> getIntegerList() {
        return mIntegerList;
    }

    public int numLongList() {
        return mLongList != null ? mLongList.size() : 0;
    }

    public boolean hasLongList() {
        return mLongList != null;
    }

    /**
     * @return The field value
     */
    public java.util.List<Long> getLongList() {
        return mLongList;
    }

    public int numDoubleList() {
        return mDoubleList != null ? mDoubleList.size() : 0;
    }

    public boolean hasDoubleList() {
        return mDoubleList != null;
    }

    /**
     * @return The field value
     */
    public java.util.List<Double> getDoubleList() {
        return mDoubleList;
    }

    public int numStringList() {
        return mStringList != null ? mStringList.size() : 0;
    }

    public boolean hasStringList() {
        return mStringList != null;
    }

    /**
     * @return The field value
     */
    public java.util.List<String> getStringList() {
        return mStringList;
    }

    public int numBinaryList() {
        return mBinaryList != null ? mBinaryList.size() : 0;
    }

    public boolean hasBinaryList() {
        return mBinaryList != null;
    }

    /**
     * @return The field value
     */
    public java.util.List<net.morimekta.util.Binary> getBinaryList() {
        return mBinaryList;
    }

    public int numBooleanSet() {
        return mBooleanSet != null ? mBooleanSet.size() : 0;
    }

    public boolean hasBooleanSet() {
        return mBooleanSet != null;
    }

    /**
     * all types as set&lt;x&gt;.
     *
     * @return The field value
     */
    public java.util.Set<Boolean> getBooleanSet() {
        return mBooleanSet;
    }

    public int numByteSet() {
        return mByteSet != null ? mByteSet.size() : 0;
    }

    public boolean hasByteSet() {
        return mByteSet != null;
    }

    /**
     * @return The field value
     */
    public java.util.Set<Byte> getByteSet() {
        return mByteSet;
    }

    public int numShortSet() {
        return mShortSet != null ? mShortSet.size() : 0;
    }

    public boolean hasShortSet() {
        return mShortSet != null;
    }

    /**
     * @return The field value
     */
    public java.util.Set<Short> getShortSet() {
        return mShortSet;
    }

    public int numIntegerSet() {
        return mIntegerSet != null ? mIntegerSet.size() : 0;
    }

    public boolean hasIntegerSet() {
        return mIntegerSet != null;
    }

    /**
     * @return The field value
     */
    public java.util.Set<Integer> getIntegerSet() {
        return mIntegerSet;
    }

    public int numLongSet() {
        return mLongSet != null ? mLongSet.size() : 0;
    }

    public boolean hasLongSet() {
        return mLongSet != null;
    }

    /**
     * @return The field value
     */
    public java.util.Set<Long> getLongSet() {
        return mLongSet;
    }

    public int numDoubleSet() {
        return mDoubleSet != null ? mDoubleSet.size() : 0;
    }

    public boolean hasDoubleSet() {
        return mDoubleSet != null;
    }

    /**
     * @return The field value
     */
    public java.util.Set<Double> getDoubleSet() {
        return mDoubleSet;
    }

    public int numStringSet() {
        return mStringSet != null ? mStringSet.size() : 0;
    }

    public boolean hasStringSet() {
        return mStringSet != null;
    }

    /**
     * @return The field value
     */
    public java.util.Set<String> getStringSet() {
        return mStringSet;
    }

    public int numBinarySet() {
        return mBinarySet != null ? mBinarySet.size() : 0;
    }

    public boolean hasBinarySet() {
        return mBinarySet != null;
    }

    /**
     * @return The field value
     */
    public java.util.Set<net.morimekta.util.Binary> getBinarySet() {
        return mBinarySet;
    }

    public int numBooleanMap() {
        return mBooleanMap != null ? mBooleanMap.size() : 0;
    }

    public boolean hasBooleanMap() {
        return mBooleanMap != null;
    }

    /**
     * all types as map&lt;x,x&gt;.
     *
     * @return The field value
     */
    public java.util.Map<Boolean,Boolean> getBooleanMap() {
        return mBooleanMap;
    }

    public int numByteMap() {
        return mByteMap != null ? mByteMap.size() : 0;
    }

    public boolean hasByteMap() {
        return mByteMap != null;
    }

    /**
     * @return The field value
     */
    public java.util.Map<Byte,Byte> getByteMap() {
        return mByteMap;
    }

    public int numShortMap() {
        return mShortMap != null ? mShortMap.size() : 0;
    }

    public boolean hasShortMap() {
        return mShortMap != null;
    }

    /**
     * @return The field value
     */
    public java.util.Map<Short,Short> getShortMap() {
        return mShortMap;
    }

    public int numIntegerMap() {
        return mIntegerMap != null ? mIntegerMap.size() : 0;
    }

    public boolean hasIntegerMap() {
        return mIntegerMap != null;
    }

    /**
     * @return The field value
     */
    public java.util.Map<Integer,Integer> getIntegerMap() {
        return mIntegerMap;
    }

    public int numLongMap() {
        return mLongMap != null ? mLongMap.size() : 0;
    }

    public boolean hasLongMap() {
        return mLongMap != null;
    }

    /**
     * @return The field value
     */
    public java.util.Map<Long,Long> getLongMap() {
        return mLongMap;
    }

    public int numDoubleMap() {
        return mDoubleMap != null ? mDoubleMap.size() : 0;
    }

    public boolean hasDoubleMap() {
        return mDoubleMap != null;
    }

    /**
     * @return The field value
     */
    public java.util.Map<Double,Double> getDoubleMap() {
        return mDoubleMap;
    }

    public int numStringMap() {
        return mStringMap != null ? mStringMap.size() : 0;
    }

    public boolean hasStringMap() {
        return mStringMap != null;
    }

    /**
     * @return The field value
     */
    public java.util.Map<String,String> getStringMap() {
        return mStringMap;
    }

    public int numBinaryMap() {
        return mBinaryMap != null ? mBinaryMap.size() : 0;
    }

    public boolean hasBinaryMap() {
        return mBinaryMap != null;
    }

    /**
     * @return The field value
     */
    public java.util.Map<net.morimekta.util.Binary,net.morimekta.util.Binary> getBinaryMap() {
        return mBinaryMap;
    }

    public int numEnumList() {
        return mEnumList != null ? mEnumList.size() : 0;
    }

    public boolean hasEnumList() {
        return mEnumList != null;
    }

    /**
     * Using enum as key and value in containers.
     *
     * @return The field value
     */
    public java.util.List<net.morimekta.test.providence.Value> getEnumList() {
        return mEnumList;
    }

    public int numEnumSet() {
        return mEnumSet != null ? mEnumSet.size() : 0;
    }

    public boolean hasEnumSet() {
        return mEnumSet != null;
    }

    /**
     * @return The field value
     */
    public java.util.Set<net.morimekta.test.providence.Value> getEnumSet() {
        return mEnumSet;
    }

    public int numEnumMap() {
        return mEnumMap != null ? mEnumMap.size() : 0;
    }

    public boolean hasEnumMap() {
        return mEnumMap != null;
    }

    /**
     * @return The field value
     */
    public java.util.Map<net.morimekta.test.providence.Value,net.morimekta.test.providence.Value> getEnumMap() {
        return mEnumMap;
    }

    public int numMessageList() {
        return mMessageList != null ? mMessageList.size() : 0;
    }

    public boolean hasMessageList() {
        return mMessageList != null;
    }

    /**
     * Using struct as key and value in containers.
     *
     * @return The field value
     */
    public java.util.List<net.morimekta.test.providence.DefaultFields> getMessageList() {
        return mMessageList;
    }

    public int numMessageSet() {
        return mMessageSet != null ? mMessageSet.size() : 0;
    }

    public boolean hasMessageSet() {
        return mMessageSet != null;
    }

    /**
     * @return The field value
     */
    public java.util.Set<net.morimekta.test.providence.DefaultFields> getMessageSet() {
        return mMessageSet;
    }

    public int numMessageMap() {
        return mMessageMap != null ? mMessageMap.size() : 0;
    }

    public boolean hasMessageMap() {
        return mMessageMap != null;
    }

    /**
     * @return The field value
     */
    public java.util.Map<String,net.morimekta.test.providence.DefaultFields> getMessageMap() {
        return mMessageMap;
    }

    public boolean hasRequiredFields() {
        return mRequiredFields != null;
    }

    /**
     * @return The field value
     */
    public net.morimekta.test.providence.RequiredFields getRequiredFields() {
        return mRequiredFields;
    }

    public boolean hasDefaultFields() {
        return mDefaultFields != null;
    }

    /**
     * @return The field value
     */
    public net.morimekta.test.providence.DefaultFields getDefaultFields() {
        return mDefaultFields;
    }

    public boolean hasOptionalFields() {
        return mOptionalFields != null;
    }

    /**
     * @return The field value
     */
    public net.morimekta.test.providence.OptionalFields getOptionalFields() {
        return mOptionalFields;
    }

    public boolean hasUnionFields() {
        return mUnionFields != null;
    }

    /**
     * @return The field value
     */
    public net.morimekta.test.providence.UnionFields getUnionFields() {
        return mUnionFields;
    }

    public boolean hasExceptionFields() {
        return mExceptionFields != null;
    }

    /**
     * @return The field value
     */
    public net.morimekta.test.providence.ExceptionFields getExceptionFields() {
        return mExceptionFields;
    }

    public boolean hasDefaultValues() {
        return mDefaultValues != null;
    }

    /**
     * @return The field value
     */
    public net.morimekta.test.providence.DefaultValues getDefaultValues() {
        return mDefaultValues;
    }

    @Override
    public boolean has(int key) {
        switch(key) {
            case 1: return hasBooleanList();
            case 2: return hasByteList();
            case 3: return hasShortList();
            case 4: return hasIntegerList();
            case 5: return hasLongList();
            case 6: return hasDoubleList();
            case 7: return hasStringList();
            case 8: return hasBinaryList();
            case 11: return hasBooleanSet();
            case 12: return hasByteSet();
            case 13: return hasShortSet();
            case 14: return hasIntegerSet();
            case 15: return hasLongSet();
            case 16: return hasDoubleSet();
            case 17: return hasStringSet();
            case 18: return hasBinarySet();
            case 21: return hasBooleanMap();
            case 22: return hasByteMap();
            case 23: return hasShortMap();
            case 24: return hasIntegerMap();
            case 25: return hasLongMap();
            case 26: return hasDoubleMap();
            case 27: return hasStringMap();
            case 28: return hasBinaryMap();
            case 31: return hasEnumList();
            case 32: return hasEnumSet();
            case 33: return hasEnumMap();
            case 41: return hasMessageList();
            case 42: return hasMessageSet();
            case 43: return hasMessageMap();
            case 51: return hasRequiredFields();
            case 52: return hasDefaultFields();
            case 53: return hasOptionalFields();
            case 54: return hasUnionFields();
            case 55: return hasExceptionFields();
            case 56: return hasDefaultValues();
            default: return false;
        }
    }

    @Override
    public int num(int key) {
        switch(key) {
            case 1: return numBooleanList();
            case 2: return numByteList();
            case 3: return numShortList();
            case 4: return numIntegerList();
            case 5: return numLongList();
            case 6: return numDoubleList();
            case 7: return numStringList();
            case 8: return numBinaryList();
            case 11: return numBooleanSet();
            case 12: return numByteSet();
            case 13: return numShortSet();
            case 14: return numIntegerSet();
            case 15: return numLongSet();
            case 16: return numDoubleSet();
            case 17: return numStringSet();
            case 18: return numBinarySet();
            case 21: return numBooleanMap();
            case 22: return numByteMap();
            case 23: return numShortMap();
            case 24: return numIntegerMap();
            case 25: return numLongMap();
            case 26: return numDoubleMap();
            case 27: return numStringMap();
            case 28: return numBinaryMap();
            case 31: return numEnumList();
            case 32: return numEnumSet();
            case 33: return numEnumMap();
            case 41: return numMessageList();
            case 42: return numMessageSet();
            case 43: return numMessageMap();
            case 51: return hasRequiredFields() ? 1 : 0;
            case 52: return hasDefaultFields() ? 1 : 0;
            case 53: return hasOptionalFields() ? 1 : 0;
            case 54: return hasUnionFields() ? 1 : 0;
            case 55: return hasExceptionFields() ? 1 : 0;
            case 56: return hasDefaultValues() ? 1 : 0;
            default: return 0;
        }
    }

    @Override
    public Object get(int key) {
        switch(key) {
            case 1: return getBooleanList();
            case 2: return getByteList();
            case 3: return getShortList();
            case 4: return getIntegerList();
            case 5: return getLongList();
            case 6: return getDoubleList();
            case 7: return getStringList();
            case 8: return getBinaryList();
            case 11: return getBooleanSet();
            case 12: return getByteSet();
            case 13: return getShortSet();
            case 14: return getIntegerSet();
            case 15: return getLongSet();
            case 16: return getDoubleSet();
            case 17: return getStringSet();
            case 18: return getBinarySet();
            case 21: return getBooleanMap();
            case 22: return getByteMap();
            case 23: return getShortMap();
            case 24: return getIntegerMap();
            case 25: return getLongMap();
            case 26: return getDoubleMap();
            case 27: return getStringMap();
            case 28: return getBinaryMap();
            case 31: return getEnumList();
            case 32: return getEnumSet();
            case 33: return getEnumMap();
            case 41: return getMessageList();
            case 42: return getMessageSet();
            case 43: return getMessageMap();
            case 51: return getRequiredFields();
            case 52: return getDefaultFields();
            case 53: return getOptionalFields();
            case 54: return getUnionFields();
            case 55: return getExceptionFields();
            case 56: return getDefaultValues();
            default: return null;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        if (o == null || !o.getClass().equals(getClass())) return false;
        Containers other = (Containers) o;
        return java.util.Objects.equals(mBooleanList, other.mBooleanList) &&
               java.util.Objects.equals(mByteList, other.mByteList) &&
               java.util.Objects.equals(mShortList, other.mShortList) &&
               java.util.Objects.equals(mIntegerList, other.mIntegerList) &&
               java.util.Objects.equals(mLongList, other.mLongList) &&
               java.util.Objects.equals(mDoubleList, other.mDoubleList) &&
               java.util.Objects.equals(mStringList, other.mStringList) &&
               java.util.Objects.equals(mBinaryList, other.mBinaryList) &&
               java.util.Objects.equals(mBooleanSet, other.mBooleanSet) &&
               java.util.Objects.equals(mByteSet, other.mByteSet) &&
               java.util.Objects.equals(mShortSet, other.mShortSet) &&
               java.util.Objects.equals(mIntegerSet, other.mIntegerSet) &&
               java.util.Objects.equals(mLongSet, other.mLongSet) &&
               java.util.Objects.equals(mDoubleSet, other.mDoubleSet) &&
               java.util.Objects.equals(mStringSet, other.mStringSet) &&
               java.util.Objects.equals(mBinarySet, other.mBinarySet) &&
               java.util.Objects.equals(mBooleanMap, other.mBooleanMap) &&
               java.util.Objects.equals(mByteMap, other.mByteMap) &&
               java.util.Objects.equals(mShortMap, other.mShortMap) &&
               java.util.Objects.equals(mIntegerMap, other.mIntegerMap) &&
               java.util.Objects.equals(mLongMap, other.mLongMap) &&
               java.util.Objects.equals(mDoubleMap, other.mDoubleMap) &&
               java.util.Objects.equals(mStringMap, other.mStringMap) &&
               java.util.Objects.equals(mBinaryMap, other.mBinaryMap) &&
               java.util.Objects.equals(mEnumList, other.mEnumList) &&
               java.util.Objects.equals(mEnumSet, other.mEnumSet) &&
               java.util.Objects.equals(mEnumMap, other.mEnumMap) &&
               java.util.Objects.equals(mMessageList, other.mMessageList) &&
               java.util.Objects.equals(mMessageSet, other.mMessageSet) &&
               java.util.Objects.equals(mMessageMap, other.mMessageMap) &&
               java.util.Objects.equals(mRequiredFields, other.mRequiredFields) &&
               java.util.Objects.equals(mDefaultFields, other.mDefaultFields) &&
               java.util.Objects.equals(mOptionalFields, other.mOptionalFields) &&
               java.util.Objects.equals(mUnionFields, other.mUnionFields) &&
               java.util.Objects.equals(mExceptionFields, other.mExceptionFields) &&
               java.util.Objects.equals(mDefaultValues, other.mDefaultValues);
    }

    @Override
    public int hashCode() {
        if (tHashCode == 0) {
            tHashCode = java.util.Objects.hash(
                    Containers.class,
                    _Field.BOOLEAN_LIST, mBooleanList,
                    _Field.BYTE_LIST, mByteList,
                    _Field.SHORT_LIST, mShortList,
                    _Field.INTEGER_LIST, mIntegerList,
                    _Field.LONG_LIST, mLongList,
                    _Field.DOUBLE_LIST, mDoubleList,
                    _Field.STRING_LIST, mStringList,
                    _Field.BINARY_LIST, mBinaryList,
                    _Field.BOOLEAN_SET, mBooleanSet,
                    _Field.BYTE_SET, mByteSet,
                    _Field.SHORT_SET, mShortSet,
                    _Field.INTEGER_SET, mIntegerSet,
                    _Field.LONG_SET, mLongSet,
                    _Field.DOUBLE_SET, mDoubleSet,
                    _Field.STRING_SET, mStringSet,
                    _Field.BINARY_SET, mBinarySet,
                    _Field.BOOLEAN_MAP, mBooleanMap,
                    _Field.BYTE_MAP, mByteMap,
                    _Field.SHORT_MAP, mShortMap,
                    _Field.INTEGER_MAP, mIntegerMap,
                    _Field.LONG_MAP, mLongMap,
                    _Field.DOUBLE_MAP, mDoubleMap,
                    _Field.STRING_MAP, mStringMap,
                    _Field.BINARY_MAP, mBinaryMap,
                    _Field.ENUM_LIST, mEnumList,
                    _Field.ENUM_SET, mEnumSet,
                    _Field.ENUM_MAP, mEnumMap,
                    _Field.MESSAGE_LIST, mMessageList,
                    _Field.MESSAGE_SET, mMessageSet,
                    _Field.MESSAGE_MAP, mMessageMap,
                    _Field.REQUIRED_FIELDS, mRequiredFields,
                    _Field.DEFAULT_FIELDS, mDefaultFields,
                    _Field.OPTIONAL_FIELDS, mOptionalFields,
                    _Field.UNION_FIELDS, mUnionFields,
                    _Field.EXCEPTION_FIELDS, mExceptionFields,
                    _Field.DEFAULT_VALUES, mDefaultValues);
        }
        return tHashCode;
    }

    @Override
    public String toString() {
        return "providence.Containers" + asString();
    }

    @Override
    public String asString() {
        StringBuilder out = new StringBuilder();
        out.append("{");

        boolean first = true;
        if (hasBooleanList()) {
            first = false;
            out.append("booleanList:")
               .append(net.morimekta.util.Strings.asString(mBooleanList));
        }
        if (hasByteList()) {
            if (first) first = false;
            else out.append(',');
            out.append("byteList:")
               .append(net.morimekta.util.Strings.asString(mByteList));
        }
        if (hasShortList()) {
            if (first) first = false;
            else out.append(',');
            out.append("shortList:")
               .append(net.morimekta.util.Strings.asString(mShortList));
        }
        if (hasIntegerList()) {
            if (first) first = false;
            else out.append(',');
            out.append("integerList:")
               .append(net.morimekta.util.Strings.asString(mIntegerList));
        }
        if (hasLongList()) {
            if (first) first = false;
            else out.append(',');
            out.append("longList:")
               .append(net.morimekta.util.Strings.asString(mLongList));
        }
        if (hasDoubleList()) {
            if (first) first = false;
            else out.append(',');
            out.append("doubleList:")
               .append(net.morimekta.util.Strings.asString(mDoubleList));
        }
        if (hasStringList()) {
            if (first) first = false;
            else out.append(',');
            out.append("stringList:")
               .append(net.morimekta.util.Strings.asString(mStringList));
        }
        if (hasBinaryList()) {
            if (first) first = false;
            else out.append(',');
            out.append("binaryList:")
               .append(net.morimekta.util.Strings.asString(mBinaryList));
        }
        if (hasBooleanSet()) {
            if (first) first = false;
            else out.append(',');
            out.append("booleanSet:")
               .append(net.morimekta.util.Strings.asString(mBooleanSet));
        }
        if (hasByteSet()) {
            if (first) first = false;
            else out.append(',');
            out.append("byteSet:")
               .append(net.morimekta.util.Strings.asString(mByteSet));
        }
        if (hasShortSet()) {
            if (first) first = false;
            else out.append(',');
            out.append("shortSet:")
               .append(net.morimekta.util.Strings.asString(mShortSet));
        }
        if (hasIntegerSet()) {
            if (first) first = false;
            else out.append(',');
            out.append("integerSet:")
               .append(net.morimekta.util.Strings.asString(mIntegerSet));
        }
        if (hasLongSet()) {
            if (first) first = false;
            else out.append(',');
            out.append("longSet:")
               .append(net.morimekta.util.Strings.asString(mLongSet));
        }
        if (hasDoubleSet()) {
            if (first) first = false;
            else out.append(',');
            out.append("doubleSet:")
               .append(net.morimekta.util.Strings.asString(mDoubleSet));
        }
        if (hasStringSet()) {
            if (first) first = false;
            else out.append(',');
            out.append("stringSet:")
               .append(net.morimekta.util.Strings.asString(mStringSet));
        }
        if (hasBinarySet()) {
            if (first) first = false;
            else out.append(',');
            out.append("binarySet:")
               .append(net.morimekta.util.Strings.asString(mBinarySet));
        }
        if (hasBooleanMap()) {
            if (first) first = false;
            else out.append(',');
            out.append("booleanMap:")
               .append(net.morimekta.util.Strings.asString(mBooleanMap));
        }
        if (hasByteMap()) {
            if (first) first = false;
            else out.append(',');
            out.append("byteMap:")
               .append(net.morimekta.util.Strings.asString(mByteMap));
        }
        if (hasShortMap()) {
            if (first) first = false;
            else out.append(',');
            out.append("shortMap:")
               .append(net.morimekta.util.Strings.asString(mShortMap));
        }
        if (hasIntegerMap()) {
            if (first) first = false;
            else out.append(',');
            out.append("integerMap:")
               .append(net.morimekta.util.Strings.asString(mIntegerMap));
        }
        if (hasLongMap()) {
            if (first) first = false;
            else out.append(',');
            out.append("longMap:")
               .append(net.morimekta.util.Strings.asString(mLongMap));
        }
        if (hasDoubleMap()) {
            if (first) first = false;
            else out.append(',');
            out.append("doubleMap:")
               .append(net.morimekta.util.Strings.asString(mDoubleMap));
        }
        if (hasStringMap()) {
            if (first) first = false;
            else out.append(',');
            out.append("stringMap:")
               .append(net.morimekta.util.Strings.asString(mStringMap));
        }
        if (hasBinaryMap()) {
            if (first) first = false;
            else out.append(',');
            out.append("binaryMap:")
               .append(net.morimekta.util.Strings.asString(mBinaryMap));
        }
        if (hasEnumList()) {
            if (first) first = false;
            else out.append(',');
            out.append("enumList:")
               .append(net.morimekta.util.Strings.asString(mEnumList));
        }
        if (hasEnumSet()) {
            if (first) first = false;
            else out.append(',');
            out.append("enumSet:")
               .append(net.morimekta.util.Strings.asString(mEnumSet));
        }
        if (hasEnumMap()) {
            if (first) first = false;
            else out.append(',');
            out.append("enumMap:")
               .append(net.morimekta.util.Strings.asString(mEnumMap));
        }
        if (hasMessageList()) {
            if (first) first = false;
            else out.append(',');
            out.append("messageList:")
               .append(net.morimekta.util.Strings.asString(mMessageList));
        }
        if (hasMessageSet()) {
            if (first) first = false;
            else out.append(',');
            out.append("messageSet:")
               .append(net.morimekta.util.Strings.asString(mMessageSet));
        }
        if (hasMessageMap()) {
            if (first) first = false;
            else out.append(',');
            out.append("messageMap:")
               .append(net.morimekta.util.Strings.asString(mMessageMap));
        }
        if (hasRequiredFields()) {
            if (first) first = false;
            else out.append(',');
            out.append("requiredFields:")
               .append(mRequiredFields.asString());
        }
        if (hasDefaultFields()) {
            if (first) first = false;
            else out.append(',');
            out.append("defaultFields:")
               .append(mDefaultFields.asString());
        }
        if (hasOptionalFields()) {
            if (first) first = false;
            else out.append(',');
            out.append("optionalFields:")
               .append(mOptionalFields.asString());
        }
        if (hasUnionFields()) {
            if (first) first = false;
            else out.append(',');
            out.append("unionFields:")
               .append(mUnionFields.asString());
        }
        if (hasExceptionFields()) {
            if (first) first = false;
            else out.append(',');
            out.append("exceptionFields:")
               .append(mExceptionFields.asString());
        }
        if (hasDefaultValues()) {
            if (!first) out.append(',');
            out.append("defaultValues:")
               .append(mDefaultValues.asString());
        }
        out.append('}');
        return out.toString();
    }

    @Override
    public int compareTo(Containers other) {
        int c;

        c = Boolean.compare(mBooleanList != null, other.mBooleanList != null);
        if (c != 0) return c;
        if (mBooleanList != null) {
            c = Integer.compare(mBooleanList.hashCode(), other.mBooleanList.hashCode());
            if (c != 0) return c;
        }

        c = Boolean.compare(mByteList != null, other.mByteList != null);
        if (c != 0) return c;
        if (mByteList != null) {
            c = Integer.compare(mByteList.hashCode(), other.mByteList.hashCode());
            if (c != 0) return c;
        }

        c = Boolean.compare(mShortList != null, other.mShortList != null);
        if (c != 0) return c;
        if (mShortList != null) {
            c = Integer.compare(mShortList.hashCode(), other.mShortList.hashCode());
            if (c != 0) return c;
        }

        c = Boolean.compare(mIntegerList != null, other.mIntegerList != null);
        if (c != 0) return c;
        if (mIntegerList != null) {
            c = Integer.compare(mIntegerList.hashCode(), other.mIntegerList.hashCode());
            if (c != 0) return c;
        }

        c = Boolean.compare(mLongList != null, other.mLongList != null);
        if (c != 0) return c;
        if (mLongList != null) {
            c = Integer.compare(mLongList.hashCode(), other.mLongList.hashCode());
            if (c != 0) return c;
        }

        c = Boolean.compare(mDoubleList != null, other.mDoubleList != null);
        if (c != 0) return c;
        if (mDoubleList != null) {
            c = Integer.compare(mDoubleList.hashCode(), other.mDoubleList.hashCode());
            if (c != 0) return c;
        }

        c = Boolean.compare(mStringList != null, other.mStringList != null);
        if (c != 0) return c;
        if (mStringList != null) {
            c = Integer.compare(mStringList.hashCode(), other.mStringList.hashCode());
            if (c != 0) return c;
        }

        c = Boolean.compare(mBinaryList != null, other.mBinaryList != null);
        if (c != 0) return c;
        if (mBinaryList != null) {
            c = Integer.compare(mBinaryList.hashCode(), other.mBinaryList.hashCode());
            if (c != 0) return c;
        }

        c = Boolean.compare(mBooleanSet != null, other.mBooleanSet != null);
        if (c != 0) return c;
        if (mBooleanSet != null) {
            c = Integer.compare(mBooleanSet.hashCode(), other.mBooleanSet.hashCode());
            if (c != 0) return c;
        }

        c = Boolean.compare(mByteSet != null, other.mByteSet != null);
        if (c != 0) return c;
        if (mByteSet != null) {
            c = Integer.compare(mByteSet.hashCode(), other.mByteSet.hashCode());
            if (c != 0) return c;
        }

        c = Boolean.compare(mShortSet != null, other.mShortSet != null);
        if (c != 0) return c;
        if (mShortSet != null) {
            c = Integer.compare(mShortSet.hashCode(), other.mShortSet.hashCode());
            if (c != 0) return c;
        }

        c = Boolean.compare(mIntegerSet != null, other.mIntegerSet != null);
        if (c != 0) return c;
        if (mIntegerSet != null) {
            c = Integer.compare(mIntegerSet.hashCode(), other.mIntegerSet.hashCode());
            if (c != 0) return c;
        }

        c = Boolean.compare(mLongSet != null, other.mLongSet != null);
        if (c != 0) return c;
        if (mLongSet != null) {
            c = Integer.compare(mLongSet.hashCode(), other.mLongSet.hashCode());
            if (c != 0) return c;
        }

        c = Boolean.compare(mDoubleSet != null, other.mDoubleSet != null);
        if (c != 0) return c;
        if (mDoubleSet != null) {
            c = Integer.compare(mDoubleSet.hashCode(), other.mDoubleSet.hashCode());
            if (c != 0) return c;
        }

        c = Boolean.compare(mStringSet != null, other.mStringSet != null);
        if (c != 0) return c;
        if (mStringSet != null) {
            c = Integer.compare(mStringSet.hashCode(), other.mStringSet.hashCode());
            if (c != 0) return c;
        }

        c = Boolean.compare(mBinarySet != null, other.mBinarySet != null);
        if (c != 0) return c;
        if (mBinarySet != null) {
            c = Integer.compare(mBinarySet.hashCode(), other.mBinarySet.hashCode());
            if (c != 0) return c;
        }

        c = Boolean.compare(mBooleanMap != null, other.mBooleanMap != null);
        if (c != 0) return c;
        if (mBooleanMap != null) {
            c = Integer.compare(mBooleanMap.hashCode(), other.mBooleanMap.hashCode());
            if (c != 0) return c;
        }

        c = Boolean.compare(mByteMap != null, other.mByteMap != null);
        if (c != 0) return c;
        if (mByteMap != null) {
            c = Integer.compare(mByteMap.hashCode(), other.mByteMap.hashCode());
            if (c != 0) return c;
        }

        c = Boolean.compare(mShortMap != null, other.mShortMap != null);
        if (c != 0) return c;
        if (mShortMap != null) {
            c = Integer.compare(mShortMap.hashCode(), other.mShortMap.hashCode());
            if (c != 0) return c;
        }

        c = Boolean.compare(mIntegerMap != null, other.mIntegerMap != null);
        if (c != 0) return c;
        if (mIntegerMap != null) {
            c = Integer.compare(mIntegerMap.hashCode(), other.mIntegerMap.hashCode());
            if (c != 0) return c;
        }

        c = Boolean.compare(mLongMap != null, other.mLongMap != null);
        if (c != 0) return c;
        if (mLongMap != null) {
            c = Integer.compare(mLongMap.hashCode(), other.mLongMap.hashCode());
            if (c != 0) return c;
        }

        c = Boolean.compare(mDoubleMap != null, other.mDoubleMap != null);
        if (c != 0) return c;
        if (mDoubleMap != null) {
            c = Integer.compare(mDoubleMap.hashCode(), other.mDoubleMap.hashCode());
            if (c != 0) return c;
        }

        c = Boolean.compare(mStringMap != null, other.mStringMap != null);
        if (c != 0) return c;
        if (mStringMap != null) {
            c = Integer.compare(mStringMap.hashCode(), other.mStringMap.hashCode());
            if (c != 0) return c;
        }

        c = Boolean.compare(mBinaryMap != null, other.mBinaryMap != null);
        if (c != 0) return c;
        if (mBinaryMap != null) {
            c = Integer.compare(mBinaryMap.hashCode(), other.mBinaryMap.hashCode());
            if (c != 0) return c;
        }

        c = Boolean.compare(mEnumList != null, other.mEnumList != null);
        if (c != 0) return c;
        if (mEnumList != null) {
            c = Integer.compare(mEnumList.hashCode(), other.mEnumList.hashCode());
            if (c != 0) return c;
        }

        c = Boolean.compare(mEnumSet != null, other.mEnumSet != null);
        if (c != 0) return c;
        if (mEnumSet != null) {
            c = Integer.compare(mEnumSet.hashCode(), other.mEnumSet.hashCode());
            if (c != 0) return c;
        }

        c = Boolean.compare(mEnumMap != null, other.mEnumMap != null);
        if (c != 0) return c;
        if (mEnumMap != null) {
            c = Integer.compare(mEnumMap.hashCode(), other.mEnumMap.hashCode());
            if (c != 0) return c;
        }

        c = Boolean.compare(mMessageList != null, other.mMessageList != null);
        if (c != 0) return c;
        if (mMessageList != null) {
            c = Integer.compare(mMessageList.hashCode(), other.mMessageList.hashCode());
            if (c != 0) return c;
        }

        c = Boolean.compare(mMessageSet != null, other.mMessageSet != null);
        if (c != 0) return c;
        if (mMessageSet != null) {
            c = Integer.compare(mMessageSet.hashCode(), other.mMessageSet.hashCode());
            if (c != 0) return c;
        }

        c = Boolean.compare(mMessageMap != null, other.mMessageMap != null);
        if (c != 0) return c;
        if (mMessageMap != null) {
            c = Integer.compare(mMessageMap.hashCode(), other.mMessageMap.hashCode());
            if (c != 0) return c;
        }

        c = Boolean.compare(mRequiredFields != null, other.mRequiredFields != null);
        if (c != 0) return c;
        if (mRequiredFields != null) {
            c = mRequiredFields.compareTo(other.mRequiredFields);
            if (c != 0) return c;
        }

        c = Boolean.compare(mDefaultFields != null, other.mDefaultFields != null);
        if (c != 0) return c;
        if (mDefaultFields != null) {
            c = mDefaultFields.compareTo(other.mDefaultFields);
            if (c != 0) return c;
        }

        c = Boolean.compare(mOptionalFields != null, other.mOptionalFields != null);
        if (c != 0) return c;
        if (mOptionalFields != null) {
            c = mOptionalFields.compareTo(other.mOptionalFields);
            if (c != 0) return c;
        }

        c = Boolean.compare(mUnionFields != null, other.mUnionFields != null);
        if (c != 0) return c;
        if (mUnionFields != null) {
            c = mUnionFields.compareTo(other.mUnionFields);
            if (c != 0) return c;
        }

        c = Boolean.compare(mExceptionFields != null, other.mExceptionFields != null);
        if (c != 0) return c;
        if (mExceptionFields != null) {
            c = mExceptionFields.compareTo(other.mExceptionFields);
            if (c != 0) return c;
        }

        c = Boolean.compare(mDefaultValues != null, other.mDefaultValues != null);
        if (c != 0) return c;
        if (mDefaultValues != null) {
            c = mDefaultValues.compareTo(other.mDefaultValues);
            if (c != 0) return c;
        }

        return 0;
    }

    @Override
    public int writeBinary(net.morimekta.util.io.BigEndianBinaryWriter writer) throws java.io.IOException {
        int length = 0;

        if (hasBooleanList()) {
            length += writer.writeByte((byte) 15);
            length += writer.writeShort((short) 1);
            length += writer.writeByte((byte) 2);
            length += writer.writeUInt32(mBooleanList.size());
            for (Boolean entry_1 : mBooleanList) {
                length += writer.writeUInt8(entry_1 ? (byte) 1 : (byte) 0);
            }
        }

        if (hasByteList()) {
            length += writer.writeByte((byte) 15);
            length += writer.writeShort((short) 2);
            length += writer.writeByte((byte) 3);
            length += writer.writeUInt32(mByteList.size());
            for (Byte entry_2 : mByteList) {
                length += writer.writeByte(entry_2);
            }
        }

        if (hasShortList()) {
            length += writer.writeByte((byte) 15);
            length += writer.writeShort((short) 3);
            length += writer.writeByte((byte) 6);
            length += writer.writeUInt32(mShortList.size());
            for (Short entry_3 : mShortList) {
                length += writer.writeShort(entry_3);
            }
        }

        if (hasIntegerList()) {
            length += writer.writeByte((byte) 15);
            length += writer.writeShort((short) 4);
            length += writer.writeByte((byte) 8);
            length += writer.writeUInt32(mIntegerList.size());
            for (Integer entry_4 : mIntegerList) {
                length += writer.writeInt(entry_4);
            }
        }

        if (hasLongList()) {
            length += writer.writeByte((byte) 15);
            length += writer.writeShort((short) 5);
            length += writer.writeByte((byte) 10);
            length += writer.writeUInt32(mLongList.size());
            for (Long entry_5 : mLongList) {
                length += writer.writeLong(entry_5);
            }
        }

        if (hasDoubleList()) {
            length += writer.writeByte((byte) 15);
            length += writer.writeShort((short) 6);
            length += writer.writeByte((byte) 4);
            length += writer.writeUInt32(mDoubleList.size());
            for (Double entry_6 : mDoubleList) {
                length += writer.writeDouble(entry_6);
            }
        }

        if (hasStringList()) {
            length += writer.writeByte((byte) 15);
            length += writer.writeShort((short) 7);
            length += writer.writeByte((byte) 11);
            length += writer.writeUInt32(mStringList.size());
            for (String entry_7 : mStringList) {
                net.morimekta.util.Binary tmp_8 = net.morimekta.util.Binary.wrap(entry_7.getBytes(java.nio.charset.StandardCharsets.UTF_8));
                length += writer.writeUInt32(tmp_8.length());
                length += writer.writeBinary(tmp_8);
            }
        }

        if (hasBinaryList()) {
            length += writer.writeByte((byte) 15);
            length += writer.writeShort((short) 8);
            length += writer.writeByte((byte) 11);
            length += writer.writeUInt32(mBinaryList.size());
            for (net.morimekta.util.Binary entry_9 : mBinaryList) {
                length += writer.writeUInt32(entry_9.length());
                length += writer.writeBinary(entry_9);
            }
        }

        if (hasBooleanSet()) {
            length += writer.writeByte((byte) 14);
            length += writer.writeShort((short) 11);
            length += writer.writeByte((byte) 2);
            length += writer.writeUInt32(mBooleanSet.size());
            for (Boolean entry_10 : mBooleanSet) {
                length += writer.writeUInt8(entry_10 ? (byte) 1 : (byte) 0);
            }
        }

        if (hasByteSet()) {
            length += writer.writeByte((byte) 14);
            length += writer.writeShort((short) 12);
            length += writer.writeByte((byte) 3);
            length += writer.writeUInt32(mByteSet.size());
            for (Byte entry_11 : mByteSet) {
                length += writer.writeByte(entry_11);
            }
        }

        if (hasShortSet()) {
            length += writer.writeByte((byte) 14);
            length += writer.writeShort((short) 13);
            length += writer.writeByte((byte) 6);
            length += writer.writeUInt32(mShortSet.size());
            for (Short entry_12 : mShortSet) {
                length += writer.writeShort(entry_12);
            }
        }

        if (hasIntegerSet()) {
            length += writer.writeByte((byte) 14);
            length += writer.writeShort((short) 14);
            length += writer.writeByte((byte) 8);
            length += writer.writeUInt32(mIntegerSet.size());
            for (Integer entry_13 : mIntegerSet) {
                length += writer.writeInt(entry_13);
            }
        }

        if (hasLongSet()) {
            length += writer.writeByte((byte) 14);
            length += writer.writeShort((short) 15);
            length += writer.writeByte((byte) 10);
            length += writer.writeUInt32(mLongSet.size());
            for (Long entry_14 : mLongSet) {
                length += writer.writeLong(entry_14);
            }
        }

        if (hasDoubleSet()) {
            length += writer.writeByte((byte) 14);
            length += writer.writeShort((short) 16);
            length += writer.writeByte((byte) 4);
            length += writer.writeUInt32(mDoubleSet.size());
            for (Double entry_15 : mDoubleSet) {
                length += writer.writeDouble(entry_15);
            }
        }

        if (hasStringSet()) {
            length += writer.writeByte((byte) 14);
            length += writer.writeShort((short) 17);
            length += writer.writeByte((byte) 11);
            length += writer.writeUInt32(mStringSet.size());
            for (String entry_16 : mStringSet) {
                net.morimekta.util.Binary tmp_17 = net.morimekta.util.Binary.wrap(entry_16.getBytes(java.nio.charset.StandardCharsets.UTF_8));
                length += writer.writeUInt32(tmp_17.length());
                length += writer.writeBinary(tmp_17);
            }
        }

        if (hasBinarySet()) {
            length += writer.writeByte((byte) 14);
            length += writer.writeShort((short) 18);
            length += writer.writeByte((byte) 11);
            length += writer.writeUInt32(mBinarySet.size());
            for (net.morimekta.util.Binary entry_18 : mBinarySet) {
                length += writer.writeUInt32(entry_18.length());
                length += writer.writeBinary(entry_18);
            }
        }

        if (hasBooleanMap()) {
            length += writer.writeByte((byte) 13);
            length += writer.writeShort((short) 21);
            length += writer.writeByte((byte) 2);
            length += writer.writeByte((byte) 2);
            length += writer.writeUInt32(mBooleanMap.size());
            for (java.util.Map.Entry<Boolean,Boolean> entry_19 : mBooleanMap.entrySet()) {
                length += writer.writeUInt8(entry_19.getKey() ? (byte) 1 : (byte) 0);
                length += writer.writeUInt8(entry_19.getValue() ? (byte) 1 : (byte) 0);
            }
        }

        if (hasByteMap()) {
            length += writer.writeByte((byte) 13);
            length += writer.writeShort((short) 22);
            length += writer.writeByte((byte) 3);
            length += writer.writeByte((byte) 3);
            length += writer.writeUInt32(mByteMap.size());
            for (java.util.Map.Entry<Byte,Byte> entry_20 : mByteMap.entrySet()) {
                length += writer.writeByte(entry_20.getKey());
                length += writer.writeByte(entry_20.getValue());
            }
        }

        if (hasShortMap()) {
            length += writer.writeByte((byte) 13);
            length += writer.writeShort((short) 23);
            length += writer.writeByte((byte) 6);
            length += writer.writeByte((byte) 6);
            length += writer.writeUInt32(mShortMap.size());
            for (java.util.Map.Entry<Short,Short> entry_21 : mShortMap.entrySet()) {
                length += writer.writeShort(entry_21.getKey());
                length += writer.writeShort(entry_21.getValue());
            }
        }

        if (hasIntegerMap()) {
            length += writer.writeByte((byte) 13);
            length += writer.writeShort((short) 24);
            length += writer.writeByte((byte) 8);
            length += writer.writeByte((byte) 8);
            length += writer.writeUInt32(mIntegerMap.size());
            for (java.util.Map.Entry<Integer,Integer> entry_22 : mIntegerMap.entrySet()) {
                length += writer.writeInt(entry_22.getKey());
                length += writer.writeInt(entry_22.getValue());
            }
        }

        if (hasLongMap()) {
            length += writer.writeByte((byte) 13);
            length += writer.writeShort((short) 25);
            length += writer.writeByte((byte) 10);
            length += writer.writeByte((byte) 10);
            length += writer.writeUInt32(mLongMap.size());
            for (java.util.Map.Entry<Long,Long> entry_23 : mLongMap.entrySet()) {
                length += writer.writeLong(entry_23.getKey());
                length += writer.writeLong(entry_23.getValue());
            }
        }

        if (hasDoubleMap()) {
            length += writer.writeByte((byte) 13);
            length += writer.writeShort((short) 26);
            length += writer.writeByte((byte) 4);
            length += writer.writeByte((byte) 4);
            length += writer.writeUInt32(mDoubleMap.size());
            for (java.util.Map.Entry<Double,Double> entry_24 : mDoubleMap.entrySet()) {
                length += writer.writeDouble(entry_24.getKey());
                length += writer.writeDouble(entry_24.getValue());
            }
        }

        if (hasStringMap()) {
            length += writer.writeByte((byte) 13);
            length += writer.writeShort((short) 27);
            length += writer.writeByte((byte) 11);
            length += writer.writeByte((byte) 11);
            length += writer.writeUInt32(mStringMap.size());
            for (java.util.Map.Entry<String,String> entry_25 : mStringMap.entrySet()) {
                net.morimekta.util.Binary tmp_26 = net.morimekta.util.Binary.wrap(entry_25.getKey().getBytes(java.nio.charset.StandardCharsets.UTF_8));
                length += writer.writeUInt32(tmp_26.length());
                length += writer.writeBinary(tmp_26);
                net.morimekta.util.Binary tmp_27 = net.morimekta.util.Binary.wrap(entry_25.getValue().getBytes(java.nio.charset.StandardCharsets.UTF_8));
                length += writer.writeUInt32(tmp_27.length());
                length += writer.writeBinary(tmp_27);
            }
        }

        if (hasBinaryMap()) {
            length += writer.writeByte((byte) 13);
            length += writer.writeShort((short) 28);
            length += writer.writeByte((byte) 11);
            length += writer.writeByte((byte) 11);
            length += writer.writeUInt32(mBinaryMap.size());
            for (java.util.Map.Entry<net.morimekta.util.Binary,net.morimekta.util.Binary> entry_28 : mBinaryMap.entrySet()) {
                length += writer.writeUInt32(entry_28.getKey().length());
                length += writer.writeBinary(entry_28.getKey());
                length += writer.writeUInt32(entry_28.getValue().length());
                length += writer.writeBinary(entry_28.getValue());
            }
        }

        if (hasEnumList()) {
            length += writer.writeByte((byte) 15);
            length += writer.writeShort((short) 31);
            length += writer.writeByte((byte) 8);
            length += writer.writeUInt32(mEnumList.size());
            for (net.morimekta.test.providence.Value entry_29 : mEnumList) {
                length += writer.writeInt(entry_29.getValue());
            }
        }

        if (hasEnumSet()) {
            length += writer.writeByte((byte) 14);
            length += writer.writeShort((short) 32);
            length += writer.writeByte((byte) 8);
            length += writer.writeUInt32(mEnumSet.size());
            for (net.morimekta.test.providence.Value entry_30 : mEnumSet) {
                length += writer.writeInt(entry_30.getValue());
            }
        }

        if (hasEnumMap()) {
            length += writer.writeByte((byte) 13);
            length += writer.writeShort((short) 33);
            length += writer.writeByte((byte) 8);
            length += writer.writeByte((byte) 8);
            length += writer.writeUInt32(mEnumMap.size());
            for (java.util.Map.Entry<net.morimekta.test.providence.Value,net.morimekta.test.providence.Value> entry_31 : mEnumMap.entrySet()) {
                length += writer.writeInt(entry_31.getKey().getValue());
                length += writer.writeInt(entry_31.getValue().getValue());
            }
        }

        if (hasMessageList()) {
            length += writer.writeByte((byte) 15);
            length += writer.writeShort((short) 41);
            length += writer.writeByte((byte) 12);
            length += writer.writeUInt32(mMessageList.size());
            for (net.morimekta.test.providence.DefaultFields entry_32 : mMessageList) {
                length += net.morimekta.providence.serializer.rw.BinaryFormatUtils.writeMessage(writer, entry_32);
            }
        }

        if (hasMessageSet()) {
            length += writer.writeByte((byte) 14);
            length += writer.writeShort((short) 42);
            length += writer.writeByte((byte) 12);
            length += writer.writeUInt32(mMessageSet.size());
            for (net.morimekta.test.providence.DefaultFields entry_33 : mMessageSet) {
                length += net.morimekta.providence.serializer.rw.BinaryFormatUtils.writeMessage(writer, entry_33);
            }
        }

        if (hasMessageMap()) {
            length += writer.writeByte((byte) 13);
            length += writer.writeShort((short) 43);
            length += writer.writeByte((byte) 11);
            length += writer.writeByte((byte) 12);
            length += writer.writeUInt32(mMessageMap.size());
            for (java.util.Map.Entry<String,net.morimekta.test.providence.DefaultFields> entry_34 : mMessageMap.entrySet()) {
                net.morimekta.util.Binary tmp_35 = net.morimekta.util.Binary.wrap(entry_34.getKey().getBytes(java.nio.charset.StandardCharsets.UTF_8));
                length += writer.writeUInt32(tmp_35.length());
                length += writer.writeBinary(tmp_35);
                length += net.morimekta.providence.serializer.rw.BinaryFormatUtils.writeMessage(writer, entry_34.getValue());
            }
        }

        if (hasRequiredFields()) {
            length += writer.writeByte((byte) 12);
            length += writer.writeShort((short) 51);
            length += net.morimekta.providence.serializer.rw.BinaryFormatUtils.writeMessage(writer, mRequiredFields);
        }

        if (hasDefaultFields()) {
            length += writer.writeByte((byte) 12);
            length += writer.writeShort((short) 52);
            length += net.morimekta.providence.serializer.rw.BinaryFormatUtils.writeMessage(writer, mDefaultFields);
        }

        if (hasOptionalFields()) {
            length += writer.writeByte((byte) 12);
            length += writer.writeShort((short) 53);
            length += net.morimekta.providence.serializer.rw.BinaryFormatUtils.writeMessage(writer, mOptionalFields);
        }

        if (hasUnionFields()) {
            length += writer.writeByte((byte) 12);
            length += writer.writeShort((short) 54);
            length += net.morimekta.providence.serializer.rw.BinaryFormatUtils.writeMessage(writer, mUnionFields);
        }

        if (hasExceptionFields()) {
            length += writer.writeByte((byte) 12);
            length += writer.writeShort((short) 55);
            length += net.morimekta.providence.serializer.rw.BinaryFormatUtils.writeMessage(writer, mExceptionFields);
        }

        if (hasDefaultValues()) {
            length += writer.writeByte((byte) 12);
            length += writer.writeShort((short) 56);
            length += net.morimekta.providence.serializer.rw.BinaryFormatUtils.writeMessage(writer, mDefaultValues);
        }

        length += writer.writeByte((byte) 0);
        return length;
    }

    @Override
    public _Builder mutate() {
        return new _Builder(this);
    }

    public enum _Field implements net.morimekta.providence.descriptor.PField {
        BOOLEAN_LIST(1, net.morimekta.providence.descriptor.PRequirement.OPTIONAL, "booleanList", net.morimekta.providence.descriptor.PList.provider(net.morimekta.providence.descriptor.PPrimitive.BOOL.provider()), null),
        BYTE_LIST(2, net.morimekta.providence.descriptor.PRequirement.OPTIONAL, "byteList", net.morimekta.providence.descriptor.PList.provider(net.morimekta.providence.descriptor.PPrimitive.BYTE.provider()), null),
        SHORT_LIST(3, net.morimekta.providence.descriptor.PRequirement.OPTIONAL, "shortList", net.morimekta.providence.descriptor.PList.provider(net.morimekta.providence.descriptor.PPrimitive.I16.provider()), null),
        INTEGER_LIST(4, net.morimekta.providence.descriptor.PRequirement.OPTIONAL, "integerList", net.morimekta.providence.descriptor.PList.provider(net.morimekta.providence.descriptor.PPrimitive.I32.provider()), null),
        LONG_LIST(5, net.morimekta.providence.descriptor.PRequirement.OPTIONAL, "longList", net.morimekta.providence.descriptor.PList.provider(net.morimekta.providence.descriptor.PPrimitive.I64.provider()), null),
        DOUBLE_LIST(6, net.morimekta.providence.descriptor.PRequirement.OPTIONAL, "doubleList", net.morimekta.providence.descriptor.PList.provider(net.morimekta.providence.descriptor.PPrimitive.DOUBLE.provider()), null),
        STRING_LIST(7, net.morimekta.providence.descriptor.PRequirement.OPTIONAL, "stringList", net.morimekta.providence.descriptor.PList.provider(net.morimekta.providence.descriptor.PPrimitive.STRING.provider()), null),
        BINARY_LIST(8, net.morimekta.providence.descriptor.PRequirement.OPTIONAL, "binaryList", net.morimekta.providence.descriptor.PList.provider(net.morimekta.providence.descriptor.PPrimitive.BINARY.provider()), null),
        BOOLEAN_SET(11, net.morimekta.providence.descriptor.PRequirement.OPTIONAL, "booleanSet", net.morimekta.providence.descriptor.PSet.provider(net.morimekta.providence.descriptor.PPrimitive.BOOL.provider()), null),
        BYTE_SET(12, net.morimekta.providence.descriptor.PRequirement.OPTIONAL, "byteSet", net.morimekta.providence.descriptor.PSet.sortedProvider(net.morimekta.providence.descriptor.PPrimitive.BYTE.provider()), null),
        SHORT_SET(13, net.morimekta.providence.descriptor.PRequirement.OPTIONAL, "shortSet", net.morimekta.providence.descriptor.PSet.orderedProvider(net.morimekta.providence.descriptor.PPrimitive.I16.provider()), null),
        INTEGER_SET(14, net.morimekta.providence.descriptor.PRequirement.OPTIONAL, "integerSet", net.morimekta.providence.descriptor.PSet.provider(net.morimekta.providence.descriptor.PPrimitive.I32.provider()), null),
        LONG_SET(15, net.morimekta.providence.descriptor.PRequirement.OPTIONAL, "longSet", net.morimekta.providence.descriptor.PSet.provider(net.morimekta.providence.descriptor.PPrimitive.I64.provider()), null),
        DOUBLE_SET(16, net.morimekta.providence.descriptor.PRequirement.OPTIONAL, "doubleSet", net.morimekta.providence.descriptor.PSet.provider(net.morimekta.providence.descriptor.PPrimitive.DOUBLE.provider()), null),
        STRING_SET(17, net.morimekta.providence.descriptor.PRequirement.OPTIONAL, "stringSet", net.morimekta.providence.descriptor.PSet.provider(net.morimekta.providence.descriptor.PPrimitive.STRING.provider()), null),
        BINARY_SET(18, net.morimekta.providence.descriptor.PRequirement.OPTIONAL, "binarySet", net.morimekta.providence.descriptor.PSet.provider(net.morimekta.providence.descriptor.PPrimitive.BINARY.provider()), null),
        BOOLEAN_MAP(21, net.morimekta.providence.descriptor.PRequirement.OPTIONAL, "booleanMap", net.morimekta.providence.descriptor.PMap.provider(net.morimekta.providence.descriptor.PPrimitive.BOOL.provider(),net.morimekta.providence.descriptor.PPrimitive.BOOL.provider()), null),
        BYTE_MAP(22, net.morimekta.providence.descriptor.PRequirement.OPTIONAL, "byteMap", net.morimekta.providence.descriptor.PMap.sortedProvider(net.morimekta.providence.descriptor.PPrimitive.BYTE.provider(),net.morimekta.providence.descriptor.PPrimitive.BYTE.provider()), null),
        SHORT_MAP(23, net.morimekta.providence.descriptor.PRequirement.OPTIONAL, "shortMap", net.morimekta.providence.descriptor.PMap.orderedProvider(net.morimekta.providence.descriptor.PPrimitive.I16.provider(),net.morimekta.providence.descriptor.PPrimitive.I16.provider()), null),
        INTEGER_MAP(24, net.morimekta.providence.descriptor.PRequirement.OPTIONAL, "integerMap", net.morimekta.providence.descriptor.PMap.provider(net.morimekta.providence.descriptor.PPrimitive.I32.provider(),net.morimekta.providence.descriptor.PPrimitive.I32.provider()), null),
        LONG_MAP(25, net.morimekta.providence.descriptor.PRequirement.OPTIONAL, "longMap", net.morimekta.providence.descriptor.PMap.provider(net.morimekta.providence.descriptor.PPrimitive.I64.provider(),net.morimekta.providence.descriptor.PPrimitive.I64.provider()), null),
        DOUBLE_MAP(26, net.morimekta.providence.descriptor.PRequirement.OPTIONAL, "doubleMap", net.morimekta.providence.descriptor.PMap.provider(net.morimekta.providence.descriptor.PPrimitive.DOUBLE.provider(),net.morimekta.providence.descriptor.PPrimitive.DOUBLE.provider()), null),
        STRING_MAP(27, net.morimekta.providence.descriptor.PRequirement.OPTIONAL, "stringMap", net.morimekta.providence.descriptor.PMap.provider(net.morimekta.providence.descriptor.PPrimitive.STRING.provider(),net.morimekta.providence.descriptor.PPrimitive.STRING.provider()), null),
        BINARY_MAP(28, net.morimekta.providence.descriptor.PRequirement.OPTIONAL, "binaryMap", net.morimekta.providence.descriptor.PMap.provider(net.morimekta.providence.descriptor.PPrimitive.BINARY.provider(),net.morimekta.providence.descriptor.PPrimitive.BINARY.provider()), null),
        ENUM_LIST(31, net.morimekta.providence.descriptor.PRequirement.OPTIONAL, "enumList", net.morimekta.providence.descriptor.PList.provider(net.morimekta.test.providence.Value.provider()), null),
        ENUM_SET(32, net.morimekta.providence.descriptor.PRequirement.OPTIONAL, "enumSet", net.morimekta.providence.descriptor.PSet.provider(net.morimekta.test.providence.Value.provider()), null),
        ENUM_MAP(33, net.morimekta.providence.descriptor.PRequirement.OPTIONAL, "enumMap", net.morimekta.providence.descriptor.PMap.provider(net.morimekta.test.providence.Value.provider(),net.morimekta.test.providence.Value.provider()), null),
        MESSAGE_LIST(41, net.morimekta.providence.descriptor.PRequirement.OPTIONAL, "messageList", net.morimekta.providence.descriptor.PList.provider(net.morimekta.test.providence.DefaultFields.provider()), null),
        MESSAGE_SET(42, net.morimekta.providence.descriptor.PRequirement.OPTIONAL, "messageSet", net.morimekta.providence.descriptor.PSet.provider(net.morimekta.test.providence.DefaultFields.provider()), null),
        MESSAGE_MAP(43, net.morimekta.providence.descriptor.PRequirement.OPTIONAL, "messageMap", net.morimekta.providence.descriptor.PMap.provider(net.morimekta.providence.descriptor.PPrimitive.STRING.provider(),net.morimekta.test.providence.DefaultFields.provider()), null),
        REQUIRED_FIELDS(51, net.morimekta.providence.descriptor.PRequirement.OPTIONAL, "requiredFields", net.morimekta.test.providence.RequiredFields.provider(), null),
        DEFAULT_FIELDS(52, net.morimekta.providence.descriptor.PRequirement.OPTIONAL, "defaultFields", net.morimekta.test.providence.DefaultFields.provider(), null),
        OPTIONAL_FIELDS(53, net.morimekta.providence.descriptor.PRequirement.OPTIONAL, "optionalFields", net.morimekta.test.providence.OptionalFields.provider(), null),
        UNION_FIELDS(54, net.morimekta.providence.descriptor.PRequirement.OPTIONAL, "unionFields", net.morimekta.test.providence.UnionFields.provider(), null),
        EXCEPTION_FIELDS(55, net.morimekta.providence.descriptor.PRequirement.OPTIONAL, "exceptionFields", net.morimekta.test.providence.ExceptionFields.provider(), null),
        DEFAULT_VALUES(56, net.morimekta.providence.descriptor.PRequirement.OPTIONAL, "defaultValues", net.morimekta.test.providence.DefaultValues.provider(), null),
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
                case 1: return _Field.BOOLEAN_LIST;
                case 2: return _Field.BYTE_LIST;
                case 3: return _Field.SHORT_LIST;
                case 4: return _Field.INTEGER_LIST;
                case 5: return _Field.LONG_LIST;
                case 6: return _Field.DOUBLE_LIST;
                case 7: return _Field.STRING_LIST;
                case 8: return _Field.BINARY_LIST;
                case 11: return _Field.BOOLEAN_SET;
                case 12: return _Field.BYTE_SET;
                case 13: return _Field.SHORT_SET;
                case 14: return _Field.INTEGER_SET;
                case 15: return _Field.LONG_SET;
                case 16: return _Field.DOUBLE_SET;
                case 17: return _Field.STRING_SET;
                case 18: return _Field.BINARY_SET;
                case 21: return _Field.BOOLEAN_MAP;
                case 22: return _Field.BYTE_MAP;
                case 23: return _Field.SHORT_MAP;
                case 24: return _Field.INTEGER_MAP;
                case 25: return _Field.LONG_MAP;
                case 26: return _Field.DOUBLE_MAP;
                case 27: return _Field.STRING_MAP;
                case 28: return _Field.BINARY_MAP;
                case 31: return _Field.ENUM_LIST;
                case 32: return _Field.ENUM_SET;
                case 33: return _Field.ENUM_MAP;
                case 41: return _Field.MESSAGE_LIST;
                case 42: return _Field.MESSAGE_SET;
                case 43: return _Field.MESSAGE_MAP;
                case 51: return _Field.REQUIRED_FIELDS;
                case 52: return _Field.DEFAULT_FIELDS;
                case 53: return _Field.OPTIONAL_FIELDS;
                case 54: return _Field.UNION_FIELDS;
                case 55: return _Field.EXCEPTION_FIELDS;
                case 56: return _Field.DEFAULT_VALUES;
            }
            return null;
        }

        public static _Field forName(String name) {
            switch (name) {
                case "booleanList": return _Field.BOOLEAN_LIST;
                case "byteList": return _Field.BYTE_LIST;
                case "shortList": return _Field.SHORT_LIST;
                case "integerList": return _Field.INTEGER_LIST;
                case "longList": return _Field.LONG_LIST;
                case "doubleList": return _Field.DOUBLE_LIST;
                case "stringList": return _Field.STRING_LIST;
                case "binaryList": return _Field.BINARY_LIST;
                case "booleanSet": return _Field.BOOLEAN_SET;
                case "byteSet": return _Field.BYTE_SET;
                case "shortSet": return _Field.SHORT_SET;
                case "integerSet": return _Field.INTEGER_SET;
                case "longSet": return _Field.LONG_SET;
                case "doubleSet": return _Field.DOUBLE_SET;
                case "stringSet": return _Field.STRING_SET;
                case "binarySet": return _Field.BINARY_SET;
                case "booleanMap": return _Field.BOOLEAN_MAP;
                case "byteMap": return _Field.BYTE_MAP;
                case "shortMap": return _Field.SHORT_MAP;
                case "integerMap": return _Field.INTEGER_MAP;
                case "longMap": return _Field.LONG_MAP;
                case "doubleMap": return _Field.DOUBLE_MAP;
                case "stringMap": return _Field.STRING_MAP;
                case "binaryMap": return _Field.BINARY_MAP;
                case "enumList": return _Field.ENUM_LIST;
                case "enumSet": return _Field.ENUM_SET;
                case "enumMap": return _Field.ENUM_MAP;
                case "messageList": return _Field.MESSAGE_LIST;
                case "messageSet": return _Field.MESSAGE_SET;
                case "messageMap": return _Field.MESSAGE_MAP;
                case "requiredFields": return _Field.REQUIRED_FIELDS;
                case "defaultFields": return _Field.DEFAULT_FIELDS;
                case "optionalFields": return _Field.OPTIONAL_FIELDS;
                case "unionFields": return _Field.UNION_FIELDS;
                case "exceptionFields": return _Field.EXCEPTION_FIELDS;
                case "defaultValues": return _Field.DEFAULT_VALUES;
            }
            return null;
        }
    }

    public static net.morimekta.providence.descriptor.PStructDescriptorProvider<Containers,_Field> provider() {
        return new _Provider();
    }

    @Override
    public net.morimekta.providence.descriptor.PStructDescriptor<Containers,_Field> descriptor() {
        return kDescriptor;
    }

    public static final net.morimekta.providence.descriptor.PStructDescriptor<Containers,_Field> kDescriptor;

    private static class _Descriptor
            extends net.morimekta.providence.descriptor.PStructDescriptor<Containers,_Field> {
        public _Descriptor() {
            super("providence", "Containers", new _Factory(), false);
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

    private final static class _Provider extends net.morimekta.providence.descriptor.PStructDescriptorProvider<Containers,_Field> {
        @Override
        public net.morimekta.providence.descriptor.PStructDescriptor<Containers,_Field> descriptor() {
            return kDescriptor;
        }
    }

    private final static class _Factory
            extends net.morimekta.providence.PMessageBuilderFactory<Containers,_Field> {
        @Override
        public _Builder builder() {
            return new _Builder();
        }
    }

    /**
     * Make a providence.Containers builder.
     * @return The builder instance.
     */
    public static _Builder builder() {
        return new _Builder();
    }

    public static class _Builder
            extends net.morimekta.providence.PMessageBuilder<Containers,_Field>
            implements net.morimekta.providence.serializer.rw.BinaryReader {
        private java.util.BitSet optionals;
        private java.util.BitSet modified;

        private net.morimekta.providence.descriptor.PList.Builder<Boolean> mBooleanList;
        private net.morimekta.providence.descriptor.PList.Builder<Byte> mByteList;
        private net.morimekta.providence.descriptor.PList.Builder<Short> mShortList;
        private net.morimekta.providence.descriptor.PList.Builder<Integer> mIntegerList;
        private net.morimekta.providence.descriptor.PList.Builder<Long> mLongList;
        private net.morimekta.providence.descriptor.PList.Builder<Double> mDoubleList;
        private net.morimekta.providence.descriptor.PList.Builder<String> mStringList;
        private net.morimekta.providence.descriptor.PList.Builder<net.morimekta.util.Binary> mBinaryList;
        private net.morimekta.providence.descriptor.PSet.Builder<Boolean> mBooleanSet;
        private net.morimekta.providence.descriptor.PSet.Builder<Byte> mByteSet;
        private net.morimekta.providence.descriptor.PSet.Builder<Short> mShortSet;
        private net.morimekta.providence.descriptor.PSet.Builder<Integer> mIntegerSet;
        private net.morimekta.providence.descriptor.PSet.Builder<Long> mLongSet;
        private net.morimekta.providence.descriptor.PSet.Builder<Double> mDoubleSet;
        private net.morimekta.providence.descriptor.PSet.Builder<String> mStringSet;
        private net.morimekta.providence.descriptor.PSet.Builder<net.morimekta.util.Binary> mBinarySet;
        private net.morimekta.providence.descriptor.PMap.Builder<Boolean,Boolean> mBooleanMap;
        private net.morimekta.providence.descriptor.PMap.Builder<Byte,Byte> mByteMap;
        private net.morimekta.providence.descriptor.PMap.Builder<Short,Short> mShortMap;
        private net.morimekta.providence.descriptor.PMap.Builder<Integer,Integer> mIntegerMap;
        private net.morimekta.providence.descriptor.PMap.Builder<Long,Long> mLongMap;
        private net.morimekta.providence.descriptor.PMap.Builder<Double,Double> mDoubleMap;
        private net.morimekta.providence.descriptor.PMap.Builder<String,String> mStringMap;
        private net.morimekta.providence.descriptor.PMap.Builder<net.morimekta.util.Binary,net.morimekta.util.Binary> mBinaryMap;
        private net.morimekta.providence.descriptor.PList.Builder<net.morimekta.test.providence.Value> mEnumList;
        private net.morimekta.providence.descriptor.PSet.Builder<net.morimekta.test.providence.Value> mEnumSet;
        private net.morimekta.providence.descriptor.PMap.Builder<net.morimekta.test.providence.Value,net.morimekta.test.providence.Value> mEnumMap;
        private net.morimekta.providence.descriptor.PList.Builder<net.morimekta.test.providence.DefaultFields> mMessageList;
        private net.morimekta.providence.descriptor.PSet.Builder<net.morimekta.test.providence.DefaultFields> mMessageSet;
        private net.morimekta.providence.descriptor.PMap.Builder<String,net.morimekta.test.providence.DefaultFields> mMessageMap;
        private net.morimekta.test.providence.RequiredFields mRequiredFields;
        private net.morimekta.test.providence.RequiredFields._Builder mRequiredFields_builder;
        private net.morimekta.test.providence.DefaultFields mDefaultFields;
        private net.morimekta.test.providence.DefaultFields._Builder mDefaultFields_builder;
        private net.morimekta.test.providence.OptionalFields mOptionalFields;
        private net.morimekta.test.providence.OptionalFields._Builder mOptionalFields_builder;
        private net.morimekta.test.providence.UnionFields mUnionFields;
        private net.morimekta.test.providence.UnionFields._Builder mUnionFields_builder;
        private net.morimekta.test.providence.ExceptionFields mExceptionFields;
        private net.morimekta.test.providence.ExceptionFields._Builder mExceptionFields_builder;
        private net.morimekta.test.providence.DefaultValues mDefaultValues;
        private net.morimekta.test.providence.DefaultValues._Builder mDefaultValues_builder;

        /**
         * Make a providence.Containers builder.
         */
        public _Builder() {
            optionals = new java.util.BitSet(36);
            modified = new java.util.BitSet(36);
            mBooleanList = new net.morimekta.providence.descriptor.PList.ImmutableListBuilder<>();
            mByteList = new net.morimekta.providence.descriptor.PList.ImmutableListBuilder<>();
            mShortList = new net.morimekta.providence.descriptor.PList.ImmutableListBuilder<>();
            mIntegerList = new net.morimekta.providence.descriptor.PList.ImmutableListBuilder<>();
            mLongList = new net.morimekta.providence.descriptor.PList.ImmutableListBuilder<>();
            mDoubleList = new net.morimekta.providence.descriptor.PList.ImmutableListBuilder<>();
            mStringList = new net.morimekta.providence.descriptor.PList.ImmutableListBuilder<>();
            mBinaryList = new net.morimekta.providence.descriptor.PList.ImmutableListBuilder<>();
            mBooleanSet = new net.morimekta.providence.descriptor.PSet.ImmutableSetBuilder<>();
            mByteSet = new net.morimekta.providence.descriptor.PSet.ImmutableSortedSetBuilder<>();
            mShortSet = new net.morimekta.providence.descriptor.PSet.LinkedHashSetBuilder<>();
            mIntegerSet = new net.morimekta.providence.descriptor.PSet.ImmutableSetBuilder<>();
            mLongSet = new net.morimekta.providence.descriptor.PSet.ImmutableSetBuilder<>();
            mDoubleSet = new net.morimekta.providence.descriptor.PSet.ImmutableSetBuilder<>();
            mStringSet = new net.morimekta.providence.descriptor.PSet.ImmutableSetBuilder<>();
            mBinarySet = new net.morimekta.providence.descriptor.PSet.ImmutableSetBuilder<>();
            mBooleanMap = new net.morimekta.providence.descriptor.PMap.ImmutableMapBuilder<>();
            mByteMap = new net.morimekta.providence.descriptor.PMap.ImmutableSortedMapBuilder<>();
            mShortMap = new net.morimekta.providence.descriptor.PMap.LinkedHashMapBuilder<>();
            mIntegerMap = new net.morimekta.providence.descriptor.PMap.ImmutableMapBuilder<>();
            mLongMap = new net.morimekta.providence.descriptor.PMap.ImmutableMapBuilder<>();
            mDoubleMap = new net.morimekta.providence.descriptor.PMap.ImmutableMapBuilder<>();
            mStringMap = new net.morimekta.providence.descriptor.PMap.ImmutableMapBuilder<>();
            mBinaryMap = new net.morimekta.providence.descriptor.PMap.ImmutableMapBuilder<>();
            mEnumList = new net.morimekta.providence.descriptor.PList.ImmutableListBuilder<>();
            mEnumSet = new net.morimekta.providence.descriptor.PSet.ImmutableSetBuilder<>();
            mEnumMap = new net.morimekta.providence.descriptor.PMap.ImmutableMapBuilder<>();
            mMessageList = new net.morimekta.providence.descriptor.PList.ImmutableListBuilder<>();
            mMessageSet = new net.morimekta.providence.descriptor.PSet.ImmutableSetBuilder<>();
            mMessageMap = new net.morimekta.providence.descriptor.PMap.ImmutableMapBuilder<>();
        }

        /**
         * Make a mutating builder off a base providence.Containers.
         *
         * @param base The base Containers
         */
        public _Builder(Containers base) {
            this();

            if (base.hasBooleanList()) {
                optionals.set(0);
                mBooleanList.addAll(base.mBooleanList);
            }
            if (base.hasByteList()) {
                optionals.set(1);
                mByteList.addAll(base.mByteList);
            }
            if (base.hasShortList()) {
                optionals.set(2);
                mShortList.addAll(base.mShortList);
            }
            if (base.hasIntegerList()) {
                optionals.set(3);
                mIntegerList.addAll(base.mIntegerList);
            }
            if (base.hasLongList()) {
                optionals.set(4);
                mLongList.addAll(base.mLongList);
            }
            if (base.hasDoubleList()) {
                optionals.set(5);
                mDoubleList.addAll(base.mDoubleList);
            }
            if (base.hasStringList()) {
                optionals.set(6);
                mStringList.addAll(base.mStringList);
            }
            if (base.hasBinaryList()) {
                optionals.set(7);
                mBinaryList.addAll(base.mBinaryList);
            }
            if (base.hasBooleanSet()) {
                optionals.set(8);
                mBooleanSet.addAll(base.mBooleanSet);
            }
            if (base.hasByteSet()) {
                optionals.set(9);
                mByteSet.addAll(base.mByteSet);
            }
            if (base.hasShortSet()) {
                optionals.set(10);
                mShortSet.addAll(base.mShortSet);
            }
            if (base.hasIntegerSet()) {
                optionals.set(11);
                mIntegerSet.addAll(base.mIntegerSet);
            }
            if (base.hasLongSet()) {
                optionals.set(12);
                mLongSet.addAll(base.mLongSet);
            }
            if (base.hasDoubleSet()) {
                optionals.set(13);
                mDoubleSet.addAll(base.mDoubleSet);
            }
            if (base.hasStringSet()) {
                optionals.set(14);
                mStringSet.addAll(base.mStringSet);
            }
            if (base.hasBinarySet()) {
                optionals.set(15);
                mBinarySet.addAll(base.mBinarySet);
            }
            if (base.hasBooleanMap()) {
                optionals.set(16);
                mBooleanMap.putAll(base.mBooleanMap);
            }
            if (base.hasByteMap()) {
                optionals.set(17);
                mByteMap.putAll(base.mByteMap);
            }
            if (base.hasShortMap()) {
                optionals.set(18);
                mShortMap.putAll(base.mShortMap);
            }
            if (base.hasIntegerMap()) {
                optionals.set(19);
                mIntegerMap.putAll(base.mIntegerMap);
            }
            if (base.hasLongMap()) {
                optionals.set(20);
                mLongMap.putAll(base.mLongMap);
            }
            if (base.hasDoubleMap()) {
                optionals.set(21);
                mDoubleMap.putAll(base.mDoubleMap);
            }
            if (base.hasStringMap()) {
                optionals.set(22);
                mStringMap.putAll(base.mStringMap);
            }
            if (base.hasBinaryMap()) {
                optionals.set(23);
                mBinaryMap.putAll(base.mBinaryMap);
            }
            if (base.hasEnumList()) {
                optionals.set(24);
                mEnumList.addAll(base.mEnumList);
            }
            if (base.hasEnumSet()) {
                optionals.set(25);
                mEnumSet.addAll(base.mEnumSet);
            }
            if (base.hasEnumMap()) {
                optionals.set(26);
                mEnumMap.putAll(base.mEnumMap);
            }
            if (base.hasMessageList()) {
                optionals.set(27);
                mMessageList.addAll(base.mMessageList);
            }
            if (base.hasMessageSet()) {
                optionals.set(28);
                mMessageSet.addAll(base.mMessageSet);
            }
            if (base.hasMessageMap()) {
                optionals.set(29);
                mMessageMap.putAll(base.mMessageMap);
            }
            if (base.hasRequiredFields()) {
                optionals.set(30);
                mRequiredFields = base.mRequiredFields;
            }
            if (base.hasDefaultFields()) {
                optionals.set(31);
                mDefaultFields = base.mDefaultFields;
            }
            if (base.hasOptionalFields()) {
                optionals.set(32);
                mOptionalFields = base.mOptionalFields;
            }
            if (base.hasUnionFields()) {
                optionals.set(33);
                mUnionFields = base.mUnionFields;
            }
            if (base.hasExceptionFields()) {
                optionals.set(34);
                mExceptionFields = base.mExceptionFields;
            }
            if (base.hasDefaultValues()) {
                optionals.set(35);
                mDefaultValues = base.mDefaultValues;
            }
        }

        @Override
        public _Builder merge(Containers from) {
            if (from.hasBooleanList()) {
                optionals.set(0);
                modified.set(0);
                mBooleanList.clear();
                mBooleanList.addAll(from.getBooleanList());
            }

            if (from.hasByteList()) {
                optionals.set(1);
                modified.set(1);
                mByteList.clear();
                mByteList.addAll(from.getByteList());
            }

            if (from.hasShortList()) {
                optionals.set(2);
                modified.set(2);
                mShortList.clear();
                mShortList.addAll(from.getShortList());
            }

            if (from.hasIntegerList()) {
                optionals.set(3);
                modified.set(3);
                mIntegerList.clear();
                mIntegerList.addAll(from.getIntegerList());
            }

            if (from.hasLongList()) {
                optionals.set(4);
                modified.set(4);
                mLongList.clear();
                mLongList.addAll(from.getLongList());
            }

            if (from.hasDoubleList()) {
                optionals.set(5);
                modified.set(5);
                mDoubleList.clear();
                mDoubleList.addAll(from.getDoubleList());
            }

            if (from.hasStringList()) {
                optionals.set(6);
                modified.set(6);
                mStringList.clear();
                mStringList.addAll(from.getStringList());
            }

            if (from.hasBinaryList()) {
                optionals.set(7);
                modified.set(7);
                mBinaryList.clear();
                mBinaryList.addAll(from.getBinaryList());
            }

            if (from.hasBooleanSet()) {
                optionals.set(8);
                modified.set(8);
                mBooleanSet.addAll(from.getBooleanSet());
            }

            if (from.hasByteSet()) {
                optionals.set(9);
                modified.set(9);
                mByteSet.addAll(from.getByteSet());
            }

            if (from.hasShortSet()) {
                optionals.set(10);
                modified.set(10);
                mShortSet.addAll(from.getShortSet());
            }

            if (from.hasIntegerSet()) {
                optionals.set(11);
                modified.set(11);
                mIntegerSet.addAll(from.getIntegerSet());
            }

            if (from.hasLongSet()) {
                optionals.set(12);
                modified.set(12);
                mLongSet.addAll(from.getLongSet());
            }

            if (from.hasDoubleSet()) {
                optionals.set(13);
                modified.set(13);
                mDoubleSet.addAll(from.getDoubleSet());
            }

            if (from.hasStringSet()) {
                optionals.set(14);
                modified.set(14);
                mStringSet.addAll(from.getStringSet());
            }

            if (from.hasBinarySet()) {
                optionals.set(15);
                modified.set(15);
                mBinarySet.addAll(from.getBinarySet());
            }

            if (from.hasBooleanMap()) {
                optionals.set(16);
                modified.set(16);
                mBooleanMap.putAll(from.getBooleanMap());
            }

            if (from.hasByteMap()) {
                optionals.set(17);
                modified.set(17);
                mByteMap.putAll(from.getByteMap());
            }

            if (from.hasShortMap()) {
                optionals.set(18);
                modified.set(18);
                mShortMap.putAll(from.getShortMap());
            }

            if (from.hasIntegerMap()) {
                optionals.set(19);
                modified.set(19);
                mIntegerMap.putAll(from.getIntegerMap());
            }

            if (from.hasLongMap()) {
                optionals.set(20);
                modified.set(20);
                mLongMap.putAll(from.getLongMap());
            }

            if (from.hasDoubleMap()) {
                optionals.set(21);
                modified.set(21);
                mDoubleMap.putAll(from.getDoubleMap());
            }

            if (from.hasStringMap()) {
                optionals.set(22);
                modified.set(22);
                mStringMap.putAll(from.getStringMap());
            }

            if (from.hasBinaryMap()) {
                optionals.set(23);
                modified.set(23);
                mBinaryMap.putAll(from.getBinaryMap());
            }

            if (from.hasEnumList()) {
                optionals.set(24);
                modified.set(24);
                mEnumList.clear();
                mEnumList.addAll(from.getEnumList());
            }

            if (from.hasEnumSet()) {
                optionals.set(25);
                modified.set(25);
                mEnumSet.addAll(from.getEnumSet());
            }

            if (from.hasEnumMap()) {
                optionals.set(26);
                modified.set(26);
                mEnumMap.putAll(from.getEnumMap());
            }

            if (from.hasMessageList()) {
                optionals.set(27);
                modified.set(27);
                mMessageList.clear();
                mMessageList.addAll(from.getMessageList());
            }

            if (from.hasMessageSet()) {
                optionals.set(28);
                modified.set(28);
                mMessageSet.addAll(from.getMessageSet());
            }

            if (from.hasMessageMap()) {
                optionals.set(29);
                modified.set(29);
                mMessageMap.putAll(from.getMessageMap());
            }

            if (from.hasRequiredFields()) {
                optionals.set(30);
                modified.set(30);
                if (mRequiredFields_builder != null) {
                    mRequiredFields_builder.merge(from.getRequiredFields());
                } else if (mRequiredFields != null) {
                    mRequiredFields_builder = mRequiredFields.mutate().merge(from.getRequiredFields());
                    mRequiredFields = null;
                } else {
                    mRequiredFields = from.getRequiredFields();
                }
            }

            if (from.hasDefaultFields()) {
                optionals.set(31);
                modified.set(31);
                if (mDefaultFields_builder != null) {
                    mDefaultFields_builder.merge(from.getDefaultFields());
                } else if (mDefaultFields != null) {
                    mDefaultFields_builder = mDefaultFields.mutate().merge(from.getDefaultFields());
                    mDefaultFields = null;
                } else {
                    mDefaultFields = from.getDefaultFields();
                }
            }

            if (from.hasOptionalFields()) {
                optionals.set(32);
                modified.set(32);
                if (mOptionalFields_builder != null) {
                    mOptionalFields_builder.merge(from.getOptionalFields());
                } else if (mOptionalFields != null) {
                    mOptionalFields_builder = mOptionalFields.mutate().merge(from.getOptionalFields());
                    mOptionalFields = null;
                } else {
                    mOptionalFields = from.getOptionalFields();
                }
            }

            if (from.hasUnionFields()) {
                optionals.set(33);
                modified.set(33);
                if (mUnionFields_builder != null) {
                    mUnionFields_builder.merge(from.getUnionFields());
                } else if (mUnionFields != null) {
                    mUnionFields_builder = mUnionFields.mutate().merge(from.getUnionFields());
                    mUnionFields = null;
                } else {
                    mUnionFields = from.getUnionFields();
                }
            }

            if (from.hasExceptionFields()) {
                optionals.set(34);
                modified.set(34);
                if (mExceptionFields_builder != null) {
                    mExceptionFields_builder.merge(from.getExceptionFields());
                } else if (mExceptionFields != null) {
                    mExceptionFields_builder = mExceptionFields.mutate().merge(from.getExceptionFields());
                    mExceptionFields = null;
                } else {
                    mExceptionFields = from.getExceptionFields();
                }
            }

            if (from.hasDefaultValues()) {
                optionals.set(35);
                modified.set(35);
                if (mDefaultValues_builder != null) {
                    mDefaultValues_builder.merge(from.getDefaultValues());
                } else if (mDefaultValues != null) {
                    mDefaultValues_builder = mDefaultValues.mutate().merge(from.getDefaultValues());
                    mDefaultValues = null;
                } else {
                    mDefaultValues = from.getDefaultValues();
                }
            }
            return this;
        }

        /**
         * all types as list&lt;x&gt;.
         *
         * @param value The new value
         * @return The builder
         */
        public _Builder setBooleanList(java.util.Collection<Boolean> value) {
            optionals.set(0);
            modified.set(0);
            mBooleanList.clear();
            mBooleanList.addAll(value);
            return this;
        }

        /**
         * all types as list&lt;x&gt;.
         *
         * @param values The added value
         * @return The builder
         */
        public _Builder addToBooleanList(boolean... values) {
            optionals.set(0);
            modified.set(0);
            for (boolean item : values) {
                mBooleanList.add(item);
            }
            return this;
        }

        /**
         * all types as list&lt;x&gt;.
         *
         * @return True if booleanList has been set.
         */
        public boolean isSetBooleanList() {
            return optionals.get(0);
        }

        /**
         * all types as list&lt;x&gt;.
         *
         * @return True if booleanList has been modified.
         */
        public boolean isModifiedBooleanList() {
            return modified.get(0);
        }

        /**
         * all types as list&lt;x&gt;.
         *
         * @return The builder
         */
        public _Builder clearBooleanList() {
            optionals.clear(0);
            modified.set(0);
            mBooleanList.clear();
            return this;
        }

        /**
         * all types as list&lt;x&gt;.
         *
         * @return The field builder
         */
        public net.morimekta.providence.descriptor.PList.Builder<Boolean> mutableBooleanList() {
            optionals.set(0);
            modified.set(0);
            return mBooleanList;
        }

        /**
         * Sets the value of byteList.
         *
         * @param value The new value
         * @return The builder
         */
        public _Builder setByteList(java.util.Collection<Byte> value) {
            optionals.set(1);
            modified.set(1);
            mByteList.clear();
            mByteList.addAll(value);
            return this;
        }

        /**
         * Adds entries to byteList.
         *
         * @param values The added value
         * @return The builder
         */
        public _Builder addToByteList(byte... values) {
            optionals.set(1);
            modified.set(1);
            for (byte item : values) {
                mByteList.add(item);
            }
            return this;
        }

        /**
         * Checks for presence of the byteList field.
         *
         * @return True if byteList has been set.
         */
        public boolean isSetByteList() {
            return optionals.get(1);
        }

        /**
         * Checks if byteList has been modified since the _Builder was created.
         *
         * @return True if byteList has been modified.
         */
        public boolean isModifiedByteList() {
            return modified.get(1);
        }

        /**
         * Clears the byteList field.
         *
         * @return The builder
         */
        public _Builder clearByteList() {
            optionals.clear(1);
            modified.set(1);
            mByteList.clear();
            return this;
        }

        /**
         * Gets the builder for the contained byteList.
         *
         * @return The field builder
         */
        public net.morimekta.providence.descriptor.PList.Builder<Byte> mutableByteList() {
            optionals.set(1);
            modified.set(1);
            return mByteList;
        }

        /**
         * Sets the value of shortList.
         *
         * @param value The new value
         * @return The builder
         */
        public _Builder setShortList(java.util.Collection<Short> value) {
            optionals.set(2);
            modified.set(2);
            mShortList.clear();
            mShortList.addAll(value);
            return this;
        }

        /**
         * Adds entries to shortList.
         *
         * @param values The added value
         * @return The builder
         */
        public _Builder addToShortList(short... values) {
            optionals.set(2);
            modified.set(2);
            for (short item : values) {
                mShortList.add(item);
            }
            return this;
        }

        /**
         * Checks for presence of the shortList field.
         *
         * @return True if shortList has been set.
         */
        public boolean isSetShortList() {
            return optionals.get(2);
        }

        /**
         * Checks if shortList has been modified since the _Builder was created.
         *
         * @return True if shortList has been modified.
         */
        public boolean isModifiedShortList() {
            return modified.get(2);
        }

        /**
         * Clears the shortList field.
         *
         * @return The builder
         */
        public _Builder clearShortList() {
            optionals.clear(2);
            modified.set(2);
            mShortList.clear();
            return this;
        }

        /**
         * Gets the builder for the contained shortList.
         *
         * @return The field builder
         */
        public net.morimekta.providence.descriptor.PList.Builder<Short> mutableShortList() {
            optionals.set(2);
            modified.set(2);
            return mShortList;
        }

        /**
         * Sets the value of integerList.
         *
         * @param value The new value
         * @return The builder
         */
        public _Builder setIntegerList(java.util.Collection<Integer> value) {
            optionals.set(3);
            modified.set(3);
            mIntegerList.clear();
            mIntegerList.addAll(value);
            return this;
        }

        /**
         * Adds entries to integerList.
         *
         * @param values The added value
         * @return The builder
         */
        public _Builder addToIntegerList(int... values) {
            optionals.set(3);
            modified.set(3);
            for (int item : values) {
                mIntegerList.add(item);
            }
            return this;
        }

        /**
         * Checks for presence of the integerList field.
         *
         * @return True if integerList has been set.
         */
        public boolean isSetIntegerList() {
            return optionals.get(3);
        }

        /**
         * Checks if integerList has been modified since the _Builder was created.
         *
         * @return True if integerList has been modified.
         */
        public boolean isModifiedIntegerList() {
            return modified.get(3);
        }

        /**
         * Clears the integerList field.
         *
         * @return The builder
         */
        public _Builder clearIntegerList() {
            optionals.clear(3);
            modified.set(3);
            mIntegerList.clear();
            return this;
        }

        /**
         * Gets the builder for the contained integerList.
         *
         * @return The field builder
         */
        public net.morimekta.providence.descriptor.PList.Builder<Integer> mutableIntegerList() {
            optionals.set(3);
            modified.set(3);
            return mIntegerList;
        }

        /**
         * Sets the value of longList.
         *
         * @param value The new value
         * @return The builder
         */
        public _Builder setLongList(java.util.Collection<Long> value) {
            optionals.set(4);
            modified.set(4);
            mLongList.clear();
            mLongList.addAll(value);
            return this;
        }

        /**
         * Adds entries to longList.
         *
         * @param values The added value
         * @return The builder
         */
        public _Builder addToLongList(long... values) {
            optionals.set(4);
            modified.set(4);
            for (long item : values) {
                mLongList.add(item);
            }
            return this;
        }

        /**
         * Checks for presence of the longList field.
         *
         * @return True if longList has been set.
         */
        public boolean isSetLongList() {
            return optionals.get(4);
        }

        /**
         * Checks if longList has been modified since the _Builder was created.
         *
         * @return True if longList has been modified.
         */
        public boolean isModifiedLongList() {
            return modified.get(4);
        }

        /**
         * Clears the longList field.
         *
         * @return The builder
         */
        public _Builder clearLongList() {
            optionals.clear(4);
            modified.set(4);
            mLongList.clear();
            return this;
        }

        /**
         * Gets the builder for the contained longList.
         *
         * @return The field builder
         */
        public net.morimekta.providence.descriptor.PList.Builder<Long> mutableLongList() {
            optionals.set(4);
            modified.set(4);
            return mLongList;
        }

        /**
         * Sets the value of doubleList.
         *
         * @param value The new value
         * @return The builder
         */
        public _Builder setDoubleList(java.util.Collection<Double> value) {
            optionals.set(5);
            modified.set(5);
            mDoubleList.clear();
            mDoubleList.addAll(value);
            return this;
        }

        /**
         * Adds entries to doubleList.
         *
         * @param values The added value
         * @return The builder
         */
        public _Builder addToDoubleList(double... values) {
            optionals.set(5);
            modified.set(5);
            for (double item : values) {
                mDoubleList.add(item);
            }
            return this;
        }

        /**
         * Checks for presence of the doubleList field.
         *
         * @return True if doubleList has been set.
         */
        public boolean isSetDoubleList() {
            return optionals.get(5);
        }

        /**
         * Checks if doubleList has been modified since the _Builder was created.
         *
         * @return True if doubleList has been modified.
         */
        public boolean isModifiedDoubleList() {
            return modified.get(5);
        }

        /**
         * Clears the doubleList field.
         *
         * @return The builder
         */
        public _Builder clearDoubleList() {
            optionals.clear(5);
            modified.set(5);
            mDoubleList.clear();
            return this;
        }

        /**
         * Gets the builder for the contained doubleList.
         *
         * @return The field builder
         */
        public net.morimekta.providence.descriptor.PList.Builder<Double> mutableDoubleList() {
            optionals.set(5);
            modified.set(5);
            return mDoubleList;
        }

        /**
         * Sets the value of stringList.
         *
         * @param value The new value
         * @return The builder
         */
        public _Builder setStringList(java.util.Collection<String> value) {
            optionals.set(6);
            modified.set(6);
            mStringList.clear();
            mStringList.addAll(value);
            return this;
        }

        /**
         * Adds entries to stringList.
         *
         * @param values The added value
         * @return The builder
         */
        public _Builder addToStringList(String... values) {
            optionals.set(6);
            modified.set(6);
            for (String item : values) {
                mStringList.add(item);
            }
            return this;
        }

        /**
         * Checks for presence of the stringList field.
         *
         * @return True if stringList has been set.
         */
        public boolean isSetStringList() {
            return optionals.get(6);
        }

        /**
         * Checks if stringList has been modified since the _Builder was created.
         *
         * @return True if stringList has been modified.
         */
        public boolean isModifiedStringList() {
            return modified.get(6);
        }

        /**
         * Clears the stringList field.
         *
         * @return The builder
         */
        public _Builder clearStringList() {
            optionals.clear(6);
            modified.set(6);
            mStringList.clear();
            return this;
        }

        /**
         * Gets the builder for the contained stringList.
         *
         * @return The field builder
         */
        public net.morimekta.providence.descriptor.PList.Builder<String> mutableStringList() {
            optionals.set(6);
            modified.set(6);
            return mStringList;
        }

        /**
         * Sets the value of binaryList.
         *
         * @param value The new value
         * @return The builder
         */
        public _Builder setBinaryList(java.util.Collection<net.morimekta.util.Binary> value) {
            optionals.set(7);
            modified.set(7);
            mBinaryList.clear();
            mBinaryList.addAll(value);
            return this;
        }

        /**
         * Adds entries to binaryList.
         *
         * @param values The added value
         * @return The builder
         */
        public _Builder addToBinaryList(net.morimekta.util.Binary... values) {
            optionals.set(7);
            modified.set(7);
            for (net.morimekta.util.Binary item : values) {
                mBinaryList.add(item);
            }
            return this;
        }

        /**
         * Checks for presence of the binaryList field.
         *
         * @return True if binaryList has been set.
         */
        public boolean isSetBinaryList() {
            return optionals.get(7);
        }

        /**
         * Checks if binaryList has been modified since the _Builder was created.
         *
         * @return True if binaryList has been modified.
         */
        public boolean isModifiedBinaryList() {
            return modified.get(7);
        }

        /**
         * Clears the binaryList field.
         *
         * @return The builder
         */
        public _Builder clearBinaryList() {
            optionals.clear(7);
            modified.set(7);
            mBinaryList.clear();
            return this;
        }

        /**
         * Gets the builder for the contained binaryList.
         *
         * @return The field builder
         */
        public net.morimekta.providence.descriptor.PList.Builder<net.morimekta.util.Binary> mutableBinaryList() {
            optionals.set(7);
            modified.set(7);
            return mBinaryList;
        }

        /**
         * all types as set&lt;x&gt;.
         *
         * @param value The new value
         * @return The builder
         */
        public _Builder setBooleanSet(java.util.Collection<Boolean> value) {
            optionals.set(8);
            modified.set(8);
            mBooleanSet.clear();
            mBooleanSet.addAll(value);
            return this;
        }

        /**
         * all types as set&lt;x&gt;.
         *
         * @param values The added value
         * @return The builder
         */
        public _Builder addToBooleanSet(boolean... values) {
            optionals.set(8);
            modified.set(8);
            for (boolean item : values) {
                mBooleanSet.add(item);
            }
            return this;
        }

        /**
         * all types as set&lt;x&gt;.
         *
         * @return True if booleanSet has been set.
         */
        public boolean isSetBooleanSet() {
            return optionals.get(8);
        }

        /**
         * all types as set&lt;x&gt;.
         *
         * @return True if booleanSet has been modified.
         */
        public boolean isModifiedBooleanSet() {
            return modified.get(8);
        }

        /**
         * all types as set&lt;x&gt;.
         *
         * @return The builder
         */
        public _Builder clearBooleanSet() {
            optionals.clear(8);
            modified.set(8);
            mBooleanSet.clear();
            return this;
        }

        /**
         * all types as set&lt;x&gt;.
         *
         * @return The field builder
         */
        public net.morimekta.providence.descriptor.PSet.Builder<Boolean> mutableBooleanSet() {
            optionals.set(8);
            modified.set(8);
            return mBooleanSet;
        }

        /**
         * Sets the value of byteSet.
         *
         * @param value The new value
         * @return The builder
         */
        public _Builder setByteSet(java.util.Collection<Byte> value) {
            optionals.set(9);
            modified.set(9);
            mByteSet.clear();
            mByteSet.addAll(value);
            return this;
        }

        /**
         * Adds entries to byteSet.
         *
         * @param values The added value
         * @return The builder
         */
        public _Builder addToByteSet(byte... values) {
            optionals.set(9);
            modified.set(9);
            for (byte item : values) {
                mByteSet.add(item);
            }
            return this;
        }

        /**
         * Checks for presence of the byteSet field.
         *
         * @return True if byteSet has been set.
         */
        public boolean isSetByteSet() {
            return optionals.get(9);
        }

        /**
         * Checks if byteSet has been modified since the _Builder was created.
         *
         * @return True if byteSet has been modified.
         */
        public boolean isModifiedByteSet() {
            return modified.get(9);
        }

        /**
         * Clears the byteSet field.
         *
         * @return The builder
         */
        public _Builder clearByteSet() {
            optionals.clear(9);
            modified.set(9);
            mByteSet.clear();
            return this;
        }

        /**
         * Gets the builder for the contained byteSet.
         *
         * @return The field builder
         */
        public net.morimekta.providence.descriptor.PSet.Builder<Byte> mutableByteSet() {
            optionals.set(9);
            modified.set(9);
            return mByteSet;
        }

        /**
         * Sets the value of shortSet.
         *
         * @param value The new value
         * @return The builder
         */
        public _Builder setShortSet(java.util.Collection<Short> value) {
            optionals.set(10);
            modified.set(10);
            mShortSet.clear();
            mShortSet.addAll(value);
            return this;
        }

        /**
         * Adds entries to shortSet.
         *
         * @param values The added value
         * @return The builder
         */
        public _Builder addToShortSet(short... values) {
            optionals.set(10);
            modified.set(10);
            for (short item : values) {
                mShortSet.add(item);
            }
            return this;
        }

        /**
         * Checks for presence of the shortSet field.
         *
         * @return True if shortSet has been set.
         */
        public boolean isSetShortSet() {
            return optionals.get(10);
        }

        /**
         * Checks if shortSet has been modified since the _Builder was created.
         *
         * @return True if shortSet has been modified.
         */
        public boolean isModifiedShortSet() {
            return modified.get(10);
        }

        /**
         * Clears the shortSet field.
         *
         * @return The builder
         */
        public _Builder clearShortSet() {
            optionals.clear(10);
            modified.set(10);
            mShortSet.clear();
            return this;
        }

        /**
         * Gets the builder for the contained shortSet.
         *
         * @return The field builder
         */
        public net.morimekta.providence.descriptor.PSet.Builder<Short> mutableShortSet() {
            optionals.set(10);
            modified.set(10);
            return mShortSet;
        }

        /**
         * Sets the value of integerSet.
         *
         * @param value The new value
         * @return The builder
         */
        public _Builder setIntegerSet(java.util.Collection<Integer> value) {
            optionals.set(11);
            modified.set(11);
            mIntegerSet.clear();
            mIntegerSet.addAll(value);
            return this;
        }

        /**
         * Adds entries to integerSet.
         *
         * @param values The added value
         * @return The builder
         */
        public _Builder addToIntegerSet(int... values) {
            optionals.set(11);
            modified.set(11);
            for (int item : values) {
                mIntegerSet.add(item);
            }
            return this;
        }

        /**
         * Checks for presence of the integerSet field.
         *
         * @return True if integerSet has been set.
         */
        public boolean isSetIntegerSet() {
            return optionals.get(11);
        }

        /**
         * Checks if integerSet has been modified since the _Builder was created.
         *
         * @return True if integerSet has been modified.
         */
        public boolean isModifiedIntegerSet() {
            return modified.get(11);
        }

        /**
         * Clears the integerSet field.
         *
         * @return The builder
         */
        public _Builder clearIntegerSet() {
            optionals.clear(11);
            modified.set(11);
            mIntegerSet.clear();
            return this;
        }

        /**
         * Gets the builder for the contained integerSet.
         *
         * @return The field builder
         */
        public net.morimekta.providence.descriptor.PSet.Builder<Integer> mutableIntegerSet() {
            optionals.set(11);
            modified.set(11);
            return mIntegerSet;
        }

        /**
         * Sets the value of longSet.
         *
         * @param value The new value
         * @return The builder
         */
        public _Builder setLongSet(java.util.Collection<Long> value) {
            optionals.set(12);
            modified.set(12);
            mLongSet.clear();
            mLongSet.addAll(value);
            return this;
        }

        /**
         * Adds entries to longSet.
         *
         * @param values The added value
         * @return The builder
         */
        public _Builder addToLongSet(long... values) {
            optionals.set(12);
            modified.set(12);
            for (long item : values) {
                mLongSet.add(item);
            }
            return this;
        }

        /**
         * Checks for presence of the longSet field.
         *
         * @return True if longSet has been set.
         */
        public boolean isSetLongSet() {
            return optionals.get(12);
        }

        /**
         * Checks if longSet has been modified since the _Builder was created.
         *
         * @return True if longSet has been modified.
         */
        public boolean isModifiedLongSet() {
            return modified.get(12);
        }

        /**
         * Clears the longSet field.
         *
         * @return The builder
         */
        public _Builder clearLongSet() {
            optionals.clear(12);
            modified.set(12);
            mLongSet.clear();
            return this;
        }

        /**
         * Gets the builder for the contained longSet.
         *
         * @return The field builder
         */
        public net.morimekta.providence.descriptor.PSet.Builder<Long> mutableLongSet() {
            optionals.set(12);
            modified.set(12);
            return mLongSet;
        }

        /**
         * Sets the value of doubleSet.
         *
         * @param value The new value
         * @return The builder
         */
        public _Builder setDoubleSet(java.util.Collection<Double> value) {
            optionals.set(13);
            modified.set(13);
            mDoubleSet.clear();
            mDoubleSet.addAll(value);
            return this;
        }

        /**
         * Adds entries to doubleSet.
         *
         * @param values The added value
         * @return The builder
         */
        public _Builder addToDoubleSet(double... values) {
            optionals.set(13);
            modified.set(13);
            for (double item : values) {
                mDoubleSet.add(item);
            }
            return this;
        }

        /**
         * Checks for presence of the doubleSet field.
         *
         * @return True if doubleSet has been set.
         */
        public boolean isSetDoubleSet() {
            return optionals.get(13);
        }

        /**
         * Checks if doubleSet has been modified since the _Builder was created.
         *
         * @return True if doubleSet has been modified.
         */
        public boolean isModifiedDoubleSet() {
            return modified.get(13);
        }

        /**
         * Clears the doubleSet field.
         *
         * @return The builder
         */
        public _Builder clearDoubleSet() {
            optionals.clear(13);
            modified.set(13);
            mDoubleSet.clear();
            return this;
        }

        /**
         * Gets the builder for the contained doubleSet.
         *
         * @return The field builder
         */
        public net.morimekta.providence.descriptor.PSet.Builder<Double> mutableDoubleSet() {
            optionals.set(13);
            modified.set(13);
            return mDoubleSet;
        }

        /**
         * Sets the value of stringSet.
         *
         * @param value The new value
         * @return The builder
         */
        public _Builder setStringSet(java.util.Collection<String> value) {
            optionals.set(14);
            modified.set(14);
            mStringSet.clear();
            mStringSet.addAll(value);
            return this;
        }

        /**
         * Adds entries to stringSet.
         *
         * @param values The added value
         * @return The builder
         */
        public _Builder addToStringSet(String... values) {
            optionals.set(14);
            modified.set(14);
            for (String item : values) {
                mStringSet.add(item);
            }
            return this;
        }

        /**
         * Checks for presence of the stringSet field.
         *
         * @return True if stringSet has been set.
         */
        public boolean isSetStringSet() {
            return optionals.get(14);
        }

        /**
         * Checks if stringSet has been modified since the _Builder was created.
         *
         * @return True if stringSet has been modified.
         */
        public boolean isModifiedStringSet() {
            return modified.get(14);
        }

        /**
         * Clears the stringSet field.
         *
         * @return The builder
         */
        public _Builder clearStringSet() {
            optionals.clear(14);
            modified.set(14);
            mStringSet.clear();
            return this;
        }

        /**
         * Gets the builder for the contained stringSet.
         *
         * @return The field builder
         */
        public net.morimekta.providence.descriptor.PSet.Builder<String> mutableStringSet() {
            optionals.set(14);
            modified.set(14);
            return mStringSet;
        }

        /**
         * Sets the value of binarySet.
         *
         * @param value The new value
         * @return The builder
         */
        public _Builder setBinarySet(java.util.Collection<net.morimekta.util.Binary> value) {
            optionals.set(15);
            modified.set(15);
            mBinarySet.clear();
            mBinarySet.addAll(value);
            return this;
        }

        /**
         * Adds entries to binarySet.
         *
         * @param values The added value
         * @return The builder
         */
        public _Builder addToBinarySet(net.morimekta.util.Binary... values) {
            optionals.set(15);
            modified.set(15);
            for (net.morimekta.util.Binary item : values) {
                mBinarySet.add(item);
            }
            return this;
        }

        /**
         * Checks for presence of the binarySet field.
         *
         * @return True if binarySet has been set.
         */
        public boolean isSetBinarySet() {
            return optionals.get(15);
        }

        /**
         * Checks if binarySet has been modified since the _Builder was created.
         *
         * @return True if binarySet has been modified.
         */
        public boolean isModifiedBinarySet() {
            return modified.get(15);
        }

        /**
         * Clears the binarySet field.
         *
         * @return The builder
         */
        public _Builder clearBinarySet() {
            optionals.clear(15);
            modified.set(15);
            mBinarySet.clear();
            return this;
        }

        /**
         * Gets the builder for the contained binarySet.
         *
         * @return The field builder
         */
        public net.morimekta.providence.descriptor.PSet.Builder<net.morimekta.util.Binary> mutableBinarySet() {
            optionals.set(15);
            modified.set(15);
            return mBinarySet;
        }

        /**
         * all types as map&lt;x,x&gt;.
         *
         * @param value The new value
         * @return The builder
         */
        public _Builder setBooleanMap(java.util.Map<Boolean,Boolean> value) {
            optionals.set(16);
            modified.set(16);
            mBooleanMap.clear();
            mBooleanMap.putAll(value);
            return this;
        }

        /**
         * all types as map&lt;x,x&gt;.
         *
         * @param key The inserted key
         * @param value The inserted value
         * @return The builder
         */
        public _Builder putInBooleanMap(boolean key, boolean value) {
            optionals.set(16);
            modified.set(16);
            mBooleanMap.put(key, value);
            return this;
        }

        /**
         * all types as map&lt;x,x&gt;.
         *
         * @return True if booleanMap has been set.
         */
        public boolean isSetBooleanMap() {
            return optionals.get(16);
        }

        /**
         * all types as map&lt;x,x&gt;.
         *
         * @return True if booleanMap has been modified.
         */
        public boolean isModifiedBooleanMap() {
            return modified.get(16);
        }

        /**
         * all types as map&lt;x,x&gt;.
         *
         * @return The builder
         */
        public _Builder clearBooleanMap() {
            optionals.clear(16);
            modified.set(16);
            mBooleanMap.clear();
            return this;
        }

        /**
         * all types as map&lt;x,x&gt;.
         *
         * @return The field builder
         */
        public net.morimekta.providence.descriptor.PMap.Builder<Boolean,Boolean> mutableBooleanMap() {
            optionals.set(16);
            modified.set(16);
            return mBooleanMap;
        }

        /**
         * Sets the value of byteMap.
         *
         * @param value The new value
         * @return The builder
         */
        public _Builder setByteMap(java.util.Map<Byte,Byte> value) {
            optionals.set(17);
            modified.set(17);
            mByteMap.clear();
            mByteMap.putAll(value);
            return this;
        }

        /**
         * Adds a mapping to byteMap.
         *
         * @param key The inserted key
         * @param value The inserted value
         * @return The builder
         */
        public _Builder putInByteMap(byte key, byte value) {
            optionals.set(17);
            modified.set(17);
            mByteMap.put(key, value);
            return this;
        }

        /**
         * Checks for presence of the byteMap field.
         *
         * @return True if byteMap has been set.
         */
        public boolean isSetByteMap() {
            return optionals.get(17);
        }

        /**
         * Checks if byteMap has been modified since the _Builder was created.
         *
         * @return True if byteMap has been modified.
         */
        public boolean isModifiedByteMap() {
            return modified.get(17);
        }

        /**
         * Clears the byteMap field.
         *
         * @return The builder
         */
        public _Builder clearByteMap() {
            optionals.clear(17);
            modified.set(17);
            mByteMap.clear();
            return this;
        }

        /**
         * Gets the builder for the contained byteMap.
         *
         * @return The field builder
         */
        public net.morimekta.providence.descriptor.PMap.Builder<Byte,Byte> mutableByteMap() {
            optionals.set(17);
            modified.set(17);
            return mByteMap;
        }

        /**
         * Sets the value of shortMap.
         *
         * @param value The new value
         * @return The builder
         */
        public _Builder setShortMap(java.util.Map<Short,Short> value) {
            optionals.set(18);
            modified.set(18);
            mShortMap.clear();
            mShortMap.putAll(value);
            return this;
        }

        /**
         * Adds a mapping to shortMap.
         *
         * @param key The inserted key
         * @param value The inserted value
         * @return The builder
         */
        public _Builder putInShortMap(short key, short value) {
            optionals.set(18);
            modified.set(18);
            mShortMap.put(key, value);
            return this;
        }

        /**
         * Checks for presence of the shortMap field.
         *
         * @return True if shortMap has been set.
         */
        public boolean isSetShortMap() {
            return optionals.get(18);
        }

        /**
         * Checks if shortMap has been modified since the _Builder was created.
         *
         * @return True if shortMap has been modified.
         */
        public boolean isModifiedShortMap() {
            return modified.get(18);
        }

        /**
         * Clears the shortMap field.
         *
         * @return The builder
         */
        public _Builder clearShortMap() {
            optionals.clear(18);
            modified.set(18);
            mShortMap.clear();
            return this;
        }

        /**
         * Gets the builder for the contained shortMap.
         *
         * @return The field builder
         */
        public net.morimekta.providence.descriptor.PMap.Builder<Short,Short> mutableShortMap() {
            optionals.set(18);
            modified.set(18);
            return mShortMap;
        }

        /**
         * Sets the value of integerMap.
         *
         * @param value The new value
         * @return The builder
         */
        public _Builder setIntegerMap(java.util.Map<Integer,Integer> value) {
            optionals.set(19);
            modified.set(19);
            mIntegerMap.clear();
            mIntegerMap.putAll(value);
            return this;
        }

        /**
         * Adds a mapping to integerMap.
         *
         * @param key The inserted key
         * @param value The inserted value
         * @return The builder
         */
        public _Builder putInIntegerMap(int key, int value) {
            optionals.set(19);
            modified.set(19);
            mIntegerMap.put(key, value);
            return this;
        }

        /**
         * Checks for presence of the integerMap field.
         *
         * @return True if integerMap has been set.
         */
        public boolean isSetIntegerMap() {
            return optionals.get(19);
        }

        /**
         * Checks if integerMap has been modified since the _Builder was created.
         *
         * @return True if integerMap has been modified.
         */
        public boolean isModifiedIntegerMap() {
            return modified.get(19);
        }

        /**
         * Clears the integerMap field.
         *
         * @return The builder
         */
        public _Builder clearIntegerMap() {
            optionals.clear(19);
            modified.set(19);
            mIntegerMap.clear();
            return this;
        }

        /**
         * Gets the builder for the contained integerMap.
         *
         * @return The field builder
         */
        public net.morimekta.providence.descriptor.PMap.Builder<Integer,Integer> mutableIntegerMap() {
            optionals.set(19);
            modified.set(19);
            return mIntegerMap;
        }

        /**
         * Sets the value of longMap.
         *
         * @param value The new value
         * @return The builder
         */
        public _Builder setLongMap(java.util.Map<Long,Long> value) {
            optionals.set(20);
            modified.set(20);
            mLongMap.clear();
            mLongMap.putAll(value);
            return this;
        }

        /**
         * Adds a mapping to longMap.
         *
         * @param key The inserted key
         * @param value The inserted value
         * @return The builder
         */
        public _Builder putInLongMap(long key, long value) {
            optionals.set(20);
            modified.set(20);
            mLongMap.put(key, value);
            return this;
        }

        /**
         * Checks for presence of the longMap field.
         *
         * @return True if longMap has been set.
         */
        public boolean isSetLongMap() {
            return optionals.get(20);
        }

        /**
         * Checks if longMap has been modified since the _Builder was created.
         *
         * @return True if longMap has been modified.
         */
        public boolean isModifiedLongMap() {
            return modified.get(20);
        }

        /**
         * Clears the longMap field.
         *
         * @return The builder
         */
        public _Builder clearLongMap() {
            optionals.clear(20);
            modified.set(20);
            mLongMap.clear();
            return this;
        }

        /**
         * Gets the builder for the contained longMap.
         *
         * @return The field builder
         */
        public net.morimekta.providence.descriptor.PMap.Builder<Long,Long> mutableLongMap() {
            optionals.set(20);
            modified.set(20);
            return mLongMap;
        }

        /**
         * Sets the value of doubleMap.
         *
         * @param value The new value
         * @return The builder
         */
        public _Builder setDoubleMap(java.util.Map<Double,Double> value) {
            optionals.set(21);
            modified.set(21);
            mDoubleMap.clear();
            mDoubleMap.putAll(value);
            return this;
        }

        /**
         * Adds a mapping to doubleMap.
         *
         * @param key The inserted key
         * @param value The inserted value
         * @return The builder
         */
        public _Builder putInDoubleMap(double key, double value) {
            optionals.set(21);
            modified.set(21);
            mDoubleMap.put(key, value);
            return this;
        }

        /**
         * Checks for presence of the doubleMap field.
         *
         * @return True if doubleMap has been set.
         */
        public boolean isSetDoubleMap() {
            return optionals.get(21);
        }

        /**
         * Checks if doubleMap has been modified since the _Builder was created.
         *
         * @return True if doubleMap has been modified.
         */
        public boolean isModifiedDoubleMap() {
            return modified.get(21);
        }

        /**
         * Clears the doubleMap field.
         *
         * @return The builder
         */
        public _Builder clearDoubleMap() {
            optionals.clear(21);
            modified.set(21);
            mDoubleMap.clear();
            return this;
        }

        /**
         * Gets the builder for the contained doubleMap.
         *
         * @return The field builder
         */
        public net.morimekta.providence.descriptor.PMap.Builder<Double,Double> mutableDoubleMap() {
            optionals.set(21);
            modified.set(21);
            return mDoubleMap;
        }

        /**
         * Sets the value of stringMap.
         *
         * @param value The new value
         * @return The builder
         */
        public _Builder setStringMap(java.util.Map<String,String> value) {
            optionals.set(22);
            modified.set(22);
            mStringMap.clear();
            mStringMap.putAll(value);
            return this;
        }

        /**
         * Adds a mapping to stringMap.
         *
         * @param key The inserted key
         * @param value The inserted value
         * @return The builder
         */
        public _Builder putInStringMap(String key, String value) {
            optionals.set(22);
            modified.set(22);
            mStringMap.put(key, value);
            return this;
        }

        /**
         * Checks for presence of the stringMap field.
         *
         * @return True if stringMap has been set.
         */
        public boolean isSetStringMap() {
            return optionals.get(22);
        }

        /**
         * Checks if stringMap has been modified since the _Builder was created.
         *
         * @return True if stringMap has been modified.
         */
        public boolean isModifiedStringMap() {
            return modified.get(22);
        }

        /**
         * Clears the stringMap field.
         *
         * @return The builder
         */
        public _Builder clearStringMap() {
            optionals.clear(22);
            modified.set(22);
            mStringMap.clear();
            return this;
        }

        /**
         * Gets the builder for the contained stringMap.
         *
         * @return The field builder
         */
        public net.morimekta.providence.descriptor.PMap.Builder<String,String> mutableStringMap() {
            optionals.set(22);
            modified.set(22);
            return mStringMap;
        }

        /**
         * Sets the value of binaryMap.
         *
         * @param value The new value
         * @return The builder
         */
        public _Builder setBinaryMap(java.util.Map<net.morimekta.util.Binary,net.morimekta.util.Binary> value) {
            optionals.set(23);
            modified.set(23);
            mBinaryMap.clear();
            mBinaryMap.putAll(value);
            return this;
        }

        /**
         * Adds a mapping to binaryMap.
         *
         * @param key The inserted key
         * @param value The inserted value
         * @return The builder
         */
        public _Builder putInBinaryMap(net.morimekta.util.Binary key, net.morimekta.util.Binary value) {
            optionals.set(23);
            modified.set(23);
            mBinaryMap.put(key, value);
            return this;
        }

        /**
         * Checks for presence of the binaryMap field.
         *
         * @return True if binaryMap has been set.
         */
        public boolean isSetBinaryMap() {
            return optionals.get(23);
        }

        /**
         * Checks if binaryMap has been modified since the _Builder was created.
         *
         * @return True if binaryMap has been modified.
         */
        public boolean isModifiedBinaryMap() {
            return modified.get(23);
        }

        /**
         * Clears the binaryMap field.
         *
         * @return The builder
         */
        public _Builder clearBinaryMap() {
            optionals.clear(23);
            modified.set(23);
            mBinaryMap.clear();
            return this;
        }

        /**
         * Gets the builder for the contained binaryMap.
         *
         * @return The field builder
         */
        public net.morimekta.providence.descriptor.PMap.Builder<net.morimekta.util.Binary,net.morimekta.util.Binary> mutableBinaryMap() {
            optionals.set(23);
            modified.set(23);
            return mBinaryMap;
        }

        /**
         * Using enum as key and value in containers.
         *
         * @param value The new value
         * @return The builder
         */
        public _Builder setEnumList(java.util.Collection<net.morimekta.test.providence.Value> value) {
            optionals.set(24);
            modified.set(24);
            mEnumList.clear();
            mEnumList.addAll(value);
            return this;
        }

        /**
         * Using enum as key and value in containers.
         *
         * @param values The added value
         * @return The builder
         */
        public _Builder addToEnumList(net.morimekta.test.providence.Value... values) {
            optionals.set(24);
            modified.set(24);
            for (net.morimekta.test.providence.Value item : values) {
                mEnumList.add(item);
            }
            return this;
        }

        /**
         * Using enum as key and value in containers.
         *
         * @return True if enumList has been set.
         */
        public boolean isSetEnumList() {
            return optionals.get(24);
        }

        /**
         * Using enum as key and value in containers.
         *
         * @return True if enumList has been modified.
         */
        public boolean isModifiedEnumList() {
            return modified.get(24);
        }

        /**
         * Using enum as key and value in containers.
         *
         * @return The builder
         */
        public _Builder clearEnumList() {
            optionals.clear(24);
            modified.set(24);
            mEnumList.clear();
            return this;
        }

        /**
         * Using enum as key and value in containers.
         *
         * @return The field builder
         */
        public net.morimekta.providence.descriptor.PList.Builder<net.morimekta.test.providence.Value> mutableEnumList() {
            optionals.set(24);
            modified.set(24);
            return mEnumList;
        }

        /**
         * Sets the value of enumSet.
         *
         * @param value The new value
         * @return The builder
         */
        public _Builder setEnumSet(java.util.Collection<net.morimekta.test.providence.Value> value) {
            optionals.set(25);
            modified.set(25);
            mEnumSet.clear();
            mEnumSet.addAll(value);
            return this;
        }

        /**
         * Adds entries to enumSet.
         *
         * @param values The added value
         * @return The builder
         */
        public _Builder addToEnumSet(net.morimekta.test.providence.Value... values) {
            optionals.set(25);
            modified.set(25);
            for (net.morimekta.test.providence.Value item : values) {
                mEnumSet.add(item);
            }
            return this;
        }

        /**
         * Checks for presence of the enumSet field.
         *
         * @return True if enumSet has been set.
         */
        public boolean isSetEnumSet() {
            return optionals.get(25);
        }

        /**
         * Checks if enumSet has been modified since the _Builder was created.
         *
         * @return True if enumSet has been modified.
         */
        public boolean isModifiedEnumSet() {
            return modified.get(25);
        }

        /**
         * Clears the enumSet field.
         *
         * @return The builder
         */
        public _Builder clearEnumSet() {
            optionals.clear(25);
            modified.set(25);
            mEnumSet.clear();
            return this;
        }

        /**
         * Gets the builder for the contained enumSet.
         *
         * @return The field builder
         */
        public net.morimekta.providence.descriptor.PSet.Builder<net.morimekta.test.providence.Value> mutableEnumSet() {
            optionals.set(25);
            modified.set(25);
            return mEnumSet;
        }

        /**
         * Sets the value of enumMap.
         *
         * @param value The new value
         * @return The builder
         */
        public _Builder setEnumMap(java.util.Map<net.morimekta.test.providence.Value,net.morimekta.test.providence.Value> value) {
            optionals.set(26);
            modified.set(26);
            mEnumMap.clear();
            mEnumMap.putAll(value);
            return this;
        }

        /**
         * Adds a mapping to enumMap.
         *
         * @param key The inserted key
         * @param value The inserted value
         * @return The builder
         */
        public _Builder putInEnumMap(net.morimekta.test.providence.Value key, net.morimekta.test.providence.Value value) {
            optionals.set(26);
            modified.set(26);
            mEnumMap.put(key, value);
            return this;
        }

        /**
         * Checks for presence of the enumMap field.
         *
         * @return True if enumMap has been set.
         */
        public boolean isSetEnumMap() {
            return optionals.get(26);
        }

        /**
         * Checks if enumMap has been modified since the _Builder was created.
         *
         * @return True if enumMap has been modified.
         */
        public boolean isModifiedEnumMap() {
            return modified.get(26);
        }

        /**
         * Clears the enumMap field.
         *
         * @return The builder
         */
        public _Builder clearEnumMap() {
            optionals.clear(26);
            modified.set(26);
            mEnumMap.clear();
            return this;
        }

        /**
         * Gets the builder for the contained enumMap.
         *
         * @return The field builder
         */
        public net.morimekta.providence.descriptor.PMap.Builder<net.morimekta.test.providence.Value,net.morimekta.test.providence.Value> mutableEnumMap() {
            optionals.set(26);
            modified.set(26);
            return mEnumMap;
        }

        /**
         * Using struct as key and value in containers.
         *
         * @param value The new value
         * @return The builder
         */
        public _Builder setMessageList(java.util.Collection<net.morimekta.test.providence.DefaultFields> value) {
            optionals.set(27);
            modified.set(27);
            mMessageList.clear();
            mMessageList.addAll(value);
            return this;
        }

        /**
         * Using struct as key and value in containers.
         *
         * @param values The added value
         * @return The builder
         */
        public _Builder addToMessageList(net.morimekta.test.providence.DefaultFields... values) {
            optionals.set(27);
            modified.set(27);
            for (net.morimekta.test.providence.DefaultFields item : values) {
                mMessageList.add(item);
            }
            return this;
        }

        /**
         * Using struct as key and value in containers.
         *
         * @return True if messageList has been set.
         */
        public boolean isSetMessageList() {
            return optionals.get(27);
        }

        /**
         * Using struct as key and value in containers.
         *
         * @return True if messageList has been modified.
         */
        public boolean isModifiedMessageList() {
            return modified.get(27);
        }

        /**
         * Using struct as key and value in containers.
         *
         * @return The builder
         */
        public _Builder clearMessageList() {
            optionals.clear(27);
            modified.set(27);
            mMessageList.clear();
            return this;
        }

        /**
         * Using struct as key and value in containers.
         *
         * @return The field builder
         */
        public net.morimekta.providence.descriptor.PList.Builder<net.morimekta.test.providence.DefaultFields> mutableMessageList() {
            optionals.set(27);
            modified.set(27);
            return mMessageList;
        }

        /**
         * Sets the value of messageSet.
         *
         * @param value The new value
         * @return The builder
         */
        public _Builder setMessageSet(java.util.Collection<net.morimekta.test.providence.DefaultFields> value) {
            optionals.set(28);
            modified.set(28);
            mMessageSet.clear();
            mMessageSet.addAll(value);
            return this;
        }

        /**
         * Adds entries to messageSet.
         *
         * @param values The added value
         * @return The builder
         */
        public _Builder addToMessageSet(net.morimekta.test.providence.DefaultFields... values) {
            optionals.set(28);
            modified.set(28);
            for (net.morimekta.test.providence.DefaultFields item : values) {
                mMessageSet.add(item);
            }
            return this;
        }

        /**
         * Checks for presence of the messageSet field.
         *
         * @return True if messageSet has been set.
         */
        public boolean isSetMessageSet() {
            return optionals.get(28);
        }

        /**
         * Checks if messageSet has been modified since the _Builder was created.
         *
         * @return True if messageSet has been modified.
         */
        public boolean isModifiedMessageSet() {
            return modified.get(28);
        }

        /**
         * Clears the messageSet field.
         *
         * @return The builder
         */
        public _Builder clearMessageSet() {
            optionals.clear(28);
            modified.set(28);
            mMessageSet.clear();
            return this;
        }

        /**
         * Gets the builder for the contained messageSet.
         *
         * @return The field builder
         */
        public net.morimekta.providence.descriptor.PSet.Builder<net.morimekta.test.providence.DefaultFields> mutableMessageSet() {
            optionals.set(28);
            modified.set(28);
            return mMessageSet;
        }

        /**
         * Sets the value of messageMap.
         *
         * @param value The new value
         * @return The builder
         */
        public _Builder setMessageMap(java.util.Map<String,net.morimekta.test.providence.DefaultFields> value) {
            optionals.set(29);
            modified.set(29);
            mMessageMap.clear();
            mMessageMap.putAll(value);
            return this;
        }

        /**
         * Adds a mapping to messageMap.
         *
         * @param key The inserted key
         * @param value The inserted value
         * @return The builder
         */
        public _Builder putInMessageMap(String key, net.morimekta.test.providence.DefaultFields value) {
            optionals.set(29);
            modified.set(29);
            mMessageMap.put(key, value);
            return this;
        }

        /**
         * Checks for presence of the messageMap field.
         *
         * @return True if messageMap has been set.
         */
        public boolean isSetMessageMap() {
            return optionals.get(29);
        }

        /**
         * Checks if messageMap has been modified since the _Builder was created.
         *
         * @return True if messageMap has been modified.
         */
        public boolean isModifiedMessageMap() {
            return modified.get(29);
        }

        /**
         * Clears the messageMap field.
         *
         * @return The builder
         */
        public _Builder clearMessageMap() {
            optionals.clear(29);
            modified.set(29);
            mMessageMap.clear();
            return this;
        }

        /**
         * Gets the builder for the contained messageMap.
         *
         * @return The field builder
         */
        public net.morimekta.providence.descriptor.PMap.Builder<String,net.morimekta.test.providence.DefaultFields> mutableMessageMap() {
            optionals.set(29);
            modified.set(29);
            return mMessageMap;
        }

        /**
         * Sets the value of requiredFields.
         *
         * @param value The new value
         * @return The builder
         */
        public _Builder setRequiredFields(net.morimekta.test.providence.RequiredFields value) {
            optionals.set(30);
            modified.set(30);
            mRequiredFields_builder = null;
            mRequiredFields = value;
            return this;
        }

        /**
         * Checks for presence of the requiredFields field.
         *
         * @return True if requiredFields has been set.
         */
        public boolean isSetRequiredFields() {
            return optionals.get(30);
        }

        /**
         * Checks if requiredFields has been modified since the _Builder was created.
         *
         * @return True if requiredFields has been modified.
         */
        public boolean isModifiedRequiredFields() {
            return modified.get(30);
        }

        /**
         * Clears the requiredFields field.
         *
         * @return The builder
         */
        public _Builder clearRequiredFields() {
            optionals.clear(30);
            modified.set(30);
            mRequiredFields = null;
            mRequiredFields_builder = null;
            return this;
        }

        /**
         * Gets the builder for the contained requiredFields.
         *
         * @return The field builder
         */
        public net.morimekta.test.providence.RequiredFields._Builder mutableRequiredFields() {
            optionals.set(30);
            modified.set(30);

            if (mRequiredFields != null) {
                mRequiredFields_builder = mRequiredFields.mutate();
                mRequiredFields = null;
            } else if (mRequiredFields_builder == null) {
                mRequiredFields_builder = net.morimekta.test.providence.RequiredFields.builder();
            }
            return mRequiredFields_builder;
        }

        /**
         * Sets the value of defaultFields.
         *
         * @param value The new value
         * @return The builder
         */
        public _Builder setDefaultFields(net.morimekta.test.providence.DefaultFields value) {
            optionals.set(31);
            modified.set(31);
            mDefaultFields_builder = null;
            mDefaultFields = value;
            return this;
        }

        /**
         * Checks for presence of the defaultFields field.
         *
         * @return True if defaultFields has been set.
         */
        public boolean isSetDefaultFields() {
            return optionals.get(31);
        }

        /**
         * Checks if defaultFields has been modified since the _Builder was created.
         *
         * @return True if defaultFields has been modified.
         */
        public boolean isModifiedDefaultFields() {
            return modified.get(31);
        }

        /**
         * Clears the defaultFields field.
         *
         * @return The builder
         */
        public _Builder clearDefaultFields() {
            optionals.clear(31);
            modified.set(31);
            mDefaultFields = null;
            mDefaultFields_builder = null;
            return this;
        }

        /**
         * Gets the builder for the contained defaultFields.
         *
         * @return The field builder
         */
        public net.morimekta.test.providence.DefaultFields._Builder mutableDefaultFields() {
            optionals.set(31);
            modified.set(31);

            if (mDefaultFields != null) {
                mDefaultFields_builder = mDefaultFields.mutate();
                mDefaultFields = null;
            } else if (mDefaultFields_builder == null) {
                mDefaultFields_builder = net.morimekta.test.providence.DefaultFields.builder();
            }
            return mDefaultFields_builder;
        }

        /**
         * Sets the value of optionalFields.
         *
         * @param value The new value
         * @return The builder
         */
        public _Builder setOptionalFields(net.morimekta.test.providence.OptionalFields value) {
            optionals.set(32);
            modified.set(32);
            mOptionalFields_builder = null;
            mOptionalFields = value;
            return this;
        }

        /**
         * Checks for presence of the optionalFields field.
         *
         * @return True if optionalFields has been set.
         */
        public boolean isSetOptionalFields() {
            return optionals.get(32);
        }

        /**
         * Checks if optionalFields has been modified since the _Builder was created.
         *
         * @return True if optionalFields has been modified.
         */
        public boolean isModifiedOptionalFields() {
            return modified.get(32);
        }

        /**
         * Clears the optionalFields field.
         *
         * @return The builder
         */
        public _Builder clearOptionalFields() {
            optionals.clear(32);
            modified.set(32);
            mOptionalFields = null;
            mOptionalFields_builder = null;
            return this;
        }

        /**
         * Gets the builder for the contained optionalFields.
         *
         * @return The field builder
         */
        public net.morimekta.test.providence.OptionalFields._Builder mutableOptionalFields() {
            optionals.set(32);
            modified.set(32);

            if (mOptionalFields != null) {
                mOptionalFields_builder = mOptionalFields.mutate();
                mOptionalFields = null;
            } else if (mOptionalFields_builder == null) {
                mOptionalFields_builder = net.morimekta.test.providence.OptionalFields.builder();
            }
            return mOptionalFields_builder;
        }

        /**
         * Sets the value of unionFields.
         *
         * @param value The new value
         * @return The builder
         */
        public _Builder setUnionFields(net.morimekta.test.providence.UnionFields value) {
            optionals.set(33);
            modified.set(33);
            mUnionFields_builder = null;
            mUnionFields = value;
            return this;
        }

        /**
         * Checks for presence of the unionFields field.
         *
         * @return True if unionFields has been set.
         */
        public boolean isSetUnionFields() {
            return optionals.get(33);
        }

        /**
         * Checks if unionFields has been modified since the _Builder was created.
         *
         * @return True if unionFields has been modified.
         */
        public boolean isModifiedUnionFields() {
            return modified.get(33);
        }

        /**
         * Clears the unionFields field.
         *
         * @return The builder
         */
        public _Builder clearUnionFields() {
            optionals.clear(33);
            modified.set(33);
            mUnionFields = null;
            mUnionFields_builder = null;
            return this;
        }

        /**
         * Gets the builder for the contained unionFields.
         *
         * @return The field builder
         */
        public net.morimekta.test.providence.UnionFields._Builder mutableUnionFields() {
            optionals.set(33);
            modified.set(33);

            if (mUnionFields != null) {
                mUnionFields_builder = mUnionFields.mutate();
                mUnionFields = null;
            } else if (mUnionFields_builder == null) {
                mUnionFields_builder = net.morimekta.test.providence.UnionFields.builder();
            }
            return mUnionFields_builder;
        }

        /**
         * Sets the value of exceptionFields.
         *
         * @param value The new value
         * @return The builder
         */
        public _Builder setExceptionFields(net.morimekta.test.providence.ExceptionFields value) {
            optionals.set(34);
            modified.set(34);
            mExceptionFields_builder = null;
            mExceptionFields = value;
            return this;
        }

        /**
         * Checks for presence of the exceptionFields field.
         *
         * @return True if exceptionFields has been set.
         */
        public boolean isSetExceptionFields() {
            return optionals.get(34);
        }

        /**
         * Checks if exceptionFields has been modified since the _Builder was created.
         *
         * @return True if exceptionFields has been modified.
         */
        public boolean isModifiedExceptionFields() {
            return modified.get(34);
        }

        /**
         * Clears the exceptionFields field.
         *
         * @return The builder
         */
        public _Builder clearExceptionFields() {
            optionals.clear(34);
            modified.set(34);
            mExceptionFields = null;
            mExceptionFields_builder = null;
            return this;
        }

        /**
         * Gets the builder for the contained exceptionFields.
         *
         * @return The field builder
         */
        public net.morimekta.test.providence.ExceptionFields._Builder mutableExceptionFields() {
            optionals.set(34);
            modified.set(34);

            if (mExceptionFields != null) {
                mExceptionFields_builder = mExceptionFields.mutate();
                mExceptionFields = null;
            } else if (mExceptionFields_builder == null) {
                mExceptionFields_builder = net.morimekta.test.providence.ExceptionFields.builder();
            }
            return mExceptionFields_builder;
        }

        /**
         * Sets the value of defaultValues.
         *
         * @param value The new value
         * @return The builder
         */
        public _Builder setDefaultValues(net.morimekta.test.providence.DefaultValues value) {
            optionals.set(35);
            modified.set(35);
            mDefaultValues_builder = null;
            mDefaultValues = value;
            return this;
        }

        /**
         * Checks for presence of the defaultValues field.
         *
         * @return True if defaultValues has been set.
         */
        public boolean isSetDefaultValues() {
            return optionals.get(35);
        }

        /**
         * Checks if defaultValues has been modified since the _Builder was created.
         *
         * @return True if defaultValues has been modified.
         */
        public boolean isModifiedDefaultValues() {
            return modified.get(35);
        }

        /**
         * Clears the defaultValues field.
         *
         * @return The builder
         */
        public _Builder clearDefaultValues() {
            optionals.clear(35);
            modified.set(35);
            mDefaultValues = null;
            mDefaultValues_builder = null;
            return this;
        }

        /**
         * Gets the builder for the contained defaultValues.
         *
         * @return The field builder
         */
        public net.morimekta.test.providence.DefaultValues._Builder mutableDefaultValues() {
            optionals.set(35);
            modified.set(35);

            if (mDefaultValues != null) {
                mDefaultValues_builder = mDefaultValues.mutate();
                mDefaultValues = null;
            } else if (mDefaultValues_builder == null) {
                mDefaultValues_builder = net.morimekta.test.providence.DefaultValues.builder();
            }
            return mDefaultValues_builder;
        }

        @Override
        public boolean equals(Object o) {
            if (o == this) return true;
            if (o == null || !o.getClass().equals(getClass())) return false;
            Containers._Builder other = (Containers._Builder) o;
            return java.util.Objects.equals(optionals, other.optionals) &&
                   java.util.Objects.equals(mBooleanList, other.mBooleanList) &&
                   java.util.Objects.equals(mByteList, other.mByteList) &&
                   java.util.Objects.equals(mShortList, other.mShortList) &&
                   java.util.Objects.equals(mIntegerList, other.mIntegerList) &&
                   java.util.Objects.equals(mLongList, other.mLongList) &&
                   java.util.Objects.equals(mDoubleList, other.mDoubleList) &&
                   java.util.Objects.equals(mStringList, other.mStringList) &&
                   java.util.Objects.equals(mBinaryList, other.mBinaryList) &&
                   java.util.Objects.equals(mBooleanSet, other.mBooleanSet) &&
                   java.util.Objects.equals(mByteSet, other.mByteSet) &&
                   java.util.Objects.equals(mShortSet, other.mShortSet) &&
                   java.util.Objects.equals(mIntegerSet, other.mIntegerSet) &&
                   java.util.Objects.equals(mLongSet, other.mLongSet) &&
                   java.util.Objects.equals(mDoubleSet, other.mDoubleSet) &&
                   java.util.Objects.equals(mStringSet, other.mStringSet) &&
                   java.util.Objects.equals(mBinarySet, other.mBinarySet) &&
                   java.util.Objects.equals(mBooleanMap, other.mBooleanMap) &&
                   java.util.Objects.equals(mByteMap, other.mByteMap) &&
                   java.util.Objects.equals(mShortMap, other.mShortMap) &&
                   java.util.Objects.equals(mIntegerMap, other.mIntegerMap) &&
                   java.util.Objects.equals(mLongMap, other.mLongMap) &&
                   java.util.Objects.equals(mDoubleMap, other.mDoubleMap) &&
                   java.util.Objects.equals(mStringMap, other.mStringMap) &&
                   java.util.Objects.equals(mBinaryMap, other.mBinaryMap) &&
                   java.util.Objects.equals(mEnumList, other.mEnumList) &&
                   java.util.Objects.equals(mEnumSet, other.mEnumSet) &&
                   java.util.Objects.equals(mEnumMap, other.mEnumMap) &&
                   java.util.Objects.equals(mMessageList, other.mMessageList) &&
                   java.util.Objects.equals(mMessageSet, other.mMessageSet) &&
                   java.util.Objects.equals(mMessageMap, other.mMessageMap) &&
                   java.util.Objects.equals(mRequiredFields, other.mRequiredFields) &&
                   java.util.Objects.equals(mDefaultFields, other.mDefaultFields) &&
                   java.util.Objects.equals(mOptionalFields, other.mOptionalFields) &&
                   java.util.Objects.equals(mUnionFields, other.mUnionFields) &&
                   java.util.Objects.equals(mExceptionFields, other.mExceptionFields) &&
                   java.util.Objects.equals(mDefaultValues, other.mDefaultValues);
        }

        @Override
        public int hashCode() {
            return java.util.Objects.hash(
                    Containers.class, optionals,
                    _Field.BOOLEAN_LIST, mBooleanList,
                    _Field.BYTE_LIST, mByteList,
                    _Field.SHORT_LIST, mShortList,
                    _Field.INTEGER_LIST, mIntegerList,
                    _Field.LONG_LIST, mLongList,
                    _Field.DOUBLE_LIST, mDoubleList,
                    _Field.STRING_LIST, mStringList,
                    _Field.BINARY_LIST, mBinaryList,
                    _Field.BOOLEAN_SET, mBooleanSet,
                    _Field.BYTE_SET, mByteSet,
                    _Field.SHORT_SET, mShortSet,
                    _Field.INTEGER_SET, mIntegerSet,
                    _Field.LONG_SET, mLongSet,
                    _Field.DOUBLE_SET, mDoubleSet,
                    _Field.STRING_SET, mStringSet,
                    _Field.BINARY_SET, mBinarySet,
                    _Field.BOOLEAN_MAP, mBooleanMap,
                    _Field.BYTE_MAP, mByteMap,
                    _Field.SHORT_MAP, mShortMap,
                    _Field.INTEGER_MAP, mIntegerMap,
                    _Field.LONG_MAP, mLongMap,
                    _Field.DOUBLE_MAP, mDoubleMap,
                    _Field.STRING_MAP, mStringMap,
                    _Field.BINARY_MAP, mBinaryMap,
                    _Field.ENUM_LIST, mEnumList,
                    _Field.ENUM_SET, mEnumSet,
                    _Field.ENUM_MAP, mEnumMap,
                    _Field.MESSAGE_LIST, mMessageList,
                    _Field.MESSAGE_SET, mMessageSet,
                    _Field.MESSAGE_MAP, mMessageMap,
                    _Field.REQUIRED_FIELDS, mRequiredFields,
                    _Field.DEFAULT_FIELDS, mDefaultFields,
                    _Field.OPTIONAL_FIELDS, mOptionalFields,
                    _Field.UNION_FIELDS, mUnionFields,
                    _Field.EXCEPTION_FIELDS, mExceptionFields,
                    _Field.DEFAULT_VALUES, mDefaultValues);
        }

        @Override
        @SuppressWarnings("unchecked")
        public net.morimekta.providence.PMessageBuilder mutator(int key) {
            switch (key) {
                case 51: return mutableRequiredFields();
                case 52: return mutableDefaultFields();
                case 53: return mutableOptionalFields();
                case 54: return mutableUnionFields();
                case 55: return mutableExceptionFields();
                case 56: return mutableDefaultValues();
                default: throw new IllegalArgumentException("Not a message field ID: " + key);
            }
        }

        @Override
        @SuppressWarnings("unchecked")
        public _Builder set(int key, Object value) {
            if (value == null) return clear(key);
            switch (key) {
                case 1: setBooleanList((java.util.List<Boolean>) value); break;
                case 2: setByteList((java.util.List<Byte>) value); break;
                case 3: setShortList((java.util.List<Short>) value); break;
                case 4: setIntegerList((java.util.List<Integer>) value); break;
                case 5: setLongList((java.util.List<Long>) value); break;
                case 6: setDoubleList((java.util.List<Double>) value); break;
                case 7: setStringList((java.util.List<String>) value); break;
                case 8: setBinaryList((java.util.List<net.morimekta.util.Binary>) value); break;
                case 11: setBooleanSet((java.util.Set<Boolean>) value); break;
                case 12: setByteSet((java.util.Set<Byte>) value); break;
                case 13: setShortSet((java.util.Set<Short>) value); break;
                case 14: setIntegerSet((java.util.Set<Integer>) value); break;
                case 15: setLongSet((java.util.Set<Long>) value); break;
                case 16: setDoubleSet((java.util.Set<Double>) value); break;
                case 17: setStringSet((java.util.Set<String>) value); break;
                case 18: setBinarySet((java.util.Set<net.morimekta.util.Binary>) value); break;
                case 21: setBooleanMap((java.util.Map<Boolean,Boolean>) value); break;
                case 22: setByteMap((java.util.Map<Byte,Byte>) value); break;
                case 23: setShortMap((java.util.Map<Short,Short>) value); break;
                case 24: setIntegerMap((java.util.Map<Integer,Integer>) value); break;
                case 25: setLongMap((java.util.Map<Long,Long>) value); break;
                case 26: setDoubleMap((java.util.Map<Double,Double>) value); break;
                case 27: setStringMap((java.util.Map<String,String>) value); break;
                case 28: setBinaryMap((java.util.Map<net.morimekta.util.Binary,net.morimekta.util.Binary>) value); break;
                case 31: setEnumList((java.util.List<net.morimekta.test.providence.Value>) value); break;
                case 32: setEnumSet((java.util.Set<net.morimekta.test.providence.Value>) value); break;
                case 33: setEnumMap((java.util.Map<net.morimekta.test.providence.Value,net.morimekta.test.providence.Value>) value); break;
                case 41: setMessageList((java.util.List<net.morimekta.test.providence.DefaultFields>) value); break;
                case 42: setMessageSet((java.util.Set<net.morimekta.test.providence.DefaultFields>) value); break;
                case 43: setMessageMap((java.util.Map<String,net.morimekta.test.providence.DefaultFields>) value); break;
                case 51: setRequiredFields((net.morimekta.test.providence.RequiredFields) value); break;
                case 52: setDefaultFields((net.morimekta.test.providence.DefaultFields) value); break;
                case 53: setOptionalFields((net.morimekta.test.providence.OptionalFields) value); break;
                case 54: setUnionFields((net.morimekta.test.providence.UnionFields) value); break;
                case 55: setExceptionFields((net.morimekta.test.providence.ExceptionFields) value); break;
                case 56: setDefaultValues((net.morimekta.test.providence.DefaultValues) value); break;
                default: break;
            }
            return this;
        }

        @Override
        public boolean isSet(int key) {
            switch (key) {
                case 1: return optionals.get(0);
                case 2: return optionals.get(1);
                case 3: return optionals.get(2);
                case 4: return optionals.get(3);
                case 5: return optionals.get(4);
                case 6: return optionals.get(5);
                case 7: return optionals.get(6);
                case 8: return optionals.get(7);
                case 11: return optionals.get(8);
                case 12: return optionals.get(9);
                case 13: return optionals.get(10);
                case 14: return optionals.get(11);
                case 15: return optionals.get(12);
                case 16: return optionals.get(13);
                case 17: return optionals.get(14);
                case 18: return optionals.get(15);
                case 21: return optionals.get(16);
                case 22: return optionals.get(17);
                case 23: return optionals.get(18);
                case 24: return optionals.get(19);
                case 25: return optionals.get(20);
                case 26: return optionals.get(21);
                case 27: return optionals.get(22);
                case 28: return optionals.get(23);
                case 31: return optionals.get(24);
                case 32: return optionals.get(25);
                case 33: return optionals.get(26);
                case 41: return optionals.get(27);
                case 42: return optionals.get(28);
                case 43: return optionals.get(29);
                case 51: return optionals.get(30);
                case 52: return optionals.get(31);
                case 53: return optionals.get(32);
                case 54: return optionals.get(33);
                case 55: return optionals.get(34);
                case 56: return optionals.get(35);
                default: break;
            }
            return false;
        }

        @Override
        public boolean isModified(int key) {
            switch (key) {
                case 1: return modified.get(0);
                case 2: return modified.get(1);
                case 3: return modified.get(2);
                case 4: return modified.get(3);
                case 5: return modified.get(4);
                case 6: return modified.get(5);
                case 7: return modified.get(6);
                case 8: return modified.get(7);
                case 11: return modified.get(8);
                case 12: return modified.get(9);
                case 13: return modified.get(10);
                case 14: return modified.get(11);
                case 15: return modified.get(12);
                case 16: return modified.get(13);
                case 17: return modified.get(14);
                case 18: return modified.get(15);
                case 21: return modified.get(16);
                case 22: return modified.get(17);
                case 23: return modified.get(18);
                case 24: return modified.get(19);
                case 25: return modified.get(20);
                case 26: return modified.get(21);
                case 27: return modified.get(22);
                case 28: return modified.get(23);
                case 31: return modified.get(24);
                case 32: return modified.get(25);
                case 33: return modified.get(26);
                case 41: return modified.get(27);
                case 42: return modified.get(28);
                case 43: return modified.get(29);
                case 51: return modified.get(30);
                case 52: return modified.get(31);
                case 53: return modified.get(32);
                case 54: return modified.get(33);
                case 55: return modified.get(34);
                case 56: return modified.get(35);
                default: break;
            }
            return false;
        }

        @Override
        public _Builder addTo(int key, Object value) {
            switch (key) {
                case 1: addToBooleanList((boolean) value); break;
                case 2: addToByteList((byte) value); break;
                case 3: addToShortList((short) value); break;
                case 4: addToIntegerList((int) value); break;
                case 5: addToLongList((long) value); break;
                case 6: addToDoubleList((double) value); break;
                case 7: addToStringList((String) value); break;
                case 8: addToBinaryList((net.morimekta.util.Binary) value); break;
                case 11: addToBooleanSet((boolean) value); break;
                case 12: addToByteSet((byte) value); break;
                case 13: addToShortSet((short) value); break;
                case 14: addToIntegerSet((int) value); break;
                case 15: addToLongSet((long) value); break;
                case 16: addToDoubleSet((double) value); break;
                case 17: addToStringSet((String) value); break;
                case 18: addToBinarySet((net.morimekta.util.Binary) value); break;
                case 31: addToEnumList((net.morimekta.test.providence.Value) value); break;
                case 32: addToEnumSet((net.morimekta.test.providence.Value) value); break;
                case 41: addToMessageList((net.morimekta.test.providence.DefaultFields) value); break;
                case 42: addToMessageSet((net.morimekta.test.providence.DefaultFields) value); break;
                default: break;
            }
            return this;
        }

        @Override
        public _Builder clear(int key) {
            switch (key) {
                case 1: clearBooleanList(); break;
                case 2: clearByteList(); break;
                case 3: clearShortList(); break;
                case 4: clearIntegerList(); break;
                case 5: clearLongList(); break;
                case 6: clearDoubleList(); break;
                case 7: clearStringList(); break;
                case 8: clearBinaryList(); break;
                case 11: clearBooleanSet(); break;
                case 12: clearByteSet(); break;
                case 13: clearShortSet(); break;
                case 14: clearIntegerSet(); break;
                case 15: clearLongSet(); break;
                case 16: clearDoubleSet(); break;
                case 17: clearStringSet(); break;
                case 18: clearBinarySet(); break;
                case 21: clearBooleanMap(); break;
                case 22: clearByteMap(); break;
                case 23: clearShortMap(); break;
                case 24: clearIntegerMap(); break;
                case 25: clearLongMap(); break;
                case 26: clearDoubleMap(); break;
                case 27: clearStringMap(); break;
                case 28: clearBinaryMap(); break;
                case 31: clearEnumList(); break;
                case 32: clearEnumSet(); break;
                case 33: clearEnumMap(); break;
                case 41: clearMessageList(); break;
                case 42: clearMessageSet(); break;
                case 43: clearMessageMap(); break;
                case 51: clearRequiredFields(); break;
                case 52: clearDefaultFields(); break;
                case 53: clearOptionalFields(); break;
                case 54: clearUnionFields(); break;
                case 55: clearExceptionFields(); break;
                case 56: clearDefaultValues(); break;
                default: break;
            }
            return this;
        }

        @Override
        public boolean valid() {
            return true;
        }

        @Override
        public void validate() {
        }

        @Override
        public net.morimekta.providence.descriptor.PStructDescriptor<Containers,_Field> descriptor() {
            return kDescriptor;
        }

        @Override
        public void readBinary(net.morimekta.util.io.BigEndianBinaryReader reader, boolean strict) throws java.io.IOException {
            byte type = reader.expectByte();
            while (type != 0) {
                int field = reader.expectShort();
                switch (field) {
                    case 1: {
                        if (type == 15) {
                            byte t_2 = reader.expectByte();
                            if (t_2 == 2) {
                                final int len_1 = reader.expectUInt32();
                                for (int i_3 = 0; i_3 < len_1; ++i_3) {
                                    Boolean key_4 = reader.expectUInt8() == 1;
                                    mBooleanList.add(key_4);
                                }
                            } else {
                                throw new net.morimekta.providence.serializer.SerializerException("Wrong item type " + net.morimekta.providence.PType.nameForId(t_2) + "(" + t_2 + ") for providence.Containers.booleanList, should be bool(2)");
                            }
                            optionals.set(0);
                        } else {
                            throw new net.morimekta.providence.serializer.SerializerException("Wrong type " + net.morimekta.providence.PType.nameForId(type) + "(" + type + ") for providence.Containers.booleanList, should be message(12)");
                        }
                        break;
                    }
                    case 2: {
                        if (type == 15) {
                            byte t_6 = reader.expectByte();
                            if (t_6 == 3) {
                                final int len_5 = reader.expectUInt32();
                                for (int i_7 = 0; i_7 < len_5; ++i_7) {
                                    Byte key_8 = reader.expectByte();
                                    mByteList.add(key_8);
                                }
                            } else {
                                throw new net.morimekta.providence.serializer.SerializerException("Wrong item type " + net.morimekta.providence.PType.nameForId(t_6) + "(" + t_6 + ") for providence.Containers.byteList, should be byte(3)");
                            }
                            optionals.set(1);
                        } else {
                            throw new net.morimekta.providence.serializer.SerializerException("Wrong type " + net.morimekta.providence.PType.nameForId(type) + "(" + type + ") for providence.Containers.byteList, should be message(12)");
                        }
                        break;
                    }
                    case 3: {
                        if (type == 15) {
                            byte t_10 = reader.expectByte();
                            if (t_10 == 6) {
                                final int len_9 = reader.expectUInt32();
                                for (int i_11 = 0; i_11 < len_9; ++i_11) {
                                    Short key_12 = reader.expectShort();
                                    mShortList.add(key_12);
                                }
                            } else {
                                throw new net.morimekta.providence.serializer.SerializerException("Wrong item type " + net.morimekta.providence.PType.nameForId(t_10) + "(" + t_10 + ") for providence.Containers.shortList, should be i16(6)");
                            }
                            optionals.set(2);
                        } else {
                            throw new net.morimekta.providence.serializer.SerializerException("Wrong type " + net.morimekta.providence.PType.nameForId(type) + "(" + type + ") for providence.Containers.shortList, should be message(12)");
                        }
                        break;
                    }
                    case 4: {
                        if (type == 15) {
                            byte t_14 = reader.expectByte();
                            if (t_14 == 8) {
                                final int len_13 = reader.expectUInt32();
                                for (int i_15 = 0; i_15 < len_13; ++i_15) {
                                    Integer key_16 = reader.expectInt();
                                    mIntegerList.add(key_16);
                                }
                            } else {
                                throw new net.morimekta.providence.serializer.SerializerException("Wrong item type " + net.morimekta.providence.PType.nameForId(t_14) + "(" + t_14 + ") for providence.Containers.integerList, should be i32(8)");
                            }
                            optionals.set(3);
                        } else {
                            throw new net.morimekta.providence.serializer.SerializerException("Wrong type " + net.morimekta.providence.PType.nameForId(type) + "(" + type + ") for providence.Containers.integerList, should be message(12)");
                        }
                        break;
                    }
                    case 5: {
                        if (type == 15) {
                            byte t_18 = reader.expectByte();
                            if (t_18 == 10) {
                                final int len_17 = reader.expectUInt32();
                                for (int i_19 = 0; i_19 < len_17; ++i_19) {
                                    Long key_20 = reader.expectLong();
                                    mLongList.add(key_20);
                                }
                            } else {
                                throw new net.morimekta.providence.serializer.SerializerException("Wrong item type " + net.morimekta.providence.PType.nameForId(t_18) + "(" + t_18 + ") for providence.Containers.longList, should be i64(10)");
                            }
                            optionals.set(4);
                        } else {
                            throw new net.morimekta.providence.serializer.SerializerException("Wrong type " + net.morimekta.providence.PType.nameForId(type) + "(" + type + ") for providence.Containers.longList, should be message(12)");
                        }
                        break;
                    }
                    case 6: {
                        if (type == 15) {
                            byte t_22 = reader.expectByte();
                            if (t_22 == 4) {
                                final int len_21 = reader.expectUInt32();
                                for (int i_23 = 0; i_23 < len_21; ++i_23) {
                                    Double key_24 = reader.expectDouble();
                                    mDoubleList.add(key_24);
                                }
                            } else {
                                throw new net.morimekta.providence.serializer.SerializerException("Wrong item type " + net.morimekta.providence.PType.nameForId(t_22) + "(" + t_22 + ") for providence.Containers.doubleList, should be double(4)");
                            }
                            optionals.set(5);
                        } else {
                            throw new net.morimekta.providence.serializer.SerializerException("Wrong type " + net.morimekta.providence.PType.nameForId(type) + "(" + type + ") for providence.Containers.doubleList, should be message(12)");
                        }
                        break;
                    }
                    case 7: {
                        if (type == 15) {
                            byte t_26 = reader.expectByte();
                            if (t_26 == 11) {
                                final int len_25 = reader.expectUInt32();
                                for (int i_27 = 0; i_27 < len_25; ++i_27) {
                                    int len_29 = reader.expectUInt32();
                                    String key_28 = new String(reader.expectBytes(len_29), java.nio.charset.StandardCharsets.UTF_8);
                                    mStringList.add(key_28);
                                }
                            } else {
                                throw new net.morimekta.providence.serializer.SerializerException("Wrong item type " + net.morimekta.providence.PType.nameForId(t_26) + "(" + t_26 + ") for providence.Containers.stringList, should be string(11)");
                            }
                            optionals.set(6);
                        } else {
                            throw new net.morimekta.providence.serializer.SerializerException("Wrong type " + net.morimekta.providence.PType.nameForId(type) + "(" + type + ") for providence.Containers.stringList, should be message(12)");
                        }
                        break;
                    }
                    case 8: {
                        if (type == 15) {
                            byte t_31 = reader.expectByte();
                            if (t_31 == 11) {
                                final int len_30 = reader.expectUInt32();
                                for (int i_32 = 0; i_32 < len_30; ++i_32) {
                                    int len_34 = reader.expectUInt32();
                                    net.morimekta.util.Binary key_33 = reader.expectBinary(len_34);
                                    mBinaryList.add(key_33);
                                }
                            } else {
                                throw new net.morimekta.providence.serializer.SerializerException("Wrong item type " + net.morimekta.providence.PType.nameForId(t_31) + "(" + t_31 + ") for providence.Containers.binaryList, should be binary(11)");
                            }
                            optionals.set(7);
                        } else {
                            throw new net.morimekta.providence.serializer.SerializerException("Wrong type " + net.morimekta.providence.PType.nameForId(type) + "(" + type + ") for providence.Containers.binaryList, should be message(12)");
                        }
                        break;
                    }
                    case 11: {
                        if (type == 14) {
                            byte t_36 = reader.expectByte();
                            if (t_36 == 2) {
                                final int len_35 = reader.expectUInt32();
                                for (int i_37 = 0; i_37 < len_35; ++i_37) {
                                    Boolean key_38 = reader.expectUInt8() == 1;
                                    mBooleanSet.add(key_38);
                                }
                            } else {
                                throw new net.morimekta.providence.serializer.SerializerException("Wrong item type " + net.morimekta.providence.PType.nameForId(t_36) + "(" + t_36 + ") for providence.Containers.booleanSet, should be bool(2)");
                            }
                            optionals.set(8);
                        } else {
                            throw new net.morimekta.providence.serializer.SerializerException("Wrong type " + net.morimekta.providence.PType.nameForId(type) + "(" + type + ") for providence.Containers.booleanSet, should be message(12)");
                        }
                        break;
                    }
                    case 12: {
                        if (type == 14) {
                            byte t_40 = reader.expectByte();
                            if (t_40 == 3) {
                                final int len_39 = reader.expectUInt32();
                                for (int i_41 = 0; i_41 < len_39; ++i_41) {
                                    Byte key_42 = reader.expectByte();
                                    mByteSet.add(key_42);
                                }
                            } else {
                                throw new net.morimekta.providence.serializer.SerializerException("Wrong item type " + net.morimekta.providence.PType.nameForId(t_40) + "(" + t_40 + ") for providence.Containers.byteSet, should be byte(3)");
                            }
                            optionals.set(9);
                        } else {
                            throw new net.morimekta.providence.serializer.SerializerException("Wrong type " + net.morimekta.providence.PType.nameForId(type) + "(" + type + ") for providence.Containers.byteSet, should be message(12)");
                        }
                        break;
                    }
                    case 13: {
                        if (type == 14) {
                            byte t_44 = reader.expectByte();
                            if (t_44 == 6) {
                                final int len_43 = reader.expectUInt32();
                                for (int i_45 = 0; i_45 < len_43; ++i_45) {
                                    Short key_46 = reader.expectShort();
                                    mShortSet.add(key_46);
                                }
                            } else {
                                throw new net.morimekta.providence.serializer.SerializerException("Wrong item type " + net.morimekta.providence.PType.nameForId(t_44) + "(" + t_44 + ") for providence.Containers.shortSet, should be i16(6)");
                            }
                            optionals.set(10);
                        } else {
                            throw new net.morimekta.providence.serializer.SerializerException("Wrong type " + net.morimekta.providence.PType.nameForId(type) + "(" + type + ") for providence.Containers.shortSet, should be message(12)");
                        }
                        break;
                    }
                    case 14: {
                        if (type == 14) {
                            byte t_48 = reader.expectByte();
                            if (t_48 == 8) {
                                final int len_47 = reader.expectUInt32();
                                for (int i_49 = 0; i_49 < len_47; ++i_49) {
                                    Integer key_50 = reader.expectInt();
                                    mIntegerSet.add(key_50);
                                }
                            } else {
                                throw new net.morimekta.providence.serializer.SerializerException("Wrong item type " + net.morimekta.providence.PType.nameForId(t_48) + "(" + t_48 + ") for providence.Containers.integerSet, should be i32(8)");
                            }
                            optionals.set(11);
                        } else {
                            throw new net.morimekta.providence.serializer.SerializerException("Wrong type " + net.morimekta.providence.PType.nameForId(type) + "(" + type + ") for providence.Containers.integerSet, should be message(12)");
                        }
                        break;
                    }
                    case 15: {
                        if (type == 14) {
                            byte t_52 = reader.expectByte();
                            if (t_52 == 10) {
                                final int len_51 = reader.expectUInt32();
                                for (int i_53 = 0; i_53 < len_51; ++i_53) {
                                    Long key_54 = reader.expectLong();
                                    mLongSet.add(key_54);
                                }
                            } else {
                                throw new net.morimekta.providence.serializer.SerializerException("Wrong item type " + net.morimekta.providence.PType.nameForId(t_52) + "(" + t_52 + ") for providence.Containers.longSet, should be i64(10)");
                            }
                            optionals.set(12);
                        } else {
                            throw new net.morimekta.providence.serializer.SerializerException("Wrong type " + net.morimekta.providence.PType.nameForId(type) + "(" + type + ") for providence.Containers.longSet, should be message(12)");
                        }
                        break;
                    }
                    case 16: {
                        if (type == 14) {
                            byte t_56 = reader.expectByte();
                            if (t_56 == 4) {
                                final int len_55 = reader.expectUInt32();
                                for (int i_57 = 0; i_57 < len_55; ++i_57) {
                                    Double key_58 = reader.expectDouble();
                                    mDoubleSet.add(key_58);
                                }
                            } else {
                                throw new net.morimekta.providence.serializer.SerializerException("Wrong item type " + net.morimekta.providence.PType.nameForId(t_56) + "(" + t_56 + ") for providence.Containers.doubleSet, should be double(4)");
                            }
                            optionals.set(13);
                        } else {
                            throw new net.morimekta.providence.serializer.SerializerException("Wrong type " + net.morimekta.providence.PType.nameForId(type) + "(" + type + ") for providence.Containers.doubleSet, should be message(12)");
                        }
                        break;
                    }
                    case 17: {
                        if (type == 14) {
                            byte t_60 = reader.expectByte();
                            if (t_60 == 11) {
                                final int len_59 = reader.expectUInt32();
                                for (int i_61 = 0; i_61 < len_59; ++i_61) {
                                    int len_63 = reader.expectUInt32();
                                    String key_62 = new String(reader.expectBytes(len_63), java.nio.charset.StandardCharsets.UTF_8);
                                    mStringSet.add(key_62);
                                }
                            } else {
                                throw new net.morimekta.providence.serializer.SerializerException("Wrong item type " + net.morimekta.providence.PType.nameForId(t_60) + "(" + t_60 + ") for providence.Containers.stringSet, should be string(11)");
                            }
                            optionals.set(14);
                        } else {
                            throw new net.morimekta.providence.serializer.SerializerException("Wrong type " + net.morimekta.providence.PType.nameForId(type) + "(" + type + ") for providence.Containers.stringSet, should be message(12)");
                        }
                        break;
                    }
                    case 18: {
                        if (type == 14) {
                            byte t_65 = reader.expectByte();
                            if (t_65 == 11) {
                                final int len_64 = reader.expectUInt32();
                                for (int i_66 = 0; i_66 < len_64; ++i_66) {
                                    int len_68 = reader.expectUInt32();
                                    net.morimekta.util.Binary key_67 = reader.expectBinary(len_68);
                                    mBinarySet.add(key_67);
                                }
                            } else {
                                throw new net.morimekta.providence.serializer.SerializerException("Wrong item type " + net.morimekta.providence.PType.nameForId(t_65) + "(" + t_65 + ") for providence.Containers.binarySet, should be binary(11)");
                            }
                            optionals.set(15);
                        } else {
                            throw new net.morimekta.providence.serializer.SerializerException("Wrong type " + net.morimekta.providence.PType.nameForId(type) + "(" + type + ") for providence.Containers.binarySet, should be message(12)");
                        }
                        break;
                    }
                    case 21: {
                        if (type == 13) {
                            byte t_70 = reader.expectByte();
                            byte t_71 = reader.expectByte();
                            if (t_70 == 2 && t_71 == 2) {
                                final int len_69 = reader.expectUInt32();
                                for (int i_72 = 0; i_72 < len_69; ++i_72) {
                                    Boolean key_73 = reader.expectUInt8() == 1;
                                    Boolean val_74 = reader.expectUInt8() == 1;
                                    mBooleanMap.put(key_73, val_74);
                                }
                            } else {
                                throw new net.morimekta.providence.serializer.SerializerException("Wrong key type " + net.morimekta.providence.PType.nameForId(t_70) + "(" + t_70 + ") or value type " + net.morimekta.providence.PType.nameForId(t_71) + "(" + t_71 + ") for providence.Containers.booleanMap, should be bool(2) and bool(2)");
                            }
                            optionals.set(16);
                        } else {
                            throw new net.morimekta.providence.serializer.SerializerException("Wrong type " + net.morimekta.providence.PType.nameForId(type) + "(" + type + ") for providence.Containers.booleanMap, should be message(12)");
                        }
                        break;
                    }
                    case 22: {
                        if (type == 13) {
                            byte t_76 = reader.expectByte();
                            byte t_77 = reader.expectByte();
                            if (t_76 == 3 && t_77 == 3) {
                                final int len_75 = reader.expectUInt32();
                                for (int i_78 = 0; i_78 < len_75; ++i_78) {
                                    Byte key_79 = reader.expectByte();
                                    Byte val_80 = reader.expectByte();
                                    mByteMap.put(key_79, val_80);
                                }
                            } else {
                                throw new net.morimekta.providence.serializer.SerializerException("Wrong key type " + net.morimekta.providence.PType.nameForId(t_76) + "(" + t_76 + ") or value type " + net.morimekta.providence.PType.nameForId(t_77) + "(" + t_77 + ") for providence.Containers.byteMap, should be byte(3) and byte(3)");
                            }
                            optionals.set(17);
                        } else {
                            throw new net.morimekta.providence.serializer.SerializerException("Wrong type " + net.morimekta.providence.PType.nameForId(type) + "(" + type + ") for providence.Containers.byteMap, should be message(12)");
                        }
                        break;
                    }
                    case 23: {
                        if (type == 13) {
                            byte t_82 = reader.expectByte();
                            byte t_83 = reader.expectByte();
                            if (t_82 == 6 && t_83 == 6) {
                                final int len_81 = reader.expectUInt32();
                                for (int i_84 = 0; i_84 < len_81; ++i_84) {
                                    Short key_85 = reader.expectShort();
                                    Short val_86 = reader.expectShort();
                                    mShortMap.put(key_85, val_86);
                                }
                            } else {
                                throw new net.morimekta.providence.serializer.SerializerException("Wrong key type " + net.morimekta.providence.PType.nameForId(t_82) + "(" + t_82 + ") or value type " + net.morimekta.providence.PType.nameForId(t_83) + "(" + t_83 + ") for providence.Containers.shortMap, should be i16(6) and i16(6)");
                            }
                            optionals.set(18);
                        } else {
                            throw new net.morimekta.providence.serializer.SerializerException("Wrong type " + net.morimekta.providence.PType.nameForId(type) + "(" + type + ") for providence.Containers.shortMap, should be message(12)");
                        }
                        break;
                    }
                    case 24: {
                        if (type == 13) {
                            byte t_88 = reader.expectByte();
                            byte t_89 = reader.expectByte();
                            if (t_88 == 8 && t_89 == 8) {
                                final int len_87 = reader.expectUInt32();
                                for (int i_90 = 0; i_90 < len_87; ++i_90) {
                                    Integer key_91 = reader.expectInt();
                                    Integer val_92 = reader.expectInt();
                                    mIntegerMap.put(key_91, val_92);
                                }
                            } else {
                                throw new net.morimekta.providence.serializer.SerializerException("Wrong key type " + net.morimekta.providence.PType.nameForId(t_88) + "(" + t_88 + ") or value type " + net.morimekta.providence.PType.nameForId(t_89) + "(" + t_89 + ") for providence.Containers.integerMap, should be i32(8) and i32(8)");
                            }
                            optionals.set(19);
                        } else {
                            throw new net.morimekta.providence.serializer.SerializerException("Wrong type " + net.morimekta.providence.PType.nameForId(type) + "(" + type + ") for providence.Containers.integerMap, should be message(12)");
                        }
                        break;
                    }
                    case 25: {
                        if (type == 13) {
                            byte t_94 = reader.expectByte();
                            byte t_95 = reader.expectByte();
                            if (t_94 == 10 && t_95 == 10) {
                                final int len_93 = reader.expectUInt32();
                                for (int i_96 = 0; i_96 < len_93; ++i_96) {
                                    Long key_97 = reader.expectLong();
                                    Long val_98 = reader.expectLong();
                                    mLongMap.put(key_97, val_98);
                                }
                            } else {
                                throw new net.morimekta.providence.serializer.SerializerException("Wrong key type " + net.morimekta.providence.PType.nameForId(t_94) + "(" + t_94 + ") or value type " + net.morimekta.providence.PType.nameForId(t_95) + "(" + t_95 + ") for providence.Containers.longMap, should be i64(10) and i64(10)");
                            }
                            optionals.set(20);
                        } else {
                            throw new net.morimekta.providence.serializer.SerializerException("Wrong type " + net.morimekta.providence.PType.nameForId(type) + "(" + type + ") for providence.Containers.longMap, should be message(12)");
                        }
                        break;
                    }
                    case 26: {
                        if (type == 13) {
                            byte t_100 = reader.expectByte();
                            byte t_101 = reader.expectByte();
                            if (t_100 == 4 && t_101 == 4) {
                                final int len_99 = reader.expectUInt32();
                                for (int i_102 = 0; i_102 < len_99; ++i_102) {
                                    Double key_103 = reader.expectDouble();
                                    Double val_104 = reader.expectDouble();
                                    mDoubleMap.put(key_103, val_104);
                                }
                            } else {
                                throw new net.morimekta.providence.serializer.SerializerException("Wrong key type " + net.morimekta.providence.PType.nameForId(t_100) + "(" + t_100 + ") or value type " + net.morimekta.providence.PType.nameForId(t_101) + "(" + t_101 + ") for providence.Containers.doubleMap, should be double(4) and double(4)");
                            }
                            optionals.set(21);
                        } else {
                            throw new net.morimekta.providence.serializer.SerializerException("Wrong type " + net.morimekta.providence.PType.nameForId(type) + "(" + type + ") for providence.Containers.doubleMap, should be message(12)");
                        }
                        break;
                    }
                    case 27: {
                        if (type == 13) {
                            byte t_106 = reader.expectByte();
                            byte t_107 = reader.expectByte();
                            if (t_106 == 11 && t_107 == 11) {
                                final int len_105 = reader.expectUInt32();
                                for (int i_108 = 0; i_108 < len_105; ++i_108) {
                                    int len_111 = reader.expectUInt32();
                                    String key_109 = new String(reader.expectBytes(len_111), java.nio.charset.StandardCharsets.UTF_8);
                                    int len_112 = reader.expectUInt32();
                                    String val_110 = new String(reader.expectBytes(len_112), java.nio.charset.StandardCharsets.UTF_8);
                                    mStringMap.put(key_109, val_110);
                                }
                            } else {
                                throw new net.morimekta.providence.serializer.SerializerException("Wrong key type " + net.morimekta.providence.PType.nameForId(t_106) + "(" + t_106 + ") or value type " + net.morimekta.providence.PType.nameForId(t_107) + "(" + t_107 + ") for providence.Containers.stringMap, should be string(11) and string(11)");
                            }
                            optionals.set(22);
                        } else {
                            throw new net.morimekta.providence.serializer.SerializerException("Wrong type " + net.morimekta.providence.PType.nameForId(type) + "(" + type + ") for providence.Containers.stringMap, should be message(12)");
                        }
                        break;
                    }
                    case 28: {
                        if (type == 13) {
                            byte t_114 = reader.expectByte();
                            byte t_115 = reader.expectByte();
                            if (t_114 == 11 && t_115 == 11) {
                                final int len_113 = reader.expectUInt32();
                                for (int i_116 = 0; i_116 < len_113; ++i_116) {
                                    int len_119 = reader.expectUInt32();
                                    net.morimekta.util.Binary key_117 = reader.expectBinary(len_119);
                                    int len_120 = reader.expectUInt32();
                                    net.morimekta.util.Binary val_118 = reader.expectBinary(len_120);
                                    mBinaryMap.put(key_117, val_118);
                                }
                            } else {
                                throw new net.morimekta.providence.serializer.SerializerException("Wrong key type " + net.morimekta.providence.PType.nameForId(t_114) + "(" + t_114 + ") or value type " + net.morimekta.providence.PType.nameForId(t_115) + "(" + t_115 + ") for providence.Containers.binaryMap, should be binary(11) and binary(11)");
                            }
                            optionals.set(23);
                        } else {
                            throw new net.morimekta.providence.serializer.SerializerException("Wrong type " + net.morimekta.providence.PType.nameForId(type) + "(" + type + ") for providence.Containers.binaryMap, should be message(12)");
                        }
                        break;
                    }
                    case 31: {
                        if (type == 15) {
                            byte t_122 = reader.expectByte();
                            if (t_122 == 8) {
                                final int len_121 = reader.expectUInt32();
                                for (int i_123 = 0; i_123 < len_121; ++i_123) {
                                    net.morimekta.test.providence.Value key_124 = net.morimekta.test.providence.Value.forValue(reader.expectInt());
                                    mEnumList.add(key_124);
                                }
                            } else {
                                throw new net.morimekta.providence.serializer.SerializerException("Wrong item type " + net.morimekta.providence.PType.nameForId(t_122) + "(" + t_122 + ") for providence.Containers.enumList, should be enum(8)");
                            }
                            optionals.set(24);
                        } else {
                            throw new net.morimekta.providence.serializer.SerializerException("Wrong type " + net.morimekta.providence.PType.nameForId(type) + "(" + type + ") for providence.Containers.enumList, should be message(12)");
                        }
                        break;
                    }
                    case 32: {
                        if (type == 14) {
                            byte t_126 = reader.expectByte();
                            if (t_126 == 8) {
                                final int len_125 = reader.expectUInt32();
                                for (int i_127 = 0; i_127 < len_125; ++i_127) {
                                    net.morimekta.test.providence.Value key_128 = net.morimekta.test.providence.Value.forValue(reader.expectInt());
                                    mEnumSet.add(key_128);
                                }
                            } else {
                                throw new net.morimekta.providence.serializer.SerializerException("Wrong item type " + net.morimekta.providence.PType.nameForId(t_126) + "(" + t_126 + ") for providence.Containers.enumSet, should be enum(8)");
                            }
                            optionals.set(25);
                        } else {
                            throw new net.morimekta.providence.serializer.SerializerException("Wrong type " + net.morimekta.providence.PType.nameForId(type) + "(" + type + ") for providence.Containers.enumSet, should be message(12)");
                        }
                        break;
                    }
                    case 33: {
                        if (type == 13) {
                            byte t_130 = reader.expectByte();
                            byte t_131 = reader.expectByte();
                            if (t_130 == 8 && t_131 == 8) {
                                final int len_129 = reader.expectUInt32();
                                for (int i_132 = 0; i_132 < len_129; ++i_132) {
                                    net.morimekta.test.providence.Value key_133 = net.morimekta.test.providence.Value.forValue(reader.expectInt());
                                    net.morimekta.test.providence.Value val_134 = net.morimekta.test.providence.Value.forValue(reader.expectInt());
                                    mEnumMap.put(key_133, val_134);
                                }
                            } else {
                                throw new net.morimekta.providence.serializer.SerializerException("Wrong key type " + net.morimekta.providence.PType.nameForId(t_130) + "(" + t_130 + ") or value type " + net.morimekta.providence.PType.nameForId(t_131) + "(" + t_131 + ") for providence.Containers.enumMap, should be enum(8) and enum(8)");
                            }
                            optionals.set(26);
                        } else {
                            throw new net.morimekta.providence.serializer.SerializerException("Wrong type " + net.morimekta.providence.PType.nameForId(type) + "(" + type + ") for providence.Containers.enumMap, should be message(12)");
                        }
                        break;
                    }
                    case 41: {
                        if (type == 15) {
                            byte t_136 = reader.expectByte();
                            if (t_136 == 12) {
                                final int len_135 = reader.expectUInt32();
                                for (int i_137 = 0; i_137 < len_135; ++i_137) {
                                    net.morimekta.test.providence.DefaultFields key_138 = net.morimekta.providence.serializer.rw.BinaryFormatUtils.readMessage(reader, net.morimekta.test.providence.DefaultFields.kDescriptor, strict);
                                    mMessageList.add(key_138);
                                }
                            } else {
                                throw new net.morimekta.providence.serializer.SerializerException("Wrong item type " + net.morimekta.providence.PType.nameForId(t_136) + "(" + t_136 + ") for providence.Containers.messageList, should be message(12)");
                            }
                            optionals.set(27);
                        } else {
                            throw new net.morimekta.providence.serializer.SerializerException("Wrong type " + net.morimekta.providence.PType.nameForId(type) + "(" + type + ") for providence.Containers.messageList, should be message(12)");
                        }
                        break;
                    }
                    case 42: {
                        if (type == 14) {
                            byte t_140 = reader.expectByte();
                            if (t_140 == 12) {
                                final int len_139 = reader.expectUInt32();
                                for (int i_141 = 0; i_141 < len_139; ++i_141) {
                                    net.morimekta.test.providence.DefaultFields key_142 = net.morimekta.providence.serializer.rw.BinaryFormatUtils.readMessage(reader, net.morimekta.test.providence.DefaultFields.kDescriptor, strict);
                                    mMessageSet.add(key_142);
                                }
                            } else {
                                throw new net.morimekta.providence.serializer.SerializerException("Wrong item type " + net.morimekta.providence.PType.nameForId(t_140) + "(" + t_140 + ") for providence.Containers.messageSet, should be message(12)");
                            }
                            optionals.set(28);
                        } else {
                            throw new net.morimekta.providence.serializer.SerializerException("Wrong type " + net.morimekta.providence.PType.nameForId(type) + "(" + type + ") for providence.Containers.messageSet, should be message(12)");
                        }
                        break;
                    }
                    case 43: {
                        if (type == 13) {
                            byte t_144 = reader.expectByte();
                            byte t_145 = reader.expectByte();
                            if (t_144 == 11 && t_145 == 12) {
                                final int len_143 = reader.expectUInt32();
                                for (int i_146 = 0; i_146 < len_143; ++i_146) {
                                    int len_149 = reader.expectUInt32();
                                    String key_147 = new String(reader.expectBytes(len_149), java.nio.charset.StandardCharsets.UTF_8);
                                    net.morimekta.test.providence.DefaultFields val_148 = net.morimekta.providence.serializer.rw.BinaryFormatUtils.readMessage(reader, net.morimekta.test.providence.DefaultFields.kDescriptor, strict);
                                    mMessageMap.put(key_147, val_148);
                                }
                            } else {
                                throw new net.morimekta.providence.serializer.SerializerException("Wrong key type " + net.morimekta.providence.PType.nameForId(t_144) + "(" + t_144 + ") or value type " + net.morimekta.providence.PType.nameForId(t_145) + "(" + t_145 + ") for providence.Containers.messageMap, should be string(11) and message(12)");
                            }
                            optionals.set(29);
                        } else {
                            throw new net.morimekta.providence.serializer.SerializerException("Wrong type " + net.morimekta.providence.PType.nameForId(type) + "(" + type + ") for providence.Containers.messageMap, should be message(12)");
                        }
                        break;
                    }
                    case 51: {
                        if (type == 12) {
                            mRequiredFields = net.morimekta.providence.serializer.rw.BinaryFormatUtils.readMessage(reader, net.morimekta.test.providence.RequiredFields.kDescriptor, strict);
                            optionals.set(30);
                        } else {
                            throw new net.morimekta.providence.serializer.SerializerException("Wrong type " + net.morimekta.providence.PType.nameForId(type) + "(" + type + ") for providence.Containers.requiredFields, should be message(12)");
                        }
                        break;
                    }
                    case 52: {
                        if (type == 12) {
                            mDefaultFields = net.morimekta.providence.serializer.rw.BinaryFormatUtils.readMessage(reader, net.morimekta.test.providence.DefaultFields.kDescriptor, strict);
                            optionals.set(31);
                        } else {
                            throw new net.morimekta.providence.serializer.SerializerException("Wrong type " + net.morimekta.providence.PType.nameForId(type) + "(" + type + ") for providence.Containers.defaultFields, should be message(12)");
                        }
                        break;
                    }
                    case 53: {
                        if (type == 12) {
                            mOptionalFields = net.morimekta.providence.serializer.rw.BinaryFormatUtils.readMessage(reader, net.morimekta.test.providence.OptionalFields.kDescriptor, strict);
                            optionals.set(32);
                        } else {
                            throw new net.morimekta.providence.serializer.SerializerException("Wrong type " + net.morimekta.providence.PType.nameForId(type) + "(" + type + ") for providence.Containers.optionalFields, should be message(12)");
                        }
                        break;
                    }
                    case 54: {
                        if (type == 12) {
                            mUnionFields = net.morimekta.providence.serializer.rw.BinaryFormatUtils.readMessage(reader, net.morimekta.test.providence.UnionFields.kDescriptor, strict);
                            optionals.set(33);
                        } else {
                            throw new net.morimekta.providence.serializer.SerializerException("Wrong type " + net.morimekta.providence.PType.nameForId(type) + "(" + type + ") for providence.Containers.unionFields, should be message(12)");
                        }
                        break;
                    }
                    case 55: {
                        if (type == 12) {
                            mExceptionFields = net.morimekta.providence.serializer.rw.BinaryFormatUtils.readMessage(reader, net.morimekta.test.providence.ExceptionFields.kDescriptor, strict);
                            optionals.set(34);
                        } else {
                            throw new net.morimekta.providence.serializer.SerializerException("Wrong type " + net.morimekta.providence.PType.nameForId(type) + "(" + type + ") for providence.Containers.exceptionFields, should be message(12)");
                        }
                        break;
                    }
                    case 56: {
                        if (type == 12) {
                            mDefaultValues = net.morimekta.providence.serializer.rw.BinaryFormatUtils.readMessage(reader, net.morimekta.test.providence.DefaultValues.kDescriptor, strict);
                            optionals.set(35);
                        } else {
                            throw new net.morimekta.providence.serializer.SerializerException("Wrong type " + net.morimekta.providence.PType.nameForId(type) + "(" + type + ") for providence.Containers.defaultValues, should be message(12)");
                        }
                        break;
                    }
                    default: {
                        if (strict) {
                            throw new net.morimekta.providence.serializer.SerializerException("No field with id " + field + " exists in providence.Containers");
                        } else {
                            net.morimekta.providence.serializer.rw.BinaryFormatUtils.readFieldValue(reader, new net.morimekta.providence.serializer.rw.BinaryFormatUtils.FieldInfo(field, type), null, false);
                        }
                        break;
                    }
                }
                type = reader.expectByte();
            }
        }

        @Override
        public Containers build() {
            return new Containers(this);
        }
    }
}
