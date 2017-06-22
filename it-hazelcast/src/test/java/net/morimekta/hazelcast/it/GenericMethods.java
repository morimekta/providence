package net.morimekta.hazelcast.it;

import com.hazelcast.config.Config;
import io.codearte.jfairy.Fairy;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.ExpectedException;

import java.util.Random;
import java.util.stream.Collectors;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

/**
 * Created by scrier on 2017-01-05.
 */
public class GenericMethods {

    static { // fix logging.
        final String logging = "hazelcast.logging.type";
        System.setProperty(logging, "none");
    }

    @Rule
    public ExpectedException exception = ExpectedException.none();

    protected Random             rand;
    protected Fairy              fairy;
    protected HazelcastGenerator generator;

    @Before
    public void setup() {
        rand = new Random();
        fairy = Fairy.create();
        generator = new HazelcastGenerator();
    }

    protected static Config getV1Config() {
        Config config = new Config();
        net.morimekta.test.hazelcast.v1.Hazelcastv1_Factory.populateConfig(config);
        config.getSerializationConfig()
              .setPortableVersion(1);
        return config;
    }

    protected static Config getV2Config() {
        Config config = new Config();
        net.morimekta.test.hazelcast.v2.Hazelcastv2_Factory.populateConfig(config);
        config.getSerializationConfig()
              .setPortableVersion(2);
        return config;
    }

    protected static Config getV3Config() {
        Config config = new Config();
        net.morimekta.test.hazelcast.v3.Hazelcastv3_Factory.populateConfig(config);
        config.getSerializationConfig()
              .setPortableVersion(3);
        return config;
    }

    protected static Config getV4Config() {
        Config config = new Config();
        net.morimekta.test.hazelcast.v4.Hazelcastv4_Factory.populateConfig(config);
        config.getSerializationConfig()
              .setPortableVersion(4);
        return config;
    }

    protected String nextString() {
        return fairy.textProducer()
                    .randomString(rand.nextInt(Byte.MAX_VALUE) + 1);
    }


    protected void assertByField(net.morimekta.test.hazelcast.v2.OptionalFields v2,
                                 net.morimekta.test.hazelcast.v1.OptionalFields v1) {
        assertByField(v1, v2);
    }

    protected void assertByField(net.morimekta.test.hazelcast.v1.OptionalFields v1,
                                 net.morimekta.test.hazelcast.v2.OptionalFields v2) {
        assertThat(v1.hasBinaryValue(), is(v2.hasBinaryValue()));
        if( v1.hasBinaryValue() ) {
            assertThat(v1.getBinaryValue(), is(v2.getBinaryValue()));
        }
        assertThat(v1.hasBooleanValue(), is(v2.hasBooleanValue()));
        if( v1.hasBooleanValue() ) {
            assertThat(v1.isBooleanValue(), is(v2.isBooleanValue()));
        }
        assertThat(v1.hasByteValue(), is(v2.hasByteValue()));
        if( v1.hasByteValue() ) {
            assertThat(v1.getByteValue(), is(v2.getByteValue()));
        }
        assertThat(v1.hasCompactValue(), is(v2.hasCompactValue()));
        if( v1.hasCompactValue() ) {
            assertByField(v1.getCompactValue(), v2.getCompactValue());
        }
        assertThat(v1.hasDoubleValue(), is(v2.hasDoubleValue()));
        if( v1.hasDoubleValue() ) {
            assertThat(v1.getDoubleValue(), is(v2.getDoubleValue()));
        }
        assertThat(v1.hasEnumValue(), is(v2.hasEnumValue()));
        if( v1.hasEnumValue() ) {
            assertThat(v1.getEnumValue().asInteger(), is(v2.getEnumValue().asInteger()));
        }
        assertThat(v1.hasIntegerValue(), is(v2.hasIntegerValue()));
        if( v1.hasIntegerValue() ) {
            assertThat(v1.getIntegerValue(), is(v2.getIntegerValue()));
        }
        assertThat(v1.hasShortValue(), is(v2.hasShortValue()));
        if( v1.hasShortValue() ) {
            assertThat(v1.getShortValue(), is(v2.getShortValue()));
        }
        assertThat(v1.hasLongValue(), is(v2.hasLongValue()));
        if( v1.hasLongValue() ) {
            assertThat(v1.getLongValue(), is(v2.getLongValue()));
        }
        assertThat(v1.hasStringValue(), is(v2.hasStringValue()));
        if( v1.hasStringValue() ) {
            assertThat(v1.getStringValue(), is(v2.getStringValue()));
        }
    }

    protected void assertByField(net.morimekta.test.hazelcast.v2.CompactFields v2,
                                 net.morimekta.test.hazelcast.v1.CompactFields v1) {
        assertByField(v1, v2);
    }

    protected void assertByField(net.morimekta.test.hazelcast.v1.CompactFields v1,
                                 net.morimekta.test.hazelcast.v2.CompactFields v2) {
        assertThat(v1.hasId(), is(v2.hasId()));
        if( v1.hasId() ) {
            assertThat(v1.getId(), is(v2.getId()));
        }
        assertThat(v1.hasLabel(), is(v2.hasLabel()));
        if( v1.hasLabel() ) {
            assertThat(v1.getLabel(), is(v2.getLabel()));
        }
        assertThat(v1.hasName(), is(v2.hasName()));
        if( v1.hasName() ) {
            assertThat(v1.getName(), is(v2.getName()));
        }
    }

    protected void assertByField(net.morimekta.test.hazelcast.v3.OptionalFields v3,
                                 net.morimekta.test.hazelcast.v2.OptionalFields v2) {
        assertByField(v2, v3);
    }

    protected void assertByField(net.morimekta.test.hazelcast.v2.OptionalFields v2,
                                 net.morimekta.test.hazelcast.v3.OptionalFields v3) {
        assertThat(v2.hasBinaryValue(), is(v3.hasBinaryValue()));
        if( v2.hasBinaryValue() ) {
            assertThat(v2.getBinaryValue(), is(v3.getBinaryValue()));
        }
        assertThat(v2.hasBooleanValue(), is(v3.hasBooleanValue()));
        if( v2.hasBooleanValue() ) {
            assertThat(v2.isBooleanValue(), is(v3.isBooleanValue()));
        }
        assertThat(v2.hasByteValue(), is(v3.hasByteValue()));
        if( v2.hasByteValue() ) {
            assertThat(v2.getByteValue(), is(v3.getByteValue()));
        }
        assertThat(v2.hasCompactValue(), is(v3.hasCompactValue()));
        if( v2.hasCompactValue() ) {
            assertByField(v2.getCompactValue(), v3.getCompactValue());
        }
        assertThat(v2.hasDoubleValue(), is(v3.hasDoubleValue()));
        if( v2.hasDoubleValue() ) {
            assertThat(v2.getDoubleValue(), is(v3.getDoubleValue()));
        }
        assertThat(v2.hasEnumValue(), is(v3.hasEnumValue()));
        if( v2.hasEnumValue() ) {
            assertThat(v2.getEnumValue().asInteger(), is(v3.getEnumValue().asInteger()));
        }
        assertThat(v2.hasIntegerValue(), is(v3.hasIntegerValue()));
        if( v2.hasIntegerValue() ) {
            assertThat(v2.getIntegerValue(), is(v3.getIntegerValue()));
        }
        assertThat(v2.hasShortValue(), is(v3.hasShortValue()));
        if( v2.hasShortValue() ) {
            assertThat(v2.getShortValue(), is(v3.getShortValue()));
        }
        assertThat(v2.hasLongValue(), is(v3.hasLongValue()));
        if( v2.hasLongValue() ) {
            assertThat(v2.getLongValue(), is(v3.getLongValue()));
        }
        assertThat(v2.hasAnotherStringValue(), is(v3.hasAnotherStringValue()));
        if( v2.hasAnotherStringValue() ) {
            assertThat(v2.getAnotherStringValue(), is(v3.getAnotherStringValue()));
        }
    }

    protected void assertByField(net.morimekta.test.hazelcast.v4.OptionalFields v4,
                                 net.morimekta.test.hazelcast.v3.OptionalFields v3) {
        assertByField(v3, v4);
    }

    protected void assertByField(net.morimekta.test.hazelcast.v3.OptionalFields v3,
                                 net.morimekta.test.hazelcast.v4.OptionalFields v4) {
        assertThat(v3.hasBinaryValue(), is(v4.hasBinaryValue()));
        if( v3.hasBinaryValue() ) {
            assertThat(v3.getBinaryValue(), is(v4.getBinaryValue()));
        }
        assertThat(v3.hasBooleanValue(), is(v4.hasBooleanValue()));
        if( v3.hasBooleanValue() ) {
            assertThat(v3.isBooleanValue(), is(v4.isBooleanValue()));
        }
        assertThat(v3.hasByteValue(), is(v4.hasByteValue()));
        if( v3.hasByteValue() ) {
            assertThat(v3.getByteValue(), is(v4.getByteValue()));
        }
        assertThat(v3.hasCompactValue(), is(v4.hasCompactValue()));
        if( v3.hasCompactValue() ) {
            assertByField(v3.getCompactValue(), v4.getCompactValue());
        }
        assertThat(v3.hasDoubleValue(), is(v4.hasDoubleValue()));
        if( v3.hasDoubleValue() ) {
            assertThat(v3.getDoubleValue(), is(v4.getDoubleValue()));
        }
        assertThat(v3.hasEnumValue(), is(v4.hasEnumValue()));
        if( v3.hasEnumValue() ) {
            assertThat(v3.getEnumValue().asInteger(), is(v4.getEnumValue().asInteger()));
        }
        assertThat(v3.hasIntegerValue(), is(v4.hasIntegerValue()));
        if( v3.hasIntegerValue() ) {
            assertThat(v3.getIntegerValue(), is(v4.getIntegerValue()));
        }
        assertThat(v3.hasLongValue(), is(v4.hasLongValue()));
        if( v3.hasLongValue() ) {
            assertThat(v3.getLongValue(), is(v4.getLongValue()));
        }
        assertThat(v3.hasAnotherStringValue(), is(v4.hasAnotherStringValue()));
        if( v3.hasAnotherStringValue() ) {
            assertThat(v3.getAnotherStringValue(), is(v4.getAnotherStringValue()));
        }
    }

    protected void assertByField(net.morimekta.test.hazelcast.v2.CompactFields v2,
                                 net.morimekta.test.hazelcast.v3.CompactFields v3) {
        assertThat(v2.hasId(), is(v3.hasId()));
        if( v2.hasId() ) {
            assertThat(v2.getId(), is(v3.getId()));
        }
        assertThat(v2.hasLabel(), is(v3.hasLabel()));
        if( v2.hasLabel() ) {
            assertThat(v2.getLabel(), is(v3.getLabel()));
        }
        assertThat(v2.hasName(), is(v3.hasName()));
        if( v2.hasName() ) {
            assertThat(v2.getName(), is(v3.getName()));
        }
    }

    protected void assertByField(net.morimekta.test.hazelcast.v3.CompactFields v3,
                                 net.morimekta.test.hazelcast.v4.CompactFields v4) {
        assertThat(v3.hasId(), is(v4.hasId()));
        if( v3.hasId() ) {
            assertThat(v3.getId(), is(v4.getId()));
        }
        assertThat(v3.hasLabel(), is(v4.hasLabel()));
        if( v3.hasLabel() ) {
            assertThat(v3.getLabel(), is(v4.getLabel()));
        }
        assertThat(v3.hasName(), is(v4.hasName()));
        if( v3.hasName() ) {
            assertThat(v3.getName(), is(v4.getName()));
        }
    }

    protected void assertByField(net.morimekta.test.hazelcast.v2.OptionalListFields v2,
                                 net.morimekta.test.hazelcast.v1.OptionalListFields v1) {
        assertByField(v1,v2);
    }

    protected void assertByField(net.morimekta.test.hazelcast.v1.OptionalListFields v1,
                                 net.morimekta.test.hazelcast.v2.OptionalListFields v2) {
        assertThat(v1.hasBooleanValues(), is(v2.hasBooleanValues()));
        if( v1.hasBooleanValues() ) {
            assertThat(v1.getBooleanValues(), is(v2.getBooleanValues()));
        }
        assertThat(v1.hasByteValues(), is(v2.hasByteValues()));
        if( v1.hasByteValues() ) {
            assertThat(v1.getByteValues(), is(v2.getByteValues()));
        }
        assertThat(v1.hasCompactValue(), is(v2.hasCompactValue()));
        if( v1.hasCompactValue() ) {
            for( int  i = 0; i < v1.numCompactValue(); i++ )
                assertByField(v1.getCompactValue().get(i), v2.getCompactValue().get(i));
        }
        assertThat(v1.hasDoubleValue(), is(v2.hasDoubleValue()));
        if( v1.hasDoubleValue() ) {
            assertThat(v1.getDoubleValue(), is(v2.getDoubleValue()));
        }
        assertThat(v1.hasIntegerValue(), is(v2.hasIntegerValue()));
        if( v1.hasIntegerValue() ) {
            assertThat(v1.getIntegerValue(), is(v2.getIntegerValue()));
        }
        assertThat(v1.hasShortValues(), is(v2.hasShortValues()));
        if( v1.hasShortValues() ) {
            assertThat(v1.getShortValues(), is(v2.getShortValues()));
        }
        assertThat(v1.hasLongValue(), is(v2.hasLongValue()));
        if( v1.hasLongValue() ) {
            assertThat(v1.getLongValue(), is(v2.getLongValue()));
        }
        assertThat(v1.hasStringValue(), is(v2.hasStringValue()));
        if( v1.hasStringValue() ) {
            assertThat(v1.getStringValue(), is(v2.getStringValue()));
        }
        assertThat(v1.hasBinaryValue(), is(v2.hasBinaryValue()));
        if( v1.hasBinaryValue() ) {
            assertThat(v1.getBinaryValue(), is(v2.getBinaryValue()));
        }
        assertThat(v1.hasValueValue(), is(v2.hasValueValue()));
        if( v1.hasValueValue() ) {
            // compare numeric values as the Value is unique for "simulating" version bumps.
            assertThat(v1.getValueValue().stream().map(t -> t.getValue()).collect(Collectors.toList()),
                       is(v2.getValueValue().stream().map(t -> t.getValue()).collect(Collectors.toList())));
        }
    }

    protected void assertByField(net.morimekta.test.hazelcast.v3.OptionalListFields v3,
                                 net.morimekta.test.hazelcast.v2.OptionalListFields v2) {
        assertByField(v2,v3);
    }

    protected void assertByField(net.morimekta.test.hazelcast.v2.OptionalListFields v2,
                                 net.morimekta.test.hazelcast.v3.OptionalListFields v3) {
        assertThat(v2.hasBooleanValues(), is(v3.hasBooleanValues()));
        if( v2.hasBooleanValues() ) {
            assertThat(v2.getBooleanValues(), is(v3.getBooleanValues()));
        }
        assertThat(v2.hasByteValues(), is(v3.hasByteValues()));
        if( v2.hasByteValues() ) {
            assertThat(v2.getByteValues(), is(v3.getByteValues()));
        }
        assertThat(v2.hasCompactValue(), is(v3.hasCompactValue()));
        if( v2.hasCompactValue() ) {
            for( int  i = 0; i < v2.numCompactValue(); i++ )
                assertByField(v2.getCompactValue().get(i), v3.getCompactValue().get(i));
        }
        assertThat(v2.hasDoubleValue(), is(v3.hasDoubleValue()));
        if( v2.hasDoubleValue() ) {
            assertThat(v2.getDoubleValue(), is(v3.getDoubleValue()));
        }
        assertThat(v2.hasIntegerValue(), is(v3.hasIntegerValue()));
        if( v2.hasIntegerValue() ) {
            assertThat(v2.getIntegerValue(), is(v3.getIntegerValue()));
        }
        assertThat(v2.hasShortValues(), is(v3.hasShortValues()));
        if( v2.hasShortValues() ) {
            assertThat(v2.getShortValues(), is(v3.getShortValues()));
        }
        assertThat(v2.hasLongValue(), is(v3.hasLongValue()));
        if( v2.hasLongValue() ) {
            assertThat(v2.getLongValue(), is(v3.getLongValue()));
        }
        assertThat(v2.hasAnotherStringValues(), is(v3.hasAnotherStringValues()));
        if( v2.hasAnotherStringValues() ) {
            assertThat(v2.getAnotherStringValues(), is(v3.getAnotherStringValues()));
        }
        assertThat(v2.hasBinaryValue(), is(v3.hasBinaryValue()));
        if( v2.hasBinaryValue() ) {
            assertThat(v2.getBinaryValue(), is(v3.getBinaryValue()));
        }
        assertThat(v2.hasValueValue(), is(v3.hasValueValue()));
        if( v2.hasValueValue() ) {
            // compare numeric values as the Value is unique for "simulating" version bumps.
            assertThat(v2.getValueValue().stream().map(t -> t.getValue()).collect(Collectors.toList()),
                       is(v3.getValueValue().stream().map(t -> t.getValue()).collect(Collectors.toList())));
        }
    }

    protected void assertByField(net.morimekta.test.hazelcast.v4.OptionalListFields v4,
                                 net.morimekta.test.hazelcast.v3.OptionalListFields v3) {
        assertByField(v3,v4);
    }

    protected void assertByField(net.morimekta.test.hazelcast.v3.OptionalListFields v3,
                                 net.morimekta.test.hazelcast.v4.OptionalListFields v4) {
        assertThat(v3.hasBooleanValues(), is(v4.hasBooleanValues()));
        if( v3.hasBooleanValues() ) {
            assertThat(v3.getBooleanValues(), is(v4.getBooleanValues()));
        }
        assertThat(v3.hasByteValues(), is(v4.hasByteValues()));
        if( v3.hasByteValues() ) {
            assertThat(v3.getByteValues(), is(v4.getByteValues()));
        }
        assertThat(v3.hasCompactValue(), is(v4.hasCompactValue()));
        if( v3.hasCompactValue() ) {
            for( int  i = 0; i < v3.numCompactValue(); i++ )
                assertByField(v3.getCompactValue().get(i), v4.getCompactValue().get(i));
        }
        assertThat(v3.hasDoubleValue(), is(v4.hasDoubleValue()));
        if( v3.hasDoubleValue() ) {
            assertThat(v3.getDoubleValue(), is(v4.getDoubleValue()));
        }
        assertThat(v3.hasShortValues(), is(v4.hasShortValues()));
        if( v3.hasShortValues() ) {
            assertThat(v3.getShortValues(), is(v4.getShortValues()));
        }
        assertThat(v3.hasLongValue(), is(v4.hasLongValue()));
        if( v3.hasLongValue() ) {
            assertThat(v3.getLongValue(), is(v4.getLongValue()));
        }
        assertThat(v3.hasAnotherStringValues(), is(v4.hasAnotherStringValues()));
        if( v3.hasAnotherStringValues() ) {
            assertThat(v3.getAnotherStringValues(), is(v4.getAnotherStringValues()));
        }
        assertThat(v3.hasBinaryValue(), is(v4.hasBinaryValue()));
        if( v3.hasBinaryValue() ) {
            assertThat(v3.getBinaryValue(), is(v4.getBinaryValue()));
        }
        assertThat(v3.hasValueValue(), is(v4.hasValueValue()));
        if( v3.hasValueValue() ) {
            // compare numeric values as the Value is unique for "simulating" version bumps.
            assertThat(v3.getValueValue().stream().map(t -> t.getValue()).collect(Collectors.toList()),
                       is(v4.getValueValue().stream().map(t -> t.getValue()).collect(Collectors.toList())));
        }
    }

    protected void assertByField(net.morimekta.test.hazelcast.v2.OptionalSetFields v2,
                                 net.morimekta.test.hazelcast.v1.OptionalSetFields v1) {
        assertByField(v1,v2);
    }

    protected void assertByField(net.morimekta.test.hazelcast.v1.OptionalSetFields v1,
                                 net.morimekta.test.hazelcast.v2.OptionalSetFields v2) {
        assertThat(v1.hasBooleanValues(), is(v2.hasBooleanValues()));
        if( v1.hasBooleanValues() ) {
            assertThat(v1.getBooleanValues(), is(v2.getBooleanValues()));
        }
        assertThat(v1.hasByteValues(), is(v2.hasByteValues()));
        if( v1.hasByteValues() ) {
            assertThat(v1.getByteValues(), is(v2.getByteValues()));
        }
        assertThat(v1.hasCompactValue(), is(v2.hasCompactValue()));
        assertThat(v1.numCompactValue(), is(v2.numCompactValue()));
        assertThat(v1.hasDoubleValue(), is(v2.hasDoubleValue()));
        if( v1.hasDoubleValue() ) {
            assertThat(v1.getDoubleValue(), is(v2.getDoubleValue()));
        }
        assertThat(v1.hasIntegerValue(), is(v2.hasIntegerValue()));
        if( v1.hasIntegerValue() ) {
            assertThat(v1.getIntegerValue(), is(v2.getIntegerValue()));
        }
        assertThat(v1.hasShortValues(), is(v2.hasShortValues()));
        if( v1.hasShortValues() ) {
            assertThat(v1.getShortValues(), is(v2.getShortValues()));
        }
        assertThat(v1.hasLongValue(), is(v2.hasLongValue()));
        if( v1.hasLongValue() ) {
            assertThat(v1.getLongValue(), is(v2.getLongValue()));
        }
        assertThat(v1.hasStringValue(), is(v2.hasStringValue()));
        if( v1.hasStringValue() ) {
            assertThat(v1.getStringValue(), is(v2.getStringValue()));
        }
        assertThat(v1.hasBinaryValue(), is(v2.hasBinaryValue()));
        if( v1.hasBinaryValue() ) {
            assertThat(v1.getBinaryValue(), is(v2.getBinaryValue()));
        }
        assertThat(v1.hasValueValue(), is(v2.hasValueValue()));
        if( v1.hasValueValue() ) {
            // compare numeric values as the Value is unique for "simulating" version bumps.
            assertThat(v1.getValueValue().stream().map(t -> t.getValue()).collect(Collectors.toList()),
                       is(v2.getValueValue().stream().map(t -> t.getValue()).collect(Collectors.toList())));
        }
    }

    protected void assertByField(net.morimekta.test.hazelcast.v3.OptionalSetFields v3,
                                 net.morimekta.test.hazelcast.v2.OptionalSetFields v2) {
        assertByField(v2,v3);
    }

    protected void assertByField(net.morimekta.test.hazelcast.v2.OptionalSetFields v2,
                                 net.morimekta.test.hazelcast.v3.OptionalSetFields v3) {
        assertThat(v2.hasBooleanValues(), is(v3.hasBooleanValues()));
        if( v2.hasBooleanValues() ) {
            assertThat(v2.getBooleanValues(), is(v3.getBooleanValues()));
        }
        assertThat(v2.hasByteValues(), is(v3.hasByteValues()));
        if( v2.hasByteValues() ) {
            assertThat(v2.getByteValues(), is(v3.getByteValues()));
        }
        assertThat(v2.hasCompactValue(), is(v3.hasCompactValue()));
        assertThat(v2.numCompactValue(), is(v3.numCompactValue()));
        assertThat(v2.hasDoubleValue(), is(v3.hasDoubleValue()));
        if( v2.hasDoubleValue() ) {
            assertThat(v2.getDoubleValue(), is(v3.getDoubleValue()));
        }
        assertThat(v2.hasIntegerValue(), is(v3.hasIntegerValue()));
        if( v2.hasIntegerValue() ) {
            assertThat(v2.getIntegerValue(), is(v3.getIntegerValue()));
        }
        assertThat(v2.hasShortValues(), is(v3.hasShortValues()));
        if( v2.hasShortValues() ) {
            assertThat(v2.getShortValues(), is(v3.getShortValues()));
        }
        assertThat(v2.hasLongValue(), is(v3.hasLongValue()));
        if( v2.hasLongValue() ) {
            assertThat(v2.getLongValue(), is(v3.getLongValue()));
        }
        assertThat(v2.hasAnotherStringValues(), is(v3.hasAnotherStringValues()));
        if( v2.hasAnotherStringValues() ) {
            assertThat(v2.getAnotherStringValues(), is(v3.getAnotherStringValues()));
        }
        assertThat(v2.hasBinaryValue(), is(v3.hasBinaryValue()));
        if( v2.hasBinaryValue() ) {
            assertThat(v2.getBinaryValue(), is(v3.getBinaryValue()));
        }
        assertThat(v2.hasValueValue(), is(v3.hasValueValue()));
        if( v2.hasValueValue() ) {
            // compare numeric values as the Value is unique for "simulating" version bumps.
            assertThat(v2.getValueValue().stream().map(t -> t.getValue()).collect(Collectors.toList()),
                       is(v3.getValueValue().stream().map(t -> t.getValue()).collect(Collectors.toList())));
        }
    }

    protected void assertByField(net.morimekta.test.hazelcast.v4.OptionalSetFields v4,
                                 net.morimekta.test.hazelcast.v3.OptionalSetFields v3) {
        assertByField(v3,v4);
    }

    protected void assertByField(net.morimekta.test.hazelcast.v3.OptionalSetFields v3,
                                 net.morimekta.test.hazelcast.v4.OptionalSetFields v4) {
        assertThat(v3.hasBooleanValues(), is(v4.hasBooleanValues()));
        if( v3.hasBooleanValues() ) {
            assertThat(v3.getBooleanValues(), is(v4.getBooleanValues()));
        }
        assertThat(v3.hasByteValues(), is(v4.hasByteValues()));
        if( v3.hasByteValues() ) {
            assertThat(v3.getByteValues(), is(v4.getByteValues()));
        }
        assertThat(v3.hasCompactValue(), is(v4.hasCompactValue()));
        assertThat(v3.numCompactValue(), is(v4.numCompactValue()));
        assertThat(v3.hasDoubleValue(), is(v4.hasDoubleValue()));
        if( v3.hasDoubleValue() ) {
            assertThat(v3.getDoubleValue(), is(v4.getDoubleValue()));
        }
        assertThat(v3.hasShortValues(), is(v4.hasShortValues()));
        if( v3.hasShortValues() ) {
            assertThat(v3.getShortValues(), is(v4.getShortValues()));
        }
        assertThat(v3.hasLongValue(), is(v4.hasLongValue()));
        if( v3.hasLongValue() ) {
            assertThat(v3.getLongValue(), is(v4.getLongValue()));
        }
        assertThat(v3.hasAnotherStringValues(), is(v4.hasAnotherStringValues()));
        if( v3.hasAnotherStringValues() ) {
            assertThat(v3.getAnotherStringValues(), is(v4.getAnotherStringValues()));
        }
        assertThat(v3.hasBinaryValue(), is(v4.hasBinaryValue()));
        if( v3.hasBinaryValue() ) {
            assertThat(v3.getBinaryValue(), is(v4.getBinaryValue()));
        }
        assertThat(v3.hasValueValue(), is(v4.hasValueValue()));
        if( v3.hasValueValue() ) {
            // compare numeric values as the Value is unique for "simulating" version bumps.
            assertThat(v3.getValueValue().stream().map(t -> t.getValue()).collect(Collectors.toList()),
                       is(v4.getValueValue().stream().map(t -> t.getValue()).collect(Collectors.toList())));
        }
    }

}
