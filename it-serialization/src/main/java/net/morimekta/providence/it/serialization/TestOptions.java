package net.morimekta.providence.it.serialization;

import net.morimekta.providence.testing.generator.SimpleGeneratorWatcher;

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
        r_fields,
        deep,
    }

    AtomicBoolean           help        = new AtomicBoolean();
    AtomicBoolean           no_progress = new AtomicBoolean();
    AtomicInteger           runs        = new AtomicInteger(100);
    AtomicInteger           generate    = new AtomicInteger(10);
    SimpleGeneratorWatcher  generator   = SimpleGeneratorWatcher.create();
    AtomicReference<Test>   test        = new AtomicReference<>();
    AtomicReference<File>   file        = new AtomicReference<>();
    AtomicReference<Format> format      = new AtomicReference<>();
}
