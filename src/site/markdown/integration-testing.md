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

         fast_binary:   0.50       --  0.32        =   0.82        ( 18 kB)
              binary:   0.59  0.38 --  0.39  0.23  =   0.98  0.60  ( 26 kB)
     binary_protocol:   0.60  0.34 --  0.44  0.22  =   1.03  0.56  ( 26 kB)
    compact_protocol:   0.68  0.38 --  0.45  0.23  =   1.13  0.61  ( 18 kB)
      tuple_protocol:   0.72  0.42 --  0.43  0.28  =   1.15  0.71  ( 16 kB)
                json:   1.69       --  1.34        =   3.03        ( 36 kB)
       json_protocol:   2.18  1.82 --  1.57  1.23  =   3.74  3.04  ( 57 kB)
          json_named:   2.34       --  2.20        =   4.54        ( 54 kB)
              pretty:   3.06       --  1.99        =   5.05        ( 74 kB)
         json_pretty:   3.01       --  2.59        =   5.60        ( 92 kB)
```

**NOTE:** Since the test is for the *speed* of the serialization, we are only
interested in the comparison between the serializers, not the absolute values.
For a future update of the IT, it should be updated to test against the
binary_protocol using a ratio to verify that the serialization speed not to
drift to become slower. **NOTE 2:** With this update, the test may become flaky,
and should not necessarily be used to block or approve updates.
