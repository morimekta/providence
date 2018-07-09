#!/bin/bash

VERSION=${1:-}

if [[ -z "$1" ]]
then
  VERSION=$(git tag | grep '^v[0-9]\+[.][0-9]\+[.][0-9]\+' | sort -V | tail -n 1 | sed 's/^v//')
fi

rm -rf ~/.m2/repository/net/morimekta/providence
while [[ true ]]
do
  mvn -q \
      download:artifact \
      -DgroupId=net.morimekta.providence \
      -DartifactId=providence-core \
      -Dversion=${VERSION} \
  && echo "Success" \
  && echo -e "\033[32m$(date --iso-8601=seconds | sed 's/+.*//')\033[00m" \
  && exit 0

  echo
  echo 'Failed download...'
  echo -e "\033[31m$(date --iso-8601=seconds | sed 's/+.*//')\033[00m"
  sleep 10
  echo
done
