load('/thrift', 'gen_thrift')

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
  name = "test",
  size = "small",
  srcs = glob(["javatests/**/*.java"]),
  deps = [
    ":reflect",
    "//core:core",
    "//third-party:junit",
  ],
)
