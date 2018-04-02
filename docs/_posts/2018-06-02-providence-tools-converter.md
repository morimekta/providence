---
layout: page
toc_title: "Data Converter"
title: "Providence CLI Tool : Data Converter"
category: cli
date: 2018-06-02 12:00:00
order: 2
---

The data converter `pvd` is a small program that can read a thrift IDL, and a
binary (or non-binary) serialized data file that follows that IDL structure,
and the output the result in some form of readable (or other binary) data
format. As for example:

Given the thrift IDL in `thrift/test.thrift`:

```thrift
struct MyData {
  1: string text
  2: i32 sequence
  3: list<string> tags
}
```

And a binary file with a set of data entries in test.MyData format. We could
do something like this:

```sh
cat test.data | pvd -I thrift/ test.MyData
```

And should make the output:

```json
{
  "text": "not a test at all",
  "sequence": 144
}
{
  "text": "test 2",
  "tags": [
    "first",
    "second"
  ]
}
```

Input and output can be specified to point directly to files (no shell
piping needed), and the input and output serialization format can be
specified too.

```sh
pvd -i fast_binary,file:test.data -o pretty -I thrift/ test.MyData
```

Which should read the data file serialized with the FastBinarySerializer format
and print it out with the simple "pretty printer" format.
