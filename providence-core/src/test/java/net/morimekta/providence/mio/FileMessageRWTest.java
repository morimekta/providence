package net.morimekta.providence.mio;

import net.morimekta.providence.PServiceCall;
import net.morimekta.providence.serializer.BinarySerializer;
import net.morimekta.providence.serializer.JsonSerializer;
import net.morimekta.test.providence.core.CompactFields;
import net.morimekta.test.providence.core.OptionalFields;
import net.morimekta.test.providence.core.calculator.Calculator;
import net.morimekta.test.providence.core.calculator.Operation;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import static net.morimekta.testing.ResourceUtils.writeContentTo;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class FileMessageRWTest {
    @Rule
    public TemporaryFolder tmp = new TemporaryFolder();


    private CompactFields m1 = new CompactFields("name", 1234, "Message");
    private OptionalFields m2 = OptionalFields.builder()
                                      .setStringValue("string")
                                      .setIntegerValue(1234)
                                      .setDoubleValue(4321.1234)
                                      .build();

    @Test
    public void testBinary() throws IOException {
        File test = tmp.newFile();

        try (FileMessageWriter writer = new FileMessageWriter(test, new BinarySerializer())) {
            writer.write(m1);
            writer.separator();
            writer.write(m2);
        }

        try (FileMessageReader reader = new FileMessageReader(test, new BinarySerializer())) {
            assertThat(m1, is(equalTo(reader.read(CompactFields.kDescriptor))));
            assertThat(m2, is(equalTo(reader.read(OptionalFields.kDescriptor))));
        }
    }

    @Test
    public void testReadable() throws IOException {
        File test = tmp.newFile();

        try (FileMessageWriter writer = new FileMessageWriter(test, new JsonSerializer())) {
            writer.write(m1);
            writer.separator();
            writer.write(m2);
        }

        try (FileMessageReader reader = new FileMessageReader(test, new JsonSerializer())) {
            assertThat(m1, is(equalTo(reader.read(CompactFields.kDescriptor))));
            assertThat(m2, is(equalTo(reader.read(OptionalFields.kDescriptor))));
        }
    }

    @Test
    public void testService() throws IOException {
        String content = "[\"calculate\",\"call\",44,{\"op\":{\"operator\":\"ADD\",\"operands\":[]}}]";
        File test = writeContentTo(content, tmp.newFile());

        PServiceCall<Operation, Operation._Field> call = null;
        try (FileMessageReader reader = new FileMessageReader(test, new JsonSerializer())) {
            call = reader.read(Calculator.kDescriptor);
        }

        File result = tmp.newFile();
        try (FileMessageWriter writer = new FileMessageWriter(result, new JsonSerializer(false, JsonSerializer.IdType.NAME, JsonSerializer.IdType.NAME, false))) {
            writer.write(call);
        }

        assertThat(new String(Files.readAllBytes(result.toPath()), StandardCharsets.UTF_8),
                   is(equalTo(content)));
    }
}
