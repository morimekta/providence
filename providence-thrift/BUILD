java_library(
    name = "providence-thrift",
    srcs = glob(["java/**/*.java"]),
    deps = [
        "//providence-core",
        "//third-party:org_apache_thrift_libthrift",
    ],
    visibility = ['//visibility:public'],
)

java_test(
    name = "javatests",
    srcs = glob(["javatests/**/*.java"]),
    deps = [
        ":providence-thrift",
        "//providence-testing",
        "//providence-internal:providence-idl",
        "//providence-internal:thrift-idl",
        "//providence-core",
        "//third-party:org_apache_thrift_libthrift",
        "//third-party:org_slf4j_slf4j_simple",
        "//third-party:org_mockito_mockito_core",
        "//third-party:junit_junit",
    ],
    resources = glob(["resourcestest/**"]),
    resource_strip_prefix = "providence-thrift/resourcestest/",
    size = 'small',
)

test_suite(
    name = "tests",
    tests = [
        ":javatests",
    ]
)
