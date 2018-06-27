package net.morimekta.providence.storage;

import net.morimekta.test.providence.storage.OptionalFields;

import org.junit.Test;

import java.util.List;
import java.util.UUID;
import java.util.function.Predicate;

import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;

public class PredicateMessageStoreSearcherTest {
    @Test
    public void testPredicateSearch() {
        InMemoryMessageStore<String, OptionalFields, OptionalFields._Field> store = new InMemoryMessageStore<>();
        store.put(UUID.randomUUID().toString(), OptionalFields.builder()
                                                              .setIntegerValue(12)
                                                              .build());
        store.put(UUID.randomUUID().toString(), OptionalFields.builder()
                                                              .setIntegerValue(123)
                                                              .build());
        store.put(UUID.randomUUID().toString(), OptionalFields.builder()
                                                              .setIntegerValue(1234)
                                                              .build());
        PredicateMessageStoreSearcher<OptionalFields, OptionalFields._Field> sut = new PredicateMessageStoreSearcher<>(store);

        List<OptionalFields> result1 = sut.search(((Predicate<OptionalFields>)
                f -> f.getIntegerValue() > 12).and(f -> f.getIntegerValue() < 1234));
        assertThat(result1, hasItems(OptionalFields.builder()
                                                   .setIntegerValue(123)
                                                   .build()));
        assertThat(result1, hasSize(1));

        List<OptionalFields> result2 = sut.search(f -> f.getIntegerValue() > 12);
        assertThat(result2, hasItems(OptionalFields.builder()
                                                   .setIntegerValue(123)
                                                   .build(),
                                     OptionalFields.builder()
                                                   .setIntegerValue(1234)
                                                   .build()));
        assertThat(result2, hasSize(2));
    }
}
