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
              binary:   1.10  1.00       --  0.80  1.00        =   0.95  1.00       -- ( 1.00 / 5.2 MiB)
         fast_binary:   1.39             --  1.22              =   1.31             -- ( 0.84 / 4.4 MiB)
      tuple_protocol:   1.79  1.04       --  1.52  0.73        =   1.66  0.89       -- ( 0.78 / 4.1 MiB)
     binary_protocol:   1.90             --  1.81              =   1.86             -- ( 1.00 / 5.2 MiB)
    compact_protocol:   2.24  1.28       --  1.73  0.89        =   1.99  1.09       -- ( 0.83 / 4.3 MiB)
                json:   6.64             --  4.97              =   5.81             -- ( 1.29 / 6.8 MiB)
          json_named:   7.58       10.37 --  5.20        1.55  =   6.39        5.96 -- ( 1.69 / 8.8 MiB)
              pretty:  10.37             --  5.80              =   8.09             -- ( 1.66 / 8.7 MiB)
         json_pretty:   9.93             --  6.87              =   8.40             -- ( 2.62 / 13.7 MiB)
              config:  11.17             --  6.39              =   8.78             -- ( 2.16 / 11.3 MiB)
```

#### Many Optional Fields:

```
                              READ                 WRITE                  SUM              SIZE
        name        :   pvd   thr   jck  --  pvd   thr   jck   =   pvd   thr   jck  -- (ratio / size)    
              binary:   0.98  1.00       --  0.80  1.00        =   0.89  1.00       -- ( 1.00 / 2.4 MiB)
         fast_binary:   1.35             --  1.31              =   1.33             -- ( 0.89 / 2.2 MiB)
      tuple_protocol:   1.65  1.05       --  1.41  0.71        =   1.53  0.88       -- ( 0.83 / 2.0 MiB)
     binary_protocol:   1.71             --  1.65              =   1.68             -- ( 1.00 / 2.4 MiB)
    compact_protocol:   2.07  1.32       --  1.60  0.93        =   1.83  1.12       -- ( 0.87 / 2.1 MiB)
                json:   5.54             --  5.12              =   5.33             -- ( 1.27 / 3.1 MiB)
          json_named:   6.32        9.10 --  5.24        1.35  =   5.78        5.23 -- ( 1.53 / 3.7 MiB)
         json_pretty:   7.73             --  6.33              =   7.03             -- ( 2.02 / 4.9 MiB)
              pretty:   9.68             --  5.55              =   7.62             -- ( 1.48 / 3.6 MiB)
              config:  10.22             --  6.00              =   8.11             -- ( 1.78 / 4.3 MiB)
```

#### Many Required Fields:

```
                              READ                 WRITE                  SUM              SIZE
        name        :   pvd   thr   jck  --  pvd   thr   jck   =   pvd   thr   jck  -- (ratio / size)    
              binary:   0.91  1.00       --  0.85  1.00        =   0.88  1.00       -- ( 1.00 / 2.4 MiB)
         fast_binary:   1.24             --  1.43              =   1.33             -- ( 0.89 / 2.2 MiB)
      tuple_protocol:   1.48  0.97       --  1.46  0.69        =   1.47  0.83       -- ( 0.83 / 2.0 MiB)
     binary_protocol:   1.62             --  1.82              =   1.72             -- ( 1.00 / 2.4 MiB)
    compact_protocol:   1.95  1.25       --  1.73  0.92        =   1.84  1.09       -- ( 0.87 / 2.1 MiB)
                json:   5.23             --  5.58              =   5.41             -- ( 1.27 / 3.1 MiB)
          json_named:   5.97        8.62 --  5.72        1.49  =   5.84        5.06 -- ( 1.53 / 3.7 MiB)
         json_pretty:   7.33             --  6.97              =   7.15             -- ( 2.03 / 4.9 MiB)
              pretty:   9.20             --  6.07              =   7.64             -- ( 1.49 / 3.6 MiB)
              config:   9.76             --  6.59              =   8.17             -- ( 1.78 / 4.3 MiB)
```

#### Deep Structure:

```
                              READ                 WRITE                  SUM              SIZE
        name        :   pvd   thr   jck  --  pvd   thr   jck   =   pvd   thr   jck  -- (ratio / size)    
              binary:   0.90  1.00       --  0.73  1.00        =   0.81  1.00       -- ( 1.00 / 7.4 MiB)
         fast_binary:   1.36             --  1.63              =   1.49             -- ( 0.82 / 6.0 MiB)
      tuple_protocol:   1.89  0.90       --  1.92  0.62        =   1.91  0.76       -- ( 0.75 / 5.6 MiB)
     binary_protocol:   2.08             --  2.40              =   2.24             -- ( 1.00 / 7.4 MiB)
    compact_protocol:   2.44  1.26       --  2.29  0.87        =   2.36  1.07       -- ( 0.81 / 6.0 MiB)
                json:   5.25             --  5.60              =   5.42             -- ( 1.22 / 9.0 MiB)
          json_named:   6.56        9.61 --  5.93        1.41  =   6.24        5.51 -- ( 1.74 / 12.8 MiB)
              pretty:  11.85             --  6.56              =   9.21             -- ( 1.71 / 12.6 MiB)
         json_pretty:  10.59             --  8.56              =   9.57             -- ( 3.40 / 25.1 MiB)
              config:  13.32             --  7.83              =  10.57             -- ( 2.64 / 19.5 MiB)
```

**NOTE:** The thrift JSON protocol was removed because some weird bug keeps messing
up deserialization and stopping the test. I will put it back once the bug is fixed or
can be bypassed.

**NOTE:** Since the test is for the *speed* of the serialization, we are only
interested in the comparison between the serializers, not the absolute values.
All numbers have been normalised to be relative to the native thrift binary protocol
implementation (that's why it's 1.00 for thrift binary).
