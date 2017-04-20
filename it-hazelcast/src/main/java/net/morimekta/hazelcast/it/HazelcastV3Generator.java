package net.morimekta.hazelcast.it;

import net.morimekta.test.hazelcast.v3.AllFields;
import net.morimekta.test.hazelcast.v3.OptionalFields;
import net.morimekta.test.hazelcast.v3.OptionalListFields;
import net.morimekta.test.hazelcast.v3.OptionalMapFields;
import net.morimekta.test.hazelcast.v3.OptionalMapListFields;
import net.morimekta.test.hazelcast.v3.OptionalMapSetFields;
import net.morimekta.test.hazelcast.v3.OptionalSetFields;
import net.morimekta.test.hazelcast.v3.UnionFields;

import com.google.common.primitives.Bytes;

import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class HazelcastV3Generator {
    private final HazelcastGenerator hazelcastGenerator;

    public HazelcastV3Generator(HazelcastGenerator hazelcastGenerator) { this.hazelcastGenerator = hazelcastGenerator; }

    public OptionalFields nextOptionalFieldsV3() {
        return nextOptionalFieldsV3(false);
    }

    public OptionalFields nextOptionalFieldsV3(boolean setAll) {
        return nextOptionalFieldsV3(setAll ?
                                    HazelcastGenerator.INDEX_XX :
                                    hazelcastGenerator.getRandom()
                                                      .nextInt());
    }

    public OptionalFields nextOptionalFieldsV3(int flags) {
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
            builder.setBinaryValue(hazelcastGenerator.getItem()
                                                     .nextBinary());
        }
        if (0 < (HazelcastGenerator.INDEX_08 & flags)) {
            builder.setEnumValue(hazelcastGenerator.getItem()
                                                   .nextV3Value());
        }
        if (0 < (HazelcastGenerator.INDEX_09 & flags)) {
            builder.setCompactValue(hazelcastGenerator.getItem()
                                                      .nextV3CompactField(flags,
                                                                          HazelcastGenerator.INDEX_10,
                                                                          HazelcastGenerator.INDEX_11,
                                                                          HazelcastGenerator.INDEX_12));
        }
        if (0 < (HazelcastGenerator.INDEX_13 & flags)) {
            builder.setAnotherStringValue(hazelcastGenerator.getItem()
                                                            .nextString());
        }
        return builder.build();
    }

    public OptionalListFields nextOptionalListFieldsV3() {
        return nextOptionalListFieldsV3(false);
    }

    public OptionalListFields nextOptionalListFieldsV3(boolean setAll) {
        return nextOptionalListFieldsV3(setAll ?
                                        HazelcastGenerator.INDEX_XX :
                                        hazelcastGenerator.getRandom()
                                                          .nextInt());
    }

    public OptionalListFields nextOptionalListFieldsV3(int flags) {
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
            builder.setCompactValue(hazelcastGenerator.getItem()
                                                      .nextV3CompactFields(flags,
                                                                           HazelcastGenerator.INDEX_08,
                                                                           HazelcastGenerator.INDEX_09,
                                                                           HazelcastGenerator.INDEX_10));
        }
        if (0 < (HazelcastGenerator.INDEX_11 & flags)) {
            builder.setBinaryValue(hazelcastGenerator.getItem()
                                                     .nextBinaries());
        }
        if (0 < (HazelcastGenerator.INDEX_12 & flags)) {
            builder.setValueValue(hazelcastGenerator.getItem()
                                                    .nextV3Values());
        }
        if (0 < (HazelcastGenerator.INDEX_13 & flags)) {
            builder.setAnotherStringValues(hazelcastGenerator.getItem()
                                                             .nextStrings());
        }
        return builder.build();
    }

    public OptionalSetFields nextOptionalSetFieldsV3() {
        return nextOptionalSetFieldsV3(false);
    }

    public OptionalSetFields nextOptionalSetFieldsV3(boolean setAll) {
        return nextOptionalSetFieldsV3(setAll ?
                                       HazelcastGenerator.INDEX_XX :
                                       hazelcastGenerator.getRandom()
                                                         .nextInt());
    }

    public OptionalSetFields nextOptionalSetFieldsV3(int flags) {
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
            builder.setCompactValue(hazelcastGenerator.getItem()
                                                      .nextV3CompactFields(flags,
                                                                           HazelcastGenerator.INDEX_08,
                                                                           HazelcastGenerator.INDEX_09,
                                                                           HazelcastGenerator.INDEX_10));
        }
        if (0 < (HazelcastGenerator.INDEX_11 & flags)) {
            builder.setBinaryValue(hazelcastGenerator.getItem()
                                                     .nextBinaries());
        }
        if (0 < (HazelcastGenerator.INDEX_12 & flags)) {
            builder.setValueValue(hazelcastGenerator.getItem()
                                                    .nextV3Values());
        }
        if (0 < (HazelcastGenerator.INDEX_13 & flags)) {
            builder.setAnotherStringValues(hazelcastGenerator.getItem()
                                                             .nextStrings());
        }
        return builder.build();
    }

    public OptionalMapFields nextOptionalMapFieldsV3() {
        return nextOptionalMapFieldsV3(false);
    }

    public OptionalMapFields nextOptionalMapFieldsV3(boolean setAll) {
        return nextOptionalMapFieldsV3(setAll ?
                                       HazelcastGenerator.INDEX_XX :
                                       hazelcastGenerator.getRandom()
                                                         .nextInt());
    }

    public OptionalMapFields nextOptionalMapFieldsV3(int flags) {
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
        if (0 < (HazelcastGenerator.INDEX_08 & flags)) {
            builder.setBinaryValue(hazelcastGenerator.getItem()
                                                     .nextBinaryMap());
        }
        if (0 < (HazelcastGenerator.INDEX_09 & flags)) {
            builder.setValueValue(hazelcastGenerator.getItem()
                                                    .nextV3ValueMap());
        }
        if (0 < (HazelcastGenerator.INDEX_10 & flags)) {
            builder.setCompactValue(hazelcastGenerator.getItem()
                                                      .nextV3CompactMap(flags,
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

    public OptionalMapListFields nextOptionalMapListFieldsV3() {
        return nextOptionalMapListFieldsV3(false);
    }

    public OptionalMapListFields nextOptionalMapListFieldsV3(boolean setAll) {
        return nextOptionalMapListFieldsV3(setAll ?
                                           HazelcastGenerator.INDEX_XX :
                                           hazelcastGenerator.getRandom()
                                                             .nextInt());
    }

    public OptionalMapListFields nextOptionalMapListFieldsV3(int flags) {
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
        if (0 < (HazelcastGenerator.INDEX_08 & flags)) {
            builder.setBinaryValueList(hazelcastGenerator.getItem()
                                                         .nextBinaryListMap());
        }
        if (0 < (HazelcastGenerator.INDEX_09 & flags)) {
            builder.setValueValueList(hazelcastGenerator.getItem()
                                                        .nextV3ValueListMap());
        }
        if (0 < (HazelcastGenerator.INDEX_10 & flags)) {
            builder.setCompactValueList(hazelcastGenerator.getItem()
                                                          .nextV3CompactListMap(flags,
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

    public OptionalMapSetFields nextOptionalMapSetFieldsV3() {
        return nextOptionalMapSetFieldsV3(false);
    }

    public OptionalMapSetFields nextOptionalMapSetFieldsV3(boolean setAll) {
        return nextOptionalMapSetFieldsV3(setAll ?
                                          HazelcastGenerator.INDEX_XX :
                                          hazelcastGenerator.getRandom()
                                                            .nextInt());
    }

    public OptionalMapSetFields nextOptionalMapSetFieldsV3(int flags) {
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
        if (0 < (HazelcastGenerator.INDEX_08 & flags)) {
            builder.setBinaryValueSet(hazelcastGenerator.getItem()
                                                        .nextBinarySetMap());
        }
        if (0 < (HazelcastGenerator.INDEX_09 & flags)) {
            builder.setValueValueSet(hazelcastGenerator.getItem()
                                                       .nextV3ValueSetMap());
        }
        if (0 < (HazelcastGenerator.INDEX_10 & flags)) {
            builder.setCompactValueSet(hazelcastGenerator.getItem()
                                                         .nextV3CompactSetMap(flags,
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

    public UnionFields nextUnionFieldsV3() {
        return nextUnionFieldsV3(false);
    }

    public UnionFields nextUnionFieldsV3(boolean setAll) {
        return nextUnionFieldsV3(setAll ?
                                 HazelcastGenerator.INDEX_XX :
                                 hazelcastGenerator.getRandom()
                                                   .nextInt());
    }

    public UnionFields nextUnionFieldsV3(int flags) {
        UnionFields._Builder builder = UnionFields.builder();
        if (0 < (HazelcastGenerator.INDEX_01 & flags)) {
            builder.setAllFields(nextAllFieldsV3());
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
        if (0 < (HazelcastGenerator.INDEX_08 & flags)) {
            builder.setAllFieldsList(IntStream.range(0, 10)
                                              .mapToObj(i -> nextAllFieldsV3())
                                              .collect(Collectors.toList()));
        }
        if (0 < (HazelcastGenerator.INDEX_09 & flags)) {
            builder.setAllFieldsSet(IntStream.range(0, 10)
                                             .mapToObj(i -> nextAllFieldsV3())
                                             .collect(Collectors.toSet()));
        }
        if (0 < (HazelcastGenerator.INDEX_10 & flags)) {
            builder.setAllFieldsMap(hazelcastGenerator.getItem()
                                                      .nextMap(() -> hazelcastGenerator.getItem()
                                                                                       .nextStrings(),
                                                               () -> IntStream.range(0, 10)
                                                                              .mapToObj(i -> nextAllFieldsV3())
                                                                              .collect(Collectors.toList()),
                                                               10));
        }
        if (0 < (HazelcastGenerator.INDEX_11 & flags)) {
            builder.setAnotherStringValue(hazelcastGenerator.getItem()
                                                            .nextString());
        }
        return builder.build();
    }

    public AllFields nextAllFieldsV3() {
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
                                                                    .nextV3CompactField(hazelcastGenerator.getRandom()
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
                                                                 .nextV3Value());
            case INTEGER_VALUE:
                return AllFields.withIntegerValue(hazelcastGenerator.getItem()
                                                                    .nextInt());
            case LONG_VALUE:
                return AllFields.withLongValue(hazelcastGenerator.getItem()
                                                                 .nextLong());
            case SHORT_VALUE:
                return AllFields.withShortValue(hazelcastGenerator.getItem()
                                                                  .nextShort());
            case ANOTHER_STRING_VALUE:
                return AllFields.withAnotherStringValue(hazelcastGenerator.getItem()
                                                                          .nextString());
        }
        return null;
    }
}