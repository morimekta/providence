java_library(
  name = "jax-rs",
  srcs = glob(["src/**/*.java"]),
  deps = [
    "//core:core",
    "//third-party:jax-rs",
  ],
  visibility = ['//visibility:public'],
)

java_test(
  name = "test",
  srcs = glob(["test/**/*.java"]),
  deps = [
    ":jax-rs",
    "//core:core",
    "//core:generated",
    "//third-party:junit",
    "//third-party:jax-rs",
  ],
  size = 'small',
)
