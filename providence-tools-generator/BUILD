java_library(
    name = "providence-tools-generator",
    srcs = glob(["java/**/*.java"]),
    deps = [
        "//providence-core",
        "//providence-reflect",
        "//third-party:args4j_args4j",
        "//third-party:net_morimekta_utils_io_util",
    ],
    visibility = ["//visibility:public"],
)

java_test(
    name = "javatests",
    srcs = glob(["javatests/**/*.java"]),
    deps = [
        "//providence-core",
        "//providence-reflect",
        "//providence-testing",
        "//third-party:junit_junit",
        "//third-party:org_mockito_mockito_core",
    ],
    resources = glob([ "resourcestest/**" ]),
    resource_strip_prefix = "providence-tools-generator/resourcestest/",
    size = 'small',
)

test_suite(
    name = "tests",
    tests = [
        ":javatests",
    ]
)
