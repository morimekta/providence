Releasing Providence
====================

The stages done to make a release.

### Making a SNAPSHOT release

Snapshot releases can be made directly from the `master` branch.

* Run `# mvn clean deploy` from master at the desired commit.

### Making a version release

Proper releases are done with a branch cut.

#### Making the release cut.

* Create a branch called `release-x.y.z` from master at the desired commit.
* Run `# mvn versions:set` to new snapshot version in branch `master`, and push.
* Run `# mvn versions:set` to release version in `release-x.y.z` branch, and push.

#### Making the actual release

* Run `# mvn clean deploy -Prelease-sign-artifacts`
* Go to [Nexus Repository Manager](https://oss.sonatype.org/#stagingRepositories)
  and find the correct `Staging Repository`. Check that the content is correct and
  select `Release`, and confirm.
