java_test(
    name = "javatests",
    srcs = glob(["javatests/**/*.java"]),
    deps = [
        "//core",
        "//reflect:reflect",
        "//testing",
        "//testing:providence-idl",
        "//tools/java/net/morimekta/console",
        "//tools/java/net/morimekta/providence/compiler:compiler",
        "//tools/java/net/morimekta/providence/converter:converter-main",
        "//tools/java/net/morimekta/providence/tools/data",
        "//third-party:junit",
    ],
    data = [
        ":speed-test",
        ":data-generator",
        ":providence-compiler",
        ":providence-converter",
    ],
    resources = glob([ "resourcestest/**" ]),
    resource_strip_prefix = "testing/resourcestest/",
    # Ensures the binaries compile properly.
    size = 'small',
)

test_suite(
    name = "tests",
    tests = [
        ":javatests",
    ]
)

java_binary(
    name = "providence-compiler",
    runtime_deps = [
        "//tools/java/net/morimekta/providence/compiler:compiler-main",
    ],
    main_class = "net.morimekta.providence.compiler.Compiler",
    visibility = ["//visibility:public"],
)

java_binary(
    name = "providence-converter",
    runtime_deps = [
        "//tools/java/net/morimekta/providence/converter:converter-main",
    ],
    main_class = "net.morimekta.providence.converter.Convert",
    visibility = ['//visibility:public'],
)

java_binary(
    name = "data-generator",
    runtime_deps = [
        "//tools/java/net/morimekta/providence/tools/generator:data-generator-main",
    ],
    main_class = 'net.morimekta.providence.tools.generator.DataGenerator',
)

java_binary(
    name = "speed-test",
    runtime_deps = [
        "//tools/java/net/morimekta/providence/tools/speedtest:speed-test-main",
    ],
    main_class = 'net.morimekta.providence.tools.speedtest.SpeedTest',
)
