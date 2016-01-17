filegroup(
  name = 'failure-thrift',
  srcs = glob(['res/failure/*.thrift']),
)

java_library(
  name = "reflect",
  srcs = glob([
    "java/**/*.java",
    "model/**/*.java",
  ]),
  deps = [
    "//core:core",
  ],
  visibility = ['//visibility:public'],
)

java_test(
  name = "javatests",
  size = "small",
  srcs = glob(["javatests/**/*.java"]),
  deps = [
    ":reflect",
    "//core:core",
    "//third-party:junit",
  ],
)

test_suite(
    name = "tests",
    tests = [
        ":javatests",
    ]
)
