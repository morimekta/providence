package net.morimekta.providence.gentests;

import net.morimekta.test.android.calculator.Operand;
import net.morimekta.test.android.calculator.Operation;
import net.morimekta.test.android.calculator.Operator;
import net.morimekta.test.android.number.Imaginary;
import net.morimekta.test.android.DefaultValues;
import net.morimekta.test.android.Value;
import net.morimekta.util.Binary;

import org.junit.Test;

import android.os.Parcel;

import static net.morimekta.providence.testing.ProvidenceMatchers.messageEq;
import static org.junit.Assert.assertThat;

/**
 * Tests of generated code providing android.os.Parcelable support.
 */
public class ParcelableTest {
    @Test
    public void testDefaultValues() {
        Parcel parcel = Parcel.obtain();

        DefaultValues original = DefaultValues.builder()
                                              .setBooleanValue(true)
                                              .setByteValue((byte) 6)
                                              .setIntegerValue(9)
                                              .setLongValue(10L)
                                              .setDoubleValue(7.8d)
                                              .setShortValue((short) 12)
                                              .setStringValue("11")
                                              .setBinaryValue(Binary.wrap(new byte[]{0, 1, 2, 3, 4, 5}))
                                              .setEnumValue(Value.THIRD)
                                              .build();
        original.writeToParcel(parcel, 0);
        DefaultValues copy = DefaultValues.CREATOR.createFromParcel(parcel);
        assertThat(copy, messageEq(original));
    }

    @Test
    public void testCalculator() {
        Parcel parcel = Parcel.obtain();

        Operation original = Operation.builder()
                                      .setOperator(Operator.MULTIPLY)
                                      .addToOperands(Operand.builder()
                                                            .setOperation(Operation.builder()
                                                                                   .setOperator(Operator.ADD)
                                                                                   .addToOperands(Operand.builder()
                                                                                                         .setNumber(1234)
                                                                                                         .build(),
                                                                                                  Operand.builder()
                                                                                                         .setNumber(
                                                                                                                 4.321)
                                                                                                         .build())
                                                                                   .build())
                                                            .build())
                                      .addToOperands(Operand.builder()
                                                            .setImaginary(Imaginary.builder()
                                                                                   .setV(1.7)
                                                                                   .setI(-2.0)
                                                                                   .build())
                                                            .build())
                                      .build();

        original.writeToParcel(parcel, 0);
        Operation copy = Operation.CREATOR.createFromParcel(parcel);
        assertThat(copy, messageEq(original));
    }
}
