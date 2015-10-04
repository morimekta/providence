#!/bin/sh

buck run compiler:thrift -- --gen java2 --out reflect/model reflect/model/model.thrift
