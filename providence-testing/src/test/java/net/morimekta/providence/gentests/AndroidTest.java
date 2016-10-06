package net.morimekta.providence.gentests;

import net.morimekta.test.android.OptionalFields;

import org.junit.Test;

import android.os.Parcel;

import static org.junit.Assert.assertEquals;

/**
 * Testing of android-generated properties
 */
public class AndroidTest {
    @Test
    public void testParcelable() {
        OptionalFields of = OptionalFields.builder()
                                          .setIntegerValue(55)
                                          .build();

        Parcel parcel = Parcel.obtain();

        parcel.writeTypedObject(of, 0);

        OptionalFields of2 = parcel.readTypedObject(OptionalFields.CREATOR);

        assertEquals(of, of2);
    }
}
