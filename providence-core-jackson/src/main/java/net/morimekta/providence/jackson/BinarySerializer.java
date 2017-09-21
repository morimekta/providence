package net.morimekta.providence.jackson;

import net.morimekta.util.Binary;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;


public class BinarySerializer extends StdSerializer<Binary> {
    public BinarySerializer() {
        super(Binary.class);
    }

    @Override
    public void serialize(Binary value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        gen.writeString(value.toBase64());
    }
}
