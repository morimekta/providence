java_test(
    name = "javatests",
    srcs = glob(["javatests/**/*.java"]),
    deps = [
        "//providence-core",
        "//providence-reflect",
        "//providence-testing",
        "//providence-tools-generator",
        "//providence-tools/java/net/morimekta/providence/compiler:compiler",
        "//providence-tools/java/net/morimekta/providence/converter:converter-main",
        "//providence-tools/java/net/morimekta/providence/tools/data",
        "//third-party:junit_junit",
        "//third-party:net_morimekta_utils_console_util",
    ],
    data = [
        ":speed-test",
        ":data-generator",
        ":providence-compiler",
        ":providence-converter",
    ],
    resources = glob([ "resourcestest/**" ]),
    resource_strip_prefix = "providence-tools/resourcestest/",
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
        "//providence-tools/java/net/morimekta/providence/compiler:compiler-main",
    ],
    main_class = "net.morimekta.providence.compiler.Compiler",
    visibility = ["//visibility:public"],
)

java_binary(
    name = "providence-converter",
    runtime_deps = [
        "//providence-tools/java/net/morimekta/providence/converter:converter-main",
    ],
    main_class = "net.morimekta.providence.converter.Convert",
    visibility = ['//visibility:public'],
)

java_binary(
    name = "data-generator",
    runtime_deps = [
        "//providence-tools/java/net/morimekta/providence/tools/generator:data-generator-main",
    ],
    main_class = 'net.morimekta.providence.tools.generator.DataGenerator',
)

java_binary(
    name = "speed-test",
    runtime_deps = [
        "//providence-tools/java/net/morimekta/providence/tools/speedtest:speed-test-main",
    ],
    main_class = 'net.morimekta.providence.tools.speedtest.SpeedTest',
)
