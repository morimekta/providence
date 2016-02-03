licenses(["notice"])  # Apache 2.0

filegroup(
    name = "srcs",
    srcs = glob(["**"]),
    visibility = ["//tools:__pkg__"],
)

# Used by pkg_deb
py_library(
    name = "archive",
    srcs = ["archive.py"],
    visibility = [
        "//tools/build_defs/docker:__subpackages__",
    ],
)

filegroup(
    name = "archive_testdata",
    srcs = glob(["testdata/**"]),
)

py_test(
    name = "archive_test",
    srcs = [
        "archive_test.py",
        "testenv.py",
    ],
    data = [":archive_testdata"],
    deps = [":archive"],
)

py_binary(
    name = "build_tar",
    srcs = ["build_tar.py"],
    visibility = ["//visibility:public"],
    deps = [
        "@bazel_tools//third_party/py/gflags",
        ":archive",
    ],
)

py_binary(
    name = "make_deb",
    srcs = ["make_deb.py"],
    visibility = ["//visibility:public"],
    deps = [
        "@bazel_tools//third_party/py/gflags",
        ":archive",
    ],
)
