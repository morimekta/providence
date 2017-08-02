Providence Utils : Testing
==========================

The `providence-testing` library is meant for helping with testing related to
providence, and comparing providence messages.

### Comparing And Matching

There are 3 notable hamcrest matchers added in the testing library. These are
available throu static methods on `ProvidenceMatchers`:

* `equalToMessage(expected)`: Checks if two messages is equal. This is
  essentially the same as `equals(value)`, but with different output on
  failure. Instead of just displaying the two messages' `toString()`
  output it tried to generate a field-by-field diff.

* `hasFieldValue(path)`: Checks that the value at the given path is
  present. So a short-hand (with better output) of checking the specified
  field. To these two checks the same, just that the last has better
  failure output:
    ```java
    assertTrue(msg.getMyField().hasOtherField());
    assertThat(msg.getMyField().hasOtherField(), is(true));
    assertThat(msg, hasFieldValue("my_field.other_field"));
    ```

* `hasFieldValueThat(path, matcher)`: Uses the same depth checking as
  `hasVieldValue`, but also takes a matcher to match against the actual
  value. So these would be equivalent, but one with better failure output:
    ```java
    assertThat(msg.getMyField().getOtherField(), startsWith("boo."));
    assertThat(msg, hasFieldValueThat("my_field.other_field", startsWith("boo.")));
    ```

### Generating Messages for Testing

There is also added a message generator that can be handled with a junit `@Rule`.
The `MessageGenerator` can generate and fill message either with totally random
data, or using special rules, the generator watcher is a simple wrapper that
handles multiple message generators, setting up default generators and keeping
messages in case of failure.

```java
class MyTest {
    @Rule
    SimpleGeneratorWatcher generator =
            GeneratorWatcher.create()
                            .dumpOnFailure()
                            .withGenerator(MyMessage.kDescriptor, gen -> {
                                gen.setValueGenerator(MyMessage._Field.UUID, ctx -> UUID.randomUUID().toString());
                            });

    @Test
    public testSomething() {
        generator.withGenerator(MyMessage.kDescriptor, gen -> {
            gen.setValueGenerator(MyMessage._Field.NAME, ctx -> ctx.getFairy().person().getFullName());
            gen.setValueGenerator(MyMessage._Field.AGE, ctx -> 20 + ctx.getRandom().nextInt(35));
        });

        MyMessage msg = generator.generate(MyMessage.kDescriptor);
        sut.doSomething(msg);

        assertThat(sut.state(), is(SystemUnderTest.CORRECT));
    }
}
```
