package net.morimekta.providence.testing.generator.extra;

import net.morimekta.providence.PEnumValue;
import net.morimekta.providence.descriptor.PEnumDescriptor;
import net.morimekta.providence.testing.generator.Generator;
import net.morimekta.providence.testing.generator.GeneratorContext;

import static java.lang.Math.abs;

/**
 * Generator helper class for enum value (asString) of the selected enum.
 */
public class EnumNameGenerator<Context extends GeneratorContext<Context>,
                                E extends PEnumValue<E>> implements Generator<Context,String>{
    private final E[] selection;

    public EnumNameGenerator(PEnumDescriptor<E> descriptor) {
        this.selection = descriptor.getValues();
    }

    @SafeVarargs
    public EnumNameGenerator(E... selection) {
        this.selection = selection;
    }

    @Override
    public String generate(Context ctx) {
        if (selection.length == 0) {
            return null;
        }
        return selection[abs(ctx.getRandom().nextInt(selection.length))].asString();
    }
}
