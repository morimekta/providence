java_binary(
  name = "thrift-j2",
  srcs = glob(["java/**/*.java"]),
  deps = [
    "//core",
    "//messageio",
    "//protocol",
    "//reflect",
    "//third-party:args4j",
    "//third-party:slf4j-simple",
    "//utils",
  ],
  main_class = "org.apache.thrift.j2.converter.Convert",
  visibility = ['//visibility:public'],
)
