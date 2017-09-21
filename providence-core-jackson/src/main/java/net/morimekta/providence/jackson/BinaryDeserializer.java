package net.morimekta.providence.jackson;

import net.morimekta.util.Binary;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;

public class BinaryDeserializer extends StdDeserializer<Binary> {
    public BinaryDeserializer() {
        super(Binary.class);
    }

    @Override
    public Binary deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        return Binary.fromBase64(p.getValueAsString());
    }

}
