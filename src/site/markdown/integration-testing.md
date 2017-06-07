Integration Testing
===================

The providence project contains integration tests. The integration
tests can be run separately, and is not a dependency for running
the main test suites.

There are currently two IT test suites.

- **[it-serialization]**: Thorough testing of serialization focusing on
  serialization speed. The thrift compatibility is for the most part
  already tested in `providence-thrift`.
- **[it-hazelcast]**: Proper testing of generated hazelcast generated
  code by using actual generated code against a local hazelcast instance
  including how thrift IDL updates are handled both forward and backward. 

To run an integration test suite, call the command:

```bash
mvn verify -Pit-serialization
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
              binary:   1.23  1.00 --  0.86  1.00  =   1.04  1.00
         fast_binary:   1.91       --  1.40        =   1.65
      tuple_protocol:   2.29  0.91 --  1.80  0.89  =   2.04  0.90
     binary_protocol:   2.61  1.00 --  2.23  1.00  =   2.42  1.00
    compact_protocol:   2.74  1.11 --  2.16  0.92  =   2.45  1.01
                json:   6.01       --  6.84        =   6.42
       json_protocol:   7.18  5.26 --  6.67  5.07  =   6.92  6.12
              pretty:  10.38       --  7.94        =   9.16
          json_named:   6.81       -- 11.90        =   9.35
         json_pretty:   9.25       -- 14.14        =  11.69
```

**NOTE:** Since the test is for the *speed* of the serialization, we are only
interested in the comparison between the serializers, not the absolute values.
All numbers have been normalised to be relative to the native thrift binary protocol
implementation (that's why it's 1.00 for thrift binary).
