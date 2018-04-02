---
layout: page
toc_title: Maven Plugin
title: "Providence Tools: Maven Plugin"
category: bld
date: 2018-05-01 12:00:00
order: 1
---

A maven plugin that compiled *java* sources from thrift files that uses
the `providence-core` libraries.

To use the plugin, add it to the `/project/build/plugins` list of the `pom.xml`
file. E.g.:

```xml
<plugin>
    <groupId>net.morimekta.providence</groupId>
    <artifactId>providence-maven-plugin</artifactId>
    <version>${providence.version}</version>
    <extensions>true</extensions>
</plugin>
```

The `compile` and `testCompile` goals are declared as default lifecycle goals for
the providence plugin (and executed if run with `extensions`). In order to also
run the assemble, the executions must be declared.

```xml
<plugin>
    <groupId>net.morimekta.providence</groupId>
    <artifactId>providence-maven-plugin</artifactId>
    <version>${providence.version}</version>
    <executions>
        <execution>
            <goals>
                <goal>compile</goal>
                <goal>assemble</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```

To include the assembled providence files as a dependency, you can do it with:

```xml
<dependency>
    <groupId>my.package</groupId>
    <artifactId>my-artifact</artifactId>
    <version>${my-artifact.version}</version>
    <classifier>providence</classifier>
    <type>zip</type>
</dependency>
```

Since the content of the artifact should not be included as code, it needs to be handled 
If you want to be able to use the providence plugin shorthand, you can add this to your
main `pom.xml` file:

```xml
<pluginManagement>
    <plugins>
        <plugin>
            <groupId>net.morimekta.providence</groupId>
            <artifactId>providence-maven-plugin</artifactId>
            <version>${providence.version}</version>
        </plugin>
    </plugins>
</pluginManagement>
```

Then you can trigger providence with the goal shorthands on the command line:

```sh
$ mvn providence:compile
$ mvn providence:testCompile
$ mvn providence:assemble
```
