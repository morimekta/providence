model:
	mvn generate-sources
	rm -rf providence-reflect/src/main/model/*
	cp -R providence-testing/target/generated-sources/model/* providence-reflect/src/main/model/

serializer:
	pvdc -o providence-core/src/main/java/ -g java providence-core/src/main/providence/service.thrift

.PHONY: serializer model
