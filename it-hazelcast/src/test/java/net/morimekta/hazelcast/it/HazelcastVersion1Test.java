package net.morimekta.hazelcast.it;

import net.morimekta.test.hazelcast.v1.PortableFields;
import net.morimekta.test.hazelcast.v1.PortableListFields;
import net.morimekta.test.hazelcast.v1.PortableMapFields;
import net.morimekta.test.hazelcast.v1.PortableMapListFields;
import net.morimekta.test.hazelcast.v1.PortableMapSetFields;
import net.morimekta.test.hazelcast.v1.PortableSetFields;

import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * TBD
 */
public class HazelcastVersion1Test extends GenericMethods {
    private static HazelcastInstance instance1;
    private static HazelcastInstance instance2;

    @BeforeClass
    public static void setUpHazelcast() {
        instance1 = Hazelcast.newHazelcastInstance(getV1Config());
        instance2 = Hazelcast.newHazelcastInstance(getV1Config());
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
