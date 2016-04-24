package net.morimekta.providence.async;

/**
 * Callback on async service calls response.
 */
public interface AsyncMethodCallback<R> {
    void onSuccess(R returnValue);

    void onException(Exception exception);
}
