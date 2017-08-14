package net.morimekta.hazelcast.it;

import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.hazelcast.nio.serialization.HazelcastSerializationException;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

/**
 * In version 2 to version 3 we have changed from the structure to remove one field for the PortableFields
 * as seen below, and PortableListFields that you can see at {@link net.morimekta.test.hazelcast.v3.PortableListFields}
 * and {@link net.morimekta.test.hazelcast.v3.PortableListFields}
 * Below is the thrift definition for version 2:
 * <pre>
 *     {@code
 *     struct PortableFields {
 *       1: Portable bool booleanValue;
 *       2: Portable byte byteValue;
 *       3: Portable i16 shortValue;
 *       4: Portable i32 integerValue;
 *       5: Portable i64 longValue;
 *       6: Portable double doubleValue;
 *       7: Portable string stringValue;
 *       8: Portable binary binaryValue;
 *       9: Portable Value enumValue;
 *       10: Portable CompactFields compactValue;
 *       11: Portable string anotherStringValue;
 *     } (hazelcast.class.id = "2")
 *     }
 * </pre>
 *
 * and version 3:
 *
 * <pre>
 *     {@code
 *     struct PortableFields {
 *       1: Portable bool booleanValue;
 *       2: Portable byte byteValue;
 *       3: Portable i16 shortValue;
 *       4: Portable i32 integerValue;
 *       5: Portable i64 longValue;
 *       6: Portable double doubleValue;
 *       //    7: Portable string stringValue;
 *       8: Portable binary binaryValue;
 *       9: Portable Value enumValue;
 *       10: Portable CompactFields compactValue;
 *       11: Portable string anotherStringValue;
 *     } (hazelcast.class.id = "2")
 *     }
 * </pre>
 *
 * Version 2 to Version 3 we remove the original stringValue from the struct.
 */
public class HazelcastVersion2ToVersion3Test extends GenericMethods {
    private static HazelcastInstance instance1;
    private static HazelcastInstance instance2;

    @BeforeClass
    public static void setUpHazelcast() {
        instance1 = Hazelcast.newHazelcastInstance(getV2Config());
        instance2 = Hazelcast.newHazelcastInstance(getV3Config());
    }

    @AfterClass
    public static void tearDownHazelcast() {
        instance1.shutdown();
        instance2.shutdown();
    }

    @Test
    public void testUpgradeIntegrityAll() throws InterruptedException {
        generator.getBaseContext().setDefaultFillRate(1.0);
        assertMapUpgradeIntegrity(instance1, instance2,
                                  net.morimekta.test.hazelcast.v2.PortableFields.kDescriptor,
                                  net.morimekta.test.hazelcast.v3.PortableFields.kDescriptor);
    }

    @Test
    public void testUpgradeIntegrityAll_list() throws InterruptedException {
        generator.getBaseContext().setDefaultFillRate(1.0);
        assertMapUpgradeIntegrity(instance1, instance2,
                                  net.morimekta.test.hazelcast.v2.PortableListFields.kDescriptor,
                                  net.morimekta.test.hazelcast.v3.PortableListFields.kDescriptor);
    }

    @Test
    public void testUpgradeIntegrityAll_set() throws InterruptedException {
        generator.getBaseContext().setDefaultFillRate(1.0);
        assertMapUpgradeIntegrity(instance1, instance2,
                                  net.morimekta.test.hazelcast.v2.PortableSetFields.kDescriptor,
                                  net.morimekta.test.hazelcast.v3.PortableSetFields.kDescriptor);
    }

    @Test
    public void testUpgradeIntegrityAll_map() throws InterruptedException {
        generator.getBaseContext().setDefaultFillRate(1.0);
        assertMapUpgradeIntegrity(instance1, instance2,
                                  net.morimekta.test.hazelcast.v2.PortableMapFields.kDescriptor,
                                  net.morimekta.test.hazelcast.v3.PortableMapFields.kDescriptor);
    }

    @Test
    public void testUpgradeIntegrityAll_maplist() throws InterruptedException {
        generator.getBaseContext().setDefaultFillRate(1.0);
        assertMapUpgradeIntegrity(instance1, instance2,
                                  net.morimekta.test.hazelcast.v2.PortableMapListFields.kDescriptor,
                                  net.morimekta.test.hazelcast.v3.PortableMapListFields.kDescriptor);
    }

    @Test
    public void testUpgradeIntegrityAll_mapset() throws InterruptedException {
        generator.getBaseContext().setDefaultFillRate(1.0);
        assertMapUpgradeIntegrity(instance1, instance2,
                                  net.morimekta.test.hazelcast.v2.PortableMapSetFields.kDescriptor,
                                  net.morimekta.test.hazelcast.v3.PortableMapSetFields.kDescriptor);
    }

    @Test
    public void testUpgradeIntegrityRand() throws InterruptedException {
        generator.getBaseContext().setDefaultFillRate(0.5);
        assertMapUpgradeIntegrity(instance1, instance2,
                                  net.morimekta.test.hazelcast.v2.PortableFields.kDescriptor,
                                  net.morimekta.test.hazelcast.v3.PortableFields.kDescriptor);
    }

    @Test
    public void testUpgradeIntegrityRand_list() throws InterruptedException {
        generator.getBaseContext().setDefaultFillRate(0.5);
        assertMapUpgradeIntegrity(instance1, instance2,
                                  net.morimekta.test.hazelcast.v2.PortableListFields.kDescriptor,
                                  net.morimekta.test.hazelcast.v3.PortableListFields.kDescriptor);
    }

    @Test
    public void testUpgradeIntegrityRand_set() throws InterruptedException {
        generator.getBaseContext().setDefaultFillRate(0.5);
        assertMapUpgradeIntegrity(instance1, instance2,
                                  net.morimekta.test.hazelcast.v2.PortableSetFields.kDescriptor,
                                  net.morimekta.test.hazelcast.v3.PortableSetFields.kDescriptor);
    }

    @Test
    public void testUpgradeIntegrityRand_map() throws InterruptedException {
        generator.getBaseContext().setDefaultFillRate(0.5);
        assertMapUpgradeIntegrity(instance1, instance2,
                                  net.morimekta.test.hazelcast.v2.PortableMapFields.kDescriptor,
                                  net.morimekta.test.hazelcast.v3.PortableMapFields.kDescriptor);
    }

    @Test
    public void testUpgradeIntegrityRand_maplist() throws InterruptedException {
        generator.getBaseContext().setDefaultFillRate(0.5);
        assertMapUpgradeIntegrity(instance1, instance2,
                                  net.morimekta.test.hazelcast.v2.PortableMapListFields.kDescriptor,
                                  net.morimekta.test.hazelcast.v3.PortableMapListFields.kDescriptor);
    }

    @Test
    public void testUpgradeIntegrityRand_mapset() throws InterruptedException {
        generator.getBaseContext().setDefaultFillRate(0.5);
        assertMapUpgradeIntegrity(instance1, instance2,
                                  net.morimekta.test.hazelcast.v2.PortableMapSetFields.kDescriptor,
                                  net.morimekta.test.hazelcast.v3.PortableMapSetFields.kDescriptor);
    }

    @Test
    public void testV2WithV3Config() throws InterruptedException {
        try {
            String mapName = nextString();

            IMap<String, net.morimekta.test.hazelcast.v2.PortableFields._Builder> writeMap = instance2.getMap(mapName);

            generator.getBaseContext()
                     .setDefaultFillRate(1.0);
            net.morimekta.test.hazelcast.v2.PortableFields expected = generator.generate(net.morimekta.test.hazelcast.v2.PortableFields.kDescriptor);

            String key = nextString();
            writeMap.put(key, expected.mutate());

            fail("no exception");
        } catch (HazelcastSerializationException e) {
            assertThat(e.getCause(), is(instanceOf(HazelcastSerializationException.class)));
            assertThat(e.getCause().getMessage(), is("Invalid field name: 'enumValue' for ClassDefinition {id: 2, version: 3}"));
        }
    }
}
