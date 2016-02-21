def gen_providence_source(name,
                          gen,
                          extension,
                          flags=[],
                          options=None,
                          deps=[],
                          srcs=[],
                          visibility=[]):
    if options != None and len(options) > 0:
        flags = flags + ['--options', ':'.join(options)]
    native.genrule(
        name=name,
        cmd='TMP=$$(mktemp -d);' +
            '$(location //providence-tools:providence-compiler) --gen %s %s --out $$TMP $(SRCS);' % (gen, ' '.join(flags)) +
            '$(location //tools/jdk:jar) cf $@ -C $$TMP .;' +
            'rm -rf $$TMP',
        srcs=srcs,
        outs=['%s.%s' % (name, extension)],
        tools=[
            '//providence-tools:providence-compiler',
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

def java_providence(name,
                    srcs,
                    flags=[],
                    options=[],
                    visibility=[]):
    gen_providence_source(
        name='__gen_%s' % name,
        gen='java',
        extension='srcjar',
        srcs=srcs,
        flags=flags,
        options=options,
    )
    deps = [
        '//providence-core',
        "//third-party:net_morimekta_utils_io_util",
    ]
    if '--android' in options:
      deps = deps + ['//third-party:net_morimekta_utils_android_util']
    if '--jackson' in options:
        deps = deps + [
            '//providence-core-jackson',
            "//third-party:com_fasterxml_jackson_core_jackson_annotations",
            "//third-party:com_fasterxml_jackson_core_jackson_databind",
        ]
    native.java_library(
        name=name,
        srcs=['__gen_%s' % name],
        deps=deps,
        visibility=visibility,
    )
