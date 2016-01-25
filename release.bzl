load("/tools/build_defs/pkg/pkg", "pkg_tar", "pkg_deb")

VERSION='0.0.1-alpha'

def release_jar(name,
                target):
    native.genrule(
        name = name,
        cmd = 'cp $(SRCS) $(OUTS)',
        srcs = [target],
        outs = ['providence-%s-%s.jar' % (name, VERSION)],
    )

def java_pkg_deb(name,
                 jar,
                 package = None,
                 exe = None,
                 extra_arguments = [],
                 location = "usr/local"):
    if package == None:
        package = name
    if exe == None:
        exe = package

    jar_location = location + "/lib/" + package + "-" + VERSION + ".jar"

    native.genrule(
        name = '__' + name + '_exe',
        cmd = "echo '#!/bin/bash' > $(OUTS);" +
              "echo '' >> $(OUTS);" +
              "echo 'java -jar /" + jar_location + " " + ' '.join(extra_arguments) + " $$@' >> $(OUTS)",
        outs = [
            location + "/bin/" + exe
        ],
    )

    native.genrule(
        name = '__' + name + '_jar',
        cmd = "cp $(SRCS) $(OUTS)",
        outs = [
            location + "/lib/" + package + "-" + VERSION + ".jar"
        ],
        srcs = [
            jar,
        ],
    )

    pkg_tar(
        name = "__" + name + "_bin",
        package_dir = location + "/bin",
        files = [
            ":__" + name + "_exe",
        ],
        mode = "0755",
    )

    pkg_tar(
        name = "__" + name + "_lib",
        package_dir = location + "/lib",
        files = [
            ":__" + name + "_jar",
        ],
        mode = "0644",
    )

    pkg_tar(
        name = "__" + name + "_data",
        deps = [
            ":__" + name + "_bin",
            ":__" + name + "_lib",
        ],
    )

    pkg_deb(
        name = name,
        data = ":__" + name + "_data",
        depends = [
            "java8-runtime",
            "java8-jdk",
        ],
        suggests = [
            "openjdk8-jre",
            "openjdk8-jdk",
        ],
        description = "Description of providence-compiler...",
        homepage = "http://github.com/morimekta/providence",
        maintainer = "Stein Eldar Johnsen <morimekta@gmail.com>",
        package = package,
        version = VERSION,
    )
