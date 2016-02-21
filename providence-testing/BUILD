java_library(
    name = "providence-testing",
    srcs = glob(["java/**/*.java"]),
    deps = [
        "//providence-core",
        "//providence-core-streams",
        "//third-party:junit_junit",
        "//third-party:net_morimekta_utils_io_util",
        "//third-party:org_hamcrest_hamcrest_core",
    ],
    visibility = ['//visibility:public'],
)

java_test(
    name = "javatests",
    srcs = glob(["javatests/**/*.java"]),
    deps = [
        ":providence-testing",
        "//providence-internal:providence-idl",
        "//providence-core",
        "//providence-core-jackson",
        "//providence-core-streams",
        "//third-party:net_morimekta_utils_android_util",
        "//third-party:net_morimekta_utils_io_util",
        "//third-party:com_fasterxml_jackson_core_jackson_annotations",
        "//third-party:com_fasterxml_jackson_core_jackson_core",
        "//third-party:com_fasterxml_jackson_core_jackson_databind",
        "//third-party:org_hamcrest_hamcrest_core",
        "//third-party:org_mockito_mockito_core",
        "//third-party:junit_junit",
    ],
    resources = glob(["resourcestest/**"]),
    resource_strip_prefix = "providence-testing/resourcestest/",
    size = 'small',
)

test_suite(
    name = "tests",
    tests = [
        ":javatests",
    ]
)
