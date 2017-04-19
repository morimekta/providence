package net.morimekta.hazelcast.it;

import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import org.junit.Test;

import java.util.stream.Collectors;

import static net.morimekta.providence.testing.ProvidenceMatchers.equalToMessage;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

/**
 * TBD
 */
public class HazelcastVersion3Test extends GenericMethods {

    @Test
    public void testVersion3OptionalFieldsAll() throws InterruptedException {
        String mapName = nextString();
        HazelcastInstance instance1 = Hazelcast.newHazelcastInstance(getV3Config());
        HazelcastInstance instance2 = Hazelcast.newHazelcastInstance(getV3Config());

        IMap<String, net.morimekta.test.hazelcast.v3.OptionalFields._Builder> writeMap = instance1.getMap(mapName);
        IMap<String, net.morimekta.test.hazelcast.v3.OptionalFields._Builder> readMap = instance2.getMap(mapName);

        net.morimekta.test.hazelcast.v3.OptionalFields expected = generator.nextOptionalFieldsV3(true);

        String key = nextString();
        writeMap.put(key, expected.mutate());

        net.morimekta.test.hazelcast.v3.OptionalFields actual = readMap.get(key)
                                                                       .build();

        assertThat(expected, is(actual));

        net.morimekta.test.hazelcast.v3.OptionalFields newExpected = actual.mutate()
                                                                           .setAnotherStringValue(
                                                                                   actual.getAnotherStringValue() + "asdf")
                                                                           .setDoubleValue(
                                                                                   actual.getDoubleValue() - 0.12345)
                                                                           .setBooleanValue(!actual.isBooleanValue())
                                                                           .setIntegerValue(
                                                                                   actual.getIntegerValue() - 12345)
                                                                           .setLongValue(actual.getLongValue() - 123456)
                                                                           .setShortValue((short) (
                                                                                   actual.getShortValue() - 1234))
                                                                           .build();

        readMap.put(key, newExpected.mutate());

        net.morimekta.test.hazelcast.v3.OptionalFields newActual = writeMap.get(key)
                                                                           .build();

        assertThat(newExpected.toString(), is(newActual.toString()));
        assertThat(newExpected, is(equalToMessage(newActual)));
        assertThat(newExpected.hashCode(), is(newActual.hashCode()));
        assertThat(newExpected, is(newActual));
    }

    @Test
    public void testVersion3OptionalFieldsRand() throws InterruptedException {
        String mapName = nextString();
        HazelcastInstance instance1 = Hazelcast.newHazelcastInstance(getV3Config());
        HazelcastInstance instance2 = Hazelcast.newHazelcastInstance(getV3Config());

        IMap<String, net.morimekta.test.hazelcast.v3.OptionalFields._Builder> writeMap = instance1.getMap(mapName);
        IMap<String, net.morimekta.test.hazelcast.v3.OptionalFields._Builder> readMap = instance2.getMap(mapName);

        net.morimekta.test.hazelcast.v3.OptionalFields expected = generator.nextOptionalFieldsV3();

        String key = nextString();
        writeMap.put(key, expected.mutate());

        net.morimekta.test.hazelcast.v3.OptionalFields actual = readMap.get(key)
                                                                       .build();

        assertThat(expected, is(actual));

        net.morimekta.test.hazelcast.v3.OptionalFields newExpected = actual.mutate()
                                                                           .setAnotherStringValue(
                                                                                   actual.getAnotherStringValue() + "asdf")
                                                                           .setDoubleValue(
                                                                                   actual.getDoubleValue() - 0.12345)
                                                                           .setBooleanValue(!actual.isBooleanValue())
                                                                           .setIntegerValue(
                                                                                   actual.getIntegerValue() - 12345)
                                                                           .setLongValue(actual.getLongValue() - 123456)
                                                                           .setShortValue((short) (
                                                                                   actual.getShortValue() - 1234))
                                                                           .build();

        readMap.put(key, newExpected.mutate());

        net.morimekta.test.hazelcast.v3.OptionalFields newActual = writeMap.get(key)
                                                                           .build();

        assertThat(newExpected.toString(), is(newActual.toString()));
        assertThat(newExpected, is(equalToMessage(newActual)));
        assertThat(newExpected.hashCode(), is(newActual.hashCode()));
        assertThat(newExpected, is(newActual));
    }

    @Test
    public void testVersion3OptionalListFieldsAll() throws InterruptedException {
        String mapName = nextString();
        HazelcastInstance instance1 = Hazelcast.newHazelcastInstance(getV3Config());
        HazelcastInstance instance2 = Hazelcast.newHazelcastInstance(getV3Config());

        IMap<String, net.morimekta.test.hazelcast.v3.OptionalListFields._Builder> writeMap = instance1.getMap(mapName);
        IMap<String, net.morimekta.test.hazelcast.v3.OptionalListFields._Builder> readMap = instance2.getMap(mapName);

        net.morimekta.test.hazelcast.v3.OptionalListFields expected = generator.nextOptionalListFieldsV3(true);

        String key = nextString();
        writeMap.put(key, expected.mutate());

        net.morimekta.test.hazelcast.v3.OptionalListFields actual = readMap.get(key)
                                                                           .build();

        assertThat(expected, is(actual));

        net.morimekta.test.hazelcast.v3.OptionalListFields newExpected =
                actual.mutate()
                      .setAnotherStringValues(actual.getAnotherStringValues().stream().filter(t -> t.contains("ab"))
                                            .collect(Collectors.toList()))
                      .setDoubleValue(actual.getDoubleValue().stream().filter(t -> t < 0.5)
                                            .collect(Collectors.toList()))
                      .setBooleanValues(actual.getBooleanValues().stream().map(t -> !t).limit(25)
                                              .collect(Collectors.toList()))
                      .setIntegerValue(actual.getIntegerValue().stream().limit(25).collect(Collectors.toList()))
                      .setLongValue(actual.getLongValue().stream().limit(23).collect(Collectors.toList()))
                      .setShortValues(actual.getShortValues().stream().limit(12).collect(Collectors.toList()))
                      .build();

        readMap.put(key, newExpected.mutate());

        net.morimekta.test.hazelcast.v3.OptionalListFields newActual = writeMap.get(key)
                                                                               .build();

        assertThat(newExpected.toString(), is(newActual.toString()));
        assertThat(newExpected, is(equalToMessage(newActual)));
        assertThat(newExpected.hashCode(), is(newActual.hashCode()));
        assertThat(newExpected, is(newActual));
    }

    @Test
    public void testVersion3OptionalListFieldsRand() throws InterruptedException {
        String mapName = nextString();
        HazelcastInstance instance1 = Hazelcast.newHazelcastInstance(getV3Config());
        HazelcastInstance instance2 = Hazelcast.newHazelcastInstance(getV3Config());

        IMap<String, net.morimekta.test.hazelcast.v3.OptionalListFields._Builder> writeMap = instance1.getMap(mapName);
        IMap<String, net.morimekta.test.hazelcast.v3.OptionalListFields._Builder> readMap = instance2.getMap(mapName);

        net.morimekta.test.hazelcast.v3.OptionalListFields expected = generator.nextOptionalListFieldsV3();

        String key = nextString();
        writeMap.put(key, expected.mutate());

        net.morimekta.test.hazelcast.v3.OptionalListFields actual = readMap.get(key)
                                                                           .build();

        assertThat(expected.toString(), is(actual.toString()));
        assertThat(expected, is(equalToMessage(actual)));
        assertThat(expected.hashCode(), is(actual.hashCode()));
        assertThat(expected, is(actual));

        net.morimekta.test.hazelcast.v3.OptionalListFields._Builder newBuilder = actual.mutate();
        if( actual.hasAnotherStringValues() ) {
            newBuilder.setAnotherStringValues(actual.getAnotherStringValues()
                                             .stream()
                                             .filter(t -> t.contains("ab"))
                                             .collect(Collectors.toList()));
        }
        if( actual.hasDoubleValue() ) {
            newBuilder.setDoubleValue(actual.getDoubleValue()
                                             .stream()
                                             .filter(t -> t < 0.5)
                                             .collect(Collectors.toList()));
        }
        if( actual.hasBooleanValues() ) {
            newBuilder.setBooleanValues(actual.getBooleanValues()
                                               .stream()
                                               .map(t -> !t)
                                               .limit(actual.numBooleanValues() - rand.nextInt(actual.numBooleanValues()))
                                               .collect(Collectors.toList()));
        }
        if( actual.hasIntegerValue() ) {
            newBuilder.setIntegerValue(actual.getIntegerValue()
                                              .stream()
                                              .limit(actual.numIntegerValue() - rand.nextInt(actual.numIntegerValue()))
                                              .collect(Collectors.toList()));
        }
        if( actual.hasLongValue() ) {
            newBuilder.setLongValue(actual.getLongValue()
                                           .stream()
                                           .limit(actual.numLongValue() - rand.nextInt(actual.numLongValue()))
                                           .collect(Collectors.toList()));
        }
        if( actual.hasShortValues() ) {
            newBuilder.setShortValues(actual.getShortValues()
                                             .stream()
                                             .limit(actual.numShortValues() - rand.nextInt(actual.numShortValues()))
                                             .collect(Collectors.toList()));
        }

        net.morimekta.test.hazelcast.v3.OptionalListFields newExpected = newBuilder.build();

        readMap.put(key, newExpected.mutate());

        net.morimekta.test.hazelcast.v3.OptionalListFields newActual = writeMap.get(key)
                                                                               .build();

        assertThat(newExpected.toString(), is(newActual.toString()));
        assertThat(newExpected, is(equalToMessage(newActual)));
        assertThat(newExpected.hashCode(), is(newActual.hashCode()));
        assertThat(newExpected, is(newActual));
    }

    @Test
    public void testVersion3OptionalSetFieldsAll() throws InterruptedException {
        String mapName = nextString();
        HazelcastInstance instance1 = Hazelcast.newHazelcastInstance(getV3Config());
        HazelcastInstance instance2 = Hazelcast.newHazelcastInstance(getV3Config());

        IMap<String, net.morimekta.test.hazelcast.v3.OptionalSetFields._Builder> writeMap = instance1.getMap(mapName);
        IMap<String, net.morimekta.test.hazelcast.v3.OptionalSetFields._Builder> readMap = instance2.getMap(mapName);

        net.morimekta.test.hazelcast.v3.OptionalSetFields expected = generator.nextOptionalSetFieldsV3(true);

        String key = nextString();
        writeMap.put(key, expected.mutate());

        net.morimekta.test.hazelcast.v3.OptionalSetFields actual = readMap.get(key)
                                                                           .build();

        assertThat(expected, is(actual));

        net.morimekta.test.hazelcast.v3.OptionalSetFields newExpected =
                actual.mutate()
                      .setAnotherStringValues(actual.getAnotherStringValues().stream().filter(t -> t.contains("ab"))
                                                    .collect(Collectors.toList()))
                      .setDoubleValue(actual.getDoubleValue().stream().filter(t -> t < 0.5)
                                            .collect(Collectors.toList()))
                      .setBooleanValues(actual.getBooleanValues().stream().map(t -> !t).limit(25)
                                              .collect(Collectors.toList()))
                      .setIntegerValue(actual.getIntegerValue().stream().limit(25).collect(Collectors.toList()))
                      .setLongValue(actual.getLongValue().stream().limit(23).collect(Collectors.toList()))
                      .setShortValues(actual.getShortValues().stream().limit(12).collect(Collectors.toList()))
                      .build();

        readMap.put(key, newExpected.mutate());

        net.morimekta.test.hazelcast.v3.OptionalSetFields newActual = writeMap.get(key)
                                                                               .build();

        assertThat(newExpected.toString(), is(newActual.toString()));
        assertThat(newExpected, is(equalToMessage(newActual)));
        assertThat(newExpected.hashCode(), is(newActual.hashCode()));
        assertThat(newExpected, is(newActual));
    }

    @Test
    public void testVersion3OptionalSetFieldsRand() throws InterruptedException {
        String mapName = nextString();
        HazelcastInstance instance1 = Hazelcast.newHazelcastInstance(getV3Config());
        HazelcastInstance instance2 = Hazelcast.newHazelcastInstance(getV3Config());

        IMap<String, net.morimekta.test.hazelcast.v3.OptionalSetFields._Builder> writeMap = instance1.getMap(mapName);
        IMap<String, net.morimekta.test.hazelcast.v3.OptionalSetFields._Builder> readMap = instance2.getMap(mapName);

        net.morimekta.test.hazelcast.v3.OptionalSetFields expected = generator.nextOptionalSetFieldsV3();

        String key = nextString();
        writeMap.put(key, expected.mutate());

        net.morimekta.test.hazelcast.v3.OptionalSetFields actual = readMap.get(key)
                                                                           .build();

        assertThat(expected, is(equalToMessage(actual)));
        assertThat(expected.hashCode(), is(actual.hashCode()));
        assertThat(expected, is(actual));

        net.morimekta.test.hazelcast.v3.OptionalSetFields._Builder newBuilder = actual.mutate();
        if( actual.hasAnotherStringValues() ) {
            newBuilder.setAnotherStringValues(actual.getAnotherStringValues()
                                                    .stream()
                                                    .filter(t -> t.contains("ab"))
                                                    .collect(Collectors.toList()));
        }
        if( actual.hasDoubleValue() ) {
            newBuilder.setDoubleValue(actual.getDoubleValue()
                                            .stream()
                                            .filter(t -> t < 0.5)
                                            .collect(Collectors.toList()));
        }
        if( actual.hasBooleanValues() ) {
            newBuilder.setBooleanValues(actual.getBooleanValues()
                                              .stream()
                                              .map(t -> !t)
                                              .limit(actual.numBooleanValues() - rand.nextInt(actual.numBooleanValues()))
                                              .collect(Collectors.toList()));
        }
        if( actual.hasIntegerValue() ) {
            newBuilder.setIntegerValue(actual.getIntegerValue()
                                             .stream()
                                             .limit(actual.numIntegerValue() - rand.nextInt(actual.numIntegerValue()))
                                             .collect(Collectors.toList()));
        }
        if( actual.hasLongValue() ) {
            newBuilder.setLongValue(actual.getLongValue()
                                          .stream()
                                          .limit(actual.numLongValue() - rand.nextInt(actual.numLongValue()))
                                          .collect(Collectors.toList()));
        }
        if( actual.hasShortValues() ) {
            newBuilder.setShortValues(actual.getShortValues()
                                            .stream()
                                            .limit(actual.numShortValues() - rand.nextInt(actual.numShortValues()))
                                            .collect(Collectors.toList()));
        }

        net.morimekta.test.hazelcast.v3.OptionalSetFields newExpected = newBuilder.build();

        readMap.put(key, newExpected.mutate());

        net.morimekta.test.hazelcast.v3.OptionalSetFields newActual = writeMap.get(key)
                                                                               .build();

        assertThat(newExpected, is(equalToMessage(newActual)));
        assertThat(newExpected.hashCode(), is(newActual.hashCode()));
        assertThat(newExpected, is(newActual));
    }

}
