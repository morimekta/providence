package net.morimekta.providence.storage;

import net.morimekta.test.providence.storage.OptionalFields;

import org.junit.Test;

import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;

import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;

public class PredicateFilterMessageStoreSearcherTest {
    @Test
    public void testPredicateSearch() {
        InMemoryMessageStore<String, OptionalFields, OptionalFields._Field > store = new InMemoryMessageStore<>();
        store.put(UUID.randomUUID().toString(), OptionalFields.builder()
                                                              .setStringValue("foo")
                                                              .setIntegerValue(12)
                                                              .build());
        store.put(UUID.randomUUID().toString(), OptionalFields.builder()
                                                              .setStringValue("bar")
                                                              .setIntegerValue(123)
                                                              .build());
        store.put(UUID.randomUUID().toString(), OptionalFields.builder()
                                                              .setStringValue("baz")
                                                              .setIntegerValue(1234)
                                                              .build());
        PredicateFilterMessageStoreSearcher<Pattern, String, OptionalFields, OptionalFields._Field> sut =
                new PredicateFilterMessageStoreSearcher<>(store, ((key, message, query) -> query.matcher(message.getStringValue()).matches()));

        List<OptionalFields> result1 = sut.search(Pattern.compile("f.*"));
        assertThat(result1, hasItems(OptionalFields.builder()
                                                   .setStringValue("foo")
                                                   .setIntegerValue(12)
                                                   .build()));
        assertThat(result1, hasSize(1));

        List<OptionalFields> result2 = sut.search(Pattern.compile("ba.*"));
        assertThat(result2, hasItems(OptionalFields.builder()
                                                   .setStringValue("bar")
                                                   .setIntegerValue(123)
                                                   .build(),
                                     OptionalFields.builder()
                                                   .setStringValue("baz")
                                                   .setIntegerValue(1234)
                                                   .build()));
        assertThat(result2, hasSize(2));
    }

    @Test
    public void testPredicateSearch_Builders() {
        InMemoryMessageStore<String, OptionalFields, OptionalFields._Field > store = new InMemoryMessageStore<>();
        store.put(UUID.randomUUID().toString(), OptionalFields.builder()
                                                              .setStringValue("foo")
                                                              .setIntegerValue(12)
                                                              .build());
        store.put(UUID.randomUUID().toString(), OptionalFields.builder()
                                                              .setStringValue("bar")
                                                              .setIntegerValue(123)
                                                              .build());
        store.put(UUID.randomUUID().toString(), OptionalFields.builder()
                                                              .setStringValue("baz")
                                                              .setIntegerValue(1234)
                                                              .build());
        PredicateFilterMessageStoreSearcher<Pattern, String, OptionalFields, OptionalFields._Field> sut =
                new PredicateFilterMessageStoreSearcher<>(store, ((key, message, query) -> query.matcher(message.getStringValue()).matches()));

        List<OptionalFields._Builder> result1 = sut.searchBuilders(Pattern.compile("f.*"));
        assertThat(result1, hasItems(OptionalFields.builder()
                                                   .setStringValue("foo")
                                                   .setIntegerValue(12)));
        assertThat(result1, hasSize(1));

        List<OptionalFields._Builder> result2 = sut.searchBuilders(Pattern.compile("ba.*"));
        assertThat(result2, hasItems(OptionalFields.builder()
                                                   .setStringValue("bar")
                                                   .setIntegerValue(123),
                                     OptionalFields.builder()
                                                   .setStringValue("baz")
                                                   .setIntegerValue(1234)));
        assertThat(result2, hasSize(2));

    }
}
