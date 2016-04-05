#!/bin/bash

for i in providence-*;
do
    if [[ -d "$i/target/site" ]]
    then
        rm -rf "target/site/$i"
        mv "$i/target/site" "target/site/$i"
    fi
done
