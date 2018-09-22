package net.morimekta.providence.client;

import com.google.api.client.util.ObjectParser;
import net.morimekta.providence.descriptor.PMessageDescriptor;
import net.morimekta.providence.serializer.JsonSerializer;
import net.morimekta.providence.serializer.Serializer;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.nio.charset.Charset;

/**
 * Object parser for providence messages.
 */
public class ProvidenceObjectParser implements ObjectParser {
    private final Serializer provider;

    public ProvidenceObjectParser(Serializer provider) {
        this.provider = provider;
    }

    @Nonnull
    private PMessageDescriptor getMessageDescriptor(Class<?> aClass) {
        try {
            Field kDescriptor = aClass.getDeclaredField("kDescriptor");
            return (PMessageDescriptor) kDescriptor.get(null);
        } catch (NoSuchFieldException | IllegalAccessException | ClassCastException e) {
            throw new IllegalStateException("Class " + aClass.getName() + " is not a providence message", e);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T parseAndClose(InputStream inputStream, Charset charset, Class<T> aClass) throws IOException {
        try {
            PMessageDescriptor descriptor = getMessageDescriptor(aClass);
            return (T) provider.deserialize(inputStream, descriptor);
        } finally {
            inputStream.close();
        }
    }

    @Override
    public Object parseAndClose(InputStream inputStream, Charset charset, Type type) throws IOException {
        if (type instanceof Class) {
            return parseAndClose(inputStream, charset, (Class<?>) type);
        }

        try {
            Class<?> aClass = ProvidenceObjectParser.class.getClassLoader().loadClass(type.getTypeName());
            return parseAndClose(inputStream, charset, aClass);
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException("Class " + type.getTypeName() + " is not a providence message", e);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T parseAndClose(Reader reader, Class<T> aClass) throws IOException {
        if (provider instanceof JsonSerializer) {
            PMessageDescriptor descriptor = getMessageDescriptor(aClass);
            JsonSerializer json = (JsonSerializer) provider;
            try {
                return (T) json.deserialize(reader, descriptor);
            } finally {
                reader.close();
            }
        }

        throw new IllegalStateException("Serializer " + provider.toString() + " does not support Reader deserialization");
    }

    @Override
    public Object parseAndClose(Reader reader, Type type) throws IOException {
        if (type instanceof Class) {
            return parseAndClose(reader, (Class<?>) type);
        }

        try {
            Class<?> aClass = ProvidenceObjectParser.class.getClassLoader().loadClass(type.getTypeName());
            return parseAndClose(reader, aClass);
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }
}
