package net.morimekta.providence.testing;

import net.morimekta.test.providence.calculator.Operation;

import org.hamcrest.Description;
import org.hamcrest.StringDescription;
import org.junit.Before;
import org.junit.Test;

import static net.morimekta.providence.testing.ProvidenceMatchers.hasFieldValueThat;
import static net.morimekta.test.providence.calculator.Operation._Field.OPERATOR;
import static net.morimekta.test.providence.calculator.Operator.DIVIDE;
import static net.morimekta.test.providence.calculator.Operator.MULTIPLY;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author Stein Eldar Johnsen
 * @since 21.01.16.
 */
public class HasFieldValueThatTest {
    private Operation matches;
    private Operation not_matches;

    @Before
    public void setUp() {
        matches = Operation.builder()
                           .setOperator(MULTIPLY)
                           .build();
        not_matches = Operation.builder().build();
    }

    @Test
    public void testMatches() {
        assertThat(matches, hasFieldValueThat("operator", is(MULTIPLY)));
        assertThat(matches, hasFieldValueThat(OPERATOR, is(MULTIPLY)));
        assertTrue(new HasFieldValueThat<>("operator", is(MULTIPLY)).matches(matches));
        assertFalse(new HasFieldValueThat<>("operator", is(DIVIDE)).matches(matches));
        assertFalse(new HasFieldValueThat<>("operator", is(MULTIPLY)).matches(not_matches));
    }

    @Test
    public void testDescribeTo() {
        Description description = new StringDescription();
        new HasFieldValueThat<>("operator", is(MULTIPLY)).describeTo(description);
        assertThat(description.toString(), is(equalTo(
                "has field 'operator' that is <MULTIPLY>")));
    }

    @Test
    public void testDescribeMismatch() {
        Description description = new StringDescription();
        new HasFieldValue<>("operator").describeMismatch(not_matches, description);
        assertThat(description.toString(), is(equalTo(
                "field \'operator\' is missing")));
    }
}
