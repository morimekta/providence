package net.morimekta.providence.generator.format.java.messages.extras;

import net.morimekta.providence.descriptor.PList;
import net.morimekta.providence.generator.GeneratorException;
import net.morimekta.providence.generator.format.java.program.extras.HazelcastPortableProgramFormatter;
import net.morimekta.providence.generator.format.java.shared.MessageMemberFormatter;
import net.morimekta.providence.generator.format.java.utils.JField;
import net.morimekta.providence.generator.format.java.utils.JHelper;
import net.morimekta.providence.generator.format.java.utils.JMessage;
import net.morimekta.providence.reflect.util.ThriftAnnotation;
import net.morimekta.util.Binary;
import net.morimekta.util.Strings;
import net.morimekta.util.io.IndentedPrintWriter;

import com.google.common.collect.ImmutableList;
import com.google.common.primitives.Booleans;
import com.google.common.primitives.Bytes;
import com.google.common.primitives.Doubles;
import com.google.common.primitives.Ints;
import com.google.common.primitives.Longs;
import com.google.common.primitives.Shorts;
import com.hazelcast.nio.serialization.Portable;
import com.hazelcast.nio.serialization.PortableReader;
import com.hazelcast.nio.serialization.PortableWriter;

import java.io.IOException;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import static net.morimekta.providence.generator.format.java.utils.JUtils.camelCase;
import static net.morimekta.providence.generator.format.java.utils.JUtils.getHazelcastClassId;
import static net.morimekta.providence.generator.format.java.utils.JUtils.getHazelcastFactory;

/**
 * Formatter to handle hazelcast_portable formatting of Portable Implementation
 * @link http://docs.hazelcast_portable.org/docs/3.5/manual/html/portableserialization.html
 *
 * @author andreas@zedge.net
 */
public class HazelcastPortableMessageFormatter implements MessageMemberFormatter {

    public static final String WRAPPER_CLASS_NAME = "_Builder";

    private static final String PORTABLE_WRITER = "portableWriter";
    private static final String PORTABLE_READER = "portableReader";

    private static final String TEMP_OPTIONALS = "__temp_optionals";

    private final IndentedPrintWriter writer;
    private final JHelper             helper;

    public HazelcastPortableMessageFormatter(IndentedPrintWriter writer, JHelper helper) {
        this.writer = writer;
        this.helper = helper;
    }

    @Override
    public Collection<String> getExtraImplements(JMessage<?> message) throws GeneratorException {
        if (message.hasAnnotation(ThriftAnnotation.JAVA_HAZELCAST_CLASS_ID)) {
            return ImmutableList.of(Portable.class.getName());
        } else {
            return new LinkedList<>();
        }
    }

    @Override
    public void appendMethods(JMessage<?> message) throws GeneratorException {
        if (message.hasAnnotation(ThriftAnnotation.JAVA_HAZELCAST_CLASS_ID)) {
            appendFactoryId(message);
            appendClassId(message);
            appendPortableWriter(message);
            appendPortableReader(message);
        }
    }

    /**
     * Method to append get factory id from hazelcast_portable.
     *
     * @param message JMessage with the information.
     * <pre>
     *  {@code
     *      @Override
     *      public int getFactoryId() {
     *          return ContentCmsPortableFactory.FACTORY_ID;
     *      }
     *  }
     * </pre>
     */
    public void appendFactoryId(JMessage<?> message) {
        writer.appendln("@Override")
              .appendln("public int getFactoryId() {")
              .begin()
              //TODO: The factory should be file unqiue. ID is struct unique so for id we want to define several constants.
              // so for content_cms we want the factory name ContentCmsFactory or ContentCmsPortableFactory.
              // as well as some way to count this up for each struct that has a hazelcast_portable tag in it.
              .formatln("return %s.%s;",
                        getHazelcastFactory(message.descriptor()),
                        HazelcastPortableProgramFormatter.FACTORY_ID)
              .end()
              .appendln("}")
              .newline();
    }

    /**
     * Method to append get class id from hazelcast_portable.
     *
     * @param message JMessage with the information.
     * <pre>
     *  {@code
     *      @Override
     *      public int getClassId() {
     *          return ContentCmsPortableFactory.CREATE_CONTENT_ID;
     *      }
     *  }
     * </pre>
     */
    public void appendClassId(JMessage<?> message) {
        writer.appendln("@Override")
              .appendln("public int getClassId() {")
              .begin()
              //TODO: Need to add method to create a constant for the description or struct here.
              .formatln("return %s.%s;",
                        getHazelcastFactory(message.descriptor()),
                        getHazelcastClassId(message.descriptor()))
              .end()
              .appendln("}")
              .newline();
    }

    /**
     * Method to append writePortable from hazelcast_portable.
     *
     * @param message JMessage with the information.
     * <pre>
     *  {@code
     *      @Override
     *      public void writePortable(PortableWriter portableWriter) throw java.io.IOException {
     *          ...
     *      }
     *  }
     * </pre>
     */
    public void appendPortableWriter(JMessage<?> message) {
        writer.appendln("@Override")
              .formatln("public void writePortable(%s %s) throws %s {",
                        PortableWriter.class.getName(),
                        PORTABLE_WRITER,
                        IOException.class.getName())
              .begin();
        //TODO write optionals bitset.
        for (JField field : message.declaredOrderFields()) {
            writePortableField(field);
        }
        writer.formatln("%s.writeByteArray(\"%s\", %s);",
                        PORTABLE_WRITER,
                        helper.getHazelcastOptionalName(message),
                        "optionals.toByteArray()")
              .end()
              .appendln("}")
              .newline();
    }

    /**
     * Method to append readPortable from hazelcast_portable.
     *
     * @param message JMessage with the information.
     * <pre>
     *  {@code
     *      @Override
     *      public void readPortable(PortableReader portableReader) throw java.io.IOException {
     *          ...
     *      }
     *  }
     * </pre>
     */
    public void appendPortableReader(JMessage<?> message) {
        writer.appendln("@Override")
              .formatln("public void readPortable(%s %s) throws %s {",
                        PortableReader.class.getName(),
                        PORTABLE_READER,
                        IOException.class.getName())
              .begin()
              .formatln("%s %s = %s.valueOf(%s.readByteArray(\"%s\"));",
                        BitSet.class.getName(),
                        TEMP_OPTIONALS,
                        BitSet.class.getName(),
                        PORTABLE_READER,
                        helper.getHazelcastOptionalName(message));
        for (JField field : message.declaredOrderFields()) {
            readPortableField(field);
        }
        writer.end()
              .appendln("}")
              .newline();
    }

    /**
     * Method to append writing of a field to hazelcast_portable.
     *
     * @param field JField to write.
     * <pre>
     *  {@code
     *      portableWriter.writeUTF("name", name);
     *  }
     * </pre>
     */
    protected void writePortableField(JField field) throws GeneratorException {
        switch (field.type()) {
            case BINARY:
                writer.formatln("%s.writeByteArray(\"%s\", %s() ? %s.get() : new byte[0]);",
                                PORTABLE_WRITER,
                                field.name(),
                                field.isSet(),
                                field.member());
                break;
            case BOOL:
                writer.formatln("%s.writeBoolean(\"%s\", %s() ? %s : %s);",
                                PORTABLE_WRITER,
                                field.name(),
                                field.isSet(),
                                field.member(),
                                field.kDefault());
                break;
            case BYTE:
                writer.formatln("%s.writeByte(\"%s\", %s() ? %s : %s);",
                                PORTABLE_WRITER,
                                field.name(),
                                field.isSet(),
                                field.member(),
                                field.kDefault());
                break;
            case DOUBLE:
                writer.formatln("%s.writeDouble(\"%s\", %s() ? %s : %s);",
                                PORTABLE_WRITER,
                                field.name(),
                                field.isSet(),
                                field.member(),
                                field.kDefault());
                break;
            case ENUM:
                writer.formatln("%s.writeInt(\"%s\", %s() ? %s.getValue() : -1);",
                                PORTABLE_WRITER,
                                field.name(),
                                field.isSet(),
                                field.member());
                break;
            case I16:
                writer.formatln("%s.writeShort(\"%s\", %s() ? %s : %s);",
                                PORTABLE_WRITER,
                                field.name(),
                                field.isSet(),
                                field.member(),
                                field.kDefault());
                break;
            case I32:
                writer.formatln("%s.writeInt(\"%s\", %s() ? %s : %s);",
                                PORTABLE_WRITER,
                                field.name(),
                                field.isSet(),
                                field.member(),
                                field.kDefault());
                break;
            case I64:
                writer.formatln("%s.writeLong(\"%s\", %s() ? %s : %s);",
                                PORTABLE_WRITER,
                                field.name(),
                                field.isSet(),
                                field.member(),
                                field.kDefault());
                break;
            case STRING:
                writer.formatln("%s.writeUTF(\"%s\", %s() ? %s : \"\");",
                                PORTABLE_WRITER,
                                field.name(),
                                field.isSet(),
                                field.member());
                break;
            case LIST:
                writePortableFieldList(field);
                break;
            case MESSAGE:
                //TODO: need to verify that this actually has the annotation later on, or the portable will give compile time exception.
                //TODO: method name should be fetched as helper from a single point, so it is collectively added all the places.
                writer.formatln("%s.writePortable(\"%s\", %s() ? %s() : new %s.%s());",
                                PORTABLE_WRITER,
                                field.name(),
                                field.isSet(),
                                Strings.camelCase("mutable", field.name()),
                                field.instanceType(),
                                "_Builder");
                break;
            default:
                throw new GeneratorException("Not implemented writePortableField for type: " + field.type() + " in " +
                                             this.getClass()
                                                 .getSimpleName());
        }
    }

    /**
     * Method to append writing of a field to hazelcast_portable.
     *
     * @param field JField to write.
     * <pre>
     *  {@code
     *      portableWriter.writeUTFArray("name", name);
     *  }
     * </pre>
     */
    protected void writePortableFieldList(JField field) throws GeneratorException {
        PList listType = (PList) (field.getPField()
                                       .getDescriptor());
        switch (listType.itemDescriptor()
                        .getType()) {
            case BYTE:
                writer.formatln("%s.writeByteArray(\"%s\", %s.toArray(%s.build()));",
                              PORTABLE_WRITER,
                              field.name(),
                              Bytes.class.getName(),
                              field.member());
                break;
            case BOOL:
                writer.formatln("%s.writeBooleanArray(\"%s\", %s.toArray(%s.build()));",
                              PORTABLE_WRITER,
                              field.name(),
                              Booleans.class.getName(),
                              field.member());
                break;
            case DOUBLE:
                writer.formatln("%s.writeDoubleArray(\"%s\", %s.toArray(%s.build()));",
                              PORTABLE_WRITER,
                              field.name(),
                              Doubles.class.getName(),
                              field.member());
                break;
            case I16:
                writer.formatln("%s.writeShortArray(\"%s\", %s.toArray(%s.build()));",
                              PORTABLE_WRITER,
                              field.name(),
                              Shorts.class.getName(),
                              field.member());
                break;
            case I32:
                writer.formatln("%s.writeIntArray(\"%s\", %s.toArray(%s.build()));",
                              PORTABLE_WRITER,
                              field.name(),
                              Ints.class.getName(),
                              field.member());
                break;
            case I64:
                writer.formatln("%s.writeLongArray(\"%s\", %s.toArray(%s.build()));",
                              PORTABLE_WRITER,
                              field.name(),
                              Longs.class.getName(),
                              field.member());
                break;
            case STRING:
                writer.formatln("%s.writeUTFArray(\"%s\", %s.build().toArray(new String[%s.build().size()]));",
                              PORTABLE_WRITER,
                              field.name(),
                              field.member(),
                              field.member());
                break;
            case MESSAGE:
                //TODO: need to verify that this actually has the annotation later on, or the portable will give compile time exception.
                writer.formatln("%s<%s.%s> %sList = %s.build().stream().map(i -> i.mutate()).collect(%s.toList());",
                                List.class.getName(),
                                helper.getValueType(listType.itemDescriptor()),
                                "_Builder",
                                camelCase("temp", field.name()),
                                field.member(),
                                Collectors.class.getName());
                writer.formatln("%s.writePortableArray(\"%s\", %s() ? %sList.toArray(new %s.%s[%sList.size()]) : new %s.%s[0]);",
                                PORTABLE_WRITER,
                                field.name(),
                                field.isSet(),
                                camelCase("temp", field.name()),
                                helper.getValueType(listType.itemDescriptor()),
                                "_Builder",
                                camelCase("temp", field.name()),
                                helper.getValueType(listType.itemDescriptor()),
                                "_Builder");
                break;
            default:
                throw new GeneratorException("Not implemented writePortableField for list with type: " +
                                             listType.itemDescriptor()
                                                     .getType() + " in " + this.getClass()
                                                                               .getSimpleName());
        }
    }

    /**
     * Method to append reading of a field to hazelcast_portable.
     *
     * @param field JField to read.
     * <pre>
     *  {@code
     *      setName(portableReader.readUTF("name"));
     *  }
     * </pre>
     */
    protected void readPortableField(JField field) {
        writer.formatln("if( %s.hasField(\"%s\") && %s.get(%s) ) {",
                        PORTABLE_READER,
                        field.name(),
                        TEMP_OPTIONALS,
                        field.index())
              .begin();
        switch (field.type()) {
            case BINARY:
                writer.formatln("%s(new %s(%s.readByteArray(\"%s\")));",
                                field.setter(),
                                Binary.class.getName(),
                                PORTABLE_READER,
                                field.name());
                break;
            case BOOL:
                writer.formatln("%s(%s.readBoolean(\"%s\"));",
                                field.setter(),
                                PORTABLE_READER,
                                field.name());
                break;
            case BYTE:
                writer.formatln("%s(%s.readByte(\"%s\"));",
                                field.setter(),
                                PORTABLE_READER,
                                field.name());
                break;
            case DOUBLE:
                writer.formatln("%s(%s.readDouble(\"%s\"));",
                                field.setter(),
                                PORTABLE_READER,
                                field.name());
                break;
            case ENUM:
                writer.formatln("%s(%s.forValue(%s.readInt(\"%s\")));",
                                field.setter(),
                                field.instanceType(),
                                PORTABLE_READER,
                                field.name());
                break;
            case I16:
                writer.formatln("%s(%s.readShort(\"%s\"));",
                                field.setter(),
                                PORTABLE_READER,
                                field.name());
                break;
            case I32:
                writer.formatln("%s(%s.readInt(\"%s\"));",
                                field.setter(),
                                PORTABLE_READER,
                                field.name());
                break;
            case I64:
                writer.formatln("%s(%s.readLong(\"%s\"));",
                                field.setter(),
                                PORTABLE_READER,
                                field.name());
                break;
            case STRING:
                writer.formatln("%s(%s.readUTF(\"%s\"));",
                                field.setter(),
                                PORTABLE_READER,
                                field.name());
                break;
            case LIST:
                readPortableFieldList(field);
                break;
            case MESSAGE: // ((CompactFields._Builder)portableReader.readPortable("compactValue")).build()
                writer.formatln("%s(((%s.%s)%s.readPortable(\"%s\")).%s());",
                                field.setter(),
                                field.instanceType(),
                                "_Builder",
                                PORTABLE_READER,
                                field.name(),
                                "build");
                break;
            default:
                throw new GeneratorException("Not implemented readPortableField for type: " + field.type() + " in " +
                                             this.getClass()
                                                 .getSimpleName());
        }
        writer.end()
              .appendln("}");
    }

    /**
     * Method to append reading of a field to hazelcast_portable.
     *
     * @param field JField to read.
     * <pre>
     *  {@code
     *      setNames(Arrays.asList(portableReader.readUTFArray("name")));
     *  }
     * </pre>
     */
    protected void readPortableFieldList(JField field) throws GeneratorException {
        PList listType = (PList) (field.getPField()
                                       .getDescriptor());
        switch (listType.itemDescriptor()
                        .getType()) {
            case BYTE:
                writer.formatln("%s(%s.asList(%s.readByteArray(\"%s\")));",
                                field.setter(),
                                Bytes.class.getName(),
                                PORTABLE_READER,
                                field.name());
                break;
            case BOOL:
                writer.formatln("%s(%s.asList(%s.readBooleanArray(\"%s\")));",
                                field.setter(),
                                Booleans.class.getName(),
                                PORTABLE_READER,
                                field.name());
                break;
            case DOUBLE:
                writer.formatln("%s(%s.asList(%s.readDoubleArray(\"%s\")));",
                                field.setter(),
                                Doubles.class.getName(),
                                PORTABLE_READER,
                                field.name());
                break;
            case I16:
                writer.formatln("%s(%s.asList(%s.readShortArray(\"%s\")));",
                                field.setter(),
                                Shorts.class.getName(),
                                PORTABLE_READER,
                                field.name());
                break;
            case I32:
                writer.formatln("%s(%s.asList(%s.readIntArray(\"%s\")));",
                                field.setter(),
                                Ints.class.getName(),
                                PORTABLE_READER,
                                field.name());
                break;
            case I64:
                writer.formatln("%s(%s.asList(%s.readLongArray(\"%s\")));",
                                field.setter(),
                                Longs.class.getName(),
                                PORTABLE_READER,
                                field.name());
                break;
            case STRING:
                writer.formatln("%s(%s.asList(%s.readUTFArray(\"%s\")));",
                                field.setter(),
                                Arrays.class.getName(),
                                PORTABLE_READER,
                                field.name());
                break;
            case MESSAGE:
                writer.formatln("%s(%s.asList(%s.readPortableArray(\"%s\")).stream()" +
                                ".map(i -> ((%s.%s)i).build()).collect(%s.toList()));",
                                field.setter(),
                                Arrays.class.getName(),
                                PORTABLE_READER,
                                field.name(),
                                listType.itemDescriptor()
                                        .getName(),
                                WRAPPER_CLASS_NAME,
                                Collectors.class.getName());

                break;
            default:
                throw new GeneratorException("Not implemented readPortableField for list with type: " +
                                             listType.itemDescriptor()
                                                     .getType() + " in " + this.getClass()
                                                                               .getSimpleName());
        }
    }

}
