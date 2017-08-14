package net.morimekta.hazelcast.it;

import net.morimekta.test.hazelcast.v1.OptionalListFields;

import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import org.junit.Test;

import java.util.stream.Collectors;

import static net.morimekta.providence.testing.ProvidenceMatchers.equalToMessage;
import static net.morimekta.test.hazelcast.v1.OptionalListFields.kDescriptor;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

/**
 * Tests found from random tests, that creates faults.
 */
public class HazelcastOutlierTests extends GenericMethods {

    @Test
    public void testVersion1OptionalListFieldsAll() throws InterruptedException {
        generator.getBaseContext().setDefaultFillRate(1.0);

        String mapName = getClass().getName();
        HazelcastInstance instance1 = Hazelcast.newHazelcastInstance(getV1Config());
        HazelcastInstance instance2 = Hazelcast.newHazelcastInstance(getV1Config());

        IMap<String, OptionalListFields._Builder> writeMap = instance1.getMap(mapName);
        IMap<String, OptionalListFields._Builder> readMap = instance2.getMap(mapName);

        OptionalListFields input = generator.generate(kDescriptor);

        // Setting an empty list will break on mutate that doesn't set the new object, although the base is OK.
        OptionalListFields expected = input.mutate()
                .setBooleanValues(input.getBooleanValues().stream().limit(0).collect(Collectors.toList()))
                .build();

        String key = generator.getBaseContext().getFairy().textProducer().randomString(123);
        writeMap.put(key, expected.mutate());

        OptionalListFields actual = readMap.get(key).build();

        assertThat(expected, is(equalToMessage(actual)));
        assertThat(expected, is(actual));
    }

}
