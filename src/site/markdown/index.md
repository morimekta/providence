Providence
==========

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

Note that you always need to add dependency to `providence-core` of the appropriate
version for it to work.

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

When using `gradle` as build engine, you can use the `providence-gradle-plugin`,
which can be added with the lines.

```groovy
var providence_version = '{providence version}'

plugins {
    id "net.morimekta.providence.gradle" version "${providence_version}" apply false
}

apply plugin: 'java'
apply plugin: 'net.morimekta.providence.gradle'

```

## Developer Setup

In order to compile providence itself, you need the java 8 `java` and `javac`
commands (I recommend `openjdk8-jdk`), and `maven` (3.3). Then check out
`git@github.com:morimekta/providence.git` and build with:

```bash
mvn clean verify install
```

## Terms and Definitions

Throughout the providence documentation I use a number of terms that
easily can be confused.

- **[thrift]**: A system of converting files that follow the `thirft` IDL
  specification found [here](https://thrift.apache.org/docs/idl) to some form of
  code or data. I currently know of 5 `thrift` systems, other than `providence`:
    - [Apache Thrift](https://thrift.apache.org): The "main" thrift implementation
      containing lots of languages.
    - [FB Thrift](https://github.com/facebook/fbthrift): A FaceBook managed fork
      of thrift with changes and features that FaceBook wants.
    - [Thrift-Nano](https://github.com/markrileybot/thrift-nano): A version of thrift
      optimized for low memory consumption e.g. for using on low-power embedded systems.
    - [Thrifty](https://github.com/Microsoft/thrifty): A compact and simple java version
      of thrift aimed at mobile platforms.
    - [ThriftPy](https://thriftpy.readthedocs.io/en/latest/): An alternative python
      library and compiler for thrift.
- **[Apache Thrift]**: Is the "official" Apache hosted implementation of thrift.
  This is essentially what is compared with for determining compatibility.
- **[Providence]**: Is all of this project (including some off-repository parts
  like [providence-gradle-plugin](https://www.github.com/morimekta/providence-gradle-plugin)).

Also inside providence, we use a little different set of definitions from
Apache Thrift.

- **[message]**: Message is the base type of all the structured data typed
  defined in a thrift file, including `struct`, `exception` and `union`.
  This is variantly called `struct` and `base type` in Apache Thrift.
- **[service call]**: Is the wrapper structure that is sent with the
  call to and response from a service method call.
  This is what is called a `message` in Apache Thrift.

## Contributing

You can send me an [email](mailto:oss@morimekta.net) to suggest a feature. Or
you can make it yourself by cloning
[the project](https://github.com/morimekta/providence), and create a
[pull request](https://github.com/morimekta/providence/pulls) for your change.

Make sure to name it properly, and describe what the change does, and assign
the request to [@morimekta](https://github.com/morimekta). That should send me
an email, so I know it's there and take care of it.
