#!/usr/bin/env bash

set -e

function __sort() {
   type -fp gsort >/dev/null && command gsort "$@" || sort "$@"
}

export pvd_version=$(git tag | grep '^v[0-9]\{1,4\}[.][0-9]\+' | __sort -V | tail -n 1)

echo "This will upload packages to bintray:"
echo
echo providence-tools/target/providence-${pvd_version}_all.deb
echo providence-tools/target/providence-tools-${pvd_version}.tar.gz
echo providence-tools/target/rpm/providence/RPMS/noarch/providence-${pvd_version}-1.noarch.rpm
echo

if [[ -f ~/.config/bintray/api-key ]]
then
  echo -n ""
else
  echo
  echo "Make sure that ~/.config/bintray/api-key contains the BINTRAY api key."
  exit 1
fi

echo "PS: Login to docker with:"
echo "cat ~/.config/bintray/api-key | docker login -u morimekta --password-stdin morimekta-docker-tools.bintray.io"
echo

CONFIRM=
echo
echo -n "Continue? (y/N):"
read -n 1 CONFIRM
echo
if [[ "$CONFIRM" != "y" ]]
then
    exit 0
fi

BINTRAY_API_KEY=$(cat ~/.config/bintray/api-key)

echo -n "providence-${pvd_version}_all.deb: "
curl -T providence-tools/target/providence-${pvd_version}_all.deb \
     -umorimekta:${BINTRAY_API_KEY} \
     https://api.bintray.com/content/morimekta/debian-ppa/providence/${pvd_version}/providence-${pvd_version}_all.deb;deb_distribution=stable;deb_component=main;deb_architecture=all
echo
echo -n "providence-${pvd_version}-1.noarch.rpm: "
curl -T providence-tools/target/rpm/providence/RPMS/noarch/providence-${pvd_version}-1.noarch.rpm \
     -umorimekta:${BINTRAY_API_KEY} \
     https://api.bintray.com/content/morimekta/yum-repo/providence/${pvd_version}/providence-${pvd_version}-1.noarch.rpm
echo
echo -n "providence-tools-${pvd_version}.tar.gz: "
curl -T providence-tools/target/providence-tools-${pvd_version}.tar.gz \
     -umorimekta:${BINTRAY_API_KEY} \
     https://api.bintray.com/content/morimekta/archive/providence/${pvd_version}/providence-tools-${pvd_version}.tar.gz
echo
echo

docker build . \
       -t morimekta-docker-tools.bintray.io/providence:${pvd_version} \
       -t morimekta-docker-tools.bintray.io/providence:latest
docker push morimekta-docker-tools.bintray.io/providence:${pvd_version}
docker push morimekta-docker-tools.bintray.io/providence:latest

echo
echo "Now go to:"
echo " - https://bintray.com/morimekta/debian-ppa/providence"
echo " - https://bintray.com/morimekta/yum-repo/providence"
echo " - https://bintray.com/morimekta/archive/providence"
echo "and publish the new files. (and add to downloads-list in archive)"
echo

SHA256SUM=$(cat providence-tools/target/providence-tools-${pvd_version}.tar.gz | \
            sha256sum | \
            sed 's/ .*//')

if [[ -f "../homebrew-tools/Formula/providence.rb" ]]
then

cat <<EOF > ../homebrew-tools/Formula/providence.rb
class Providence < Formula
    desc "Providence Tools"
    homepage "http://www.morimekta.net/providence"
    version "${pvd_version}"
    url "https://bintray.com/morimekta/archive/download_file?file_path=providence-tools-#{version}.tar.gz"
    sha256 "${SHA256SUM}"

    depends_on :java => "1.8+"

    def install
        bin.install Dir["bin/*"]
        share.install Dir["share/*"]
    end
end
EOF

    echo
    echo "And go to ../homebrew-tools and commit and push changes."
else
    echo
    echo "Please go to 'homebrew-tools/Formula/providence.rb' and set:"
    echo "    version ${pvd_version}"
    echo "    sha256 ${SHA256SUM}"
fi

echo
