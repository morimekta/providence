load('/thrift', 'java_legacy_thrift')
load('/thrift', 'java_thrift')

genrule(
    name = "thrift-idl",
    cmd = "cat $(SRCS) | sed s/providence/thrift/ > $(OUTS)",
    srcs = ["resources/providence-idl.thrift"],
    outs = ["thrift-idl.thrift"],
)

java_thrift(
    name = "providence",
    srcs = [
        "resources/providence-idl.thrift"
    ],
    options = [
        "--android",
    ]
)

java_legacy_thrift(
    name = "thrift",
    srcs = [
        ":thrift-idl.thrift",
    ],
    flags = [
        "android",
    ]
)

java_binary(
    name = "generate-data",
    srcs = glob(
         ["java/net/morimekta/generator/*.java"],
    ),
    main_class = 'net.morimekta.generator.GenerateData',
    deps = [
        ":thrift",
        ":providence",
        "//core:core",
        "//protocol:protocol",
        "//utils:utils",
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
        ":thrift",
        ":providence",
        "//core:core",
        "//protocol:protocol",
        "//utils:utils",
        "//third-party:args4j",
        "//third-party:commons-math3",
        "//third-party:libthrift",
        "//third-party:slf4j-simple",
    ]
)
