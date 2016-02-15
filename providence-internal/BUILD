load("/tools/build_defs/pvd/providence", "java_providence")
load("/tools/build_defs/pvd/thrift", "java_thrift")

java_providence(
    name = "providence-idl",
    srcs = glob([
        "resources/*/*.thrift",
    ]),
    options = [
        "--android",
        "--jackson",
        "--containers=SORTED",
    ],
    visibility = ["//visibility:public"],
)

java_thrift(
    name = "thrift-idl",
    srcs = [
        "resources/test-thrift.thrift",
    ],
    visibility = ["//visibility:public"],
)

java_library(
    name = "idl",
    resources = glob(["resources/**"]),
    resource_strip_prefix = "providence-internal/resources/",
)
