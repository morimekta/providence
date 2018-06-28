package net.morimekta.providence.storage;

import net.morimekta.test.providence.storage.OptionalFields;

import org.junit.Test;

import java.util.List;
import java.util.UUID;
import java.util.function.BiPredicate;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;

public class BiPredicateMessageStoreSearcherTest {
    @Test
    public void testPredicateSearch() {
        UUID uuid1 = UUID.randomUUID();
        UUID uuid2 = UUID.nameUUIDFromBytes(uuid1.toString().getBytes(UTF_8));
        UUID uuid3 = UUID.randomUUID();

        InMemoryMessageStore<UUID, OptionalFields, OptionalFields._Field> store = new InMemoryMessageStore<>();
        store.put(uuid1, OptionalFields.builder()
                                       .setIntegerValue(12)
                                       .build());
        store.put(uuid2, OptionalFields.builder()
                                       .setIntegerValue(123)
                                       .build());
        store.put(uuid3, OptionalFields.builder()
                                       .setIntegerValue(1234)
                                       .build());
        BiPredicateMessageStoreSearcher<UUID, OptionalFields, OptionalFields._Field> sut = new BiPredicateMessageStoreSearcher<>(store);

        List<OptionalFields> result1 = sut.search(((BiPredicate<UUID, OptionalFields>)
                (k, f) -> f.getIntegerValue() > 12).and((k, f) -> f.getIntegerValue() < 1234));
        assertThat(result1, hasItems(OptionalFields.builder()
                                                   .setIntegerValue(123)
                                                   .build()));
        assertThat(result1, hasSize(1));

        List<OptionalFields> result2 = sut.search((k, f) -> k.version() == 4);
        assertThat(result2, hasItems(OptionalFields.builder()
                                                   .setIntegerValue(12)
                                                   .build(),
                                     OptionalFields.builder()
                                                   .setIntegerValue(1234)
                                                   .build()));
        assertThat(result2, hasSize(2));
    }
}
