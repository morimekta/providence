java_library(
    name = "providence-core",
    srcs = glob(["java/**/*.java"]),
    deps = [
        "//third-party:net_morimekta_utils_io_util",
    ],
    visibility = ["//visibility:public"],
)

java_test(
    name = "javatests",
    srcs = glob(["javatests/**/*.java"]),
    deps = [
        ":providence-core",
        "//third-party:net_morimekta_utils_io_util",
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
