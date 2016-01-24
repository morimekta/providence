java_library(
    name = "compiler",
    srcs = glob(
        ["**/*.java"],
        exclude = ['Compiler.java']),
    deps = [
        "//core:core",
        "//reflect:reflect",
        "//tools/java/net/morimekta/console",
        "//third-party:args4j",
    ],
    visibility = ['//visibility:public'],
)

java_library(
    name = "compiler-main",
    srcs = ["Compiler.java"],
    deps = [
        ":compiler",
        "//core:core",
        "//reflect:reflect",
        "//third-party:args4j",
    ],
    visibility = ['//visibility:public'],
)
