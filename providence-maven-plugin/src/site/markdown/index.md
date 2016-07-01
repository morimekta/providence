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
    <version>0.2.0-SNAPSHOT</version>
    <executions>
        <execution>
            <id>generate-providence-sources</id>
            <phase>generate-sources</phase>
            <goals>
                <goal>compile</goal>
            </goals>
        </execution>
        <execution>
            <id>generate-jackson-sources-for-test</id>
            <phase>generate-test-sources</phase>
            <goals>
                <goal>testCompile</goal>
            </goals>
            <configuration>
                <tiny>true</tiny>
                <jackson>true</jackson>
                <inputFiles>
                    <includes>
                        <include>src/test/jackson/**/*.thrift</include>
                    </includes>
                </inputFiles>
                <outputDir>target/generated-test-sources/jackson</outputDir>
            </configuration>
        </execution>
    </executions>
</plugin>
```
