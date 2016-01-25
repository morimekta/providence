INSTALL_DIR="${HOME}/.apps/lib/jars"
BIN_DIR="${HOME}/.apps/bin"

model:
	# rm -rf ${PWD}/reflect/model/net
	bazel run //tools:providence-compiler -- --gen java2 --options --containers=ORDERED --out ${PWD}/reflect/model ${PWD}/reflect/model/model.thrift

resources:
	mkdir -p ${PWD}/generated/java
	mkdir -p ${PWD}/core/generated
	bazel run //tools:providence-compiler -- --gen java2 --options --android:--jackson:--containers=SORTED --out ${PWD}/generated/java ${PWD}/testing/defs/*/*.thrift
	bazel build //testing:thrift-idl
	thrift --gen java:android -out ${PWD}/generated/java ${PWD}/bazel-genfiles/testing/thrift-idl.thrift

data:
	mkdir -p tmp/
	bazel run //tools:data-generator -- --entries 10000 --out ${PWD}/generated/resources

speedtest:
	bazel run //tools:speed-test -- --entries 10000 ${PWD}/generated/resources

# --- Under here is for installing the binaries.

install:
	bazel build :providence-compiler-deb :providence-converter-deb
	@echo '[INFO]: To install run:'
	@echo '[INFO]: $ sudo gdebi bazel-bin/providence-compiler-deb.deb'
	@echo '[INFO]: $ sudo gdebi bazel-bin/providence-converter-deb.deb'

.PHONY: *
