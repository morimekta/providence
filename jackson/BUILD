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
