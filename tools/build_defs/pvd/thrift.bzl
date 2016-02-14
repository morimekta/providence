def gen_thrift_source(name,
                      gen,
                      extension,
                      flags=[],
                      options=[],
                      deps=[],
                      srcs=[],
                      visibility=[]):
    flags = [gen] + flags
    native.genrule(
            name=name,
            cmd='TMP=$$(mktemp -d);' +
                'thrift --gen %s -out $$TMP $(SRCS);' % (':'.join(flags)) +
                '$(location //tools/jdk:jar) cf $@ -C $$TMP .;' +
                'rm -rf $$TMP',
            srcs=srcs,
            outs=['%s.%s' % (name, extension)],
            tools=[
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
                options=[],
                visibility=[]):
    gen = ['java']
    if 'android' in options:
        gen = gen + ['android']
    gen_thrift_source(
            name='__gen_%s' % name,
            gen=':'.join(gen),
            extension='srcjar',
            srcs=srcs,
            flags=flags,
    )
    deps = ['//third-party:org_apache_thrift_libthrift']
    if 'android' in flags:
        deps = deps + ['//third-party:net_morimekta_utils_android_util']
    native.java_library(
            name=name,
            srcs=['__gen_%s' % name],
            deps=deps,
            visibility=visibility,
    )
