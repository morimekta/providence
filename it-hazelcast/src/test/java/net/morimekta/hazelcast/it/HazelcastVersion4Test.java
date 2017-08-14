package net.morimekta.hazelcast.it;

import net.morimekta.test.hazelcast.v4.PortableFields;
import net.morimekta.test.hazelcast.v4.PortableListFields;
import net.morimekta.test.hazelcast.v4.PortableMapFields;
import net.morimekta.test.hazelcast.v4.PortableMapListFields;
import net.morimekta.test.hazelcast.v4.PortableMapSetFields;
import net.morimekta.test.hazelcast.v4.PortableSetFields;

import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * TBD
 */
public class HazelcastVersion4Test extends GenericMethods {
    private static HazelcastInstance instance1;
    private static HazelcastInstance instance2;

    @BeforeClass
    public static void setUpHazelcast() {
        instance1 = Hazelcast.newHazelcastInstance(getV4Config());
        instance2 = Hazelcast.newHazelcastInstance(getV4Config());
    }

    @AfterClass
    public static void tearDownHazelcast() {
        instance1.shutdown();
        instance2.shutdown();
    }

    @Test
    public void testMapIntegrityAll() throws InterruptedException {
        generator.getBaseContext().setDefaultFillRate(1.0);
        assertMapIntegrity(instance1, instance2, PortableFields.kDescriptor);
    }

    @Test
    public void testMapIntegrityAll_list() throws InterruptedException {
        generator.getBaseContext().setDefaultFillRate(1.0);
        assertMapIntegrity(instance1, instance2, PortableListFields.kDescriptor);
    }

    @Test
    public void testMapIntegrityAll_set() throws InterruptedException {
        generator.getBaseContext().setDefaultFillRate(1.0);
        assertMapIntegrity(instance1, instance2, PortableSetFields.kDescriptor);
    }

    @Test
    public void testMapIntegrityAll_map() throws InterruptedException {
        generator.getBaseContext().setDefaultFillRate(1.0);
        assertMapIntegrity(instance1, instance2, PortableMapFields.kDescriptor);
    }

    @Test
    public void testMapIntegrityAll_maplist() throws InterruptedException {
        generator.getBaseContext().setDefaultFillRate(1.0);
        assertMapIntegrity(instance1, instance2, PortableMapListFields.kDescriptor);
    }

    @Test
    public void testMapIntegrityAll_mapset() throws InterruptedException {
        generator.getBaseContext().setDefaultFillRate(1.0);
        assertMapIntegrity(instance1, instance2, PortableMapSetFields.kDescriptor);
    }

    @Test
    public void testMapIntegrityRand() throws InterruptedException {
        generator.getBaseContext().setDefaultFillRate(0.5);
        assertMapIntegrity(instance1, instance2, PortableFields.kDescriptor);
    }

    @Test
    public void testMapIntegrityRand_list() throws InterruptedException {
        generator.getBaseContext().setDefaultFillRate(0.5);
        assertMapIntegrity(instance1, instance2, PortableListFields.kDescriptor);
    }

    @Test
    public void testMapIntegrityRand_set() throws InterruptedException {
        generator.getBaseContext().setDefaultFillRate(0.5);
        assertMapIntegrity(instance1, instance2, PortableSetFields.kDescriptor);
    }

    @Test
    public void testMapIntegrityRand_map() throws InterruptedException {
        generator.getBaseContext().setDefaultFillRate(0.5);
        assertMapIntegrity(instance1, instance2, PortableMapFields.kDescriptor);
    }

    @Test
    public void testMapIntegrityRand_maplist() throws InterruptedException {
        generator.getBaseContext().setDefaultFillRate(0.5);
        assertMapIntegrity(instance1, instance2, PortableMapListFields.kDescriptor);
    }

    @Test
    public void testMapIntegrityRand_mapset() throws InterruptedException {
        generator.getBaseContext().setDefaultFillRate(0.5);
        assertMapIntegrity(instance1, instance2, PortableMapSetFields.kDescriptor);
    }
}
