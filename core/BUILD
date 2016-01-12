load('/thrift', 'java_thrift')

java_library(
  name = "core",
  srcs = glob(["java/**/*.java"]),
  visibility = ['//visibility:public'],
)

java_thrift(
  name = 'generated',
  srcs = glob(['res/definitions/*.thrift']),
  options = ['--android', '--jackson'],
  visibility = ['//visibility:public'],
)

java_test(
  name = "test",
  srcs = glob(["javatests/**/*.java"]),
  deps = [
    "//third-party:junit",
    ":core",
    ":generated",
  ],
  size = 'small',
)
