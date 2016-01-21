exports_files(glob(["resources/**"]))

java_library(
  name = "core-jackson",
  srcs = glob(["java/**/*.java"]),
  visibility = ['//visibility:public'],
  deps = [
    "//core",
    "//third-party:jackson-databind",
  ],
)

java_test(
  name = "javatests",
  srcs = glob(["javatests/**/*.java"]),
  deps = [
    ":core-jackson",
    "//third-party:jackson-annotations",
    "//third-party:jackson-core",
    "//third-party:jackson-databind",
    "//third-party:junit",
    "//core",
  ],
  size = 'small',
)

test_suite(
    name = "tests",
    tests = [
        ":javatests",
    ]
)
