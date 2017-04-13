package net.morimekta.providence.streams;

import net.morimekta.providence.serializer.BinarySerializer;
import net.morimekta.providence.serializer.JsonSerializer;
import net.morimekta.test.providence.core.CompactFields;

import com.google.common.collect.ImmutableList;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * @author Stein Eldar Johnsen
 * @since 07.11.15.
 */
public class MessageStreamsTest {
    @Rule
    public TemporaryFolder tmp = new TemporaryFolder();

    private List<CompactFields> list;

    @Before
    public void setUp() {
        list = ImmutableList.of(new CompactFields("first", 1234, "The first!"),
                                new CompactFields("second", 4321, null),
                                new CompactFields("third", 5432, "Noop!"));
    }

    @Test
    public void testToPath() throws IOException {
        Path file = tmp.newFile().toPath();

        int size = list.stream()
                       .collect(MessageCollectors.toPath(file, new BinarySerializer()));

        assertThat(Files.size(file), is((long) size));

        List<CompactFields> out = MessageStreams.path(file, new BinarySerializer(), CompactFields.kDescriptor)
                                                .collect(Collectors.toList());

        assertThat(out, is(equalTo(list)));
    }

    @Test
    public void testToFile() throws IOException {
        File file = tmp.newFile();

        int size = list.stream()
                       .collect(MessageCollectors.toFile(file, new BinarySerializer()));

        assertThat(Files.size(file.toPath()), is((long) size));

        List<CompactFields> out = MessageStreams.file(file, new BinarySerializer(), CompactFields.kDescriptor)
                                                .collect(Collectors.toList());

        assertThat(out, is(equalTo(list)));
    }

    @Test
    public void testToStream() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        int size = list.stream()
                       .collect(MessageCollectors.toStream(baos, new JsonSerializer()));

        assertThat(baos.size(), is(size));

        ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
        List<CompactFields> out = MessageStreams.stream(bais, new JsonSerializer(), CompactFields.kDescriptor)
                                                .collect(Collectors.toList());

        assertThat(out, is(equalTo(list)));
    }
}
