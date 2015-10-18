#!/bin/sh

buck run compiler:compile -- --gen java2 --out reflect/model reflect/model/model.thrift
