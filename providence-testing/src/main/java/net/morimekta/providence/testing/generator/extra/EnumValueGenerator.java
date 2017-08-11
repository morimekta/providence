package net.morimekta.providence.testing.generator.extra;

import net.morimekta.providence.PEnumValue;
import net.morimekta.providence.descriptor.PEnumDescriptor;
import net.morimekta.providence.testing.generator.Generator;
import net.morimekta.providence.testing.generator.GeneratorContext;

import static java.lang.Math.abs;

/**
 * Generator helper class for enum value (asInteger) of the selected enum.
 */
public class EnumValueGenerator<Context extends GeneratorContext<Context>,
                                E extends PEnumValue<E>> implements Generator<Context,Integer>{
    private final E[] selection;

    public EnumValueGenerator(PEnumDescriptor<E> descriptor) {
        this.selection = descriptor.getValues();
    }

    @SafeVarargs
    public EnumValueGenerator(E... selection) {
        this.selection = selection;
    }

    @Override
    public Integer generate(Context ctx) {
        if (selection.length == 0) {
            return null;
        }
        return selection[abs(ctx.getRandom().nextInt(selection.length))].asInteger();
    }
}
