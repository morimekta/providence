load('release', 'release_jar')

release_jar('core', '//core:core')
release_jar('reflect', '//reflect:reflect')
release_jar('jax-rs', '//jax-rs:jax-rs')
release_jar('messageio', '//messageio:messageio')
release_jar('thrift', '//thrift:thrift')

filegroup(
    name = 'providence',
    srcs = [
        ':core',
        ':jax-rs',
        ':messageio',
        ':thrift',
        ':reflect',
    ]
)

test_suite(
    name = 'all_tests',
    tests = [
        '//compiler:test',
        # '//converter:test',
        '//core:test',
        '//jackson:test',
        '//jax-rs:test',
        '//messageio:test',
        '//reflect:test',
        '//tests:test',
    ],
)
