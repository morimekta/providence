package net.morimekta.providence.testing;

import net.morimekta.test.providence.testing.calculator.Operation;
import net.morimekta.test.providence.testing.calculator.Operator;

import org.hamcrest.Description;
import org.hamcrest.StringDescription;
import org.junit.Before;
import org.junit.Test;

import static net.morimekta.providence.testing.ProvidenceMatchers.hasFieldValue;
import static net.morimekta.test.providence.testing.calculator.Operation._Field.OPERATOR;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author Stein Eldar Johnsen
 * @since 21.01.16.
 */
public class HasFieldValueTest {
    private Operation matches;
    private Operation not_matches;

    @Before
    public void setUp() {
        matches = Operation.builder()
                           .setOperator(Operator.MULTIPLY)
                           .build();
        not_matches = Operation.builder().build();
    }

    @Test
    public void testMatches() {
        assertThat(matches, hasFieldValue("operator"));
        assertThat(matches, hasFieldValue(OPERATOR));
        assertTrue(new HasFieldValue<>("operator").matches(matches));
        assertFalse(new HasFieldValue<>("operator").matches(not_matches));
    }

    @Test
    public void testDescribeTo() {
        Description description = new StringDescription();
        new HasFieldValue<>("operator").describeTo(description);
        assertThat(description.toString(), is(equalTo(
                "has field 'operator'")));
    }

    @Test
    public void testDescribeMismatch() {
        Description description = new StringDescription();
        new HasFieldValue<>("operator").describeMismatch(not_matches, description);
        assertThat(description.toString(), is(equalTo(
                "field \'operator\' is missing")));
    }
}
