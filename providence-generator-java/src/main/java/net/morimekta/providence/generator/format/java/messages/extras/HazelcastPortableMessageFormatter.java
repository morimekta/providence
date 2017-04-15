package net.morimekta.providence.generator.format.java.messages.extras;

import net.morimekta.providence.descriptor.PList;
import net.morimekta.providence.generator.GeneratorException;
import net.morimekta.providence.generator.format.java.program.extras.HazelcastPortableProgramFormatter;
import net.morimekta.providence.generator.format.java.shared.MessageMemberFormatter;
import net.morimekta.providence.util.hazelcast.HSerialization;
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
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import static net.morimekta.providence.generator.format.java.utils.JUtils.camelCase;
import static net.morimekta.providence.generator.format.java.utils.JUtils.getHazelcastClassId;
import static net.morimekta.providence.generator.format.java.utils.JUtils.getHazelcastFactory;

/**
 * Formatter to handle hazelcast_portable formatting of Portable Implementation
 * See <a href="http://docs.hazelcast_portable.org/docs/3.5/manual/html/portableserialization.html">Hazelcast.org</a>
 *
 * @author andreas@zedge.net
 */
public class HazelcastPortableMessageFormatter implements MessageMemberFormatter {

    public static final String WRAPPER_CLASS_NAME = "_Builder";

    private static final String PORTABLE_WRITER = "portableWriter";
    private static final String PORTABLE_READER = "portableReader";

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
     * {@code
     * public int getFactoryId() {
     *   return ContentCmsPortableFactory.FACTORY_ID;
     * }
     * }
     * </pre>
     */
    private void appendFactoryId(JMessage<?> message) {
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
     * {@code
     * public int getClassId() {
     *   return ContentCmsPortableFactory.CREATE_CONTENT_ID;
     * }
     * }
     * </pre>
     */
    private void appendClassId(JMessage<?> message) {
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
     * {@code
     * public void writePortable(com.hazelcast.nio.serialization.PortableWriter portableWriter) throws java.io.IOException {
     *   ...
     *   portableWriter.writeByteArray("__hzOptionalsForClassOptionalFields", optionals.toByteArray());
     * }
     * }
     * </pre>
     */
    private void appendPortableWriter(JMessage<?> message) {
        writer.appendln("@Override")
              .formatln("public void writePortable(%s %s) throws %s {",
                        PortableWriter.class.getName(),
                        PORTABLE_WRITER,
                        IOException.class.getName())
              .begin();
        //TODO write optionals bitset.
        for (JField field : message.declaredOrderFields()) {
            if( !field.isRequired() ) {
                writer.formatln("if( %s() ) {", field.isSet())
                      .begin();
            }
            writePortableField(field);
            if( !field.isRequired() ) {
                writer.end()
                      .appendln("} else {")
                      .begin();
                writeDefaultPortableField(field);
                writer.end()
                      .appendln("}");
            }
        }
        writer.end()
              .appendln("}")
              .newline();
    }

    /**
     * Method to append readPortable from hazelcast_portable.
     *
     * @param message JMessage with the information.
     * <pre>
     * {@code
     * public void readPortable(com.hazelcast.nio.serialization.PortableReader portableReader) throws java.io.IOException {
     *   java.util.BitSet __temp_optionals = java.util.BitSet.valueOf(portableReader.readByteArray("__hzOptionalsForClassOptionalFields"));
     *   ...
     * }
     * }
     * </pre>
     */
    private void appendPortableReader(JMessage<?> message) {
        writer.appendln("@Override")
              .formatln("public void readPortable(%s %s) throws %s {",
                        PortableReader.class.getName(),
                        PORTABLE_READER,
                        IOException.class.getName())
              .begin();
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
     * {@code
     *   // for required fields.
     *   portableWriter.writeInt("id", mId);
     *
     *   // for optional fields.
     *   if( isSetLabel() ) {
     *     portableWriter.writeUTF("label", mLabel);
     *   }
     * }
     * </pre>
     */
    private void writePortableField(JField field) throws GeneratorException {
        switch (field.type()) {
            case BINARY:
                writer.formatln("%s.writeByteArray(\"%s\", %s.get());",
                                PORTABLE_WRITER,
                                field.name(),
                                field.member());
                break;
            case BOOL:
                writer.formatln("%s.writeBoolean(\"%s\", %s);",
                                PORTABLE_WRITER,
                                field.name(),
                                field.member());
                break;
            case BYTE:
                writer.formatln("%s.writeByte(\"%s\", %s);",
                                PORTABLE_WRITER,
                                field.name(),
                                field.member());
                break;
            case DOUBLE:
                writer.formatln("%s.writeDouble(\"%s\", %s);",
                                PORTABLE_WRITER,
                                field.name(),
                                field.member());
                break;
            case ENUM:
                writer.formatln("%s.writeInt(\"%s\", %s.getValue());",
                                PORTABLE_WRITER,
                                field.name(),
                                field.member());
                break;
            case I16:
                writer.formatln("%s.writeShort(\"%s\", %s);",
                                PORTABLE_WRITER,
                                field.name(),
                                field.member());
                break;
            case I32:
                writer.formatln("%s.writeInt(\"%s\", %s);",
                                PORTABLE_WRITER,
                                field.name(),
                                field.member());
                break;
            case I64:
                writer.formatln("%s.writeLong(\"%s\", %s);",
                                PORTABLE_WRITER,
                                field.name(),
                                field.member());
                break;
            case STRING:
                writer.formatln("%s.writeUTF(\"%s\", %s);",
                                PORTABLE_WRITER,
                                field.name(),
                                field.member());
                break;
            case LIST:
                writePortableFieldList(field);
                break;
            case MESSAGE:
                //TODO: need to verify that this actually has the annotation later on, or the portable will give compile time exception.
                //TODO: method name should be fetched as helper from a single point, so it is collectively added all the places.
                writer.formatln("%s.writePortable(\"%s\", %s());",
                                PORTABLE_WRITER,
                                field.name(),
                                Strings.camelCase("mutable", field.name()));
                break;
            default:
                throw new GeneratorException("Not implemented writePortableField for type: " + field.type() + " in " +
                                             this.getClass()
                                                 .getSimpleName());
        }
        writer.formatln("%s.writeBoolean(\"%s\", true);",
                          PORTABLE_WRITER,
                          field.hasName());
    }

    /**
     * Method to append writing of a field to hazelcast_portable.
     *
     * @param field JField to write.
     * <pre>
     * {@code
     *   // for required fields.
     *   portableWriter.writeInt("id", mId);
     *
     *   // for optional fields.
     *   if( isSetLabel() ) {
     *     portableWriter.writeUTF("label", mLabel);
     *   }
     * }
     * </pre>
     */
    private void writeDefaultPortableField(JField field) throws GeneratorException {
        switch (field.type()) {
            case BINARY:
                writer.formatln("%s.writeByteArray(\"%s\", %s);",
                                PORTABLE_WRITER,
                                field.name(),
                                field.hasDefaultConstant() ? field.kDefault() : "null");
                break;
            case BOOL:
                writer.formatln("%s.writeBoolean(\"%s\", %s);",
                                PORTABLE_WRITER,
                                field.name(),
                                field.hasDefaultConstant() ? field.kDefault() : "null");
                break;
            case BYTE:
                writer.formatln("%s.writeByte(\"%s\", %s);",
                                PORTABLE_WRITER,
                                field.name(),
                                field.hasDefaultConstant() ? field.kDefault() : "null");
                break;
            case DOUBLE:
                writer.formatln("%s.writeDouble(\"%s\", %s);",
                                PORTABLE_WRITER,
                                field.name(),
                                field.hasDefaultConstant() ? field.kDefault() : "null");
                break;
            case ENUM:
                writer.formatln("%s.writeInt(\"%s\", %s);",
                                PORTABLE_WRITER,
                                field.name(),
                                "0");
                break;
            case I16:
                writer.formatln("%s.writeShort(\"%s\", %s);",
                                PORTABLE_WRITER,
                                field.name(),
                                field.hasDefaultConstant() ? field.kDefault() : "null");
                break;
            case I32:
                writer.formatln("%s.writeInt(\"%s\", %s);",
                                PORTABLE_WRITER,
                                field.name(),
                                field.hasDefaultConstant() ? field.kDefault() : "null");
                break;
            case I64:
                writer.formatln("%s.writeLong(\"%s\", %s);",
                                PORTABLE_WRITER,
                                field.name(),
                                field.hasDefaultConstant() ? field.kDefault() : "null");
                break;
            case STRING:
                writer.formatln("%s.writeUTF(\"%s\", %s);",
                                PORTABLE_WRITER,
                                field.name(),
                                field.hasDefaultConstant() ? field.kDefault() : "null");
                break;
            case LIST:
                writeDefaultPortableFieldList(field);
                break;
            case MESSAGE:
                writer.formatln("%s.writePortable(\"%s\", %s);",
                                PORTABLE_WRITER,
                                field.name(),
                                field.hasDefaultConstant() ? field.kDefault() : "null");
                break;
            default:
                throw new GeneratorException("Not implemented writePortableField for type: " + field.type() + " in " +
                                             this.getClass()
                                                 .getSimpleName());
        }
        writer.formatln("%s.writeBoolean(\"%s\", false);",
                        PORTABLE_WRITER,
                        field.hasName());
    }

    /**
     * Method to append writing of a field to hazelcast_portable.
     *
     * @param field JField to write.
     * <pre>
     * {@code
     *   if( isSetBooleanValues() ) {
     *     portableWriter.writeBooleanArray("booleanValues", com.google.common.primitives.Booleans.toArray(mBooleanValues.build()));
     *   }
     * }
     * </pre>
     */
    private void writePortableFieldList(JField field) throws GeneratorException {
        PList listType =  field.toPList();
        switch (listType.itemDescriptor()
                        .getType()) {
            case BYTE:
                writer.formatln("%s.writeByteArray(\"%s\", %s.toArray(%s.build()));",
                              PORTABLE_WRITER,
                              field.name(),
                              Bytes.class.getName(),
                              field.member());
                break;
            case BINARY:
                writer.formatln("%s.writeByteArray(\"%s\", %s.%s(%s.build()));",
                                PORTABLE_WRITER,
                                field.name(),
                                HSerialization.class.getName(),
                                "fromBinaryList", //TODO change to method name if possible.
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
            case ENUM:
                writer.formatln("%s.writeIntArray(\"%s\", %s.build().stream().mapToInt(t -> t.getValue()).toArray());",
                                PORTABLE_WRITER,
                                field.name(),
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
                writer.formatln("%s.writeUTFArray(\"%s\", %s.build().toArray(new String[0]));",
                              PORTABLE_WRITER,
                              field.name(),
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
                writer.formatln("%s.writePortableArray(\"%s\", %sList.toArray(new %s.%s[%sList.size()]));",
                                PORTABLE_WRITER,
                                field.name(),
                                camelCase("temp", field.name()),
                                helper.getValueType(listType.itemDescriptor()),
                                "_Builder",
                                camelCase("temp", field.name()));
                break;
            default:
                throw new GeneratorException("Not implemented writePortableField for list with type: " +
                                             listType.itemDescriptor()
                                                     .getType() + " in " + this.getClass()
                                                                               .getSimpleName());
        }
    }

    /**
     * Method to append writing of a field to hazelcast_portable.
     *
     * @param field JField to write.
     * <pre>
     * {@code
     * if( isSetBooleanValues() ) {
     *   portableWriter.writeBooleanArray("booleanValues", com.google.common.primitives.Booleans.toArray(mBooleanValues.build()));
     * }
     * }
     * </pre>
     */
    private void writeDefaultPortableFieldList(JField field) throws GeneratorException {
        PList listType =  field.toPList();
        switch (listType.itemDescriptor()
                        .getType()) {
            case BYTE:
                writer.formatln("%s.writeByteArray(\"%s\", new %s[0]);",
                                PORTABLE_WRITER,
                                field.name(),
                                helper.getValueType(listType.itemDescriptor()));
                break;
            case BINARY:
                writer.formatln("%s.writeByteArray(\"%s\", new %s[0]);",
                                PORTABLE_WRITER,
                                field.name(),
                                "byte"); //TODO becomes binary otherwise, and doesn't fit with byte array.
                break;
            case BOOL:
                writer.formatln("%s.writeBooleanArray(\"%s\", new %s[0]);",
                                PORTABLE_WRITER,
                                field.name(),
                                helper.getValueType(listType.itemDescriptor()));
                break;
            case DOUBLE:
                writer.formatln("%s.writeDoubleArray(\"%s\", new %s[0]);",
                                PORTABLE_WRITER,
                                field.name(),
                                helper.getValueType(listType.itemDescriptor()));
                break;
            case ENUM:
                writer.formatln("%s.writeIntArray(\"%s\", new %s[0]);",
                                PORTABLE_WRITER,
                                field.name(),
                                int.class.getName()); //TODO need fixed as value isn't doable.
                break;
            case I16:
                writer.formatln("%s.writeShortArray(\"%s\", new %s[0]);",
                                PORTABLE_WRITER,
                                field.name(),
                                helper.getValueType(listType.itemDescriptor()));
                break;
            case I32:
                writer.formatln("%s.writeIntArray(\"%s\", new %s[0]);",
                                PORTABLE_WRITER,
                                field.name(),
                                helper.getValueType(listType.itemDescriptor()));
                break;
            case I64:
                writer.formatln("%s.writeLongArray(\"%s\", new %s[0]);",
                                PORTABLE_WRITER,
                                field.name(),
                                helper.getValueType(listType.itemDescriptor()));
                break;
            case STRING:
                writer.formatln("%s.writeUTFArray(\"%s\", new %s[0]);",
                                PORTABLE_WRITER,
                                field.name(),
                                helper.getValueType(listType.itemDescriptor()));
                break;
            case MESSAGE:
                writer.formatln("%s.writePortableArray(\"%s\", new %s._Builder[0]);",
                                PORTABLE_WRITER,
                                field.name(),
                                helper.getValueType(listType.itemDescriptor()));
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
     * {@code
     *   // for required fields.
     *   setId(portableReader.readInt("id"));
     *
     *   // for optional fields.
     *   if( portableReader.hasField("label") && __temp_optionals.get(2) ) {
     *     setLabel(portableReader.readUTF("label"));
     *   }
     * }
     * </pre>
     */
    private void readPortableField(JField field) {
        if( !field.isRequired() ) {
            writer.formatln("if( %s.hasField(\"%s\") && %s.readBoolean(\"%s\") && %s.hasField(\"%s\") ) {",
                            PORTABLE_READER,
                            field.hasName(),
                            PORTABLE_READER,
                            field.hasName(),
                            PORTABLE_READER,
                            field.name())
                  .begin();
        }
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
        if( !field.isRequired() ) {
            writer.end()
                  .appendln("}");
        }
    }

    /**
     * Method to append reading of a field to hazelcast_portable.
     *
     * @param field JField to read.
     * <pre>
     * {@code
     * if( portableReader.hasField("integerValue") && __temp_optionals.get(3) ) {
     *   setIntegerValue(com.google.common.primitives.Ints.asList(portableReader.readIntArray("integerValue")));
     * }
     * }
     * </pre>
     */
    private void readPortableFieldList(JField field) throws GeneratorException {
        PList listType = field.toPList();
        switch (listType.itemDescriptor()
                        .getType()) {
            case BYTE:
                writer.formatln("%s(%s.asList(%s.readByteArray(\"%s\")));",
                                field.setter(),
                                Bytes.class.getName(),
                                PORTABLE_READER,
                                field.name());
                break;
            case BINARY:
                writer.formatln("%s(%s.%s(%s.readByteArray(\"%s\")));",
                                field.setter(),
                                HSerialization.class.getName(),
                                "toBinaryList", //TODO change to method name if possible.
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
            case ENUM:
                writer.formatln("%s(%s.asList(%s.readIntArray(\"%s\")).stream().map(t -> %s.%s(t.intValue())).collect(%s.toList()));",
                                field.setter(),
                                Ints.class.getName(),
                                PORTABLE_READER,
                                field.name(),
                                listType.itemDescriptor().getName(),
                                "forValue", //TODO need to change this to another value.
                                Collectors.class.getName());
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
