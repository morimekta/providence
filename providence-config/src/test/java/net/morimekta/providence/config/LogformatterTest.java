package net.morimekta.providence.config;

import net.morimekta.test.config.Credentials;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class LogformatterTest {
    @Test
    public void testFormat() {
        Credentials credentials = new Credentials("username", "password");
        LogFormatter formatter = new LogFormatter(false, (writer, field, value) -> {
            if (field.getName()
                     .contains("password")) {
                writer.append("\"********\"");
                return true;
            }
            return false;
        });

        assertThat(formatter.format(credentials),
                   is("{username=\"username\",password=\"********\"}"));
    }

    @Test
    public void testFormat_pretty() {
        Credentials credentials = new Credentials("username", "password");
        LogFormatter formatter = new LogFormatter(true, (writer, field, value) -> {
            if (field.getName()
                     .contains("password")) {
                writer.append("\"********\"");
                return true;
            }
            return false;
        });

        assertThat(formatter.format(credentials),
                   is("{\n" +
                      "  username = \"username\"\n" +
                      "  password = \"********\"\n" +
                      "}"));
    }
}
