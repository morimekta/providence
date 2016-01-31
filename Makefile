INSTALL_DIR="${HOME}/.apps/lib/jars"
BIN_DIR="${HOME}/.apps/bin"

model:
	# rm -rf ${PWD}/reflect/model/net
	bazel run //tools:providence-compiler -- --gen java2 --options --containers=ORDERED --out ${PWD}/reflect/model ${PWD}/reflect/model/model.thrift

data:
	mkdir -p tmp/
	bazel run //tools:data-generator -- --entries 20000 --out ${PWD}/tmp

speedtest:
	bazel run //tools:speed-test -- --entries 20000 ${PWD}/tmp

# --- Under here is for installing the binaries.

install:
	bazel build :providence-compiler-deb :providence-converter-deb
	@echo '[INFO]: To install run:'
	@echo '[INFO]: $ sudo gdebi bazel-bin/providence-compiler-deb.deb'
	@echo '[INFO]: $ sudo gdebi bazel-bin/providence-converter-deb.deb'

.PHONY: *
