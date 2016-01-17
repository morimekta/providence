exports_files(glob(["resources/**"]))

java_library(
  name = "jackson",
  srcs = glob(["java/**/*.java"]),
  visibility = ['//visibility:public'],
  deps = [
    "//core:core",
    "//third-party:jackson-databind",
  ],
)

java_test(
  name = "javatests",
  srcs = glob(["javatests/**/*.java"]),
  deps = [
    ":jackson",
    "//third-party:jackson-annotations",
    "//third-party:jackson-core",
    "//third-party:jackson-databind",
    "//third-party:junit",
    "//core:core",
    "//core:providence-idl",
  ],
  size = 'small',
)

test_suite(
    name = "tests",
    tests = [
        ":javatests",
    ]
)
