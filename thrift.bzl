def gen_thrift(name,
               gen,
               extension,
               flags=[],
               deps=[],
               srcs=[],
               visibility=[]):
    native.genrule(
        name=name,
        cmd='TMP=$$(mktemp -d);' +
            '$(location //compiler:thrift-j2c) --gen %s %s --out $$TMP $(SRCS);' % (gen, ' '.join(flags)) +
            '$(location //tools/jdk:jar) cf $@ -C $$TMP .;' +
            'rm -rf $$TMP',
        srcs=srcs,
        outs=['%s.%s' % (name, extension)],
        tools=[
            '//compiler:thrift-j2c',
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

def gen_legacy_thrift(name,
                      gen,
                      extension,
                      flags=[],
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
                visibility=[]):
    gen_thrift(
        name='__gen_%s' % name,
        gen='java2',
        extension='srcjar',
        srcs=srcs,
        flags=flags,
    )
    deps = ['//core:core']
    if '--android' in flags:
      deps = deps + ['//third-party:android-util']
    native.java_library(
        name=name,
        srcs=['__gen_%s' % name],
        deps=deps,
        visibility=visibility,
    )

def java_legacy_thrift(name,
                       srcs,
                       flags=[],
                       visibility=[]):
    gen_legacy_thrift(
            name='__gen_%s' % name,
            gen='java',
            extension='srcjar',
            srcs=srcs,
            flags=flags,
    )
    deps = ['//third-party:libthrift']
    if 'android' in flags:
        deps = deps + ['//third-party:android-util']
    native.java_library(
            name=name,
            srcs=['__gen_%s' % name],
            deps=deps,
            visibility=visibility,
    )
