package net.morimekta.providence.gentests;

import net.morimekta.providence.gentests.hazelcast.PortableReader;
import net.morimekta.providence.gentests.hazelcast.PortableWriter;
import net.morimekta.test.hazelcast.Hazelcast_Factory;
import net.morimekta.test.hazelcast.OptionalFields;
import net.morimekta.test.hazelcast.CompactFields;
import net.morimekta.test.hazelcast.OptionalListFields;
import net.morimekta.test.hazelcast.Value;
import net.morimekta.util.Binary;

import com.google.common.primitives.Bytes;
import com.hazelcast.config.Config;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.jfairy.Fairy;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import static org.junit.Assert.assertEquals;

/**
 * Hazelcast serialization and deserialization tests.
 */
@RunWith(Parameterized.class)
public class HazelcastIT {

    OptionalFields optionalFields;
    OptionalListFields optionalListFields;
    private static Random rand = new Random();
    private static Fairy fairy = Fairy.create();

    private static final boolean REPEAT_TEST      = false;
    private static final boolean IGNORE_HAZELCAST = true;
    private static final int     NO_OF_REPEATS    = (REPEAT_TEST ? 1000 : 1);

    private static final int MAX_LIST_LENGTH = Byte.MAX_VALUE;

    private static final int POS_0 = 0x00000001;
    private static final int POS_1 = 0x00000002;
    private static final int POS_2 = 0x00000004;
    private static final int POS_3 = 0x00000008;
    private static final int POS_4 = 0x00000010;
    private static final int POS_5 = 0x00000020;
    private static final int POS_6 = 0x00000040;
    private static final int POS_7 = 0x00000080;
    private static final int POS_8 = 0x00000100;
    private static final int POS_9 = 0x00000200;
    private static final int POS_10 = 0x00000400;
    private static final int POS_11 = 0x00000800;
    private static final int POS_12 = 0x00001000;
    private static final int POS_13 = 0x00002000;
    private static final int POS_14 = 0x00004000;
    private static final int POS_15 = 0x00008000;
    private static final int POS_16 = 0x00010000;
    private static final int POS_17 = 0x00020000;

    static HazelcastInstance instance1;
    static HazelcastInstance instance2;
    static Config config1;

    @Before
    public void setup() {
        int flags = rand.nextInt();
        byte[] bytes = new byte[rand.nextInt(Byte.MAX_VALUE)];
        rand.nextBytes(bytes);
        optionalFields = new OptionalFields(0 < (POS_0 & flags) ? rand.nextBoolean() : null,
                                            0 < (POS_1 & flags) ? (byte)rand.nextInt(Byte.MAX_VALUE) : null,
                                            0 < (POS_2 & flags) ? (short)rand.nextInt(Short.MAX_VALUE) : null,
                                            0 < (POS_3 & flags) ? rand.nextInt() : null,
                                            0 < (POS_4 & flags) ? rand.nextLong() : null,
                                            0 < (POS_5 & flags) ? rand.nextDouble() : null,
                                            0 < (POS_6 & flags) ? fairy.textProducer().loremIpsum() : null,
                                            0 < (POS_7 & flags) ? Binary.wrap(bytes) : null,
                                            0 < (POS_8 & flags) ? Value.forValue(rand.nextInt(Value.values().length)) : null,
                                            0 < (POS_9 & flags) ? genCompactFields() : null);
        optionalListFields = new OptionalListFields(0 < (POS_10 & flags) ? genBooleanList(rand.nextInt(MAX_LIST_LENGTH)) : null,
                                                    0 < (POS_11 & flags) ? genByteList(rand.nextInt(MAX_LIST_LENGTH)) : null,
                                                    0 < (POS_12 & flags) ? genShortList(rand.nextInt(MAX_LIST_LENGTH)) : null,
                                                    0 < (POS_13 & flags) ? genIntList(rand.nextInt(MAX_LIST_LENGTH)) : null,
                                                    0 < (POS_14 & flags) ? genLongList(rand.nextInt(MAX_LIST_LENGTH)) : null,
                                                    0 < (POS_15 & flags) ? genDoubleList(rand.nextInt(MAX_LIST_LENGTH)) : null,
                                                    0 < (POS_16 & flags) ? genStringList(rand.nextInt(MAX_LIST_LENGTH)) : null,
                                                    0 < (POS_17 & flags) ? genCompactList(rand.nextInt(MAX_LIST_LENGTH)) : null);

    }

    @BeforeClass
    public static void setupClass() {
        config1 = new Config();
        Hazelcast_Factory.populateConfig(config1);
        config1.getSerializationConfig().setPortableVersion(1);
        instance1 = Hazelcast.newHazelcastInstance(config1);
        instance2 = Hazelcast.newHazelcastInstance(config1);
    }

    @AfterClass
    public static void afterClass() {
        instance1.shutdown();
        instance2.shutdown();
    }

    @Parameterized.Parameters
    public static List<Object[]> data() {
        return Arrays.asList(new Object[NO_OF_REPEATS][0]);
    }

    public static CompactFields genCompactFields() {
        return new CompactFields(
                fairy.textProducer().loremIpsum(),
                rand.nextInt(),
                rand.nextBoolean() ? fairy.textProducer().loremIpsum() : null);
    }

    public static List<Boolean> genBooleanList(int no) {
        List<Boolean> result = new ArrayList<>();
        for( int i = 0; i < no; i++ ) {
            result.add(rand.nextBoolean());
        }
        return result;
    }

    public static List<Byte> genByteList(int no) {
        byte[] items = new byte[no];
        rand.nextBytes(items);
        return Bytes.asList(items);
    }

    public static List<Short> genShortList(int no) {
        List<Short> result = new ArrayList<>();
        for( int i = 0; i < no; i++ ) {
            result.add(((Integer)rand.nextInt(Short.MAX_VALUE)).shortValue());
        }
        return result;
    }

    public static List<Integer> genIntList(int no) {
        List<Integer> result = new ArrayList<>();
        for( int i = 0; i < no; i++ ) {
            result.add(rand.nextInt());
        }
        return result;
    }

    public static List<Long> genLongList(int no) {
        List<Long> result = new ArrayList<>();
        for( int i = 0; i < no; i++ ) {
            result.add(rand.nextLong());
        }
        return result;
    }

    public static List<Double> genDoubleList(int no) {
        List<Double> result = new ArrayList<>();
        for( int i = 0; i < no; i++ ) {
            result.add(rand.nextDouble());
        }
        return result;
    }

    public static List<String> genStringList(int no) {
        List<String> result = new ArrayList<>();
        for( int i = 0; i < no; i++ ) {
            result.add(fairy.textProducer().loremIpsum());
        }
        return result;
    }

    public static List<CompactFields> genCompactList(int no) {
        List<CompactFields> result = new ArrayList<>();
        for( int i = 0; i < no; i++ ) {
            result.add(genCompactFields());
        }
        return result;
    }

    @Test
    public void testBaseEntities() throws IOException {
        OptionalFields._Builder testObject = new OptionalFields._Builder(optionalFields);
        PortableWriter writer = new PortableWriter();
        testObject.writePortable(writer);
        PortableReader reader = new PortableReader(writer);
        OptionalFields._Builder actual = new OptionalFields._Builder();
        actual.readPortable(reader);
        assertEquals(optionalFields, actual.build());
    }

    @Test
    public void testListEntities() throws IOException {
        OptionalListFields._Builder testObject = new OptionalListFields._Builder(optionalListFields);
        PortableWriter writer = new PortableWriter();
        testObject.writePortable(writer);
        PortableReader reader = new PortableReader(writer);
        OptionalListFields._Builder actual = new OptionalListFields._Builder();
        actual.readPortable(reader);
        assertEquals(testObject.build(), actual.build());
    }

    @Test
    public void testBaseEntitiesHazelcast() {
        if( IGNORE_HAZELCAST ) return;
        String key = fairy.textProducer().randomString(rand.nextInt(Byte.MAX_VALUE) + 1);
        String map = fairy.textProducer().randomString(rand.nextInt(Byte.MAX_VALUE) + 1);
        IMap<String, OptionalFields._Builder> writeMap = instance1.getMap(map);
        IMap<String, OptionalFields._Builder> readMap = instance2.getMap(map);

        OptionalFields._Builder expected = new OptionalFields._Builder(optionalFields);

        writeMap.set(key, expected);
        OptionalFields._Builder actual = readMap.get(key);

        assertEquals(expected.build(), actual.build());
    }

    @Test
    public void testListEntitiesHazelcast() {
        if( IGNORE_HAZELCAST ) return;
        String key = fairy.textProducer().randomString(rand.nextInt(Byte.MAX_VALUE) + 1);
        String map = fairy.textProducer().randomString(rand.nextInt(Byte.MAX_VALUE) + 1);
        IMap<String, OptionalListFields._Builder> writeMap = instance1.getMap(map);
        IMap<String, OptionalListFields._Builder> readMap = instance2.getMap(map);

        OptionalListFields._Builder expected = new OptionalListFields._Builder(optionalListFields);

        writeMap.set(key, expected);
        OptionalListFields._Builder actual = readMap.get(key);

        assertEquals(expected.build(), actual.build());
    }



}
