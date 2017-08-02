package net.morimekta.providence.testing.generator.defaults;

import net.morimekta.providence.PEnumValue;
import net.morimekta.providence.descriptor.PEnumDescriptor;
import net.morimekta.providence.testing.generator.Generator;
import net.morimekta.providence.testing.generator.GeneratorContext;

import static java.lang.Math.abs;

/**
 * Default generator for an enum field.
 */
public class EnumGenerator<Context extends GeneratorContext<Context>, E extends PEnumValue<E>>
        implements Generator<Context, E> {
    private final E[] values;

    public EnumGenerator(PEnumDescriptor<E> descriptor) {
        values = descriptor.getValues();
    }

    @Override
    public E generate(Context ctx) {
        if (values.length == 0) {
            return null;
        }
        return values[abs(ctx.getRandom().nextInt(values.length))];
    }
}
