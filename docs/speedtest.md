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
          json_named:  259 867 kB in  6,39s
                json:  228 742 kB in  4,77s
       json_protocol:  253 320 kB in  4,19s
              binary:  175 170 kB in  2,68s
    compact_protocol:  168 476 kB in  1,75s
     binary_protocol:  178 944 kB in  1,47s
      tuple_protocol:  166 064 kB in  1,19s
```

# Results 2015-12-27

`# bazel run //tests:speed-test -- --entries 10000 ${PWD}/tests/generated`

```
OUT: /tmp/thrift-j2-3440926060240928395-speed-test

 --- thrift ---

          json_named: [skipped]
                json: [skipped]
       json_protocol: 12,05s  (r:  7,51s, w:  4,51s) # 253 330 kB -> 253 330 kB
              binary: [skipped]
    compact_protocol:  3,18s  (r:  1,63s, w:  1,52s) # 168 486 kB -> 168 486 kB
     binary_protocol:  1,85s  (r:  0,79s, w:  0,83s) # 178 953 kB -> 178 953 kB
      tuple_protocol:  2,00s  (r:  1,08s, w:  0,90s) # 166 074 kB -> 166 074 kB

 --- thrift-j2 ---

          json_named: 18,40s  (r: 14,73s, w:  3,67s) # 259 877 kB -> 259 877 kB
                json: 16,78s  (r: 13,37s, w:  3,41s) # 228 752 kB -> 228 752 kB
       json_protocol: 10,48s  (r:  6,67s, w:  3,81s) # 253 330 kB -> 253 330 kB
              binary:  1,86s  (r:  1,02s, w:  0,83s) # 175 180 kB -> 175 180 kB
    compact_protocol:  2,96s  (r:  1,34s, w:  1,62s) # 168 486 kB -> 168 486 kB
     binary_protocol:  1,99s  (r:  1,22s, w:  0,77s) # 178 953 kB -> 178 953 kB
      tuple_protocol:  1,81s  (r:  1,20s, w:  0,61s) # 166 074 kB -> 166 074 kB
```
