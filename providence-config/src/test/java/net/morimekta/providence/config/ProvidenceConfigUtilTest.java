package net.morimekta.providence.config;

import net.morimekta.providence.model.ConstType;
import net.morimekta.providence.model.Declaration;

import org.junit.Test;

import static net.morimekta.providence.config.ProvidenceConfigUtil.getInMessage;
import static org.junit.Assert.assertEquals;

/**
 * Testing for the providence config utils.
 */
public class ProvidenceConfigUtilTest {
    @Test
    public void testGetInMessage() {
        Declaration declaration = Declaration.withDeclConst(ConstType.builder()
                                                                     .setName("Name")
                                                                     .setType("i32")
                                                                     .setValue("44")
                                                                     .build());

        // Return the field value.
        assertEquals("44", getInMessage(declaration, "decl_const.value"));
        assertEquals("Name", getInMessage(declaration, "decl_const.name"));
        // Return the field value even when default is set.
        assertEquals("44", getInMessage(declaration, "decl_const.value", "66"));
        // Return null when there are no default in thrift, and none specified.
        assertEquals(null, getInMessage(declaration, "decl_const.documentation"));
    }
}
