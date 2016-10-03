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

         fast_binary:   1.41       --  1.41        =   1.41        ( 18 kB)
              binary:   1.69  1.00 --  1.68  1.00  =   1.69  1.00  ( 26 kB)
      tuple_protocol:   1.82  0.90 --  1.60  0.89  =   1.74  0.89  ( 16 kB)
     binary_protocol:   1.79  0.97 --  1.91  0.96  =   1.84  0.97  ( 26 kB)
    compact_protocol:   1.95  1.04 --  1.93  0.96  =   1.94  1.01  ( 18 kB)
                json:   4.92       --  6.02        =   5.35        ( 36 kB)
       json_protocol:   5.85  4.84 --  6.54  5.28  =   6.11  5.01  ( 57 kB)
          json_named:   5.78       --  8.50        =   6.83        ( 54 kB)
         json_pretty:   8.00       -- 10.61        =   9.01        ( 92 kB)
              pretty:   9.30       --  9.50        =   9.38        ( 74 kB)
```

**NOTE:** Since the test is for the *speed* of the serialization, we are only
interested in the comparison between the serializers, not the absolute values.
All numbers have been normalised to be relative to the native thrift binary protocol
implementation (that's why it's 1.00 for thrift binary).
