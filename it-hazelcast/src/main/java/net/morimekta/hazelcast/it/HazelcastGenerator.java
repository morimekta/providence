package net.morimekta.hazelcast.it;

import java.util.Random;

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
    public static final int INDEX_17 = 0x00010000;
    public static final int INDEX_XX = 0x0001FFFF;

    private final Random        random;
    public  final ItemGenerator item;
    private final HazelcastV1Generator hazelcastV1Generator = new HazelcastV1Generator(this);
    private final HazelcastV2Generator hazelcastV2Generator = new HazelcastV2Generator(this);
    private final HazelcastV3Generator hazelcastV3Generator = new HazelcastV3Generator(this);
    private final HazelcastV4Generator hazelcastV4Generator = new HazelcastV4Generator(this);

    public HazelcastGenerator() {
        random = new Random();
        item = new ItemGenerator();
    }

    public ItemGenerator getItem() {
        return this.item;
    }

    public net.morimekta.test.hazelcast.v1.OptionalFields nextOptionalFieldsV1() {
        return hazelcastV1Generator.nextOptionalFieldsV1();
    }

    public net.morimekta.test.hazelcast.v1.OptionalFields nextOptionalFieldsV1(boolean setAll) {
        return hazelcastV1Generator.nextOptionalFieldsV1(setAll);
    }

    public net.morimekta.test.hazelcast.v1.OptionalFields nextOptionalFieldsV1(int flags) {
        return hazelcastV1Generator.nextOptionalFieldsV1(flags);
    }

    public net.morimekta.test.hazelcast.v2.OptionalFields nextOptionalFieldsV2() {
        return hazelcastV2Generator.nextOptionalFieldsV2();
    }

    public net.morimekta.test.hazelcast.v2.OptionalFields nextOptionalFieldsV2(boolean setAll) {
        return hazelcastV2Generator.nextOptionalFieldsV2(setAll);
    }

    public net.morimekta.test.hazelcast.v2.OptionalFields nextOptionalFieldsV2(int flags) {
        return hazelcastV2Generator.nextOptionalFieldsV2(flags);
    }

    public net.morimekta.test.hazelcast.v3.OptionalFields nextOptionalFieldsV3() {
        return hazelcastV3Generator.nextOptionalFieldsV3();
    }

    public net.morimekta.test.hazelcast.v3.OptionalFields nextOptionalFieldsV3(boolean setAll) {
        return hazelcastV3Generator.nextOptionalFieldsV3(setAll);
    }

    public net.morimekta.test.hazelcast.v3.OptionalFields nextOptionalFieldsV3(int flags) {
        return hazelcastV3Generator.nextOptionalFieldsV3(flags);
    }

    public net.morimekta.test.hazelcast.v4.OptionalFields nextOptionalFieldsV4() {
        return hazelcastV4Generator.nextOptionalFieldsV4();
    }

    public net.morimekta.test.hazelcast.v4.OptionalFields nextOptionalFieldsV4(boolean setAll) {
        return hazelcastV4Generator.nextOptionalFieldsV4(setAll);
    }

    public net.morimekta.test.hazelcast.v4.OptionalFields nextOptionalFieldsV4(int flags) {
        return hazelcastV4Generator.nextOptionalFieldsV4(flags);
    }

    public net.morimekta.test.hazelcast.v1.OptionalListFields nextOptionalListFieldsV1() {
        return hazelcastV1Generator.nextOptionalListFieldsV1();
    }

    public net.morimekta.test.hazelcast.v1.OptionalListFields nextOptionalListFieldsV1(boolean setAll) {
        return hazelcastV1Generator.nextOptionalListFieldsV1(setAll);
    }

    public net.morimekta.test.hazelcast.v1.OptionalListFields nextOptionalListFieldsV1(int flags) {
        return hazelcastV1Generator.nextOptionalListFieldsV1(flags);
    }

    public net.morimekta.test.hazelcast.v2.OptionalListFields nextOptionalListFieldsV2() {
        return hazelcastV2Generator.nextOptionalListFieldsV2();
    }

    public net.morimekta.test.hazelcast.v2.OptionalListFields nextOptionalListFieldsV2(boolean setAll) {
        return hazelcastV2Generator.nextOptionalListFieldsV2(setAll);
    }

    public net.morimekta.test.hazelcast.v2.OptionalListFields nextOptionalListFieldsV2(int flags) {
        return hazelcastV2Generator.nextOptionalListFieldsV2(flags);
    }

    public net.morimekta.test.hazelcast.v3.OptionalListFields nextOptionalListFieldsV3() {
        return hazelcastV3Generator.nextOptionalListFieldsV3();
    }

    public net.morimekta.test.hazelcast.v3.OptionalListFields nextOptionalListFieldsV3(boolean setAll) {
        return hazelcastV3Generator.nextOptionalListFieldsV3(setAll);
    }

    public net.morimekta.test.hazelcast.v3.OptionalListFields nextOptionalListFieldsV3(int flags) {
        return hazelcastV3Generator.nextOptionalListFieldsV3(flags);
    }

    public net.morimekta.test.hazelcast.v4.OptionalListFields nextOptionalListFieldsV4() {
        return hazelcastV4Generator.nextOptionalListFieldsV4();
    }

    public net.morimekta.test.hazelcast.v4.OptionalListFields nextOptionalListFieldsV4(boolean setAll) {
        return hazelcastV4Generator.nextOptionalListFieldsV4(setAll);
    }

    public net.morimekta.test.hazelcast.v4.OptionalListFields nextOptionalListFieldsV4(int flags) {
        return hazelcastV4Generator.nextOptionalListFieldsV4(flags);
    }

    public net.morimekta.test.hazelcast.v1.OptionalSetFields nextOptionalSetFieldsV1() {
        return hazelcastV1Generator.nextOptionalSetFieldsV1();
    }

    public net.morimekta.test.hazelcast.v1.OptionalSetFields nextOptionalSetFieldsV1(boolean setAll) {
        return hazelcastV1Generator.nextOptionalSetFieldsV1(setAll);
    }

    public net.morimekta.test.hazelcast.v1.OptionalSetFields nextOptionalSetFieldsV1(int flags) {
        return hazelcastV1Generator.nextOptionalSetFieldsV1(flags);
    }

    public net.morimekta.test.hazelcast.v2.OptionalSetFields nextOptionalSetFieldsV2() {
        return hazelcastV2Generator.nextOptionalSetFieldsV2();
    }

    public net.morimekta.test.hazelcast.v2.OptionalSetFields nextOptionalSetFieldsV2(boolean setAll) {
        return hazelcastV2Generator.nextOptionalSetFieldsV2(setAll);
    }

    public net.morimekta.test.hazelcast.v2.OptionalSetFields nextOptionalSetFieldsV2(int flags) {
        return hazelcastV2Generator.nextOptionalSetFieldsV2(flags);
    }

    public net.morimekta.test.hazelcast.v3.OptionalSetFields nextOptionalSetFieldsV3() {
        return hazelcastV3Generator.nextOptionalSetFieldsV3();
    }

    public net.morimekta.test.hazelcast.v3.OptionalSetFields nextOptionalSetFieldsV3(boolean setAll) {
        return hazelcastV3Generator.nextOptionalSetFieldsV3(setAll);
    }

    public net.morimekta.test.hazelcast.v3.OptionalSetFields nextOptionalSetFieldsV3(int flags) {
        return hazelcastV3Generator.nextOptionalSetFieldsV3(flags);
    }

    public net.morimekta.test.hazelcast.v4.OptionalSetFields nextOptionalSetFieldsV4() {
        return hazelcastV4Generator.nextOptionalSetFieldsV4();
    }

    public net.morimekta.test.hazelcast.v4.OptionalSetFields nextOptionalSetFieldsV4(boolean setAll) {
        return hazelcastV4Generator.nextOptionalSetFieldsV4(setAll);
    }

    public net.morimekta.test.hazelcast.v4.OptionalSetFields nextOptionalSetFieldsV4(int flags) {
        return hazelcastV4Generator.nextOptionalSetFieldsV4(flags);
    }

    public net.morimekta.test.hazelcast.v1.OptionalMapFields nextOptionalMapFieldsV1() {
        return hazelcastV1Generator.nextOptionalMapFieldsV1();
    }

    public net.morimekta.test.hazelcast.v1.OptionalMapFields nextOptionalMapFieldsV1(boolean setAll) {
        return hazelcastV1Generator.nextOptionalMapFieldsV1(setAll);
    }

    public net.morimekta.test.hazelcast.v1.OptionalMapFields nextOptionalMapFieldsV1(int flags) {
        return hazelcastV1Generator.nextOptionalMapFieldsV1(flags);
    }

    public net.morimekta.test.hazelcast.v2.OptionalMapFields nextOptionalMapFieldsV2() {
        return hazelcastV2Generator.nextOptionalMapFieldsV2();
    }

    public net.morimekta.test.hazelcast.v2.OptionalMapFields nextOptionalMapFieldsV2(boolean setAll) {
        return hazelcastV2Generator.nextOptionalMapFieldsV2(setAll);
    }

    public net.morimekta.test.hazelcast.v2.OptionalMapFields nextOptionalMapFieldsV2(int flags) {
        return hazelcastV2Generator.nextOptionalMapFieldsV2(flags);
    }

    public net.morimekta.test.hazelcast.v3.OptionalMapFields nextOptionalMapFieldsV3() {
        return hazelcastV3Generator.nextOptionalMapFieldsV3();
    }

    public net.morimekta.test.hazelcast.v3.OptionalMapFields nextOptionalMapFieldsV3(boolean setAll) {
        return hazelcastV3Generator.nextOptionalMapFieldsV3(setAll);
    }

    public net.morimekta.test.hazelcast.v3.OptionalMapFields nextOptionalMapFieldsV3(int flags) {
        return hazelcastV3Generator.nextOptionalMapFieldsV3(flags);
    }

    public net.morimekta.test.hazelcast.v4.OptionalMapFields nextOptionalMapFieldsV4() {
        return hazelcastV4Generator.nextOptionalMapFieldsV4();
    }

    public net.morimekta.test.hazelcast.v4.OptionalMapFields nextOptionalMapFieldsV4(boolean setAll) {
        return hazelcastV4Generator.nextOptionalMapFieldsV4(setAll);
    }

    public net.morimekta.test.hazelcast.v4.OptionalMapFields nextOptionalMapFieldsV4(int flags) {
        return hazelcastV4Generator.nextOptionalMapFieldsV4(flags);
    }

    public net.morimekta.test.hazelcast.v1.OptionalMapListFields nextOptionalMapListFieldsV1() {
        return hazelcastV1Generator.nextOptionalMapListFieldsV1();
    }

    public net.morimekta.test.hazelcast.v1.OptionalMapListFields nextOptionalMapListFieldsV1(boolean setAll) {
        return hazelcastV1Generator.nextOptionalMapListFieldsV1(setAll);
    }

    public net.morimekta.test.hazelcast.v1.OptionalMapListFields nextOptionalMapListFieldsV1(int flags) {
        return hazelcastV1Generator.nextOptionalMapListFieldsV1(flags);
    }

    public net.morimekta.test.hazelcast.v2.OptionalMapListFields nextOptionalMapListFieldsV2() {
        return hazelcastV2Generator.nextOptionalMapListFieldsV2();
    }

    public net.morimekta.test.hazelcast.v2.OptionalMapListFields nextOptionalMapListFieldsV2(boolean setAll) {
        return hazelcastV2Generator.nextOptionalMapListFieldsV2(setAll);
    }

    public net.morimekta.test.hazelcast.v2.OptionalMapListFields nextOptionalMapListFieldsV2(int flags) {
        return hazelcastV2Generator.nextOptionalMapListFieldsV2(flags);
    }

    public net.morimekta.test.hazelcast.v3.OptionalMapListFields nextOptionalMapListFieldsV3() {
        return hazelcastV3Generator.nextOptionalMapListFieldsV3();
    }

    public net.morimekta.test.hazelcast.v3.OptionalMapListFields nextOptionalMapListFieldsV3(boolean setAll) {
        return hazelcastV3Generator.nextOptionalMapListFieldsV3(setAll);
    }

    public net.morimekta.test.hazelcast.v3.OptionalMapListFields nextOptionalMapListFieldsV3(int flags) {
        return hazelcastV3Generator.nextOptionalMapListFieldsV3(flags);
    }

    public net.morimekta.test.hazelcast.v4.OptionalMapListFields nextOptionalMapListFieldsV4() {
        return hazelcastV4Generator.nextOptionalMapListFieldsV4();
    }

    public net.morimekta.test.hazelcast.v4.OptionalMapListFields nextOptionalMapListFieldsV4(boolean setAll) {
        return hazelcastV4Generator.nextOptionalMapListFieldsV4(setAll);
    }

    public net.morimekta.test.hazelcast.v4.OptionalMapListFields nextOptionalMapListFieldsV4(int flags) {
        return hazelcastV4Generator.nextOptionalMapListFieldsV4(flags);
    }

    public net.morimekta.test.hazelcast.v1.OptionalMapSetFields nextOptionalMapSetFieldsV1() {
        return hazelcastV1Generator.nextOptionalMapSetFieldsV1();
    }

    public net.morimekta.test.hazelcast.v1.OptionalMapSetFields nextOptionalMapSetFieldsV1(boolean setAll) {
        return hazelcastV1Generator.nextOptionalMapSetFieldsV1(setAll);
    }

    public net.morimekta.test.hazelcast.v1.OptionalMapSetFields nextOptionalMapSetFieldsV1(int flags) {
        return hazelcastV1Generator.nextOptionalMapSetFieldsV1(flags);
    }

    public net.morimekta.test.hazelcast.v2.OptionalMapSetFields nextOptionalMapSetFieldsV2() {
        return hazelcastV2Generator.nextOptionalMapSetFieldsV2();
    }

    public net.morimekta.test.hazelcast.v2.OptionalMapSetFields nextOptionalMapSetFieldsV2(boolean setAll) {
        return hazelcastV2Generator.nextOptionalMapSetFieldsV2(setAll);
    }

    public net.morimekta.test.hazelcast.v2.OptionalMapSetFields nextOptionalMapSetFieldsV2(int flags) {
        return hazelcastV2Generator.nextOptionalMapSetFieldsV2(flags);
    }

    public net.morimekta.test.hazelcast.v3.OptionalMapSetFields nextOptionalMapSetFieldsV3() {
        return hazelcastV3Generator.nextOptionalMapSetFieldsV3();
    }

    public net.morimekta.test.hazelcast.v3.OptionalMapSetFields nextOptionalMapSetFieldsV3(boolean setAll) {
        return hazelcastV3Generator.nextOptionalMapSetFieldsV3(setAll);
    }

    public net.morimekta.test.hazelcast.v3.OptionalMapSetFields nextOptionalMapSetFieldsV3(int flags) {
        return hazelcastV3Generator.nextOptionalMapSetFieldsV3(flags);
    }

    public net.morimekta.test.hazelcast.v4.OptionalMapSetFields nextOptionalMapSetFieldsV4() {
        return hazelcastV4Generator.nextOptionalMapSetFieldsV4();
    }

    public net.morimekta.test.hazelcast.v4.OptionalMapSetFields nextOptionalMapSetFieldsV4(boolean setAll) {
        return hazelcastV4Generator.nextOptionalMapSetFieldsV4(setAll);
    }

    public net.morimekta.test.hazelcast.v4.OptionalMapSetFields nextOptionalMapSetFieldsV4(int flags) {
        return hazelcastV4Generator.nextOptionalMapSetFieldsV4(flags);
    }

    public net.morimekta.test.hazelcast.v1.UnionFields nextUnionFieldsV1() {
        return hazelcastV1Generator.nextUnionFieldsV1();
    }

    public net.morimekta.test.hazelcast.v1.UnionFields nextUnionFieldsV1(boolean setAll) {
        return hazelcastV1Generator.nextUnionFieldsV1(setAll);
    }

    public net.morimekta.test.hazelcast.v1.UnionFields nextUnionFieldsV1(int flags) {
        return hazelcastV1Generator.nextUnionFieldsV1(flags);
    }

    public net.morimekta.test.hazelcast.v2.UnionFields nextUnionFieldsV2() {
        return hazelcastV2Generator.nextUnionFieldsV2();
    }

    public net.morimekta.test.hazelcast.v2.UnionFields nextUnionFieldsV2(boolean setAll) {
        return hazelcastV2Generator.nextUnionFieldsV2(setAll);
    }

    public net.morimekta.test.hazelcast.v2.UnionFields nextUnionFieldsV2(int flags) {
        return hazelcastV2Generator.nextUnionFieldsV2(flags);
    }

    public net.morimekta.test.hazelcast.v3.UnionFields nextUnionFieldsV3() {
        return hazelcastV3Generator.nextUnionFieldsV3();
    }

    public net.morimekta.test.hazelcast.v3.UnionFields nextUnionFieldsV3(boolean setAll) {
        return hazelcastV3Generator.nextUnionFieldsV3(setAll);
    }

    public net.morimekta.test.hazelcast.v3.UnionFields nextUnionFieldsV3(int flags) {
        return hazelcastV3Generator.nextUnionFieldsV3(flags);
    }

    public net.morimekta.test.hazelcast.v4.UnionFields nextUnionFieldsV4() {
        return hazelcastV4Generator.nextUnionFieldsV4();
    }

    public net.morimekta.test.hazelcast.v4.UnionFields nextUnionFieldsV4(boolean setAll) {
        return hazelcastV4Generator.nextUnionFieldsV4(setAll);
    }

    public net.morimekta.test.hazelcast.v4.UnionFields nextUnionFieldsV4(int flags) {
        return hazelcastV4Generator.nextUnionFieldsV4(flags);
    }

    public net.morimekta.test.hazelcast.v1.AllFields nextAllFieldsV1() {
        return hazelcastV1Generator.nextAllFieldsV1();
    }

    public net.morimekta.test.hazelcast.v2.AllFields nextAllFieldsV2() {
        return hazelcastV2Generator.nextAllFieldsV2();
    }

    public net.morimekta.test.hazelcast.v3.AllFields nextAllFieldsV3() {
        return hazelcastV3Generator.nextAllFieldsV3();
    }

    public net.morimekta.test.hazelcast.v4.AllFields nextAllFieldsV4() {
        return hazelcastV4Generator.nextAllFieldsV4();
    }

    public Random getRandom() {
        return random;
    }
}
