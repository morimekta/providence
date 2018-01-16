package net.morimekta.providence.config;

import net.morimekta.test.providence.config.Database;
import net.morimekta.testing.time.FakeClock;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class FixedConfigSupplierTest {
    @Test
    @SuppressWarnings("unchecked")
    public void testCreate() {
        FakeClock clock = FakeClock.forCurrentTimeMillis(System.currentTimeMillis());
        Database value = Database.builder()
                                 .build();

        ConfigSupplier<Database, Database._Field> supplier = mock(ConfigSupplier.class);
        when(supplier.get()).thenReturn(value).thenReturn(value);
        when(supplier.configTimestamp()).thenAnswer(i -> clock.millis());
        when(supplier.snapshot()).thenCallRealMethod();

        FixedConfigSupplier<Database,Database._Field> fixed = new FixedConfigSupplier<>(supplier);

        clock.tick(1000);

        ConfigSupplier<Database,Database._Field> snapshot = supplier.snapshot();

        assertThat(fixed.get(), is(sameInstance(value)));
        assertThat(snapshot.get(), is(sameInstance(value)));

        assertThat(fixed.configTimestamp(), is(snapshot.configTimestamp() - 1000));
        assertThat(fixed.snapshot(), is(sameInstance(fixed)));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testListeners() {
        FixedConfigSupplier<Database,Database._Field> fixed = new FixedConfigSupplier<>(
                Database.builder().build());
        ConfigListener<Database,Database._Field> listener = mock(ConfigListener.class);
        fixed.addListener(listener);
        fixed.removeListener(listener);
        fixed.removeListener(listener);

        // since the object is immutable there is no way to "trigger a change" and verify
        // no calls to the listener...
    }
}
