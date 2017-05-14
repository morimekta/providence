package net.morimekta.providence.util;

import net.morimekta.providence.serializer.BinarySerializer;
import net.morimekta.providence.serializer.PrettySerializer;
import net.morimekta.providence.streams.MessageStreams;
import net.morimekta.test.providence.core.Containers;
import net.morimekta.test.providence.core.OptionalFields;
import net.morimekta.util.Binary;

import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static java.nio.charset.StandardCharsets.UTF_8;
import static net.morimekta.testing.ExtraMatchers.equalToLines;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class LogformatterTest {
    @Test
    public void testFormat() {
        OptionalFields credentials = OptionalFields
                .builder()
                .setStringValue("username")
                .setBinaryValue(Binary.fromBase64("password"))
                .build();
        LogFormatter formatter = new LogFormatter((writer, field, value) -> {
            if (field.getName()
                     .contains("binary")) {
                writer.append("\"********\"");
                return true;
            }
            return false;
        });

        assertThat(formatter.format(credentials),
                   is("providence.OptionalFields{stringValue=\"username\",binaryValue=\"********\"}"));
    }

    @Test
    public void testFormat_pretty() {
        OptionalFields credentials = OptionalFields
                .builder()
                .setStringValue("username")
                .setBinaryValue(Binary.fromBase64("password"))
                .build();

        LogFormatter formatter = new LogFormatter(true, (writer, field, value) -> {
            if (field.getName()
                     .contains("binary")) {
                writer.append("\"********\"");
                return true;
            }
            return false;
        });

        assertThat(formatter.format(credentials),
                   is("providence.OptionalFields {\n" +
                      "  stringValue = \"username\"\n" +
                      "  binaryValue = \"********\"\n" +
                      "}"));
    }

    @Test
    public void testFormat_null() {
        LogFormatter formatter = new LogFormatter(true);

        assertThat(formatter.format(null), is("null"));
    }

    @Test
    public void testFormat_EverythingDefault() throws IOException {
        Containers containers = MessageStreams.resource("/compat/binary.data", new BinarySerializer(), Containers.kDescriptor)
                                              .findFirst()
                                              .orElseThrow(() -> new AssertionError("resource"));
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        new PrettySerializer().config().serialize(baos, containers);

        assertThat(new LogFormatter(true).format(containers),
                   is(equalToLines(new String(baos.toByteArray(), UTF_8))));
    }
}
