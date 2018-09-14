#!/bin/bash

VERSION=${1:-}

if [[ -z "$1" ]]
then
  VERSION=$(git tag | grep '^v[0-9]\+[.][0-9]\+[.][0-9]\+' | sort -V | tail -n 1 | sed 's/^v//')
fi

TRY=1

rm -rf ~/.m2/repository/net/morimekta/providence
while [[ true ]]
do
  echo -e "\033[32mDownloading: net.morimekta.providence:providence-core:${VERSION}\033[00m"
  echo -e " -- Attempt ${TRY}"
  mvn -q download:artifact \
      -DgroupId=net.morimekta.providence \
      -DartifactId=providence-core \
      -Dversion=${VERSION} > /dev/null \
  && echo -e "\033[32mSuccess!\033[00m" \
  && echo -e " -- $(date --iso-8601=seconds | sed 's/+.*//')" \
  && exit 0

  echo -e '\033[31mFailed download...\033[00m'
  echo -e " -- $(date --iso-8601=seconds | sed 's/+.*//')"
  sleep 180
  TRY=$(echo ${TRY} + 1 | bc)
  echo
done
