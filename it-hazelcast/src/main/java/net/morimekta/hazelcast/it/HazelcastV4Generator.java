package net.morimekta.hazelcast.it;

import net.morimekta.test.hazelcast.v4.AllFields;
import net.morimekta.test.hazelcast.v4.OptionalFields;
import net.morimekta.test.hazelcast.v4.OptionalListFields;
import net.morimekta.test.hazelcast.v4.OptionalMapFields;
import net.morimekta.test.hazelcast.v4.OptionalMapListFields;
import net.morimekta.test.hazelcast.v4.OptionalMapSetFields;
import net.morimekta.test.hazelcast.v4.OptionalSetFields;
import net.morimekta.test.hazelcast.v4.UnionFields;

import com.google.common.primitives.Bytes;

import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class HazelcastV4Generator {
    private final HazelcastGenerator hazelcastGenerator;

    public HazelcastV4Generator(HazelcastGenerator hazelcastGenerator) { this.hazelcastGenerator = hazelcastGenerator; }

    public OptionalFields nextOptionalFieldsV4() {
        return nextOptionalFieldsV4(false);
    }

    public OptionalFields nextOptionalFieldsV4(boolean setAll) {
        return nextOptionalFieldsV4(setAll ?
                                    HazelcastGenerator.INDEX_XX :
                                    hazelcastGenerator.getRandom()
                                                      .nextInt());
    }

    public OptionalFields nextOptionalFieldsV4(int flags) {
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
            builder.setIntegerValue(hazelcastGenerator.getItem()
                                                      .nextInt());
        }
        if (0 < (HazelcastGenerator.INDEX_04 & flags)) {
            builder.setLongValue(hazelcastGenerator.getItem()
                                                   .nextLong());
        }
        if (0 < (HazelcastGenerator.INDEX_05 & flags)) {
            builder.setDoubleValue(hazelcastGenerator.getItem()
                                                     .nextDouble());
        }
        if (0 < (HazelcastGenerator.INDEX_06 & flags)) {
            builder.setBinaryValue(hazelcastGenerator.getItem()
                                                     .nextBinary());
        }
        if (0 < (HazelcastGenerator.INDEX_07 & flags)) {
            builder.setEnumValue(hazelcastGenerator.getItem()
                                                   .nextV4Value());
        }
        if (0 < (HazelcastGenerator.INDEX_08 & flags)) {
            builder.setCompactValue(hazelcastGenerator.getItem()
                                                      .nextV4CompactField(flags,
                                                                          HazelcastGenerator.INDEX_09,
                                                                          HazelcastGenerator.INDEX_10,
                                                                          HazelcastGenerator.INDEX_11));
        }
        if (0 < (HazelcastGenerator.INDEX_12 & flags)) {
            builder.setAnotherStringValue(hazelcastGenerator.getItem()
                                                            .nextString());
        }
        if (0 < (HazelcastGenerator.INDEX_13 & flags)) {
            builder.setAnotherShortValue(hazelcastGenerator.getItem()
                                                           .nextShort());
        }
        return builder.build();
    }

    public OptionalListFields nextOptionalListFieldsV4() {
        return nextOptionalListFieldsV4(false);
    }

    public OptionalListFields nextOptionalListFieldsV4(boolean setAll) {
        return nextOptionalListFieldsV4(setAll ?
                                        HazelcastGenerator.INDEX_XX :
                                        hazelcastGenerator.getRandom()
                                                          .nextInt());
    }

    public OptionalListFields nextOptionalListFieldsV4(int flags) {
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
            builder.setLongValue(hazelcastGenerator.getItem()
                                                   .nextLongs());
        }
        if (0 < (HazelcastGenerator.INDEX_05 & flags)) {
            builder.setDoubleValue(hazelcastGenerator.getItem()
                                                     .nextDoubles());
        }
        if (0 < (HazelcastGenerator.INDEX_06 & flags)) {
            builder.setCompactValue(hazelcastGenerator.getItem()
                                                      .nextV4CompactFields(flags,
                                                                           HazelcastGenerator.INDEX_07,
                                                                           HazelcastGenerator.INDEX_08,
                                                                           HazelcastGenerator.INDEX_09));
        }
        if (0 < (HazelcastGenerator.INDEX_10 & flags)) {
            builder.setBinaryValue(hazelcastGenerator.getItem()
                                                     .nextBinaries());
        }
        if (0 < (HazelcastGenerator.INDEX_11 & flags)) {
            builder.setValueValue(hazelcastGenerator.getItem()
                                                    .nextV4Values());
        }
        if (0 < (HazelcastGenerator.INDEX_12 & flags)) {
            builder.setAnotherStringValues(hazelcastGenerator.getItem()
                                                             .nextStrings());
        }
        if (0 < (HazelcastGenerator.INDEX_13 & flags)) {
            builder.setAnotherIntegerValue(hazelcastGenerator.getItem()
                                                             .nextInts());
        }
        return builder.build();
    }

    public OptionalSetFields nextOptionalSetFieldsV4() {
        return nextOptionalSetFieldsV4(false);
    }

    public OptionalSetFields nextOptionalSetFieldsV4(boolean setAll) {
        return nextOptionalSetFieldsV4(setAll ?
                                       HazelcastGenerator.INDEX_XX :
                                       hazelcastGenerator.getRandom()
                                                         .nextInt());
    }

    public OptionalSetFields nextOptionalSetFieldsV4(int flags) {
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
            builder.setLongValue(hazelcastGenerator.getItem()
                                                   .nextLongs());
        }
        if (0 < (HazelcastGenerator.INDEX_05 & flags)) {
            builder.setDoubleValue(hazelcastGenerator.getItem()
                                                     .nextDoubles());
        }
        if (0 < (HazelcastGenerator.INDEX_06 & flags)) {
            builder.setCompactValue(hazelcastGenerator.getItem()
                                                      .nextV4CompactFields(flags,
                                                                           HazelcastGenerator.INDEX_07,
                                                                           HazelcastGenerator.INDEX_08,
                                                                           HazelcastGenerator.INDEX_09));
        }
        if (0 < (HazelcastGenerator.INDEX_10 & flags)) {
            builder.setBinaryValue(hazelcastGenerator.getItem()
                                                     .nextBinaries());
        }
        if (0 < (HazelcastGenerator.INDEX_11 & flags)) {
            builder.setValueValue(hazelcastGenerator.getItem()
                                                    .nextV4Values());
        }
        if (0 < (HazelcastGenerator.INDEX_12 & flags)) {
            builder.setAnotherStringValues(hazelcastGenerator.getItem()
                                                             .nextStrings());
        }
        if (0 < (HazelcastGenerator.INDEX_13 & flags)) {
            builder.setAnotherIntegerValue(hazelcastGenerator.getItem()
                                                             .nextInts());
        }
        return builder.build();
    }

    public OptionalMapFields nextOptionalMapFieldsV4() {
        return nextOptionalMapFieldsV4(false);
    }

    public OptionalMapFields nextOptionalMapFieldsV4(boolean setAll) {
        return nextOptionalMapFieldsV4(setAll ?
                                       HazelcastGenerator.INDEX_XX :
                                       hazelcastGenerator.getRandom()
                                                         .nextInt());
    }

    public OptionalMapFields nextOptionalMapFieldsV4(int flags) {
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
            builder.setAnotherIntegerValue(hazelcastGenerator.getItem()
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
                                                    .nextV4ValueMap());
        }
        if (0 < (HazelcastGenerator.INDEX_10 & flags)) {
            builder.setCompactValue(hazelcastGenerator.getItem()
                                                      .nextV4CompactMap(flags,
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

    public OptionalMapListFields nextOptionalMapListFieldsV4() {
        return nextOptionalMapListFieldsV4(false);
    }

    public OptionalMapListFields nextOptionalMapListFieldsV4(boolean setAll) {
        return nextOptionalMapListFieldsV4(setAll ?
                                           HazelcastGenerator.INDEX_XX :
                                           hazelcastGenerator.getRandom()
                                                             .nextInt());
    }

    public OptionalMapListFields nextOptionalMapListFieldsV4(int flags) {
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
            builder.setAnotherIntegerValueList(hazelcastGenerator.getItem()
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
                                                        .nextV4ValueListMap());
        }
        if (0 < (HazelcastGenerator.INDEX_10 & flags)) {
            builder.setCompactValueList(hazelcastGenerator.getItem()
                                                          .nextV4CompactListMap(flags,
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

    public OptionalMapSetFields nextOptionalMapSetFieldsV4() {
        return nextOptionalMapSetFieldsV4(false);
    }

    public OptionalMapSetFields nextOptionalMapSetFieldsV4(boolean setAll) {
        return nextOptionalMapSetFieldsV4(setAll ?
                                          HazelcastGenerator.INDEX_XX :
                                          hazelcastGenerator.getRandom()
                                                            .nextInt());
    }

    public OptionalMapSetFields nextOptionalMapSetFieldsV4(int flags) {
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
            builder.setAnotherIntegerValueSet(hazelcastGenerator.getItem()
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
                                                       .nextV4ValueSetMap());
        }
        if (0 < (HazelcastGenerator.INDEX_10 & flags)) {
            builder.setCompactValueSet(hazelcastGenerator.getItem()
                                                         .nextV4CompactSetMap(flags,
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

    public UnionFields nextUnionFieldsV4() {
        return nextUnionFieldsV4(false);
    }

    public UnionFields nextUnionFieldsV4(boolean setAll) {
        return nextUnionFieldsV4(setAll ?
                                 HazelcastGenerator.INDEX_XX :
                                 hazelcastGenerator.getRandom()
                                                   .nextInt());
    }

    public UnionFields nextUnionFieldsV4(int flags) {
        UnionFields._Builder builder = UnionFields.builder();
        if (0 < (HazelcastGenerator.INDEX_01 & flags)) {
            builder.setAllFields(nextAllFieldsV4());
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
            builder.setAnotherIntegerValue(hazelcastGenerator.getItem()
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
                                              .mapToObj(i -> nextAllFieldsV4())
                                              .collect(Collectors.toList()));
        }
        if (0 < (HazelcastGenerator.INDEX_09 & flags)) {
            builder.setAllFieldsSet(IntStream.range(0, 10)
                                             .mapToObj(i -> nextAllFieldsV4())
                                             .collect(Collectors.toSet()));
        }
        if (0 < (HazelcastGenerator.INDEX_10 & flags)) {
            builder.setAllFieldsMap(hazelcastGenerator.getItem()
                                                      .nextMap(() -> hazelcastGenerator.getItem()
                                                                                       .nextStrings(),
                                                               () -> IntStream.range(0, 10)
                                                                              .mapToObj(i -> nextAllFieldsV4())
                                                                              .collect(Collectors.toList()),
                                                               10));
        }
        if (0 < (HazelcastGenerator.INDEX_11 & flags)) {
            builder.setAnotherStringValue(hazelcastGenerator.getItem()
                                                            .nextString());
        }
        return builder.build();
    }

    public AllFields nextAllFieldsV4() {
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
                                                                    .nextV4CompactField(hazelcastGenerator.getRandom()
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
                                                                 .nextV4Value());
            case ANOTHER_INTEGER_VALUE:
                return AllFields.withAnotherIntegerValue(hazelcastGenerator.getItem()
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