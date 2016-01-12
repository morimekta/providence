java_library(
  name = "compiler",
  srcs = glob(
    ["java/**/*.java"],
    exclude = ['java/net/morimekta/providence/compiler/Compiler.java']),
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
  data = [
    ":providence-compiler",
  ],
  size = 'small',
)

java_binary(
  name = "providence-compiler",
  srcs = [
    'java/net/morimekta/providence/compiler/Compiler.java',
  ],
  deps = [
    ":compiler",
    "//core:core",
    "//reflect:reflect",
    "//third-party:args4j",
  ],
  main_class = "net.morimekta.providence.compiler.Compiler",
  visibility = [
    "//visibility:public"
  ],
)
