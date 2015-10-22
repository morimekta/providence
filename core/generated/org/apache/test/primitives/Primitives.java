package org.apache.test.primitives;

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

public class Primitives
        implements TMessage<Primitives>, Serializable, Parcelable {
    private final static boolean kDefaultBl = false;
    private final static byte kDefaultBt = (byte)0;
    private final static short kDefaultSh = (short)0;
    private final static int kDefaultI = 0;
    private final static long kDefaultL = 0l;
    private final static double kDefaultD = 0.0d;

    private final Boolean mBl;
    private final Byte mBt;
    private final Short mSh;
    private final Integer mI;
    private final Long mL;
    private final Double mD;
    private final String mS;
    private final byte[] mBn;
    private final Value mV;

    private Primitives(Builder builder) {
        mBl = builder.mBl;
        mBt = builder.mBt;
        mSh = builder.mSh;
        mI = builder.mI;
        mL = builder.mL;
        mD = builder.mD;
        mS = builder.mS;
        mBn = builder.mBn;
        mV = builder.mV;
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
        return mS;
    }

    public boolean hasBn() {
        return mBn != null;
    }

    public byte[] getBn() {
        return mBn;
    }

    public boolean hasV() {
        return mV != null;
    }

    /** Enums are stored as primitive values. */
    public Value getV() {
        return mV;
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
            default: return null;
        }
    }

    @Override
    public boolean isCompact() {
        return false;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || !(o instanceof Primitives)) return false;
        Primitives other = (Primitives) o;
        return TTypeUtils.equals(mBl, other.mBl) &&
               TTypeUtils.equals(mBt, other.mBt) &&
               TTypeUtils.equals(mSh, other.mSh) &&
               TTypeUtils.equals(mI, other.mI) &&
               TTypeUtils.equals(mL, other.mL) &&
               TTypeUtils.equals(mD, other.mD) &&
               TTypeUtils.equals(mS, other.mS) &&
               TTypeUtils.equals(mBn, other.mBn) &&
               TTypeUtils.equals(mV, other.mV);
    }

    @Override
    public int hashCode() {
        return Primitives.class.hashCode() +
               TTypeUtils.hashCode(mBl) +
               TTypeUtils.hashCode(mBt) +
               TTypeUtils.hashCode(mSh) +
               TTypeUtils.hashCode(mI) +
               TTypeUtils.hashCode(mL) +
               TTypeUtils.hashCode(mD) +
               TTypeUtils.hashCode(mS) +
               TTypeUtils.hashCode(mBn) +
               TTypeUtils.hashCode(mV);
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
        BL(1, false, "bl", TPrimitive.BOOL.provider(), null),
        BT(2, false, "bt", TPrimitive.BYTE.provider(), null),
        SH(3, false, "sh", TPrimitive.I16.provider(), null),
        I(4, false, "i", TPrimitive.I32.provider(), null),
        L(5, false, "l", TPrimitive.I64.provider(), null),
        D(6, false, "d", TPrimitive.DOUBLE.provider(), null),
        S(7, false, "s", TPrimitive.STRING.provider(), null),
        BN(8, false, "bn", TPrimitive.BINARY.provider(), null),
        V(9, false, "v", Value.provider(), null),
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
            builder.append(Primitives.class.getSimpleName())
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
                case 1: return Primitives.Field.BL;
                case 2: return Primitives.Field.BT;
                case 3: return Primitives.Field.SH;
                case 4: return Primitives.Field.I;
                case 5: return Primitives.Field.L;
                case 6: return Primitives.Field.D;
                case 7: return Primitives.Field.S;
                case 8: return Primitives.Field.BN;
                case 9: return Primitives.Field.V;
                default: return null;
            }
        }

        public static Field forName(String name) {
            switch (name) {
                case "bl": return Primitives.Field.BL;
                case "bt": return Primitives.Field.BT;
                case "sh": return Primitives.Field.SH;
                case "i": return Primitives.Field.I;
                case "l": return Primitives.Field.L;
                case "d": return Primitives.Field.D;
                case "s": return Primitives.Field.S;
                case "bn": return Primitives.Field.BN;
                case "v": return Primitives.Field.V;
            }
            return null;
        }
    }

    @Override
    public TStructDescriptor<Primitives> getDescriptor() {
        return sDescriptor;
    }

    public static TStructDescriptor<Primitives> descriptor() {
        return sDescriptor;
    }

    public static final TStructDescriptor<Primitives> sDescriptor;

    private final static class Factory
            extends TMessageBuilderFactory<Primitives> {
        @Override
        public Primitives.Builder builder() {
            return new Primitives.Builder();
        }
    }

    static {
        sDescriptor = new TStructDescriptor<>(null, "primitives", "Primitives", Primitives.Field.values(), new Factory(), false);
    }

    public static TStructDescriptorProvider<Primitives> provider() {
        return new TStructDescriptorProvider<Primitives>() {
            @Override
            public TStructDescriptor<Primitives> descriptor() {
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
        dest.writeInt(0);
    }

    public static final Parcelable.Creator<Primitives> CREATOR = new Parcelable.Creator<Primitives>() {
        @Override
        public Primitives createFromParcel(Parcel source) {
            Primitives.Builder builder = new Primitives.Builder();
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
                        builder.setV(Value.forValue(source.readInt()));
                        break;
                    default: throw new IllegalArgumentException("Unknown field ID: " + field);
                }
            }

            return builder.build();
        }

        @Override
        public Primitives[] newArray(int size) {
            return new Primitives[size];
        }
    };

    @Override
    public Primitives.Builder mutate() {
        return new Primitives.Builder(this);
    }

    public static Primitives.Builder builder() {
        return new Primitives.Builder();
    }

    public static class Builder
            extends TMessageBuilder<Primitives> {
        private Boolean mBl;
        private Byte mBt;
        private Short mSh;
        private Integer mI;
        private Long mL;
        private Double mD;
        private String mS;
        private byte[] mBn;
        private Value mV;

        public Builder() {
        }

        public Builder(Primitives base) {
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
        }

        public Builder setBl(boolean value) {
            mBl = value;
            return this;
        }

        public Builder clearBl() {
            mBl = null;
            return this;
        }

        public Builder setBt(byte value) {
            mBt = value;
            return this;
        }

        public Builder clearBt() {
            mBt = null;
            return this;
        }

        public Builder setSh(short value) {
            mSh = value;
            return this;
        }

        public Builder clearSh() {
            mSh = null;
            return this;
        }

        public Builder setI(int value) {
            mI = value;
            return this;
        }

        public Builder clearI() {
            mI = null;
            return this;
        }

        public Builder setL(long value) {
            mL = value;
            return this;
        }

        public Builder clearL() {
            mL = null;
            return this;
        }

        public Builder setD(double value) {
            mD = value;
            return this;
        }

        public Builder clearD() {
            mD = null;
            return this;
        }

        public Builder setS(String value) {
            mS = value;
            return this;
        }

        public Builder clearS() {
            mS = null;
            return this;
        }

        public Builder setBn(byte[] value) {
            mBn = value;
            return this;
        }

        public Builder clearBn() {
            mBn = null;
            return this;
        }

        /** Enums are stored as primitive values. */
        public Builder setV(Value value) {
            mV = value;
            return this;
        }

        public Builder clearV() {
            mV = null;
            return this;
        }

        @Override
        public Builder set(int key, Object value) {
            switch (key) {
                case 1: setBl((boolean) value); break;
                case 2: setBt((byte) value); break;
                case 3: setSh((short) value); break;
                case 4: setI((int) value); break;
                case 5: setL((long) value); break;
                case 6: setD((double) value); break;
                case 7: setS((String) value); break;
                case 8: setBn((byte[]) value); break;
                case 9: setV((Value) value); break;
            }
            return this;
        }

        @Override
        public boolean isValid() {
            return true;
        }

        @Override
        public Primitives build() {
            return new Primitives(this);
        }
    }
}
