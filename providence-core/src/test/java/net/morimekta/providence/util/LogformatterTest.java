package net.morimekta.providence.util;

import net.morimekta.test.providence.core.OptionalFields;
import net.morimekta.util.Binary;

import org.junit.Test;

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
        LogFormatter formatter = new LogFormatter(false, (writer, field, value) -> {
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
}
