package net.morimekta.hazelcast.it;

import net.morimekta.test.hazelcast.v1.AllFields;
import net.morimekta.test.hazelcast.v1.OptionalFields;
import net.morimekta.test.hazelcast.v1.OptionalListFields;
import net.morimekta.test.hazelcast.v1.OptionalMapFields;
import net.morimekta.test.hazelcast.v1.OptionalMapListFields;
import net.morimekta.test.hazelcast.v1.OptionalMapSetFields;
import net.morimekta.test.hazelcast.v1.OptionalSetFields;
import net.morimekta.test.hazelcast.v1.UnionFields;

import com.google.common.primitives.Bytes;

import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class HazelcastV1Generator {
    private final HazelcastGenerator hazelcastGenerator;

    public HazelcastV1Generator(HazelcastGenerator hazelcastGenerator) { this.hazelcastGenerator = hazelcastGenerator; }

    public OptionalFields nextOptionalFieldsV1() {
        return nextOptionalFieldsV1(false);
    }

    public OptionalFields nextOptionalFieldsV1(boolean setAll) {
        return nextOptionalFieldsV1(setAll ?
                                    HazelcastGenerator.INDEX_XX :
                                    hazelcastGenerator.getRandom()
                                                      .nextInt());
    }

    public OptionalFields nextOptionalFieldsV1(int flags) {
        OptionalFields._Builder builder = OptionalFields.builder();
        if (0 < (HazelcastGenerator.INDEX_01 & flags)) {
            builder.setBooleanValue(hazelcastGenerator.getItem()
                                                      .nextBoolean());
        }
        if (0 < (HazelcastGenerator.INDEX_02 & flags)) {
            builder.setByteValue(hazelcastGenerator.getItem()
                                                   .nextByte());
        }
        if (0 < (HazelcastGenerator.INDEX_03 & flags)) {
            builder.setShortValue(hazelcastGenerator.getItem()
                                                    .nextShort());
        }
        if (0 < (HazelcastGenerator.INDEX_04 & flags)) {
            builder.setIntegerValue(hazelcastGenerator.getItem()
                                                      .nextInt());
        }
        if (0 < (HazelcastGenerator.INDEX_05 & flags)) {
            builder.setLongValue(hazelcastGenerator.getItem()
                                                   .nextLong());
        }
        if (0 < (HazelcastGenerator.INDEX_06 & flags)) {
            builder.setDoubleValue(hazelcastGenerator.getItem()
                                                     .nextDouble());
        }
        if (0 < (HazelcastGenerator.INDEX_07 & flags)) {
            builder.setStringValue(hazelcastGenerator.getItem()
                                                     .nextString());
        }
        if (0 < (HazelcastGenerator.INDEX_08 & flags)) {
            builder.setBinaryValue(hazelcastGenerator.getItem()
                                                     .nextBinary());
        }
        if (0 < (HazelcastGenerator.INDEX_09 & flags)) {
            builder.setEnumValue(hazelcastGenerator.getItem()
                                                   .nextV1Value());
        }
        if (0 < (HazelcastGenerator.INDEX_10 & flags)) {
            builder.setCompactValue(hazelcastGenerator.getItem()
                                                      .nextV1CompactField(flags,
                                                                          HazelcastGenerator.INDEX_11,
                                                                          HazelcastGenerator.INDEX_12,
                                                                          HazelcastGenerator.INDEX_13));
        }
        return builder.build();
    }

    public OptionalListFields nextOptionalListFieldsV1() {
        return nextOptionalListFieldsV1(false);
    }

    public OptionalListFields nextOptionalListFieldsV1(boolean setAll) {
        return nextOptionalListFieldsV1(setAll ?
                                        HazelcastGenerator.INDEX_XX :
                                        hazelcastGenerator.getRandom()
                                                          .nextInt());
    }

    public OptionalListFields nextOptionalListFieldsV1(int flags) {
        OptionalListFields._Builder builder = OptionalListFields.builder();
        if (0 < (HazelcastGenerator.INDEX_01 & flags)) {
            builder.setBooleanValues(hazelcastGenerator.getItem()
                                                       .nextBooleans());
        }
        if (0 < (HazelcastGenerator.INDEX_02 & flags)) {
            builder.setByteValues(Bytes.asList(hazelcastGenerator.getItem()
                                                                 .nextBytes()));
        }
        if (0 < (HazelcastGenerator.INDEX_03 & flags)) {
            builder.setShortValues(hazelcastGenerator.getItem()
                                                     .nextShorts());
        }
        if (0 < (HazelcastGenerator.INDEX_04 & flags)) {
            builder.setIntegerValue(hazelcastGenerator.getItem()
                                                      .nextInts());
        }
        if (0 < (HazelcastGenerator.INDEX_05 & flags)) {
            builder.setLongValue(hazelcastGenerator.getItem()
                                                   .nextLongs());
        }
        if (0 < (HazelcastGenerator.INDEX_06 & flags)) {
            builder.setDoubleValue(hazelcastGenerator.getItem()
                                                     .nextDoubles());
        }
        if (0 < (HazelcastGenerator.INDEX_07 & flags)) {
            builder.setStringValue(hazelcastGenerator.getItem()
                                                     .nextStrings());
        }
        if (0 < (HazelcastGenerator.INDEX_08 & flags)) {
            builder.setBinaryValue(hazelcastGenerator.getItem()
                                                     .nextBinaries());
        }
        if (0 < (HazelcastGenerator.INDEX_09 & flags)) {
            builder.setValueValue(hazelcastGenerator.getItem()
                                                    .nextV1Values());
        }
        if (0 < (HazelcastGenerator.INDEX_10 & flags)) {
            builder.setCompactValue(hazelcastGenerator.getItem()
                                                      .nextV1CompactFields(flags,
                                                                           HazelcastGenerator.INDEX_09,
                                                                           HazelcastGenerator.INDEX_10,
                                                                           HazelcastGenerator.INDEX_11));
        }
        return builder.build();
    }

    public OptionalSetFields nextOptionalSetFieldsV1() {
        return nextOptionalSetFieldsV1(false);
    }

    public OptionalSetFields nextOptionalSetFieldsV1(boolean setAll) {
        return nextOptionalSetFieldsV1(setAll ?
                                       HazelcastGenerator.INDEX_XX :
                                       hazelcastGenerator.getRandom()
                                                         .nextInt());
    }

    public OptionalSetFields nextOptionalSetFieldsV1(int flags) {
        OptionalSetFields._Builder builder = OptionalSetFields.builder();
        if (0 < (HazelcastGenerator.INDEX_01 & flags)) {
            builder.setBooleanValues(hazelcastGenerator.getItem()
                                                       .nextBooleans());
        }
        if (0 < (HazelcastGenerator.INDEX_02 & flags)) {
            builder.setByteValues(Bytes.asList(hazelcastGenerator.getItem()
                                                                 .nextBytes()));
        }
        if (0 < (HazelcastGenerator.INDEX_03 & flags)) {
            builder.setShortValues(hazelcastGenerator.getItem()
                                                     .nextShorts());
        }
        if (0 < (HazelcastGenerator.INDEX_04 & flags)) {
            builder.setIntegerValue(hazelcastGenerator.getItem()
                                                      .nextInts());
        }
        if (0 < (HazelcastGenerator.INDEX_05 & flags)) {
            builder.setLongValue(hazelcastGenerator.getItem()
                                                   .nextLongs());
        }
        if (0 < (HazelcastGenerator.INDEX_06 & flags)) {
            builder.setDoubleValue(hazelcastGenerator.getItem()
                                                     .nextDoubles());
        }
        if (0 < (HazelcastGenerator.INDEX_07 & flags)) {
            builder.setStringValue(hazelcastGenerator.getItem()
                                                     .nextStrings());
        }
        if (0 < (HazelcastGenerator.INDEX_08 & flags)) {
            builder.setBinaryValue(hazelcastGenerator.getItem()
                                                     .nextBinaries());
        }
        if (0 < (HazelcastGenerator.INDEX_09 & flags)) {
            builder.setValueValue(hazelcastGenerator.getItem()
                                                    .nextV1Values());
        }
        if (0 < (HazelcastGenerator.INDEX_10 & flags)) {
            builder.setCompactValue(hazelcastGenerator.getItem()
                                                      .nextV1CompactFields(flags,
                                                                           HazelcastGenerator.INDEX_09,
                                                                           HazelcastGenerator.INDEX_10,
                                                                           HazelcastGenerator.INDEX_11));
        }
        return builder.build();
    }

    public OptionalMapFields nextOptionalMapFieldsV1() {
        return nextOptionalMapFieldsV1(false);
    }

    public OptionalMapFields nextOptionalMapFieldsV1(boolean setAll) {
        return nextOptionalMapFieldsV1(setAll ?
                                       HazelcastGenerator.INDEX_XX :
                                       hazelcastGenerator.getRandom()
                                                         .nextInt());
    }

    public OptionalMapFields nextOptionalMapFieldsV1(int flags) {
        OptionalMapFields._Builder builder = OptionalMapFields.builder();
        if (0 < (HazelcastGenerator.INDEX_01 & flags)) {
            builder.setBooleanValue(hazelcastGenerator.getItem()
                                                      .nextBooleanMap());
        }
        if (0 < (HazelcastGenerator.INDEX_02 & flags)) {
            builder.setByteValue(hazelcastGenerator.getItem()
                                                   .nextByteMap());
        }
        if (0 < (HazelcastGenerator.INDEX_03 & flags)) {
            builder.setShortValue(hazelcastGenerator.getItem()
                                                    .nextShortMap());
        }
        if (0 < (HazelcastGenerator.INDEX_04 & flags)) {
            builder.setIntegerValue(hazelcastGenerator.getItem()
                                                      .nextIntegerMap());
        }
        if (0 < (HazelcastGenerator.INDEX_05 & flags)) {
            builder.setLongValue(hazelcastGenerator.getItem()
                                                   .nextLongMap());
        }
        if (0 < (HazelcastGenerator.INDEX_06 & flags)) {
            builder.setDoubleValue(hazelcastGenerator.getItem()
                                                     .nextDoubleMap());
        }
        if (0 < (HazelcastGenerator.INDEX_07 & flags)) {
            builder.setStringValue(hazelcastGenerator.getItem()
                                                     .nextStringMap());
        }
        if (0 < (HazelcastGenerator.INDEX_08 & flags)) {
            builder.setBinaryValue(hazelcastGenerator.getItem()
                                                     .nextBinaryMap());
        }
        if (0 < (HazelcastGenerator.INDEX_09 & flags)) {
            builder.setValueValue(hazelcastGenerator.getItem()
                                                    .nextV1ValueMap());
        }
        if (0 < (HazelcastGenerator.INDEX_10 & flags)) {
            builder.setCompactValue(hazelcastGenerator.getItem()
                                                      .nextV1CompactMap(flags,
                                                                        HazelcastGenerator.INDEX_11,
                                                                        HazelcastGenerator.INDEX_12,
                                                                        HazelcastGenerator.INDEX_13,
                                                                        HazelcastGenerator.INDEX_14,
                                                                        HazelcastGenerator.INDEX_15,
                                                                        HazelcastGenerator.INDEX_16));
        }
        return builder.build();
    }

    public OptionalMapListFields nextOptionalMapListFieldsV1() {
        return nextOptionalMapListFieldsV1(false);
    }

    public OptionalMapListFields nextOptionalMapListFieldsV1(boolean setAll) {
        return nextOptionalMapListFieldsV1(setAll ?
                                           HazelcastGenerator.INDEX_XX :
                                           hazelcastGenerator.getRandom()
                                                             .nextInt());
    }

    public OptionalMapListFields nextOptionalMapListFieldsV1(int flags) {
        OptionalMapListFields._Builder builder = OptionalMapListFields.builder();
        if (0 < (HazelcastGenerator.INDEX_01 & flags)) {
            builder.setBooleanValueList(hazelcastGenerator.getItem()
                                                          .nextBooleanListMap());
        }
        if (0 < (HazelcastGenerator.INDEX_02 & flags)) {
            builder.setByteValueList(hazelcastGenerator.getItem()
                                                       .nextByteListMap());
        }
        if (0 < (HazelcastGenerator.INDEX_03 & flags)) {
            builder.setShortValueList(hazelcastGenerator.getItem()
                                                        .nextShortListMap());
        }
        if (0 < (HazelcastGenerator.INDEX_04 & flags)) {
            builder.setIntegerValueList(hazelcastGenerator.getItem()
                                                          .nextIntegerListMap());
        }
        if (0 < (HazelcastGenerator.INDEX_05 & flags)) {
            builder.setLongValueList(hazelcastGenerator.getItem()
                                                       .nextLongListMap());
        }
        if (0 < (HazelcastGenerator.INDEX_06 & flags)) {
            builder.setDoubleValueList(hazelcastGenerator.getItem()
                                                         .nextDoubleListMap());
        }
        if (0 < (HazelcastGenerator.INDEX_07 & flags)) {
            builder.setStringValueList(hazelcastGenerator.getItem()
                                                         .nextStringListMap());
        }
        if (0 < (HazelcastGenerator.INDEX_08 & flags)) {
            builder.setBinaryValueList(hazelcastGenerator.getItem()
                                                         .nextBinaryListMap());
        }
        if (0 < (HazelcastGenerator.INDEX_09 & flags)) {
            builder.setValueValueList(hazelcastGenerator.getItem()
                                                        .nextV1ValueListMap());
        }
        if (0 < (HazelcastGenerator.INDEX_10 & flags)) {
            builder.setCompactValueList(hazelcastGenerator.getItem()
                                                          .nextV1CompactListMap(flags,
                                                                                HazelcastGenerator.INDEX_11,
                                                                                HazelcastGenerator.INDEX_12,
                                                                                HazelcastGenerator.INDEX_13));
        }
        return builder.build();
    }

    public OptionalMapSetFields nextOptionalMapSetFieldsV1() {
        return nextOptionalMapSetFieldsV1(false);
    }

    public OptionalMapSetFields nextOptionalMapSetFieldsV1(boolean setAll) {
        return nextOptionalMapSetFieldsV1(setAll ?
                                          HazelcastGenerator.INDEX_XX :
                                          hazelcastGenerator.getRandom()
                                                            .nextInt());
    }

    public OptionalMapSetFields nextOptionalMapSetFieldsV1(int flags) {
        OptionalMapSetFields._Builder builder = OptionalMapSetFields.builder();
        if (0 < (HazelcastGenerator.INDEX_01 & flags)) {
            builder.setBooleanValueSet(hazelcastGenerator.getItem()
                                                         .nextBooleanSetMap());
        }
        if (0 < (HazelcastGenerator.INDEX_02 & flags)) {
            builder.setByteValueSet(hazelcastGenerator.getItem()
                                                      .nextByteSetMap());
        }
        if (0 < (HazelcastGenerator.INDEX_03 & flags)) {
            builder.setShortValueSet(hazelcastGenerator.getItem()
                                                       .nextShortSetMap());
        }
        if (0 < (HazelcastGenerator.INDEX_04 & flags)) {
            builder.setIntegerValueSet(hazelcastGenerator.getItem()
                                                         .nextIntegerSetMap());
        }
        if (0 < (HazelcastGenerator.INDEX_05 & flags)) {
            builder.setLongValueSet(hazelcastGenerator.getItem()
                                                      .nextLongSetMap());
        }
        if (0 < (HazelcastGenerator.INDEX_06 & flags)) {
            builder.setDoubleValueSet(hazelcastGenerator.getItem()
                                                        .nextDoubleSetMap());
        }
        if (0 < (HazelcastGenerator.INDEX_07 & flags)) {
            builder.setStringValueSet(hazelcastGenerator.getItem()
                                                        .nextStringSetMap());
        }
        if (0 < (HazelcastGenerator.INDEX_08 & flags)) {
            builder.setBinaryValueSet(hazelcastGenerator.getItem()
                                                        .nextBinarySetMap());
        }
        if (0 < (HazelcastGenerator.INDEX_09 & flags)) {
            builder.setValueValueSet(hazelcastGenerator.getItem()
                                                       .nextV1ValueSetMap());
        }
        if (0 < (HazelcastGenerator.INDEX_10 & flags)) {
            builder.setCompactValueSet(hazelcastGenerator.getItem()
                                                         .nextV1CompactSetMap(flags,
                                                                              HazelcastGenerator.INDEX_11,
                                                                              HazelcastGenerator.INDEX_12,
                                                                              HazelcastGenerator.INDEX_13));
        }
        return builder.build();
    }

    public UnionFields nextUnionFieldsV1() {
        return nextUnionFieldsV1(false);
    }

    public UnionFields nextUnionFieldsV1(boolean setAll) {
        return nextUnionFieldsV1(setAll ?
                                 HazelcastGenerator.INDEX_XX :
                                 hazelcastGenerator.getRandom()
                                                   .nextInt());
    }

    public UnionFields nextUnionFieldsV1(int flags) {
        UnionFields._Builder builder = UnionFields.builder();
        if (0 < (HazelcastGenerator.INDEX_01 & flags)) {
            builder.setAllFields(nextAllFieldsV1());
        }
        if (0 < (HazelcastGenerator.INDEX_02 & flags)) {
            builder.setByteValue(hazelcastGenerator.getItem()
                                                   .nextByte());
        }
        if (0 < (HazelcastGenerator.INDEX_03 & flags)) {
            builder.setShortValue(hazelcastGenerator.getItem()
                                                    .nextShort());
        }
        if (0 < (HazelcastGenerator.INDEX_04 & flags)) {
            builder.setIntegerValue(hazelcastGenerator.getItem()
                                                      .nextInt());
        }
        if (0 < (HazelcastGenerator.INDEX_05 & flags)) {
            builder.setLongValue(hazelcastGenerator.getItem()
                                                   .nextLong());
        }
        if (0 < (HazelcastGenerator.INDEX_06 & flags)) {
            builder.setDoubleValue(hazelcastGenerator.getItem()
                                                     .nextDouble());
        }
        if (0 < (HazelcastGenerator.INDEX_07 & flags)) {
            builder.setStringValue(hazelcastGenerator.getItem()
                                                     .nextString());
        }
        if (0 < (HazelcastGenerator.INDEX_08 & flags)) {
            builder.setAllFieldsList(IntStream.range(0, 10)
                                              .mapToObj(i -> nextAllFieldsV1())
                                              .collect(Collectors.toList()));
        }
        if (0 < (HazelcastGenerator.INDEX_09 & flags)) {
            builder.setAllFieldsSet(IntStream.range(0, 10)
                                             .mapToObj(i -> nextAllFieldsV1())
                                             .collect(Collectors.toSet()));
        }
        if (0 < (HazelcastGenerator.INDEX_10 & flags)) {
            builder.setAllFieldsMap(hazelcastGenerator.getItem()
                                                      .nextMap(() -> hazelcastGenerator.getItem()
                                                                                       .nextStrings(),
                                                               () -> IntStream.range(0, 10)
                                                                              .mapToObj(i -> nextAllFieldsV1())
                                                                              .collect(Collectors.toList()),
                                                               10));
        }
        return builder.build();
    }

    public AllFields nextAllFieldsV1() {
        switch (hazelcastGenerator.getItem()
                                  .randFromArray(AllFields._Field.values())) {
            case BINARY_VALUE:
                return AllFields.withBinaryValue(hazelcastGenerator.getItem()
                                                                   .nextBinary());
            case BOOLEAN_VALUE:
                return AllFields.withBooleanValue(hazelcastGenerator.getItem()
                                                                    .nextBoolean());
            case BYTE_VALUE:
                return AllFields.withByteValue(hazelcastGenerator.getItem()
                                                                 .nextByte());
            case COMPACT_VALUE:
                return AllFields.withCompactValue(hazelcastGenerator.getItem()
                                                                    .nextV1CompactField(hazelcastGenerator.getRandom()
                                                                                                          .nextInt(
                                                                                                                  HazelcastGenerator.INDEX_XX),
                                                                                        HazelcastGenerator.INDEX_01,
                                                                                        HazelcastGenerator.INDEX_02,
                                                                                        HazelcastGenerator.INDEX_03));
            case DOUBLE_VALUE:
                return AllFields.withDoubleValue(hazelcastGenerator.getItem()
                                                                   .nextDouble());
            case ENUM_VALUE:
                return AllFields.withEnumValue(hazelcastGenerator.getItem()
                                                                 .nextV1Value());
            case INTEGER_VALUE:
                return AllFields.withIntegerValue(hazelcastGenerator.getItem()
                                                                    .nextInt());
            case LONG_VALUE:
                return AllFields.withLongValue(hazelcastGenerator.getItem()
                                                                 .nextLong());
            case SHORT_VALUE:
                return AllFields.withShortValue(hazelcastGenerator.getItem()
                                                                  .nextShort());
            case STRING_VALUE:
                return AllFields.withStringValue(hazelcastGenerator.getItem()
                                                                   .nextString());
        }
        return null;
    }
}