load("/providence", "java_providence")
load("/thrift", "java_thrift")

java_library(
  name = "core",
  srcs = glob(["java/**/*.java"]),
  visibility = ["//visibility:public"],
)

java_providence(
  name = "providence-idl",
  srcs = glob([ "resources/definitions/**/*.thrift" ]),
  visibility = [ "//visibility:public" ],
)

java_providence(
  name = "providence-idl-with-deps",
  srcs = glob([ "resources/definitions/**/*.thrift" ]),
  options = [ "--android", "--jackson" ],
  visibility = [ "//visibility:public" ],
)

# Make sure that this uses the same structure, but in a non-conflicting namespace.
genrule(
    name = "thrift-idl_thrift",
    cmd = "cat $(SRCS) | sed s/providence/thrift/ > $(OUTS)",
    srcs = [ "resources/definitions/tests/test.thrift" ],
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

java_test(
    name = "javatests",
    srcs = glob(["javatests/**/*.java"]),
    deps = [
        ":core",
        ":providence-idl",
        "//third-party:junit",
    ],
    resources = [
        ":test-data",
    ],
    resource_strip_prefix = "core/resources",
    size = "small",
)

test_suite(
    name = "tests",
    tests = [
        ":javatests",
    ]
)
