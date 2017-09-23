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
java -jar /home/morimekta/src/providence/it-serialization/target/it-serialization.jar --runs 50 --generate 1000
```

#### Many containers:

```
                              READ                 WRITE                  SUM              SIZE
        name        :   pvd   thr   jck  --  pvd   thr   jck   =   pvd   thr   jck  -- (ratio / size)    
              binary:   1.05  1.00       --  0.79  1.00        =   0.92  1.00       -- ( 1.00 / 5.2 MiB)
         fast_binary:   1.38             --  1.39              =   1.39             -- ( 0.84 / 4.4 MiB)
      tuple_protocol:   1.99  1.06       --  1.54  0.78        =   1.77  0.92       -- ( 0.78 / 4.1 MiB)
     binary_protocol:   2.05             --  1.98              =   2.02             -- ( 1.00 / 5.2 MiB)
    compact_protocol:   2.43  1.31       --  1.90  0.92        =   2.17  1.12       -- ( 0.83 / 4.3 MiB)
       json_protocol:   8.72  7.19       --  6.59  5.34        =   7.66  6.26       -- ( 1.76 / 9.1 MiB)
                json:   9.69             --  6.95              =   8.32             -- ( 1.30 / 6.7 MiB)
          json_named:  11.21        8.33 --  7.44        1.65  =   9.32        4.99 -- ( 1.69 / 8.8 MiB)
              pretty:  12.77             --  7.65              =  10.21             -- ( 1.66 / 8.6 MiB)
              config:  13.28             --  8.44              =  10.86             -- ( 2.16 / 11.2 MiB)
         json_pretty:  16.17             --  9.38              =  12.77             -- ( 2.62 / 13.6 MiB)
```

#### Many Optional Fields:

```
                              READ                 WRITE                  SUM              SIZE
        name        :   pvd   thr   jck  --  pvd   thr   jck   =   pvd   thr   jck  -- (ratio / size)    
              binary:   1.03  1.00       --  0.91  1.00        =   0.97  1.00       -- ( 1.00 / 2.4 MiB)
         fast_binary:   1.59             --  1.56              =   1.57             -- ( 0.89 / 2.1 MiB)
      tuple_protocol:   2.23  1.11       --  1.84  0.94        =   2.03  1.02       -- ( 0.83 / 2.0 MiB)
     binary_protocol:   2.42             --  2.73              =   2.57             -- ( 1.00 / 2.4 MiB)
    compact_protocol:   2.85  1.31       --  2.52  0.88        =   2.68  1.10       -- ( 0.87 / 2.1 MiB)
       json_protocol:   9.27  7.32       --  8.06  5.44        =   8.67  6.38       -- ( 1.66 / 4.0 MiB)
                json:   9.94             --  8.32              =   9.13             -- ( 1.27 / 3.1 MiB)
          json_named:  11.07        6.43 --  8.66        1.57  =   9.86        4.00 -- ( 1.54 / 3.7 MiB)
              pretty:  13.23             --  8.75              =  10.99             -- ( 1.49 / 3.6 MiB)
              config:  13.53             --  9.42              =  11.48             -- ( 1.78 / 4.3 MiB)
         json_pretty:  14.57             -- 10.25              =  12.41             -- ( 2.03 / 4.9 MiB)
```

#### Many Required Fields:

```
                              READ                 WRITE                  SUM              SIZE
        name        :   pvd   thr   jck  --  pvd   thr   jck   =   pvd   thr   jck  -- (ratio / size)    
              binary:   1.00  1.00       --  0.87  1.00        =   0.93  1.00       -- ( 1.00 / 2.4 MiB)
         fast_binary:   1.51             --  1.60              =   1.55             -- ( 0.89 / 2.2 MiB)
      tuple_protocol:   2.02  1.02       --  1.60  0.77        =   1.81  0.89       -- ( 0.83 / 2.0 MiB)
     binary_protocol:   2.35             --  2.73              =   2.54             -- ( 1.00 / 2.4 MiB)
    compact_protocol:   2.85  1.31       --  2.62  0.93        =   2.73  1.12       -- ( 0.87 / 2.1 MiB)
       json_protocol:   9.02  7.19       --  8.03  5.53        =   8.53  6.36       -- ( 1.65 / 4.0 MiB)
                json:   9.72             --  8.68              =   9.20             -- ( 1.27 / 3.1 MiB)
          json_named:  10.87        6.29 --  9.03        1.56  =   9.95        3.92 -- ( 1.53 / 3.7 MiB)
              pretty:  12.68             --  9.15              =  10.92             -- ( 1.49 / 3.6 MiB)
              config:  13.02             --  9.80              =  11.41             -- ( 1.78 / 4.3 MiB)
         json_pretty:  14.30             -- 10.60              =  12.45             -- ( 2.02 / 4.9 MiB)
```

#### Deep Structure:

```
                              READ                 WRITE                  SUM              SIZE
        name        :   pvd   thr   jck  --  pvd   thr   jck   =   pvd   thr   jck  -- (ratio / size)    
              binary:   0.85  1.00       --  0.67  1.00        =   0.76  1.00       -- ( 1.00 / 7.3 MiB)
         fast_binary:   1.34             --  1.59              =   1.47             -- ( 0.82 / 6.0 MiB)
      tuple_protocol:   2.14  0.85       --  1.90  0.60        =   2.02  0.72       -- ( 0.75 / 5.5 MiB)
     binary_protocol:   2.50             --  2.90              =   2.70             -- ( 1.00 / 7.3 MiB)
    compact_protocol:   2.87  1.29       --  2.69  0.88        =   2.78  1.08       -- ( 0.81 / 6.0 MiB)
                json:   9.28             --  7.24              =   8.26             -- ( 1.22 / 9.0 MiB)
       json_protocol:   9.44  7.19       --  8.36  5.39        =   8.90  6.29       -- ( 1.80 / 13.2 MiB)
          json_named:  11.16        8.58 --  7.76        1.38  =   9.46        4.98 -- ( 1.74 / 12.8 MiB)
              pretty:  13.78             --  8.05              =  10.91             -- ( 1.72 / 12.6 MiB)
              config:  14.58             --  9.48              =  12.03             -- ( 2.65 / 19.4 MiB)
         json_pretty:  18.95             -- 10.80              =  14.87             -- ( 3.41 / 25.0 MiB)
```

**NOTE:** Since the test is for the *speed* of the serialization, we are only
interested in the comparison between the serializers, not the absolute values.
All numbers have been normalised to be relative to the native thrift binary protocol
implementation (that's why it's 1.00 for thrift binary).
