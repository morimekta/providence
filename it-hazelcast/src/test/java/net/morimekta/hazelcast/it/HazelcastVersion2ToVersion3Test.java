package net.morimekta.hazelcast.it;

import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.hazelcast.nio.serialization.HazelcastSerializationException;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

/**
 * In version 2 to version 3 we have changed from the structure to remove one field for the OptionalFields
 * as seen below, and OptionalListFields that you can see at {@link net.morimekta.test.hazelcast.v2.OptionalListFields}
 * and {@link net.morimekta.test.hazelcast.v3.OptionalListFields}
 * Below is the thrift definition for version 2:
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
 * and version 3:
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
 *       //    7: optional string stringValue;
 *       8: optional binary binaryValue;
 *       9: optional Value enumValue;
 *       10: optional CompactFields compactValue;
 *       11: optional string anotherStringValue;
 *     } (hazelcast.class.id = "2")
 *     }
 * </pre>
 *
 * Version 2 to Version 3 we remove the original stringValue from the struct.
 */
public class HazelcastVersion2ToVersion3Test extends GenericMethods {

    @Test
    public void testV2toV3OptionalFieldsAll() throws InterruptedException {
        String mapName = nextString();
        HazelcastInstance instance1 = Hazelcast.newHazelcastInstance(getV2Config());
        HazelcastInstance instance2 = Hazelcast.newHazelcastInstance(getV3Config());

        IMap<String, net.morimekta.test.hazelcast.v2.OptionalFields._Builder> writeMap = instance1.getMap(mapName);
        IMap<String, net.morimekta.test.hazelcast.v3.OptionalFields._Builder> readMap = instance2.getMap(mapName);

        net.morimekta.test.hazelcast.v2.OptionalFields expected = generator.nextOptionalFieldsV2(true);

        String key = nextString();
        writeMap.put(key, expected.mutate());

        net.morimekta.test.hazelcast.v3.OptionalFields actual = readMap.get(key)
                                                                       .build();

        assertByField(expected, actual);

        net.morimekta.test.hazelcast.v3.OptionalFields newExpected = actual.mutate()
                .setAnotherStringValue(nextString()).build();

        readMap.put(key, newExpected.mutate());

        net.morimekta.test.hazelcast.v2.OptionalFields newActual = writeMap.get(key)
                                                                       .build();

        assertByField(newExpected, newActual);
    }

    @Test
    public void testV2toV3OptionalFieldsRand() throws InterruptedException {
        String mapName = nextString();
        HazelcastInstance instance1 = Hazelcast.newHazelcastInstance(getV2Config());
        HazelcastInstance instance2 = Hazelcast.newHazelcastInstance(getV3Config());

        IMap<String, net.morimekta.test.hazelcast.v2.OptionalFields._Builder> writeMap = instance1.getMap(mapName);
        IMap<String, net.morimekta.test.hazelcast.v3.OptionalFields._Builder> readMap = instance2.getMap(mapName);

        net.morimekta.test.hazelcast.v2.OptionalFields expected = generator.nextOptionalFieldsV2();

        String key = nextString();
        writeMap.put(key, expected.mutate());

        net.morimekta.test.hazelcast.v3.OptionalFields actual = readMap.get(key)
                                                                       .build();

        assertByField(expected, actual);

        net.morimekta.test.hazelcast.v3.OptionalFields newExpected = actual.mutate()
                                                                           .setAnotherStringValue(nextString()).build();

        readMap.put(key, newExpected.mutate());

        net.morimekta.test.hazelcast.v2.OptionalFields newActual = writeMap.get(key)
                                                                           .build();

        assertByField(newExpected, newActual);
    }

    @Test
    public void testV3toV2OptionalFieldsAll() throws InterruptedException {
        String mapName = nextString();
        HazelcastInstance instance1 = Hazelcast.newHazelcastInstance(getV3Config());
        HazelcastInstance instance2 = Hazelcast.newHazelcastInstance(getV2Config());

        IMap<String, net.morimekta.test.hazelcast.v3.OptionalFields._Builder> readMap = instance1.getMap(mapName);
        IMap<String, net.morimekta.test.hazelcast.v2.OptionalFields._Builder> writeMap = instance2.getMap(mapName);

        net.morimekta.test.hazelcast.v2.OptionalFields expected = generator.nextOptionalFieldsV2(true);

        String key = nextString();
        writeMap.put(key, expected.mutate());

        net.morimekta.test.hazelcast.v3.OptionalFields actual = readMap.get(key)
                                                                       .build();

        assertByField(expected, actual);

        readMap.put(key, actual.mutate());

        net.morimekta.test.hazelcast.v2.OptionalFields reRead = writeMap.get(key)
                .build();

        assertThat("Expect field to be lost when written from an earlier version", expected, is(not(reRead)));
        assertThat("Expect field to be there from new version", expected.hasStringValue(), is(true));
        assertThat("Expect field to be lost when it's republished from the old version", reRead.hasStringValue(), is(false));

        // "Expect it to still be compatible with the old version."
        assertByField(reRead, actual);
    }

    @Test
    public void testV2WithV3Config() throws InterruptedException {
        exception.expect(HazelcastSerializationException.class);
        exception.expectMessage("Invalid field name: 'stringValue' for ClassDefinition {id: 2, version: 3}");

        String mapName = nextString();
        HazelcastInstance instance1 = Hazelcast.newHazelcastInstance(getV3Config());

        IMap<String, net.morimekta.test.hazelcast.v2.OptionalFields._Builder> writeMap = instance1.getMap(mapName);

        net.morimekta.test.hazelcast.v2.OptionalFields expected = generator.nextOptionalFieldsV2(true);

        String key = nextString();
        writeMap.put(key, expected.mutate());
    }

    @Test
    public void testV2toV3OptionalListFieldsAll() throws InterruptedException {
        String mapName = nextString();
        HazelcastInstance instance1 = Hazelcast.newHazelcastInstance(getV2Config());
        HazelcastInstance instance2 = Hazelcast.newHazelcastInstance(getV3Config());

        IMap<String, net.morimekta.test.hazelcast.v2.OptionalListFields._Builder> writeMap = instance1.getMap(mapName);
        IMap<String, net.morimekta.test.hazelcast.v3.OptionalListFields._Builder> readMap = instance2.getMap(mapName);

        net.morimekta.test.hazelcast.v2.OptionalListFields expected = generator.nextOptionalListFieldsV2(true);

        String key = nextString();
        writeMap.put(key, expected.mutate());

        net.morimekta.test.hazelcast.v3.OptionalListFields actual = readMap.get(key)
                                                                       .build();

        assertByField(expected, actual);

        net.morimekta.test.hazelcast.v3.OptionalListFields newExpected =
                actual.mutate()
                      .setAnotherStringValues(generator.entities.nextStrings()).build();

        readMap.put(key, newExpected.mutate());

        net.morimekta.test.hazelcast.v2.OptionalListFields newActual = writeMap.get(key)
                                                                           .build();

        assertByField(newExpected, newActual);
    }

    @Test
    public void testV2toV3OptionalListFieldsRand() throws InterruptedException {
        String mapName = nextString();
        HazelcastInstance instance1 = Hazelcast.newHazelcastInstance(getV2Config());
        HazelcastInstance instance2 = Hazelcast.newHazelcastInstance(getV3Config());

        IMap<String, net.morimekta.test.hazelcast.v2.OptionalListFields._Builder> writeMap = instance1.getMap(mapName);
        IMap<String, net.morimekta.test.hazelcast.v3.OptionalListFields._Builder> readMap = instance2.getMap(mapName);

        net.morimekta.test.hazelcast.v2.OptionalListFields expected = generator.nextOptionalListFieldsV2();

        String key = nextString();
        writeMap.put(key, expected.mutate());

        net.morimekta.test.hazelcast.v3.OptionalListFields actual = readMap.get(key)
                                                                           .build();

        assertByField(expected, actual);

        net.morimekta.test.hazelcast.v3.OptionalListFields newExpected =
                actual.mutate()
                      .setAnotherStringValues(generator.entities.nextStrings()).build();

        readMap.put(key, newExpected.mutate());

        net.morimekta.test.hazelcast.v2.OptionalListFields newActual = writeMap.get(key)
                                                                               .build();

        assertByField(newExpected, newActual);
    }


    @Test
    public void testV2toV3OptionalSetFieldsAll() throws InterruptedException {
        String mapName = nextString();
        HazelcastInstance instance1 = Hazelcast.newHazelcastInstance(getV2Config());
        HazelcastInstance instance2 = Hazelcast.newHazelcastInstance(getV3Config());

        IMap<String, net.morimekta.test.hazelcast.v2.OptionalSetFields._Builder> writeMap = instance1.getMap(mapName);
        IMap<String, net.morimekta.test.hazelcast.v3.OptionalSetFields._Builder> readMap = instance2.getMap(mapName);

        net.morimekta.test.hazelcast.v2.OptionalSetFields expected = generator.nextOptionalSetFieldsV2(true);

        String key = nextString();
        writeMap.put(key, expected.mutate());

        net.morimekta.test.hazelcast.v3.OptionalSetFields actual = readMap.get(key)
                                                                           .build();

        assertByField(expected, actual);

        net.morimekta.test.hazelcast.v3.OptionalSetFields newExpected =
                actual.mutate()
                      .setAnotherStringValues(generator.entities.nextStrings()).build();

        readMap.put(key, newExpected.mutate());

        net.morimekta.test.hazelcast.v2.OptionalSetFields newActual = writeMap.get(key)
                                                                               .build();

        assertByField(newExpected, newActual);
    }

    @Test
    public void testV2toV3OptionalSetFieldsRand() throws InterruptedException {
        String mapName = nextString();
        HazelcastInstance instance1 = Hazelcast.newHazelcastInstance(getV2Config());
        HazelcastInstance instance2 = Hazelcast.newHazelcastInstance(getV3Config());

        IMap<String, net.morimekta.test.hazelcast.v2.OptionalSetFields._Builder> writeMap = instance1.getMap(mapName);
        IMap<String, net.morimekta.test.hazelcast.v3.OptionalSetFields._Builder> readMap = instance2.getMap(mapName);

        net.morimekta.test.hazelcast.v2.OptionalSetFields expected = generator.nextOptionalSetFieldsV2();

        String key = nextString();
        writeMap.put(key, expected.mutate());

        net.morimekta.test.hazelcast.v3.OptionalSetFields actual = readMap.get(key)
                                                                           .build();

        assertByField(expected, actual);

        net.morimekta.test.hazelcast.v3.OptionalSetFields newExpected =
                actual.mutate()
                      .setAnotherStringValues(generator.entities.nextStrings()).build();

        readMap.put(key, newExpected.mutate());

        net.morimekta.test.hazelcast.v2.OptionalSetFields newActual = writeMap.get(key)
                                                                               .build();

        assertByField(newExpected, newActual);
    }

}
