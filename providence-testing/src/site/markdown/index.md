Providence Utils : Testing
==========================

The `providence-testing` library is meant for helping with testing related to
providence, and comparing providence messages.

### Comparing And Matching

There are 3 notable hamcrest matchers added in the testing library:

* `equalToMessage(expected)`: Checks if two messages is equal. This is
  essentially the same as `equals(value)`, but with different output on
  failure. Instead of just displaying the two messages' `toString()`
  output it tried to generate a field-by-field diff.

* `hasFieldValue(path, value)`: Checks if the 'actual' value is a message
  that has a field with the specified path and the given value. So a
  short-hand (with better output) of checking the specified field. To these
  two checks the same, just that the latter has better failure output.:
    ```java
    assertThat(msg.getMyField().getOtherField(), is("something"));
    assertThat(msg, hasFieldValue("my_field.other_field", "something"));
    ```

* `hasFieldValueThat(path, matcher)`: Essentially same as `hasFieldValue`
  bit instead of just value comparison, matches the value with a complete
  hamcrest matcher. E.g.:
    ```java
    assertThat(msg, hasFieldValueThat("my_field.other_field", startsWith("boo.")));
    ```

### Message Generator

There is also added a message generator that can be handled as a junit `@Rule`.
The `MessageGenerator` can generate and fill message either with totally random
data, or using special rules:

```java
class MyTest {
    @Rule
    public MessageGenerator gen = new MessageGenerator()
            .dumpOnFailure()
            .addFactory(f -> f.getName().endsWith("uuid") ? () -> UUID.randomUUID().toString() : null);

    @Test
    public testSomething() {
        gen.addFactory(f -> f.equals(MyMessage._Field.NAME) ? () -> "name" : null);
        MyMessage msg = gen.generate(MyMessage.kDescriptor);
        sut.doSomething(msg);

        assertThat(sut.state(), is(SystemToTest.CORRECT));
    }
}
```