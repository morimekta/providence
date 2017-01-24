Providence Gradle Plugin
========================

A gradle plugin for generating providence serialization models. To enable
the providence plugin, use the plugin with the **same version** as the
providence release you are using.

Note that the plugin requires the `java` plugin to be applied, and must
be applied **after** the java plugin. See example below to ensure that.

```groovy
var providence_version = '0.3.7'

plugins {
    id "net.morimekta.providence.gradle" version "${providence_version}" apply false
}

apply plugin: 'java'
apply plugin: 'net.morimekta.providence.gradle'

// etc...
dependencies {
    // The generated code needs the matching providence-core code to be available
    // at compile time.
    compile "net.morimekta.providence:providence-core:${providence_version}"
}
```

The providence plugin can also easily be configured, e.g. for handling a multi-
project IDL based thrift repository, you can set up input and includes separately.
Input files are the thrift programs that will be generated code for, and includes
will we available at compile time, but not generated for.

```groovy
providence {
    main {
        // In order to only compile the locally defined IDL files
        input = fileTree('idl') {
            include 'github.com/morimekta/providence/**/*.thrift'
        }
        // Non compiled but included program files.
        include = fileTree('idl') {
            include '**/*.thrift'
            exclude 'github.com/morimekta/providence/**/*.thrift'
        }

        // Flags to enable specific providence generation features.
        android = false
        jackson = false
    }
}
```

#### Source Code

The gradle plugin is placed in [it's own](https://github.com/morimekta/providence-gradle-plugin)
repository as it itself uses the gradle build system (not maven as here).
