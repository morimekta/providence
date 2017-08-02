package net.morimekta.providence.testing.generator.extra;

import net.morimekta.providence.testing.generator.Generator;
import net.morimekta.providence.testing.generator.GeneratorContext;

import javax.annotation.Nonnull;
import java.util.Collection;

import static java.lang.Math.abs;

/**
 * Default generator for selecting one of a set of values of the same type..
 */
public class OneOfGenerator<Context extends GeneratorContext<Context>, T>
        implements Generator<Context,T> {
    private final T[] selection;

    @SafeVarargs
    public OneOfGenerator(T... selection) {
        this.selection = selection;
    }

    @SuppressWarnings("unchecked")
    public OneOfGenerator(@Nonnull Collection<T> selection) {
        this((T[]) selection.toArray());
    }

    @Override
    public T generate(Context ctx) {
        if (selection.length == 0) {
            return null;
        }
        return selection[abs(ctx.getRandom().nextInt(selection.length))];
    }
}
