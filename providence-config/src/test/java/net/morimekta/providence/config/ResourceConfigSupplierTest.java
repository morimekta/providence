package net.morimekta.providence.config;

import net.morimekta.test.providence.config.Service;
import net.morimekta.test.providence.config.ServicePort;
import net.morimekta.util.Binary;

import com.google.common.collect.ImmutableMap;
import org.junit.Before;
import org.junit.Test;

import static net.morimekta.providence.testing.ProvidenceMatchers.equalToMessage;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;


public class ResourceConfigSupplierTest {
    private Service expected;

    @Before
    public void setUp() {
        expected = Service.builder()
                          .setAdmin(ServicePort.builder()
                                               .setPort((short) 8088)
                                               .setOauthTokenKey(Binary.fromBase64("VGVzdCBPYXV0aCBLZXkK"))
                                               .build())
                          .setHttp(ServicePort.builder()
                                              .setPort((short) 8080)
                                              .setContext("/app")
                                              .setSignatureKeys(ImmutableMap.of(
                                                      "app1", Binary.fromBase64("VGVzdCBPYXV0aCBLZXkK")
                                              ))
                                              .addToSignatureOverrideKeys("not_really_app_1")
                                              .build())
                          .build();
    }

    @Test
    public void testWithJson() throws ProvidenceConfigException {
        ConfigSupplier<Service, Service._Field> supplier = new ResourceConfigSupplier<>(
                "/net/morimekta/providence/config/service.json", Service.kDescriptor);

        assertThat(supplier.get(), is(equalToMessage(expected)));
    }

    @Test
    public void testWithPretty() throws ProvidenceConfigException {
        ConfigSupplier<Service, Service._Field> supplier = new ResourceConfigSupplier<>(
                "/net/morimekta/providence/config/service.cfg", Service.kDescriptor);

        assertThat(supplier.get(), is(equalToMessage(expected)));
    }

    @Test
    public void testName() throws ProvidenceConfigException {
        ConfigSupplier<Service, Service._Field> supplier = new ResourceConfigSupplier<>(
                "/net/morimekta/providence/config/service.cfg", Service.kDescriptor);

        assertThat(supplier.getName(), is("ResourceConfig{/net/morimekta/providence/config/service.cfg}"));
        assertThat(supplier.toString(), is("ResourceConfig{/net/morimekta/providence/config/service.cfg}"));
    }
}
