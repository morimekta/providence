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
 * In version 1 to version 2 we have changed from the structure to add one field for the OptionalFields
 * as seen below, and OptionalListFields that you can see at {@link net.morimekta.test.hazelcast.v1.OptionalListFields}
 * and {@link net.morimekta.test.hazelcast.v2.OptionalListFields}
 * Bwloe is the thrift definition for version 1:
 * <pre>
 *     {@code
 *     struct OptionalFields {
 *       1: optional bool booleanValue;
 *       2: optional byte byteValue;
 *       3: optional i16 shortValue;
 *       4: optional i32 integerValue;
 *       5: optional i64 longValue;
 *       6: optional double doubleValue;
 *       7: optional string stringValue;
 *       8: optional binary binaryValue;
 *       9: optional Value enumValue;
 *       10: optional CompactFields compactValue;
 *     } (hazelcast.class.id = "2")
 *     }
 * </pre>
 *
 * and version 2:
 *
 * <pre>
 *     {@code
 *     struct OptionalFields {
 *       1: optional bool booleanValue;
 *       2: optional byte byteValue;
 *       3: optional i16 shortValue;
 *       4: optional i32 integerValue;
 *       5: optional i64 longValue;
 *       6: optional double doubleValue;
 *       7: optional string stringValue;
 *       8: optional binary binaryValue;
 *       9: optional Value enumValue;
 *       10: optional CompactFields compactValue;
 *       11: optional string anotherStringValue;
 *     } (hazelcast.class.id = "2")
 *     }
 * </pre>
 *
 * Version 1 to Version 2 will just add a new field that will have a default value.
 * Going from version 2 to version 1 will make you loose the field anotherStringValue,
 * So it's important to remove the write functionality for version 1 when rolling out
 * the version 2 hazelcast code. This is something solved programmaticly, so it's up
 * to you.
 */
public class HazelcastVersion1ToVersion2Test extends GenericMethods {

    static HazelcastInstance instance1;
    static HazelcastInstance instance2;

    @BeforeClass
    public static void setupClass() {
        instance1 = Hazelcast.newHazelcastInstance(getV1Config());
        instance2 = Hazelcast.newHazelcastInstance(getV2Config());
    }

    @AfterClass
    public static void shutDownClass() {
        Hazelcast.shutdownAll();
    }

    @Test
    public void testV1toV2OptionalFieldsAll() throws InterruptedException {
        String mapName = nextString();

        IMap<String, net.morimekta.test.hazelcast.v1.OptionalFields._Builder> writeMap = instance1.getMap(mapName);
        IMap<String, net.morimekta.test.hazelcast.v2.OptionalFields._Builder> readMap = instance2.getMap(mapName);

        net.morimekta.test.hazelcast.v1.OptionalFields expected = generator.nextOptionalFieldsV1(true);

        String key = nextString();
        writeMap.put(key, expected.mutate());

        net.morimekta.test.hazelcast.v2.OptionalFields actual = readMap.get(key)
                                                                       .build();

        assertByField(expected, actual);

        net.morimekta.test.hazelcast.v2.OptionalFields newExpected = actual.mutate()
                .setAnotherStringValue(nextString()).build();

        readMap.put(key, newExpected.mutate());

        net.morimekta.test.hazelcast.v1.OptionalFields newActual = writeMap.get(key)
                                                                       .build();

        assertByField(newExpected, newActual);
    }

    @Test
    public void testV1toV2OptionalFieldsRand() throws InterruptedException {
        String mapName = nextString();

        IMap<String, net.morimekta.test.hazelcast.v1.OptionalFields._Builder> writeMap = instance1.getMap(mapName);
        IMap<String, net.morimekta.test.hazelcast.v2.OptionalFields._Builder> readMap = instance2.getMap(mapName);

        net.morimekta.test.hazelcast.v1.OptionalFields expected = generator.nextOptionalFieldsV1();

        String key = nextString();
        writeMap.put(key, expected.mutate());

        net.morimekta.test.hazelcast.v2.OptionalFields actual = readMap.get(key)
                                                                       .build();

        assertByField(expected, actual);

        net.morimekta.test.hazelcast.v2.OptionalFields newExpected = actual.mutate()
                                                                           .setAnotherStringValue(nextString()).build();

        readMap.put(key, newExpected.mutate());

        net.morimekta.test.hazelcast.v1.OptionalFields newActual = writeMap.get(key)
                                                                           .build();

        assertByField(newExpected, newActual);
    }

    @Test
    public void testV2toV1OptionalFieldsAll() throws InterruptedException {
        String mapName = nextString();

        IMap<String, net.morimekta.test.hazelcast.v1.OptionalFields._Builder> readMap = instance1.getMap(mapName);
        IMap<String, net.morimekta.test.hazelcast.v2.OptionalFields._Builder> writeMap = instance2.getMap(mapName);

        net.morimekta.test.hazelcast.v2.OptionalFields expected = generator.nextOptionalFieldsV2(true);

        String key = nextString();
        writeMap.put(key, expected.mutate());

        net.morimekta.test.hazelcast.v1.OptionalFields actual = readMap.get(key)
                                                                       .build();

        assertByField(expected, actual);

        readMap.put(key, actual.mutate());

        net.morimekta.test.hazelcast.v2.OptionalFields reRead = writeMap.get(key)
                .build();

        assertThat("Expect field to be lost when written from an earlier version", expected, is(not(reRead)));
        assertThat("Expect field to be there from new version", expected.hasAnotherStringValue(), is(true));
        assertThat("Expect field to be lost when it's republished from the old version", reRead.hasAnotherStringValue(), is(false));

        // "Expect it to still be compatible with the old version."
        assertByField(reRead, actual);
    }

    @Test
    public void testV2WithV1Config() throws InterruptedException {
        exception.expect(HazelcastSerializationException.class);
        exception.expectMessage("Invalid field name: 'anotherStringValue' for ClassDefinition {id: 2, version: 1}");

        String mapName = nextString();

        IMap<String, net.morimekta.test.hazelcast.v2.OptionalFields._Builder> writeMap = instance1.getMap(mapName);

        net.morimekta.test.hazelcast.v2.OptionalFields expected = generator.nextOptionalFieldsV2(true);

        String key = nextString();
        writeMap.put(key, expected.mutate());
    }

    @Test
    public void testV1toV2OptionalListFieldsAll() throws InterruptedException {
        String mapName = nextString();

        IMap<String, net.morimekta.test.hazelcast.v1.OptionalListFields._Builder> writeMap = instance1.getMap(mapName);
        IMap<String, net.morimekta.test.hazelcast.v2.OptionalListFields._Builder> readMap = instance2.getMap(mapName);

        net.morimekta.test.hazelcast.v1.OptionalListFields expected = generator.nextOptionalListFieldsV1(true);

        String key = nextString();
        writeMap.put(key, expected.mutate());

        net.morimekta.test.hazelcast.v2.OptionalListFields actual = readMap.get(key)
                                                                       .build();

        assertByField(expected, actual);

        net.morimekta.test.hazelcast.v2.OptionalListFields newExpected =
                actual.mutate()
                      .setAnotherStringValues(generator.entities.nextStrings()).build();

        readMap.put(key, newExpected.mutate());

        net.morimekta.test.hazelcast.v1.OptionalListFields newActual = writeMap.get(key)
                                                                           .build();

        assertByField(newExpected, newActual);
    }

    @Test
    public void testV1toV2OptionalListFieldsRand() throws InterruptedException {
        String mapName = nextString();

        IMap<String, net.morimekta.test.hazelcast.v1.OptionalListFields._Builder> writeMap = instance1.getMap(mapName);
        IMap<String, net.morimekta.test.hazelcast.v2.OptionalListFields._Builder> readMap = instance2.getMap(mapName);

        net.morimekta.test.hazelcast.v1.OptionalListFields expected = generator.nextOptionalListFieldsV1();

        String key = nextString();
        writeMap.put(key, expected.mutate());

        net.morimekta.test.hazelcast.v2.OptionalListFields actual = readMap.get(key)
                                                                           .build();

        assertByField(expected, actual);

        net.morimekta.test.hazelcast.v2.OptionalListFields newExpected =
                actual.mutate()
                      .setAnotherStringValues(generator.entities.nextStrings()).build();

        readMap.put(key, newExpected.mutate());

        net.morimekta.test.hazelcast.v1.OptionalListFields newActual = writeMap.get(key)
                                                                               .build();

        assertByField(newExpected, newActual);
    }

    @Test
    public void testV1toV2OptionalSetFieldsAll() throws InterruptedException {
        String mapName = nextString();

        IMap<String, net.morimekta.test.hazelcast.v1.OptionalSetFields._Builder> writeMap = instance1.getMap(mapName);
        IMap<String, net.morimekta.test.hazelcast.v2.OptionalSetFields._Builder> readMap = instance2.getMap(mapName);

        net.morimekta.test.hazelcast.v1.OptionalSetFields expected = generator.nextOptionalSetFieldsV1(true);

        String key = nextString();
        writeMap.put(key, expected.mutate());

        net.morimekta.test.hazelcast.v2.OptionalSetFields actual = readMap.get(key)
                                                                          .build();

        assertByField(expected, actual);

        net.morimekta.test.hazelcast.v2.OptionalSetFields newExpected =
                actual.mutate()
                      .setAnotherStringValues(generator.entities.nextStrings()).build();

        readMap.put(key, newExpected.mutate());

        net.morimekta.test.hazelcast.v1.OptionalSetFields newActual = writeMap.get(key)
                                                                              .build();

        assertByField(newExpected, newActual);
    }

    @Test
    public void testV1toV2OptionalSetFieldsRand() throws InterruptedException {
        String mapName = nextString();

        IMap<String, net.morimekta.test.hazelcast.v1.OptionalSetFields._Builder> writeMap = instance1.getMap(mapName);
        IMap<String, net.morimekta.test.hazelcast.v2.OptionalSetFields._Builder> readMap = instance2.getMap(mapName);

        net.morimekta.test.hazelcast.v1.OptionalSetFields expected = generator.nextOptionalSetFieldsV1();

        String key = nextString();
        writeMap.put(key, expected.mutate());

        net.morimekta.test.hazelcast.v2.OptionalSetFields actual = readMap.get(key)
                                                                           .build();

        assertByField(expected, actual);

        net.morimekta.test.hazelcast.v2.OptionalSetFields newExpected =
                actual.mutate()
                      .setAnotherStringValues(generator.entities.nextStrings()).build();

        readMap.put(key, newExpected.mutate());

        net.morimekta.test.hazelcast.v1.OptionalSetFields newActual = writeMap.get(key)
                                                                               .build();

        assertByField(newExpected, newActual);
    }


}
