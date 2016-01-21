package net.morimekta.providence.testing;

import net.morimekta.providence.Binary;
import net.morimekta.providence.PEnumValue;
import net.morimekta.providence.PMessage;
import net.morimekta.providence.descriptor.PField;
import net.morimekta.providence.util.PStringUtils;
import net.morimekta.providence.util.PTypeUtils;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Stein Eldar Johnsen
 * @since 21.01.16.
 */
public class MessageEq<T extends PMessage<T>>
        extends BaseMatcher<PMessage<T>> {
    private final PMessage<T> expected;

    public MessageEq(PMessage<T> expected) {
        this.expected = expected;
    }

    @Override
    public boolean matches(Object item) {
        if (expected == null)
            return item == null;
        return expected.equals(item);
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("equals(")
                   .appendText(limitToString(expected))
                   .appendText(")");
    }

    @Override
    public void describeMismatch(Object actual, Description mismatchDescription) {
        if (expected == null) {
            mismatchDescription.appendText("expected null, but got " + toString(actual));
        } else if (actual == null) {
            mismatchDescription.appendText("expected " + toString(expected) + " but got null");
        } else {
            LinkedList<String> mismatches = new LinkedList<>();
            collectMismatches("", (PMessage) expected, (PMessage) actual, mismatches);
            mismatchDescription.appendText("expected[\n");
            int i = 0;
            for (String mismatch : mismatches) {
                if (i >= 20) {
                    int remaining = mismatches.size() - i;
                    mismatchDescription.appendText("    ... and " + remaining + "more\n");
                    break;
                }
                mismatchDescription.appendText("    " + mismatch + "\n");
                ++i;
            }
            mismatchDescription.appendText("]");
        }
    }

    protected static <T extends PMessage<T>> void collectMismatches(
            String xPath,
            T expected,
            T actual,
            LinkedList<String> mismatches) {
        // This is pretty heavy calculation, but since it's only done on
        // mismatch / test failure, it should be fine.
        for (PField<?> field : expected.descriptor().getFields()) {
            int key = field.getKey();
            String fieldXPath = xPath.isEmpty() ? field.getName() : xPath + "." + field.getName();

            if (!PTypeUtils.equals(expected.get(key), actual.get(key))) {
                if (!expected.has(key)) {
                    mismatches.add(String.format(
                            "expected %s to be missing, but was %s",
                            fieldXPath,
                            toString(actual.get(field.getKey()))));
                } else if (!actual.has(key)) {
                    mismatches.add(String.format(
                            "expected %s to be %s, but was missing",
                            fieldXPath,
                            toString(expected.get(field.getKey()))));
                } else {
                    switch (field.getType()) {
                        case MESSAGE: {
                            collectMismatches(fieldXPath,
                                              (PMessage) expected.get(key),
                                              (PMessage) actual.get(key),
                                              mismatches);
                            break;
                        }
                        case LIST: {
                            collectListMismatches(fieldXPath,
                                                  (List) expected.get(key),
                                                  (List) actual.get(key),
                                                  mismatches);
                            break;
                        }
                        case SET: {
                            collectSetMismatches(fieldXPath,
                                                 (Set) expected.get(key),
                                                 (Set) actual.get(key),
                                                 mismatches);
                            break;
                        }
                        case MAP: {
                            collectMapMismatches(fieldXPath,
                                                 (Map) expected.get(key),
                                                 (Map) actual.get(key),
                                                 mismatches);
                            break;
                        }
                        default: {
                            mismatches.add(String.format(
                                    "expected %s to be %s, but was %s",
                                    fieldXPath,
                                    toString(expected.get(field.getKey())),
                                    toString(actual.get(field.getKey()))));
                            break;
                        }
                    }
                }
            }
        }
    }

    protected static <K, V> void collectMapMismatches(String xPath,
                                                      Map<K, V> expected,
                                                      Map<K, V> actual,
                                                      LinkedList<String> mismatches) {
        mismatches.addAll(actual.keySet()
                                .stream()
                                .filter(key -> !expected.keySet().contains(key))
                                .map(key -> String.format(
                                        "unexpected map key %s in %s, was %s",
                                        Objects.toString(key),
                                        xPath,
                                        toString(actual.get(key))))
                                .collect(Collectors.toList()));

        for (K key : expected.keySet()) {
            if (!actual.keySet().contains(key)) {
                mismatches.add(String.format(
                        "missing map key %s in %s, to be %s",
                        toString(key),
                        xPath,
                        toString(expected.get(key))));
            } else {
                V exp = expected.get(key);
                V act = actual.get(key);
                if (!PTypeUtils.equals(exp, act)) {
                    // value differs.
                    String keyedXPath = String.format("%s[%s]",
                                                      xPath,
                                                      toString(key));
                    if (exp == null || act == null) {
                        mismatches.add(String.format("expected %s to be %s, but was %s",
                                                     keyedXPath,
                                                     toString(exp),
                                                     toString(act)));
                    } else if (act instanceof PMessage) {
                        collectMismatches(keyedXPath,
                                          (PMessage) exp,
                                          (PMessage) act,
                                          mismatches);
                    } else {
                        mismatches.add(String.format("expected %s to be %s, but was %s",
                                                     keyedXPath,
                                                     toString(exp),
                                                     toString(act)));
                    }
                }
            }
        }
    }

    protected static <T> void collectSetMismatches(String xPath,
                                                   Set<T> expected,
                                                   Set<T> actual,
                                                   LinkedList<String> mismatches) {
        // order does NOT matter regardless of type. The only
        // errors are missing and unexpected values. Partial
        // matches are not checked.
        mismatches.addAll(
                actual.stream()
                      .filter(item -> !expected.contains(item))
                      .map(item -> String.format(
                              "unexpected set value %s in %s",
                              toString(item),
                              xPath))
                      .collect(Collectors.toList()));

        mismatches.addAll(
                expected.stream()
                        .filter(item -> !actual.contains(item))
                        .map(item -> String.format(
                                "missing set value %s in %s",
                                toString(item),
                                xPath))
                        .collect(Collectors.toList()));

    }

    protected static <T> void collectListMismatches(String xPath,
                                                    List<T> expected,
                                                    List<T> actual,
                                                    LinkedList<String> mismatches) {
        Set<T> handledItems = new HashSet<>();

        boolean hasReorder = false;
        LinkedList<String> reordering = new LinkedList<>();
        for (int expectedIndex = 0; expectedIndex < expected.size(); ++expectedIndex) {
            String indexedXPath = String.format("%s[%d]", xPath, expectedIndex);
            T expectedItem = expected.get(expectedIndex);
            handledItems.add(expectedItem);

            int actualIndex = actual.indexOf(expectedItem);
            T actualItem =
                    actual.size() >= expectedIndex ? null : actual.get(expectedIndex);
            if (PTypeUtils.equals(expectedItem, actualItem)) {
                continue;
            }

            int actualItemExpectedIndex = -1;
            if (actualItem != null) {
                actualItemExpectedIndex = expected.indexOf(actualItem);
            }

            if (actualIndex < 0) {
                reordering.add("NaN");
                // this item is missing.
                if (actualItemExpectedIndex < 0) {
                    handledItems.add(actualItem);
                    // replaced with new item, diff them normally.
                    if (actualItem instanceof PMessage) {
                        collectMismatches(indexedXPath,
                                          (PMessage) expectedItem,
                                          (PMessage) actualItem,
                                          mismatches);
                    } else {
                        mismatches.add(String.format(
                                "expected %s to be %s, but was %s",
                                indexedXPath,
                                toString(expectedItem),
                                toString(actualItem)));
                    }
                } else {
                    // the other item is reordered, so this is blindly inserted.
                    mismatches.add(String.format(
                            "missing item %s in %s",
                            toString(expectedItem),
                            indexedXPath));
                }
            } else if (actualIndex != expectedIndex) {
                reordering.add(String.format("%+d", actualIndex - expectedIndex));
                hasReorder = true;
            } else {
                reordering.add("±0");
            }
        }
        for (int actualIndex = 0; actualIndex < actual.size(); ++actualIndex) {
            T actualItem = actual.get(actualIndex);
            if (handledItems.contains(actualItem))
                continue;
            if (expected.contains(actualItem))
                continue;
            String indexedXPath = String.format("%s[%d]", xPath, actualIndex);
            mismatches.add(String.format(
                    "unexpected item %s in %s",
                    toString(actualItem),
                    indexedXPath));
        }
        if (hasReorder) {
            mismatches.add(String.format(
                    "unexpected item ordering in %s: [%s]",
                    xPath,
                    PStringUtils.join(",", reordering)));
        }

    }

    protected static String toString(Object o) {
        if (o == null) {
            return "null";
        } else if (o instanceof PMessage) {
            return limitToString((PMessage) o);
        } else if (o instanceof PEnumValue) {
            return ((PEnumValue) o).descriptor().getName() + "." + ((PEnumValue) o).getName();
        } else if (o instanceof Map) {
            return PStringUtils.join(
                    ",",
                    ((Map<?, ?>) o).entrySet().stream()
                                   .map(e -> toString(e.getKey()) + ":" + toString(e.getValue()))
                                   .collect(Collectors.toList()));
        } else if (o instanceof Collection) {
            return PStringUtils.join(
                    ",",
                    ((Collection<?>) o).stream()
                                       .map(MessageEq::toString)
                                       .collect(Collectors.toList()));
        } else if (o instanceof CharSequence) {
            return "\"" + o.toString() + "\"";
        } else if (o instanceof Binary) {
            int len = ((Binary) o).length();
            if (len > 65) {
                return String.format("binary[%s...+%d]",
                                     ((Binary) o).toHexString().substring(0, 50),
                                     len - 50);
            } else {
                return "binary[" + ((Binary) o).toHexString() + "]";
            }
        } else if (o instanceof Double) {
            long l = ((Double) o).longValue();
            if (o.equals((double) l)) {
                return Long.toString(l);
            } else {
                return o.toString();
            }
        } else {
            return o.toString();
        }
    }

    protected static String limitToString(PMessage<?> message) {
        String tos = message == null ? "null" : message.toString();
        if (tos.length() > 45) {
            tos = tos.substring(0, 40) + "...}";
        }

        return tos;
    }
}
