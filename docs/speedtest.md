Testing the efficiency of thrift-j2
===================================

In order to be sure that I have not just made a rabble of serialization code,
there is a testing module with a speed-test.

# Generate test data

Run one of the following data generation commands:

`bazel run //tests:generate-data -- --entries 10000 --out ${PWD}/tests/generated/`

`bazel run //tests:generate-data -- --entries 20000 --items_min 5 --items_max=15 --fill_grade 0.5 --out ${PWD}/tests/generated/`

And you should see output like this.
```
         json_pretty:  224,943 kB in  6.93s
          json_named:  177,677 kB in  5.31s
                json:  160,877 kB in  5.05s
       json_protocol:  174,968 kB in  3.67s
              binary:  121,022 kB in  1.43s
    compact_protocol:  116,322 kB in  1.42s
     binary_protocol:  123,306 kB in  1.31s
      tuple_protocol:  115,161 kB in  1.30s
```

# Results 2015-12-27

`# bazel run //tests:speed-test -- --entries 10000 ${PWD}/tests/generated`

```
OUT: /tmp/thrift-j2-3440926060240928395-speed-test

 --- thrift ---

         json_pretty: [skipped]
          json_named: [skipped]
                json: [skipped]
       json_protocol: 11.58s  (r:  6.98s, w:  4.29s)  #  174,988 kB -> 186,596 kB
              binary: [skipped]
    compact_protocol:  3.14s  (r:  1.69s, w:  1.40s)  #  116,341 kB -> 118,827 kB
     binary_protocol:  2.07s  (r:  0.89s, w:  1.10s)  #  123,326 kB -> 129,128 kB
      tuple_protocol:  1.76s  (r:  0.86s, w:  0.80s)  #  115,180 kB -> 115,180 kB

 --- thrift-j2 ---

         json_pretty: 13.07s  (r:  7.70s, w:  5.37s)  #  224,963 kB -> 224,963 kB
          json_named: 12.42s  (r:  7.34s, w:  5.08s)  #  177,697 kB -> 177,697 kB
                json: 11.46s  (r:  6.86s, w:  4.60s)  #  160,896 kB -> 160,896 kB
       json_protocol:  8.34s  (r:  5.28s, w:  3.06s)  #  174,988 kB -> 174,988 kB
              binary:  2.41s  (r:  1.17s, w:  1.24s)  #  121,041 kB -> 121,041 kB
    compact_protocol:  2.42s  (r:  1.50s, w:  0.92s)  #  116,341 kB -> 116,341 kB
     binary_protocol:  1.87s  (r:  1.17s, w:  0.71s)  #  123,326 kB -> 123,326 kB
      tuple_protocol:  2.07s  (r:  1.35s, w:  0.72s)  #  115,180 kB -> 115,180 kB
```
