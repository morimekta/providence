load('/thrift', 'gen_thrift')

java_library(
  name = "core",
  srcs = glob(["src/**/*.java"]),
  visibility = ['//visibility:public'],
)

gen_thrift(
  name = 'generated-thrift',
  gen = 'java2',
  flags = ['--android'],
  srcs = glob(['res/definitions/*.thrift']),
)

java_library(
  name = "generated",
  srcs = [':generated-thrift'],
  deps = [
    ":core",
    "//third-party:android",
  ],
  visibility = ['//visibility:public'],
)

java_test(
  name = "test",
  srcs = glob(["test/**/*.java"]),
  deps = [
    "//third-party:junit",
    ":core",
    ":generated",
  ],
)
