java_library(
    name = "compiler",
    srcs = glob(
        ["**/*.java"],
        exclude = ['Compiler.java']),
    deps = [
        "//providence-core",
        "//providence-reflect",
        "//third-party:console-util",
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
