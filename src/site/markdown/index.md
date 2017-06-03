Providence Models
=================

The `providence` project was made in order to make an immutable model java
library for thrift. It is mostly separate from the thrift library, but can use
the standard thrift protocols to serialize and serialize messages. It is mainly
based on the Facebook / Apache [thrift](https://thrift.apache.org/) library,
but with some differences and limitations.

Note that providence **requires** java >= 8.

## Providence Setup

You do not need to install anything (but java) in order to start using `providence`.
The project has builder plugins for `maven` and `gradle`, but if neither is used
there is a binary code generator available
[here](https://github.com/morimekta/providence/releases) (check for the version of
providence that you want to use).

### Maven

In maven the setup is simply to add a directory `src/main/providence` or
`src/test/providence` there thrift definition files go, and add the `providence-maven-plugin`
to the `pom.xml` file like this:

```xml
<build>
    <plugins>
        <plugin>
            <groupId>net.morimekta.providence</groupId>
            <artifactId>providence-maven-plugin</artifactId>
            <version>${providence.version}</version>
            <extensions>true</extensions>
        </plugin>
    </plugins>
</build>
```

Then you can add your `.thrift` files and run:

```bash
mvn generate-sources
# or
mvn generage-test-sources
```

Now the generated providence files should be available in your project.

### Gradle

TODO: Show example.

## Developer Setup

In order to compile providence itself, you need the java 8 `java` and `javac`
commands (I recommend `openjdk8-jdk`), and `maven` (3.3). Then check out
`git@github.com:morimekta/providence.git` and build with:

```bash
mvn clean verify install
```

## Contributing

You can send me an [email](mailto:oss@morimekta.net) to suggest a feature. Or
you can make it yourself by cloning
[the project](https://github.com/morimekta/providence), and create a
[pull request](https://github.com/morimekta/providence/pulls) for your change.

Make sure to name it properly, and describe what the change does, and assign
the request to [@morimekta](https://github.com/morimekta). That should send me
an email, so I know it's there and take care of it.
