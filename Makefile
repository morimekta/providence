model:
	mvn generate-sources
	rm -rf providence-reflect/src/main/model/*
	cp -R providence-testing/target/generated-sources/model/* providence-reflect/src/main/model/

serializer:
	pvdc -o providence-core/src/main/java/ -g java providence-core/src/main/providence/service.thrift

test-models:
	pvdc -o providence-core/src/test/java/ -g java \
	    providence-core/src/test/providence/*.thrift \
	    providence-core/src/test/providence/*/*.thrift

.PHONY: serializer model test-models
