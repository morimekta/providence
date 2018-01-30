package net.morimekta.providence.config;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class UncheckedProvidenceConfigExceptionTest {
    @Test
    public void testException() {
        ProvidenceConfigException e = new ProvidenceConfigException("Test");
        UncheckedProvidenceConfigException ue = new UncheckedProvidenceConfigException(e);

        assertThat(ue.getMessage(), is(e.getMessage()));
        assertThat(ue.asString(), is(e.asString()));
        assertThat(ue.toString(), is(e.toString()));
    }
}
