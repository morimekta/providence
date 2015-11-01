INSTALL_DIR="${HOME}/.apps/lib/jars"
BIN_DIR="${HOME}/.apps/bin"

model:
	rm -rf ${PWD}/reflect/model/net
	bazel run //compiler:compile -- --gen java2 --out ${PWD}/reflect/model ${PWD}/reflect/model/*.thrift

resources:
	rm -rf ${PWD}/core/generated/net
	bazel run //compiler:compile -- --gen java2 --out ${PWD}/core/generated ${PWD}/core/res/definitions/*.thrift

test:
	bazel test //core:test //reflect:test //compiler:test //jax-rs:test

.PHONY: model resources test
