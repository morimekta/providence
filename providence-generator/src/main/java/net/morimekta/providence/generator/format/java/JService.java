package net.morimekta.providence.generator.format.java;

import net.morimekta.providence.reflect.contained.CService;
import net.morimekta.providence.reflect.contained.CServiceMethod;

/**
 * Created by morimekta on 4/24/16.
 */
public class JService {
    private final CService service;
    private final JHelper helper;

    public JService(CService service, JHelper helper) {
        this.service = service;
        this.helper = helper;
    }

    public String className() {
        return JUtils.getClassName(service);
    }

    public JServiceMethod[] methods() {
        CServiceMethod[] ma = service.getMethods().toArray(new CServiceMethod[service.getMethods().size()]);
        JServiceMethod[] ret = new JServiceMethod[ma.length];
        for (int i = 0; i < service.getMethods().size(); ++i) {
            ret[i] = new JServiceMethod(service, ma[i], helper);
        }
        return ret;
    }

    public CService getService() {
        return service;
    }
}
