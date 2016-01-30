java_library(
    name = 'core-streams',
    srcs = glob(['java/**/*.java']),
    deps = [
        '//core',
    ],
    visibility = ['//visibility:public'],
)

java_test(
    name = 'javatests',
    srcs = glob(['javatests/**/*.java']),
    deps = [
        ':core-streams',
        '//core:core',
        '//third-party:junit',
    ],
    size = 'small',
)


test_suite(
    name = "tests",
    tests = [
        ":javatests",
    ]
)
