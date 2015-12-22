java_library(
    name = 'messageio',
    srcs = glob(['java/**/*.java']),
    deps = [
        '//core:core',
    ],
    visibility = ['//visibility:public'],
)

java_test(
    name = 'test',
    srcs = glob(['javatests/**/*.java']),
    deps = [
        ':messageio',
        '//core:core',
        '//third-party:junit',
    ],
    size = 'small',
)
