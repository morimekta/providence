package net.morimekta.providence.config;

import net.morimekta.providence.model.Declaration;
import net.morimekta.providence.model.Requirement;
import net.morimekta.providence.model.ThriftField;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Testing for the providence config utils.
 */
public class ProvidenceConfigUtilTest {
    @Test
    public void testGetInMessage() {
        Declaration declaration = Declaration.withDeclConst(
                ThriftField.builder()
                           .setName("Name")
                           .setKey(44)
                           .setType("i32")
                           .build());

        assertEquals(44, ProvidenceConfigUtil.getInMessage(declaration, "decl_const.key"));
        assertEquals(44, ProvidenceConfigUtil.getInMessage(declaration, "decl_const.key", 66));
        assertEquals(null, ProvidenceConfigUtil.getInMessage(declaration, "decl_const.requirement"));
        assertEquals(Requirement.REQUIRED, ProvidenceConfigUtil.getInMessage(declaration, "decl_const.requirement", Requirement.REQUIRED));
    }
}
