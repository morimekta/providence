java_library(
    name = "tools",
    srcs = glob(
        ["java/**/*.java"],
        exclude = [
            "java/**/generator/*.java",
            "java/**/speedtest/*.java",
        ]
    ),
    deps = [
        "//core",
        "//testing:providence-idl",
        "//third-party:commons-math3",
        "//third-party:hamcrest-core",
        "//third-party:junit",
    ],
)

java_test(
    name = "javatests",
    srcs = glob(["javatests/**/*.java"]),
    deps = [
        ":tools",
        "//core",
        "//testing:providence-idl",
        "//third-party:junit",
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
    name = "data-generator",
    srcs = glob(
         ["java/net/morimekta/providence/tools/generator/*.java"],
    ),
    main_class = 'net.morimekta.providence.tools.generator.DataGenerator',
    deps = [
        ":tools",
        "//testing:providence-idl",
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
        ["java/net/morimekta/providence/tools/speedtest/*.java"],
    ),
    main_class = 'net.morimekta.speedtest.SpeedTest',
    deps = [
        "//testing:thrift-idl",
        "//testing:providence-idl",
        "//core",
        "//thrift",
        "//utils",
        "//third-party:android-util",
        "//third-party:args4j",
        "//third-party:commons-math3",
        "//third-party:libthrift",
        "//third-party:slf4j-simple",
    ]
)
