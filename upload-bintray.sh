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

echo
echo Now go to https://bintray.com/morimekta/debian-ppa/providence and publish the new file.
echo
