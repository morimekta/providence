package net.morimekta.providence.thrift;

import net.morimekta.providence.PEnumValue;
import net.morimekta.providence.PMessage;
import net.morimekta.providence.PMessageVariant;
import net.morimekta.providence.PUnion;
import net.morimekta.providence.descriptor.PField;
import net.morimekta.providence.serializer.PSerializeException;
import net.morimekta.providence.serializer.PSerializer;
import net.morimekta.test.providence.Containers;
import net.morimekta.util.Binary;

import org.apache.thrift.TBase;
import org.apache.thrift.TEnum;
import org.apache.thrift.TException;
import org.apache.thrift.TFieldIdEnum;
import org.apache.thrift.TUnion;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TCompactProtocol;
import org.apache.thrift.protocol.TJSONProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.protocol.TProtocolFactory;
import org.apache.thrift.protocol.TSimpleJSONProtocol;
import org.apache.thrift.protocol.TTupleProtocol;
import org.apache.thrift.transport.TIOStreamTransport;
import org.apache.thrift.transport.TTransport;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static net.morimekta.providence.testing.ProvidenceHelper.arrayListFromJsonResource;
import static net.morimekta.providence.testing.ProvidenceMatchers.messageEq;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

/**
 */
public class TProtocolSerializerTest {
    private static ArrayList<Containers> containers;

    @Before
    public void setUp() throws PSerializeException, IOException {
        synchronized (TProtocolSerializerTest.class) {
            // Since these are immutable, we don't need to read for each test.
            if (containers == null) {
                containers = arrayListFromJsonResource("/providence/test.json", Containers.kDescriptor);
            }
        }
    }

    public <PM extends PMessage<PM>, F extends TFieldIdEnum, TB extends TBase<TB, F>> void assertConsistent(String prefix,
                                                                                                            PM providence,
                                                                                                            TB thrift) {
        if (providence.descriptor()
                      .getVariant() == PMessageVariant.UNION) {
            TUnion<?, F> t_union = (TUnion) thrift;
            PUnion<?> p_union = (PUnion) providence;

            F t_field = t_union.getSetField();
            PField p_field = p_union.unionField();

            assertEquals(p_field.getKey(), t_field.getThriftFieldId());
        } else {
            for (PField field : providence.descriptor()
                                             .getFields()) {
                F thriftField = thrift.fieldForId(field.getKey());

                String fieldPath = (prefix.isEmpty() ? "" : prefix + ".") + field.getName();

                assertEquals("has " + fieldPath, providence.has(field.getKey()), thrift.isSet(thriftField));
                if (providence.has(field.getKey())) {
                    switch (field.getType()) {
                        case MESSAGE:
                            assertConsistent(fieldPath,
                                             (PMessage) providence.get(field.getKey()),
                                             (TBase) thrift.getFieldValue(thriftField));
                            break;
                        case ENUM: {
                            PEnumValue<?> pe = (PEnumValue) providence.get(field.getKey());
                            TEnum te = (TEnum) thrift.getFieldValue(thriftField);
                            assertEquals(fieldPath, pe.getValue(), te.getValue());
                            break;
                        }
                        case BINARY: {
                            Binary pBin = (Binary) providence.get(field.getKey());
                            byte[] tBytes = (byte[]) thrift.getFieldValue(thriftField);
                            Binary tBin = Binary.wrap(tBytes);
                            assertEquals(fieldPath, pBin, tBin);
                            break;
                        }
                        case MAP: {
                            Map pm = (Map) providence.get(field.getKey());
                            Map tm = (Map) thrift.getFieldValue(thriftField);
                            assertEquals(fieldPath + " size", pm.size(), tm.size());

                            // TODO: Compare actual content.
                            break;
                        }
                        case SET: {
                            Set ps = (Set) providence.get(field.getKey());
                            Set ts = (Set) thrift.getFieldValue(thriftField);
                            assertEquals(fieldPath + " size", ps.size(), ts.size());

                            // TODO: Compare actual content.
                            break;
                        }
                        case LIST: {
                            List pl = (List) providence.get(field.getKey());
                            List tl = (List) thrift.getFieldValue(thriftField);

                            assertEquals(fieldPath + " size", pl.size(), tl.size());

                            for (int i = 0; i < pl.size(); ++i) {
                                String itemPath = fieldPath + "[" + i + "]";

                                Object pi = pl.get(i);
                                Object ti = tl.get(i);

                                if (pi instanceof PMessage) {
                                    assertConsistent(itemPath, (PMessage) pi, (TBase) ti);
                                } else if (pi instanceof Collection) {
                                    // TODO: Compare actual content.
                                } else if (pi instanceof Map) {
                                    // TODO: Compare actual content.
                                } else if (pi instanceof Binary) {
                                    Binary pb = (Binary) pi;
                                    Binary tb = Binary.wrap(((ByteBuffer) ti).array());
                                    assertEquals(itemPath, pb, tb);
                                } else if (pi instanceof PEnumValue) {
                                    PEnumValue pe = (PEnumValue) pi;
                                    TEnum te = (TEnum) ti;
                                    assertEquals(itemPath, pe.getValue(), te.getValue());
                                } else {
                                    assertEquals(itemPath, pi, ti);
                                }
                            }
                            break;
                        }
                        default:
                            assertEquals(fieldPath, providence.get(field.getKey()), thrift.getFieldValue(thriftField));
                            break;
                    }
                }
            }
        }
    }

    public void testRecoding(TProtocolFactory factory, PSerializer serializer)
            throws IOException, PSerializeException, TException {
        assertEquals(10, containers.size());

        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        // First write containers to bytes.
        for (int i = 0; i < containers.size(); ++i) {
            serializer.serialize(baos, containers.get(i));
            if (!serializer.binaryProtocol()) {
                baos.write('\n');
            }
        }

        // Read back as providence to check simple Write-Read integrity.
        ByteArrayInputStream in = new ByteArrayInputStream(baos.toByteArray());
        for (int i = 0; i < containers.size(); ++i) {
            Containers back = serializer.deserialize(in, Containers.kDescriptor);
            assertThat(back, messageEq(containers.get(i)));
            if (!serializer.binaryProtocol()) {
                assertEquals('\n', in.read());
            }
        }

        // Read back with thrift to check compatibility.
        in = new ByteArrayInputStream(baos.toByteArray());
        TTransport inTrans = new TIOStreamTransport(in);
        TProtocol inProt = factory.getProtocol(inTrans);

        // And write it back the the BAOS to check that we can get back the same again.
        baos.reset();
        TTransport outTrans = new TIOStreamTransport(baos);
        TProtocol outProt = factory.getProtocol(outTrans);

        for (int i = 0; i < containers.size(); ++i) {
            net.morimekta.test.thrift.Containers tc = new net.morimekta.test.thrift.Containers();
            tc.read(inProt);

            Containers expected = containers.get(i);
            assertConsistent("[" + i + "]", expected, tc);

            tc.write(outProt);

            if (!serializer.binaryProtocol()) {
                assertEquals('\n', in.read());
                baos.write('\n');
            }
        }

        // And read back from thrift again.
        in = new ByteArrayInputStream(baos.toByteArray());
        for (int i = 0; i < containers.size(); ++i) {
            Containers back = serializer.deserialize(in, Containers.kDescriptor);
            assertThat(back, messageEq(containers.get(i)));
            if (!serializer.binaryProtocol()) {
                assertEquals('\n', in.read());
            }
        }
    }

    @Test
    public void testTBinaryProtocol() throws IOException, PSerializeException, TException {
        testRecoding(new TBinaryProtocol.Factory(), new TBinaryProtocolSerializer());
    }

    @Test
    public void testTCompactProtocol() throws IOException, PSerializeException, TException {
        testRecoding(new TCompactProtocol.Factory(), new TCompactProtocolSerializer());
    }

    @Test
    public void testTTupleProtocol() throws IOException, PSerializeException, TException {
        testRecoding(new TTupleProtocol.Factory(), new TTupleProtocolSerializer());
    }

    @Test
    public void testTJsonProtocol() throws IOException, PSerializeException, TException {
        testRecoding(new TJSONProtocol.Factory(), new TJsonProtocolSerializer());
    }

    @Test
    @Ignore("TSimpleJsonProtocol is write-only.")
    public void testTSimpleJsonProtocol() throws IOException, PSerializeException, TException {
        testRecoding(new TSimpleJSONProtocol.Factory(), new TSimpleJsonProtocolSerializer());
    }
}
