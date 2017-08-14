package net.morimekta.hazelcast.it;

import net.morimekta.test.hazelcast.v1.PortableListFields;

import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.stream.Collectors;

import static net.morimekta.providence.testing.ProvidenceMatchers.equalToMessage;
import static net.morimekta.test.hazelcast.v1.PortableListFields.kDescriptor;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

/**
 * Tests found from random tests, that creates faults.
 */
public class HazelcastOutlierTests extends GenericMethods {
    private static HazelcastInstance instance1;
    private static HazelcastInstance instance2;

    @BeforeClass
    public static void setUpClass() {
        instance1 = Hazelcast.newHazelcastInstance(getV1Config());
        instance2 = Hazelcast.newHazelcastInstance(getV1Config());
    }

    @AfterClass
    public static void tearDownClass() {
        instance1.shutdown();
        instance2.shutdown();
    }

    @Test
    public void testVersion1OptionalListFieldsAll() throws InterruptedException {
        generator.getBaseContext().setDefaultFillRate(1.0);

        String mapName = getClass().getName();

        IMap<String, PortableListFields._Builder> writeMap = instance1.getMap(mapName);
        IMap<String, PortableListFields._Builder> readMap = instance2.getMap(mapName);

        PortableListFields input = generator.generate(kDescriptor);

        // Setting an empty list will break on mutate that doesn't set the new object, although the base is OK.
        PortableListFields expected = input.mutate()
                .setBooleanValues(input.getBooleanValues().stream().limit(0).collect(Collectors.toList()))
                .build();

        String key = generator.getBaseContext().getFairy().textProducer().randomString(123);
        writeMap.put(key, expected.mutate());

        PortableListFields actual = readMap.get(key).build();

        assertThat(expected, is(equalToMessage(actual)));
        assertThat(expected, is(actual));
    }

}
