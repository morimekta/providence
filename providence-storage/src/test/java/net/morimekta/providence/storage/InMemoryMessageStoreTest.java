package net.morimekta.providence.storage;

import net.morimekta.test.providence.storage.Containers;
import net.morimekta.test.providence.storage.OptionalFields;

import com.google.common.collect.ImmutableList;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Map;
import java.util.TreeSet;
import java.util.UUID;

import static net.morimekta.providence.testing.ProvidenceMatchers.equalToMessage;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

public class InMemoryMessageStoreTest extends TestBase {
    @Test
    public void testConformity() {
        MessageStore<String, OptionalFields, OptionalFields._Field> store = new InMemoryMessageStore<>();
        assertConformity(store);
    }

    @Test
    public void testStore() {
        MessageStore<UUID, Containers, Containers._Field> store = new InMemoryMessageStore<>();

        for (int i = 0; i < 100; ++i) {
            store.put(UUID.randomUUID(), generator.generate(Containers.kDescriptor));
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

        Map<UUID, Containers._Builder> bld = store.getAllBuilders(new ArrayList<>(ids).subList(30, 45));

        bld.forEach((k, b) -> {
            b.clearBinaryList();
            b.clearBooleanList();
            b.clearListListI32();
            b.clearByteList();
        });

        store.putAllBuilders(bld);

        Map<UUID, Containers> tmp2 = store.getAll(bld.keySet());
        tmp2.forEach((k, v) -> {
            assertThat(v.hasBinaryList(), is(false));
            assertThat(v.hasBooleanList(), is(false));
            assertThat(v.hasListListI32(), is(false));
            assertThat(v.hasByteList(), is(false));
        });

        Containers._Builder builder = Containers.builder();
        builder.addToIntegerList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        UUID uuid = UUID.randomUUID();
        store.putBuilder(uuid, builder);

        Containers containers = store.get(uuid);
        assertThat(containers, is(notNullValue()));
        assertThat(containers.getIntegerList(), is(ImmutableList.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)));

        Containers._Builder otherBuilder = store.getBuilder(uuid);

        assertThat(containers, is(equalToMessage(otherBuilder.build())));
    }
}
