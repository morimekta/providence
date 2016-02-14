java_library(
    name = "providence-core-streams",
    srcs = glob(["java/**/*.java"]),
    deps = [
        "//providence-core",
    ],
    visibility = ["//visibility:public"],
)

java_test(
    name = "javatests",
    srcs = glob(["javatests/**/*.java"]),
    deps = [
        ":providence-core-streams",
        "//providence-core",
        "//third-party:junit_junit",
    ],
    size = "small",
)

test_suite(
    name = "tests",
    tests = [
        ":javatests",
    ],
)
