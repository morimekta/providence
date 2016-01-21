java_library(
  name = "core",
  srcs = glob(["java/**/*.java"]),
  visibility = ["//visibility:public"],
)

java_test(
    name = "javatests",
    srcs = glob(["javatests/**/*.java"]),
    deps = [
        ":core",
        "//third-party:junit",
    ],
    size = "small",
)

test_suite(
    name = "tests",
    tests = [
        ":javatests",
    ]
)
