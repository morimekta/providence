load('/thrift', 'java_thrift')

java_library(
  name = "jackson",
  srcs = glob(["java/**/*.java"]),
  visibility = ['//visibility:public'],
  deps = [
    "//core:core",
    "//third-party:jackson-databind",
  ],
)

java_test(
  name = "test",
  srcs = glob(["javatests/**/*.java"]),
  deps = [
    "//third-party:jackson-annotations",
    "//third-party:jackson-core",
    "//third-party:jackson-databind",
    "//third-party:junit",
    "//core:core",
    "//core:generated",
  ],
  size = 'small',
)
