java_library(
    name = "compiler",
    srcs = glob(
        ["**/*.java"],
        exclude = ['Compiler.java']),
    deps = [
        "//providence-core",
        "//providence-reflect",
        "//providence-reflect:providence-model",
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
        "//third-party:args4j_args4j",
    ],
    visibility = ['//visibility:public'],
)
