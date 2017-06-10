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
              binary:   1.18  1.00 --  0.84  1.00  =   1.01  1.00
         fast_binary:   1.46       --  1.45        =   1.45
      tuple_protocol:   1.90  0.90 --  1.77  0.81  =   1.84  0.85
     binary_protocol:   2.22  0.99 --  2.19  1.00  =   2.20  0.99
    compact_protocol:   2.42  1.13 --  2.18  0.94  =   2.30  1.04
                json:   7.48       --  5.86        =   6.67
       json_protocol:   6.90  5.45 --  6.79  5.28  =   6.85  5.36
          json_named:   8.77       --  6.38        =   7.57
              pretty:  10.11       --  7.86        =   8.98
         json_pretty:  12.85       --  8.62        =  10.74
```

**NOTE:** Since the test is for the *speed* of the serialization, we are only
interested in the comparison between the serializers, not the absolute values.
All numbers have been normalised to be relative to the native thrift binary protocol
implementation (that's why it's 1.00 for thrift binary).
