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

### Serialization Speed

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
- The `jck` columns are the jackson serialization that should match the `json_named`
  serialization.

Latest output from the serialization speed IT, sorted by the SUM of the providence
serialization time. Lower is better. There are a number tests, each with a structure
focusing on some aspect or style of structures to test. Exceptions are **not** tested
as part of this, as the way the stack traces are handled quickly deteriorates the
results for reading providence, though not affecting the others much.

```bash
java -jar it-serialization/target/it-serialization.jar --runs 50 --generate 1000
```

#### Many containers:

```
                              READ                 WRITE                  SUM              SIZE
        name        :   pvd   thr   jck  --  pvd   thr   jck   =   pvd   thr   jck  -- (ratio / size)    
              binary:   0.97  1.00       --  0.85  1.00        =   0.91  1.00       -- ( 1.00 / 5.2 MiB)
         fast_binary:   1.26             --  1.30              =   1.28             -- ( 0.84 / 4.3 MiB)
      tuple_protocol:   1.65  0.97       --  1.62  0.76        =   1.64  0.87       -- ( 0.78 / 4.0 MiB)
     binary_protocol:   1.81             --  1.94              =   1.87             -- ( 1.00 / 5.2 MiB)
    compact_protocol:   2.04  1.21       --  1.81  0.91        =   1.93  1.06       -- ( 0.83 / 4.3 MiB)
                json:   5.21             --  5.35              =   5.28             -- ( 1.30 / 6.7 MiB)
          json_named:   6.08        9.51 --  5.74        1.66  =   5.91        5.59 -- ( 1.69 / 8.8 MiB)
         json_pretty:   8.08             --  7.72              =   7.90             -- ( 2.62 / 13.6 MiB)
              pretty:   9.21             --  6.45              =   7.83             -- ( 1.66 / 8.6 MiB)
              config:   9.79             --  7.30              =   8.54             -- ( 2.16 / 11.2 MiB)
```

#### Many Optional Fields:

```
                              READ                 WRITE                  SUM              SIZE
        name        :   pvd   thr   jck  --  pvd   thr   jck   =   pvd   thr   jck  -- (ratio / size)    
              binary:   0.94  1.00       --  0.91  1.00        =   0.93  1.00       -- ( 1.00 / 2.5 MiB)
         fast_binary:   1.24             --  1.50              =   1.37             -- ( 0.89 / 2.2 MiB)
      tuple_protocol:   1.64  1.04       --  1.63  0.84        =   1.63  0.94       -- ( 0.83 / 2.0 MiB)
     binary_protocol:   1.71             --  1.92              =   1.81             -- ( 1.00 / 2.5 MiB)
    compact_protocol:   1.97  1.30       --  1.82  0.92        =   1.89  1.11       -- ( 0.87 / 2.1 MiB)
                json:   5.30             --  5.95              =   5.62             -- ( 1.27 / 3.1 MiB)
          json_named:   6.01        8.91 --  6.12        1.56  =   6.06        5.24 -- ( 1.53 / 3.8 MiB)
         json_pretty:   7.23             --  7.50              =   7.37             -- ( 2.02 / 5.0 MiB)
              pretty:   8.91             --  6.87              =   7.89             -- ( 1.48 / 3.6 MiB)
              config:   9.37             --  7.45              =   8.41             -- ( 1.77 / 4.4 MiB)
```

#### Many Required Fields:

```
                              READ                 WRITE                  SUM              SIZE
        name        :   pvd   thr   jck  --  pvd   thr   jck   =   pvd   thr   jck  -- (ratio / size)    
              binary:   0.89  1.00       --  0.94  1.00        =   0.92  1.00       -- ( 1.00 / 2.5 MiB)
         fast_binary:   1.18             --  1.59              =   1.39             -- ( 0.89 / 2.2 MiB)
      tuple_protocol:   1.47  0.96       --  1.64  0.74        =   1.55  0.85       -- ( 0.83 / 2.0 MiB)
     binary_protocol:   1.61             --  2.02              =   1.82             -- ( 1.00 / 2.5 MiB)
    compact_protocol:   1.86  1.27       --  1.90  0.97        =   1.88  1.12       -- ( 0.87 / 2.1 MiB)
                json:   5.01             --  6.25              =   5.63             -- ( 1.27 / 3.1 MiB)
          json_named:   5.79        8.46 --  6.60        1.67  =   6.19        5.07 -- ( 1.53 / 3.8 MiB)
         json_pretty:   6.87             --  8.09              =   7.48             -- ( 2.02 / 5.0 MiB)
              pretty:   8.61             --  7.31              =   7.96             -- ( 1.48 / 3.6 MiB)
              config:   8.94             --  7.89              =   8.42             -- ( 1.78 / 4.4 MiB)
```

#### Deep Structure:

```
                              READ                 WRITE                  SUM              SIZE
        name        :   pvd   thr   jck  --  pvd   thr   jck   =   pvd   thr   jck  -- (ratio / size)    
              binary:   0.86  1.00       --  0.73  1.00        =   0.79  1.00       -- ( 1.00 / 7.3 MiB)
         fast_binary:   1.34             --  1.65              =   1.50             -- ( 0.82 / 6.0 MiB)
      tuple_protocol:   1.96  0.89       --  2.11  0.65        =   2.04  0.77       -- ( 0.75 / 5.5 MiB)
    compact_protocol:   2.47  1.23       --  2.40  0.86        =   2.43  1.04       -- ( 0.81 / 6.0 MiB)
     binary_protocol:   2.31             --  2.73              =   2.52             -- ( 1.00 / 7.3 MiB)
                json:   5.14             --  5.83              =   5.49             -- ( 1.22 / 9.0 MiB)
          json_named:   6.22        9.55 --  6.44        1.55  =   6.33        5.55 -- ( 1.74 / 12.8 MiB)
              pretty:  10.85             --  6.94              =   8.89             -- ( 1.71 / 12.6 MiB)
         json_pretty:   9.62             -- 10.09              =   9.85             -- ( 3.41 / 25.0 MiB)
              config:  11.91             --  8.56              =  10.23             -- ( 2.65 / 19.4 MiB)
```

**NOTE:** The thrift JSON protocol was removed because some weird bug keeps messing
up deserialization and stopping the test. I will put it back once the bug is fixed or
can be bypassed.

**NOTE:** Since the test is for the *speed* of the serialization, we are only
interested in the comparison between the serializers, not the absolute values.
All numbers have been normalised to be relative to the native thrift binary protocol
implementation (that's why it's 1.00 for thrift binary).
