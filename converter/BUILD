java_binary(
  name = "thrift-j2",
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
