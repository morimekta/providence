load('/tools/build_defs/pvd/release', 'release_jar')
load('/tools/build_defs/pvd/release', 'java_pkg_deb')

release_jar('core', '//providence-core')
release_jar('core-jackson', '//providence-core-jackson')
release_jar('core-streams', '//providence-core-streams')
release_jar('reflect', '//providence-reflect')

release_jar('messageio', '//providence-messageio')
release_jar('thrift', '//providence-thrift')
release_jar('testing', '//providence-testing')

java_pkg_deb(
    name = "compiler-deb",
    jar = "//providence-tools:providence-compiler_deploy.jar",
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
    name = "converter-deb",
    description = "Providence data converter.\n" +
                  "Converts data files from one format to another using providence definitions.\n",
    jar = "//providence-tools:providence-converter_deploy.jar",
    package = "providence-converter",
    exe = "pvd",
)

filegroup(
    name = 'release',
    srcs = [
        ### libraries ###
        ':core',
        ':core-jackson',
        ':core-streams',
        ':reflect',
        ### extra utilities ###
        ':messageio',
        ':testing',
        ':thrift',
        ### packages ###
        ':compiler-deb',
        ':converter-deb',
    ]
)

test_suite(
    name = 'all_tests',
    tests = [
        '//providence-core:tests',
        '//providence-core-jackson:tests',
        '//providence-core-streams:tests',
        '//providence-reflect:tests',

        '//providence-messageio:tests',
        '//providence-testing:tests',
        '//providence-thrift:tests',

        '//providence-tools-generator:tests',
        '//providence-tools:tests',
    ],
)
