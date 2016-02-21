Testing the efficiency of thrift-j2
===================================

In order to be sure that I have not just made a rabble of serialization code,
there is a testing module with a speed-test.

# Generate test data

Run one of the following data generation commands:

`bazel run //tests:generate-data -- --entries 10000 --out ${PWD}/generated/resources`

`bazel run //tests:generate-data -- --entries 20000 --items_min 5 --items_max=15 --fill_grade 0.5 --out ${PWD}/generated/resources`

And you should see output like this.
```
         json_pretty:  324,907 kB in  8.27s
                json:  225,523 kB in  7.27s
              binary:  169,322 kB in  1.85s
     binary_protocol:  173,091 kB in  1.54s
```

# Results 2016-01-07

`# bazel run //tests:speed-test -- --entries 10000 ${PWD}/generated/resources`

```
OUT: /tmp/thrift-j2-3440926060240928395-speed-test

[providence]  1:         json_pretty in  17.66s (r: 10.54s, w:  7.12s)
[providence]  1:          json_named in  16.22s (r: 10.01s, w:  6.21s)
[providence]  1:                json in  14.98s (r:  9.20s, w:  5.78s)
[providence]  1:       json_protocol in  10.79s (r:  6.72s, w:  4.07s)
[thrift]      1:       json_protocol in  11.76s (r:  7.38s, w:  4.38s)
[providence]  1:              binary in   2.66s (r:  1.25s, w:  1.41s)
[providence]  1:    compact_protocol in   2.90s (r:  1.59s, w:  1.31s)
[thrift]      1:    compact_protocol in   2.43s (r:  1.20s, w:  1.24s)
[providence]  1:     binary_protocol in   2.25s (r:  1.36s, w:  0.88s)
[thrift]      1:     binary_protocol in   1.95s (r:  0.73s, w:  1.21s)
[providence]  1:      tuple_protocol in   2.16s (r:  1.40s, w:  0.75s)
[thrift]      1:      tuple_protocol in   1.62s (r:  0.88s, w:  0.74s)
```
