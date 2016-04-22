model:
	mvn generate-sources
	rm -rf providence-reflect/src/main/model/*
	cp -R providence-testing/target/generated-sources/providence/* providence-reflect/src/main/model/
