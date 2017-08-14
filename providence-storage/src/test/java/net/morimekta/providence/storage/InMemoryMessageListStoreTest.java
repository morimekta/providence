package net.morimekta.providence.storage;

import net.morimekta.test.providence.storage.OptionalFields;

import com.google.common.collect.ImmutableList;
import org.junit.Test;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.UUID;

import static net.morimekta.providence.testing.ProvidenceMatchers.equalToMessage;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class InMemoryMessageListStoreTest extends TestBase {
    @Test
    public void testConformity() {
        MessageListStore<String, OptionalFields, OptionalFields._Field> store = new InMemoryMessageListStore<>();
        assertConformity(store);
    }

    @Test
    public void testStore() {
        MessageListStore<UUID, OptionalFields, OptionalFields._Field> store = new InMemoryMessageListStore<>();

        for (int i = 0; i < 100; ++i) {
            List<OptionalFields> list = new LinkedList<>();
            for (int j = 0; j < 10; ++j) {
                list.add(generator.generate(OptionalFields.kDescriptor));
            }
            store.put(UUID.randomUUID(), list);
        }
        TreeSet<UUID> ids = new TreeSet<>(store.keys());

        assertThat(ids, hasSize(100));
        for (UUID id : ids) {
            assertThat(store.containsKey(id), is(true));
        }
        TreeSet<UUID> missing = new TreeSet<>();
        for (int i = 0; i < 100; ++i) {
            UUID uuid = UUID.randomUUID();
            assertThat(store.containsKey(uuid), is(false));;
            missing.add(uuid);
        }

        assertThat(store.getAll(missing).entrySet(), hasSize(0));
        store.remove(ids.first());
        store.removeAll(new ArrayList<>(ids).subList(45, 55));

        assertThat(store.getAll(ids).entrySet(), hasSize(89));

        Map<UUID, List<OptionalFields._Builder>> bld = store.getAllBuilders(new ArrayList<>(ids).subList(30, 45));

        bld.forEach((k, list) -> {
            for (OptionalFields._Builder b : list) {
                b.clearBinaryValue();
                b.clearBooleanValue();
                b.clearByteValue();
            }
        });

        store.putAllBuilders(bld);

        Map<UUID, List<OptionalFields>> tmp2 = store.getAll(bld.keySet());
        tmp2.forEach((k, list) -> {
            for (OptionalFields v : list) {
                assertThat(v.hasBooleanValue(), is(false));
                assertThat(v.hasByteValue(), is(false));
                assertThat(v.hasBinaryValue(), is(false));
            }
        });

        OptionalFields._Builder builder = OptionalFields.builder();
        builder.setIntegerValue(10);
        builder.setBooleanValue(true);
        builder.setDoubleValue(12345.6789);
        UUID uuid = UUID.randomUUID();
        store.putBuilders(uuid, ImmutableList.of(builder));

        List<OptionalFields> list = store.get(uuid);
        assertThat(list, hasSize(1));
        assertThat(list, hasItem(builder.build()));

        List<OptionalFields._Builder> otherBuilder = store.getBuilders(uuid);

        assertThat(otherBuilder, hasSize(1));
        assertThat(otherBuilder.get(0).build(), is(equalToMessage(builder.build())));
    }
}
