Releasing Providence
====================

The stages done to make a release.

### Making a SNAPSHOT release

Snapshot releases can be made directly from the `master` branch.

* Run `# mvn clean deploy` from master at the desired commit.

### Making a version release

Proper releases are done with a branch cut.

#### Check for dependency updates

Run the maven versions plugin to see what has been updated of dependencies and
plugins. See if updates should be done. Usually it's better to depend on
newer versions, as you may drag in older versions into other projects that
misses features or has specific bugs.

```bash
mvn versions:display-dependency-updates
mvn versions:display-plugin-updates
```

#### Making the release cut.

```bash
# Do the maven release:
mvn -Plib,cli,it release:prepare
mvn -Plib release:perform
git fetch origin
```

If the artifacts found at the [Nexus Repository Manager](https://oss.sonatype.org/#stagingRepositories)
are correct, you're ready to make the release. If not a git hard rollback is needed (to remove release
version, tag and commits). First make the actual binary release:

```bash
# check out the release commit
git checkout HEAD~1 -b release
# make the versions env variable:
export pvd_version=$(cat pom.xml | grep '^    <version>' | sed 's: *[<][/]\?version[>]::g')

# build the tarball, DEB and RPM packages, which produces:
# - providence-tools/target/providence-tools-${pvd_version}.tar.gz
# - providence-tools/target/providence-${pvd_version}_all.deb
# - providence-tools/target/rpm/providence/RPMS/noarch/providence-${pvd_version}-1.noarch.rpm
mvn -Pcli clean package

# Site release:
mvn -Plib clean verify site site:stage
git checkout gh-pages && git pull -p && cp -R target/staging/* .
git add .
git commit -a -m "Site release for ${pvd_version}"
git push
```

Then update and release the gradle plugin. You may need to wait 20+ minutes for
the maven indices to be updates with the providence release before you can do this
step:

* Go to `providence-gradle-plugin` repository.
* Update version in `gradle.properties` and in the `README.md` file.

```bash
# in the providence-gradle-plugin repository:
./gradlew clean test publishPlugins
```

Now the release is complete.
