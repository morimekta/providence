java_library(
    name = "compiler",
    srcs = glob(
        ["**/*.java"],
        exclude = ['Compiler.java']),
    deps = [
        "//providence-core",
        "//providence-reflect",
        "//providence-tools-generator",
        "//third-party:net_morimekta_utils_console_util",
        "//third-party:args4j_args4j",
    ],
    visibility = ['//visibility:public'],
)

java_library(
    name = "compiler-main",
    srcs = ["Compiler.java"],
    deps = [
        ":compiler",
        "//providence-core",
        "//providence-reflect",
        "//providence-tools-generator",
        "//third-party:args4j_args4j",
        "//third-party:net_morimekta_utils_io_util",
    ],
    visibility = ['//visibility:public'],
)
