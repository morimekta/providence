package net.morimekta.providence.generator.format.java;

import net.morimekta.providence.generator.format.java.messages.BuilderCommonMemberFormatter;
import net.morimekta.providence.generator.format.java.messages.BuilderCoreOverridesFormatter;
import net.morimekta.providence.generator.format.java.messages.CommonBuilderFormatter;
import net.morimekta.providence.generator.format.java.messages.CommonMemberFormatter;
import net.morimekta.providence.generator.format.java.messages.CommonOverridesFormatter;
import net.morimekta.providence.generator.format.java.messages.CoreOverridesFormatter;
import net.morimekta.providence.generator.format.java.messages.extra.AndroidMessageFormatter;
import net.morimekta.providence.generator.format.java.messages.extra.JacksonMessageFormatter;
import net.morimekta.providence.generator.format.java.shared.BaseMessageFormatter;
import net.morimekta.providence.generator.format.java.shared.MessageMemberFormatter;
import net.morimekta.providence.generator.format.java.utils.JHelper;
import net.morimekta.providence.generator.format.java.utils.JMessage;
import net.morimekta.util.io.IndentedPrintWriter;

import com.google.common.collect.ImmutableList;

import java.util.List;

/**
 * TODO(steineldar): Make a proper class description.
 */
public class JavaMessageFormatter extends BaseMessageFormatter {
    public JavaMessageFormatter(IndentedPrintWriter writer,
                                JHelper helper,
                                JavaOptions options) {
        this(false, false, writer, helper, options);
    }

    public JavaMessageFormatter(boolean inner,
                                boolean makePrivate,
                                IndentedPrintWriter writer,
                                JHelper helper,
                                JavaOptions options) {
        super(inner, makePrivate, writer, helper, getFormatters(writer, helper, options));
    }

    public String getClassName(JMessage<?> message) {
        return message.instanceType();
    }

    private static List<MessageMemberFormatter> getFormatters(IndentedPrintWriter writer,
                                                              JHelper helper,
                                                              JavaOptions options) {
        ImmutableList.Builder<MessageMemberFormatter> builderFormatters = ImmutableList.builder();
        builderFormatters.add(new BuilderCommonMemberFormatter(writer, helper))
                         .add(new BuilderCoreOverridesFormatter(writer, helper));

        ImmutableList.Builder<MessageMemberFormatter> formatters = ImmutableList.builder();
        formatters.add(new CommonMemberFormatter(writer, helper))
                  .add(new CoreOverridesFormatter(writer))
                  .add(new CommonOverridesFormatter(writer))
                  .add(new CommonBuilderFormatter(writer, helper, builderFormatters.build()));

        if (options.android) {
            formatters.add(new AndroidMessageFormatter(writer));
        }
        if (options.jackson) {
            formatters.add(new JacksonMessageFormatter(writer, helper));
        }

        return formatters.build();
    }
}
