package net.morimekta.providence.jackson;

import net.morimekta.util.Binary;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

public class ProvidenceModule extends SimpleModule {
    public ProvidenceModule() {
        super("providence-core-jackson");

        addSerializer(Binary.class, new BinarySerializer());
        addDeserializer(Binary.class, new BinaryDeserializer());

        addKeySerializer(Binary.class, new BinaryKeySerializer());
        addKeyDeserializer(Binary.class, new BinaryKeyDeserializer());
    }

    public static void register(ObjectMapper mapper) {
        mapper.registerModule(new ProvidenceModule());
    }
}
