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
  name = "test",
  srcs = glob(["javatests/**/*.java"]),
  deps = [
    ":jax-rs",
    "//core:core",
    "//core:generated",
    "//third-party:junit",
    "//third-party:javax.ws.rs-api",
  ],
  size = 'small',
)
