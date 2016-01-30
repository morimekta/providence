load("/providence", "java_providence")
load("/thrift", "java_thrift")

java_providence(
  name = "providence-idl",
  srcs = glob([ "defs/*/*.thrift" ]),
  options = [ "--android", "--jackson", "--containers=SORTED" ],
  visibility = [ "//visibility:public" ],
)

# Make sure that this uses the same structure, but in a non-conflicting namespace.
genrule(
    name = "thrift-idl_thrift",
    cmd = "cat $(SRCS) | sed s/providence/thrift/ > $(OUTS)",
    srcs = [ "defs/tests/test.thrift" ],
    outs = [ "thrift-idl.thrift" ],
)

java_thrift(
    name = "thrift-idl",
    srcs = [
        ":thrift-idl.thrift",
    ],
    visibility = [ "//visibility:public" ],
)

filegroup(
   name = "test-data",
   srcs = glob([ "resources/**" ]),
   visibility = [ "//visibility:public" ],
)

java_library(
    name = "testing",
    srcs = glob(["java/**/*.java"]),
    deps = [
        "//core",
        "//core-streams",
        "//third-party:hamcrest-core",
        "//third-party:junit",
    ],
    testonly = 1,
    visibility = ["//visibility:public"],
)

java_test(
    name = "javatests",
    srcs = glob(["javatests/**/*.java"]),
    deps = [
        ":providence-idl",
        ":testing",
        "//core",
        "//core-jackson",
        "//core-streams",
        "//third-party:android-util",
        "//third-party:jackson-annotations",
        "//third-party:jackson-core",
        "//third-party:jackson-databind",
        "//third-party:hamcrest-core",
        "//third-party:mockito-core",
        "//third-party:junit",
    ],
    resources = glob(["resourcestest/**"]),
    resource_strip_prefix = "testing/resourcestest/",
    size = 'small',
)

test_suite(
    name = "tests",
    tests = [
        ":javatests",
    ]
)
