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
              binary:   1,04  1,00       --  0,81  1,00        =   0,93  1,00       -- ( 1,00 / 5,2 MiB)
         fast_binary:   1,38             --  1,43              =   1,40             -- ( 0,84 / 4,4 MiB)
      tuple_protocol:   1,88  1,06       --  1,63  0,76        =   1,75  0,91       -- ( 0,78 / 4,1 MiB)
     binary_protocol:   1,99             --  1,88              =   1,93             -- ( 1,00 / 5,2 MiB)
    compact_protocol:   2,30  1,31       --  1,85  0,95        =   2,07  1,13       -- ( 0,83 / 4,3 MiB)
                json:   5,89             --  5,13              =   5,51             -- ( 1,29 / 6,7 MiB)
          json_named:   6,81       10,96 --  5,37        1,61  =   6,09        6,28 -- ( 1,69 / 8,8 MiB)
         json_pretty:   9,14             --  7,02              =   8,08             -- ( 2,61 / 13,6 MiB)
              pretty:  12,08             --  5,89              =   8,98             -- ( 1,66 / 8,6 MiB)
              config:  13,34             --  6,51              =   9,93             -- ( 2,15 / 11,2 MiB)
```

#### Many Optional Fields:

```
                              READ                 WRITE                  SUM              SIZE
        name        :   pvd   thr   jck  --  pvd   thr   jck   =   pvd   thr   jck  -- (ratio / size)    
              binary:   0,90  1,00       --  0,87  1,00        =   0,89  1,00       -- ( 1,00 / 2,4 MiB)
         fast_binary:   1,26             --  1,42              =   1,34             -- ( 0,89 / 2,2 MiB)
      tuple_protocol:   1,65  0,97       --  1,54  0,77        =   1,60  0,87       -- ( 0,83 / 2,0 MiB)
     binary_protocol:   1,71             --  1,78              =   1,75             -- ( 1,00 / 2,4 MiB)
    compact_protocol:   2,00  1,24       --  1,73  1,01        =   1,87  1,12       -- ( 0,87 / 2,1 MiB)
                json:   5,44             --  5,44              =   5,44             -- ( 1,27 / 3,1 MiB)
          json_named:   6,32        9,19 --  5,67        1,41  =   6,00        5,30 -- ( 1,53 / 3,7 MiB)
         json_pretty:   7,44             --  6,75              =   7,10             -- ( 2,02 / 4,9 MiB)
              pretty:  10,26             --  5,96              =   8,11             -- ( 1,49 / 3,6 MiB)
              config:  11,07             --  6,37              =   8,72             -- ( 1,78 / 4,3 MiB)
```

#### Many Required Fields:

```
                              READ                 WRITE                  SUM              SIZE
        name        :   pvd   thr   jck  --  pvd   thr   jck   =   pvd   thr   jck  -- (ratio / size)    
              binary:   0,92  1,00       --  0,89  1,00        =   0,90  1,00       -- ( 1,00 / 2,4 MiB)
         fast_binary:   1,35             --  1,55              =   1,45             -- ( 0,89 / 2,2 MiB)
      tuple_protocol:   1,60  0,88       --  1,54  0,70        =   1,57  0,79       -- ( 0,83 / 2,0 MiB)
     binary_protocol:   1,77             --  1,93              =   1,85             -- ( 1,00 / 2,4 MiB)
    compact_protocol:   2,11  1,31       --  1,89  1,12        =   2,00  1,21       -- ( 0,87 / 2,1 MiB)
                json:   5,69             --  6,05              =   5,87             -- ( 1,27 / 3,1 MiB)
          json_named:   6,43        9,52 --  6,10        1,54  =   6,27        5,53 -- ( 1,53 / 3,7 MiB)
         json_pretty:   7,68             --  7,26              =   7,47             -- ( 2,02 / 4,9 MiB)
              pretty:  10,64             --  6,49              =   8,57             -- ( 1,48 / 3,6 MiB)
              config:  11,51             --  7,04              =   9,27             -- ( 1,78 / 4,4 MiB)
```

#### Deep Structure:

```
                              READ                 WRITE                  SUM              SIZE
        name        :   pvd   thr   jck  --  pvd   thr   jck   =   pvd   thr   jck  -- (ratio / size)    
              binary:   0,91  1,00       --  0,72  1,00        =   0,81  1,00       -- ( 1,00 / 7,4 MiB)
         fast_binary:   1,39             --  1,61              =   1,50             -- ( 0,82 / 6,0 MiB)
      tuple_protocol:   1,96  0,90       --  1,95  0,61        =   1,96  0,75       -- ( 0,75 / 5,6 MiB)
     binary_protocol:   2,24             --  2,44              =   2,34             -- ( 1,00 / 7,4 MiB)
    compact_protocol:   2,56  1,22       --  2,33  0,89        =   2,45  1,05       -- ( 0,81 / 6,0 MiB)
                json:   5,76             --  5,36              =   5,56             -- ( 1,22 / 9,0 MiB)
          json_named:   7,14       11,85 --  5,66        1,31  =   6,40        6,58 -- ( 1,73 / 12,8 MiB)
         json_pretty:  11,39             --  8,22              =   9,80             -- ( 3,40 / 25,1 MiB)
              pretty:  13,80             --  6,33              =  10,07             -- ( 1,71 / 12,6 MiB)
              config:  16,05             --  7,60              =  11,82             -- ( 2,64 / 19,5 MiB)
```

**NOTE:** The thrift JSON protocol was removed because some weird bug keeps messing
up deserialization and stopping the test. I will put it back once the bug is fixed or
can be bypassed.

**NOTE:** Since the test is for the *speed* of the serialization, we are only
interested in the comparison between the serializers, not the absolute values.
All numbers have been normalised to be relative to the native thrift binary protocol
implementation (that's why it's 1.00 for thrift binary).
