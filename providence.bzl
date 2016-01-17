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
            '$(location //compiler:providence-compiler) --gen %s %s --out $$TMP $(SRCS);' % (gen, ' '.join(flags)) +
            '$(location //tools/jdk:jar) cf $@ -C $$TMP .;' +
            'rm -rf $$TMP',
        srcs=srcs,
        outs=['%s.%s' % (name, extension)],
        tools=[
            '//compiler:providence-compiler',
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
        gen='java2',
        extension='srcjar',
        srcs=srcs,
        flags=flags,
        options=options,
    )
    deps = ['//core:core']
    if '--android' in options:
      deps = deps + ['//third-party:android-util']
    if '--jackson' in options:
        deps = deps + [
            '//third-party:jackson-annotations',
            '//third-party:jackson-databind',
            '//jackson:jackson',
        ]
    native.java_library(
        name=name,
        srcs=['__gen_%s' % name],
        deps=deps,
        visibility=visibility,
    )
