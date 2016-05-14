Releasing Providence
====================

The stages done to make a release.

### Making a SNAPSHOT release

Snapshot releases can be made directly from the `master` branch.

* Run `# mvn clean deploy` from master at the desired commit.

### Making a version release

Proper releases are done with a branch cut.

#### Check for dependency updates

* Run `mvn versions:display-dependency-updates` to see what has been updated of
  dependencies. See if updates should be done. Usually it's better to depend on
  newer versions, as you may drag in older versions into other projects that
  misses features or has specific bugs.

#### Making the release cut.

* Create a branch called `release-x.y.z` from master at the desired commit.
* Run `# mvn clean verify install site` to build and verify the snapshot build
  you want to release.
* Run `# mvn release:prepare`, which will create two new commits, one with the
  actual release, and one with the "next development cycle".
* Run `# mvn release:perform -Prelease-sign-artifacts` to generate the artifacts
  and push to sonatype for staging.
* Merge the last commit into `master`, and **go back to the release branch**.
* Run `# git reset --hard $(git log --oneline --format=%h -n 2 | tail -n 1)`.
  This will check out the actual release commit.
* Run `# mvn clean verify site site:stage`, which will build the website for the
  release.
* Run `# git checkout gh-pages`, and `# cp -R target/site/* .`, which will
  prepare the page site for the release.
* Run `# git commit -a -m "Site release for ${version}"` to commit.

Not it's time for verification.

* Run `# jekyll serve` and go to `http://localhost:4000/` and go through the
  docs. If that looks right, and the artifacts found at the
  [Nexus Repository Manager](https://oss.sonatype.org/#stagingRepositories) are
  correct, you're ready to make the release.
* Select artifact and push `release` in the top action bar to push the artifacts
  to the maven repository of sonatype.
* Check out the `master` branch again, and run `git push`.
* Then wait for a bit and check out the `gh-pages` branch, and run `git push`.

Now the release is complete.
