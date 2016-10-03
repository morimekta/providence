Integration Testing
===================

Integration testing of the providence protocols. To run the integration test
suite run the command:

```sh
$ mvn verify -Pit
```

It will take a while, and print quite a log to the std out.

#### Serialization Speed

This is a comparative serialization speed test. It is used to see the progress of
serialization optimization, and to compare with the native thrift libraries.

the `pvd` columns are the native providence serialization or in the case of the
`*_protocol` the thrift protocol wrapper performance.

The `thr` columns is for the native thrift protocol read / write. The "content" is
exactly same as the providence version. But compatibility tests are not done here,
but in the `providence-testing` module.

Latest output from the serialization speed IT, sorted by the SUM of the providence
serialization time. Lower is better.

```
                           read          write            SUM
        name        :   pvd   thr  --  pvd   thr   =   pvd   thr

         fast_binary:   1.34       --  0.84        =   2.18        ( 18 kB)
              binary:   1.58  0.94 --  1.03  0.57  =   2.61  1.51  ( 26 kB)
      tuple_protocol:   1.69  0.86 --  0.97  0.55  =   2.66  1.41  ( 16 kB)
     binary_protocol:   1.72  0.92 --  1.16  0.55  =   2.88  1.47  ( 26 kB)
    compact_protocol:   1.81  0.95 --  1.14  0.55  =   2.94  1.50  ( 18 kB)
                json:   4.75       --  3.59        =   8.34        ( 36 kB)
       json_protocol:   5.58  4.64 --  4.02  3.15  =   9.61  7.80  ( 57 kB)
          json_named:   5.50       --  5.01        =  10.50        ( 54 kB)
         json_pretty:   7.69       --  6.48        =  14.16        ( 92 kB)
              pretty:   9.51       --  5.59        =  15.10        ( 74 kB)
```

**TODO:** Fix so the numbers are all relative to *thrift binary protocol*,
that should stabilize the test to make it an actual test.

**NOTE:** Since the test is for the *speed* of the serialization, we are only
interested in the comparison between the serializers, not the absolute values.
For a future update of the IT, it should be updated to test against the
binary_protocol using a ratio to verify that the serialization speed not to
drift to become slower. **NOTE 2:** With this update, the test may become flaky,
and should not necessarily be used to block or approve updates.
