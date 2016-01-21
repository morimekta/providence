package net.morimekta.providence.thrift;

import net.morimekta.providence.PMessage;
import net.morimekta.providence.descriptor.PField;

import junit.framework.AssertionFailedError;
import org.apache.thrift.TBase;
import org.apache.thrift.TFieldIdEnum;
import org.junit.internal.builders.IgnoredClassRunner;
import org.junit.runner.RunWith;

import java.util.Objects;

/**
 * Utilities for thrift vs providence serialization comparison.
 */
@RunWith(IgnoredClassRunner.class)
public class TTestUtils {
    public static <TB extends TBase<TB, TF>, TF extends TFieldIdEnum, PM extends PMessage<PM>>
    void messageEqStruct(PM expected, TB actual) {
        messageEqStruct(expected, actual, "");
    }

    public static <TB extends TBase<TB, TF>, TF extends TFieldIdEnum, PM extends PMessage<PM>>
    void messageEqStruct(PM expected, TB actual, String what) {
        if (expected == null) {
            if (what.isEmpty()) {
                what = "TMessage";
            }
            if (actual != null) {
                throw new AssertionFailedError(String.format(
                        "Expected %s to be null, but was %s",
                        what, actual.toString()));
            }
        } else if (actual == null) {
            if (what.isEmpty()) {
                what = "TMessage";
            }
            throw new AssertionFailedError(String.format(
                    "Expected %s to be %s, but was null",
                    what, expected.toString()));
        } else {
            for (PField<?> pField : expected.descriptor().getFields()) {
                String xPath = what.isEmpty() ? pField.getName() : what + "." + pField.getName();
                int key = pField.getKey();
                TF tfield = actual.fieldForId(pField.getKey());

                if (expected.has(key) != actual.isSet(tfield)) {
                    if (expected.has(pField.getKey())) {
                        throw new AssertionFailedError(String.format(
                                "Expected %s to be null, but was %s",
                                xPath, Objects.toString(actual.getFieldValue(tfield))));
                    } else {
                        throw new AssertionFailedError(String.format(
                                "Expected %s to be %s, but was null",
                                what, expected.toString()));
                    }
                } else if (expected.has(pField.getKey())) {

                }
            }
        }
    }
    public static <TB extends TBase<TB, TF>, TF extends TFieldIdEnum, PM extends PMessage<PM>>
    void structEqMessage(TB expected, PM actual) {
        structEqMessage(expected, actual, "");
    }

    public static <TB extends TBase<TB, TF>, TF extends TFieldIdEnum, PM extends PMessage<PM>>
    void structEqMessage(TB expected, PM actual, String what) {
        if (expected == null) {
            if (what.isEmpty()) {
                what = "TBase";
            }
            if (actual != null) {
                throw new AssertionFailedError(String.format(
                        "Expected %s to be null, but was %s",
                        what, actual.toString()));
            }
        } else if (actual == null) {
            if (what.isEmpty()) {
                what = "TBase";
            }
            throw new AssertionFailedError(String.format(
                    "Expected %s to be %s, but was null",
                    what, expected.toString()));
        } else {
            for (PField<?> field : actual.descriptor().getFields()) {
                if (actual.has(field.getKey()) != expected.isSet(expected.fieldForId(field.getKey()))) {
                    if (actual.has(field.getKey())) {

                    } else {

                    }
                }
            }
        }
    }
}
