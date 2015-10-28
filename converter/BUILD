java_binary(
  name = "convert",
  srcs = glob(["src/**/*.java"]),
  deps = [
    "//core",
    "//messageio",
    "//protocol",
    "//reflect",
    "//third-party:args4j",
    "//utils",
  ],
  main_class = "org.apache.thrift.j2.converter.Convert",
  visibility = ['//visibility:public'],
)
