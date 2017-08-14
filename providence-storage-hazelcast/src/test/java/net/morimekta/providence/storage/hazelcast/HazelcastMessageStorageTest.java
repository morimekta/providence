package net.morimekta.providence.storage.hazelcast;

import net.morimekta.test.providence.storage.hazelcast.OptionalFields;

import com.hazelcast.core.IMap;
import org.junit.Test;

public class HazelcastMessageStorageTest extends TestBase {
    @Test
    public void testStorageConformity() {
        IMap<String, OptionalFields> map = instance.getMap(getClass().getName());
        HazelcastMessageStorage<String,OptionalFields,OptionalFields._Field> storage =
                new HazelcastMessageStorage<>(map);
        assertConformity(storage);
    }
}
