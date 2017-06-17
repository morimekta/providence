package net.morimekta.providence.it.serialization;

import net.morimekta.providence.testing.util.MessageGenerator;

import java.io.File;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Created by morimekta on 17.06.17.
 */
public class TestOptions {
    public enum Test {
        containers,
        fields,
        deep,
    }

    AtomicBoolean           no_progress = new AtomicBoolean();
    AtomicInteger           runs        = new AtomicInteger(100);
    AtomicInteger           generate    = new AtomicInteger(10);
    MessageGenerator        generator   = new MessageGenerator();
    AtomicReference<Test>   test        = new AtomicReference<>();
    AtomicReference<File>   file        = new AtomicReference<>();
    AtomicReference<Format> format      = new AtomicReference<>();
}
