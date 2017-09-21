package net.morimekta.providence.jackson;

import net.morimekta.test.providence.jackson.Request;
import net.morimekta.util.Binary;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import org.junit.Test;

import java.io.IOException;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class ProvidenceModuleTest {
    @Test
    public void testProvidenceModule() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        ProvidenceModule.register(mapper);

        Request src = new Request(Binary.fromHexString("ABCDEF"),
                                  "ABCDEF",
                                  ImmutableList.of(Binary.fromBase64("2VjwBLYvTR6HkyjQY5rvZw")),
                                  ImmutableMap.of(Binary.fromBase64("5S+zeblRQF6kV5ScoTtSxA"),
                                                  Binary.fromBase64("LlWY54WoTpOonW9sWnM7Kw")));

        String tmp = mapper.writerFor(Request.class).writeValueAsString(src);

        assertThat(tmp, is(
                "{\"the_binary\":\"q83v\"," +
                "\"not_binary\":\"ABCDEF\"," +
                "\"list_of\":[\"2VjwBLYvTR6HkyjQY5rvZw\"]," +
                "\"map_of\":{\"5S+zeblRQF6kV5ScoTtSxA\":\"LlWY54WoTpOonW9sWnM7Kw\"}}"));

        Request req = mapper.readerFor(Request.class).readValue(tmp);
        assertThat(req, is(src));
    }
}
