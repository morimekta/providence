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

#### Serialization Speed

To run the serialization integration test suite, after installing the library,
run:

```bash
mvn package -Pit-serialization
java -jar it-serialization/target/it-serialization.jar --help
java -jar it-serialization/target/it-serialization.jar
```

It will take a while, and print quite a lot to std out.

This is a comparative serialization speed test. It is used to see the progress of
serialization optimization, and to compare with the native thrift libraries.

- The `pvd` columns are the native providence serialization or in the case of the
  `*_protocol` the thrift protocol wrapper performance.
- The `thr` columns is for the native thrift protocol read / write. The "content" is
  exactly same as the providence version. But compatibility tests are not done here,
  but in the `providence-testing` module.

Latest output from the serialization speed IT, sorted by the SUM of the providence
serialization time. Lower is better. There are a number tests, each with a structure
focusing on some aspect or style of structures to test. Exceptions are **not** tested
as part of this, as the way the stack traces are handled quickly deteriorates the
results for reading providence, though not affecting the others much.

**Many containers:**
```
                           READ          WRITE            SUM            SIZE
        name        :   pvd   thr  --  pvd   thr   =   pvd   thr  -- (ratio / size)
              binary:   1.04  1.00 --  0.93  1.00  =   0.99  1.00 -- ( 1.00 / 255.0 KiB)
         fast_binary:   1.32       --  1.39        =   1.35       -- ( 0.84 / 213.6 KiB)
      tuple_protocol:   1.65  0.92 --  1.78  0.86  =   1.72  0.89 -- ( 0.78 / 199.8 KiB)
     binary_protocol:   1.89       --  2.05        =   1.97       -- ( 1.00 / 255.0 KiB)
    compact_protocol:   2.11  1.16 --  2.02  0.98  =   2.06  1.07 -- ( 0.83 / 210.7 KiB)
       json_protocol:   7.40  6.19 --  7.84  5.55  =   7.62  5.87 -- ( 1.75 / 446.4 KiB)
                json:   9.34       --  8.04        =   8.69       -- ( 1.30 / 330.6 KiB)
          json_named:  10.79       --  8.42        =   9.61       -- ( 1.69 / 430.8 KiB)
              pretty:  12.25       --  8.40        =  10.32       -- ( 1.66 / 422.1 KiB)
              config:  12.86       --  9.34        =  11.10       -- ( 2.15 / 547.3 KiB)
         json_pretty:  15.55       -- 10.38        =  12.96       -- ( 2.60 / 664.2 KiB)
```

**Many Optional Fields:**
```
                           READ          WRITE            SUM            SIZE
        name        :   pvd   thr  --  pvd   thr   =   pvd   thr  -- (ratio / size)
              binary:   1.03  1.00 --  1.04  1.00  =   1.04  1.00 -- ( 1.00 / 125.1 KiB)
         fast_binary:   1.37       --  1.70        =   1.54       -- ( 0.89 / 111.0 KiB)
      tuple_protocol:   1.62  0.96 --  1.84  0.91  =   1.73  0.94 -- ( 0.83 / 104.0 KiB)
     binary_protocol:   1.83       --  2.21        =   2.02       -- ( 1.00 / 125.1 KiB)
    compact_protocol:   2.05  1.13 --  2.16  0.94  =   2.10  1.04 -- ( 0.87 / 108.8 KiB)
       json_protocol:   7.52  6.37 --  7.99  5.62  =   7.75  6.00 -- ( 1.65 / 206.3 KiB)
                json:   9.48       --  9.40        =   9.44       -- ( 1.27 / 158.9 KiB)
          json_named:  10.53       --  9.73        =  10.13       -- ( 1.53 / 191.4 KiB)
              pretty:  12.47       --  9.45        =  10.96       -- ( 1.48 / 185.6 KiB)
              config:  12.93       -- 10.06        =  11.50       -- ( 1.78 / 222.4 KiB)
         json_pretty:  13.91       -- 10.83        =  12.37       -- ( 2.02 / 252.7 KiB)
```

**Many Required Fields:**
```
                           READ          WRITE            SUM            SIZE
        name        :   pvd   thr  --  pvd   thr   =   pvd   thr  -- (ratio / size)
              binary:   1.00  1.00 --  1.02  1.00  =   1.01  1.00 -- ( 1.00 / 122.8 KiB)
         fast_binary:   1.42       --  1.70        =   1.56       -- ( 0.89 / 109.0 KiB)
      tuple_protocol:   1.60  0.89 --  1.77  0.79  =   1.68  0.84 -- ( 0.83 / 101.7 KiB)
     binary_protocol:   1.87       --  2.22        =   2.05       -- ( 1.00 / 122.8 KiB)
    compact_protocol:   2.08  1.17 --  2.19  0.95  =   2.14  1.06 -- ( 0.87 / 106.8 KiB)
       json_protocol:   7.58  6.42 --  8.18  5.83  =   7.88  6.12 -- ( 1.67 / 204.6 KiB)
                json:   9.33       --  9.57        =   9.45       -- ( 1.28 / 157.3 KiB)
          json_named:  10.52       --  9.96        =  10.24       -- ( 1.54 / 189.8 KiB)
              pretty:  12.73       --  9.85        =  11.29       -- ( 1.50 / 183.9 KiB)
              config:  13.06       -- 10.36        =  11.71       -- ( 1.80 / 220.9 KiB)
         json_pretty:  13.94       -- 11.21        =  12.58       -- ( 2.04 / 251.1 KiB)
```

**Deep Structure:**
```
                           READ          WRITE            SUM            SIZE
        name        :   pvd   thr  --  pvd   thr   =   pvd   thr  -- (ratio / size)
              binary:   0.94  1.00 --  0.84  1.00  =   0.89  1.00 -- ( 1.00 / 371.5 KiB)
         fast_binary:   1.40       --  1.74        =   1.57       -- ( 0.81 / 302.2 KiB)
      tuple_protocol:   1.89  0.81 --  2.13  0.71  =   2.01  0.76 -- ( 0.75 / 278.4 KiB)
     binary_protocol:   2.34       --  2.85        =   2.60       -- ( 1.00 / 371.5 KiB)
    compact_protocol:   2.51  1.12 --  2.75  0.91  =   2.63  1.02 -- ( 0.81 / 301.4 KiB)
       json_protocol:   8.32  6.67 --  8.82  5.89  =   8.57  6.28 -- ( 1.80 / 670.3 KiB)
                json:   9.16       --  8.31        =   8.73       -- ( 1.22 / 454.9 KiB)
          json_named:  11.55       --  9.13        =  10.34       -- ( 1.75 / 648.7 KiB)
              pretty:  14.71       --  9.24        =  11.97       -- ( 1.72 / 640.2 KiB)
              config:  15.77       -- 10.87        =  13.32       -- ( 2.67 / 990.4 KiB)
         json_pretty:  19.78       -- 12.28        =  16.03       -- ( 3.44 / 1.2 MiB)
```

**NOTE:** Since the test is for the *speed* of the serialization, we are only
interested in the comparison between the serializers, not the absolute values.
All numbers have been normalised to be relative to the native thrift binary protocol
implementation (that's why it's 1.00 for thrift binary).
