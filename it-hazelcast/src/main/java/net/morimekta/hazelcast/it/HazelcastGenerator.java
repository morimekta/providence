package net.morimekta.hazelcast.it;

import net.morimekta.test.hazelcast.v1.CompactFields;
import net.morimekta.test.hazelcast.v1.Value;
import net.morimekta.util.Binary;

import com.google.common.primitives.Bytes;
import org.jfairy.Fairy;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Helper for generating sources.
 */
public class HazelcastGenerator {

    public static final int INDEX_01 = 0x00000001;
    public static final int INDEX_02 = 0x00000002;
    public static final int INDEX_03 = 0x00000004;
    public static final int INDEX_04 = 0x00000008;
    public static final int INDEX_05 = 0x00000010;
    public static final int INDEX_06 = 0x00000020;
    public static final int INDEX_07 = 0x00000040;
    public static final int INDEX_08 = 0x00000080;
    public static final int INDEX_09 = 0x00000100;
    public static final int INDEX_10 = 0x00000200;
    public static final int INDEX_11 = 0x00000400;
    public static final int INDEX_12 = 0x00000800;
    public static final int INDEX_13 = 0x00001000;
    public static final int INDEX_14 = 0x00002000;

    private final Random random;
    private final Fairy  fairy;
    public final Entities entities;

    public HazelcastGenerator() {
        random = new Random();
        fairy = Fairy.create();
        entities = new Entities();
    }

    public net.morimekta.test.hazelcast.v1.OptionalFields nextOptionalFieldsV1() {
        return nextOptionalFieldsV1(false);
    }

    public net.morimekta.test.hazelcast.v1.OptionalFields nextOptionalFieldsV1(boolean setAll) {
        return nextOptionalFieldsV1(setAll ? 0x0000FFFF : random.nextInt());
    }

    public net.morimekta.test.hazelcast.v1.OptionalFields nextOptionalFieldsV1(int flags) {
        net.morimekta.test.hazelcast.v1.OptionalFields._Builder builder =
                net.morimekta.test.hazelcast.v1.OptionalFields.builder();
        if( 0 < (INDEX_01 & flags) ) {
            builder.setBooleanValue(entities.nextBoolean());
        }
        if( 0 < (INDEX_02 & flags) ) {
            builder.setByteValue(entities.nextByte());
        }
        if( 0 < (INDEX_03 & flags) ) {
            builder.setShortValue(entities.nextShort());
        }
        if( 0 < (INDEX_04 & flags) ) {
            builder.setIntegerValue(entities.nextInt());
        }
        if( 0 < (INDEX_05 & flags) ) {
            builder.setLongValue(entities.nextLong());
        }
        if( 0 < (INDEX_06 & flags) ) {
            builder.setDoubleValue(entities.nextDouble());
        }
        if( 0 < (INDEX_07 & flags) ) {
            builder.setStringValue(entities.nextString());
        }
        if( 0 < (INDEX_08 & flags) ) {
            builder.setBinaryValue(entities.nextBinary());
        }
        if( 0 < (INDEX_09 & flags) ) {
            builder.setEnumValue(entities.nextV1Value());
        }
        if( 0 < (INDEX_10 & flags) ) {
            builder.setCompactValue(entities.nextV1CompactField(flags, INDEX_11, INDEX_12, INDEX_13));
        }
        return builder.build();
    }

    public net.morimekta.test.hazelcast.v2.OptionalFields nextOptionalFieldsV2() {
        return nextOptionalFieldsV2(false);
    }

    public net.morimekta.test.hazelcast.v2.OptionalFields nextOptionalFieldsV2(boolean setAll) {
        return nextOptionalFieldsV2(setAll ? 0x0000FFFF : random.nextInt());
    }

    public net.morimekta.test.hazelcast.v2.OptionalFields nextOptionalFieldsV2(int flags) {
        net.morimekta.test.hazelcast.v2.OptionalFields._Builder builder =
                net.morimekta.test.hazelcast.v2.OptionalFields.builder();
        if( 0 < (INDEX_01 & flags) ) {
            builder.setBooleanValue(entities.nextBoolean());
        }
        if( 0 < (INDEX_02 & flags) ) {
            builder.setByteValue(entities.nextByte());
        }
        if( 0 < (INDEX_03 & flags) ) {
            builder.setShortValue(entities.nextShort());
        }
        if( 0 < (INDEX_04 & flags) ) {
            builder.setIntegerValue(entities.nextInt());
        }
        if( 0 < (INDEX_05 & flags) ) {
            builder.setLongValue(entities.nextLong());
        }
        if( 0 < (INDEX_06 & flags) ) {
            builder.setDoubleValue(entities.nextDouble());
        }
        if( 0 < (INDEX_07 & flags) ) {
            builder.setStringValue(entities.nextString());
        }
        if( 0 < (INDEX_08 & flags) ) {
            builder.setBinaryValue(entities.nextBinary());
        }
        if( 0 < (INDEX_09 & flags) ) {
            builder.setEnumValue(entities.nextV2Value());
        }
        if( 0 < (INDEX_10 & flags) ) {
            builder.setCompactValue(entities.nextV2CompactField(flags, INDEX_11, INDEX_12, INDEX_13));
        }
        if( 0 < (INDEX_14 & flags) ) {
            builder.setAnotherStringValue(entities.nextString());
        }
        return builder.build();
    }

    public net.morimekta.test.hazelcast.v3.OptionalFields nextOptionalFieldsV3() {
        return nextOptionalFieldsV3(false);
    }

    public net.morimekta.test.hazelcast.v3.OptionalFields nextOptionalFieldsV3(boolean setAll) {
        return nextOptionalFieldsV3(setAll ? 0x0000FFFF : random.nextInt());
    }

    public net.morimekta.test.hazelcast.v3.OptionalFields nextOptionalFieldsV3(int flags) {
        net.morimekta.test.hazelcast.v3.OptionalFields._Builder builder =
                net.morimekta.test.hazelcast.v3.OptionalFields.builder();
        if( 0 < (INDEX_01 & flags) ) {
            builder.setBooleanValue(entities.nextBoolean());
        }
        if( 0 < (INDEX_02 & flags) ) {
            builder.setByteValue(entities.nextByte());
        }
        if( 0 < (INDEX_03 & flags) ) {
            builder.setShortValue(entities.nextShort());
        }
        if( 0 < (INDEX_04 & flags) ) {
            builder.setIntegerValue(entities.nextInt());
        }
        if( 0 < (INDEX_05 & flags) ) {
            builder.setLongValue(entities.nextLong());
        }
        if( 0 < (INDEX_06 & flags) ) {
            builder.setDoubleValue(entities.nextDouble());
        }
        if( 0 < (INDEX_07 & flags) ) {
            builder.setBinaryValue(entities.nextBinary());
        }
        if( 0 < (INDEX_08 & flags) ) {
            builder.setEnumValue(entities.nextV3Value());
        }
        if( 0 < (INDEX_09 & flags) ) {
            builder.setCompactValue(entities.nextV3CompactField(flags, INDEX_10, INDEX_11, INDEX_12));
        }
        if( 0 < (INDEX_13 & flags) ) {
            builder.setAnotherStringValue(entities.nextString());
        }
        return builder.build();
    }

    public net.morimekta.test.hazelcast.v4.OptionalFields nextOptionalFieldsV4() {
        return nextOptionalFieldsV4(false);
    }

    public net.morimekta.test.hazelcast.v4.OptionalFields nextOptionalFieldsV4(boolean setAll) {
        return nextOptionalFieldsV4(setAll ? 0x0000FFFF : random.nextInt());
    }

    public net.morimekta.test.hazelcast.v4.OptionalFields nextOptionalFieldsV4(int flags) {
        net.morimekta.test.hazelcast.v4.OptionalFields._Builder builder =
                net.morimekta.test.hazelcast.v4.OptionalFields.builder();
        if( 0 < (INDEX_01 & flags) ) {
            builder.setBooleanValue(entities.nextBoolean());
        }
        if( 0 < (INDEX_02 & flags) ) {
            builder.setByteValue(entities.nextByte());
        }
        if( 0 < (INDEX_03 & flags) ) {
            builder.setIntegerValue(entities.nextInt());
        }
        if( 0 < (INDEX_04 & flags) ) {
            builder.setLongValue(entities.nextLong());
        }
        if( 0 < (INDEX_05 & flags) ) {
            builder.setDoubleValue(entities.nextDouble());
        }
        if( 0 < (INDEX_06 & flags) ) {
            builder.setBinaryValue(entities.nextBinary());
        }
        if( 0 < (INDEX_07 & flags) ) {
            builder.setEnumValue(entities.nextV4Value());
        }
        if( 0 < (INDEX_08 & flags) ) {
            builder.setCompactValue(entities.nextV4CompactField(flags, INDEX_09, INDEX_10, INDEX_11));
        }
        if( 0 < (INDEX_12 & flags) ) {
            builder.setAnotherStringValue(entities.nextString());
        }
        if( 0 < (INDEX_13 & flags)) {
            builder.setAnotherShortValue(entities.nextShort());
        }
        return builder.build();
    }

    public net.morimekta.test.hazelcast.v1.OptionalListFields nextOptionalListFieldsV1() {
        return nextOptionalListFieldsV1(false);
    }

    public net.morimekta.test.hazelcast.v1.OptionalListFields nextOptionalListFieldsV1(boolean setAll) {
        return nextOptionalListFieldsV1(setAll ? 0x0000FFFF : random.nextInt());
    }

    public net.morimekta.test.hazelcast.v1.OptionalListFields nextOptionalListFieldsV1(int flags) {
        net.morimekta.test.hazelcast.v1.OptionalListFields._Builder builder =
                net.morimekta.test.hazelcast.v1.OptionalListFields.builder();
        if( 0 < (INDEX_01 & flags) ) {
            builder.setBooleanValues(entities.nextBooleans());
        }
        if( 0 < (INDEX_02 & flags) ) {
            builder.setByteValues(Bytes.asList(entities.nextBytes()));
        }
        if( 0 < (INDEX_03 & flags) ) {
            builder.setShortValues(entities.nextShorts());
        }
        if( 0 < (INDEX_04 & flags) ) {
            builder.setIntegerValue(entities.nextInts());
        }
        if( 0 < (INDEX_05 & flags) ) {
            builder.setLongValue(entities.nextLongs());
        }
        if( 0 < (INDEX_06 & flags) ) {
            builder.setDoubleValue(entities.nextDoubles());
        }
        if( 0 < (INDEX_07 & flags) ) {
            builder.setStringValue(entities.nextStrings());
        }
        if( 0 < (INDEX_08 & flags) ) {
            builder.setBinaryValue(entities.nextBinaries());
        }
        if( 0 < (INDEX_09 & flags) ) {
            builder.setValueValue(entities.nextV1Values());
        }
        if( 0 < (INDEX_10 & flags) ) {
            builder.setCompactValue(entities.nextV1CompactFields(flags, INDEX_09, INDEX_10, INDEX_11));
        }
        return builder.build();
    }

    public net.morimekta.test.hazelcast.v2.OptionalListFields nextOptionalListFieldsV2() {
        return nextOptionalListFieldsV2(false);
    }

    public net.morimekta.test.hazelcast.v2.OptionalListFields nextOptionalListFieldsV2(boolean setAll) {
        return nextOptionalListFieldsV2(setAll ? 0x00000FFF : random.nextInt());
    }

    public net.morimekta.test.hazelcast.v2.OptionalListFields nextOptionalListFieldsV2(int flags) {
        net.morimekta.test.hazelcast.v2.OptionalListFields._Builder builder =
                net.morimekta.test.hazelcast.v2.OptionalListFields.builder();
        if( 0 < (INDEX_01 & flags) ) {
            builder.setBooleanValues(entities.nextBooleans());
        }
        if( 0 < (INDEX_02 & flags) ) {
            builder.setByteValues(Bytes.asList(entities.nextBytes()));
        }
        if( 0 < (INDEX_03 & flags) ) {
            builder.setShortValues(entities.nextShorts());
        }
        if( 0 < (INDEX_04 & flags) ) {
            builder.setIntegerValue(entities.nextInts());
        }
        if( 0 < (INDEX_05 & flags) ) {
            builder.setLongValue(entities.nextLongs());
        }
        if( 0 < (INDEX_06 & flags) ) {
            builder.setDoubleValue(entities.nextDoubles());
        }
        if( 0 < (INDEX_07 & flags) ) {
            builder.setStringValue(entities.nextStrings());
        }
        if( 0 < (INDEX_08 & flags) ) {
            builder.setCompactValue(entities.nextV2CompactFields(flags, INDEX_09, INDEX_10, INDEX_11));
        }
        if( 0 < (INDEX_12 & flags) ) {
            builder.setBinaryValue(entities.nextBinaries());
        }
        if( 0 < (INDEX_13 & flags) ) {
            builder.setValueValue(entities.nextV2Values());
        }
        if( 0 < (INDEX_14 & flags) ) {
            builder.setAnotherStringValues(entities.nextStrings());
        }
        return builder.build();
    }

    public net.morimekta.test.hazelcast.v3.OptionalListFields nextOptionalListFieldsV3() {
        return nextOptionalListFieldsV3(false);
    }

    public net.morimekta.test.hazelcast.v3.OptionalListFields nextOptionalListFieldsV3(boolean setAll) {
        return nextOptionalListFieldsV3(setAll ? 0x0000FFFF : random.nextInt());
    }

    public net.morimekta.test.hazelcast.v3.OptionalListFields nextOptionalListFieldsV3(int flags) {
        net.morimekta.test.hazelcast.v3.OptionalListFields._Builder builder =
                net.morimekta.test.hazelcast.v3.OptionalListFields.builder();
        if( 0 < (INDEX_01 & flags) ) {
            builder.setBooleanValues(entities.nextBooleans());
        }
        if( 0 < (INDEX_02 & flags) ) {
            builder.setByteValues(Bytes.asList(entities.nextBytes()));
        }
        if( 0 < (INDEX_03 & flags) ) {
            builder.setShortValues(entities.nextShorts());
        }
        if( 0 < (INDEX_04 & flags) ) {
            builder.setIntegerValue(entities.nextInts());
        }
        if( 0 < (INDEX_05 & flags) ) {
            builder.setLongValue(entities.nextLongs());
        }
        if( 0 < (INDEX_06 & flags) ) {
            builder.setDoubleValue(entities.nextDoubles());
        }
        if( 0 < (INDEX_07 & flags) ) {
            builder.setCompactValue(entities.nextV3CompactFields(flags, INDEX_08, INDEX_09, INDEX_10));
        }
        if( 0 < (INDEX_11 & flags) ) {
            builder.setBinaryValue(entities.nextBinaries());
        }
        if( 0 < (INDEX_12 & flags) ) {
            builder.setValueValue(entities.nextV3Values());
        }
        if( 0 < (INDEX_13 & flags) ) {
            builder.setAnotherStringValues(entities.nextStrings());
        }
        return builder.build();
    }

    public net.morimekta.test.hazelcast.v4.OptionalListFields nextOptionalListFieldsV4() {
        return nextOptionalListFieldsV4(false);
    }

    public net.morimekta.test.hazelcast.v4.OptionalListFields nextOptionalListFieldsV4(boolean setAll) {
        return nextOptionalListFieldsV4(setAll ? 0x0000FFFF : random.nextInt());
    }

    public net.morimekta.test.hazelcast.v4.OptionalListFields nextOptionalListFieldsV4(int flags) {
        net.morimekta.test.hazelcast.v4.OptionalListFields._Builder builder =
                net.morimekta.test.hazelcast.v4.OptionalListFields.builder();
        if( 0 < (INDEX_01 & flags) ) {
            builder.setBooleanValues(entities.nextBooleans());
        }
        if( 0 < (INDEX_02 & flags) ) {
            builder.setByteValues(Bytes.asList(entities.nextBytes()));
        }
        if( 0 < (INDEX_03 & flags) ) {
            builder.setShortValues(entities.nextShorts());
        }
        if( 0 < (INDEX_04 & flags) ) {
            builder.setLongValue(entities.nextLongs());
        }
        if( 0 < (INDEX_05 & flags) ) {
            builder.setDoubleValue(entities.nextDoubles());
        }
        if( 0 < (INDEX_06 & flags) ) {
            builder.setCompactValue(entities.nextV4CompactFields(flags, INDEX_07, INDEX_08, INDEX_09));
        }
        if( 0 < (INDEX_10 & flags) ) {
            builder.setBinaryValue(entities.nextBinaries());
        }
        if( 0 < (INDEX_11 & flags) ) {
            builder.setValueValue(entities.nextV4Values());
        }
        if( 0 < (INDEX_12 & flags) ) {
            builder.setAnotherStringValues(entities.nextStrings());
        }
        if( 0 < (INDEX_13 & flags) ) {
            builder.setAnotherIntegerValue(entities.nextInts());
        }
        return builder.build();
    }

    public class Entities {

        public Entities() {  }

        public boolean nextBoolean() {
            return random.nextBoolean();
        }

        public List<Boolean> nextBooleans() {
            return Arrays.asList(Stream.generate(() -> nextBoolean())
                                       .limit(random
                                                                .nextInt(Byte.MAX_VALUE) + 1)
                                       .toArray())
                         .stream()
                         .map(t -> (Boolean) t)
                         .collect(Collectors.toList());
        }

        public byte nextByte() {
            return (byte) random
                                            .nextInt(Byte.MAX_VALUE);
        }

        public short nextShort() {
            return (short) random
                                             .nextInt(Short.MAX_VALUE);
        }

        public List<Short> nextShorts() {
            return Arrays.asList(Stream.generate(() -> nextShort())
                                       .limit(random
                                                                .nextInt(Byte.MAX_VALUE) + 1)
                                       .toArray())
                         .stream()
                         .map(t -> (Short) t)
                         .collect(Collectors.toList());
        }

        public int nextInt() {
            return random
                                     .nextInt();
        }

        private List<Integer> nextInts() {
            return Arrays.asList(Stream.generate(() -> nextInt())
                                       .limit(random
                                                                .nextInt(Byte.MAX_VALUE) + 1)
                                       .toArray())
                         .stream()
                         .map(t -> (Integer) t)
                         .collect(Collectors.toList());
        }

        public long nextLong() {
            return random
                                     .nextLong();
        }

        public List<Long> nextLongs() {
            return Arrays.asList(Stream.generate(() -> nextLong())
                                       .limit(random
                                                                .nextInt(Byte.MAX_VALUE) + 1)
                                       .toArray())
                         .stream()
                         .map(t -> (Long) t)
                         .collect(Collectors.toList());
        }

        public double nextDouble() {
            return random
                                     .nextDouble();
        }

        public List<Double> nextDoubles() {
            return Arrays.asList(Stream.generate(() -> nextDouble())
                                       .limit(random
                                                                .nextInt(Byte.MAX_VALUE) + 1)
                                       .toArray())
                         .stream()
                         .map(t -> (Double) t)
                         .collect(Collectors.toList());
        }

        public String nextString() {
            return fairy
                                     .textProducer()
                                     .randomString(random
                                                                     .nextInt(Byte.MAX_VALUE));
        }

        public List<String> nextStrings() {
            return Arrays.asList(Stream.generate(() -> nextString())
                                       .limit(random
                                                                .nextInt(Byte.MAX_VALUE) + 1)
                                       .toArray())
                         .stream()
                         .map(t -> (String) t)
                         .collect(Collectors.toList());
        }

        public Binary nextBinary() {
            return Binary.copy(nextBytes());
        }


        public List<Binary> nextBinaries() {
            return Arrays.asList(Stream.generate(() -> nextBinary())
                                       .limit(random
                                                      .nextInt(Byte.MAX_VALUE) + 1)
                                       .toArray())
                         .stream()
                         .map(t -> (Binary) t)
                         .collect(Collectors.toList());
        }

        public byte[] nextBytes() {
            byte[] result = new byte[random
                                                       .nextInt(Byte.MAX_VALUE)];
            random
                              .nextBytes(result);
            return result;
        }

        public Value nextV1Value() {
            return Arrays.asList(Value.values())
                         .stream()
                         .findAny()
                         .get();
        }

        public List<Value> nextV1Values() {
            return Arrays.asList(Stream.generate(() -> nextV1Value())
                                       .limit(random
                                                      .nextInt(Byte.MAX_VALUE) + 1)
                                       .toArray())
                         .stream()
                         .map(t -> (Value) t)
                         .collect(Collectors.toList());
        }

        public net.morimekta.test.hazelcast.v2.Value nextV2Value() {
            return Arrays.asList(net.morimekta.test.hazelcast.v2.Value.values())
                         .stream()
                         .findAny()
                         .get();
        }

        public List<net.morimekta.test.hazelcast.v2.Value> nextV2Values() {
            return Arrays.asList(Stream.generate(() -> nextV2Value())
                                       .limit(random
                                                      .nextInt(Byte.MAX_VALUE) + 1)
                                       .toArray())
                         .stream()
                         .map(t -> (net.morimekta.test.hazelcast.v2.Value) t)
                         .collect(Collectors.toList());
        }

        public net.morimekta.test.hazelcast.v3.Value nextV3Value() {
            return Arrays.asList(net.morimekta.test.hazelcast.v3.Value.values())
                         .stream()
                         .findAny()
                         .get();
        }

        public List<net.morimekta.test.hazelcast.v3.Value> nextV3Values() {
            return Arrays.asList(Stream.generate(() -> nextV3Value())
                                       .limit(random
                                                      .nextInt(Byte.MAX_VALUE) + 1)
                                       .toArray())
                         .stream()
                         .map(t -> (net.morimekta.test.hazelcast.v3.Value) t)
                         .collect(Collectors.toList());
        }

        public net.morimekta.test.hazelcast.v4.Value nextV4Value() {
            return Arrays.asList(net.morimekta.test.hazelcast.v4.Value.values())
                         .stream()
                         .findAny()
                         .get();
        }

        public List<net.morimekta.test.hazelcast.v4.Value> nextV4Values() {
            return Arrays.asList(Stream.generate(() -> nextV4Value())
                                       .limit(random
                                                      .nextInt(Byte.MAX_VALUE) + 1)
                                       .toArray())
                         .stream()
                         .map(t -> (net.morimekta.test.hazelcast.v4.Value) t)
                         .collect(Collectors.toList());
        }

        public CompactFields nextV1CompactField(int flags, int index1, int index2, int index3) {
            CompactFields._Builder builder = CompactFields.builder();
            if (0 < (index1 & flags)) {
                builder.setName(nextString());
            }
            if (0 < (index2 & flags)) {
                builder.setId(nextInt());
            }
            if (0 < (index3 & flags)) {
                builder.setLabel(nextString());
            }
            return builder.build();
        }

        public List<CompactFields> nextV1CompactFields(int flags, int index1, int index2, int index3) {
            return Arrays.asList(Stream.generate(() -> nextV1CompactField(flags, index1, index2, index3))
                                       .limit(random
                                                                .nextInt(Byte.MAX_VALUE) + 1)
                                       .toArray())
                         .stream()
                         .map(t -> (CompactFields) t)
                         .collect(Collectors.toList());
        }

        public net.morimekta.test.hazelcast.v2.CompactFields nextV2CompactField(int flags,
                                                                                 int index1,
                                                                                 int index2,
                                                                                 int index3) {
            net.morimekta.test.hazelcast.v2.CompactFields._Builder builder = net.morimekta.test.hazelcast.v2.CompactFields.builder();
            if (0 < (index1 & flags)) {
                builder.setName(nextString());
            }
            if (0 < (index2 & flags)) {
                builder.setId(nextInt());
            }
            if (0 < (index3 & flags)) {
                builder.setLabel(nextString());
            }
            return builder.build();
        }

        public List<net.morimekta.test.hazelcast.v2.CompactFields> nextV2CompactFields(int flags,
                                                                                        int index1,
                                                                                        int index2,
                                                                                        int index3) {
            return Arrays.asList(Stream.generate(() -> nextV2CompactField(flags, index1, index2, index3))
                                       .limit(random
                                                                .nextInt(Byte.MAX_VALUE) + 1)
                                       .toArray())
                         .stream()
                         .map(t -> (net.morimekta.test.hazelcast.v2.CompactFields) t)
                         .collect(Collectors.toList());
        }

        public net.morimekta.test.hazelcast.v3.CompactFields nextV3CompactField(int flags,
                                                                                int index1,
                                                                                int index2,
                                                                                int index3) {
            net.morimekta.test.hazelcast.v3.CompactFields._Builder builder = net.morimekta.test.hazelcast.v3.CompactFields.builder();
            if (0 < (index1 & flags)) {
                builder.setName(nextString());
            }
            if (0 < (index2 & flags)) {
                builder.setId(nextInt());
            }
            if (0 < (index3 & flags)) {
                builder.setLabel(nextString());
            }
            return builder.build();
        }

        public List<net.morimekta.test.hazelcast.v3.CompactFields> nextV3CompactFields(int flags,
                                                                                       int index1,
                                                                                       int index2,
                                                                                       int index3) {
            return Arrays.asList(Stream.generate(() -> nextV3CompactField(flags, index1, index2, index3))
                                       .limit(random
                                                      .nextInt(Byte.MAX_VALUE) + 1)
                                       .toArray())
                         .stream()
                         .map(t -> (net.morimekta.test.hazelcast.v3.CompactFields) t)
                         .collect(Collectors.toList());
        }

        public net.morimekta.test.hazelcast.v4.CompactFields nextV4CompactField(int flags,
                                                                                int index1,
                                                                                int index2,
                                                                                int index3) {
            net.morimekta.test.hazelcast.v4.CompactFields._Builder builder = net.morimekta.test.hazelcast.v4.CompactFields.builder();
            if (0 < (index1 & flags)) {
                builder.setName(nextString());
            }
            if (0 < (index2 & flags)) {
                builder.setId(nextInt());
            }
            if (0 < (index3 & flags)) {
                builder.setLabel(nextString());
            }
            return builder.build();
        }

        public List<net.morimekta.test.hazelcast.v4.CompactFields> nextV4CompactFields(int flags,
                                                                                       int index1,
                                                                                       int index2,
                                                                                       int index3) {
            return Arrays.asList(Stream.generate(() -> nextV4CompactField(flags, index1, index2, index3))
                                       .limit(random
                                                      .nextInt(Byte.MAX_VALUE) + 1)
                                       .toArray())
                         .stream()
                         .map(t -> (net.morimekta.test.hazelcast.v4.CompactFields) t)
                         .collect(Collectors.toList());
        }
        
    }
}
