java_test(
    name = "javatests",
    srcs = glob(["javatests/**/*.java"]),
    deps = [
        "//third-party:android-util",
        "//third-party:jackson-annotations",
        "//third-party:jackson-core",
        "//third-party:jackson-databind",
        "//third-party:junit",
        "//core:core",
        "//core:providence-idl-with-deps"
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
    name = "generate-data",
    srcs = glob(
         ["java/net/morimekta/generator/*.java"],
    ),
    main_class = 'net.morimekta.generator.GenerateData',
    deps = [
        "//core:providence-idl",
        "//core:core",
        "//thrift",
        "//utils",
        "//third-party:args4j",
        "//third-party:slf4j-simple",
        "//third-party:libthrift",
    ]
)

java_binary(
    name = "speed-test",
    srcs = glob(
        ["java/net/morimekta/speedtest/*.java"],
    ),
    main_class = 'net.morimekta.speedtest.SpeedTest',
    deps = [
        "//core:thrift-idl",
        "//core:providence-idl",
        "//core",
        "//thrift",
        "//utils",
        "//third-party:args4j",
        "//third-party:commons-math3",
        "//third-party:libthrift",
        "//third-party:slf4j-simple",
    ]
)
