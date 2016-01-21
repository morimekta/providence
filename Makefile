INSTALL_DIR="${HOME}/.apps/lib/jars"
BIN_DIR="${HOME}/.apps/bin"

model:
	# rm -rf ${PWD}/reflect/model/net
	bazel run //compiler:providence-compiler -- --gen java2 --options --containers=ORDERED --out ${PWD}/reflect/model ${PWD}/reflect/model/model.thrift

resources:
	mkdir -p ${PWD}/generated/java
	mkdir -p ${PWD}/core/generated
	bazel run //compiler:providence-compiler -- --gen java2 --options --android:--jackson --out ${PWD}/generated/java ${PWD}/core/defs/*/*.thrift
	bazel build //testing:thrift-idl
	thrift --gen java:android -out ${PWD}/generated/java ${PWD}/bazel-genfiles/testing/thrift-idl.thrift

data:
	bazel run //tests:generate-data -- --entries 10000 --out ${PWD}/generated/resources

speedtest:
	bazel run //tests:speed-test -- --entries 10000 ${PWD}/generated/resources

# --- Under here is for installing the binaries.

install:
	bazel build //converter:providence-converter_deploy.jar
	bazel build //compiler:providence-compiler_deploy.jar
	mkdir -p ${HOME}/.local/bin ${HOME}/.local/lib
	cp -f bazel-bin/compiler/providence-compiler_deploy.jar ${HOME}/.local/lib
	cp -f bazel-bin/converter/providence-converter_deploy.jar ${HOME}/.local/lib
	cp -f scripts/providence-compiler scripts/providence-converter ${HOME}/.local/bin
	chmod a+x ${HOME}/.local/bin/providence-converter ${HOME}/.local/bin/providence-compiler
	@echo '[INFO]: Remember to add to PATH: "${HOME}/.local/bin"'

.PHONY: *
