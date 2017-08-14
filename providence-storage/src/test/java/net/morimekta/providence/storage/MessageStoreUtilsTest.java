package net.morimekta.providence.storage;

import net.morimekta.providence.PMessage;

import org.junit.Test;

import java.util.List;

import static net.morimekta.providence.storage.MessageStoreUtils.buildAll;
import static net.morimekta.providence.storage.MessageStoreUtils.buildIfNotNull;
import static net.morimekta.providence.storage.MessageStoreUtils.mutateAll;
import static net.morimekta.providence.storage.MessageStoreUtils.mutateIfNotNull;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

public class MessageStoreUtilsTest {
    @Test
    public void testMutateAll() {
        // Extra cast needed to normalize unknown generic.
        assertThat((List) mutateAll(null), is(nullValue()));
    }

    @Test
    public void testBuildAll() {
        // Extra cast needed to normalize unknown generic.
        assertThat((List) buildAll(null), is(nullValue()));
    }

    @Test
    public void testMutateIfNotNull() {
        // Extra cast needed to normalize unknown generic.
        assertThat((PMessage) mutateIfNotNull(null), is(nullValue()));
    }

    @Test
    public void testBuildIfNotNull() {
        // Extra cast needed to normalize unknown generic.
        assertThat((PMessage) buildIfNotNull(null), is(nullValue()));
    }
}
