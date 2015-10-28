load('release', 'release_jar')

release_jar('core', '//core')
release_jar('reflect', '//reflect')
release_jar('jax-rs', '//jax-rs')
release_jar('messageio', '//messageio')

filegroup(
    name = 'thrift-j2',
    srcs = [
        ':core',
        ':jax-rs',
        ':messageio',
        ':reflect',
    ]
)
