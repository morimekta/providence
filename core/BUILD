load('/thrift', 'java_thrift')

java_library(
  name = "core",
  srcs = glob(["src/**/*.java"]),
  visibility = ['//visibility:public'],
)

java_thrift(
  name = 'generated',
  srcs = glob(['res/definitions/*.thrift']),
  # flags = ['--android'],
  visibility = ['//visibility:public'],
)

java_test(
  name = "test",
  srcs = glob(["test/**/*.java"]),
  deps = [
    "//external:android-util",
    "//third-party:junit",
    ":core",
    ":generated",
  ],
  size = 'small',
)
