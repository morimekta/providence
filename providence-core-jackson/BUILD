java_library(
    name = "providence-core-jackson",
    srcs = glob(["java/**/*.java"]),
    deps = [
        "//providence-core",
        "//third-party:net_morimekta_utils_io_util",
        "//third-party:com_fasterxml_jackson_core_jackson_annotations",
        "//third-party:com_fasterxml_jackson_core_jackson_core",
        "//third-party:com_fasterxml_jackson_core_jackson_databind",
    ],
    visibility = ["//visibility:public"],
)

java_test(
    name = "javatests",
    srcs = glob(["javatests/**/*.java"]),
    deps = [
        ":providence-core-jackson",
        "//third-party:junit_junit",
        "//third-party:net_morimekta_utils_io_util",
        "//third-party:com_fasterxml_jackson_core_jackson_annotations",
        "//third-party:com_fasterxml_jackson_core_jackson_core",
        "//third-party:com_fasterxml_jackson_core_jackson_databind",
    ],
    size = "small",
)

test_suite(
    name = "tests",
    tests = [
        ":javatests",
    ],
)
