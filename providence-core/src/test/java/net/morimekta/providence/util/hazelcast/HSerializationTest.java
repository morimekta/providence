package net.morimekta.providence.util.hazelcast;

import net.morimekta.util.Binary;

import org.junit.Test;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static net.morimekta.providence.util.hazelcast.HSerialization.fromBinaryList;
import static net.morimekta.providence.util.hazelcast.HSerialization.toBinaryList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertTrue;

/**
 * Created by scrier on 2017-04-14.
 */
public class HSerializationTest {

    @Test
    public void serializeDeserializeLong() throws IOException {
        Random rand = new Random();
        List<Binary> expected = new ArrayList<>();
        for( int i = 0; i < rand.nextInt(50) + 50; i++ ) {
            expected.add(new Binary(ByteBuffer.allocate(8).putLong(rand.nextLong()).array()));
        }
        byte[] serialized = fromBinaryList(expected);
        assertTrue (serialized.length > (((50 * 8) - 1)));
        List<Binary> actual = toBinaryList(serialized);
        assertThat(actual.isEmpty(), is(false));
        assertThat(actual.size(), is(expected.size()));
        for( int i = 0; i < actual.size(); i++ ) {
            assertThat(actual.get(i).length(), is(expected.get(i).length()));
            assertThat(actual.get(i).get(), is(expected.get(i).get()));
        }
    }

}
