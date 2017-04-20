package net.morimekta.hazelcast.it;

import net.morimekta.test.hazelcast.v2.AllFields;
import net.morimekta.test.hazelcast.v2.OptionalFields;
import net.morimekta.test.hazelcast.v2.OptionalListFields;
import net.morimekta.test.hazelcast.v2.OptionalMapFields;
import net.morimekta.test.hazelcast.v2.OptionalMapListFields;
import net.morimekta.test.hazelcast.v2.OptionalMapSetFields;
import net.morimekta.test.hazelcast.v2.OptionalSetFields;
import net.morimekta.test.hazelcast.v2.UnionFields;

import com.google.common.primitives.Bytes;

import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class HazelcastV2Generator {
    private final HazelcastGenerator hazelcastGenerator;

    public HazelcastV2Generator(HazelcastGenerator hazelcastGenerator) { this.hazelcastGenerator = hazelcastGenerator; }

    public OptionalFields nextOptionalFieldsV2() {
        return nextOptionalFieldsV2(false);
    }

    public OptionalFields nextOptionalFieldsV2(boolean setAll) {
        return nextOptionalFieldsV2(setAll ?
                                    HazelcastGenerator.INDEX_XX :
                                    hazelcastGenerator.getRandom()
                                                      .nextInt());
    }

    public OptionalFields nextOptionalFieldsV2(int flags) {
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
                                                   .nextV2Value());
        }
        if (0 < (HazelcastGenerator.INDEX_10 & flags)) {
            builder.setCompactValue(hazelcastGenerator.getItem()
                                                      .nextV2CompactField(flags,
                                                                          HazelcastGenerator.INDEX_11,
                                                                          HazelcastGenerator.INDEX_12,
                                                                          HazelcastGenerator.INDEX_13));
        }
        if (0 < (HazelcastGenerator.INDEX_14 & flags)) {
            builder.setAnotherStringValue(hazelcastGenerator.getItem()
                                                            .nextString());
        }
        return builder.build();
    }

    public OptionalListFields nextOptionalListFieldsV2() {
        return nextOptionalListFieldsV2(false);
    }

    public OptionalListFields nextOptionalListFieldsV2(boolean setAll) {
        return nextOptionalListFieldsV2(setAll ?
                                        HazelcastGenerator.INDEX_XX :
                                        hazelcastGenerator.getRandom()
                                                          .nextInt());
    }

    public OptionalListFields nextOptionalListFieldsV2(int flags) {
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
            builder.setCompactValue(hazelcastGenerator.getItem()
                                                      .nextV2CompactFields(flags,
                                                                           HazelcastGenerator.INDEX_09,
                                                                           HazelcastGenerator.INDEX_10,
                                                                           HazelcastGenerator.INDEX_11));
        }
        if (0 < (HazelcastGenerator.INDEX_12 & flags)) {
            builder.setBinaryValue(hazelcastGenerator.getItem()
                                                     .nextBinaries());
        }
        if (0 < (HazelcastGenerator.INDEX_13 & flags)) {
            builder.setValueValue(hazelcastGenerator.getItem()
                                                    .nextV2Values());
        }
        if (0 < (HazelcastGenerator.INDEX_14 & flags)) {
            builder.setAnotherStringValues(hazelcastGenerator.getItem()
                                                             .nextStrings());
        }
        return builder.build();
    }

    public OptionalSetFields nextOptionalSetFieldsV2() {
        return nextOptionalSetFieldsV2(false);
    }

    public OptionalSetFields nextOptionalSetFieldsV2(boolean setAll) {
        return nextOptionalSetFieldsV2(setAll ?
                                       HazelcastGenerator.INDEX_XX :
                                       hazelcastGenerator.getRandom()
                                                         .nextInt());
    }

    public OptionalSetFields nextOptionalSetFieldsV2(int flags) {
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
            builder.setCompactValue(hazelcastGenerator.getItem()
                                                      .nextV2CompactFields(flags,
                                                                           HazelcastGenerator.INDEX_09,
                                                                           HazelcastGenerator.INDEX_10,
                                                                           HazelcastGenerator.INDEX_11));
        }
        if (0 < (HazelcastGenerator.INDEX_12 & flags)) {
            builder.setBinaryValue(hazelcastGenerator.getItem()
                                                     .nextBinaries());
        }
        if (0 < (HazelcastGenerator.INDEX_13 & flags)) {
            builder.setValueValue(hazelcastGenerator.getItem()
                                                    .nextV2Values());
        }
        if (0 < (HazelcastGenerator.INDEX_14 & flags)) {
            builder.setAnotherStringValues(hazelcastGenerator.getItem()
                                                             .nextStrings());
        }
        return builder.build();
    }

    public OptionalMapFields nextOptionalMapFieldsV2() {
        return nextOptionalMapFieldsV2(false);
    }

    public OptionalMapFields nextOptionalMapFieldsV2(boolean setAll) {
        return nextOptionalMapFieldsV2(setAll ?
                                       HazelcastGenerator.INDEX_XX :
                                       hazelcastGenerator.getRandom()
                                                         .nextInt());
    }

    public OptionalMapFields nextOptionalMapFieldsV2(int flags) {
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
                                                    .nextV2ValueMap());
        }
        if (0 < (HazelcastGenerator.INDEX_10 & flags)) {
            builder.setCompactValue(hazelcastGenerator.getItem()
                                                      .nextV2CompactMap(flags,
                                                                        HazelcastGenerator.INDEX_11,
                                                                        HazelcastGenerator.INDEX_12,
                                                                        HazelcastGenerator.INDEX_13,
                                                                        HazelcastGenerator.INDEX_14,
                                                                        HazelcastGenerator.INDEX_15,
                                                                        HazelcastGenerator.INDEX_16));
        }
        if (0 < (HazelcastGenerator.INDEX_17 & flags)) {
            builder.setAnotherStringValue(hazelcastGenerator.getItem()
                                                            .nextStringMap());
        }
        return builder.build();
    }

    public OptionalMapListFields nextOptionalMapListFieldsV2() {
        return nextOptionalMapListFieldsV2(false);
    }

    public OptionalMapListFields nextOptionalMapListFieldsV2(boolean setAll) {
        return nextOptionalMapListFieldsV2(setAll ?
                                           HazelcastGenerator.INDEX_XX :
                                           hazelcastGenerator.getRandom()
                                                             .nextInt());
    }

    public OptionalMapListFields nextOptionalMapListFieldsV2(int flags) {
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
                                                        .nextV2ValueListMap());
        }
        if (0 < (HazelcastGenerator.INDEX_10 & flags)) {
            builder.setCompactValueList(hazelcastGenerator.getItem()
                                                          .nextV2CompactListMap(flags,
                                                                                HazelcastGenerator.INDEX_11,
                                                                                HazelcastGenerator.INDEX_12,
                                                                                HazelcastGenerator.INDEX_13));
        }
        if (0 < (HazelcastGenerator.INDEX_14 & flags)) {
            builder.setAnotherStringValueList(hazelcastGenerator.getItem()
                                                                .nextStringListMap());
        }
        return builder.build();
    }

    public OptionalMapSetFields nextOptionalMapSetFieldsV2() {
        return nextOptionalMapSetFieldsV2(false);
    }

    public OptionalMapSetFields nextOptionalMapSetFieldsV2(boolean setAll) {
        return nextOptionalMapSetFieldsV2(setAll ?
                                          HazelcastGenerator.INDEX_XX :
                                          hazelcastGenerator.getRandom()
                                                            .nextInt());
    }

    public OptionalMapSetFields nextOptionalMapSetFieldsV2(int flags) {
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
                                                       .nextV2ValueSetMap());
        }
        if (0 < (HazelcastGenerator.INDEX_10 & flags)) {
            builder.setCompactValueSet(hazelcastGenerator.getItem()
                                                         .nextV2CompactSetMap(flags,
                                                                              HazelcastGenerator.INDEX_11,
                                                                              HazelcastGenerator.INDEX_12,
                                                                              HazelcastGenerator.INDEX_13));
        }
        if (0 < (HazelcastGenerator.INDEX_14 & flags)) {
            builder.setAnotherStringValueSet(hazelcastGenerator.getItem()
                                                               .nextStringSetMap());
        }
        return builder.build();
    }

    public UnionFields nextUnionFieldsV2() {
        return nextUnionFieldsV2(false);
    }

    public UnionFields nextUnionFieldsV2(boolean setAll) {
        return nextUnionFieldsV2(setAll ?
                                 HazelcastGenerator.INDEX_XX :
                                 hazelcastGenerator.getRandom()
                                                   .nextInt());
    }

    public UnionFields nextUnionFieldsV2(int flags) {
        UnionFields._Builder builder = UnionFields.builder();
        if (0 < (HazelcastGenerator.INDEX_01 & flags)) {
            builder.setAllFields(nextAllFieldsV2());
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
                                              .mapToObj(i -> nextAllFieldsV2())
                                              .collect(Collectors.toList()));
        }
        if (0 < (HazelcastGenerator.INDEX_09 & flags)) {
            builder.setAllFieldsSet(IntStream.range(0, 10)
                                             .mapToObj(i -> nextAllFieldsV2())
                                             .collect(Collectors.toSet()));
        }
        if (0 < (HazelcastGenerator.INDEX_10 & flags)) {
            builder.setAllFieldsMap(hazelcastGenerator.getItem()
                                                      .nextMap(() -> hazelcastGenerator.getItem()
                                                                                       .nextStrings(),
                                                               () -> IntStream.range(0, 10)
                                                                              .mapToObj(i -> nextAllFieldsV2())
                                                                              .collect(Collectors.toList()),
                                                               10));
        }
        if (0 < (HazelcastGenerator.INDEX_11 & flags)) {
            builder.setAnotherStringValue(hazelcastGenerator.getItem()
                                                            .nextString());
        }
        return builder.build();
    }

    public AllFields nextAllFieldsV2() {
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
                                                                    .nextV2CompactField(hazelcastGenerator.getRandom()
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
                                                                 .nextV2Value());
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
            case ANOTHER_STRING_VALUE:
                return AllFields.withAnotherStringValue(hazelcastGenerator.getItem()
                                                                          .nextString());
        }
        return null;
    }
}