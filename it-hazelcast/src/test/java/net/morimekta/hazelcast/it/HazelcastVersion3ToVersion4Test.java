package net.morimekta.hazelcast.it;

import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.hazelcast.nio.serialization.HazelcastSerializationException;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

/**
 * In version 3 to version 4 we have changed from the structure to remove one field for the OptionalFields and add another
 * as seen below, and OptionalListFields that you can see at {@link net.morimekta.test.hazelcast.v3.OptionalListFields}
 * and {@link net.morimekta.test.hazelcast.v4.OptionalListFields}
 * Below is the thrift definition for version 3:
 * <pre>
 *     {@code
 *     struct OptionalFields {
 *       1: optional bool booleanValue;
 *       2: optional byte byteValue;
 *       3: optional i16 shortValue;
 *       4: optional i32 integerValue;
 *       5: optional i64 longValue;
 *       6: optional double doubleValue;
 *       //    7: optional string stringValue;
 *       8: optional binary binaryValue;
 *       9: optional Value enumValue;
 *       10: optional CompactFields compactValue;
 *       11: optional string anotherStringValue;
 *     } (hazelcast.class.id = "2")
 * </pre>
 *
 * and version 4:
 *
 * <pre>
 *     {@code
 *     struct OptionalFields {
 *       1: optional bool booleanValue;
 *       2: optional byte byteValue;
 *       //    3: optional i16 shortValue;
 *       4: optional i32 integerValue;
 *       5: optional i64 longValue;
 *       6: optional double doubleValue;
 *       //    7: optional string stringValue;
 *       8: optional binary binaryValue;
 *       9: optional Value enumValue;
 *       10: optional CompactFields compactValue;
 *       11: optional string anotherStringValue;
 *       2017: optional i16 anotherShortValue;
 *     } (hazelcast.class.id = "2")
 *     }
 * </pre>
 *
 * Version 3 to Version 4 we add a new short value and remove the old.
 */
public class HazelcastVersion3ToVersion4Test extends GenericMethods {

    static HazelcastInstance instance1;
    static HazelcastInstance instance2;

    @BeforeClass
    public static void setupClass() {
        instance1 = Hazelcast.newHazelcastInstance(getV3Config());
        instance2 = Hazelcast.newHazelcastInstance(getV4Config());
    }

    @AfterClass
    public static void shutDownClass() {
        Hazelcast.shutdownAll();
    }

    @Test
    public void testV3toV4OptionalFieldsAll() throws InterruptedException {
        String mapName = nextString();

        IMap<String, net.morimekta.test.hazelcast.v3.OptionalFields._Builder> writeMap = instance1.getMap(mapName);
        IMap<String, net.morimekta.test.hazelcast.v4.OptionalFields._Builder> readMap = instance2.getMap(mapName);

        net.morimekta.test.hazelcast.v3.OptionalFields expected = generator.nextOptionalFieldsV3(true);

        String key = nextString();
        writeMap.put(key, expected.mutate());

        net.morimekta.test.hazelcast.v4.OptionalFields actual = readMap.get(key)
                                                                       .build();

        assertByField(expected, actual);

        net.morimekta.test.hazelcast.v4.OptionalFields newExpected = actual.mutate()
                .setAnotherStringValue(nextString()).build();

        readMap.put(key, newExpected.mutate());

        net.morimekta.test.hazelcast.v3.OptionalFields newActual = writeMap.get(key)
                                                                       .build();

        assertByField(newExpected, newActual);
    }

    @Test
    public void testV3toV4OptionalFieldsRand() throws InterruptedException {
        String mapName = nextString();

        IMap<String, net.morimekta.test.hazelcast.v3.OptionalFields._Builder> writeMap = instance1.getMap(mapName);
        IMap<String, net.morimekta.test.hazelcast.v4.OptionalFields._Builder> readMap = instance2.getMap(mapName);

        net.morimekta.test.hazelcast.v3.OptionalFields expected = generator.nextOptionalFieldsV3();

        String key = nextString();
        writeMap.put(key, expected.mutate());

        net.morimekta.test.hazelcast.v4.OptionalFields actual = readMap.get(key)
                                                                       .build();

        assertByField(expected, actual);

        net.morimekta.test.hazelcast.v4.OptionalFields newExpected = actual.mutate()
                                                                           .setAnotherStringValue(nextString()).build();

        readMap.put(key, newExpected.mutate());

        net.morimekta.test.hazelcast.v3.OptionalFields newActual = writeMap.get(key)
                                                                           .build();

        assertByField(newExpected, newActual);
    }

    @Test
    public void testV4toV3OptionalFieldsAll() throws InterruptedException {
        String mapName = nextString();

        IMap<String, net.morimekta.test.hazelcast.v4.OptionalFields._Builder> readMap = instance2.getMap(mapName);
        IMap<String, net.morimekta.test.hazelcast.v3.OptionalFields._Builder> writeMap = instance1.getMap(mapName);

        net.morimekta.test.hazelcast.v3.OptionalFields expected = generator.nextOptionalFieldsV3(true);

        String key = nextString();
        writeMap.put(key, expected.mutate());

        net.morimekta.test.hazelcast.v4.OptionalFields actual = readMap.get(key)
                                                                       .build();

        assertByField(expected, actual);

        readMap.put(key, actual.mutate());

        net.morimekta.test.hazelcast.v3.OptionalFields reRead = writeMap.get(key)
                .build();

        assertThat("Expect field to be lost when written from an earlier version", expected, is(not(reRead)));
        assertThat("Expect field to be there from new version", expected.hasShortValue(), is(true));
        assertThat("Expect field to be lost when it's republished from the old version", reRead.hasShortValue(), is(false));

        // "Expect it to still be compatible with the old version."
        assertByField(reRead, actual);
    }

    @Test
    public void testV3WithV4Config() throws InterruptedException {
        exception.expect(HazelcastSerializationException.class);
        exception.expectMessage("Invalid field name: 'shortValue' for ClassDefinition {id: 2, version: 4}");

        String mapName = nextString();

        IMap<String, net.morimekta.test.hazelcast.v3.OptionalFields._Builder> writeMap = instance2.getMap(mapName);

        net.morimekta.test.hazelcast.v3.OptionalFields expected = generator.nextOptionalFieldsV3(true);

        String key = nextString();
        writeMap.put(key, expected.mutate());
    }

    @Test
    public void testV3toV4OptionalListFieldsAll() throws InterruptedException {
        String mapName = nextString();

        IMap<String, net.morimekta.test.hazelcast.v3.OptionalListFields._Builder> writeMap = instance1.getMap(mapName);
        IMap<String, net.morimekta.test.hazelcast.v4.OptionalListFields._Builder> readMap = instance2.getMap(mapName);

        net.morimekta.test.hazelcast.v3.OptionalListFields expected = generator.nextOptionalListFieldsV3(true);

        String key = nextString();
        writeMap.put(key, expected.mutate());

        net.morimekta.test.hazelcast.v4.OptionalListFields actual = readMap.get(key)
                                                                       .build();

        assertByField(expected, actual);

        net.morimekta.test.hazelcast.v4.OptionalListFields newExpected =
                actual.mutate()
                      .setAnotherStringValues(generator.entities.nextStrings()).build();

        readMap.put(key, newExpected.mutate());

        net.morimekta.test.hazelcast.v3.OptionalListFields newActual = writeMap.get(key)
                                                                           .build();

        assertByField(newExpected, newActual);
    }

    @Test
    public void testV3toV4OptionalListFieldsRand() throws InterruptedException {
        String mapName = nextString();

        IMap<String, net.morimekta.test.hazelcast.v3.OptionalListFields._Builder> writeMap = instance1.getMap(mapName);
        IMap<String, net.morimekta.test.hazelcast.v4.OptionalListFields._Builder> readMap = instance2.getMap(mapName);

        net.morimekta.test.hazelcast.v3.OptionalListFields expected = generator.nextOptionalListFieldsV3();

        String key = nextString();
        writeMap.put(key, expected.mutate());

        net.morimekta.test.hazelcast.v4.OptionalListFields actual = readMap.get(key)
                                                                           .build();

        assertByField(expected, actual);

        net.morimekta.test.hazelcast.v4.OptionalListFields newExpected =
                actual.mutate()
                      .setAnotherStringValues(generator.entities.nextStrings()).build();

        readMap.put(key, newExpected.mutate());

        net.morimekta.test.hazelcast.v3.OptionalListFields newActual = writeMap.get(key)
                                                                               .build();

        assertByField(newExpected, newActual);
    }


    @Test
    public void testV3toV4OptionalSetFieldsAll() throws InterruptedException {
        String mapName = nextString();

        IMap<String, net.morimekta.test.hazelcast.v3.OptionalSetFields._Builder> writeMap = instance1.getMap(mapName);
        IMap<String, net.morimekta.test.hazelcast.v4.OptionalSetFields._Builder> readMap = instance2.getMap(mapName);

        net.morimekta.test.hazelcast.v3.OptionalSetFields expected = generator.nextOptionalSetFieldsV3(true);

        String key = nextString();
        writeMap.put(key, expected.mutate());

        net.morimekta.test.hazelcast.v4.OptionalSetFields actual = readMap.get(key)
                                                                           .build();

        assertByField(expected, actual);

        net.morimekta.test.hazelcast.v4.OptionalSetFields newExpected =
                actual.mutate()
                      .setAnotherStringValues(generator.entities.nextStrings()).build();

        readMap.put(key, newExpected.mutate());

        net.morimekta.test.hazelcast.v3.OptionalSetFields newActual = writeMap.get(key)
                                                                               .build();

        assertByField(newExpected, newActual);
    }

    @Test
    public void testV3toV4OptionalSetFieldsRand() throws InterruptedException {
        String mapName = nextString();

        IMap<String, net.morimekta.test.hazelcast.v3.OptionalSetFields._Builder> writeMap = instance1.getMap(mapName);
        IMap<String, net.morimekta.test.hazelcast.v4.OptionalSetFields._Builder> readMap = instance2.getMap(mapName);

        net.morimekta.test.hazelcast.v3.OptionalSetFields expected = generator.nextOptionalSetFieldsV3();

        String key = nextString();
        writeMap.put(key, expected.mutate());

        net.morimekta.test.hazelcast.v4.OptionalSetFields actual = readMap.get(key)
                                                                           .build();

        assertByField(expected, actual);

        net.morimekta.test.hazelcast.v4.OptionalSetFields newExpected =
                actual.mutate()
                      .setAnotherStringValues(generator.entities.nextStrings()).build();

        readMap.put(key, newExpected.mutate());

        net.morimekta.test.hazelcast.v3.OptionalSetFields newActual = writeMap.get(key)
                                                                               .build();

        assertByField(newExpected, newActual);
    }

}
