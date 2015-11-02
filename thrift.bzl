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
    native.genrule(
        name='%s-src' % name,
        srcs=[name],
        outs=['%s-src.jar' % name],
        cmd='cp $< $@'
    )
    native.filegroup(
        name='__%s_srcs' % name,
        srcs=srcs,
    )

def java_thrift(name,
                srcs,
                flags=[],
                visibility=[]):
    gen_thrift(
        name='__gen_%s' % name,
        gen='java2',
        srcs=srcs,
        flags=flags,
    )
    native.java_library(
        name=name,
        srcs=['__gen_%s' % name],
        deps=['//core:core'],
        visibility=visibility,
    )
