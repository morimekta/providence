package org.apache.test.alltypes;

import java.io.Serializable;

import android.os.Parcel;
import android.os.Parcelable;

import org.apache.thrift.j2.TMessage;
import org.apache.thrift.j2.TMessageBuilder;
import org.apache.thrift.j2.TMessageBuilderFactory;
import org.apache.thrift.j2.TType;
import org.apache.thrift.j2.descriptor.TDefaultValueProvider;
import org.apache.thrift.j2.descriptor.TDescriptor;
import org.apache.thrift.j2.descriptor.TDescriptorProvider;
import org.apache.thrift.j2.descriptor.TField;
import org.apache.thrift.j2.descriptor.TPrimitive;
import org.apache.thrift.j2.descriptor.TStructDescriptor;
import org.apache.thrift.j2.descriptor.TStructDescriptorProvider;
import org.apache.thrift.j2.descriptor.TValueProvider;
import org.apache.thrift.j2.util.TTypeUtils;

public class AllTypes
        implements TMessage<AllTypes>, Serializable, Parcelable {
    private final static boolean kDefaultBl = true;
    private final static byte kDefaultBt = (byte)-125;
    private final static short kDefaultSh = (short)8117;
    private final static int kDefaultI = 1234567890;
    private final static long kDefaultL = 1234567890123456789L;
    private final static double kDefaultD = 2.99792458E8d;
    private final static String kDefaultS = "test\twith escapes\nand unicode.";
    private final static byte[] kDefaultBn = new byte[]{0x74,0x65,0x73,0x74,0x20,0x20,0x20,0x20,0x77,0x69,0x74,0x68,0x20,0x65,0x73,0x63,0x61,0x70,0x65,0x73,0x0d,0x0a,0x61,0x6e,0x64,0x20,0x75,0x6e,0x69,0x63,0x6f,0x64,0x65,0x2e};
    private final static Values kDefaultV = Values.SECOND;

    private final Boolean mBl;
    private final Byte mBt;
    private final Short mSh;
    private final Integer mI;
    private final Long mL;
    private final Double mD;
    private final String mS;
    private final byte[] mBn;
    private final Values mV;
    private final Other mO;
    private final AllTypes mSelf;

    private AllTypes(_Builder builder) {
        mBl = builder.mBl;
        mBt = builder.mBt;
        mSh = builder.mSh;
        mI = builder.mI;
        mL = builder.mL;
        mD = builder.mD;
        mS = builder.mS;
        mBn = builder.mBn;
        mV = builder.mV;
        mO = builder.mO;
        mSelf = builder.mSelf;
    }

    public boolean hasBl() {
        return mBl != null;
    }

    public boolean getBl() {
        return hasBl() ? mBl : kDefaultBl;
    }

    public boolean hasBt() {
        return mBt != null;
    }

    public byte getBt() {
        return hasBt() ? mBt : kDefaultBt;
    }

    public boolean hasSh() {
        return mSh != null;
    }

    public short getSh() {
        return hasSh() ? mSh : kDefaultSh;
    }

    public boolean hasI() {
        return mI != null;
    }

    public int getI() {
        return hasI() ? mI : kDefaultI;
    }

    public boolean hasL() {
        return mL != null;
    }

    public long getL() {
        return hasL() ? mL : kDefaultL;
    }

    public boolean hasD() {
        return mD != null;
    }

    public double getD() {
        return hasD() ? mD : kDefaultD;
    }

    public boolean hasS() {
        return mS != null;
    }

    public String getS() {
        return hasS() ? mS : kDefaultS;
    }

    public boolean hasBn() {
        return mBn != null;
    }

    public byte[] getBn() {
        return hasBn() ? mBn : kDefaultBn;
    }

    public boolean hasV() {
        return mV != null;
    }

    public Values getV() {
        return hasV() ? mV : kDefaultV;
    }

    public boolean hasO() {
        return mO != null;
    }

    public Other getO() {
        return mO;
    }

    public boolean hasSelf() {
        return mSelf != null;
    }

    public AllTypes getSelf() {
        return mSelf;
    }

    @Override
    public boolean has(int key) {
        switch(key) {
            case 1: return hasBl();
            case 2: return hasBt();
            case 3: return hasSh();
            case 4: return hasI();
            case 5: return hasL();
            case 6: return hasD();
            case 7: return hasS();
            case 8: return hasBn();
            case 9: return hasV();
            case 10: return hasO();
            case 11: return hasSelf();
            default: return false;
        }
    }

    @Override
    public int num(int key) {
        switch(key) {
            case 1: return hasBl() ? 1 : 0;
            case 2: return hasBt() ? 1 : 0;
            case 3: return hasSh() ? 1 : 0;
            case 4: return hasI() ? 1 : 0;
            case 5: return hasL() ? 1 : 0;
            case 6: return hasD() ? 1 : 0;
            case 7: return hasS() ? 1 : 0;
            case 8: return hasBn() ? 1 : 0;
            case 9: return hasV() ? 1 : 0;
            case 10: return hasO() ? 1 : 0;
            case 11: return hasSelf() ? 1 : 0;
            default: return 0;
        }
    }

    @Override
    public Object get(int key) {
        switch(key) {
            case 1: return getBl();
            case 2: return getBt();
            case 3: return getSh();
            case 4: return getI();
            case 5: return getL();
            case 6: return getD();
            case 7: return getS();
            case 8: return getBn();
            case 9: return getV();
            case 10: return getO();
            case 11: return getSelf();
            default: return null;
        }
    }

    @Override
    public boolean isCompact() {
        return false;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || !(o instanceof AllTypes)) return false;
        AllTypes other = (AllTypes) o;
        return TTypeUtils.equals(mBl, other.mBl) &&
               TTypeUtils.equals(mBt, other.mBt) &&
               TTypeUtils.equals(mSh, other.mSh) &&
               TTypeUtils.equals(mI, other.mI) &&
               TTypeUtils.equals(mL, other.mL) &&
               TTypeUtils.equals(mD, other.mD) &&
               TTypeUtils.equals(mS, other.mS) &&
               TTypeUtils.equals(mBn, other.mBn) &&
               TTypeUtils.equals(mV, other.mV) &&
               TTypeUtils.equals(mO, other.mO) &&
               TTypeUtils.equals(mSelf, other.mSelf);
    }

    @Override
    public int hashCode() {
        return AllTypes.class.hashCode() +
               TTypeUtils.hashCode(mBl) +
               TTypeUtils.hashCode(mBt) +
               TTypeUtils.hashCode(mSh) +
               TTypeUtils.hashCode(mI) +
               TTypeUtils.hashCode(mL) +
               TTypeUtils.hashCode(mD) +
               TTypeUtils.hashCode(mS) +
               TTypeUtils.hashCode(mBn) +
               TTypeUtils.hashCode(mV) +
               TTypeUtils.hashCode(mO) +
               TTypeUtils.hashCode(mSelf);
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
        BL(1, false, "bl", TPrimitive.BOOL.provider(), new TDefaultValueProvider<>(kDefaultBl)),
        BT(2, false, "bt", TPrimitive.BYTE.provider(), new TDefaultValueProvider<>(kDefaultBt)),
        SH(3, false, "sh", TPrimitive.I16.provider(), new TDefaultValueProvider<>(kDefaultSh)),
        I(4, false, "i", TPrimitive.I32.provider(), new TDefaultValueProvider<>(kDefaultI)),
        L(5, false, "l", TPrimitive.I64.provider(), new TDefaultValueProvider<>(kDefaultL)),
        D(6, false, "d", TPrimitive.DOUBLE.provider(), new TDefaultValueProvider<>(kDefaultD)),
        S(7, false, "s", TPrimitive.STRING.provider(), new TDefaultValueProvider<>(kDefaultS)),
        BN(8, false, "bn", TPrimitive.BINARY.provider(), new TDefaultValueProvider<>(kDefaultBn)),
        V(9, false, "v", Values.provider(), new TDefaultValueProvider<>(kDefaultV)),
        O(10, false, "o", Other.provider(), null),
        SELF(11, false, "self", AllTypes.provider(), null),
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
            builder.append(AllTypes.class.getSimpleName())
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
                case 1: return _Field.BL;
                case 2: return _Field.BT;
                case 3: return _Field.SH;
                case 4: return _Field.I;
                case 5: return _Field.L;
                case 6: return _Field.D;
                case 7: return _Field.S;
                case 8: return _Field.BN;
                case 9: return _Field.V;
                case 10: return _Field.O;
                case 11: return _Field.SELF;
                default: return null;
            }
        }

        public static _Field forName(String name) {
            switch (name) {
                case "bl": return _Field.BL;
                case "bt": return _Field.BT;
                case "sh": return _Field.SH;
                case "i": return _Field.I;
                case "l": return _Field.L;
                case "d": return _Field.D;
                case "s": return _Field.S;
                case "bn": return _Field.BN;
                case "v": return _Field.V;
                case "o": return _Field.O;
                case "self": return _Field.SELF;
            }
            return null;
        }
    }

    @Override
    public TStructDescriptor<AllTypes> getDescriptor() {
        return sDescriptor;
    }

    public static TStructDescriptor<AllTypes> descriptor() {
        return sDescriptor;
    }

    public static final TStructDescriptor<AllTypes> sDescriptor;

    private final static class _Factory
            extends TMessageBuilderFactory<AllTypes> {
        @Override
        public _Builder builder() {
            return new _Builder();
        }
    }

    static {
        sDescriptor = new TStructDescriptor<>(null, "alltypes", "AllTypes", _Field.values(), new _Factory(), false);
    }

    public static TStructDescriptorProvider<AllTypes> provider() {
        return new TStructDescriptorProvider<AllTypes>() {
            @Override
            public TStructDescriptor<AllTypes> descriptor() {
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
        if (hasBl()) {
            dest.writeInt(1);
            dest.writeByte(mBl ? (byte) 1 : (byte) 0);
        }
        if (hasBt()) {
            dest.writeInt(2);
            dest.writeByte(mBt);
        }
        if (hasSh()) {
            dest.writeInt(3);
            dest.writeInt(mSh);
        }
        if (hasI()) {
            dest.writeInt(4);
            dest.writeInt(mI);
        }
        if (hasL()) {
            dest.writeInt(5);
            dest.writeLong(mL);
        }
        if (hasD()) {
            dest.writeInt(6);
            dest.writeDouble(mD);
        }
        if (hasS()) {
            dest.writeInt(7);
            dest.writeString(mS);
        }
        if (hasBn()) {
            dest.writeInt(8);
            dest.writeInt(mBn.length);
            dest.writeByteArray(mBn);
        }
        if (hasV()) {
            dest.writeInt(9);
            dest.writeInt(mV.getValue());
        }
        if (hasO()) {
            dest.writeInt(10);
            dest.writeParcelable(mO, 0);
        }
        if (hasSelf()) {
            dest.writeInt(11);
            dest.writeParcelable(mSelf, 0);
        }
        dest.writeInt(0);
    }

    public static final Parcelable.Creator<AllTypes> CREATOR = new Parcelable.Creator<AllTypes>() {
        @Override
        public AllTypes createFromParcel(Parcel source) {
            _Builder builder = new _Builder();
            loop: while (source.dataAvail() > 0) {
                int field = source.readInt();
                switch (field) {
                    case 0: break loop;
                    case 1:
                        builder.setBl(source.readByte() > 0);
                        break;
                    case 2:
                        builder.setBt(source.readByte());
                        break;
                    case 3:
                        builder.setSh((short)source.readInt());
                        break;
                    case 4:
                        builder.setI(source.readInt());
                        break;
                    case 5:
                        builder.setL(source.readLong());
                        break;
                    case 6:
                        builder.setD(source.readDouble());
                        break;
                    case 7:
                        builder.setS(source.readString());
                        break;
                    case 8: {
                            int len = source.readInt();
                            byte[] bytes = new byte[len];
                            source.readByteArray(bytes);
                            builder.setBn(bytes);
                        }
                        break;
                    case 9:
                        builder.setV(Values.forValue(source.readInt()));
                        break;
                    case 10:
                        builder.setO((Other) source.readParcelable(Other.class.getClassLoader()));
                        break;
                    case 11:
                        builder.setSelf((AllTypes) source.readParcelable(AllTypes.class.getClassLoader()));
                        break;
                    default: throw new IllegalArgumentException("Unknown field ID: " + field);
                }
            }

            return builder.build();
        }

        @Override
        public AllTypes[] newArray(int size) {
            return new AllTypes[size];
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
            extends TMessageBuilder<AllTypes> {
        private Boolean mBl;
        private Byte mBt;
        private Short mSh;
        private Integer mI;
        private Long mL;
        private Double mD;
        private String mS;
        private byte[] mBn;
        private Values mV;
        private Other mO;
        private AllTypes mSelf;

        public _Builder() {
        }

        public _Builder(AllTypes base) {
            this();

            mBl = base.mBl;
            mBt = base.mBt;
            mSh = base.mSh;
            mI = base.mI;
            mL = base.mL;
            mD = base.mD;
            mS = base.mS;
            mBn = base.mBn;
            mV = base.mV;
            mO = base.mO;
            mSelf = base.mSelf;
        }

        public _Builder setBl(boolean value) {
            mBl = value;
            return this;
        }

        public _Builder clearBl() {
            mBl = null;
            return this;
        }

        public _Builder setBt(byte value) {
            mBt = value;
            return this;
        }

        public _Builder clearBt() {
            mBt = null;
            return this;
        }

        public _Builder setSh(short value) {
            mSh = value;
            return this;
        }

        public _Builder clearSh() {
            mSh = null;
            return this;
        }

        public _Builder setI(int value) {
            mI = value;
            return this;
        }

        public _Builder clearI() {
            mI = null;
            return this;
        }

        public _Builder setL(long value) {
            mL = value;
            return this;
        }

        public _Builder clearL() {
            mL = null;
            return this;
        }

        public _Builder setD(double value) {
            mD = value;
            return this;
        }

        public _Builder clearD() {
            mD = null;
            return this;
        }

        public _Builder setS(String value) {
            mS = value;
            return this;
        }

        public _Builder clearS() {
            mS = null;
            return this;
        }

        public _Builder setBn(byte[] value) {
            mBn = value;
            return this;
        }

        public _Builder clearBn() {
            mBn = null;
            return this;
        }

        public _Builder setV(Values value) {
            mV = value;
            return this;
        }

        public _Builder clearV() {
            mV = null;
            return this;
        }

        public _Builder setO(Other value) {
            mO = value;
            return this;
        }

        public _Builder clearO() {
            mO = null;
            return this;
        }

        public _Builder setSelf(AllTypes value) {
            mSelf = value;
            return this;
        }

        public _Builder clearSelf() {
            mSelf = null;
            return this;
        }

        @Override
        public _Builder set(int key, Object value) {
            switch (key) {
                case 1: setBl((boolean) value); break;
                case 2: setBt((byte) value); break;
                case 3: setSh((short) value); break;
                case 4: setI((int) value); break;
                case 5: setL((long) value); break;
                case 6: setD((double) value); break;
                case 7: setS((String) value); break;
                case 8: setBn((byte[]) value); break;
                case 9: setV((Values) value); break;
                case 10: setO((Other) value); break;
                case 11: setSelf((AllTypes) value); break;
            }
            return this;
        }

        @Override
        public boolean isValid() {
            return true;
        }

        @Override
        public AllTypes build() {
            return new AllTypes(this);
        }
    }
}
