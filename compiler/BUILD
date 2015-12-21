java_library(
  name = "compiler",
  srcs = glob(
    ["java/**/*.java"],
    exclude = ['java/org/apache/thrift/j2/compiler/Compiler.java']),
  deps = [
    "//core:core",
    "//reflect:reflect",
    "//third-party:args4j",
    "//utils:utils",
  ],
  visibility = [
    "//visibility:public"
  ],
)

java_test(
  name = "test",
  srcs = glob(["javatests/**/*.java"]),
  deps = [
    ":compiler",
    "//core:core",
    "//reflect:reflect",
    "//third-party:junit",
  ],
  size = 'small',
)

java_binary(
  name = "thrift-j2c",
  srcs = [
    'java/org/apache/thrift/j2/compiler/Compiler.java',
  ],
  deps = [
    ":compiler",
    "//core:core",
    "//reflect:reflect",
    "//third-party:args4j",
  ],
  main_class = "org.apache.thrift.j2.compiler.Compiler",
  visibility = [
    "//visibility:public"
  ],
)
