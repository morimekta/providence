package net.morimekta.providence;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Interface for handling a call request from a synchronous client.
 */
public abstract class PClient {
    private AtomicInteger nextSequenceId;

    protected PClient() {
        nextSequenceId = new AtomicInteger(0);
    }

    protected int getNextSequenceId() {
        return nextSequenceId.getAndIncrement();
    }
}
