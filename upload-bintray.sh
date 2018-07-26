#!/usr/bin/env bash

set -e

export pvd_version=$(cat pom.xml | grep '^    <version>' | sed 's: *[<][/]\?version[>]::g')

echo "This will upload packages to bintray:"
echo
echo providence-tools/target/providence-${pvd_version}_all.deb
echo providence-tools/target/providence-tools-${pvd_version}.tar.gz
echo providence-tools/target/rpm/providence/RPMS/noarch/providence-${pvd_version}-1.noarch.rpm
echo
echo curl -T providence-tools/target/providence-${pvd_version}_all.deb \
     -umorimekta:...... \
     https://api.bintray.com/content/morimekta/debian-ppa/providence/${pvd_version}/providence-${pvd_version}_all.deb;deb_distribution=stable;deb_component=main;deb_architecture=all

if [[ -f ~/.config/bintray/api-key ]]
then
  echo -n ""
else
  echo
  echo "Make sure that ~/.config/bintray/api-key contains the BINTRAY api key."
  exit 1
fi

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

curl -T providence-tools/target/providence-${pvd_version}_all.deb \
     -umorimekta:${BINTRAY_API_KEY} \
     https://api.bintray.com/content/morimekta/debian-ppa/providence/${pvd_version}/providence-${pvd_version}_all.deb;deb_distribution=stable;deb_component=main;deb_architecture=all

curl -T providence-tools/target/rpm/providence/RPMS/noarch/providence-${pvd_version}-1.noarch.rpm \
     -umorimekta:${BINTRAY_API_KEY} \
     https://api.bintray.com/content/morimekta/yum-repo/providence/${pvd_version}/providence-${pvd_version}-1.noarch.rpm

echo
echo "Now go to:"
echo " - https://bintray.com/morimekta/debian-ppa/providence"
echo " - https://bintray.com/morimekta/yum-repo/providence"
echo "and publish the new files."
echo

SHA256SUM=$(curl -sL https://github.com/morimekta/providence/releases/download/v${pvd_version}/providence-tools-${pvd_version}.tar.gz --output - | \
            sha256sum | \
            sed 's/ .*//')

if [[ -f "../homebrew-tools/Formula/providence.rb" ]]
then

cat <<EOF > ../homebrew-tools/Formula/providence.rb
class Providence < Formula
    desc "Providence Tools"
    homepage "http://www.morimekta.net/providence"
    version "${pvd_version}"
    url "https://github.com/morimekta/providence/releases/download/v#{version}/providence-tools-#{version}.tar.gz"
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
    echo "Please go to 'homebrew-tools/Formula/providence.rb' and set:
    echo "    version ${pvd_version}"
    echo "    sha256 ${SHA256SUM}"
fi

echo
