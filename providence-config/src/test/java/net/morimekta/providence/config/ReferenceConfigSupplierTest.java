package net.morimekta.providence.config;

import net.morimekta.providence.config.util.TestConfigSupplier;
import net.morimekta.test.providence.config.Service;
import net.morimekta.test.providence.config.ServicePort;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

public class ReferenceConfigSupplierTest {
    @Test
    public void testReferenceConfig() throws ProvidenceConfigException {
        TestConfigSupplier<Service, Service._Field> parent = new TestConfigSupplier<>(
                Service.builder()
                       .setAdmin(ServicePort.builder()
                                            .setPort((short) 1234)
                                            .build())
                       .build());

        ReferenceConfigSupplier<ServicePort, ServicePort._Field,
                Service, Service._Field> ref = new ReferenceConfigSupplier<>("admin", parent);

        assertThat(ref.get(), is(notNullValue()));
        assertThat(ref.get().getPort(), is((short) 1234));
    }

    @Test
    public void testName() throws ProvidenceConfigException {
        FixedConfigSupplier<Service, Service._Field> parent = new FixedConfigSupplier<>(
                Service.builder()
                       .setAdmin(ServicePort.builder()
                                            .setPort((short) 1234)
                                            .build())
                       .build()
        );

        ReferenceConfigSupplier<ServicePort, ServicePort._Field,
                                Service, Service._Field> ref = new ReferenceConfigSupplier<>("admin", parent);

        assertThat(ref.getName(), is("ReferenceConfig{admin}"));
        assertThat(ref.toString(), is("ReferenceConfig{admin, parent=InMemoryConfig}"));
    }
}
