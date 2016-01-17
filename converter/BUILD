java_binary(
  name = "providence-converter",
  srcs = glob(["java/**/*.java"]),
  deps = [
    "//core",
    "//messageio",
    "//thrift",
    "//reflect",
    "//third-party:args4j",
    "//third-party:slf4j-simple",
    "//utils",
  ],
  main_class = "Convert",
  visibility = ['//visibility:public'],
)


test_suite(
    name = "tests",
)
