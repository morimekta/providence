load('/thrift', 'java_legacy_thrift')
load('/thrift', 'java_thrift')

genrule(
    name = "test-j2",
    cmd = "cat $(SRCS) | sed s/net.morimekta.test/net.morimekta.test.j2/ > $(OUTS)",
    srcs = ["resources/test.thrift"],
    outs = ["test-j2.thrift"],
)

java_thrift(
    name = "thrift-j2",
    srcs = [
        ":test-j2.thrift",
    ],
    flags = [
        "--android",
    ]
)

java_legacy_thrift(
    name = "thrift",
    srcs = ["resources/test.thrift"],
    flags = [
        "android",
    ]
)

java_binary(
    name = "generate-data",
    srcs = [
        "java/net/morimekta/test/generator/GenerateData.java",
    ],
    main_class = 'net.morimekta.test.generator.GenerateData',
    deps = [
        ":thrift-j2",
        ":thrift",
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
     srcs = [
         "java/net/morimekta/test/speedtest/SpeedTest.java",
     ],
     main_class = 'net.morimekta.test.speedtest.SpeedTest',
     deps = [
         ":thrift-j2",
         ":thrift",
         "//core:core",
         "//protocol:protocol",
         "//utils:utils",
         "//third-party:args4j",
         "//third-party:slf4j-simple",
         "//third-party:libthrift",
     ]
 )
