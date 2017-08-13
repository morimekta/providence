package net.morimekta.hazelcast.it;

import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.hazelcast.nio.serialization.HazelcastSerializationException;
import org.junit.BeforeClass;
import org.junit.Test;

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
    public static void setUpHazelcast() {
        instance1 = Hazelcast.newHazelcastInstance(getV1Config());
        instance2 = Hazelcast.newHazelcastInstance(getV2Config());
    }

    @Test
    public void testUpgradeIntegrityAll() throws InterruptedException {
        generator.getBaseContext().setDefaultFillRate(1.0);
        assertMapUpgradeIntegrity(instance1, instance2,
                                  net.morimekta.test.hazelcast.v1.OptionalFields.kDescriptor,
                                  net.morimekta.test.hazelcast.v2.OptionalFields.kDescriptor);
        assertMapUpgradeIntegrity(instance1, instance2,
                                  net.morimekta.test.hazelcast.v1.OptionalListFields.kDescriptor,
                                  net.morimekta.test.hazelcast.v2.OptionalListFields.kDescriptor);
        assertMapUpgradeIntegrity(instance1, instance2,
                                  net.morimekta.test.hazelcast.v1.OptionalSetFields.kDescriptor,
                                  net.morimekta.test.hazelcast.v2.OptionalSetFields.kDescriptor);
        assertMapUpgradeIntegrity(instance1, instance2,
                                  net.morimekta.test.hazelcast.v1.OptionalMapFields.kDescriptor,
                                  net.morimekta.test.hazelcast.v2.OptionalMapFields.kDescriptor);
        assertMapUpgradeIntegrity(instance1, instance2,
                                  net.morimekta.test.hazelcast.v1.OptionalMapListFields.kDescriptor,
                                  net.morimekta.test.hazelcast.v2.OptionalMapListFields.kDescriptor);
        assertMapUpgradeIntegrity(instance1, instance2,
                                  net.morimekta.test.hazelcast.v1.OptionalMapSetFields.kDescriptor,
                                  net.morimekta.test.hazelcast.v2.OptionalMapSetFields.kDescriptor);
    }

    @Test
    public void testUpgradeIntegrityRand() throws InterruptedException {
        generator.getBaseContext().setDefaultFillRate(0.5);
        assertMapUpgradeIntegrity(instance1, instance2,
                                  net.morimekta.test.hazelcast.v1.OptionalFields.kDescriptor,
                                  net.morimekta.test.hazelcast.v2.OptionalFields.kDescriptor);
        assertMapUpgradeIntegrity(instance1, instance2,
                                  net.morimekta.test.hazelcast.v1.OptionalListFields.kDescriptor,
                                  net.morimekta.test.hazelcast.v2.OptionalListFields.kDescriptor);
        assertMapUpgradeIntegrity(instance1, instance2,
                                  net.morimekta.test.hazelcast.v1.OptionalSetFields.kDescriptor,
                                  net.morimekta.test.hazelcast.v2.OptionalSetFields.kDescriptor);
        assertMapUpgradeIntegrity(instance1, instance2,
                                  net.morimekta.test.hazelcast.v1.OptionalMapFields.kDescriptor,
                                  net.morimekta.test.hazelcast.v2.OptionalMapFields.kDescriptor);
        assertMapUpgradeIntegrity(instance1, instance2,
                                  net.morimekta.test.hazelcast.v1.OptionalMapListFields.kDescriptor,
                                  net.morimekta.test.hazelcast.v2.OptionalMapListFields.kDescriptor);
        assertMapUpgradeIntegrity(instance1, instance2,
                                  net.morimekta.test.hazelcast.v1.OptionalMapSetFields.kDescriptor,
                                  net.morimekta.test.hazelcast.v2.OptionalMapSetFields.kDescriptor);
    }

    @Test
    public void testV2WithV1Config() throws InterruptedException {
        exception.expect(HazelcastSerializationException.class);
        exception.expectMessage("Invalid field name: 'anotherStringValue' for ClassDefinition {id: 2, version: 1}");

        String mapName = nextString();

        IMap<String, net.morimekta.test.hazelcast.v2.OptionalFields._Builder> writeMap = instance1.getMap(mapName);

        net.morimekta.test.hazelcast.v2.OptionalFields expected = generator.generate(net.morimekta.test.hazelcast.v2.OptionalFields.kDescriptor);

        String key = nextString();
        writeMap.put(key, expected.mutate());
    }
}
