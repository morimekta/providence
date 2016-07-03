Providence Utils : Testing
==========================

The `providence-testing` library is meant for testing and comparing providence
messages. Notable methods are:

* `MessageMatchers.messageEq(PMessage m)`: Creates a matcher to use with
  junit's `Asserts.assertThat(T actual, Matcher<T> matcher)`. Example:
  `Asserts.assertThat(actual, MessageMatchers.messageEq(expected))`. The
  printed output on a mismatch is constructed to be as useful as possible
  when a lower number of differences appear (<10), printing single differences
  per line.
