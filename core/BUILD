java_library(
  name = "core",
  srcs = glob(["src/**/*.java"]),
  visibility = ['//visibility:public'],
)

java_library(
  name = "generated",
  srcs = glob(["generated/**/*.java"]),
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
