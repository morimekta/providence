#!/bin/sh

bazel run compiler:compile -- --gen java2 --out ${PWD}/reflect/model ${PWD}/reflect/model/model.thrift
