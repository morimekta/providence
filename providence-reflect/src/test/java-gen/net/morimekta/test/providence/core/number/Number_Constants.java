package net.morimekta.test.providence.core.number;

@SuppressWarnings("unused")
public class Number_Constants {
    private Number_Constants() {}

    public static final net.morimekta.test.providence.core.number.Imaginary kSqrtMinusOne;
    static {
        kSqrtMinusOne = net.morimekta.test.providence.core.number.Imaginary.builder()
                .setV(0.0d)
                .setI(-1.0d)
                .build();
    }
}