java_library(
    name = "thrift",
    srcs = glob(["java/**/*.java"]),
    deps = [
        "//core:core",
        "//third-party:libthrift",
    ],
    visibility = ['//visibility:public'],
)


java_test(
    name = "javatests",
    srcs = glob(["javatests/**/*.java"]),
    deps = [
        ":thrift",
        "//testing",
        "//testing:providence-idl",
        "//testing:thrift-idl",
        "//core",
        "//third-party:libthrift",
        "//third-party:slf4j-simple",
        "//third-party:mockito-core",
        "//third-party:junit",
    ],
    resources = glob(["resourcestest/**"]),
    resource_strip_prefix = "thrift/resourcestest/",
    size = 'small',
)

test_suite(
    name = "tests",
    tests = [
        ":javatests",
    ]
)
