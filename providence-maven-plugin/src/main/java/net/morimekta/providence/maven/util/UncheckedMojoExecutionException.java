package net.morimekta.providence.maven.util;

public class UncheckedMojoExecutionException extends RuntimeException {
    public UncheckedMojoExecutionException(String message, Throwable e) {
        super(message, e);
    }
}
