INSTALL_DIR="${HOME}/.apps/lib/jars"
BIN_DIR="${HOME}/.apps/bin"

thrift-j2-%.jar:
	buck build //$*:$*
	cp buck-out/gen/$*/lib__$*__output/$*.jar thrift-j2-$*.jar

compile.jar:
	buck build //compiler:compile
	cp buck-out/gen/compiler/compile.jar .

convert.jar:
	buck build //converter:convert
	cp buck-out/gen/converter/convert.jar .

libs: thrift-j2-core.jar thrift-j2-reflect.jar thrift-j2-jax-rs.jar thrift-j2-protocol.jar thrift-j2-client.jar

install: compile.jar convert.jar
	mkdir -p ${INSTALL_DIR}
	cp compile.jar ${INSTALL_DIR}
	cp convert.jar ${INSTALL_DIR}
	echo '#!/bin/bash' > ${BIN_DIR}/tcompile
	echo 'java -jar ${INSTALL_DIR}/compile.jar $$@' >> ${BIN_DIR}/tcompile
	chmod a+x ${BIN_DIR}/tcompile
	echo '#!/bin/bash' > ${BIN_DIR}/tconv
	echo 'java -jar ${INSTALL_DIR}/convert.jar $$@' >> ${BIN_DIR}/tconv
	chmod a+x ${BIN_DIR}/tconv

clean:
	buck clean
	rm -rf *.jar

.PHONY: libs install clean
