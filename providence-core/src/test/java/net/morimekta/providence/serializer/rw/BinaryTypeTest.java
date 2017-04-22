package net.morimekta.providence.serializer.rw;

import net.morimekta.providence.PType;

import org.junit.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

public class BinaryTypeTest {
    @Test
    public void testAsString() {
        assertThat(BinaryType.asString(BinaryType.STOP), is("stop(0)"));
        assertThat(BinaryType.asString(BinaryType.VOID), is("void(1)"));
        assertThat(BinaryType.asString(BinaryType.BOOL), is("bool(2)"));
        assertThat(BinaryType.asString(BinaryType.BYTE), is("byte(3)"));
        assertThat(BinaryType.asString(BinaryType.DOUBLE), is("double(4)"));
        assertThat(BinaryType.asString(BinaryType.I16), is("i16(6)"));
        assertThat(BinaryType.asString(BinaryType.I32), is("i32(8)"));
        assertThat(BinaryType.asString(BinaryType.I64), is("i64(10)"));
        assertThat(BinaryType.asString(BinaryType.STRING), is("string(11)"));
        assertThat(BinaryType.asString(BinaryType.STRUCT), is("struct(12)"));
        assertThat(BinaryType.asString(BinaryType.MAP), is("map(13)"));
        assertThat(BinaryType.asString(BinaryType.SET), is("set(14)"));
        assertThat(BinaryType.asString(BinaryType.LIST), is("list(15)"));
        assertThat(BinaryType.asString((byte) 7), is("unknown(7)"));
    }

    @Test
    public void testForType() {
        assertThat(BinaryType.forType(PType.VOID),   is(BinaryType.VOID));
        assertThat(BinaryType.forType(PType.BOOL),   is(BinaryType.BOOL));
        assertThat(BinaryType.forType(PType.BYTE),   is(BinaryType.BYTE));
        assertThat(BinaryType.forType(PType.DOUBLE), is(BinaryType.DOUBLE));
        assertThat(BinaryType.forType(PType.I16),    is(BinaryType.I16));
        assertThat(BinaryType.forType(PType.I32),    is(BinaryType.I32));
        assertThat(BinaryType.forType(PType.I64),    is(BinaryType.I64));
        assertThat(BinaryType.forType(PType.STRING), is(BinaryType.STRING));
        assertThat(BinaryType.forType(PType.BINARY), is(BinaryType.STRING));
        assertThat(BinaryType.forType(PType.MESSAGE),is(BinaryType.STRUCT));
        assertThat(BinaryType.forType(PType.ENUM),   is(BinaryType.I32));
        assertThat(BinaryType.forType(PType.MAP),    is(BinaryType.MAP));
        assertThat(BinaryType.forType(PType.SET),    is(BinaryType.SET));
        assertThat(BinaryType.forType(PType.LIST),   is(BinaryType.LIST));
    }

    @Test
    public void testConstructor()
            throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        Constructor<BinaryType> c = BinaryType.class.getDeclaredConstructor();
        assertThat(c.isAccessible(), is(false));
        c.setAccessible(true);
        assertThat(c.newInstance(), is(notNullValue()));
        c.setAccessible(false);
    }
}
