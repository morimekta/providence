model:
	mvn generate-sources
	rm -rf providence-reflect/src/main/model/*
	cp -R providence-testing/target/generated-sources/model/* providence-reflect/src/main/model/
