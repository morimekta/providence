java_library(
    name = "providence-model",
    srcs = glob(["model/**/*.java"]),
    deps = [
        "//providence-core",
    ],
)

java_library(
    name = "providence-reflect",
    srcs = glob(["java/**/*.java"]),
    deps = [
        ":providence-model",
        "//providence-core",
        "//third-party:net_morimekta_utils_io_util",
    ],
    exports = [
        ":providence-model",
    ],
    resources = glob([ "resources/**" ]),
    resource_strip_prefix = "providence-reflect/resources/",
    visibility = ["//visibility:public"],
)

java_test(
    name = "javatests",
    srcs = glob(["javatests/**/*.java"]),
    deps = [
        ":providence-model",
        ":providence-reflect",
        "//providence-core",
        "//third-party:junit_junit",
    ],
    resources = glob([ "resourcestest/**" ]),
    resource_strip_prefix = "providence-reflect/resourcestest/",
    size = "small",
)

test_suite(
    name = "tests",
    tests = [
        ":javatests",
    ],
)
