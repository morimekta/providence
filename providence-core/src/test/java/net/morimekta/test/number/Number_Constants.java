package net.morimekta.test.number;

@SuppressWarnings("unused")
public class Number_Constants {
    private Number_Constants() {}

    public static final net.morimekta.test.number.Imaginary kSqrtMinusOne;
    static {
        kSqrtMinusOne = net.morimekta.test.number.Imaginary.builder()
                .setV(0.0d)
                .setI(-1.0d)
                .build();
    }
}