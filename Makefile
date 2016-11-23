VERSION := $(shell cat pom.xml | grep "^    <version>" | sed -e 's:.*<version>::' -e 's:</version>.*::')

clean:
	mvn clean

resources:
	cp providence-testing/src/test/providence/service.thrift providence-core-client/src/test/providence/
	cp providence-testing/src/test/providence/service.thrift providence-core-server/src/test/providence/
	cp providence-testing/src/test/thrift/service.thrift providence-core-client/src/test/thrift/
	cp providence-testing/src/test/thrift/service.thrift providence-core-server/src/test/thrift/

compile:
	mvn net.morimekta.providence:providence-maven-plugin:$(VERSION):compile

test-compile:
	mvn net.morimekta.providence:providence-maven-plugin:$(VERSION):testCompile

models: compile
	rm -rf providence-reflect/src/main/model/*
	mv providence-reflect/target/generated-sources/providence/* \
	   providence-reflect/src/main/model/
	mv providence-core/target/generated-sources/providence/net/morimekta/providence/* \
	   providence-core/src/main/java/net/morimekta/providence

test-models: test-compile
	cp -R providence-core/target/generated-test-sources/providence/* \
	      providence-core/src/test/java/
	rm -rf providence-core/target/generated-test-sources/providence/*

.PHONY: clean compile test-compile models test-models
