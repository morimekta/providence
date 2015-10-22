package org.apache.test.containers;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import android.os.Parcel;
import android.os.Parcelable;

import org.apache.test.primitives.Primitives;
import org.apache.test.primitives.Value;
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
import org.apache.thrift.j2.descriptor.TSet;
import org.apache.thrift.j2.descriptor.TStructDescriptor;
import org.apache.thrift.j2.descriptor.TStructDescriptorProvider;
import org.apache.thrift.j2.descriptor.TValueProvider;
import org.apache.thrift.j2.util.TTypeUtils;

public class Containers
        implements TMessage<Containers>, Serializable, Parcelable {
    private final List<Boolean> mLbl;
    private final List<Byte> mLbt;
    private final List<Short> mLsh;
    private final List<Integer> mLi;
    private final List<Long> mLl;
    private final List<Double> mLd;
    private final List<String> mLs;
    private final List<byte[]> mLbn;
    private final Set<Boolean> mSbl;
    private final Set<Byte> mSbt;
    private final Set<Short> mSsh;
    private final Set<Integer> mSi;
    private final Set<Long> mSl;
    private final Set<Double> mSd;
    private final Set<String> mSs;
    private final Set<byte[]> mSbn;
    private final Map<Boolean,Boolean> mMbl;
    private final Map<Byte,Byte> mMbt;
    private final Map<Short,Short> mMsh;
    private final Map<Integer,Integer> mMi;
    private final Map<Long,Long> mMl;
    private final Map<Double,Double> mMd;
    private final Map<String,String> mMs;
    private final Map<byte[],byte[]> mMbn;
    private final List<Value> mLv;
    private final Set<Value> mSv;
    private final Map<Value,Value> mMv;
    private final List<Primitives> mLp;
    private final Set<Primitives> mSp;
    private final Map<Integer,Primitives> mMp;

    private Containers(Builder builder) {
        mLbl = Collections.unmodifiableList(new LinkedList<>(builder.mLbl));
        mLbt = Collections.unmodifiableList(new LinkedList<>(builder.mLbt));
        mLsh = Collections.unmodifiableList(new LinkedList<>(builder.mLsh));
        mLi = Collections.unmodifiableList(new LinkedList<>(builder.mLi));
        mLl = Collections.unmodifiableList(new LinkedList<>(builder.mLl));
        mLd = Collections.unmodifiableList(new LinkedList<>(builder.mLd));
        mLs = Collections.unmodifiableList(new LinkedList<>(builder.mLs));
        mLbn = Collections.unmodifiableList(new LinkedList<>(builder.mLbn));
        mSbl = Collections.unmodifiableSet(new LinkedHashSet<>(builder.mSbl));
        mSbt = Collections.unmodifiableSet(new LinkedHashSet<>(builder.mSbt));
        mSsh = Collections.unmodifiableSet(new LinkedHashSet<>(builder.mSsh));
        mSi = Collections.unmodifiableSet(new LinkedHashSet<>(builder.mSi));
        mSl = Collections.unmodifiableSet(new LinkedHashSet<>(builder.mSl));
        mSd = Collections.unmodifiableSet(new LinkedHashSet<>(builder.mSd));
        mSs = Collections.unmodifiableSet(new LinkedHashSet<>(builder.mSs));
        mSbn = Collections.unmodifiableSet(new LinkedHashSet<>(builder.mSbn));
        mMbl = Collections.unmodifiableMap(new LinkedHashMap<>(builder.mMbl));
        mMbt = Collections.unmodifiableMap(new LinkedHashMap<>(builder.mMbt));
        mMsh = Collections.unmodifiableMap(new LinkedHashMap<>(builder.mMsh));
        mMi = Collections.unmodifiableMap(new LinkedHashMap<>(builder.mMi));
        mMl = Collections.unmodifiableMap(new LinkedHashMap<>(builder.mMl));
        mMd = Collections.unmodifiableMap(new LinkedHashMap<>(builder.mMd));
        mMs = Collections.unmodifiableMap(new LinkedHashMap<>(builder.mMs));
        mMbn = Collections.unmodifiableMap(new LinkedHashMap<>(builder.mMbn));
        mLv = Collections.unmodifiableList(new LinkedList<>(builder.mLv));
        mSv = Collections.unmodifiableSet(new LinkedHashSet<>(builder.mSv));
        mMv = Collections.unmodifiableMap(new LinkedHashMap<>(builder.mMv));
        mLp = Collections.unmodifiableList(new LinkedList<>(builder.mLp));
        mSp = Collections.unmodifiableSet(new LinkedHashSet<>(builder.mSp));
        mMp = Collections.unmodifiableMap(new LinkedHashMap<>(builder.mMp));
    }

    public int numLbl() {
        return mLbl.size();
    }

    /** all types as list<x>. */
    public List<Boolean> getLbl() {
        return mLbl;
    }

    public int numLbt() {
        return mLbt.size();
    }

    public List<Byte> getLbt() {
        return mLbt;
    }

    public int numLsh() {
        return mLsh.size();
    }

    public List<Short> getLsh() {
        return mLsh;
    }

    public int numLi() {
        return mLi.size();
    }

    public List<Integer> getLi() {
        return mLi;
    }

    public int numLl() {
        return mLl.size();
    }

    public List<Long> getLl() {
        return mLl;
    }

    public int numLd() {
        return mLd.size();
    }

    public List<Double> getLd() {
        return mLd;
    }

    public int numLs() {
        return mLs.size();
    }

    public List<String> getLs() {
        return mLs;
    }

    public int numLbn() {
        return mLbn.size();
    }

    public List<byte[]> getLbn() {
        return mLbn;
    }

    public int numSbl() {
        return mSbl.size();
    }

    /** all types as set<x>. */
    public Set<Boolean> getSbl() {
        return mSbl;
    }

    public int numSbt() {
        return mSbt.size();
    }

    public Set<Byte> getSbt() {
        return mSbt;
    }

    public int numSsh() {
        return mSsh.size();
    }

    public Set<Short> getSsh() {
        return mSsh;
    }

    public int numSi() {
        return mSi.size();
    }

    public Set<Integer> getSi() {
        return mSi;
    }

    public int numSl() {
        return mSl.size();
    }

    public Set<Long> getSl() {
        return mSl;
    }

    public int numSd() {
        return mSd.size();
    }

    public Set<Double> getSd() {
        return mSd;
    }

    public int numSs() {
        return mSs.size();
    }

    public Set<String> getSs() {
        return mSs;
    }

    public int numSbn() {
        return mSbn.size();
    }

    public Set<byte[]> getSbn() {
        return mSbn;
    }

    public int numMbl() {
        return mMbl.size();
    }

    /** all types as map<x,x>. */
    public Map<Boolean,Boolean> getMbl() {
        return mMbl;
    }

    public int numMbt() {
        return mMbt.size();
    }

    public Map<Byte,Byte> getMbt() {
        return mMbt;
    }

    public int numMsh() {
        return mMsh.size();
    }

    public Map<Short,Short> getMsh() {
        return mMsh;
    }

    public int numMi() {
        return mMi.size();
    }

    public Map<Integer,Integer> getMi() {
        return mMi;
    }

    public int numMl() {
        return mMl.size();
    }

    public Map<Long,Long> getMl() {
        return mMl;
    }

    public int numMd() {
        return mMd.size();
    }

    public Map<Double,Double> getMd() {
        return mMd;
    }

    public int numMs() {
        return mMs.size();
    }

    public Map<String,String> getMs() {
        return mMs;
    }

    public int numMbn() {
        return mMbn.size();
    }

    public Map<byte[],byte[]> getMbn() {
        return mMbn;
    }

    public int numLv() {
        return mLv.size();
    }

    /** Using enum as key and value in containers. */
    public List<Value> getLv() {
        return mLv;
    }

    public int numSv() {
        return mSv.size();
    }

    public Set<Value> getSv() {
        return mSv;
    }

    public int numMv() {
        return mMv.size();
    }

    public Map<Value,Value> getMv() {
        return mMv;
    }

    public int numLp() {
        return mLp.size();
    }

    /** Using struct as value in containers. */
    public List<Primitives> getLp() {
        return mLp;
    }

    public int numSp() {
        return mSp.size();
    }

    public Set<Primitives> getSp() {
        return mSp;
    }

    public int numMp() {
        return mMp.size();
    }

    public Map<Integer,Primitives> getMp() {
        return mMp;
    }

    @Override
    public boolean has(int key) {
        switch(key) {
            case 1: return numLbl() > 0;
            case 2: return numLbt() > 0;
            case 3: return numLsh() > 0;
            case 4: return numLi() > 0;
            case 5: return numLl() > 0;
            case 6: return numLd() > 0;
            case 7: return numLs() > 0;
            case 8: return numLbn() > 0;
            case 11: return numSbl() > 0;
            case 12: return numSbt() > 0;
            case 13: return numSsh() > 0;
            case 14: return numSi() > 0;
            case 15: return numSl() > 0;
            case 16: return numSd() > 0;
            case 17: return numSs() > 0;
            case 18: return numSbn() > 0;
            case 21: return numMbl() > 0;
            case 22: return numMbt() > 0;
            case 23: return numMsh() > 0;
            case 24: return numMi() > 0;
            case 25: return numMl() > 0;
            case 26: return numMd() > 0;
            case 27: return numMs() > 0;
            case 28: return numMbn() > 0;
            case 31: return numLv() > 0;
            case 32: return numSv() > 0;
            case 33: return numMv() > 0;
            case 41: return numLp() > 0;
            case 42: return numSp() > 0;
            case 43: return numMp() > 0;
            default: return false;
        }
    }

    @Override
    public int num(int key) {
        switch(key) {
            case 1: return numLbl();
            case 2: return numLbt();
            case 3: return numLsh();
            case 4: return numLi();
            case 5: return numLl();
            case 6: return numLd();
            case 7: return numLs();
            case 8: return numLbn();
            case 11: return numSbl();
            case 12: return numSbt();
            case 13: return numSsh();
            case 14: return numSi();
            case 15: return numSl();
            case 16: return numSd();
            case 17: return numSs();
            case 18: return numSbn();
            case 21: return numMbl();
            case 22: return numMbt();
            case 23: return numMsh();
            case 24: return numMi();
            case 25: return numMl();
            case 26: return numMd();
            case 27: return numMs();
            case 28: return numMbn();
            case 31: return numLv();
            case 32: return numSv();
            case 33: return numMv();
            case 41: return numLp();
            case 42: return numSp();
            case 43: return numMp();
            default: return 0;
        }
    }

    @Override
    public Object get(int key) {
        switch(key) {
            case 1: return getLbl();
            case 2: return getLbt();
            case 3: return getLsh();
            case 4: return getLi();
            case 5: return getLl();
            case 6: return getLd();
            case 7: return getLs();
            case 8: return getLbn();
            case 11: return getSbl();
            case 12: return getSbt();
            case 13: return getSsh();
            case 14: return getSi();
            case 15: return getSl();
            case 16: return getSd();
            case 17: return getSs();
            case 18: return getSbn();
            case 21: return getMbl();
            case 22: return getMbt();
            case 23: return getMsh();
            case 24: return getMi();
            case 25: return getMl();
            case 26: return getMd();
            case 27: return getMs();
            case 28: return getMbn();
            case 31: return getLv();
            case 32: return getSv();
            case 33: return getMv();
            case 41: return getLp();
            case 42: return getSp();
            case 43: return getMp();
            default: return null;
        }
    }

    @Override
    public boolean isCompact() {
        return false;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || !(o instanceof Containers)) return false;
        Containers other = (Containers) o;
        return TTypeUtils.equals(mLbl, other.mLbl) &&
               TTypeUtils.equals(mLbt, other.mLbt) &&
               TTypeUtils.equals(mLsh, other.mLsh) &&
               TTypeUtils.equals(mLi, other.mLi) &&
               TTypeUtils.equals(mLl, other.mLl) &&
               TTypeUtils.equals(mLd, other.mLd) &&
               TTypeUtils.equals(mLs, other.mLs) &&
               TTypeUtils.equals(mLbn, other.mLbn) &&
               TTypeUtils.equals(mSbl, other.mSbl) &&
               TTypeUtils.equals(mSbt, other.mSbt) &&
               TTypeUtils.equals(mSsh, other.mSsh) &&
               TTypeUtils.equals(mSi, other.mSi) &&
               TTypeUtils.equals(mSl, other.mSl) &&
               TTypeUtils.equals(mSd, other.mSd) &&
               TTypeUtils.equals(mSs, other.mSs) &&
               TTypeUtils.equals(mSbn, other.mSbn) &&
               TTypeUtils.equals(mMbl, other.mMbl) &&
               TTypeUtils.equals(mMbt, other.mMbt) &&
               TTypeUtils.equals(mMsh, other.mMsh) &&
               TTypeUtils.equals(mMi, other.mMi) &&
               TTypeUtils.equals(mMl, other.mMl) &&
               TTypeUtils.equals(mMd, other.mMd) &&
               TTypeUtils.equals(mMs, other.mMs) &&
               TTypeUtils.equals(mMbn, other.mMbn) &&
               TTypeUtils.equals(mLv, other.mLv) &&
               TTypeUtils.equals(mSv, other.mSv) &&
               TTypeUtils.equals(mMv, other.mMv) &&
               TTypeUtils.equals(mLp, other.mLp) &&
               TTypeUtils.equals(mSp, other.mSp) &&
               TTypeUtils.equals(mMp, other.mMp);
    }

    @Override
    public int hashCode() {
        return Containers.class.hashCode() +
               TTypeUtils.hashCode(mLbl) +
               TTypeUtils.hashCode(mLbt) +
               TTypeUtils.hashCode(mLsh) +
               TTypeUtils.hashCode(mLi) +
               TTypeUtils.hashCode(mLl) +
               TTypeUtils.hashCode(mLd) +
               TTypeUtils.hashCode(mLs) +
               TTypeUtils.hashCode(mLbn) +
               TTypeUtils.hashCode(mSbl) +
               TTypeUtils.hashCode(mSbt) +
               TTypeUtils.hashCode(mSsh) +
               TTypeUtils.hashCode(mSi) +
               TTypeUtils.hashCode(mSl) +
               TTypeUtils.hashCode(mSd) +
               TTypeUtils.hashCode(mSs) +
               TTypeUtils.hashCode(mSbn) +
               TTypeUtils.hashCode(mMbl) +
               TTypeUtils.hashCode(mMbt) +
               TTypeUtils.hashCode(mMsh) +
               TTypeUtils.hashCode(mMi) +
               TTypeUtils.hashCode(mMl) +
               TTypeUtils.hashCode(mMd) +
               TTypeUtils.hashCode(mMs) +
               TTypeUtils.hashCode(mMbn) +
               TTypeUtils.hashCode(mLv) +
               TTypeUtils.hashCode(mSv) +
               TTypeUtils.hashCode(mMv) +
               TTypeUtils.hashCode(mLp) +
               TTypeUtils.hashCode(mSp) +
               TTypeUtils.hashCode(mMp);
    }

    @Override
    public String toString() {
        return getDescriptor().getQualifiedName(null) + TTypeUtils.toString(this);
    }

    @Override
    public boolean isValid() {
        return true;
    }

    public enum Field implements TField {
        LBL(1, false, "lbl", TList.provider(TPrimitive.BOOL.provider()), null),
        LBT(2, false, "lbt", TList.provider(TPrimitive.BYTE.provider()), null),
        LSH(3, false, "lsh", TList.provider(TPrimitive.I16.provider()), null),
        LI(4, false, "li", TList.provider(TPrimitive.I32.provider()), null),
        LL(5, false, "ll", TList.provider(TPrimitive.I64.provider()), null),
        LD(6, false, "ld", TList.provider(TPrimitive.DOUBLE.provider()), null),
        LS(7, false, "ls", TList.provider(TPrimitive.STRING.provider()), null),
        LBN(8, false, "lbn", TList.provider(TPrimitive.BINARY.provider()), null),
        SBL(11, false, "sbl", TSet.provider(TPrimitive.BOOL.provider()), null),
        SBT(12, false, "sbt", TSet.provider(TPrimitive.BYTE.provider()), null),
        SSH(13, false, "ssh", TSet.provider(TPrimitive.I16.provider()), null),
        SI(14, false, "si", TSet.provider(TPrimitive.I32.provider()), null),
        SL(15, false, "sl", TSet.provider(TPrimitive.I64.provider()), null),
        SD(16, false, "sd", TSet.provider(TPrimitive.DOUBLE.provider()), null),
        SS(17, false, "ss", TSet.provider(TPrimitive.STRING.provider()), null),
        SBN(18, false, "sbn", TSet.provider(TPrimitive.BINARY.provider()), null),
        MBL(21, false, "mbl", TMap.provider(TPrimitive.BOOL.provider(),TPrimitive.BOOL.provider()), null),
        MBT(22, false, "mbt", TMap.provider(TPrimitive.BYTE.provider(),TPrimitive.BYTE.provider()), null),
        MSH(23, false, "msh", TMap.provider(TPrimitive.I16.provider(),TPrimitive.I16.provider()), null),
        MI(24, false, "mi", TMap.provider(TPrimitive.I32.provider(),TPrimitive.I32.provider()), null),
        ML(25, false, "ml", TMap.provider(TPrimitive.I64.provider(),TPrimitive.I64.provider()), null),
        MD(26, false, "md", TMap.provider(TPrimitive.DOUBLE.provider(),TPrimitive.DOUBLE.provider()), null),
        MS(27, false, "ms", TMap.provider(TPrimitive.STRING.provider(),TPrimitive.STRING.provider()), null),
        MBN(28, false, "mbn", TMap.provider(TPrimitive.BINARY.provider(),TPrimitive.BINARY.provider()), null),
        LV(31, false, "lv", TList.provider(Value.provider()), null),
        SV(32, false, "sv", TSet.provider(Value.provider()), null),
        MV(33, false, "mv", TMap.provider(Value.provider(),Value.provider()), null),
        LP(41, false, "lp", TList.provider(Primitives.provider()), null),
        SP(42, false, "sp", TSet.provider(Primitives.provider()), null),
        MP(43, false, "mp", TMap.provider(TPrimitive.I32.provider(),Primitives.provider()), null),
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
            builder.append(Containers.class.getSimpleName())
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
            for (Field field : values()) {
                if (field.mKey == key) return field;
            }
            return null;
        }

        public static Field forName(String name) {
            for (Field field : values()) {
                if (field.mName.equals(name)) return field;
            }
            return null;
        }
    }

    @Override
    public TStructDescriptor<Containers> getDescriptor() {
        return sDescriptor;
    }

    public static TStructDescriptor<Containers> descriptor() {
        return sDescriptor;
    }

    public static final TStructDescriptor<Containers> sDescriptor;

    private final static class Factory
            extends TMessageBuilderFactory<Containers> {
        @Override
        public Containers.Builder builder() {
            return new Containers.Builder();
        }
    }

    static {
        sDescriptor = new TStructDescriptor<>(null, "containers", "Containers", Containers.Field.values(), new Factory(), false);
    }

    public static TStructDescriptorProvider<Containers> provider() {
        return new TStructDescriptorProvider<Containers>() {
            @Override
            public TStructDescriptor<Containers> descriptor() {
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
        if (numLbl() > 0) {
            dest.writeInt(1);
            dest.writeArray(mLbl.toArray());
        }
        if (numLbt() > 0) {
            dest.writeInt(2);
            dest.writeArray(mLbt.toArray());
        }
        if (numLsh() > 0) {
            dest.writeInt(3);
            dest.writeArray(mLsh.toArray());
        }
        if (numLi() > 0) {
            dest.writeInt(4);
            dest.writeArray(mLi.toArray());
        }
        if (numLl() > 0) {
            dest.writeInt(5);
            dest.writeArray(mLl.toArray());
        }
        if (numLd() > 0) {
            dest.writeInt(6);
            dest.writeArray(mLd.toArray());
        }
        if (numLs() > 0) {
            dest.writeInt(7);
            dest.writeArray(mLs.toArray());
        }
        if (numLbn() > 0) {
            dest.writeInt(8);
            dest.writeInt(mLbn.size());
            for (byte[] item : mLbn) {
                dest.writeInt(item.length);
                dest.writeByteArray(item);
            }
            dest.writeArray(mLbn.toArray());
        }
        if (numSbl() > 0) {
            dest.writeInt(11);
            dest.writeArray(mSbl.toArray());
        }
        if (numSbt() > 0) {
            dest.writeInt(12);
            dest.writeArray(mSbt.toArray());
        }
        if (numSsh() > 0) {
            dest.writeInt(13);
            dest.writeArray(mSsh.toArray());
        }
        if (numSi() > 0) {
            dest.writeInt(14);
            dest.writeArray(mSi.toArray());
        }
        if (numSl() > 0) {
            dest.writeInt(15);
            dest.writeArray(mSl.toArray());
        }
        if (numSd() > 0) {
            dest.writeInt(16);
            dest.writeArray(mSd.toArray());
        }
        if (numSs() > 0) {
            dest.writeInt(17);
            dest.writeArray(mSs.toArray());
        }
        if (numSbn() > 0) {
            dest.writeInt(18);
            dest.writeInt(mSbn.size());
            for (byte[] item : mSbn) {
                dest.writeInt(item.length);
                dest.writeByteArray(item);
            }
            dest.writeArray(mSbn.toArray());
        }
        if (numMbl() > 0) {
            dest.writeInt(21);
            dest.writeInt(mMbl.size());
            dest.writeArray(mMbl.keySet().toArray(new Boolean[mMbl.size()]));
            dest.writeArray(mMbl.values().toArray(new Boolean[mMbl.size()]));
        }
        if (numMbt() > 0) {
            dest.writeInt(22);
            dest.writeInt(mMbt.size());
            dest.writeArray(mMbt.keySet().toArray(new Byte[mMbt.size()]));
            dest.writeArray(mMbt.values().toArray(new Byte[mMbt.size()]));
        }
        if (numMsh() > 0) {
            dest.writeInt(23);
            dest.writeInt(mMsh.size());
            dest.writeArray(mMsh.keySet().toArray(new Short[mMsh.size()]));
            dest.writeArray(mMsh.values().toArray(new Short[mMsh.size()]));
        }
        if (numMi() > 0) {
            dest.writeInt(24);
            dest.writeInt(mMi.size());
            dest.writeArray(mMi.keySet().toArray(new Integer[mMi.size()]));
            dest.writeArray(mMi.values().toArray(new Integer[mMi.size()]));
        }
        if (numMl() > 0) {
            dest.writeInt(25);
            dest.writeInt(mMl.size());
            dest.writeArray(mMl.keySet().toArray(new Long[mMl.size()]));
            dest.writeArray(mMl.values().toArray(new Long[mMl.size()]));
        }
        if (numMd() > 0) {
            dest.writeInt(26);
            dest.writeInt(mMd.size());
            dest.writeArray(mMd.keySet().toArray(new Double[mMd.size()]));
            dest.writeArray(mMd.values().toArray(new Double[mMd.size()]));
        }
        if (numMs() > 0) {
            dest.writeInt(27);
            dest.writeInt(mMs.size());
            dest.writeArray(mMs.keySet().toArray(new String[mMs.size()]));
            dest.writeArray(mMs.values().toArray(new String[mMs.size()]));
        }
        if (numMbn() > 0) {
            dest.writeInt(28);
            dest.writeInt(mMbn.size());
            for (byte[] item : mMbn.keySet()) {
                dest.writeInt(item.length);
                dest.writeByteArray(item);
            }
            for (byte[] item : mMbn.values()) {
                dest.writeInt(item.length);
                dest.writeByteArray(item);
            }
        }
        if (numLv() > 0) {
            dest.writeInt(31);
            dest.writeArray(mLv.toArray());
        }
        if (numSv() > 0) {
            dest.writeInt(32);
            dest.writeArray(mSv.toArray());
        }
        if (numMv() > 0) {
            dest.writeInt(33);
            dest.writeInt(mMv.size());
            dest.writeArray(mMv.keySet().toArray(new Value[mMv.size()]));
            dest.writeArray(mMv.values().toArray(new Value[mMv.size()]));
        }
        if (numLp() > 0) {
            dest.writeInt(41);
            dest.writeParcelableArray(mLp.toArray(new Primitives[mLp.size()]), mLp.size());
        }
        if (numSp() > 0) {
            dest.writeInt(42);
            dest.writeParcelableArray(mSp.toArray(new Primitives[mSp.size()]), mSp.size());
        }
        if (numMp() > 0) {
            dest.writeInt(43);
            dest.writeInt(mMp.size());
            dest.writeArray(mMp.keySet().toArray(new Integer[mMp.size()]));
            Primitives[] values = mMp.values().toArray(new Primitives[mMp.size()]);
            dest.writeParcelableArray(values, values.length);
        }
        dest.writeInt(0);
    }

    public static final Parcelable.Creator<Containers> CREATOR = new Parcelable.Creator<Containers>() {
        @Override
        public Containers createFromParcel(Parcel source) {
            Containers.Builder builder = new Containers.Builder();
            loop: while (source.dataAvail() > 0) {
                int field = source.readInt();
                switch (field) {
                    case 0: break loop;
                    case 1:
                        builder.addToLbl((Boolean[]) source.readArray(Boolean.class.getClassLoader()));
                        break;
                    case 2:
                        builder.addToLbt((Byte[]) source.readArray(Byte.class.getClassLoader()));
                        break;
                    case 3:
                        builder.addToLsh((Short[]) source.readArray(Short.class.getClassLoader()));
                        break;
                    case 4:
                        builder.addToLi((Integer[]) source.readArray(Integer.class.getClassLoader()));
                        break;
                    case 5:
                        builder.addToLl((Long[]) source.readArray(Long.class.getClassLoader()));
                        break;
                    case 6:
                        builder.addToLd((Double[]) source.readArray(Double.class.getClassLoader()));
                        break;
                    case 7:
                        builder.addToLs((String[]) source.readArray(String.class.getClassLoader()));
                        break;
                    case 8: {
                            int len = source.readInt();
                            for (int i = 0; i < len; ++i) {
                                int bl = source.readInt();
                                byte[] bytes = new byte[bl];
                                source.readByteArray(bytes);
                                builder.addToLbn(bytes);
                            }
                        }
                        break;
                    case 11:
                        builder.addToSbl((Boolean[]) source.readArray(Boolean.class.getClassLoader()));
                        break;
                    case 12:
                        builder.addToSbt((Byte[]) source.readArray(Byte.class.getClassLoader()));
                        break;
                    case 13:
                        builder.addToSsh((Short[]) source.readArray(Short.class.getClassLoader()));
                        break;
                    case 14:
                        builder.addToSi((Integer[]) source.readArray(Integer.class.getClassLoader()));
                        break;
                    case 15:
                        builder.addToSl((Long[]) source.readArray(Long.class.getClassLoader()));
                        break;
                    case 16:
                        builder.addToSd((Double[]) source.readArray(Double.class.getClassLoader()));
                        break;
                    case 17:
                        builder.addToSs((String[]) source.readArray(String.class.getClassLoader()));
                        break;
                    case 18: {
                            int len = source.readInt();
                            for (int i = 0; i < len; ++i) {
                                int bl = source.readInt();
                                byte[] bytes = new byte[bl];
                                source.readByteArray(bytes);
                                builder.addToSbn(bytes);
                            }
                        }
                        break;
                    case 21: {
                            int len = source.readInt();
                            Boolean[] keys = (Boolean[]) source.readArray(Boolean.class.getClassLoader());
                            Boolean[] values = (Boolean[]) source.readArray(Boolean.class.getClassLoader());
                            for (int i = 0; i < len; ++i) {
                                builder.addToMbl(keys[i], values[i]);
                            }
                        }
                        break;
                    case 22: {
                            int len = source.readInt();
                            Byte[] keys = (Byte[]) source.readArray(Byte.class.getClassLoader());
                            Byte[] values = (Byte[]) source.readArray(Byte.class.getClassLoader());
                            for (int i = 0; i < len; ++i) {
                                builder.addToMbt(keys[i], values[i]);
                            }
                        }
                        break;
                    case 23: {
                            int len = source.readInt();
                            Short[] keys = (Short[]) source.readArray(Short.class.getClassLoader());
                            Short[] values = (Short[]) source.readArray(Short.class.getClassLoader());
                            for (int i = 0; i < len; ++i) {
                                builder.addToMsh(keys[i], values[i]);
                            }
                        }
                        break;
                    case 24: {
                            int len = source.readInt();
                            Integer[] keys = (Integer[]) source.readArray(Integer.class.getClassLoader());
                            Integer[] values = (Integer[]) source.readArray(Integer.class.getClassLoader());
                            for (int i = 0; i < len; ++i) {
                                builder.addToMi(keys[i], values[i]);
                            }
                        }
                        break;
                    case 25: {
                            int len = source.readInt();
                            Long[] keys = (Long[]) source.readArray(Long.class.getClassLoader());
                            Long[] values = (Long[]) source.readArray(Long.class.getClassLoader());
                            for (int i = 0; i < len; ++i) {
                                builder.addToMl(keys[i], values[i]);
                            }
                        }
                        break;
                    case 26: {
                            int len = source.readInt();
                            Double[] keys = (Double[]) source.readArray(Double.class.getClassLoader());
                            Double[] values = (Double[]) source.readArray(Double.class.getClassLoader());
                            for (int i = 0; i < len; ++i) {
                                builder.addToMd(keys[i], values[i]);
                            }
                        }
                        break;
                    case 27: {
                            int len = source.readInt();
                            String[] keys = (String[]) source.readArray(String.class.getClassLoader());
                            String[] values = (String[]) source.readArray(String.class.getClassLoader());
                            for (int i = 0; i < len; ++i) {
                                builder.addToMs(keys[i], values[i]);
                            }
                        }
                        break;
                    case 28: {
                            int len = source.readInt();
                            byte[][] keys = new byte[len][];
                            for (int i = 0; i < len; ++i) {
                                keys[i] = new byte[source.readInt()];
                                source.readByteArray(keys[i]);
                            }
                            byte[][] values = new byte[len][];
                            for (int i = 0; i < len; ++i) {
                                values[i] = new byte[source.readInt()];
                                source.readByteArray(values[i]);
                            }
                            for (int i = 0; i < len; ++i) {
                                builder.addToMbn(keys[i], values[i]);
                            }
                        }
                        break;
                    case 31:
                        builder.addToLv((Value[]) source.readArray(Value.class.getClassLoader()));
                        break;
                    case 32:
                        builder.addToSv((Value[]) source.readArray(Value.class.getClassLoader()));
                        break;
                    case 33: {
                            int len = source.readInt();
                            Value[] keys = (Value[]) source.readArray(Value.class.getClassLoader());
                            Value[] values = (Value[]) source.readArray(Value.class.getClassLoader());
                            for (int i = 0; i < len; ++i) {
                                builder.addToMv(keys[i], values[i]);
                            }
                        }
                        break;
                    case 41:
                        builder.addToLp((Primitives[]) source.readParcelableArray(Primitives.class.getClassLoader()));
                        break;
                    case 42:
                        builder.addToSp((Primitives[]) source.readParcelableArray(Primitives.class.getClassLoader()));
                        break;
                    case 43: {
                            int len = source.readInt();
                            Integer[] keys = (Integer[]) source.readArray(Integer.class.getClassLoader());
                            Primitives[] values = (Primitives[]) source.readParcelableArray(Primitives.class.getClassLoader());
                            for (int i = 0; i < len; ++i) {
                                builder.addToMp(keys[i], values[i]);
                            }
                        }
                        break;
                    default: throw new IllegalArgumentException("Unknown field ID: " + field);
                }
            }

            return builder.build();
        }

        @Override
        public Containers[] newArray(int size) {
            return new Containers[size];
        }
    };

    @Override
    public Containers.Builder mutate() {
        return new Containers.Builder(this);
    }

    public static Containers.Builder builder() {
        return new Containers.Builder();
    }

    public static class Builder
            extends TMessageBuilder<Containers> {
        private List<Boolean> mLbl;
        private List<Byte> mLbt;
        private List<Short> mLsh;
        private List<Integer> mLi;
        private List<Long> mLl;
        private List<Double> mLd;
        private List<String> mLs;
        private List<byte[]> mLbn;
        private Set<Boolean> mSbl;
        private Set<Byte> mSbt;
        private Set<Short> mSsh;
        private Set<Integer> mSi;
        private Set<Long> mSl;
        private Set<Double> mSd;
        private Set<String> mSs;
        private Set<byte[]> mSbn;
        private Map<Boolean,Boolean> mMbl;
        private Map<Byte,Byte> mMbt;
        private Map<Short,Short> mMsh;
        private Map<Integer,Integer> mMi;
        private Map<Long,Long> mMl;
        private Map<Double,Double> mMd;
        private Map<String,String> mMs;
        private Map<byte[],byte[]> mMbn;
        private List<Value> mLv;
        private Set<Value> mSv;
        private Map<Value,Value> mMv;
        private List<Primitives> mLp;
        private Set<Primitives> mSp;
        private Map<Integer,Primitives> mMp;

        public Builder() {
            mLbl = new LinkedList<>();
            mLbt = new LinkedList<>();
            mLsh = new LinkedList<>();
            mLi = new LinkedList<>();
            mLl = new LinkedList<>();
            mLd = new LinkedList<>();
            mLs = new LinkedList<>();
            mLbn = new LinkedList<>();
            mSbl = new LinkedHashSet<>();
            mSbt = new LinkedHashSet<>();
            mSsh = new LinkedHashSet<>();
            mSi = new LinkedHashSet<>();
            mSl = new LinkedHashSet<>();
            mSd = new LinkedHashSet<>();
            mSs = new LinkedHashSet<>();
            mSbn = new LinkedHashSet<>();
            mMbl = new LinkedHashMap<>();
            mMbt = new LinkedHashMap<>();
            mMsh = new LinkedHashMap<>();
            mMi = new LinkedHashMap<>();
            mMl = new LinkedHashMap<>();
            mMd = new LinkedHashMap<>();
            mMs = new LinkedHashMap<>();
            mMbn = new LinkedHashMap<>();
            mLv = new LinkedList<>();
            mSv = new LinkedHashSet<>();
            mMv = new LinkedHashMap<>();
            mLp = new LinkedList<>();
            mSp = new LinkedHashSet<>();
            mMp = new LinkedHashMap<>();
        }

        public Builder(Containers base) {
            this();

            mLbl.addAll(base.mLbl);
            mLbt.addAll(base.mLbt);
            mLsh.addAll(base.mLsh);
            mLi.addAll(base.mLi);
            mLl.addAll(base.mLl);
            mLd.addAll(base.mLd);
            mLs.addAll(base.mLs);
            mLbn.addAll(base.mLbn);
            mSbl.addAll(base.mSbl);
            mSbt.addAll(base.mSbt);
            mSsh.addAll(base.mSsh);
            mSi.addAll(base.mSi);
            mSl.addAll(base.mSl);
            mSd.addAll(base.mSd);
            mSs.addAll(base.mSs);
            mSbn.addAll(base.mSbn);
            mMbl.putAll(base.mMbl);
            mMbt.putAll(base.mMbt);
            mMsh.putAll(base.mMsh);
            mMi.putAll(base.mMi);
            mMl.putAll(base.mMl);
            mMd.putAll(base.mMd);
            mMs.putAll(base.mMs);
            mMbn.putAll(base.mMbn);
            mLv.addAll(base.mLv);
            mSv.addAll(base.mSv);
            mMv.putAll(base.mMv);
            mLp.addAll(base.mLp);
            mSp.addAll(base.mSp);
            mMp.putAll(base.mMp);
        }

        /** all types as list<x>. */
        public Builder setLbl(Collection<Boolean> value) {
            mLbl.clear();
            mLbl.addAll(value);
            return this;
        }

        /** all types as list<x>. */
        public Builder addToLbl(Boolean... values) {
            for (Boolean item : values) {
                mLbl.add(item);
            }
            return this;
        }

        public Builder clearLbl() {
            mLbl.clear();
            return this;
        }

        public Builder setLbt(Collection<Byte> value) {
            mLbt.clear();
            mLbt.addAll(value);
            return this;
        }

        public Builder addToLbt(Byte... values) {
            for (Byte item : values) {
                mLbt.add(item);
            }
            return this;
        }

        public Builder clearLbt() {
            mLbt.clear();
            return this;
        }

        public Builder setLsh(Collection<Short> value) {
            mLsh.clear();
            mLsh.addAll(value);
            return this;
        }

        public Builder addToLsh(Short... values) {
            for (Short item : values) {
                mLsh.add(item);
            }
            return this;
        }

        public Builder clearLsh() {
            mLsh.clear();
            return this;
        }

        public Builder setLi(Collection<Integer> value) {
            mLi.clear();
            mLi.addAll(value);
            return this;
        }

        public Builder addToLi(Integer... values) {
            for (Integer item : values) {
                mLi.add(item);
            }
            return this;
        }

        public Builder clearLi() {
            mLi.clear();
            return this;
        }

        public Builder setLl(Collection<Long> value) {
            mLl.clear();
            mLl.addAll(value);
            return this;
        }

        public Builder addToLl(Long... values) {
            for (Long item : values) {
                mLl.add(item);
            }
            return this;
        }

        public Builder clearLl() {
            mLl.clear();
            return this;
        }

        public Builder setLd(Collection<Double> value) {
            mLd.clear();
            mLd.addAll(value);
            return this;
        }

        public Builder addToLd(Double... values) {
            for (Double item : values) {
                mLd.add(item);
            }
            return this;
        }

        public Builder clearLd() {
            mLd.clear();
            return this;
        }

        public Builder setLs(Collection<String> value) {
            mLs.clear();
            mLs.addAll(value);
            return this;
        }

        public Builder addToLs(String... values) {
            for (String item : values) {
                mLs.add(item);
            }
            return this;
        }

        public Builder clearLs() {
            mLs.clear();
            return this;
        }

        public Builder setLbn(Collection<byte[]> value) {
            mLbn.clear();
            mLbn.addAll(value);
            return this;
        }

        public Builder addToLbn(byte[]... values) {
            for (byte[] item : values) {
                mLbn.add(item);
            }
            return this;
        }

        public Builder clearLbn() {
            mLbn.clear();
            return this;
        }

        /** all types as set<x>. */
        public Builder setSbl(Collection<Boolean> value) {
            mSbl.clear();
            mSbl.addAll(value);
            return this;
        }

        /** all types as set<x>. */
        public Builder addToSbl(Boolean... values) {
            for (Boolean item : values) {
                mSbl.add(item);
            }
            return this;
        }

        public Builder clearSbl() {
            mSbl.clear();
            return this;
        }

        public Builder setSbt(Collection<Byte> value) {
            mSbt.clear();
            mSbt.addAll(value);
            return this;
        }

        public Builder addToSbt(Byte... values) {
            for (Byte item : values) {
                mSbt.add(item);
            }
            return this;
        }

        public Builder clearSbt() {
            mSbt.clear();
            return this;
        }

        public Builder setSsh(Collection<Short> value) {
            mSsh.clear();
            mSsh.addAll(value);
            return this;
        }

        public Builder addToSsh(Short... values) {
            for (Short item : values) {
                mSsh.add(item);
            }
            return this;
        }

        public Builder clearSsh() {
            mSsh.clear();
            return this;
        }

        public Builder setSi(Collection<Integer> value) {
            mSi.clear();
            mSi.addAll(value);
            return this;
        }

        public Builder addToSi(Integer... values) {
            for (Integer item : values) {
                mSi.add(item);
            }
            return this;
        }

        public Builder clearSi() {
            mSi.clear();
            return this;
        }

        public Builder setSl(Collection<Long> value) {
            mSl.clear();
            mSl.addAll(value);
            return this;
        }

        public Builder addToSl(Long... values) {
            for (Long item : values) {
                mSl.add(item);
            }
            return this;
        }

        public Builder clearSl() {
            mSl.clear();
            return this;
        }

        public Builder setSd(Collection<Double> value) {
            mSd.clear();
            mSd.addAll(value);
            return this;
        }

        public Builder addToSd(Double... values) {
            for (Double item : values) {
                mSd.add(item);
            }
            return this;
        }

        public Builder clearSd() {
            mSd.clear();
            return this;
        }

        public Builder setSs(Collection<String> value) {
            mSs.clear();
            mSs.addAll(value);
            return this;
        }

        public Builder addToSs(String... values) {
            for (String item : values) {
                mSs.add(item);
            }
            return this;
        }

        public Builder clearSs() {
            mSs.clear();
            return this;
        }

        public Builder setSbn(Collection<byte[]> value) {
            mSbn.clear();
            mSbn.addAll(value);
            return this;
        }

        public Builder addToSbn(byte[]... values) {
            for (byte[] item : values) {
                mSbn.add(item);
            }
            return this;
        }

        public Builder clearSbn() {
            mSbn.clear();
            return this;
        }

        /** all types as map<x,x>. */
        public Builder setMbl(Map<Boolean,Boolean> value) {
            mMbl.clear();
            mMbl.putAll(value);
            return this;
        }

        /** all types as map<x,x>. */
        public Builder addToMbl(Boolean key, Boolean value) {
            mMbl.put(key, value);
            return this;
        }

        public Builder clearMbl() {
            mMbl.clear();
            return this;
        }

        public Builder setMbt(Map<Byte,Byte> value) {
            mMbt.clear();
            mMbt.putAll(value);
            return this;
        }

        public Builder addToMbt(Byte key, Byte value) {
            mMbt.put(key, value);
            return this;
        }

        public Builder clearMbt() {
            mMbt.clear();
            return this;
        }

        public Builder setMsh(Map<Short,Short> value) {
            mMsh.clear();
            mMsh.putAll(value);
            return this;
        }

        public Builder addToMsh(Short key, Short value) {
            mMsh.put(key, value);
            return this;
        }

        public Builder clearMsh() {
            mMsh.clear();
            return this;
        }

        public Builder setMi(Map<Integer,Integer> value) {
            mMi.clear();
            mMi.putAll(value);
            return this;
        }

        public Builder addToMi(Integer key, Integer value) {
            mMi.put(key, value);
            return this;
        }

        public Builder clearMi() {
            mMi.clear();
            return this;
        }

        public Builder setMl(Map<Long,Long> value) {
            mMl.clear();
            mMl.putAll(value);
            return this;
        }

        public Builder addToMl(Long key, Long value) {
            mMl.put(key, value);
            return this;
        }

        public Builder clearMl() {
            mMl.clear();
            return this;
        }

        public Builder setMd(Map<Double,Double> value) {
            mMd.clear();
            mMd.putAll(value);
            return this;
        }

        public Builder addToMd(Double key, Double value) {
            mMd.put(key, value);
            return this;
        }

        public Builder clearMd() {
            mMd.clear();
            return this;
        }

        public Builder setMs(Map<String,String> value) {
            mMs.clear();
            mMs.putAll(value);
            return this;
        }

        public Builder addToMs(String key, String value) {
            mMs.put(key, value);
            return this;
        }

        public Builder clearMs() {
            mMs.clear();
            return this;
        }

        public Builder setMbn(Map<byte[],byte[]> value) {
            mMbn.clear();
            mMbn.putAll(value);
            return this;
        }

        public Builder addToMbn(byte[] key, byte[] value) {
            mMbn.put(key, value);
            return this;
        }

        public Builder clearMbn() {
            mMbn.clear();
            return this;
        }

        /** Using enum as key and value in containers. */
        public Builder setLv(Collection<Value> value) {
            mLv.clear();
            mLv.addAll(value);
            return this;
        }

        /** Using enum as key and value in containers. */
        public Builder addToLv(Value... values) {
            for (Value item : values) {
                mLv.add(item);
            }
            return this;
        }

        public Builder clearLv() {
            mLv.clear();
            return this;
        }

        public Builder setSv(Collection<Value> value) {
            mSv.clear();
            mSv.addAll(value);
            return this;
        }

        public Builder addToSv(Value... values) {
            for (Value item : values) {
                mSv.add(item);
            }
            return this;
        }

        public Builder clearSv() {
            mSv.clear();
            return this;
        }

        public Builder setMv(Map<Value,Value> value) {
            mMv.clear();
            mMv.putAll(value);
            return this;
        }

        public Builder addToMv(Value key, Value value) {
            mMv.put(key, value);
            return this;
        }

        public Builder clearMv() {
            mMv.clear();
            return this;
        }

        /** Using struct as value in containers. */
        public Builder setLp(Collection<Primitives> value) {
            mLp.clear();
            mLp.addAll(value);
            return this;
        }

        /** Using struct as value in containers. */
        public Builder addToLp(Primitives... values) {
            for (Primitives item : values) {
                mLp.add(item);
            }
            return this;
        }

        public Builder clearLp() {
            mLp.clear();
            return this;
        }

        public Builder setSp(Collection<Primitives> value) {
            mSp.clear();
            mSp.addAll(value);
            return this;
        }

        public Builder addToSp(Primitives... values) {
            for (Primitives item : values) {
                mSp.add(item);
            }
            return this;
        }

        public Builder clearSp() {
            mSp.clear();
            return this;
        }

        public Builder setMp(Map<Integer,Primitives> value) {
            mMp.clear();
            mMp.putAll(value);
            return this;
        }

        public Builder addToMp(Integer key, Primitives value) {
            mMp.put(key, value);
            return this;
        }

        public Builder clearMp() {
            mMp.clear();
            return this;
        }

        @Override
        public Builder set(int key, Object value) {
            switch (key) {
                case 1: setLbl((List<Boolean>) value); break;
                case 2: setLbt((List<Byte>) value); break;
                case 3: setLsh((List<Short>) value); break;
                case 4: setLi((List<Integer>) value); break;
                case 5: setLl((List<Long>) value); break;
                case 6: setLd((List<Double>) value); break;
                case 7: setLs((List<String>) value); break;
                case 8: setLbn((List<byte[]>) value); break;
                case 11: setSbl((Set<Boolean>) value); break;
                case 12: setSbt((Set<Byte>) value); break;
                case 13: setSsh((Set<Short>) value); break;
                case 14: setSi((Set<Integer>) value); break;
                case 15: setSl((Set<Long>) value); break;
                case 16: setSd((Set<Double>) value); break;
                case 17: setSs((Set<String>) value); break;
                case 18: setSbn((Set<byte[]>) value); break;
                case 21: setMbl((Map<Boolean,Boolean>) value); break;
                case 22: setMbt((Map<Byte,Byte>) value); break;
                case 23: setMsh((Map<Short,Short>) value); break;
                case 24: setMi((Map<Integer,Integer>) value); break;
                case 25: setMl((Map<Long,Long>) value); break;
                case 26: setMd((Map<Double,Double>) value); break;
                case 27: setMs((Map<String,String>) value); break;
                case 28: setMbn((Map<byte[],byte[]>) value); break;
                case 31: setLv((List<Value>) value); break;
                case 32: setSv((Set<Value>) value); break;
                case 33: setMv((Map<Value,Value>) value); break;
                case 41: setLp((List<Primitives>) value); break;
                case 42: setSp((Set<Primitives>) value); break;
                case 43: setMp((Map<Integer,Primitives>) value); break;
            }
            return this;
        }

        @Override
        public boolean isValid() {
            return true;
        }

        @Override
        public Containers build() {
            return new Containers(this);
        }
    }
}
