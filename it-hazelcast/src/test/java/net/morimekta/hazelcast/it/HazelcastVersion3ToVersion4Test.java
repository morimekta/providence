package net.morimekta.hazelcast.it;

import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.hazelcast.nio.serialization.HazelcastSerializationException;
import org.junit.BeforeClass;
import org.junit.Test;

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
    public static void setUpHazelcast() {
        instance1 = Hazelcast.newHazelcastInstance(getV3Config());
        instance2 = Hazelcast.newHazelcastInstance(getV4Config());
    }

    @Test
    public void testUpgradeIntegrityAll() throws InterruptedException {
        generator.getBaseContext().setDefaultFillRate(1.0);
        assertMapUpgradeIntegrity(instance1, instance2,
                                  net.morimekta.test.hazelcast.v3.OptionalFields.kDescriptor,
                                  net.morimekta.test.hazelcast.v4.OptionalFields.kDescriptor);
        assertMapUpgradeIntegrity(instance1, instance2,
                                  net.morimekta.test.hazelcast.v3.OptionalListFields.kDescriptor,
                                  net.morimekta.test.hazelcast.v4.OptionalListFields.kDescriptor);
        assertMapUpgradeIntegrity(instance1, instance2,
                                  net.morimekta.test.hazelcast.v3.OptionalSetFields.kDescriptor,
                                  net.morimekta.test.hazelcast.v4.OptionalSetFields.kDescriptor);
        assertMapUpgradeIntegrity(instance1, instance2,
                                  net.morimekta.test.hazelcast.v3.OptionalMapFields.kDescriptor,
                                  net.morimekta.test.hazelcast.v4.OptionalMapFields.kDescriptor);
        assertMapUpgradeIntegrity(instance1, instance2,
                                  net.morimekta.test.hazelcast.v3.OptionalMapListFields.kDescriptor,
                                  net.morimekta.test.hazelcast.v4.OptionalMapListFields.kDescriptor);
        assertMapUpgradeIntegrity(instance1, instance2,
                                  net.morimekta.test.hazelcast.v3.OptionalMapSetFields.kDescriptor,
                                  net.morimekta.test.hazelcast.v4.OptionalMapSetFields.kDescriptor);
    }

    @Test
    public void testUpgradeIntegrityRand() throws InterruptedException {
        generator.getBaseContext().setDefaultFillRate(0.5);
        assertMapUpgradeIntegrity(instance1, instance2,
                                  net.morimekta.test.hazelcast.v3.OptionalFields.kDescriptor,
                                  net.morimekta.test.hazelcast.v4.OptionalFields.kDescriptor);
        assertMapUpgradeIntegrity(instance1, instance2,
                                  net.morimekta.test.hazelcast.v3.OptionalListFields.kDescriptor,
                                  net.morimekta.test.hazelcast.v4.OptionalListFields.kDescriptor);
        assertMapUpgradeIntegrity(instance1, instance2,
                                  net.morimekta.test.hazelcast.v3.OptionalSetFields.kDescriptor,
                                  net.morimekta.test.hazelcast.v4.OptionalSetFields.kDescriptor);
        assertMapUpgradeIntegrity(instance1, instance2,
                                  net.morimekta.test.hazelcast.v3.OptionalMapFields.kDescriptor,
                                  net.morimekta.test.hazelcast.v4.OptionalMapFields.kDescriptor);
        assertMapUpgradeIntegrity(instance1, instance2,
                                  net.morimekta.test.hazelcast.v3.OptionalMapListFields.kDescriptor,
                                  net.morimekta.test.hazelcast.v4.OptionalMapListFields.kDescriptor);
        assertMapUpgradeIntegrity(instance1, instance2,
                                  net.morimekta.test.hazelcast.v3.OptionalMapSetFields.kDescriptor,
                                  net.morimekta.test.hazelcast.v4.OptionalMapSetFields.kDescriptor);
    }

    @Test
    public void testV3WithV4Config() throws InterruptedException {
        exception.expect(HazelcastSerializationException.class);
        exception.expectMessage("Invalid field name: 'shortValue' for ClassDefinition {id: 2, version: 4}");

        String mapName = nextString();

        IMap<String, net.morimekta.test.hazelcast.v3.OptionalFields._Builder> writeMap = instance2.getMap(mapName);

        net.morimekta.test.hazelcast.v3.OptionalFields expected = generator.generate(net.morimekta.test.hazelcast.v3.OptionalFields.kDescriptor);

        String key = nextString();
        writeMap.put(key, expected.mutate());
    }


}
