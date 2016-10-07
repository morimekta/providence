Providence Tools : Maven Plugin
===============================

A maven plugin that compiled *java* sources from thrift files that uses
the `providence-core` libraries (or tiny, which have almost no dependencies).

To use the plugin, add it to the `/project/build/plugins` list of the `pom.xml`
file. E.g.:

```xml
<plugin>
    <groupId>net.morimekta.providence</groupId>
    <artifactId>providence-maven-plugin</artifactId>
    <version>0.2.2-SNAPSHOT</version>
    <configuration>
        <dependencies>
            <dependency>
                <groupId>net.morimekta.providence</groupId>
                <artifactId>it-common</artifactId>
                <version>0.2.2-SNAPSHOT</version>
            </dependency>
        </dependencies>
    </configuration>
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
