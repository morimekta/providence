package net.morimekta.hazelcast.it;

import net.morimekta.test.hazelcast.v1.OptionalListFields;

import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import org.junit.Test;

import java.util.stream.Collectors;

import static net.morimekta.providence.testing.ProvidenceMatchers.equalToMessage;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

/**
 * Tests found from random tests, that creates faults.
 */
public class HazelcastOutlierTests extends GenericMethods {

    @Test
    public void testVersion1OptionalListFieldsAll() throws InterruptedException {
        String mapName = nextString();
        HazelcastInstance instance1 = Hazelcast.newHazelcastInstance(getV1Config());
        HazelcastInstance instance2 = Hazelcast.newHazelcastInstance(getV1Config());

        IMap<String, OptionalListFields._Builder> writeMap = instance1.getMap(mapName);
        IMap<String, net.morimekta.test.hazelcast.v1.OptionalListFields._Builder> readMap = instance2.getMap(mapName);

        net.morimekta.test.hazelcast.v1.OptionalListFields input = generator.nextOptionalListFieldsV1(true);

        // Setting an empty list will break on mutate that doesn't set the new object, although the base is OK.
        net.morimekta.test.hazelcast.v1.OptionalListFields expected = input.mutate()
                .setBooleanValues(input.getBooleanValues().stream().limit(0).collect(Collectors.toList()))
                .build();

        String key = nextString();
        writeMap.put(key, expected.mutate());

        net.morimekta.test.hazelcast.v1.OptionalListFields actual = readMap.get(key)
                                                                           .build();

        assertThat(expected, is(equalToMessage(actual)));
        assertThat(expected, is(actual));
    }

}
