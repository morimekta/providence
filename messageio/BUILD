java_library(
    name = 'messageio',
    srcs = glob(['java/**/*.java']),
    deps = [
        '//core:core',
    ],
    visibility = ['//visibility:public'],
)

java_test(
    name = 'javatests',
    srcs = glob(['javatests/**/*.java']),
    deps = [
        ':messageio',
        '//core:core',
        '//core:providence-idl-with-deps',
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
