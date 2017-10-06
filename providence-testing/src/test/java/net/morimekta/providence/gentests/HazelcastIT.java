package net.morimekta.providence.gentests;

import net.morimekta.providence.testing.generator.GeneratorWatcher;
import net.morimekta.providence.testing.generator.SimpleGeneratorWatcher;
import net.morimekta.test.hazelcast.Hazelcast_Factory;
import net.morimekta.test.hazelcast.OptionalFields;
import net.morimekta.test.hazelcast.OptionalListFields;

import com.hazelcast.config.Config;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Hazelcast serialization and deserialization tests.
 */
public class HazelcastIT {
    private static final boolean IGNORE_HAZELCAST = false;

    private static HazelcastInstance instance1;
    private static HazelcastInstance instance2;

    @Rule
    public SimpleGeneratorWatcher generator = GeneratorWatcher.create();

    private OptionalFields     optionalFields;
    private OptionalListFields optionalListFields;

    @Before
    public void setup() {
        if (IGNORE_HAZELCAST) return;
        generator.dumpOnFailure();
        optionalFields = generator.generate(OptionalFields.kDescriptor);
        optionalListFields = generator.generate(OptionalListFields.kDescriptor);
    }

    @BeforeClass
    public static void setupClass() {
        if (IGNORE_HAZELCAST) return;
        System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "off");

        Config config1 = new Config();
        config1.setProperties(System.getProperties());
        config1.setProperty("hazelcast.logging.type", "slf4j");
        Hazelcast_Factory.populateConfig(config1, 1);
        config1.getSerializationConfig().setPortableVersion(1);

        instance1 = Hazelcast.newHazelcastInstance(config1);
        instance2 = Hazelcast.newHazelcastInstance(config1);
    }

    @AfterClass
    public static void afterClass() {
        if (IGNORE_HAZELCAST) return;
        instance1.shutdown();
        instance2.shutdown();
    }

    @Test
    public void testBaseEntitiesHazelcast() {
        if( IGNORE_HAZELCAST ) return;
        IMap<String, OptionalFields._Builder> writeMap = instance1.getMap("testBaseEntitiesHazelcast");
        IMap<String, OptionalFields._Builder> readMap = instance2.getMap("testBaseEntitiesHazelcast");

        OptionalFields._Builder expected = optionalFields.mutate();

        writeMap.set("1234", expected);
        OptionalFields._Builder actual = readMap.get("1234");

        assertEquals(expected.build(), actual.build());
    }

    @Test
    public void testListEntitiesHazelcast() {
        if( IGNORE_HAZELCAST ) return;
        IMap<String, OptionalListFields._Builder> writeMap = instance1.getMap("testListEntitiesHazelcast");
        IMap<String, OptionalListFields._Builder> readMap = instance2.getMap("testListEntitiesHazelcast");

        OptionalListFields._Builder expected = optionalListFields.mutate();

        writeMap.set("1234", expected);
        OptionalListFields._Builder actual = readMap.get("1234");

        assertEquals(expected.build(), actual.build());
    }
}
