load('release', 'release_jar')

release_jar('core', '//core:core')
release_jar('reflect', '//reflect:reflect')
release_jar('messageio', '//messageio:messageio')
release_jar('thrift', '//thrift:thrift')

filegroup(
    name = 'providence',
    srcs = [
        ':core',
        ':messageio',
        ':thrift',
        ':reflect',
    ]
)

test_suite(
    name = 'all_tests',
    tests = [
        '//compiler:tests',
        # '//converter:tests',
        '//core:tests',
        '//core-jackson:tests',
        '//messageio:tests',
        '//reflect:tests',
        '//testing:tests',
        '//tools:tests',
        # '//thrift:tests',
    ],
)
