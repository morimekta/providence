#!/bin/sh

buck run compiler:thrift -- --gen java2 --android --out core/generated  core/res/definitions/*.thrift
