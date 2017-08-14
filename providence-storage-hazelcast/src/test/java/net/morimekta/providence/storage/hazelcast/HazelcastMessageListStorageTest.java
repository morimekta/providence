package net.morimekta.providence.storage.hazelcast;

import net.morimekta.test.providence.storage.hazelcast.OptionalFields;

import com.hazelcast.core.IMap;
import org.junit.Test;

import java.util.List;

public class HazelcastMessageListStorageTest extends TestBase {
    @Test
    public void testStorageConformity() {
        IMap<String, List<OptionalFields>> map = instance.getMap(getClass().getName());
        HazelcastMessageListStorage<String,OptionalFields,OptionalFields._Field> storage =
                new HazelcastMessageListStorage<>(map);
        assertConformity(storage);
    }
}
