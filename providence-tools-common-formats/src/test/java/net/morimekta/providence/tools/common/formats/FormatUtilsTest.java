package net.morimekta.providence.tools.common.formats;

import net.morimekta.providence.tools.common.ProvidenceTools;
import net.morimekta.testing.rules.ConsoleWatcher;
import net.morimekta.util.Base64;

import org.junit.Rule;
import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class FormatUtilsTest {
    @Rule
    public ConsoleWatcher console = new ConsoleWatcher();

    @Test
    public void testGetOutput() throws IOException {
        List<ProvidenceTools> tool = new ArrayList<>();
        tool.add(ProvidenceTools.builder()
                                .setIncludesBasePath("foo")
                                .build());

        int i = tool.stream()
                    .collect(FormatUtils.getOutput(new ConvertStream(Format.json, null, true, false), false));

        assertThat(i, is(12));
        assertThat(console.output(), is("eyIxIjoiZm9vIn0K"));

        // TODO: Figure out how to get rid of the inner newline.
        assertThat(new String(Base64.decode(console.output()), StandardCharsets.UTF_8), is("{\"1\":\"foo\"}\n"));
    }

    @Test
    public void testGetInput() {

    }

}
