package net.morimekta.providence.jackson;

import net.morimekta.util.Binary;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

public class ProvidenceModule {
    public static void register(ObjectMapper mapper) {
        SimpleModule module = new SimpleModule("providence-core-jackson");

        module.addSerializer(Binary.class, new BinarySerializer());
        module.addDeserializer(Binary.class, new BinaryDeserializer());

        module.addKeySerializer(Binary.class, new BinaryKeySerializer());
        module.addKeyDeserializer(Binary.class, new BinaryKeyDeserializer());

        mapper.registerModule(module);
    }
}
