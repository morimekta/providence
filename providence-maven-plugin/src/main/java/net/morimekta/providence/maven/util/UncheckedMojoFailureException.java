package net.morimekta.providence.maven.util;

public class UncheckedMojoFailureException extends RuntimeException {
    public UncheckedMojoFailureException(String message, Throwable e) {
        super(message, e);
    }
}
