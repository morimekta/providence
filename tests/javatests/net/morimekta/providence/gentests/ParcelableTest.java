package net.morimekta.providence.gentests;

import net.morimekta.providence.Binary;
import net.morimekta.test.alltypes.AllTypes;
import net.morimekta.test.alltypes.Empty;
import net.morimekta.test.alltypes.OneType;
import net.morimekta.test.alltypes.Other;
import net.morimekta.test.alltypes.Values;
import net.morimekta.test.calculator.Operand;
import net.morimekta.test.calculator.Operation;
import net.morimekta.test.calculator.Operator;
import net.morimekta.test.number.Imaginary;
import net.morimekta.test.requirement.ExceptionFields;
import net.morimekta.test.requirement.Value;

import org.junit.Test;

import android.os.Parcel;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

/**
 * Tests of generated code providing android.os.Parcelable support.
 */
public class ParcelableTest {
    @Test
    public void testAllTypes() {
        Parcel parcel = Parcel.obtain();

        AllTypes original = AllTypes.builder()
                                    .setBl(true)
                                    .setBn(Binary.wrap(new byte[]{0, 1, 2, 3, 4, 5}))
                                    .setBt((byte) 6)
                                    .setD(7.8d)
                                    .setI(9)
                                    .setL(10L)
                                    .setO(Other.builder().setV(Values.FIFTH).build())
                                    .setS("11")
                                    .setSh((short) 12)
                                    .setV(Values.THIRD)
                                    .build();
        original.writeToParcel(parcel, 0);
        Empty empty = Empty.builder().build();
        empty.writeToParcel(parcel, 0);

        AllTypes copy = AllTypes.CREATOR.createFromParcel(parcel);
        assertEquals(original, copy);
        Empty other = Empty.CREATOR.createFromParcel(parcel);
        assertEquals(empty, other);
    }

    @Test
    public void testCalculator() {
        Parcel parcel = Parcel.obtain();

        Operation original = Operation
                .builder()
                .setOperator(Operator.MULTIPLY)
                .addToOperands(
                        Operand.builder()
                               .setOperation(
                                       Operation.builder()
                                                .setOperator(Operator.ADD)
                                                .addToOperands(Operand.builder()
                                                                      .setNumber(1234)
                                                                      .build(),
                                                               Operand.builder()
                                                                      .setNumber(4.321)
                                                                      .build())
                                                .build())
                               .build())
                .addToOperands(
                        Operand.builder().setImaginary(
                                Imaginary.builder()
                                         .setV(1.7)
                                         .setI(-2.0)
                                         .build())
                               .build())
                .build();

        original.writeToParcel(parcel, 0);
        Operation copy = Operation.CREATOR.createFromParcel(parcel);
        assertEquals(original, copy);
    }
}
