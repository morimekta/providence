java_library(
  name = "jax-rs",
  srcs = glob(["java/**/*.java"]),
  deps = [
    "//core:core",
    "//third-party:javax.ws.rs-api",
  ],
  visibility = ['//visibility:public'],
)

java_test(
  name = "javatests",
  srcs = glob(["javatests/**/*.java"]),
  deps = [
    ":jax-rs",
    "//core:core",
    "//testing:providence-idl",
    "//third-party:junit",
    "//third-party:javax.ws.rs-api",
  ],
  size = 'small',
)

test_suite(
    name = "tests",
    tests = [
        ":javatests",
    ]
)
