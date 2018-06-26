package net.morimekta.providence.storage;

import com.google.common.collect.ImmutableList;
import net.morimekta.test.providence.storage.OptionalFields;
import net.morimekta.util.Pair;
import org.junit.Test;

import javax.annotation.Nonnull;
import java.util.List;

import static java.util.stream.Collectors.toList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;

/**
 * TBD
 */
public class MessageSearcherTest extends TestBase {

    @Test
    public void testRangeNotInclusiveSearch() {
        MessageSearcherRangeImpl testObject = new MessageSearcherRangeImpl(list1);

        List<Long> ranges = list1.stream().filter(OptionalFields::hasLongValue).map(OptionalFields::getLongValue)
                .sorted().collect(toList());

        assertThat(testObject.search(new Pair<>(ranges.get(0)-1, ranges.get(0)+1)), hasSize(1));
        assertThat(testObject.search(new Pair<>(ranges.get(0), ranges.get(3))), hasSize(2));
        assertThat(testObject.search(new Pair<>(ranges.get(1), ranges.get(1))), hasSize(0));
        assertThat(testObject.search(new Pair<>(ranges.get(2)-1, ranges.get(3)+1)), hasSize(2));
    }

    @Test
    public void testRangeLessThanContainsSearch() {
        MessageSearcherLessThanContainsImpl testObject = new MessageSearcherLessThanContainsImpl(list1);

        List<OptionalFields> expected = testObject.search(new Pair<>(1234567L, "a"));

        List<OptionalFields> actual = list1.stream().filter(item -> item.getLongValue() > 1234567L)
                .filter(item -> item.getStringValue().contains("a")).collect(toList());

        assertThat(expected, is(actual));
    }

    private static class MessageSearcherRangeImpl implements MessageSearcher<Pair<Long, Long>,OptionalFields,OptionalFields._Field> {

        private final ImmutableList<OptionalFields> source;

        public MessageSearcherRangeImpl(List<OptionalFields> source) {
            this.source = ImmutableList.copyOf(source);
        }

        @Nonnull
        @Override
        public List<OptionalFields> search(@Nonnull Pair<Long, Long> search) {
            return this.source.stream().filter(OptionalFields::hasLongValue)
                    .filter(item -> search.first < item.getLongValue())
                    .filter(item -> search.second > item.getLongValue())
                    .collect(toList());
        }

    }

    private static class MessageSearcherLessThanContainsImpl implements MessageSearcher<Pair<Long, String>, OptionalFields,OptionalFields._Field> {

        private final ImmutableList<OptionalFields> source;

        public MessageSearcherLessThanContainsImpl(List<OptionalFields> source) {
            this.source = ImmutableList.copyOf(source);
        }

        @Nonnull
        @Override
        public List<OptionalFields> search(@Nonnull Pair<Long, String> search) {
            return this.source.stream().filter(OptionalFields::hasLongValue)
                    .filter(OptionalFields::hasStringValue)
                    .filter(item -> search.first < item.getLongValue())
                    .filter(item -> item.getStringValue().contains(search.second))
                    .collect(toList());
        }

    }

}