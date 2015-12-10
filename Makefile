INSTALL_DIR="${HOME}/.apps/lib/jars"
BIN_DIR="${HOME}/.apps/bin"

model:
	rm -rf ${PWD}/reflect/model/net
	bazel run //compiler:thrift-j2c -- --gen java2 --out ${PWD}/reflect/model ${PWD}/reflect/model/*.thrift

resources:
	mkdir -p ${PWD}/core/generated
	rm -rf ${PWD}/core/generated/net
	bazel run //compiler:thrift-j2c -- --gen java2 --out ${PWD}/core/generated ${PWD}/core/res/definitions/*.thrift

release:
	bazel build //:thrift-j2

test:
	bazel test :tests

thrift-j2:
	bazel build //converter:thrift-j2_deploy.jar

thrift-j2c:
	bazel build //compiler:thrift-j2c_deploy.jar

install: thrift-j2 thrift-j2c
	mkdir -p ${HOME}/.local/bin ${HOME}/.local/lib
	cp -f bazel-bin/compiler/thrift-j2c_deploy.jar ${HOME}/.local/lib
	cp -f bazel-bin/converter/thrift-j2_deploy.jar ${HOME}/.local/lib
	cp -f scripts/thrift-j2 scripts/thrift-j2c ${HOME}/.local/bin
	chmod a+x ${HOME}/.local/bin/thrift-j2 ${HOME}/.local/bin/thrift-j2c
	@echo '[INFO]: Remember to add to PATH: "${HOME}/.local/bin"'

.PHONY: model resources test
