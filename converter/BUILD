java_binary(
  name = "convert",
  srcs = glob(["src/**/*.java"]),
  deps = [
    "//core:core",
    "//protocol:protocol",
    "//reflect:reflect",
    "//third-party:args4j",
    "//utils:utils",
  ],
  main_class = "org.apache.thrift.j2.converter.Convert",
  visibility = ['//visibility:public'],
)
