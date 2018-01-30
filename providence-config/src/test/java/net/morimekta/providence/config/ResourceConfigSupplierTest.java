package net.morimekta.providence.config;

import com.google.common.collect.ImmutableMap;
import net.morimekta.test.providence.config.Service;
import net.morimekta.test.providence.config.ServicePort;
import net.morimekta.util.Binary;
import org.junit.Before;
import org.junit.Test;

import static net.morimekta.providence.testing.ProvidenceMatchers.equalToMessage;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;


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
    public void testWithBadConfig() {
        try {
            new ResourceConfigSupplier<>("/net/morimekta/providence/config/bad.config", Service.kDescriptor);
            fail("no exception");
        } catch (ProvidenceConfigException e) {
            assertThat(e.asString(),
                       is("Error in bad.config on line 1, pos 1: Expected qualifier config.Service or message start, Got 'config.DoesNotExist'\n" +
                          "config.DoesNotExist {\n" +
                          "^^^^^^^^^^^^^^^^^^^"));
        }
    }

    @Test
    public void testWithBadJson() {
        try {
            new ResourceConfigSupplier<>("/net/morimekta/providence/config/bad.json", Service.kDescriptor);
            fail("no exception");
        } catch (ProvidenceConfigException e) {
            assertThat(e.asString(),
                       is("Error in bad.json on line 2, pos 5: Wrongly terminated JSON number: '12:'\n" +
                          "  12: \"bar\"\n" +
                          "----^"));
        }
    }

    @Test
    public void testWithBadFormat() {
        try {
            new ResourceConfigSupplier<>("/net/morimekta/providence/config/bad.yaml", Service.kDescriptor);
            fail("no exception");
        } catch (ProvidenceConfigException e) {
            assertThat(e.asString(),
                       is("Error: Unrecognized resource config type: .yaml (/net/morimekta/providence/config/bad.yaml)"));
        }
    }

    @Test
    public void testWithBadName() {
        try {
            new ResourceConfigSupplier<>("/net/morimekta/providence/config/bad", Service.kDescriptor);
            fail("no exception");
        } catch (ProvidenceConfigException e) {
            assertThat(e.asString(),
                       is("Error: No file ending, or no resource file name: /net/morimekta/providence/config/bad"));
        }
    }

    @Test
    public void testNoSuchResource() {
        try {
            new ResourceConfigSupplier<>("/net/morimekta/providence/config/does.not.config", Service.kDescriptor);
            fail("no exception");
        } catch (ProvidenceConfigException e) {
            assertThat(e.asString(),
                       is("Error: No such config resource: /net/morimekta/providence/config/does.not.config"));
        }

    }

    @Test
    public void testName() throws ProvidenceConfigException {
        ConfigSupplier<Service, Service._Field> supplier = new ResourceConfigSupplier<>(
                "/net/morimekta/providence/config/service.cfg", Service.kDescriptor);

        assertThat(supplier.getName(), is("ResourceConfig{/net/morimekta/providence/config/service.cfg}"));
        assertThat(supplier.toString(), is("ResourceConfig{/net/morimekta/providence/config/service.cfg}"));
    }
}
