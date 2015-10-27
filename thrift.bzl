def gen_thrift(name,
               gen,
               flags=[],
               deps=[],
               srcs=[],
               visibility=[]):
    native.genrule(
        name=name,
        cmd='TMP=$$(mktemp -d);' +
            '$(location //compiler:compile) --gen %s %s --out $$TMP $(SRCS);' % (gen, ' '.join(flags)) +
            '$(location //tools/jdk:jar) cf $@ -C $$TMP .;' +
            'rm -rf $$TMP',
        srcs=srcs,
        outs=['%s.srcjar' % name],
        tools=[
            '//compiler:compile',
            '//tools/jdk:jar',
        ],
        local=1,
        visibility=visibility,
    )
