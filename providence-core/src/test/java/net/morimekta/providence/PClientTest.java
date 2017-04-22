package net.morimekta.providence;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class PClientTest {
    @Test
    public void testClient() {
        TestClient testClient = new TestClient();

        assertThat(testClient.getNextSequenceId(), is(0));
        assertThat(testClient.getNextSequenceId(), is(1));
    }

    private static class TestClient extends PClient {
        // empty.
    }
}
