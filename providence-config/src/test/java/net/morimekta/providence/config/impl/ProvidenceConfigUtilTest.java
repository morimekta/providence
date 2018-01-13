package net.morimekta.providence.config.impl;

import net.morimekta.providence.config.ProvidenceConfig;
import net.morimekta.providence.config.ProvidenceConfigException;
import net.morimekta.providence.model.ConstType;
import net.morimekta.providence.model.Declaration;
import net.morimekta.providence.util.SimpleTypeRegistry;
import net.morimekta.test.providence.config.Service;
import net.morimekta.test.providence.config.ServicePort;
import net.morimekta.util.Numeric;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import static net.morimekta.providence.config.impl.ProvidenceConfigUtil.asBoolean;
import static net.morimekta.providence.config.impl.ProvidenceConfigUtil.asInteger;
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
    private Service service;

    @Before
    public void setUp() throws IOException {
        declaration = Declaration.withDeclConst(ConstType.builder()
                                                         .setName("Name")
                                                         .setType("i32")
                                                         .setValue("44")
                                                         .build());
        SimpleTypeRegistry registry = new SimpleTypeRegistry();
        registry.registerRecursively(Service.kDescriptor);

        copyResourceTo("/net/morimekta/providence/config/files/base_service.cfg", tmp.getRoot());
        copyResourceTo("/net/morimekta/providence/config/files/stage_db.cfg", tmp.getRoot());

        File cfg = copyResourceTo("/net/morimekta/providence/config/files/stage.cfg", tmp.getRoot());

        service = new ProvidenceConfig(registry).getConfig(cfg);
    }

    @Test
    public void testGetInMessage() throws ProvidenceConfigException {
        // Return the field value.
        assertEquals("44", ProvidenceConfigUtil.getInMessage(declaration, "decl_const.value"));
        assertEquals("Name", ProvidenceConfigUtil.getInMessage(declaration, "decl_const.name"));
        // Return the field value even when default is set.
        assertEquals("44", ProvidenceConfigUtil.getInMessage(declaration, "decl_const.value", "66"));
        // Return null when there are no default in thrift, and none specified.
        assertEquals(null, ProvidenceConfigUtil.getInMessage(declaration, "decl_const.documentation"));

        assertThat(ProvidenceConfigUtil.getInMessage(service, "name"), is("stage"));
        assertThat(ProvidenceConfigUtil.getInMessage(service, "admin"), is(nullValue()));
        ServicePort def = ServicePort.builder().build();
        assertThat(ProvidenceConfigUtil.getInMessage(service, "admin", def), is(sameInstance(def)));
    }

    @Test
    public void testGetInMessage_fail() {
        try {
            ProvidenceConfigUtil.getInMessage(service, "does_not_exist");
            fail("No exception");
        } catch (ProvidenceConfigException e) {
            assertThat(e.getMessage(), is("Message config.Service has no field named does_not_exist"));
        }

        try {
            ProvidenceConfigUtil.getInMessage(service, "does_not_exist.name");
            fail("No exception");
        } catch (ProvidenceConfigException e) {
            assertThat(e.getMessage(), is("Message config.Service has no field named does_not_exist"));
        }

        try {
            ProvidenceConfigUtil.getInMessage(service, "db.does_not_exist");
            fail("No exception");
        } catch (ProvidenceConfigException e) {
            assertThat(e.getMessage(), is("Message config.Database has no field named does_not_exist"));
        }

        try {
            ProvidenceConfigUtil.getInMessage(service, "name.db");
            fail("No exception");
        } catch (ProvidenceConfigException e) {
            assertThat(e.getMessage(), is("Field 'name' is not of message type in config.Service"));
        }
    }

    @Test
    public void testAsBoolean() throws ProvidenceConfigException {
        assertThat(asBoolean("true"), is(true));
        assertThat(asBoolean(true), is(true));
        assertThat(asBoolean(1L), is(true));
        assertThat(asBoolean((byte) 0), is(false));
        assertThat(asBoolean(new StringBuilder("F")), is(false));
        try {
            asBoolean(new Object());
            fail("no exception");
        } catch (ProvidenceConfigException e) {
            assertThat(e.getMessage(), is("Unable to convert Object to a boolean"));
        }
        try {
            asBoolean("foo");
            fail("no exception");
        } catch (ProvidenceConfigException e) {
            assertThat(e.getMessage(), is("Unable to parse the string \"foo\" to boolean"));
        }
        try {
            asBoolean(111);
            fail("no exception");
        } catch (ProvidenceConfigException e) {
            assertThat(e.getMessage(), is("Unable to convert number 111 to boolean"));
        }
        try {
            asBoolean(1.0);
            fail("no exception");
        } catch (ProvidenceConfigException e) {
            assertThat(e.getMessage(), is("Unable to convert real value to boolean"));
        }
    }

    @Test
    public void testAsInteger() throws ProvidenceConfigException {
        assertThat(asInteger(2,
                             Integer.MIN_VALUE, Integer.MAX_VALUE), is(2));
        assertThat(asInteger("1234",
                             Integer.MIN_VALUE, Integer.MAX_VALUE), is(1234));
        assertThat(asInteger("0xff",
                             Integer.MIN_VALUE, Integer.MAX_VALUE), is(0xff));
        assertThat(asInteger("0777",
                             Integer.MIN_VALUE, Integer.MAX_VALUE), is(511));
        assertThat(asInteger((Numeric) () -> 111,
                             Integer.MIN_VALUE, Integer.MAX_VALUE), is(111));
        assertThat(asInteger(new StringBuilder("111"),
                             Integer.MIN_VALUE, Integer.MAX_VALUE), is(111));
        assertThat(asInteger(false,
                             Integer.MIN_VALUE, Integer.MAX_VALUE), is(0));
        assertThat(asInteger(new Date(1234567890000L),
                             Integer.MIN_VALUE, Integer.MAX_VALUE), is(1234567890));
        assertThat(asInteger(12345.0,
                             Integer.MIN_VALUE, Integer.MAX_VALUE), is(12345));

        try {
            asInteger(new StringBuilder("foo"), Integer.MIN_VALUE, Integer.MAX_VALUE);
            fail("no exception");
        } catch (ProvidenceConfigException e) {
            assertThat(e.getMessage(), is("Unable to parse string \"foo\" to an int"));
        }
        try {
            asInteger(1234567890123456789L, Integer.MIN_VALUE, Integer.MAX_VALUE);
            fail("no exception");
        } catch (ProvidenceConfigException e) {
            assertThat(e.getMessage(), is("Long value outsize of bounds: 1234567890123456789 > 2147483647"));
        }
        try {
            asInteger(-1234, Byte.MIN_VALUE, Byte.MAX_VALUE);
            fail("no exception");
        } catch (ProvidenceConfigException e) {
            assertThat(e.getMessage(), is("Integer value outsize of bounds: -1234 < -128"));
        }
        try {
            asInteger(12.345, Integer.MIN_VALUE, Integer.MAX_VALUE);
            fail("no exception");
        } catch (ProvidenceConfigException e) {
            assertThat(e.getMessage(), is("Truncating integer decimals from 12.345"));
        }
        try {
            asInteger(new Object(), Byte.MIN_VALUE, Byte.MAX_VALUE);
            fail("no exception");
        } catch (ProvidenceConfigException e) {
            assertThat(e.getMessage(), is("Unable to convert Object to an int"));
        }
    }

    @Test
    public void testAsLong() {

    }
}
