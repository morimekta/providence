package net.morimekta.providence.generator.format.java.program.extras;

import net.morimekta.providence.PType;
import net.morimekta.providence.descriptor.PDeclaredDescriptor;
import net.morimekta.providence.descriptor.PDescriptor;
import net.morimekta.providence.descriptor.PList;
import net.morimekta.providence.descriptor.PSet;
import net.morimekta.providence.generator.GeneratorException;
import net.morimekta.providence.generator.GeneratorOptions;
import net.morimekta.providence.generator.format.java.JavaOptions;
import net.morimekta.providence.generator.format.java.messages.extras.HazelcastPortableMessageFormatter;
import net.morimekta.providence.generator.format.java.shared.BaseProgramFormatter;
import net.morimekta.providence.generator.format.java.utils.BlockCommentBuilder;
import net.morimekta.providence.generator.format.java.utils.JField;
import net.morimekta.providence.generator.format.java.utils.JHelper;
import net.morimekta.providence.generator.format.java.utils.JMessage;
import net.morimekta.providence.reflect.contained.CConst;
import net.morimekta.providence.reflect.contained.CProgram;
import net.morimekta.providence.reflect.contained.CStructDescriptor;
import net.morimekta.providence.util.ThriftAnnotation;
import net.morimekta.util.io.IndentedPrintWriter;

import com.hazelcast.config.Config;
import com.hazelcast.nio.serialization.ClassDefinition;
import com.hazelcast.nio.serialization.ClassDefinitionBuilder;
import com.hazelcast.nio.serialization.Portable;
import com.hazelcast.nio.serialization.PortableFactory;

import javax.annotation.Generated;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static net.morimekta.providence.generator.format.java.utils.JUtils.camelCase;
import static net.morimekta.providence.generator.format.java.utils.JUtils.getHazelcastClassId;

/**
 * TBD
 */
public class HazelcastPortableProgramFormatter implements BaseProgramFormatter {

    public static final String FACTORY_ID = "FACTORY_ID";

    private static final String FACTORY_IMPL = "PortableFactoryImpl";
    private static final String PORTABLE_VERSION = "portableVersion";
    private static final String CONFIG = "config";
    private static final String INSTANCE = "instance";

    private final JHelper             helper;
    private final IndentedPrintWriter writer;
    private final GeneratorOptions    generatorOptions;
    private final JavaOptions         javaOptions;

    public HazelcastPortableProgramFormatter(IndentedPrintWriter writer,
                                             JHelper helper,
                                             GeneratorOptions generatorOptions,
                                             JavaOptions javaOptions) {
        this.writer = writer;
        this.helper = helper;
        this.generatorOptions = generatorOptions;
        this.javaOptions = javaOptions;
    }

    @Override
    public void appendProgramClass(CProgram document) throws GeneratorException {
        if (document.getDocumentation() != null) {
            new BlockCommentBuilder(writer).comment(document.getDocumentation())
                                           .finish();
        }

        if (javaOptions.generated_annotation_version) {
            writer.formatln("@%s(\"%s %s\")",
                            Generated.class.getName(),
                            generatorOptions.generator_program_name,
                            generatorOptions.program_version);
        } else {
            writer.formatln("@%s(\"%s\")",
                            Generated.class.getName(),
                            generatorOptions.generator_program_name);
        }

        writer.formatln("public class %s {", helper.getHazelcastFactoryClassName(document))
              .begin()
              .newline();

        Optional<CConst> factoryID = document.getConstants()
                                             .stream()
                                             .filter(t -> t.getName()
                                                           .equals(FACTORY_ID))
                                             .findFirst();

        if (!factoryID.isPresent()) {
            throw new GeneratorException(
                    "Need to provide \"const i32 FACTORY_ID = ?\" in the thrift file for " + "hazelcast generation!");
        } else {
            CConst c = factoryID.get();
            String type = helper.getValueType(c.getDescriptor());
            String name = c.getName();
            writer.formatln("public static final %s %s = %s.%s;",
                            type,
                            name,
                            helper.getConstantsClassName(document),
                            name)
                  .newline();
        }

        List<CStructDescriptor> messages = new ArrayList<>();

        for (PDeclaredDescriptor c : document.getDeclaredTypes()) {
            try {
                if (PType.MESSAGE == c.getType() && c instanceof CStructDescriptor) {
                    CStructDescriptor message = (CStructDescriptor) c;
                    if (message.hasAnnotation(ThriftAnnotation.JAVA_HAZELCAST_CLASS_ID)) {
                        writer.formatln("public static final int %s = %s;",
                                        getHazelcastClassId(message.getName()),
                                        message.getAnnotationValue(ThriftAnnotation.JAVA_HAZELCAST_CLASS_ID));
                        messages.add(message);
                    }
                }
            } catch (Exception e) {
                throw new GeneratorException(e.getMessage());
            }
        }
        writer.newline();

        appendPopulateMethod(messages);

        if (messages.isEmpty()) {
            throw new GeneratorException("No annotations available to generate!");
        } else {
            writer.formatln("private static class %s implements %s {", FACTORY_IMPL, PortableFactory.class.getName())
                  .begin()
                  .newline();
            appendCreateMethod(messages);
            appendGetDefinitions(messages);
            writer.end()
                  .appendln("}");
        }

        writer.end()
              .appendln("}")
              .newline();
    }

    /**
     * Method to write the create method from implemented PortableFactory
     *
     * @param messages List with CStructDescriptor.
     * <pre>
     * {@code
     *  @Override
     *  public com.hazelcast.nio.serialization.Portable create(int classId) {
     *      switch(classId) {
     *          ...
     *          default: {
     *              return null;
     *          }
     *      }
     *  }
     * }
     * </pre>
     */
    private void appendCreateMethod(List<CStructDescriptor> messages) {
        writer.appendln("@Override")
              .formatln("public %s create(int classId) {", Portable.class.getName())
              .begin()
              .appendln("switch(classId) {")
              .begin();
        for (CStructDescriptor message : messages) {
            writer.formatln("case %s: {", getHazelcastClassId(message.getName()))
                  .begin()
                  .formatln("return new %s.%s();",
                            message.getName(),
                            HazelcastPortableMessageFormatter.WRAPPER_CLASS_NAME)
                  .end()
                  .appendln("}");
        }
        writer.appendln("default: {")
              .begin()
              .appendln("return null;")
              .end()
              .appendln("}")
              .end();
        writer.appendln("}")
              .end()
              .appendln("}")
              .newline();
    }

    /**
     * Method to append populate methods for the Hazelcast Config.
     *
     * @param messages List with CStructDescriptor to iterate through.
     * <pre>
     * {@code
     *  public static final com.hazelcast.config.Config populateConfig(com.hazelcast.config.Config config) {
     *      PortableFactoryImpl instance = new PortableFactoryImpl();
     *      config.getSerializationConfig().addPortableFactory(FACTORY_ID, instance);
     *      ...
     *      return config;
     *  }
     * }
     * </pre>
     */
    private void appendPopulateMethod(List<CStructDescriptor> messages) {
        writer.formatln("public static final %s populateConfig(%s %s, int %s) {",
                        Config.class.getName(),
                        Config.class.getName(),
                        CONFIG,
                        PORTABLE_VERSION)
              .begin()
              .formatln("%s %s = new %s();", FACTORY_IMPL, INSTANCE, FACTORY_IMPL)
              .formatln("%s.getSerializationConfig().addPortableFactory(%s, %s);", CONFIG, FACTORY_ID, INSTANCE)
              .formatln("%s.getSerializationConfig().setPortableVersion(%s);", CONFIG, PORTABLE_VERSION)
              .appendln()
              .formatln("%s.getSerializationConfig()", CONFIG)
              .begin()
              .begin();
        for (CStructDescriptor struct : messages) {
            writer.formatln(".addClassDefinition(%s.%s(%s))",
                            INSTANCE,
                            camelCase("get", struct.getName() + "Definition"),
                            PORTABLE_VERSION);
        }
        writer.append(";")
              .end()
              .end()
              .formatln("return %s;", CONFIG)
              .end()
              .appendln("}")
              .newline();
    }

    private void appendGetDefinitions(List<CStructDescriptor> messages) {
        for (CStructDescriptor message : messages) {
            appendGetDefinition(new JMessage<>(message, helper));
        }
    }

    private void appendGetDefinition(JMessage<?> message) {
        writer.formatln("public %s %s(int %s) {",
                        ClassDefinition.class.getName(),
                        camelCase("get",
                                  message.descriptor()
                                         .getName() + "Definition"),
                        PORTABLE_VERSION)
              .begin()
              .formatln("return new %s(%s, %s, %s)",
                        ClassDefinitionBuilder.class.getName(),
                        FACTORY_ID,
                        getHazelcastClassId(message.instanceType()),
                        PORTABLE_VERSION)
              .begin()
              .begin();
        writer.formatln(".addIntArrayField(\"__fields__\")");
        for (JField field : message.declaredOrderFields()) {
            appendTypeField(field);
        }
        writer.appendln(".build();")
              .end()
              .end()
              .end()
              .appendln("}")
              .newline();
    }

    private void appendTypeField(JField field) {
        if (field.portableRequiresBinarySerialization()) {
            writer.formatln(".addByteArrayField(\"%s\")", field.name());
            return;
        }

        switch (field.type()) {
            case BINARY:
                writer.formatln(".addByteArrayField(\"%s\")", field.name());
                break;
            case BYTE:
                writer.formatln(".addByteField(\"%s\")", field.name());
                break;
            case BOOL:
                writer.formatln(".addBooleanField(\"%s\")", field.name());
                break;
            case DOUBLE:
                writer.formatln(".addDoubleField(\"%s\")", field.name());
                break;
            case ENUM:
            case I32:
                writer.formatln(".addIntField(\"%s\")", field.name());
                break;
            case I16:
                writer.formatln(".addShortField(\"%s\")", field.name());
                break;
            case I64:
                writer.formatln(".addLongField(\"%s\")", field.name());
                break;
            case STRING:
                writer.formatln(".addUTFField(\"%s\")", field.name());
                break;
            case LIST:
                final PList pList = field.toPList();
                appendCollectionTypeField(field, pList.itemDescriptor());
                break;
            case SET:
                final PSet pSet = field.toPSet();
                appendCollectionTypeField(field, pSet.itemDescriptor());
                break;
            case MESSAGE:
                writer.formatln(".addPortableField(\"%s\", %s(%s))",
                                field.name(),
                                camelCase("get",
                                          field.field()
                                               .getDescriptor()
                                               .getName() + "Definition"),
                                PORTABLE_VERSION);
                break;
            default:
                throw new GeneratorException("Not implemented appendTypeField for type: " + field.type() + " in " +
                                             this.getClass()
                                                 .getSimpleName());
        }
    }

    /**
     * Append a specific list type field to the definition.
     *
     * @param field JField to append.
     * <pre>
     * {@code
     *  .addShortArrayField("shortValues")
     * }
     * </pre>
     */
    private void appendCollectionTypeField(JField field, PDescriptor descriptor) {
        switch (descriptor.getType()) {
            case BYTE:
            case BINARY:
                writer.formatln(".addByteArrayField(\"%s\")", field.name());
                break;
            case BOOL:
                writer.formatln(".addBooleanArrayField(\"%s\")", field.name());
                break;
            case DOUBLE:
                writer.formatln(".addDoubleArrayField(\"%s\")", field.name());
                break;
            case I16:
                writer.formatln(".addShortArrayField(\"%s\")", field.name());
                break;
            case I32:
            case ENUM:
                writer.formatln(".addIntArrayField(\"%s\")", field.name());
                break;
            case I64:
                writer.formatln(".addLongArrayField(\"%s\")", field.name());
                break;
            case STRING:
                writer.formatln(".addUTFArrayField(\"%s\")", field.name());
                break;
            case MESSAGE:
                writer.formatln(".addPortableArrayField(\"%s\", %s(%s))",
                                field.name(),
                                camelCase("get", descriptor.getName() + "Definition"),
                                PORTABLE_VERSION);
                break;
            default:
                throw new GeneratorException(
                        "Not implemented appendCollectionTypeField for list with type: " + descriptor.getType() + " in " +
                        this.getClass()
                            .getSimpleName());
        }
    }

}
