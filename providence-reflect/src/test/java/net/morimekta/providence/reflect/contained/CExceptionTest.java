package net.morimekta.providence.reflect.contained;

import net.morimekta.providence.descriptor.PPrimitive;
import net.morimekta.providence.descriptor.PRequirement;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.junit.Assert.assertThat;

public class CExceptionTest {
    @Test
    public void testException() {
        CField text = new CField("", 1, PRequirement.REQUIRED, "text", PPrimitive.STRING.provider(), null, null);
        CField num = new CField("", 2, PRequirement.OPTIONAL, "num", PPrimitive.I32.provider(), null, null);
        CExceptionDescriptor descriptor = new CExceptionDescriptor("comment",
                                                                   "program",
                                                                   "MyEx",
                                                                   ImmutableList.of(text, num),
                                                                   null);

        assertThat(descriptor.getDocumentation(), is("comment"));
        assertThat(descriptor.getField("text"), is(sameInstance(text)));
        assertThat(descriptor.getField(2), is(sameInstance(num)));
        assertThat(descriptor.getAnnotations(), is(ImmutableSet.of()));
        assertThat(descriptor.hasAnnotation("boo"), is(false));
        assertThat(descriptor.getAnnotationValue("boo"), is(nullValue()));
        assertThat(descriptor.isSimple(), is(true));

        CException ex = new CException.Builder(descriptor).set(text, "text")
                                                          .build();

        assertThat(ex.has(text), is(true));
        assertThat(ex.has(num), is(false));
        assertThat(ex.has(5), is(false));

        assertThat(ex.num(text), is(0));
        assertThat(ex.num(num), is(0));

        assertThat(ex.get(text), is("text"));
        assertThat(ex.get(num), is(0));
        assertThat(ex.get(5), is(nullValue()));

        CException other = new CException.Builder(descriptor).set(text, "diff")
                                                             .build();
        CException same = new CException.Builder(descriptor).set(text, "text")
                                                            .build();

        assertThat(ex.equals(ex), is(true));
        assertThat(ex.equals(same), is(true));
        assertThat(ex.equals(other), is(false));
        assertThat(ex.equals(null), is(false));
        assertThat(ex.equals(new Object()), is(false));

        assertThat(ex.hashCode(), is(same.hashCode()));
        assertThat(ex.hashCode(), is(not(other.hashCode())));

        assertThat(ex.compareTo(same), is(0));
        assertThat(ex.compareTo(other), is(not(0)));

        assertThat(ex.toString(), is("program.MyEx{text=\"text\"}"));
        assertThat(ex.asString(), is("{text=\"text\"}"));
    }
}
