package net.morimekta.providence.storage;

import net.morimekta.providence.serializer.PrettySerializer;
import net.morimekta.test.providence.storage.Containers;
import net.morimekta.test.providence.storage.OptionalFields;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static net.morimekta.providence.testing.ProvidenceMatchers.equalToMessage;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

public class DirectoryMessageStoreTest extends TestBase {
    @Rule
    public TemporaryFolder tmp = new TemporaryFolder();

    @Test
    public void testConformity() {
        try (DirectoryMessageStore<String, OptionalFields, OptionalFields._Field> store = new DirectoryMessageStore<>(
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
    public void testDirectoryStore() throws IOException, InterruptedException {
        Map<UUID, Containers> source = new HashMap<>();
        for (int i = 0; i < 100; ++i) {
            source.put(UUID.randomUUID(),
                       generator.generate(Containers.kDescriptor));
        }

        try (DirectoryMessageStore<UUID, Containers, Containers._Field> store = new DirectoryMessageStore<>(
                tmp.getRoot(),
                UUID::toString,
                UUID::fromString,
                Containers.kDescriptor,
                new PrettySerializer().config())) {
            store.putAll(source);
        } catch (IOException e) {
            fail(e.getMessage());
        }

        Thread.sleep(1);

        try (DirectoryMessageStore<UUID, Containers, Containers._Field> store = new DirectoryMessageStore<>(
                tmp.getRoot(),
                UUID::toString,
                UUID::fromString,
                Containers.kDescriptor,
                new PrettySerializer().config())) {
            assertThat(store.keys(), is(source.keySet()));

            Set<UUID> random5 = new HashSet<>();
            source.forEach((k, v) -> {
                if (random5.size() < 5) {
                    random5.add(k);
                }
                assertThat(store.containsKey(k), is(true));
                assertThat(k.toString(), store.get(k), is(equalToMessage(v)));
            });
            for (int i = 0; i < 100; ++i) {
                assertThat(store.containsKey(UUID.randomUUID()), is(false));
            }
            assertThat(store.getAll(source.keySet()), is(source));

            store.removeAll(random5);

            for (UUID k : random5) {
                assertThat(store.containsKey(k), is(false));
                assertThat(store.get(k), is(nullValue()));
            }
        } catch (IOException e) {
            fail(e.getMessage());
        }
    }
}
