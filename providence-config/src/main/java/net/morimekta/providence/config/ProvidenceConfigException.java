package net.morimekta.providence.config;

import net.morimekta.providence.serializer.SerializerException;
import net.morimekta.providence.serializer.pretty.TokenizerException;

import java.io.File;

/**
 * Providence config exceptions are extensions of the serializer exception (as
 * parsing config can be seen as parsing or de-serializing any serialized
 * message).
 */
public class ProvidenceConfigException extends TokenizerException {
    public ProvidenceConfigException(String format, Object... args) {
        super(format, args);
    }

    public ProvidenceConfigException(Throwable cause, String format, Object... args) {
        super(cause, format, args);
    }

    public ProvidenceConfigException(SerializerException cause) {
        super(cause.getMessage());
        setExceptionType(cause.getExceptionType());
        initCause(cause);
    }

    public ProvidenceConfigException(TokenizerException cause) {
        super(cause, null);
        setFile(cause.getFile());
    }
}
