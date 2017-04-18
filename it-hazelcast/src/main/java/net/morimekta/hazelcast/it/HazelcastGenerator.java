package net.morimekta.hazelcast.it;

import net.morimekta.util.Binary;

import com.google.common.primitives.Bytes;
import org.jfairy.Fairy;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
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
    public static final int INDEX_15 = 0x00004000;
    public static final int INDEX_16 = 0x00008000;

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

    public net.morimekta.test.hazelcast.v1.OptionalSetFields nextOptionalSetFieldsV1() {
        return nextOptionalSetFieldsV1(false);
    }

    public net.morimekta.test.hazelcast.v1.OptionalSetFields nextOptionalSetFieldsV1(boolean setAll) {
        return nextOptionalSetFieldsV1(setAll ? 0x0000FFFF : random.nextInt());
    }

    public net.morimekta.test.hazelcast.v1.OptionalSetFields nextOptionalSetFieldsV1(int flags) {
        net.morimekta.test.hazelcast.v1.OptionalSetFields._Builder builder =
                net.morimekta.test.hazelcast.v1.OptionalSetFields.builder();
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

    public net.morimekta.test.hazelcast.v2.OptionalSetFields nextOptionalSetFieldsV2() {
        return nextOptionalSetFieldsV2(false);
    }

    public net.morimekta.test.hazelcast.v2.OptionalSetFields nextOptionalSetFieldsV2(boolean setAll) {
        return nextOptionalSetFieldsV2(setAll ? 0x00000FFF : random.nextInt());
    }

    public net.morimekta.test.hazelcast.v2.OptionalSetFields nextOptionalSetFieldsV2(int flags) {
        net.morimekta.test.hazelcast.v2.OptionalSetFields._Builder builder =
                net.morimekta.test.hazelcast.v2.OptionalSetFields.builder();
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

    public net.morimekta.test.hazelcast.v3.OptionalSetFields nextOptionalSetFieldsV3() {
        return nextOptionalSetFieldsV3(false);
    }

    public net.morimekta.test.hazelcast.v3.OptionalSetFields nextOptionalSetFieldsV3(boolean setAll) {
        return nextOptionalSetFieldsV3(setAll ? 0x0000FFFF : random.nextInt());
    }

    public net.morimekta.test.hazelcast.v3.OptionalSetFields nextOptionalSetFieldsV3(int flags) {
        net.morimekta.test.hazelcast.v3.OptionalSetFields._Builder builder =
                net.morimekta.test.hazelcast.v3.OptionalSetFields.builder();
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

    public net.morimekta.test.hazelcast.v4.OptionalSetFields nextOptionalSetFieldsV4() {
        return nextOptionalSetFieldsV4(false);
    }

    public net.morimekta.test.hazelcast.v4.OptionalSetFields nextOptionalSetFieldsV4(boolean setAll) {
        return nextOptionalSetFieldsV4(setAll ? 0x0000FFFF : random.nextInt());
    }

    public net.morimekta.test.hazelcast.v4.OptionalSetFields nextOptionalSetFieldsV4(int flags) {
        net.morimekta.test.hazelcast.v4.OptionalSetFields._Builder builder =
                net.morimekta.test.hazelcast.v4.OptionalSetFields.builder();
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

        public net.morimekta.test.hazelcast.v1.Value nextV1Value() {
            net.morimekta.test.hazelcast.v1.Value[] values = net.morimekta.test.hazelcast.v1.Value.values();
            return values[random.nextInt(values.length)];
        }

        public List<net.morimekta.test.hazelcast.v1.Value> nextV1Values() {
            return Arrays.asList(Stream.generate(() -> nextV1Value())
                                       .limit(random
                                                      .nextInt(Byte.MAX_VALUE) + 1)
                                       .toArray())
                         .stream()
                         .map(t -> (net.morimekta.test.hazelcast.v1.Value) t)
                         .collect(Collectors.toList());
        }

        public net.morimekta.test.hazelcast.v2.Value nextV2Value() {
            net.morimekta.test.hazelcast.v2.Value[] values = net.morimekta.test.hazelcast.v2.Value.values();
            return values[random.nextInt(values.length)];
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
            net.morimekta.test.hazelcast.v3.Value[] values = net.morimekta.test.hazelcast.v3.Value.values();
            return values[random.nextInt(values.length)];
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
            net.morimekta.test.hazelcast.v4.Value[] values = net.morimekta.test.hazelcast.v4.Value.values();
            return values[random.nextInt(values.length)];
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

        public net.morimekta.test.hazelcast.v1.CompactFields nextV1CompactField(int flags, int index1, int index2, int index3) {
            net.morimekta.test.hazelcast.v1.CompactFields._Builder builder = net.morimekta.test.hazelcast.v1.CompactFields.builder();
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

        public List<net.morimekta.test.hazelcast.v1.CompactFields> nextV1CompactFields(int flags, int index1, int index2, int index3) {
            return Arrays.asList(Stream.generate(() -> nextV1CompactField(flags, index1, index2, index3))
                                       .limit(random
                                                                .nextInt(Byte.MAX_VALUE) + 1)
                                       .toArray())
                         .stream()
                         .map(t -> (net.morimekta.test.hazelcast.v1.CompactFields) t)
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

        public Map<Boolean, Boolean> nextBooleanMap() {
            return nextMap(() -> nextBooleans(), () -> nextBooleans(), 20);
        }

        public Map<Byte, Byte> nextByteMap() {
            return nextMap(() -> Bytes.asList(nextBytes()), () -> Bytes.asList(nextBytes()), 20);
        }

        public Map<Short, Short> nextShortMap() {
            return nextMap(() -> nextShorts(), () -> nextShorts(), 20);
        }

        public Map<Integer, Integer> nextIntegerMap() {
            return nextMap(() -> nextInts(), () -> nextInts(), 20);
        }

        public Map<Long, Long> nextLongMap() {
            return nextMap(() -> nextLongs(), () -> nextLongs(), 20);
        }

        public Map<Double, Double> nextDoubleMap() {
            return nextMap(() -> nextDoubles(), () -> nextDoubles(), 20);
        }

        public Map<String, String> nextStringMap() {
            return nextMap(() -> nextStrings(), () -> nextStrings(), 20);
        }

        public Map<Binary, Binary> nextBinaryMap() {
            return nextMap(() -> nextBinaries(), () -> nextBinaries(), 10);
        }

        public Map<net.morimekta.test.hazelcast.v1.Value, net.morimekta.test.hazelcast.v1.Value> nextV1ValueMap() {
            return nextMap(() -> nextV1Values(), () -> nextV1Values(), 10);
        }

        public Map<net.morimekta.test.hazelcast.v1.CompactFields, net.morimekta.test.hazelcast.v1.CompactFields>
        nextV1CompactMap(int flags, int index1, int index2, int index3, int index4, int index5, int index6) {
            return nextMap(() -> nextV1CompactFields(flags, index1, index2, index3),
                           () -> nextV1CompactFields(flags, index4, index5, index6), 10);
        }

        public Map<Integer, List<Boolean>> nextBooleanListMap() {
            return nextMap(() -> nextInts(),
                           () -> IntStream.range(0, 20).mapToObj(i -> nextBooleans()).collect(Collectors.toList()),
                           20);
        }

        public Map<Integer, List<Byte>> nextByteListMap() {
            return nextMap(() -> nextInts(),
                           () -> IntStream.range(0, 20).mapToObj(i -> Bytes.asList(nextBytes()))
                                          .collect(Collectors.toList()), 20);
        }

        public Map<Integer, List<Short>> nextShortListMap() {
            return nextMap(() -> nextInts(),
                           () -> IntStream.range(0, 20).mapToObj(i -> nextShorts())
                                          .collect(Collectors.toList()), 20);
        }

        public Map<Integer, List<Integer>> nextIntegerListMap() {
            return nextMap(() -> nextInts(),
                           () -> IntStream.range(0, 20).mapToObj(i -> nextInts())
                                          .collect(Collectors.toList()), 20);
        }

        public Map<Integer, List<Long>> nextLongListMap() {
            return nextMap(() -> nextInts(),
                           () -> IntStream.range(0, 20).mapToObj(i -> nextLongs()).collect(Collectors.toList()),
                           20);
        }

        public Map<Integer, List<Double>> nextDoubleListMap() {
            return nextMap(() -> nextInts(),
                           () -> IntStream.range(0, 20).mapToObj(i -> nextDoubles()).collect(Collectors.toList()),
                           20);
        }

        public Map<Integer, List<String>> nextStringListMap() {
            return nextMap(() -> nextInts(),
                           () -> IntStream.range(0, 20).mapToObj(i -> nextStrings()).collect(Collectors.toList()),
                           20);
        }

        public Map<Integer, List<Binary>> nextBinaryListMap() {
            return nextMap(() -> nextInts(),
                           () -> IntStream.range(0, 10).mapToObj(i -> nextBinaries()).collect(Collectors.toList()),
                           10);
        }

        public Map<Integer, List<net.morimekta.test.hazelcast.v1.Value>> nextV1ValueListMap() {
            return nextMap(() -> nextInts(),
                           () -> IntStream.range(0, 10).mapToObj(i -> nextV1Values()).collect(Collectors.toList()),
                           10);
        }

        public Map<Integer, List<net.morimekta.test.hazelcast.v1.CompactFields>>
        nextV1CompactListMap(int flags, int index1, int index2, int index3) {
            return nextMap(() -> nextInts(),
                           () -> IntStream.range(0, 10).mapToObj(i -> nextV1CompactFields(flags, index1, index2, index3))
                                          .collect(Collectors.toList()), 10);
        }

        public Map<Integer, Set<Boolean>> nextBooleanSetMap() {
            return nextMap(() -> nextInts(),
                           () -> IntStream.range(0, 20).mapToObj(i -> new HashSet<>(nextBooleans())).collect(Collectors.toList()),
                           20);
        }

        public Map<Integer, Set<Byte>> nextByteSetMap() {
            return nextMap(() -> nextInts(),
                           () -> IntStream.range(0, 20).mapToObj(i -> new HashSet<>(Bytes.asList(nextBytes())))
                                          .collect(Collectors.toList()), 20);
        }

        public Map<Integer, Set<Short>> nextShortSetMap() {
            return nextMap(() -> nextInts(),
                           () -> IntStream.range(0, 20).mapToObj(i -> new HashSet<>(nextShorts()))
                                          .collect(Collectors.toList()), 20);
        }

        public Map<Integer, Set<Integer>> nextIntegerSetMap() {
            return nextMap(() -> nextInts(),
                           () -> IntStream.range(0, 20).mapToObj(i -> new HashSet<>(nextInts()))
                                          .collect(Collectors.toList()), 20);
        }

        public Map<Integer, Set<Long>> nextLongSetMap() {
            return nextMap(() -> nextInts(),
                           () -> IntStream.range(0, 20).mapToObj(i -> new HashSet<>(nextLongs()))
                                          .collect(Collectors.toList()),
                           20);
        }

        public Map<Integer, Set<Double>> nextDoubleSetMap() {
            return nextMap(() -> nextInts(),
                           () -> IntStream.range(0, 20).mapToObj(i -> new HashSet<>(nextDoubles())).collect(Collectors.toList()),
                           20);
        }

        public Map<Integer, Set<String>> nextStringSetMap() {
            return nextMap(() -> nextInts(),
                           () -> IntStream.range(0, 20).mapToObj(i -> new HashSet<>(nextStrings())).collect(Collectors.toList()),
                           20);
        }

        public Map<Integer, Set<Binary>> nextBinarySetMap() {
            return nextMap(() -> nextInts(),
                           () -> IntStream.range(0, 10).mapToObj(i -> new HashSet<>(nextBinaries())).collect(Collectors.toList()),
                           10);
        }

        public Map<Integer, Set<net.morimekta.test.hazelcast.v1.Value>> nextV1ValueSetMap() {
            return nextMap(() -> nextInts(),
                           () -> IntStream.range(0, 10).mapToObj(i -> new HashSet<>(nextV1Values())).collect(Collectors.toList()),
                           10);
        }

        public Map<Integer, Set<net.morimekta.test.hazelcast.v1.CompactFields>>
        nextV1CompactSetMap(int flags, int index1, int index2, int index3) {
            return nextMap(() -> nextInts(),
                           () -> IntStream.range(0, 10).mapToObj(i -> new HashSet<>(nextV1CompactFields(flags, index1, index2, index3)))
                                          .collect(Collectors.toList()), 10);
        }

        public <K, V> Map<K, V> nextMap(Supplier<List<K>> keyInput, Supplier<List<V>> valueInput, int maxSize) {
            Map<K, V> result = new HashMap<>();
            List<K> keys = keyInput.get().stream().distinct().collect(Collectors.toList());
            List<V> values = valueInput.get();
            int size = keys.size() > values.size() ? values.size() : keys.size();
            size = ((maxSize < size && maxSize != 0) ? maxSize : size);
            for( int i = 0; i < size; i++ ) {
                result.put(keys.get(i), values.get(i));
            }
            return result;
        }
        
    }


    public net.morimekta.test.hazelcast.v1.OptionalMapFields nextOptionalMapFieldsV1() {
        return nextOptionalMapFieldsV1(false);
    }

    public net.morimekta.test.hazelcast.v1.OptionalMapFields nextOptionalMapFieldsV1(boolean setAll) {
        return nextOptionalMapFieldsV1(setAll ? 0x0000FFFF : random.nextInt());
    }

    public net.morimekta.test.hazelcast.v1.OptionalMapFields nextOptionalMapFieldsV1(int flags) {
        net.morimekta.test.hazelcast.v1.OptionalMapFields._Builder builder =
                net.morimekta.test.hazelcast.v1.OptionalMapFields.builder();
        if( 0 < (INDEX_01 & flags) ) {
            builder.setBooleanValue(entities.nextBooleanMap());
        }
        if( 0 < (INDEX_02 & flags) ) {
            builder.setByteValue(entities.nextByteMap());
        }
        if( 0 < (INDEX_03 & flags) ) {
            builder.setShortValue(entities.nextShortMap());
        }
        if( 0 < (INDEX_04 & flags) ) {
            builder.setIntegerValue(entities.nextIntegerMap());
        }
        if( 0 < (INDEX_05 & flags) ) {
            builder.setLongValue(entities.nextLongMap());
        }
        if( 0 < (INDEX_06 & flags) ) {
            builder.setDoubleValue(entities.nextDoubleMap());
        }
        if( 0 < (INDEX_07 & flags) ) {
            builder.setStringValue(entities.nextStringMap());
        }
        if( 0 < (INDEX_08 & flags) ) {
            builder.setBinaryValue(entities.nextBinaryMap());
        }
        if( 0 < (INDEX_09 & flags) ) {
            builder.setValueValue(entities.nextV1ValueMap());
        }
        if( 0 < (INDEX_10 & flags) ) {
            builder.setCompactValue(entities.nextV1CompactMap(flags, INDEX_11, INDEX_12, INDEX_13, INDEX_14, INDEX_15, INDEX_16));
        }
        return builder.build();
    }

    public net.morimekta.test.hazelcast.v1.OptionalMapListFields nextOptionalMapListFieldsV1() {
        return nextOptionalMapListFieldsV1(false);
    }

    public net.morimekta.test.hazelcast.v1.OptionalMapListFields nextOptionalMapListFieldsV1(boolean setAll) {
        return nextOptionalMapListFieldsV1(setAll ? 0x0000FFFF : random.nextInt());
    }

    public net.morimekta.test.hazelcast.v1.OptionalMapListFields nextOptionalMapListFieldsV1(int flags) {
        net.morimekta.test.hazelcast.v1.OptionalMapListFields._Builder builder =
                net.morimekta.test.hazelcast.v1.OptionalMapListFields.builder();
        if( 0 < (INDEX_01 & flags) ) {
            builder.setBooleanValueList(entities.nextBooleanListMap());
        }
        if( 0 < (INDEX_02 & flags) ) {
            builder.setByteValueList(entities.nextByteListMap());
        }
        if( 0 < (INDEX_03 & flags) ) {
            builder.setShortValueList(entities.nextShortListMap());
        }
        if( 0 < (INDEX_04 & flags) ) {
            builder.setIntegerValueList(entities.nextIntegerListMap());
        }
        if( 0 < (INDEX_05 & flags) ) {
            builder.setLongValueList(entities.nextLongListMap());
        }
        if( 0 < (INDEX_06 & flags) ) {
            builder.setDoubleValueList(entities.nextDoubleListMap());
        }
        if( 0 < (INDEX_07 & flags) ) {
            builder.setStringValueList(entities.nextStringListMap());
        }
        if( 0 < (INDEX_08 & flags) ) {
            builder.setBinaryValueList(entities.nextBinaryListMap());
        }
        if( 0 < (INDEX_09 & flags) ) {
            builder.setValueValueList(entities.nextV1ValueListMap());
        }
        if( 0 < (INDEX_10 & flags) ) {
            builder.setCompactValueList(entities.nextV1CompactListMap(flags, INDEX_11, INDEX_12, INDEX_13));
        }
        return builder.build();
    }

    public net.morimekta.test.hazelcast.v1.OptionalMapSetFields nextOptionalMapSetFieldsV1() {
        return nextOptionalMapSetFieldsV1(false);
    }

    public net.morimekta.test.hazelcast.v1.OptionalMapSetFields nextOptionalMapSetFieldsV1(boolean setAll) {
        return nextOptionalMapSetFieldsV1(setAll ? 0x0000FFFF : random.nextInt());
    }

    public net.morimekta.test.hazelcast.v1.OptionalMapSetFields nextOptionalMapSetFieldsV1(int flags) {
        net.morimekta.test.hazelcast.v1.OptionalMapSetFields._Builder builder =
                net.morimekta.test.hazelcast.v1.OptionalMapSetFields.builder();
        if( 0 < (INDEX_01 & flags) ) {
            builder.setBooleanValueSet(entities.nextBooleanSetMap());
        }
        if( 0 < (INDEX_02 & flags) ) {
            builder.setByteValueSet(entities.nextByteSetMap());
        }
        if( 0 < (INDEX_03 & flags) ) {
            builder.setShortValueSet(entities.nextShortSetMap());
        }
        if( 0 < (INDEX_04 & flags) ) {
            builder.setIntegerValueSet(entities.nextIntegerSetMap());
        }
        if( 0 < (INDEX_05 & flags) ) {
            builder.setLongValueSet(entities.nextLongSetMap());
        }
        if( 0 < (INDEX_06 & flags) ) {
            builder.setDoubleValueSet(entities.nextDoubleSetMap());
        }
        if( 0 < (INDEX_07 & flags) ) {
            builder.setStringValueSet(entities.nextStringSetMap());
        }
        if( 0 < (INDEX_08 & flags) ) {
            builder.setBinaryValueSet(entities.nextBinarySetMap());
        }
        if( 0 < (INDEX_09 & flags) ) {
            builder.setValueValueSet(entities.nextV1ValueSetMap());
        }
        if( 0 < (INDEX_10 & flags) ) {
            builder.setCompactValueSet(entities.nextV1CompactSetMap(flags, INDEX_11, INDEX_12, INDEX_13));
        }
        return builder.build();
    }

}
