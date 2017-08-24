package net.morimekta.providence.config;

/**
 * Unchecked config exception wrapping the providence config exception.
 * Handy for using config in streams etc.
 */
public class UncheckedProvidenceConfigException extends RuntimeException {
    public UncheckedProvidenceConfigException(ProvidenceConfigException cause) {
        super(cause.getMessage(), cause);
    }

    @Override
    public UncheckedProvidenceConfigException initCause(Throwable cause) {
        if (!(cause instanceof ProvidenceConfigException)) {
            throw new IllegalArgumentException("Exception " + cause.getClass().getName() + " is not a config exception");
        }
        super.initCause(cause);
        return this;
    }

    public ProvidenceConfigException getCause() {
        return (ProvidenceConfigException) super.getCause();
    }
}
