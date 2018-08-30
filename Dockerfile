FROM java:openjdk-8-jre-alpine

RUN apk add --no-cache curl bash

COPY providence-tools/src/deb/bin/*                                   /usr/bin/
COPY providence-generator-java/target/java.jar                        /usr/share/providence/generator/
COPY providence-tools-generator/target/providence-tools-generator.jar /usr/share/providence/
COPY providence-tools-converter/target/providence-tools-converter.jar /usr/share/providence/
COPY providence-tools-config/target/providence-tools-config.jar       /usr/share/providence/
COPY providence-tools-rpc/target/providence-tools-rpc.jar             /usr/share/providence/

ENTRYPOINT []
CMD []