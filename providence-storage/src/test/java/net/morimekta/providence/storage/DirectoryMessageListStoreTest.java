package net.morimekta.providence.storage;

import net.morimekta.providence.serializer.PrettySerializer;
import net.morimekta.test.providence.storage.OptionalFields;

import com.google.common.collect.ImmutableList;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
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
import static org.junit.Assert.fail;

public class DirectoryMessageListStoreTest extends TestBase {
    @Rule
    public TemporaryFolder        tmp       = new TemporaryFolder();

    @Test
    public void testConformity() {
        try (DirectoryMessageListStore<String, OptionalFields, OptionalFields._Field> store = new DirectoryMessageListStore<>(
                tmp.getRoot(),
                i -> i,
                i -> i,
                OptionalFields.kDescriptor,
                new PrettySerializer().config())) {
            assertConformity(store);
        } catch (IOException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testStore() {
        Map<UUID, List<OptionalFields>> source = new HashMap<>();
        for (int i = 0; i < 100; ++i) {
            UUID uuid = UUID.randomUUID();
            for (int j = 0; j < 10; ++j) {
                source.computeIfAbsent(uuid, u -> new LinkedList<>())
                      .add(generator.generate(OptionalFields.kDescriptor));
            }
        }

        try (DirectoryMessageListStore<UUID, OptionalFields, OptionalFields._Field> store = new DirectoryMessageListStore<>(
                tmp.getRoot(),
                UUID::toString,
                UUID::fromString,
                OptionalFields.kDescriptor,
                new PrettySerializer().config())) {
            store.putAll(source);
        } catch (IOException e) {
            fail(e.getMessage());
        }

        try (DirectoryMessageListStore<UUID, OptionalFields, OptionalFields._Field> store = new DirectoryMessageListStore<>(
                tmp.getRoot(),
                UUID::toString,
                UUID::fromString,
                OptionalFields.kDescriptor,
                new PrettySerializer().config())) {
            TreeSet<UUID> ids = new TreeSet<>(store.keys());

            assertThat(ids, hasSize(100));
            for (UUID id : ids) {
                assertThat(store.containsKey(id), is(true));
            }
            TreeSet<UUID> missing = new TreeSet<>();
            for (int i = 0; i < 100; ++i) {
                UUID uuid = UUID.randomUUID();
                assertThat(store.containsKey(uuid), is(false));
                ;
                missing.add(uuid);
            }

            assertThat(store.getAll(missing)
                            .entrySet(), hasSize(0));
            store.remove(ids.first());
            store.removeAll(new ArrayList<>(ids).subList(45, 55));

            assertThat(store.getAll(ids)
                            .entrySet(), hasSize(89));

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
            assertThat(otherBuilder.get(0)
                                   .build(), is(equalToMessage(builder.build())));
        } catch (IOException e) {
            fail(e.getMessage());
        }
    }
}
