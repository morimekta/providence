VERSION := $(shell cat pom.xml | grep "^    <version>" | sed -e 's:.*<version>::' -e 's:</version>.*::')

clean:
	mvn clean

compile:
	mvn net.morimekta.providence:providence-maven-plugin:$(VERSION):compile

test-compile:
	mvn net.morimekta.providence:providence-maven-plugin:$(VERSION):testCompile

models: compile
	rm -rf providence-reflect/src/main/model/*
	cp -R providence-reflect/target/generated-sources/providence/* \
	      providence-reflect/src/main/model/
	cp -R providence-core/target/generated-sources/providence/net/morimekta/providence/serializer/* \
	      providence-core/src/main/java/net/morimekta/providence/serializer

test-models: test-compile
	pvdc -o providence-core/src/test/java/ -g java \
	    providence-core/src/test/providence/*.thrift \
	    providence-core/src/test/providence/*/*.thrift

.PHONY: clean compile test-compile models test-models
