package net.morimekta.providence.descriptor;

import net.morimekta.util.Binary;

import org.junit.Test;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

public class PContainerTest {
    @Test
    public void testList() {
        PContainerProvider<List<Binary>, PList<Binary>> binaryProvider =
                PList.provider(PPrimitive.BINARY.provider());

        PList<Binary> list = binaryProvider.descriptor();

        assertThat(list.getProgramName(), is(nullValue()));
        assertThat(list.getName(), is("list<binary>"));
        assertThat(list.toString(), is("list<binary>"));

        PContainerProvider<List<Binary>, PList<Binary>> otherBinaryProvider =
                PList.provider(PPrimitive.BINARY.provider());

        assertThat(list, is(otherBinaryProvider.descriptor()));
        assertThat(list.hashCode(), is(otherBinaryProvider.descriptor().hashCode()));

        PContainerProvider<List<Integer>, PList<Integer>> i32Provider =
                PList.provider(PPrimitive.I32.provider());

        assertThat(list, is(not(i32Provider.descriptor())));
        assertThat(list.equals(null), is(false));
        assertThat(list.hashCode(), is(not(i32Provider.descriptor().hashCode())));
    }

    @Test
    public void testSet() {
        PContainerProvider<Set<Binary>, PSet<Binary>> binaryProvider =
                PSet.provider(PPrimitive.BINARY.provider());

        PSet<Binary> set = binaryProvider.descriptor();

        assertThat(set.getProgramName(), is(nullValue()));
        assertThat(set.getName(), is("set<binary>"));
        assertThat(set.toString(), is("set<binary>"));

        PContainerProvider<Set<Binary>, PSet<Binary>> otherBinaryProvider =
                PSet.provider(PPrimitive.BINARY.provider());

        assertThat(set, is(otherBinaryProvider.descriptor()));
        assertThat(set.hashCode(), is(otherBinaryProvider.descriptor().hashCode()));

        PContainerProvider<Set<Integer>, PSet<Integer>> i32Provider =
                PSet.provider(PPrimitive.I32.provider());

        assertThat(set, is(not(i32Provider.descriptor())));
        assertThat(set.equals(null), is(false));
        assertThat(set.hashCode(), is(not(i32Provider.descriptor().hashCode())));
    }

    @Test
    public void testMap() {
        PContainerProvider<Map<Integer,Binary>, PMap<Integer,Binary>> binaryProvider =
                PMap.provider(PPrimitive.I32.provider(), PPrimitive.BINARY.provider());

        PMap<Integer,Binary> set = binaryProvider.descriptor();

        assertThat(set.getProgramName(), is(nullValue()));
        assertThat(set.getName(), is("map<i32,binary>"));
        assertThat(set.toString(), is("map<i32,binary>"));

        PContainerProvider<Map<Integer,Binary>, PMap<Integer,Binary>> otherBinaryProvider =
                PMap.provider(PPrimitive.I32.provider(), PPrimitive.BINARY.provider());

        assertThat(set, is(otherBinaryProvider.descriptor()));
        assertThat(set.hashCode(), is(otherBinaryProvider.descriptor().hashCode()));

        PContainerProvider<Map<Integer,Integer>, PMap<Integer,Integer>> i32Provider =
                PMap.provider(PPrimitive.I32.provider(), PPrimitive.I32.provider());

        assertThat(set, is(not(i32Provider.descriptor())));
        assertThat(set.equals(null), is(false));
        assertThat(set.hashCode(), is(not(i32Provider.descriptor().hashCode())));
    }
}
