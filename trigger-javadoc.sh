#!/bin/bash
VERSION=${1:-}

if [[ -z "$1" ]]
then
  VERSION=$(git tag | grep '^v[0-9]' | sort -V | tail -n 1 | sed 's/^v//')
fi

for m in providence $(\
    cat pom.xml | \
    grep '<module>providence-' | \
    sed -e 's/.*<module>//' -e 's:</.*::' | \
    grep -v 'providence-tools' | \
    sort | uniq )
do
  URL="http://www.javadoc.io/doc/net.morimekta.providence/$m/$VERSION"
  echo -e "\033[01m$URL\033[00m"
  curl -s $URL > /dev/null || exit 1
done
