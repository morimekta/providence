load('release', 'release_jar')

release_jar('core', '//core:core')
release_jar('reflect', '//reflect:reflect')
release_jar('jax-rs', '//jax-rs:jax-rs')

filegroup(
    name = 'thrift-j2',
    srcs = [
        ':core',
        ':reflect',
        ':jax-rs',
    ]
)
