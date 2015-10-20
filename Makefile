INSTALL_DIR="${HOME}/.apps/lib/jars"
BIN_DIR="${HOME}/.apps/bin"

libs:
	mkdir -p ./libs
	buck build //core:core //reflect:reflect
	cp buck-out/gen/core/lib__core__output/core.jar libs/thrift-j2-core.jar
	cp buck-out/gen/reflect/lib__reflect__output/reflect.jar libs/thrift-j2-reflect.jar

apps:
	buck build //compiler:compile //converter:convert

install: apps
	mkdir -p ${INSTALL_DIR}
	cp buck-out/gen/compiler/compile.jar ${INSTALL_DIR}
	cp buck-out/gen/converter/convert.jar ${INSTALL_DIR}
	echo '#!/bin/bash' > ${BIN_DIR}/tcompile
	echo 'java -jar ${INSTALL_DIR}/compile.jar $@' >> ${BIN_DIR}/tcompile
	chmod a+x ${BIN_DIR}/tcompile
	echo '#!/bin/bash' > ${BIN_DIR}/tconv
	echo 'java -jar ${INSTALL_DIR}/convert.jar $@' >> ${BIN_DIR}/tconv
	chmod a+x ${BIN_DIR}/tconv

