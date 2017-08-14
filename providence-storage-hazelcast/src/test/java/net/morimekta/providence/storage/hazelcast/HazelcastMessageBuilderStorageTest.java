package net.morimekta.providence.storage.hazelcast;


import net.morimekta.test.providence.storage.hazelcast.OptionalFields;

import com.hazelcast.core.IMap;
import com.hazelcast.query.EntryObject;
import com.hazelcast.query.Predicate;
import com.hazelcast.query.PredicateBuilder;
import org.junit.Test;

import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class HazelcastMessageBuilderStorageTest extends TestBase {
    @Test
    public void testStorageConformity() {
        IMap<String, OptionalFields._Builder> map = instance.getMap(getClass().getName());
        HazelcastMessageBuilderStorage<String,OptionalFields,OptionalFields._Field,OptionalFields._Builder> storage =
                new HazelcastMessageBuilderStorage<>(map);
        assertConformity(storage);
    }

    @Test
    public void testQueries() {
        // To ensure that the messages are stored in an indexable way, we add an index to query it directly.
        IMap<String, OptionalFields._Builder> map = instance.getMap(getClass().getName() + "_queries");
        HazelcastMessageBuilderStorage<String,OptionalFields,OptionalFields._Field,OptionalFields._Builder> storage =
                new HazelcastMessageBuilderStorage<>(map);

        map.addIndex(OptionalFields._Field.INTEGER_VALUE.getName(), true);

        for (int i = 0; i < 100; ++i) {
            storage.put(UUID.randomUUID().toString(),
                        generator.generate(OptionalFields.kDescriptor));
        }
        generator.withGenerator(OptionalFields.kDescriptor, gen -> {
            gen.setAlwaysPresent(OptionalFields._Field.INTEGER_VALUE);
        });
        String theId = UUID.randomUUID().toString();
        OptionalFields toSearchFor = generator.generate(OptionalFields.kDescriptor);
        storage.put(theId, toSearchFor);

        EntryObject eo = new PredicateBuilder().getEntryObject();
        Predicate q = eo.get(OptionalFields._Field.INTEGER_VALUE.getName()).equal(toSearchFor.getIntegerValue());

        Set<Map.Entry<String,OptionalFields._Builder>> res = map.entrySet(q);

        assertThat(res, hasSize(1)); // There is a 100 / 2 p 32 chance of collision.
        AtomicReference<String> id = new AtomicReference<>();
        AtomicReference<OptionalFields> value = new AtomicReference<>();
        res.forEach(e -> {
            id.set(e.getKey());
            value.set(e.getValue().build());
        });

        assertThat(id.get(), is(theId));
        assertThat(value.get(), is(toSearchFor));
    }
}
