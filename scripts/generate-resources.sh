#!/bin/sh

bazel run compiler:compile -- --gen java2 --android --out ${PWD}/core/generated  ${PWD}/core/res/definitions/*.thrift
