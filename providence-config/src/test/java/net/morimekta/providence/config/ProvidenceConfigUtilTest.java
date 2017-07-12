package net.morimekta.providence.config;

import net.morimekta.config.IncompatibleValueException;
import net.morimekta.config.KeyNotFoundException;
import net.morimekta.providence.model.ConstType;
import net.morimekta.providence.model.Declaration;
import net.morimekta.providence.util.SimpleTypeRegistry;
import net.morimekta.test.providence.config.Service;
import net.morimekta.test.providence.config.ServicePort;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;
import java.util.function.Supplier;

import static net.morimekta.testing.ResourceUtils.copyResourceTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

/**
 * Testing for the providence config utils.
 */
public class ProvidenceConfigUtilTest {
    @Rule
    public TemporaryFolder tmp = new TemporaryFolder();

    private Declaration declaration;
    private Supplier<Service> service;

    @Before
    public void setUp() throws IOException {
        declaration = Declaration.withDeclConst(ConstType.builder()
                                                         .setName("Name")
                                                         .setType("i32")
                                                         .setValue("44")
                                                         .build());
        SimpleTypeRegistry registry = new SimpleTypeRegistry();
        registry.registerRecursively(Service.kDescriptor);

        copyResourceTo("/net/morimekta/providence/config/base_service.cfg", tmp.getRoot());
        copyResourceTo("/net/morimekta/providence/config/stage_db.cfg", tmp.getRoot());

        File cfg = copyResourceTo("/net/morimekta/providence/config/stage.cfg", tmp.getRoot());

        service = new ProvidenceConfig(registry).getSupplier(cfg);
    }

    @Test
    public void testGetInMessage() {
        // Return the field value.
        assertEquals("44", ProvidenceConfigUtil.getInMessage(declaration, "decl_const.value"));
        assertEquals("Name", ProvidenceConfigUtil.getInMessage(declaration, "decl_const.name"));
        // Return the field value even when default is set.
        assertEquals("44", ProvidenceConfigUtil.getInMessage(declaration, "decl_const.value", "66"));
        // Return null when there are no default in thrift, and none specified.
        assertEquals(null, ProvidenceConfigUtil.getInMessage(declaration, "decl_const.documentation"));

        assertThat(ProvidenceConfigUtil.getInMessage(service.get(), "name"), is("stage"));
        assertThat(ProvidenceConfigUtil.getInMessage(service.get(), "admin"), is(nullValue()));
        ServicePort def = ServicePort.builder().build();
        assertThat(ProvidenceConfigUtil.getInMessage(service.get(), "admin", def), is(sameInstance(def)));
    }

    @Test
    public void testGetInMessage_fail() {
        try {
            ProvidenceConfigUtil.getInMessage(service.get(), "does_not_exist");
            fail("No exception");
        } catch (KeyNotFoundException e) {
            assertThat(e.getMessage(), is("Message config.Service has no field named does_not_exist"));
        }

        try {
            ProvidenceConfigUtil.getInMessage(service.get(), "does_not_exist.name");
            fail("No exception");
        } catch (KeyNotFoundException e) {
            assertThat(e.getMessage(), is("Message config.Service has no field named does_not_exist"));
        }

        try {
            ProvidenceConfigUtil.getInMessage(service.get(), "db.does_not_exist");
            fail("No exception");
        } catch (KeyNotFoundException e) {
            assertThat(e.getMessage(), is("Message config.Database has no field named does_not_exist"));
        }

        try {
            ProvidenceConfigUtil.getInMessage(service.get(), "name.db");
            fail("No exception");
        } catch (IncompatibleValueException e) {
            assertThat(e.getMessage(), is("Field 'name' is not of message type in config.Service"));
        }
    }
}
