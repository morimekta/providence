load('/tools/build_defs/pvd/release', 'release_jar')
load('/tools/build_defs/pvd/release', 'java_pkg_deb')

release_jar('providence-core', '//providence-core')
release_jar('providence-core-jackson', '//providence-core-jackson')
release_jar('providence-core-streams', '//providence-core-streams')
release_jar('providence-reflect', '//providence-reflect')
release_jar('providence-messageio', '//providence-messageio')
release_jar('providence-thrift', '//providence-thrift')

java_pkg_deb(
    name = "providence-compiler-deb",
    jar = "//tools:providence-compiler_deploy.jar",
    description = "Source code compiler for providence.\n" +
                  "Compiles *.thrift and *.json definition files to providence source code.\n",
    package = "providence-compiler",
    exe = "pvdc",
    depends = [
        "java8-jdk",
    ],
    suggests = [
        "openjdk8-jdk",
    ],
)

java_pkg_deb(
    name = "providence-converter-deb",
    description = "Providence data converter.\n" +
                  "Converts data files from one format to another using providence definitions.\n",
    jar = "//tools:providence-converter_deploy.jar",
    package = "providence-converter",
    exe = "pvd",
)

filegroup(
    name = 'release',
    srcs = [
        ### libraries ###
        ':core',
        ':core-jackson',
        ':messageio',
        ':thrift',
        ':reflect',
        ### packages ###
        ':providence-compiler-deb',
        ':providence-converter-deb',
    ]
)

test_suite(
    name = 'all_tests',
    tests = [
        '//core:tests',
        '//core-jackson:tests',
        '//core-streams:tests',
        '//messageio:tests',
        '//reflect:tests',
        '//testing:tests',
        '//tools:tests',
        '//thrift:tests',
    ],
)
