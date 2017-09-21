package net.morimekta.providence.jackson;

import net.morimekta.util.Binary;

import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.KeyDeserializer;

import java.io.IOException;

public class BinaryKeyDeserializer extends KeyDeserializer {
    public BinaryKeyDeserializer() {
        super();
    }

    @Override
    public Object deserializeKey(String key, DeserializationContext ctxt) throws IOException {
        return Binary.fromBase64(key);
    }
}
