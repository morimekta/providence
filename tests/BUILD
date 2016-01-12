load('/thrift', 'java_legacy_thrift')
load('/thrift', 'java_thrift')

genrule(
    name = "thrift-idl_thrift",
    cmd = "cat $(SRCS) | sed s/providence/thrift/ > $(OUTS)",
    srcs = ["resources/providence-idl.thrift"],
    outs = ["thrift-idl.thrift"],
)

java_thrift(
    name = "providence-idl",
    srcs = [
        "resources/providence-idl.thrift"
    ]
)

java_legacy_thrift(
    name = "thrift-idl",
    srcs = [
        ":thrift-idl.thrift",
    ]
)

java_test(
  name = "test",
  srcs = glob(["javatests/**/*.java"]),
  deps = [
    "//third-party:android-util",
    "//third-party:jackson-annotations",
    "//third-party:jackson-core",
    "//third-party:jackson-databind",
    "//third-party:junit",
    "//core:core",
    "//core:generated",
  ],
  # Ensures the binaries compile properly.
  data = [
    ":generate-data",
    ":speed-test",
  ],
  size = 'small',
)


java_binary(
    name = "generate-data",
    srcs = glob(
         ["java/net/morimekta/generator/*.java"],
    ),
    main_class = 'net.morimekta.generator.GenerateData',
    deps = [
        ":providence-idl",
        "//core",
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
        ":thrift-idl",
        ":providence-idl",
        "//core",
        "//thrift",
        "//utils",
        "//third-party:args4j",
        "//third-party:commons-math3",
        "//third-party:libthrift",
        "//third-party:slf4j-simple",
    ]
)
