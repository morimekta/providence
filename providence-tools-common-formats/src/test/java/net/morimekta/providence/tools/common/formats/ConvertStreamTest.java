package net.morimekta.providence.tools.common.formats;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.IOException;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class ConvertStreamTest {
    @Rule
    public TemporaryFolder tmp = new TemporaryFolder();

    @Test
    public void testRoundTrip() throws IOException {
        ConvertStream stream = new ConvertStream(Format.config,
                                                 tmp.newFile("foo"),
                                                 true);
        assertThat(stream.toString(), is("config,base64,file:" + tmp.getRoot().getAbsolutePath() + "/foo"));

        ConvertStreamParser parser = new ConvertStreamParser();
        ConvertStream other = parser.parse(stream.toString());
        assertThat(other, is(stream));
    }
}
