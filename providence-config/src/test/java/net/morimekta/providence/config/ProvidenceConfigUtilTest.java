package net.morimekta.providence.config;

import net.morimekta.config.IncompatibleValueException;
import net.morimekta.config.KeyNotFoundException;
import net.morimekta.providence.model.ConstType;
import net.morimekta.providence.model.Declaration;
import net.morimekta.providence.testing.util.ResourceUtils;
import net.morimekta.providence.util.TypeRegistry;
import net.morimekta.test.config.Database;
import net.morimekta.test.config.Service;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import static net.morimekta.providence.config.ProvidenceConfigUtil.getInMessage;
import static net.morimekta.providence.testing.util.ResourceUtils.copyResourceTo;
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
    private Service service;

    @Before
    public void setUp() throws IOException {
        declaration = Declaration.withDeclConst(ConstType.builder()
                                                         .setName("Name")
                                                         .setType("i32")
                                                         .setValue("44")
                                                         .build());
        TypeRegistry registry = new TypeRegistry();
        registry.registerRecursively(Service.kDescriptor);

        copyResourceTo("/net/morimekta/providence/config/base_service.cfg", tmp.getRoot());
        copyResourceTo("/net/morimekta/providence/config/stage_db.cfg", tmp.getRoot());

        File cfg = ResourceUtils.copyResourceTo("/net/morimekta/providence/config/stage.cfg", tmp.getRoot());

        service = new ProvidenceConfig(registry, new HashMap<>()).load(cfg);
    }

    @Test
    public void testGetInMessage() {
        // Return the field value.
        assertEquals("44", getInMessage(declaration, "decl_const.value"));
        assertEquals("Name", getInMessage(declaration, "decl_const.name"));
        // Return the field value even when default is set.
        assertEquals("44", getInMessage(declaration, "decl_const.value", "66"));
        // Return null when there are no default in thrift, and none specified.
        assertEquals(null, getInMessage(declaration, "decl_const.documentation"));

        assertThat(getInMessage(service, "name"), is("stage"));
        assertThat(getInMessage(service, "db"), is(nullValue()));
        Database def = Database.builder().build();
        assertThat(getInMessage(service, "db", def), is(sameInstance(def)));
    }

    @Test
    public void testGetInMessage_fail() {
        try {
            getInMessage(service, "does_not_exist");
            fail("No exception");
        } catch (KeyNotFoundException e) {
            assertThat(e.getMessage(), is("Message config.Service has no field named does_not_exist"));
        }

        try {
            getInMessage(service, "does_not_exist.name");
            fail("No exception");
        } catch (KeyNotFoundException e) {
            assertThat(e.getMessage(), is("Message config.Service has no field named does_not_exist"));
        }

        try {
            getInMessage(service, "db.does_not_exist");
            fail("No exception");
        } catch (KeyNotFoundException e) {
            assertThat(e.getMessage(), is("Message config.Database has no field named does_not_exist"));
        }

        try {
            getInMessage(service, "name.db");
            fail("No exception");
        } catch (IncompatibleValueException e) {
            assertThat(e.getMessage(), is("Field 'name' is not of message type in config.Service"));
        }
    }
}
