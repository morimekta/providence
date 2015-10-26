VERSION='v0.0.1-alpha'

def release_jar(name,
                target):
    native.genrule(
        name = name,
        cmd = 'cp $(SRCS) $(OUTS)',
        srcs = [target],
        outs = ['thrift-j2-%s-%s.jar' % (name, VERSION)],
    )
