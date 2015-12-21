package org.apache.test;

import android.os.Parcel;

import org.apache.test.alltypes.AllTypes;
import org.apache.test.alltypes.Empty;
import org.apache.test.alltypes.Other;
import org.apache.test.alltypes.Values;
import org.apache.test.calculator.Operand;
import org.apache.test.calculator.Operation;
import org.apache.test.calculator.Operator;
import org.apache.test.number.Imaginary;
import org.junit.Assert;
import org.junit.Test;

/**
 * Tests of generated code providing android.os.Parcelable support.
 */
public class ParcelableTest {
    @Test
    public void testAllTypes() {
        Parcel parcel = Parcel.obtain();

        AllTypes original = AllTypes.builder()
                                    .setBl(true)
                                    .setBn(new byte[]{0, 1, 2, 3, 4, 5})
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
        Assert.assertEquals(original, copy);
        Empty other = Empty.CREATOR.createFromParcel(parcel);
        Assert.assertEquals(empty, other);
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
        Assert.assertEquals(original, copy);
    }
}
