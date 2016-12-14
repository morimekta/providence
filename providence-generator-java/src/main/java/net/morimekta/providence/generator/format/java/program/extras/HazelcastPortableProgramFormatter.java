package net.morimekta.providence.generator.format.java.program.extras;

import net.morimekta.providence.PType;
import net.morimekta.providence.descriptor.PDeclaredDescriptor;
import net.morimekta.providence.generator.GeneratorException;
import net.morimekta.providence.generator.format.java.messages.extras.HazelcastPortableMessageFormatter;
import net.morimekta.providence.generator.format.java.shared.BaseProgramFormatter;
import net.morimekta.providence.generator.format.java.utils.BlockCommentBuilder;
import net.morimekta.providence.generator.format.java.utils.JHelper;
import net.morimekta.providence.generator.format.java.utils.ValueBuilder;
import net.morimekta.providence.reflect.contained.CConst;
import net.morimekta.providence.reflect.contained.CStructDescriptor;
import net.morimekta.providence.reflect.contained.CProgram;
import net.morimekta.providence.reflect.util.ThriftAnnotation;
import net.morimekta.util.io.IndentedPrintWriter;

import com.hazelcast.nio.serialization.Portable;
import com.hazelcast.nio.serialization.PortableFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static net.morimekta.providence.generator.format.java.utils.JUtils.getHazelcastClassId;

/**
 * TBD
 */
public class HazelcastPortableProgramFormatter implements BaseProgramFormatter {

    public static final String FACTORY_ID = "FACTORY_ID";

    private final JHelper helper;
    private final IndentedPrintWriter writer;

    public HazelcastPortableProgramFormatter(IndentedPrintWriter writer, JHelper helper) {
        this.writer = writer;
        this.helper = helper;
    }

    @Override
    public void appendProgramClass(CProgram document) throws GeneratorException {
        ValueBuilder value = new ValueBuilder(writer, helper);

        if (document.getComment() != null) {
            new BlockCommentBuilder(writer)
                    .comment(document.getComment())
                    .finish();
        }

        writer.formatln("public class %s implements %s {", helper.getHazelcastFactoryClassName(document),
                        PortableFactory.class.getName())
                .begin()
                .newline();

        Optional<CConst> factoryID = document.getConstants().stream()
                .filter(t -> t.getName().equals(FACTORY_ID)).findFirst();

        if( !factoryID.isPresent() ) {
            throw new GeneratorException("Need to provide \"const i32 FACTORY_ID = ?\" in the thrift file for " +
                    "hazelcast generation!");
        } else {
            CConst c = factoryID.get();
            String type = helper.getValueType(c.getDescriptor());
            String name = c.getName();
            writer.formatln("public static final %s %s = ", type, name);
            value.appendTypedValue(c.getDefaultValue(), c.getDescriptor());
            writer.append(";")
                    .newline();
        }

        List<CStructDescriptor> messages = new ArrayList<>();

        for (PDeclaredDescriptor c : document.getDeclaredTypes()) {
            try {
                if (PType.MESSAGE == c.getType()) {
                    CStructDescriptor messsage = (CStructDescriptor) c;
                    if (messsage.hasAnnotation(ThriftAnnotation.JAVA_HAZELCAST_CLASS_ID)) {
                        writer.formatln("public static final int %s = %s;", getHazelcastClassId(messsage.getName()),
                                messsage.getAnnotationValue(ThriftAnnotation.JAVA_HAZELCAST_CLASS_ID));
                        messages.add(messsage);
                    }
                }
            } catch (Exception e) {
                throw new GeneratorException(e.getMessage());
            }
        }

        if( messages.isEmpty() ) {
            throw new GeneratorException("No annotations available to generate!");
        } else {
            writer.newline()
                    .appendln("@Override")
                    .formatln("public %s create(int classId) {", Portable.class.getName())
                    .begin()
                    .appendln("switch(classId) {")
                    .begin();
            for( CStructDescriptor message : messages ) {
                writer.formatln("case %s: {", getHazelcastClassId(message.getName()))
                        .begin()
                        .formatln("return new %s.%s();", message.getName(),
                                HazelcastPortableMessageFormatter.WRAPPER_CLASS_NAME)
                        .end()
                        .appendln("}");
            }
            writer.appendln("default: {")
                    .begin()
                    .appendln("return null;")
                    .end()
                    .appendln("}")
                    .end()
                    .appendln("}")
                    .end()
                    .appendln("}");
        }

        writer.end()
                .newline()
                .appendln('}')
                .newline();
    }

}
