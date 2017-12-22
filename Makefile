VERSION := $(shell cat pom.xml | grep "^    <version>" | sed -e 's:.*<version>::' -e 's:</version>.*::')

compile:
	mvn net.morimekta.providence:providence-maven-plugin:$(VERSION):compile

test-compile:
	mvn net.morimekta.providence:providence-maven-plugin:$(VERSION):testCompile
	mvn -Dprovidence.gen.rw_binary=false -Dprovidence.test.input=src/test/no_rw_binary/**/*.thrift net.morimekta.providence:providence-maven-plugin:$(VERSION):testCompile

models: compile
	rm -rf providence-reflect/src/main/model/*
	mv providence-reflect/target/generated-sources/providence/* \
	   providence-reflect/src/main/model/
	mv providence-core/target/generated-sources/providence/net/morimekta/providence/* \
	   providence-core/src/main/java/net/morimekta/providence

test-models: test-compile
	rm -rf providence-core/src/test/java-gen/*
	mv providence-core/target/generated-test-sources/providence/* \
	   providence-core/src/test/java-gen/
	rm -rf providence-reflect/src/test/java-gen/*
	mv providence-reflect/target/generated-test-sources/providence/* \
	   providence-reflect/src/test/java-gen/

thrift:
	gradle -b thrift.gradle generateStaticThrift

resources:
	mvn clean package -Pit-generator
	cp -R it-generator-java/target/java.jar providence-tools-generator/src/test/resources/generator
	cp -R it-generator-js/target/js.jar     providence-tools-generator/src/test/resources/generator

.PHONY: compile test-compile models test-models thrift js
