load('release', 'release_jar')

release_jar('core', '//core')
release_jar('core-jackson', '//core-jackson')
release_jar('reflect', '//reflect')
release_jar('messageio', '//messageio')
release_jar('thrift', '//thrift')

filegroup(
    name = 'providence',
    srcs = [
        ':core',
        ':core-jackson',
        ':messageio',
        ':thrift',
        ':reflect',
    ]
)

test_suite(
    name = 'all_tests',
    tests = [
        # '//converter:tests',
        '//core:tests',
        '//core-jackson:tests',
        '//messageio:tests',
        '//reflect:tests',
        '//testing:tests',
        '//tools:tests',
        '//thrift:tests',
    ],
)
